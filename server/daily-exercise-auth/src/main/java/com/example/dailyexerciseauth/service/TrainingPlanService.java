package com.example.dailyexerciseauth.service;

import com.example.dailyexerciseauth.entity.TrainingPlan;
import java.util.List;

public interface TrainingPlanService {
    List<TrainingPlan> getPlansByUserId(Integer userID);
    TrainingPlan getPlanById(Integer planID);
    boolean createPlan(TrainingPlan plan);
    boolean updatePlan(TrainingPlan plan);
    boolean deletePlan(Integer planID);
    List<TrainingPlan> getAllPublicPlans();
    List<TrainingPlan> getPendingPlans();
    boolean submitAudit(Integer planID);
    boolean auditPass(Integer planID);
    boolean auditReject(Integer planID);
    boolean startPlan(Integer planID);
    boolean completePlan(Integer planID);
    boolean updateProgress(Integer planID, Float percentage);
    List<TrainingPlan> getPublishedPlans();
}
