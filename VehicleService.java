package com.Transport;

import com.Transport.VehicleDAO;
import com.Transport.Vehicle;
import java.util.List;

public class VehicleService {
    private VehicleDAO vehicleDAO;
    
    public VehicleService() {
        this.vehicleDAO = new VehicleDAO();
    }
    
    // Add new vehicle
    public boolean addVehicle(Vehicle vehicle) {
        return vehicleDAO.createVehicle(vehicle);
    }
    
    // Get vehicle by ID
    public Vehicle getVehicleById(int vehicleId) {
        return vehicleDAO.getVehicleById(vehicleId);
    }
    
    // Get available vehicles
    public List<Vehicle> getAvailableVehicles() {
        return vehicleDAO.getAvailableVehicles();
    }
    
    // Update vehicle
    public boolean updateVehicle(Vehicle vehicle) {
        return vehicleDAO.updateVehicle(vehicle);
    }
    
    // Assign vehicle to driver
    public boolean assignVehicleToDriver(int vehicleId, int driverId) {
        Vehicle vehicle = vehicleDAO.getVehicleById(vehicleId);
        if (vehicle != null) {
            vehicle.setDriverId(driverId);
            vehicle.setAssignedSince(java.time.LocalDateTime.now());
            vehicle.setStatus("Assigned");
            return vehicleDAO.updateVehicle(vehicle);
        }
        return false;
    }
    
    // Mark vehicle for maintenance
    public boolean markVehicleForMaintenance(int vehicleId) {
        Vehicle vehicle = vehicleDAO.getVehicleById(vehicleId);
        if (vehicle != null) {
            vehicle.setStatus("Under Maintenance");
            vehicle.setLastMaintenanceDate(java.time.LocalDateTime.now());
            return vehicleDAO.updateVehicle(vehicle);
        }
        return false;
    }
}