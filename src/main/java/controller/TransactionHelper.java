package controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

/**
 * This helper class contains static methods used to manipulate information in the 
 * Transaction Tracker program.
 * @author L. James Davidson
 */
public class TransactionHelper {

	// File path to this folder
	protected static final String FILEPATH = (new File("")).getAbsolutePath();
	
	// Log file
	private static File LOG = new File(FILEPATH + "\\.log");
	
	// Log file writer
	private static PrintWriter pw;
	
	/** Prepares the log file for any errors which might occur. */
	public static void prepare() {
		try {
			pw = new PrintWriter(new FileWriter(LOG, true), true);
		} catch (IOException e) {
			System.out.println("Could not set up connection to log file.");
			System.exit(1);
		}
	}
	
    /**
     * Asks the user a yes or no question.
     * @param input is the scanner used to read user input.
     * @param question is the true-false question which the user will be prompted with.
     * @return A true for yes and a false for no.
     */
    public static boolean yesNoQuestion(Scanner input, String question) {
    	String response;
    	
		while (true) {
			System.out.print('\n' + question + '\n' + "> ");
			response = input.nextLine();
			
	    	if (response.equalsIgnoreCase("yes") || response.equalsIgnoreCase("y")) {
	    		return true;
	    	} else if (response.equalsIgnoreCase("no") || response.equalsIgnoreCase("n") ) {
	    		return false;
	    	} else {
	    		System.out.println("Invalid response. Please type 'yes' or 'no'");
	    	}
		}
    }
    
    /**
     * Asks the user a question which requires an integer response.
     * @param input The scanner used to read user input.
     * @param question The question which the user will need to answer.
     * @param upperBound The largest integer which is a valid response.
     * @return The user's integer answer.
     */
    public static int numberResponse(Scanner input, String question, int lowerBound, int upperBound) {
    	int response;
    	
		while (true) {
			System.out.print('\n' + question + '\n' + "> ");
			try {
				response = Integer.parseInt(input.nextLine());
				if (response >= lowerBound && response <= upperBound)
					return response;	
			} catch (NumberFormatException e) {
				// Do nothing, question will be alerted of invalid response and re-prompted.
			}
			
			System.out.println("Invalid response. Please type a number between " + lowerBound 
					+ " and " + upperBound + ".");
		}
    }
    
	
	/**
	 * Gets the user specified year to answer the parameter question.
	 * @param input the scanner to read user input.
	 * @param dateInfo is the array of integers which represent the date information.
	 * @param question The question the user will be prompted to answer.
	 * @modifies dateInfo[0], which is used to store the integer representing the year.
	 */
	public static void setYear(Scanner input, int[] dateInfo, String question) {
		dateInfo[0] = TransactionHelper.numberResponse(input, question, 
				0, LocalDate.now().getYear());
	}
	
	/**
	 * Gets the user specified month to answer the parameter question.
	 * @param input the scanner to read user input.
	 * @param dateInfo is the array of integers which represent the date information.
	 * @param question The question the user will be prompted to answer.
	 * @modifies dateInfo[1], which is used to store the integer representing the month.
	 */
	public static void setMonth(Scanner input, int[] dateInfo, String question) {
		LocalDate today = LocalDate.now();			
		int maxMonth = 12;
		if (dateInfo[0] == today.getYear())
			maxMonth = today.getMonthValue();
		dateInfo[1] = TransactionHelper.numberResponse(input, question, 1, maxMonth);
	}
	
	/**
	 * Gets the user specified day to answer the parameter question.
	 * @param input the scanner to read user input.
	 * @param dateInfo is the array of integers which represent the date information.
	 * @param question The question the user will be prompted to answer.
	 * @modifies dateInfo[2], which is used to store the integer representing the day.
	 */
	public static void setDay(Scanner input, int[] dateInfo, String question) {
		LocalDate today = LocalDate.now();
		int maxDay = LocalDate.of(dateInfo[0], dateInfo[1], 1).lengthOfMonth();
		if (dateInfo[0] == today.getYear() && dateInfo[1] == today.getMonthValue())
			maxDay = today.getDayOfMonth();
		dateInfo[2] = TransactionHelper.numberResponse(input, question, 1, maxDay);
	}
    
    /**
     * Prints an error to the log file.
     * @param e is the exception.
     */
    public static void printErrorToLog(Exception e) {
		// Get the local date and time.
		LocalDate currDate = LocalDate.now();
		LocalTime currTime = LocalTime.now();
		
		// Write to log file the error information.
		pw.println("!SESSION " + currDate + " " + currTime);
		pw.println("!MESSAGE " + e.getMessage());
		pw.println("!STACK TRACE");
		e.printStackTrace(pw);
		pw.println();
		pw.close();
		
		// Alert user to error.
		System.out.println("Something went wrong, please see log file at " + FILEPATH + "\\.log.");
    }
    
	
	/**
	 * Prints all categories in the following format
	 * One category        -- category1
	 * Two categories      -- category1 and category2
	 * Multiple categories -- category1, category2, category3, ..., categoryN-1 and categoryN
	 * without a new line character at the end.
	 * @param categories the categories which will be printed.
	 */
	public static void printCategories(String[] categories) {
		if (categories.length == 1) {
			System.out.print(categories[0]);
			
		} else if (categories.length == 2) {
			System.out.print(categories[0] + " and " + categories[1]);
			
		} else {
			for (int i = 0; i < categories.length - 2; i++) {
				System.out.print(categories[i] + ", ");
			}
			System.out.print(categories[categories.length - 2] + " and " 
					+ categories[categories.length - 1]);
		}
	}
}  

