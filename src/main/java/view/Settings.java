package view;

import java.util.Scanner;

import controller.TransactionsDB;
import model.BCrypt;
import model.User;

class Settings {

	// Constants regarding the restrictions from the SQLite database tables.
	private static final int MAX_CATNAME_CHARS = 30;

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
				+ "'back' to return to the settings menu.");
	}

	/**
	 * Adds a new expense category from user input.
	 * @param db the database whose categories will be edited.
	 * @param input the scanner to read user input.
	 */
	private static void addCategory(TransactionsDB db, Scanner input) {
		while (true) {
			// Print out current categories.
			System.out.print("Current categories are ");
			db.printCategories();
			
			System.out.print('\n' + "What is the name of the category you would like to add?"
					+ '\n' + "> ");
			String newCategory = input.nextLine();
			
			if (newCategory.length() > MAX_CATNAME_CHARS) {
				System.out.println("Category names must be less than " + MAX_CATNAME_CHARS 
						+ " characters long.");
			}
			
			// Determine if this is a new category.
			String dbCategory = db.isACategory(newCategory);
			if (dbCategory != null) {
				System.out.println(dbCategory + " is already a category, "
						+ "please pick a different new category name.");
			} else {
				// Capitalize first letter
				newCategory = newCategory.toUpperCase().substring(0, 1)	
						+ newCategory.toLowerCase().substring(1, newCategory.length());
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
			String password = input.nextLine();
			
			// Check if user typed old password matches the old password.
			if (!BCrypt.checkpw(password, user.getPassword())) {
				System.out.println("Incorrect password.");
						
			} else {
				// Get new full name
				String newName = CreateNewUser.setFullName(input);
				
				// Have DB change the user's full name
				boolean executed = db.changeFullName(user, newName);
				
				// Inform user of output.
				if (executed) {
					System.out.println("Full name changed from '" + user.getFullName() + "' to '"
							+ newName + "'.");
					
					// Only set the user object's full name to newName once the execution was 
					// successfully completed.
					user.setUserFullName(newName);
					
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
			String password = input.nextLine();
			
			// Check if user typed old password matches the old password.
			if (!BCrypt.checkpw(password, user.getPassword())) {
				System.out.println("Incorrect password.");
				
			} else {
				// Get new password
				String newPassword = CreateNewUser.setPassword(input);
				
				// Have DB change the user password		
				boolean executed = db.changePassword(user, newPassword);
				
				// Inform user of output.
				if (executed) {
					System.out.println("Password successfully changed.");
					
					// Only set the logged in user's password to the new password if the update 
					// was executed successfully.
					user.setPassword(newPassword);
					
				} else {
					System.out.println("Unable to change password, please try again later.");
				}

				break;
			}
		}
	}
}