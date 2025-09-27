package com.example.edutrack;
import android.app.AlertDialog;
import android.content.Context;
import android.view.*;
import android.widget.*;

import java.util.HashMap;
import java.util.List;

public class StudentAdapter extends BaseAdapter {

    private final Context context; //inflating layouts
    private final List<Student> studentList;
    private final HashMap<Integer, String> attendanceStatus;
    private final Runnable updateCounterCallback;

    public StudentAdapter(Context context, List<Student> studentList,
                          HashMap<Integer, String> savedStatus,
                          Runnable updateCounterCallback) {
        this.context = context;
        this.studentList = studentList;
        this.updateCounterCallback = updateCounterCallback;

        attendanceStatus = new HashMap<>();
        for (Student student : studentList) {
            int roll = student.getRollNumber();
            attendanceStatus.put(roll, savedStatus.getOrDefault(roll, "Present"));
        }
    }


    public HashMap<Integer, String> getAttendanceStatus() {
        return attendanceStatus;
    } //Returns current attendance status

    @Override
    public int getCount() {
        return studentList.size();
    }

    @Override
    public Object getItem(int position) {
        return studentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return studentList.get(position).getRollNumber();
    }


    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.student_item, parent, false);

        TextView nameText = convertView.findViewById(R.id.studentName);
        ImageView presentIcon = convertView.findViewById(R.id.presentIcon);

        Student student = studentList.get(pos);
        int roll = student.getRollNumber();
        String name = student.getName();

        nameText.setText(roll + " - " + name);

        String status = attendanceStatus.get(roll);
        if (status != null && status.startsWith("Absent")) {
            presentIcon.setImageResource(R.drawable.crosssx);
        } else {
            presentIcon.setImageResource(R.drawable.checkex);
        }

        presentIcon.setOnClickListener(v -> {
            if (status != null && status.startsWith("Absent")) {
                attendanceStatus.put(roll, "Present");
                presentIcon.setImageResource(R.drawable.checkex);
            } else {
                showLeaveReasonDialog(roll, name, presentIcon);
            }
            updateCounterCallback.run();
        });

        return convertView;
    }

    private void showLeaveReasonDialog(int roll, String name, ImageView icon) {
        EditText input = new EditText(context);
        input.setHint("Enter leave reason");

        new AlertDialog.Builder(context)
                .setTitle("Leave Reason")
                .setMessage("Why is " + name + " absent?")
                .setView(input)
                .setPositiveButton("OK", (dialog, which) -> {
                    String reason = input.getText().toString().trim();
                    if (reason.isEmpty()) reason = "No reason";
                    attendanceStatus.put(roll, "Absent: " + reason);
                    icon.setImageResource(R.drawable.crosssx);
                    updateCounterCallback.run();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

}
