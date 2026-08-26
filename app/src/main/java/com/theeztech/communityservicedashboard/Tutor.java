package com.theeztech.communityservicedashboard;

public class Tutor {
    private String id;
    private String name;
    private String phone;
    private String education;
    private String subjects;
    private String salary;
    private String address;
    private String experience;
    private String status;

    public Tutor(String id, String name, String phone, String education, String subjects, String salary, String address, String experience, String status) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.education = education;
        this.subjects = subjects;
        this.salary = salary;
        this.address = address;
        this.experience = experience;
        this.status = status;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEducation() { return education; }
    public String getSubjects() { return subjects; }
    public String getSalary() { return salary; }
    public String getAddress() { return address; }
    public String getExperience() { return experience; }
    public String getStatus() { return status; }
}
