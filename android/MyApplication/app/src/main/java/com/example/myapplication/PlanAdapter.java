package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    private List<Plan> planList;
    private OnPlanClickListener listener;

    public interface OnPlanClickListener {
        void onPlanClick(Plan plan);
    }

    public PlanAdapter(List<Plan> planList, OnPlanClickListener listener) {
        this.planList = planList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        Plan plan = planList.get(position);
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

    public void setPlanList(List<Plan> planList) {
        this.planList = planList;
        notifyDataSetChanged();
    }

    static class PlanViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPlanName;
        private TextView tvSportType;
        private TextView tvDateRange;
        private TextView tvDailyTarget;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlanName = itemView.findViewById(R.id.tv_plan_name);
            tvSportType = itemView.findViewById(R.id.tv_sport_type);
            tvDateRange = itemView.findViewById(R.id.tv_date_range);
            tvDailyTarget = itemView.findViewById(R.id.tv_daily_target);
        }

        public void bind(Plan plan) {
            if (tvPlanName != null) tvPlanName.setText(plan.getPlanName());
            if (tvSportType != null) tvSportType.setText(plan.getSportType());
            if (tvDateRange != null) {
                tvDateRange.setText(plan.getStartDate() + " - " + plan.getEndDate());
            }
            if (tvDailyTarget != null) {
                tvDailyTarget.setText("每日目标: " + plan.getDailyExercise() + "km / " + plan.getDailyCalorie() + "kcal");
            }
        }
    }
}