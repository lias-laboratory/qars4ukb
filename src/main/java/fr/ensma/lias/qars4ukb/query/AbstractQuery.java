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
package fr.ensma.lias.qars4ukb.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import fr.ensma.lias.qars4ukb.AbstractSession;
import fr.ensma.lias.qars4ukb.Session;
import fr.ensma.lias.qars4ukb.exception.NotYetImplementedException;

/**
 * @author Stéphane JEAN
 */
public abstract class AbstractQuery implements Query {

    /**
     * The two implemented algorithms to compute MFSs and XSSs
     */
    public enum ComputeMFSAndXSSAlgorithm {
	LBA, DFS
    }

    /**
     * Factory to create other queries
     */
    protected QueryFactory factory;

    @Override
    public QueryFactory getFactory() {
	return factory;
    }

    /**
     * String of this query
     */
    protected String rdfQuery;

    /**
     * List of triple patterns of this query
     */
    protected List<TriplePattern> triplePatterns;

    /**
     * Number of triple patterns of this query (this could be computed)
     */
    protected int nbTriplePatterns;

    /**
     * List of the MFSs of this query
     */
    public Set<Query> allMFS;

    /**
     * List of the XSSs of this query
     */
    public Set<Query> allXSS;

    /**
     * Most queries will be created during the execution of LBA the
     * newInitialQuery represents the query on which LBA was executed
     */
    protected Query newInitialQuery;

    /**
     * Get the initial query on which LBA was executed
     * 
     * @return
     */
    public Query getInitialQuery() {
	return this.newInitialQuery;
    }

    /**
     * Set the initial query on which LBA was executed
     * 
     * @param query
     *            the initial query on which LBA was executed
     */
    public void setInitialQuery(Query query) {
	this.newInitialQuery = query;
    }

    /**
     * Builds a query from its string and a reference to its factory
     * 
     * @param factory
     *            a factory to create some queries
     * @param query
     *            the string of this query
     */
    public AbstractQuery(QueryFactory factory, String query) {
	this.factory = factory;
	this.rdfQuery = query;
	this.decomposeQuery();
	nbTriplePatterns = triplePatterns.size();
    }

    /**
     * Decompose a SPARQL Query into a set of triple patterns.
     */
    protected void decomposeQuery() {
	triplePatterns = new ArrayList<TriplePattern>();

	if (!rdfQuery.equals("")) {
	    int indiceOfTriplePattern = 1;
	    int indexOfLeftEmbrace = rdfQuery.indexOf('{');
	    int indexOfDot = rdfQuery.indexOf(" . ", indexOfLeftEmbrace);

	    while (indexOfDot != -1) {
		triplePatterns.add(new TriplePattern(rdfQuery.substring(indexOfLeftEmbrace + 2, indexOfDot),
			indiceOfTriplePattern));
		indiceOfTriplePattern++;
		indexOfLeftEmbrace = indexOfDot + 1;
		indexOfDot = rdfQuery.indexOf(" . ", indexOfLeftEmbrace);
	    }
	    triplePatterns.add(new TriplePattern(rdfQuery.substring(indexOfLeftEmbrace + 2, rdfQuery.length() - 2),
		    indiceOfTriplePattern));
	}
    }

