package view;

import java.sql.SQLException;
import java.util.Scanner;

import controller.TransactionsDB;
import model.Transaction;
import model.User;

/**
 * Text based interface for clients to query and update the database in order to log in, add 
 * transactions, breakdown expenses via date, price, categories, and more.
 * @author L. James Davidson
 */
public class TransactionsTrackerApp {
	
	// An array of strings containing the default user expense categories.
	private final String[] defaultCategories = new String[]
			{"Deposit", "Food", "Housing", "Transportation", "Misc."};
	
	// The Transaction Tracker database.
	private final TransactionsDB db;
	
	// The current signed in user to this database.
	private User currentUser;
	
	/** Initializes this application */
	private TransactionsTrackerApp(TransactionsDB db) {
		this.db = db;
	}
	
	/** Entry point for the text user interface */
	public static void main( String args[] ) throws SQLException {
		TransactionsDB db = new TransactionsDB();
	    db.open();
	      
	    try {
	    	//db.prepare();
	    	  
	    	TransactionsTrackerApp app = new TransactionsTrackerApp(db);
	    	app.run();
	    } finally {
	    	db.close();
	    }
	 }
	 
	/** Runs the client until the user quits */
	public void run() {
		String response;
		Scanner input = new Scanner(System.in);
		
	    System.out.println("Welcome to Transactions Tracker!");
	    System.out.println("Type '0' to get a list of commands.");
	    
	    while (true) {
		    System.out.print('\n' + "What would you like to do?" + '\n' + "> ");
		    response = input.nextLine();
		    
	    	if (response.equals("0")) {
	    		// Print available commands
	    		this.printCommands();
	    	
	    	} else if (response.equals("1") && this.currentUser == null) {
	    		// Log in a user
	    		this.login(input);
	    		
	    	} else if (response.equals("1") && this.currentUser != null) {
	    		// Log out the current user
	    		this.logout();
	    		
	    	} else if (response.equals("2")) {
	    		// Creates a new user
	    		this.createUser(input);	
	    		
	    	} else if (response.equals("3")) {
	    		// Preferences
	    		this.preferences();
	    	
	    	} else if (response.equals("4")) {
	    		// Create a new expense
	    		this.addTransaction(input);
	    		
	    	} else if (response.equals("5")) {
	    		// Show transaction history
	    		this.showTransactionHistory();
	    	
	    	} else if (response.equals("exit")) {
	    		// Exit the application
	    		this.exit();
	    		break;

	    	} else {
	    		System.out.println("Unrecognised command : " + response);
				System.out.println("Type '0' to see a list of "
						+ "valid commands.");
	    	}
	    }
	}

	/**
	 * Prints the available commands for this program.
	 */
	private void printCommands() {
		System.out.println('\n' + "Commands are:");
		System.out.println("'0' to get a list of commands.");
		
		// If no user logged in, option 1 is to log in.
		if (this.currentUser == null) {
			System.out.println("'1' to log in.");
		// If already logged in, option 1 is to logout.
		} else {
			System.out.println("'1' to log out.");
		}
		
		System.out.println("'2' to create a new user.");
		System.out.println("'3' for program preferences.");
		System.out.println("'4' to enter a new expense.");
		System.out.println("'5' to display your transaction history.");
		System.out.println("'exit' to leave.");
	}

	/** 
	 * Attempts to log in a user 
	 * @param input is the scanner used to read user input.
	 */
	private void login(Scanner input) {

		// Get user name and password from the user.
		System.out.print('\n' + "Username: ");
		String username = input.nextLine();
		System.out.print("Password: ");
		String password = input.nextLine();
		
		System.out.println("Logging in...");
		User loggedInUser = this.db.logIn(username, password);
		
		while (loggedInUser == null) {
			System.out.println("Invalid username or password.");
			
			System.out.print('\n' + "Username: ");
			username = input.nextLine();
			System.out.print("Password: ");
			password = input.nextLine();
			
			loggedInUser = this.db.logIn(username, password);
		}
		
		this.currentUser = loggedInUser;
		System.out.println("Hello " + this.currentUser.getName());
	}
	
	/** Attempts to logout the currently logged in user. */
	private void logout() {
		System.out.println("Logging out...");
		User oldUser = this.currentUser;
		this.currentUser = null;
		System.out.println(oldUser.getUsername() + " logged out.");
	}
	
	/** 
	 * Creates a new user and adds it to the database. 
	 * @param input is the scanner used to read user input.
	 */
	private void createUser(Scanner input) {
		System.out.println("Creating new user...");
		User newUser = CreateNewUser.run(input, this.db);
		boolean addedCorrectly = this.db.addNewUser(newUser);
		
		if (addedCorrectly) {
			System.out.println("Added user.");
		} else {
			System.out.println("Failed to add user and default categories.");
			System.out.println("Please try again later.");
		}
	}

	/** Allows user to edit program preferences */
	private void preferences() {
		System.out.println("This option has not yet been implemented.");
		System.out.println("Please pick a different option.");
	}
	
	/**
	 * Creates a new transaction object using the user input and adds it to the database.
	 * @param input is the scanner used to read user input.
	 */
	private void addTransaction(Scanner input) {
		if (this.currentUser == null) {
			System.out.println("You must be logged in for this feature.");
			return;
		}
		
		// Get user specific categories.
		String[] categories = db.getCategories();

		// If categories are null set them to the default categories and alert user.
		if (categories == null) {
			System.out.println("Cannot get all avaliable categories at this time.");
			System.out.println("Default categories will only be avaliable.");
			categories = this.defaultCategories;
		}
		
		// Make a Transaction object.
		Transaction trans = CreateNewTransaction.run(input, categories);
		
		// Add transaction to database
		Double balance = this.db.addExpense(trans, this.currentUser.getUsername());
		
		if (balance == null) {
			System.out.println("Unable to add transaction and update balance. "
					+ "Please try again later.");
			return;
		}
		
		// Set the logged in user objects' new balance.
		this.currentUser.setUserBalance(balance);
		
		// Print new balance
		System.out.println("Your current balance is $" + balance.doubleValue());
	}

	
	/** Allows the user to see their transaction history */
	private void showTransactionHistory() {
		if (this.currentUser == null) {
			System.out.println("You must be logged in for this feature.");
			return;
		}
		System.out.println("This option has not yet been implemented.");
		System.out.println("Please pick a different option.");
	}
	
	/** Exits the application */
	private void exit() {
		System.out.println('\n' + "Thank you for monitoring your expenses "
				+ "with Transactions Tracker!");
		System.out.println("Goodbye.");
	}
}