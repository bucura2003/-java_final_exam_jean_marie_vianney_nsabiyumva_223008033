package com.transport;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RouteFormDialog extends JDialog {
    private boolean success = false;
    private JTextField routeNameField, startPointField, endPointField;
    private JTextField distanceField, timeField, fareField;
    private JComboBox<String> statusCombo;
    private Integer routeId;
    
    public RouteFormDialog(Frame parent, Integer routeId) {
        super(parent, routeId == null ? "Add New Route" : "Edit Route", true);
        this.routeId = routeId;
        initializeUI();
        if (routeId != null) {
            loadRouteData();
        }
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(500, 400);
        setLocationRelativeTo(getParent());
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        formPanel.add(new JLabel("Route Name *:"));
        routeNameField = new JTextField();
        formPanel.add(routeNameField);
        
        formPanel.add(new JLabel("Start Point:"));
        startPointField = new JTextField();
        formPanel.add(startPointField);
        
        formPanel.add(new JLabel("End Point:"));
        endPointField = new JTextField();
        formPanel.add(endPointField);
        
        formPanel.add(new JLabel("Distance (km):"));
        distanceField = new JTextField();
        formPanel.add(distanceField);
        
        formPanel.add(new JLabel("Estimated Time (min):"));
        timeField = new JTextField();
        formPanel.add(timeField);
        
        formPanel.add(new JLabel("Fare per km:"));
        fareField = new JTextField("0.00");
        formPanel.add(fareField);
        
        formPanel.add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"Active", "Inactive"});
        formPanel.add(statusCombo);
        
        add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> saveRoute());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set default focus
        if (routeId == null) {
            routeNameField.requestFocus();
        }
    }
    
    private void loadRouteData() {
        try {
            String query = "SELECT * FROM routes WHERE route_id = ?";
            PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
            pstmt.setInt(1, routeId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                routeNameField.setText(rs.getString("route_name"));
                startPointField.setText(rs.getString("start_point"));
                endPointField.setText(rs.getString("end_point"));
                distanceField.setText(String.valueOf(rs.getDouble("distance_km")));
                timeField.setText(String.valueOf(rs.getInt("estimated_time_minutes")));
                fareField.setText(String.valueOf(rs.getDouble("fare_per_km")));
                statusCombo.setSelectedItem(rs.getString("status"));
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading route data: " + e.getMessage());
        }
    }
    
    private void saveRoute() {
        // Validation
        if (routeNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Route name is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            routeNameField.requestFocus();
            return;
        }
        
        try {
            double distance = distanceField.getText().isEmpty() ? 0 : Double.parseDouble(distanceField.getText());
            int time = timeField.getText().isEmpty() ? 0 : Integer.parseInt(timeField.getText());
            double fare = fareField.getText().isEmpty() ? 0 : Double.parseDouble(fareField.getText());
            
            if (routeId == null) {
                // Insert new route
                String query = "INSERT INTO routes (route_name, start_point, end_point, distance_km, " +
                             "estimated_time_minutes, fare_per_km, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
                pstmt.setString(1, routeNameField.getText().trim());
                pstmt.setString(2, startPointField.getText().trim());
                pstmt.setString(3, endPointField.getText().trim());
                pstmt.setDouble(4, distance);
                pstmt.setInt(5, time);
                pstmt.setDouble(6, fare);
                pstmt.setString(7, (String) statusCombo.getSelectedItem());
                
                pstmt.executeUpdate();
                pstmt.close();
                
                DatabaseConnection.addNotification(
                    "New Route Added",
                    "Route " + routeNameField.getText() + " has been added",
                    "route",
                    "Medium"
                );
                
            } else {
                // Update existing route
                String query = "UPDATE routes SET route_name=?, start_point=?, end_point=?, distance_km=?, " +
                             "estimated_time_minutes=?, fare_per_km=?, status=? WHERE route_id=?";
                PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
                pstmt.setString(1, routeNameField.getText().trim());
                pstmt.setString(2, startPointField.getText().trim());
                pstmt.setString(3, endPointField.getText().trim());
                pstmt.setDouble(4, distance);
                pstmt.setInt(5, time);
                pstmt.setDouble(6, fare);
                pstmt.setString(7, (String) statusCombo.getSelectedItem());
                pstmt.setInt(8, routeId);
                
                pstmt.executeUpdate();
                pstmt.close();
            }
            
            success = true;
            JOptionPane.showMessageDialog(this, 
                routeId == null ? "Route added successfully!" : "Route updated successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for distance, time and fare!", "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving route: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isSuccess() {
        return success;
    }
}