package com.example.gpacalculator;

public enum GradeEnum {
    O(10.0),
    A_PLUS(9.0, "A+"),
    A(8.0),
    B_PLUS(7.0, "B+"),
    B(6.0),
    C(5.0),
    U(0.0),
    AB(0.0);

    private final double points;
    private final String display;

    GradeEnum(double points) {
        this.points = points;
        this.display = name();
    }

    GradeEnum(double points, String display) {
        this.points = points;
        this.display = display;
    }

    public double getPoints() {
        return points;
    }

    public String getDisplay() {
        return display;
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