package fr.ensma.lias.qars4ukb.algo;

import java.util.ArrayList;
import java.util.List;

import fr.ensma.lias.qars4ukb.Session;
import fr.ensma.lias.qars4ukb.cache.ExtendedCacheLBA;
import fr.ensma.lias.qars4ukb.query.Query;

public class AlgoBottomUp extends AbstractAlgo {
    
    public AlgoBottomUp() {
	super();
    }

    @Override
    protected AlgoResult computesAlphaMFSsAndXSSsAux(Query q, List<Double> listOfAlpha) {
	ExtendedCacheLBA.getInstance().clearCache();
	Session session = q.getFactory().createSession();
	AlgoResult result = new AlgoResult();

	// first executes the normal version of LBA for the first alpha
	Double firstAlpha = listOfAlpha.get(0);
	q.runLBA(session, firstAlpha);
	nbExecutedQuery = session.getExecutedQueryCount();
	List<Query> discoverMFSs = q.getAllMFS();
	List<Query> discoverXSSs = q.getAllXSS();
	result.addAlphaMFSs(firstAlpha, discoverMFSs);
	result.addAlphaXSSs(firstAlpha, discoverXSSs);

	for (int i = 1; i < listOfAlpha.size(); i++) {
	    Double currentAlpha = listOfAlpha.get(i);
	    // we clear the number of executed queries by the previous run of LBA
	    session.clearExecutedQueryCount();
	    discoverMFSs = discoverMFS(discoverMFSs, currentAlpha, session);
	    discoverXSSs = discoverXSS(discoverXSSs, currentAlpha, session);
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
     * Discover a set of MFSs for a degree alpha from a set of MFSs from a greater threshold
     * @param discoveredMFS the previous set of MFSs
     * @param alpha the threshold
     * @param session the connection to the KB
     */
    protected List<Query> discoverMFS(List<Query> discoveredMFS, Double alpha, Session session) {
	List<Query> res = new ArrayList<Query>();
	List<Query> fq = new ArrayList<Query>(discoveredMFS);
	// we could start by removing the atomic MFSs, but I dont think it's worth
	while (!fq.isEmpty()) {
	    Query previousMFS = fq.remove(0);
	    if (previousMFS.size() == 1) { // this an MFS for this alpha
		res.add(previousMFS);
	    }
	    else { // we search an MFS
		Query newMFS = previousMFS.findAnMFS(session, alpha);
		res.add(newMFS);
		for (Query qPrim : fq) {
		    if (qPrim.includes(newMFS)) {
			fq.remove(qPrim);
		    }
		}
	    }
	}
	return res;
    }
    
    /**
     * Discover a set of XSSs for a degree alpha from a set of XSSs from a greater threshold
     * @param discoveredXSS the previous set of XSSs
     * @param alpha the threshold
     * @param session the connection to the KB
     */
    protected List<Query> discoverXSS(List<Query> discoveredXSS, Double alpha, Session session) {
	List<Query> res = new ArrayList<Query>();
	for (Query previousXSS : discoveredXSS) {
	    if (!previousXSS.isFailing(session, alpha)) {
		res.add(previousXSS);
	    }
	}
	return res;
    }

}
