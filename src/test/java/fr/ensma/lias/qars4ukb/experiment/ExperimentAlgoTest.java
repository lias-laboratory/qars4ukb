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
package fr.ensma.lias.qars4ukb.experiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.tdb.TDB;
import org.junit.Assert;
import org.junit.Test;

import fr.ensma.lias.qars4ukb.algo.AlgoBottomUp;
import fr.ensma.lias.qars4ukb.algo.AlgoHybrid;
import fr.ensma.lias.qars4ukb.algo.AlgoNLBA;
import fr.ensma.lias.qars4ukb.algo.AlgoResult;
import fr.ensma.lias.qars4ukb.algo.AlgoTopDown;
import fr.ensma.lias.qars4ukb.algo.IAlgo;
import fr.ensma.lias.qars4ukb.query.Query;
import fr.ensma.lias.qars4ukb.query.QueryFactory;
import fr.ensma.lias.qars4ukb.triplestore.jenatdbgraph.JenaTDBGraphQueryExtFactory;
import fr.ensma.lias.qars4ukb.triplestore.jenatdbgraph.JenaTDBGraphQueryOptFactory;
import fr.ensma.lias.qars4ukb.triplestore.sparqlendpoint.SPARQLEndpointQueryExtFactory;
import fr.ensma.lias.qars4ukb.triplestore.sparqlendpoint.SPARQLEndpointQueryOptFactory;

/**
 * @author Stéphane JEAN
 */
public class ExperimentAlgoTest {

    /**
     * File containing all the queries
     */
    private static final String FILE_QUERIES = "queries-icwe2017.test";

    private static final String FILE_QUERY_EXP2 = "query-icwe2017-exp2.test";

    /**
     * number of execution of each algorithm for a given query
     */
    private static final int NB_EXEC = 5;

    /**
     * 
     */
    private List<Double> listOfAlpha = Arrays.asList(0.2, 0.4, 0.6, 0.8);

    /**
     * Factory to create the queries
     */
    protected QueryFactory factory;

    /**
     * The used platform
     */
    private static final String PLAT_VIRTUOSO = "VIRTUOSO";
    private static final String PLAT_JENA = "JENA";

    /**
     * The algorithm launched
     */
    private IAlgo algo = null;

    /**
     * The 4 algorithms to compute MFSs and XSSs for several thresholds
     */
    public enum Algorithm {
	NLBA, TOPDOWN, BOTTOMUP, HYBRID
    }

    /*************************
     * Jena
     *************************/

    @Test
    public void testJena() {
	System.out.println("=============== JENA EXPERIMENTAION ==============");
	TDB.getContext().setTrue(TDB.symUnionDefaultGraph);
	// testThatAllQueriesFailsForAnyAlpha();
	testJenaNLBA();
	testJenaBottomUp();
	testJenaTopDown();
	testJenaHybrid();
    }

    public void testJenaNLBA() {
	factory = new JenaTDBGraphQueryOptFactory();
	testAlgo(Algorithm.NLBA, PLAT_JENA, listOfAlpha);
    }

    public void testJenaBottomUp() {
	factory = new JenaTDBGraphQueryExtFactory();
	testAlgo(Algorithm.BOTTOMUP, PLAT_JENA, listOfAlpha);
    }

    public void testJenaTopDown() {
	factory = new JenaTDBGraphQueryExtFactory();
	testAlgo(Algorithm.TOPDOWN, PLAT_JENA, listOfAlpha);
    }

    public void testJenaHybrid() {
	factory = new JenaTDBGraphQueryExtFactory();
	testAlgo(Algorithm.HYBRID, PLAT_JENA, listOfAlpha);
    }

    @Test
    public void testVirtuoso() {
	System.out.println("=============== VIRTUOSO EXPERIMENTATION==============");
	// testThatAllQueriesFailsForAnyAlpha();
	testVirtuosoNLBA();
	testVirtuosoBottomUp();
	testVirtuosoTopDown();
	testVirtuosoHybrid();
    }

