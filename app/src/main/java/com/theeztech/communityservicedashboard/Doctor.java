package com.theeztech.communityservicedashboard;

public class Doctor {
    private String id;
    private String name;
    private String specialization;
    private String qualification;
    private String phone;
    private String visitFee;
    private String chamberTime;
    private String chamberDay;
    private String healthcareName;
    private int pendingCount = 0;

    public Doctor(String id, String name, String specialization, String qualification, String phone, String visitFee, String chamberTime, String chamberDay) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.qualification = qualification;
        this.phone = phone;
        this.visitFee = visitFee;
        this.chamberTime = chamberTime;
        this.chamberDay = chamberDay;
    }

    public Doctor(String id, String name, String specialization, String qualification, String phone, String visitFee, String chamberTime, String chamberDay, String healthcareName) {
        this(id, name, specialization, qualification, phone, visitFee, chamberTime, chamberDay);
        this.healthcareName = healthcareName;
    }

    public Doctor(String id, String name, String specialization, String qualification, String phone, String visitFee, String chamberTime, String chamberDay, String healthcareName, int pendingCount) {
        this(id, name, specialization, qualification, phone, visitFee, chamberTime, chamberDay, healthcareName);
        this.pendingCount = pendingCount;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public String getQualification() { return qualification; }
    public String getPhone() { return phone; }
    public String getVisitFee() { return visitFee; }
    public String getChamberTime() { return chamberTime; }
    public String getChamberDay() { return chamberDay; }
    public String getHealthcareName() { return healthcareName; }
    public void setHealthcareName(String healthcareName) { this.healthcareName = healthcareName; }
    public int getPendingCount() { return pendingCount; }
    public void setPendingCount(int pendingCount) { this.pendingCount = pendingCount; }
}
