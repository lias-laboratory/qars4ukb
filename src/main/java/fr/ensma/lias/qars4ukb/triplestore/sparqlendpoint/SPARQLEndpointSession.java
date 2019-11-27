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

import fr.ensma.lias.qars4ukb.AbstractSession;
import fr.ensma.lias.qars4ukb.triplestore.sparqlendpoint.util.SPARQLEndpointClient;

/**
 * @author Mickael BARON
 */
public class SPARQLEndpointSession extends AbstractSession {

	private SPARQLEndpointClient currentSession;

	public SPARQLEndpointSession(SPARQLEndpointClient pCurrentSession) {
		this.currentSession = pCurrentSession;
	}

	public SPARQLEndpointClient getSPARQLEndpointClient() {
		return currentSession;
	}

	@Override
	public void close() throws Exception {
	}
}
