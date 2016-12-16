/*********************************************************************************
* This file is part of QARS Project.
* Copyright (C) 2015 LIAS - ENSMA
*   Teleport 2 - 1 avenue Clement Ader
*   BP 40109 - 86961 Futuroscope Chasseneuil Cedex - FRANCE
* 
* QARS is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* QARS is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with QARS.  If not, see <http://www.gnu.org/licenses/>.
**********************************************************************************/
package fr.ensma.lias.qars4ukb.query;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ensma.lias.qars4ukb.Session;
import fr.ensma.lias.qars4ukb.cache.ICache;

/**
 * @author Stephane JEAN
 */
public abstract class AbstractQueryOpt extends AbstractQuery {

    Logger log = Logger.getLogger(AbstractQueryOpt.class);

    /**
     * Encoding of this query 1011 => t1 ^ t3 ^ t4
     */
    private BitSet queryAsBitSet;

    /**
     * The variables of this query
     */
    private Set<String> variables;
    
    /**
     * Cache of queries 
     * Used to avoid executing some queries
     */
    private ICache cache;

    /**
     * Build a query with its factory and its string
     * @param factory the factory
     * @param query the string of this query
     * @param cache the query cache
     */
    public AbstractQueryOpt(QueryFactory factory, String query, ICache cache) {
	super(factory, query);
	this.cache = cache;
    }

    /**
     * Build a query with its factory and a set of triple patterns
     * @param factory the factory
     * @param tps a set of triple patterns
     * @param cache the query cache
     */
    public AbstractQueryOpt(QueryFactory factory, List<TriplePattern> tps, ICache cache) {
	super(factory, tps);
	this.cache = cache;
    }

    /**
     * Checks if this query is failing by executing it
     * @param q the query
     * @param session the connection to the KB
     * @param alpha the threshold
     * @return true iff this query is failing
     */
    protected abstract boolean isFailingWithExecution(Query q, Session session, Double alpha);

    /**
     * Get the variables of this query
     * @return the variables of this query
     */
    public Set<String> getVariables() {
	if (variables == null)
	    initVariables();
	return variables;
    }

    /**
     * Compute the set of variables of this query
     */
    public void initVariables() {
	variables = new HashSet<String>();
	for (TriplePattern t : triplePatterns) {
	    variables.addAll(t.getVariables());
	}
    }

    /**
     * Get the bitset representation of this query
     * @return the bitset representation of this query
     */
    public BitSet getQueryAsBitSet() {
	if (queryAsBitSet == null)
	    initQueryAsBitSet();
	return queryAsBitSet;
    }

    /**
     * Computes the bitset representation of this query
     */
    private void initQueryAsBitSet() {
	queryAsBitSet = new BitSet(nbTriplePatterns);
	for (TriplePattern t : triplePatterns) {
	    if (newInitialQuery == null) {
		queryAsBitSet.set(this.getTriplePatterns().indexOf(t));
	    } else {
		queryAsBitSet.set(newInitialQuery.getTriplePatterns().indexOf(t));
	    }
	}
    }

    @Override
    public boolean includes(Query q) {
	BitSet qBitSet = (BitSet) ((AbstractQueryOpt) q).getQueryAsBitSet().clone();
	qBitSet.andNot(getQueryAsBitSet());
	return qBitSet.isEmpty();
    }

    /**
     * Decompose a Cartesian Product into several connected parts
     * 
     * @return the connected parts of a Cartesian Product
     */
    public List<Query> getConnectedParts() {
	List<Query> res = new ArrayList<Query>(1);
	// we are sure that the query has at least one TP
	res.add(factory.createQuery("SELECT * WHERE { " + triplePatterns.get(0).toString() + " }", newInitialQuery));
	for (int i = 1; i < triplePatterns.size(); i++) {
	    int isAlreadyConnected = -1;
	    TriplePattern t = triplePatterns.get(i);
	    List<Query> resTemp = new ArrayList<Query>(res);
	    for (int j = 0; j < resTemp.size(); j++) {
		Query q = resTemp.get(j);
		if (((AbstractQueryOpt) q).isConnectedWith(t)) {
		    if (isAlreadyConnected == -1) {
			((AbstractQuery) q).addTriplePattern(t);
			isAlreadyConnected = j;
		    } else {
			// merge q with q' with is at the position
			// isAlradyConnected
			res.remove(j);
			res.set(isAlreadyConnected, ((AbstractQuery) q).concat(res.get(isAlreadyConnected)));
		    }
		}
	    }
	    if (isAlreadyConnected == -1) {
		res.add(factory.createQuery("SELECT * WHERE { " + t.toString() + " }", newInitialQuery));
	    }
	}
	// log.info("*************res: " + res + "<--");
	return res;
    }

    /**
     * Checks whether this query is connected with a given triple pattern
     * @param t a triple pattern
     * @return true iff this query is connected with the triple pattern
     */
    public boolean isConnectedWith(TriplePattern t) {
	boolean res = false;
	Set<String> queryVariables = getVariables();
	for (String v : t.getVariables()) {
	    if (queryVariables.contains(v))
		return true;
	}
	return res;
    }

    @Override
    public boolean isFailingAux(Session session, Double alpha) {
	// System.out.println(this.toSimpleString(newInitialQuery));
	List<Query> connectedParts = getConnectedParts();
	boolean isCartesianProduct = (connectedParts.size() > 1);
	boolean res = false;
	for (Query q : connectedParts) {
	    boolean isSuccessFullByCache = false;
	    if (cache.isSuccessfulByCache(q, alpha)) {
		if (!isCartesianProduct) {
		    return false;
		} else {
		    isSuccessFullByCache = true;
		}
	    }
	    if (cache.isFailingByCache(q, alpha)) {
		return true;
	    }
	    if (!isSuccessFullByCache) {
		res = isFailingWithExecution(q, session, alpha);
		if (res) {
		    cache.addFailingQuery(q, isCartesianProduct, alpha);
		    return true;
		} else {
		    cache.addSuccessfulQuery(q, alpha);
		}
	    }
	}
	return res;
    }

    @Override
    public void initLBA() {
	// we need to init the cache
	cache.initCache();
    }

    @Override
    public void addTriplePattern(TriplePattern tp) {
	super.addTriplePattern(tp);
	if (variables != null)
	    variables.addAll(tp.getVariables());
    }
}
