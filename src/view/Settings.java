package view;

import java.util.Scanner;

import controller.TransactionsDB;
import model.User;

class Settings {

	// Constants regarding the restrictions from the SQLite database tables.
	private static final int MAX_CATNAME_CHARS = 30;
	private static final int MAX_FULLNAME_CHARS = CreateNewUser.MAX_FULLNAME_CHARS;
	private static final int MAX_PASSWORD_CHARS = CreateNewUser.MAX_PASSWORD_CHARS;
	private static final int MIN_PASSWORD_CHARS = CreateNewUser.MIN_PASSWORD_CHARS;
	
	// An array of strings containing the default user expense categories.
	protected static final String[] DEFAULT_CATEGORIES = new String[]
			{"Deposit", "Food", "Housing", "Transportation", "Misc."};

	/**
	 * Allows user to access available settings.
	 * @param db is the database whose information may be modified.
	 * @param input the scanner used to read user input.
	 * @param currentUser is the currently logged in user.
	 */
	protected static void run(TransactionsDB db, Scanner input, User currentUser) {
		// Has user choose between user and program settings.
		System.out.println('\n' + "Settings:");
		printSettingsCommands();
		
		// Initializing user response.
		String response;
		boolean validCommand = false;
		
		// While a valid command has not been chosen.
		while (!validCommand) {
		    System.out.print('\n' + "What would you like to do?" + '\n' + "> ");
			response = input.nextLine();
			
			// Cases for user input.
			switch (response) {
			
				case "0":
					// Print available options
					printSettingsCommands();
					break;
					
				case "1":
					// User preferences
					userSettings(db, input, currentUser);
					validCommand = true;
					break;
				
				case "2":
					// Program preferences
					programSettings(db, input);
					validCommand = true;
					break;
					
				default:
					System.out.println("Unrecognised command : " + response);
					System.out.println("Type '0' to see a list of "
							+ "valid commands.");
			}
		}
	}

	/** Prints the available settings commands. */
	private static void printSettingsCommands() {
		System.out.println("'0' for settings commands list." + '\n'
				 + "'1' to go to program settings." + '\n'
				 + "'2' to go to user settings.");
		
	}

	/**
	 * Allows user to access the program settings.
	 * @param db is the database whose information may be modified.
	 * @param input the scanner to read user input.
	 */
	private static void programSettings(TransactionsDB db, Scanner input) {
		System.out.println('\n' + "Program Settings:");
		printProgramSettingsCommands();
		
		// Initializing user response.
		String response;
		boolean validCommand = false;
		
		// While a valid command has not been chosen.
		while (!validCommand) {
		    System.out.print('\n' + "What would you like to do?" + '\n' + "> ");
			response = input.nextLine();
			
			// Cases for user input.
			switch (response) {
			
				case "0":
					// Print available options
					printProgramSettingsCommands();
					break;
					
				case "1":
					// Add a category
					addCategory(db, input);
					validCommand = true;
					break;
				
				case "2":
					// Remove a category
					removeCategory(db, input);
					validCommand = true;
					break;
					
				default:
					System.out.println("Unrecognised command : " + response);
					System.out.println("Type '0' to see a list of "
							+ "valid commands.");
			}
		}
	}	
	
	/** Prints program settings commands. */
	private static void printProgramSettingsCommands() {
		System.out.println("'0' for program settings commands list." + '\n'
				 + "'1' to add a new expense category." + '\n'
				 + "'2' to remove an expense category.");
	}

	/**
	 * Adds a new expense category from user input.
	 * @param db the database whose categories will be edited.
	 * @param input the scanner to read user input.
	 */
	private static void addCategory(TransactionsDB db, Scanner input) {
		String[] categories = db.getCategories();

		// Print out current categories.
		System.out.print("Current categories are ");
		for (int i = 0; i < categories.length - 1; i++) {
			System.out.print(categories[i] + ", ");
		}
		System.out.println("and " + categories[categories.length - 1] + ".");
		
		// While the user inputs a string which matches a current category.
		while (true) {
			System.out.println('\n' + "What is the name of the category you would like to add?");
			String newCategory = input.nextLine();
			
			if (newCategory.length() > MAX_CATNAME_CHARS) {
				System.out.println("Category names must be less than " + MAX_CATNAME_CHARS 
						+ " characters long.");
			}
			
			for (int i = 0; i < categories.length; i++) {
				if (categories[i].toLowerCase().equals(newCategory.toLowerCase())) {
					System.out.println(newCategory + " is already a category");
					System.out.println("Please pick a different new category name.");
					continue;
				}
			}
			
			// Add category to DB
			boolean executed = db.addCategory(newCategory);
			
			// If update ran into an exception then alert user.
			if (!executed) {
				System.out.println("Unable to add category, please try again later.");
			}
			break;
		}
	}
	
