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
package fr.ensma.lias.qars4ukb.cache;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lias.qars4ukb.SQLScriptRunner;
import fr.ensma.lias.qars4ukb.Session;
import fr.ensma.lias.qars4ukb.query.Query;
import fr.ensma.lias.qars4ukb.query.QueryFactory;
import fr.ensma.lias.qars4ukb.triplestore.jdbcdb.JDBCQueryExtFactory;
import fr.ensma.lias.qars4ukb.triplestore.jdbcdb.JDBCSession;

public class ExtendedCacheLBATest {

    private QueryFactory factoryExt;

    private Session session;

    private Query q1, q2, q3, q4;

    @Before
    public void setUp() throws Exception {
	ExtendedCacheLBA.getInstance().clearCache();
	factoryExt = new JDBCQueryExtFactory();
	session = factoryExt.createSession();
	SQLScriptRunner newScriptRunner = new SQLScriptRunner(((JDBCSession) session).getConnection(), false, false);
	InputStream resourceAsStream = getClass().getResourceAsStream("/test_dataset1.sql");
	newScriptRunner.runScript(new InputStreamReader(resourceAsStream));
	q4 = factoryExt.createQuery(
		"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");
	q1 = factoryExt.createQuery(
		"SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }", q4);
	q2 = factoryExt.createQuery(
		"SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }", q4);
	q3 = factoryExt.createQuery(
		"SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t }",
		q4);
    }

    @Test
    public void testIsSuccessfulByCache() {
	// The query is in the cache with a greater or equals degree => cache
	// hits
	ExtendedCacheLBA.getInstance().addSuccessfulQuery(q1, 0.8);
	Assert.assertTrue(ExtendedCacheLBA.getInstance().isSuccessfulByCache(q1, 0.8));
	Map<Query, Double> obtainedMap = ExtendedCacheLBA.getInstance().getSuccessfulCachedQueries();
	Assert.assertEquals(0.8, (Double) obtainedMap.get(q1), 0.0001);
	Assert.assertTrue(obtainedMap.size() == 1);
	Assert.assertTrue(ExtendedCacheLBA.getInstance().isSuccessfulByCache(q1, 0.7));
	// The query is in the cache with a lower degree => cache miss
	Assert.assertFalse(ExtendedCacheLBA.getInstance().isSuccessfulByCache(q1, 0.81));
	// now we consider the case where the query is not in the cache
	// first there is a superquery with a degree greater or equals => cache
	// hits + add
	ExtendedCacheLBA.getInstance().clearCache();
	ExtendedCacheLBA.getInstance().addSuccessfulQuery(q3, 0.7);
	ExtendedCacheLBA.getInstance().addSuccessfulQuery(q4, 0.6);
	Assert.assertTrue(ExtendedCacheLBA.getInstance().isSuccessfulByCache(q1, 0.5));
	obtainedMap = ExtendedCacheLBA.getInstance().getSuccessfulCachedQueries();
	Assert.assertEquals(0.7, (Double) obtainedMap.get(q1), 0.0001);
	Assert.assertTrue(obtainedMap.size() == 3);
	ExtendedCacheLBA.getInstance().clearCache();
	ExtendedCacheLBA.getInstance().addSuccessfulQuery(q3, 0.7);
	ExtendedCacheLBA.getInstance().addSuccessfulQuery(q4, 0.6);
	Assert.assertTrue(ExtendedCacheLBA.getInstance().isSuccessfulByCache(q1, 0.6));
	obtainedMap = ExtendedCacheLBA.getInstance().getSuccessfulCachedQueries();
	Assert.assertEquals(0.7, (Double) obtainedMap.get(q1), 0.0001);
	Assert.assertTrue(obtainedMap.size() == 3);
	ExtendedCacheLBA.getInstance().clearCache();
	ExtendedCacheLBA.getInstance().addSuccessfulQuery(q3, 0.7);
	ExtendedCacheLBA.getInstance().addSuccessfulQuery(q4, 0.6);
	Assert.assertTrue(ExtendedCacheLBA.getInstance().isSuccessfulByCache(q1, 0.7));
	obtainedMap = ExtendedCacheLBA.getInstance().getSuccessfulCachedQueries();
	Assert.assertEquals(0.7, (Double) obtainedMap.get(q1), 0.0001);
	Assert.assertTrue(obtainedMap.size() == 3);
	ExtendedCacheLBA.getInstance().clearCache();
	ExtendedCacheLBA.getInstance().addSuccessfulQuery(q3, 0.7);
	ExtendedCacheLBA.getInstance().addSuccessfulQuery(q4, 0.6);
	Assert.assertFalse(ExtendedCacheLBA.getInstance().isSuccessfulByCache(q1, 0.8));
	obtainedMap = ExtendedCacheLBA.getInstance().getSuccessfulCachedQueries();
	Assert.assertTrue(obtainedMap.size() == 2);

	// second there is a superquery with a lower degree => cache miss + no
	// add
	ExtendedCacheLBA.getInstance().clearCache();
	ExtendedCacheLBA.getInstance().addSuccessfulQuery(q3, 0.7);
	ExtendedCacheLBA.getInstance().addSuccessfulQuery(q4, 0.6);
	Assert.assertFalse(ExtendedCacheLBA.getInstance().isSuccessfulByCache(q1, 0.8));
	obtainedMap = ExtendedCacheLBA.getInstance().getSuccessfulCachedQueries();
	Assert.assertNull(obtainedMap.get(q1));
	Assert.assertTrue(obtainedMap.size() == 2);

	// last there is no superquery => cache miss
	ExtendedCacheLBA.getInstance().clearCache();
	ExtendedCacheLBA.getInstance().addSuccessfulQuery(q2, 0.7);
	Assert.assertFalse(ExtendedCacheLBA.getInstance().isSuccessfulByCache(q1, 0.6));
	obtainedMap = ExtendedCacheLBA.getInstance().getSuccessfulCachedQueries();
	Assert.assertNull(obtainedMap.get(q1));
	Assert.assertTrue(obtainedMap.size() == 1);
    }

