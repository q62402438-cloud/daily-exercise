package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.model.ExerciseRecord;

import java.util.List;

public class ExerciseRecordAdapter extends RecyclerView.Adapter<ExerciseRecordAdapter.ViewHolder> {

    private List<ExerciseRecord> records;
    private OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(ExerciseRecord record, int position);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public ExerciseRecordAdapter(List<ExerciseRecord> records) {
        this.records = records;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseRecord record = records.get(position);
        holder.tvDate.setText(record.getSportsDate());

        String displayName = getRecordDisplayName(record);
        holder.tvSportName.setText(displayName);

        String startTime = record.getStartTime();
        String endTime = record.getEndTime();
        if (startTime != null && startTime.length() >= 11) {
            holder.tvTime.setText(startTime.substring(11, 16) + " - " +
                    (endTime != null && endTime.length() >= 11 ? endTime.substring(11, 16) : ""));
        }

        holder.tvDuration.setText(record.getExerciseDuration() + " 分钟");
        holder.tvCalorie.setText(record.getCalorie() + " kcal");

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(record, position);
            }
        });
    }

    private String getRecordDisplayName(ExerciseRecord record) {
        String name = record.getSportName();
        if (name == null || name.isEmpty()) {
            name = "未知";
        }

        Integer recordType = record.getRecordType();
        if (recordType == null) {
            return name;
        }

        if (recordType == 1) {
            return "[计划] " + name;
        } else {
            return "[项目] " + name;
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public void updateData(List<ExerciseRecord> newRecords) {
        this.records = newRecords;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvSportName;
        TextView tvTime;
        TextView tvDuration;
        TextView tvCalorie;
        TextView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_record_date);
            tvSportName = itemView.findViewById(R.id.tv_record_sport_name);
            tvTime = itemView.findViewById(R.id.tv_record_time);
            tvDuration = itemView.findViewById(R.id.tv_record_duration);
            tvCalorie = itemView.findViewById(R.id.tv_record_calorie);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
