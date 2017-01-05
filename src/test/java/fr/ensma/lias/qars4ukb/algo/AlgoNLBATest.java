/*********************************************************************************
* This file is part of QARS4UKB Project.
* Copyright (C) 2017 LIAS - ENSMA
*   Teleport 2 - 1 avenue Clement Ader
*   BP 40109 - 86961 Futuroscope Chasseneuil Cedex - FRANCE
* 
* QARS4UKB is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* QARS4UKB is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with QARS4UKB.  If not, see <http://www.gnu.org/licenses/>.
**********************************************************************************/
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
import fr.ensma.lias.qars4ukb.query.Query;
import fr.ensma.lias.qars4ukb.query.QueryFactory;
import fr.ensma.lias.qars4ukb.triplestore.jdbcdb.JDBCQueryOptFactory;
import fr.ensma.lias.qars4ukb.triplestore.jdbcdb.JDBCSession;

/**
 * @author Stéphane JEAN
 */
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
	q4 = factoryOpt
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q5 = factoryOpt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }");
	q3 = factoryOpt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }");
	q6 = factoryOpt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }");
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

	// compare it to the result of DFS
	q1.runDFS(session, 0.4);
	expectedMFS = q1.getAllMFS();
	expectedXSS = q1.getAllXSS();
	Assert.assertTrue(obtainedMFS.containsAll(expectedMFS));
	Assert.assertTrue(expectedMFS.containsAll(obtainedMFS));
	Assert.assertTrue(obtainedXSS.containsAll(expectedXSS));
	Assert.assertTrue(expectedXSS.containsAll(obtainedXSS));

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
	Assert.assertEquals(3, algo.getNbCacheHits());
	Assert.assertEquals(18, algo.getNbExecutedQuery());

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