    @Test
    public void testCacheWithIsFailing() {
	q1.isFailing(session, 0.8);
	Assert.assertTrue(ExtendedCacheLBA.getInstance().isFailingByCache(q1, 1.0));
	Assert.assertTrue(ExtendedCacheLBA.getInstance().isFailingByCache(q1, 0.9));
	Assert.assertTrue(ExtendedCacheLBA.getInstance().isFailingByCache(q1, 0.8));
	Assert.assertFalse(ExtendedCacheLBA.getInstance().isFailingByCache(q1, 0.7));
	q1.isFailing(session, 0.4);
	Assert.assertTrue(ExtendedCacheLBA.getInstance().isSuccessfulByCache(q1, 0.3));
	Assert.assertTrue(ExtendedCacheLBA.getInstance().isSuccessfulByCache(q1, 0.2));
	Assert.assertTrue(ExtendedCacheLBA.getInstance().isSuccessfulByCache(q1, 0.1));
	Assert.assertFalse(ExtendedCacheLBA.getInstance().isSuccessfulByCache(q1, 0.5));
	// we check a more complete example
	ExtendedCacheLBA.getInstance().clearCache();
	q4.isFailing(session, 0.8);
	q3.isFailing(session, 0.7);
	Assert.assertTrue(ExtendedCacheLBA.getInstance().isFailingByCache(q4, 0.7));
	// check the number of cache hits
	ExtendedCacheLBA.getInstance().clearCache();
	q4.isFailing(session, 0.4);
	q3.isFailing(session, 0.4);
	q2.isFailing(session, 0.4);
	q1.isFailing(session, 0.4);
	q4.isFailing(session, 0.8); // cache hit
	q3.isFailing(session, 0.8); // cache hit
	q2.isFailing(session, 0.8); // cache hit
	q1.isFailing(session, 0.8);
	Assert.assertEquals(3, ExtendedCacheLBA.getInstance().getNbCacheHits());
	q4.isFailing(session, 0.9); // cache hit
	q3.isFailing(session, 0.9); // cache hit
	q2.isFailing(session, 0.9); // cache hit
	q1.isFailing(session, 0.9); // cache hit
	Assert.assertEquals(7, ExtendedCacheLBA.getInstance().getNbCacheHits());
    }

