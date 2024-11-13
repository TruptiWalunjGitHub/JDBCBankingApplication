package BankingManagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountManager {
	private Connection con;
	private Scanner sc;

	AccountManager(Connection con, Scanner sc) {
		this.con = con;
		this.sc = sc;
	}

	// Credit money
	public void credit_money(long accNum) throws SQLException {
		sc.nextLine();
		System.out.println("Enter Amount: ");
		double amt = sc.nextDouble();

		sc.nextLine();
		System.out.println("Enter Sequrity Pin: ");
		String seqPin = sc.nextLine();

		try {
			con.setAutoCommit(false);
		if (accNum != 0) {
			String query = 
				"SELECT * FROM accounts WHERE account_number = ? AND security_pin = ?";

				PreparedStatement preStt = con.prepareStatement(query);
				preStt.setLong(1, accNum);
				preStt.setString(2, seqPin);

		ResultSet rs = preStt.executeQuery();	//retrieve
			if (rs.next()) {

			String credit_qry = 
				"UPDATE accounts SET balance = balance + ? WHERE account_number = ?";

				PreparedStatement preStt1 = con.prepareStatement(credit_qry);
				preStt1.setDouble(1, amt);
				preStt1.setLong(2, accNum);
			int rowAffected = preStt1.executeUpdate();	//insert, update, delete

			if (rowAffected > 0) {
				System.out.println("Rs. " + amt + " credited Successfulley..!!");
				con.commit();
				con.setAutoCommit(true);
				return;
			} else {
				System.out.println("Transaction Failed.!");
				con.rollback();
				con.setAutoCommit(true);
			}
		} else {
				System.out.println("Invalid sequrity Pin!");
				}
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		con.setAutoCommit(true);
	}

	// Debit money
	public void debit_money(long accNum) throws SQLException {
		sc.nextLine();
		System.out.println("Enter Amount: ");
		double amt = sc.nextDouble();

		sc.nextLine();
		System.out.println("Enter Sequrity Pin: ");
		String seqPin = sc.nextLine();
		
		try {
			con.setAutoCommit(false);	

		if (accNum != 0) {
			String query = 
				"SELECT * FROM accounts WHERE account_number = ? AND security_pin = ?";

			PreparedStatement preStt = con.prepareStatement(query);
			preStt.setLong(1, accNum);
			preStt.setString(2, seqPin);

		ResultSet rs = preStt.executeQuery();	//retrieve
		if (rs.next()) {
			double current_bal = rs.getDouble("balance");

		if (amt <= current_bal) {	//Else -> Insufficient balance
			String debit_qry = 
				"UPDATE accounts SET balance = balance - ? WHERE account_number = ?";

					PreparedStatement preStt1 = con.prepareStatement(debit_qry);
						preStt1.setDouble(1, amt);
						preStt1.setLong(2, accNum);
			int rowAffected = preStt1.executeUpdate();	//insert, update, delete

			if (rowAffected > 0) {
				System.out.println("Rs. " + amt + " debited Successfulley..!!");
				con.commit();
				con.setAutoCommit(true);
				return;
			}	else {
					System.out.println("Transaction Failed.!");
					con.rollback();
					con.setAutoCommit(true);
				}
			} else {
					System.out.println("Insufficient Balance!");
				}
			} else {
					System.out.println("Invalid sequrity Pin!");
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		con.setAutoCommit(true);
	}

	// Transfer Money
	public void transfer_money(long sender_accNum) throws SQLException {
		sc.nextLine();
		System.out.print("Enter Receiver Account Number: ");
		long receiver_accNum = sc.nextLong();

		System.out.print("Enter Amount: ");
		double amt = sc.nextDouble();
		sc.nextLine();

		System.out.print("Enter Security Pin: ");
		String seqPin = sc.nextLine();

		try {
			con.setAutoCommit(false);

			if (sender_accNum != 0 && receiver_accNum != 0) {	//else -> invalid acc num
				String query = 
					"SELECT * FROM accounts WHERE account_number = ? AND security_pin = ?";
				PreparedStatement preStt = con.prepareStatement(query);

				preStt.setLong(1, sender_accNum);
				preStt.setString(2, seqPin);

				ResultSet rs = preStt.executeQuery();	//retrieve
				if (rs.next()) {	//Invalid Security Pin!
					double current_bal = rs.getDouble("balance");

					if (amt <= current_bal) {	//Insufficient Balance!

						// Write debit and credit queries
						String debit_qry = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
						String credit_qry = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";

						// Debit and Credit prepared Statements
						PreparedStatement debitPreStt = con.prepareStatement(debit_qry);
						PreparedStatement creditPreStt = con.prepareStatement(credit_qry);

						// Set Values for debit and credit prepared statements
						creditPreStt.setDouble(1, amt);
						creditPreStt.setLong(2, receiver_accNum);

						debitPreStt.setDouble(1, amt);
						debitPreStt.setLong(2, sender_accNum);

						int rowAffcDebit = debitPreStt.executeUpdate();
						int rowAffecCredit = creditPreStt.executeUpdate();

						if (rowAffcDebit > 0 && rowAffecCredit > 0) {
					//To Achieve Data Consistency
							System.out.println("Transaction Succesful..!!");
							System.out.println("Rs. " + amt + " transferred Successfulley.");
							con.commit();
							con.setAutoCommit(true);
							return;
						} else {
							System.out.println("Insufficient Balance!");
						}
					} else {
						System.out.println("Invalid Security Pin!");
					}
				} else {
					System.out.println("Invalid account number");
				}
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		con.setAutoCommit(true);
	}

	// get remaining Account balance
	public void getBalance(long accNum) throws SQLException {
		sc.nextLine();
		System.out.println("Enter Sequrity Pin: ");
		String seqPin = sc.nextLine();

		String qry =
			"SELECT balance FROM accounts WHERE account_number = ? AND security_pin = ?";

				PreparedStatement preStt = con.prepareStatement(qry);
				preStt.setLong(1, accNum);
				preStt.setString(2, seqPin);

				ResultSet rs = preStt.executeQuery();	//retrieve
				if (rs.next()) {
					double bal = rs.getDouble("balance");
					System.out.println("Balance: " + bal);
				} else {
					System.out.println("Invalid Pin!");
				}
	}
}
