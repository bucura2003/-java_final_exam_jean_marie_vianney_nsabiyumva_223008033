package com.transport;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MaintenanceFormDialog extends JDialog {
    private boolean success = false;
    private JTextField referenceField, descriptionField, maintenanceDateField;
    private JTextField completionDateField, costField, mechanicField;
    private JComboBox<String> statusCombo, vehicleCombo;
    private Integer maintenanceId;
    
    public MaintenanceFormDialog(Frame parent, Integer maintenanceId) {
        super(parent, maintenanceId == null ? "Add New Maintenance" : "Edit Maintenance", true);
        this.maintenanceId = maintenanceId;
        initializeUI();
        loadVehicles();
        if (maintenanceId != null) {
            loadMaintenanceData();
        }
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(500, 500);
        setLocationRelativeTo(getParent());
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        formPanel.add(new JLabel("Reference ID *:"));
        referenceField = new JTextField();
        formPanel.add(referenceField);
        
        formPanel.add(new JLabel("Description *:"));
        descriptionField = new JTextField();
        formPanel.add(descriptionField);
        
        formPanel.add(new JLabel("Maintenance Date *:"));
        maintenanceDateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        formPanel.add(maintenanceDateField);
        
        formPanel.add(new JLabel("Completion Date:"));
        completionDateField = new JTextField();
        formPanel.add(completionDateField);
        
        formPanel.add(new JLabel("Cost:"));
        costField = new JTextField("0.00");
        formPanel.add(costField);
        
        formPanel.add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"Scheduled", "In Progress", "Completed", "Cancelled"});
        formPanel.add(statusCombo);
        
        formPanel.add(new JLabel("Mechanic Name:"));
        mechanicField = new JTextField();
        formPanel.add(mechanicField);
        
        formPanel.add(new JLabel("Vehicle *:"));
        vehicleCombo = new JComboBox<>();
        formPanel.add(vehicleCombo);
        
        add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> saveMaintenance());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set default focus
        if (maintenanceId == null) {
            referenceField.requestFocus();
        }
    }
    
    private void loadVehicles() {
        try {
            ResultSet rs = DatabaseConnection.executeQuery("SELECT vehicle_id, name FROM vehicles");
            vehicleCombo.addItem("Select Vehicle");
            while (rs.next()) {
                vehicleCombo.addItem(rs.getString("name") + " (ID: " + rs.getInt("vehicle_id") + ")");
            }
            rs.getStatement().close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading vehicles: " + e.getMessage());
        }
    }
    
    private void loadMaintenanceData() {
        try {
            String query = "SELECT * FROM maintenance WHERE maintenance_id = ?";
            PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
            pstmt.setInt(1, maintenanceId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                referenceField.setText(rs.getString("reference_id"));
                descriptionField.setText(rs.getString("description"));
                maintenanceDateField.setText(rs.getString("maintenance_date"));
                completionDateField.setText(rs.getString("completion_date"));
                costField.setText(String.valueOf(rs.getDouble("cost")));
                statusCombo.setSelectedItem(rs.getString("status"));
                mechanicField.setText(rs.getString("mechanic_name"));
                
                // Set vehicle selection
                int vehicleId = rs.getInt("vehicle_id");
                for (int i = 0; i < vehicleCombo.getItemCount(); i++) {
                    String item = vehicleCombo.getItemAt(i);
                    if (item.contains("(ID: " + vehicleId + ")")) {
                        vehicleCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading maintenance data: " + e.getMessage());
        }
    }
    
    private void saveMaintenance() {
        // Validation
        if (referenceField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Reference ID is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            referenceField.requestFocus();
            return;
        }
        if (descriptionField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Description is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            descriptionField.requestFocus();
            return;
        }
        
        try {
            double cost = costField.getText().isEmpty() ? 0 : Double.parseDouble(costField.getText());
            int vehicleId = extractIdFromCombo(vehicleCombo);
            
            if (vehicleId == 0) {
                JOptionPane.showMessageDialog(this, "Please select a vehicle!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (maintenanceId == null) {
                // Check for duplicate reference ID
                if (isDuplicateReference(referenceField.getText().trim())) {
                    JOptionPane.showMessageDialog(this, "Reference ID already exists!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    referenceField.requestFocus();
                    return;
                }
                
                // Insert new maintenance
                String query = "INSERT INTO maintenance (reference_id, description, maintenance_date, " +
                             "completion_date, cost, status, vehicle_id, mechanic_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
                pstmt.setString(1, referenceField.getText().trim());
                pstmt.setString(2, descriptionField.getText().trim());
                pstmt.setString(3, maintenanceDateField.getText().trim());
                pstmt.setString(4, completionDateField.getText().isEmpty() ? null : completionDateField.getText().trim());
                pstmt.setDouble(5, cost);
                pstmt.setString(6, (String) statusCombo.getSelectedItem());
                pstmt.setInt(7, vehicleId);
                pstmt.setString(8, mechanicField.getText().trim());
                
                pstmt.executeUpdate();
                pstmt.close();
                
                DatabaseConnection.addNotification(
                    "New Maintenance Scheduled",
                    "Maintenance " + referenceField.getText() + " scheduled",
                    "maintenance",
                    "Medium"
                );
                
            } else {
                // Update existing maintenance
                String query = "UPDATE maintenance SET reference_id=?, description=?, maintenance_date=?, " +
                             "completion_date=?, cost=?, status=?, vehicle_id=?, mechanic_name=? WHERE maintenance_id=?";
                PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
                pstmt.setString(1, referenceField.getText().trim());
                pstmt.setString(2, descriptionField.getText().trim());
                pstmt.setString(3, maintenanceDateField.getText().trim());
                pstmt.setString(4, completionDateField.getText().isEmpty() ? null : completionDateField.getText().trim());
                pstmt.setDouble(5, cost);
                pstmt.setString(6, (String) statusCombo.getSelectedItem());
                pstmt.setInt(7, vehicleId);
                pstmt.setString(8, mechanicField.getText().trim());
                pstmt.setInt(9, maintenanceId);
                
                pstmt.executeUpdate();
                pstmt.close();
            }
            
            success = true;
            JOptionPane.showMessageDialog(this, 
                maintenanceId == null ? "Maintenance added successfully!" : "Maintenance updated successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for cost!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            costField.requestFocus();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving maintenance: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean isDuplicateReference(String referenceId) throws SQLException {
        String query = "SELECT COUNT(*) as count FROM maintenance WHERE reference_id = ?";
        PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
        pstmt.setString(1, referenceId);
        ResultSet rs = pstmt.executeQuery();
        boolean exists = rs.next() && rs.getInt("count") > 0;
        rs.close();
        pstmt.close();
        return exists;
    }
    
    private int extractIdFromCombo(JComboBox<String> combo) {
        String selected = (String) combo.getSelectedItem();
        if (selected != null && selected.contains("(ID: ") && !selected.equals("Select Vehicle")) {
            return Integer.parseInt(selected.split("\\(ID: ")[1].replace(")", ""));
        }
        return 0;
    }
    
    public boolean isSuccess() {
        return success;
    }
}