package com.example.dailyexerciseauth.service;

import com.example.dailyexerciseauth.entity.ExerciseRecord;
import java.util.List;

public interface ExerciseRecordService {
    List<ExerciseRecord> getRecordsByUserId(Integer userID);
    ExerciseRecord getRecordById(Integer recordID);
    boolean addRecord(ExerciseRecord record);
    boolean updateRecord(ExerciseRecord record);
    boolean deleteRecord(Integer recordID);
    List<ExerciseRecord> getRecordsByDate(Integer userID, String date);
    List<ExerciseRecord> getRecordsByDateRange(Integer userID, String startDate, String endDate);
}
