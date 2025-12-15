package com.Transport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class DatabaseConnection {
    // UPDATE THESE FOR YOUR MySQL SETUP
    private static final String URL = "jdbc:mysql://localhost:3306/TransportAutomationSystem1";
    private static final String USERNAME = "root";
    private static final String PASSWORD = ""; // Leave empty if no password
    
    private static Connection connection;
    
    static {
        initializeDatabase();
    }
    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            showErrorDialog("Database Connection Error", 
                "Cannot connect to database!\n\n" +
                "Please make sure:\n" +
                "1. MySQL server is running\n" +
                "2. Database 'transport_system' exists\n" +
                "3. Username and password are correct\n\n" +
                "Error: " + e.getMessage());
        }
        return connection;
    }
    
    private static void initializeDatabase() {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Create database if it doesn't exist
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE DATABASE IF NOT EXISTS transport_system");
            stmt.close();
            conn.close();
            
            // Connect to the database and create tables
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            createTables(conn);
            conn.close();
            
            System.out.println("Database initialized successfully!");
            
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            showErrorDialog("Driver Error", "MySQL JDBC Driver not found!\nPlease add mysql-connector-java to your project.");
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }
    
    private static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        
        String createDriversTable = 
            "CREATE TABLE IF NOT EXISTS drivers (" +
            "driver_id INT PRIMARY KEY AUTO_INCREMENT," +
            "username VARCHAR(100) UNIQUE NOT NULL," +
            "password_hash VARCHAR(255) NOT NULL," +
            "email VARCHAR(255)," +
            "full_name VARCHAR(255)," +
            "license_number VARCHAR(50)," +
            "phone VARCHAR(20)," +
            "status VARCHAR(50) DEFAULT 'Active'," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")";
        
        stmt.execute(createDriversTable);
        stmt.close();
    }
    
    private static void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}