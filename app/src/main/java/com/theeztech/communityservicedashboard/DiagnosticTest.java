package com.theeztech.communityservicedashboard;

public class DiagnosticTest {
    private String id;
    private String name;
    private String description;
    private String price;

    public DiagnosticTest(String id, String name, String description, String price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
}
