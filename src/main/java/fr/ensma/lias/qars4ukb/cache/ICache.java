package fr.ensma.lias.qars4ukb.cache;

import fr.ensma.lias.qars4ukb.query.Query;

public interface ICache {

    /**
     * Initialize the cache
     */
    void initCache();

    /**
     * 
     * @return
     */
    int getCacheHits();

    /**
     * Check in the cache whether this query is successful
     * 
     * @param q
     *            the query
     * @return true iff this query is successful
     */
    boolean isSuccessfulByCache(Query q);

    /**
     * Check in the cache whether this query is failing
     * 
     * @param q
     *            the query
     * @return true iff this query is failing
     */
    boolean isFailingByCache(Query q);

    /**
     * Add this failing query to the cache
     * @param q the failing query
     * @param isCartesianProduct true if the query is a cartesian product
     */
    void addFailingQuery(Query q, boolean isCartesianProduct);

    /**
     * Add this successful query to the cache
     * @param q the successful query
     */
    void addSuccessfulQuery(Query q);

}