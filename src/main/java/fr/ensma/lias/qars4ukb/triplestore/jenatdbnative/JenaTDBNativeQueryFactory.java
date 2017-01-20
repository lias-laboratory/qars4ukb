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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.jena.query.Dataset;
import org.apache.jena.tdb.TDBFactory;

import fr.ensma.lias.qars4ukb.Session;
import fr.ensma.lias.qars4ukb.exception.TripleStoreException;
import fr.ensma.lias.qars4ukb.query.AbstractQueryFactory;
import fr.ensma.lias.qars4ukb.query.Query;
import fr.ensma.lias.qars4ukb.query.TriplePattern;

/**
 * @author Stephane JEAN
 */
public class JenaTDBNativeQueryFactory extends AbstractQueryFactory {

    @Override
    public Query createQuery(String rdfQuery) {
	return new JenaTDBNativeQuery(this, rdfQuery);
    }

    @Override
    public Query createQuery(List<TriplePattern> tps) {
	return new JenaTDBNativeQuery(this, tps);
    }

    @Override
    public Session createSession() {
	final String jenatdbRepository = this.getConfig().jenanatifRepository();

	if (Files.isDirectory(Paths.get(jenatdbRepository))) {

	    throw new TripleStoreException("File:" + jenatdbRepository + " is not found.");
	}

	Dataset dataset = TDBFactory.createDataset(jenatdbRepository);
	return new JenaTDBNativeSession(dataset);
    }
}
