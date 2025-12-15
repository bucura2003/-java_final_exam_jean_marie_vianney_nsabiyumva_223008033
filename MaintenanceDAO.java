package com.Transport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceDAO {
    
    public List<Maintenance> getMaintenanceByDriverId(int driverId) {
        String sql = "SELECT m.*, v.license_plate FROM maintenance m " +
                    "LEFT JOIN vehicles v ON m.vehicle_id = v.vehicle_id " +
                    "WHERE m.driver_id = ? ORDER BY m.scheduled_date DESC";
        List<Maintenance> maintenances = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, driverId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                maintenances.add(mapResultSetToMaintenance(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting maintenance records: " + e.getMessage());
        }
        return maintenances;
    }
    
    private Maintenance mapResultSetToMaintenance(ResultSet rs) throws SQLException {
        Maintenance maintenance = new Maintenance();
        maintenance.setMaintenanceId(rs.getInt("maintenance_id"));
        maintenance.setMaintenanceReference(rs.getString("maintenance_reference"));
        maintenance.setMaintenanceType(rs.getString("maintenance_type"));
        maintenance.setDescription(rs.getString("description"));
        
        Timestamp scheduledDate = rs.getTimestamp("scheduled_date");
        if (scheduledDate != null) {
            maintenance.setScheduledDate(scheduledDate.toLocalDateTime());
        }
        
        Timestamp completedDate = rs.getTimestamp("completed_date");
        if (completedDate != null) {
            maintenance.setCompletedDate(completedDate.toLocalDateTime());
        }
        
        maintenance.setCost(rs.getObject("cost", Double.class));
        maintenance.setStatus(rs.getString("status"));
        maintenance.setRemarks(rs.getString("remarks"));
        maintenance.setVehicleId(rs.getObject("vehicle_id", Integer.class));
        maintenance.setDriverId(rs.getObject("driver_id", Integer.class));
        
        return maintenance;
    }
}
