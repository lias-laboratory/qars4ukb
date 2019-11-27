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

import java.util.List;

/**
 * This interface manages the result of a query
 * 
 * @author Stephane JEAN
 */
public interface Result {

	/**
	 * Close the result set
	 */
	void close();

	/**
	 * Iterates to the next result
	 * 
	 * @return true if there is a next result
	 */
	boolean next();

	/**
	 * Return the string value of the result for the given column
	 * 
	 * @param col the column
	 * @return the string value of the result for the given column
	 */
	String getString(int col);

	/**
	 * Return the number of rows of this result
	 * 
	 * @return the number of rows of this result
	 * @throws Exception
	 */
	int getNbRow();

	/**
	 * Return return a list of results ; the maximum number of element is set with
	 * maxk
	 * 
	 * @return the number of rows of this result with a maximum set to maxk
	 */
	List<String> getNbRow(int maxK);

	/**
	 * Return the string values of the result for the given columns
	 * 
	 * @param cols the given columns
	 * @return the string values of the result for the given columns
	 */
	String[] getString(String[] cols);
}
