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
package fr.ensma.lias.qars4ukb.triplestore.jenatdbgraph;

import java.util.function.Predicate;

import org.apache.jena.atlas.lib.Tuple;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.tdb.store.NodeId;
import org.apache.jena.tdb.sys.SystemTDB;
import org.apache.jena.tdb.sys.TDBInternal;

import fr.ensma.lias.qars4ukb.AbstractSession;
import fr.ensma.lias.qars4ukb.Result;
import fr.ensma.lias.qars4ukb.Session;
import fr.ensma.lias.qars4ukb.query.Query;
import fr.ensma.lias.qars4ukb.query.SPARQLQueryHelper;

/**
 * @author Stephane JEAN
 * @author Ibrahim DELLAL
 */
public class JenaTDBGraphQueryHelper extends SPARQLQueryHelper {

    public JenaTDBGraphQueryHelper(Query q) {
	super(q);
    }

    @Override
    public boolean executeQuery(Session session, Double alpha) {
	String sparqlQueryString = toNativeQuery(alpha);
	org.apache.jena.query.Query query = org.apache.jena.query.QueryFactory.create(sparqlQueryString);
	QueryExecution qexec = QueryExecutionFactory.create(query, ((JenaTDBGraphSession) session).getDataset());
	// add the filter function to the Query Execution
	//qexec.getContext().set(SystemTDB.symTupleFilter,
		//createFilter(((JenaTDBGraphSession) session).getDataset(), alpha));
	ResultSet results = qexec.execSelect();

	((AbstractSession) session).setExecutedQueryCount(((AbstractSession) session).getExecutedQueryCount() + 1);
	boolean res = !results.hasNext();
	qexec.close();
	return res;
    }

    /**
     * this function return a filter function witch accept or reject a triple
     * depending on its trust value
     * 
     * @param ds
     *            data set containing quad
     * @param alpha
     *            minimum trust value of each result
     * @return A filter function depending on alpha
     */
    private static Predicate<Tuple<NodeId>> createFilter(final Dataset ds, Double alpha) {
	// Filter for accept/reject as quad as being visible.
	// Return true for "accept", false for "reject"
	Predicate<Tuple<NodeId>> filter = new Predicate<Tuple<NodeId>>() {
	    public boolean test(Tuple<NodeId> item) {
		NodeId QuadTrustValueNodeID = item.get(0);
		Double QuadTrustValue = Double.parseDouble(TDBInternal.getNode(ds, QuadTrustValueNodeID).getURI());
		if (item.asList().size() == 4 && QuadTrustValue >= alpha) {
		    return true;
		}
		return false;
	    }
	};

	return filter;
    }
    @Override 
    public String toNativeQuery(Double alpha) { 
   	 int i = 1; String s = q.toString();
   	 
   	 String res = null;
   	 if (s.contains("WHERE")) 
   	 	res = s.replace("WHERE", "{ GRAPH ?g" + i); 
   	 else
   		res = s.replace("where", "{ GRAPH ?g" + i); 
   	 	
   	 i++;
   	// System.out.println("res " + res); 
   	 res = res.replace(" . ", "unpoint");
   	 String resultat = ""; 
   	 String[] tokens = res.split("unpoint"); 
   	 for (int k = 0; k < tokens.length - 1; k++)// String t : tokens) 
   	{
   		 //System.out.println("  " + i + tokens[k]);
   		 resultat += tokens[k] + " } . GRAPH ?g" + i + " { "; i++; 
   	}
   	 
   	resultat+=tokens[tokens.length-1];
   	for(int j = 1;j<i-1;j++)
   	{
   		resultat += " FILTER(xsd:double(str(?g" + j + ")) > " + alpha + ") .";

   	}
   	resultat+=" FILTER(xsd:double(str(?g"+(i-1)+")) > "+alpha+") .";
   	resultat="PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "+resultat+" }";
   	//System.out.println(resultat);
   	return resultat;

   	}
    @Override
    public Result getResult(Session s, Double alpha) {
	QueryExecution qexec = QueryExecutionFactory.create(toNativeQuery(alpha),
		((JenaTDBGraphSession) s).getDataset());
	// add the filter function to the Query Execution
	qexec.getContext().set(SystemTDB.symTupleFilter, createFilter(((JenaTDBGraphSession) s).getDataset(), alpha));
	ResultSet results = qexec.execSelect();
	return new JenaTDBGraphResult(results);
    }

}