    public void testVirtuosoNLBA() {
	factory = new SPARQLEndpointQueryOptFactory();
	testAlgo(Algorithm.NLBA, PLAT_VIRTUOSO, listOfAlpha);
    }

    public void testVirtuosoBottomUp() {
	factory = new SPARQLEndpointQueryExtFactory();
	testAlgo(Algorithm.BOTTOMUP, PLAT_VIRTUOSO, listOfAlpha);
    }

    public void testVirtuosoTopDown() {
	factory = new SPARQLEndpointQueryExtFactory();
	testAlgo(Algorithm.TOPDOWN, PLAT_VIRTUOSO, listOfAlpha);
    }

    public void testVirtuosoHybrid() {
	factory = new SPARQLEndpointQueryExtFactory();
	testAlgo(Algorithm.HYBRID, PLAT_VIRTUOSO, listOfAlpha);
    }

    /*************************
     * Test with an HSQL Database in-memory
     *************************/
    /*
     * @Test public void testHSQLDB() throws Exception { testHSQLDBNLBA();
     * testHSQLDBBottomUp(); testHSQLDBTopDown(); testHSQLDBHybrid(); }
     * 
     * @Test public void testHSQLDBNLBA() throws Exception { factory = new
     * JDBCQueryOptFactory(); Session session = factory.createSession();
     * SQLScriptRunner newScriptRunner = new SQLScriptRunner(((JDBCSession)
     * session).getConnection(), false, false); InputStream resourceAsStream =
     * getClass().getResourceAsStream("/test_dataset1.sql");
     * newScriptRunner.runScript(new InputStreamReader(resourceAsStream));
     * testAlgo(Algorithm.NLBA, PLAT_JENA, listOfAlpha); }
     * 
     * @Test public void testHSQLDBBottomUp() throws Exception { factory = new
     * JDBCQueryExtFactory(); testAlgo(Algorithm.BOTTOMUP, PLAT_JENA,
     * listOfAlpha); }
     * 
     * @Test public void testHSQLDBTopDown() throws Exception { factory = new
     * JDBCQueryExtFactory(); testAlgo(Algorithm.TOPDOWN, PLAT_JENA,
     * listOfAlpha); }
     * 
     * @Test public void testHSQLDBHybrid() throws Exception { factory = new
     * JDBCQueryExtFactory(); testAlgo(Algorithm.HYBRID, PLAT_JENA,
     * listOfAlpha); }
     */
    /************************************
     * Main method to test the algorithms
     ************************************/

    private void testAlgo(Algorithm typeAlgo, String platform, List<Double> listOfAlpha) {
	List<QueryExplain> newTestResultPairList = null;
	try {
	    newTestResultPairList = this.newTestResultPairList("/" + FILE_QUERIES);
	    ExpRelaxResult results = new ExpRelaxResult(NB_EXEC);
	    for (int i = 0; i < newTestResultPairList.size(); i++) {
		QueryExplain qExplain = newTestResultPairList.get(i);
		Query q = qExplain.getQuery();
		String description = qExplain.getDescription();
		System.out.println("-----------------------------------------------------------");
		System.out.println("Query (" + description + "): " + q);
		System.out.println("-----------------------------------------------------------");

		for (int k = 0; k <= NB_EXEC; k++) {
		    q = factory.createQuery(q.toString());
		    executeAlgo(q, typeAlgo, listOfAlpha);
		    int nbExecutedQuery = algo.getNbExecutedQuery();
		    int nbCacheHits = algo.getNbCacheHits();
		    float tps = algo.getComputingTime();
		    if (k > 0) {
			results.addQueryResult(k - 1, q, tps, nbExecutedQuery, nbCacheHits);
		    }
		    System.out.println(typeAlgo.name() + " - Time = " + tps + ", NbQueriesExecuted: " + nbExecutedQuery
			    + ", NbCacheHits: " + nbCacheHits);
		}
	    }
	    System.out.println("---------- BILAN ------------------");
	    System.out.println(results.toString());
	    System.out.println("------------------------------------");

	    results.toFile("exp-" + platform + "-" + typeAlgo.name() + ".csv");
	} catch (IOException e) {
	    System.out.println("Unable to read the queries in the file.");
	    e.printStackTrace();
	}

    }

