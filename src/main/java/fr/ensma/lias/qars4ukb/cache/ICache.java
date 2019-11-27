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

import fr.ensma.lias.qars4ukb.query.Query;

/**
 * @author Stéphane JEAN
 * @author Mickael BARON
 */
public interface ICache {

	/**
	 * empty the cache
	 */
	void clearCache();

	/**
	 * Initialize the cache
	 */
	void initCacheBeforeLBA();

	/**
	 * Get the number of cache hits
	 * 
	 * @return the number of cache hits
	 */
	int getNbCacheHits();

	/**
	 * Check in the cache whether this query is successful
	 * 
	 * @param q     the query
	 * @param alpha the threshold
	 * @return true iff this query is successful
	 */
	boolean isSuccessfulByCache(Query q, Double alpha);

	/**
	 * Check in the cache whether this query is failing
	 * 
	 * @param q     the query
	 * @param alpha the threshold
	 * @return true iff this query is failing
	 */
	boolean isFailingByCache(Query q, Double alpha);

	/**
	 * Add this failing query to the cache
	 * 
	 * @param q                  the failing query
	 * @param alpha              the threshold
	 * @param isCartesianProduct true if the query is a cartesian product
	 */
	void addFailingQuery(Query q, boolean isCartesianProduct, Double alpha);

	/**
	 * Add this successful query to the cache
	 * 
	 * @param q     the successful query
	 * @param alpha the threshold
	 */
	void addSuccessfulQuery(Query q, Double alpha);

}