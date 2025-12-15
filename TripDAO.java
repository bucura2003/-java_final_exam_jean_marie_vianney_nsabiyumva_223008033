package com.Transport;

import java.security.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TripDAO {
    
    public List<Trip> getTripsByDriverId(int driverId) {
        String sql = "SELECT * FROM Trip WHERE DriverID = ? ORDER BY StartTime DESC";
        List<Trip> trips = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, driverId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                trips.add(mapResultSetToTrip(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting trips: " + e.getMessage());
            e.printStackTrace();
        }
        return trips;
    }
    
    public boolean createTrip(Trip trip) {
        String sql = "INSERT INTO Trip (TripNumber, StartLocation, EndLocation, StartTime, " +
                    "EndTime, Distance, Duration, Status, TotalFare, PaymentMethod, Notes, DriverID) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {  // No generated keys
            
            setTripParameters(stmt, trip);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating trip: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateTripStatus(int tripId, String status) {
        String sql = "UPDATE Trip SET Status = ? WHERE TripID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, tripId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating trip status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    private void setTripParameters(PreparedStatement stmt, Trip trip) throws SQLException {
        stmt.setString(1, trip.getTripNumber());
        stmt.setString(2, trip.getStartLocation());
        stmt.setString(3, trip.getEndLocation());
        stmt.setTimestamp(4, trip.getStartTime() != null ? Timestamp(trip.getStartTime()) : null);
        stmt.setTimestamp(5, trip.getEndTime() != null ? Timestamp(trip.getEndTime()) : null);
        stmt.setObject(6, trip.getDistance(), Types.DECIMAL);
        stmt.setObject(7, trip.getDuration(), Types.INTEGER);
        stmt.setString(8, trip.getStatus());
        stmt.setObject(9, trip.getTotalFare(), Types.DECIMAL);
        stmt.setString(10, trip.getPaymentMethod());
        stmt.setString(11, trip.getNotes());
        stmt.setInt(12, trip.getDriverId());
    }
    
    private java.sql.Timestamp Timestamp(LocalDateTime startTime) {
		// TODO Auto-generated method stub
		return null;
	}

	private Trip mapResultSetToTrip(ResultSet rs) throws SQLException {
        Trip trip = new Trip();
        trip.setTripId(rs.getInt("TripID"));
        trip.setTripNumber(rs.getString("TripNumber"));
        trip.setStartLocation(rs.getString("StartLocation"));
        trip.setEndLocation(rs.getString("EndLocation"));
        
        java.sql.Timestamp startTime = rs.getTimestamp("StartTime");
        if (startTime != null) {
            trip.setStartTime(startTime.toLocalDateTime());
        }
        
        java.sql.Timestamp endTime = rs.getTimestamp("EndTime");
        if (endTime != null) {
            trip.setEndTime(endTime.toLocalDateTime());
        }
        
        trip.setDistance(rs.getObject("Distance", Double.class));
        trip.setDuration(rs.getObject("Duration", Integer.class));
        trip.setStatus(rs.getString("Status"));
        trip.setTotalFare(rs.getObject("TotalFare", Double.class));
        trip.setPaymentMethod(rs.getString("PaymentMethod"));
        trip.setNotes(rs.getString("Notes"));
        trip.setDriverId(rs.getInt("DriverID"));
        
        return trip;
    }
}