    /**
     * Execute the algorithm for the given query
     * 
     * @param q
     *            the query
     * @param typeAlgo
     *            the used algorithm
     */
    private void executeAlgo(Query q, Algorithm typeAlgo, List<Double> listOfAlpha) {
	switch (typeAlgo) {
	case NLBA:
	    algo = new AlgoNLBA(q.getFactory().createSession());
	    break;
	case BOTTOMUP:
	    algo = new AlgoBottomUp(q.getFactory().createSession());
	    break;
	case TOPDOWN:
	    algo = new AlgoTopDown(q.getFactory().createSession());
	    break;
	case HYBRID:
	    algo = new AlgoHybrid(q.getFactory().createSession());
	    break;
	}
	algo.computesAlphaMFSsAndXSSs(q, listOfAlpha);
    }

    /*********************************************
     * Inner class to load the queries from a file
     *********************************************/

    class QueryExplain {

	protected int index;

	protected String description;

	protected Query query;

	protected List<Query> mfs;

	protected List<Query> xss;

	public String getDescription() {
	    return description;
	}

	public void setDescription(String description) {
	    this.description = description;
	}

	public List<Query> getMfs() {
	    return mfs;
	}

	public List<Query> getXss() {
	    return xss;
	}

	public QueryExplain() {
	    this.mfs = new ArrayList<Query>();
	    this.xss = new ArrayList<Query>();
	}

	public Query getQuery() {
	    return query;
	}

	public void setQuery(Query pQuery) {
	    this.query = pQuery;
	}

	public void addMFS(Query mfs) {
	    this.mfs.add(mfs);
	}

	public void addXSS(Query xss) {
	    this.xss.add(xss);
	}

	public void setIndex(int pIndex) {
	    this.index = pIndex;
	}

	public int getIndex() {
	    return this.index;
	}
    }

    protected List<QueryExplain> newTestResultPairList(final String filename) throws IOException {
	final List<QueryExplain> queries = new ArrayList<QueryExplain>();
	final URL fileUrl = ExperimentAlgoTest.class.getResource(filename);
	final FileReader file = new FileReader(fileUrl.getFile());
	BufferedReader in = null;
	try {
	    in = new BufferedReader(file);
	    StringBuffer test = null;
	    StringBuffer mfsresult = null;
	    StringBuffer xssresult = null;

	    final Pattern pTest = Pattern.compile("# Test (\\w+) \\((.*)\\)");
	    final Pattern pMFS = Pattern.compile("# MFS (\\w+)");
	    final Pattern pXSS = Pattern.compile("# XSS (\\w+)");

	    String line;
	    int lineNumber = 0;

	    String testNumber = null;
	    String testName = null;
	    StringBuffer curbuf = null;

	    while ((line = in.readLine()) != null) {
		lineNumber++;
		final Matcher mTest = pTest.matcher(line);
		final Matcher mMFS = pMFS.matcher(line);
		final Matcher mXSS = pXSS.matcher(line);
		if (mTest.matches()) { // # Test
		    addTestResultPair(queries, test, mfsresult, xssresult, testNumber, testName);

		    testNumber = mTest.group(1);
		    testName = mTest.group(2);

		    test = new StringBuffer();
		    mfsresult = new StringBuffer();
		    xssresult = new StringBuffer();

		    curbuf = test;
		} else if (mMFS.matches()) { // # Result
		    if (testNumber == null) {
			throw new RuntimeException("Test file has result without a test (line " + lineNumber + ")");
		    }
		    final String resultNumber = mMFS.group(1);
		    if (!testNumber.equals(resultNumber)) {
			throw new RuntimeException(
				"Result " + resultNumber + " test " + testNumber + " (line " + lineNumber + ")");
		    }

		    curbuf = mfsresult;
		} else if (mXSS.matches()) {
		    if (testNumber == null) {
			throw new RuntimeException("Test file has result without a test (line " + lineNumber + ")");
		    }
		    final String resultNumber = mXSS.group(1);
		    if (!testNumber.equals(resultNumber)) {
			throw new RuntimeException(
				"Result " + resultNumber + " test " + testNumber + " (line " + lineNumber + ")");
		    }

		    curbuf = xssresult;
		} else {
		    line = line.trim();
		    if (!line.isEmpty()) {
			curbuf.append(line);
			curbuf.append("\n");
		    }
		}
	    }

	    addTestResultPair(queries, test, mfsresult, xssresult, testNumber, testName);

	} finally {
	    if (in != null) {
		try {
		    in.close();
		} catch (final IOException e) {
		}
	    }
	}

	return queries;
    }

