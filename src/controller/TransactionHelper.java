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

	// File path to log file.
	private static final String LOG_FILEPATH = "C:\\Users\\ljame\\git\\Transaction-Tracker\\.log";
	
	// Log file
	private static File LOG = new File(LOG_FILEPATH);
	
	// Log file writer
	private static PrintWriter pw;
	
	/** Prepares the log file for any errors which might occur. */
	public static void prepare() {
		try {
			pw = new PrintWriter(new FileWriter(LOG, true), true);
		} catch (IOException e) {
			System.out.println("Could not set up connection to log file.");
			System.exit(0);
		}
		
	}
	
    /**
     * Asks the user a yes or no question.
     * @param input is the scanner used to read user input.
     * @param question is the true-false question which the user will be prompted with.
     * @return A true for yes and a false for no.
     */
    public static boolean yesNo(Scanner input, String question) {
    	String response;
    	
		while (true) {
			System.out.print('\n' + question + '\n' + "> ");
			response = input.nextLine();
			
	    	if (response.equals("yes")) {
	    		return true;
	    	} else if (response.equals("no")) {
	    		return false;
	    	} else {
	    		System.out.println("Invalid response. Please type 'yes' or 'no'");
	    	}
		}
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
		System.out.println("Something went wrong, please see log file at " + LOG_FILEPATH + ".");
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

