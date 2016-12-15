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
package fr.ensma.lias.qars4ukb.query;

import java.util.List;

import fr.ensma.lias.qars4ukb.Session;

/**
 * @author Stephane JEAN
 */
public interface QueryFactory {

    
    /**
     * Creates a query
     * @param rdfQuery the string of the query
     * @return the resulting query
     */
    Query createQuery(String rdfQuery);
    
    /**
     * Creates a query
     * @param tp a list of triple patterns
     * @return the resulting query
     */
    Query createQuery(List<TriplePattern> tp);
    
    
    /**
     * Creates a subquery of a query
     * @param rdfQuery string of the subquery
     * @param initialQuery the superquery
     * @return a subquery of a query
     */
    Query createQuery(String rdfQuery, Query initialQuery);

    /**
     * Creates a subquery of a query
     * @param tp a list of triple patterns
     * @param initialQuery the superquery
     * @return a subquery of a query
     */
    Query createQuery(List<TriplePattern> tp, Query initialQuery);
    
    /**
     * Creates a connection to the KB
     * @return a connection to the KB
     */
    Session createSession();
}
