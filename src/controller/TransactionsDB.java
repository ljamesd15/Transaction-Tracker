package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Transaction;
import model.User;

/**
 * Allows clients to query and update the database in order to log in, add transactions, and 
 * breakdown expenses via date, price, categories, and more.
 */
public class TransactionsDB {

	/** Holds the connection to the database. */
	private Connection conn;
	
	// Statements which will be used for beginning, rolling back, and committing DB transactions.
	private PreparedStatement beginTxnStmt;
	private PreparedStatement commitTxnStmt;
	private PreparedStatement abortTxnStmt;
    
    /** Opens a connection with the TransactionsTracker database **/
    public void open() {
        try {
        	Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(
					 "jdbc:sqlite:" + TransactionHelper.FILEPATH + "\\data\\TT.db");
			
			// Set up the transaction start, commit, and roll back statements
		    beginTxnStmt = this.conn.prepareStatement("BEGIN TRANSACTION;");
		    commitTxnStmt = this.conn.prepareStatement("COMMIT");
		    abortTxnStmt = this.conn.prepareStatement("ROLLBACK;");
		    
		} catch (SQLException | ClassNotFoundException e) {
			TransactionHelper.printErrorToLog(e);
			System.out.println("Error establishing connection, please see log file.");
			System.exit(1);
		}
    }
    
    /** Closes the connection to the database. */
    public void close() throws SQLException {
      conn.close();
    }
    
    /** 
	 * Begins a new transaction which will only be committed when explicitly requested.
	 *  @throws SQLException if a database access error occurs, the database connection is closed,
	 *  	or the beginTxnStmt is closed.
	 */    
    public void beginTransaction() throws SQLException {
        beginTxnStmt.executeUpdate();  
    }

    /**
     * Commits the current transaction and new transactions will auto commit.
     * @throws SQLException if a database access error occurs, the database connection is closed,
	 *  	the commitTxnStmt is closed, or currently participating in a distributed transaction.
     */
    public void commitTransaction() throws SQLException {
        commitTxnStmt.executeUpdate(); 
    }

    /**
     * Rolls back the current transaction and new transaction will auto commit.
     * @throws SQLException if a database access error occurs, the database connection is closed,
	 *  	the abortTxnStmt is closed, or currently participating in a distributed transaction.
     */
    public void rollbackTransaction() throws SQLException {
        abortTxnStmt.executeUpdate();
  	}
    
    /**
     * Checks to see if a user name is already taken.
     * @param username is the user name being checked.
     * @return True if the user name is taken and false if it is available.
     */
	public boolean isUsernameTaken(String username) {
		// Initialize query and statement.
		PreparedStatement check;
		String sqlStmt = "SELECT *\n"
					   + "FROM Users \n"
					   + "WHERE username = ?";
		
		try {
			// Clear parameters and sanitize string.
			check = this.conn.prepareStatement(sqlStmt);
			check.clearParameters();
			
			// Fill in parameter and execute query
			check.setString(1, username);
			ResultSet users = check.executeQuery();
			
			// If there are no values in the result set then this user name is not taken.
			return users.next();
			
		} catch (SQLException e) {
			TransactionHelper.printErrorToLog(e);
			return true;
		}
	}
	
