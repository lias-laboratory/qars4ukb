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
     * @throws Exception
     */
    Result getResult(Session s) throws Exception;

    /**
     * Checks whether this query has an empty result or not
     * 
     * @return True is the result of this query is empty
     */
    boolean isFailing(Session session) throws Exception;

    /**
     * Returns the triple patterns of the query
     * 
     * @return the triple patterns of the query
     */
    List<TriplePattern> getTriplePatterns();

    /**
     * Run the LBA algorithm. It fills the allMFS and allXSS variable
     * 
     * @param session
     * @throws Exception
     */
    public void runLBA(Session session) throws Exception;

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
     * Return the inverse of this query
     * 
     * @param q
     *            the query to which the returned query is the inverse
     * @return the inverse of this query
     */
    Query inverseOf(Query q);

    
    /**
     * Compute the set of MFS of this query with a specific algorithm.
     * 
     * @param p
     * @param algo
     * @return
     * @throws Exception
     */
    List<Query> computeAllMFS(Session p, ComputeMFSAndXSSAlgorithm algo) throws Exception;
    
    /**
     * Compute the set of XSS of this query with a specific algorithm.
     * 
     * @param p
     * @param algo
     * @return
     * @throws Exception
     */
    List<Query> computeAllXSS(Session p, ComputeMFSAndXSSAlgorithm algo) throws Exception;
    
    /**
     * @return
     */
    List<Query> getAllMFS();
    
    /**
     * @return
     */
    List<Query> getAllXSS();
}
