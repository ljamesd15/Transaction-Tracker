package view;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import controller.Helper;
import controller.DB;
import model.Transaction;
import model.Transaction.TransactionBuilder;
import model.User;

public class ShowHistory {

	/** Attributes in TransactionsDB used to store user transactions. */
	private static final String ATTRIBUTE_OPTIONS = "Avaliable options are \n1. Description\n2. Date\n"
		+ "3. Price\n4. Category\n5. Memo";
	
	/** Attributes of transactions will be shown to the user. */
	private static final String[] PRINTED_TRANS_ATTRIB = {"Description", "Amount", 
			"Date", "Category", "Memo"};
	
	private static final int CHARS_IN_DATE = 10;
	private static final int PADDING = 3;
	
	/**
	 * Main function of ShowHistory. Asks users how they would like to see their transactions
	 * history. Allows for particular ordering (up to 2), and filtering of past transactions.
	 * Also allows a user to ask for a breakdown of each group with the transactions totals from 
	 * each specified grouping.
	 * @param input the scanner to read user input.
	 * @param db The database which will be queried to find the user transactions.
	 * @param user The user whose transactions this will display.
	 */
	public static void run(Scanner input, DB db, User user) {
		
		// Ask user regarding ordering of output transactions.
		String[] orderByClause = new String[DB.T_TAB_ATTRIBS.length];
		int numOfOrderingAttributes = 0;
		boolean addAnother;
		do {
			addToOrderBy(input, orderByClause, numOfOrderingAttributes++);
			addAnother = Helper.yesNoQuestion(input, "Would you like to order your history by "
					+ "another transactions' attribute?");
			
		} while (addAnother && numOfOrderingAttributes < DB.T_TAB_ATTRIBS.length);
		
		// Ask user regarding filtering of transactions which will be output.	
		List<String> whereClauses = new ArrayList<String>();
		
		boolean filterTrans = Helper.yesNoQuestion(input, "Would you like to filter your "
				+ "transactions? \nNote: Any filters applied to the same attributes will "
				+ "allow transactions which fit any of the criteria for that attribute.");
		if (filterTrans) {
			do {
				addToWhere(input, db, user, whereClauses);
				filterTrans = Helper.yesNoQuestion(input, "Would you like to further filter "
						+ "your transactions?");
			} while (filterTrans);
		}

		String stmt = db.prepareHistoryQuery(user, orderByClause, whereClauses);
		outputResults(db, stmt);
	}
	
	/**
	 * Outputs the user's transaction history.
	 * @param db THe database which holds the user's transaction history.
	 * @param query The SQL statement to execute regarding the user's transaction history.
	 */
	private static void outputResults(DB db, String query) {
		// Send query to db to be executed
		ResultSet results = db.executeQuery(query);
		
		try {
			if (!results.next()) {
				System.out.println("No results found.");
				return;
			}
			
			List<Transaction> output = new ArrayList<Transaction>();
			TransactionBuilder trans;
			int price;
			
			do {
				price = results.getInt("price_in_cents");
				trans = new TransactionBuilder(price > 0);
				
				trans.setAmountInCents(price);
				trans.setDescription(results.getString("description"));
				trans.setDate(LocalDate.parse(results.getString("day")));
				trans.setCategory(results.getString("catName"));
				trans.setMemo(results.getString("memo"));
				output.add(trans.build());
			} while (results.next());
			
			printInfo(output);
			
		} catch (SQLException e) {
			Helper.printErrorToLog(e);
		}
	}
	
