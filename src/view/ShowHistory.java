package view;

import java.sql.ResultSet;
import java.util.Scanner;

import controller.TransactionHelper;
import controller.TransactionsDB;
import model.User;

public class ShowHistory {

	private static final int DISPLAY_AS_LIST = 0;
	private static final int DISPLAY_AS_PIE = 1;
	private static final int DISPLAY_AS_BAR = 2;
	
	/**
	 * Allows the user to see their previous transaction history.
	 * @param db the database whose transactions will be shown.
	 * @param input the scanner used for user input.
	 * @param user the user whose transactions will be shown.
	 */
	protected static void run(TransactionsDB db, Scanner input, User user) {
		String[] categories = db.getCategories();
		
		// Ask user how they would like there transactions displayed.
		int displayType = display(input);
		if (displayType == -1) {
			// User wants to return to the main menu.
			return;
		}
		
		// Ask user how they would like to refine displayed transactions.
		String whereClause = refine(categories, input);
		if (whereClause == null) {
			// User wants to return to the main menu.
			return;
		}
		
		// Ask user how they would like to break down the refined transactions.
		String[] selectClause = new String[]
		String groupByClause = breakdown(categories, input, displayType);
		if (groupByClause == null) {
			// User wants to return to the main menu.
			return;
		}
		
		// Query the database
		ResultSet transactions = db.getTransactions(whereClause, groupByClause, user);
		
		// Display transactions
		switch (displayType) {
		
			case DISPLAY_AS_LIST:
				displayList(transactions);
				
			case DISPLAY_AS_PIE:
				displayPieChart(transactions);
				
			case DISPLAY_AS_BAR:
				displayBarGraph(transactions);
		}
	}

	/**
	 * Alerts the user that their command was invalid.
	 * @param command is the user entered command which is invalid.
	 */
	private static void invalidCommand(String command) {
		System.out.println("Invalid command: '" + command + "'.");
		System.out.println("Type 'options' to see a list of valid options.");
	}
	
	/**
	 * Asks user how they would like the transaction history displayed.
	 * @param input is the scanner used to read user input.
	 * @return An integer representing the display type. -1 will be returned if the user decides
	 * 		they want to go back to the main menu.
	 */
	private static int display(Scanner input) {
		String response;
		System.out.println('\n' + "To see a list of available display optios type 'options'.");
		
		while (true) {
			System.out.print('\n' + "How would you like to display your transactions?" 
					+ '\n' + "> ");
			response = input.nextLine();
			
			switch (response) {
				
				case "0":
					// List
					return DISPLAY_AS_LIST;
					
				case "1":
					// Pie chart
					//return DISPLAY_AS_PIE;
					System.out.println("This option has not yet been implemented.");
					
				case "2":
					// Bar graph
					//return DISPLAY_AS_BAR;
					System.out.println("This option has not yet been implemented.");
					
				case "options":
					// Show display options
					displayOptions();
					break;
					
				case "back":
					// Return to main menu
					return -1;
									
				default:
					// Invalid command
					invalidCommand(response);
			}
		}
	}
	
	/** Prints the display options for displaying the transaction history. */
	private static void displayOptions() {
		System.out.println("Options to display transaction history are...");
		System.out.println("'0' to display transactions as a list.");
		System.out.println("'1' to display transactions as a pie chart (not yet implemented).");
		System.out.println("'2' to display transactions as a bar graph (not yet implemenetd).");
		System.out.println("'options' to see the list of display options.");
		System.out.println("'back' to go back to the main menu.");
	}
	
	/**
	 * Asks the user how they would like to refine their results.
	 * @param categories are the transaction categories which can be used to refine the 
	 * 		ResultSet of transactions queried from the database.
	 * @param input is the scanner used to read user input.
	 * @return A SQL where condition which will refine the results a ResultSet of transactions
	 * 		queried from a database. Null may also be returned if the user decides they wish to
	 * 		go back to the main menu.
	 */
	private static String refine(String[] categories, Scanner input) {
		String response;
		System.out.println('\n' + "Type 'options' to see available refining options.");
		
		// Until a SQL where clause is returned from one of the options.
		while (true) {
			System.out.print('\n' + "(Refine) How would you like to refine your transactions?"
					 + '\n' + "> ");
			response = input.nextLine();
			
			switch (response) {
				
				case "0":
					// Refine to a single category.
					return refineByCategory(categories, input);
					
				case "1":
					// Refine to a certain month in all years.
					return refineByMonth(input);
					
				case "2":
					// Refine to a certain year.
					return refineByYear(input);
					
				case "3":
					// Refine to a certain month within a certain year.
					return refineByMonthAndYear(input);
					
				case "4":
					// Refine by a certain price.
					return refineByPrice(input);
					
				case "all":
					// Do not refine transactions in preparation of breakdown.
					return "";
					
				case "options":
					// Show refining options
					refineOptions();
					
				case "back":
					// Go back to main menu.
					return null;
					
				default:
					// Invalid command.
					invalidCommand(response);
			}
		}
	}
	
