package com.example.gpacalculator;

import java.util.Map;

public class GpaCalculator {
    public static double calculate(Map<Subject, GradeEnum> subjectGrades) {
        if (subjectGrades.isEmpty()) {
            return 0.0;
        }

        double totalPoints = 0;
        int totalCredits = 0;

        for (Map.Entry<Subject, GradeEnum> entry : subjectGrades.entrySet()) {
            Subject subject = entry.getKey();
            GradeEnum grade = entry.getValue();

            int credits = subject.getCredits();
            double gradePoints = grade.getPoints();

            totalPoints += credits * gradePoints;
            totalCredits += credits;
        }
        System.out.println(totalCredits);
        System.out.println(totalPoints);


        return totalCredits > 0 ? totalPoints / totalCredits : 0;
    }
}