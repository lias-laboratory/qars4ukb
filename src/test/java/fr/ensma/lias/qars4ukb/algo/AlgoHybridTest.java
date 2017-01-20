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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

/**
 * @author Stéphane JEAN
 */
public class AlgoHybridTest {

    AlgoHybrid algo;

    private QueryFactory factoryExt;

    private Session session;

    private Query q1, q2, q3, q4, q5, q6;

    @Before
    public void setup() throws Exception {
	factoryExt = new JDBCQueryExtFactory();
	session = factoryExt.createSession();
	algo = new AlgoHybrid(session);
	SQLScriptRunner newScriptRunner = new SQLScriptRunner(((JDBCSession) session).getConnection(), false, false);
	InputStream resourceAsStream = getClass().getResourceAsStream("/test_dataset1.sql");
	newScriptRunner.runScript(new InputStreamReader(resourceAsStream));
    }

    @Test
    public void testGetCommonQuery() {
	// **************** given ******************
	q1 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q2 = factoryExt
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }");
	q3 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }");
	q4 = factoryExt
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q5 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }");
	q6 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }");
	Set<Query> firstList = new HashSet<>();
	firstList.add(q1);
	firstList.add(q2);
	firstList.add(q3);
	firstList.add(q6);
	Set<Query> secondList = new HashSet<>();
	secondList.add(q2);
	secondList.add(q4);
	secondList.add(q6);

	// **************** when ******************
	Set<Query> res = algo.getCommonQueries(firstList, secondList);

	// **************** then ******************
	assertEquals(res.size(), 2);
	assertTrue(res.contains(q2));
	assertTrue(res.contains(q6));

    }

    @Test
    public void testRemoveASetOfQuery() {
	q1 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q2 = factoryExt
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }");
	q3 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }");
	q4 = factoryExt
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q5 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }");
	q6 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }");
	Set<Query> initial = new HashSet<>();
	initial.add(q1);
	initial.add(q2);
	initial.add(q3);
	initial.add(q6);
	Set<Query> toRemove = new HashSet<>();
	toRemove.add(q2);
	toRemove.add(q4);
	toRemove.add(q6);

	// **************** when ******************
	Set<Query> res = algo.removeASetOfQueries(initial, toRemove);

	// **************** then ******************
	assertEquals(res.size(), 2);
	assertFalse(res.contains(q2));
	assertFalse(res.contains(q6));
	assertTrue(res.contains(q1));
	assertTrue(res.contains(q3));
    }

    @Test
    public void testGetAtomicQueries() {
	q1 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q2 = factoryExt
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }");
	q3 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }");
	q4 = factoryExt
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q5 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }");
	q6 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }");
	Set<Query> initial = new HashSet<>();
	initial.add(q1);
	initial.add(q2);
	initial.add(q3);
	initial.add(q4);
	initial.add(q5);
	initial.add(q6);

	// **************** when ******************
	Set<Query> res = algo.getAtomicQueries(initial);

	// **************** then ******************
	assertEquals(res.size(), 4);
	assertFalse(res.contains(q1));
	assertFalse(res.contains(q5));
	assertTrue(res.contains(q2));
	assertTrue(res.contains(q3));
	assertTrue(res.contains(q4));
	assertTrue(res.contains(q6));
    }

    @Test
    public void testGetProperSubQueries() {
	q1 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q2 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }",
		q1);
	q3 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }",
		q1);
	q4 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }",
		q1);
	q5 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }",
		q1);
	q6 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }",
		q1);
	Set<Query> initial = new HashSet<>();

	initial.add(q2);
	initial.add(q3);
	initial.add(q4);
	initial.add(q5);
	initial.add(q6);

	// **************** when ******************
	Set<Query> res = algo.getProperSubQueries(q1, initial);

	// **************** then ******************
	assertEquals(res.size(), 3);
	assertFalse(res.contains(q1));
	assertFalse(res.contains(q4));
	assertFalse(res.contains(q5));
	assertTrue(res.contains(q2));
	assertTrue(res.contains(q3));
	assertTrue(res.contains(q6));
    }

    @Test
    public void testRemoveQueriesIncludingAtomic() {
	// **************** given ******************
	q1 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q2 = factoryExt
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }");
	q3 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }");
	q4 = factoryExt
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q5 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }");
	q6 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");

	Set<Query> initial = new HashSet<>();
	initial.add(q1);
	initial.add(q2);
	initial.add(q5);
	initial.add(q6);

	Set<Query> atomic = new HashSet<>();
	atomic.add(q2);
	atomic.add(q4);

	// **************** when ******************
	Set<Query> res = algo.removeQueriesIncludingAQuery(initial, atomic);

	// **************** then ******************
	System.out.println(q5.includes(q2));
	assertEquals(res.size(), 1);
	assertTrue(res.contains(q5));

    }

    @Test
    public void testRemoveQueriesIncludedInProperSubQueries() {
	// **************** given ******************
	q1 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q2 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }",
		q1);
	q3 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }",
		q1);
	q4 = factoryExt
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }", q1);
	q5 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }",
		q1);
	q6 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }",
		q1);

	Set<Query> proper = new HashSet<>();
	proper.add(q2);
	proper.add(q3);

	Set<Query> queries = new HashSet<>();
	queries.add(q4);
	queries.add(q5);
	queries.add(q6);

	// **************** when ******************
	Set<Query> res = algo.removeQueriesIncludedInQuery(proper, queries);

	// **************** then ******************
	assertEquals(res.size(), 1);
	assertTrue(res.contains(q6));

    }

    @Test
    public void testGetSuccessXss() {
	// **************** given ******************
	q1 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q3 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }", q1);
	q6 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }", q1);

	ExtendedCacheLBA.getInstance().clearCache();
	session.clearExecutedQueryCount();
	Set<Query> discoveredXSS = new HashSet<>();
	discoveredXSS.add(q3);
	discoveredXSS.add(q6);
	// **************** when ******************
	Set<Query> res = algo.getSuccessXSS(discoveredXSS, 0.5, session);

	// **************** then ******************
	Assert.assertTrue(res.containsAll(discoveredXSS));
	Assert.assertTrue(discoveredXSS.containsAll(res));
	Assert.assertEquals(2, session.getExecutedQueryCount());

	// **************** given ******************
	ExtendedCacheLBA.getInstance().clearCache();
	session.clearExecutedQueryCount();
	discoveredXSS = new HashSet<>();
	discoveredXSS.add(q3);
	discoveredXSS.add(q6);

	// **************** when ******************
	res = algo.getSuccessXSS(discoveredXSS, 0.7, session);

	// **************** then ******************
	Assert.assertTrue(res.isEmpty());
	Assert.assertEquals(2, session.getExecutedQueryCount());
    }

    @Test
    public void testGetFailingMFS() {
	// **************** given ******************
	q1 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q3 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }", q1);
	q6 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }", q1);
	ExtendedCacheLBA.getInstance().clearCache();
	session.clearExecutedQueryCount();
	Set<Query> discoveredMFS = new HashSet<>();
	discoveredMFS.add(q3);
	discoveredMFS.add(q6);

	// **************** when ******************
	Set<Query> res = algo.GetFailingMFS(discoveredMFS, 0.7, session);

	// **************** then ******************
	Assert.assertTrue(res.containsAll(discoveredMFS));
	Assert.assertTrue(discoveredMFS.containsAll(res));
	Assert.assertEquals(2, session.getExecutedQueryCount());

	// **************** given ******************
	ExtendedCacheLBA.getInstance().clearCache();
	session.clearExecutedQueryCount();
	discoveredMFS = new HashSet<>();
	discoveredMFS.add(q3);
	discoveredMFS.add(q6);

	// **************** when ******************
	res = algo.GetFailingMFS(discoveredMFS, 0.5, session);

	// **************** then ******************
	Assert.assertTrue(res.isEmpty());
	Assert.assertEquals(2, session.getExecutedQueryCount());
    }

    @Test
    public void TestFindAnMFSInEachQuery() {

	// **************** given ******************
	// Q1 is MFS for the 0.5 degree
	q1 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }");
	// Q2 0.7 is MFS for the 0.7 degree
	q2 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }");
	ExtendedCacheLBA.getInstance().clearCache();
	session.clearExecutedQueryCount();
	Set<Query> discoveredMFS = new HashSet<>();
	discoveredMFS.add(q1);
	Set<Query> expectedMFS = new HashSet<>();
	expectedMFS.add(q2);

	// **************** when ******************
	Set<Query> res = algo.findAnMFSInEachQuery(discoveredMFS, 0.7, session);

	// **************** then ******************
	Assert.assertTrue(res.containsAll(expectedMFS));
	Assert.assertTrue(expectedMFS.containsAll(res));
	Assert.assertEquals(1, session.getExecutedQueryCount());
    }

    @Test
    public void testfindAnXSSIfgcnEachQuery() {
	// **************** given ******************

	// Q1 is the initial query
	q1 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	// Q2 is XSS for the 0.6 degree
	q2 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }", q1);
	// Q3 is XSS for the 0.6 degree
	q3 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }", q1);
	ExtendedCacheLBA.getInstance().clearCache();
	session.clearExecutedQueryCount();
	Set<Query> discoveredXSS = new HashSet<>();
	discoveredXSS.add(q2);
	discoveredXSS.add(q3);
	Set<Query> res = algo.findAnXSSInEachQuery(q1, discoveredXSS, 0.4, session);
	Set<Query> expectedXSS = new HashSet<>();
	expectedXSS.add(q2);
	expectedXSS.add(q3);
	Assert.assertTrue(res.containsAll(expectedXSS));
	Assert.assertTrue(expectedXSS.containsAll(res));
	Assert.assertEquals(5, session.getExecutedQueryCount());
	Assert.assertEquals(1, ExtendedCacheLBA.getInstance().getNbCacheHits());

	ExtendedCacheLBA.getInstance().clearCache();
	session.clearExecutedQueryCount();
	discoveredXSS = new HashSet<>();
	res = algo.findAnXSSInEachQuery(q1, discoveredXSS, 0.7, session);
	expectedXSS = new HashSet<>();
	Assert.assertTrue(res.containsAll(expectedXSS));
	Assert.assertTrue(expectedXSS.containsAll(res));
    }

    @Test
    public void TestexecutionOrderAux() {
	// **************** given ******************

	List<Double> listOfAlpha1 = new ArrayList<>();
	List<Double> listOfAlpha2 = new ArrayList<>();
	List<Double> listOfAlpha3 = new ArrayList<>();
	List<Double> listOfAlpha4 = new ArrayList<>();
	List<Double> listOfAlpha5 = new ArrayList<>();
	List<Double> listOfAlpha6 = new ArrayList<>();
	listOfAlpha1.add(0.2);

	listOfAlpha2.add(0.2);
	listOfAlpha2.add(0.4);

	listOfAlpha3.add(0.2);
	listOfAlpha3.add(0.4);
	listOfAlpha3.add(0.6);

	listOfAlpha4.add(0.2);
	listOfAlpha4.add(0.4);
	listOfAlpha4.add(0.6);
	listOfAlpha4.add(0.8);

	listOfAlpha5.add(0.2);
	listOfAlpha5.add(0.4);
	listOfAlpha5.add(0.6);
	listOfAlpha5.add(0.8);
	listOfAlpha5.add(1.0);

	listOfAlpha6.add(0.0);
	listOfAlpha6.add(0.2);
	listOfAlpha6.add(0.4);
	listOfAlpha6.add(0.6);
	listOfAlpha6.add(0.8);
	listOfAlpha6.add(1.0);

	List<HybridAlgorithmElement> res1 = new ArrayList<>();
	List<HybridAlgorithmElement> expecttedRes1 = new ArrayList<>();
	expecttedRes1.add(new HybridAlgorithmElement(0.2, null, null));
	List<HybridAlgorithmElement> res2 = new ArrayList<>();
	List<HybridAlgorithmElement> res3 = new ArrayList<>();
	List<HybridAlgorithmElement> res4 = new ArrayList<>();
	List<HybridAlgorithmElement> res5 = new ArrayList<>();

	// **************** when ******************
	algo.executionOrder(res1, listOfAlpha1);
	algo.executionOrder(res2, listOfAlpha2);
	algo.executionOrder(res3, listOfAlpha3);
	algo.executionOrder(res4, listOfAlpha4);
	algo.executionOrder(res5, listOfAlpha5);
	// **************** then ******************
	assertTrue(res1.size() == 1);
	assertTrue(res1.get(0).getAlpha() == 0.2);
	assertTrue(res1.get(0).getLeft() == null);
	assertTrue(res1.get(0).getRight() == null);

	assertTrue(res2.size() == 2);
	assertTrue(res2.get(0).getAlpha() == 0.2);
	assertTrue(res2.get(0).getLeft() == null);
	assertTrue(res2.get(0).getRight() == null);
	assertTrue(res2.get(1).getAlpha() == 0.4);
	assertTrue(res2.get(1).getLeft() == 0.2);
	assertTrue(res2.get(1).getRight() == null);

	assertTrue(res3.size() == 3);
	assertTrue(res3.get(0).getAlpha() == 0.2);
	assertTrue(res3.get(0).getLeft() == null);
	assertTrue(res3.get(0).getRight() == null);
	assertTrue(res3.get(1).getAlpha() == 0.6);
	assertTrue(res3.get(1).getLeft() == 0.2);
	assertTrue(res3.get(1).getRight() == null);
	assertTrue(res3.get(2).getAlpha() == 0.4);
	assertTrue(res3.get(2).getLeft() == 0.2);
	assertTrue(res3.get(2).getRight() == 0.6);

	assertTrue(res4.size() == 4);
	assertTrue(res4.get(0).getAlpha() == 0.2);
	assertTrue(res4.get(0).getLeft() == null);
	assertTrue(res4.get(0).getRight() == null);
	assertTrue(res4.get(1).getAlpha() == 0.8);
	assertTrue(res4.get(1).getLeft() == 0.2);
	assertTrue(res4.get(1).getRight() == null);
	assertTrue(res4.get(2).getAlpha() == 0.4);
	assertTrue(res4.get(2).getLeft() == 0.2);
	assertTrue(res4.get(3).getRight() == 0.8);
	assertTrue(res4.get(3).getAlpha() == 0.6);
	assertTrue(res4.get(3).getLeft() == 0.4);
	assertTrue(res4.get(3).getRight() == 0.8);

	assertTrue(res5.size() == 5);
	assertTrue(res5.get(0).getAlpha() == 0.2);
	assertTrue(res5.get(0).getLeft() == null);
	assertTrue(res5.get(0).getRight() == null);
	assertTrue(res5.get(1).getAlpha() == 1.0);
	assertTrue(res5.get(1).getLeft() == 0.2);
	assertTrue(res5.get(1).getRight() == null);
	assertTrue(res5.get(2).getAlpha() == 0.6);
	assertTrue(res5.get(2).getLeft() == 0.2);
	assertTrue(res5.get(2).getRight() == 1.0);
	assertTrue(res5.get(3).getAlpha() == 0.4);
	assertTrue(res5.get(3).getLeft() == 0.2);
	assertTrue(res5.get(3).getRight() == 0.6);
	assertTrue(res5.get(4).getAlpha() == 0.8);
	assertTrue(res5.get(4).getLeft() == 0.6);
	assertTrue(res5.get(4).getRight() == 1.0);

    }

    @Test
    public void testComputesAlphaMFSsAndXSSs() {
	q1 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q2 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }", q1);
	q4 = factoryExt
		.createQuery("SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }", q1);
	q5 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }",
		q1);
	q3 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }", q1);
	q6 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }", q1);
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
	Assert.assertEquals(5, algo.getNbCacheHits());
	Assert.assertEquals(11, algo.getNbExecutedQuery());

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
	// Assert.assertEquals(10,algo.getNbCacheHits());
	// Assert.assertEquals(21,algo.getNbExecutedQuery());
	System.out.println(algo.getNbCacheHits());
	System.out.println(algo.getNbExecutedQuery());
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
	Assert.assertEquals(3, algo.getNbCacheHits());
	Assert.assertEquals(9, algo.getNbExecutedQuery());
    }

}
