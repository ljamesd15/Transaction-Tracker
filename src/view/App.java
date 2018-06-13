package view;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import controller.Helper;
import controller.DB;
import model.Transaction;
import model.User;
import model.BCrypt;

/**
 * Text based interface for clients to query and update the database in order to log in, add 
 * transactions, breakdown expenses via date, price, categories, and more.
 * @author L. James Davidson
 */
public class App {
	
	// Max number of incorrect password attempts.
	private final int MIN_INCORRECT_LOGIN_ATTEMPTS = 3;
	
	// The Transaction Tracker database.
	private final DB db;
	
	// The current signed in user to this database.
	private User currentUser;
	
	/** Initializes this application */
	private App(DB db) {
		this.db = db;
	}
	
	/** Entry point for the text user interface */
	public static void main(String[] args) throws SQLException {
		DB db = new DB();
	    db.open(); 
	    try {
	    	Helper.prepare();
	    	App app = new App(db);
	    	app.run();
	    } finally {
	    	db.close();
	    	Helper.close();
	    }
	 }
	 
	/** Runs the client until the user quits */
	public void run() {
		boolean keepGoing = true;
		String response;
		Scanner input = new Scanner(System.in);
	    System.out.println("Welcome to Transaction Tracker!");
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
		    			this.login(input);
		    		} else {
		    			this.logout();
		    		}
		    		break;
		    		
		    	case "2":
		    		this.createUser(input);
		    		break;
		    		
		    	case "3":
		    		this.settings(input);
		    		break;
		    	
		    	case "4":
		    		this.addTransaction(input);
		    		break;
		    		
		    	case "5":
		    		this.showTransactionHistory(input);
		    		break;
		    		
		    	case "exit":
		    		this.exit();
		    		keepGoing = false;
		    		break;
		    	
		    	default:
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
		String username;
		String password;
		User dbUser;
		int incorrectAttempts = 0;

		do {
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
					Helper.printErrorToLog(e);
				}
				System.exit(1);
			}
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
		User newUser = NewUser.run(input, this.db);
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
			System.out.println("Please log in before editing settings.");
		} else {
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
		List<String> categories = db.getCategories(this.currentUser);
		if (categories == null) {
			System.out.println("Cannot get avaliable categories at this time.");
			System.out.println("Please try again later.");
			return;
		}
		
		Transaction trans = NewTransaction.run(input, categories);
		try {
			this.db.beginTransaction();
			this.db.addExpense(trans, this.currentUser.getUsername());
			int newBal = this.db.updateBalance(this.currentUser.getUsername(), 
					trans.getAmountInCents());
			this.db.commitTransaction();
			
			// Set the logged in user objects' new balance.
			this.currentUser.setUserBalance((double)newBal / Helper.CENTS_IN_A_DOLLAR);
			System.out.println("Your current balance is $" 
					+ Helper.amountInCentsToFormattedDouble(newBal));
			
		} catch (SQLException e) {
			try {
				this.db.rollbackTransaction();
				
			} catch (SQLException e1) {
				Helper.printErrorToLog(e1);
			}
			Helper.printErrorToLog(e);
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
				+ "with Transactions Tracker!\nGoodbye.");
	}
}
