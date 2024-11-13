package BankingManagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Accounts {
	private Connection con;
	private Scanner sc;

	public Accounts(Connection con, Scanner sc) {
		super();
		this.con = con;
		this.sc = sc;
	}

	//open new account
	public long open_account(String email) {
		if (!account_exists(email)) {
			String open_account_query = 
		"INSERT INTO accounts(account_number, full_name, email, balance, security_pin) VALUES (?, ?, ?, ?, ?)";

			sc.nextLine();
			System.out.println("Enter Full name: ");
			String fullName = sc.nextLine();

			System.out.println("Enter Initial Ammount: ");
			double iniAmt = sc.nextDouble();
			sc.nextLine();
			
			System.out.println("Enter Sequrity Pin: ");
			String seqPin = sc.nextLine();
			
	//It will Automatically generate -> using method of generateAccountNumber()
			try {
				long accNum = generateAccountNumber();	

				PreparedStatement preStt = con.prepareStatement(open_account_query);
				preStt.setLong(1, accNum);
				preStt.setString(2, fullName);
				preStt.setString(3, email);
				preStt.setDouble(4, iniAmt);
				preStt.setString(5, seqPin);

				int rowsAffected = preStt.executeUpdate();	//insert, delete, update
				if (rowsAffected > 0) {
					return accNum;
				} else {
					throw new RuntimeException("Account Creation Failed..!!");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		throw new RuntimeException("Account Already Exist");
	}

	//to get acc num
	public long getAccount_number(String email) throws SQLException {
		String getAccNo = 
			"SELECT account_number FROM accounts WHERE email = ?";
		PreparedStatement preStt = con.prepareStatement(getAccNo);
		preStt.setString(1, email);
		
		ResultSet rs = preStt.executeQuery();	//retrieve
		if(rs.next()) {
			return rs.getLong("account_number");
		}
		throw new RuntimeException("Account Number Doesn't Exist.");
	}

	//generate account number
	private long generateAccountNumber(){
		try {
			Statement stt = con.createStatement();
			String query = 
				"SELECT account_number FROM accounts ORDER BY account_number DESC LIMIT 1";
			ResultSet rs = stt.executeQuery(query);
			if (rs.next()) {
				long lastAccNo = rs.getLong("account_number");
			//"account_number" =>present in SQL 
				return lastAccNo + 1;
			} else {
				return 10000100;
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 10000100;
	}

	//Checking account exists or not
	public boolean account_exists(String email) {
		//Email is unique
		String query = "SELECT account_number FROM accounts WHERE email = ?";
		try {
			PreparedStatement preStt = con.prepareStatement(query);
			preStt.setString(1, email);		
			
			ResultSet rs = preStt.executeQuery();	//retrieve
			if (rs.next()) {
				return true;
			} else {
				return false; 
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
// CREATE TABLE accounts (
// account_number BIGINT NOT NULL PRIMARY KEY,
// full_name VARCHAR(255) NOT NULL,
// email VARCHAR(255) NOT NULL UNIQUE,
// balance DECIMAL(10,2) NOT NULL,
// sequrity_pin CHAR(4) NOT NULL
// );