package fr.ensma.lias.qars4ukb.algo;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lias.qars4ukb.SQLScriptRunner;
import fr.ensma.lias.qars4ukb.Session;
import fr.ensma.lias.qars4ukb.query.Query;
import fr.ensma.lias.qars4ukb.query.QueryFactory;
import fr.ensma.lias.qars4ukb.triplestore.jdbcdb.JDBCQueryExtFactory;
import fr.ensma.lias.qars4ukb.triplestore.jdbcdb.JDBCSession;

public class AlgoBottomUpTest {

    IAlgo algo;
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
	
    }
    
    @Test
    public void testComputesAlphaMFSsAndXSSs() {
	List<Double> listOfAlpha = new ArrayList<>();
	listOfAlpha.add(0.4);
	listOfAlpha.add(0.8);
	AlgoResult result = algo.computesAlphaMFSsAndXSSs(q1, listOfAlpha);
	
	q2 = factoryExt
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }");
	q4 = factoryExt.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q5 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }");
	q3 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }");
	q6 = factoryExt.createQuery(
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
	

	// Test with 0.8
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
	Assert.assertEquals(10,algo.getNbCacheHits());
	Assert.assertEquals(11,algo.getNbExecutedQuery());	
	
    }

}
