package com.theeztech.communityservicedashboard;

public class EducationInstitution {
    private String id;
    private String name;
    private String category; // School, College, Madrasa, Tutor
    private String eiin;
    private String address;
    private String contact;
    private String website;

    public EducationInstitution(String id, String name, String category, String eiin, String address, String contact, String website) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.eiin = eiin;
        this.address = address;
        this.contact = contact;
        this.website = website;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getEiin() { return eiin; }
    public String getAddress() { return address; }
    public String getContact() { return contact; }
    public String getWebsite() { return website; }
}
