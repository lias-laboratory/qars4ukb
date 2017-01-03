
package fr.ensma.lias.qars4ukb.experiment;

/**
 * @author Stephane JEAN and Ibrahim DELLAL
 */
public class QueryResult {

    private float time;

    private int nbExecutedQuery;
    
    private int nbCacheHits;

    public QueryResult(float time, int nbExecutedQuery, int nbCacheHits) {
	super();
	this.time = time;
	this.nbExecutedQuery = nbExecutedQuery;
	this.nbCacheHits = nbCacheHits;
    }

    public float getTime() {
	return time;
    }

    public int getNbExecutedQuery() {
        return nbExecutedQuery;
    }

    public int getNbCacheHits() {
        return nbCacheHits;
    }




}
