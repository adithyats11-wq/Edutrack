package com.example.edutrack;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    CardView cardYear1, cardYear2, cardYear3;
    LinearLayout btnProfile, btnExit;
    TextView teacherNameText;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Make sure your XML file is activity_main.xml

        // Initialize views by ID
        cardYear1 = findViewById(R.id.cardYear1);
        cardYear2 = findViewById(R.id.cardYear2);
        cardYear3 = findViewById(R.id.cardYear3);
        btnProfile = findViewById(R.id.btnProfile);
        btnExit = findViewById(R.id.btnExit);
        teacherNameText = findViewById(R.id.teacherNameText);

        dbHelper = new DatabaseHelper(this);

        // Load teacher name initially
        loadTeacherName();

        // ==========================
        // Year Card Clicks
        // ==========================
        cardYear1.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, YearActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_up_fade_in, R.anim.fade_out);
        });

        cardYear2.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SecondYearActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_up_fade_in, R.anim.fade_out);
        });

        cardYear3.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ThirdYearActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_up_fade_in, R.anim.fade_out);
        });

        // ==========================
        // Bottom Nav Buttons
        // ==========================
        btnProfile.setOnClickListener(v -> {
            Intent profileIntent = new Intent(MainActivity.this, TeacherProfileActivity.class);
            startActivity(profileIntent);
        });

        btnExit.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Exiting Edutrack...", Toast.LENGTH_SHORT).show();
            finishAffinity(); // Close all activities and exit the app
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh teacher name whenever coming back from Profile screen
        loadTeacherName();
    }

    private void loadTeacherName() {
        Cursor cursor = dbHelper.getTeacherProfile();
        if (cursor.moveToFirst()) {
            String teacherName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            teacherNameText.setText(teacherName + "\nWELCOME BACK");
        } else {
            teacherNameText.setText("WELCOME BACK");
        }
        cursor.close();
    }
}
