package com.theeztech.communityservicedashboard;

public class Doctor {
    private String id;
    private String name;
    private String specialization;
    private String qualification;
    private String phone;
    private String visitFee;
    private String chamberTime;
    private int pendingCount = 0;

    public Doctor(String id, String name, String specialization, String qualification, String phone, String visitFee, String chamberTime) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.qualification = qualification;
        this.phone = phone;
        this.visitFee = visitFee;
        this.chamberTime = chamberTime;
    }

    public Doctor(String id, String name, String specialization, String qualification, String phone, String visitFee, String chamberTime, int pendingCount) {
        this(id, name, specialization, qualification, phone, visitFee, chamberTime);
        this.pendingCount = pendingCount;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public String getQualification() { return qualification; }
    public String getPhone() { return phone; }
    public String getVisitFee() { return visitFee; }
    public String getChamberTime() { return chamberTime; }
    public int getPendingCount() { return pendingCount; }
    public void setPendingCount(int pendingCount) { this.pendingCount = pendingCount; }
}
