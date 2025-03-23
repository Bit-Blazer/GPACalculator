package com.example.gpacalculator;

public class Subject {
    private final String code;
    private final String name;
    private final String shortName;
    private final int credits;
    private final int semester;

    public Subject(String code, String name, String shortName, int credits, int semester) {
        this.code = code;
        this.name = name;
        this.shortName = shortName;
        this.credits = credits;
        this.semester = semester;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getShortName() { return shortName; }
    public int getCredits() { return credits; }
    public int getSemester() { return semester; }
}