    @Test
    public void testIsFailingByCache() {
	// Test different adding to the cache
	ExtendedCacheLBA.getInstance().addFailingQuery(q1, false, 0.8);
	Assert.assertTrue(ExtendedCacheLBA.getInstance().isFailingByCache(q1, 0.9));
	Map<Query, Double> obtainedMap = ExtendedCacheLBA.getInstance().getFailingCachedQueries();
	Assert.assertEquals(0.8, (Double) obtainedMap.get(q1), 0.0001);
	Assert.assertTrue(obtainedMap.size() == 1);
	Assert.assertTrue(ExtendedCacheLBA.getInstance().isFailingByCache(q1, 0.95));
	obtainedMap = ExtendedCacheLBA.getInstance().getFailingCachedQueries();
	Assert.assertEquals(0.8, (Double) obtainedMap.get(q1), 0.0001);
	Assert.assertTrue(obtainedMap.size() == 1);
	Assert.assertFalse(ExtendedCacheLBA.getInstance().isFailingByCache(q1, 0.7));
	obtainedMap = ExtendedCacheLBA.getInstance().getFailingCachedQueries();
	Assert.assertEquals(0.8, (Double) obtainedMap.get(q1), 0.0001);
	// not changed (not added yet)
	Assert.assertTrue(obtainedMap.size() == 1);

	// q3 is a superquery of q1
	Assert.assertTrue(ExtendedCacheLBA.getInstance().isFailingByCache(q3, 0.9));
	obtainedMap = ExtendedCacheLBA.getInstance().getFailingCachedQueries();
	Assert.assertEquals(0.8, (Double) obtainedMap.get(q1), 0.0001);
	Assert.assertEquals(0.8, (Double) obtainedMap.get(q3), 0.0001);
	Assert.assertTrue(obtainedMap.size() == 2);
	// test with several subqueries (q4 is the superquery of q1 and q2)
	ExtendedCacheLBA.getInstance().clearCache();
	ExtendedCacheLBA.getInstance().addFailingQuery(q1, false, 0.8);
	ExtendedCacheLBA.getInstance().addFailingQuery(q2, false, 0.4);
	obtainedMap = ExtendedCacheLBA.getInstance().getFailingCachedQueries();
	Assert.assertEquals(0.8, (Double) obtainedMap.get(q1), 0.0001);
	Assert.assertEquals(0.4, (Double) obtainedMap.get(q2), 0.0001);
	// q4 is a superquery of q1 and q2
	Assert.assertTrue(ExtendedCacheLBA.getInstance().isFailingByCache(q4, 0.7));
	obtainedMap = ExtendedCacheLBA.getInstance().getFailingCachedQueries();
	Assert.assertEquals(0.8, (Double) obtainedMap.get(q1), 0.0001);
	Assert.assertEquals(0.4, (Double) obtainedMap.get(q2), 0.0001);
	Assert.assertEquals(0.4, (Double) obtainedMap.get(q4), 0.0001);
	Assert.assertTrue(obtainedMap.size() == 3);
	Assert.assertFalse(ExtendedCacheLBA.getInstance().isFailingByCache(q1, 0.3));
	ExtendedCacheLBA.getInstance().addFailingQuery(q1, false, 0.3);
	obtainedMap = ExtendedCacheLBA.getInstance().getFailingCachedQueries();
	Assert.assertEquals(0.3, (Double) obtainedMap.get(q1), 0.0001);
	Assert.assertEquals(0.4, (Double) obtainedMap.get(q2), 0.0001);
	Assert.assertEquals(0.3, (Double) obtainedMap.get(q4), 0.0001);
	Assert.assertTrue(obtainedMap.size() == 3);

	// Test with super and subquery
	ExtendedCacheLBA.getInstance().clearCache();
	ExtendedCacheLBA.getInstance().addFailingQuery(q1, false, 0.8);
	ExtendedCacheLBA.getInstance().addFailingQuery(q2, false, 0.4);
	// q3 is a superquery of q1 and a subquery of q4
	ExtendedCacheLBA.getInstance().addFailingQuery(q3, false, 0.2);
	Assert.assertTrue(ExtendedCacheLBA.getInstance().isFailingByCache(q4, 0.4));
	obtainedMap = ExtendedCacheLBA.getInstance().getFailingCachedQueries();
	Assert.assertEquals(0.8, (Double) obtainedMap.get(q1), 0.0001);
	Assert.assertEquals(0.4, (Double) obtainedMap.get(q2), 0.0001);
	Assert.assertEquals(0.2, (Double) obtainedMap.get(q3), 0.0001);
	Assert.assertEquals(0.2, (Double) obtainedMap.get(q4), 0.0001);
	Assert.assertTrue(obtainedMap.size() == 4);
	// Test with several superqueries
	ExtendedCacheLBA.getInstance().clearCache();
	ExtendedCacheLBA.getInstance().addFailingQuery(q4, false, 0.6);
	ExtendedCacheLBA.getInstance().addFailingQuery(q3, false, 0.5);
	ExtendedCacheLBA.getInstance().addFailingQuery(q1, false, 0.4);
	ExtendedCacheLBA.getInstance().addFailingQuery(q2, false, 0.3);
	obtainedMap = ExtendedCacheLBA.getInstance().getFailingCachedQueries();
	Assert.assertEquals(0.4, (Double) obtainedMap.get(q1), 0.0001);
	Assert.assertEquals(0.3, (Double) obtainedMap.get(q2), 0.0001);
	Assert.assertEquals(0.4, (Double) obtainedMap.get(q3), 0.0001);
	Assert.assertEquals(0.3, (Double) obtainedMap.get(q4), 0.0001);
    }

