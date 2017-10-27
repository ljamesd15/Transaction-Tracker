package view;
/*  This program allows the user to input an deposits and withdrawals. These
can be printed to a file and their information saved.

PATCH NOTES
v 1.1.00  The user is now allowed to add a date corresponding to the expense in
      order to better keep track of the expenses. (09/20/16)

v 1.1.10  The user is now able to edit the information if there are any errors 
      before they are printed to the file. In addition invalid response 
      messages were added along with exception flags which are thrown when 
      there are information type mismatches. (10/01/2016)

v 1.1.20  Cleaned up code a bit. Re-did method head comments and in-line
      as well. Increased the overall readability of the program. Added an
      escape sequence as well if the user decided they didn't want to edit
      the purchase information. Improved the yesNo method. Made certain
      methods private. (10/02/2016)
    
v 1.1.21  Made the program match with boolean zen. Made method comments more
      readable. Added new responses to edit information and made it easier
      to move faster through it. Added another slot in the purchase 
      information storage for the price so it will no longer need to return
      price everywhere as I replaced all of it with a parse from a string 
      to double. The final statement of total price was redone. Allowed
      program to deal with invalid responses in a similar way.
      (10/12/2016)

v 1.1.30  Added categories option to further describe the expense created. 
      Fixed a few string bugs. Added the capability to edit the information
      multiple times in a row before finalizing and allowing the user to
      see the most updated version of the purchase information. Changed the
      way the purchase information is stored. Created a new class file for
      all supporting methods. (10/14/2016)
      
v 1.1.40 Separated files to create a main class which can do more than just
     create the expense objects and will be expanded upon later. 
     (10/15/2016)
     
v 1.1.41 Added a quit option to the opening. Edited getDate method and method 
     names to make more sense. (10/16/2016)
     
v 1.1.50 The information will now be printed to a file based on its month of
     purchase. If the file for that month of purchase does not exist, then
     one will be created and the information will be printed to it. 
     (10/20/2016)
     
v 1.2.00 Changed the way the expense data is stored by creating actual expense 
     objects. Combined the helper classes as inner classes within the 
     expense object class. (10/30/2016)
     
v 1.2.10 Fixed the auto-complete date if the purchase was today bug. Corrected
     the expense price to only accept positive numbers. Added the ability
     keep track of deposits as well. (11/4/2016)

v 1.2.20 Streamlined the ability to add both deposits and withdrawals so both
     transactions' information are stored in the same way and in the future
     if need be. (11/24/2016)
     
v 1.2.21 Changed the way a file is created. Added a toString and comparable to
 	 Transaction. Attempted to add a sorting feature for transactions still 
 	 needs debugging. (6/3/2017)
 	 
v 1.2.22 Moved files to their respective MVC positions. Began rewriting the view code so that
	it will optimize the transaction building process along with looking a lot cleaner. (10/24/17)
	
v 1.2.23 Added enums to Transaction, implemented most of view. (10/26/17)

TO DO LIST: 

- add tests

- print transaction info to different areas, change file prefix

- add this to a read me file

- edit all javadocs

- add switch statements

- Sort purchases made earlier in the month within the file

- show transaction history

- The program will chart the expenses as a bar or pie chart.

- The program will open with, what would you like to do?, with options of 
adding a new expense, adding a new category, or viewing expenses - current
month, previous month, chart form, or pie graph.

- GUI?
*/

import java.io.IOException;
import java.time.LocalDate;
import java.util.Scanner;

import controller.ItemiserHelper;
import model.Transaction;
import model.Transaction.CategoryTag;
import model.Transaction.TransactionBuilder;

/**
* This program allows the user to keep track of their finances is an easier 
* fashion.
* @author L. James Davidson
*/
public class Itemiser {

