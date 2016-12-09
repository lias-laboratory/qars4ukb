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
import fr.ensma.lias.qars4ukb.query.AbstractQuery;
import fr.ensma.lias.qars4ukb.query.QueryHelper;
import fr.ensma.lias.qars4ukb.query.TriplePattern;

/**
 * @author Stephane JEAN
 */
public class JDBCQuery extends AbstractQuery {

	private QueryHelper helper;

	public JDBCQuery(JDBCQueryFactory factory, String query) {
		super(factory, query);
		helper = factory.createQueryHelper(this);
	}

	public JDBCQuery(JDBCQueryFactory factory, List<TriplePattern> tps) {
		super(factory, tps);
		helper = factory.createQueryHelper(this);
	}

	@Override
	public boolean isFailingAux(Session session) {
		try {
			Statement stmt = ((JDBCSession) session).getConnection().createStatement();
			ResultSet rset = stmt.executeQuery(toNativeQuery());
			((AbstractSession) session).setExecutedQueryCount(((AbstractSession) session).getExecutedQueryCount() + 1);
			boolean res = !rset.next();
			rset.close();
			stmt.close();
			return res;
		} catch (SQLException e) {
			System.out.println("Unable to execute the query: " + e.getMessage());
			e.printStackTrace();
			throw new TripleStoreException();
		}
	}

	@Override
	public String toNativeQuery() {
		return helper.toNativeQuery();
	}

	@Override
	public Result getResult(Session session) {
		return helper.getResult(session);
	}
}
