package com.transport;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReportsPanel extends JPanel {
    private JComboBox<String> reportTypeCombo;
    private JComboBox<String> timeRangeCombo;
    private JButton generateBtn, exportBtn, printBtn;
    private JTextArea reportArea;
    private JTable reportTable;
    private DefaultTableModel reportTableModel;

    public ReportsPanel() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel header = new JLabel("Reports & Analytics");
        header.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(header, BorderLayout.WEST);

        // Controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        controlsPanel.add(new JLabel("Report Type:"));
        reportTypeCombo = new JComboBox<>(new String[]{
            "Revenue Report", "Trips Summary", "Vehicle Utilization", 
            "Driver Performance", "Maintenance History", "Ticket Sales"
        });
        controlsPanel.add(reportTypeCombo);

        controlsPanel.add(new JLabel("Time Range:"));
        timeRangeCombo = new JComboBox<>(new String[]{
            "Today", "This Week", "This Month", "Last Month", "This Year", "Custom"
        });
        controlsPanel.add(timeRangeCombo);

        generateBtn = new JButton("📊 Generate Report");
        exportBtn = new JButton("💾 Export CSV");
        printBtn = new JButton("🖨️ Print");

        controlsPanel.add(generateBtn);
        controlsPanel.add(exportBtn);
        controlsPanel.add(printBtn);

        // Report table
        String[] columns = {"Category", "Value", "Percentage", "Trend"};
        reportTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reportTable = new JTable(reportTableModel);
        JScrollPane tableScroll = new JScrollPane(reportTable);

        // Report area for detailed text
        reportArea = new JTextArea(10, 50);
        reportArea.setEditable(false);
        JScrollPane textScroll = new JScrollPane(reportArea);

        // Charts panel (placeholder for actual charts)
        JPanel chartsPanel = createChartsPanel();

        // Add action listeners
        generateBtn.addActionListener(e -> generateReport());
        exportBtn.addActionListener(e -> exportToCSV());
        printBtn.addActionListener(e -> printReport());

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(controlsPanel, BorderLayout.CENTER);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Summary Table", tableScroll);
        tabbedPane.addTab("Detailed Report", textScroll);
        tabbedPane.addTab("Charts", chartsPanel);

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createChartsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Chart placeholders (in real implementation, use JFreeChart or similar)
        panel.add(createChartPanel("Revenue Trend", "📈"));
        panel.add(createChartPanel("Vehicle Utilization", "🚗"));
        panel.add(createChartPanel("Trip Distribution", "🛣️"));
        panel.add(createChartPanel("Maintenance Status", "🔧"));

        return panel;
    }

    private JPanel createChartPanel(String title, String icon) {
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(title),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        chartPanel.setBackground(Color.WHITE);

        JLabel chartLabel = new JLabel(icon, SwingConstants.CENTER);
        chartLabel.setFont(new Font("Arial", Font.PLAIN, 48));
        chartLabel.setOpaque(true);
        chartLabel.setBackground(Color.LIGHT_GRAY);
        chartLabel.setPreferredSize(new Dimension(200, 150));

        JLabel descLabel = new JLabel("Chart visualization for " + title, SwingConstants.CENTER);
        
        chartPanel.add(chartLabel, BorderLayout.CENTER);
        chartPanel.add(descLabel, BorderLayout.SOUTH);

        return chartPanel;
    }

    private void generateReport() {
        String reportType = (String) reportTypeCombo.getSelectedItem();
        String timeRange = (String) timeRangeCombo.getSelectedItem();
        
        reportTableModel.setRowCount(0);
        reportArea.setText("");

        try {
            switch (reportType) {
                case "Revenue Report":
                    generateRevenueReport(timeRange);
                    break;
                case "Trips Summary":
                    generateTripsReport(timeRange);
                    break;
                case "Vehicle Utilization":
                    generateVehicleReport(timeRange);
                    break;
                case "Driver Performance":
                    generateDriverReport(timeRange);
                    break;
                case "Maintenance History":
                    generateMaintenanceReport(timeRange);
                    break;
                case "Ticket Sales":
                    generateTicketReport(timeRange);
                    break;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage());
        }
    }

    private void generateRevenueReport(String timeRange) throws SQLException {
        String sql = "SELECT strftime('%Y-%m', date) as month, SUM(total_amount) as revenue " +
                    "FROM trips WHERE date >= date('now', '-6 months') " +
                    "GROUP BY strftime('%Y-%m', date) ORDER BY month";
        
        ResultSet rs = DatabaseConnection.executeQuery(sql);
        double totalRevenue = 0;
        
        reportArea.append("=== REVENUE REPORT ===\n");
        reportArea.append("Generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()) + "\n\n");
        
        while (rs.next()) {
            String month = rs.getString("month");
            double revenue = rs.getDouble("revenue");
            totalRevenue += revenue;
            
            reportTableModel.addRow(new Object[]{
                month, 
                String.format("$%.2f", revenue),
                "100%",
                "↗️"
            });
            
            reportArea.append(String.format("%s: $%.2f\n", month, revenue));
        }
        
        reportArea.append("\nTotal Revenue (6 months): $" + String.format("%.2f", totalRevenue) + "\n");
        rs.getStatement().close();
    }

    private void generateTripsReport(String timeRange) throws SQLException {
        String sql = "SELECT status, COUNT(*) as count FROM trips GROUP BY status";
        
        ResultSet rs = DatabaseConnection.executeQuery(sql);
        int totalTrips = 0;
        
        reportArea.append("=== TRIPS SUMMARY REPORT ===\n\n");
        
        while (rs.next()) {
            String status = rs.getString("status");
            int count = rs.getInt("count");
            totalTrips += count;
            
            double percentage = totalTrips > 0 ? (count * 100.0 / totalTrips) : 0;
            reportTableModel.addRow(new Object[]{
                status,
                count + " trips",
                String.format("%.1f%%", percentage),
                "📊"
            });
            
            reportArea.append(String.format("%s: %d trips\n", status, count));
        }
        
        reportArea.append("\nTotal Trips: " + totalTrips + "\n");
        rs.getStatement().close();
    }

    private void generateVehicleReport(String timeRange) throws SQLException {
        String sql = "SELECT v.name, v.status, COUNT(t.trip_id) as trip_count " +
                    "FROM vehicles v LEFT JOIN trips t ON v.vehicle_id = t.vehicle_id " +
                    "GROUP BY v.vehicle_id, v.name, v.status";
        
        ResultSet rs = DatabaseConnection.executeQuery(sql);
        
        reportArea.append("=== VEHICLE UTILIZATION REPORT ===\n\n");
        
        while (rs.next()) {
            String vehicle = rs.getString("name");
            String status = rs.getString("status");
            int tripCount = rs.getInt("trip_count");
            
            reportTableModel.addRow(new Object[]{
                vehicle,
                status + " (" + tripCount + " trips)",
                "N/A",
                getTrendIcon(tripCount)
            });
            
            reportArea.append(String.format("%s: %s - %d trips\n", vehicle, status, tripCount));
        }
        
        rs.getStatement().close();
    }

    private void generateDriverReport(String timeRange) throws SQLException {
        String sql = "SELECT d.full_name, d.status, COUNT(t.trip_id) as trip_count, " +
                    "COALESCE(SUM(t.total_amount), 0) as total_revenue " +
                    "FROM drivers d LEFT JOIN trips t ON d.driver_id = t.driver_id " +
                    "GROUP BY d.driver_id, d.full_name, d.status " +
                    "ORDER BY total_revenue DESC";
        
        ResultSet rs = DatabaseConnection.executeQuery(sql);
        
        reportArea.append("=== DRIVER PERFORMANCE REPORT ===\n\n");
        
        while (rs.next()) {
            String driver = rs.getString("full_name");
            String status = rs.getString("status");
            int tripCount = rs.getInt("trip_count");
            double revenue = rs.getDouble("total_revenue");
            
            reportTableModel.addRow(new Object[]{
                driver,
                String.format("%d trips, $%.2f", tripCount, revenue),
                status,
                getTrendIcon(tripCount)
            });
            
            reportArea.append(String.format("%s: %d trips, $%.2f revenue\n", driver, tripCount, revenue));
        }
        
        rs.getStatement().close();
    }

    private void generateMaintenanceReport(String timeRange) throws SQLException {
        String sql = "SELECT m.reference_id, m.description, m.status, m.cost, v.name as vehicle_name " +
                    "FROM maintenance m JOIN vehicles v ON m.vehicle_id = v.vehicle_id " +
                    "ORDER BY m.maintenance_date DESC LIMIT 10";
        
        ResultSet rs = DatabaseConnection.executeQuery(sql);
        
        reportArea.append("=== MAINTENANCE HISTORY REPORT ===\n\n");
        
        while (rs.next()) {
            String reference = rs.getString("reference_id");
            String description = rs.getString("description");
            String status = rs.getString("status");
            double cost = rs.getDouble("cost");
            String vehicle = rs.getString("vehicle_name");
            
            reportTableModel.addRow(new Object[]{
                reference,
                description + " (" + vehicle + ")",
                String.format("$%.2f - %s", cost, status),
                getMaintenanceIcon(status)
            });
            
            reportArea.append(String.format("%s: %s - $%.2f - %s\n", reference, description, cost, status));
        }
        
        rs.getStatement().close();
    }

    private void generateTicketReport(String timeRange) throws SQLException {
        String sql = "SELECT status, COUNT(*) as count, SUM(price) as total_revenue " +
                    "FROM tickets GROUP BY status";
        
        ResultSet rs = DatabaseConnection.executeQuery(sql);
        double totalRevenue = 0;
        int totalTickets = 0;
        
        reportArea.append("=== TICKET SALES REPORT ===\n\n");
        
        while (rs.next()) {
            String status = rs.getString("status");
            int count = rs.getInt("count");
            double revenue = rs.getDouble("total_revenue");
            totalRevenue += revenue;
            totalTickets += count;
            
            double percentage = totalTickets > 0 ? (count * 100.0 / totalTickets) : 0;
            reportTableModel.addRow(new Object[]{
                status,
                count + " tickets",
                String.format("$%.2f (%.1f%%)", revenue, percentage),
                "🎫"
            });
            
            reportArea.append(String.format("%s: %d tickets, $%.2f revenue\n", status, count, revenue));
        }
        
        reportArea.append(String.format("\nTotal: %d tickets, $%.2f revenue\n", totalTickets, totalRevenue));
        rs.getStatement().close();
    }

    private String getTrendIcon(int value) {
        if (value > 10) return "🚀";
        if (value > 5) return "↗️";
        if (value > 0) return "➡️";
        return "↘️";
    }

    private String getMaintenanceIcon(String status) {
        switch (status) {
            case "Completed": return "✅";
            case "In Progress": return "🔄";
            case "Scheduled": return "📅";
            default: return "❌";
        }
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Report as CSV");
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile() + ".csv")) {
                // Write headers
                for (int i = 0; i < reportTableModel.getColumnCount(); i++) {
                    writer.write(reportTableModel.getColumnName(i));
                    if (i < reportTableModel.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }
                writer.write("\n");
                
                // Write data
                for (int row = 0; row < reportTableModel.getRowCount(); row++) {
                    for (int col = 0; col < reportTableModel.getColumnCount(); col++) {
                        Object value = reportTableModel.getValueAt(row, col);
                        writer.write(value != null ? value.toString().replace(",", ";") : "");
                        if (col < reportTableModel.getColumnCount() - 1) {
                            writer.write(",");
                        }
                    }
                    writer.write("\n");
                }
                
                JOptionPane.showMessageDialog(this, "Report exported successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting report: " + e.getMessage());
            }
        }
    }

    private void printReport() {
        try {
            reportTable.print();
            JOptionPane.showMessageDialog(this, "Report sent to printer!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error printing report: " + e.getMessage());
        }
    }
}