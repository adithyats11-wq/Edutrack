package com.example.edutrack;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LeaveReasonAdapter extends RecyclerView.Adapter<LeaveReasonAdapter.ViewHolder> {

    private Context context;
    private List<LeaveRequest> leaveList;
    private DatabaseHelper dbHelper;


    public LeaveReasonAdapter(Context context, List<LeaveRequest> leaveList, DatabaseHelper dbHelper) {
        this.context = context;
        this.leaveList = leaveList;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public LeaveReasonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_leave_reason, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaveReasonAdapter.ViewHolder holder, int position) {
        LeaveRequest item = leaveList.get(position);

        holder.roll.setText("Roll no: " + item.getRollNumber());
        holder.name.setText(item.getStudentName());
        holder.date.setText("Date: " + item.getDate());

        // ✅ Display proper reason
        if (item.getReason() != null && !item.getReason().isEmpty()) {
            holder.reason.setText("Reason: " + item.getReason());
        } else {
            holder.reason.setText("Reason: -");
        }

        // ✅ Delete reason
        holder.deleteBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Leave")
                    .setMessage("Delete leave for " + item.getStudentName() + " on " + item.getDate() + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        dbHelper.deleteLeaveRequest(item.getStudentName(), item.getDate(), "Absent");
                        leaveList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Leave deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

        @Override
    public int getItemCount() {
        return leaveList.size();
    }

    public void updateList(List<LeaveRequest> newList) {
        leaveList.clear();
        leaveList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView roll, name, date, reason;
        ImageButton deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            roll = itemView.findViewById(R.id.tvRollNumber);
            name = itemView.findViewById(R.id.tvStudentName);
            date = itemView.findViewById(R.id.tvLeaveDate);
            reason = itemView.findViewById(R.id.tvLeaveReason);
            deleteBtn = itemView.findViewById(R.id.btnDelete);
        }
    }
}
