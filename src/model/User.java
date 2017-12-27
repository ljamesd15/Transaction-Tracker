package model;

/** 
 * Holds all the information of a certain transaction. 
 * @author L. James Davidson
 */
public class User {

  private final String username;
  private final String fullName;
  private double balance;
  private final String password;

  /** 
   * Creates a User object. 
   * @param username is the user name of this user.
   * @param fullName is the full name of this user.
   * @param balance is the account balance of this user.
   * @param password is the password of this user.
   */
  public User(String username, String fullName, double balance, String password) {
    this.username = username;
    this.fullName = fullName;
    this.balance = balance;
    this.password = password;
  }
  
  /** @return the user name of this user. */
  public String getUsername() {
	  return this.username;
  }
  
  /** @return the full name of this user. */
  public String getName() {
	  return this.fullName;
  }
  
  /** @param newBalance is the new balance of this user. */
  public void setUserBalance(double newBalance) {
	  this.balance = newBalance;
  }
  
  /** @return the users' account balance. */
  public double getUserBalance() {
	  return this.balance;
  }
  
  /** @return the password of this user. */
  public String getPassword() {
	  return this.password;
  }
  
  @Override
  /** @return the string representation of this user. */
  public String toString() {
	  return this.fullName;
  }
}