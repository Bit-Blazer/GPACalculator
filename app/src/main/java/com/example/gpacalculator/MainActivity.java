package com.example.gpacalculator;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MainActivity extends AppCompatActivity implements PdfParser.PdfParseListener {
    private static final int READ_REQUEST_CODE = 42;

    private SubjectRepository subjectRepository;
    private LinearLayout subjectsContainer;
    private List<View> subjectViews = new ArrayList<>();
    private TextView resultTextView;
    private Button selectPdfButton;
    private Button calculateButton;
    private int selectedSemester = 1;
    private AlertDialog progressDialog;
    private ActivityResultLauncher<Intent> pdfPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register PDF launcher
        pdfPickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                progressDialog.show();

                Intent data = result.getData();
                // Handle multiple files selection
                if (data.getClipData() != null) {
                    ClipData clipData = data.getClipData();
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri uri = clipData.getItemAt(i).getUri();
                        PdfParser.parseFromUri(uri, getContentResolver(), MainActivity.this);
                    }
                } else if (data.getData() != null) {
                    // Handle single file selection
                    Uri uri = data.getData();
                    PdfParser.parseFromUri(uri, getContentResolver(), MainActivity.this);
                }
            }
        });

        // Initialize PDFBox
        PDFBoxResourceLoader.init(getApplicationContext());

        // Initialize repository
        subjectRepository = SubjectRepository.getInstance();

        // Initialize UI elements
        initializeUI();

        // Add the first empty row
    }

    private void initializeUI() {
        subjectsContainer = findViewById(R.id.subjects_container);
        resultTextView = findViewById(R.id.result_text_view);
        selectPdfButton = findViewById(R.id.select_pdf_button);
        calculateButton = findViewById(R.id.calculate_button);

        // Set up PDF button
        selectPdfButton.setOnClickListener(v -> openPdfSelection());

        // Set up calculate button
        calculateButton.setOnClickListener(v -> calculateGPA());

        // Set up FAB for adding subjects
        FloatingActionButton addSubjectButton = findViewById(R.id.add_subject_button);
        addSubjectButton.setOnClickListener(v -> addSubjectRow());

        // Create custom progress dialog using Material Components
        View progressView = getLayoutInflater().inflate(R.layout.dialog_progress, null);
        progressDialog = new MaterialAlertDialogBuilder(this).setView(progressView).setCancelable(false).create();
    }

    private void openPdfSelection() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pdfPickerLauncher.launch(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            progressDialog.show();

            // Handle multiple files selection
            if (data.getClipData() != null) {
                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri uri = clipData.getItemAt(i).getUri();
                    PdfParser.parseFromUri(uri, getContentResolver(), MainActivity.this);
                }
            } else if (data.getData() != null) {
                // Handle single file selection
                Uri uri = data.getData();
                PdfParser.parseFromUri(uri, getContentResolver(), MainActivity.this);
            }
        }
    }

    @Override
    public void onPdfParseSuccess(List<SubjectResult> subjectResults) {
        runOnUiThread(() -> {
            progressDialog.dismiss();

            if (subjectResults.isEmpty()) {
                Toast.makeText(MainActivity.this,
                        "No subjects found for semester " + selectedSemester,
                        Toast.LENGTH_LONG).show();
                return;
            }



            // Add each subject from the results
            for (SubjectResult result : subjectResults) {
                View subjectRow = addSubjectRow();
                AutoCompleteTextView subjectSpinner = subjectRow.findViewById(R.id.subject_spinner);
                AutoCompleteTextView gradeSpinner = subjectRow.findViewById(R.id.grade_spinner);

                // Find the subject by code
                Optional<Subject> subject = subjectRepository.findByCode(result.getCode());
                subject.ifPresent(s -> subjectSpinner.setText(s.getShortName()));

                // Set grade
                gradeSpinner.setText(result.getGrade());
            }

            // Calculate GPA
            calculateGPA();
        });
    }

    @Override
    public void onPdfParseError(Exception e) {
        runOnUiThread(() -> {
            progressDialog.dismiss();
            Toast.makeText(MainActivity.this,
                    "Error parsing PDF: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        });
    }

    private View addSubjectRow() {
        View subjectRow = getLayoutInflater().inflate(R.layout.subject_row, null);

        AutoCompleteTextView subjectSpinner = subjectRow.findViewById(R.id.subject_spinner);
        AutoCompleteTextView gradeSpinner = subjectRow.findViewById(R.id.grade_spinner);

        // Prepare subject items with format: "ShortName - Full Name"
        List<String> subjectItems = new ArrayList<>();
        for (Subject subject : subjectRepository.getAllSubjects()) {
            subjectItems.add(subject.getShortName() + " - " + subject.getName());
        }

        // Set up subject dropdown
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, subjectItems);
        subjectSpinner.setAdapter(subjectAdapter);

        // Extract just the shortName when selected
        subjectSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selected = subjectAdapter.getItem(position);
            String shortName = selected.split(" - ")[0];
            subjectSpinner.setText(shortName);
        });

        // Set up grade dropdown
        String[] grades = {"O", "A+", "A", "B+", "B", "C", "U", "AB"};
        ArrayAdapter<String> gradeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, grades);
        gradeSpinner.setAdapter(gradeAdapter);

        // Set up the swipe dismiss functionality
        SwipeDismissLayout swipeLayout = (SwipeDismissLayout) subjectRow;
        swipeLayout.setDismissCallback(() -> {
            if (subjectViews.size() > 1) {
                subjectsContainer.removeView(subjectRow);
                subjectViews.remove(subjectRow);
            } else {
                Toast.makeText(MainActivity.this,
                        "You need at least one subject",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Add to container and list
        subjectsContainer.addView(subjectRow);
        subjectViews.add(subjectRow);

        return subjectRow;
    }

    private void calculateGPA() {
        if (subjectViews.isEmpty()) {
            Toast.makeText(this, "Please add at least one subject", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<Subject, GradeEnum> subjectGrades = new HashMap<>();
        boolean allValid = true;

        for (View subjectRow : subjectViews) {
            AutoCompleteTextView subjectSpinner = subjectRow.findViewById(R.id.subject_spinner);
            AutoCompleteTextView gradeSpinner = subjectRow.findViewById(R.id.grade_spinner);

            String shortName = subjectSpinner.getText().toString();
            String gradeStr = gradeSpinner.getText().toString();

            if (shortName.isEmpty() || gradeStr.isEmpty()) {
                allValid = false;
                break;
            }

            Optional<Subject> subject = subjectRepository.findByShortName(shortName);
            if (subject.isEmpty()) {
                Toast.makeText(this, "Unknown subject: " + shortName, Toast.LENGTH_SHORT).show();
                allValid = false;
                break;
            }

            GradeEnum grade = GradeEnum.fromString(gradeStr);
            subjectGrades.put(subject.get(), grade);
        }

        if (!allValid) {
            Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show();
            return;
        }

        double gpa = GpaCalculator.calculate(subjectGrades);
        resultTextView.setText(String.format("GPA: %.2f", gpa));
    }
}