package com.theeztech.communityservicedashboard;

public class Business {
    private String id;
    private String name;
    private String ownerName;
    private String mobile;
    private String address;
    private String category;
    private String description;

    public Business(String id, String name, String ownerName, String mobile, String address, String category, String description) {
        this.id = id;
        this.name = name;
        this.ownerName = ownerName;
        this.mobile = mobile;
        this.address = address;
        this.category = category;
        this.description = description;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getOwnerName() { return ownerName; }
    public String getMobile() { return mobile; }
    public String getAddress() { return address; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
}
