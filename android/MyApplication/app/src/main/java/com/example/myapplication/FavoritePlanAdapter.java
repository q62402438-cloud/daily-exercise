package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoritePlanAdapter extends RecyclerView.Adapter<FavoritePlanAdapter.FavoritePlanViewHolder> {

    private List<Plan> planList;
    private OnFavoriteActionListener listener;

    public interface OnFavoriteActionListener {
        void onPlanClick(Plan plan);
        void onUnfavoriteClick(Plan plan, int position);
    }

    public FavoritePlanAdapter(List<Plan> planList, OnFavoriteActionListener listener) {
        this.planList = planList;
        this.listener = listener;
    }

    public void setPlanList(List<Plan> planList) {
        this.planList = planList;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < planList.size()) {
            planList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public FavoritePlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_plan, parent, false);
        return new FavoritePlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritePlanViewHolder holder, int position) {
        Plan plan = planList.get(position);
        holder.bind(plan);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlanClick(plan);
            }
        });

        holder.btnUnfavorite.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUnfavoriteClick(plan, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return planList != null ? planList.size() : 0;
    }

    public static class FavoritePlanViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlanName, tvSportType, tvDateRange, tvDailyTarget;
        ImageButton btnUnfavorite;

        public FavoritePlanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlanName = itemView.findViewById(R.id.tv_plan_name);
            tvSportType = itemView.findViewById(R.id.tv_sport_type);
            tvDateRange = itemView.findViewById(R.id.tv_date_range);
            tvDailyTarget = itemView.findViewById(R.id.tv_daily_target);
            btnUnfavorite = itemView.findViewById(R.id.btn_unfavorite);
        }

        public void bind(Plan plan) {
            if (tvPlanName != null) tvPlanName.setText(plan.getPlanName());
            if (tvSportType != null) tvSportType.setText(plan.getSportType());
            if (tvDateRange != null) {
                tvDateRange.setText(plan.getStartDate() + " - " + plan.getEndDate());
            }
            if (tvDailyTarget != null) {
                tvDailyTarget.setText("每日目标: " + plan.getDailyExercise() + "分钟");
            }
        }
    }
}
