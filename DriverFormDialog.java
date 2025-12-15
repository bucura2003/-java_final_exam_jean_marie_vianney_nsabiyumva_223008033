package com.transport;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DriverFormDialog extends JDialog {
    private boolean success = false;
    private JTextField fullNameField, licenseField, phoneField, emailField;
    private JTextField addressField, emergencyContactField;
    private JComboBox<String> statusCombo;
    private Integer driverId;
    
    public DriverFormDialog(Frame parent, Integer driverId) {
        super(parent, driverId == null ? "Add New Driver" : "Edit Driver", true);
        this.driverId = driverId;
        initializeUI();
        if (driverId != null) {
            loadDriverData();
        }
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(500, 500);
        setLocationRelativeTo(getParent());
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Form fields
        formPanel.add(new JLabel("Full Name *:"));
        fullNameField = new JTextField();
        formPanel.add(fullNameField);
        
        formPanel.add(new JLabel("License Number *:"));
        licenseField = new JTextField();
        formPanel.add(licenseField);
        
        formPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        formPanel.add(phoneField);
        
        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);
        
        formPanel.add(new JLabel("Address:"));
        addressField = new JTextField();
        formPanel.add(addressField);
        
        formPanel.add(new JLabel("Emergency Contact:"));
        emergencyContactField = new JTextField();
        formPanel.add(emergencyContactField);
        
        formPanel.add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"Active", "On Leave", "Suspended"});
        formPanel.add(statusCombo);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> saveDriver());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set default focus
        if (driverId == null) {
            fullNameField.requestFocus();
        }
    }
    
    private void loadDriverData() {
        try {
            String query = "SELECT * FROM drivers WHERE driver_id = ?";
            PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
            pstmt.setInt(1, driverId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                fullNameField.setText(rs.getString("full_name"));
                licenseField.setText(rs.getString("license_number"));
                phoneField.setText(rs.getString("phone"));
                emailField.setText(rs.getString("email"));
                addressField.setText(rs.getString("address"));
                emergencyContactField.setText(rs.getString("emergency_contact"));
                statusCombo.setSelectedItem(rs.getString("status"));
            }
            
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading driver data: " + e.getMessage());
        }
    }
    
    private void saveDriver() {
        // Validation
        if (fullNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Full name is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            fullNameField.requestFocus();
            return;
        }
        
        if (licenseField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "License number is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            licenseField.requestFocus();
            return;
        }
        
        try {
            if (driverId == null) {
                // Check for duplicate license number
                if (isDuplicateLicense(licenseField.getText().trim())) {
                    JOptionPane.showMessageDialog(this, "License number already exists!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    licenseField.requestFocus();
                    return;
                }
                
                // Insert new driver
                String query = "INSERT INTO drivers (full_name, license_number, phone, email, " +
                             "address, emergency_contact, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
                pstmt.setString(1, fullNameField.getText().trim());
                pstmt.setString(2, licenseField.getText().trim());
                pstmt.setString(3, phoneField.getText().trim());
                pstmt.setString(4, emailField.getText().trim());
                pstmt.setString(5, addressField.getText().trim());
                pstmt.setString(6, emergencyContactField.getText().trim());
                pstmt.setString(7, (String) statusCombo.getSelectedItem());
                
                pstmt.executeUpdate();
                pstmt.close();
                
                // Add notification
                DatabaseConnection.addNotification(
                    "New Driver Added",
                    "Driver " + fullNameField.getText() + " has been added to the system",
                    "driver",
                    "Medium"
                );
                
            } else {
                // Update existing driver
                String query = "UPDATE drivers SET full_name=?, license_number=?, phone=?, " +
                             "email=?, address=?, emergency_contact=?, status=?, " +
                             "updated_at=CURRENT_TIMESTAMP WHERE driver_id=?";
                PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
                pstmt.setString(1, fullNameField.getText().trim());
                pstmt.setString(2, licenseField.getText().trim());
                pstmt.setString(3, phoneField.getText().trim());
                pstmt.setString(4, emailField.getText().trim());
                pstmt.setString(5, addressField.getText().trim());
                pstmt.setString(6, emergencyContactField.getText().trim());
                pstmt.setString(7, (String) statusCombo.getSelectedItem());
                pstmt.setInt(8, driverId);
                
                pstmt.executeUpdate();
                pstmt.close();
            }
            
            success = true;
            JOptionPane.showMessageDialog(this, 
                driverId == null ? "Driver added successfully!" : "Driver updated successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving driver: " + e.getMessage(),
                                        "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean isDuplicateLicense(String licenseNumber) throws SQLException {
        String query = "SELECT COUNT(*) as count FROM drivers WHERE license_number = ?";
        PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
        pstmt.setString(1, licenseNumber);
        ResultSet rs = pstmt.executeQuery();
        boolean exists = rs.next() && rs.getInt("count") > 0;
        rs.close();
        pstmt.close();
        return exists;
    }
    
    public boolean isSuccess() {
        return success;
    }
}