package view;

class Preferences {

	// Constants regarding the restrictions from the SQLite database tables.
	//private static final int MAX_CATNAME_CHARS = 30;
	
	/** Allows user to edit program preferences */
	protected void run() {
		// Has user choose between user and program preferences.
		
		
		// User preferences
		this.userPreferences();
		
		// Program preferences
		this.programPreferences();
	}

	private void programPreferences() {
		// Edit categories available
		this.editCategories();
		
	}
	
	/** Edits the available categories */
	private void editCategories() {
		System.out.println("This option has not yet been implemented.");
		System.out.println("Please pick a different option.");
		// Can't remove default categories
	}

	private void userPreferences() {
		// Change full name
		
		// Change password
		
	}
}