	/**
	 * Outputs transactions to the user with a header.
	 * @param output The transactions to be printed.
	 */
	private static void printInfo(List<Transaction> output) {
		// Find length of longest output for each attribute.
		int maxDescriptLen = PRINTED_TRANS_ATTRIB[0].length() + PADDING;
		int maxPriceLen = PRINTED_TRANS_ATTRIB[1].length() + PADDING;
		int maxDateLen = CHARS_IN_DATE + PADDING;
		int maxCatLen = PRINTED_TRANS_ATTRIB[3].length() + PADDING;
		int maxMemoLen = PRINTED_TRANS_ATTRIB[4].length() + PADDING;
		
		for (int i = 0; i < output.size(); i++) {
			Transaction t = output.get(i);
			maxDescriptLen = Math.max(maxDescriptLen, t.getDescription().length() + PADDING);
			maxPriceLen = Math.max(maxPriceLen, 
					String.valueOf(t.getAmountInCents()).length() + PADDING + 1); // +1 for decimal
			maxCatLen = Math.max(maxCatLen, t.getCategory().length() + PADDING);
			maxMemoLen = Math.max(maxMemoLen, t.getMemo().length() + PADDING);
		}
		
		// Print the heading of the transactions.
		System.out.print("\n");
		System.out.printf("%-" + maxDescriptLen + "s %-" + maxPriceLen + "s %-" + maxDateLen
				+ "s %-" + maxCatLen + "s %-" + maxMemoLen + "s", 
				PRINTED_TRANS_ATTRIB[0], PRINTED_TRANS_ATTRIB[1], PRINTED_TRANS_ATTRIB[2], 
				PRINTED_TRANS_ATTRIB[3], PRINTED_TRANS_ATTRIB[4]);
		System.out.print("\n");
		for (int i = 0; i < maxDescriptLen; i++) {
			System.out.print("-");
		}
		System.out.print(" ");
		for (int i = 0; i < maxPriceLen; i++) {
			System.out.print("-");
		}
		System.out.print(" ");
		for (int i = 0; i < maxDateLen; i++) {
			System.out.print("-");
		}
		System.out.print(" ");
		for (int i = 0; i < maxCatLen; i++) {
			System.out.print("-");
		}
		System.out.print(" ");
		for (int i = 0; i < maxMemoLen; i++) {
			System.out.print("-");
		}
		System.out.print("\n");

		// Print out the transactions
		for (int i = 0; i < output.size(); i++) {
			Transaction t = output.get(i);
			String price = Helper.amountInCentsToFormattedDouble(t.getAmountInCents());
			System.out.printf("%-" + maxDescriptLen + "s %-" + maxPriceLen + "s %-" + maxDateLen
					+ "s %-" + maxCatLen + "s %-" + maxMemoLen + "s\n", 
					t.getDescription(), price, t.getDate().toString(), 
					t.getCategory(), t.getMemo());
		}
	}
	
	/**
	 * Adds an attribute to order the transactions from a SQL query.
	 * @param input the scanner to read user input.
	 * @param orderByClause the array of attributes the users transactions will be ordered by.
	 * @param orderPriority The priority of the ordering of transactions by the attribute which the
	 * user will choose.
	 */
	private static void addToOrderBy(Scanner input, String[] orderByClause, int orderPriority) {
		int answer = Helper.numberResponse(input, "What would you like to order your transactions by?\n" 
				+ ATTRIBUTE_OPTIONS, 1, DB.T_TAB_ATTRIBS.length);
		switch (answer) {
			case 1: // Description
				orderByClause[orderPriority] = DB.T_TAB_ABBREV + ".description";
				break;
			case 2: // Date
				orderByClause[orderPriority] = DB.T_TAB_ABBREV + ".day";
				break;
			case 3: // Price
				orderByClause[orderPriority] = DB.T_TAB_ABBREV + ".price_in_cents";
				break;
			case 4: // Category
				orderByClause[orderPriority] = DB.C_TAB_ABBREV + ".catName";
				break;
			case 5: // Memo
				orderByClause[orderPriority] = DB.T_TAB_ABBREV + ".memo";
				break;
		}
	}
	
	/**
	 * Asks and records to the whereClause the filtering attributes which will be applied to the 
	 * user's transactions history.
	 * @param input The scanner used to read user input.
	 * @param user The user whose transactions are being searched.
	 * @param whereClause The list of filtering statements which will be applied to the user's 
	 * transaction history.
	 */
	private static void addToWhere(Scanner input, DB db, User user, List<String> whereClause) {
		System.out.println("Which attributes would you like to filter your transactions with?");
		int answer = Helper.numberResponse(input, ATTRIBUTE_OPTIONS, 1, 
				DB.T_TAB_ATTRIBS.length);
		switch (answer) {
			case 1: // Description
				filterByDescription(input, whereClause);
				break;
			case 2: // Date
				filterByDate(input, whereClause);
				break;
			case 3: // Price
				filterByAmount(input, whereClause);
				break;
			case 4: // Category
				filterByCategory(input, db, user, whereClause);
				break;
			case 5: // Memo
				filterByMemo(input, whereClause);
				break;
		}
	}
	
	/**
	 * Determines how the user would like to filter by transaction's description.
	 * @param input The scanner used to read user input.
	 * @param whereClause The list of where clauses to filter the user's transactions.
	 */
	private static void filterByDescription(Scanner input, List<String> whereClause) {
		System.out.print("Which description would you like to include in your filter?\n> ");
		whereClause.add(DB.T_TAB_ABBREV + ".description = '" + input.nextLine() + "'");
	}
	
