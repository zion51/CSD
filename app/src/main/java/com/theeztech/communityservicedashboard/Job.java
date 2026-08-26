package com.theeztech.communityservicedashboard;

public class Job {
    private String id;
    private String title;
    private String category;
    private String type;
    private String vacancies;
    private String salary;
    private String workplace;
    private String education;
    private String experience;
    private String deadline;
    private String description;
    private String ownerPhone;

    public Job(String id, String title, String category, String type, String vacancies, String salary, String workplace, String education, String experience, String deadline, String description, String ownerPhone) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.type = type;
        this.vacancies = vacancies;
        this.salary = salary;
        this.workplace = workplace;
        this.education = education;
        this.experience = experience;
        this.deadline = deadline;
        this.description = description;
        this.ownerPhone = ownerPhone;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getType() { return type; }
    public String getVacancies() { return vacancies; }
    public String getSalary() { return salary; }
    public String getWorkplace() { return workplace; }
    public String getEducation() { return education; }
    public String getExperience() { return experience; }
    public String getDeadline() { return deadline; }
    public String getDescription() { return description; }
    public String getOwnerPhone() { return ownerPhone; }
}
