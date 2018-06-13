package view;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import controller.Helper;
import model.Transaction;
import model.Transaction.TransactionBuilder;

/**
 * Helper class of TransactionsTrackerApp. Helps the application to create new valid transaction
 * objects from user information.
 * @author L. James Davidson
 */
abstract class NewTransaction {

	// Constants regarding the restrictions from the SQLite database tables.
	private static final int MAX_DESCR_CHARS = 20;
	private static final int MAX_MEMO_CHARS = 20;
	
	/**
	 * Creates a new transaction object using the user input.
	 * @param input is the scanner which reads user input.
	 * @param categoryNames The list of categories which are available to this user.
	 */
	protected static Transaction run(Scanner input, List<String> categoryNames) {
		
		TransactionBuilder transfer = new TransactionBuilder(getType(input));
		setDescription(input, transfer);
		setAmount(input, transfer);
		setDate(input, transfer);
		setCategory(input, transfer, categoryNames);
		setMemo(input, transfer);
		checkProperInfo(input, transfer, categoryNames);
		return transfer.build();
	}
	
	/**
	 * Asks user if this transaction was a deposit.
	 * @param input the scanner to read user input.
	 * @return True if this transaction was a deposit.
	 */
	private static boolean getType(Scanner input) {
		String question = "Was this transaction a deposit?";
		return Helper.yesNoQuestion(input, question);
	}

	/**
	 * Sets the description of the TransactionBuilder.
	 * @param input is the scanner used to get user input.
	 * @param transfer is the TransactionBuilder object whose description is being set.
	 */
	private static void setDescription(Scanner input, TransactionBuilder transfer) {
		String descr;
		while (true) {
			System.out.print('\n' + "What is the description for this transaction?" + '\n' + "> ");
			descr = input.nextLine();
			if (descr.length() <= MAX_DESCR_CHARS)
				break;
			System.out.println("The description must be less than " + MAX_DESCR_CHARS
					+ " characters long.");
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
				transfer.setAmountInCents((int) Math.round(Double.parseDouble(response) 
						* Helper.CENTS_IN_A_DOLLAR));
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
		if (Helper.yesNoQuestion(input, "Was the transaction made today?")) {
			transfer.setDate(LocalDate.now());
			return;
		}
		
		int[] dateInfo = new int[3];
		Helper.setYear(input, dateInfo, "What year was the transaction made?");
		Helper.setMonth(input, dateInfo, "What month was the transaction made?");
		Helper.setDay(input, dateInfo, "What day was the transaction made?");
		transfer.setDate(LocalDate.of(dateInfo[0], dateInfo[1], dateInfo[2]));
	}
	
	/**
	 * Sets the category of the TransactionBuilder.
	 * @param input is the scanner used to get user input.
	 * @param transfer is the TransactionBuilder object whose category is being set.
	 * @param categories THe list of categories available to this user.
	 */
	private static void setCategory(Scanner input, TransactionBuilder transfer, 
			List<String> categories) {
		if (transfer.isADeposit()) {
			transfer.setCategory("Deposit");
			return;
		}
		
		while (true) {
			System.out.print('\n' + "Avaliable categories are: ");
			
			// Print out all the categories with their corresponding number in front.
			for (int i = 0; i < categories.size() - 1; i++) {
				System.out.print(i + "-" + categories.get(i) + ", ");
			}
			System.out.println((categories.size() - 1) + "-" 
					+ categories.get(categories.size() - 1));
			
			System.out.print("(You can add more cateogries from the settings menu.) \n"
					+ "Please type the number corresponding to which category best fits"
					+ " this transaction." + '\n' + "> ");
			
			int catNum;
			try {
				catNum = Integer.parseInt(input.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("Invalid response. Please type a number.");
				continue;
			}
			
			if (catNum < 0 || catNum >= categories.size()) {
				System.out.println("Invalid response. Please type a number between 0 and " 
						+ (categories.size() - 1) + ".");
				continue;
			}
			
			try {
				transfer.setCategory(categories.get(catNum));
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
		
		while (true) {
			System.out.print('\n' + "Please type the memo which will be attached to this "
					+ "transaction." + '\n' + "> ");
			memo = input.nextLine();
			if (memo.length() <= MAX_MEMO_CHARS)
				break;
			System.out.println("The memo must be less than " + MAX_MEMO_CHARS
				+ " characters long.");
		}
		
		transfer.setMemo(memo);
	}
	
	/**
	 * Asks the user if all of the transaction information is correct.
	 * @param input is the scanner used to get user input.
	 * @param transfer is the TransactionBuilder object whose information is being double checked
	 * 		by the user.
	 * @param categories The list of categories which are available to this user.
	 * @modifies transfer if the user decides that they wish to edit some of the transaction 
	 * 		information.
	 */
	private static void checkProperInfo(Scanner input, TransactionBuilder transfer,
			List<String> categories) {
		System.out.println("The information of the transaction is as follows...");
		printTransInfo(transfer);		
		while (!Helper.yesNoQuestion(input, "Would you like to edit anything?")) {
			editTransInfo(input, transfer, categories);
		}
	}

	/**
	 * Allows the user to edit any of the transaction's information.
	 * @param input is the scanner to read user input.
	 * @param transfer is the TransactionBuilder whose information will be edited.
	 * @param categories List of categories available to this user.
	 * @modifies The transactions information where the user sees fit.
	 */
	private static void editTransInfo(Scanner input, TransactionBuilder transfer, 
			List<String> categories) {
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
					setCategory(input, transfer, categories);
					break;
					
				case 4:
					setMemo(input, transfer);
					break;
					
				case 5:
					transfer.setAsDeposit(getType(input));
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
		System.out.printf(Helper.amountInCentsToFormattedDouble(transfer.getAmountInCents()) );
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
