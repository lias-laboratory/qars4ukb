package fr.ensma.lias.qars4ukb.algo;

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
	    discoverMFSs = discoverMFS(discoverMFSs, currentAlpha);
	    discoverXSSs = discoverXSS(discoverXSSs, currentAlpha);
	    q.runLBA(session, discoverMFSs, discoverXSSs, firstAlpha);
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
     */
    private List<Query> discoverMFS(List<Query> discoveredMFS, Double alpha) {
	return null;
    }
    
    /**
     * Discover a set of XSSs for a degree alpha from a set of XSSs from a lower threshold
     * (Algorithm DiscoverMFSXSS of the publication)
     */
    private List<Query> discoverXSS(List<Query> discoveredXSS, Double alpha) {
	return null;
    }

}
