package com.example.edutrack;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.*;

public class MarkAttendanceActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    ListView studentListView;
    CalendarView calendarView;
    Button submitButton;
    TextView countText;
    ImageButton backBtn;
    String selectedDate;

    List<Student> studentList = new ArrayList<>();
    HashMap<String, HashMap<Integer, String>> attendanceMap = new HashMap<>();
    StudentAdapter adapter;
    int studentYear; // store year for DB

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        dbHelper = new DatabaseHelper(this);
        studentListView = findViewById(R.id.studentListView);
        calendarView = findViewById(R.id.calendarView);
        submitButton = findViewById(R.id.submitButton);
        countText = findViewById(R.id.countText);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> finish());

        selectedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                .format(new Date(calendarView.getDate()));

        // Get the student year from Intent (default 1 if not found)
        studentYear = getIntent().getIntExtra("year", 1);

        String[] names;
        if (studentYear == 1) {
            names = new String[]{
                    "Revathy", "Amritha", "Trina", "Fidha", "Adithya", "Aswini", "Anuja", "Devika",
                    "Gopika", "Jeena", "Anu", "Anjali", "Sneha", "Judit", "Anusa", "Shreya", "Nandha",
                    "Tenu", "Niveditha", "Angelina", "Muhsina"
            };
        } else if (studentYear == 2) {
            names = new String[]{
                    "Rahul", "Vishnu", "Neha", "Pooja", "Kiran", "Varun", "Akhil", "Meera",
                    "Anand", "Ritika", "Suresh", "Arya", "Deepak", "Parvathi", "Gautham", "Saranya"
            };
        } else { // studentYear == 3
            names = new String[]{
                    "Arjun", "Harsha", "Ananya", "Vivek", "Lekha", "Mithun", "Swathi", "Rohit",
                    "Priya", "Santhosh", "Divya", "Hari", "Krishna", "Manju", "Nirmala", "Akash"
            };
        }

        // Populate student list
        studentList.clear();
        for (int i = 0; i < names.length; i++) {
            studentList.add(new Student(i + 1, names[i]));
        }

        loadAdapterForDate(selectedDate);
        updateCountText();

        // FIX: renamed calendar year parameter to avoid conflict
        calendarView.setOnDateChangeListener((view, selectedYear, month, dayOfMonth) -> {
            // Save current data
            attendanceMap.put(selectedDate, adapter.getAttendanceStatus());

            selectedDate = String.format(Locale.getDefault(),
                    "%02d-%02d-%04d", dayOfMonth, month + 1, selectedYear);
            Toast.makeText(this, "Selected Date: " + selectedDate, Toast.LENGTH_SHORT).show();

            loadAdapterForDate(selectedDate);
            updateCountText();
        });

        submitButton.setOnClickListener(v -> {
            // Delete previous records for this date & year to avoid duplicates
            dbHelper.deleteAttendanceForDateAndYear(selectedDate, studentYear);

            HashMap<Integer, String> statuses = adapter.getAttendanceStatus();
            for (Student s : studentList) {
                String status = statuses.getOrDefault(s.getRollNumber(), "Present");

                String finalStatus;
                String reason = "";

                if (status.startsWith("Absent")) {
                    finalStatus = "Absent";
                    // ✅ Extract reason if entered
                    if (status.contains(":")) {
                        reason = status.substring(status.indexOf(":") + 1).trim();
                    }
                } else {
                    finalStatus = "Present";
                }

                // Save clean status + reason separately
                dbHelper.saveAttendance(
                        s.getRollNumber(),
                        s.getName(),
                        studentYear,
                        finalStatus,
                        selectedDate,
                        reason
                );
            }
            Toast.makeText(this, "Attendance saved!", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadAdapterForDate(String date) {
        HashMap<Integer, String> savedStatus = new HashMap<>();

        // Pull entries from DB for this date & year
        Cursor cursor = dbHelper.getAttendanceByDateAndYear(date, studentYear);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int roll = cursor.getInt(cursor.getColumnIndexOrThrow("roll"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                String reason = cursor.getString(cursor.getColumnIndexOrThrow("reason"));

                if ("Absent".equals(status)) {
                    if (reason != null && !reason.isEmpty()) {
                        savedStatus.put(roll, "Absent: " + reason); // ✅ restore reason
                    } else {
                        savedStatus.put(roll, "Absent");
                    }
                } else {
                    savedStatus.put(roll, "Present");
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Adapter auto-fills remaining as "Present"
        adapter = new StudentAdapter(this, studentList, savedStatus, this::updateCountText);
        studentListView.setAdapter(adapter);
    }

    private void updateCountText() {
        int present = 0;
        int absent = 0;

        for (String status : adapter.getAttendanceStatus().values()) {
            if (status.startsWith("Absent")) {
                absent++;
            } else {
                present++;
            }
        }

        countText.setText("Present: " + present + " | Absent: " + absent);
    }
}