	/**
	 * Determines how the user would like to filter by transaction's date.
	 * @param input The scanner used to read user input.
	 * @param whereClause The list of where clauses to filter the user's transactions.
	 */
	private static void filterByDate(Scanner input, List<String> whereClause) {
		LocalDate from, to;
		int[] fromDateInfo = new int[3];
		int[] toDateInfo = new int[3];
		
		while (true) {
			Helper.setYear(input, fromDateInfo, "What is the starting year of your date filter?");
			Helper.setMonth(input, fromDateInfo, "What is the starting month of your date filter?");
			Helper.setDay(input, fromDateInfo, "What is the starting day of your date filter?");
			from = LocalDate.of(fromDateInfo[0], fromDateInfo[1], fromDateInfo[2]);
			
			Helper.setYear(input, toDateInfo, "What is the ending year of your date filter?");
			Helper.setMonth(input, toDateInfo, "What is the ending month of your date filter?");
			Helper.setDay(input, toDateInfo, "What is the ending day of your date filter?");
			to = LocalDate.of(toDateInfo[0], toDateInfo[1], toDateInfo[2]);
			
			if (from.isAfter(to)) {
				System.out.println("Your starting date is before your ending date.");
				continue;
			}
			
			System.out.println("Transactions from " + from.toString() + " till " + to.toString());
			boolean answer = Helper.yesNoQuestion(input, "Is this correct?");
			if (answer)
				break;
		}
		
		whereClause.add(DB.T_TAB_ABBREV + ".day >='" + from.toString() + "' AND " 
				+ DB.T_TAB_ABBREV + ".day <='" + to.toString() + "'");
	}
	
	/**
	 * Determines how the user would like to filter by transaction's amount.
	 * @param input The scanner used to read user input.
	 * @param whereClause The list of where clauses to filter the user's transactions.
	 */
	private static void filterByAmount(Scanner input, List<String> whereClause) {
		int upper = -1;
		int lower = 0;
		
		do {
			upper = Helper.numberResponse(input, "What would you like the upper "
					+ "price bound to be?", Integer.MIN_VALUE / Helper.CENTS_IN_A_DOLLAR, 
					Integer.MAX_VALUE / Helper.CENTS_IN_A_DOLLAR);
			
			lower = Helper.numberResponse(input, "What would you like the lower "
					+ "price bound to be?", Integer.MIN_VALUE / Helper.CENTS_IN_A_DOLLAR, 
					Integer.MAX_VALUE / Helper.CENTS_IN_A_DOLLAR);
			
			if (upper < lower)
				System.out.println("Your upper bound is below your lower bound.");
		} while (upper < lower);
		
		whereClause.add(DB.T_TAB_ABBREV + ".price_in_cents >= '" 
				+ lower * Helper.CENTS_IN_A_DOLLAR	+ "' AND " 
				+ DB.T_TAB_ABBREV + ".price_in_cents <= '" 
				+ upper * Helper.CENTS_IN_A_DOLLAR + "'");
	}
	
	/**
	 * Determines how the user would like to filter by transaction's category.
	 * @param input The scanner used to read user input.
	 * @param db The TransactionsDB which holds this user's transactions.
	 * @param user The user whose transactions are being searched.
	 * @param whereClause The list of where clauses to filter the user's transactions.
	 */
	private static void filterByCategory(Scanner input, DB db, User user, 
			List<String> whereClause) {
		System.out.println("\nWhich category would you like to include in your filter?");
		String category;
		do {
			System.out.println("Avaliable categories are ");
			db.printCategories(user);
			System.out.print("> ");
			category = input.nextLine();
			category = db.isACategory(user, category);
			if (category == null)
				System.out.println("Invalid cateogry choice.");
		} while (category == null);

		whereClause.add(DB.T_TAB_ABBREV + ".user_cat = '" + user.getUsername() + "_" + category + "'");
	}
	
	/**
	 * Determines how the user would like to filter by transaction's memo.
	 * @param input The scanner used to read user input.
	 * @param whereClause The list of where clauses to filter the user's transactions.
	 */
	private static void filterByMemo(Scanner input, List<String> whereClause) {
		System.out.print("Which memo would you like to include in your filter?\n> ");
		whereClause.add(DB.T_TAB_ABBREV + ".memo = '" + input.nextLine() + "'");
	}
}