    @Test
    public void testAddFailingQuery() {
	// just add the query with a lower degree
	ExtendedCacheLBA.getInstance().addFailingQuery(q1, false, 0.8);
	Map<Query, Double> obtainedMap = ExtendedCacheLBA.getInstance().getFailingCachedQueries();
	Assert.assertEquals(0.8, (Double) obtainedMap.get(q1), 0.0001);
	ExtendedCacheLBA.getInstance().addFailingQuery(q1, false, 0.7);
	obtainedMap = ExtendedCacheLBA.getInstance().getFailingCachedQueries();
	Assert.assertEquals(0.7, (Double) obtainedMap.get(q1), 0.0001);
	ExtendedCacheLBA.getInstance().addFailingQuery(q1, false, 0.6);
	obtainedMap = ExtendedCacheLBA.getInstance().getFailingCachedQueries();
	Assert.assertEquals(0.6, (Double) obtainedMap.get(q1), 0.0001);
	// test with a superquery that have a lower degree
	ExtendedCacheLBA.getInstance().clearCache();
	ExtendedCacheLBA.getInstance().addFailingQuery(q1, false, 0.8);
	ExtendedCacheLBA.getInstance().addFailingQuery(q3, false, 0.7);
	obtainedMap = ExtendedCacheLBA.getInstance().getFailingCachedQueries();
	Assert.assertEquals(0.8, (Double) obtainedMap.get(q1), 0.0001);
	Assert.assertEquals(0.7, (Double) obtainedMap.get(q3), 0.0001);
	Assert.assertTrue(obtainedMap.size() == 2);
	ExtendedCacheLBA.getInstance().addFailingQuery(q1, false, 0.5);
	obtainedMap = ExtendedCacheLBA.getInstance().getFailingCachedQueries();
	Assert.assertEquals(0.5, (Double) obtainedMap.get(q1), 0.0001);
	Assert.assertEquals(0.5, (Double) obtainedMap.get(q3), 0.0001);
	// test with several queries
	ExtendedCacheLBA.getInstance().clearCache();
	ExtendedCacheLBA.getInstance().addFailingQuery(q1, false, 0.8);
	ExtendedCacheLBA.getInstance().addFailingQuery(q2, false, 0.4);
	obtainedMap = ExtendedCacheLBA.getInstance().getFailingCachedQueries();
	Assert.assertEquals(0.8, (Double) obtainedMap.get(q1), 0.0001);
	Assert.assertEquals(0.4, (Double) obtainedMap.get(q2), 0.0001);
	// q3 is a superquery of q1 and a subquery of q4
	ExtendedCacheLBA.getInstance().addFailingQuery(q3, false, 0.2);
	ExtendedCacheLBA.getInstance().addFailingQuery(q4, false, 0.1);
	obtainedMap = ExtendedCacheLBA.getInstance().getFailingCachedQueries();
	Assert.assertEquals(0.8, (Double) obtainedMap.get(q1), 0.0001);
	Assert.assertEquals(0.4, (Double) obtainedMap.get(q2), 0.0001);
	Assert.assertEquals(0.2, (Double) obtainedMap.get(q3), 0.0001);
	Assert.assertEquals(0.1, (Double) obtainedMap.get(q4), 0.0001);
	Assert.assertTrue(obtainedMap.size() == 4);
	// test the need to update the superqueries
	ExtendedCacheLBA.getInstance().clearCache();
	ExtendedCacheLBA.getInstance().addFailingQuery(q1, false, 0.8);
	ExtendedCacheLBA.getInstance().addFailingQuery(q4, false, 0.6);
	ExtendedCacheLBA.getInstance().addFailingQuery(q3, false, 0.5);
	obtainedMap = ExtendedCacheLBA.getInstance().getFailingCachedQueries();
	Assert.assertEquals(0.8, (Double) obtainedMap.get(q1), 0.0001);
	Assert.assertEquals(0.5, (Double) obtainedMap.get(q4), 0.0001);
	Assert.assertEquals(0.5, (Double) obtainedMap.get(q3), 0.0001);
	// again but with several subqueries
	ExtendedCacheLBA.getInstance().addFailingQuery(q4, false, 0.6);
	ExtendedCacheLBA.getInstance().addFailingQuery(q3, false, 0.5);
	obtainedMap = ExtendedCacheLBA.getInstance().getFailingCachedQueries();
	Assert.assertEquals(0.5, (Double) obtainedMap.get(q4), 0.0001);
	Assert.assertEquals(0.5, (Double) obtainedMap.get(q3), 0.0001);
	ExtendedCacheLBA.getInstance().addFailingQuery(q1, false, 0.3);
	obtainedMap = ExtendedCacheLBA.getInstance().getFailingCachedQueries();
	Assert.assertEquals(0.3, (Double) obtainedMap.get(q1), 0.0001);
	Assert.assertEquals(0.3, (Double) obtainedMap.get(q4), 0.0001);
	Assert.assertEquals(0.3, (Double) obtainedMap.get(q3), 0.0001);

    }

