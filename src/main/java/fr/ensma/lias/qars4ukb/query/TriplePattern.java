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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Stephane JEAN
 */
public class TriplePattern {

    private String triplePattern;

    private String subject;

    private String predicate;

    private String object;

    private Set<String> variables;

    /**
     * Similarity of each relaxedTriplePattern with this triplePattern
     */
    private Map<TriplePattern, Double> simRelaxedTriplePatterns;

    /**
     * Next relaxed triple pattern (if t(1) return t(2)).
     */
    private TriplePattern nextRelaxedTriplePattern;

    public TriplePattern(String triplePattern, int indiceInQuery) {
	this.triplePattern = triplePattern;
	decompose();
    }

    public void decompose() {
	int indexOfFirstSpace = triplePattern.indexOf(' ');
	subject = triplePattern.substring(0, indexOfFirstSpace);
	int indexOfSecondSpace = triplePattern.indexOf(' ',
		indexOfFirstSpace + 1);
	predicate = triplePattern.substring(indexOfFirstSpace + 1,
		indexOfSecondSpace);
	object = triplePattern.substring(indexOfSecondSpace + 1,
		triplePattern.length());
	// log.info("--> " + object + "<--");
	removeSyntax();
    }

    private void removeSyntax() {
	subject = removeSyntax(subject);
	// log.info("pred avant : " + predicate);
	predicate = removeSyntax(predicate);
	// log.info("pred : " + predicate);
	object = removeSyntax(object);
    }

    private String removeSyntax(String input) {
	String res = input;
	if (!isVariable(input)) {
	    res = input.substring(1, input.length() - 1);
	}
	// log.info(res);
	return res;
    }

    public Set<String> getVariables() {
	if (variables == null)
	    initVariables();
	return variables;
    }

    public void initVariables() {
	variables = new HashSet<String>();
	if (isSubjectVariable())
	    variables.add(getVariable(subject));
	if (isPredicateVariable())
	    variables.add(getVariable(predicate));
	if (isObjectVariable())
	    variables.add(getVariable(object));
    }

    private String getVariable(String s) {
	return s.substring(0);
    }

    private boolean isVariable(String s) {
	return s.startsWith("?");
    }

    public boolean isSubjectVariable() {
	return isVariable(subject);
    }

    public boolean isPredicateVariable() {
	return isVariable(predicate);
    }

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
	String res = "";
	for (int i = 0; i < inputList.size(); i++) {
	    if (i > 0)
		res += separator;
	    res += inputList.get(i);
	}
	return res;
    }

    public String toSQL() {
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
	if (!valWhere.isEmpty())
	    res += " where " + listWithSeparator(valWhere, " and ");
	return res;
    }

    // For a star query
    public String toSQL(String variable) {
	String res = "select distinct s from t where p='" + predicate + "'";
	if (!isObjectVariable())
	    res += " and o='" + object + "'";
	return res;
    }

    public String getSubject() {
	return subject;
    }

    public String getPredicate() {
	return predicate;
    }

    public String getObject() {
	return object;
    }

    @Override
    public String toString() {
	return triplePattern;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((triplePattern == null) ? 0 : triplePattern.hashCode());
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

    class TriplePatternComp implements Comparator<TriplePattern> {
	@Override
	public int compare(TriplePattern t1, TriplePattern t2) {
	    Double simT1 = simRelaxedTriplePatterns.get(t1);
	    Double simT2 = simRelaxedTriplePatterns.get(t2);
	    if (simT1 <= simT2) {
		return 1;
	    } else {
		return -1;
	    }
	}
    }

    public TriplePattern getNextRelaxedTriplePattern() {
	return nextRelaxedTriplePattern;
    }
}
