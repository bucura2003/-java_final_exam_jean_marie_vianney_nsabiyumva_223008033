package com.transport;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;
import javax.swing.RowFilter;

public class CompleteTransportSystem extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    // All panels
    private JPanel dashboardPanel, driversPanel, vehiclesPanel, tripsPanel;
    private JPanel routesPanel, ticketsPanel, maintenancePanel, reportsPanel, settingsPanel;

    // Data models
    private DefaultTableModel driversTableModel, vehiclesTableModel, tripsTableModel;
    private DefaultTableModel routesTableModel, ticketsTableModel, maintenanceTableModel;

    // Search components
    private Map<String, JTextField> searchFields = new HashMap<>();
    private Map<String, TableRowSorter<?>> tableSorters = new HashMap<>();

    // Real-time components
    private Timer notificationTimer;
    private JLabel notificationBadge;
    private int unreadNotifications = 0;

    public CompleteTransportSystem() {
        // Initialize data models FIRST
        initializeDataModels();
        
        // Then initialize UI components
        initializeUI();
        setupNavigation();
        
        // Then load data and start services
        loadAllDataFromDatabase();
        startNotificationService();
        refreshDashboard();
    }

    private void setupNavigation() {
        // Set initial panel to Dashboard
        showPanel("DASHBOARD");
        
        // Add keyboard shortcuts
        setupKeyboardShortcuts();
        
        // Add window listener for cleanup
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                cleanup();
            }
        });
    }

    private void setupKeyboardShortcuts() {
        // Add Ctrl+number shortcuts for quick navigation
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke("ctrl 1"), "showDashboard");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke("ctrl 2"), "showDrivers");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke("ctrl 3"), "showVehicles");
            
        getRootPane().getActionMap().put("showDashboard", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPanel("DASHBOARD");
            }
        });
        
        getRootPane().getActionMap().put("showDrivers", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPanel("DRIVERS");
            }
        });
        
        getRootPane().getActionMap().put("showVehicles", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPanel("VEHICLES");
            }
        });
    }

    private void cleanup() {
        // Stop notification timer
        if (notificationTimer != null) {
            notificationTimer.cancel();
        }
        
        // Close database connection
        DatabaseConnection.closeConnection();
        
        System.out.println("Application cleanup completed.");
    }

    private void initializeUI() {
        setTitle("🚌 Transport Automation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createSidebar(), BorderLayout.WEST);
        
        cardLayout = new CardLayout();
        JPanel contentPanel = new JPanel(cardLayout);
        
        // Initialize all panels
        dashboardPanel = createDashboardPanel();
        driversPanel = createDriversPanel();
        vehiclesPanel = createVehiclesPanel();
        tripsPanel = createTripsPanel();
        routesPanel = createRoutesPanel();
        ticketsPanel = createTicketsPanel();
        maintenancePanel = createMaintenancePanel();
        reportsPanel = createReportsPanel();
        settingsPanel = createSettingsPanel();
        
        // Add panels to card layout
        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(driversPanel, "DRIVERS");
        contentPanel.add(vehiclesPanel, "VEHICLES");
        contentPanel.add(tripsPanel, "TRIPS");
        contentPanel.add(routesPanel, "ROUTES");
        contentPanel.add(ticketsPanel, "TICKETS");
        contentPanel.add(maintenancePanel, "MAINTENANCE");
        contentPanel.add(reportsPanel, "REPORTS");
        contentPanel.add(settingsPanel, "SETTINGS");
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void initializeDataModels() {
        // Drivers table model
        String[] driverColumns = {"ID", "Full Name", "License Number", "Phone", "Email", "Status"};
        driversTableModel = new DefaultTableModel(driverColumns, 0) {
            @Override 
            public boolean isCellEditable(int row, int column) { 
                return false; 
            }
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };
        
        // Vehicles table model
        String[] vehicleColumns = {"ID", "Name", "Identifier", "Type", "Capacity", "Status", "Location", "Driver"};
        vehiclesTableModel = new DefaultTableModel(vehicleColumns, 0) {
            @Override 
            public boolean isCellEditable(int row, int column) { 
                return false; 
            }
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };
        
        // Trips table model
        String[] tripColumns = {"ID", "Order Number", "Date", "Start Location", "End Location", "Status", "Amount", "Vehicle", "Driver"};
        tripsTableModel = new DefaultTableModel(tripColumns, 0) {
            @Override 
            public boolean isCellEditable(int row, int column) { 
                return false; 
            }
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };
        
        // Routes table model
        String[] routeColumns = {"ID", "Route Name", "Start Point", "End Point", "Distance (km)", "Estimated Time", "Fare/km", "Status"};
        routesTableModel = new DefaultTableModel(routeColumns, 0) {
            @Override 
            public boolean isCellEditable(int row, int column) { 
                return false; 
            }
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };
        
        // Tickets table model
        String[] ticketColumns = {"ID", "Ticket Number", "Passenger Name", "Phone", "Seat", "Price", "Status", "Trip ID"};
        ticketsTableModel = new DefaultTableModel(ticketColumns, 0) {
            @Override 
            public boolean isCellEditable(int row, int column) { 
                return false; 
            }
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };
        
        // Maintenance table model
        String[] maintenanceColumns = {"ID", "Reference", "Description", "Date", "Completion", "Cost", "Status", "Vehicle"};
        maintenanceTableModel = new DefaultTableModel(maintenanceColumns, 0) {
            @Override 
            public boolean isCellEditable(int row, int column) { 
                return false; 
            }
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };
    }

    private void loadAllDataFromDatabase() {
        loadDriversData();
        loadVehiclesData();
        loadTripsData();
        loadRoutesData();
        loadTicketsData();
        loadMaintenanceData();
        loadNotificationsCount();
    }

    private void loadDriversData() {
        try {
            driversTableModel.setRowCount(0);
            ResultSet rs = DatabaseConnection.executeQuery(
                "SELECT driver_id, full_name, license_number, phone, email, status FROM drivers ORDER BY driver_id"
            );
            while (rs.next()) {
                driversTableModel.addRow(new Object[]{
                    rs.getInt("driver_id"),
                    rs.getString("full_name"),
                    rs.getString("license_number"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("status")
                });
            }
            rs.getStatement().close();
        } catch (SQLException e) {
            showError("Error loading drivers: " + e.getMessage());
        }
    }

    private void loadVehiclesData() {
        try {
            vehiclesTableModel.setRowCount(0);
            ResultSet rs = DatabaseConnection.executeQuery(
                "SELECT v.vehicle_id, v.name, v.identifier, v.type, v.capacity, v.status, v.location, d.full_name " +
                "FROM vehicles v LEFT JOIN drivers d ON v.driver_id = d.driver_id ORDER BY v.vehicle_id"
            );
            while (rs.next()) {
                vehiclesTableModel.addRow(new Object[]{
                    rs.getInt("vehicle_id"),
                    rs.getString("name"),
                    rs.getString("identifier"),
                    rs.getString("type"),
                    rs.getInt("capacity"),
                    rs.getString("status"),
                    rs.getString("location"),
                    rs.getString("full_name") != null ? rs.getString("full_name") : "Unassigned"
                });
            }
            rs.getStatement().close();
        } catch (SQLException e) {
            showError("Error loading vehicles: " + e.getMessage());
        }
    }

    private void loadTripsData() {
        try {
            tripsTableModel.setRowCount(0);
            ResultSet rs = DatabaseConnection.executeQuery(
                "SELECT t.trip_id, t.order_number, t.date, t.start_location, t.end_location, t.status, " +
                "t.total_amount, v.name as vehicle_name, d.full_name as driver_name " +
                "FROM trips t " +
                "LEFT JOIN vehicles v ON t.vehicle_id = v.vehicle_id " +
                "LEFT JOIN drivers d ON t.driver_id = d.driver_id " +
                "ORDER BY t.date DESC"
            );
            while (rs.next()) {
                tripsTableModel.addRow(new Object[]{
                    rs.getInt("trip_id"),
                    rs.getString("order_number"),
                    rs.getString("date"),
                    rs.getString("start_location"),
                    rs.getString("end_location"),
                    rs.getString("status"),
                    String.format("$%.2f", rs.getDouble("total_amount")),
                    rs.getString("vehicle_name"),
                    rs.getString("driver_name")
                });
            }
            rs.getStatement().close();
        } catch (SQLException e) {
            showError("Error loading trips: " + e.getMessage());
        }
    }

    private void loadRoutesData() {
        try {
            routesTableModel.setRowCount(0);
            ResultSet rs = DatabaseConnection.executeQuery(
                "SELECT route_id, route_name, start_point, end_point, distance_km, estimated_time_minutes, fare_per_km, status FROM routes ORDER BY route_id"
            );
            while (rs.next()) {
                routesTableModel.addRow(new Object[]{
                    rs.getInt("route_id"),
                    rs.getString("route_name"),
                    rs.getString("start_point"),
                    rs.getString("end_point"),
                    rs.getDouble("distance_km"),
                    rs.getInt("estimated_time_minutes"),
                    String.format("$%.2f", rs.getDouble("fare_per_km")),
                    rs.getString("status")
                });
            }
            rs.getStatement().close();
        } catch (SQLException e) {
            showError("Error loading routes: " + e.getMessage());
        }
    }

    private void loadTicketsData() {
        try {
            ticketsTableModel.setRowCount(0);
            ResultSet rs = DatabaseConnection.executeQuery(
                "SELECT ticket_id, ticket_number, passenger_name, passenger_phone, seat_number, price, status, trip_id FROM tickets ORDER BY ticket_id"
            );
            while (rs.next()) {
                ticketsTableModel.addRow(new Object[]{
                    rs.getInt("ticket_id"),
                    rs.getString("ticket_number"),
                    rs.getString("passenger_name"),
                    rs.getString("passenger_phone"),
                    rs.getString("seat_number"),
                    String.format("$%.2f", rs.getDouble("price")),
                    rs.getString("status"),
                    rs.getInt("trip_id")
                });
            }
            rs.getStatement().close();
        } catch (SQLException e) {
            showError("Error loading tickets: " + e.getMessage());
        }
    }

    private void loadMaintenanceData() {
        try {
            maintenanceTableModel.setRowCount(0);
            ResultSet rs = DatabaseConnection.executeQuery(
                "SELECT m.maintenance_id, m.reference_id, m.description, m.maintenance_date, m.completion_date, m.cost, m.status, v.name " +
                "FROM maintenance m JOIN vehicles v ON m.vehicle_id = v.vehicle_id ORDER BY m.maintenance_date DESC"
            );
            while (rs.next()) {
                maintenanceTableModel.addRow(new Object[]{
                    rs.getInt("maintenance_id"),
                    rs.getString("reference_id"),
                    rs.getString("description"),
                    rs.getString("maintenance_date"),
                    rs.getString("completion_date"),
                    String.format("$%.2f", rs.getDouble("cost")),
                    rs.getString("status"),
                    rs.getString("name")
                });
            }
            rs.getStatement().close();
        } catch (SQLException e) {
            showError("Error loading maintenance: " + e.getMessage());
        }
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(51, 51, 51));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Header with notification badge
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(51, 51, 51));
        
        JLabel logo = new JLabel("🚌 Transport System");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Arial", Font.BOLD, 16));
        
        notificationBadge = new JLabel("0");
        notificationBadge.setForeground(Color.WHITE);
        notificationBadge.setBackground(Color.RED);
        notificationBadge.setOpaque(true);
        notificationBadge.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
        notificationBadge.setVisible(false);
        
        headerPanel.add(logo, BorderLayout.WEST);
        headerPanel.add(notificationBadge, BorderLayout.EAST);
        sidebar.add(headerPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        String[] menuItems = {
            "📊 Dashboard", "👥 Drivers Management", "🚚 Vehicles Management",
            "🛣️ Trips Management", "📍 Routes Management", "🎫 Tickets Management",
            "🔧 Maintenance", "📈 Reports & Analytics", "⚙️ Settings"
        };

        for (String item : menuItems) {
            JButton menuButton = createMenuButton(item);
            sidebar.add(menuButton);
            sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        // System status
        sidebar.add(Box.createVerticalGlue());
        JLabel statusLabel = new JLabel("🟢 System Online");
        statusLabel.setForeground(Color.GREEN);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(statusLabel);

        return sidebar;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 45));
        button.setBackground(new Color(70, 70, 70));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(90, 90, 90));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 70, 70));
            }
        });
        
        button.addActionListener(new MenuButtonListener(text));
        return button;
    }

    private class MenuButtonListener implements ActionListener {
        private String panelName;
        
        public MenuButtonListener(String panelName) {
            this.panelName = panelName;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (panelName) {
                case "📊 Dashboard": showPanel("DASHBOARD"); break;
                case "👥 Drivers Management": showPanel("DRIVERS"); break;
                case "🚚 Vehicles Management": showPanel("VEHICLES"); break;
                case "🛣️ Trips Management": showPanel("TRIPS"); break;
                case "📍 Routes Management": showPanel("ROUTES"); break;
                case "🎫 Tickets Management": showPanel("TICKETS"); break;
                case "🔧 Maintenance": showPanel("MAINTENANCE"); break;
                case "📈 Reports & Analytics": showPanel("REPORTS"); break;
                case "⚙️ Settings": showPanel("SETTINGS"); break;
            }
        }
    }

    private void showPanel(String panelName) {
        cardLayout.show((Container) mainPanel.getComponent(1), panelName);
        if ("DASHBOARD".equals(panelName)) {
            refreshDashboard();
        }
    }

    private void loadNotificationsCount() {
        unreadNotifications = DatabaseConnection.getUnreadNotificationCount();
        updateNotificationBadge();
    }

    private void updateNotificationBadge() {
        SwingUtilities.invokeLater(() -> {
            notificationBadge.setText(String.valueOf(unreadNotifications));
            notificationBadge.setVisible(unreadNotifications > 0);
        });
    }

    // DASHBOARD PANEL
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 245, 245));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel header = new JLabel("Transport System Dashboard");
        header.setFont(new Font("Arial", Font.BOLD, 28));
        
        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> refreshDashboard());
        
        headerPanel.add(header, BorderLayout.WEST);
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Stats cards
        panel.add(createDashboardStats(), BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createDashboardStats() {
        JPanel statsPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        try {
            Map<String, String> stats = getDashboardStatistics();
            
            DashboardStat[] dashboardStats = {
                new DashboardStat("Active Drivers", stats.get("active_drivers"), "👥", new Color(74, 144, 226)),
                new DashboardStat("Available Vehicles", stats.get("available_vehicles"), "🚚", new Color(86, 188, 138)),
                new DashboardStat("Today's Trips", stats.get("today_trips"), "🛣️", new Color(242, 120, 75)),
                new DashboardStat("Pending Tickets", stats.get("pending_tickets"), "🎫", new Color(155, 81, 224)),
                new DashboardStat("Active Routes", stats.get("active_routes"), "📍", new Color(242, 201, 76)),
                new DashboardStat("Maintenance Due", stats.get("maintenance_due"), "🔧", new Color(237, 85, 100)),
                new DashboardStat("Monthly Revenue", stats.get("monthly_revenue"), "💰", new Color(47, 194, 175)),
                new DashboardStat("System Uptime", "99.8%", "🟢", new Color(120, 120, 120))
            };
            
            for (DashboardStat stat : dashboardStats) {
                statsPanel.add(createStatCard(stat));
            }
            
        } catch (SQLException e) {
            showError("Error loading dashboard statistics: " + e.getMessage());
        }
        
        return statsPanel;
    }

    private Map<String, String> getDashboardStatistics() throws SQLException {
        Map<String, String> stats = new HashMap<>();
        
        // Active drivers
        ResultSet rs = DatabaseConnection.executeQuery("SELECT COUNT(*) as count FROM drivers WHERE status = 'Active'");
        stats.put("active_drivers", rs.next() ? String.valueOf(rs.getInt("count")) : "0");
        rs.getStatement().close();
        
        // Available vehicles
        rs = DatabaseConnection.executeQuery("SELECT COUNT(*) as count FROM vehicles WHERE status = 'Available'");
        stats.put("available_vehicles", rs.next() ? String.valueOf(rs.getInt("count")) : "0");
        rs.getStatement().close();
        
        // Today's trips
        rs = DatabaseConnection.executeQuery("SELECT COUNT(*) as count FROM trips WHERE DATE(date) = DATE('now')");
        stats.put("today_trips", rs.next() ? String.valueOf(rs.getInt("count")) : "0");
        rs.getStatement().close();
        
        // Pending tickets
        rs = DatabaseConnection.executeQuery("SELECT COUNT(*) as count FROM tickets WHERE status = 'Pending'");
        stats.put("pending_tickets", rs.next() ? String.valueOf(rs.getInt("count")) : "0");
        rs.getStatement().close();
        
        // Active routes
        rs = DatabaseConnection.executeQuery("SELECT COUNT(*) as count FROM routes WHERE status = 'Active'");
        stats.put("active_routes", rs.next() ? String.valueOf(rs.getInt("count")) : "0");
        rs.getStatement().close();
        
        // Maintenance due
        rs = DatabaseConnection.executeQuery("SELECT COUNT(*) as count FROM maintenance WHERE status = 'Scheduled'");
        stats.put("maintenance_due", rs.next() ? String.valueOf(rs.getInt("count")) : "0");
        rs.getStatement().close();
        
        // Monthly revenue
        rs = DatabaseConnection.executeQuery("SELECT SUM(total_amount) as revenue FROM trips WHERE strftime('%Y-%m', date) = strftime('%Y-%m', 'now')");
        stats.put("monthly_revenue", rs.next() ? String.format("$%.2f", rs.getDouble("revenue")) : "$0.00");
        rs.getStatement().close();
        
        return stats;
    }

    private JPanel createStatCard(DashboardStat stat) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        
        JLabel iconLabel = new JLabel(stat.getIcon());
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        
        JLabel valueLabel = new JLabel(stat.getValue());
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(stat.getColor());
        
        topPanel.add(iconLabel, BorderLayout.WEST);
        topPanel.add(valueLabel, BorderLayout.EAST);
        
        JLabel titleLabel = new JLabel(stat.getTitle());
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(Color.GRAY);
        
        card.add(topPanel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);
        
        return card;
    }

    // DRIVERS MANAGEMENT PANEL
    private JPanel createDriversPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header with buttons
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel header = new JLabel("Drivers Management");
        header.setFont(new Font("Arial", Font.BOLD, 24));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("➕ Add Driver");
        JButton refreshButton = new JButton("🔄 Refresh");
        JButton sampleDataButton = new JButton("📊 Add Sample Data");
        
        addButton.addActionListener(e -> showAddDriverDialog());
        refreshButton.addActionListener(e -> refreshDriversTable());
        sampleDataButton.addActionListener(e -> addSampleData());
        
        buttonPanel.add(sampleDataButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        
        headerPanel.add(header, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Search panel
        panel.add(createSearchPanel("DRIVERS"), BorderLayout.NORTH);
        
        // Drivers table
        JTable driversTable = new JTable(driversTableModel);
        driversTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        driversTable.getTableHeader().setReorderingAllowed(false);
        
        // Add row sorter for search
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(driversTableModel);
        driversTable.setRowSorter(sorter);
        tableSorters.put("DRIVERS", sorter);
        
        JScrollPane scrollPane = new JScrollPane(driversTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Action buttons for selected row
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton editButton = new JButton("✏️ Edit");
        JButton deleteButton = new JButton("🗑️ Delete");
        
        editButton.addActionListener(e -> {
            int selectedRow = driversTable.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = driversTable.convertRowIndexToModel(selectedRow);
                editDriver(modelRow);
            } else {
                showError("Please select a driver to edit.");
            }
        });
        
        deleteButton.addActionListener(e -> {
            int selectedRow = driversTable.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = driversTable.convertRowIndexToModel(selectedRow);
                deleteDriver(modelRow);
            } else {
                showError("Please select a driver to delete.");
            }
        });
        
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createSearchPanel(String module) {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        
        JTextField searchField = new JTextField(20);
        searchFields.put(module, searchField);
        
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { performSearch(module); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { performSearch(module); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { performSearch(module); }
        });
        
        searchPanel.add(searchField);
        return searchPanel;
    }

    private void performSearch(String module) {
        JTextField searchField = searchFields.get(module);
        TableRowSorter<?> sorter = tableSorters.get(module);
        
        if (searchField != null && sorter != null) {
            String text = searchField.getText();
            if (text.trim().length() == 0) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        }
    }

    // VEHICLES MANAGEMENT PANEL
    private JPanel createVehiclesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header with buttons
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel header = new JLabel("Vehicles Management");
        header.setFont(new Font("Arial", Font.BOLD, 24));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("➕ Add Vehicle");
        JButton refreshButton = new JButton("🔄 Refresh");
        
        addButton.addActionListener(e -> showVehicleForm());
        refreshButton.addActionListener(e -> loadVehiclesData());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        
        headerPanel.add(header, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Search panel
        panel.add(createSearchPanel("VEHICLES"), BorderLayout.NORTH);
        
        // Vehicles table
        JTable vehiclesTable = new JTable(vehiclesTableModel);
        vehiclesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(vehiclesTableModel);
        vehiclesTable.setRowSorter(sorter);
        tableSorters.put("VEHICLES", sorter);
        
        JScrollPane scrollPane = new JScrollPane(vehiclesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Action buttons for selected row
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton editButton = new JButton("✏️ Edit");
        JButton deleteButton = new JButton("🗑️ Delete");
        
        editButton.addActionListener(e -> {
            int selectedRow = vehiclesTable.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = vehiclesTable.convertRowIndexToModel(selectedRow);
                editVehicle(modelRow);
            } else {
                showError("Please select a vehicle to edit.");
            }
        });
        
        deleteButton.addActionListener(e -> {
            int selectedRow = vehiclesTable.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = vehiclesTable.convertRowIndexToModel(selectedRow);
                deleteVehicle(modelRow);
            } else {
                showError("Please select a vehicle to delete.");
            }
        });
        
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    // TRIPS MANAGEMENT PANEL
    private JPanel createTripsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header with buttons
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel header = new JLabel("Trips Management");
        header.setFont(new Font("Arial", Font.BOLD, 24));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("➕ Add Trip");
        JButton refreshButton = new JButton("🔄 Refresh");
        
        addButton.addActionListener(e -> showAddTripDialog());
        refreshButton.addActionListener(e -> refreshTripsTable());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        
        headerPanel.add(header, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Search panel
        panel.add(createSearchPanel("TRIPS"), BorderLayout.NORTH);
        
        // Trips table
        JTable tripsTable = new JTable(tripsTableModel);
        tripsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tripsTable.getTableHeader().setReorderingAllowed(false);
        
        // Add row sorter for search
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tripsTableModel);
        tripsTable.setRowSorter(sorter);
        tableSorters.put("TRIPS", sorter);
        
        JScrollPane scrollPane = new JScrollPane(tripsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Action buttons for selected row
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton editButton = new JButton("✏️ Edit");
        JButton deleteButton = new JButton("🗑️ Delete");
        
        editButton.addActionListener(e -> {
            int selectedRow = tripsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = tripsTable.convertRowIndexToModel(selectedRow);
                editTrip(modelRow);
            } else {
                showError("Please select a trip to edit.");
            }
        });
        
        deleteButton.addActionListener(e -> {
            int selectedRow = tripsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = tripsTable.convertRowIndexToModel(selectedRow);
                deleteTrip(modelRow);
            } else {
                showError("Please select a trip to delete.");
            }
        });
        
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    // ROUTES MANAGEMENT PANEL
    private JPanel createRoutesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel header = new JLabel("Routes Management");
        header.setFont(new Font("Arial", Font.BOLD, 24));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("➕ Add Route");
        JButton refreshButton = new JButton("🔄 Refresh");
        
        addButton.addActionListener(e -> showRouteForm());
        refreshButton.addActionListener(e -> loadRoutesData());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        
        headerPanel.add(header, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Search panel
        panel.add(createSearchPanel("ROUTES"), BorderLayout.NORTH);
        
        // Routes table
        JTable routesTable = new JTable(routesTableModel);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(routesTableModel);
        routesTable.setRowSorter(sorter);
        tableSorters.put("ROUTES", sorter);
        
        JScrollPane scrollPane = new JScrollPane(routesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton editButton = new JButton("✏️ Edit");
        JButton deleteButton = new JButton("🗑️ Delete");
        
        editButton.addActionListener(e -> {
            int selectedRow = routesTable.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = routesTable.convertRowIndexToModel(selectedRow);
                editRoute(modelRow);
            }
        });
        
        deleteButton.addActionListener(e -> {
            int selectedRow = routesTable.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = routesTable.convertRowIndexToModel(selectedRow);
                deleteRoute(modelRow);
            }
        });
        
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    // TICKETS MANAGEMENT PANEL
    private JPanel createTicketsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel header = new JLabel("Tickets Management");
        header.setFont(new Font("Arial", Font.BOLD, 24));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("➕ Add Ticket");
        JButton refreshButton = new JButton("🔄 Refresh");
        
        addButton.addActionListener(e -> showTicketForm());
        refreshButton.addActionListener(e -> loadTicketsData());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        
        headerPanel.add(header, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Search panel
        panel.add(createSearchPanel("TICKETS"), BorderLayout.NORTH);
        
        // Tickets table
        JTable ticketsTable = new JTable(ticketsTableModel);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(ticketsTableModel);
        ticketsTable.setRowSorter(sorter);
        tableSorters.put("TICKETS", sorter);
        
        JScrollPane scrollPane = new JScrollPane(ticketsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton editButton = new JButton("✏️ Edit");
        JButton deleteButton = new JButton("🗑️ Delete");
        
        editButton.addActionListener(e -> {
            int selectedRow = ticketsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = ticketsTable.convertRowIndexToModel(selectedRow);
                editTicket(modelRow);
            }
        });
        
        deleteButton.addActionListener(e -> {
            int selectedRow = ticketsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = ticketsTable.convertRowIndexToModel(selectedRow);
                deleteTicket(modelRow);
            }
        });
        
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    // MAINTENANCE MANAGEMENT PANEL
    private JPanel createMaintenancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel header = new JLabel("Maintenance Management");
        header.setFont(new Font("Arial", Font.BOLD, 24));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("➕ Add Maintenance");
        JButton refreshButton = new JButton("🔄 Refresh");
        
        addButton.addActionListener(e -> showMaintenanceForm());
        refreshButton.addActionListener(e -> loadMaintenanceData());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        
        headerPanel.add(header, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Search panel
        panel.add(createSearchPanel("MAINTENANCE"), BorderLayout.NORTH);
        
        // Maintenance table
        JTable maintenanceTable = new JTable(maintenanceTableModel);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(maintenanceTableModel);
        maintenanceTable.setRowSorter(sorter);
        tableSorters.put("MAINTENANCE", sorter);
        
        JScrollPane scrollPane = new JScrollPane(maintenanceTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton editButton = new JButton("✏️ Edit");
        JButton deleteButton = new JButton("🗑️ Delete");
        JButton completeButton = new JButton("✅ Complete");
        
        editButton.addActionListener(e -> {
            int selectedRow = maintenanceTable.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = maintenanceTable.convertRowIndexToModel(selectedRow);
                editMaintenance(modelRow);
            }
        });
        
        deleteButton.addActionListener(e -> {
            int selectedRow = maintenanceTable.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = maintenanceTable.convertRowIndexToModel(selectedRow);
                deleteMaintenance(modelRow);
            }
        });
        
        completeButton.addActionListener(e -> {
            int selectedRow = maintenanceTable.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = maintenanceTable.convertRowIndexToModel(selectedRow);
                completeMaintenance(modelRow);
            }
        });
        
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(completeButton);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    // REPORTS PANEL
    private JPanel createReportsPanel() {
        return new ReportsPanel();
    }

    // SETTINGS PANEL
    private JPanel createSettingsPanel() {
        return new SettingsPanel();
    }

    // REAL-TIME SERVICES
    private void startNotificationService() {
        notificationTimer = new Timer(true);
        notificationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkForNewNotifications();
            }
        }, 0, 30000); // Check every 30 seconds
    }

    private void checkForNewNotifications() {
        int newCount = DatabaseConnection.getUnreadNotificationCount();
        if (newCount != unreadNotifications) {
            unreadNotifications = newCount;
            updateNotificationBadge();
        }
    }

    private void loadRealTimeData() {
        refreshDashboard();
    }

    private void refreshDashboard() {
        loadAllDataFromDatabase();
    }

    // DRIVER OPERATIONS
    private void showAddDriverDialog() {
        DriverFormDialog dialog = new DriverFormDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            refreshDriversTable();
        }
    }

    private void addSampleData() {
        try {
            // Clear existing sample data first to avoid duplicates
            DatabaseConnection.executeUpdate("DELETE FROM tickets WHERE ticket_number LIKE 'TKT-%'");
            DatabaseConnection.executeUpdate("DELETE FROM trips WHERE order_number LIKE 'TRIP-%'");
            DatabaseConnection.executeUpdate("DELETE FROM maintenance WHERE reference_id LIKE 'MAINT-%'");
            DatabaseConnection.executeUpdate("DELETE FROM routes WHERE route_name LIKE '%Sample%'");
            DatabaseConnection.executeUpdate("DELETE FROM vehicles WHERE name LIKE '%Sample%'");
            DatabaseConnection.executeUpdate("DELETE FROM drivers WHERE full_name LIKE '%Sample%'");

            // Insert sample drivers
            DatabaseConnection.executeUpdate(
                "INSERT INTO drivers (full_name, license_number, phone, email, status) VALUES " +
                "('Sample Driver 1', 'SMPL001', '+250788111111', 'driver1@sample.com', 'Active')," +
                "('Sample Driver 2', 'SMPL002', '+250788222222', 'driver2@sample.com', 'Active')"
            );
            
            // Insert sample vehicles
            DatabaseConnection.executeUpdate(
                "INSERT INTO vehicles (name, identifier, type, capacity, status, location) VALUES " +
                "('Sample Bus 101', 'SMPL-BUS-101', 'Bus', 45, 'Available', 'Kigali Depot')," +
                "('Sample Coach 202', 'SMPL-COACH-202', 'Coach', 30, 'Available', 'Huye Station')"
            );
            
            // Insert sample routes
            DatabaseConnection.executeUpdate(
                "INSERT INTO routes (route_name, start_point, end_point, distance_km, estimated_time_minutes, fare_per_km) VALUES " +
                "('Sample Route Kigali-Huye', 'Kigali City', 'Huye District', 120.5, 150, 2.75)," +
                "('Sample Route Kigali-Musanze', 'Kigali City', 'Musanze Town', 105.0, 120, 3.00)"
            );

            JOptionPane.showMessageDialog(this, 
                "Sample data added successfully!\n\n" +
                "Added:\n" +
                "• 2 Sample Drivers\n" +
                "• 2 Sample Vehicles\n" +
                "• 2 Sample Routes",
                "Sample Data Added", JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh all tables
            refreshDriversTable();
            loadVehiclesData();
            loadRoutesData();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error adding sample data: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshDriversTable() {
        loadDriversData();
        JOptionPane.showMessageDialog(this, "Drivers table refreshed!");
    }

    private void editDriver(int modelRow) {
        int driverId = (int) driversTableModel.getValueAt(modelRow, 0);
        DriverFormDialog dialog = new DriverFormDialog(this, driverId);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            refreshDriversTable();
        }
    }

    private void deleteDriver(int modelRow) {
        int driverId = (int) driversTableModel.getValueAt(modelRow, 0);
        String driverName = (String) driversTableModel.getValueAt(modelRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete driver: " + driverName + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM drivers WHERE driver_id = ?";
                java.sql.PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
                pstmt.setInt(1, driverId);
                pstmt.executeUpdate();
                pstmt.close();
                
                DatabaseConnection.addNotification(
                    "Driver Deleted",
                    "Driver " + driverName + " has been removed from the system",
                    "driver",
                    "High"
                );
                
                refreshDriversTable();
                JOptionPane.showMessageDialog(this, "Driver deleted successfully!");
                
            } catch (java.sql.SQLException e) {
                showError("Error deleting driver: " + e.getMessage());
            }
        }
    }

    // VEHICLE OPERATIONS
    private void showVehicleForm() {
        VehicleFormDialog dialog = new VehicleFormDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadVehiclesData();
        }
    }

    private void editVehicle(int modelRow) {
        int vehicleId = (int) vehiclesTableModel.getValueAt(modelRow, 0);
        VehicleFormDialog dialog = new VehicleFormDialog(this, vehicleId);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadVehiclesData();
        }
    }

    private void deleteVehicle(int modelRow) {
        int vehicleId = (int) vehiclesTableModel.getValueAt(modelRow, 0);
        String vehicleName = (String) vehiclesTableModel.getValueAt(modelRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete vehicle: " + vehicleName + "?\n" +
            "This will also delete associated trips and maintenance records!",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // First check if vehicle has associated trips
                ResultSet rs = DatabaseConnection.executeQuery(
                    "SELECT COUNT(*) as trip_count FROM trips WHERE vehicle_id = " + vehicleId
                );
                if (rs.next() && rs.getInt("trip_count") > 0) {
                    int confirm2 = JOptionPane.showConfirmDialog(this,
                        "This vehicle has " + rs.getInt("trip_count") + " associated trips.\n" +
                        "Deleting will remove all trip records. Continue?",
                        "Warning: Associated Data", JOptionPane.YES_NO_OPTION);
                    
                    if (confirm2 != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                rs.getStatement().close();

                String query = "DELETE FROM vehicles WHERE vehicle_id = ?";
                java.sql.PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
                pstmt.setInt(1, vehicleId);
                pstmt.executeUpdate();
                pstmt.close();
                
                DatabaseConnection.addNotification(
                    "Vehicle Deleted",
                    "Vehicle " + vehicleName + " has been removed from the system",
                    "vehicle",
                    "High"
                );
                
                loadVehiclesData();
                JOptionPane.showMessageDialog(this, "Vehicle deleted successfully!");
                
            } catch (java.sql.SQLException e) {
                showError("Error deleting vehicle: " + e.getMessage());
            }
        }
    }

    // TRIP OPERATIONS
    private void showAddTripDialog() {
        TripFormDialog dialog = new TripFormDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadTripsData();
        }
    }

    private void editTrip(int modelRow) {
        int tripId = (int) tripsTableModel.getValueAt(modelRow, 0);
        TripFormDialog dialog = new TripFormDialog(this, tripId);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadTripsData();
        }
    }

    private void deleteTrip(int modelRow) {
        int tripId = (int) tripsTableModel.getValueAt(modelRow, 0);
        String orderNumber = (String) tripsTableModel.getValueAt(modelRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete trip: " + orderNumber + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM trips WHERE trip_id = ?";
                java.sql.PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
                pstmt.setInt(1, tripId);
                pstmt.executeUpdate();
                pstmt.close();
                
                DatabaseConnection.addNotification(
                    "Trip Deleted",
                    "Trip " + orderNumber + " has been deleted",
                    "trip",
                    "High"
                );
                
                loadTripsData();
                JOptionPane.showMessageDialog(this, "Trip deleted successfully!");
                
            } catch (java.sql.SQLException e) {
                showError("Error deleting trip: " + e.getMessage());
            }
        }
    }

    private void refreshTripsTable() {
        loadTripsData();
        JOptionPane.showMessageDialog(this, "Trips table refreshed!");
    }

    // ROUTE OPERATIONS
    private void showRouteForm() {
        RouteFormDialog dialog = new RouteFormDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadRoutesData();
        }
    }

    private void editRoute(int modelRow) {
        int routeId = (int) routesTableModel.getValueAt(modelRow, 0);
        RouteFormDialog dialog = new RouteFormDialog(this, routeId);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadRoutesData();
        }
    }

    private void deleteRoute(int modelRow) {
        int routeId = (int) routesTableModel.getValueAt(modelRow, 0);
        String routeName = (String) routesTableModel.getValueAt(modelRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete route: " + routeName + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM routes WHERE route_id = ?";
                java.sql.PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
                pstmt.setInt(1, routeId);
                pstmt.executeUpdate();
                pstmt.close();
                
                DatabaseConnection.addNotification(
                    "Route Deleted",
                    "Route " + routeName + " has been deleted",
                    "route",
                    "High"
                );
                
                loadRoutesData();
                JOptionPane.showMessageDialog(this, "Route deleted successfully!");
                
            } catch (java.sql.SQLException e) {
                showError("Error deleting route: " + e.getMessage());
            }
        }
    }

    // TICKET OPERATIONS
    private void showTicketForm() {
        TicketFormDialog dialog = new TicketFormDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadTicketsData();
        }
    }

    private void editTicket(int modelRow) {
        int ticketId = (int) ticketsTableModel.getValueAt(modelRow, 0);
        TicketFormDialog dialog = new TicketFormDialog(this, ticketId);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadTicketsData();
        }
    }

    private void deleteTicket(int modelRow) {
        int ticketId = (int) ticketsTableModel.getValueAt(modelRow, 0);
        String ticketNumber = (String) ticketsTableModel.getValueAt(modelRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete ticket: " + ticketNumber + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM tickets WHERE ticket_id = ?";
                java.sql.PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
                pstmt.setInt(1, ticketId);
                pstmt.executeUpdate();
                pstmt.close();
                
                DatabaseConnection.addNotification(
                    "Ticket Deleted",
                    "Ticket " + ticketNumber + " has been deleted",
                    "ticket",
                    "Medium"
                );
                
                loadTicketsData();
                JOptionPane.showMessageDialog(this, "Ticket deleted successfully!");
                
            } catch (java.sql.SQLException e) {
                showError("Error deleting ticket: " + e.getMessage());
            }
        }
    }

    // MAINTENANCE OPERATIONS
    private void showMaintenanceForm() {
        MaintenanceFormDialog dialog = new MaintenanceFormDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadMaintenanceData();
        }
    }

    private void editMaintenance(int modelRow) {
        int maintenanceId = (int) maintenanceTableModel.getValueAt(modelRow, 0);
        MaintenanceFormDialog dialog = new MaintenanceFormDialog(this, maintenanceId);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadMaintenanceData();
        }
    }

    private void deleteMaintenance(int modelRow) {
        int maintenanceId = (int) maintenanceTableModel.getValueAt(modelRow, 0);
        String referenceId = (String) maintenanceTableModel.getValueAt(modelRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete maintenance record: " + referenceId + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM maintenance WHERE maintenance_id = ?";
                java.sql.PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
                pstmt.setInt(1, maintenanceId);
                pstmt.executeUpdate();
                pstmt.close();
                
                DatabaseConnection.addNotification(
                    "Maintenance Deleted",
                    "Maintenance " + referenceId + " has been deleted",
                    "maintenance",
                    "Medium"
                );
                
                loadMaintenanceData();
                JOptionPane.showMessageDialog(this, "Maintenance record deleted successfully!");
                
            } catch (java.sql.SQLException e) {
                showError("Error deleting maintenance record: " + e.getMessage());
            }
        }
    }

    private void completeMaintenance(int modelRow) {
        int maintenanceId = (int) maintenanceTableModel.getValueAt(modelRow, 0);
        String referenceId = (String) maintenanceTableModel.getValueAt(modelRow, 1);
        
        try {
            String query = "UPDATE maintenance SET status = 'Completed', completion_date = datetime('now') WHERE maintenance_id = ?";
            java.sql.PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query);
            pstmt.setInt(1, maintenanceId);
            pstmt.executeUpdate();
            pstmt.close();
            
            DatabaseConnection.addNotification(
                "Maintenance Completed",
                "Maintenance " + referenceId + " has been completed",
                "maintenance",
                "Low"
            );
            
            loadMaintenanceData();
            JOptionPane.showMessageDialog(this, "Maintenance marked as completed!");
            
        } catch (java.sql.SQLException e) {
            showError("Error completing maintenance: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new CompleteTransportSystem().setVisible(true);
        });
    }
}