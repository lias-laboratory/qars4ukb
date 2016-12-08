/*********************************************************************************
* This file is part of QARS Project.
* Copyright (C) 2015 LIAS - ENSMA
*   Teleport 2 - 1 avenue Clement Ader
*   BP 40109 - 86961 Futuroscope Chasseneuil Cedex - FRANCE
* 
* QARS is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* QARS is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with QARS.  If not, see <http://www.gnu.org/licenses/>.
**********************************************************************************/
package fr.ensma.lias.qars4ukb.query;

import java.util.List;

import org.aeonbits.owner.ConfigFactory;

import fr.ensma.lias.qars4ukb.cfg.QARS4UKBConfig;

/**
 * @author Stephane JEAN
 * @author Mickael BARON
 */
public abstract class AbstractQueryFactory implements QueryFactory {

    private QARS4UKBConfig config;
    
    public AbstractQueryFactory() {
	config = ConfigFactory.create(QARS4UKBConfig.class);
    }
    
    protected QARS4UKBConfig getConfig() {
	return this.config;
    }
    
    @Override
    public Query createQuery(String rdfQuery, Query initialQuery) {
	final Query createQuery = this.createQuery(rdfQuery);
	
	if (initialQuery != null) {
	    ((AbstractQuery)createQuery).setInitialQuery(initialQuery);
	}
	return createQuery;
    }
    
    @Override
    public Query createQuery(List<TriplePattern> tp, Query initialQuery) {
	final Query createQuery = this.createQuery(tp);
	
	if (initialQuery != null) {
	    ((AbstractQuery)createQuery).setInitialQuery(initialQuery);
	}
	return createQuery;
    }
}
