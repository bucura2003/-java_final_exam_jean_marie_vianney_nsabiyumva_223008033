package com.transport;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VehicleFormDialog extends JDialog {
    private boolean success = false;
    private JTextField nameField, identifierField, capacityField, locationField, contactField;
    private JComboBox<String> typeCombo, statusCombo, driverCombo;
    private Integer vehicleId;
    
    public VehicleFormDialog(Frame parent, Integer vehicleId) {
        super(parent, vehicleId == null ? "Add New Vehicle" : "Edit Vehicle", true);
        this.vehicleId = vehicleId;
        initializeUI();
        loadDrivers();
        if (vehicleId != null) {
            loadVehicleData();
        }
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(500, 400);
        setLocationRelativeTo(getParent());
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        formPanel.add(new JLabel("Vehicle Name *:"));
        nameField = new JTextField();
        formPanel.add(nameField);
        
        formPanel.add(new JLabel("Identifier *:"));
        identifierField = new JTextField();
        formPanel.add(identifierField);
        
        formPanel.add(new JLabel("Type *:"));
        typeCombo = new JComboBox<>(new String[]{"Bus", "Coach", "Van", "Shuttle", "Car"});
        formPanel.add(typeCombo);
        
        formPanel.add(new JLabel("Capacity *:"));
        capacityField = new JTextField();
        formPanel.add(capacityField);
        
        formPanel.add(new JLabel("Location:"));
        locationField = new JTextField();
        formPanel.add(locationField);
        
        formPanel.add(new JLabel("Contact:"));
        contactField = new JTextField();
        formPanel.add(contactField);
        
        formPanel.add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"Available", "On Trip", "Maintenance", "Out of Service"});
        formPanel.add(statusCombo);
        
        formPanel.add(new JLabel("Assigned Driver:"));
        driverCombo = new JComboBox<>();
        driverCombo.addItem("Unassigned");
        formPanel.add(driverCombo);
        
        add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> saveVehicle());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set default focus
        if (vehicleId == null) {
            nameField.requestFocus();
        }
    }
    
    private void loadDrivers() {
        try {
            ResultSet rs = DatabaseConnection.executeQuery(
                "SELECT driver_id, full_name FROM drivers WHERE status = 'Active'"
            );
            while (rs.next()) {
                driverCombo.addItem(rs.getString("full_name") + " (ID: " + rs.getInt("driver_id") + ")");
            }
            rs.getStatement().close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading drivers: " + e.getMessage());
        }
    }
    
    private void loadVehicleData() {
        try {
            String query = "SELECT v.*, d.full_name FROM vehicles v LEFT JOIN drivers d ON v.driver_id = d.driver_id WHERE v.vehicle_id = ?";
            PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
            pstmt.setInt(1, vehicleId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                identifierField.setText(rs.getString("identifier"));
                typeCombo.setSelectedItem(rs.getString("type"));
                capacityField.setText(String.valueOf(rs.getInt("capacity")));
                locationField.setText(rs.getString("location"));
                contactField.setText(rs.getString("contact"));
                statusCombo.setSelectedItem(rs.getString("status"));
                
                if (rs.getString("full_name") != null) {
                    driverCombo.setSelectedItem(rs.getString("full_name") + " (ID: " + rs.getInt("driver_id") + ")");
                }
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading vehicle data: " + e.getMessage());
        }
    }
    
    private void saveVehicle() {
        // Validation
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vehicle name is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return;
        }
        if (identifierField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vehicle identifier is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            identifierField.requestFocus();
            return;
        }
        if (capacityField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Capacity is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            capacityField.requestFocus();
            return;
        }
        
        try {
            int capacity = Integer.parseInt(capacityField.getText().trim());
            Integer driverId = extractIdFromCombo(driverCombo);
            
            if (vehicleId == null) {
                // Check for duplicate identifier
                if (isDuplicateIdentifier(identifierField.getText().trim())) {
                    JOptionPane.showMessageDialog(this, "Vehicle identifier already exists!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    identifierField.requestFocus();
                    return;
                }
                
                // Insert new vehicle
                String query = "INSERT INTO vehicles (name, identifier, type, capacity, location, contact, status, driver_id) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
                pstmt.setString(1, nameField.getText().trim());
                pstmt.setString(2, identifierField.getText().trim());
                pstmt.setString(3, (String) typeCombo.getSelectedItem());
                pstmt.setInt(4, capacity);
                pstmt.setString(5, locationField.getText().trim());
                pstmt.setString(6, contactField.getText().trim());
                pstmt.setString(7, (String) statusCombo.getSelectedItem());
                pstmt.setObject(8, driverId);
                
                pstmt.executeUpdate();
                pstmt.close();
                
                DatabaseConnection.addNotification(
                    "New Vehicle Added",
                    "Vehicle " + identifierField.getText() + " has been added",
                    "vehicle",
                    "Medium"
                );
                
            } else {
                // Update vehicle
                String query = "UPDATE vehicles SET name=?, identifier=?, type=?, capacity=?, " +
                             "location=?, contact=?, status=?, driver_id=? WHERE vehicle_id=?";
                PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
                pstmt.setString(1, nameField.getText().trim());
                pstmt.setString(2, identifierField.getText().trim());
                pstmt.setString(3, (String) typeCombo.getSelectedItem());
                pstmt.setInt(4, capacity);
                pstmt.setString(5, locationField.getText().trim());
                pstmt.setString(6, contactField.getText().trim());
                pstmt.setString(7, (String) statusCombo.getSelectedItem());
                pstmt.setObject(8, driverId);
                pstmt.setInt(9, vehicleId);
                
                pstmt.executeUpdate();
                pstmt.close();
            }
            
            success = true;
            JOptionPane.showMessageDialog(this, 
                vehicleId == null ? "Vehicle added successfully!" : "Vehicle updated successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Capacity must be a valid number!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            capacityField.requestFocus();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving vehicle: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean isDuplicateIdentifier(String identifier) throws SQLException {
        String query = "SELECT COUNT(*) as count FROM vehicles WHERE identifier = ?";
        PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
        pstmt.setString(1, identifier);
        ResultSet rs = pstmt.executeQuery();
        boolean exists = rs.next() && rs.getInt("count") > 0;
        rs.close();
        pstmt.close();
        return exists;
    }
    
    private Integer extractIdFromCombo(JComboBox<String> combo) {
        String selected = (String) combo.getSelectedItem();
        if (selected != null && selected.contains("(ID: ") && !selected.equals("Unassigned")) {
            return Integer.parseInt(selected.split("\\(ID: ")[1].replace(")", ""));
        }
        return null;
    }
    
    public boolean isSuccess() {
        return success;
    }
}