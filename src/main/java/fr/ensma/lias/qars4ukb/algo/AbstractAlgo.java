package fr.ensma.lias.qars4ukb.algo;

import java.util.List;

import fr.ensma.lias.qars4ukb.query.Query;

public abstract class AbstractAlgo implements IAlgo {

    /**
     * Number of executed query on the KB
     */
    protected int nbExecutedQuery;
    
    /**
     * Number of cache hits
     */
    protected int nbCacheHits;
    
    /**
     * Computing time of the algorithm
     */
    protected float computingTime;
    
    public AbstractAlgo() {
	nbExecutedQuery = 0;
	nbCacheHits = 0;
    }
    
    
    @Override
    public int getNbExecutedQuery() {
	return nbExecutedQuery;
    }

    @Override
    public int getNbCacheHits() {
	return nbCacheHits;
    }

    @Override
    public float getComputingTime() {
	return computingTime;
    }
    
    @Override
    public AlgoResult computesAlphaMFSsAndXSSs(Query q, List<Double> listOfAlpha) {
	long begin = System.currentTimeMillis();
	AlgoResult result = computesAlphaMFSsAndXSSsAux(q, listOfAlpha);
	long end = System.currentTimeMillis();
	computingTime = ((float) (end - begin)) / 1000f;
	return result;
    }
    
    /**
     * Same as computesAlphaMFSsAndXSSs but without the computing time
     * @return the AlphaMFSs and XSSs 
     */
    protected abstract AlgoResult computesAlphaMFSsAndXSSsAux(Query q, List<Double> listOfAlpha);
}
