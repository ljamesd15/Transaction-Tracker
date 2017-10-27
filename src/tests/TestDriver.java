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
				"to read from a file: java hw5.test.HW5TestDriver <name of "
						+ "input script>");
		System.err.println(
				"to read from standard in: java hw5.test.HW5TestDriver");
	}

	/** String -> Graph: maps the names of graphs to the actual graph **/
	private final Map<String, Graph<String, Double>> graphs = 
			new HashMap<String, Graph<String, Double>>();
	private final PrintWriter output;
	private final BufferedReader input;

	/**
	 * @requires r != null && w != null
	 *
	 * @effects Creates a new HW7TestDriver which reads command from <tt>r</tt>
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

	private void executeCommand(String command, List<String> arguments) {
		try {
			if (command.equals("CreateGraph")) {
				createGraph(arguments);
			} else if (command.equals("AddNode")) {
				addNode(arguments);
			} else if (command.equals("AddEdge")) {
				addEdge(arguments);
			} else if (command.equals("ListNodes")) {
				listNodes(arguments);
			} else if (command.equals("ListChildren")) {
				listChildren(arguments);
			} else if (command.equals("LoadGraph")) {
				loadGraph(arguments);
			} else if (command.equals("FindPath")) {
				findPath(arguments);
			} else {
				output.println("Unrecognized command: " + command);
			}
		} catch (Exception e) {
			output.println("Exception: " + e.toString());
		}
	}

	private void createGraph(List<String> arguments) {
		if (arguments.size() != 1) {
			throw new CommandException(
					"Bad arguments to CreateGraph: " + arguments);
		}

		String graphName = arguments.get(0);
		createGraph(graphName);
	}

	private void createGraph(String graphName) {
		graphs.put(graphName, new Graph<String, Double>());
		output.println("created graph " + graphName);
	}

	private void addNode(List<String> arguments) {
		if (arguments.size() != 2) {
			throw new CommandException(
					"Bad arguments to addNode: " + arguments);
		}

		String graphName = arguments.get(0);
		String nodeName = arguments.get(1);

		addNode(graphName, nodeName);
	}

	private void addNode(String graphName, String nodeName) {
		if (!(graphs.containsKey(graphName))) {
			output.println("The graph '" + graphName + "' does not exist.");
			return;
		}
		Graph<String, Double> currGraph = graphs.get(graphName);
		boolean added = true;
		currGraph.addNode(nodeName);
		if (added) {
			output.println("added node " + nodeName + " to " + graphName);
		} else {
			output.println("The node " + nodeName
					+ " was unsuccessfully added to " + graphName);
		}
	}

	private void addEdge(List<String> arguments) {
		if (arguments.size() != 4) {
			throw new CommandException(
					"Bad arguments to addEdge: " + arguments);
		}

		String graphName = arguments.get(0);
		String parentName = arguments.get(1);
		String childName = arguments.get(2);
		Double edgeWeight = Double.parseDouble(arguments.get(3));

		addEdge(graphName, parentName, childName, edgeWeight);
	}

	private void addEdge(String graphName, String parentName, String childName,
			Double edgeWeight) {
		if (!(graphs.containsKey(graphName))) {
			output.println("The graph '" + graphName + "' does not exist.");
			return;
		}
		Graph<String, Double> currGraph = graphs.get(graphName);
		boolean added = currGraph.addConnection(parentName, childName,
				edgeWeight);
		if (added) {
			output.print("added edge ");
			output.printf("%.3f", edgeWeight);
			output.println(" from " + parentName + " to " + childName + " in "
					+ graphName);
		} else {
			output.print("The edge ");
			output.printf("%.3f", edgeWeight);
			output.println(" from " + parentName + " to " + childName 
					+ " was unsuccessfully added to " + graphName);
		}
	}

	private void listNodes(List<String> arguments) {
		if (arguments.size() != 1) {
			throw new CommandException(
					"Bad arguments to listNodes: " + arguments);
		}

		String graphName = arguments.get(0);
		listNodes(graphName);
	}

	private void listNodes(String graphName) {
		if (!(graphs.containsKey(graphName))) {
			output.println("The graph '" + graphName + "' does not exist.");
			return;
		}
		Graph<String, Double> currGraph = graphs.get(graphName);
		Set<String> characterSet = currGraph.getNodes();
		output.print(graphName + " contains:");
		for (String character : characterSet) {
			output.print(" " + character);
		}
		output.println();
	}

	private void listChildren(List<String> arguments) {
		if (arguments.size() != 2) {
			throw new CommandException(
					"Bad arguments to listChildren: " + arguments);
		}

		String graphName = arguments.get(0);
		String parentName = arguments.get(1);
		listChildren(graphName, parentName);
	}

	private void listChildren(String graphName, String parentName) {
		if (!(graphs.containsKey(graphName))) {
			output.println("The graph '" + graphName + "' does not exist.");
			return;
		}
		Graph<String, Double> currGraph = graphs.get(graphName);
		Set<Connection<String, Double>> connections = currGraph
				.isConnectedWith(parentName);
		Set<String> connectedCharSet = new TreeSet<String>();
		for (Connection<String, Double> con : connections) {
			connectedCharSet.add(con.getTo());
		}
		output.print("the children of " + parentName + " in " + graphName
				+ " are:");
		for (String character : connectedCharSet) {
			Double edgeWeight = currGraph.getFirstConnectionLabel
					(parentName, character);
			output.print(" " + character + "(" + edgeWeight + ")");
		}
		output.println();
	}

	private void loadGraph(List<String> arguments)
			throws MalformedDataException {
		if (arguments.size() != 2) {
			throw new CommandException(
					"Bad arguments to LoadGraph: " + arguments);
		}

		String graphName = arguments.get(0);
		String filename = arguments.get(1);

		this.loadGraph(graphName, filename);
	}

	private void loadGraph(String graphName, String filename)
			throws MalformedDataException {
		try {
			graphs.put(graphName, MarvelPaths2.loadWeightedGraph(filename));
		} catch (IOException e) {
			output.println(filename + " does not exist.");
			return;
		} catch (MalformedDataException e) {
			output.println("graph " + filename + " could not be loaded due to"
					+ " formatting issues.");
			return;
		}
		output.println("loaded graph " + graphName);
	}

	private void findPath(List<String> arguments) {
		if (arguments.size() != 3) {
			throw new CommandException(
					"Bad arguments to FindPath: " + arguments);
		}

		String graphName = arguments.get(0);
		String from = arguments.get(1);
		String to = arguments.get(2);

		this.findPath(graphName, from, to);
	}

	private void findPath(String graphName, String from, String to) {
		if (!(graphs.containsKey(graphName))) {
			output.println("The graph '" + graphName + "' does not exist.");
			return;
		}
		Graph<String, Double> multigraph = graphs.get(graphName);

		// Converting underscores in names to white space.
		String[] fromSplit = from.split("_");
		String newFrom = fromSplit[0];
		for (int i = 1; i < fromSplit.length; i++) {
			newFrom += " " + fromSplit[i];
		}

		String[] toSplit = to.split("_");
		String newTo = toSplit[0];
		for (int i = 1; i < toSplit.length; i++) {
			newTo += " " + toSplit[i];
		}
		
		// If characters don't exists print unknown character 'name'.
		boolean fromExists = multigraph.nodeExists(newFrom);
		boolean toExists = multigraph.nodeExists(newTo);
		if (!(fromExists && toExists)) {
			if (!fromExists) {
				output.println("unknown character " + newFrom);
			}
			if (!toExists) {
				output.println("unknown character " + newTo);
			}
		} else {
			// Now that we know they both exist we can attempt to find the 
			// path.
			List<Node<String>> solution = DijkstrasAlgorithm.
					findShortestweightedPath(newFrom, newTo, multigraph);
			output.println("path from " + newFrom + " to " + newTo + ":");
			if (solution == null) {
				output.println("no path found");
			} else {
				int size = solution.size();
				double totalCost = 0.000;
				for (int i = 1; i < size; i++) {
					// Save the nodes from and to for quicker use below.
					Node<String> nodeFrom = solution.get(i - 1);
					Node<String> nodeTo = solution.get(i);
					
					
					String charFrom = nodeFrom.getNodeName();
					String charTo = nodeTo.getNodeName();
					double edgeValue = nodeTo.getPathWeight() - 
							nodeFrom.getPathWeight();
					output.print(charFrom + " to " + charTo + " with weight ");
					output.printf("%.3f", edgeValue);
					output.println();
					
					totalCost += edgeValue;
				}
				output.print("total cost: ");
				output.printf("%.3f", totalCost);
				output.println();
			}
		}
	}

	/**
	 * This exception results when the input file cannot be parsed properly
	 **/
	static class CommandException extends RuntimeException {

		public CommandException() {
			super();
		}

		public CommandException(String s) {
			super(s);
		}

		public static final long serialVersionUID = 3495;
	}
}
