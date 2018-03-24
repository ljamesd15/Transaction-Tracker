package testing;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import controller.TransactionDB;
import controller.TransactionHelper;
import model.Transaction;
import model.User;

public class TestTransactionDB {

	private static TransactionDB db;
	
	@BeforeClass
	public static void setupDB() {
		String dbFilePath = (new File("")).getAbsolutePath() + "\\data\\TT.db";
		db = new TransactionDB(dbFilePath);
		
    	TransactionHelper.prepare();
	}
	
	@Test
	public void recentTransactions() {
		User user = new User("ljd22", "James", 100, "password");
		int num = 3;
		
		Transaction[] trans = db.getRecentTransactions(user, num);
		
		for (int i = 0; i < num; i++) {
			System.out.println(trans[i]);
		}
	}
}
