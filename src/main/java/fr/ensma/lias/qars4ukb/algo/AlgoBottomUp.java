/*********************************************************************************
* This file is part of QARS4UKB Project.
* Copyright (C) 2017 LIAS - ENSMA
*   Teleport 2 - 1 avenue Clement Ader
*   BP 40109 - 86961 Futuroscope Chasseneuil Cedex - FRANCE
* 
* QARS4UKB is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* QARS4UKB is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with QARS4UKB.  If not, see <http://www.gnu.org/licenses/>.
**********************************************************************************/
package fr.ensma.lias.qars4ukb.algo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import fr.ensma.lias.qars4ukb.Session;
import fr.ensma.lias.qars4ukb.cache.ExtendedCacheLBA;
import fr.ensma.lias.qars4ukb.query.Query;

/**
 * @author Stéphane JEAN
 */
public class AlgoBottomUp extends AbstractAlgo {

	public AlgoBottomUp(Session pSession) {
		super(pSession);
	}

	@Override
	protected AlgoResult computesAlphaMFSsAndXSSsAux(Query q, List<Double> listOfAlpha) {
		ExtendedCacheLBA.getInstance().clearCache();
		AlgoResult result = new AlgoResult();

		// first executes the normal version of LBA for the first alpha
		Double firstAlpha = listOfAlpha.get(0);
		q.runLBA(this.getSession(), firstAlpha);
		nbExecutedQuery = this.getSession().getExecutedQueryCount();
		Set<Query> discoverMFSs = q.getAllMFS();
		Set<Query> discoverXSSs = q.getAllXSS();
		result.addAlphaMFSs(firstAlpha, discoverMFSs);
		result.addAlphaXSSs(firstAlpha, discoverXSSs);

		for (int i = 1; i < listOfAlpha.size(); i++) {
			Double currentAlpha = listOfAlpha.get(i);
			// we clear the number of executed queries by the previous run of
			// LBA
			this.getSession().clearExecutedQueryCount();
			discoverMFSs = discoverMFS(discoverMFSs, currentAlpha, this.getSession());
			discoverXSSs = discoverXSS(discoverXSSs, currentAlpha, this.getSession());
			nbExecutedQuery += this.getSession().getExecutedQueryCount();
			q.runLBA(this.getSession(), discoverMFSs, discoverXSSs, currentAlpha);
			nbExecutedQuery += this.getSession().getExecutedQueryCount();
			discoverMFSs = q.getAllMFS();
			discoverXSSs = q.getAllXSS();
			result.addAlphaMFSs(currentAlpha, discoverMFSs);
			result.addAlphaXSSs(currentAlpha, discoverXSSs);
		}
		nbCacheHits = ExtendedCacheLBA.getInstance().getNbCacheHits();
		return result;
	}

	/**
	 * Discover a set of MFSs for a degree alpha from a set of MFSs from a greater
	 * threshold
	 * 
	 * @param discoveredMFS the previous set of MFSs
	 * @param alpha         the threshold
	 * @param session       the connection to the KB
	 */
	protected Set<Query> discoverMFS(Set<Query> discoveredMFS, Double alpha, Session session) {
		Set<Query> res = new HashSet<Query>();
		Set<Query> fq = new HashSet<Query>(discoveredMFS);
		// we could start by removing the atomic MFSs, but I don't think it's
		// worth
		Iterator<Query> iter = fq.iterator();
		Set<Query> toRemove = new HashSet<Query>();
		while (iter.hasNext()) {
			Query previousMFS = iter.next();
			iter.remove();
			if (previousMFS.size() == 1) { // this an MFS for this alpha
				res.add(previousMFS);
			} else { // we search an MFS
				if (!toRemove.contains(previousMFS)) {
					Query newMFS = previousMFS.findAnMFS(session, alpha);
					res.add(newMFS);
					for (Query qPrim : fq) {
						if (qPrim.includes(newMFS)) {
							toRemove.add(qPrim);
						}
					}
				}
			}
		}
		return res;
	}

	/**
	 * Discover a set of XSSs for a degree alpha from a set of XSSs from a greater
	 * threshold
	 * 
	 * @param discoveredXSS the previous set of XSSs
	 * @param alpha         the threshold
	 * @param session       the connection to the KB
	 */
	protected Set<Query> discoverXSS(Set<Query> discoveredXSS, Double alpha, Session session) {
		Set<Query> res = new HashSet<Query>();
		for (Query previousXSS : discoveredXSS) {
			if (!previousXSS.isFailing(session, alpha)) {
				res.add(previousXSS);
			}
		}
		return res;
	}
}
