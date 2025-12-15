package com.Transport;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Route {
    private int routeId;
    private String routeName;
    private String startPoint;
    private String endPoint;
    private Double totalDistance;
    private Integer estimatedDuration;
    private String routeDescription;
    private LocalDateTime createdAt;
    
    // Foreign key
    private Integer driverId;
    private Driver driver;
    
    // Relationships
    private List<Ticket> tickets = new ArrayList<>();
    
    // Constructors
    public Route() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Route(String routeName, String startPoint, String endPoint) {
        this();
        this.routeName = routeName;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }
    
    // Getters and Setters
    public int getRouteId() { return routeId; }
    public void setRouteId(int routeId) { this.routeId = routeId; }
    
    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }
    
    public String getStartPoint() { return startPoint; }
    public void setStartPoint(String startPoint) { this.startPoint = startPoint; }
    
    public String getEndPoint() { return endPoint; }
    public void setEndPoint(String endPoint) { this.endPoint = endPoint; }
    
    public Double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(Double totalDistance) { this.totalDistance = totalDistance; }
    
    public Integer getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(Integer estimatedDuration) { this.estimatedDuration = estimatedDuration; }
    
    public String getRouteDescription() { return routeDescription; }
    public void setRouteDescription(String routeDescription) { this.routeDescription = routeDescription; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public Integer getDriverId() { return driverId; }
    public void setDriverId(Integer driverId) { this.driverId = driverId; }
    
    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }
    
    public List<Ticket> getTickets() { return tickets; }
    public void setTickets(List<Ticket> tickets) { this.tickets = tickets; }
}