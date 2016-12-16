package fr.ensma.lias.qars4ukb.algo;

import java.util.List;

import fr.ensma.lias.qars4ukb.Session;
import fr.ensma.lias.qars4ukb.cache.CacheLBA;
import fr.ensma.lias.qars4ukb.query.Query;

public class AlgoNLBA extends AbstractAlgo {
    
    @Override
    public AlgoResult computesAlphaMFSsAndXSSsAux(Query q, List<Double> listOfAlpha) {
	Session session = q.getFactory().createSession();
	AlgoResult result = new AlgoResult();
	for (Double alpha : listOfAlpha) {
	    q.runLBA(session, alpha);
	    result.addAlphaMFSs(alpha, q.getAllMFS());
	    result.addAlphaXSSs(alpha, q.getAllXSS());
	    // this query must used the CacheLBA (and not the extended cache)
	    nbCacheHits += CacheLBA.getInstance().getNbCacheHits();
	}
	nbExecutedQuery = session.getExecutedQueryCount();
	return result;
    }


}