    private void addTestResultPair(List<QueryExplain> queries, StringBuffer query, StringBuffer mfsResult,
	    StringBuffer xssResult, String number, String description) throws IOException {
	if (query == null || mfsResult == null || xssResult == null) {
	    return;
	}

	QueryExplain currentQuery = new QueryExplain();
	currentQuery.setQuery(this.factory.createQuery(query.toString().trim()));
	currentQuery.setIndex(Integer.valueOf(number));
	currentQuery.setDescription(description.trim());

	BufferedReader bufReader = new BufferedReader(new StringReader(mfsResult.toString()));
	String line = null;
	while ((line = bufReader.readLine()) != null) {
	    currentQuery.addMFS(this.factory.createQuery(line.trim()));
	}

	bufReader = new BufferedReader(new StringReader(xssResult.toString()));
	line = null;
	while ((line = bufReader.readLine()) != null) {
	    currentQuery.addXSS(this.factory.createQuery(line.trim()));
	}

	queries.add(currentQuery);
    }

    /********************************************************
     * Method to check that each algorithm returns the same result
     ********************************************************/

    /*
     * @Test public void testValidityOfAlgorithms() { try { factory = new
     * JDBCQueryOptFactory(); Session session = factory.createSession();
     * SQLScriptRunner newScriptRunner = new SQLScriptRunner(((JDBCSession)
     * session).getConnection(), false, false); InputStream resourceAsStream =
     * getClass().getResourceAsStream("/test_dataset1.sql");
     * newScriptRunner.runScript(new InputStreamReader(resourceAsStream));
     * 
     * List<QueryExplain> newTestResultPairList = null;
     * 
     * newTestResultPairList = this.newTestResultPairList("/" + FILE_QUERIES);
     * for (int i = 0; i < newTestResultPairList.size(); i++) { QueryExplain
     * qExplain = newTestResultPairList.get(i); Query q = qExplain.getQuery();
     * // String description = qExplain.getDescription(); //
     * System.out.println("Query (" + description + "): "); // NLBA factory =
     * new JDBCQueryOptFactory(); q = factory.createQuery(q.toString()); algo =
     * new AlgoNLBA(); AlgoResult algoResultNLBA =
     * algo.computesAlphaMFSsAndXSSs(q, listOfAlpha); // HYBRID factory = new
     * JDBCQueryExtFactory(); q = factory.createQuery(q.toString()); algo = new
     * AlgoHybrid(); AlgoResult algoResultHybrid =
     * algo.computesAlphaMFSsAndXSSs(q, listOfAlpha); // BOTTOM UP factory = new
     * JDBCQueryExtFactory(); q = factory.createQuery(q.toString()); algo = new
     * AlgoBottomUp(); AlgoResult algoResultBottomUp =
     * algo.computesAlphaMFSsAndXSSs(q, listOfAlpha); // TOP DOWN factory = new
     * JDBCQueryExtFactory(); q = factory.createQuery(q.toString()); algo = new
     * AlgoTopDown(); AlgoResult algoResultTopDown =
     * algo.computesAlphaMFSsAndXSSs(q, listOfAlpha);
     * 
     * Assert.assertEquals(algoResultNLBA, algoResultBottomUp);
     * Assert.assertEquals(algoResultTopDown, algoResultBottomUp);
     * System.out.println("=============== RESULT TOP DOWN=============");
     * System.out.println(algoResultTopDown);
     * System.out.println("=============== RESULT HYBRID =============");
     * System.out.println(algoResultHybrid);
     * 
     * Assert.assertEquals(algoResultTopDown, algoResultHybrid);
     * 
     * }
     * 
     * } catch (IOException | SQLException e) {
     * System.out.println("Unable to read the queries in the file.");
     * e.printStackTrace(); }
     * 
     * }
     */

