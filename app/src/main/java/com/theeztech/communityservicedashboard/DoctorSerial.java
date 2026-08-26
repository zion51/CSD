package com.theeztech.communityservicedashboard;

public class DoctorSerial {
    private String id;
    private String doctorName;
    private String patientName;
    private String patientPhone;
    private String serialNumber;
    private String date;
    private String status;

    public DoctorSerial(String id, String doctorName, String patientName, String patientPhone, String serialNumber, String date, String status) {
        this.id = id;
        this.doctorName = doctorName;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.serialNumber = serialNumber;
        this.date = date;
        this.status = status;
    }

    public String getId() { return id; }
    public String getDoctorName() { return doctorName; }
    public String getPatientName() { return patientName; }
    public String getPatientPhone() { return patientPhone; }
    public String getSerialNumber() { return serialNumber; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
}
