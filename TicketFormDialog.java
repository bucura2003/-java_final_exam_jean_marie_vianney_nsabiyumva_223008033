package com.transport;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TicketFormDialog extends JDialog {
    private boolean success = false;
    private JTextField ticketNumberField, passengerNameField, passengerPhoneField;
    private JTextField passengerEmailField, seatNumberField, priceField;
    private JComboBox<String> statusCombo, tripCombo;
    private Integer ticketId;
    
    public TicketFormDialog(Frame parent, Integer ticketId) {
        super(parent, ticketId == null ? "Add New Ticket" : "Edit Ticket", true);
        this.ticketId = ticketId;
        initializeUI();
        loadTrips();
        if (ticketId != null) {
            loadTicketData();
        }
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(500, 500);
        setLocationRelativeTo(getParent());
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        formPanel.add(new JLabel("Ticket Number *:"));
        ticketNumberField = new JTextField();
        formPanel.add(ticketNumberField);
        
        formPanel.add(new JLabel("Passenger Name *:"));
        passengerNameField = new JTextField();
        formPanel.add(passengerNameField);
        
        formPanel.add(new JLabel("Passenger Phone:"));
        passengerPhoneField = new JTextField();
        formPanel.add(passengerPhoneField);
        
        formPanel.add(new JLabel("Passenger Email:"));
        passengerEmailField = new JTextField();
        formPanel.add(passengerEmailField);
        
        formPanel.add(new JLabel("Seat Number:"));
        seatNumberField = new JTextField();
        formPanel.add(seatNumberField);
        
        formPanel.add(new JLabel("Price:"));
        priceField = new JTextField("0.00");
        formPanel.add(priceField);
        
        formPanel.add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"Confirmed", "Pending", "Cancelled"});
        formPanel.add(statusCombo);
        
        formPanel.add(new JLabel("Trip *:"));
        tripCombo = new JComboBox<>();
        formPanel.add(tripCombo);
        
        add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> saveTicket());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set default focus
        if (ticketId == null) {
            ticketNumberField.requestFocus();
        }
    }
    
    private void loadTrips() {
        try {
            ResultSet rs = DatabaseConnection.executeQuery(
                "SELECT trip_id, order_number FROM trips ORDER BY date DESC"
            );
            tripCombo.addItem("Select Trip");
            while (rs.next()) {
                tripCombo.addItem(rs.getString("order_number") + " (ID: " + rs.getInt("trip_id") + ")");
            }
            rs.getStatement().close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading trips: " + e.getMessage());
        }
    }
    
    private void loadTicketData() {
        try {
            String query = "SELECT * FROM tickets WHERE ticket_id = ?";
            PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
            pstmt.setInt(1, ticketId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                ticketNumberField.setText(rs.getString("ticket_number"));
                passengerNameField.setText(rs.getString("passenger_name"));
                passengerPhoneField.setText(rs.getString("passenger_phone"));
                passengerEmailField.setText(rs.getString("passenger_email"));
                seatNumberField.setText(rs.getString("seat_number"));
                priceField.setText(String.valueOf(rs.getDouble("price")));
                statusCombo.setSelectedItem(rs.getString("status"));
                
                // Set trip selection
                int tripId = rs.getInt("trip_id");
                for (int i = 0; i < tripCombo.getItemCount(); i++) {
                    String item = tripCombo.getItemAt(i);
                    if (item.contains("(ID: " + tripId + ")")) {
                        tripCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading ticket data: " + e.getMessage());
        }
    }
    
    private void saveTicket() {
        // Validation
        if (ticketNumberField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ticket number is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            ticketNumberField.requestFocus();
            return;
        }
        if (passengerNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Passenger name is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            passengerNameField.requestFocus();
            return;
        }
        
        try {
            double price = priceField.getText().isEmpty() ? 0 : Double.parseDouble(priceField.getText());
            int tripId = extractIdFromCombo(tripCombo);
            
            if (tripId == 0) {
                JOptionPane.showMessageDialog(this, "Please select a trip!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (ticketId == null) {
                // Check for duplicate ticket number
                if (isDuplicateTicketNumber(ticketNumberField.getText().trim())) {
                    JOptionPane.showMessageDialog(this, "Ticket number already exists!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    ticketNumberField.requestFocus();
                    return;
                }
                
                // Insert new ticket
                String query = "INSERT INTO tickets (ticket_number, passenger_name, passenger_phone, " +
                             "passenger_email, seat_number, price, status, trip_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
                pstmt.setString(1, ticketNumberField.getText().trim());
                pstmt.setString(2, passengerNameField.getText().trim());
                pstmt.setString(3, passengerPhoneField.getText().trim());
                pstmt.setString(4, passengerEmailField.getText().trim());
                pstmt.setString(5, seatNumberField.getText().trim());
                pstmt.setDouble(6, price);
                pstmt.setString(7, (String) statusCombo.getSelectedItem());
                pstmt.setInt(8, tripId);
                
                pstmt.executeUpdate();
                pstmt.close();
                
                DatabaseConnection.addNotification(
                    "New Ticket Booked",
                    "Ticket " + ticketNumberField.getText() + " for " + passengerNameField.getText(),
                    "ticket",
                    "Low"
                );
                
            } else {
                // Update existing ticket
                String query = "UPDATE tickets SET ticket_number=?, passenger_name=?, passenger_phone=?, " +
                             "passenger_email=?, seat_number=?, price=?, status=? WHERE ticket_id=?";
                PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
                pstmt.setString(1, ticketNumberField.getText().trim());
                pstmt.setString(2, passengerNameField.getText().trim());
                pstmt.setString(3, passengerPhoneField.getText().trim());
                pstmt.setString(4, passengerEmailField.getText().trim());
                pstmt.setString(5, seatNumberField.getText().trim());
                pstmt.setDouble(6, price);
                pstmt.setString(7, (String) statusCombo.getSelectedItem());
                pstmt.setInt(8, ticketId);
                
                pstmt.executeUpdate();
                pstmt.close();
            }
            
            success = true;
            JOptionPane.showMessageDialog(this, 
                ticketId == null ? "Ticket added successfully!" : "Ticket updated successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for price!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            priceField.requestFocus();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving ticket: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean isDuplicateTicketNumber(String ticketNumber) throws SQLException {
        String query = "SELECT COUNT(*) as count FROM tickets WHERE ticket_number = ?";
        PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
        pstmt.setString(1, ticketNumber);
        ResultSet rs = pstmt.executeQuery();
        boolean exists = rs.next() && rs.getInt("count") > 0;
        rs.close();
        pstmt.close();
        return exists;
    }
    
    private int extractIdFromCombo(JComboBox<String> combo) {
        String selected = (String) combo.getSelectedItem();
        if (selected != null && selected.contains("(ID: ") && !selected.equals("Select Trip")) {
            return Integer.parseInt(selected.split("\\(ID: ")[1].replace(")", ""));
        }
        return 0;
    }
    
    public boolean isSuccess() {
        return success;
    }
}