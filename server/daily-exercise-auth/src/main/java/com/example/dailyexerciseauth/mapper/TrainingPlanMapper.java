package com.example.dailyexerciseauth.mapper;

import com.example.dailyexerciseauth.entity.TrainingPlan;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TrainingPlanMapper {

    @Select("SELECT * FROM training_plan WHERE userID = #{userID} ORDER BY startTime DESC")
    List<TrainingPlan> getPlansByUserId(Integer userID);

    @Select("SELECT * FROM training_plan WHERE planID = #{planID}")
    TrainingPlan getPlanById(Integer planID);

    @Insert("""
        INSERT INTO training_plan (planName, userID, planType, startTime, endTime, sportName, exerciseAmount, percentage, detail)
        VALUES (#{planName}, #{userID}, #{planType}, #{startTime}, #{endTime}, #{sportName}, #{exerciseAmount}, #{percentage}, #{detail})
        """)
    int insertPlan(TrainingPlan plan);

    @Update("""
        UPDATE training_plan 
        SET planName = #{planName}, planType = #{planType}, startTime = #{startTime}, endTime = #{endTime},
            sportName = #{sportName}, exerciseAmount = #{exerciseAmount}, percentage = #{percentage}, detail = #{detail}
        WHERE planID = #{planID}
        """)
    int updatePlan(TrainingPlan plan);

    @Delete("DELETE FROM training_plan WHERE planID = #{planID}")
    int deletePlan(Integer planID);

    @Select("SELECT * FROM training_plan WHERE planType = 1 ORDER BY startTime DESC")
    List<TrainingPlan> getAllPublicPlans();

    @Select("SELECT * FROM training_plan WHERE planType % 10 = 1 ORDER BY startTime DESC")
    List<TrainingPlan> getPendingPlans();

    @Update("UPDATE training_plan SET planType = #{planType} WHERE planID = #{planID}")
    int updatePlanType(@Param("planID") Integer planID, @Param("planType") Integer planType);

    @Update("UPDATE training_plan SET percentage = #{percentage} WHERE planID = #{planID}")
    int updatePercentage(@Param("planID") Integer planID, @Param("percentage") Float percentage);

    @Select("SELECT * FROM training_plan WHERE planType % 10 = 2 ORDER BY startTime DESC")
    List<TrainingPlan> getPublishedPlans();
}
