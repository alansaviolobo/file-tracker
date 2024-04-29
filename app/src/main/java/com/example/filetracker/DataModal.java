package com.example.filetracker;

public class DataModal {

    // variables for our Employee,
    // description, tracks and duration, id.
    private String EmployeeName;
    private String DivisionName;

    private int id;

    // creating getter and setter methods
    public String getEmployeeName() { return EmployeeName; }

    public void setEmployeeName(String EmployeeName)
    {
        this.EmployeeName = EmployeeName;
    }

    public String getDivisionName()
    {
        return DivisionName;
    }

    public void setDivisionName(String DivisionName)
    {
        this.DivisionName = DivisionName;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    // constructor
    public DataModal(String EmployeeName,
                    String DivisionName
    )
    {
        this.EmployeeName =EmployeeName;
        this.DivisionName = DivisionName;

    }
}
