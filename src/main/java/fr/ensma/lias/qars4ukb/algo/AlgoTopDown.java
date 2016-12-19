package fr.ensma.lias.qars4ukb.algo;

import java.util.ArrayList;
import java.util.List;

import fr.ensma.lias.qars4ukb.Session;
import fr.ensma.lias.qars4ukb.cache.ExtendedCacheLBA;
import fr.ensma.lias.qars4ukb.query.Query;

public class AlgoTopDown extends AbstractAlgo {
    
    public AlgoTopDown() {
	super();
    }

    @Override
    protected AlgoResult computesAlphaMFSsAndXSSsAux(Query q, List<Double> listOfAlpha) {
	ExtendedCacheLBA.getInstance().clearCache();
	Session session = q.getFactory().createSession();
	AlgoResult result = new AlgoResult();

	// first executes the normal version of LBA for the last alpha
	int nbAlpha = listOfAlpha.size();
	Double lastAlpha = listOfAlpha.get(nbAlpha-1);
	q.runLBA(session, lastAlpha);
	nbExecutedQuery = session.getExecutedQueryCount();
	List<Query> discoverMFSs = q.getAllMFS();
	List<Query> discoverXSSs = q.getAllXSS();
	result.addAlphaMFSs(lastAlpha, discoverMFSs);
	result.addAlphaXSSs(lastAlpha, discoverXSSs);

	for (int i = nbAlpha-2; i >= 0; i--) {
	    Double currentAlpha = listOfAlpha.get(i);
	    // we clear the number of executed queries by the previous run of LBA
	    session.clearExecutedQueryCount();
	    discoverMFSs = discoverMFS(discoverMFSs, currentAlpha, session);
	    discoverXSSs = discoverXSS(q, discoverXSSs, currentAlpha, session);
	    nbExecutedQuery += session.getExecutedQueryCount();
	    q.runLBA(session, discoverMFSs, discoverXSSs, currentAlpha);
	    nbExecutedQuery += session.getExecutedQueryCount();
	    discoverMFSs = q.getAllMFS();
	    discoverXSSs = q.getAllXSS();
	    result.addAlphaMFSs(currentAlpha, discoverMFSs);
	    result.addAlphaXSSs(currentAlpha, discoverXSSs);
	}
	nbCacheHits = ExtendedCacheLBA.getInstance().getNbCacheHits();
	return result;
    }
    
    /**
     * Discover a set of MFSs for a degree alpha from a set of MFSs from a lower threshold
     * (Algorithm DiscoverMFSXSS of the publication)
     * @param discoveredMFS the previous set of MFSs
     * @param alpha the threshold
     * @param session the connection to the KB
     */
    protected List<Query> discoverMFS(List<Query> discoveredMFS, Double alpha, Session session) {
	List<Query> res = new ArrayList<Query>();
	for (Query previousMFS : discoveredMFS) {
	    if (previousMFS.isFailing(session, alpha)) {
		res.add(previousMFS);
	    }
	}
	return res;
    }
    
    /**
     * Discover a set of XSSs for a degree alpha from a set of XSSs from a lower threshold
     * (Algorithm DiscoverMFSXSS of the publication)
     * @parm initialQuery the query on which the algorithm is executed
     * @param discoveredXSS the previous set of XSSs
     * @param alpha the threshold
     * @param session the connection to the KB
     */
    protected List<Query> discoverXSS(Query initialQuery, List<Query> discoveredXSS, Double alpha, Session session) {
	List<Query> res = new ArrayList<Query>();
	List<Query> sq = new ArrayList<Query>(discoveredXSS);
	int sizeInitialQuery = initialQuery.size();
	while (!sq.isEmpty()) {
	    Query previousXSS = sq.remove(0);
	    if (previousXSS.size() == (sizeInitialQuery - 1)) { // this an MFS for this alpha
		res.add(previousXSS);
	    }
	    else { // we search an XSS
		Query newXSS = initialQuery.findAnXSS(session, alpha, previousXSS);
		res.add(newXSS);
		for (Query qPrim : sq) {
		    if (newXSS.includes(qPrim)) {
			sq.remove(qPrim);
		    }
		}
	    }
	}
	return res;
    }

}
