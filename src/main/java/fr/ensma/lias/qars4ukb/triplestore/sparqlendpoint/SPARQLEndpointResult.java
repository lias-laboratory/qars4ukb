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
package fr.ensma.lias.qars4ukb.triplestore.sparqlendpoint;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import fr.ensma.lias.qars4ukb.Result;
import fr.ensma.lias.qars4ukb.exception.NotYetImplementedException;
import fr.ensma.lias.qars4ukb.exception.TripleStoreException;

/**
 * @author Mickael BARON
 */
public class SPARQLEndpointResult implements Result {

	private String rawResult;

	private List<String> rows;

	private short cursor = -1;

	public SPARQLEndpointResult(String result) {
		this.rawResult = result;
		String[] split = rawResult.split("\n");
		if (split.length > 0) {
			split = ArrayUtils.remove(split, 0);
		}

		rows = Arrays.asList(split);
	}

	@Override
	public void close() {
	}

	@Override
	public boolean next() {
		if (cursor + 1 != rows.size()) {
			cursor++;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getString(int col) {
		if (cursor == -1) {
			throw new TripleStoreException();
		}
		final String[] split = rows.get(cursor).split("\\s+");
		if (split != null && ((col - 1) >= 0 && (col - 1) < split.length)) {
			return split[col - 1].replaceAll("\"", "");
		} else {
			System.out.println("The columnIndex is not valid.");
			throw new TripleStoreException();
		}
	}

	@Override
	public int getNbRow() {
		return rows.size();
	}

	@Override
	public String[] getString(String[] cols) {
		// TODO: ask to Stef how this method is working.
		// String[] resultArray = new String[cols.length];
		//
		// for (int i = 0 ; i < cols.length; i++) {
		// resultArray[i] = getString(cols[i]);
		// }

		throw new NotYetImplementedException();
	}

	@Override
	public List<String> getNbRow(int maxK) {
		if (rows.size() > maxK) {
			return rows.subList(0, maxK);
		}
		return rows;
	}
}
