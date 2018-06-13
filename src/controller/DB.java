package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Transaction;
import model.User;

/**
 * Allows clients to query and update the database in order to log in, add transactions, and 
 * breakdown expenses via date, price, categories, and more.
 */
public class DB {
	
	private static final int TABLE_ABBREV_LEN = 2; // 1 for the character and 1 for the '.'
	
	// Attribute names for the tables used.
	public static final String T_TABLE = "Transactions";
	public static final char T_TAB_ABBREV = 't';
	public static final String T_DESCRIP = "description";
	public static final String T_PRICE = "price_in_cents";
	public static final String T_DATE = "day";
	public static final String T_MEMO = "memo";
	public static final String T_UCAT = "user_cat";

	public static final String C_TABLE = "Categories";
	public static final char C_TAB_ABBREV = 'c';
	public static final String C_UCAT = "user_cat";
	public static final String C_CAT = "catName";
	public static final String C_USER = "belongsTo";

	public static final String U_TABLE = "Users";
	public static final char U_TAB_ABBREV = 'u';
	public static final String U_USER = "username";
	public static final String U_NAME = "name";
	public static final String U_BAL = "balance_in_cents";
	public static final String U_PASS = "password";
	
	/** Attributes of transactions that will be shown to user which match db attribute names. */
	public static final String[] T_TAB_ATTRIBS = {T_DESCRIP, T_PRICE, T_DATE, T_MEMO, T_UCAT};

	/** Holds the connection to the database. */
	private Connection conn;
	
	// Statements which will be used for beginning, rolling back, and committing DB transactions.
	private PreparedStatement beginTxnStmt;
	private PreparedStatement commitTxnStmt;
	private PreparedStatement abortTxnStmt;
	
	/** Categories which will be automatically added when a user is added to the database. */
	private final String[] defaultCats = {"N/A", "Misc", "Deposit"};
    
