package view;

import java.sql.SQLException;
import java.util.Scanner;

import controller.TransactionHelper;
import controller.TransactionsDB;
import model.Transaction;
import model.User;
import model.BCrypt;

/**
 * Text based interface for clients to query and update the database in order to log in, add 
 * transactions, breakdown expenses via date, price, categories, and more.
 * @author L. James Davidson
 */
public class TransactionsTrackerApp {
	
	// Max number of incorrect password attempts.
	private final int MIN_INCORRECT_LOGIN_ATTEMPTS = 3;
	
	// The Transaction Tracker database.
	private final TransactionsDB db;
	
	// The current signed in user to this database.
	private User currentUser;
	
	/** Initializes this application */
	private TransactionsTrackerApp(TransactionsDB db) {
		this.db = db;
	}
	
	/** Entry point for the text user interface */
	public static void main(String[] args) throws SQLException {
		TransactionsDB db = new TransactionsDB();
	    db.open();
	      
	    try {
	    	//db.prepare();
	    	TransactionHelper.prepare();
	    	TransactionsTrackerApp app = new TransactionsTrackerApp(db);
	    	app.run();
	    } finally {
	    	db.close();
	    }
	 }
	 
	/** Runs the client until the user quits */
	public void run() {
		boolean keepGoing = true;
		String response;
		Scanner input = new Scanner(System.in);
		
	    System.out.println("Welcome to Transactions Tracker!");
	    System.out.println("Type '0' to get a list of commands.");
	    
	    while (keepGoing) {
		    System.out.print('\n' + "(Main Menu) What would you like to do?" + '\n' + "> ");
		    response = input.nextLine().toLowerCase();
		    
		    switch (response) {
		    
		    	case "0":
		    		// Print available commands
		    		this.printCommands();
		    		break;
		    		
		    	case "1":	
		    		if (this.currentUser == null) {
		    			// Log in a user
		    			this.login(input);
		    		} else {
		    			// Log out the current user
		    			this.logout();
		    		}
		    		break;
		    		
		    	case "2":
		    		// Creates a new user
		    		this.createUser(input);
		    		break;
		    		
		    	case "3":
		    		// Settings
		    		this.settings(input);
		    		break;
		    	
		    	case "4":
		    		// Create a new expense
		    		this.addTransaction(input);
		    		break;
		    		
		    	case "5":
		    		// Show transaction history
		    		this.showTransactionHistory(input);
		    		break;
		    		
		    	case "exit":
	    	   		// Exit the application
		    		this.exit();
		    		keepGoing = false;
		    		break;
		    	
		    	default:
		    		// Does not fit into any cases.
		    		this.unrecognisedCommand(response);
	    	}
	    }
	}
	
	/** 
	 * Prints a message alerting the user that the command was invalid.
	 * @param The command which was invalid.
	 */
	private void unrecognisedCommand(String response) {
		System.out.println("Unrecognised command: " + response);
		System.out.println("Type '0' to see a list of "
				+ "valid commands.");
	}

	/** Prints the available commands for this program. */
	private void printCommands() {
		System.out.println('\n' + "Main menu commands are:");
		System.out.println("'0' to get a list of main menu commands.");
		
		// If no user logged in, option 1 is to log in.
		if (this.currentUser == null) {
			System.out.println("'1' to log in.");
		// If already logged in, option 1 is to logout.
		} else {
			System.out.println("'1' to log out.");
		}
		
		System.out.println("'2' to create a new user.");
		System.out.println("'3' for settings.");
		System.out.println("'4' to enter a new expense.");
		System.out.println("'5' to display your transaction history.");
		System.out.println("'exit' to leave.");
	}

	/** 
	 * Attempts to log in a user 
	 * @param input is the scanner used to read user input.
	 */
	private void login(Scanner input) {
		
		// Initialize variables
		String username;
		String password;
		User dbUser;
		int incorrectAttempts = 0;

		do {
			// Get user name and password from the user.
			System.out.print('\n' + "Username: ");
			username = input.nextLine();
			System.out.print("Password: ");
			password = input.nextLine();
			
			System.out.println("Logging in...");
			dbUser = this.db.logIn(username);
			
			// Check if user name is valid (non-null dbUser) and if password is correct for 
			// user name.
			if (dbUser != null && BCrypt.checkpw(password, dbUser.getPassword())) {
				break;
			}
			
			incorrectAttempts++;
			
			// If there has been MIN_INCORRECT_LOGIN_ATTEMPTS then exit program.
			if (incorrectAttempts == MIN_INCORRECT_LOGIN_ATTEMPTS) {
				System.out.println("You have entered too many incorrect log-in attempts.");
				System.out.println("Please try again later.");
				try {
					this.db.close();
				} catch (SQLException e) {
					TransactionHelper.printErrorToLog(e);
				}
				System.exit(1);
			}
			
			// Alert user of invalid log in information
			System.out.println("Invalid username or password.");
			
		} while(true);
		
		this.currentUser = dbUser;
		System.out.println("Hello " + this.currentUser.getFullName());
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
			System.out.println("Failed to add user, please try again later.");
		}
	}

	/** 
	 * Allows user to edit settings. 
	 * @param input the scanner to read user input.
	 */
	private void settings(Scanner input) {
		if (this.currentUser == null) {
			// Must be logged in before editing settings.
			System.out.println("Please log in before editing settings.");
		} else {
			// Go to settings.
			Settings.run(this.db, input, this.currentUser);
		}
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
			System.out.println("Cannot get avaliable categories at this time.");
			System.out.println("Please try again later.");
			return;
		}
		
		// Make a Transaction object.
		Transaction trans = CreateNewTransaction.run(input, categories);
		

		try {
			// Begin the transaction
			this.db.beginTransaction();
			
			// Add transaction to database
			this.db.addExpense(trans, this.currentUser.getUsername());
			
			// Change the user's balance by the amount of the transaction. 
			double newBal = this.db.updateBalance(this.currentUser.getUsername(), 
					trans.getAmountInCents());
			
			// Commit the transaction
			this.db.commitTransaction();
			
			// Set the logged in user objects' new balance.
			this.currentUser.setUserBalance(newBal / 100.0);
			// We are dividing by 100 because the balances are stored in cents on the database.
			
			// Print new balance
			System.out.println("Your current balance is $" + this.currentUser.getUserBalance());
			
		} catch (SQLException e) {
			
			// Roll back the transaction
			try {
				this.db.rollbackTransaction();
				
			} catch (SQLException e1) {
				TransactionHelper.printErrorToLog(e1);
			}
			
			TransactionHelper.printErrorToLog(e);
			System.out.println("Error adding transaction. Please see the log file.");
			return;
		}
	}

	/** 
	 * Allows the user to see their transaction history.
	 * @param input is the scanner used to read user input.
	 */
	private void showTransactionHistory(Scanner input) {
		if (this.currentUser == null) {
			System.out.println("You must be logged in for this feature.");
			return;
		}
		ShowHistory.run(input, db, this.currentUser);
	}
	
	/** Exits the application */
	private void exit() {
		System.out.println('\n' + "Thank you for monitoring your expenses "
				+ "with Transactions Tracker!");
		System.out.println("Goodbye.");
	}
}
