package com.example.gpacalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private LinearLayout subjectsContainer;
    private Button calculateButton;
    private TextView resultTextView;
    private FloatingActionButton addSubjectButton;


    // Map to store credits for each subject
    private final Map<String, Integer> subjectCredits = new HashMap<>();

    // List to store dynamically added subject views
    private final List<View> subjectViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeSubjectCredits();

        subjectsContainer = findViewById(R.id.subjects_container);
        addSubjectButton = findViewById(R.id.add_subject_button);
        calculateButton = findViewById(R.id.calculate_button);
        resultTextView = findViewById(R.id.result_text_view);

        addSubjectButton.setOnClickListener(v -> addSubjectRow());
        calculateButton.setOnClickListener(v -> calculateGPA());

        // Add first subject row by default
        addSubjectRow();
    }

    private void initializeSubjectCredits() {
        // Add predefined credits for subjects from the marksheet
        subjectCredits.put("BEEE",3);
        subjectCredits.put("BEEELAB",1);
        subjectCredits.put("CA",3);
        subjectCredits.put("CE",2);
        subjectCredits.put("CELAB",1);
        subjectCredits.put("CG",3);
        subjectCredits.put("CN",3);
        subjectCredits.put("CNLAB",1);
        subjectCredits.put("COI",0);
        subjectCredits.put("CPLAB",2);
        subjectCredits.put("DAA",4);
        subjectCredits.put("DBMS",3);
        subjectCredits.put("DBMSLAB",1);
        subjectCredits.put("DLC",4);
        subjectCredits.put("DM",4);
        subjectCredits.put("DS",3);
        subjectCredits.put("DSLAB",1);
        subjectCredits.put("EC",3);
        subjectCredits.put("ECLAB",1);
        subjectCredits.put("EG",4);
        subjectCredits.put("EP",3);
        subjectCredits.put("EPLAB",1);
        subjectCredits.put("EVS",0);
        subjectCredits.put("FNS",0);
        subjectCredits.put("GIS",3);
        subjectCredits.put("HOT",1);
        subjectCredits.put("IICT",3);
        subjectCredits.put("IP",3);
        subjectCredits.put("IPLAB",1);
        subjectCredits.put("JP",3);
        subjectCredits.put("JPLAB",1);
        subjectCredits.put("MDIC",4);
        subjectCredits.put("OOPS",3);
        subjectCredits.put("OOPSLAB",1);
        subjectCredits.put("OOSE",3);
        subjectCredits.put("OS",3);
        subjectCredits.put("OSLAB",1);
        subjectCredits.put("PEHV",3);
        subjectCredits.put("PPSC",3);
        subjectCredits.put("PPSP",4);
        subjectCredits.put("PQT",4);
        subjectCredits.put("QABS",1);
        subjectCredits.put("QACS",1);
        subjectCredits.put("QASS",1);
        subjectCredits.put("QAVR",1);
        subjectCredits.put("SC",3);
        subjectCredits.put("TOC",4);
        subjectCredits.put("TT",1);
        subjectCredits.put("VCCF",4);
        subjectCredits.put("WORKSHOP",2);
    }

    private void addSubjectRow() {
        View subjectRow = getLayoutInflater().inflate(R.layout.subject_row, null);

        AutoCompleteTextView subjectSpinner = subjectRow.findViewById(R.id.subject_spinner);
        AutoCompleteTextView gradeSpinner = subjectRow.findViewById(R.id.grade_spinner);
        ImageButton removeButton = subjectRow.findViewById(R.id.remove_subject_button);

        // Set up subject spinner
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>(subjectCredits.keySet()));
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(subjectAdapter);

        // Set up grade spinner
        ArrayAdapter<String> gradeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line,
                new String[]{"O", "A+", "A", "B+", "B", "C"});
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeSpinner.setAdapter(gradeAdapter);

        // Set up remove button
        removeButton.setOnClickListener(v -> {
            if (subjectViews.size() > 1) {
                subjectsContainer.removeView(subjectRow);
                subjectViews.remove(subjectRow);
            } else {
                Toast.makeText(MainActivity.this, "You need at least one subject", Toast.LENGTH_SHORT).show();
            }
        });

        subjectsContainer.addView(subjectRow);
        subjectViews.add(subjectRow);
    }

    private void calculateGPA() {
        if (subjectViews.isEmpty()) {
            Toast.makeText(this, "Please add at least one subject", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalPoints = 0;
        int totalCredits = 0;
        boolean allFieldsFilled = true;

        for (View subjectRow : subjectViews) {
            AutoCompleteTextView subjectSpinner = subjectRow.findViewById(R.id.subject_spinner);
            AutoCompleteTextView gradeSpinner = subjectRow.findViewById(R.id.grade_spinner);


            String subject = subjectSpinner.getText().toString();
            String grade = gradeSpinner.getText().toString();

            if (subject.isEmpty() || grade.isEmpty() || !subjectCredits.containsKey(subject)) {
                allFieldsFilled = false;
                break;
            }

            int credits = subjectCredits.get(subject);
            double gradePoints = getGradePoints(grade);

            totalPoints += credits * gradePoints;
            totalCredits += credits;
        }
        if (!allFieldsFilled) {
            Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show();
            return;
        }

        if (totalCredits == 0) {
            Toast.makeText(this, "Total credits cannot be zero", Toast.LENGTH_SHORT).show();
            return;
        }


        double gpa = totalPoints / totalCredits;
        resultTextView.setText(String.format("GPA: %.2f", gpa));
    }

    private double getGradePoints(String grade) {
        switch (grade) {
            case "O": return 10.0;
            case "A+": return 9.0;
            case "A": return 8.0;
            case "B+": return 7.0;
            case "B": return 6.0;
            case "C": return 5.0;
            default: return 0.0;
        }
    }
}