	/** 
	 * This may throw an IOExcpetion if the file attached to the writer, which
	 * prints the expense to a file, is invalid.
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
	    Scanner input = new Scanner(System.in);
	    intro(input);
	}
	
	/**
	 * This will get information regarding what the user wants to do during 
	 * this session.
	 * 
	 * @param input is the scanner that is hooked up to user input.
	 * @throws IOException
	 */
	public static void intro(Scanner input) throws IOException {
		String response;
	    
	    System.out.println("Welcome to your personal itemiser!");
	    System.out.println("Type '0' to get a list of commands.");
	    System.out.print("What would you like to do? ");

	    
	    while (true) {
		    response = input.nextLine();
		    
	    	if (response.equals("0")) {
	    		ItemiserHelper.printCommands();
	    	} else if (response.equals("1")) {
	    		createTransaction(input, true);
	    	} else if (response.equals("2")) {
	    		createTransaction(input, false);
	    	} else if (response.equals("3")) {
	    		// Show transaction history
	    		System.out.println("This option has not yet been implemented.");
	    		System.out.println("Please pick a different option.");
	    	} else if (response.equals("exit")) {
	    		System.out.println('\n' + "Thank you for tracking your transactions with Itemiser!");
	    		System.out.println("Goodbye.");
	    		System.exit(1);
	    	} else {
	    		System.out.println("Unrecognised command : " + response);
				System.out.println("Type '0' to see a list of "
						+ "valid commands.");
	    	}
		    System.out.print('\n' + "What would you like to do? ");
		    System.out.print('\n');
	    }
	}
	

	
	private static void createTransaction(Scanner input, boolean isWithdrawal) {		
		TransactionBuilder transfer = new TransactionBuilder();
		transfer.setAsWithdrawal(isWithdrawal);
		
		// Get the location of the TransactionBuilder.
		setLocation(input, transfer);
		
		// Get the amount of the TransactionBuilder.
		setAmount(input, transfer);
		
		// Get the date of the TransactionBuilder.
		setDate(input, transfer);
		
		
		// Get the category of the TransactionBuilder.
		setCategory(input, transfer);
		
		// Get the memo for the TransactionBuilder.
		setMemo(input, transfer);
		
		// Ask user if all the information is set properly.
		checkProperInfo(input, transfer);
		
		// Build Transaction.
		Transaction trans = transfer.build();
		
		// Add transaction to file.
		ItemiserHelper.addToFile(trans);
		
	}
	
	/**
	 * Sets the location of the TransactionBuilder.
	 * @param input is the scanner used to get user input.
	 * @param transfer is the TransactionBuilder object whose location is being set.
	 */
	private static void setLocation(Scanner input, TransactionBuilder transfer) {
		System.out.println('\n' + "Where was the transaction made at?");
		transfer.setLocation(input.nextLine());
	}
	
	/**
	 * Sets the amount of the TransactionBuilder.
	 * @param input is the scanner used to get user input.
	 * @param transfer is the TransactionBuilder object whose amount is being set.
	 */
	private static void setAmount(Scanner input, TransactionBuilder transfer) {
		String response;
		
		while (true) {
			System.out.println('\n' + "What was the amount of the transaction?");
			response = input.nextLine();
			try {
				transfer.setAmount(Double.parseDouble(response));
				break;
				
			} catch (NumberFormatException e) {
				System.out.println("Invalid response. Please type a number.");
			}
		}
	}
	
