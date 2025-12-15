package com.transport;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
            
            // Initialize database connection
            DatabaseConnection.getConnection();
            
            // Show main application
            SwingUtilities.invokeLater(() -> {
                CompleteTransportSystem system = new CompleteTransportSystem();
                system.setVisible(true);
                
                // Show welcome message
                JOptionPane.showMessageDialog(system, 
                    "🚌 Transport Automation System\n\n" +
                    "Welcome to the Transport Automation System!\n\n" +
                    "To get started:\n" +
                    "1. Use the sidebar to navigate\n" +
                    "2. Click 'Add Sample Data' in Drivers panel\n" +
                    "3. Start managing your transport operations",
                    "Welcome", JOptionPane.INFORMATION_MESSAGE);
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Failed to start application: " + e.getMessage() + "\n\n" +
                "Please ensure:\n" +
                "• SQLite JDBC driver is available\n" +
                "• Java version is 8 or higher\n" +
                "• Write permissions in current directory",
                "Startup Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}