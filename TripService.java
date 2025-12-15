package com.Transport;

import java.util.List;

public class TripService {
    private TripDAO tripDAO;
    
    public TripService() {
        this.tripDAO = new TripDAO();
    }
    
    public List<Trip> getTripsByDriverId(int driverId) {
        return tripDAO.getTripsByDriverId(driverId);
    }
    
    public boolean createTrip(Trip trip) {
        // Generate a unique trip number
        if (trip.getTripNumber() == null || trip.getTripNumber().isEmpty()) {
            trip.setTripNumber("TRIP_" + System.currentTimeMillis());
        }
        return tripDAO.createTrip(trip);
    }
    
    public boolean updateTripStatus(int tripId, String status) {
        return tripDAO.updateTripStatus(tripId, status);
    }
    
    public boolean startTrip(int tripId) {
        return updateTripStatus(tripId, "In Progress");
    }
    
    public boolean completeTrip(int tripId) {
        return updateTripStatus(tripId, "Completed");
    }
}