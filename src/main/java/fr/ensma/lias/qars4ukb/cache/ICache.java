package fr.ensma.lias.qars4ukb.cache;

import fr.ensma.lias.qars4ukb.query.Query;

public interface ICache {

    /**
     * Initialize the cache
     */
    void initCache();

    /**
     * Get the number of cache hits
     * @return the number of cache hits
     */
    int getNbCacheHits();

    /**
     * Check in the cache whether this query is successful
     * 
     * @param q
     *            the query
     * @param alpha the threshold
     * @return true iff this query is successful
     */
    boolean isSuccessfulByCache(Query q, Double alpha);

    /**
     * Check in the cache whether this query is failing
     * 
     * @param q
     *            the query
     * @param alpha the threshold
     * @return true iff this query is failing
     */
    boolean isFailingByCache(Query q, Double alpha);

    /**
     * Add this failing query to the cache
     * @param q the failing query
     * @param alpha the threshold
     * @param isCartesianProduct true if the query is a cartesian product
     */
    void addFailingQuery(Query q, boolean isCartesianProduct, Double alpha);

    /**
     * Add this successful query to the cache
     * @param q the successful query
     * @param alpha the threshold
     */
    void addSuccessfulQuery(Query q, Double alpha);

}