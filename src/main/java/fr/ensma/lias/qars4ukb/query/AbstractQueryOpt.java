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
import fr.ensma.lias.qars4ukb.exception.NotYetImplementedException;

/**
 * @author Stephane JEAN
 */
public abstract class AbstractQueryOpt extends AbstractQuery {

    Logger log = Logger.getLogger(AbstractQueryOpt.class);

    /**
     * Encoding of this query 1011 => t1 ^ t3 ^ t4
     */
    private BitSet queryAsBitSet;

    private Set<String> variables;

    public AbstractQueryOpt(QueryFactory factory, String query) {
	super(factory, query);
    }

    public AbstractQueryOpt(QueryFactory factory, List<TriplePattern> tps) {
	super(factory, tps);
    }

    protected abstract boolean executeQuery(Query q, Session session)
	    throws Exception;
    
    public Set<String> getVariables() {
	if (variables == null)
	    initVariables();
	return variables;
    }

    public void initVariables() {
	variables = new HashSet<String>();
	for (TriplePattern t : triplePatterns) {
	    variables.addAll(t.getVariables());
	}
    }

    public BitSet getQueryAsBitSet() {
	if (queryAsBitSet == null)
	    initQueryAsBitSet();
	return queryAsBitSet;
    }

    private void initQueryAsBitSet() {
	// log.info("query: " + this.toString());
	queryAsBitSet = new BitSet(nbTriplePatterns);
	for (TriplePattern t : triplePatterns) {
	    // System.out.println("triple: " + t + "<--" + initialQuery);
	    // MB: initialQuery
	    if (newInitialQuery == null) {
		queryAsBitSet.set(this.getTriplePatterns().indexOf(t));		
	    } else {
		queryAsBitSet.set(newInitialQuery.getTriplePatterns().indexOf(t));
	    }
	}
    }

    @Override
    public boolean includes(Query q) {
	BitSet qBitSet = (BitSet) ((AbstractQueryOpt) q).getQueryAsBitSet()
		.clone();
	qBitSet.andNot(getQueryAsBitSet());
	return qBitSet.isEmpty();
    }

    public List<Query> getConnectedParts() {
	List<Query> res = new ArrayList<Query>(1);
	// we are sure that the query has at least one TP
	res.add(factory.createQuery(
		"SELECT * WHERE { " + triplePatterns.get(0).toString() + " }"));
	for (int i = 1; i < triplePatterns.size(); i++) {
	    int isAlreadyConnected = -1;
	    TriplePattern t = triplePatterns.get(i);
	    List<Query> resTemp = new ArrayList<Query>(res);
	    for (int j = 0; j < resTemp.size(); j++) {
		Query q = resTemp.get(j);
		if (((AbstractQueryOpt) q).isConnectedWith(t)) {
		    if (isAlreadyConnected == -1) {
			((AbstractQuery)q).addTriplePattern(t);
			isAlreadyConnected = j;
		    } else {
			// merge q with q' with is at the position
			// isAlradyConnected
			res.remove(j);
			res.set(isAlreadyConnected,
				((AbstractQuery)q).concat(res.get(isAlreadyConnected)));
		    }
		}
	    }
	    if (isAlreadyConnected == -1) {
		res.add(factory.createQuery(
			"SELECT * WHERE { " + t.toString() + " }"));
	    }
	}
	// log.info("*************res: " + res + "<--");
	return res;
    }

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
    public boolean isFailingAux(Session session) {
	throw new NotYetImplementedException();
	
//	List<Query> connectedParts = getConnectedParts();
//	boolean isCartesianProduct = (connectedParts.size() > 1);
//	boolean res = false;
//	for (Query q : connectedParts) {
//	    // log.debug(q.toSimpleString(initialQuery));
//	    boolean isSuccessFullByCache = false;
//	    for (Query qCache : CacheLBA.getInstance().getCachedQueries()) {
//		if (((AbstractQuery)qCache).includes(q)) {
//		    // System.out.println(qCache.toSimpleString(initialQuery));
//		    // System.out.println(qCache);
//		    // System.out.println("cache success");
//		    CacheLBA.getInstance().incrementeNbRepetedQuery();
//		    // log.debug("cache [success]: " +
//		    // q.toSimpleString(initialQuery));
//		    if (!isCartesianProduct) {
//			return false;
//		    } else {
//			isSuccessFullByCache = true;
//			break;
//		    }
//		}
//	    }
//	    for (Query qCache : CacheLBA.getInstance().getFailingCachedQueries()) {
//		if (((AbstractQuery)q).includes(qCache)) {
//		    // log.debug("cache [failure]: " +
//		    // q.toSimpleString(initialQuery));
//		    CacheLBA.getInstance().incrementeNbRepetedQuery();
//		    return true;
//		}
//	    }
//	    if (!isSuccessFullByCache) {
//		// log.debug("execution");
//		// log.debug(q.toNativeQuery());
//		res = executeQuery(q, session);
//		// log.debug("fin execution");
//		if (res) {
//		    if (isCartesianProduct) {
//			CacheLBA.getInstance().getFailingCachedQueries().add(q);
//		    }
//		    return true;
//		} else {
//		    CacheLBA.getInstance().getCachedQueries().add(q);
//		}
//	    }
//	}
//	return res;
    }

    public void addTriplePattern(TriplePattern tp) {
	super.addTriplePattern(tp);
	if (variables != null)
	    variables.addAll(tp.getVariables());
    }    
}
