package com.example.gpacalculator;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private LinearLayout subjectsContainer;
    private Button calculateButton;
    private TextView resultTextView;
    private FloatingActionButton addSubjectButton;
    private static final int READ_REQUEST_CODE = 42;
    private Button selectPdfButton;

    private Spinner semesterSpinner;
    private int selectedSemester = 5; // Default semester

    // Map to store credits for each subject
    private final Map<String, Integer> subjectCredits = new HashMap<>();

    private static final List<Subject> ALL_SUBJECTS = Arrays.asList(
            new Subject("EE101", "Basic Electrical and Electronics Engineering", "BEEE", 3, 1),
            new Subject("EE101L", "BEEE Lab", "BEEELAB", 1, 1),
            new Subject("CS101", "Computer Architecture", "CA", 3, 2),
            new Subject("CE101", "Civil Engineering", "CE", 2, 1),
            new Subject("CE101L", "Civil Engineering Lab", "CELAB", 1, 1),
            new Subject("CG201", "Computer Graphics", "CG", 3, 3),
            new Subject("21BA05T", "Professional Ethics and Human Values", "PEHV", 1, 5),
            new Subject("21CS301T", "Internet Programming", "IP", 1, 5),
            new Subject("21CS302T", "Theory of Computation", "TOC", 1, 5),
            new Subject("21CS303T", "Computer Networks", "CN", 1, 5),
            new Subject("21CS304L", "Internet Programming Laboratory", "IP LAB", 1, 5),
            new Subject("21CS305L", "Computer Networks Laboratory", "CN LAB", 1, 5),
            new Subject("21CS314T", "Soft Computing", "SC", 1, 5),
            new Subject("21OCE323T", "Geographic Information System", "GIS", 1, 5),
            new Subject("21TP301L", "Quantitative Aptitude and Soft Skills", "QA & SS", 1, 5),
            new Subject("WS101", "Workshop", "WORKSHOP", 2, 1)
    );

    // List to store dynamically added subject views
    private final List<View> subjectViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize semester spinner
        semesterSpinner = findViewById(R.id.semester_spinner);
        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                new String[]{"1", "2", "3", "4", "5", "6", "7", "8"});
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semesterSpinner.setAdapter(semesterAdapter);
        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSemester = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSemester = 5;
            }
        });

        for (Subject subject : ALL_SUBJECTS) {
            subjectCredits.put(subject.getShortName(), subject.getCredits());
        }

        subjectsContainer = findViewById(R.id.subjects_container);
        addSubjectButton = findViewById(R.id.add_subject_button);
        calculateButton = findViewById(R.id.calculate_button);
        resultTextView = findViewById(R.id.result_text_view);

        addSubjectButton.setOnClickListener(v -> addSubjectRow());
        calculateButton.setOnClickListener(v -> calculateGPA());

        // Add first subject row by default
        addSubjectRow();
        // Initialize PDF selection button
        selectPdfButton = findViewById(R.id.select_pdf_button);
        selectPdfButton.setOnClickListener(v -> openPdfSelection());

        // Initialize PDFBox
        PDFBoxResourceLoader.init(getApplicationContext());

    }
    private void openPdfSelection() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent, READ_REQUEST_CODE);
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                Uri uri = resultData.getData();
                try {
                    parsePdfFromUri(uri);
                    System.out.println("URI: ");

                    System.out.println(uri);
                } catch (Exception e) {
                    Toast.makeText(this, "Error parsing PDF: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    }

    private void parsePdfFromUri(Uri uri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        PDDocument document = PDDocument.load(bufferedInputStream);
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        String text = pdfTextStripper.getText(document);
        document.close();

        // Process the extracted text
        processExtractedText(text);
    }
    private void processExtractedText(String text) {
        System.out.println("\n\n\n" + text + "\n\n\n");
        // Clear existing subject rows
        subjectsContainer.removeAllViews();
        subjectViews.clear();

        // Get the selected semester
        String semPrefix = String.valueOf(selectedSemester);

        // Dynamic regex pattern with the selected semester
        String regexPattern = semPrefix + "\\s(2[123][A-Z]{2,3}\\d{2,3}[TL]) - ([A-Za-z\\s]+)\\s(A\\+?|B\\+?|C|O|U|AB)\\s";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(text);

        boolean subjectsFound = false;

        while (matcher.find()) {
            String subjectCode = matcher.group(1);
            String subjectName = matcher.group(2).trim();
            String grade = matcher.group(3);
            System.out.println(subjectName);

            // Add subject to UI
            View subjectRow = addSubjectRow();
            AutoCompleteTextView subjectSpinner = subjectRow.findViewById(R.id.subject_spinner);
            AutoCompleteTextView gradeSpinner = subjectRow.findViewById(R.id.grade_spinner);

            // Find matching subject in ALL_SUBJECTS
            for (Subject subject : ALL_SUBJECTS) {
                if (subject.getCode().equals(subjectCode)) {
                    subjectSpinner.setText(subject.getShortName());
                    break;
                }
            }

            gradeSpinner.setText(grade);
            subjectsFound = true;
        }

        if (!subjectsFound) {
            Toast.makeText(this, "No matching subjects found for semester " + selectedSemester,
                    Toast.LENGTH_LONG).show();
            // Add one empty row if nothing was found
            addSubjectRow();
        } else {
            // Calculate GPA automatically
            calculateGPA();
        }
    }
    private View addSubjectRow() {
        View subjectRow = getLayoutInflater().inflate(R.layout.subject_row, null);

        AutoCompleteTextView subjectSpinner = subjectRow.findViewById(R.id.subject_spinner);
        AutoCompleteTextView gradeSpinner = subjectRow.findViewById(R.id.grade_spinner);
        ImageButton removeButton = subjectRow.findViewById(R.id.remove_subject_button);

        // Set up subject spinner with better labels
        List<String> subjectItems = new ArrayList<>();
        for (Subject subject : ALL_SUBJECTS) {
            subjectItems.add(subject.getShortName() + " - " + subject.getName());
        }

        // Set up subject spinner
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line,
                subjectItems);
        subjectSpinner.setAdapter(subjectAdapter);

        // Add a listener to extract just the shortName when an item is selected
        subjectSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selected = subjectAdapter.getItem(position);
            String shortName = selected.split(" - ")[0];
            subjectSpinner.setText(shortName);
        });

        // Set up grade spinner
        ArrayAdapter<String> gradeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line,
                new String[]{"O", "A+", "A", "B+", "B", "C"});
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
        return subjectRow;
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