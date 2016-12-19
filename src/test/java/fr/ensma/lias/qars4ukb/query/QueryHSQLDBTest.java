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
import fr.ensma.lias.qars4ukb.cache.CacheLBA;
import fr.ensma.lias.qars4ukb.triplestore.jdbcdb.JDBCQueryFactory;
import fr.ensma.lias.qars4ukb.triplestore.jdbcdb.JDBCQueryOptFactory;
import fr.ensma.lias.qars4ukb.triplestore.jdbcdb.JDBCSession;

public class QueryHSQLDBTest {

    private QueryFactory factory, factoryOpt;
    private Session session;
    private Query q1, q2, q3, q4, q5, q6, q1Opt, q2Opt, q3Opt, q4Opt, q5Opt, q6Opt;

    @Before
    public void setUp() throws Exception {
	factory = new JDBCQueryFactory();
	factoryOpt = new JDBCQueryOptFactory();
	session = factory.createSession();
	SQLScriptRunner newScriptRunner = new SQLScriptRunner(((JDBCSession) session).getConnection(), false, false);
	InputStream resourceAsStream = getClass().getResourceAsStream("/test_dataset1.sql");
	newScriptRunner.runScript(new InputStreamReader(resourceAsStream));
    }

    @Test
    public void isFailing() {
	q1 = factory.createQuery(
		"SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }");
	Assert.assertFalse(q1.isFailing(session, 0.4));
	Assert.assertTrue(q1.isFailing(session, 0.8));
	q1 = factory.createQuery(
		"SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course0034' }");
	Assert.assertTrue(q1.isFailing(session, 0.1));
    }

    @Test
    public void testGetResult() {
	q1 = factory.createQuery(
		"SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }");
	Result res = q1.getResult(session, 0.4);
	Assert.assertEquals(1, res.getNbRow());
	res = q1.getResult(session, 0.5);
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
	q2 = factory
		.createQuery("SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }");
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
	Assert.assertEquals(
		"select * from (select s as p from t where p='http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name' and o='Course33' and tv>=0.4) t0",
		q1.toNativeQuery(0.4));
	Assert.assertEquals(
		"select * from (select s as p from t where p='http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name' and o='Course33' and tv>=0.5) t0 NATURAL JOIN (select s as p, o as t from t where p='http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone' and tv>=0.5) t1",
		q2.toNativeQuery(0.5));
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
	q2 = factory
		.createQuery("SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }");
	Assert.assertEquals(q2, q1.findAnMFS(session, 0.4));
	Assert.assertEquals(q2, q1.findAnMFS(session, 0.8));
    }

    @Test
    public void testFindAnXSS() {
	q1 = factory.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q2 = factory.createQuery(
		"SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }");
	q3 = factory.createQuery(
		"SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }");
	q4 = factory.createQuery("", q1);
	Assert.assertEquals(q2, q1.findAnXSS(session, 0.4, q4));
	Assert.assertEquals(q3, q1.findAnXSS(session, 0.4, q3));	
	Assert.assertEquals(q4, q1.findAnXSS(session, 0.8,q4));
    }

