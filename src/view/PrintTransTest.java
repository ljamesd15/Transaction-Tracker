package view;

import java.time.LocalDate;

import model.Transaction.TransactionBuilder;

public class PrintTransTest {

	public static void main (String[] args) {
		TransactionBuilder trans = new TransactionBuilder();
		trans.setDescription("Fred Meyer");
		trans.setAmount(-79.56);
		trans.setDate(LocalDate.now());
		trans.setCategory("Food");
		trans.setMemo("For parents");
		
		CreateNewTransaction.printTransInfo(trans);
	}
}
