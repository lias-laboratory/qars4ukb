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

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import fr.ensma.lias.qars4ukb.Result;

/**
 * @author Mickael BARON
 */
public class JenaTDBNativeResult implements Result {

    private ResultSet rset;

    public JenaTDBNativeResult(ResultSet rset) {
	super();
	this.rset = rset;
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public boolean next() throws Exception {
	return rset.hasNext();
    }

    @Override
    public String getString(int col) throws Exception {
	QuerySolution sol = rset.nextSolution();
	return sol.get("X").toString();
    }

    public String[] getString(String[] cols) throws Exception {
	String[] res = new String[cols.length];
	QuerySolution sol = rset.nextSolution();
	for (int i = 0; i < cols.length; i++) {
	    res[i] = sol.get(cols[i]).toString();
	}
	return res;

    }

    @Override
    public int getNbRow() throws Exception {
	int res = 0;
	while (rset.hasNext()) {
	    res++;
	    rset.nextSolution();
	}
	return res;
    }

    @Override
    public List<String> getNbRow(int maxK) throws Exception {
	List<String> res = new ArrayList<String>();
	while (rset.hasNext() && res.size() <= maxK) {
	    res.add(rset.nextSolution().toString());
	}
	return res;
    }
}
