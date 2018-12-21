package view;

import java.time.LocalDate;
import java.util.Scanner;

import controller.TransactionHelper;
import model.Transaction;
import model.Transaction.TransactionBuilder;

/**
 * Helper class of TransactionsTrackerApp. Helps the application to create new valid transaction
 * objects from user information.
 * @author L. James Davidson
 */
abstract class CreateNewTransaction {

	// Constants regarding the restrictions from the SQLite database tables.
	private static final int MAX_DESCR_CHARS = 20;
	private static final int MAX_MEMO_CHARS = 20;
	
	// An array of strings containing the default user expense categories.
	private static String[] categories;
	
	/**
	 * Creates a new transaction object using the user input.
	 * @param input is the scanner which reads user input.
	 */
	protected static Transaction run(Scanner input, String[] categoryNames) {
		
		// Get the type of the transaction
		boolean isDeposit = getType(input);
		
		TransactionBuilder transfer = new TransactionBuilder(isDeposit);
		categories = categoryNames;
		
		// Get the description of the TransactionBuilder.
		setDescription(input, transfer);
		
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
		return transfer.build();
	}
	
	/**
	 * Asks user if this transaction was a deposit.
	 * @param input the scanner to read user input.
	 * @return True if this transaction was a deposit.
	 */
	private static boolean getType(Scanner input) {
		String question = "Was this transaction a deposit?";
		return TransactionHelper.yesNoQuestion(input, question);
	}

	/**
	 * Sets the description of the TransactionBuilder.
	 * @param input is the scanner used to get user input.
	 * @param transfer is the TransactionBuilder object whose description is being set.
	 */
	private static void setDescription(Scanner input, TransactionBuilder transfer) {
		String descr;
		while (true) {
			// Ask user the description of the transaction
			System.out.print('\n' + "What is the description for this transaction?" + '\n' + "> ");
			descr = input.nextLine();
			
			// Check if description is too long.
			if (descr.length() > MAX_DESCR_CHARS) {
				System.out.println("The description must be less than " + MAX_DESCR_CHARS
						+ " characters long.");
				continue;
			}
			
			break;
		}
		
		transfer.setDescription(descr);
	}
	
