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

import java.util.List;

import fr.ensma.lias.qars4ukb.Result;
import fr.ensma.lias.qars4ukb.Session;
import fr.ensma.lias.qars4ukb.query.AbstractQuery;
import fr.ensma.lias.qars4ukb.query.QueryFactory;
import fr.ensma.lias.qars4ukb.query.TriplePattern;

/**
 * @author Stephane JEAN
 */
public class JenaTDBNativeQuery extends AbstractQuery {

    private JenaTDBNativeQueryHelper helper;

    public JenaTDBNativeQuery(QueryFactory factory, String query) {
	super(factory, query);
	helper = new JenaTDBNativeQueryHelper(this);
    }

    public JenaTDBNativeQuery(QueryFactory factory, List<TriplePattern> tps) {
	super(factory, tps);
	helper = new JenaTDBNativeQueryHelper(this);
    }

    @Override
    public boolean isFailingAux(Session session) {
	return helper.executeQuery(session);
    }

    @Override
    public Result getResult(Session session)  {
	return helper.getResult(session);
    }

    @Override
    public String toNativeQuery() {
	return helper.toNativeQuery();
    }
}
