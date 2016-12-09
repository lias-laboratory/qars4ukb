package fr.ensma.lias.qars4ukb.query;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TriplePatternTest {
	
    /**
     *  Constants for RDF namespace
     */
    public static String PREFIX_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    /**
     *  Constants for RDFS prefixes
     */
    public static String PREFIX_RDFS = "http://www.w3.org/2000/01/rdf-schema#";
    /**
     *  Constants for OWL prefixes
     */
    public static String PREFIX_OWL = "http://www.w3.org/2002/07/owl#";
    /**
     *  Constants for the WatDiv benchmark
     */
    public static String PREFIX_WATDIV = "http://db.uwaterloo.ca/watdiv/";

    private TriplePattern t1, t2, t3,t4,t5;

    @Before
    public void setUp() throws Exception {
    	t1 = new TriplePattern("?p <type> <p>",1);
    	t2 = new TriplePattern("?p <advises> ?s",1);
    	t3 = new TriplePattern("?s <age> '25'",1);
    	t4 = new TriplePattern("?s <"+ PREFIX_RDF + "type> <" + PREFIX_WATDIV + "Book>",1);
    	t5 = new TriplePattern("<http://db.uwaterloo.ca/watdiv/Product2> <"+ PREFIX_WATDIV + "availableAt" + "> <http://db.uwaterloo.ca/watdiv/Retailer23>",1);
    }

    @Test
    public void testGetSuject() {
    	Assert.assertEquals("?p", t1.getSubject());
    	Assert.assertEquals("?p", t2.getSubject());
    	Assert.assertEquals("?s", t3.getSubject());
    	Assert.assertEquals("?s", t4.getSubject());
    	Assert.assertEquals("http://db.uwaterloo.ca/watdiv/Product2", t5.getSubject());
    }
    
    @Test
    public void testGetPredicate() {
    	Assert.assertEquals("type", t1.getPredicate());
    	Assert.assertEquals("advises", t2.getPredicate());
    	Assert.assertEquals("age", t3.getPredicate());
    	Assert.assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", t4.getPredicate());
    	Assert.assertEquals("http://db.uwaterloo.ca/watdiv/availableAt", t5.getPredicate());
    }
    
    @Test
    public void testGetObject() {
    	Assert.assertEquals("p", t1.getObject());
    	Assert.assertEquals("?s", t2.getObject());
    	Assert.assertEquals("25", t3.getObject());
    	Assert.assertEquals("http://db.uwaterloo.ca/watdiv/Book", t4.getObject());
    	Assert.assertEquals("http://db.uwaterloo.ca/watdiv/Retailer23", t5.getObject());
    }
    
    @Test
    public void testToString() {
    	Assert.assertEquals("?p <type> <p>", t1.toString());
    	Assert.assertEquals("?p <advises> ?s", t2.toString());
    	Assert.assertEquals("?s <age> '25'", t3.toString());
    	Assert.assertEquals("?s <"+ PREFIX_RDF + "type> <" + PREFIX_WATDIV + "Book>", t4.toString());
    	Assert.assertEquals("<http://db.uwaterloo.ca/watdiv/Product2> <"+ PREFIX_WATDIV + "availableAt" + "> <http://db.uwaterloo.ca/watdiv/Retailer23>", t5.toString());
    }
    
    @Test
    public void testToSimpleString() {
    	Assert.assertEquals("t1", t1.toSimpleString());
    	Assert.assertEquals("t1", t2.toSimpleString());
    	Assert.assertEquals("t1", t3.toSimpleString());
    	Assert.assertEquals("t1", t4.toSimpleString());
    	Assert.assertEquals("t1", t5.toSimpleString());
    }
    
    @Test
    public void testGetVariables() {
    	Assert.assertEquals(1, t1.getVariables().size());
    	Assert.assertEquals(2, t2.getVariables().size());
    	Assert.assertEquals(1, t3.getVariables().size());
    	Assert.assertEquals(1, t4.getVariables().size());
    	Assert.assertEquals(0, t5.getVariables().size());
    }
    
    @Test
    public void testIsSubjectVariable(){
    	Assert.assertTrue(t1.isSubjectVariable());
    	Assert.assertTrue(t2.isSubjectVariable());
    	Assert.assertTrue(t3.isSubjectVariable());
    	Assert.assertTrue(t4.isSubjectVariable());
    	Assert.assertFalse(t5.isSubjectVariable());
    }
    
    @Test
    public void testIsPredicateVariable(){
    	Assert.assertFalse(t1.isPredicateVariable());
    	Assert.assertFalse(t2.isPredicateVariable());
    	Assert.assertFalse(t3.isPredicateVariable());
    	Assert.assertFalse(t4.isPredicateVariable());
    	Assert.assertFalse(t5.isPredicateVariable());
    }
    
    @Test
    public void testIsObjectVariable(){
    	Assert.assertFalse(t1.isObjectVariable());
    	Assert.assertTrue(t2.isObjectVariable());
    	Assert.assertFalse(t3.isObjectVariable());
    	Assert.assertFalse(t4.isObjectVariable());
    	Assert.assertFalse(t5.isObjectVariable());
    }
    
    @Test
	public void testEquals() {
    	Assert.assertTrue(t1.equals(new TriplePattern("?p <type> <p>", 2)));
    }
    
    @Test
    public void testToSQL() {
    	String sqlT1 = "select s as p from t where p='type' and o='p'";
    	Assert.assertEquals(sqlT1, t1.toSQL());
    	String sqlT2 = "select s as p, o as s from t where p='advises'";
    	Assert.assertEquals(sqlT2, t2.toSQL());
    }

}
