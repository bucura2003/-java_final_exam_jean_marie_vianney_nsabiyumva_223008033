package com.Transport;

import java.util.List;

public class MaintenanceService {
    private MaintenanceDAO maintenanceDAO;
    
    public MaintenanceService() {
        this.maintenanceDAO = new MaintenanceDAO();
    }
    
    public List<Maintenance> getMaintenanceByDriverId(int driverId) {
        return maintenanceDAO.getMaintenanceByDriverId(driverId);
    }
}