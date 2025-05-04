# GPA Calculator

An Android application designed to help students calculate their Grade Point Average (GPA) easily and efficiently.

## Features

- **Manual Input**: Add subjects and grades manually through a simple interface
- **PDF Import**: Automatically extract subject information and grades from PDF result files
  - Supports result PDFs from Velammal Student Portal
  - Compatible with DigiLocker Degree marksheets
- **Dynamic Calculation**: GPA updates instantly as subjects and grades are modified
- **Swipe-to-Delete**: Remove subject entries with a simple swipe gesture

## Technologies Used

- **Language**: Java
- **PDF Processing**: [PDFBox for Android](https://github.com/TomRoush/PdfBox-Android)
- **UI Components**: [Material Design 3](https://m3.material.io/)

## How to Use

1. **Manual Input**:

   - Add a subject entry by long-pressing the "Add PDF" button
   - Select a subject from the dropdown list
   - Choose a grade (O, A+, A, B+, B, C, U, AB)

2. **PDF Import**:

   - Tap the PDF icon in the top app bar
   - Select one or more result PDFs from your device
   - The app will parse the PDFs and automatically populate the subjects and grades

3. **Clear All Entries**:
   - Long-press the settings icon to clear all subject entries

## Disclaimer

The PDF extraction functionality is specifically designed for marksheets from Velammal Engineering College. Other institution marksheets may have different formatting and might not be parsed correctly. If you need to use this app with marksheets from other institutions, you may need to modify the regex pattern in [PdfParser.java](app/src/main/java/com/example/gpacalculator/PdfParser.java#L49):

## Grade Point System

The application uses the following grade point system:

- O: 10.0 points
- A+: 9.0 points
- A: 8.0 points
- B+: 7.0 points
- B: 6.0 points
- C: 5.0 points
- U: 0.0 points (Unsatisfactory)
- AB: 0.0 points (Absent)

## Project Structure

```plaintext
📦 GPACalculator/
├─ 📂 .github/
│  └─ 📂 workflows/
│     └─ 📄 build-release.yml
├─ 📂 app/
│  ├─ 📂 src/
│  │  └─ 📂 main/
│  │     ├─ 📂 java/
│  │     │  └─ 📂 com/
│  │     │     └─ 📂 example/
│  │     │        └─ 📂 gpacalculator/
│  │     │           ├─ 📄 GpaCalculator.java
│  │     │           ├─ 📄 GradeEnum.java
│  │     │           ├─ 📄 MainActivity.java
│  │     │           ├─ 📄 PdfParser.java
│  │     │           ├─ 📄 Subject.java
│  │     │           ├─ 📄 SubjectRepository.java
│  │     │           ├─ 📄 SubjectResult.java
│  │     │           └─ 📄 SwipeDismissLayout.java
│  │     ├─ 📂 res/
│  │     │  ├─ 📂 drawable/
│  │     │  │  ├─ 📄 add_pdf.xml
│  │     │  │  ├─ 📄 ic_launcher_background.xml
│  │     │  │  ├─ 📄 ic_launcher_foreground.xml
│  │     │  │  └─ 📄 settings.xml
│  │     │  ├─ 📂 layout/
│  │     │  │  ├─ 📄 activity_main.xml
│  │     │  │  ├─ 📄 dialog_progress.xml
│  │     │  │  └─ 📄 subject_row.xml
│  │     │  ├─ 📂 menu/
│  │     │  │  └─ 📄 top_app_bar_menu.xml
│  │     │  └─ 📂 values/
│  │     │     └─ 📄 strings.xml
│  │     └─ 📄 AndroidManifest.xml
│  ├─ 📄 .gitignore
│  ├─ 📄 build.gradle.kts
│  └─ 📄 proguard-rules.pro
├─ 📂 gradle/
│  ├─ 📂 wrapper/
│  │  ├─ 📄 gradle-wrapper.jar
│  │  └─ 📄 gradle-wrapper.properties
│  └─ 📄 libs.versions.toml
├─ 📄 .gitignore
├─ 📄 build.gradle.kts
├─ 📄 gradle.properties
├─ 📄 gradlew
├─ 📄 gradlew.bat
├─ 📄 README.md
└─ 📄 settings.gradle.kts
```

- `MainActivity`: Main application UI and logic
- `PdfParser`: PDF extraction functionality
- `GpaCalculator`: GPA calculation algorithm
- `SubjectRepository`: Subject database management
- `SwipeDismissLayout`: Custom view for swipe-to-delete functionality

## Building the Project

This project uses Gradle for building. To build the application:

```bash
# Clone the repository
git clone https://github.com/yourusername/GPACalculator.git

# Navigate to project directory
cd GPACalculator

# Build debug APK
./gradlew assembleDebug

# Build release APK (requires keystore configuration)
./gradlew assembleRelease
```

For release builds, you need to set up the keystore:

- Create a `keystore` directory in the project root
- Place your `keystore.jks` file in the keystore directory
- Create a `keystore.properties` file with the following content:

```properties
storeFile=../keystore/keystore.jks
storePassword=your_store_password
keyAlias=your_key_alias
keyPassword=your_key_password
```

## License

MIT License
