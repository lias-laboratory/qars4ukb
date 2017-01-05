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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import fr.ensma.lias.qars4ukb.Session;
import fr.ensma.lias.qars4ukb.cache.ExtendedCacheLBA;
import fr.ensma.lias.qars4ukb.query.AbstractQuery;
import fr.ensma.lias.qars4ukb.query.Query;

/**
 * @author Stéphane JEAN
 */
public class AlgoHybrid extends AbstractAlgo {

    public AlgoHybrid() {
	super();
    }

    /**
     * return the common queries into two list of query
     * 
     * @param a
     *            list of queries
     * @param b
     *            list of queries
     * @return a list containing the common queries into two list of query
     */
    public Set<Query> getCommonQueries(Set<Query> a, Set<Query> b) {
	Set<Query> res = new HashSet<>();
	if (a != null) {
	    for (Query q : a) {
		if (b.contains(q))
		    res.add(q);
	    }
	}
	return res;
    }

    /**
     * Remove a collection of queries from another collection
     * 
     * @param initial
     *            list of queries
     * @param toRemove
     *            collection to remove from the initial
     * @return the initial list with out including toRemove elements
     */
    public Set<Query> removeASetOfQueries(Set<Query> initial, Set<Query> toRemove) {
	initial.removeAll(toRemove);
	return initial;
    }

    /**
     * Get the atomic queries from a list of queries
     * 
     * @param list
     *            of queries
     * @return the atomic queries founded in the list
     */
    public Set<Query> getAtomicQueries(Set<Query> list) {
	Set<Query> res = new HashSet<>();
	for (Query q : list) {
	    if (q.size() == 1) {
		res.add(q);
	    }
	}
	return res;
    }

    /**
     * Get the proper sub queries of the initialQuery
     * 
     * @param list
     *            of queries
     * @return the proper sub queries of the initialQuery
     */
    public Set<Query> getProperSubQueries(Query initialQuery, Set<Query> list) {
	int sizeInitialQuery = initialQuery.size();
	Set<Query> res = new HashSet<>();
	for (Query q : list) {
	    if (q.size() == (sizeInitialQuery - 1)) {
		res.add(q);
	    }
	}
	return res;
    }

    /**
     * 
     * @param list
     *            list of queries
     * @param properSubQueries
     *            proper sub queries of the initial query
     * @return the list of queries with out queries included in properSubQueries
     */
    public Set<Query> removeQueriesIncludedInQuery(Set<Query> properSubQueries, Set<Query> list) {
	Set<Query> res = new HashSet<>();

	for (Query q : list) {
	    if (!((AbstractQuery) q).isIncludedInAQueryOf(properSubQueries)) {
		res.add(q);
	    }
	}
	return res;
    }

    /**
     * 
     * @param initial
     *            list of queries
     * @param atomicQ
     *            list of atomic queries
     * @return the initial list of queries with out including queries witch
     *         contains atomic one
     */
    public Set<Query> removeQueriesIncludingAQuery(Set<Query> initial, Set<Query> atomicQ) {
	Set<Query> res = new HashSet<>();

	for (Query q : initial) {
	    if (!q.includesAQueryOf(atomicQ)) {
		res.add(q);
	    }
	}
	return res;
    }

    /**
     * Discover a set of XSSs for a degree alpha from a set of XSSs from a
     * greater threshold
     * 
     * @param discoveredXSS
     *            the previous set of XSSs
     * @param alpha
     *            the threshold
     * @param session
     *            the connection to the KB
     */
    protected Set<Query> getSuccessXSS(Set<Query> discoveredXSS, Double alpha, Session session) {
	Set<Query> res = new HashSet<Query>();
	for (Query previousXSS : discoveredXSS) {
	    if (!previousXSS.isFailing(session, alpha)) {
		res.add(previousXSS);
	    }
	}
	return res;
    }

    /**
     * Discover a set of MFSs for a degree alpha from a set of MFSs from a lower
     * threshold (Algorithm DiscoverMFSXSS of the publication)
     * 
     * @param discoveredMFS
     *            the previous set of MFSs
     * @param alpha
     *            the threshold
     * @param session
     *            the connection to the KB
     */
    protected Set<Query> GetFailingMFS(Set<Query> discoveredMFS, Double alpha, Session session) {
	Set<Query> res = new HashSet<Query>();
	for (Query previousMFS : discoveredMFS) {
	    if (previousMFS.isFailing(session, alpha)) {
		res.add(previousMFS);
	    }
	}
	return res;
    }

