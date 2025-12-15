package com.Transport;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VehicleManagementFrame extends JFrame {
    private Driver currentDriver;
    private VehicleService vehicleService;
    private JTable vehiclesTable;
    private DefaultTableModel tableModel;
    
    public VehicleManagementFrame(Driver driver) {
        this.currentDriver = driver;
        this.vehicleService = new VehicleService();
        initializeUI();
        loadVehicles();
    }
    
    private void initializeUI() {
        setTitle("Vehicle Management - " + currentDriver.getFullName());
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JLabel headerLabel = new JLabel("My Vehicles", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Table
        String[] columns = {"ID", "License Plate", "Make", "Model", "Year", "Color", "Capacity", "Status", "Last Maintenance"};
        tableModel = new DefaultTableModel(columns, 0);
        vehiclesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(vehiclesTable);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("Refresh");
        JButton requestMaintenanceButton = new JButton("Request Maintenance");
        
        refreshButton.addActionListener(e -> loadVehicles());
        requestMaintenanceButton.addActionListener(e -> requestMaintenance());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(requestMaintenanceButton);
        
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void loadVehicles() {
        tableModel.setRowCount(0);
        List<Vehicle> vehicles = vehicleService.getAvailableVehicles();
        
        for (Vehicle vehicle : vehicles) {
            Object[] row = {
                vehicle.getVehicleId(),
                vehicle.getLicensePlate(),
                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getYear(),
                vehicle.getColor(),
                vehicle.getCapacity(),
                vehicle.getStatus(),
                vehicle.getLastMaintenanceDate() != null ? 
                    vehicle.getLastMaintenanceDate().toLocalDate().toString() : "Never"
            };
            tableModel.addRow(row);
        }
        
        if (vehicles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No vehicles available at the moment.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void requestMaintenance() {
        int selectedRow = vehiclesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int vehicleId = (int) tableModel.getValueAt(selectedRow, 0);
        String vehicleInfo = (String) tableModel.getValueAt(selectedRow, 1) + " " + 
                           (String) tableModel.getValueAt(selectedRow, 2) + " " + 
                           (String) tableModel.getValueAt(selectedRow, 3);
        
        String issue = JOptionPane.showInputDialog(this, "Describe the maintenance issue for:\n" + vehicleInfo);
        if (issue != null && !issue.trim().isEmpty()) {
            boolean success = vehicleService.markVehicleForMaintenance(vehicleId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Maintenance request submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadVehicles();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to submit maintenance request.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}