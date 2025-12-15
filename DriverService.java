package com.Transport;

import com.Transport.DriverDAO;
import com.Transport.Driver;
import com.Transport.PasswordHasher;
import java.util.List;

public class DriverService {
    private DriverDAO driverDAO;
    private PasswordHasher passwordHasher;
    
    public DriverService() {
        this.driverDAO = new DriverDAO();
        this.passwordHasher = new PasswordHasher();
    }
    
    // Register a new driver with validation
    public boolean registerDriver(Driver driver, String plainPassword) {
        // Validate input
        if (!validateDriverInput(driver, plainPassword)) {
            return false;
        }
        
        // Check if username already exists
        if (driverDAO.usernameExists(driver.getUsername())) {
            javax.swing.JOptionPane.showMessageDialog(null, 
                "Username '" + driver.getUsername() + "' already exists!\nPlease choose a different username.", 
                "Registration Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Hash the password before storing
        String hashedPassword = passwordHasher.hashPassword(plainPassword);
        driver.setPasswordHash(hashedPassword);
        
        return driverDAO.createDriver(driver);
    }
    
    // Login driver
    public Driver loginDriver(String username, String plainPassword) {
        if (username == null || username.trim().isEmpty() || plainPassword == null || plainPassword.isEmpty()) {
            return null;
        }
        
        String hashedPassword = passwordHasher.hashPassword(plainPassword);
        return driverDAO.loginDriver(username.trim(), hashedPassword);
    }
    
    // Get driver by ID
    public Driver getDriverById(int driverId) {
        return driverDAO.getDriverById(driverId);
    }
    
    // Get all drivers
    public List<Driver> getAllDrivers() {
        return driverDAO.getAllDrivers();
    }
    
    // Validate driver input
    private boolean validateDriverInput(Driver driver, String password) {
        if (driver.getUsername() == null || driver.getUsername().trim().isEmpty()) {
            showValidationError("Username is required");
            return false;
        }
        
        if (password == null || password.trim().isEmpty()) {
            showValidationError("Password is required");
            return false;
        }
        
        if (password.length() < 6) {
            showValidationError("Password must be at least 6 characters long");
            return false;
        }
        
        if (driver.getEmail() == null || driver.getEmail().trim().isEmpty()) {
            showValidationError("Email is required");
            return false;
        }
        
        if (!driver.getEmail().contains("@") || !driver.getEmail().contains(".")) {
            showValidationError("Please enter a valid email address");
            return false;
        }
        
        if (driver.getFullName() == null || driver.getFullName().trim().isEmpty()) {
            showValidationError("Full name is required");
            return false;
        }
        
        return true;
    }
    
    private void showValidationError(String message) {
        javax.swing.JOptionPane.showMessageDialog(null, 
            message, 
            "Validation Error", 
            javax.swing.JOptionPane.ERROR_MESSAGE);
    }
}