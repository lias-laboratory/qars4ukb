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
package fr.ensma.lias.qars4ukb.triplestore.jdbcdb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.ensma.lias.qars4ukb.Result;
import fr.ensma.lias.qars4ukb.exception.TripleStoreException;

/**
 * @author Stephane JEAN
 */
public class JDBCResult implements Result {

	private ResultSet rset;

	public JDBCResult(ResultSet rset) {
		this.rset = rset;
	}

	@Override
	public void close() {
		try {
			rset.close();
		} catch (SQLException e) {
			System.out.println("Unable to close the resultset: " + e.getMessage());
			e.printStackTrace();
			throw new TripleStoreException();
		}
	}

	@Override
	public boolean next() {
		try {
			return rset.next();
		} catch (SQLException e) {
			System.out.println("Unable to call the next method of the resultset: " + e.getMessage());
			e.printStackTrace();
			throw new TripleStoreException();
		}

	}

	@Override
	public String getString(int col) {
		try {
			return rset.getString(col);
		} catch (SQLException e) {
			System.out.println("Unable to call the get method of the resultset: " + e.getMessage());
			e.printStackTrace();
			throw new TripleStoreException();
		}
	}

	@Override
	public int getNbRow() {
		try {
			int res = 0;
			while (rset.next()) {
				res++;
			}
			return res;
		} catch (SQLException e) {
			System.out.println("Unable to get the number of row of the resultset: " + e.getMessage());
			e.printStackTrace();
			throw new TripleStoreException();
		}

	}

	@Override
	public List<String> getNbRow(int maxK) {
		try {
			List<String> res = new ArrayList<String>();
			while (rset.next() && res.size() <= maxK) {
				res.add(rset.getString(1));
			}
			return res;
		} catch (SQLException e) {
			System.out.println("Unable to call the next method of the resultset: " + e.getMessage());
			e.printStackTrace();
			throw new TripleStoreException();
		}
	}

	@Override
	public String[] getString(String[] cols) {
		try {
			String[] res = new String[cols.length];
			for (int i = 0; i < cols.length; i++) {
				res[i] = rset.getString(cols[i]);
			}
			return res;
		} catch (SQLException e) {
			System.out.println("Unable to call the getString method of the resultset: " + e.getMessage());
			e.printStackTrace();
			throw new TripleStoreException();
		}
	}
}