	/**
	 * Sets the amount of the TransactionBuilder.
	 * @param input is the scanner used to get user input.
	 * @param transfer is the TransactionBuilder object whose amount is being set.
	 */
	private static void setAmount(Scanner input, TransactionBuilder transfer) {
		String response;
		
		while (true) {
			System.out.print('\n' + "What was the amount of the transaction?" + '\n' + "> ");
			response = input.nextLine();
			try {
				transfer.setAmountInCents((int) Math.round(Double.parseDouble(response) * 100));
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
		// If the transaction occurred today then set the transaction information
		if (TransactionHelper.yesNoQuestion(input, "Was the transaction made today?")) {
			transfer.setDate(LocalDate.now());
			return;
		}
		
		// If transaction was not made today then get the day the transaction was made.
		int[] dateInfo = new int[3];
		TransactionHelper.setYear(input, dateInfo, "What year was the transaction made?");
		TransactionHelper.setMonth(input, dateInfo, "What month was the transaction made?");
		TransactionHelper.setDay(input, dateInfo, "What day was the transaction made?");
		transfer.setDate(LocalDate.of(dateInfo[0], dateInfo[1], dateInfo[2]));
	}
	
	/**
	 * Sets the category of the TransactionBuilder.
	 * @param input is the scanner used to get user input.
	 * @param transfer is the TransactionBuilder object whose category is being set.
	 */
	private static void setCategory(Scanner input, TransactionBuilder transfer) {
		if (transfer.isADeposit()) {
			transfer.setCategory("Deposit");
			return;
		}
		
		while (true) {
			System.out.print('\n' + "Avaliable categories are: ");
			
			// Print out all the categories with their corresponding number in front. 
			// Fence post with comma
			for (int i = 0; i < categories.length - 1; i++) {
				System.out.print(i + "-" + categories[i] + ", ");
			}
			System.out.println((categories.length - 1) + "-" 
					+ categories[categories.length - 1]);
			
			System.out.print("(You can add more cateogries from the settings menu.) \n"
					+ "Please type the number corresponding to which category best fits"
					+ " this transaction." + '\n' + "> ");
			int catNum;
			
			// Attempt to parse an integer from the user input.
			try {
				catNum = Integer.parseInt(input.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("Invalid response. Please type a number.");
				continue;
			}
			
			// If number is invalid then inform user and try again.
			if (catNum < 0 || catNum >= categories.length) {
				System.out.println("Invalid response. Please type a number between 0 and " 
						+ (categories.length - 1) + ".");
				continue;
			}
			
			// Attempt to set the category of the expense.
			try {
				transfer.setCategory(categories[catNum]);
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
		String memo;
		
		// Ask user for the memo of this transaction
		while (true) {
			System.out.print('\n' + "Please type the memo which will be attached to this "
					+ "transaction." + '\n' + "> ");
			memo = input.nextLine();
			
			if (memo.length() > MAX_MEMO_CHARS) {
				System.out.println("The memo must be less than " + MAX_MEMO_CHARS
						+ " characters long.");
				continue;
			}
			
			break;
		}
		
		transfer.setMemo(memo);
	}
	
	/**
	 * Asks the user if all of the transaction information is correct.
	 * @param input is the scanner used to get user input.
	 * @param transfer is the TransactionBuilder object whose information is being double checked
	 * 		by the user.
	 * @modifies transfer if the user decides that they wish to edit some of the transaction 
	 * 		information.
	 */
	private static void checkProperInfo(Scanner input, TransactionBuilder transfer) {
		System.out.println("The information of the transaction is as follows...");
		printTransInfo(transfer);
		boolean answer;
		
		while (true) {	
			String question = "Would you like to edit anything?";
			answer = TransactionHelper.yesNoQuestion(input, question);
			
			// If the information is not correct then have the user edit the information.
			if (answer) {
				editTransInfo(input, transfer);
			} else {
				break;
			}
		}
	}

	/**
	 * Allows the user to edit any of the transaction's information.
	 * @param input is the scanner to read user input.
	 * @param transfer is the TransactionBuilder whose information will be edited.
	 * @modifies The transactions information where the user sees fit.
	 */
	private static void editTransInfo(Scanner input, TransactionBuilder transfer) {
		System.out.println('\n' + "Type '0' to edit the description.");
		System.out.println("Type '1' to edit the amount.");
		System.out.println("Type '2' to edit the date.");
		System.out.println("Type '3' to edit the category.");
		System.out.println("Type '4' to edit the memo.");
		System.out.println("Type '5' to change if this transaction was a deposit.");
		
		while (true) {
			System.out.print('\n' + "What would you like to edit?" + '\n' + "> ");
			int response;
			try {
				response = Integer.parseInt(input.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("Invalid response. Please type a number.");
				continue;
			}
			
			switch (response) {
				case 0: 
					setDescription(input, transfer);
					break;
					
				case 1:
					setAmount(input, transfer);
					break;
					
				case 2:
					setDate(input, transfer);
					break;
					
				case 3:
					setCategory(input, transfer);
					break;
					
				case 4:
					setMemo(input, transfer);
					break;
					
				case 5:
					transfer.setAsDeposit(getType(input));
					// Reset the amount of the transaction because the type may have changed.
					transfer.setAmountInCents(transfer.getAmountInCents());
					break;
					
				default:
					System.out.println("Invalid response please type a number 0-5.");
					continue;
					
			}
			
			break;
		}

		System.out.println("The updated transaction information is as follows...");
		printTransInfo(transfer);
	}
	
	/**
	 * Prints out the information regarding a transaction.
	 * @param transfer is the TransactionBuilder object whose information will be printed.
	 */
	public static void printTransInfo(TransactionBuilder transfer) {
		System.out.print("Location:  ");
		System.out.printf("%-20s", transfer.getDescription());
		System.out.println();
		
		System.out.print("Amount:    $");
		System.out.printf("%+.2f", transfer.getAmountInCents() / 100.0);
		System.out.println();
		
		System.out.print("Date:      ");
		System.out.printf("%-10s", transfer.getDate().toString());
		System.out.println();
		
		System.out.print("Category:  ");
		System.out.printf("%-20s", transfer.getCategory());
		System.out.println();
		
		System.out.print("Memo:      ");
		System.out.printf("%-30s", transfer.getMemo());
		System.out.println();
	}
}
