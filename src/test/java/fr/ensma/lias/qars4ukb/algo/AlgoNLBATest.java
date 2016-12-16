package fr.ensma.lias.qars4ukb.algo;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lias.qars4ukb.SQLScriptRunner;
import fr.ensma.lias.qars4ukb.Session;
import fr.ensma.lias.qars4ukb.cache.CacheLBA;
import fr.ensma.lias.qars4ukb.query.Query;
import fr.ensma.lias.qars4ukb.query.QueryFactory;
import fr.ensma.lias.qars4ukb.triplestore.jdbcdb.JDBCQueryFactory;
import fr.ensma.lias.qars4ukb.triplestore.jdbcdb.JDBCQueryOptFactory;
import fr.ensma.lias.qars4ukb.triplestore.jdbcdb.JDBCSession;

public class AlgoNLBATest {

    IAlgo algo;
    private QueryFactory factoryOpt;
    private Session session;
    private Query q1, q2, q3, q4, q5, q6;
    
    @Before
    public void setup() throws Exception {
	algo = new AlgoNLBA();
	factoryOpt = new JDBCQueryOptFactory();
	session = factoryOpt.createSession();
	SQLScriptRunner newScriptRunner = new SQLScriptRunner(((JDBCSession) session).getConnection(), false, false);
	InputStream resourceAsStream = getClass().getResourceAsStream("/test_dataset1.sql");
	newScriptRunner.runScript(new InputStreamReader(resourceAsStream));
	q1 = factoryOpt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	
    }
    
    @Test
    public void testComputesAlphaMFSsAndXSSs() {
	List<Double> listOfAlpha = new ArrayList<>();
	listOfAlpha.add(0.4);
	listOfAlpha.add(0.8);
	AlgoResult result = algo.computesAlphaMFSsAndXSSs(q1, listOfAlpha);
	
	q2 = factoryOpt
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }");
	q4 = factoryOpt.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q5 = factoryOpt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }");
	q3 = factoryOpt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }");
	q6 = factoryOpt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }");
	List<Query> expectedMFS = new ArrayList<>();
	expectedMFS.add(q2);
	expectedMFS.add(q4);
	expectedMFS.add(q5);
	List<Query> expectedXSS = new ArrayList<>();
	expectedXSS.add(q3);
	expectedXSS.add(q6);
	

	
	List<Query> obtainedMFS = result.getAlphaMFSs(0.4);
	List<Query> obtainedXSS = result.getAlphaXSSs(0.4);
	Assert.assertTrue(obtainedMFS.containsAll(expectedMFS));
	Assert.assertTrue(expectedMFS.containsAll(obtainedMFS));
	Assert.assertTrue(obtainedXSS.containsAll(expectedXSS));
	Assert.assertTrue(expectedXSS.containsAll(obtainedXSS));
	
	// compare it to the result of DFS
	q1.runDFS(session, 0.4);
	expectedMFS = q1.getAllMFS();
	expectedXSS = q1.getAllXSS();
	Assert.assertTrue(obtainedMFS.containsAll(expectedMFS));
	Assert.assertTrue(expectedMFS.containsAll(obtainedMFS));
	Assert.assertTrue(obtainedXSS.containsAll(expectedXSS));
	Assert.assertTrue(expectedXSS.containsAll(obtainedXSS));
	
	expectedMFS = new ArrayList<>();
	expectedMFS.add(q2);
	expectedMFS.add(q4);
	expectedMFS.add(q3);
	expectedMFS.add(q6);
	expectedXSS = new ArrayList<>();
	obtainedMFS = result.getAlphaMFSs(0.8);
	obtainedXSS = result.getAlphaXSSs(0.8);
	Assert.assertTrue(obtainedMFS.containsAll(expectedMFS));
	Assert.assertTrue(expectedMFS.containsAll(obtainedMFS));
	Assert.assertTrue(obtainedXSS.containsAll(expectedXSS));
	Assert.assertTrue(expectedXSS.containsAll(obtainedXSS));
	Assert.assertEquals(3,algo.getNbCacheHits());
	Assert.assertEquals(18,algo.getNbExecutedQuery());

	// compare it to the result of DFS
	q1.runDFS(session, 0.8);
	expectedMFS = q1.getAllMFS();
	expectedXSS = q1.getAllXSS();
	Assert.assertTrue(obtainedMFS.containsAll(expectedMFS));
	Assert.assertTrue(expectedMFS.containsAll(obtainedMFS));
	Assert.assertTrue(obtainedXSS.containsAll(expectedXSS));
	Assert.assertTrue(expectedXSS.containsAll(obtainedXSS));
	
	
    }

}