	/** Print the options to refine a breakdown of transactions. */
	private static void refineOptions() {
		System.out.println("Available ways to refine transactions are...");
		System.out.println("'0' by a category.");
		System.out.println("'1' by a month.");
		System.out.println("'2' by a year.");
		System.out.println("'3' by a month within a certain year.");
		System.out.println("'4' by a price.");
		System.out.println("'all' to break down all entered transactions.");
		System.out.println("'options' to show transaction refinement options.");
		System.out.println("'back' to go back to the main menu.");
	}
	
	private static String refineByCategory(String[] categories, Scanner input) {
		while (true) {
			System.out.print('\n' + "Available categories are ");
			TransactionHelper.printCategories(categories);
			System.out.println(".");
			
			
			
		}
	}

	private static String refineByMonth(Scanner input) {
		// TODO Auto-generated method stub
		return null;
	}

	private static String refineByYear(Scanner input) {
		// TODO Auto-generated method stub
		return null;
	}

	private static String refineByMonthAndYear(Scanner input) {
		// TODO Auto-generated method stub
		return null;
	}

	private static String refineByPrice(Scanner input) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Asks user how they would like to breakdown their transactions.
	 * @param categories are the transaction categories which can be used to breakdown the 
	 * 		ResultSet of transactions queried from the database.
	 * @param input is the scanner used to read user input.
	 * @return A SQL where condition which will refine the results a ResultSet of transactions
	 * 		queried from a database. Null may also be returned if the user decides they wish to
	 * 		go back to the main menu.
	 */
	private static String breakdown(String[] categories, Scanner input, int displayType) {
		String response;
		System.out.println('\n' + "Type 'options' to see available break down options.");
		
		// Until a SQL group by clause is returned from one of the options.
		while (true) {
			System.out.print('\n' + "(Breakdown) How would you like to break down your transactions?"
					 + '\n' + "> ");
			response = input.nextLine();
			
			switch (response) {
				
				case "0":
					// Break down by category.
					return breakDownByCategory(categories, input);
					
				case "1":
					// Break down by months in all years.
					return breakDownByMonth(input);
					
				case "2":
					// Break down by years.
					return breakDownByYear(input);
					
				case "3":
					// Break down by months within a certain year.
					return breakDownByMonthAndYear(input);
					
				case "4":
					// Break down by price ranges.
					return breakDownByPrice(input);
					
				case "all":
					if (displayType == DISPLAY_AS_LIST) {
						// Do not break down transactions.
						return "";
					}
					// If the display type is not a list, let this command fall through into 
					// invalid command.
					
				case "options":
					// Show refining options
					breakDownOptions();
					
				case "back":
					// Go back to main menu.
					return null;
					
				default:
					// Invalid command.
					invalidCommand(response);
			}
		}
	}

	/** Prints available options to break down transaction histories with.	 */
	private static void breakDownOptions() {
		System.out.println("Available ways to break down transactions are...");
		System.out.println("'0' by categories.");
		System.out.println("'1' by months.");
		System.out.println("'2' by years.");
		System.out.println("'3' by months within a certain year.");
		System.out.println("'4' by price ranges.");
		System.out.println("'all' to list all entered transactions (Only available if transactions"
				+ " were chosen to be displayed as a list).");
		System.out.println("'options' to show transaction refinement options.");
		System.out.println("'back' to go back to the main menu.");
	}

	private static String breakDownByCategory(String[] categories, Scanner input) {
		// TODO Auto-generated method stub
		return null;
	}

	private static String breakDownByMonth(Scanner input) {
		// TODO Auto-generated method stub
		return null;
	}

	private static String breakDownByYear(Scanner input) {
		// TODO Auto-generated method stub
		return null;
	}

	private static String breakDownByMonthAndYear(Scanner input) {
		// TODO Auto-generated method stub
		return null;
	}

	private static String breakDownByPrice(Scanner input) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Displays the transactions as a list.
	 * @param transactions are the transactions which will be displayed in a list.
	 */
	private static void displayList(ResultSet transactions) {
		
	}
	
	/**
	 * Displays the transactions as a pie chart.
	 * @param transactions are the transactions which will be displayed in a pie chart.
	 */
	private static void displayPieChart(ResultSet transactions) {
		
	}
	
	/**
	 * Displays the transactions as a bar graph.
	 * @param transactions are the transactions which will be displayed in a bar graph.
	 */
	private static void displayBarGraph(ResultSet transactions) {
		
	}
}
