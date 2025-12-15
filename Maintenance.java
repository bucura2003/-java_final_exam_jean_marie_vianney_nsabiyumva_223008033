package com.Transport;

import java.time.LocalDateTime;

public class Maintenance {
    private int maintenanceId;
    private String maintenanceReference;
    private String maintenanceType;
    private String description;
    private LocalDateTime scheduledDate;
    private LocalDateTime completedDate;
    private Double cost;
    private String status;
    private String remarks;
    
    // Foreign keys
    private Integer vehicleId;
    private Integer driverId;
    private Vehicle vehicle;
    private Driver driver;
    
    // Constructors
    public Maintenance() {
        this.status = "Scheduled";
    }
    
    public Maintenance(String maintenanceReference, String maintenanceType, String description) {
        this();
        this.maintenanceReference = maintenanceReference;
        this.maintenanceType = maintenanceType;
        this.description = description;
    }
    
    // Getters and Setters
    public int getMaintenanceId() { return maintenanceId; }
    public void setMaintenanceId(int maintenanceId) { this.maintenanceId = maintenanceId; }
    
    public String getMaintenanceReference() { return maintenanceReference; }
    public void setMaintenanceReference(String maintenanceReference) { this.maintenanceReference = maintenanceReference; }
    
    public String getMaintenanceType() { return maintenanceType; }
    public void setMaintenanceType(String maintenanceType) { this.maintenanceType = maintenanceType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }
    
    public LocalDateTime getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDateTime completedDate) { this.completedDate = completedDate; }
    
    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    
    public Integer getVehicleId() { return vehicleId; }
    public void setVehicleId(Integer vehicleId) { this.vehicleId = vehicleId; }
    
    public Integer getDriverId() { return driverId; }
    public void setDriverId(Integer driverId) { this.driverId = driverId; }
    
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    
    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }
}