    /**
     * Discover a set of MFSs for a degree alpha from a set of MFSs from a
     * greater threshold
     * 
     * @param discoveredMFS
     *            the previous set of MFSs
     * @param alpha
     *            the threshold
     * @param session
     *            the connection to the KB
     */
    protected Set<Query> findAnMFSInEachQuery(Set<Query> discoveredMFS, Double alpha, Session session) {
	Set<Query> res = new HashSet<Query>();
	Set<Query> fq = new HashSet<Query>(discoveredMFS);
	Iterator<Query> iter = fq.iterator();
	Set<Query> toRemove = new HashSet<Query>();
	while (iter.hasNext()) {
	    Query previousMFS = iter.next();
	    iter.remove();
	    if (!toRemove.contains(previousMFS)) {
		Query newMFS = previousMFS.findAnMFS(session, alpha);
		res.add(newMFS);
		for (Query qPrim : discoveredMFS) {
		    if (qPrim.includes(newMFS)) {
			toRemove.add(qPrim);
		    }
		}
	    }
	}

	return res;
    }

    /**
     * Discover a set of XSSs for a degree alpha from a set of XSSs from a lower
     * threshold (Algorithm DiscoverMFSXSS of the publication)
     * 
     * @param initialQuery
     *            the query on which the algorithm is executed
     * @param discoveredXSS
     *            the previous set of XSSs
     * @param alpha
     *            the threshold
     * @param session
     *            the connection to the KB
     */
    protected Set<Query> findAnXSSInEachQuery(Query initialQuery, Set<Query> discoveredXSS, Double alpha,
	    Session session) {
	Set<Query> res = new HashSet<Query>();
	Set<Query> sq = new HashSet<Query>(discoveredXSS);
	Iterator<Query> iter = sq.iterator();
	Set<Query> toRemove = new HashSet<Query>();
	while (iter.hasNext()) {
	    Query previousXSS = iter.next();
	    iter.remove();
	    if (!toRemove.contains(previousXSS)) {
		Query newXSS = initialQuery.findAnXSS(session, alpha, previousXSS);
		res.add(newXSS);
		for (Query qPrim : sq) {
		    if (newXSS.includes(qPrim)) {
			toRemove.add(qPrim);
		    }
		}
	    }

	}
	return res;

    }

    /**
     * Calculate the alpha execution order for Hybrid algorithm
     * 
     * @param left
     *            position of the minimum threshold
     * @param right
     *            position of the maximum threshold
     * @param res
     *            the ordered list
     * @param tab
     *            the initial list of threshold
     */
    public void executionOrder(List<HybridAlgorithmElement> res, List<Double> tab) {
	if (tab.size() == 1) {
	    res.add(new HybridAlgorithmElement(tab.get(0), null, null));
	} else {
	    res.add(new HybridAlgorithmElement(tab.get(0), null, null));
	    res.add(new HybridAlgorithmElement(tab.get(tab.size() - 1), tab.get(0), null));
	}

	executionOrderAux(0, tab.size() - 1, res, tab);
    }

    public void executionOrderAux(int left, int right, List<HybridAlgorithmElement> res, List<Double> tab) {
	if ((right - left) > 1) {
	    int midle = (left + right) / 2;
	    res.add(new HybridAlgorithmElement(tab.get(midle), tab.get(left), tab.get(right)));
	    executionOrderAux(left, midle, res, tab);
	    executionOrderAux(midle, right, res, tab);
	}
    }

    @Override
    protected AlgoResult computesAlphaMFSsAndXSSsAux(Query q, List<Double> listOfAlphaNotOrdred) {
	ExtendedCacheLBA.getInstance().clearCache();
	Session session = q.getFactory().createSession();
	AlgoResult result = new AlgoResult();

	// first executes the normal version of LBA for the last alpha
	List<HybridAlgorithmElement> listOfAlpha = new ArrayList<>();
	executionOrder(listOfAlpha, listOfAlphaNotOrdred);

	// first executes the normal version of LBA for the first alpha

	HybridAlgorithmElement firstAlpha = listOfAlpha.get(0);
	q.runLBA(session, firstAlpha.getAlpha());
	nbExecutedQuery = session.getExecutedQueryCount();
	Set<Query> discoverMFSs = q.getAllMFS();
	Set<Query> discoverXSSs = q.getAllXSS();
	result.addAlphaMFSs(firstAlpha.getAlpha(), discoverMFSs);
	result.addAlphaXSSs(firstAlpha.getAlpha(), discoverXSSs);

	Set<Query> discoverMFSsLeft = new HashSet<>();
	Set<Query> discoverXSSsLeft = new HashSet<>();
	Set<Query> discoverMFSsRight = new HashSet<>();
	Set<Query> discoverXSSsRight = new HashSet<>();
	Double left;
	Double right;
	for (int i = 1; i < listOfAlpha.size(); i++) {
	    HybridAlgorithmElement currentAlpha = listOfAlpha.get(i);
	    // we clear the number of executed queries by the previous run of
	    // LBA
	    session.clearExecutedQueryCount();
	    left = currentAlpha.getLeft();
	    right = currentAlpha.getRight();

	    if (left != null) {
		discoverMFSsLeft = new HashSet<>(result.getAlphaMFSs(left));
		discoverXSSsLeft = new HashSet<>(result.getAlphaXSSs(left));
	    }
	    if (right != null) {
		discoverMFSsRight = new HashSet<>(result.getAlphaMFSs(right));
		discoverXSSsRight = new HashSet<>(result.getAlphaXSSs(right));
	    }

	    runHybrid(session, q, discoverMFSsLeft, discoverXSSsLeft, discoverMFSsRight, discoverXSSsRight,
		    currentAlpha, result);
	    nbExecutedQuery += session.getExecutedQueryCount();

	}
	nbCacheHits = ExtendedCacheLBA.getInstance().getNbCacheHits();
	return result;
    }

