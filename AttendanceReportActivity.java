package com.example.edutrack;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

public class AttendanceReportActivity extends AppCompatActivity {

    private PieChart pieChart;
    private BarChart barChart;
    private Spinner studentSpinner;
    private TextView tvPresentCount, tvLeaveCount;
    private ImageButton backBtn, searchButton;
    private EditText searchDateEditText;
    private DatabaseHelper dbHelper;

    private String[] studentNames;
    private int selectedRoll = -1;
    private String selectedMonthYear = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_report);

        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);
        tvPresentCount = findViewById(R.id.tvPresent);
        tvLeaveCount = findViewById(R.id.tvLeave);
        backBtn = findViewById(R.id.backBtn);
        studentSpinner = findViewById(R.id.studentSpinner);
        searchDateEditText = findViewById(R.id.searchDateEditText);
        searchButton = findViewById(R.id.searchButton);
        dbHelper = new DatabaseHelper(this);

        int year = getIntent().getIntExtra("year", 1);
        loadStudentList(year);

        backBtn.setOnClickListener(v -> finish());
        searchButton.setOnClickListener(v -> generateReport());

        setupBarChart(); // configure BarChart once
    }

    private void loadStudentList(int year) {
        if (year == 1) {
            studentNames = new String[]{
                    "Revathy", "Amritha", "Trina", "Fidha", "Adithya", "Aswini", "Anuja", "Devika",
                    "Gopika", "Jeena", "Anu", "Anjali", "Sneha", "Judit", "Anusa", "Shreya", "Nandha",
                    "Tenu", "Niveditha", "Angelina", "Muhsina"
            };
        } else if (year == 2) {
            studentNames = new String[]{
                    "Rahul", "Vishnu", "Neha", "Pooja", "Kiran", "Varun", "Akhil", "Meera",
                    "Anand", "Ritika", "Suresh", "Arya", "Deepak", "Parvathi", "Gautham", "Saranya"
            };
        } else {
            studentNames = new String[]{
                    "Arjun", "Harsha", "Ananya", "Vivek", "Lekha", "Mithun", "Swathi", "Rohit",
                    "Priya", "Santhosh", "Divya", "Hari", "Krishna", "Manju", "Nirmala", "Akash"
            };
        }

        List<String> studentDisplayList = new ArrayList<>();
        for (int i = 0; i < studentNames.length; i++) {
            studentDisplayList.add((i + 1) + " - " + studentNames[i]);
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, studentDisplayList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studentSpinner.setAdapter(spinnerAdapter);
    }

    private void generateReport() {
        String monthYearRaw = searchDateEditText.getText().toString().trim(); // mm-yyyy
        if (!monthYearRaw.matches("\\d{2}-\\d{4}")) {
            Toast.makeText(this, "Enter month in mm-yyyy format", Toast.LENGTH_SHORT).show();
            return;
        }

        String spinnerVal = studentSpinner.getSelectedItem().toString();
        int rollNo = Integer.parseInt(spinnerVal.split(" - ")[0]);
        selectedRoll = rollNo;
        selectedMonthYear = monthYearRaw;

        try {
            int workingDays = dbHelper.getWorkingDaysForMonth(monthYearRaw);
            int absentDays = dbHelper.getAbsentCountForMonth(rollNo, monthYearRaw);
            int presentDays = workingDays - absentDays;
            if (presentDays < 0) presentDays = 0;

            // ❌ Removed holiday calculation

            tvPresentCount.setText(presentDays + "\nPresent");
            tvLeaveCount.setText(absentDays + "\nAbsent");

            updatePieChart(presentDays, absentDays, workingDays);
            updateBarChart(rollNo, monthYearRaw);

        } catch (Exception e) {
            Log.e("AttendanceReport", "Error generating report", e);
            Toast.makeText(this, "Error generating report", Toast.LENGTH_SHORT).show();
        }
    }

    public int getTotalDaysInMonth(String monthYearRaw) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy", Locale.getDefault());
            Date date = sdf.parse(monthYearRaw);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        } catch (Exception e) {
            e.printStackTrace();
            return 30;
        }
    }

    private void updatePieChart(int present, int absent, int workingDays) {
        List<PieEntry> entries = new ArrayList<>();
        if (workingDays > 0) entries.add(new PieEntry(workingDays, "Working Days"));
        if (present > 0) entries.add(new PieEntry(present, "Present"));
        if (absent > 0) entries.add(new PieEntry(absent, "Absent"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.BLACK);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(45f);
        pieChart.setTransparentCircleRadius(50f);
        pieChart.setCenterText("Monthly\nAttendance");
        pieChart.setCenterTextSize(16f);

        Description d = new Description();
        d.setText("");
        pieChart.setDescription(d);

        pieChart.animateY(800);
        pieChart.invalidate();
    }

    // ✅ Bar Chart setup
    private void setupBarChart() {
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        barChart.getAxisRight().setEnabled(false);

        Description d = new Description();
        d.setText("");
        barChart.setDescription(d);

        barChart.getAxisLeft().setAxisMinimum(0f);
    }

    // ✅ Bar Chart update
    private void updateBarChart(int rollNo, String monthYear) {
        List<Integer> weeklyPresent = dbHelper.getWeeklyPresentDays(rollNo, monthYear);
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        final String[] labels;

        if (weeklyPresent.isEmpty()) {
            barEntries.add(new BarEntry(0, 0));
            labels = new String[]{"Week 1"};
        } else {
            labels = new String[weeklyPresent.size()];
            for (int i = 0; i < weeklyPresent.size(); i++) {
                barEntries.add(new BarEntry(i, weeklyPresent.get(i)));
                labels[i] = "Week " + (i + 1);
            }
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Weekly Present Days");
        barDataSet.setColor(0xFF1976D2);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(12f);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.6f);

        barChart.setData(barData);
        barChart.setFitBars(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setLabelCount(labels.length);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int i = (int) value;
                if (i >= 0 && i < labels.length) return labels[i];
                return "";
            }
        });

        barChart.animateY(800);
        barChart.invalidate();
    }
}
