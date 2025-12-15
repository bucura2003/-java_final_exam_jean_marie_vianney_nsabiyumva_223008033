package com.Transport;

import com.Transport.Driver;
import com.Transport.DriverService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistrationFrame extends JFrame {
    private JTextField usernameField, emailField, fullNameField, licenseField, phoneField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton registerButton, cancelButton;
    private DriverService driverService;
    
    public RegistrationFrame() {
        this.driverService = new DriverService();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Driver Registration");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel with nice background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = new JLabel("DRIVER REGISTRATION", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setForeground(new Color(0, 100, 200));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        
        // Form panel with card-like appearance
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // ===== USERNAME =====
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        formPanel.add(usernameField, gbc);
        
        // ===== PASSWORD =====
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        formPanel.add(passwordField, gbc);
        
        // ===== CONFIRM PASSWORD =====
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(confirmPasswordLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmPasswordField.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        formPanel.add(confirmPasswordField, gbc);
        
        // ===== EMAIL =====
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(emailLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        formPanel.add(emailField, gbc);
        
        // ===== FULL NAME =====
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel fullNameLabel = new JLabel("Full Name:");
        fullNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(fullNameLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 4;
        fullNameField = new JTextField(20);
        fullNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        fullNameField.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        formPanel.add(fullNameField, gbc);
        
        // ===== LICENSE NUMBER =====
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel licenseLabel = new JLabel("License Number:");
        licenseLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(licenseLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 5;
        licenseField = new JTextField(20);
        licenseField.setFont(new Font("Arial", Font.PLAIN, 14));
        licenseField.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        formPanel.add(licenseField, gbc);
        
        // ===== PHONE =====
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(phoneLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 6;
        phoneField = new JTextField(20);
        phoneField.setFont(new Font("Arial", Font.PLAIN, 14));
        phoneField.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        formPanel.add(phoneField, gbc);
        
        // ===== BUTTONS PANEL =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setBackground(new Color(0, 120, 215));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));
        cancelButton.setBackground(new Color(240, 240, 240));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));
        cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);
        
        // Add components to main panel
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        
        setupEventListeners();
    }
    
    private void setupEventListeners() {
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerDriver();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        // Enter key support for all fields
        usernameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerDriver();
            }
        });
        
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerDriver();
            }
        });
        
        confirmPasswordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerDriver();
            }
        });
        
        emailField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerDriver();
            }
        });
        
        fullNameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerDriver();
            }
        });
        
        licenseField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerDriver();
            }
        });
        
        phoneField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerDriver();
            }
        });
    }
    
    private void registerDriver() {
        // Check if service is properly initialized
        if (driverService == null) {
            JOptionPane.showMessageDialog(this,
                "System not properly initialized. Please restart the application.",
                "System Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get values from form
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
        String email = emailField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String licenseNumber = licenseField.getText().trim();
        String phone = phoneField.getText().trim();
        
        // Validate form
        if (!validateForm(username, password, confirmPassword, email, fullName, licenseNumber, phone)) {
            return;
        }
        
        // Create driver object
        Driver driver = new Driver();
        driver.setUsername(username);
        driver.setEmail(email);
        driver.setFullName(fullName);
        driver.setLicenseNumber(licenseNumber);
        driver.setPhone(phone);
        
        // Show loading indicator
        registerButton.setEnabled(false);
        registerButton.setText("Registering...");
        
        // Try to register driver
        boolean success = driverService.registerDriver(driver, password);
        
        // Reset button
        registerButton.setEnabled(true);
        registerButton.setText("Register");
        
        if (success) {
            JOptionPane.showMessageDialog(this,
                "Driver registered successfully!\n\n" +
                "Username: " + username + "\n" +
                "Name: " + fullName + "\n" +
                "You can now login with your credentials.",
                "Registration Successful",
                JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Close registration window
        } else {
            // Error message is shown by the service layer
            // Just reset the username field for retry
            usernameField.setText("");
            usernameField.requestFocus();
        }
    }
    
    private boolean validateForm(String username, String password, String confirmPassword, 
                                String email, String fullName, String licenseNumber, String phone) {
        
        // Check for empty required fields
        if (username.isEmpty()) {
            showValidationError("Username is required");
            usernameField.requestFocus();
            return false;
        }
        
        if (password.isEmpty()) {
            showValidationError("Password is required");
            passwordField.requestFocus();
            return false;
        }
        
        if (confirmPassword.isEmpty()) {
            showValidationError("Please confirm your password");
            confirmPasswordField.requestFocus();
            return false;
        }
        
        if (email.isEmpty()) {
            showValidationError("Email is required");
            emailField.requestFocus();
            return false;
        }
        
        if (fullName.isEmpty()) {
            showValidationError("Full name is required");
            fullNameField.requestFocus();
            return false;
        }
        
        if (licenseNumber.isEmpty()) {
            showValidationError("License number is required");
            licenseField.requestFocus();
            return false;
        }
        
        // Password validation
        if (password.length() < 6) {
            showValidationError("Password must be at least 6 characters long");
            passwordField.setText("");
            confirmPasswordField.setText("");
            passwordField.requestFocus();
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            showValidationError("Passwords do not match");
            passwordField.setText("");
            confirmPasswordField.setText("");
            passwordField.requestFocus();
            return false;
        }
        
        // Email validation
        if (!isValidEmail(email)) {
            showValidationError("Please enter a valid email address");
            emailField.requestFocus();
            return false;
        }
        
        // Username validation (alphanumeric only)
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            showValidationError("Username can only contain letters, numbers, and underscores");
            usernameField.requestFocus();
            return false;
        }
        
        // Phone validation (basic check)
        if (!phone.isEmpty() && !phone.matches("^[0-9+\\-\\s()]+$")) {
            showValidationError("Please enter a valid phone number");
            phoneField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    
    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Validation Error",
            JOptionPane.ERROR_MESSAGE);
    }
}