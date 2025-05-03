package com.example.gpacalculator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SubjectRepository {
    private static SubjectRepository instance;
    private final List<Subject> allSubjects;
    private final Map<String, Subject> subjectByCode;
    private final Map<String, Subject> subjectByShortName;

    public static synchronized SubjectRepository getInstance() {
        if (instance == null) {
            instance = new SubjectRepository();
        }
        return instance;
    }

    private SubjectRepository() {
        allSubjects = initializeSubjects();

        // Build lookup maps for quick access
        subjectByCode = new HashMap<>();
        subjectByShortName = new HashMap<>();

        for (Subject subject : allSubjects) {
            subjectByCode.put(subject.getCode(), subject);
            subjectByShortName.put(subject.getShortName(), subject);
        }
    }

    public List<Subject> getAllSubjects() {
        return allSubjects;
    }

    public Optional<Subject> findByCode(String code) {
        return Optional.ofNullable(subjectByCode.get(code));
    }

    public Optional<Subject> findByShortName(String shortName) {
        return Optional.ofNullable(subjectByShortName.get(shortName));
    }

    private List<Subject> initializeSubjects() {
        return Arrays.asList(
                //TODO: Modify the Credits correctly
                // Semester 6
                new Subject("21CS306T", "Mobile Computing", "MC", 3, 6),
                new Subject("21CS307T", "Compiler Design", "CD", 3, 6),
                new Subject("21CS308T", "Artificial Intelligence", "AI", 3, 6),
                new Subject("21CS317T", "Software Project Management", "SPM", 4, 6),
                new Subject("21MA301T", "Resource Management Techniques", "RMT", 1, 6),
                new Subject("21OCE328T", "Disaster Management", "DM", 3, 6),
                new Subject("21CS309L", "Mobile Application Development Laboratory", "MAD LAB", 1, 6),
                new Subject("21CS310L", "Compiler Design Laboratory", "CD LAB", 3, 6),
                new Subject("21CS311L", "Internship", "Intern", 1, 6),
                new Subject("21CS312L", "Mini Project", "MP", 1, 6),

                // Semester 5
                new Subject("21BA05T", "Professional Ethics and Human Values", "Ethics", 3, 5),
                new Subject("21CS301T", "Internet Programming", "IP", 3, 5),
                new Subject("21CS302T", "Theory of Computation", "TOC", 4, 5),
                new Subject("21CS303T", "Computer Networks", "CN", 3, 5),
                new Subject("21CS304L", "Internet Programming Laboratory", "IP LAB", 1, 5),
                new Subject("21CS305L", "Computer Networks Laboratory", "CN LAB", 1, 5),
                new Subject("21CS314T", "Soft Computing", "SC", 3, 5),
                new Subject("21OCE323T", "Geographic Information System", "GIS", 3, 5),
                new Subject("21TP301L", "Quantitative Aptitude and Soft Skills", "QA & SS", 1, 5),

                // Semester 4
                new Subject("21CH201T", "Environmental Science and Engineering", "EVS", 0, 4),
                new Subject("21CS204T", "Operating Systems", "OS", 3, 4),
                new Subject("21CS205T", "Design and Analysis of Algorithms", "DAA", 4, 4),
                new Subject("21CS206L", "Operating System Laboratory", "OS LAB", 1, 4),
                new Subject("21CS207L", "Programming in Java Laboratory", "JAVA LAB", 1, 4),
                new Subject("21IT204T", "Object Oriented Software Engineering", "OOSE", 3, 4),
                new Subject("21IT205T", "Database Management Systems", "DBMS", 3, 4),
                new Subject("21IT207T", "Java Programming", "JAVA", 3, 4),
                new Subject("21IT208L", "Database Management Systems Laboratory", "DBMS LAB", 1, 4),
                new Subject("21MA205T", "Probability and Queueing Theory", "PQT", 4, 4),
                new Subject("21TP202L", "Quantitative Aptitude and Communication Skills", "QA & CS", 1, 4),
                new Subject("23GE102T", "Tamils and Technology", "Tamil 2", 1, 4),

                // Semester 3
                new Subject("21CS201T", "Data Structures", "DS", 3, 3),
                new Subject("21CS202T", "Digital Logic Circuits", "DLC", 4, 3),
                new Subject("21CS203L", "Data Structures Laboratory", "DS LAB", 1, 3),
                new Subject("21IT201T", "Object Oriented Programming", "OOPS", 3, 3),
                new Subject("21IT202T", "Computer Architecture", "CA", 3, 3),
                new Subject("21IT203L", "Object Oriented Programming Laboratory", "OOPS LAB", 1, 3),
                new Subject("21MA201T", "Discrete Mathematics", "DM", 4, 3),
                new Subject("21PH205T", "Fundamentals of Nano Science", "FNS", 0, 3),
                new Subject("21TP201L", "Quantitative Aptitude and Behavioural Skills", "QA & BS", 1, 3),
                new Subject("23GE101T", "Heritage of Tamils", "Tamil 1", 1, 3),

                // Semester 2
                new Subject("21CS103T", "Programming for Problem Solving Using Python", "Python", 4, 2),
                new Subject("21CS104T", "Introduction to Information and Computing Technology", "IICT", 3, 2),
                new Subject("21EE103T", "Basic Electrical Electronics and Communication Engineering", "BEEE", 3, 2),
                new Subject("21EE104L", "Basic Electrical, Electronics and Communication Engineering", "BEEE LAB", 1, 2),
                new Subject("21EN103T", "Constitution of India", "COI", 0, 2),
                new Subject("21MA102T", "Vector Calculus and Complex Functions", "VCCF", 4, 2),
                new Subject("21ME104L", "Workshop Practice", "Workshop", 2, 2),
                new Subject("21PH101T", "Engineering Physics", "Physics", 3, 2),
                new Subject("21PH102L", "Physics Laboratory", "Physics Lab", 1, 2),
                new Subject("21TP101L", "Quantitative Aptitude and Verbal Reasoning", "QA & VR", 1, 2),

                // Semester 1
                new Subject("21EN101T", "Communicative English", "English", 2, 1),
                new Subject("21MA101T", "Matrices, Differential and Integral Calculus", "MDIC", 4, 1),
                new Subject("21CH101T", "Engineering Chemistry", "Chemistry", 3, 1),
                new Subject("21CS101T", "Programming for Problem Solving in C", "PPSC", 3, 1),
                new Subject("21ME101T", "Engineering Graphics", "EG", 4, 1),
                new Subject("21EN102L", "Communicative English Laboratory", "English LAB", 1, 1),
                new Subject("21CH102L", "Chemistry Laboratory", "Chemistry LAB", 1, 1),
                new Subject("21CS102L", "C Programming Laboratory", "CP LAB", 2, 1)
        );
    }
}