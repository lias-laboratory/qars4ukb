package fr.ensma.lias.qars4ukb.cache;

import java.util.Map;

import fr.ensma.lias.qars4ukb.exception.NotYetImplementedException;
import fr.ensma.lias.qars4ukb.query.Query;

/**
 * Extended management of the cache for executing
 * top-down, bottom-up and hybrid approaches
 * @author Stéphane JEAN
 */

public class ExtendedCacheLBA implements ICache {
    
    /**
     * Singleton implementation
     */
    private static ExtendedCacheLBA instance;

    private ExtendedCacheLBA() {
    }

    public static ExtendedCacheLBA getInstance() {
	if (instance == null) {
	    instance = new ExtendedCacheLBA();
	}
	return instance;
    }
    
    /**
     * Successful queries in the cache
     * with the maximum degree to which it is successful
     */
    private Map<Query, Double> successfulCachedQueries;
    
    /**
     * Failing queries in the cache
     * with the minimum degree to which it is failing
     */
    private Map<Query, Double> failingCachedQueries;
    
    /**
     * Number of cache hits
     */
    private int nbCacheHits;
    
    @Override
    public void initCache() {
	// we do nothing as the extended cache should not be
	// initialized after each execution of LBA
    }

    @Override
    public int getNbCacheHits() {
	return nbCacheHits;
    }

    @Override
    public boolean isSuccessfulByCache(Query q, Double alpha) {
	throw new NotYetImplementedException();
    }

    @Override
    public boolean isFailingByCache(Query q, Double alpha) {
	throw new NotYetImplementedException();
    }

    @Override
    public void addFailingQuery(Query q, boolean isCartesianProduct, Double alpha) {
	throw new NotYetImplementedException();
    }

    @Override
    public void addSuccessfulQuery(Query q, Double alpha) {
	throw new NotYetImplementedException();
    }

}
