package com.theeztech.communityservicedashboard;

public class Transport {
    private String id;
    private String vehicleType;
    private String vehicleNumber;
    private String driverName;
    private String contactNumber;

    public Transport(String id, String vehicleType, String vehicleNumber, String driverName, String contactNumber) {
        this.id = id;
        this.vehicleType = vehicleType;
        this.vehicleNumber = vehicleNumber;
        this.driverName = driverName;
        this.contactNumber = contactNumber;
    }

    public String getId() { return id; }
    public String getVehicleType() { return vehicleType; }
    public String getVehicleNumber() { return vehicleNumber; }
    public String getDriverName() { return driverName; }
    public String getContactNumber() { return contactNumber; }
}
