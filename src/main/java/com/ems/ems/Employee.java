package com.ems.ems;

public class Employee extends Person {
    private String department;
    private double salary;

    public Employee(int id, String name, String department, double salary) {
        super(id, name);
        this.department = department;
        this.salary = salary;
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    @Override
    public String getRoleDescription() {
        return "Regular Corporate Staff Member";
    }
}