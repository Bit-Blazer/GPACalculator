package com.example.gpacalculator;

public class SubjectResult {
    private final String code;
    private final String name;
    private final String grade;

    public SubjectResult(String code, String name, String grade) {
        this.code = code;
        this.name = name;
        this.grade = grade;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getGrade() {
        return grade;
    }
}