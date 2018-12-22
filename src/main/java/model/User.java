package model;

/** 
 * Holds all the information of a certain transaction. 
 * @author L. James Davidson
 */
public final class User {

  private final String username;
  private String fullName;
  private double balance;
  private String password;

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
  
  /** @return The user name of this user. */
  public String getUsername() {
	  return this.username;
  }
  
  /** @param fullName is the new full name of this user. */
  public void setUserFullName(String fullName) {
	  this.fullName = fullName;
  }
  
  /** @return The full name of this user. */
  public String getFullName() {
	  return this.fullName;
  }
  
  /** @param newBalance is the new balance of this user. */
  public void setUserBalance(double newBalance) {
	  this.balance = newBalance;
  }
  
  /** @return The users' account balance. */
  public double getUserBalance() {
	  return this.balance;
  }
  
  /** @param password is the new password of this user. */
  public void setPassword(String password) {
	  this.password = password;
  }
  
  /** @return The password of this user. */
  public String getPassword() {
	  return this.password;
  }
  
  @Override
  /** @return the string representation of this user. */
  public String toString() {
	  return this.fullName;
  }
}