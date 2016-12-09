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

import fr.ensma.lias.qars4ukb.AbstractSession;
import fr.ensma.lias.qars4ukb.Session;
import fr.ensma.lias.qars4ukb.exception.NotYetImplementedException;

/**
 * @author Stéphane JEAN
 */
public abstract class AbstractQuery implements Query {


	public enum ComputeMFSAndXSSAlgorithm {
		LBA, DFS
	}

	protected String rdfQuery;

	protected QueryFactory factory;

	protected List<TriplePattern> triplePatterns;
	protected int nbTriplePatterns;

	public List<Query> allMFS;
	public List<Query> allXSS;

	protected Query newInitialQuery;

	public AbstractQuery(QueryFactory factory, String query) {
		this.factory = factory;
		this.rdfQuery = query;
		this.decomposeQuery();
		nbTriplePatterns = triplePatterns.size();
	}

	public AbstractQuery(QueryFactory factory, List<TriplePattern> tps) {
		this.factory = factory;
		this.rdfQuery = computeRDFQuery(tps);
		triplePatterns = tps;
		nbTriplePatterns = triplePatterns.size();
	}

	public void setInitialQuery(Query query) {
		this.newInitialQuery = query;
	}

	public Query getInitialQuery() {
		return this.newInitialQuery;
	}

	@Override
	public List<Query> getAllMFS() {
		return this.allMFS;
	}

	@Override
	public List<Query> getAllXSS() {
		return this.allXSS;
	}

	@Override
	public List<TriplePattern> getTriplePatterns() {
		return triplePatterns;
	}

	protected abstract boolean isFailingAux(Session session);

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

	@Override
	public String toString() {
		return rdfQuery;
	}

	@Override
	public boolean isEmpty() {
		return nbTriplePatterns == 0;
	}

	@Override
	public boolean isFailing(Session session) {
		if (isEmpty())
			return false;
		return isFailingAux(session);
	}

