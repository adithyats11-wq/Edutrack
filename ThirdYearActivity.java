package com.example.edutrack;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ThirdYearActivity extends AppCompatActivity {

    ImageView profile_image;
    ImageButton backBtn;
    CardView cardMarkAttendance, cardAttendanceReport, cardViewLeave;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_year);

        profile_image = findViewById(R.id.profile_image);
        cardMarkAttendance = findViewById(R.id.cardMarkAttendance);
        cardAttendanceReport = findViewById(R.id.cardAttendanceReport);
        cardViewLeave = findViewById(R.id.cardViewLeaveReason);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> finish());

        profile_image.setOnClickListener(v -> {
            startActivity(new Intent(this,TeacherProfileActivity.class));
        });

        // Mark Attendance → Pass year = 3
        cardMarkAttendance.setOnClickListener(v -> {
            Intent intent = new Intent(this, MarkAttendanceActivity.class);
            intent.putExtra("year", 3); // 2nd year
            startActivity(intent);
        });

        // Attendance Report → Pass year = 3
        cardAttendanceReport.setOnClickListener(v -> {
            Intent intent = new Intent(this, AttendanceReportActivity.class);
            intent.putExtra("year", 3); // 2 = second-year students
            startActivity(intent);
        });

        // View Leave Reasons
        cardViewLeave.setOnClickListener(v -> {
            Intent intent = new Intent(this, ViewLeaveReasonActivity.class);
            intent.putExtra("year", 3); // 2 = second-year students
            startActivity(intent);
        });
    }
}