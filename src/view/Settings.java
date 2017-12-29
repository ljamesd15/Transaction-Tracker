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
		printSettingsCommands();
		
		// Initializing user response.
		String response;
		boolean keepGoing = true;
		
		// While a valid command has not been chosen.
		while (keepGoing) {
		    System.out.print('\n' + "(Settings) What would you like to do?" + '\n' + "> ");
			response = input.nextLine().toLowerCase();
			
			// Cases for user input.
			switch (response) {
			
				case "0":
					// Print available options
					printSettingsCommands();
					break;
					
				case "1":
					// Program preferences
					programSettings(db, input);
					break;
				
				case "2":
					// User preferences
					userSettings(db, input, currentUser);
					break;
					
				case "back":
					// Return to main menu
					keepGoing = false;
					break;
					
				default:
					System.out.println("Unrecognised command: " + response);
					System.out.println("Type '0' to see a list of "
							+ "valid commands.");
			}
		}
	}

	/** Prints the available settings commands. */
	private static void printSettingsCommands() {
		System.out.println('\n' + "Settings menu commands are:" + '\n'
				+ "'0' to get a list of settings menu commands." + '\n'
				+ "'1' to go to program settings." + '\n'
				+ "'2' to go to user settings." + '\n'
				+ "'back' to return to the main menu.");
		
	}

	/**
	 * Allows user to access the program settings.
	 * @param db is the database whose information may be modified.
	 * @param input the scanner to read user input.
	 */
	private static void programSettings(TransactionsDB db, Scanner input) {
		printProgramSettingsCommands();
		
		// Initializing user response.
		String response;
		boolean keepGoing = true;
		
		// While a valid command has not been chosen.
		while (keepGoing) {
		    System.out.print('\n' + "(Program Settings) What would you like to do?" + '\n' + "> ");
			response = input.nextLine().toLowerCase();
			
			// Cases for user input.
			switch (response) {
			
				case "0":
					// Print available options
					printProgramSettingsCommands();
					break;
					
				case "1":
					// Add a category
					addCategory(db, input);
					break;
				
				case "2":
					// Remove a category
					removeCategory(db, input);
					break;
				
				case "back":
					// Return to settings menu
					keepGoing = false;
					break;
				
				default:
					System.out.println("Unrecognised command: " + response);
					System.out.println("Type '0' to see a list of "
							+ "valid commands.");
			}
		}
	}	
	
	/** Prints program settings commands. */
	private static void printProgramSettingsCommands() {
		System.out.println('\n' + "Program settings menu commands are:" + '\n'
				+ "'0' to get a list of program settings menu commands." + '\n'
				+ "'1' to add a new expense category." + '\n'
				+ "'2' to remove an expense category." + '\n'
				+ "'back' to return to the settings menu.");
	}

	/**
	 * Adds a new expense category from user input.
	 * @param db the database whose categories will be edited.
	 * @param input the scanner to read user input.
	 */
	private static void addCategory(TransactionsDB db, Scanner input) {
		String[] categories = db.getCategories();

		while (true) {
			// Print out current categories.
			System.out.print("Current categories are ");
			for (int i = 0; i < categories.length - 1; i++) {
				System.out.print(categories[i] + ", ");
			}
			System.out.println("and " + categories[categories.length - 1] + ".");
			
			System.out.print('\n' + "What is the name of the category you would like to add?"
					+ '\n' + "> ");
			String newCategory = input.nextLine();
			
			if (newCategory.length() > MAX_CATNAME_CHARS) {
				System.out.println("Category names must be less than " + MAX_CATNAME_CHARS 
						+ " characters long.");
			}
			
			// Determine if this is a new category.
			boolean isNewCategory = true;
			for (int i = 0; i < categories.length; i++) {
				if (categories[i].toLowerCase().equals(newCategory.toLowerCase())) {
					System.out.println(categories[i] + " is already a category, "
							+ "please pick a different new category name.");
					isNewCategory = false;
				}
			}
			
			// If its a new category add it to the DB
			if (isNewCategory) {
				// Add category to DB
				boolean executed = db.addCategory(newCategory);
				
				// Inform user of outcome.
				if (executed) {
					System.out.println("Successfully added category: " + newCategory);
				} else {
					System.out.println("Unable to add category, please try again later.");
				}
				break;
			}
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
			
			// Print non-default categories differently depending on if there is 1, 2, or more.
			if (numOfNonDefaultCategories == 1) {
				System.out.println(nonDefaultCategories[0] + ".");
				
			} else  if (numOfNonDefaultCategories == 2){
				System.out.println(nonDefaultCategories[0] + " and " + nonDefaultCategories[1]);
				
			} else {
				for (int i = 0; i < numOfNonDefaultCategories - 1; i++) {
					System.out.print(nonDefaultCategories[i] + ", ");
				}
				System.out.println("and " + nonDefaultCategories[numOfNonDefaultCategories - 1]);
			}
			
			System.out.print('\n' + "What is the name of the category you would like to "
					+ "remove?" + '\n' + "> ");
			String categoryName = input.nextLine();
			
			// Determine if user response matches a non-default category. 
			for (int i = 0; i < numOfNonDefaultCategories; i++) {
				
				if (nonDefaultCategories[i].toLowerCase().equals(categoryName.toLowerCase())) {
					
					// Remove category from DB
					boolean executed = db.removeCategory(nonDefaultCategories[i]);
					
					// Inform user of outcome
					if (executed) {
						System.out.println("Successfully removed category: " 
								+ nonDefaultCategories[i]);
					} else {
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
		printUserSettingsCommands();
		
		// Initializing user response.
		String response;
		boolean keepGoing = true;
		
		// While a valid command has not been chosen.
		while (keepGoing) {
		    System.out.print('\n' + "(User Settings) What would you like to do?" + '\n' + "> ");
			response = input.nextLine().toLowerCase();
			
			// Cases for user input.
			switch (response) {
			
				case "0":
					// Print available options
					printUserSettingsCommands();
					break;
					
				case "1":
					// Change full name
					editUserFullName(db, input, user);
					break;
				
				case "2":
					// Change password
					editUserPassword(db, input, user);
					break;
					
				case "back":
					// Go back to settings menu
					keepGoing = false;
					break;
					
				default:
					System.out.println("Unrecognised command: " + response);
					System.out.println("Type '0' to see a list of "
							+ "valid commands.");
			}
		}
	}

	/** Prints the available commands of the user settings menu. */
	private static void printUserSettingsCommands() {
		System.out.println('\n' + "User settings menu commands are:" + '\n'
				+ "'0' to get a list of user settings menu commands." + '\n'
				+ "'1' to edit the full name of a user." + '\n'
				+ "'2' to edit the password of the user." + '\n'
				+ "'back' to return to the settings menu.");
	}
	
	/** 
	 * Allows logged in user to edit their full name.
	 * @param input the scanner used to read user input.
	 * @param user is the logged in user whose information will be edited.
	 * @modifies The logged in user's full name.
	 */
	private static void editUserFullName(TransactionsDB db, Scanner input, User user) {
		
		while (true) {
			System.out.print('\n' + "Please enter your current password" + '\n' + "> ");
			String oldPassword = input.nextLine();
			System.out.print("Please enter your new full name" + '\n' + "> ");
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
				String oldName = user.getFullName();
				user.setUserFullName(newFullName);
				boolean executed = db.changeFullName(user);
				
				// Inform user of output.
				if (executed) {
					System.out.println("Full name changed from '" + oldName + "' to '"
							+ newFullName + "'.");
				} else {
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
			System.out.print('\n' + "Please enter your current password" + '\n' + "> ");
			String oldPassword = input.nextLine();
			System.out.print("Please enter your new password" + '\n' + "> ");
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
				
				// Inform user of output.
				if (executed) {
					System.out.println("Password successfully changed.");
				} else {
					System.out.println("Unable to change password, please try again later.");
				}

				break;
			}
		}
	}
}