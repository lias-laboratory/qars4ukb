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
import java.sql.Statement;
import java.util.List;

import fr.ensma.lias.qars4ukb.AbstractSession;
import fr.ensma.lias.qars4ukb.Result;
import fr.ensma.lias.qars4ukb.Session;
import fr.ensma.lias.qars4ukb.exception.TripleStoreException;
import fr.ensma.lias.qars4ukb.query.Query;
import fr.ensma.lias.qars4ukb.query.QueryHelper;
import fr.ensma.lias.qars4ukb.query.TriplePattern;

/**
 * @author Stephane JEAN
 */
public abstract class AbstractJDBCQueryHelper implements QueryHelper {

	protected Query q;

	public AbstractJDBCQueryHelper(Query q) {
		this.q = q;
	}

	@Override
	public boolean executeQuery(Session session, Double alpha) {
		Statement stmt;
		try {
			stmt = ((JDBCSession) session).getConnection().createStatement();
			ResultSet rset = stmt.executeQuery(q.toNativeQuery(alpha));
			((AbstractSession) session).setExecutedQueryCount(((AbstractSession) session).getExecutedQueryCount() + 1);
			boolean isEmpty = !rset.next();
			rset.close();
			stmt.close();
			return isEmpty;
		} catch (SQLException e) {
			System.out.println("Unable to execute the query: " + e.getMessage());
			e.printStackTrace();
			throw new TripleStoreException();
		}
	}

	@Override
	public Result getResult(Session s, Double alpha) {
		try {
			Statement reqOracle = ((JDBCSession) s).getConnection().createStatement();
			ResultSet rset = reqOracle.executeQuery(toNativeQuery(alpha));
			return new JDBCResult(rset);
		} catch (SQLException e) {
			System.out.println("Unable to execute the query: " + e.getMessage());
			e.printStackTrace();
			throw new TripleStoreException();
		}
	}
	
	@Override
	public String toNativeQuery(Double alpha) {
		String res = "select * from ";
		List<TriplePattern> triplePatterns = q.getTriplePatterns();
		for (int i = 0; i < triplePatterns.size(); i++) {
		    if (i > 0)
			res += " NATURAL JOIN ";
		    res += "(" + triplePatterns.get(i).toSQL(alpha) + ") " + "t" + i;
		}
		return res;
	}
}
