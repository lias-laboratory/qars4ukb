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

import java.util.List;

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
	 * @return the result
	 */
	Result getResult(Session s);

	/**
	 * Checks whether this query has an empty result or not
	 * 
	 * @param s
	 *            the connection to the triplestore
	 * @return True is the result of this query is empty
	 */
	boolean isFailing(Session session);

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
	 * @return an MFS of this query
	 */
	Query findAnMFS(Session session);

	/**
	 * Run the LBA algorithm. It fills the allMFS and allXSS variable
	 * 
	 * @param s
	 *            the connection to the triplestore
	 */
	public void runLBA(Session session);

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
	 * 
	 * @return the query executed on the target platform
	 */
	String toNativeQuery();

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
	 * @return the set of MFSs of this query
	 */
	List<Query> computeAllMFS(Session s, ComputeMFSAndXSSAlgorithm algo);

	/**
	 * Compute the set of XSSs of this query with a specific algorithm.
	 * 
	 * @param s
	 *            the connection to the triplestore
	 * @param algo
	 *            the chosen algorithms
	 * @return the set of XSSs of this query
	 */
	List<Query> computeAllXSS(Session s, ComputeMFSAndXSSAlgorithm algo);

	/**
	 * Return the MFSs of this query
	 * @return the MFSs of this query
	 */
	List<Query> getAllMFS();

	/** 
	 * Return the XSSs of this query
	 * @return the XSSs of this query
	 */
	List<Query> getAllXSS();
	
	/**
	 * Test if the input query is included in this query
	 * 
	 * @param q
	 *            the input query
	 * @return True if the input query is included in this query
	 */
	boolean includes(Query q);
}
