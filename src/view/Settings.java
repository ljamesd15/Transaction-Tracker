package view;

import java.util.Scanner;

import controller.DB;
import model.BCrypt;
import model.User;

class Settings {

	// Constants regarding the restrictions from the SQLite database tables.
	private static final int MAX_CATNAME_CHARS = 30;
	
	/**
	 * Allows user to access user specific settings.
	 * @param db is the database whose user information will be edited.
	 * @param input the scanner to read user input.
	 * @param user is the user whose settings will be accessed.
	 */
	public static void run(DB db, Scanner input, User user) {
		printUserSettingsCommands();
		String response;
		boolean keepGoing = true;
		
		while (keepGoing) {
		    System.out.print('\n' + "(Settings) What would you like to do?" + '\n' + "> ");
			response = input.nextLine().toLowerCase();
			
			switch (response) {
			
				case "0":
					printUserSettingsCommands();
					break;
					
				case "1":
					editUserFullName(db, input, user);
					break;
				
				case "2":
					editUserPassword(db, input, user);
					break;
					
				case "3":
					addCategory(db, input, user);
					break;
					
				case "back":
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
				+ "'3' to add a new expense category." + '\n'
				+ "'back' to return to the settings menu.");
	}
	
	/** 
	 * Allows logged in user to edit their full name.
	 * @param input the scanner used to read user input.
	 * @param user is the logged in user whose information will be edited.
	 * @modifies The logged in user's full name.
	 */
	private static void editUserFullName(DB db, Scanner input, User user) {
		while (true) {
			System.out.print('\n' + "Please enter your current password" + '\n' + "> ");
			String password = input.nextLine();
			
			// Check if user typed old password matches the old password.
			if (!BCrypt.checkpw(password, user.getPassword())) {
				System.out.println("Incorrect password.");
						
			} else {
				String newName = NewUser.setFullName(input);
				if (db.changeFullName(user, newName)) {
					System.out.println("Full name changed from '" + user.getFullName() + "' to '"
							+ newName + "'.");
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
	private static void editUserPassword(DB db, Scanner input, User user) {
		while (true) {
			System.out.print('\n' + "Please enter your current password" + '\n' + "> ");
			String password = input.nextLine();
			
			// Check if user typed old password matches the old password.
			if (!BCrypt.checkpw(password, user.getPassword())) {
				System.out.println("Incorrect password.");
				
			} else {
				String newPassword = NewUser.setPassword(input);
				if (db.changePassword(user, newPassword)) {
					System.out.println("Password successfully changed.");
					user.setPassword(newPassword);
					
				} else {
					System.out.println("Unable to change password, please try again later.");
				}
				break;
			}
		}
	}
	
	/**
	 * Adds a new expense category from user input.
	 * @param db the database whose categories will be edited.
	 * @param input the scanner to read user input.
	 * @param user The user which will get the new category.
	 */
	private static void addCategory(DB db, Scanner input, User user) {
		while (true) {
			System.out.print("Current categories are ");
			db.printCategories(user);
			
			System.out.print('\n' + "What is the name of the category you would like to add?"
					+ '\n' + "> ");
			String newCategory = input.nextLine();
			
			if (newCategory.length() > MAX_CATNAME_CHARS) {
				System.out.println("Category names must be less than " + MAX_CATNAME_CHARS 
						+ " characters long.");
			}
			
			// Determine if this is a new category.
			String dbCategory = db.isACategory(user, newCategory);
			if (dbCategory != null) {
				System.out.println(dbCategory + " is already a category, "
						+ "please pick a different new category name.");
			} else {
				// Capitalize first letter
				newCategory = newCategory.toUpperCase().substring(0, 1)	
						+ newCategory.toLowerCase().substring(1, newCategory.length());
				
				if (db.addCategory(user, newCategory)) {
					System.out.println("Successfully added category: " + newCategory);
				} else {
					System.out.println("Unable to add category, please try again later.");
				}
				break;
			}
		}
	}
}