package com.Transport;

import java.time.LocalDateTime;

public class Ticket {
    private int ticketId;
    private String ticketNumber;
    private String passengerName;
    private String passengerPhone;
    private String pickupLocation;
    private String dropoffLocation;
    private String ticketType;
    private Double price;
    private String status;
    private LocalDateTime createdAt;
    
    // Foreign key
    private Integer driverId;
    private Driver driver;
    
    // Constructors
    public Ticket() {
        this.status = "Active";
        this.createdAt = LocalDateTime.now();
    }
    
    public Ticket(String ticketNumber, String passengerName, String pickupLocation, String dropoffLocation) {
        this();
        this.ticketNumber = ticketNumber;
        this.passengerName = passengerName;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
    }
    
    // Getters and Setters
    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }
    
    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
    
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    
    public String getPassengerPhone() { return passengerPhone; }
    public void setPassengerPhone(String passengerPhone) { this.passengerPhone = passengerPhone; }
    
    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    
    public String getDropoffLocation() { return dropoffLocation; }
    public void setDropoffLocation(String dropoffLocation) { this.dropoffLocation = dropoffLocation; }
    
    public String getTicketType() { return ticketType; }
    public void setTicketType(String ticketType) { this.ticketType = ticketType; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public Integer getDriverId() { return driverId; }
    public void setDriverId(Integer driverId) { this.driverId = driverId; }
    
    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }
}