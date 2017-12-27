package view;

import java.util.Scanner;

import controller.TransactionHelper;
import controller.TransactionsDB;
import model.User;

/**
 * Helper class of TransactionsTrackerApp. Helps the application to create new valid user
 * objects from user information.
 * @author L. James Davidson
 */
class CreateNewUser {

	// Constants regarding the restrictions from the SQLite database tables.
	private static final int MAX_USERNAME_CHARS = 30;
	private static final int MAX_FULLNAME_CHARS = 50;
	private static final int MAX_PASSWORD_CHARS = 30;
	private static final int MIN_PASSWORD_CHARS = 8;
	
	// Starting balance of all new users
	private static final double STARTING_BALANCE = 0.0;
	
	/**
	 * Uses user given information to make a new user object.
	 * @param input is the scanner to receive user input.
	 * @param db is the database where the new user will be added to.
	 * @return A User object containing the new user information.
	 */
	protected static User run(Scanner input, TransactionsDB db) {
		String[] userInfo = new String[3];
		
		// Determine the new user's user name
		setUsername(input, db, userInfo);
		
		// Determine the new user's full name.
		setFullName(input, userInfo);
		
		// Determine the new user's password
		setPassword(input, userInfo);
		
		// Ensure that the user information is correct
		checkInformation(input, db, userInfo);
		
		return new User(userInfo[0], userInfo[1], STARTING_BALANCE, userInfo[2]);
	}

	/**
	 * Asks the user what they would like their new user name to be.
	 * @param input is the scanner used to receive user input.
	 * @param db is the database which the new user will eventually be added to.
	 * @param userInfo is the string array containing the soon to be created user's information.
	 * @modifies The zeroth index of userInfo which stores the user selected user name.
	 */
	private static void setUsername(Scanner input, TransactionsDB db, String[] userInfo) {
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
		
		userInfo[0] = username;
	}
	
	/**
	 * Asks the user what there full name is.
	 * @param input is the scanner used to receive user input.
	 * @param userInfo is the string array containing the soon to be created user's information.
	 * @modifies The first index of userInfo which stores the user's full name.
	 */
	private static void setFullName(Scanner input, String[] userInfo) {
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
		
		userInfo[1] = name;
	}

	/**
	 * Asks the user what there would like their password to be.
	 * @param input is the scanner used to receive user input.
	 * @param userInfo is the string array containing the soon to be created user's information.
	 * @modifies The second index of userInfo which stores the user's chosen password.
	 */
	private static void setPassword(Scanner input, String[] userInfo) {
		String password;
		while (true) {
			// Ask user what they want their password to be.
			System.out.print('\n' + "What do you want you password to be?" + '\n' + "> ");
			password = input.nextLine();
			
			// Check if password is at least a minimum amount of characters long.
			if (password.length() < MIN_PASSWORD_CHARS) {
				System.out.println("Passwords must be at least " + MIN_PASSWORD_CHARS 
						+ " characters long.");
				continue;
			
			// Check if password is less than the maximum amount of characters long.
			} else if (password.length() > MAX_PASSWORD_CHARS) {
				System.out.println("Passwords must be less than " + MIN_PASSWORD_CHARS 
						+ " characters long.");
				continue;
			}
			
			break;
		}
		
		userInfo[2] = password;
	}

	/**
	 * Asks the user if all the new user information is correct.
	 * @param input is the scanner used to receive user input.
	 * @param db is the database which the new user will eventually be added to.
	 * @param userInfo is the string array containing the soon to be created user's information.
	 * @modifies The zeroth index of userInfo which stores the user chosen user name.
	 * @modifies The first index of userInfo which stores the user's full name.
	 * @modifies The second index of userInfo which stores the user's chosen password.
	 */
	private static void checkInformation(Scanner input, TransactionsDB db, String[] userInfo) {
		// Show the user the new user information.
		System.out.println("The new user information is as follows...");
		printUserInfo(userInfo);
		
		// Ask user if information is correct.
		String question = "Is this information correct?";
		boolean answer = TransactionHelper.yesNo(input, question);
		
		// If the user wants to change something allow them to change it.
		if (!answer) {
			editUserInfo(input, db, userInfo);
		}
	}
	
	/**
	 * Prints the user information.
	 * @param userInfo the information for the new user-- which will be printed.
	 */
	private static void printUserInfo(String[] userInfo) {
		System.out.println("Username: " + userInfo[0]);
		System.out.println("Full name: " + userInfo[1]);
		System.out.println("Password: " + userInfo[2]);
	}
	
	/**
	 * Allows the user to edit the new user information.
	 * @param input is the scanner used to receive user input.
	 * @param db is the database which the new user will eventually be added to.
	 * @param userInfo is the string array containing the soon to be created user's information.
	 * @modifies The zeroth index of userInfo which stores the user selected user name.
	 * @modifies The first index of userInfo which stores the user's full name.
	 * @modifies The second index of userInfo which stores the user's chosen password.
	 */
	private static void editUserInfo(Scanner input, TransactionsDB db, String[] userInfo) {
		while (true) {
			// Ask user which piece of information they would like to edit.
			System.out.println("Type '0' to edit the username.");
			System.out.println("Type '1' to edit your full name.");
			System.out.println("Type '2' to edit the password.");
			
			System.out.print('\n' + "What would you like to edit?" + '\n' + "> ");
			int response;
			try {
				response = Integer.parseInt(input.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("Invalid response. Please type a number.");
				continue;
			}
			
			if (response == 0) {
				setUsername(input, db, userInfo);
			} else if (response == 1) {
				setFullName(input, userInfo);
			} else if (response == 2) {
				setPassword(input, userInfo);
			} else {
				System.out.println("Invalid response please type a number 0-2.");
				continue;
			}
			
			System.out.println("The updated user information is as follows...");
			printUserInfo(userInfo);
			
			while(true) {
				String question = "Is this information correct?";
				boolean answer = TransactionHelper.yesNo(input, question);
				
				// If the user does not want to edit anything else then return, otherwise break
				// from this while loop.
				if (!answer) {
					break;
				} else {
					return;
				}
			}
		}
	}
}
