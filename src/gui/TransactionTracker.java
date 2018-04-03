package gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import controller.TransactionDB;
import controller.TransactionHelper;
import model.BCrypt;
import model.Transaction;
import model.User;

public class TransactionTracker {
	
	// Fonts for the GUI
	private static final Font titleFont = new Font(Font.SERIF, Font.BOLD, 20);
	private static final Font appFont = new Font(Font.SERIF, Font.PLAIN, 14);
	
	// Max number of incorrect password attempts.
	private int LOGIN_ATTEMPTS_REMAINING = 5;
	
	// Number of recent transaction to display
	private static final int REC_TRANS_TO_DISP = 5;
	
	// Create a new user constants
	private static final int MAX_USERNAME_CHARS = 30;
	private static final int MIN_USERNAME_CHARS = 1;
	private static final int MAX_FULLNAME_CHARS = 50;
	private static final int MAX_PASSWORD_CHARS = 30;
	private static final int MIN_PASSWORD_CHARS = 8;
	
	// The Transaction-Tracker DB
	private final TransactionDB db;
	
	// Currently logged in user
	private User currentUser;
	
	// Main GUI frame & panel
	private JFrame mainFrame;
	private JPanel mainPanel;
	
	/** Create a new GUI Transaction Tracker application */
	private TransactionTracker(TransactionDB db) {
		this.db = db;
	}
	
	/**
	 * Entry point into the Transaction Tracker GUI
	 * @param args
	 * @throws SQLException
	 */
	public static void main(String args[]) throws SQLException {
    	TransactionHelper.prepare();
		String dbFilePath = (new File("")).getAbsolutePath() + "\\src\\model\\TT.db";
		TransactionDB db = new TransactionDB(dbFilePath);
    	TransactionTracker app = new TransactionTracker(db);
    	app.run();
	}
	
