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
package fr.ensma.lias.qars4ukb.experiment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.ensma.lias.qars4ukb.query.Query;

/**
 * A class to get the results of the experiments on the NLBA, BOTTOM-UP,
 * TOP-DOWN and HYBRID algorithms
 * 
 * @author Stephane JEAN
 * @author Ibrahim DELLAL
 */
public class ExpRelaxResult {

	private Logger logger = Logger.getLogger(ExpRelaxResult.class);

	/**
	 * Constants for the metrics
	 */
	private static int ID_TIME = 1;

	private static int ID_NB_EXECUTED_QUERY = 2;

	private static int ID_NB_CACHE_HITS = 3;

	/**
	 * Number of execution of each algorithm
	 */
	protected int nbExecutions;

	protected List<Query> listOfQueries;

	protected Map<Query, QueryResult[]> resultsByQuery;

	public ExpRelaxResult(int nbExecutionQuery) {
		super();
		logger.setLevel(Level.DEBUG);
		this.nbExecutions = nbExecutionQuery;
		listOfQueries = new ArrayList<Query>();
		this.resultsByQuery = new HashMap<Query, QueryResult[]>();
	}

	public void addQueryResult(int i, Query q, float time, int nbExecutedQuery, int nbCacheHits) {
		QueryResult[] queryResults = resultsByQuery.get(q);
		if (queryResults == null) {
			queryResults = new QueryResult[nbExecutions];
			listOfQueries.add(q);
		}
		queryResults[i] = new QueryResult(time, nbExecutedQuery, nbCacheHits);
		resultsByQuery.put(q, queryResults);
	}

	/***********************************************
	 * Methods to compute the average of the metrics
	 ***********************************************/

	/**
	 * Get the average of the computing time
	 * 
	 * @param q the query
	 * @return the average of the computing time
	 */
	public float getAvgTime(Query q) {
		return getAvgMetric(q, ID_TIME);
	}

	/**
	 * Get the average of the number of executed query
	 * 
	 * @param q the query
	 * @return the average of the number of executed query
	 */
	public float getAvgNbExecutedQuery(Query q) {
		return getAvgMetric(q, ID_NB_EXECUTED_QUERY);
	}

	/**
	 * Get the average of the number of cache hits
	 * 
	 * @param q the query
	 * @return the average of the number of cache hits
	 */
	public float getAvgCacheHits(Query q) {
		return getAvgMetric(q, ID_NB_CACHE_HITS);
	}

	/**
	 * Computes the average of a given metrics
	 * 
	 * @param q        the query
	 * @param idMetric the given metric
	 * @return the average of the given metrics
	 */
	public float getAvgMetric(Query q, int idMetric) {
		float res = 0;
		QueryResult[] results = resultsByQuery.get(q);
		if (results != null) {
			for (int j = 0; j < results.length; j++) {
				if (idMetric == ID_TIME)
					res += results[j].getTime();
				else if (idMetric == ID_NB_EXECUTED_QUERY)
					res += results[j].getNbExecutedQuery();
				else if (idMetric == ID_NB_CACHE_HITS)
					res += results[j].getNbCacheHits();
			}
		}
		return res / nbExecutions;
	}

	/**
	 * Round a float to certain number of decimals
	 */
	public static float round(float d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(Float.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}

	/***************************************************
	 * Methods to display the results of the experiments
	 ***************************************************/

	@Override
	public String toString() {
		StringBuffer res = new StringBuffer("");
		for (int i = 0; i < listOfQueries.size(); i++) {
			Query q = listOfQueries.get(i);
			res.append("Q" + (i + 1) + "\t");
			Float valTime = round(getAvgTime(q), 2);
			res.append(valTime.toString().replace('.', ',') + "\t");
			int nbExecutedQuery = Math.round(getAvgNbExecutedQuery(q));
			res.append(nbExecutedQuery + "\t");
			int nbCacheHits = Math.round(getAvgCacheHits(q));
			res.append(nbCacheHits + "\n");
		}
		return res.toString();
	}

	/**
	 * Create a file with the results of the experiments
	 * 
	 * @param descriExp the name of the file
	 */
	public void toFile(String descriExp) {
		BufferedWriter fichier;
		try {
			fichier = new BufferedWriter(new FileWriter(descriExp));
			fichier.write(toString());
			fichier.close();
		} catch (IOException e) {
			System.out.println("Unable to create the file with the experiment results.");
			e.printStackTrace();
		}
	}

}
