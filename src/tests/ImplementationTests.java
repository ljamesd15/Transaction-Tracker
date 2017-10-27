package tests;

import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.runners.Suite.*;

/**
 * ImplementationTests is a test suite used to encapsulate all
 * tests specific to your implementation of this problem set.
 *
 * For instance, unit tests for your individual methods would
 * go here.
 */

@RunWith(Suite.class)
@SuiteClasses({ CheckAsserts.class,	 TransactionTest.class})

public final class ImplementationTests {
	
	/**
    * Checks that assertions are enabled. If they are not, an error message
    * is printed, and the system exits.
    */
	public static void checkAssertsEnabled() {
		try {
			assert false;

			// assertions are not enabled
			System.err.println("Java Asserts are not currently enabled. Follow "
					+ " writeup instructions to enable asserts on all JUnit "
					+ "Test files.");
			System.exit(1);

		} catch (AssertionError e) {
			// do nothing
			// assertions are enabled
		}
	}
}

