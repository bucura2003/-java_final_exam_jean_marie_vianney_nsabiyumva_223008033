package com.Transport;

public class TestDataGenerator {
    public static void main(String[] args) {
        // Add sample vehicles
        VehicleService vehicleService = new VehicleService();
        
        Vehicle v1 = new Vehicle("ABC123", "Toyota", "Corolla");
        v1.setYear(2020);
        v1.setColor("White");
        v1.setCapacity(4);
        vehicleService.addVehicle(v1);
        
        Vehicle v2 = new Vehicle("XYZ789", "Honda", "CR-V");
        v2.setYear(2021);
        v2.setColor("Black");
        v2.setCapacity(5);
        vehicleService.addVehicle(v2);
        
        // Add sample trips
        TripService tripService = new TripService();
        
        Trip t1 = new Trip("TRIP001", "Downtown", "Airport");
        t1.setDriverId(1); // Assuming driver ID 1 exists
        t1.setDistance(25.5);
        t1.setTotalFare(35.00);
        tripService.createTrip(t1);
        
        Trip t2 = new Trip("TRIP002", "City Center", "Shopping Mall");
        t2.setDriverId(1);
        t2.setDistance(8.2);
        t2.setTotalFare(12.50);
        t2.setStatus("In Progress");
        tripService.createTrip(t2);
        
        System.out.println("Sample data added successfully!");
    }
}