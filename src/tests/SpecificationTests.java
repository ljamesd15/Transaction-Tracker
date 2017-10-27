package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * SpecificationTests is a test suite used to encapsulate all
 * tests specific to the specification of this homework.
 *
 * For HW7, ScriptFileTests should be the only test class listed in
 * SpecificationTests. If you are tempted to add other classes, recall that
 * any tests you add to SpecificationTests must be valid tests for any other
 * student's implementation for this assignment, even though other students
 * will have designed a different public API.
 * 
 **/
@RunWith(Suite.class)
@SuiteClasses({ CheckAsserts.class, tests.ScriptFileTests.class})

public final class SpecificationTests
{
	/**
	 * Checks that assertions are enabled. If they are not, an error message is
	 * printed, and the system exits.
	 */
	public static void checkAssertsEnabled() {
		try {
			assert false;

			// assertions are not enabled
			System.err.println(
					"Java Asserts are not currently enabled. Follow homework "
					+ "writeup instructions to enable asserts on all JUnit "
					+ "Test files.");
			System.exit(1);

		} catch (AssertionError e) {
			// do nothing
			// assertions are enabled
		}
	}
}

