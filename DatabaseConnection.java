package com.transport;

import java.sql.*;
import javax.swing.JOptionPane;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:transport_system.db";
    private static Connection connection;
    
    static {
        initializeDatabase();
    }
    
    private static void initializeDatabase() {
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            System.out.println("✅ SQLite JDBC Driver loaded successfully!");
            
            // Create database connection
            getConnection();
            createTables();
            insertSampleData();
            
            System.out.println("✅ Database initialized successfully!");
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ SQLite JDBC Driver not found!");
            showErrorDialog("Missing Driver", 
                "SQLite JDBC Driver not found!\n\n" +
                "Please download sqlite-jdbc-3.45.1.0.jar from:\n" +
                "https://github.com/xerial/sqlite-jdbc/releases\n\n" +
                "And add it to your project build path.");
            System.exit(1);
        } catch (SQLException e) {
            System.err.println("❌ Database initialization failed: " + e.getMessage());
            showErrorDialog("Database Error", 
                "Failed to initialize database:\n" + e.getMessage());
        }
    }
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
            // Enable foreign keys and other settings
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
                stmt.execute("PRAGMA journal_mode = WAL");
            }
            System.out.println("✅ Database connection established!");
        }
        return connection;
    }
    
    private static void createTables() throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        
        // Create tables in correct order
        String[] createTables = {
            // Drivers Table
            "CREATE TABLE IF NOT EXISTS drivers (" +
            "driver_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "full_name VARCHAR(255) NOT NULL," +
            "license_number VARCHAR(100) UNIQUE NOT NULL," +
            "phone VARCHAR(20)," +
            "email VARCHAR(255)," +
            "address TEXT," +
            "emergency_contact VARCHAR(20)," +
            "status VARCHAR(50) DEFAULT 'Active'," +
            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ")",
            
            // Vehicles Table
            "CREATE TABLE IF NOT EXISTS vehicles (" +
            "vehicle_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name VARCHAR(255) NOT NULL," +
            "identifier VARCHAR(50) UNIQUE NOT NULL," +
            "type VARCHAR(100)," +
            "capacity INTEGER," +
            "status VARCHAR(50) DEFAULT 'Available'," +
            "location VARCHAR(255)," +
            "contact VARCHAR(20)," +
            "assigned_since DATETIME," +
            "last_maintenance DATE," +
            "next_maintenance DATE," +
            "driver_id INTEGER" +
            ")",
            
            // Routes Table
            "CREATE TABLE IF NOT EXISTS routes (" +
            "route_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "route_name VARCHAR(255) NOT NULL," +
            "start_point VARCHAR(255)," +
            "end_point VARCHAR(255)," +
            "distance_km DECIMAL(8,2)," +
            "estimated_time_minutes INTEGER," +
            "fare_per_km DECIMAL(6,2)," +
            "status VARCHAR(50) DEFAULT 'Active'," +
            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ")",
            
            // Trips Table
            "CREATE TABLE IF NOT EXISTS trips (" +
            "trip_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "order_number VARCHAR(100) UNIQUE NOT NULL," +
            "date DATETIME NOT NULL," +
            "start_location VARCHAR(255)," +
            "end_location VARCHAR(255)," +
            "status VARCHAR(50) DEFAULT 'Scheduled'," +
            "total_amount DECIMAL(10,2)," +
            "payment_method VARCHAR(50)," +
            "notes TEXT," +
            "passenger_count INTEGER," +
            "vehicle_id INTEGER NOT NULL," +
            "driver_id INTEGER NOT NULL," +
            "route_id INTEGER," +
            "actual_start_time DATETIME," +
            "actual_end_time DATETIME" +
            ")",
            
            // Tickets Table
            "CREATE TABLE IF NOT EXISTS tickets (" +
            "ticket_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "ticket_number VARCHAR(100) UNIQUE NOT NULL," +
            "passenger_name VARCHAR(255) NOT NULL," +
            "passenger_phone VARCHAR(20)," +
            "passenger_email VARCHAR(255)," +
            "seat_number VARCHAR(10)," +
            "price DECIMAL(8,2)," +
            "status VARCHAR(50) DEFAULT 'Confirmed'," +
            "booking_date DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "trip_id INTEGER NOT NULL" +
            ")",
            
            // Maintenance Table
            "CREATE TABLE IF NOT EXISTS maintenance (" +
            "maintenance_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "reference_id VARCHAR(100) UNIQUE NOT NULL," +
            "description TEXT NOT NULL," +
            "maintenance_date DATETIME NOT NULL," +
            "completion_date DATETIME," +
            "cost DECIMAL(10,2)," +
            "status VARCHAR(50) DEFAULT 'Scheduled'," +
            "remarks TEXT," +
            "vehicle_id INTEGER NOT NULL," +
            "mechanic_name VARCHAR(255)" +
            ")",
            
            // Notifications Table
            "CREATE TABLE IF NOT EXISTS notifications (" +
            "notification_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "title VARCHAR(255) NOT NULL," +
            "message TEXT NOT NULL," +
            "type VARCHAR(50)," +
            "priority VARCHAR(20) DEFAULT 'Medium'," +
            "is_read BOOLEAN DEFAULT 0," +
            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ")"
        };
        
        // Execute all table creations
        for (String createTable : createTables) {
            try {
                stmt.execute(createTable);
            } catch (SQLException e) {
                System.err.println("Error creating table: " + e.getMessage());
            }
        }
        
        stmt.close();
        System.out.println("✅ All database tables created successfully!");
    }
    
    private static void insertSampleData() throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        
        // Check if sample data already exists
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM drivers");
        if (rs.next() && rs.getInt(1) == 0) {
            System.out.println("📝 Inserting sample data...");
            
            // Insert sample drivers - FIXED: removed tab characters from email
            stmt.executeUpdate(
                "INSERT INTO drivers (full_name, license_number, phone, email, address, emergency_contact, status) VALUES " +
                "('NSABIYUMVA JMV', 'DL123456', '+250786488092', 'jeanmarie3@gmail.com', 'KG 120 St, Town', '+250786488092', 'Active')," +
                "('IGIHOZO Joselyne', 'DL789012', '+25079648878', 'gihozojoselyne@gmail.com', 'KK 112 Ave, Town', '+25079567898', 'Active')," +
                "('Berwa Bolingo', 'DL345678', '+250788300', 'berwabolingo5@gmail.com', '789 Pine Rd, Yellow City', '+1234567897', 'On Leave')"
            );
            
            // Insert sample vehicles - FIXED: corrected phone numbers
            stmt.executeUpdate(
                "INSERT INTO vehicles (name, identifier, type, capacity, status, location, contact, driver_id) VALUES " +
                "('Jali Transport 101', 'BUS-001', 'Bus', 50, 'Available', 'NYABUGOGO', '+250784635887', 1)," +
                "('Express Ritco 202', 'BUS-202', 'Bus', 40, 'On Trip', 'BWERAMVURA Station', '+250784567802', 2)," +
                "('TOYOTA 303', 'VAN-303', 'Van', 15, 'Maintenance', 'Service Center', '+250784567803', NULL)"
            );
            
            // Insert sample routes - FIXED: complete SQL statement
            stmt.executeUpdate(
                "INSERT INTO routes (route_name, start_point, end_point, distance_km, estimated_time_minutes, fare_per_km, status) VALUES " +
                "('City Center Express', 'Downtown Terminal', 'City Center Mall', 15.5, 45, 2.50, 'Active')," +
                "('Airport Shuttle', 'Central Station', 'International Airport', 25.0, 60, 3.00, 'Active')," +
                "('University Line', 'Student Union', 'Faculty Building', 8.2, 25, 1.80, 'Active')"
            );
            
            // Insert sample trips - FIXED: complete SQL statement
            stmt.executeUpdate(
                "INSERT INTO trips (order_number, date, start_location, end_location, status, total_amount, vehicle_id, driver_id, route_id) VALUES " +
                "('TRIP-001', datetime('now'), 'Downtown', 'Airport', 'Completed', 75.00, 1, 1, 1)," +
                "('TRIP-002', datetime('now', '+1 hour'), 'City Center', 'University', 'Scheduled', 25.00, 2, 2, 3)"
            );
            
            // Insert sample tickets - FIXED: complete SQL statement
            stmt.executeUpdate(
                "INSERT INTO tickets (ticket_number, passenger_name, passenger_phone, seat_number, price, status, trip_id) VALUES " +
                "('TKT-001', 'Alice Brown', '+1234567810', 'A1', 75.00, 'Confirmed', 1)," +
                "('TKT-002', 'Bob Wilson', '+1234567811', 'B2', 25.00, 'Confirmed', 2)"
            );
            
            // Insert sample maintenance - FIXED: complete SQL statement
            stmt.executeUpdate(
                "INSERT INTO maintenance (reference_id, description, maintenance_date, cost, status, vehicle_id, mechanic_name) VALUES " +
                "('MAINT-001', 'Regular service and oil change', datetime('now'), 150.00, 'Completed', 1, 'Mike Mechanic')," +
                "('MAINT-002', 'Brake system inspection', datetime('now', '+7 days'), 200.00, 'Scheduled', 3, 'Sarah Technician')"
            );
            
            System.out.println("✅ Sample data inserted successfully!");
        } else {
            System.out.println("✅ Sample data already exists, skipping insertion.");
        }
        
        rs.close();
        stmt.close();
    }
    
    public static ResultSet executeQuery(String query) throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(query);
    }
    
    public static int executeUpdate(String query) throws SQLException {
        Connection conn = getConnection();
        try (Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(query);
        }
    }
    
    public static void addNotification(String title, String message, String type, String priority) {
        try {
            String query = "INSERT INTO notifications (title, message, type, priority) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = getConnection().prepareStatement(query);
            pstmt.setString(1, title);
            pstmt.setString(2, message);
            pstmt.setString(3, type);
            pstmt.setString(4, priority);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error adding notification: " + e.getMessage());
        }
    }
    
    public static int getUnreadNotificationCount() {
        try {
            ResultSet rs = executeQuery("SELECT COUNT(*) as count FROM notifications WHERE is_read = 0");
            if (rs.next()) {
                int count = rs.getInt("count");
                rs.getStatement().close();
                return count;
            }
            return 0;
        } catch (SQLException e) {
            return 0;
        }
    }
    
    private static void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Database connection closed!");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
    
    public static boolean testConnection() {
        try {
            getConnection();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}