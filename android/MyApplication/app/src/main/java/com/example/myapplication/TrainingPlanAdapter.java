package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.model.TrainingPlan;
import java.util.List;

public class TrainingPlanAdapter extends RecyclerView.Adapter<TrainingPlanAdapter.TrainingPlanViewHolder> {

    private List<TrainingPlan> planList;
    private OnPlanClickListener listener;

    public interface OnPlanClickListener {
        void onPlanClick(TrainingPlan plan);
    }

    public TrainingPlanAdapter(List<TrainingPlan> planList, OnPlanClickListener listener) {
        this.planList = planList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TrainingPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plan, parent, false);
        return new TrainingPlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingPlanViewHolder holder, int position) {
        TrainingPlan plan = planList.get(position);
        holder.bind(plan);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlanClick(plan);
            }
        });
    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    public void setPlanList(List<TrainingPlan> planList) {
        this.planList = planList;
        notifyDataSetChanged();
    }

    static class TrainingPlanViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPlanName;
        private TextView tvSportType;
        private TextView tvDateRange;
        private TextView tvDailyTarget;

        public TrainingPlanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlanName = itemView.findViewById(R.id.tv_plan_name);
            tvSportType = itemView.findViewById(R.id.tv_sport_type);
            tvDateRange = itemView.findViewById(R.id.tv_date_range);
            tvDailyTarget = itemView.findViewById(R.id.tv_daily_target);
        }

        public void bind(TrainingPlan plan) {
            if (tvPlanName != null) tvPlanName.setText(plan.getPlanName() != null ? plan.getPlanName() : "未命名计划");
            if (tvSportType != null) tvSportType.setText(plan.getSportName() != null ? plan.getSportName() : "未指定");
            if (tvDateRange != null) {
                String startDate = plan.getStartTime() != null ? plan.getStartTime() : "未知";
                String endDate = plan.getEndTime() != null ? plan.getEndTime() : "未知";
                tvDateRange.setText(startDate + " - " + endDate);
            }
            if (tvDailyTarget != null) {
                String exerciseAmount = plan.getExerciseAmount() != null ? plan.getExerciseAmount() : "未设置";
                tvDailyTarget.setText("每日运动时长: " + exerciseAmount + "分钟");
            }
        }
    }
}