    /**
     * 
     * @param session
     * @param discoverMFSsLeft
     * @param discoverXSSsLeft
     * @param discoverMFSsRight
     * @param discoverXSSsRight
     * @param currentAlpha
     * @param result
     */
    private void runHybrid(Session session, Query q, Set<Query> discoverMFSsLeft, Set<Query> discoverXSSsLeft,
	    Set<Query> discoverMFSsRight, Set<Query> discoverXSSsRight, HybridAlgorithmElement currentAlpha,
	    AlgoResult result) {

	Set<Query> discoverMFSs = new HashSet<>();
	Set<Query> discoverXSSs = new HashSet<>();

	// add common left and right MFS to the discoverMFSs
	Set<Query> tmp = getCommonQueries(discoverMFSsLeft, discoverMFSsRight);
	discoverMFSs.addAll(tmp);
	discoverMFSsLeft.removeAll(tmp);
	discoverMFSsLeft.removeAll(tmp);

	// add common left and right XSS to the discoverXSSs
	tmp = getCommonQueries(discoverXSSsLeft, discoverXSSsRight);
	discoverXSSs.addAll(tmp);
	discoverXSSsLeft.removeAll(tmp);
	discoverXSSsLeft.removeAll(tmp);

	// add atomic discoverMFSsLeft to the discoverMFSs
	tmp = getAtomicQueries(discoverMFSsLeft);
	discoverMFSs.addAll(tmp);
	discoverMFSsLeft.removeAll(tmp);

	// remove discoverMFSsRight queries witch contains atomic
	// discoverMFSsLeft
	removeQueriesIncludingAQuery(discoverMFSsRight, tmp);

	// add failing discoverMFSsRight for the currentAlpha to discoverMFSs
	tmp = GetFailingMFS(discoverMFSsRight, currentAlpha.getAlpha(), session);
	discoverMFSs.addAll(tmp);
	discoverMFSsRight.removeAll(tmp);

	// remove discoverMFSsRight queries witch contains failing
	// discoverMFSsLeft
	// discoverMFSsLeft
	// removeQueriesIncludingAQuery(discoverMFSsRight, tmp);

	// Find MFSs in the rest of discoverMFSsLeft
	tmp = findAnMFSInEachQuery(discoverMFSsLeft, currentAlpha.getAlpha(), session);
	discoverMFSs.addAll(tmp);
	discoverMFSsLeft.removeAll(tmp);

	// add founded proper sub queries in the discoverXSSsRight to the
	// discoverXSSs
	tmp = getProperSubQueries(q, discoverXSSsRight);
	discoverXSSs.addAll(tmp);
	discoverXSSsRight.removeAll(tmp);

	// remove discoverXSSsLeft queries Included In the founded proper sub
	// queries
	removeQueriesIncludedInQuery(discoverXSSsLeft, tmp);

	// add successful discoverXSSsLeft for the currentAlpha to discoverXSSs
	tmp = getSuccessXSS(discoverXSSsLeft, currentAlpha.getAlpha(), session);
	discoverXSSs.addAll(tmp);
	discoverXSSsLeft.removeAll(tmp);

	// remove successful discoverXSSsLeft queries Included In the founded
	// proper sub
	// queries
	// removeQueriesIncludedInQuery(discoverXSSsLeft, tmp);

	// Find XSSs in the rest of discoverXSSLeft
	tmp = findAnXSSInEachQuery(q, discoverXSSsRight, currentAlpha.getAlpha(), session);
	discoverXSSs.addAll(tmp);
	// add result
	q.runLBA(session, discoverMFSs, discoverXSSs, currentAlpha.getAlpha());
	nbExecutedQuery += session.getExecutedQueryCount();
	result.addAlphaMFSs(currentAlpha.getAlpha(), q.getAllMFS());
	result.addAlphaXSSs(currentAlpha.getAlpha(), q.getAllXSS());
    }
}
