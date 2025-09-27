package com.example.edutrack;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class TeacherProfileActivity extends AppCompatActivity {

    EditText usernameEdit, emailEdit, phoneEdit, classEdit, subjectEdit, qualificationEdit;
    Button editButton, finalDoneButton;
    ImageButton backBtn;
    boolean isEditing = false;

    DatabaseHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        // Initialize views
        usernameEdit = findViewById(R.id.usernameEdit);
        emailEdit = findViewById(R.id.emailEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        classEdit = findViewById(R.id.classEdit);
        subjectEdit = findViewById(R.id.subjectEdit);
        qualificationEdit = findViewById(R.id.qualificationEdit);
        editButton = findViewById(R.id.editButton);
        backBtn = findViewById(R.id.backBtn);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Load saved profile from database
        loadProfileFromDatabase();

        // Back button click
        backBtn.setOnClickListener(v -> finish());

        // Edit button toggle
        editButton.setOnClickListener(v -> {
            isEditing = !isEditing;
            toggleEditMode(isEditing);
            editButton.setText(isEditing ? "Save" : "Edit");

            if (!isEditing) {
                saveProfileToDatabase();
                Toast.makeText(TeacherProfileActivity.this, "Profile Saved", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Enable or disable editing
    private void toggleEditMode(boolean enabled) {
        usernameEdit.setEnabled(enabled);
        emailEdit.setEnabled(enabled);
        phoneEdit.setEnabled(enabled);
        classEdit.setEnabled(enabled);
        subjectEdit.setEnabled(enabled);
        qualificationEdit.setEnabled(enabled);

        int bg = enabled ? android.R.drawable.edit_text : android.R.color.transparent;

        usernameEdit.setBackgroundResource(bg);
        emailEdit.setBackgroundResource(bg);
        phoneEdit.setBackgroundResource(bg);
        classEdit.setBackgroundResource(bg);
        subjectEdit.setBackgroundResource(bg);
        qualificationEdit.setBackgroundResource(bg);
    }

    // Load profile from SQLite
    private void loadProfileFromDatabase() {
        Cursor cursor = dbHelper.getTeacherProfile();
        if (cursor.moveToFirst()) {
            usernameEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            emailEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            phoneEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            classEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow("class")));
            subjectEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow("subject")));
            qualificationEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow("qualification")));
        }
        cursor.close();
        toggleEditMode(false);
    }

    // Save profile to SQLiteg
    private void saveProfileToDatabase() {
        String name = usernameEdit.getText().toString();
        String email = emailEdit.getText().toString();
        String phone = phoneEdit.getText().toString();
        String className = classEdit.getText().toString();
        String subject = subjectEdit.getText().toString();
        String qualification = qualificationEdit.getText().toString();

        dbHelper.saveTeacherProfile(name, email, phone, className, subject, qualification);
    }
}