    @Test
    public void testAddSuccessfulQuery() {
	// add a query not in the cache with no subqueries
	ExtendedCacheLBA.getInstance().addSuccessfulQuery(q1, 0.4);
	Map<Query, Double> obtainedMap = ExtendedCacheLBA.getInstance().getSuccessfulCachedQueries();
	Assert.assertEquals(0.4, (Double) obtainedMap.get(q1), 0.0001);
	// now we add it again with a greater degree (it's not correct to give a
	// lower degree)
	ExtendedCacheLBA.getInstance().addSuccessfulQuery(q1, 0.5);
	obtainedMap = ExtendedCacheLBA.getInstance().getSuccessfulCachedQueries();
	Assert.assertEquals(0.5, (Double) obtainedMap.get(q1), 0.0001);
	// another time
	ExtendedCacheLBA.getInstance().addSuccessfulQuery(q1, 0.6);
	obtainedMap = ExtendedCacheLBA.getInstance().getSuccessfulCachedQueries();
	Assert.assertEquals(0.6, (Double) obtainedMap.get(q1), 0.0001);

	// now consider some cases with subqueries
	ExtendedCacheLBA.getInstance().clearCache();
	ExtendedCacheLBA.getInstance().addSuccessfulQuery(q1, 0.4);
	ExtendedCacheLBA.getInstance().addSuccessfulQuery(q3, 0.5);
	obtainedMap = ExtendedCacheLBA.getInstance().getSuccessfulCachedQueries();
	Assert.assertEquals(0.5, (Double) obtainedMap.get(q1), 0.0001);
	Assert.assertEquals(0.5, (Double) obtainedMap.get(q3), 0.0001);
	ExtendedCacheLBA.getInstance().addSuccessfulQuery(q4, 0.2);
	Assert.assertEquals(0.5, (Double) obtainedMap.get(q1), 0.0001);
	Assert.assertEquals(0.5, (Double) obtainedMap.get(q3), 0.0001);
	Assert.assertEquals(0.2, (Double) obtainedMap.get(q4), 0.0001);
	ExtendedCacheLBA.getInstance().addSuccessfulQuery(q4, 0.6);
	Assert.assertEquals(0.6, (Double) obtainedMap.get(q1), 0.0001);
	Assert.assertEquals(0.6, (Double) obtainedMap.get(q3), 0.0001);
	Assert.assertEquals(0.6, (Double) obtainedMap.get(q4), 0.0001);

    }
}