  //  @Test
    public void testValidityOfAlgorithmsJenaGraph() {
	try {

	    TDB.getContext().setTrue(TDB.symUnionDefaultGraph);

	    factory = new JenaTDBGraphQueryOptFactory();
	    List<QueryExplain> newTestResultPairList = null;
	    newTestResultPairList = this.newTestResultPairList("/" + FILE_QUERIES);
	    for (int i = 0; i < newTestResultPairList.size(); i++) {
		QueryExplain qExplain = newTestResultPairList.get(i);
		Query q = qExplain.getQuery();
		// String description = qExplain.getDescription();
		// System.out.println("Query (" + description + "): ");
		// NLBA
		factory = new JenaTDBGraphQueryOptFactory();
		q = factory.createQuery(q.toString());
		algo = new AlgoNLBA(q.getFactory().createSession());
		AlgoResult algoResultNLBA = algo.computesAlphaMFSsAndXSSs(q, listOfAlpha);
		// HYBRID
		factory = new JenaTDBGraphQueryExtFactory();
		q = factory.createQuery(q.toString());
		algo = new AlgoHybrid(q.getFactory().createSession());
		AlgoResult algoResultHybrid = algo.computesAlphaMFSsAndXSSs(q, listOfAlpha);
		// BOTTOM UP
		factory = new JenaTDBGraphQueryExtFactory();
		q = factory.createQuery(q.toString());
		algo = new AlgoBottomUp(q.getFactory().createSession());
		AlgoResult algoResultBottomUp = algo.computesAlphaMFSsAndXSSs(q, listOfAlpha);
		// TOP DOWN
		factory = new JenaTDBGraphQueryExtFactory();
		q = factory.createQuery(q.toString());
		algo = new AlgoTopDown(q.getFactory().createSession());
		AlgoResult algoResultTopDown = algo.computesAlphaMFSsAndXSSs(q, listOfAlpha);

		Assert.assertEquals(algoResultNLBA, algoResultBottomUp);
		Assert.assertEquals(algoResultTopDown, algoResultBottomUp);
		System.out.println("=============== RESULT NLBA==============");
		System.out.println(algoResultNLBA);
		System.out.println("=============== RESULT TOP DOWN==============");
		System.out.println(algoResultTopDown);
		System.out.println("=============== RESULT BOTTOM UP==============");
		System.out.println(algoResultBottomUp);
		System.out.println("=============== RESULT HYBRID =============");
		System.out.println(algoResultHybrid);

		Assert.assertEquals(algoResultTopDown, algoResultHybrid);

	    }

	} catch (IOException e) {
	    System.out.println("Unable to read the queries in the file.");
	    e.printStackTrace();
	}

    }

    /*****************************************
     * Test with several list of alpha (Exp 2)
     *****************************************/

