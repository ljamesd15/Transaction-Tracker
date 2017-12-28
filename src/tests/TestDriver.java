package tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import hw5.Connection;
import hw5.Graph;
import hw6.MarvelParser.MalformedDataException;
import hw7.DijkstrasAlgorithm;
import hw7.MarvelPaths2;
import hw7.Node;

/**
 * This class implements a testing driver which reads test scripts from files
 * for your graph ADT and improved MarvelPaths application using Dijkstra's
 * algorithm.
 * 
 * @param <T>
 **/
public class TestDriver {
	
	public static void main(String args[]) {
		try {
			if (args.length > 1) {
				printUsage();
				return;
			}

			TestDriver td;

			if (args.length == 0) {
				td = new TestDriver(new InputStreamReader(System.in),
						new OutputStreamWriter(System.out));
			} else {

				String fileName = args[0];
				File tests = new File(fileName);

				if (tests.exists() || tests.canRead()) {
					td = new TestDriver(new FileReader(tests),
							new OutputStreamWriter(System.out));
				} else {
					System.err.println("Cannot read from " + tests.toString());
					printUsage();
					return;
				}
			}

			td.runTests();

		} catch (IOException e) {
			System.err.println(e.toString());
			e.printStackTrace(System.err);
		}
	}

	private static void printUsage() {
		System.err.println("Usage:");
		System.err.println(
				"to read from a file: java src.tests.TestDriver <name of "
						+ "input script>");
		System.err.println(
				"to read from standard in: java src.tests.TestDriver");
	}
	
	private final PrintWriter output;
	private final BufferedReader input;

	/**
	 * @requires r != null && w != null
	 *
	 * @effects Creates a new TestDriver which reads command from <tt>r</tt>
	 *          and writes results to <tt>w</tt>.
	 **/
	public TestDriver(Reader r, Writer w) {
		input = new BufferedReader(r);
		output = new PrintWriter(w);
	}

	/**
	 * @effects Executes the commands read from the input and writes results to
	 *          the output
	 * @throws IOException
	 *             if the input or output sources encounter an IOException
	 **/
	public void runTests() throws IOException {
		String inputLine;
		while ((inputLine = input.readLine()) != null) {
			if ((inputLine.trim().length() == 0)
					|| (inputLine.charAt(0) == '#')) {
				// echo blank and comment lines
				output.println(inputLine);
			} else {
				// separate the input line on white space
				StringTokenizer st = new StringTokenizer(inputLine);
				if (st.hasMoreTokens()) {
					String command = st.nextToken();

					List<String> arguments = new ArrayList<String>();
					while (st.hasMoreTokens()) {
						arguments.add(st.nextToken());
					}

					executeCommand(command, arguments);
				}
			}
			output.flush();
		}
	}

	/**
	 * Test driver receives input and directs the arguments to the desired method.
	 * @param command the string determines which method the arguments get directed to.
	 * @param arguments the information used to execute a test on a certain portion of 
	 * 		Transaction Tracker.
	 */
	private void executeCommand(String command, List<String> arguments) {
		switch (command) {
			case "Create User": 
				createUser(arguments);
				break;
			
			case "Log in":
				logIn(arguments);
				break;
				
			case "Log out":
				logOut();
				break;
				
			case "New Transaction":
				addExpense(arguments);
				break;
				
			case "exit":
				exit();
				break;
		}
	}

	/**
	 * Creates a user given a list of arguments.
	 * @param arguments the information used to test the creation of a new user.
	 */
	private void createUser(List<String> arguments) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Logs in a user given a list of arguments.
	 * @param arguments the informations used to test logging in a user.
	 */
	private void logIn(List<String> arguments) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Logs out the currently logged in user.
	 */
	private void logOut() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Adds an expense given a list of arguments.
	 * @param arguments the information used to test the addition of a new expense.
	 */
	private void addExpense(List<String> arguments) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Exits the Transaction Tracker application.
	 */
	private void exit() {
		// TODO Auto-generated method stub
		
	}

	
}
