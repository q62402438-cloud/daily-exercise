package com.example.dailyexerciseauth.mapper;

import com.example.dailyexerciseauth.entity.ExerciseRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ExerciseRecordMapper {

    @Select("""
        SELECT er.*, se.sportName, tp.planName 
        FROM exercise_record er 
        LEFT JOIN sports_event se ON er.recordType = 0 AND er.eventID = se.eventID 
        LEFT JOIN training_plan tp ON er.recordType = 1 AND er.eventID = tp.planID 
        WHERE er.userID = #{userID} 
        ORDER BY er.sportsDate DESC
        """)
    List<ExerciseRecord> getRecordsByUserId(Integer userID);

    @Select("""
        SELECT er.*, se.sportName, tp.planName 
        FROM exercise_record er 
        LEFT JOIN sports_event se ON er.recordType = 0 AND er.eventID = se.eventID 
        LEFT JOIN training_plan tp ON er.recordType = 1 AND er.eventID = tp.planID 
        WHERE er.recordID = #{recordID}
        """)
    ExerciseRecord getRecordById(Integer recordID);

    @Insert("""
        INSERT INTO exercise_record (userID, sportsDate, eventID, startTime, endTime, exerciseDuration, exerciseAmount, calorie, recordType)
        VALUES (#{userID}, #{sportsDate}, #{eventID}, #{startTime}, #{endTime}, #{exerciseDuration}, #{exerciseAmount}, #{calorie}, #{recordType})
        """)
    int insertRecord(ExerciseRecord record);

    @Update("""
        UPDATE exercise_record 
        SET sportsDate = #{sportsDate}, eventID = #{eventID}, startTime = #{startTime}, endTime = #{endTime},
            exerciseDuration = #{exerciseDuration}, exerciseAmount = #{exerciseAmount}, calorie = #{calorie}, recordType = #{recordType}
        WHERE recordID = #{recordID}
        """)
    int updateRecord(ExerciseRecord record);

    @Delete("DELETE FROM exercise_record WHERE recordID = #{recordID}")
    int deleteRecord(Integer recordID);

    @Select("""
        SELECT er.*, se.sportName, tp.planName 
        FROM exercise_record er 
        LEFT JOIN sports_event se ON er.recordType = 0 AND er.eventID = se.eventID 
        LEFT JOIN training_plan tp ON er.recordType = 1 AND er.eventID = tp.planID 
        WHERE er.userID = #{userID} AND er.sportsDate = #{sportsDate}
        """)
    List<ExerciseRecord> getRecordsByDate(Integer userID, String sportsDate);

    @Select("""
        SELECT er.*, se.sportName, tp.planName 
        FROM exercise_record er 
        LEFT JOIN sports_event se ON er.recordType = 0 AND er.eventID = se.eventID 
        LEFT JOIN training_plan tp ON er.recordType = 1 AND er.eventID = tp.planID 
        WHERE er.userID = #{userID} AND er.sportsDate >= #{startDate} AND er.sportsDate <= #{endDate} 
        ORDER BY er.sportsDate DESC
        """)
    List<ExerciseRecord> getRecordsByDateRange(@Param("userID") Integer userID, @Param("startDate") String startDate, @Param("endDate") String endDate);
}
