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
package fr.ensma.lias.qars4ukb;

import java.io.InputStream;
import java.util.Arrays;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.tdb.TDB;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.TDBLoader;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.tdb.sys.TDBInternal;
import org.apache.jena.util.FileManager;

import fr.ensma.lias.qars4ukb.algo.AlgoNLBA;
import fr.ensma.lias.qars4ukb.algo.AlgoResult;
import fr.ensma.lias.qars4ukb.algo.IAlgo;
import fr.ensma.lias.qars4ukb.query.Query;
import fr.ensma.lias.qars4ukb.query.QueryFactory;
import fr.ensma.lias.qars4ukb.triplestore.jenatdbgraph.JenaTDBGraphQueryOptFactory;

/**
 * @author Mickael BARON
 */
public class QaRS4UKBSample {
	public static void main(String[] args) {
		// Initialize a JenaTDB instance by using the memory version.
		Location locationMemory = Location.mem("watdivtdbmemory");
		DatasetGraph currentGraph = TDBFactory.createDatasetGraph(locationMemory);
		Dataset dataset = TDBFactory.createDataset(locationMemory);
		dataset.begin(ReadWrite.READ);
		TDB.getContext().setTrue(TDB.symUnionDefaultGraph);

		// Load the N-Quads from the previous generated file.
		FileManager fm = FileManager.get();
		InputStream in = fm.open("/Users/baronm/workspacejava/qars4ukb/src/main/resources/test_jena_quad.nq");
		TDBLoader.load(TDBInternal.getBaseDatasetGraphTDB(currentGraph), in, true);

		// Define the SPARQL query.
		QueryFactory factory = new JenaTDBGraphQueryOptFactory();
		Query createQuery = factory.createQuery(
				"SELECT * WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course35' . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone> ?t . ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#fax> ?f }");

		// Choose an algorithm.
		Session currentSession = ((JenaTDBGraphQueryOptFactory) factory).createSession(dataset);
		IAlgo algo = new AlgoNLBA(currentSession);
		// IAlgo algo = new AlgoBottomUp(((JenaTDBGraphQueryOptFactory)
		// factory).createSession(dataset));
		// IAlgo algo = new AlgoHybrid(((JenaTDBGraphQueryOptFactory)
		// factory).createSession(dataset));
		// IAlgo algo = new AlgoTopDown(((JenaTDBGraphQueryOptFactory)
		// factory).createSession(dataset));

		// Execute the algorithm.
		AlgoResult computesAlphaMFSsAndXSSs = algo.computesAlphaMFSsAndXSSs(createQuery,
				Arrays.asList(0.2, 0.4, 0.6, 0.8));

		// Statistics on the executed algorithm.
		System.out.println("Time = " + algo.getComputingTime() + ", NbQueriesExecuted: " + algo.getNbExecutedQuery()
				+ ", NbCacheHits: " + algo.getNbCacheHits());

		// Display results on 0.2 MFS and 0.2 XSS.
		System.out.println("NbMFS for 0.2:" + computesAlphaMFSsAndXSSs.getAlphaMFSs(0.2).size());
		System.out.println(computesAlphaMFSsAndXSSs.getAlphaXSSs(0.2).size());
	}
}
