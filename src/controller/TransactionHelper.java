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
    
	
	/** Returns the period of the day.
	 * @returns "morning" for [0, 12), "afternoon" for [12, 17), and "evening" for [17, 23]
	 */
	public static String getPeriodOfDay() {
		String periodOfDay;
		int hour = (LocalTime.now()).getHour();
		if (hour < 12) {
			// [0, 12)
			periodOfDay = "morning";
			
		} else if (hour < 17) {
			// [12, 17)
			periodOfDay = "afternoon";
			
		} else {
			// [17, 23]
			periodOfDay = "evening";
		}
		
		return periodOfDay;
	}
}  

