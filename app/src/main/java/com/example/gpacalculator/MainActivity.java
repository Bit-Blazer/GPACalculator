package com.example.gpacalculator;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MainActivity extends ComponentActivity implements PdfParser.PdfParseListener {

    // UI Elements
    private LinearLayout subjectsContainer;
    private TextView resultTextView;
    private View resultCard;
    private AlertDialog progressDialog;

    // PDF picker launcher
    private ActivityResultLauncher<Intent> pdfPickerLauncher;

    // Subject management
    private final List<View> subjectViews = new ArrayList<>();
    private SubjectRepository subjectRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize PDFBox for PDF parsing
        PDFBoxResourceLoader.init(getApplicationContext());

        // Initialize repository
        subjectRepository = SubjectRepository.getInstance();

        // Initialize UI components and listeners
        initializeUI();

        // Register PDF picker result launcher
        pdfPickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                progressDialog.show();

                Intent data = result.getData();
                // Handle multiple files selection
                if (data.getClipData() != null) {
                    ClipData clipData = data.getClipData();
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri uri = clipData.getItemAt(i).getUri();
                        PdfParser.parseFromUri(uri, getContentResolver(), this);
                    }
                } else if (data.getData() != null) {
                    // Handle single file selection
                    Uri uri = data.getData();
                    PdfParser.parseFromUri(uri, getContentResolver(), this);
                }
            }
        });
    }

    private void initializeUI() {
        // Bind UI elements
        subjectsContainer = findViewById(R.id.subjects_container);
        resultTextView = findViewById(R.id.result_text_view);
        resultCard = findViewById(R.id.resultCard);

        // Set up the top app bar and its menu items
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        // Handle menu item clicks
        topAppBar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.select_pdf_button) {
                openPdfSelection();
                return true;
            } else if (id == R.id.settings_button) {
                // Implement settings functionality if needed
                System.out.println("Called settings_button");
                return true;
            }
            return false;
        });
        // Set long press actions on the menu items
        topAppBar.post(() -> {
            View selectPdfView = topAppBar.findViewById(R.id.select_pdf_button);
            if (selectPdfView != null) {
                selectPdfView.setOnLongClickListener(v -> {
                    addSubjectRow();
                    return true; // consume the long press
                });
            }
            View settings = topAppBar.findViewById(R.id.settings_button);
            if (settings != null) {
                settings.setOnLongClickListener(v -> {
                    resetSubjectsContainer();
                    return true;
                });
            }
        });

        // Set up custom progress dialog using Material Components
        View progressView = getLayoutInflater().inflate(R.layout.dialog_progress, null);
        progressDialog = new MaterialAlertDialogBuilder(this).setView(progressView).setCancelable(false).create();
    }

    // Open PDF file picker
    private void openPdfSelection() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pdfPickerLauncher.launch(intent);
    }

    // Reset subjects container (clear views and data)
    private void resetSubjectsContainer() {
        subjectsContainer.removeAllViews();
        subjectViews.clear();
        resultCard.setVisibility(View.GONE);
    }

    // Add a new subject row
    private View addSubjectRow() {
        View subjectRow = getLayoutInflater().inflate(R.layout.subject_row, null);
        AutoCompleteTextView subjectSpinner = subjectRow.findViewById(R.id.subject_spinner);
        AutoCompleteTextView gradeSpinner = subjectRow.findViewById(R.id.grade_spinner);

        List<String> subjectItems = new ArrayList<>();
        for (Subject subject : subjectRepository.getAllSubjects()) {
            subjectItems.add(subject.getShortName() + " - " + subject.getName());
        }
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subjectItems);
        subjectSpinner.setAdapter(subjectAdapter);

        subjectSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selected = subjectAdapter.getItem(position);
            String shortName = selected.split(" - ")[0];
            subjectSpinner.setText(shortName);
            calculateGPA();
        });

        String[] grades = {"O", "A+", "A", "B+", "B", "C", "U", "AB"};
        ArrayAdapter<String> gradeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, grades);
        gradeSpinner.setAdapter(gradeAdapter);

        gradeSpinner.setOnItemClickListener((parent, view, position, id) -> calculateGPA());

        SwipeDismissLayout swipeLayout = (SwipeDismissLayout) subjectRow;
        swipeLayout.setDismissCallback(() -> {
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

    // Calculate GPA based on selected subjects and grades
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
        resultCard.setVisibility(View.VISIBLE);
        resultTextView.setText(String.format("GPA: %.2f", gpa));
    }

    @Override
    public void onPdfParseSuccess(List<SubjectResult> subjectResults) {
        runOnUiThread(() -> {
            progressDialog.dismiss();

            if (subjectResults.isEmpty()) {
                Toast.makeText(MainActivity.this, "No subjects found", Toast.LENGTH_LONG).show();
                return;
            }

            // Add subjects from PDF results
            for (SubjectResult result : subjectResults) {
                View subjectRow = addSubjectRow();
                AutoCompleteTextView subjectSpinner = subjectRow.findViewById(R.id.subject_spinner);
                AutoCompleteTextView gradeSpinner = subjectRow.findViewById(R.id.grade_spinner);

                Optional<Subject> subject = subjectRepository.findByCode(result.getCode());
                subject.ifPresent(s -> subjectSpinner.setText(s.getShortName()));
                gradeSpinner.setText(result.getGrade());
            }

            // Recalculate GPA after loading subjects
            calculateGPA();
        });
    }

    @Override
    public void onPdfParseError(Exception e) {
        runOnUiThread(() -> {
            progressDialog.dismiss();
            Toast.makeText(MainActivity.this, "Error parsing PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}