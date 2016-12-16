package fr.ensma.lias.qars4ukb.cache;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lias.qars4ukb.SQLScriptRunner;
import fr.ensma.lias.qars4ukb.Session;
import fr.ensma.lias.qars4ukb.query.Query;
import fr.ensma.lias.qars4ukb.query.QueryFactory;
import fr.ensma.lias.qars4ukb.triplestore.jdbcdb.JDBCQueryExtFactory;
import fr.ensma.lias.qars4ukb.triplestore.jdbcdb.JDBCSession;

public class ExtendedCacheLBATest {

    private QueryFactory factoryExt;
    private Session session;
    private Query q1, q2;

    @Before
    public void setUp() throws Exception {
	factoryExt = new JDBCQueryExtFactory();
	session = factoryExt.createSession();
	SQLScriptRunner newScriptRunner = new SQLScriptRunner(((JDBCSession) session).getConnection(), false, false);
	InputStream resourceAsStream = getClass().getResourceAsStream("/test_dataset1.sql");
	newScriptRunner.runScript(new InputStreamReader(resourceAsStream));
	q1 = factoryExt.createQuery(
		"SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name> 'Course33' }");
	q2 = factoryExt
		.createQuery("SELECT ?p WHERE { ?p <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#email> ?e }");
    }
    
    @Test
    public void testIsFailingByCache() {
	q1.isFailing(session, 0.8);
	Assert.assertTrue(ExtendedCacheLBA.getInstance().isFailingByCache(q1, 1.0));
	Assert.assertTrue(ExtendedCacheLBA.getInstance().isFailingByCache(q1, 0.9));
	Assert.assertTrue(ExtendedCacheLBA.getInstance().isFailingByCache(q1, 0.8));
	Assert.assertFalse(ExtendedCacheLBA.getInstance().isFailingByCache(q1, 0.7));
	
    }
    
    @Test
    public void testIsSuccessfulByCache () {
	
    }
    
    @Test
    public void testAddFailingQuery() {
	
    }
    
    @Test
    public void testAddSuccessfulQuery() {
	
    }
}
