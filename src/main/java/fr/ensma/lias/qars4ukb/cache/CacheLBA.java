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
package fr.ensma.lias.qars4ukb.cache;

import java.util.ArrayList;
import java.util.List;

import fr.ensma.lias.qars4ukb.query.Query;

/**
 * @author Stéphane JEAN
 * @author Mickael BARON
 */
public class CacheLBA implements ICache {

    protected int nbCacheHits;

    protected List<Query> successfulCachedQueries;

    protected List<Query> failingCachedQueries;

    private static CacheLBA instance;

    private CacheLBA() {
    }

    public static CacheLBA getInstance() {
	if (instance == null) {
	    instance = new CacheLBA();
	}
	return instance;
    }

    /* (non-Javadoc)
     * @see fr.ensma.lias.qars4ukb.cache.ICache#initCache()
     */
    @Override
    public void initCache() {
	nbCacheHits = 0;
	successfulCachedQueries = new ArrayList<Query>();
	// System.out.println("cache query empty");
	failingCachedQueries = new ArrayList<Query>();
    }

    public void incrementeNbRepetedQuery() {
	nbCacheHits++;
    }


    @Override
    public int getCacheHits() {
	return nbCacheHits;
    }

    public List<Query> getSuccessfulCachedQueries() {
	return successfulCachedQueries;
    }

    public List<Query> getFailingCachedQueries() {
	return failingCachedQueries;
    }


    @Override
    public boolean isSuccessfulByCache(Query q) {
	for (Query qCache : getSuccessfulCachedQueries()) {
	    if (qCache.includes(q)) {
		nbCacheHits++;
		return true;
	    }
	}
	return false;
    }


    @Override
    public boolean isFailingByCache(Query q) {
	for (Query qCache : getFailingCachedQueries()) {
	    if (q.includes(qCache)) {
		nbCacheHits++;
		return true;
	    }
	}
	return false;
    }

    @Override
    public void addFailingQuery(Query q, boolean isCartesianProduct) {
	if (isCartesianProduct) {
	    getFailingCachedQueries().add(q);
	}
    }
    
    @Override
    public void addSuccessfulQuery(Query q) {
	    getSuccessfulCachedQueries().add(q);
    }
}
