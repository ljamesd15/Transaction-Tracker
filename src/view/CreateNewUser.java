package view;

import java.util.Scanner;

import controller.TransactionsDB;
import model.BCrypt;
import model.User;

/**
 * Helper class of TransactionsTrackerApp. Helps the application to create new valid user
 * objects from user information.
 * @author L. James Davidson
 */
abstract class CreateNewUser {

	// Constants regarding the restrictions from the SQLite database tables.
	protected static final int MAX_USERNAME_CHARS = 30;
	protected static final int MAX_FULLNAME_CHARS = 50;
	protected static final int MAX_PASSWORD_CHARS = 30;
	protected static final int MIN_PASSWORD_CHARS = 8;
	
	// Starting balance of all new users
	private static final int STARTING_BALANCE = 0;
	
	/**
	 * Uses user given information to make a new user object.
	 * @param input is the scanner to receive user input.
	 * @param db is the database where the new user will be added to.
	 * @return A User object containing the new user information.
	 */
	protected static User run(Scanner input, TransactionsDB db) {
		
		// Determine the new user's user name
		String username = setUsername(input, db);
		
		// Determine the new user's full name.
		String fullName = setFullName(input);
		
		// Determine the new user's password
		String password = setPassword(input);
		
		return new User(username, fullName, STARTING_BALANCE, password);
	}

	/**
	 * Asks the user what they would like their new user name to be.
	 * @param input is the scanner used to receive user input.
	 * @param db is the database which the new user will eventually be added to.
	 * @return The user's user name
	 */
	private static String setUsername(Scanner input, TransactionsDB db) {
		String username;
		boolean usernameTaken;
		
		while (true) {
			// Ask user which user name they would like to have.
			System.out.print('\n' + "What would you like your username to be?" + '\n' + "> ");
			username = input.nextLine();
			
			// Check if the user name is less than the maximum number of characters.
			if (username.length() > MAX_USERNAME_CHARS) {
				System.out.print("Usernames must be less than " + MAX_USERNAME_CHARS 
						+ " characters long.");
				continue;
			}
			
			// Check if the user name has already been taken.
			usernameTaken = db.isUsernameTaken(username);
			if (usernameTaken) {
				System.out.println("The username '" + username + "' is already taken.");
				continue;
			}
			
			break;
		}
		
		return username;
	}
	
	/**
	 * Asks the user what there full name is.
	 * @param input is the scanner used to receive user input.
	 * @return The user's full name.
	 */
	protected static String setFullName(Scanner input) {
		String name;
		while (true) {
			// Ask user what their full name is.
			System.out.print('\n' + "What is your full name?" + '\n' + "> ");
			name = input.nextLine();
			
			// Check if the name is less than the maximum number of characters allowed.
			if (name.length() > MAX_FULLNAME_CHARS) {
				System.out.println("Names must be less than " + MAX_FULLNAME_CHARS 
						+ " characters long.");
				continue;
			}
			
			break;
		}
		
		return name;
	}

	/**
	 * Asks the user what there would like their password to be.
	 * @param input is the scanner used to receive user input.
	 * @return The user's password.
	 */
	protected static String setPassword(Scanner input) {
		String hashed;
		while (true) {
			// Ask user what they want their password to be.
			System.out.print('\n' + "What do you want you password to be?" + '\n' + "> ");
			String password = input.nextLine();
			
			// Check if password is at least a minimum amount of characters long.
			if (password.length() < MIN_PASSWORD_CHARS) {
				System.out.println("Passwords must be at least " + MIN_PASSWORD_CHARS 
						+ " characters long.");
				continue;
			
			// Check if password is less than the maximum amount of characters long.
			} else if (password.length() > MAX_PASSWORD_CHARS) {
				System.out.println("Passwords must be less than " + MAX_PASSWORD_CHARS 
						+ " characters long.");
				continue;
			}
			
			// Have user type in their password again.
			System.out.println("Please type the password again to confirm." + '\n' + "> ");
			String passAgain = input.nextLine();
			
			if (!passAgain.equals(password)) {
				System.out.println("Passwords do not match.");
				continue;
			}
			
			// Hash the password using BCrypt
			hashed = BCrypt.hashpw(password, BCrypt.gensalt());
			
			break;
		}
		
		return hashed;
	}
}
