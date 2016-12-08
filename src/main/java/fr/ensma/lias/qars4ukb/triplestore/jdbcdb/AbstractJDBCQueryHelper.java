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
import java.sql.Statement;

import fr.ensma.lias.qars4ukb.AbstractSession;
import fr.ensma.lias.qars4ukb.Result;
import fr.ensma.lias.qars4ukb.Session;
import fr.ensma.lias.qars4ukb.query.Query;
import fr.ensma.lias.qars4ukb.query.QueryHelper;

/**
 * @author Stephane JEAN
 */
public abstract class AbstractJDBCQueryHelper implements QueryHelper {

    protected Query q;

    public AbstractJDBCQueryHelper(Query q) {
	this.q = q;
    }

    @Override
    public boolean executeQuery(Session session) throws Exception {
	Statement stmt = ((JDBCSession) session).getConnection()
		.createStatement();
	ResultSet rset = stmt.executeQuery(q.toNativeQuery());
	((AbstractSession) session).setExecutedQueryCount(
		((AbstractSession) session).getExecutedQueryCount() + 1);
	boolean isEmpty = !rset.next();
	rset.close();
	stmt.close();
	return isEmpty;
    }

    @Override
    public Result getResult(Session s) throws Exception {
	Statement reqOracle = ((JDBCSession) s).getConnection()
		.createStatement();
	ResultSet rset = reqOracle.executeQuery(toNativeQuery());
	return new JDBCResult(rset);
    }
}
