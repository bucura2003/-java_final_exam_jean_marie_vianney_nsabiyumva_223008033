package com.Transport;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Vehicle {
    private int vehicleId;
    private String licensePlate;
    private String make;
    private String model;
    private Integer year;
    private String color;
    private Integer capacity;
    private String status;
    private LocalDateTime lastMaintenanceDate;
    private LocalDateTime assignedSince;
    
    // Foreign key
    private Integer driverId;
    private Driver driver;
    
    // Relationships
    private List<Trip> trips = new ArrayList<>();
    private List<Maintenance> maintenances = new ArrayList<>();
    
    // Constructors
    public Vehicle() {
        this.status = "Available";
    }
    
    public Vehicle(String licensePlate, String make, String model) {
        this();
        this.licensePlate = licensePlate;
        this.make = make;
        this.model = model;
    }
    
    // Getters and Setters
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getLastMaintenanceDate() { return lastMaintenanceDate; }
    public void setLastMaintenanceDate(LocalDateTime lastMaintenanceDate) { this.lastMaintenanceDate = lastMaintenanceDate; }
    
    public LocalDateTime getAssignedSince() { return assignedSince; }
    public void setAssignedSince(LocalDateTime assignedSince) { this.assignedSince = assignedSince; }
    
    public Integer getDriverId() { return driverId; }
    public void setDriverId(Integer driverId) { this.driverId = driverId; }
    
    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }
    
    public List<Trip> getTrips() { return trips; }
    public void setTrips(List<Trip> trips) { this.trips = trips; }
    
    public List<Maintenance> getMaintenances() { return maintenances; }
    public void setMaintenances(List<Maintenance> maintenances) { this.maintenances = maintenances; }
}