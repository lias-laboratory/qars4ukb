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
package fr.ensma.lias.qars4ukb.triplestore.sparqlendpoint.util;

/**
 * @author Mickael BARON
 */
public enum OutputFormat {
    HTML("text/html"),
    HTML_BASIC_BROWSING_LINKS("text/x-html+tr"),
    SPARQL_XML("application/sparql-results+xml"),
    JSON("application/sparql-results+json"),
    JAVASCRIPT("application/javascript"),
    TURTLE("text/turtle"),
    RDF_XML("application/rdf+xml"),
    N_TRIPLES("text/plain"),
    CSV("text/csv"),
    TAB_SEPARATED("text/tab-separated-values"); 

    private final String mimeType;

    OutputFormat(String mimeType) {
	this.mimeType = mimeType;
    }

    public String getMimeType() {
	return mimeType;
    };
};