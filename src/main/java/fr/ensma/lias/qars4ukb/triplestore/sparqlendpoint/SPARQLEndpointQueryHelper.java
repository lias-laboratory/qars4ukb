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
	prefixes = new HashMap<String, String>();
	prefixes.put("http://www.w3.org/1999/02/22-rdf-syntax-ns", "rdf");
	prefixes.put("http://swat.cse.lehigh.edu/onto/univ-bench.owl", "lubm");

	StringBuffer newQuery = new StringBuffer(this.q.toString());
	for (String current : prefixes.keySet()) {
	    Pattern pattern = Pattern.compile("<" + current + "#");
	    Matcher matcher = pattern.matcher(newQuery.toString());
	    newQuery = new StringBuffer(matcher.replaceAll(prefixes.get(current) + ":"));

	    final String[] split = newQuery.toString().split(prefixes.get(current) + ":");
	    Pattern secondPattern = Pattern.compile("([^>]*)>(.*)");
	    StringJoiner js = new StringJoiner(prefixes.get(current) + ":");
	    js.add(split[0]);
	    for (int i = 1; i < split.length; i++) {
		final Matcher secondMatcher = secondPattern.matcher(split[i]);
		if (secondMatcher.matches()) {
		    js.add(secondMatcher.replaceAll("$1$2"));
		}
	    }
	    newQuery = new StringBuffer(js.toString());
	}

	StringBuffer prefix = new StringBuffer();
	for (String current : prefixes.keySet()) {
	    prefix.append("PREFIX " + prefixes.get(current) + ":<" + current + "#> ");
	}

	prefix.append(" " + newQuery.toString() + " LIMIT 350");

	return prefix.toString();
    }
}
