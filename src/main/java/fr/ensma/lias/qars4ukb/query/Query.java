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
import java.util.List;

import fr.ensma.lias.qars4ukb.AbstractSession;
import fr.ensma.lias.qars4ukb.Result;
import fr.ensma.lias.qars4ukb.Session;
import fr.ensma.lias.qars4ukb.query.AbstractQuery.ComputeMFSAndXSSAlgorithm;

/**
 * @author Stephane JEAN
 */
public interface Query {

	/**
	 * Execute this query
	 * 
	 * @param s
	 *            the connection to the triplestore
	 * @param alpha the threshold
	 * @return the result
	 */
	Result getResult(Session s, Double alpha);

	/**
	 * Checks whether this query has an empty result or not
	 * 
	 * @param s
	 *            the connection to the triplestore
	 * @param alpha the threshold
	 * @return True is the result of this query is empty
	 */
	boolean isFailing(Session session, Double alpha);

	/**
	 * Add a triple pattern to this query
	 * 
	 * @param tp
	 *            the added triple pattern
	 */
	void addTriplePattern(TriplePattern tp);

	/**
	 * Returns the triple patterns of the query
	 * 
	 * @return the triple patterns of the query
	 */
	List<TriplePattern> getTriplePatterns();

	/**
	 * Return an MFS of this query (must be failing)
	 * 
	 * @param s
	 *            the connection to the triplestore
	 * @param alpha the threshold
	 * @return an MFS of this query
	 */
	Query findAnMFS(Session session, Double alpha);

	/**
	 * Run the LBA algorithm. It fills the allMFS and allXSS variable
	 * 
	 * @param s
	 *            the connection to the triplestore
	 * @param alpha the threshold
	 */
	public void runLBA(Session session, Double alpha);
	
	

	/**
	 * Test if this query includes one of the input queries
	 * 
	 * @param queries
	 *            the input queries
	 * @return true if this query includes one of the input queries
	 */
	public boolean includesAQueryOf(List<Query> queries);

	/**
	 * Get the query executed on the target platform
	 * @param alpha the threshold
	 * @return the query executed on the target platform
	 */
	String toNativeQuery(Double alpha);

	/**
	 * Return true if this query is empty
	 * 
	 * @return true if this query is empty
	 */
	boolean isEmpty();

	/**
	 * Compute the set of MFSs of this query with a specific algorithm.
	 * 
	 * @param s
	 *            the connection to the triplestore
	 * @param algo
	 *            the chosen algorithms
	 * @param alpha the threshold
	 * @return the set of MFSs of this query
	 */
	List<Query> computeAllMFS(Session s, ComputeMFSAndXSSAlgorithm algo, Double alpha);

	/**
	 * Compute the set of XSSs of this query with a specific algorithm.
	 * 
	 * @param s
	 *            the connection to the triplestore
	 * @param algo
	 *            the chosen algorithms
	 * @param alpha the threshold
	 * @return the set of XSSs of this query
	 */
	List<Query> computeAllXSS(Session s, ComputeMFSAndXSSAlgorithm algo, Double alpha);

	/**
	 * Return the current MFSs of this query (for the last given alpha)
	 * @return the MFSs of this query
	 */
	List<Query> getAllMFS();

	/** 
	 * Return the current XSSs of this query (for the last given alpha)
	 * @return the XSSs of this query
	 */
	List<Query> getAllXSS();
	
	/**
	 * Test if the input query is included or equals to this query
	 * 
	 * @param q
	 *            the input query
	 * @return True if the input query is included in this query
	 */
	boolean includes(Query q);
	
	/**
	 * Get the factory of this query
	 */
	QueryFactory getFactory();

	/**
	 * Run the DFS algorithm and fills allMFS and XSS
	 * @param session connection to the KB
	 * @param alpha the threshold
	 */
	void runDFS(Session session, Double alpha);

	
	/**
	 * Launch the LBA algorithm with a set of known MFSs
	 * @param session the connection to the KB
	 * @param knownMFS the known MFSs
	 * @param knownXSS the known XSSs
	 * @param alpha the threshold
	 */
	void runLBA(Session session, List<Query> knownMFS, List<Query> knownXSS, Double alpha);
}
