package model;

import java.time.LocalDate;

/**
 * Holds all the information of a certain transaction.
 * @author L. James Davidson
 */
public class Transaction {

	private final String description;
	private final int amount_in_cents;
	private final LocalDate date;
	private final String category;
	private final String memo;
	private final boolean isDeposit;
	
	// AF: 
	// The description of the transaction must be non-null. 
	// The date must be non-null
	// The category of this must be non-null.
	// The memo of the transaction must be non-null.
	
	// RI: location != null, date != null, category != null, memo != null
	
	/**
	 * Creates a new immutable transaction object using the builder object.
	 * @param builder is a TransactionBuilder containing all the information 
	 * necessary to create a transaction object.
	 */
	private Transaction(TransactionBuilder builder) {
		this.description = builder.description;
		this.amount_in_cents = builder.amount_in_cents;
		this.date = builder.date;
		this.category = builder.category;
		this.memo = builder.memo;
		this.isDeposit = builder.isDeposit;
		this.checkRep();
	}
	
	/**
	 * @return The description of this transaction.
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * @return The amount that was transfered due to this transaction in cents.
	 */
	public int getAmountInCents() {
		return this.amount_in_cents;
	}
	
	/**
	 * @return The date when this transaction occurred.
	 */
	public LocalDate getDate() {
		return this.date;
	}
	
	/**
	 * @return The category of the this transaction.
	 */
	public String getCategory() {
		return this.category;
	}
	
	/**
	 * @return The memo attached to this transaction.
	 */
	public String getMemo() {
		return this.memo;
	}
	
	/**
	 * @return True if this transaction is a deposit.
	 */
	public boolean isADeposit() {
		return this.isDeposit;
	}
	
	
	/**
	 * A builder object for Transaction.
	 * @author L. James Davidson
	 */
	public static class TransactionBuilder {
		
		private int amount_in_cents;
		private String description;
		private String category;
		private String memo;
		private LocalDate date;
		private boolean isDeposit;
		
		/**
		 * Creates a TransactionBuilder object which can be slowly fleshed out until all 
		 * information has been filled in. Once enough information has been filled in then a
		 * Transaction object may be built.
		 * @param isDeposit determines if this transaction is a deposit.
		 */
		public TransactionBuilder(boolean isDeposit) {
			this.isDeposit = isDeposit;
		}
		
		/**
		 * Sets the description of this transaction.
		 * @param descr is the description of the transaction.
		 * @throws IllegalArgumentException if the parameter is null.
		 */
		public void setDescription(String descr) {
			if (descr == null)
				throw new IllegalArgumentException("The location of the "
						+ "transaction must be non-null.");
			this.description = descr;
		}
		
		/**
		 * @return The description of the transaction.
		 */
		public String getDescription() {
			return this.description;
		}
		
		/**
		 * Sets the amount of the this transaction. If this transaction's type is a deposit
		 * then the amount will be made positive and otherwise will be made negative.
		 * @param amount is the amount that was transfered during this transaction in cents.
		 */
		public void setAmountInCents(int amount) {
			// If this transaction is a deposit set the amount to positive.
			if (this.isDeposit) {
				this.amount_in_cents = Math.abs(amount);
			} else {
				this.amount_in_cents = -1 * Math.abs(amount);
			}
		}
		
		/**
		 * @return the amount of this transaction in cents.
		 */
		public int getAmountInCents() {
			return this.amount_in_cents;
		}
		
		/**
		 * Sets the date of this transaction.
		 * @param date is the LocalDate object representing the date of the transaction.
		 * @throws IllegalArgumentException if date is null.
		 */
		public void setDate(LocalDate date) {
			if (date == null)
				throw new IllegalArgumentException("The date of a transaction cannot be null.");
			this.date = date;
		}
		
		/**
		 * @return The date of this transaction.
		 */
		public LocalDate getDate() {
			return this.date;
		}
		
		/**
		 * Sets the category type of this transaction.
		 * @param category is the category of expense of this transaction.
		 * @modifies The amount of this transaction to either positive (if the category is a
		 * 		deposit) or negative (any other category).
		 * @throws IllegalArgumentException if category is null.
		 */
		public void setCategory(String category) {
			if (category == null)
				throw new IllegalArgumentException("The category of a transaction "
						+ "cannot be null.");
			this.category = category;
			
			// Resetting the amount so that if this transactions amount is positive only if 
			// it is a deposit.
			this.setAmountInCents(this.amount_in_cents);
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
		 * @throws An InvalidArgumentException if memo is null.
		 */
		public void setMemo(String memo) {
			if (memo == null)
				throw new IllegalArgumentException("The memo cannot be null.");
			this.memo = memo;
		}
		
		/**
		 * @return The memo of this transaction.
		 */
		public String getMemo() {
			return this.memo;
		}
		
		/**
		 * Sets the transaction type to the given parameter.
		 * @param isDeposit determines is this transaction is a deposit.
		 */
		public void setAsDeposit(boolean isDeposit) {
			this.isDeposit = isDeposit;
		}
		
		/**
		 * @return True if this transaction is a deposit.
		 */
		public boolean isADeposit() {
			return this.isDeposit;
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
	 * Ensures that the representation invariant is preserved.
	 */
	private void checkRep() {
		assert(this.description != null) : "The description must be non-null.";
		assert (this.date != null) : "The date for this transaction must be non-null";
		assert (this.category != null) : "The category for this transaction must be non-null.";
		assert (this.memo != null) : "The memo for this transaction must be non-null.";
	}

}
