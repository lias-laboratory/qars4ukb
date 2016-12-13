package fr.ensma.lias.qars4ukb.query;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lias.qars4ukb.Result;
import fr.ensma.lias.qars4ukb.SQLScriptRunner;
import fr.ensma.lias.qars4ukb.Session;
import fr.ensma.lias.qars4ukb.triplestore.jdbcdb.JDBCQueryFactory;
import fr.ensma.lias.qars4ukb.triplestore.jdbcdb.JDBCSession;

public class QueryHSQLDBTest {

	private QueryFactory factory;
	private Session session;
	private Query q1, q2, q3, q4, q5, q6;

	@Before
	public void setUp() throws Exception {
		factory = new JDBCQueryFactory();
		session = factory.createSession();
		SQLScriptRunner newScriptRunner = new SQLScriptRunner(((JDBCSession) session).getConnection(), false, false);
		InputStream resourceAsStream = getClass().getResourceAsStream("/test_dataset1.sql");
		newScriptRunner.runScript(new InputStreamReader(resourceAsStream));
		factory = new JDBCQueryFactory();
	}

	@Test
	public void isFailing() {
		q1 = factory.createQuery(
				"SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }");
		Assert.assertFalse(q1.isFailing(session));
		q1 = factory.createQuery(
				"SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course0034' }");
		Assert.assertTrue(q1.isFailing(session));
	}

	@Test
	public void testGetResult() {
		q1 = factory.createQuery(
				"SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }");
		Result res = q1.getResult(session);
		Assert.assertEquals(1, res.getNbRow());
		res = q1.getResult(session);
		Assert.assertTrue(res.next());
		Assert.assertEquals("http://www.Department11.University0.edu/Course33", res.getString(1));
	}

	@Test
	public void testAddTriplePattern() {
		q1 = factory.createQuery(
				"SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }");
		TriplePattern t = new TriplePattern("?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }",
				2);
		q1.addTriplePattern(t);
		Assert.assertEquals(2, q1.getTriplePatterns().size());
		Assert.assertEquals(t, q1.getTriplePatterns().get(1));
		Assert.assertEquals(
				new TriplePattern("?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33'", 1),
				q1.getTriplePatterns().get(0));
	}

	@Test
	public void testGetTriplePattern() {
		q1 = factory.createQuery(
				"SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }");
		Assert.assertEquals(1, q1.getTriplePatterns().size());
		q1 = factory.createQuery(
				"SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }");
		Assert.assertEquals(2, q1.getTriplePatterns().size());
	}

	@Test
	public void testIncludesAQueryOf() {
		q1 = factory.createQuery(
				"SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }");
		q2 = factory.createQuery(
				"SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }");
		q3 = factory.createQuery(
				"SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }");
		List<Query> queries = new ArrayList<>(2);
		queries.add(q1);
		queries.add(q2);
		Assert.assertTrue(q3.includesAQueryOf(queries));
		queries = new ArrayList<>(2);
		queries.add(q2);
		queries.add(q3);
		Assert.assertFalse(q1.includesAQueryOf(queries));
	}
	
	@Test
	public void testToNativeQuery() {
		q1 = factory.createQuery(
				"SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }");
		q2 = factory.createQuery(
				"SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }");
		Assert.assertEquals("select * from (select s as p from t where p='http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name' and o='Course33') t0", q1.toNativeQuery());
		Assert.assertEquals("select * from (select s as p from t where p='http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name' and o='Course33') t0 NATURAL JOIN (select s as p, o as t from t where p='http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone') t1", q2.toNativeQuery());
	}
	
	@Test
	public void testIsEmpty() {
		q1 = factory.createQuery(
				"SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }");
		Assert.assertFalse(q1.isEmpty());
		q2 = factory.createQuery("");
		Assert.assertTrue(q2.isEmpty());
	}
	
	@Test
	public void testFindAnMFS() {
		q1 = factory.createQuery(
				"SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }");
		q2 = factory.createQuery("SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }");
		Assert.assertEquals(q2, q1.findAnMFS(session));
	}

	@Test
	public void testRunLBA() {
		q1 = factory.createQuery(
				"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#size> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
		q2 = factory.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }");
		q3 = factory.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#size> ?t }");
		q4 = factory.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
		q5 = factory.createQuery(
				"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }");
		List<Query> expectedMFS = new ArrayList<>();
		expectedMFS.add(q2);
		expectedMFS.add(q3);
		expectedMFS.add(q4);
		List<Query> expectedXSS = new ArrayList<>();
		expectedXSS.add(q5);
		q1.runLBA(session);
		Assert.assertTrue(q1.getAllMFS().containsAll(expectedMFS));
		Assert.assertTrue(expectedMFS.containsAll(q1.getAllMFS()));
		Assert.assertTrue(q1.getAllXSS().containsAll(expectedXSS));
		Assert.assertTrue(expectedXSS.containsAll(q1.getAllXSS()));
		
		q1 = factory.createQuery(
				"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
		q2 = factory.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }");
		q4 = factory.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
		q5 = factory.createQuery(
				"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }");
		q3 = factory.createQuery(
				"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }");
		q6 = factory.createQuery(
				"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }");
		expectedMFS = new ArrayList<>();
		expectedMFS.add(q2);
		expectedMFS.add(q4);
		expectedMFS.add(q5);
		expectedXSS = new ArrayList<>();
		expectedXSS.add(q3);
		expectedXSS.add(q6);
		q1.runLBA(session);
		Assert.assertTrue(q1.getAllMFS().containsAll(expectedMFS));
		Assert.assertTrue(expectedMFS.containsAll(q1.getAllMFS()));
		Assert.assertTrue(q1.getAllXSS().containsAll(expectedXSS));
		Assert.assertTrue(expectedXSS.containsAll(q1.getAllXSS()));
	}
}