	/**
	 * Runs the Transaction Tracker GUI
	 */
	private void run() {
		this.intialiseFrame();
		
		// Close the db when the mainFrame is closed.
		this.mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					db.close();
				} catch (SQLException ex) {
					TransactionHelper.printErrorToLog(ex);
					e.getWindow().dispose();
				}
				
			}
		});
		
		this.createNewMainPanel();
		this.createWelcomePage(this.mainPanel, false);
		this.mainFrame.setVisible(true);
	}
	
	/** Creates a main GUI frame, sets mainFrame and sets mainPanel class variables. */
	private void intialiseFrame() {
		this.mainFrame = new JFrame("Transaction Tracker");
		this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.mainFrame.setSize(500, 500);
		UIManager.put("Label.font", appFont);
		UIManager.put("Button.font", appFont);
		SwingUtilities.updateComponentTreeUI(this.mainFrame);
	}
	
	/** Creates a fresh mainPanel with a box layout, the largest panel in mainFrame. */
	private void createNewMainPanel() {
		Container c = this.mainFrame.getContentPane();
		if (this.mainPanel != null) {
			c.remove(this.mainPanel);
		}
		this.mainPanel = new JPanel();
		this.mainPanel.setLayout(new BoxLayout(this.mainPanel, BoxLayout.PAGE_AXIS));
		c.add(this.mainPanel);
	}
	
	/** Creates the opening page for the Transaction Tracker GUI 
	 * @param panel The JPanel which the welcome page will be displayed on.
	 * @param invalidSignIn Determines whether a user will be alerted of an invalid sign-in 
	 * attempt.
	 */
	private void createWelcomePage(JPanel panel, boolean invalidSignIn) {		
		// Add title
		this.addTitle(panel);
		
		// Add greeting
		JPanel greeting = new JPanel(new FlowLayout(FlowLayout.LEFT));
		greeting.add(new JLabel("Good " + TransactionHelper.getPeriodOfDay() + "!"));
		panel.add(greeting);
		
		// Add sign up area
		this.addSignUpField(this.mainPanel);

		// Add sign in area
		this.addSignInField(panel, invalidSignIn);
	}
	
	/** Adds a title of "Transaction Tracker" to the panel. */
	private void addTitle(JPanel panel) {
		JPanel title = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel titleLabel = new JLabel("Transaction Tracker");
		titleLabel.setFont(titleFont);
		title.add(titleLabel);
		panel.add(title);
	}
	
	/** Adds a sign up field to the parameter panel. */
	private void addSignUpField(JPanel panel) {
		JPanel signUp = new JPanel();
		signUp.setLayout(new BoxLayout(signUp, BoxLayout.PAGE_AXIS));
		
		signUp.add((new JPanel(new FlowLayout(FlowLayout.LEFT)).add(
				new JLabel("Don't have an account?"))));
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton signUpButton = new JButton("Sign up");
		signUpButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				createNewMainPanel();
				createSignUpPage(mainPanel);
				mainFrame.revalidate();
			}
			
		});
		
		buttonPanel.add(signUpButton);
		
		signUp.add(buttonPanel);
		JPanel outerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		outerPanel.add(signUp);
		panel.add(outerPanel);
	}
	
	/** Creates a sign up page on the parameter panel. */
	private void createSignUpPage(JPanel panel) {
		
		this.addTitle(panel);
		
		// Get the desired user name
		JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		usernamePanel.add(new JLabel("Username"));
		JTextField username = new JTextField(20);
		usernamePanel.add(username);
		
		// Add two user name related error messages and make initially invisible
		JLabel usernameTakenMessage = new JLabel("Username is taken.");
		JLabel usernameMessage = new JLabel("Username must be greater than " + MIN_USERNAME_CHARS 
				+ " and less than " + MAX_USERNAME_CHARS + " characters long.");
		usernameTakenMessage.setForeground(Color.RED);
		usernameMessage.setForeground(Color.RED);
		usernamePanel.add(usernameTakenMessage);
		usernamePanel.add(usernameMessage);
		usernameMessage.setVisible(false);
		usernameTakenMessage.setVisible(false);
		
		panel.add(usernamePanel);
		
		
		// Get the user's full name
		JPanel fullNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		fullNamePanel.add(new JLabel("Full name"));
		JTextField fullName = new JTextField(20);
		fullNamePanel.add(fullName);
		
		// Add full name length error message and set invisible
		JLabel fullNameMessage = new JLabel("Your name must be less than " + MAX_FULLNAME_CHARS 
				+ " characters.");
		fullNameMessage.setForeground(Color.RED);
		fullNamePanel.add(fullNameMessage);
		fullNameMessage.setVisible(false);
		
		panel.add(fullNamePanel);
		
		
		// Get the user desired password
		JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		passwordPanel.add(new JLabel("Password"));
		JPasswordField password = new JPasswordField(20);
		passwordPanel.add(password);
		
		// Add password length error message and set invisible
		JLabel passwordMessage = new JLabel("Password must be between " + MIN_PASSWORD_CHARS 
				+ " - " + MAX_PASSWORD_CHARS + " characters.");
		passwordPanel.add(passwordMessage);
		passwordMessage.setForeground(Color.RED);
		passwordMessage.setVisible(false);
		
		panel.add(passwordPanel);
		
		
		// Confirm the user has typed in the password correctly.
		JPanel confirmPasswordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		confirmPasswordPanel.add(new JLabel("Confirm password"));
		JPasswordField confirmedPassword = new JPasswordField(20);
		confirmPasswordPanel.add(confirmedPassword);
		
		// Add password matching error message, set invisible.
		JLabel confirmedMessage = new JLabel("Passwords do not match.");
		confirmPasswordPanel.add(confirmedMessage);
		confirmedMessage.setForeground(Color.RED);
		confirmedMessage.setVisible(false);
		
		panel.add(confirmPasswordPanel);
		
		
		// Button to create the account
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton signUp = new JButton("Create account");
		signUp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {				
				// Get the values from the text fields
				String usernameStr = username.getText();
				String fullNameStr = fullName.getText();
				char[] passwordArr = password.getPassword();
				char[] confirmedPasswordArr = confirmedPassword.getPassword();
				password.setText("");
				confirmedPassword.setText("");
				
				// Get hashed password
				String pass = BCrypt.hashpw(passwordArr.toString(), BCrypt.gensalt());
				
				// Compare the two passwords and clear passwordArr
				boolean passwordsMatch = passwordArr.length == confirmedPasswordArr.length;	
				for (int i = 0; i < Math.min(passwordArr.length, 
						confirmedPasswordArr.length); i++) {
					passwordsMatch = passwordsMatch && passwordArr[i] == confirmedPasswordArr[i];
					passwordArr[i] = 0;
				}

				// Clear confirmedPasswordArr
				for (int i = 0; i < confirmedPasswordArr.length; i++) {
					confirmedPasswordArr[i] = 0;
				}
				
				// Remove all previous error messages
				usernameTakenMessage.setVisible(false);
				usernameMessage.setVisible(false);
				fullNameMessage.setVisible(false);
				passwordMessage.setVisible(false);
				confirmedMessage.setVisible(false);
				
				if (db.isUsernameTaken(usernameStr)) {
					usernameTakenMessage.setVisible(true);
					
				} else if (usernameStr.length() > MAX_USERNAME_CHARS 
						|| usernameStr.length() < MIN_USERNAME_CHARS) {
					usernameMessage.setVisible(true);
					
				} else if (fullNameStr.length() > MAX_FULLNAME_CHARS) {
					fullNameMessage.setVisible(true);
					
				} else if (passwordArr.length > MAX_PASSWORD_CHARS 
						|| passwordArr.length < MIN_PASSWORD_CHARS) {
					passwordMessage.setVisible(true);
					
				} else if (!passwordsMatch) {
					confirmedMessage.setVisible(true);
					
				} else {
					// No errors
					User newUser = new User(usernameStr, fullNameStr, 0.0, pass);
					boolean added = db.addNewUser(newUser);
					
					createNewMainPanel();
					createWelcomePage(mainPanel, false);
					
					JPanel response = new JPanel(new FlowLayout(FlowLayout.CENTER));
					JLabel message = new JLabel();
					message.setFont(new Font(Font.SERIF, Font.BOLD, 16));
					response.add(message);
					mainPanel.add(response);
					
					if (added) {
						message.setText("Successfully added user.");
					} else {
						// User was unable to be added to the db
						message.setText("Unable to add user, please try again later.");
					}
				}
				
				mainFrame.revalidate();
			}
			
		});
		buttonPanel.add(signUp);
		panel.add(buttonPanel);
	}
	
	/** Adds a sign in field to the parameter panel. 
	 * @param panel The JPanel which the sign in field page will be displayed on.
	 * @param invalidSignIn Determines whether a user will be alerted of an invalid sign-in 
	 * attempt.
	 */
	private void addSignInField(JPanel panel, boolean invalidSignIn) {
		JPanel userFields = new JPanel();
		userFields.setLayout(new BoxLayout(userFields, BoxLayout.PAGE_AXIS));
		
		// Add alert to user if there was an invalid log in attempt
		if (invalidSignIn) {
			JPanel alert = new JPanel();
			alert.setLayout(new BoxLayout(alert, BoxLayout.PAGE_AXIS));
			alert.add(new JLabel("Invalid username or password."));
			alert.add(new JButton("Forgot password?"));
			
			if (LOGIN_ATTEMPTS_REMAINING > 1) {
				alert.add(new JLabel("You have " + LOGIN_ATTEMPTS_REMAINING + " attempts left."));
			} else {
				alert.add(new JLabel("You have 1 attempt left."));
			}
			JPanel invalidNotice = new JPanel(new FlowLayout(FlowLayout.LEFT));
			invalidNotice.add(alert);
			userFields.add(invalidNotice);
		}
		
		// User name text box
		JPanel username = new JPanel(new FlowLayout(FlowLayout.LEFT));
		username.add(new JLabel("Username "));
		JTextField usernameField = new JTextField(20);
		username.add(usernameField);
		userFields.add(username);
		
		// Password text box
		JPanel password = new JPanel(new FlowLayout(FlowLayout.LEFT));
		password.add(new JLabel("Password "));
		JPasswordField passwordField = new JPasswordField(20);
		password.add(passwordField);
		userFields.add(password);
		
		// Sign in button
		JPanel signIn = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton signInButton = new JButton("Sign-in");
		signInButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				char[] password = passwordField.getPassword();
				passwordField.setText("");
				String username = usernameField.getText();
				User loggedInUser = signIn(username, password);
				
				// Clear password array
				for (int i = 0; i < password.length; i++) {
					password[i] = 0;
				}
				
				if (loggedInUser == null) {
					// Incorrect log-in attempt
					LOGIN_ATTEMPTS_REMAINING--;
					if (LOGIN_ATTEMPTS_REMAINING <= 0) {
						usernameField.setEnabled(false);
						passwordField.setEnabled(false);
					} else {
						createNewMainPanel();
						createWelcomePage(mainPanel, true);
						mainFrame.revalidate();
					}
					
				} else {
					currentUser = loggedInUser;
					createNewMainPanel();
					createUserWelcomePage(mainPanel);
					mainFrame.revalidate();
				}
			}
		});
		signIn.add(signInButton);
		userFields.add(signIn);


		JPanel outerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		outerPanel.add(userFields);
		panel.add(outerPanel);
	}
	
	/**
	 * Returns the user object whose user name and password match the parameters. Returns null 
	 * if there is no user name and password combination in the db which matches the parameters.
	 * @param username is the user name of the user attempted to be logged in
	 * @param password is the password of the user attempted to be logged in
	 * @return The user object who is now logged in.
	 */
	private User signIn(String username, char[] password) {
		User u = this.db.logIn(username);
		
		if (u == null) {
			return null;
		} else {
			return BCrypt.checkpw(new String(password), u.getPassword()) ? u : null;
		}
	}
	
	/**
	 * Creates a user based welcome page. Used to welcome a recently logged in user.
	 * @param panel The JPanel which will have the user welcome page displayed on it.
	 * @requires currentUser must be non-null.
	 */
	private void createUserWelcomePage(JPanel panel) {
		// Title and greeting
		this.addTitle(panel);
		JPanel greeting = new JPanel(new FlowLayout(FlowLayout.LEFT));
		greeting.add(new JLabel("Good " + TransactionHelper.getPeriodOfDay() + " " 
				+ this.currentUser.getFullName()));
		panel.add(greeting);
		
		// Display current user balance
		this.addCurrUserBalance(panel);
		
		// Display recent transactions
		this.addRecentUserTransactions(this.mainPanel, this.currentUser, REC_TRANS_TO_DISP);
		
		// Add buttons for each of the user options
		this.addMainMenuOptions(this.mainPanel);
	}
	
	/** Adds the current user's balance to the parameter panel. 
	 * @requires currentUser must be non-null
	 */
	private void addCurrUserBalance(JPanel panel) {
		String balance = String.format("%-12.2f", this.currentUser.getUserBalance());
		
		JPanel userBalance = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		userBalance.add(new JLabel("Current balance: $" + balance 
				+  " (as of " + LocalDateTime.now() + ")"));
		panel.add(userBalance);
	}
	
	/**
	 * Adds recent transactions of a specific user to the parameter panel.
	 * @param panel THe JPanel which will have recent user transactions added to it.
	 * @param user The user whose recent transactions will be displayed.
	 * @param numOfTransactions The number of recent transactions to be displayed.
	 */
	private void addRecentUserTransactions(JPanel panel, User user, int numOfTransactions) {
		JPanel transactions = new JPanel();
		transactions.setLayout(new BoxLayout(transactions, BoxLayout.PAGE_AXIS));
		
		JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		String header = String.format("%-30s%-14s%-12s%-20s%-30s", "Location", "Amount", "Date", 
				"Memo", "Category");
		headerPanel.add(new JLabel(header));
		transactions.add(headerPanel);
		
		Transaction[] recentTrans = this.db.getRecentTransactions(user, numOfTransactions);
		
		for (int i = 0; i < recentTrans.length; i++) {
			JPanel currTransPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			currTransPanel.add(new JLabel(recentTrans[i].toString()));
			transactions.add(currTransPanel);
		}
		
		panel.add(transactions);
	}
	
	/** Adds main menu options to the parameter panel. */
	private void addMainMenuOptions(JPanel panel) {
		String option1 = "Add a new transaction";
		String option2 = "Show transacton history";
		String option3 = "User settings";
		String option4 = "Program settings";
		
		GridLayout grid = new GridLayout(2, 2);
		JPanel buttons = new JPanel(grid);
		
		buttons.add(new JButton(option1));
		buttons.add(new JButton(option2));
		buttons.add(new JButton(option3));
		buttons.add(new JButton(option4));
		
		panel.add(buttons);
	}
}
