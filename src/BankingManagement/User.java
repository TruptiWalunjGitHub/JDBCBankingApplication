package BankingManagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class User {
	private Connection con;
	private Scanner sc;

	public User(Connection con, Scanner sc) { // Constructor
		super();
		this.con = con;
		this.sc = sc;
	}

	public void register() throws SQLException {
		sc.nextLine();
		System.out.println("Full Name: ");
		String fullName = sc.nextLine();

		System.out.println("Email: ");
		String email = sc.nextLine();

		System.out.println("Password: ");
		String password = sc.nextLine();

		if (user_exist(email)) { 
			//if user_exist() then we will return otherwise will get further processing
			System.out.println("User Already Exists for this Email Address!!");
			return;
		}
		String register_query = "INSERT INTO user (full_name, email, password) VALUES (?, ?, ?)";
		PreparedStatement preStt = con.prepareStatement(register_query);
		preStt.setString(1, fullName);
		preStt.setString(2, email);
		preStt.setString(3, password);

		int affectedRows = preStt.executeUpdate();	//insert, update, Delete
		if (affectedRows > 0) {
			System.out.println("Registration Successfull!");
		} else {
			System.out.println("Registration Failed!");
		}
	}

	public String login() throws SQLException {
		sc.nextLine();

		System.out.println("Email: ");
		String email = sc.nextLine();

		System.out.println("Password: ");
		String password = sc.nextLine();
		
		String login_query = "SELECT * FROM user WHERE email = ? AND password = ?";
		try {
			
			PreparedStatement preStt = con.prepareStatement(login_query);
			preStt.setString(1, email);
			preStt.setString(2, password);

			ResultSet rs = preStt.executeQuery();	//Retrieve
			if (rs.next()) {
				return email;	//Id user put correct details then entry will pass in string format
			} else {
				return null;	//If user enter incorrect info then entry will not pass
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	public boolean user_exist(String email){
		//Check email of this particular user
		
		try {
			String query = "SELECT * FROM user WHERE email = ?";
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
// USE bankmanagementjdbc;
// CREATE TABLE user (
// full_name VARCHAR(255) NOT NULL,
// email VARCHAR(255) NOT NULL PRIMARY KEY,
// password VARCHAR(255) NOT NULL
// );