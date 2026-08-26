package com.theeztech.communityservicedashboard;

public class Donor {
    private String name;
    private String phone;
    private String bloodGroup;
    private String lastDonationDate;

    public Donor(String name, String phone, String bloodGroup, String lastDonationDate) {
        this.name = name;
        this.phone = phone;
        this.bloodGroup = bloodGroup;
        this.lastDonationDate = lastDonationDate;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getBloodGroup() { return bloodGroup; }
    public String getLastDonationDate() { return lastDonationDate; }
}