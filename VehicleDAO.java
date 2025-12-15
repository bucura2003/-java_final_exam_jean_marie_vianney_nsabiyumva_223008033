package com.Transport;

import com.Transport.Vehicle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {
    
    // Create a new vehicle
    public boolean createVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (license_plate, make, model, year, color, capacity, status, " +
                    "last_maintenance_date, assigned_since, driver_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setVehicleParameters(stmt, vehicle);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        vehicle.setVehicleId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating vehicle: " + e.getMessage());
        }
        return false;
    }
    
    // Get vehicle by ID
    public Vehicle getVehicleById(int vehicleId) {
        String sql = "SELECT * FROM vehicles WHERE vehicle_id = ?";
        Vehicle vehicle = null;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, vehicleId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                vehicle = mapResultSetToVehicle(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting vehicle: " + e.getMessage());
        }
        return vehicle;
    }
    
    // Get available vehicles
    public List<Vehicle> getAvailableVehicles() {
        String sql = "SELECT * FROM vehicles WHERE status = 'Available'";
        List<Vehicle> vehicles = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting available vehicles: " + e.getMessage());
        }
        return vehicles;
    }
    
    // Update vehicle
    public boolean updateVehicle(Vehicle vehicle) {
        String sql = "UPDATE vehicles SET license_plate = ?, make = ?, model = ?, year = ?, color = ?, " +
                    "capacity = ?, status = ?, last_maintenance_date = ?, assigned_since = ?, driver_id = ? " +
                    "WHERE vehicle_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setVehicleParameters(stmt, vehicle);
            stmt.setInt(11, vehicle.getVehicleId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating vehicle: " + e.getMessage());
        }
        return false;
    }
    
    // Helper method to set vehicle parameters
    private void setVehicleParameters(PreparedStatement stmt, Vehicle vehicle) throws SQLException {
        stmt.setString(1, vehicle.getLicensePlate());
        stmt.setString(2, vehicle.getMake());
        stmt.setString(3, vehicle.getModel());
        stmt.setObject(4, vehicle.getYear(), Types.INTEGER);
        stmt.setString(5, vehicle.getColor());
        stmt.setObject(6, vehicle.getCapacity(), Types.INTEGER);
        stmt.setString(7, vehicle.getStatus());
        stmt.setTimestamp(8, vehicle.getLastMaintenanceDate() != null ? 
            Timestamp.valueOf(vehicle.getLastMaintenanceDate()) : null);
        stmt.setTimestamp(9, vehicle.getAssignedSince() != null ? 
            Timestamp.valueOf(vehicle.getAssignedSince()) : null);
        stmt.setObject(10, vehicle.getDriverId(), Types.INTEGER);
    }
    
    // Helper method to map ResultSet to Vehicle object
    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId(rs.getInt("vehicle_id"));
        vehicle.setLicensePlate(rs.getString("license_plate"));
        vehicle.setMake(rs.getString("make"));
        vehicle.setModel(rs.getString("model"));
        vehicle.setYear(rs.getObject("year", Integer.class));
        vehicle.setColor(rs.getString("color"));
        vehicle.setCapacity(rs.getObject("capacity", Integer.class));
        vehicle.setStatus(rs.getString("status"));
        
        Timestamp lastMaintenance = rs.getTimestamp("last_maintenance_date");
        if (lastMaintenance != null) {
            vehicle.setLastMaintenanceDate(lastMaintenance.toLocalDateTime());
        }
        
        Timestamp assignedSince = rs.getTimestamp("assigned_since");
        if (assignedSince != null) {
            vehicle.setAssignedSince(assignedSince.toLocalDateTime());
        }
        
        vehicle.setDriverId(rs.getObject("driver_id", Integer.class));
        return vehicle;
    }
}