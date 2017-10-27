package controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Month;

import model.Transaction;
import model.Transaction.TransactionBuilder;

/**
 * This helper class contains valuable methods in order for the Expense to
 * work properly.
 * @author L. James Davidson
 */
public class ItemiserHelper {

	// This is the directory location where the expenses file will be created at.
	private static final String FILE_PREFIX = "C:\\Users\\littl\\OneDrive\\Documents\\" 
			 + "Transactions\\";
	
	private static final String HEADING = "Location,Amount,Date,Category,Memo";
	
	/**
	 * Prints the available commands for this program.
	 */
	public static void printCommands() {
		System.out.println('\n' + "Commands are:");
		System.out.println("'0' to get the avaliable commands.");
		System.out.println("'1' to enter a new deposit.");
		System.out.println("'2' to enter a new withdrawal.");
		System.out.println("'3' to see your transaction history.");
		System.out.println("'exit' to leave." + '\n');
	}

    /**
     * @return A true for yes and a false for no. A null value is returned if the question was 
     * not answer with a 'yes' or a 'no'.
     */
    public static Boolean yesNo(String response) {
    	if (response.equals("yes")) {
    		return true;
    	} else if (response.equals("no")) {
    		return false;
    	} else {
    		return null;
    	}
    }
    
	/**
	 * Prints out the information regarding a transaction.
	 * @param transfer is the TransactionBuilder object whose information will be printed.
	 */
	public static void printTransInfo(TransactionBuilder transfer) {
		System.out.print("Location: ");
		System.out.printf("%-20s", transfer.getLocation());
		System.out.println();
		
		System.out.print("Amount: ");
		System.out.printf("%+.2f", transfer.getAmount());
		System.out.println();
		
		System.out.print("Date: ");
		System.out.printf("%-10s", transfer.getDate().toString());
		System.out.println();
		
		System.out.print("Category: ");
		System.out.printf("%-20s", transfer.getCategory());
		System.out.println();
		
		System.out.print("Memo: ");
		System.out.printf("%-30s", transfer.getMemo());
		System.out.println();
	}

	/**
	 * Adds the transaction information to the file specified by the date of the transaction.
	 */
	public static void addToFile(Transaction transfer) {
		// Get the year and month of the transaction
		int year = transfer.getDate().getYear();
		Month[] months = Month.values();
		String month = months[transfer.getDate().getMonthValue()].toString();
		
		// See if the directory for the year the transaction was made exists.
		String directoryName = FILE_PREFIX + year;
		
		// Determine if the directory already exists.
		File yearDirectory = new File(directoryName);
		
		// If 
		if (!yearDirectory.isDirectory()) {
			if (!yearDirectory.mkdir()) {
				System.out.println("Could not add transaction to file. Please check the file path ");
				System.out.println(FILE_PREFIX);
				System.out.println(" to ensure that his path is valid.");
				return;
			}
		}
		
		File monthFile = new File(directoryName + "\\" + month + ".csv");
		boolean fileExists = monthFile.exists();
		
		// Attempt to make a PrintWriter on the file.
		PrintWriter pw;
		
		try {
			 pw = new PrintWriter(new FileWriter(monthFile, true), true);
		} catch (IOException e) {
			System.out.println("Could not add transaction to file. Please check the file path");
			System.out.println(monthFile.getAbsolutePath());
			System.out.println("to ensure that his path is valid.");
			return;
		}
		
		// If the file does not exist then add the heading to the CSV file.
		if (!fileExists) {
			pw.write(month + '\n' + HEADING + '\n');
		}
		
		pw.write(transfer.getLocation() + "," + Double.valueOf(transfer.getAmount()) + "," 
				+ transfer.getDate() + "," + transfer.getCategory() + "," 
				+ transfer.getMemo() + '\n');
		pw.close();
	}
}  
