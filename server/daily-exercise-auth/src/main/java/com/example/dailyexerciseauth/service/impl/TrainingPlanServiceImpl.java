package com.example.dailyexerciseauth.service.impl;

import com.example.dailyexerciseauth.entity.TrainingPlan;
import com.example.dailyexerciseauth.mapper.TrainingPlanMapper;
import com.example.dailyexerciseauth.service.TrainingPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainingPlanServiceImpl implements TrainingPlanService {

    @Autowired
    private TrainingPlanMapper trainingPlanMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TrainingPlan> getPlansByUserId(Integer userID) {
        return trainingPlanMapper.getPlansByUserId(userID);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingPlan getPlanById(Integer planID) {
        return trainingPlanMapper.getPlanById(planID);
    }

    @Override
    @Transactional
    public boolean createPlan(TrainingPlan plan) {
        if (plan.getPercentage() == null) {
            plan.setPercentage(0f);
        }
        if (plan.getPlanType() == null) {
            plan.setPlanType(0);
        }
        return trainingPlanMapper.insertPlan(plan) > 0;
    }

    @Override
    @Transactional
    public boolean updatePlan(TrainingPlan plan) {
        return trainingPlanMapper.updatePlan(plan) > 0;
    }

    @Override
    @Transactional
    public boolean deletePlan(Integer planID) {
        return trainingPlanMapper.deletePlan(planID) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingPlan> getAllPublicPlans() {
        return trainingPlanMapper.getAllPublicPlans();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingPlan> getPendingPlans() {
        return trainingPlanMapper.getPendingPlans();
    }

    @Override
    @Transactional
    public boolean submitAudit(Integer planID) {
        TrainingPlan plan = trainingPlanMapper.getPlanById(planID);
        if (plan == null) return false;
        Integer currentType = plan.getPlanType() != null ? plan.getPlanType() : 0;
        Integer tens = (currentType / 10) * 10;
        Integer newType = tens + 1;
        plan.setPlanType(newType);
        return trainingPlanMapper.updatePlanType(planID, newType) > 0;
    }

    @Override
    @Transactional
    public boolean auditPass(Integer planID) {
        TrainingPlan plan = trainingPlanMapper.getPlanById(planID);
        if (plan == null) return false;
        Integer currentType = plan.getPlanType() != null ? plan.getPlanType() : 0;
        Integer tens = (currentType / 10) * 10;
        Integer newType = tens + 2;
        return trainingPlanMapper.updatePlanType(planID, newType) > 0;
    }

    @Override
    @Transactional
    public boolean auditReject(Integer planID) {
        TrainingPlan plan = trainingPlanMapper.getPlanById(planID);
        if (plan == null) return false;
        Integer currentType = plan.getPlanType() != null ? plan.getPlanType() : 0;
        Integer tens = (currentType / 10) * 10;
        Integer newType = tens + 3;
        return trainingPlanMapper.updatePlanType(planID, newType) > 0;
    }

    @Override
    @Transactional
    public boolean startPlan(Integer planID) {
        TrainingPlan plan = trainingPlanMapper.getPlanById(planID);
        if (plan == null) return false;
        Integer currentType = plan.getPlanType() != null ? plan.getPlanType() : 0;
        Integer units = currentType % 10;
        Integer newType = 10 + units;
        return trainingPlanMapper.updatePlanType(planID, newType) > 0;
    }

    @Override
    @Transactional
    public boolean completePlan(Integer planID) {
        TrainingPlan plan = trainingPlanMapper.getPlanById(planID);
        if (plan == null) return false;
        Integer currentType = plan.getPlanType() != null ? plan.getPlanType() : 0;
        Integer units = currentType % 10;
        Integer newType = 20 + units;
        return trainingPlanMapper.updatePlanType(planID, newType) > 0;
    }

    @Override
    @Transactional
    public boolean updateProgress(Integer planID, Float percentage) {
        return trainingPlanMapper.updatePercentage(planID, percentage) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingPlan> getPublishedPlans() {
        return trainingPlanMapper.getPublishedPlans();
    }
}