	/**
	 * Adds a new user to the database.
	 * @param newUser is the user object which contains the information of the new user.
	 * @return True if the user was added successfully.
	 */
	public boolean addNewUser(User newUser) {
		// Initialize query and SQL statement.
		PreparedStatement addUser;
		String sqlStmt = "INSERT INTO Users VALUES (?, ?, ?, ?)";
		
		try {
			// Sanitize string and clear parameters.
			addUser = this.conn.prepareStatement(sqlStmt);
			addUser.clearParameters();
			
			// Add in parameter info.
			addUser.setString(1, newUser.getUsername());
			addUser.setString(2, newUser.getFullName());
			addUser.setFloat(3, (float) 0.00);
			addUser.setString(4, newUser.getPassword());
			
			// Execute query
			addUser.execute();
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
		
		return true;
	}
    
    /**
     * Attempts to log in a user to this database.
     * @returns A user object containing the information for the user which just logged in or 
     * 		null if login failed.
     */
    public User logIn(String username) {
      
  	  // Create query and statement
  	  PreparedStatement logIn;
  	  String sqlStmt = 
  			  "SELECT *\n"
  			  + "FROM Users\n"
  			  + "WHERE username = ?";
  	  
  	ResultSet results;
  	  try {
  	  	  // Prepare the statement and clear parameters
  	  	  logIn = conn.prepareStatement(sqlStmt);
  	  	  logIn.clearParameters();
  	  	  
  	  	  // Set the information in this statement
  	  	  logIn.setString(1, username);
  	  	  
  	  	  // Execute the query
  	  	  results = logIn.executeQuery();
  	  	  
  	  	  // If no results then return null
  	  	  if (!results.next()) {
  	  		  return null;
  	  	  }
  	  	  
  	  	  return new User(results.getString("username"), results.getString("name"), 
  	  			  results.getInt("balance_in_cents") / 100.0, results.getString("password"));
  	  	  
  	  } catch (SQLException e) {
  		  TransactionHelper.printErrorToLog(e);
  		  return null;
  	  }
    }
    
    /**
     * Adds an expense to a specified user.
     * @param expense is the transaction to be added to the database.
     * @param username is the user who the transaction will be added to.
     * @throws SQLException if there was an error when adding the transaction to the database.
     */
    public void addExpense(Transaction expense, String username) throws SQLException {    	
    	// Initialize query and statement.
    	PreparedStatement insert;
    	String sqlStmt = "INSERT INTO Transactions VALUES (?, ?, ?, ? , ?, ?)";
    	      	
    	// Clear parameters
    	insert = this.conn.prepareStatement(sqlStmt);
    	insert.clearParameters();
    	
    	// Insert parameters from Transaction object.
    	insert.setString(1, expense.getDescription());
    	insert.setFloat(2, expense.getAmountInCents());
    	insert.setString(3, expense.getDate().toString());
    	insert.setString(4, expense.getMemo());
    	insert.setString(5, expense.getCategory());
    	insert.setString(6, username);
    	
		// Add transaction
		insert.execute();
    }
    
    /**
     * Updates a users balance by an amount in the database.
     * @param username is the user whose balance will be updated in the database.
     * @param amount is the change applied to the user's balance.
     * @return The new account balance.
     * @throws SQLException if there was a problem updating the user's balance.
     */
    public Integer updateBalance(String username, int amount) throws SQLException {
    	// Initialize both SQL statement and the prepared statements.
    	PreparedStatement check;
    	String checkStmt = "SELECT balance_in_cents FROM Users WHERE username = ?";
    	PreparedStatement update;
    	String updateStmt = "UPDATE Users SET balance_in_cents = ? WHERE username = ?";
    	
    	// Get current balance value
		check = this.conn.prepareStatement(checkStmt);
		check.clearParameters();
		check.setString(1, username);
		ResultSet checkResult = check.executeQuery();
		
		// Move the cursor forward
		checkResult.next();
		int balance = checkResult.getInt(1);
		
		// Calculate new balance
		balance = balance + amount;
		
		// Update user balance
		update = this.conn.prepareStatement(updateStmt);
		update.clearParameters();
		update.setFloat(1, balance);
		update.setString(2, username);
		update.execute();
		
		return balance;
    }

    /**
     * Gets the categories which are available in this program.
     * @return A list of strings containing the available categories or null if there was
     * 		an exception which prevented the database from being accessed properly.
     */
	public String[] getCategories() {
		// Find the number of categories in the DB
		PreparedStatement query;
		String sqlStmt = "SELECT count(*) \n"
				   + "FROM Categories";
		
		try {
			query = conn.prepareStatement(sqlStmt);
			ResultSet result = query.executeQuery();
			
			// Move cursor to the first row and grab the number of categories.
			result.next();
			int rows = result.getInt(1);
			String[] categories = new String[rows];
			
			// Find the categories in the DB.
			sqlStmt = "SELECT catName \n"
						   + "FROM Categories";
			
			// Execute query
			query = conn.prepareStatement(sqlStmt);
			result = query.executeQuery();
			
			// Add categories from the result set to the string array.
			for (int i = 0; i < rows; i++) {
				// Add next category name to categories
				// Only 1 column, always getting the string i the first column.
				result.next();
				categories[i] = result.getString(1);
			}

			return categories;
			
		} catch (SQLException e) {
			TransactionHelper.printErrorToLog(e);
			return null;
		}
	}
	
	/**
	 * Determines if a string is a category in the provided database.
	 * @return The category with correct casing if the string parameter matches a transaction 
	 * category otherwise returns null.
	 */
    public String isACategory(String category) {
    	String[] categories = this.getCategories();
    	
		for (int i = 0; i < categories.length; i++) {
			if (categories[i].equalsIgnoreCase(category)) {
				return categories[i];
			}
		}
		return null;
    }
    
    /**
     * Prints the categories one after another with a comma and space separating each one.
     */
    public void printCategories() {
    	String[] categories = this.getCategories();
		for (int i = 0; i < categories.length - 1; i++) {
			System.out.print(categories[i] + ", ");
		}
		System.out.println("and " + categories[categories.length - 1] + ".");
    }
	
	/**
	 * Changes the full name of a user in the database.
	 * @param user is the user whose name will be changed.
	 * @param newName is the new full name of the user.
	 * @return True if the update was successfully executed.
	 */
	public boolean changeFullName(User user, String newName) {
		// Initialize query and statement.
		PreparedStatement update;
		String sqlStmt = "UPDATE Users SET name = ? WHERE username = ?";
		
		try {
			// Clear the parameters of the query.
			update = this.conn.prepareStatement(sqlStmt);
			update.clearParameters();
			
			// Set parameters
			update.setString(1, newName);
			update.setString(2, user.getUsername());
			
			// Execute update
			update.execute();
			return true;
			
		} catch (SQLException e) {
			TransactionHelper.printErrorToLog(e);
			return false;
		}
	}
	
	/**
	 * Changes the password of a user in the database.
	 * @param user is the user whose password will be updated.
	 * @param newPassword is the new password for this user.
	 * @return True if the update was successfully executed.
	 */
	public boolean changePassword(User user, String newPassword) {
		// Initialize query and statement.
		PreparedStatement update;
		String sqlStmt = "UPDATE Users SET password = ? WHERE username = ?";
		
		try {
			// Clear the parameters of the query.
			update = this.conn.prepareStatement(sqlStmt);
			update.clearParameters();
			
			// Set parameters
			update.setString(1, newPassword);
			update.setString(2, user.getUsername());
			
			// Execute update
			update.execute();
			return true;
			
		} catch (SQLException e) {
			TransactionHelper.printErrorToLog(e);
			return false;
		}
	}
	
	/**
	 * Adds a new category to this DB.
	 * @param category is the new category.
	 * @return True if the category was successfully added.
	 */
	public boolean addCategory(String category) {
		// Initialize query and SQL statement
		PreparedStatement insert;
		String sqlStmt = "INSERT INTO Categories VALUES(?)";
		
		try {
			// Clear parameters
			insert = this.conn.prepareStatement(sqlStmt);
			insert.clearParameters();
			
			// Set parameter
			insert.setString(1, category);
			
			// Execute insert
			insert.execute();
			return true;
			
		} catch (SQLException e) {
			TransactionHelper.printErrorToLog(e);
			return false;
		}
	}
	
	/**
	 * Allows a safe way to execute a query on this database by other code.
	 * @param query The SQL statement executed.
	 * @return The result set returned from executing the provided query.
	 */
	public ResultSet executeQuery(String query) {
		try {
			PreparedStatement stmt = this.conn.prepareStatement(query);
			return stmt.executeQuery();
		} catch (SQLException e) {
			TransactionHelper.printErrorToLog(e);
			return null;
		}
	}
}
