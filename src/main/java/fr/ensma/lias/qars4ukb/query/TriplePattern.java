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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Stephane JEAN
 */
public class TriplePattern {

	/**
	 * a string representation of the triple pattern
	 */
	private String triplePattern;

	/**
	 * subject of this triple pattern
	 */
	private String subject;

	/**
	 * predicate of this triple pattern
	 */
	private String predicate;

	/**
	 * object of this triple pattern
	 */
	private String object;

	/**
	 * set of variables of this triple pattern
	 */
	private Set<String> variables;

	
	/**
	 * indice of this triple pattern in a query
	 */
	private int indiceInQuery;

	/**
	 * Get the subject of this triple pattern
	 * 
	 * @return the subject of this triple pattern
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Get the predicate of this triple pattern
	 * 
	 * @return the predicate of this triple pattern
	 */
	public String getPredicate() {
		return predicate;
	}

	/**
	 * Get the object of this triple pattern
	 * 
	 * @return the object of this triple pattern
	 */
	public String getObject() {
		return object;
	}

	@Override
	public String toString() {
		return triplePattern;
	}
	
	/**
	 * Return a simple string representation of this triple pattern : ti
	 * @return a simple string representation of this triple pattern
	 */
	public String toSimpleString() {
		return "t" + indiceInQuery;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((triplePattern == null) ? 0 : triplePattern.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TriplePattern other = (TriplePattern) obj;
		if (triplePattern == null) {
			if (other.triplePattern != null)
				return false;
		} else if (!triplePattern.equals(other.triplePattern))
			return false;
		return true;
	}
	
	
	/**
	 * Create a triple pattern with its string representation and its indice in
	 * a query
	 * 
	 * @param triplePattern
	 *            string representation of the triple pattern
	 * @param indiceInQuery
	 *            indice in a query
	 */
	public TriplePattern(String triplePattern, int indiceInQuery) {
		this.indiceInQuery = indiceInQuery;
		this.triplePattern = triplePattern;
		decompose();
	}

	/**
	 * Computes the subject, predicate and object of this triple pattern
	 */
	public void decompose() {
		int indexOfFirstSpace = triplePattern.indexOf(' ');
		subject = triplePattern.substring(0, indexOfFirstSpace);
		int indexOfSecondSpace = triplePattern.indexOf(' ', indexOfFirstSpace + 1);
		predicate = triplePattern.substring(indexOfFirstSpace + 1, indexOfSecondSpace);
		object = triplePattern.substring(indexOfSecondSpace + 1, triplePattern.length());
		removeSyntax();
	}

	/**
	 * Remove the syntax (i.e, "<" ">" of the sujet, predicate and object of
	 * this triple pattern
	 */
	private void removeSyntax() {
		subject = removeSyntax(subject);
		predicate = removeSyntax(predicate);
		object = removeSyntax(object);
	}

	/**
	 * remove the SPARQL syntax of a subject, predicate or object of a triple
	 * pattern
	 * 
	 * @param input
	 *            the subject, predicate or object
	 * @return the subject, predicate or object without the SPARQL syntax
	 */
	private String removeSyntax(String input) {
		String res = input;
		// a variable does not have a specific syntax
		if (!isVariable(input)) {
			res = input.substring(1, input.length() - 1);
		}
		return res;
	}

	/**
	 * Computes the variables of this triple pattern
	 * 
	 * @return a set of variable of this predicate
	 */
	public Set<String> getVariables() {
		// we compute the variables if it has not already been done
		if (variables == null)
			initVariables();
		return variables;
	}

	/**
	 * Initializes the set of variables of this triple pattern
	 */
	public void initVariables() {
		variables = new HashSet<String>();
		if (isSubjectVariable())
			variables.add(getVariable(subject));
		if (isPredicateVariable())
			variables.add(getVariable(predicate));
		if (isObjectVariable())
			variables.add(getVariable(object));
	}

	/**
	 * Get the variable name from its SPARQL representation (i.e, ?x -> x)
	 * 
	 * @param s
	 *            the SPARQL representation of the variable
	 * @return the variable name (without the ?)
	 */
	private String getVariable(String s) {
		return s.substring(0);
	}

	/**
	 * Checks whether the input string is a variable
	 * 
	 * @param s
	 *            the input string
	 * @return true if this is a variable, false otherwise
	 */
	private boolean isVariable(String s) {
		return s.startsWith("?");
	}

	/**
	 * Checks whether the subject is a variable
	 * 
	 * @return true if and only if the subject of this triple pattern is
	 *         variable
	 */
	public boolean isSubjectVariable() {
		return isVariable(subject);
	}

	/**
	 * Checks whether the predicate is a variable
	 * 
	 * @return true if and only if the predicate of this triple pattern is
	 *         variable
	 */
	public boolean isPredicateVariable() {
		return isVariable(predicate);
	}

	/**
	 * Checks whether the object is a variable
	 * 
	 * @return true if and only if the object of this triple pattern is variable
	 */
	public boolean isObjectVariable() {
		return isVariable(object);
	}

	/**
	 * Return a String representing the inpulist with the separtor between each
	 * element
	 * 
	 * @param inputList
	 *            the input list
	 * @param separator
	 *            the separator between each element
	 * @return the output string
	 */
	private String listWithSeparator(List<String> inputList, String separator) {
		// This method should be in a "util" class.
		String res = "";
		for (int i = 0; i < inputList.size(); i++) {
			if (i > 0)
				res += separator;
			res += inputList.get(i);
		}
		return res;
	}

	/**
	 * Computes the SQL query corresponding to this triple pattern on a table t(s,p,o,tv)
	 * @param alpha the threshold
	 * @return the SQL query corresponding to this triple pattern
	 */
	public String toSQL(Double alpha) {
		String res = "select ";
		List<String> valSelect = new ArrayList<String>();
		List<String> valWhere = new ArrayList<String>();
		if (!isSubjectVariable())
			valWhere.add("s='" + subject + "'");
		else
			valSelect.add("s as " + subject.substring(1));
		if (!isPredicateVariable())
			valWhere.add("p='" + predicate + "'");
		else
			valSelect.add("p as " + predicate.substring(1));
		if (!isObjectVariable())
			valWhere.add("o='" + object + "'");
		else
			valSelect.add("o as " + object.substring(1));
		if (valSelect.isEmpty())
			res += "*";
		else
			res += listWithSeparator(valSelect, ", ");
		res += " from t";
		// we add the trust value. We only code the min for the moment
		valWhere.add("tv>=" + alpha);
		if (!valWhere.isEmpty())
			res += " where " + listWithSeparator(valWhere, " and ");
		return res;
	}



}
