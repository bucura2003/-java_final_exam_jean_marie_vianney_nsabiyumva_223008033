package com.Transport;

import com.Transport.Driver;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainDashboard extends JFrame {
    private Driver currentDriver;
    private JLabel welcomeLabel;
    
    public MainDashboard(Driver driver) {
        this.currentDriver = driver;
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Transport Automation System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        JMenu managementMenu = new JMenu("Management");
        JMenuItem driversItem = new JMenuItem("Driver Management");
        JMenuItem vehiclesItem = new JMenuItem("Vehicle Management");
        JMenuItem tripsItem = new JMenuItem("Trip Management");
        
        managementMenu.add(driversItem);
        managementMenu.add(vehiclesItem);
        managementMenu.add(tripsItem);
        
        menuBar.add(fileMenu);
        menuBar.add(managementMenu);
        setJMenuBar(menuBar);
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        welcomeLabel = new JLabel("Welcome, " + currentDriver.getFullName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(new Color(0, 100, 200));
        
        JLabel roleLabel = new JLabel("Driver ID: " + currentDriver.getDriverId());
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(roleLabel, BorderLayout.EAST);
        
        // Dashboard cards panel
        JPanel cardsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cardsPanel.setBackground(new Color(240, 240, 240));
        
        // Create dashboard cards
        cardsPanel.add(createDashboardCard("👤 Driver Profile", "View and manage your profile", Color.BLUE));
        cardsPanel.add(createDashboardCard("🚗 Vehicles", "Manage vehicles and assignments", Color.GREEN));
        cardsPanel.add(createDashboardCard("🗺️ Trips", "View and manage trips", Color.ORANGE));
        cardsPanel.add(createDashboardCard("🎫 Tickets", "Handle passenger tickets", Color.MAGENTA));
        cardsPanel.add(createDashboardCard("🔧 Maintenance", "Vehicle maintenance tracking", Color.RED));
        cardsPanel.add(createDashboardCard("📊 Reports", "View performance reports", Color.CYAN));
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(cardsPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Event listeners
        setupEventListeners(logoutItem, exitItem, driversItem, vehiclesItem, tripsItem);
    }
    
    private void setupEventListeners(JMenuItem logoutItem, JMenuItem exitItem, JMenuItem driversItem,
			JMenuItem vehiclesItem, JMenuItem tripsItem) {
		// TODO Auto-generated method stub
		
	}

	private JPanel createDashboardCard(String title, String description, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(color);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);
        
        // Make it clickable
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Add click listener based on title
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                handleCardClick(title);
            }
        });
        
        return card;
    }

    private void handleCardClick(String cardTitle) {
        switch (cardTitle) {
            case "👤 Driver Profile":
                showDriverProfile();
                break;
            case "🚗 Vehicles":
                new VehicleManagementFrame(currentDriver).setVisible(true);
                break;
            case "🗺️ Trips":
                new TripManagementFrame(currentDriver).setVisible(true);
                break;
            case "🎫 Tickets":
                JOptionPane.showMessageDialog(this, 
                    "Ticket Management feature coming soon!", 
                    "Info", 
                    JOptionPane.INFORMATION_MESSAGE);
                break;
            case "🔧 Maintenance":
                // You can create a MaintenanceManagementFrame similar to the others
                showMaintenanceInfo();
                break;
            case "📊 Reports":
                JOptionPane.showMessageDialog(this, 
                    "Reports feature coming soon!", 
                    "Info", 
                    JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }

    private void showDriverProfile() {
        String profileInfo = String.format(
            "Driver Profile:\n\n" +
            "Name: %s\n" +
            "Username: %s\n" +
            "Email: %s\n" +
            "License: %s\n" +
            "Phone: %s\n" +
            "Status: %s\n" +
            "Member since: %s",
            currentDriver.getFullName(),
            currentDriver.getUsername(),
            currentDriver.getEmail(),
            currentDriver.getLicenseNumber(),
            currentDriver.getPhone(),
            currentDriver.getStatus(),
            currentDriver.getCreatedAt().toLocalDate().toString()
        );
        
        JOptionPane.showMessageDialog(this, profileInfo, "Driver Profile", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showMaintenanceInfo() {
        MaintenanceService maintenanceService = new MaintenanceService();
        java.util.List<Maintenance> maintenances = maintenanceService.getMaintenanceByDriverId(currentDriver.getDriverId());
        
        if (maintenances.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No maintenance records found.", "Maintenance", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder sb = new StringBuilder("Maintenance Records:\n\n");
            for (Maintenance m : maintenances) {
                sb.append(String.format("Reference: %s\nType: %s\nStatus: %s\n\n", 
                    m.getMaintenanceReference(), m.getMaintenanceType(), m.getStatus()));
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Maintenance Records", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}