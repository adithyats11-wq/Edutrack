package com.example.edutrack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "edutrack.db";
    private static final int DATABASE_VERSION = 4;

    // ==========================
    // Attendance Table
    // ==========================
    public static final String TABLE_ATTENDANCE = "attendance";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_ROLL = "roll";
    public static final String COL_YEAR = "year";
    public static final String COL_STATUS = "status";
    public static final String COL_DATE = "date"; // format: dd-MM-yyyy
    public static final String COL_REASON = "reason";

    // ==========================
    // Teacher Profile Table
    // ==========================
    public static final String TABLE_TEACHER = "teacher";
    public static final String COL_TID = "tid";
    public static final String COL_EMAIL = "email";
    public static final String COL_PHONE = "phone";
    public static final String COL_CLASS = "class";
    public static final String COL_SUBJECT = "subject";
    public static final String COL_QUALIFICATION = "qualification";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Attendance Table
        String createAttendanceTable = "CREATE TABLE " + TABLE_ATTENDANCE + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_ROLL + " INTEGER, " +
                COL_YEAR + " INTEGER, " +
                COL_STATUS + " TEXT, " +
                COL_DATE + " TEXT, " +
                COL_REASON + " TEXT)";
        db.execSQL(createAttendanceTable);

        // Teacher Table
        String createTeacherTable = "CREATE TABLE " + TABLE_TEACHER + " (" +
                COL_TID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_PHONE + " TEXT, " +
                COL_CLASS + " TEXT, " +
                COL_SUBJECT + " TEXT, " +
                COL_QUALIFICATION + " TEXT)";
        db.execSQL(createTeacherTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEACHER);
        onCreate(db);
    }

    // ==========================
    // Attendance Operations
    // ==========================
    public void saveAttendance(int roll, String name, int year, String status, String date, String reason) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ROLL, roll);
        values.put(COL_NAME, name);
        values.put(COL_YEAR, year);
        values.put(COL_STATUS, status);
        values.put(COL_DATE, date);
        values.put(COL_REASON, reason);
        db.insert(TABLE_ATTENDANCE, null, values);
    }

    public void deleteAttendanceForDate(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ATTENDANCE, COL_DATE + "=?", new String[]{date});
    }

    public void deleteAttendanceForDateAndYear(String date, int year) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ATTENDANCE, COL_DATE + "=? AND " + COL_YEAR + "=?", new String[]{date, String.valueOf(year)});
    }

    public Cursor getAttendanceByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ATTENDANCE + " WHERE " + COL_DATE + "=?", new String[]{date});
    }

    public Cursor getAttendanceByDateAndYear(String date, int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ATTENDANCE + " WHERE " + COL_DATE + "=? AND " + COL_YEAR + "=?",
                new String[]{date, String.valueOf(year)});
    }

    public Cursor getAttendanceByRollAndDate(int rollNo, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ATTENDANCE + " WHERE " + COL_ROLL + "=? AND " + COL_DATE + "=?",
                new String[]{String.valueOf(rollNo), date});
    }

    // ==========================
    // Leave Requests
    // ==========================
    public List<LeaveRequest> getAllLeaveRequests() {
        List<LeaveRequest> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT roll, name, date, status, reason FROM " + TABLE_ATTENDANCE +
                " WHERE " + COL_STATUS + " LIKE 'Absent%'", null);

        if (cursor.moveToFirst()) {
            do {
                int roll = cursor.getInt(0);
                String name = cursor.getString(1);
                String date = cursor.getString(2);
                String status = cursor.getString(3);
                String reason = cursor.getString(4);
                list.add(new LeaveRequest(roll, name, date, status, reason));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<LeaveRequest> getLeaveRequestsByDate(String selectedDate) {
        List<LeaveRequest> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT roll, name, date, status, reason FROM " + TABLE_ATTENDANCE +
                " WHERE " + COL_DATE + "=? AND " + COL_STATUS + " LIKE 'Absent%'", new String[]{selectedDate});

        if (cursor.moveToFirst()) {
            do {
                int roll = cursor.getInt(0);
                String name = cursor.getString(1);
                String date = cursor.getString(2);
                String status = cursor.getString(3);
                String reason = cursor.getString(4);
                list.add(new LeaveRequest(roll, name, date, status, reason));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }


    public List<LeaveRequest> getLeaveRequestsByYear(int year) {
        List<LeaveRequest> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT roll, name, date, status, reason FROM " + TABLE_ATTENDANCE +
                " WHERE " + COL_YEAR + "=? AND " + COL_STATUS + " LIKE 'Absent%'", new String[]{String.valueOf(year)});

        if (cursor.moveToFirst()) {
            do {
                int roll = cursor.getInt(0);
                String name = cursor.getString(1);
                String date = cursor.getString(2);
                String status = cursor.getString(3);
                String reason = cursor.getString(4);
                list.add(new LeaveRequest(roll, name, date, status, reason));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<LeaveRequest> getLeaveRequestsByDateAndYear(String date, int year) {
        List<LeaveRequest> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT roll, name, date, status, reason FROM " + TABLE_ATTENDANCE +
                        " WHERE " + COL_DATE + "=? AND " + COL_YEAR + "=? AND " + COL_STATUS + " LIKE 'Absent%'",
                new String[]{date, String.valueOf(year)});

        if (cursor.moveToFirst()) {
            do {
                int roll = cursor.getInt(0);
                String name = cursor.getString(1);
                String dateVal = cursor.getString(2);
                String status = cursor.getString(3);
                String reason = cursor.getString(4);
                list.add(new LeaveRequest(roll, name, dateVal, status, reason));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void deleteLeaveRequest(String studentName, String date, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ATTENDANCE, COL_NAME + "=? AND " + COL_DATE + "=? AND " + COL_STATUS + "=?",
                new String[]{studentName, date, status});
    }

    // ==========================
    // Attendance Counts & Reports
    // ==========================
    public int getAbsentCountForMonth(int roll, String monthYear) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT COUNT(DISTINCT " + COL_DATE + ") FROM " + TABLE_ATTENDANCE +
                        " WHERE " + COL_ROLL + "=? AND " + COL_STATUS + " = ? AND " + COL_DATE + " LIKE ?",
                new String[]{String.valueOf(roll), "Absent", "%-" + monthYear});

        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }


    public int getWorkingDaysForMonth(String monthYear) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(DISTINCT " + COL_DATE + ") FROM " + TABLE_ATTENDANCE +
                " WHERE " + COL_DATE + " LIKE ?", new String[]{"%-" + monthYear});
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    public int getHolidayCountForMonth(String monthYear) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(DISTINCT " + COL_DATE + ") FROM " + TABLE_ATTENDANCE +
                        " WHERE " + COL_STATUS + "='Holiday' AND " + COL_DATE + " LIKE ?",
                new String[]{"%-" + monthYear});
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public int getCountByStatus(String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ATTENDANCE +
                " WHERE " + COL_STATUS + "=?", new String[]{status});
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public int getTotalDaysInMonth(String monthYear) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + COL_DATE + " FROM " + TABLE_ATTENDANCE +
                " WHERE " + COL_DATE + " LIKE ?", new String[]{"%-" + monthYear});
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    // ==========================
    // Weekly Calculations
    // ==========================
    public List<Integer> getWeeklyPresentDays(int rollNo, String monthYear) {
        List<Integer> weeklyPresent = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0));
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy", Locale.getDefault());
            Date date = sdf.parse(monthYear);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int totalDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            for (int day = 1; day <= totalDays; day++) {
                calendar.set(Calendar.DAY_OF_MONTH, day);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) continue;

                String currentDate = String.format(Locale.getDefault(), "%02d-%02d-%04d",
                        day, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));

                int absentCount = 0;
                Cursor cursor = this.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + TABLE_ATTENDANCE +
                                " WHERE " + COL_ROLL + "=? AND " + COL_DATE + "=? AND " + COL_STATUS + "='Absent'",
                        new String[]{String.valueOf(rollNo), currentDate});
                if (cursor.moveToFirst()) absentCount = cursor.getInt(0);
                cursor.close();

                if (absentCount == 0) {
                    int weekIndex = (day - 1) / 7;
                    if (weekIndex < weeklyPresent.size()) {
                        weeklyPresent.set(weekIndex, weeklyPresent.get(weekIndex) + 1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weeklyPresent;
    }

    // ==========================
    // Teacher Profile
    // ==========================
    public void saveTeacherProfile(String name, String email, String phone, String className, String subject, String qualification) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TEACHER, null, null);
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_EMAIL, email);
        values.put(COL_PHONE, phone);
        values.put(COL_CLASS, className);
        values.put(COL_SUBJECT, subject);
        values.put(COL_QUALIFICATION, qualification);
        db.insert(TABLE_TEACHER, null, values);
    }

    public Cursor getTeacherProfile() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TEACHER + " LIMIT 1", null);
    }
}
