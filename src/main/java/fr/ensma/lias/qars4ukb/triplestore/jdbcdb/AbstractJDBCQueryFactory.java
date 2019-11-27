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

import java.sql.Connection;
import java.sql.DriverManager;

import fr.ensma.lias.qars4ukb.QARS4UKBConstants;
import fr.ensma.lias.qars4ukb.Session;
import fr.ensma.lias.qars4ukb.exception.NotYetImplementedException;
import fr.ensma.lias.qars4ukb.exception.TripleStoreException;
import fr.ensma.lias.qars4ukb.query.AbstractQueryFactory;
import fr.ensma.lias.qars4ukb.query.Query;
import fr.ensma.lias.qars4ukb.query.QueryHelper;
import fr.ensma.lias.qars4ukb.triplestore.jdbcdb.database.hsqldb.HSQLDBQueryHelper;
import fr.ensma.lias.qars4ukb.triplestore.jdbcdb.database.oracle.OracleQueryHelper;

/**
 * @author Stephane JEAN
 */
public abstract class AbstractJDBCQueryFactory extends AbstractQueryFactory {

	protected QueryHelper createQueryHelper(Query q) {
		String jdbcDatabase = this.getConfig().jdbcDatabase();
		switch (jdbcDatabase) {
		case QARS4UKBConstants.JDBC_DB_HSQLDB:
			return new HSQLDBQueryHelper(q);
		case QARS4UKBConstants.JDBC_DB_ORACLE:
			return new OracleQueryHelper(q);
		default: {
			throw new NotYetImplementedException();
		}
		}
	}

	@Override
	public Session createSession() {
		try {
			Class.forName(this.getConfig().jdbcDriver());
			Connection cnxJDBC = DriverManager.getConnection(this.getConfig().jdbcUrl(), this.getConfig().jdbcLogin(),
					this.getConfig().jdbcPassword());
			return new JDBCSession(cnxJDBC);
		} catch (Exception e) {
			System.out.println("Unable to create session");
			e.printStackTrace();
			throw new TripleStoreException(e.getMessage());
		}
	}
}
