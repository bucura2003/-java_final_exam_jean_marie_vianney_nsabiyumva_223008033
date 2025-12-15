package com.Transport;

import com.Transport.Driver;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class DriverDAO {
    
    // Create a new driver
    public boolean createDriver(Driver driver) {
        // Check if connection is available
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.err.println("Database connection is null - cannot create driver");
            return false;
        }
        
        String sql = "INSERT INTO drivers (username, password_hash, email, full_name, license_number, phone, status, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, driver.getUsername());
            stmt.setString(2, driver.getPasswordHash());
            stmt.setString(3, driver.getEmail());
            stmt.setString(4, driver.getFullName());
            stmt.setString(5, driver.getLicenseNumber());
            stmt.setString(6, driver.getPhone());
            stmt.setString(7, driver.getStatus());
            stmt.setTimestamp(8, Timestamp.valueOf(driver.getCreatedAt()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        driver.setDriverId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating driver: " + e.getMessage());
            // Show user-friendly error message for duplicate username
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("username")) {
                JOptionPane.showMessageDialog(null, 
                    "Username already exists! Please choose a different username.", 
                    "Registration Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }
    
    // Get driver by ID
    public Driver getDriverById(int driverId) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return null;
        }
        
        String sql = "SELECT * FROM drivers WHERE driver_id = ?";
        Driver driver = null;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, driverId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                driver = mapResultSetToDriver(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting driver: " + e.getMessage());
        }
        return driver;
    }
    
    // Get all drivers
    public List<Driver> getAllDrivers() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return new ArrayList<>();
        }
        
        String sql = "SELECT * FROM drivers";
        List<Driver> drivers = new ArrayList<>();
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                drivers.add(mapResultSetToDriver(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all drivers: " + e.getMessage());
        }
        return drivers;
    }
    
    // Login driver
    public Driver loginDriver(String username, String passwordHash) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return null;
        }
        
        String sql = "SELECT * FROM drivers WHERE username = ? AND password_hash = ? AND status = 'Active'";
        Driver driver = null;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, passwordHash);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                driver = mapResultSetToDriver(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
        }
        return driver;
    }
    
    // Check if username exists
    public boolean usernameExists(String username) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }
        
        String sql = "SELECT COUNT(*) FROM drivers WHERE username = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
        }
        return false;
    }
    
    // Helper method to map ResultSet to Driver object
    private Driver mapResultSetToDriver(ResultSet rs) throws SQLException {
        Driver driver = new Driver();
        driver.setDriverId(rs.getInt("driver_id"));
        driver.setUsername(rs.getString("username"));
        driver.setPasswordHash(rs.getString("password_hash"));
        driver.setEmail(rs.getString("email"));
        driver.setFullName(rs.getString("full_name"));
        driver.setLicenseNumber(rs.getString("license_number"));
        driver.setPhone(rs.getString("phone"));
        driver.setStatus(rs.getString("status"));
        driver.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return driver;
    }
}