	/**
	 * Removes an expense category from user input.
	 * @param db the database whose categories will be edited.
	 * @param input the scanner to read user input.
	 */
	private static void removeCategory(TransactionsDB db, Scanner input) {
		// Determine which categories which can be removed. 
		// (Categories added which are not default categories.)
		String[] categories = db.getCategories();
		
		int numOfNonDefaultCategories = 0;
		String[] nonDefaultCategories = new String[categories.length - DEFAULT_CATEGORIES.length];
		for (int i = 0; i < categories.length; i++) {
			boolean contained = false;
			
			// Nested loop but default categories length is 4. So O(categories.length) runtime
			for (int j = 0; j < DEFAULT_CATEGORIES.length; j++) {
				
				if (categories[i].equals(DEFAULT_CATEGORIES[j])) {
					// This category is a default category.
					contained = true;
				}
			}
			
			// If the category is not contained in both arrays then add it to the 
			// non-default category array.
			if (!contained) {
				nonDefaultCategories[numOfNonDefaultCategories] = categories[i];
				numOfNonDefaultCategories++;
			}
		}
		
		// If no non-default categories exist then alert user and return.
		if (numOfNonDefaultCategories == 0) {
			System.out.println("No removable categories.");
			return;
		}

		
		// While the user input does not match a non-default category.
		while (true) {
			
			// Print out non-default categories
			System.out.print("Removable categories: ");
			
			// If only one non-default category then print it differently.
			if (numOfNonDefaultCategories == 1) {
				System.out.println(nonDefaultCategories[0] + ".");
				
			} else {
				for (int i = 0; i < numOfNonDefaultCategories; i++) {
					System.out.print(nonDefaultCategories[i] + ", ");
				}
				System.out.println("and " + nonDefaultCategories[numOfNonDefaultCategories - 1]);
			}
			
			System.out.println('\n' + "What is the name of the category you would like to "
					+ "remove?");
			String categoryName = input.nextLine();
			
			// Determine if user response matches a non-default category. 
			for (int i = 0; i < numOfNonDefaultCategories; i++) {
				
				if (nonDefaultCategories[i].toLowerCase().equals(categoryName.toLowerCase())) {
					
					// Remove category from DB
					boolean executed = db.removeCategory(nonDefaultCategories[i]);
					
					// If update ran into an exception then alert user.
					if (!executed) {
						System.out.println("Unable to remove category, please try again later.");
					}
					
					return;
				}
			}
			
			// If we reach this point then the user response did not match a non-default category.
			System.out.println("Invalid response. " + categoryName 
					+ " did not match any of the removable categories.");
		}
	}

	/**
	 * Allows user to access user specific settings.
	 * @param db is the database whose user information will be edited.
	 * @param input the scanner to read user input.
	 * @param user is the user whose settings will be accessed.
	 */
	private static void userSettings(TransactionsDB db, Scanner input, User user) {
		System.out.println('\n' + "User Settings:");
		printUserSettingsCommands();
		
		// Initializing user response.
		String response;
		boolean validCommand = false;
		
		// While a valid command has not been chosen.
		while (!validCommand) {
		    System.out.print('\n' + "What would you like to do?" + '\n' + "> ");
			response = input.nextLine();
			
			// Cases for user input.
			switch (response) {
			
				case "0":
					// Print available options
					printUserSettingsCommands();
					break;
					
				case "1":
					// Change full name
					editUserFullName(db, input, user);
					validCommand = true;
					break;
				
				case "2":
					// Change password
					editUserPassword(db, input, user);
					validCommand = true;
					break;
					
				default:
					System.out.println("Unrecognised command : " + response);
					System.out.println("Type '0' to see a list of "
							+ "valid commands.");
			}
		}
	}

	/** Prints the available commands of the user settings menu. */
	private static void printUserSettingsCommands() {
		System.out.println("'0' for user settings commands list." + '\n'
						 + "'1' to edit the full name of a user." + '\n'
						 + "'2' to edit the password of the user.");
	}
	
	/** 
	 * Allows logged in user to edit their full name.
	 * @param input the scanner used to read user input.
	 * @param user is the logged in user whose information will be edited.
	 * @modifies The logged in user's full name.
	 */
	private static void editUserFullName(TransactionsDB db, Scanner input, User user) {
		
		while (true) {
			System.out.println("Please enter your current password" + '\n' + "> ");
			String oldPassword = input.nextLine();
			System.out.println("Please enter your new full name" + '\n' + "> ");
			String newFullName = input.nextLine();
			
			// Check if user typed old password matches the old password.
			if (!user.getPassword().equals(oldPassword)) {
				System.out.println("Incorrect password.");
					
			// Check if the name is less than the maximum number of characters allowed.
			} else if (newFullName.length() > MAX_FULLNAME_CHARS) {
				System.out.println("Names must be less than " + MAX_FULLNAME_CHARS 
						+ " characters long.");
				
			} else {
				// Have DB change the user's full name
				user.setUserFullName(newFullName);
				boolean executed = db.changeFullName(user);
				
				// If update ran into an exception then alert user.
				if (!executed) {
					System.out.println("Unable to change full name, please try again later.");
				}
				break;
			}
		}
	}

	/** 
	 * Allows logged in user to edit their password.
	 * @param input the scanner used to read user input.
	 * @param user is the logged in user whose information will be edited.
	 * @modifies The logged in user's password.
	 */
	private static void editUserPassword(TransactionsDB db, Scanner input, User user) {
		
		while (true) {
			System.out.println("Please enter your current password" + '\n' + "> ");
			String oldPassword = input.nextLine();
			System.out.println("Please enter your new password" + '\n' + "> ");
			String newPassword = input.nextLine();
			
			// Check if user typed old password matches the old password.
			if (!user.getPassword().equals(oldPassword)) {
				System.out.println("Incorrect password.");
				
			// Check if password is at least a minimum amount of characters long.
			} else if (newPassword.length() < MIN_PASSWORD_CHARS) {
					System.out.println("Passwords must be at least " + MIN_PASSWORD_CHARS 
							+ " characters long.");
				
			// Check if password is less than the maximum amount of characters long.
			} else if (newPassword.length() > MAX_PASSWORD_CHARS) {
				System.out.println("Passwords must be less than " + MAX_PASSWORD_CHARS 
						+ " characters long.");
				
			} else {
				// Have DB change the user password
				user.setPassword(newPassword);
				boolean executed = db.changePassword(user);
				
				// If update ran into an exception then alert user.
				if (!executed) {
					System.out.println("Unable to change password, please try again later.");
				}
				break;
			}
		}
	}
}