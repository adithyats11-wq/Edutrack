package com.example.edutrack;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ViewLeaveReasonActivity extends AppCompatActivity {

    RecyclerView leaveRecyclerView;
    DatabaseHelper dbHelper;
    LeaveReasonAdapter adapter;
    ImageButton backBtn, searchButton;
    EditText searchDateEditText;
    List<LeaveRequest> leaveRequests;

    int selectedYear; // ← store the year passed from intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_leave_reason);

        leaveRecyclerView = findViewById(R.id.leaveRecyclerView);
        searchDateEditText = findViewById(R.id.searchDateEditText);
        searchButton = findViewById(R.id.searchButton);
        backBtn = findViewById(R.id.backBtn);
        dbHelper = new DatabaseHelper(this);

        // ✅ Get the year from Intent
        selectedYear = getIntent().getIntExtra("year", 1);

        // Load leave reasons for this year
        loadLeaveReasons();

        backBtn.setOnClickListener(v -> finish());

        searchButton.setOnClickListener(v -> {
            String dateInput = searchDateEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(dateInput)) {
                List<LeaveRequest> filteredList =
                        dbHelper.getLeaveRequestsByDateAndYear(dateInput, selectedYear);

                adapter.updateList(filteredList);
                if (filteredList.isEmpty()) {
                    Toast.makeText(this, "No absentees in year " + selectedYear + " on " + dateInput, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter a date", Toast.LENGTH_SHORT).show();
            }
        });

        searchDateEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                String dateInput = searchDateEditText.getText().toString().trim();

                if (!TextUtils.isEmpty(dateInput)) {
                    List<LeaveRequest> filteredList =
                            dbHelper.getLeaveRequestsByDateAndYear(dateInput, selectedYear);

                    if (filteredList.isEmpty()) {
                        Toast.makeText(this, "No absentees in year " + selectedYear + " on " + dateInput, Toast.LENGTH_SHORT).show();
                    }

                    adapter.updateList(filteredList);
                } else {
                    Toast.makeText(this, "Please enter a date", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });
    }

    private void loadLeaveReasons() {
        leaveRequests = dbHelper.getLeaveRequestsByYear(selectedYear);
        adapter = new LeaveReasonAdapter(this, leaveRequests, dbHelper);
        leaveRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        leaveRecyclerView.setAdapter(adapter);
    }
}
