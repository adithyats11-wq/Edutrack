package com.example.edutrack;

public class LeaveRequest {
    private int rollNumber;
    private String studentName;
    private String date;
    private String status;
    private String reason; // ✅ add this

    // Constructor for full leave record
    public LeaveRequest(int rollNumber, String studentName, String date, String status, String reason) {
        this.rollNumber = rollNumber;
        this.studentName = studentName;
        this.date = date;
        this.status = status;
        this.reason = reason;
    }

    // Old constructor (for backward compatibility)
    public LeaveRequest(int rollNumber, String studentName, String date, String status) {
        this(rollNumber, studentName, date, status, "");
    }

    public int getRollNumber() { return rollNumber; }
    public String getStudentName() { return studentName; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
    public String getReason() { return reason; } // ✅ getter
}

