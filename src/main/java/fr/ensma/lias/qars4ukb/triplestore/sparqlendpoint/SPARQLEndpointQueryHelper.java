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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.ensma.lias.qars4ukb.AbstractSession;
import fr.ensma.lias.qars4ukb.Result;
import fr.ensma.lias.qars4ukb.Session;
import fr.ensma.lias.qars4ukb.exception.TripleStoreException;
import fr.ensma.lias.qars4ukb.query.Query;
import fr.ensma.lias.qars4ukb.query.SPARQLQueryHelper;

/**
 * @author Mickael BARON
 */
public class SPARQLEndpointQueryHelper extends SPARQLQueryHelper {

    private Map<String, String> prefixes;

    public SPARQLEndpointQueryHelper(Query q) {
	super(q);
    }

    @Override
    public boolean executeQuery(Session session, Double alpha) {
	final Result result = this.getResult(session, alpha);
	((AbstractSession) session).setExecutedQueryCount(((AbstractSession) session).getExecutedQueryCount() + 1);
	return result.getNbRow() == 0;
    }

    @Override
    public Result getResult(Session session, Double alpha) {
	SPARQLEndpointSession currentJenaSession = (SPARQLEndpointSession) session;
	String query;
	try {
	    query = currentJenaSession.getSPARQLEndpointClient().query(this.toNativeQuery(alpha));
	    return new SPARQLEndpointResult(query);
	} catch (IOException e) {
	    System.out.println("Unable to get the result of the query: " + e.getMessage());
	    e.printStackTrace();
	    throw new TripleStoreException();
	}
    }

    @Override
    public String toNativeQuery(Double alpha) {

	String s = q.toString();
	String res = null;
	if (s.contains("WHERE"))
	    res = s.replace("WHERE", "{ GRAPH ?g");
	else
	    res = s.replace("where", "{ GRAPH ?g");

	res = res.replace("}", ".} FILTER(xsd:double(str(?g)) > " +alpha+") }");
	res = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " + res;
	return res;

    }
}
