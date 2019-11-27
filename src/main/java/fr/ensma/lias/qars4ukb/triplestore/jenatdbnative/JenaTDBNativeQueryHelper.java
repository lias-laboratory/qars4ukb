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
package fr.ensma.lias.qars4ukb.triplestore.jenatdbnative;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;

import fr.ensma.lias.qars4ukb.AbstractSession;
import fr.ensma.lias.qars4ukb.Result;
import fr.ensma.lias.qars4ukb.Session;
import fr.ensma.lias.qars4ukb.query.Query;
import fr.ensma.lias.qars4ukb.query.SPARQLQueryHelper;

/**
 * @author Stephane JEAN
 */
public class JenaTDBNativeQueryHelper extends SPARQLQueryHelper {

	public JenaTDBNativeQueryHelper(Query q) {
		super(q);
	}

	@Override
	public boolean executeQuery(Session session, Double alpha) {
		String sparqlQueryString = toNativeQuery(alpha);
		org.apache.jena.query.Query query = org.apache.jena.query.QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, ((JenaTDBNativeSession) session).getDataset());
		ResultSet results = qexec.execSelect();

		((AbstractSession) session).setExecutedQueryCount(((AbstractSession) session).getExecutedQueryCount() + 1);
		boolean res = !results.hasNext();
		qexec.close();
		return res;
	}

	@Override
	public Result getResult(Session s, Double alpha) {
		QueryExecution qexec = QueryExecutionFactory.create(toNativeQuery(alpha),
				((JenaTDBNativeSession) s).getDataset());
		ResultSet results = qexec.execSelect();
		return new JenaTDBNativeResult(results);
	}
}