	/**
	 * Sets the date of the TransactionBuilder.
	 * @param input is the scanner used to get user input.
	 * @param transfer is the TransactionBuilder object whose date is being set.
	 */
	private static void setDate(Scanner input, TransactionBuilder transfer) {
		LocalDate date = null;
		
		while (true) {
			System.out.println('\n' + "Was the transaction made today?");
			Boolean answer = ItemiserHelper.yesNo(input.nextLine());
			if (answer != null) {
				if (answer.booleanValue()) {
					transfer.setDate(LocalDate.now());
					return;
				}
				break;
				
			} else {
				System.out.println("Invalid response. Please type 'yes' or 'no'");
			}
		}
		
		// If transaction was not made today then get the day the transaction was made.
		LocalDate today = LocalDate.now();
		int year;
		int month;
		int day;
		
		// Ask if the current transaction occurred this year.
		while (true) {
			System.out.println('\n' + "Was the transaction made this year?");
			Boolean answer = ItemiserHelper.yesNo(input.nextLine());
			if (answer != null) {
				if (answer.booleanValue()) {
					year = today.getYear();
				}
				break;
				
			} else {
				System.out.println("Invalid response. Please type 'yes' or 'no'");
			}
		}
		
		// Get the year of the transaction.
		while (true) {
			System.out.println('\n' + "What year was the transaction made?");
			int tempYear;
			
			// Ensure that the response is an integer.
			try {
				tempYear = Integer.parseInt(input.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("Invalid response. Please type an integer.");
				continue;
			}
			
			// Ensure that the year is equal to or before the current year.
			if (tempYear > today.getYear()) {
				System.out.println("Invalid response. Please type a current or past year.");
				continue;
			}
			
			year = tempYear;
			break;
		}
		
		// Ask if this transaction occurred this month.
		while (true) {
			System.out.println("Was the transaction made this month?");
			Boolean answer = ItemiserHelper.yesNo(input.nextLine());
			if (answer != null) {
				if (answer.booleanValue()) {
					month = today.getMonthValue();
				}
				break;
			} else {
				System.out.println("Invalid response. Please type 'yes' or 'no'");
			}
		}
		
		// Get the month of the transaction.
		while (true) {
			System.out.println('\n' + "What month was the transaction made?");
			int tempMonth;
			
			// Ensure that the response is an integer.
			try {
				tempMonth = Integer.parseInt(input.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("Invalid response. Please type an integer.");
				continue;
			}
			
			// Ensure the integer is a valid month integer.
			if (tempMonth < 1 || tempMonth > 12) {
				System.out.println("Invalid response. Please type an integer between 1 and 12.");
				continue;
			}
			
			// Ensure that the month is equal to or before the current month of this year.
			if (year == today.getYear() && tempMonth > today.getMonthValue()) {
				System.out.println("Invalid response. Please type a current or past month of "
						+ "this year.");
				continue;

			}
			
			month = tempMonth;
			break;
		}
		
		// Get the day of the transaction.
		
		// Getting the maximum number of days of the month which the transaction took place.
		int maxDay = LocalDate.of(year, month, 1).lengthOfMonth();
		
		while (true) {
			System.out.println('\n' + "What day was the transaction made?");
			int tempDay;
			
			// Ensure that the response is an integer.
			try {
				tempDay = Integer.parseInt(input.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("Invalid response. Please type an integer.");
				continue;
			}
			
			// Ensure the integer is a valid day.
			if (tempDay < 1 || tempDay > maxDay) {
				System.out.println("Invalid response. Please type an integer between 1 and " 
							+ maxDay + ".");
				continue;
			}
			
			// Ensure that the month is equal to or before the current month of this year.
			if (year == today.getYear() && month == today.getMonthValue() 
					&& tempDay > today.getDayOfMonth()) {
				
				System.out.println("Invalid response. Please type a current or past day of this"
						+ " year.");
				continue;

			}
		
			day = tempDay;
			break;
		}
		
		// Set date to the information given by the user.
		date = LocalDate.of(year, month, day);
		transfer.setDate(date);
	}
	
	/**
	 * Sets the type of the TransactionBuilder (deposit or withdrawal).
	 * @param input is the scanner used to get user input.
	 * @param transfer is the TransactionBuilder object whose type is being set.
	 */
	private static void setTransType(Scanner input, TransactionBuilder transfer) {
		transfer.setAsWithdrawal(!transfer.isWithdrawal());
		
		if (!transfer.isWithdrawal()) {
			// This is now a deposit so set the category as such.
			transfer.setCategory(CategoryTag.DEPOSIT);
			return;
		}
		
		System.out.println('\n' + "This transaction is now a withdrawal.");
		System.out.println("Please edit the category to an appropriate type.");
		setCategory(input, transfer);
	}
	
	/**
	 * Sets the category of the TransactionBuilder.
	 * @param input is the scanner used to get user input.
	 * @param transfer is the TransactionBuilder object whose category is being set.
	 */
	private static void setCategory(Scanner input, TransactionBuilder transfer) {
		// If this is a deposit then add the tag as deposit and return.
		if (!transfer.isWithdrawal()) {
			transfer.setCategory(CategoryTag.DEPOSIT);
			return;
		}
		
		System.out.println('\n' + "This program only supports specific tags. Please choose the tags which"
				+ " best describe this transaction.");
		
		while (true) {
			System.out.print("Avaliable tags are: ");
			
			CategoryTag[] tagList = Transaction.values();
			for (int i = 0; i < tagList.length - 1; i++) {
				System.out.print(i + "-" + tagList[i] + ", ");
			}
			System.out.println((tagList.length - 1) + "-" + tagList[tagList.length - 1] + ".");
			
			System.out.println("Please type the number corresponding to which tag best fits this "
					+ "transaction.");
			int catTagNum;
			
			try {
				catTagNum = Integer.parseInt(input.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("Invalid response. Please type a number.");
				continue;
			}
			

				
			try {
				if (catTagNum == 0) {
					transfer.setCategory(CategoryTag.FOOD);
				} else if (catTagNum == 1) {
					transfer.setCategory(CategoryTag.HOUSING);
				} else if (catTagNum == 2) {
					transfer.setCategory(CategoryTag.SCHOOL);
				} else if (catTagNum == 3) {
					transfer.setCategory(CategoryTag.TRANSPORTATION);
				} else if (catTagNum == 4) {
					// Since this is not a deposit it cannot have a category tag of deposit.
					throw new IllegalArgumentException('\n' + "This transaction is not a deposit "
							+ "and therefore cannot have a category tag of deposit.");
				} else {
					// If the category tag number is not tied to a specific category alert user.
					System.out.println("Invalid response. Please type a valid number.");
					continue;
				}
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
				continue;
			}

			break;
		}
	}
	
	/**
	 * Sets the memo of the TransactionBuilder.
	 * @param input is the scanner used to get user input.
	 * @param transfer is the TransactionBuilder object whose memo is being set.
	 */
	private static void setMemo(Scanner input, TransactionBuilder transfer) {
		while (true) {
			System.out.println('\n' + "Would you like to add a memo to this transaction?");
			Boolean answer = ItemiserHelper.yesNo(input.nextLine());
			
			if (answer != null) {
				String memo = "";
				if (answer.booleanValue()) {
					System.out.println("Please type the memo which will be attached to this "
							+ "transaction.");
					memo = input.nextLine();
				}
				transfer.setMemo(memo);
				break;
			} else {
				System.out.println("Invalid response. Please type 'yes' or 'no'");
			}
		}
	}
	

	
	/**
	 * Checks with the user that all the information of the transaction is correct.
	 * @param input is the scanner used to get user input.
	 * @param transfer is the TransactionBuilder object whose information is being double checked
	 * and possible edited by the user.
	 */
	private static void checkProperInfo(Scanner input, TransactionBuilder transfer) {
		while (true) {
			System.out.println("The information of the transaction is as follows...");
			ItemiserHelper.printTransInfo(transfer);
			
			System.out.println('\n' + "Is this information correct?");
			Boolean answer = ItemiserHelper.yesNo(input.nextLine());
			if (answer != null) {
				if (!answer.booleanValue()) {
					editTransInfo(input, transfer);
				}
				break;
			} else {
				System.out.println("Invalid response. Please type 'yes' or 'no'");
			}
		}
	}

	

	
	/**
	 * Allows the user to edit any of the transaction's information.
	 * @param input is the scanner to read user input.
	 * @param transfer is the TransactionBuilder whose information will be edited.
	 */
	private static void editTransInfo(Scanner input, TransactionBuilder transfer) {
		while (true) {
			System.out.println("Type '0' to edit the location.");
			System.out.println("Type '1' to edit the amount.");
			System.out.println("Type '2' to edit the date.");
			System.out.println("Type '3' to edit the category.");
			System.out.println("Type '4' to edit the transaction type (change from withdrawal to "
					+ "deposit or vice versa).");
			System.out.println("Type '5' to edit the memo.");
			
			System.out.println('\n' + "What would you like to edit?");
			int response;
			try {
				response = Integer.parseInt(input.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("Invalid response. Please type a number.");
				continue;
			}
			
			if (response == 0) {
				setLocation(input, transfer);
			} else if (response == 1) {
				setAmount(input, transfer);
			} else if (response == 2) {
				setDate(input, transfer);
			} else if (response == 3) {
				setCategory(input, transfer);
			} else if (response == 4) {
				setTransType(input, transfer);
			} else if (response == 5) {
				setMemo(input, transfer);
			} else {
				System.out.println("Invalid response please typpe a number 0-5.");
			}
			
			System.out.println("The updated transaction information is as follows...");
			ItemiserHelper.printTransInfo(transfer);
			
			while(true) {
				System.out.println('\n' + "Would you like to edit anything else?");
				Boolean answer = ItemiserHelper.yesNo(input.nextLine());
				if (answer != null) {
					if (answer.booleanValue()) {
						break;
					} else {
						return;
					}
				} else {
					System.out.println("Invalid response. Please type 'yes' or 'no'");
				}
			}
		}
	}
	

}