	/**
	 * Return an MFS of this query (must be failing)
	 * 
	 * @return an MFS of this query
	 * @throws Exception
	 */
	protected Query findAnMFS(Session session) {
		Query qPrim = factory.createQuery(rdfQuery);
		Query qStar = factory.createQuery("", newInitialQuery);
		Query qTemp;
		TriplePattern tp;
		for (int i = 0; i < nbTriplePatterns; i++) {
			tp = ((AbstractQuery) qPrim).removeTriplePattern();
			qTemp = ((AbstractQuery) qPrim).concat(qStar);
			if (!qTemp.isFailing(session))
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
		// un-comment to test the non-determinism
		int numTriplePattern = 0;
		// if (nbTriplePatterns>1) {
		// Random r = new Random();
		// numTriplePattern = r.nextInt(nbTriplePatterns);
		// }
		TriplePattern res = triplePatterns.remove(numTriplePattern);
		updateQueryAfterRemoveTP();
		return res;
	}

	/**
	 * Remove the input triple pattern from this query
	 * 
	 * @param the
	 *            triple pattern to remove
	 */
	protected void removeTriplePattern(TriplePattern t) {
		triplePatterns.remove(t);
		updateQueryAfterRemoveTP();
	}

	protected void updateQueryAfterRemoveTP() {
		nbTriplePatterns--;
		rdfQuery = computeRDFQuery(triplePatterns);
	}

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
	public List<Query> computeAllMFS(Session p, ComputeMFSAndXSSAlgorithm algo) {
		if (allMFS == null) {
			startAlgorithm(p, algo);
		}
		return allMFS;
	}

	@Override
	public List<Query> computeAllXSS(Session p, ComputeMFSAndXSSAlgorithm algo) {
		if (allXSS == null) {
			this.startAlgorithm(p, algo);
		}
		return allXSS;
	}

	protected void startAlgorithm(Session session, ComputeMFSAndXSSAlgorithm algo) {
		switch (algo) {
		case LBA:
			runLBA(session);
			break;
		case DFS:
			runDFS(session);
			break;
		}
	}

	/**
	 * Choose a random element from the list
	 * 
	 * @param queries
	 * @return
	 */
	protected Query element(List<Query> queries) {
		// Random r = new Random();
		// int numQuery = r.nextInt(queries.size());
		// return queries.get(numQuery);
		// // j'enlève le non-déterminisme
		return queries.get(0);
	}

	/**
	 * Test if the input query is included in this query
	 * 
	 * @param q
	 *            the input query
	 * @return True if the input query is included in this query
	 */
	protected boolean includes(Query q) {
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
	 * @return
	 */
	private boolean includesSimple(Query q) {
		for (TriplePattern tp : q.getTriplePatterns()) {
			if (!includes(tp))
				return false;
		}
		return true;
	}

	private boolean includes(TriplePattern t) {
		if (rdfQuery.indexOf(t.toString()) == -1)
			return false;
		return true;
	}

	/**
	 * Test if this query is included in one of the input queries
	 * 
	 * @param queries
	 *            the input queries
	 * @return true if this query is included in one of the input queries
	 */
	protected boolean isIncludedInAQueryOf(List<Query> queries) {
		for (Query q : queries) {
			if (((AbstractQuery) q).includes(this)) {
				return true;
			}
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

	public void initLBA() {
	}

	public void runLBA(Session session, List<Query> knownMFS) {
		((AbstractSession) session).setExecutedQueryCount(0);
		this.setInitialQuery(this);
		initLBA();
		// List<Query> pxssPrim;
		Query qPrim, qStarStar;
		allMFS = new ArrayList<Query>();
		allXSS = new ArrayList<Query>();
		List<Query> pxss = null;
		if (knownMFS.size() == 0) {
			Query qStar = (AbstractQuery) findAnMFS(session);
			allMFS.add(qStar);
			// System.out.println("MFS : " + qStar.toSimpleString(this));
			pxss = computePotentialXSS(qStar);
			// System.out.println("Size of PXSS : " + pxss.size());
		} else {
			allMFS.addAll(knownMFS);
			Query firstMFS = knownMFS.get(0);
			pxss = computePotentialXSS(firstMFS);
			for (int i = 1; i < knownMFS.size(); i++) {
				refactor(knownMFS.get(i), pxss);
			}
		}
		while (!pxss.isEmpty()) {
			// System.out.println("********** Liste des PXSS *************");
			// System.out.println(this.toSimpleString(pxss));
			// System.out.println("***************************************");
			qPrim = element(pxss);
			// System.out.println("Test of the PXSS : " +
			// System.out.println(qPrim.toSimpleString(this));
			// System.out.println(qPrim);
			if (!qPrim.isFailing(session)) { // Q' is an XSS
				// System.out.println("Added to the XSS !");
				allXSS.add(qPrim);
				pxss.remove(qPrim);
			} else { // Q' contains an MFS
				qStarStar = ((AbstractQuery) qPrim).findAnMFS(session);
				// System.out.println("Empty, its MFS : " +
				// qStarStar.toSimpleString(this));
				allMFS.add(qStarStar);
				// System.out.println("-- browse PXSS to replace the one that
				// include the mfs : -- ");
				refactor(qStarStar, pxss);
			}
		}
	}

	/**
	 * Add a new MFS and change the pxss accordingly.
	 * 
	 * @param qStar
	 * @param pxss
	 */
	private void refactor(Query qStar, List<Query> pxss) {
		List<Query> pxssPrim;
		for (ListIterator<Query> itQPrimPrim = pxss.listIterator(); itQPrimPrim.hasNext();) {
			Query qPrimPrim = itQPrimPrim.next();
			// System.out.println("---- Element de pxss testé : -- " +
			// qPrimPrim.toSimpleString(this));
			if (((AbstractQuery) qPrimPrim).includes(qStar)) {
				// System.out.println("---- il inclut la mfs, on l'enlève de
				// pxss ----");
				itQPrimPrim.remove();
				pxssPrim = ((AbstractQuery) qPrimPrim).computePotentialXSS(qStar);
				// System.out.println("-- nombre de PXSS correspondant que l'on
				// peut ajouter : "
				// + pxssPrim.size());
				for (Query qJ : pxssPrim) {
					if (!((AbstractQuery) qJ).isIncludedInAQueryOf(pxss)
							&& !((AbstractQuery) qJ).isIncludedInAQueryOf(allXSS)) {
						// System.out.println("****** on ajoute la requête
						// suivante à pxss car elle n'est pas inclu dans une
						// autre : "
						// + qJ.toSimpleString(this));
						itQPrimPrim.add(qJ);
					}
				}
			}
		}
	}

	@Override
	public void runLBA(Session session) {
		runLBA(session, new ArrayList<Query>());
	}

	public String toSimpleString(List<Query> queries) {
		String res = "";
		for (Query q : queries) {
			res += "\t" + ((AbstractQuery) q).toSimpleString(this) + "\n";
		}
		return res;
	}

	/**
	 * Create a query with the triples patterns that have the input positions.
	 * 
	 * @param pos
	 * @return
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
	 * Get the position of the triple of this query in the input query
	 * 
	 * @param initialQuery
	 * @return
	 */
	public List<Integer> getIndexOfTriplePattern(Query initialQuery) {
		List<Integer> res = new ArrayList<Integer>();
		for (int i = 0; i < triplePatterns.size(); i++) {
			TriplePattern temp = triplePatterns.get(i);
			res.add(initialQuery.getTriplePatterns().indexOf(temp));
		}
		return res;
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
	protected String toSimpleString(Query initialQuery) {
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
	public String toNativeQuery() {
		return rdfQuery;
	}

	private boolean isFailingForDFS(Map<Query, Boolean> executedQueries, Session s) {
		if (this.equals(this.getInitialQuery())) {
			return true;
		}
		Boolean val = executedQueries.get(this);
		if (val == null) {
			val = isFailing(s);
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
				((AbstractQuery) qNew).addTriplePattern(tp);
				res.add(qNew);
			}
		}
		return res;
	}

	public void runDFS(Session session) {
		allMFS = new ArrayList<Query>();
		allXSS = new ArrayList<Query>();
		this.setInitialQuery(this);
		((AbstractSession) session).setExecutedQueryCount(0);
		List<Query> listQuery = new ArrayList<Query>();
		Map<Query, Boolean> executedQueries = new HashMap<Query, Boolean>();
		Map<Query, Boolean> markedQueries = new HashMap<Query, Boolean>();
		listQuery.add(this);
		while (!listQuery.isEmpty()) {
			Query qTemp = listQuery.remove(0);
			// System.out.println("Traitement de "
			// + qTemp.toSimpleString(initialQuery));
			if (!markedQueries.containsKey(qTemp)) {
				markedQueries.put(qTemp, true);
				List<Query> subqueries = ((AbstractQuery) qTemp).getSubQueries();
				if (((AbstractQuery) qTemp).isFailingForDFS(executedQueries, session)) {
					// this is a potential MFS
					// System.out.println("potential mfs");
					boolean isMFS = true;
					for (Query subquery : subqueries) {
						if (((AbstractQuery) subquery).isFailingForDFS(executedQueries, session))
							isMFS = false;
					}
					if (isMFS)
						allMFS.add(qTemp);
				} else { // Potential XSS
					List<Query> superqueries = ((AbstractQuery) qTemp).getSuperQueries();
					boolean isXSS = true;
					for (Query superquery : superqueries) {
						if (!((AbstractQuery) superquery).isFailingForDFS(executedQueries, session))
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
