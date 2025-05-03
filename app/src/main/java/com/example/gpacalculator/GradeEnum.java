package com.example.gpacalculator;

public enum GradeEnum {
    O(10.0, "O"), A_PLUS(9.0, "A+"), A(8.0, "A"), B_PLUS(7.0, "B+"), B(6.0, "B"), C(5.0, "C"), U(0.0, "U"), AB(0.0, "AB");

    private final double points;
    private final String display;

    GradeEnum(double points, String display) {
        this.points = points;
        this.display = display;
    }

    public double getPoints() {
        return points;
    }

    public static GradeEnum fromString(String grade) {
        for (GradeEnum gradeEnum : values()) {
            if (gradeEnum.display.equals(grade)) {
                return gradeEnum;
            }
        }
        return U; // Default to U (Unsatisfactory)
    }
}