    /**
     * Builds a query from its triple patterns and a reference to its factory
     * 
     * @param factory
     *            a factory to create some queries
     * @param tps
     *            the triple patterns of the query
     */
    public AbstractQuery(QueryFactory factory, List<TriplePattern> tps) {
	this.factory = factory;
	this.rdfQuery = computeRDFQuery(tps);
	triplePatterns = tps;
	nbTriplePatterns = triplePatterns.size();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((triplePatterns == null) ? 0 : new HashSet<TriplePattern>(triplePatterns).hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	AbstractQuery other = (AbstractQuery) obj;
	if (other.nbTriplePatterns != this.nbTriplePatterns) // same
	    // size
	    return false;
	if (!this.includesSimple(other)) // and one is included in the other
	    return false;
	return true;
    }

    @Override
    public Set<Query> getAllMFS() {
	return this.allMFS;
    }

    @Override
    public Set<Query> getAllXSS() {
	return this.allXSS;
    }

    @Override
    public List<TriplePattern> getTriplePatterns() {
	return triplePatterns;
    }

    @Override
    public String toString() {
	return rdfQuery;
    }

    @Override
    public boolean isEmpty() {
	return nbTriplePatterns == 0;
    }

    @Override
    public boolean isFailing(Session session, Double alpha) {
	if (isEmpty())
	    return false;
	return isFailingAux(session, alpha);
    }

    protected abstract boolean isFailingAux(Session session, Double alpha);

    @Override
    public Query findAnXSS(Session session, Double alpha, Query inputQuery) {
	Query qStar = factory.createQuery(inputQuery.toString(), this);
	List<TriplePattern> triplePatternsInitialQuery = new ArrayList<>(triplePatterns);
	List<TriplePattern> triplePatternsQStar = qStar.getTriplePatterns();
	for (int i = 0; i < triplePatternsQStar.size(); i++) {
	    triplePatternsInitialQuery.remove(triplePatternsQStar.get(i));
	}
	TriplePattern tp;
	for (int i = 0; i < triplePatternsInitialQuery.size(); i++) {
	    tp = triplePatternsInitialQuery.get(i);
	    qStar.addTriplePattern(tp);
	    if (qStar.isFailing(session, alpha)) {
		qStar.removeTriplePattern(tp);
	    }
	}
	return qStar;
    }

    @Override
    public Query findAnMFS(Session session, Double alpha) {
	Query qPrim = factory.createQuery(rdfQuery, newInitialQuery);
	Query qStar = factory.createQuery("", newInitialQuery);
	Query qTemp;
	TriplePattern tp;
	for (int i = 0; i < nbTriplePatterns; i++) {
	    tp = ((AbstractQuery) qPrim).removeTriplePattern();
	    qTemp = ((AbstractQuery) qPrim).concat(qStar);
	    if (!qTemp.isFailing(session, alpha))
		qStar.addTriplePattern(tp);
	}
	return qStar;
    }

    /**
     * Remove a random triple pattern from this query
     * 
     * @return the removed triple pattern
     */
    protected TriplePattern removeTriplePattern() {
	// we remove them in the order of the query
	// it could also be non-deterministic
	int numTriplePattern = 0;
	TriplePattern res = triplePatterns.remove(numTriplePattern);
	updateQueryAfterRemoveTP();
	return res;
    }

    @Override
    public void removeTriplePattern(TriplePattern t) {
	triplePatterns.remove(t);
	updateQueryAfterRemoveTP();
    }

    /**
     * Computes the string of this query when a some triple patterns have
     * changed
     */
    protected void updateQueryAfterRemoveTP() {
	nbTriplePatterns--;
	rdfQuery = computeRDFQuery(triplePatterns);
    }

    /**
     * Computes the string of this query from a list of triple patterns
     * 
     * @param listTP
     *            a list of triple patterns
     * @return the string of this query
     */
    private String computeRDFQuery(List<TriplePattern> listTP) {
	String res = "";
	int nbTPs = listTP.size();
	if (nbTPs > 0) {
	    res = "SELECT * WHERE { ";
	    for (int i = 0; i < nbTPs; i++) {
		if (i > 0)
		    res += " . ";
		res += listTP.get(i).toString();
	    }
	    res += " }";
	}
	return res;
    }

    /**
     * Return a new query that is the concatenation of this query with the input
     * query
     * 
     * @param queryToConcat
     *            query that must be concatened
     * @return the concatened query
     */
    protected Query concat(Query queryToConcat) {
	List<TriplePattern> listTP = new ArrayList<TriplePattern>(this.triplePatterns);
	listTP.addAll(queryToConcat.getTriplePatterns());
	final Query createQuery = factory.createQuery(computeRDFQuery(listTP), newInitialQuery);
	return createQuery;
    }

    @Override
    public void addTriplePattern(TriplePattern tp) {
	triplePatterns.add(tp);
	nbTriplePatterns++;
	if (rdfQuery.equals(""))
	    rdfQuery = "SELECT * WHERE { " + tp.toString() + " }";
	else {
	    rdfQuery = rdfQuery.substring(0, rdfQuery.length() - 1) + ". " + tp.toString() + " }";
	}
    }

    /**
     * Compute the larger subqueries that do not include the input MFS
     * 
     * @param mfs
     *            an MFS of the query
     */
    protected List<Query> computePotentialXSS(Query mfs) {
	List<Query> res = new ArrayList<Query>();
	if (nbTriplePatterns == 1)
	    return res;
	for (TriplePattern t : mfs.getTriplePatterns()) {
	    AbstractQuery q = (AbstractQuery) factory.createQuery(rdfQuery, newInitialQuery);
	    q.removeTriplePattern(t);
	    res.add(q);
	}
	return res;
    }

    @Override
    public Set<Query> computeAllMFS(Session p, ComputeMFSAndXSSAlgorithm algo, Double alpha) {
	// we only computes the MFSs if it was not already done
	if (allMFS == null) {
	    startAlgorithm(p, algo, alpha);
	}
	return allMFS;
    }

    @Override
    public Set<Query> computeAllXSS(Session p, ComputeMFSAndXSSAlgorithm algo, Double alpha) {
	// we only computes the XSSs if it was not already done
	if (allXSS == null) {
	    this.startAlgorithm(p, algo, alpha);
	}
	return allXSS;
    }

    /**
     * Launch the chosen algorithm to compute the XSSs and MFSs
     * 
     * @param session
     *            the connection to the KB
     * @param algo
     *            the chosen algorithm
     */
    protected void startAlgorithm(Session session, ComputeMFSAndXSSAlgorithm algo, Double alpha) {
	switch (algo) {
	case LBA:
	    runLBA(session, alpha);
	    break;
	case DFS:
	    runDFS(session, alpha);
	    break;
	default:
	    throw new NotYetImplementedException();
	}
    }

    /**
     * Choose a query from a list of queries
     * 
     * @param queries
     *            a list of queries
     * @return
     */
    protected Query element(List<Query> queries) {
	// We do not use a random query as it seems to be more efficient to take
	// the first one
	return queries.get(0);
    }

    @Override
    public boolean includes(Query q) {
	for (TriplePattern tp : q.getTriplePatterns()) {
	    if (!includes(tp))
		return false;
	}
	return true;
    }

    /**
     * The opt query uses bitmap so we define a method to avoid using bitmap
     * when needed.
     * 
     * @param q
     *            a query
     * @return true if this query includes q
     */
    private boolean includesSimple(Query q) {
	for (TriplePattern tp : q.getTriplePatterns()) {
	    if (!includes(tp))
		return false;
	}
	return true;
    }

    /**
     * Checks whether this query includes a triple pattern
     * 
     * @param t
     *            a triple pattern
     * @return true if this query includes t
     */
    private boolean includes(TriplePattern t) {
	if (rdfQuery.indexOf(t.toString()) == -1)
	    return false;
	return true;
    }

    /**
     * Check whether this query is included in one of the input queries
     * 
     * @param queries
     *            the input queries
     * @return true if this query is included in one of the input queries
     */
    public boolean isIncludedInAQueryOf(Set<Query> queries) {
	for (Query q : queries) {
	    if (((Query) q).includes(this)) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Check whether this query is included in one of the input queries
     * 
     * @param queries
     *            the input queries
     * @return true if this query is included in one of the input queries
     */
    public boolean isIncludedInAQueryOf(List<Query> queries) {
	for (Query q : queries) {
	    if (((Query) q).includes(this)) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public boolean includesAQueryOf(Set<Query> queries) {
	for (Query q : queries) {
	    if (this.includesSimple(q))
		return true;
	}
	return false;
    }

    @Override
    public boolean includesAQueryOf(List<Query> queries) {
	for (Query q : queries) {
	    if (this.includesSimple(q))
		return true;
	}
	return false;
    }

    /**
     * Init the LBA algorithm
     */
    public void initLBA() {
    }

    @Override
    public void runLBA(Session session, Set<Query> knownMFS, Set<Query> knownXSS, Double alpha) {
	((AbstractSession) session).setExecutedQueryCount(0);
	this.setInitialQuery(this);
	initLBA();
	Query qPrim, qStarStar;
	allMFS = new HashSet<Query>();
	allXSS = new HashSet<Query>();
	List<Query> pxss = null;
	if (knownMFS.size() == 0) {
	    Query qStar = (Query) findAnMFS(session, alpha);
	    allMFS.add(qStar);
	    pxss = computePotentialXSS(qStar);
	} else {
	    allMFS.addAll(knownMFS);
	    for (Query query : knownMFS) {
		if (pxss == null) {
		    pxss = computePotentialXSS(query);
		} else {
		    refactor(query, pxss);
		}
	    }
	}
	// if the pxss includes an xss, it is failing and we need to keep it to
	// find other MFSs
	// otherwise the pxss is equals to the xss and we can remove it
	allXSS.addAll(knownXSS);
	pxss.removeAll(knownXSS);
	while (!pxss.isEmpty()) {
	    qPrim = element(pxss);
	    if (!qPrim.isFailing(session, alpha)) { // Q' is an XSS
		allXSS.add(qPrim);
		pxss.remove(qPrim);
	    } else { // Q' contains an MFS
		qStarStar = qPrim.findAnMFS(session, alpha);
		allMFS.add(qStarStar);
		refactor(qStarStar, pxss);
	    }
	}
    }

    /**
     * Add a new MFS and change the pxss accordingly.
     * 
     * @param qStar
     *            the MFS
     * @param pxss
     *            the current list of pxss
     */
    private void refactor(Query qStar, List<Query> pxss) {
	List<Query> pxssPrim;
	for (ListIterator<Query> itQPrimPrim = pxss.listIterator(); itQPrimPrim.hasNext();) {
	    Query qPrimPrim = itQPrimPrim.next();
	    if (((Query) qPrimPrim).includes(qStar)) {
		itQPrimPrim.remove();
		pxssPrim = ((AbstractQuery) qPrimPrim).computePotentialXSS(qStar);
		for (Query qJ : pxssPrim) {
		    if (!((AbstractQuery) qJ).isIncludedInAQueryOf(pxss)
			    && !((AbstractQuery) qJ).isIncludedInAQueryOf(allXSS)) {
			itQPrimPrim.add(qJ);
		    }
		}
	    }
	}
    }

    @Override
    public void runLBA(Session session, Double alpha) {
	runLBA(session, new HashSet<Query>(), new HashSet<Query>(), alpha);
    }

    /**
     * Display nicely a list of queries "t1^t2^t3"
     * 
     * @param queries
     *            a list of queries
     * @return a nice string representation of this list of queries
     */
    public String toSimpleString(List<Query> queries) {
	String res = "";
	for (Query q : queries) {
	    res += "\t" + ((AbstractQuery) q).toSimpleString(this) + "\n";
	}
	return res;
    }

    /**
     * Create a query with the triple patterns that have the input positions.
     * 
     * @param pos
     *            the input positions
     * @return a query with the triple patterns corresponding to the positions
     */
    public Query createCorrespondingQuery(List<Integer> pos) {
	List<TriplePattern> tp = new ArrayList<TriplePattern>();
	List<TriplePattern> allTp = getTriplePatterns();
	for (int i = 0; i < pos.size(); i++) {
	    tp.add(allTp.get(pos.get(i)));
	}
	final Query createQuery = factory.createQuery(tp, newInitialQuery);
	return createQuery;
    }

    /**
     * Get the position of the triple patterns of this query in the initial
     * query
     * 
     * @param initialQuery
     *            the initial query
     * @return the position the positions of the triple patterns of this query
     *         in the initial query
     */
    public List<Integer> getIndexOfTriplePattern(Query initialQuery) {
	List<Integer> res = new ArrayList<Integer>();
	for (int i = 0; i < triplePatterns.size(); i++) {
	    TriplePattern temp = triplePatterns.get(i);
	    res.add(initialQuery.getTriplePatterns().indexOf(temp));
	}
	return res;
    }

    @Override
    public int size() {
	return nbTriplePatterns;
    }

    /**
     * Return a string representing the query in the simple form: ti ^ ... ^ tj
     * w.r.t the initial query
     * 
     * @param initialQuery
     *            the initial failing query
     * @return a string representing the query in the simple form: ti ^ ... ^ tj
     *         w.r.t the initial query
     */
    public String toSimpleString(Query initialQuery) {
	TriplePattern temp;
	String res = "";
	for (int i = 0; i < triplePatterns.size(); i++) {
	    if (i > 0)
		res += " ^ ";
	    temp = triplePatterns.get(i);
	    if (initialQuery != null)
		res += "t" + (initialQuery.getTriplePatterns().indexOf(temp) + 1);
	}
	return res;
    }

    @Override
    public String toNativeQuery(Double alpha) {
	return rdfQuery;
    }

    /**
     * 
     * @param executedQueries
     *            a cache of already executed queries
     * @param s
     *            connection to the KB
     * @return true iff this query is failing
     */
    private boolean isFailingForDFS(Map<Query, Boolean> executedQueries, Session s, Double alpha) {
	if (this.equals(this.getInitialQuery())) {
	    return true;
	}
	Boolean val = executedQueries.get(this);
	if (val == null) {
	    val = isFailing(s, alpha);
	    executedQueries.put(this, val);
	}
	return val;
    }

    /**
     * Get all the subqueries of this query
     * 
     * @return the subqueries of this query
     */
    public List<Query> getSubQueries() {
	List<Query> res = new ArrayList<Query>();
	for (TriplePattern tp : getTriplePatterns()) {
	    AbstractQuery qNew = (AbstractQuery) factory.createQuery(toString(), newInitialQuery);
	    qNew.removeTriplePattern(tp);
	    res.add(qNew);
	}
	return res;
    }

    /**
     * Get all the superqueries of this query
     * 
     * @return the superqueries of this query
     */
    protected List<Query> getSuperQueries() {
	List<Query> res = new ArrayList<Query>();
	for (TriplePattern tp : this.getInitialQuery().getTriplePatterns()) {
	    if (!includes(tp)) {
		Query qNew = factory.createQuery(toString(), newInitialQuery);
		((Query) qNew).addTriplePattern(tp);
		res.add(qNew);
	    }
	}
	return res;
    }

    /**
     * Enable us to do some initialization in subclass
     */
    protected void initDFS() {
    }

    @Override
    public void runDFS(Session session, Double alpha) {
	initDFS();
	allMFS = new HashSet<Query>();
	allXSS = new HashSet<Query>();
	this.setInitialQuery(this);
	((AbstractSession) session).setExecutedQueryCount(0);
	List<Query> listQuery = new ArrayList<Query>();
	Map<Query, Boolean> executedQueries = new HashMap<Query, Boolean>();
	Map<Query, Boolean> markedQueries = new HashMap<Query, Boolean>();
	listQuery.add(this);
	while (!listQuery.isEmpty()) {
	    Query qTemp = listQuery.remove(0);
	    if (!markedQueries.containsKey(qTemp)) {
		markedQueries.put(qTemp, true);
		List<Query> subqueries = ((AbstractQuery) qTemp).getSubQueries();
		if (((AbstractQuery) qTemp).isFailingForDFS(executedQueries, session, alpha)) {
		    boolean isMFS = true;
		    for (Query subquery : subqueries) {
			if (((AbstractQuery) subquery).isFailingForDFS(executedQueries, session, alpha))
			    isMFS = false;
		    }
		    if (isMFS)
			allMFS.add(qTemp);
		} else { // Potential XSS
		    List<Query> superqueries = ((AbstractQuery) qTemp).getSuperQueries();
		    boolean isXSS = true;
		    for (Query superquery : superqueries) {
			if (!((AbstractQuery) superquery).isFailingForDFS(executedQueries, session, alpha))
			    isXSS = false;
		    }
		    if (isXSS && !qTemp.isEmpty())
			allXSS.add(qTemp);
		}
		listQuery.addAll(0, subqueries);
	    }

	}
    }

}
