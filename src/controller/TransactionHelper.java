package controller;

import java.util.Scanner;

/**
 * This helper class contains static methods used to manipulate information in the 
 * Transaction Tracker program.
 * @author L. James Davidson
 */
public class TransactionHelper {

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
}  

