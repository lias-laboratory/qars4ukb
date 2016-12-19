package fr.ensma.lias.qars4ukb;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import fr.ensma.lias.qars4ukb.algo.AlgoBottomUpTest;
import fr.ensma.lias.qars4ukb.algo.AlgoNLBATest;
import fr.ensma.lias.qars4ukb.cache.ExtendedCacheLBATest;
import fr.ensma.lias.qars4ukb.query.QueryHSQLDBTest;
import fr.ensma.lias.qars4ukb.query.TriplePatternTest;

/**
 * @author Mickael BARON
 */
@RunWith(Suite.class)
@SuiteClasses(value = {
	QueryHSQLDBTest.class, TriplePatternTest.class, AlgoNLBATest.class, ExtendedCacheLBATest.class, AlgoBottomUpTest.class}
	)
public class AllTests {

}
