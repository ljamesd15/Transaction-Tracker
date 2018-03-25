package gui;

import java.awt.Color;
import java.awt.Component;
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
		String dbFilePath = (new File("")).getAbsolutePath() + "\\data\\TT.db";
		TransactionDB db = new TransactionDB(dbFilePath);
	      
    	TransactionHelper.prepare();
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
		JPanel usernamePanel = this.createSignUpUsername(null);
		panel.add(usernamePanel);
		
		// Get the user's full name
		JPanel fullNamePanel = this.createSignUpFullName(null);
		panel.add(fullNamePanel);
		
		// Get the user desired password
		JPanel passwordPanel = this.createSignUpPassword();
		panel.add(passwordPanel);
		
		// Confirm the user has typed in the password correctly.
		JPanel confirmPasswordPanel = this.createSignUpPassword();
		panel.add(confirmPasswordPanel);
		
		// Button to create the account
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton signUp = new JButton("Create account");
		signUp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Get text and password fields
				JTextField username = getTextField(usernamePanel);
				JTextField fullName = getTextField(fullNamePanel);
				JPasswordField password = getPasswordField(passwordPanel);
				JPasswordField confirmedPassword = getPasswordField(confirmPasswordPanel);
				
				// Get the values from the text fields
				String usernameStr = username.getText();
				String fullNameStr = fullName.getText();
				char[] passwordStr = password.getPassword();
				char[] confirmedPasswordStr = confirmedPassword.getPassword();
				
							
				// Compare the two passwords
				boolean passwordsMatch = true;	
				for (int i = 0; i < passwordStr.length; i++) {
					passwordsMatch = passwordsMatch && passwordStr[i] == confirmedPasswordStr[i];
				}
				
				// Get hashed password
				String pass = BCrypt.hashpw(passwordStr.toString(), BCrypt.gensalt());
				
				// Clear password char arrays
				for (int i = 0; i < passwordStr.length; i++) {
					passwordStr[i] = 0;
				}
				for (int i = 0; i < confirmedPasswordStr.length; i++) {
					confirmedPasswordStr[i] = 0;
				}
				
				// Remove previous error messages
				panel.removeAll();
				panel.add(createSignUpUsername(null));
				panel.add(createSignUpFullName(null));
				panel.add(createSignUpPassword());
				panel.add(createSignUpPassword());
				
				if (!passwordsMatch) {
					// Alert user of passwords not matching
					password.setText("");
					confirmedPassword.setText("");
					
					JLabel message = new JLabel("Passwords do not match.");
					message.setForeground(Color.RED);
					((Container)(panel.getComponent(3))).add(message);
					
				} else if (db.isUsernameTaken(usernameStr) || usernameStr.equals("") 
						|| usernameStr == null) {
					// If its null then set it to an empty string for the method call
					if (usernameStr == null) {
						usernameStr = "";
					}
					
				} else {
					// Everything is good
				}
				
				mainFrame.revalidate();
			}
			
		});
		buttonPanel.add(signUp);
		panel.add(buttonPanel);
	}
	
	/** Creates and returns a JPanel which will contain the sign up user name field. 
	 * @param username Text which will be initially displayed in the user name text box. Pass null
	 * if you wish to have nothing displayed.
	 */
	private JPanel createSignUpUsername(String username) {
		JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		usernamePanel.add(new JLabel("Username"));
		if (username != null) {
			usernamePanel.add(new JTextField(username, 20));
		} else {
			usernamePanel.add(new JTextField(20));
		}
		return usernamePanel;
	}
	
	/**
	 * Creates and returns a JPanel which will contain the sign up full name field.
	 * @param fullName Text which will be initially displayed in the user name text box. Pass null
	 * if you wish to have nothing displayed.
	 */
	private JPanel createSignUpFullName(String fullName) {
		JPanel fullNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		fullNamePanel.add(new JLabel("Full name"));
		if (fullName != null) {
			fullNamePanel.add(new JTextField(fullName, 20));
		} else {
			fullNamePanel.add(new JTextField(20));
		}
		return fullNamePanel;
	}
	
	/** Creates and returns a JPanel which will contain the sign up password field. */
	private JPanel createSignUpPassword() {
		JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		passwordPanel.add(new JLabel("Confirm password"));
		passwordPanel.add(new JPasswordField(20));
		return passwordPanel;
	}
	
	/** Gets the only JTextField from the parameter panel. 
	 * @return A JTextField from panel, null if there is no JTextField.
	 */
	private JTextField getTextField(JPanel panel) {
		for (Component comp : panel.getComponents()) {
			if (comp.getClass().equals(JTextField.class)) {
				return (JTextField) comp;
			}
		}
		return null;
	}
	
	/** Gets the only JPasswordField from the parameter panel. 
	 * @return A JPasswordField from panel, null if there is no JPasswordField.
	 */
	private JPasswordField getPasswordField(JPanel panel) {
		for (Component comp : panel.getComponents()) {
			if (comp.getClass().equals(JPasswordField.class)) {
				return (JPasswordField) comp;
			}
		}
		return null;
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
				User loggedInUser = signIn(username, password.toString());
				
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
	private User signIn(String username, String password) {
		User u = this.db.logIn(username);
		
		if (u == null) {
			return null;
		} else {
			return BCrypt.checkpw(password, u.getPassword()) ? u : null;
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
