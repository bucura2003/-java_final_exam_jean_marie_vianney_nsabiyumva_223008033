package com.transport;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TripFormDialog extends JDialog {
    private boolean success = false;
    private JTextField orderNumberField, dateField, startLocationField, endLocationField;
    private JTextField totalAmountField, passengerCountField;
    private JComboBox<String> statusCombo, vehicleCombo, driverCombo, routeCombo;
    private JComboBox<String> paymentMethodCombo;
    private JTextArea notesArea;
    private Integer tripId;
    
    public TripFormDialog(Frame parent, Integer tripId) {
        super(parent, tripId == null ? "Add New Trip" : "Edit Trip", true);
        this.tripId = tripId;
        initializeUI();
        loadComboBoxData();
        if (tripId != null) {
            loadTripData();
        }
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(600, 600);
        setLocationRelativeTo(getParent());
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Form fields
        formPanel.add(new JLabel("Order Number *:"));
        orderNumberField = new JTextField();
        formPanel.add(orderNumberField);
        
        formPanel.add(new JLabel("Date *:"));
        dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        formPanel.add(dateField);
        
        formPanel.add(new JLabel("Start Location:"));
        startLocationField = new JTextField();
        formPanel.add(startLocationField);
        
        formPanel.add(new JLabel("End Location:"));
        endLocationField = new JTextField();
        formPanel.add(endLocationField);
        
        formPanel.add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"Scheduled", "In Progress", "Completed", "Cancelled"});
        formPanel.add(statusCombo);
        
        formPanel.add(new JLabel("Vehicle *:"));
        vehicleCombo = new JComboBox<>();
        formPanel.add(vehicleCombo);
        
        formPanel.add(new JLabel("Driver *:"));
        driverCombo = new JComboBox<>();
        formPanel.add(driverCombo);
        
        formPanel.add(new JLabel("Route:"));
        routeCombo = new JComboBox<>();
        formPanel.add(routeCombo);
        
        formPanel.add(new JLabel("Total Amount:"));
        totalAmountField = new JTextField("0.00");
        formPanel.add(totalAmountField);
        
        formPanel.add(new JLabel("Payment Method:"));
        paymentMethodCombo = new JComboBox<>(new String[]{"Cash", "Card", "Mobile", "Not Paid"});
        formPanel.add(paymentMethodCombo);
        
        formPanel.add(new JLabel("Passenger Count:"));
        passengerCountField = new JTextField("1");
        formPanel.add(passengerCountField);
        
        formPanel.add(new JLabel("Notes:"));
        notesArea = new JTextArea(3, 20);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        formPanel.add(notesScroll);
        
        add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> saveTrip());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set default focus
        if (tripId == null) {
            orderNumberField.requestFocus();
        }
    }
    
    private void loadComboBoxData() {
        try {
            // Load vehicles
            ResultSet rs = DatabaseConnection.executeQuery(
                "SELECT vehicle_id, name FROM vehicles WHERE status IN ('Available', 'On Trip')"
            );
            vehicleCombo.addItem("Select Vehicle");
            while (rs.next()) {
                vehicleCombo.addItem(rs.getString("name") + " (ID: " + rs.getInt("vehicle_id") + ")");
            }
            rs.getStatement().close();
            
            // Load drivers
            rs = DatabaseConnection.executeQuery(
                "SELECT driver_id, full_name FROM drivers WHERE status = 'Active'"
            );
            driverCombo.addItem("Select Driver");
            while (rs.next()) {
                driverCombo.addItem(rs.getString("full_name") + " (ID: " + rs.getInt("driver_id") + ")");
            }
            rs.getStatement().close();
            
            // Load routes
            rs = DatabaseConnection.executeQuery("SELECT route_id, route_name FROM routes WHERE status = 'Active'");
            routeCombo.addItem("Select Route");
            while (rs.next()) {
                routeCombo.addItem(rs.getString("route_name") + " (ID: " + rs.getInt("route_id") + ")");
            }
            rs.getStatement().close();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }
    
    private void loadTripData() {
        try {
            String query = "SELECT * FROM trips WHERE trip_id = ?";
            PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
            pstmt.setInt(1, tripId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                orderNumberField.setText(rs.getString("order_number"));
                dateField.setText(rs.getString("date"));
                startLocationField.setText(rs.getString("start_location"));
                endLocationField.setText(rs.getString("end_location"));
                statusCombo.setSelectedItem(rs.getString("status"));
                totalAmountField.setText(String.valueOf(rs.getDouble("total_amount")));
                paymentMethodCombo.setSelectedItem(rs.getString("payment_method"));
                passengerCountField.setText(String.valueOf(rs.getInt("passenger_count")));
                notesArea.setText(rs.getString("notes"));
                
                // Set combo box selections
                setComboSelection(vehicleCombo, rs.getInt("vehicle_id"));
                setComboSelection(driverCombo, rs.getInt("driver_id"));
                setComboSelection(routeCombo, rs.getInt("route_id"));
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading trip data: " + e.getMessage());
        }
    }
    
    private void setComboSelection(JComboBox<String> combo, int id) {
        if (id > 0) {
            for (int i = 0; i < combo.getItemCount(); i++) {
                String item = combo.getItemAt(i);
                if (item.contains("(ID: " + id + ")")) {
                    combo.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
    
    private void saveTrip() {
        // Validation
        if (orderNumberField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Order number is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            orderNumberField.requestFocus();
            return;
        }
        
        try {
            double totalAmount = totalAmountField.getText().isEmpty() ? 0 : Double.parseDouble(totalAmountField.getText());
            int passengerCount = passengerCountField.getText().isEmpty() ? 1 : Integer.parseInt(passengerCountField.getText());
            int vehicleId = extractIdFromCombo(vehicleCombo);
            int driverId = extractIdFromCombo(driverCombo);
            int routeId = extractIdFromCombo(routeCombo);
            
            if (vehicleId == 0 || driverId == 0) {
                JOptionPane.showMessageDialog(this, "Please select both vehicle and driver!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (tripId == null) {
                // Check for duplicate order number
                if (isDuplicateOrderNumber(orderNumberField.getText().trim())) {
                    JOptionPane.showMessageDialog(this, "Order number already exists!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    orderNumberField.requestFocus();
                    return;
                }
                
                // Insert new trip
                String query = "INSERT INTO trips (order_number, date, start_location, end_location, status, " +
                             "total_amount, payment_method, passenger_count, notes, vehicle_id, driver_id, route_id) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
                pstmt.setString(1, orderNumberField.getText().trim());
                pstmt.setString(2, dateField.getText().trim());
                pstmt.setString(3, startLocationField.getText().trim());
                pstmt.setString(4, endLocationField.getText().trim());
                pstmt.setString(5, (String) statusCombo.getSelectedItem());
                pstmt.setDouble(6, totalAmount);
                pstmt.setString(7, (String) paymentMethodCombo.getSelectedItem());
                pstmt.setInt(8, passengerCount);
                pstmt.setString(9, notesArea.getText().trim());
                pstmt.setInt(10, vehicleId);
                pstmt.setInt(11, driverId);
                pstmt.setInt(12, routeId == 0 ? null : routeId);
                
                pstmt.executeUpdate();
                pstmt.close();
                
                DatabaseConnection.addNotification(
                    "New Trip Scheduled",
                    "Trip " + orderNumberField.getText() + " has been scheduled",
                    "trip",
                    "Medium"
                );
                
            } else {
                // Update existing trip
                String query = "UPDATE trips SET order_number=?, date=?, start_location=?, end_location=?, " +
                             "status=?, total_amount=?, payment_method=?, passenger_count=?, notes=?, " +
                             "vehicle_id=?, driver_id=?, route_id=? WHERE trip_id=?";
                PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
                pstmt.setString(1, orderNumberField.getText().trim());
                pstmt.setString(2, dateField.getText().trim());
                pstmt.setString(3, startLocationField.getText().trim());
                pstmt.setString(4, endLocationField.getText().trim());
                pstmt.setString(5, (String) statusCombo.getSelectedItem());
                pstmt.setDouble(6, totalAmount);
                pstmt.setString(7, (String) paymentMethodCombo.getSelectedItem());
                pstmt.setInt(8, passengerCount);
                pstmt.setString(9, notesArea.getText().trim());
                pstmt.setInt(10, vehicleId);
                pstmt.setInt(11, driverId);
                pstmt.setInt(12, routeId == 0 ? null : routeId);
                pstmt.setInt(13, tripId);
                
                pstmt.executeUpdate();
                pstmt.close();
            }
            
            success = true;
            JOptionPane.showMessageDialog(this, 
                tripId == null ? "Trip added successfully!" : "Trip updated successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for amount and passenger count!", "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving trip: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean isDuplicateOrderNumber(String orderNumber) throws SQLException {
        String query = "SELECT COUNT(*) as count FROM trips WHERE order_number = ?";
        PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
        pstmt.setString(1, orderNumber);
        ResultSet rs = pstmt.executeQuery();
        boolean exists = rs.next() && rs.getInt("count") > 0;
        rs.close();
        pstmt.close();
        return exists;
    }
    
    private int extractIdFromCombo(JComboBox<String> combo) {
        String selected = (String) combo.getSelectedItem();
        if (selected != null && selected.contains("(ID: ") && !selected.equals("Select Vehicle") && 
            !selected.equals("Select Driver") && !selected.equals("Select Route")) {
            return Integer.parseInt(selected.split("\\(ID: ")[1].replace(")", ""));
        }
        return 0;
    }
    
    public boolean isSuccess() {
        return success;
    }
}