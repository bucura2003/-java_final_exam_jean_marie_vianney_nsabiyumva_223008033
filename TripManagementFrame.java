package com.Transport;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class TripManagementFrame extends JFrame {
    private Driver currentDriver;
    private TripService tripService;
    private JTable tripsTable;
    private DefaultTableModel tableModel;
    
    public TripManagementFrame(Driver driver) {
        this.currentDriver = driver;
        this.tripService = new TripService();
        initializeUI();
        loadTrips();
    }
    
    private void initializeUI() {
        setTitle("Trip Management - " + currentDriver.getFullName());
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JLabel headerLabel = new JLabel("My Trips", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Table
        String[] columns = {"Trip ID", "From", "To", "Start Time", "End Time", "Distance", "Status", "Fare"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tripsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tripsTable);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("Refresh");
        JButton startTripButton = new JButton("Start Trip");
        JButton completeTripButton = new JButton("Complete Trip");
        JButton newTripButton = new JButton("New Trip");
        
        refreshButton.addActionListener(e -> loadTrips());
        startTripButton.addActionListener(e -> startSelectedTrip());
        completeTripButton.addActionListener(e -> completeSelectedTrip());
        newTripButton.addActionListener(e -> createNewTrip());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(startTripButton);
        buttonPanel.add(completeTripButton);
        buttonPanel.add(newTripButton);
        
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void loadTrips() {
        tableModel.setRowCount(0);
        List<Trip> trips = tripService.getTripsByDriverId(currentDriver.getDriverId());
        
        for (Trip trip : trips) {
            Object[] row = {
                trip.getTripNumber(),
                trip.getStartLocation(),
                trip.getEndLocation(),
                trip.getStartTime() != null ? trip.getStartTime().toString() : "Not started",
                trip.getEndTime() != null ? trip.getEndTime().toString() : "Not completed",
                trip.getDistance() != null ? trip.getDistance() + " km" : "N/A",
                trip.getStatus(),
                trip.getTotalFare() != null ? "$" + trip.getTotalFare() : "N/A"
            };
            tableModel.addRow(row);
        }
    }
    
    private void startSelectedTrip() {
        int selectedRow = tripsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a trip first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String tripNumber = (String) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 6);
        
        if (!"Scheduled".equals(status)) {
            JOptionPane.showMessageDialog(this, "Only scheduled trips can be started.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // In a real application, you would get the trip ID from the database
        // For now, we'll simulate starting a trip
        JOptionPane.showMessageDialog(this, "Trip " + tripNumber + " started successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        loadTrips(); // Refresh the list
    }
    
    private void completeSelectedTrip() {
        int selectedRow = tripsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a trip first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String tripNumber = (String) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 6);
        
        if (!"In Progress".equals(status)) {
            JOptionPane.showMessageDialog(this, "Only trips in progress can be completed.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Simulate completing a trip
        JOptionPane.showMessageDialog(this, "Trip " + tripNumber + " completed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        loadTrips(); // Refresh the list
    }
    
    private void createNewTrip() {
        JTextField fromField = new JTextField();
        JTextField toField = new JTextField();
        JTextField distanceField = new JTextField();
        JTextField fareField = new JTextField();
        
        Object[] message = {
            "From:", fromField,
            "To:", toField,
            "Distance (km):", distanceField,
            "Fare:", fareField
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Create New Trip", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            if (fromField.getText().trim().isEmpty() || toField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Trip newTrip = new Trip();
            newTrip.setTripNumber("TRIP" + System.currentTimeMillis());
            newTrip.setStartLocation(fromField.getText().trim());
            newTrip.setEndLocation(toField.getText().trim());
            newTrip.setDriverId(currentDriver.getDriverId());
            newTrip.setStatus("Scheduled");
            
            try {
                if (!distanceField.getText().trim().isEmpty()) {
                    newTrip.setDistance(Double.parseDouble(distanceField.getText().trim()));
                }
                if (!fareField.getText().trim().isEmpty()) {
                    newTrip.setTotalFare(Double.parseDouble(fareField.getText().trim()));
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for distance and fare.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean success = tripService.createTrip(newTrip);
            if (success) {
                JOptionPane.showMessageDialog(this, "New trip created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTrips();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create trip.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}