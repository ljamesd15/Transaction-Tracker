package model;

import java.time.LocalDate;

public class Transaction {

	private String location;
	private double amount;
	private LocalDate date;
	private CategoryTag category;
	private String memo;
	
	// AF: 
	// The location of the transaction must be non-null. 
	// The date must be non-null
	// The category of this must be non-null.
	// The memo of the transaction must be non-null.
	
	// RI: location != null, date != null, category != null, memo != null,
	
	
	/**
	 * Creates a new immutable transaction object using the builder object.
	 * @param builder is a TransactionBuilder containing all the information 
	 * necessary to create a transaction object.
	 */
	private Transaction(TransactionBuilder builder) {
		this.location = builder.location;
		this.amount = builder.amount;
		this.date = builder.date;
		this.category = builder.category;
		this.memo = builder.memo;
		
		this.checkRep();
	}
	
	/**
	 * @return The location of where this transaction was made.
	 */
	public String getLocation() {
		return this.location;
	}
	
	/**
	 * @return The amount that was transfered due to this transaction.
	 */
	public double getAmount() {
		return this.amount;
	}
	
	/**
	 * @return The date when this transaction occurred.
	 */
	public LocalDate getDate() {
		return this.date;
	}
	
	/**
	 * @return Whether this transaction was a withdrawal.
	 */
	public boolean isWithdrawal() {
		return this.amount < 0.0;
	}
	
	/**
	 * @return The category of the this transaction.
	 */
	public CategoryTag getCategory() {
		return this.category;
	}
	
	/**
	 * @return The memo attached to this transaction.
	 */
	public String getMemo() {
		return this.memo;
	}
	
	
	/**
	 * A builder object for the Transaction object.
	 * @author L. James Davidson
	 */
	public static class TransactionBuilder {
		
		private double amount;
		private String location;
		private boolean isWithdrawal;
		private CategoryTag category;
		private String memo;
		private LocalDate date;
		
		/**
		 * Creates a TransactionBuilder object which can be slowly fleshed out until all 
		 * information has been filled in. Once enough information has been filled in then a
		 * Transaction object may be built.
		 */
		public TransactionBuilder() {
			
		}
		
		/**
		 * Sets the location of the this transaction.
		 * @param loc is the location of the of transaction.
		 * @modifies The location of the transaction.
		 * @effects Sets the location of this transaction to the parameter.
		 * @throws IllegalArgumentException if the parameter is null.
		 * @return True if the location was successfully set to this.
		 */
		public boolean setLocation(String loc) {
			if (loc == null) {
				throw new IllegalArgumentException("The location of the "
						+ "transaction must be non-null.");
			} else {
				this.location = loc;
				return true;
			}
		}
		
		/**
		 * @return The location of the transaction.
		 */
		public String getLocation() {
			return this.location;
		}
		
		/**
		 * Sets the amount of the this to the parameter.
		 * @param amount is the amount that was transfered during this transaction.
		 * @return True if the amount of this was successfully set.
		 */
		public boolean setAmount(double amount) {
			if (this.isWithdrawal) {
				this.amount = -1 * amount;
			} else {
				this.amount = amount;
			}
			
			return true;
		}
		
		/**
		 * @return the amount of this transaction.
		 */
		public double getAmount() {
			return this.amount;
		}
		
		/**
		 * Sets the date of this transaction.
		 * @param date is the LocalDate object representing the date of the transaction.
		 * @return True if the date was set successfully.
		 */
		public boolean setDate(LocalDate date) {
			this.date = date;
			return true;
		}
		
		/**
		 * @return The date of this transaction.
		 */
		public LocalDate getDate() {
			return this.date;
		}
		
		public boolean setAsWithdrawal(boolean isWithdrawal) {
			this.isWithdrawal = isWithdrawal;
			
			// Update the amount of the transaction if necessary.
			if ((this.isWithdrawal && this.amount > 0) ||
				 !this.isWithdrawal && this.amount < 0) {
				this.amount *= -1;
			}
			
			return true;
		}
		
		/**
		 * @return True if this is a withdrawal and a false if this is a 
		 * deposit.
		 */
		public boolean isWithdrawal() {
			return this.isWithdrawal;
		}
		
		/**
		 * Sets the category type of this.
		 * @param tag is the category of this.
		 * @modifies The category of this.
		 * @effects Sets the category to the parameter tag.
		 * @throws InvalidArgumentException if this transaction is not a 
		 * withdrawal.
		 * @throws InvalidArgumentException if the parameter is null or not 
		 * in the list of acceptable tags.
		 * @return True if the category was successfully set.
		 */
		public boolean setCategory(CategoryTag tag) {
			
			if (tag == null) {
				throw new IllegalArgumentException("The category tag must "
						+ "be non-null.");
			} 
			
			if (!isWithdrawal) {
				if (tag != CategoryTag.DEPOSIT) {
				// If this is a deposit it must have a deposit category tag.
				throw new IllegalArgumentException('\n' + "This transaction must have the "
						+ "category tag of deposit since it this is a deposit.");
				}

			} else {
				if (tag == CategoryTag.DEPOSIT) {
				// If this is not a deposit it cannot have a category tag of deposit.
				throw new IllegalArgumentException('\n' + "This transaction is not a deposit "
						+ "and therefore cannot have a category tag of deposit.");
				}
			}
			this.category = tag;
			return true;
		}
		
		/**
		 * @return The category of this transaction.
		 */
		public String getCategory(){
			return this.category.toString();
		}
		
		/**
		 * Sets the memo line of this transaction.
		 * @param memo is the memo string for this transaction.
		 * @modifies The memo for this.
		 * @effects Sets the transaction memo to the parameter.
		 * @throws An InvalidArgumentException if memo is null.
		 * @return True if the memo was successfully set.
		 */
		public boolean setMemo(String memo) {
			if (memo == null) {
				throw new IllegalArgumentException("The memo cannot be null.");
			} else {
				this.memo = memo;
				return true;
			}
		}
		
		/**
		 * @return The memo of this transaction.
		 */
		public String getMemo() {
			return this.memo;
		}
		
		/**
		 * Builds a transaction object from the information contained within 
		 * this TransactionBuilder object.
		 * @return
		 */
		public Transaction build() {
			return new Transaction(this);
		}
		
	}
	
	/**
	 * All the category types a transaction may have.
	 * @author L. James Davidson
	 */
	public enum CategoryTag {
		FOOD, HOUSING, SCHOOL, TRANSPORTATION, DEPOSIT
	}

	/**
	 * @return an array containing the constants of this enum type, in the order they are declared.
	 */
	public static CategoryTag[] values() {
		CategoryTag[] tags = new CategoryTag[5];
		tags[0] = CategoryTag.FOOD;
		tags[1] = CategoryTag.HOUSING;
		tags[2] = CategoryTag.SCHOOL;
		tags[3] = CategoryTag.TRANSPORTATION;
		tags[4] = CategoryTag.DEPOSIT;
		
		return tags;
	}
	
	/**
	 * Ensures that the representation invariant is preserved.
	 */
	private void checkRep() {
		// The location of the transaction must be non-null.
		assert(this.location != null) : "The location must be non-null.";
		
		// The date must be non-null		
		assert (this.date != null) : "The date for this transaction must be non-null";
		
		// The category of this must be non-null.
		assert (this.category != null) : "The category for this transaction must be non-null.";
		
		// The memo of the transaction must be non-null.
		assert (this.memo != null) : "The memo for this transaction must be non-null.";
	}

}
