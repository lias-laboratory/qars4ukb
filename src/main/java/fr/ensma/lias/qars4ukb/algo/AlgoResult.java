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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.ensma.lias.qars4ukb.query.AbstractQuery;
import fr.ensma.lias.qars4ukb.query.Query;

/**
 * Result of the algorithms to compute the MFSs and XSSs for different alpha.
 * 
 * @author Stéphane JEAN
 */
public class AlgoResult {

	/**
	 * Set of alphaMFSs for different alpha
	 */
	private Map<Double, Set<Query>> alphaMFSs;

	/**
	 * Set of alphaXSSs for different alpha
	 */
	private Map<Double, Set<Query>> alphaXSSs;

	/**
	 * Initialize this result
	 */
	public AlgoResult() {
		alphaMFSs = new HashMap<>();
		alphaXSSs = new HashMap<>();
	}

	/**
	 * Add a set of alphaMFSs for a given alpha
	 * 
	 * @param alpha a given threshold
	 * @param mfs   a list of MFSs
	 */
	public void addAlphaMFSs(Double alpha, Set<Query> mfs) {
		alphaMFSs.put(alpha, mfs);
	}

	/**
	 * Add a set of alphaXSSs for a given alpha
	 * 
	 * @param alpha a given threshold
	 * @param mfs   a list of XSSs
	 */
	public void addAlphaXSSs(Double alpha, Set<Query> xss) {
		alphaXSSs.put(alpha, xss);
	}

	/**
	 * Get the alphaMFSs for a given alpha
	 * 
	 * @param alpha a given threshold
	 * @return the alphaMFSs
	 */
	public Set<Query> getAlphaMFSs(Double alpha) {
		return alphaMFSs.get(alpha);
	}

	/**
	 * Get the alphaXSSs for a given alpha
	 * 
	 * @param alpha a given threshold
	 * @return the alphaXSSs
	 */
	public Set<Query> getAlphaXSSs(Double alpha) {
		return alphaXSSs.get(alpha);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alphaMFSs == null) ? 0 : alphaMFSs.hashCode());
		result = prime * result + ((alphaXSSs == null) ? 0 : alphaXSSs.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AlgoResult other = (AlgoResult) obj;
		if (alphaMFSs == null) {
			if (other.alphaMFSs != null)
				return false;
		} else if (!alphaMFSs.equals(other.alphaMFSs))
			return false;
		if (alphaXSSs == null) {
			if (other.alphaXSSs != null)
				return false;
		} else if (!alphaXSSs.equals(other.alphaXSSs))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuffer res = new StringBuffer();
		res.append("------------- Alpha-MFSs -------------\n");
		toStringAux(res, alphaMFSs);
		res.append("------------- Alpha-XSSs -------------\n");
		toStringAux(res, alphaXSSs);
		return res.toString();
	}

	private void toStringAux(StringBuffer res, Map<Double, Set<Query>> mapQueries) {
		for (Double alpha : mapQueries.keySet()) {
			res.append(alpha + ": ");
			Set<Query> queries = mapQueries.get(alpha);
			int i = 0;
			for (Query query : queries) {
				if (i > 0)
					res.append(", ");
				AbstractQuery aQuery = (AbstractQuery) query;
				res.append(aQuery.toSimpleString(aQuery.getInitialQuery()));
				i++;
			}
			res.append("\n");
		}
	}
}
