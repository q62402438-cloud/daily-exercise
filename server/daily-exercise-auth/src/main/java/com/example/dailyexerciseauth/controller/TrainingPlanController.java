package com.example.dailyexerciseauth.controller;

import com.example.dailyexerciseauth.common.Result;
import com.example.dailyexerciseauth.common.TypeConverter;
import com.example.dailyexerciseauth.entity.TrainingPlan;
import com.example.dailyexerciseauth.service.TrainingPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainingPlan")
@CrossOrigin
public class TrainingPlanController {

    @Autowired
    private TrainingPlanService trainingPlanService;

    @GetMapping("/user/{userId}")
    public Result getPlansByUserId(@PathVariable Integer userId) {
        List<TrainingPlan> plans = trainingPlanService.getPlansByUserId(userId);
        return Result.success(plans);
    }

    @GetMapping("/{id}")
    public Result getPlanById(@PathVariable Integer id) {
        TrainingPlan plan = trainingPlanService.getPlanById(id);
        if (plan != null) {
            return Result.success(plan);
        } else {
            return Result.error("训练计划不存在");
        }
    }

    @GetMapping("/public")
    public Result getAllPublicPlans() {
        List<TrainingPlan> plans = trainingPlanService.getAllPublicPlans();
        return Result.success(plans);
    }

    @GetMapping("/getPending")
    public Result getPendingPlans() {
        List<TrainingPlan> plans = trainingPlanService.getPendingPlans();
        return Result.success(plans);
    }

    @PostMapping("/listByUser")
    public Result getPlansByUserIdPost(@RequestBody java.util.Map<String, Object> params) {
        Integer userId = TypeConverter.toInteger(params.get("userID"));
        List<TrainingPlan> plans = trainingPlanService.getPlansByUserId(userId);
        return Result.success(plans);
    }

    @PostMapping("/get")
    public Result getPlanByIdPost(@RequestBody java.util.Map<String, Object> params) {
        Integer planID = TypeConverter.toInteger(params.get("planID"));
        TrainingPlan plan = trainingPlanService.getPlanById(planID);
        if (plan != null) {
            return Result.success(plan);
        } else {
            return Result.error("训练计划不存在");
        }
    }

    @PostMapping("/update")
    public Result updatePlanPost(@RequestBody java.util.Map<String, Object> params) {
        TrainingPlan plan = new TrainingPlan();
        plan.setPlanID(TypeConverter.toInteger(params.get("planID")));
        plan.setPlanName((String) params.get("planName"));
        plan.setPlanType(TypeConverter.toInteger(params.get("planType")));
        plan.setStartTime(TypeConverter.toLocalDateTime(params.get("startTime")));
        plan.setEndTime(TypeConverter.toLocalDateTime(params.get("endTime")));
        plan.setSportName((String) params.get("sportName"));
        plan.setExerciseAmount(TypeConverter.toFloat(params.get("exerciseAmount")));
        plan.setPercentage(TypeConverter.toFloat(params.get("percentage")));
        plan.setDetail((String) params.get("detail"));
        boolean success = trainingPlanService.updatePlan(plan);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }

    @PostMapping("/delete")
    public Result deletePlanPost(@RequestBody java.util.Map<String, Object> params) {
        Integer planID = TypeConverter.toInteger(params.get("planID"));
        boolean success = trainingPlanService.deletePlan(planID);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    @PostMapping("/create")
    public Result createPlan(@RequestBody TrainingPlan plan) {
        if (plan.getPlanType() == null) {
            plan.setPlanType(0);
        }
        boolean success = trainingPlanService.createPlan(plan);
        if (success) {
            return Result.success("创建成功");
        } else {
            return Result.error("创建失败");
        }
    }

    @PutMapping("/update")
    public Result updatePlan(@RequestBody TrainingPlan plan) {
        boolean success = trainingPlanService.updatePlan(plan);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result deletePlan(@PathVariable Integer id) {
        boolean success = trainingPlanService.deletePlan(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    @PostMapping("/submitAudit")
    public Result submitAudit(@RequestBody java.util.Map<String, Object> params) {
        Integer planID = TypeConverter.toInteger(params.get("planID"));
        boolean success = trainingPlanService.submitAudit(planID);
        if (success) {
            return Result.success("提交审核成功");
        } else {
            return Result.error("提交审核失败");
        }
    }

    @PostMapping("/auditPass")
    public Result auditPass(@RequestBody java.util.Map<String, Object> params) {
        Integer planID = TypeConverter.toInteger(params.get("planID"));
        if (planID == null) {
            return Result.error("计划ID不能为空");
        }
        boolean success = trainingPlanService.auditPass(planID);
        if (success) {
            return Result.success("审核通过成功");
        } else {
            return Result.error("审核通过失败");
        }
    }

    @PostMapping("/auditReject")
    public Result auditReject(@RequestBody java.util.Map<String, Object> params) {
        Integer planID = TypeConverter.toInteger(params.get("planID"));
        if (planID == null) {
            return Result.error("计划ID不能为空");
        }
        boolean success = trainingPlanService.auditReject(planID);
        if (success) {
            return Result.success("审核拒绝成功");
        } else {
            return Result.error("审核拒绝失败");
        }
    }

    @PostMapping("/start")
    public Result startPlan(@RequestBody java.util.Map<String, Object> params) {
        Integer planID = TypeConverter.toInteger(params.get("planID"));
        boolean success = trainingPlanService.startPlan(planID);
        if (success) {
            return Result.success("开始执行计划成功");
        } else {
            return Result.error("开始执行计划失败");
        }
    }

    @PostMapping("/complete")
    public Result completePlan(@RequestBody java.util.Map<String, Object> params) {
        Integer planID = TypeConverter.toInteger(params.get("planID"));
        boolean success = trainingPlanService.completePlan(planID);
        if (success) {
            return Result.success("完成计划成功");
        } else {
            return Result.error("完成计划失败");
        }
    }

    @PostMapping("/updateProgress")
    public Result updateProgress(@RequestBody java.util.Map<String, Object> params) {
        Integer planID = TypeConverter.toInteger(params.get("planID"));
        Float percentage = TypeConverter.toFloat(params.get("percentage"));
        boolean success = trainingPlanService.updateProgress(planID, percentage);
        if (success) {
            return Result.success("更新完成度成功");
        } else {
            return Result.error("更新完成度失败");
        }
    }

    @PostMapping("/listPublished")
    public Result getPublishedPlans() {
        List<TrainingPlan> plans = trainingPlanService.getPublishedPlans();
        return Result.success(plans);
    }
}
