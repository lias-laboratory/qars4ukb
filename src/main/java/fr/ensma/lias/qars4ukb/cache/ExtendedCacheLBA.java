package fr.ensma.lias.qars4ukb.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.ensma.lias.qars4ukb.query.Query;

/**
 * Extended management of the cache for executing top-down, bottom-up and hybrid
 * approaches
 * 
 * I try an implemetation that fills extensively the cache (with redundant
 * information to optimize the finding with the hash function)
 * 
 * @author Stéphane JEAN
 */
public class ExtendedCacheLBA implements ICache {

	/**
	 * Singleton implementation
	 */
	private static ExtendedCacheLBA instance;

	private ExtendedCacheLBA() {
		failingCachedQueries = new HashMap<>();
		successfulCachedQueries = new HashMap<>();
	}

	public static ExtendedCacheLBA getInstance() {
		if (instance == null) {
			instance = new ExtendedCacheLBA();
		}
		return instance;
	}

	/**
	 * Successful queries in the cache with the maximum degree to which it is
	 * successful
	 */
	private Map<Query, Double> successfulCachedQueries;

	/**
	 * Getter of successful cached queries
	 * 
	 * @return the successful cached queries
	 */
	public Map<Query, Double> getSuccessfulCachedQueries() {
		// I only include it for Junit Test
		return successfulCachedQueries;
	}

	/**
	 * Failing queries in the cache with the minimum degree to which it is failing
	 */
	private Map<Query, Double> failingCachedQueries;

	/**
	 * Getter of failing cached queries
	 * 
	 * @return the failing cached queries
	 */
	public Map<Query, Double> getFailingCachedQueries() {
		// I only include it for Junit Test
		return failingCachedQueries;
	}

	/**
	 * Number of cache hits
	 */
	private int nbCacheHits;

	@Override
	public void clearCache() {
		nbCacheHits = 0;
		failingCachedQueries.clear();
		successfulCachedQueries.clear();
	}

	@Override
	public void initCacheBeforeLBA() {
		// we do nothing as the extended cache should not be
		// initialized after each execution of LBA
	}

	@Override
	public int getNbCacheHits() {
		return nbCacheHits;
	}

	@Override
	public boolean isSuccessfulByCache(Query q, Double alpha) {
		boolean res = false;
		Double alphaCache = successfulCachedQueries.get(q);
		if (alphaCache != null) {
			// the query is already in the cache
			if (alpha <= alphaCache) {
				// with a greater degree => this query is successful
				nbCacheHits++;
				res = true;
			} else {
				// no need to search for a superQuery (they will be with the
				// same or lower degree)
				res = false;
			}
		} else {
			// the query is not in the cache, we should look for a superquery in
			// the cache with a greater or equals degree
			// if this is the case, the query is successful
			// and we add it with the maximum threshold of all its subqueries
			Double maxAlpha = alpha;
			Set<Query> queriesInCache = successfulCachedQueries.keySet();
			for (Query qCache : queriesInCache) {
				if (qCache.includes(q)) {
					// if the superquery succeeds with a greater degree than
					// alpha then q succeeeds for this degree
					alphaCache = successfulCachedQueries.get(qCache);
					if (alphaCache >= maxAlpha) {
						res = true;
						maxAlpha = alphaCache;
					}
				}
			}
			if (res) { // we add it with the maximum degree
				successfulCachedQueries.put(q, maxAlpha);
				nbCacheHits++;
			}
		}
		return res;
	}

	@Override
	public boolean isFailingByCache(Query q, Double alpha) {
		boolean res = false;
		Double alphaCache = failingCachedQueries.get(q);
		if (alphaCache != null) {
			// the query is already in the cache
			if (alpha >= alphaCache) {
				// with a lower degree => this query is failing
				res = true;
				nbCacheHits++;
			} else {
				// no need to search for a subquery (they will be with the same
				// or greater degree)
				res = false;
			}
		} else {
			// the query is not in the cache, we should look for a subquery in
			// the cache with a lower or equals degree
			// if this is the case, the query is failing
			// and we add it with the minimum threshold of all its subqueries
			Double minAlpha = alpha;
			Set<Query> queriesInCache = failingCachedQueries.keySet();
			for (Query qCache : queriesInCache) {
				if (q.includes(qCache)) {
					// if the included query fails with a lower degree than
					// alpha then q fails for this degree
					alphaCache = failingCachedQueries.get(qCache);
					if (alphaCache <= minAlpha) {
						res = true;
						minAlpha = alphaCache;
					}
				}
			}
			if (res) { // we add it with the minimum degree
				failingCachedQueries.put(q, minAlpha);
				// there cannot be a superquery in the cache with a greater
				// degree
				nbCacheHits++;
			}
		}
		return res;
	}

	@Override
	public void addFailingQuery(Query q, boolean isCartesianProduct, Double alpha) {
		// this query could be in the cache with a greater degree
		// or not in the cache (and we are sure there is no subquery with a
		// lower or equals degree to alpha)
		// in both cases we add it to the cache with the degree alpha
		failingCachedQueries.put(q, alpha);
		// all its superqueries should have the same min degree (or lower)
		updateSuperqueriesFailing(q, alpha);
	}

	/**
	 * Update all the superqueries of the query q with the given threshold
	 * 
	 * @param q     the query
	 * @param alpha the threshold
	 */
	private void updateSuperqueriesFailing(Query q, Double alpha) {
		Double alphaCache;
		Set<Query> queriesInCache = failingCachedQueries.keySet();
		for (Query qCache : queriesInCache) {
			if (qCache.includes(q)) {
				alphaCache = failingCachedQueries.get(qCache);
				if (alphaCache > alpha)
					failingCachedQueries.put(qCache, alpha);
			}
		}
	}

	@Override
	public void addSuccessfulQuery(Query q, Double alpha) {
		// this query could be in the cache with a lower degree
		// or not in the cache (and we are sure there is no subquery with a
		// lower or equals degree to alpha)
		// in both cases we add it to the cache with the degree alpha
		successfulCachedQueries.put(q, alpha);
		// all its subqueries should have the same max degree (or greater)
		updateSubqueriesSuccessful(q, alpha);
	}

	/**
	 * Update all the subqueries of the query q with the given threshold
	 * 
	 * @param q     the query
	 * @param alpha the threshold
	 */
	private void updateSubqueriesSuccessful(Query q, Double alpha) {
		Double alphaCache;
		Set<Query> queriesInCache = successfulCachedQueries.keySet();
		for (Query qCache : queriesInCache) {
			if (q.includes(qCache)) {
				alphaCache = successfulCachedQueries.get(qCache);
				if (alphaCache < alpha)
					successfulCachedQueries.put(qCache, alpha);
			}
		}
	}

}