    @Test
    public void testRunLBA() {
	q1 = factory.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#size> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q2 = factory
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }");
	q3 = factory
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#size> ?t }");
	q4 = factory.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q5 = factory.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }");
	List<Query> expectedMFS = new ArrayList<>();
	expectedMFS.add(q2);
	expectedMFS.add(q3);
	expectedMFS.add(q4);
	List<Query> expectedXSS = new ArrayList<>();
	expectedXSS.add(q5);
	q1.runLBA(session, 0.4);
	Assert.assertTrue(q1.getAllMFS().containsAll(expectedMFS));
	Assert.assertTrue(expectedMFS.containsAll(q1.getAllMFS()));
	Assert.assertTrue(q1.getAllXSS().containsAll(expectedXSS));
	Assert.assertTrue(expectedXSS.containsAll(q1.getAllXSS()));

	// Test with a higher threshold
	expectedMFS = new ArrayList<>();
	expectedMFS.add(q2);
	expectedMFS.add(q3);
	expectedMFS.add(q4);
	expectedMFS.add(q5);
	expectedXSS = new ArrayList<>();
	q1.runLBA(session, 0.7);
	Assert.assertTrue(q1.getAllMFS().containsAll(expectedMFS));
	Assert.assertTrue(expectedMFS.containsAll(q1.getAllMFS()));
	Assert.assertTrue(q1.getAllXSS().containsAll(expectedXSS));
	Assert.assertTrue(expectedXSS.containsAll(q1.getAllXSS()));

	// we check that LBAOpt returns the same MFSs and XSSs
	q1Opt = factoryOpt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#size> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q2Opt = factoryOpt
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }");
	q3Opt = factoryOpt
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#size> ?t }");
	q4Opt = factoryOpt
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q5Opt = factoryOpt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }");
	expectedMFS = new ArrayList<>();
	expectedMFS.add(q2Opt);
	expectedMFS.add(q3Opt);
	expectedMFS.add(q4Opt);
	expectedXSS = new ArrayList<>();
	expectedXSS.add(q5Opt);
	q1Opt.runLBA(session, 0.4);
	Assert.assertTrue(q1Opt.getAllMFS().containsAll(expectedMFS));
	Assert.assertTrue(expectedMFS.containsAll(q1Opt.getAllMFS()));
	Assert.assertTrue(q1Opt.getAllXSS().containsAll(expectedXSS));
	Assert.assertTrue(expectedXSS.containsAll(q1Opt.getAllXSS()));

	// with higher threshold
	expectedMFS = new ArrayList<>();
	expectedMFS.add(q2Opt);
	expectedMFS.add(q3Opt);
	expectedMFS.add(q4Opt);
	expectedMFS.add(q5Opt);
	expectedXSS = new ArrayList<>();
	q1Opt.runLBA(session, 0.7);
	Assert.assertTrue(q1Opt.getAllMFS().containsAll(expectedMFS));
	Assert.assertTrue(expectedMFS.containsAll(q1Opt.getAllMFS()));
	Assert.assertTrue(q1Opt.getAllXSS().containsAll(expectedXSS));
	Assert.assertTrue(expectedXSS.containsAll(q1Opt.getAllXSS()));

	q1 = factory.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q2 = factory
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }");
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
	q1.runLBA(session, 0.4);
	Assert.assertTrue(q1.getAllMFS().containsAll(expectedMFS));
	Assert.assertTrue(expectedMFS.containsAll(q1.getAllMFS()));
	Assert.assertTrue(q1.getAllXSS().containsAll(expectedXSS));
	Assert.assertTrue(expectedXSS.containsAll(q1.getAllXSS()));

	// with a higher threshold
	expectedMFS = new ArrayList<>();
	expectedMFS.add(q2);
	expectedMFS.add(q4);
	expectedMFS.add(q3);
	expectedMFS.add(q6);
	expectedXSS = new ArrayList<>();
	q1.runLBA(session, 0.8);
	Assert.assertTrue(q1.getAllMFS().containsAll(expectedMFS));
	Assert.assertTrue(expectedMFS.containsAll(q1.getAllMFS()));
	Assert.assertTrue(q1.getAllXSS().containsAll(expectedXSS));
	Assert.assertTrue(expectedXSS.containsAll(q1.getAllXSS()));

	// we check again that LBAOpt returns the same result
	q1Opt = factoryOpt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q2Opt = factoryOpt
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }");
	q4Opt = factoryOpt
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q5Opt = factoryOpt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }");
	q3Opt = factoryOpt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }");
	q6Opt = factoryOpt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }");
	expectedMFS = new ArrayList<>();
	expectedMFS.add(q2Opt);
	expectedMFS.add(q4Opt);
	expectedMFS.add(q5Opt);
	expectedXSS = new ArrayList<>();
	expectedXSS.add(q3Opt);
	expectedXSS.add(q6Opt);
	q1Opt.runLBA(session, 0.4);
	Assert.assertTrue(q1Opt.getAllMFS().containsAll(expectedMFS));
	Assert.assertTrue(expectedMFS.containsAll(q1Opt.getAllMFS()));
	Assert.assertTrue(q1Opt.getAllXSS().containsAll(expectedXSS));
	Assert.assertTrue(expectedXSS.containsAll(q1Opt.getAllXSS()));

	// with a higher threshold
	expectedMFS = new ArrayList<>();
	expectedMFS.add(q2Opt);
	expectedMFS.add(q4Opt);
	expectedMFS.add(q3Opt);
	expectedMFS.add(q6Opt);
	expectedXSS = new ArrayList<>();
	q1Opt.runLBA(session, 0.8);
	Assert.assertTrue(q1Opt.getAllMFS().containsAll(expectedMFS));
	Assert.assertTrue(expectedMFS.containsAll(q1Opt.getAllMFS()));
	Assert.assertTrue(q1Opt.getAllXSS().containsAll(expectedXSS));
	Assert.assertTrue(expectedXSS.containsAll(q1Opt.getAllXSS()));
    }

    @Test
    public void testCacheLBAOpt() {
	q1Opt = factoryOpt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q1Opt.runLBA(session, 0.4);
	Assert.assertEquals(3, CacheLBA.getInstance().getNbCacheHits());
	q3Opt = factoryOpt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }");
	q6Opt = factoryOpt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }");
	List<Query> expectedSuccessfulCachedQueries = new ArrayList<>();
	expectedSuccessfulCachedQueries.add(q3Opt);
	expectedSuccessfulCachedQueries.add(q6Opt);
	Assert.assertTrue(
		CacheLBA.getInstance().getSuccessfulCachedQueries().containsAll(expectedSuccessfulCachedQueries));
	Assert.assertTrue(
		expectedSuccessfulCachedQueries.containsAll(CacheLBA.getInstance().getSuccessfulCachedQueries()));

	q1Opt = factoryOpt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#size> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q1Opt.runLBA(session, 0.4);
	Assert.assertEquals(0, CacheLBA.getInstance().getNbCacheHits());
	expectedSuccessfulCachedQueries = new ArrayList<>();
	expectedSuccessfulCachedQueries.add(q3Opt);
	Assert.assertTrue(
		CacheLBA.getInstance().getSuccessfulCachedQueries().containsAll(expectedSuccessfulCachedQueries));
	Assert.assertTrue(
		expectedSuccessfulCachedQueries.containsAll(CacheLBA.getInstance().getSuccessfulCachedQueries()));

    }

    @Test
    public void testIncludes() {
	q1 = factory.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q2 = factory
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }");
	Assert.assertTrue(q1.includes(q2));
	q3 = factory
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#size> ?e }");
	Assert.assertFalse(q1.includes(q3));

	// Test with QueryOpt
	q1Opt = factoryOpt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q2Opt = factoryOpt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }", q1Opt);
	Assert.assertTrue(q1Opt.includes(q2Opt));
	q3Opt = factoryOpt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }", q1Opt);
	Assert.assertFalse(q2Opt.includes(q3Opt));
	Assert.assertFalse(q3Opt.includes(q2Opt));
    }

    @Test
    public void testRemoveTriplePattern() {
	q2 = factory
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }");
	TriplePattern tp1 = q2.getTriplePatterns().get(0);
	q2.removeTriplePattern(tp1);
	Assert.assertTrue(q2.isEmpty());
	q1 = factory.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	tp1 = q1.getTriplePatterns().get(0);
	TriplePattern tp2 = q1.getTriplePatterns().get(1);
	q2 = factory.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q1.removeTriplePattern(tp1);
	q1.removeTriplePattern(tp2);
	Assert.assertEquals(q1, q2);
    }
}
