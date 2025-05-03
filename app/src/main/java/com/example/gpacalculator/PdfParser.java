package com.example.gpacalculator;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfParser {
    private static final String TAG = "PdfParser";

    public interface PdfParseListener {
        void onPdfParseSuccess(List<SubjectResult> subjectResults);

        void onPdfParseError(Exception e);
    }

    public static void parseFromUri(Uri uri, ContentResolver contentResolver, PdfParseListener listener) {
        new Thread(() -> {
            try {
                List<SubjectResult> results = extractFromPdf(uri, contentResolver);
                listener.onPdfParseSuccess(results);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing PDF", e);
                listener.onPdfParseError(e);
            }
        }).start();
    }

    private static List<SubjectResult> extractFromPdf(Uri uri, ContentResolver contentResolver) throws Exception {
        List<SubjectResult> results = new ArrayList<>();

        InputStream inputStream = contentResolver.openInputStream(uri);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        PDDocument document = PDDocument.load(bufferedInputStream);
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        String text = pdfTextStripper.getText(document);
        document.close();

        String regexPattern = "(2[123][A-Z]{2,3}\\d{2,3}[TL])\\s*(?:-\\s|\\s)([A-Za-z,\\s]+)\\s(A\\+?|B\\+?|C|O|U|AB)\\s";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String subjectCode = matcher.group(1);
            String subjectName = matcher.group(2).trim();
            String grade = matcher.group(3);
            System.out.println(subjectCode + " " + subjectName + " " + grade);

            results.add(new SubjectResult(subjectCode, subjectName, grade));
        }

        return results;
    }


}