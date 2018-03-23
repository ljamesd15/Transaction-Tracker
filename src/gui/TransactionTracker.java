package gui;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

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
import model.User;

public class TransactionTracker {
	
	// Fonts for the GUI
	private static final Font titleFont = new Font(Font.SERIF, Font.BOLD, 20);
	private static final Font appFont = new Font(Font.SERIF, Font.PLAIN, 14);
	
	// Max number of incorrect password attempts.
	private int INCORRECT_LOGIN_ATTEMPTS_REMAINING = 3;
	
	// The Transaction-Tracker DB
	private final TransactionDB db;
	
	// Currently logged in user
	private User currentUser;
	
	// Main GUI frame
	private JFrame mainFrame;
	
	private TransactionTracker(TransactionDB db) {
		this.db = db;
	}
	
	/**
	 * Entry point into the Transaction Tracker GUI
	 * @param args
	 * @throws SQLException
	 */
	public static void main(String args[]) throws SQLException {
		TransactionDB db = new TransactionDB();
	    db.open();
	      
	    try {
	    	//db.prepare();
	    	TransactionHelper.prepare();
	    	TransactionTracker app = new TransactionTracker(db);
	    	app.run();
	    } finally {
	    	db.close();
	    }
	}
	
	/**
	 * Runs the Transaction Tracker GUI
	 */
	private void run() {
		JPanel panel = intialiseFrame();
		addTitle(panel);
		addSignUpArea(panel);
		addSignInArea(panel);
	}
	
	/**
	 * Creates a main GUI frame and sets mainFrame class variable
	 * @return The largest JPanel in the mainFrame.
	 */
	private JPanel intialiseFrame() {
		JFrame frame = new JFrame("Transaction Tracker");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		UIManager.put("Label.font", appFont);
		UIManager.put("Button.font", appFont);
		SwingUtilities.updateComponentTreeUI(frame);
		
		Container c = frame.getContentPane();
		JPanel panelOfPanels = new JPanel();
		panelOfPanels.setLayout(new BoxLayout(panelOfPanels, BoxLayout.PAGE_AXIS));
		c.add(panelOfPanels);
		
		frame.setSize(500, 500);
		this.mainFrame = frame;
		return panelOfPanels;
	}
	
	/**
	 * Adds a title panel to the parameter panel.
	 * @param panel the JPanel which will have a title panel added to it.
	 */
	private void addTitle(JPanel panel) {
		JPanel title = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel titleLabel = new JLabel("Transaction Tracker");
		titleLabel.setFont(titleFont);
		title.add(titleLabel);
		
		panel.add(title);
	}
	
	/**
	 * Adds a sign up panel to the parameter panel.
	 * @param panel is JPanel which will have a sign-up panel added to it.
	 */
	private void addSignUpArea(JPanel panel) {
		
	}
	
	/**
	 * Adds a log in panel to parameter panel.
	 * @param panel the JPanel which will have a log in panel added to it.
	 */
	private void addSignInArea(JPanel panel) {
		// User info text boxes
		JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel userFields = new JPanel();
		userInfo.add(userFields);
		userFields.setLayout(new BoxLayout(userFields, BoxLayout.PAGE_AXIS));
		
		// User name text box
		JPanel username = new JPanel(new FlowLayout(FlowLayout.LEFT));
		username.add(new JLabel("Username "));
		JTextField usernameField = new JTextField(20);
		username.add(usernameField);
		userFields.add(username);
		
		// Password text box
		JPanel password = new JPanel(new FlowLayout(FlowLayout.LEFT));
		password.add(new JLabel("Password "));
		JTextField passwordField = new JPasswordField(20);
		password.add(passwordField);
		userFields.add(password);
		
		// Sign in button
		JPanel signIn = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton signInButton = new JButton("Sign-in");
		signInButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String password = passwordField.getText();
				passwordField.setText("");
				String username = usernameField.getText();
				User loggedInUser = signIn(username, password);
				
				if (loggedInUser == null) {
					// Incorrect log-in attempt
					INCORRECT_LOGIN_ATTEMPTS_REMAINING--;
					if (INCORRECT_LOGIN_ATTEMPTS_REMAINING == 0) {
						usernameField.setEnabled(false);
						passwordField.setEnabled(false);
					}
				} else {
					currentUser = loggedInUser;
				}
			}
		});
		signIn.add(signInButton);
		userFields.add(signIn);
		
		panel.add(userInfo);
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
}
