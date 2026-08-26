package com.theeztech.communityservicedashboard;

public class HealthcareCenter {
    private String id;
    private String name;
    private String type;
    private String contact;
    private String email;
    private String address;
    private String description;

    public HealthcareCenter(String id, String name, String type, String contact, String email, String address, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.contact = contact;
        this.email = email;
        this.address = address;
        this.description = description;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getContact() { return contact; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getDescription() { return description; }
}