    private List<List<Double>> listOfListOfAlpha = Arrays.asList(Arrays.asList(0.1), Arrays.asList(0.1, 0.2),
	    Arrays.asList(0.1, 0.2, 0.3), Arrays.asList(0.1, 0.2, 0.3, 0.4), Arrays.asList(0.1, 0.2, 0.3, 0.4, 0.5),
	    Arrays.asList(0.1, 0.2, 0.3, 0.4, 0.5, 0.6), Arrays.asList(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7),
	    Arrays.asList(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8),
	    Arrays.asList(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9));

    // @Test
    public void testExp2() {
	TDB.getContext().setTrue(TDB.symUnionDefaultGraph);
	testExp2NLBA();
	testExp2BottomUp();
	testExp2TopDown();
	testExp2Hybrid();
    }

    public void testExp2NLBA() {
	factory = new JenaTDBGraphQueryOptFactory();
	launchExp2(Algorithm.NLBA, PLAT_JENA, listOfListOfAlpha);
    }

    public void testExp2BottomUp() {
	factory = new JenaTDBGraphQueryExtFactory();
	launchExp2(Algorithm.BOTTOMUP, PLAT_JENA, listOfListOfAlpha);
    }

    public void testExp2TopDown() {
	factory = new JenaTDBGraphQueryExtFactory();
	launchExp2(Algorithm.TOPDOWN, PLAT_JENA, listOfListOfAlpha);
    }

    public void testExp2Hybrid() {
	factory = new JenaTDBGraphQueryExtFactory();
	launchExp2(Algorithm.HYBRID, PLAT_JENA, listOfListOfAlpha);
    }

    public void launchExp2(Algorithm typeAlgo, String platform, List<List<Double>> listOfListOfAlpha) {
	List<QueryExplain> newTestResultPairList = null;
	try {
	    newTestResultPairList = this.newTestResultPairList("/" + FILE_QUERY_EXP2);
	    QueryExplain qExplain = newTestResultPairList.get(0);
	    Query q = qExplain.getQuery();
	    String description = qExplain.getDescription();
	    System.out.println("-----------------------------------------------------------");
	    System.out.println("Query (" + description + "): " + q);
	    System.out.println("-----------------------------------------------------------");
	    BufferedWriter fichier = new BufferedWriter(
		    new FileWriter("exp2-" + platform + "-" + typeAlgo.name() + ".csv"));
	    for (int i = 0; i < listOfListOfAlpha.size(); i++) {
		ExpRelaxResult results = new ExpRelaxResult(NB_EXEC);
		System.out.println("change list of alpha " + listOfListOfAlpha.get(i));
		for (int k = 0; k <= NB_EXEC; k++) {
		    q = factory.createQuery(q.toString());
		    executeAlgo(q, typeAlgo, listOfListOfAlpha.get(i));
		    int nbExecutedQuery = algo.getNbExecutedQuery();
		    int nbCacheHits = algo.getNbCacheHits();
		    float tps = algo.getComputingTime();
		    if (k > 0) {
			results.addQueryResult(k - 1, q, tps, nbExecutedQuery, nbCacheHits);
		    }
		    System.out.println(typeAlgo.name() + " - Time = " + tps + ", NbQueriesExecuted: " + nbExecutedQuery
			    + ", NbCacheHits: " + nbCacheHits);
		}
		StringBuffer res = new StringBuffer("");
		Float valTime = ExpRelaxResult.round(results.getAvgTime(q), 2);
		res.append(valTime.toString().replace('.', ',') + "\t");
		int nbExecutedQuery = Math.round(results.getAvgNbExecutedQuery(q));
		res.append(nbExecutedQuery + "\t");
		int nbCacheHits = Math.round(results.getAvgCacheHits(q));
		res.append(nbCacheHits + "\n");
		fichier.write(res.toString());
	    }
	    fichier.close();
	} catch (IOException e) {
	    System.out.println("Unable to read the queries in the file.");
	    e.printStackTrace();
	}
    }

}
