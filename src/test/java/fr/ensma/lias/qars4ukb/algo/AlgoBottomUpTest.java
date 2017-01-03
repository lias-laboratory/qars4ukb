package fr.ensma.lias.qars4ukb.algo;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lias.qars4ukb.SQLScriptRunner;
import fr.ensma.lias.qars4ukb.Session;
import fr.ensma.lias.qars4ukb.cache.ExtendedCacheLBA;
import fr.ensma.lias.qars4ukb.query.Query;
import fr.ensma.lias.qars4ukb.query.QueryFactory;
import fr.ensma.lias.qars4ukb.triplestore.jdbcdb.JDBCQueryExtFactory;
import fr.ensma.lias.qars4ukb.triplestore.jdbcdb.JDBCSession;

public class AlgoBottomUpTest {

    AlgoBottomUp algo;
    private QueryFactory factoryExt;
    private Session session;
    private Query q1, q2, q3, q4, q5, q6;
    
    @Before
    public void setup() throws Exception {
	algo = new AlgoBottomUp();
	factoryExt = new JDBCQueryExtFactory();
	session = factoryExt.createSession();
	SQLScriptRunner newScriptRunner = new SQLScriptRunner(((JDBCSession) session).getConnection(), false, false);
	InputStream resourceAsStream = getClass().getResourceAsStream("/test_dataset1.sql");
	newScriptRunner.runScript(new InputStreamReader(resourceAsStream));
	q1 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q2 = factoryExt
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }", q1);
	q4 = factoryExt.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }", q1);
	q5 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }", q1);
	q3 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }", q1);
	q6 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }", q1);
    }
    
    @Test
    public void testDiscoverXSS() {
	ExtendedCacheLBA.getInstance().clearCache();
	session.clearExecutedQueryCount();
	Set<Query> discoveredXSS = new HashSet<>();
	discoveredXSS.add(q3);
	discoveredXSS.add(q6);
	Set<Query> res = algo.discoverXSS(discoveredXSS, 0.5, session);
	Assert.assertTrue(res.containsAll(discoveredXSS));
	Assert.assertTrue(discoveredXSS.containsAll(res));
	Assert.assertEquals(2, session.getExecutedQueryCount());
	
	ExtendedCacheLBA.getInstance().clearCache();
	session.clearExecutedQueryCount();
	discoveredXSS = new HashSet<>();
	discoveredXSS.add(q3);
	discoveredXSS.add(q6);
	res = algo.discoverXSS(discoveredXSS, 0.7, session);
	Assert.assertTrue(res.isEmpty());
	Assert.assertEquals(2, session.getExecutedQueryCount());
    }
    
    @Test
    public void testDiscoverMFS() {
	ExtendedCacheLBA.getInstance().clearCache();
	session.clearExecutedQueryCount();
	Set<Query> discoveredMFS = new HashSet<>();
	discoveredMFS.add(q2);
	discoveredMFS.add(q4);
	discoveredMFS.add(q5);
	Set<Query> res = algo.discoverMFS(discoveredMFS, 0.5, session);
	Set<Query> expectedMFS = new HashSet<>();
	expectedMFS.add(q2);
	expectedMFS.add(q4);
	expectedMFS.add(q5);
	Assert.assertTrue(res.containsAll(expectedMFS));
	Assert.assertTrue(expectedMFS.containsAll(res));
	Assert.assertEquals(2, session.getExecutedQueryCount());
	
	ExtendedCacheLBA.getInstance().clearCache();
	session.clearExecutedQueryCount();
	res = algo.discoverMFS(discoveredMFS, 0.7, session);
	expectedMFS = new HashSet<>();
	expectedMFS.add(q2);
	expectedMFS.add(q4);
	expectedMFS.add(q6);
	Assert.assertTrue(res.containsAll(expectedMFS));
	Assert.assertTrue(expectedMFS.containsAll(res));
	Assert.assertEquals(1, session.getExecutedQueryCount());
    }
    
    @Test
    public void testComputesAlphaMFSsAndXSSs() {
	List<Double> listOfAlpha = new ArrayList<>();
	listOfAlpha.add(0.4);
	listOfAlpha.add(0.8);
	AlgoResult result = algo.computesAlphaMFSsAndXSSs(q1, listOfAlpha);
	
	// Test with 0.4
	Set<Query> expectedMFS = new HashSet<>();
	expectedMFS.add(q2);
	expectedMFS.add(q4);
	expectedMFS.add(q5);
	Set<Query> expectedXSS = new HashSet<>();
	expectedXSS.add(q3);
	expectedXSS.add(q6);
	Set<Query> obtainedMFS = result.getAlphaMFSs(0.4);
	Set<Query> obtainedXSS = result.getAlphaXSSs(0.4);
	Assert.assertTrue(obtainedMFS.containsAll(expectedMFS));
	Assert.assertTrue(expectedMFS.containsAll(obtainedMFS));
	Assert.assertTrue(obtainedXSS.containsAll(expectedXSS));
	Assert.assertTrue(expectedXSS.containsAll(obtainedXSS));

	// Test with 0.8
	expectedMFS = new HashSet<>();
	expectedMFS.add(q2);
	expectedMFS.add(q4);
	expectedMFS.add(q3);
	expectedMFS.add(q6);
	expectedXSS = new HashSet<>();
	obtainedMFS = result.getAlphaMFSs(0.8);
	obtainedXSS = result.getAlphaXSSs(0.8);
	Assert.assertTrue(obtainedMFS.containsAll(expectedMFS));
	Assert.assertTrue(expectedMFS.containsAll(obtainedMFS));
	Assert.assertTrue(obtainedXSS.containsAll(expectedXSS));
	Assert.assertTrue(expectedXSS.containsAll(obtainedXSS));
	
	// Test the number of cache hits and executed query
	Assert.assertEquals(5,algo.getNbCacheHits());
	Assert.assertEquals(11,algo.getNbExecutedQuery());
	
	
	// A new test with 4 tresholds
	listOfAlpha = new ArrayList<>();
	listOfAlpha.add(0.4);
	listOfAlpha.add(0.6);
	listOfAlpha.add(0.8);
	listOfAlpha.add(1.0);
	result = algo.computesAlphaMFSsAndXSSs(q1, listOfAlpha);
	
	// Test with 0.4
	expectedMFS = new HashSet<>();
	expectedMFS.add(q2);
	expectedMFS.add(q4);
	expectedMFS.add(q5);
	expectedXSS = new HashSet<>();
	expectedXSS.add(q3);
	expectedXSS.add(q6);	
	obtainedMFS = result.getAlphaMFSs(0.4);
	obtainedXSS = result.getAlphaXSSs(0.4);
	Assert.assertTrue(obtainedMFS.containsAll(expectedMFS));
	Assert.assertTrue(expectedMFS.containsAll(obtainedMFS));
	Assert.assertTrue(obtainedXSS.containsAll(expectedXSS));
	Assert.assertTrue(expectedXSS.containsAll(obtainedXSS));
	
	// Test with 0.6
	obtainedMFS = result.getAlphaMFSs(0.6);
	obtainedXSS = result.getAlphaXSSs(0.6);
	Assert.assertTrue(obtainedMFS.containsAll(expectedMFS));
	Assert.assertTrue(expectedMFS.containsAll(obtainedMFS));
	Assert.assertTrue(obtainedXSS.containsAll(expectedXSS));
	Assert.assertTrue(expectedXSS.containsAll(obtainedXSS));
	
	// Test with 0.8
	expectedMFS = new HashSet<>();
	expectedMFS.add(q2);
	expectedMFS.add(q4);
	expectedMFS.add(q3);
	expectedMFS.add(q6);
	expectedXSS = new HashSet<>();
	obtainedMFS = result.getAlphaMFSs(0.8);
	obtainedXSS = result.getAlphaXSSs(0.8);
	Assert.assertTrue(obtainedMFS.containsAll(expectedMFS));
	Assert.assertTrue(expectedMFS.containsAll(obtainedMFS));
	Assert.assertTrue(obtainedXSS.containsAll(expectedXSS));
	Assert.assertTrue(expectedXSS.containsAll(obtainedXSS));
	
	// Test with 1.0
	obtainedMFS = result.getAlphaMFSs(1.0);
	obtainedXSS = result.getAlphaXSSs(1.0);
	Assert.assertTrue(obtainedMFS.containsAll(expectedMFS));
	Assert.assertTrue(expectedMFS.containsAll(obtainedMFS));
	Assert.assertTrue(obtainedXSS.containsAll(expectedXSS));
	Assert.assertTrue(expectedXSS.containsAll(obtainedXSS));
	
	// Test the number of cache hits and executed queries
	Assert.assertEquals(7,algo.getNbCacheHits());
	Assert.assertEquals(13,algo.getNbExecutedQuery());
	
	// with only one treshold
	listOfAlpha = new ArrayList<>();
	listOfAlpha.add(0.4);
        result = algo.computesAlphaMFSsAndXSSs(q1, listOfAlpha);
	
	// Test with 0.4
	expectedMFS = new HashSet<>();
	expectedMFS.add(q2);
	expectedMFS.add(q4);
	expectedMFS.add(q5);
	expectedXSS = new HashSet<>();
	expectedXSS.add(q3);
	expectedXSS.add(q6);
	obtainedMFS = result.getAlphaMFSs(0.4);
	obtainedXSS = result.getAlphaXSSs(0.4);
	Assert.assertTrue(obtainedMFS.containsAll(expectedMFS));
	Assert.assertTrue(expectedMFS.containsAll(obtainedMFS));
	Assert.assertTrue(obtainedXSS.containsAll(expectedXSS));
	Assert.assertTrue(expectedXSS.containsAll(obtainedXSS));
	Assert.assertEquals(3,algo.getNbCacheHits());
	Assert.assertEquals(9,algo.getNbExecutedQuery());
    }

}
