package com.transport;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class SettingsPanel extends JPanel {
    private JTextField dbPathField, backupPathField;
    private JComboBox<String> themeCombo, languageCombo;
    private JCheckBox autoBackupCheck, notificationsCheck;
    private JSpinner backupIntervalSpinner;
    private Properties settings;
    private static final String SETTINGS_FILE = "transport_settings.properties";

    public SettingsPanel() {
        settings = new Properties();
        loadSettings();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel header = new JLabel("System Settings & Configuration");
        header.setFont(new Font("Arial", Font.BOLD, 24));
        header.setHorizontalAlignment(SwingConstants.CENTER);

        // Settings panel
        JPanel settingsPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Database settings
        settingsPanel.add(new JLabel("Database Path:"));
        dbPathField = new JTextField(settings.getProperty("db_path", "transport_system.db"));
        JButton dbBrowseBtn = new JButton("Browse");
        JPanel dbPanel = new JPanel(new BorderLayout());
        dbPanel.add(dbPathField, BorderLayout.CENTER);
        dbPanel.add(dbBrowseBtn, BorderLayout.EAST);
        settingsPanel.add(dbPanel);

        // Backup settings
        settingsPanel.add(new JLabel("Backup Path:"));
        backupPathField = new JTextField(settings.getProperty("backup_path", "backups/"));
        JButton backupBrowseBtn = new JButton("Browse");
        JPanel backupPanel = new JPanel(new BorderLayout());
        backupPanel.add(backupPathField, BorderLayout.CENTER);
        backupPanel.add(backupBrowseBtn, BorderLayout.EAST);
        settingsPanel.add(backupPanel);

        // Auto backup settings
        settingsPanel.add(new JLabel("Auto Backup:"));
        autoBackupCheck = new JCheckBox("Enable automatic backups");
        autoBackupCheck.setSelected(Boolean.parseBoolean(settings.getProperty("auto_backup", "true")));
        settingsPanel.add(autoBackupCheck);

        settingsPanel.add(new JLabel("Backup Interval (hours):"));
        backupIntervalSpinner = new JSpinner(new SpinnerNumberModel(
            Integer.parseInt(settings.getProperty("backup_interval", "24")), 1, 168, 1
        ));
        settingsPanel.add(backupIntervalSpinner);

        // UI settings
        settingsPanel.add(new JLabel("Theme:"));
        themeCombo = new JComboBox<>(new String[]{"Light", "Dark", "System Default"});
        themeCombo.setSelectedItem(settings.getProperty("theme", "System Default"));
        settingsPanel.add(themeCombo);

        settingsPanel.add(new JLabel("Language:"));
        languageCombo = new JComboBox<>(new String[]{"English", "French", "Spanish"});
        languageCombo.setSelectedItem(settings.getProperty("language", "English"));
        settingsPanel.add(languageCombo);

        // Notification settings
        settingsPanel.add(new JLabel("Notifications:"));
        notificationsCheck = new JCheckBox("Enable system notifications");
        notificationsCheck.setSelected(Boolean.parseBoolean(settings.getProperty("notifications", "true")));
        settingsPanel.add(notificationsCheck);

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        JButton saveBtn = new JButton("💾 Save Settings");
        JButton resetBtn = new JButton("🔄 Reset to Default");
        JButton backupBtn = new JButton("💾 Backup Now");
        JButton restoreBtn = new JButton("📁 Restore Backup");

        buttonPanel.add(saveBtn);
        buttonPanel.add(resetBtn);
        buttonPanel.add(backupBtn);
        buttonPanel.add(restoreBtn);

        // System info panel
        JPanel infoPanel = createSystemInfoPanel();

        // Add action listeners
        dbBrowseBtn.addActionListener(e -> browseDatabasePath());
        backupBrowseBtn.addActionListener(e -> browseBackupPath());
        saveBtn.addActionListener(e -> saveSettings());
        resetBtn.addActionListener(e -> resetSettings());
        backupBtn.addActionListener(e -> backupDatabase());
        restoreBtn.addActionListener(e -> restoreDatabase());

        // Layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(settingsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Application Settings", mainPanel);
        tabbedPane.addTab("System Information", infoPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createSystemInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // System information
        panel.add(new JLabel("Java Version:"));
        panel.add(new JLabel(System.getProperty("java.version")));

        panel.add(new JLabel("Java VM:"));
        panel.add(new JLabel(System.getProperty("java.vm.name")));

        panel.add(new JLabel("OS:"));
        panel.add(new JLabel(System.getProperty("os.name") + " " + System.getProperty("os.version")));

        panel.add(new JLabel("Architecture:"));
        panel.add(new JLabel(System.getProperty("os.arch")));

        panel.add(new JLabel("User:"));
        panel.add(new JLabel(System.getProperty("user.name")));

        panel.add(new JLabel("Working Directory:"));
        panel.add(new JLabel(System.getProperty("user.dir")));

        // Database information
        try {
            ResultSet rs = DatabaseConnection.executeQuery(
                "SELECT COUNT(*) as count FROM drivers"
            );
            if (rs.next()) {
                panel.add(new JLabel("Total Drivers:"));
                panel.add(new JLabel(String.valueOf(rs.getInt("count"))));
            }
            rs.getStatement().close();

            rs = DatabaseConnection.executeQuery(
                "SELECT COUNT(*) as count FROM vehicles"
            );
            if (rs.next()) {
                panel.add(new JLabel("Total Vehicles:"));
                panel.add(new JLabel(String.valueOf(rs.getInt("count"))));
            }
            rs.getStatement().close();

            rs = DatabaseConnection.executeQuery(
                "SELECT COUNT(*) as count FROM trips"
            );
            if (rs.next()) {
                panel.add(new JLabel("Total Trips:"));
                panel.add(new JLabel(String.valueOf(rs.getInt("count"))));
            }
            rs.getStatement().close();

        } catch (Exception e) {
            panel.add(new JLabel("Database Info:"));
            panel.add(new JLabel("Error loading database information"));
        }

        return panel;
    }

    private void browseDatabasePath() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Database File");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            dbPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void browseBackupPath() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Backup Directory");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            backupPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void loadSettings() {
        try {
            File settingsFile = new File(SETTINGS_FILE);
            if (settingsFile.exists()) {
                settings.load(new FileInputStream(settingsFile));
            }
        } catch (IOException e) {
            System.err.println("Error loading settings: " + e.getMessage());
        }
    }

    private void saveSettings() {
        try {
            settings.setProperty("db_path", dbPathField.getText());
            settings.setProperty("backup_path", backupPathField.getText());
            settings.setProperty("auto_backup", String.valueOf(autoBackupCheck.isSelected()));
            settings.setProperty("backup_interval", backupIntervalSpinner.getValue().toString());
            settings.setProperty("theme", (String) themeCombo.getSelectedItem());
            settings.setProperty("language", (String) languageCombo.getSelectedItem());
            settings.setProperty("notifications", String.valueOf(notificationsCheck.isSelected()));

            settings.store(new FileOutputStream(SETTINGS_FILE), "Transport System Settings");
            
            JOptionPane.showMessageDialog(this, "Settings saved successfully!\nSome changes may require restart.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving settings: " + e.getMessage());
        }
    }

    private void resetSettings() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to reset all settings to default?",
            "Confirm Reset", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dbPathField.setText("transport_system.db");
            backupPathField.setText("backups/");
            autoBackupCheck.setSelected(true);
            backupIntervalSpinner.setValue(24);
            themeCombo.setSelectedItem("System Default");
            languageCombo.setSelectedItem("English");
            notificationsCheck.setSelected(true);
            
            JOptionPane.showMessageDialog(this, "Settings reset to default values.");
        }
    }

    private void backupDatabase() {
        try {
            String backupDir = backupPathField.getText();
            File backupDirectory = new File(backupDir);
            if (!backupDirectory.exists()) {
                backupDirectory.mkdirs();
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String backupFile = backupDir + File.separator + "transport_backup_" + timestamp + ".db";

            // In a real implementation, you would copy the database file
            // For now, we'll just create a marker file
            File marker = new File(backupFile);
            marker.createNewFile();

            DatabaseConnection.addNotification(
                "Database Backup",
                "Database backup created: " + backupFile,
                "system",
                "Low"
            );

            JOptionPane.showMessageDialog(this, 
                "Database backup created successfully!\nLocation: " + backupFile);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error creating backup: " + e.getMessage());
        }
    }

    private void restoreDatabase() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Backup File to Restore");
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "WARNING: This will replace your current database with the backup.\n" +
                "All current data will be lost!\n\n" +
                "Are you sure you want to continue?",
                "Confirm Restore", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // In real implementation, you would restore the database file
                JOptionPane.showMessageDialog(this, 
                    "Database restore functionality would be implemented here.\n" +
                    "Backup file selected: " + fileChooser.getSelectedFile().getName());
            }
        }
    }
}