    /** Opens a connection with the TransactionsTracker database **/
    public void open() {
        try {
        	Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(
					 "jdbc:sqlite:" + Helper.FILEPATH + "\\data\\TT.db");
			
			// Set up the transaction start, commit, and roll back statements
		    beginTxnStmt = this.conn.prepareStatement("BEGIN TRANSACTION;");
		    commitTxnStmt = this.conn.prepareStatement("COMMIT");
		    abortTxnStmt = this.conn.prepareStatement("ROLLBACK;");
		    
		} catch (SQLException | ClassNotFoundException e) {
			Helper.printErrorToLog(e);
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
		String sqlStmt = "SELECT * FROM " + U_TABLE + "WHERE " + U_USER + " = ?";
		
		try {
			check = this.conn.prepareStatement(sqlStmt);
			check.clearParameters();
			check.setString(1, username);
			ResultSet users = check.executeQuery();
			
			// If there are no values in the result set then this user name is not taken.
			return users.next();
			
		} catch (SQLException e) {
			Helper.printErrorToLog(e);
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
		String sqlStmt = "INSERT INTO " + U_TABLE + " VALUES (?, ?, ?, ?)";
		boolean correctlyExecuted = true;
		
		try {
			addUser = this.conn.prepareStatement(sqlStmt);
			addUser.clearParameters();
			addUser.setString(1, newUser.getUsername());
			addUser.setString(2, newUser.getFullName());
			addUser.setFloat(3, (float) 0.00);
			addUser.setString(4, newUser.getPassword());
			addUser.execute();
			
			// Add each of the default categories for this new user.
			for (int i = 0; i < defaultCats.length; i++) {
				correctlyExecuted = correctlyExecuted && this.addCategory(newUser, defaultCats[i]);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
		return correctlyExecuted;
	}
    
    /**
     * Attempts to log in a user to this database.
     * @returns A user object containing the information for the user which just logged in or 
     * 		null if login failed.
     */
    public User logIn(String username) {
      
  	  // Create query and statement
  	  PreparedStatement logIn;
  	  String sqlStmt = "SELECT * FROM " + U_TABLE + " WHERE " + U_USER + " = ?";
  	  
  	ResultSet results;
  	  try {
  	  	  logIn = conn.prepareStatement(sqlStmt);
  	  	  logIn.clearParameters();
  	  	  logIn.setString(1, username);
  	  	  results = logIn.executeQuery();
  	  	  
  	  	  if (!results.next())
  	  		  return null;
  	  	  return new User(results.getString(U_USER), results.getString(U_NAME), 
  	  			  results.getInt(U_BAL) / 100.0, results.getString(U_PASS)); 
  	  } catch (SQLException e) {
  		  Helper.printErrorToLog(e);
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
    	String sqlStmt = "INSERT INTO " + T_TABLE + " VALUES (?, ?, ?, ?, ?)";
    	      	
    	insert = this.conn.prepareStatement(sqlStmt);
    	insert.clearParameters();
    	insert.setString(1, expense.getDescription());
    	insert.setInt(2, expense.getAmountInCents());
    	insert.setString(3, expense.getDate().toString());
    	insert.setString(4, expense.getMemo());
    	insert.setString(5, username + "_" + expense.getCategory());
		insert.execute();
    }
    
    /**
     * Updates a users balance by an amount in the database.
     * @param username is the user whose balance will be updated in the database.
     * @param amount is the change applied to the user's balance.
     * @return The new account balance.
     * @throws SQLException if there was a problem updating the user's balance.
     */
    public int updateBalance(String username, int amount) throws SQLException {
    	// Initialize both SQL statement and the prepared statements.
    	PreparedStatement check;
    	String checkStmt = "SELECT " + U_BAL + " FROM " + U_TABLE + " WHERE " + U_USER + " = ?";
    	PreparedStatement update;
    	String updateStmt = "UPDATE " + U_TABLE + " SET " + U_BAL + " = ? WHERE " + U_USER + " = ?";
    	
    	// Get current balance value
		check = this.conn.prepareStatement(checkStmt);
		check.clearParameters();
		check.setString(1, username);
		ResultSet checkResult = check.executeQuery();
		
		checkResult.next();
		int balance = checkResult.getInt(1);
		
		// Calculate & update new balance
		balance = balance + amount;
		update = this.conn.prepareStatement(updateStmt);
		update.clearParameters();
		update.setInt(1, balance);
		update.setString(2, username);
		update.execute();
		return balance;
    }
    
    /**
     * Gets the categories which are available to the specified user.
     * @return A list of strings containing the available categories or null if there was
     * 		an exception which prevented the database from being accessed properly.
     */
	public List<String> getCategories(User user) {
		// Find the categories in the DB.
		String sqlStmt = "SELECT " + C_CAT + " FROM " + C_TABLE + " WHERE " + C_USER + " = ?";
		try {
			PreparedStatement query = conn.prepareStatement(sqlStmt);			
			query.clearParameters();
			query.setString(1, user.getUsername());
			ResultSet result = query.executeQuery();
			List<String> categories = new ArrayList<String>();
			
			while (result.next()) {
				categories.add(result.getString(1));
			}
			return categories;
		} catch (SQLException e) {
			Helper.printErrorToLog(e);
			return null;
		}
	}
	
	/**
	 * Determines if a string matches a category for the specified user.
	 * @return The category with correct casing if the string parameter matches a transaction 
	 * category otherwise returns null.
	 */
    public String isACategory(User user, String category) {
    	List<String> categories = this.getCategories(user);
		for (int i = 0; i < categories.size(); i++) {
			if (categories.get(i).equalsIgnoreCase(category)) {
				return categories.get(i);
			}
		}
		return null;
    }
    
    /**
     * Prints the categories available to the specified user
     * one after another with a comma and space separating each one.
     */
    public void printCategories(User user) {
    	List<String> categories = this.getCategories(user);
		for (int i = 0; i < categories.size() - 1; i++) {
			System.out.print(categories.get(i) + ", ");
		}
		System.out.println("and " + categories.get(categories.size() - 1) + ".");
    }

	/**
	 * Changes the full name of a user in the database.
	 * @param user is the user whose name will be changed.
	 * @param newName is the new full name of the user.
	 * @return True if the update was successfully executed.
	 */
	public boolean changeFullName(User user, String newName) {
		PreparedStatement update;
		String sqlStmt = "UPDATE " + U_TABLE + " SET " + U_NAME + " = ? WHERE " + U_USER + " = ?";
		
		try {
			update = this.conn.prepareStatement(sqlStmt);
			update.clearParameters();
			update.setString(1, newName);
			update.setString(2, user.getUsername());
			update.execute();
			return true;
			
		} catch (SQLException e) {
			Helper.printErrorToLog(e);
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
		PreparedStatement update;
		String sqlStmt = "UPDATE " + U_TABLE + " SET " + U_PASS + " = ? WHERE " + U_USER + " = ?";
		
		try {
			update = this.conn.prepareStatement(sqlStmt);
			update.clearParameters();
			update.setString(1, newPassword);
			update.setString(2, user.getUsername());
			update.execute();
			return true;
			
		} catch (SQLException e) {
			Helper.printErrorToLog(e);
			return false;
		}
	}
	
	/**
	 * Adds a new category to this DB for the specified user.
	 * @param user The user which will have access to the specified category.
	 * @param category is the new category for the specified user.
	 * @return True if the category was successfully added.
	 */
	public boolean addCategory(User user, String category) {
		PreparedStatement insert;
		String sqlStmt = "INSERT INTO " + C_TABLE + " VALUES(?, ?, ?)";
		
		try {
			insert = this.conn.prepareStatement(sqlStmt);
			insert.clearParameters();
			insert.setString(1, user.getUsername() + "_" + category);
			insert.setString(2, user.getUsername());
			insert.setString(3, category);
			insert.execute();
			return true;
			
		} catch (SQLException e) {
			Helper.printErrorToLog(e);
			return false;
		}
	}
	
	
	
	/**
	 * Prepares a SQL statement which can be used to queried a user's transaction history.
	 * @param user THe user whose transaction's will be queried.
	 * @param orderByClauses The order in which the output should be ordered.
	 * @param whereClauses Filters which will be applied to the user's transaction history.
	 * @return A SQL statement to be executed and get the user's requested transaction history.
	 */
	public String prepareHistoryQuery(User user, String[] orderByClauses, List<String> whereClauses) {
		// Construct query using given user specifications.
		String stmt = "SELECT ";
		for (int i = 0; i < T_TAB_ATTRIBS.length; i++) {
			stmt += T_TAB_ABBREV + "." + T_TAB_ATTRIBS[i] + ", ";
		}
		stmt += C_TAB_ABBREV + ".catName FROM Transactions " + T_TAB_ABBREV 
				+ ", Categories " + C_TAB_ABBREV + " WHERE ";
		
		List<String> attributeSpecificClauses;
		for (int i = 0; i < T_TAB_ATTRIBS.length; i++) {
			attributeSpecificClauses = new ArrayList<String>();
			String attribute = T_TAB_ATTRIBS[i];
			
			for (int j = 0; j < whereClauses.size(); j++) {
				// If the where clause matches the attribute add to attributeSpecificClauses
				String clause = whereClauses.get(j);
				if (clause.substring(0, attribute.length() + TABLE_ABBREV_LEN)
						.equals(T_TAB_ABBREV + "." + attribute))
					attributeSpecificClauses.add(clause);
			}
			
			if (attributeSpecificClauses.size() == 0)
				continue;
			// 'or' the clauses which have the same attribute together.
			stmt += "(";
			for (int k = 0; k < attributeSpecificClauses.size() - 1; k++) {
				whereClauses.remove(attributeSpecificClauses.get(k));
				stmt += attributeSpecificClauses.get(k) + " OR ";
			}
			stmt += attributeSpecificClauses.get(attributeSpecificClauses.size() - 1) + ") AND ";
			whereClauses.remove(attributeSpecificClauses.get(attributeSpecificClauses.size() - 1));
			// Removing used where clauses to reduce scanning time for next attribute.
		}
		
		stmt += T_TAB_ABBREV + ".user_cat = " + C_TAB_ABBREV + ".user_cat ORDER BY ";
		
		for (int i = 0; i < T_TAB_ATTRIBS.length; i++) {
			stmt += orderByClauses[i];
			if (i == T_TAB_ATTRIBS.length - 1 || orderByClauses[i + 1] == null) {
				// Last ordering attribute
				stmt += ";";
				break;
			} else {
				stmt += ", ";
			}
		}
		
		return stmt;
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
			Helper.printErrorToLog(e);
			return null;
		}
	}
}
