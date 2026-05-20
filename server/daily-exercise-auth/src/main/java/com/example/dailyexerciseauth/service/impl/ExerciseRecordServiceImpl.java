package com.example.dailyexerciseauth.service.impl;

import com.example.dailyexerciseauth.entity.ExerciseRecord;
import com.example.dailyexerciseauth.mapper.ExerciseRecordMapper;
import com.example.dailyexerciseauth.service.ExerciseRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
public class ExerciseRecordServiceImpl implements ExerciseRecordService {

    @Autowired
    private ExerciseRecordMapper exerciseRecordMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseRecord> getRecordsByUserId(Integer userID) {
        return exerciseRecordMapper.getRecordsByUserId(userID);
    }

    @Override
    @Transactional(readOnly = true)
    public ExerciseRecord getRecordById(Integer recordID) {
        return exerciseRecordMapper.getRecordById(recordID);
    }

    @Override
    @Transactional
    public boolean addRecord(ExerciseRecord record) {
        if (record.getStartTime() != null && record.getEndTime() != null) {
            record.setExerciseDuration((int) Duration.between(record.getStartTime(), record.getEndTime()).toMinutes());
        }
        return exerciseRecordMapper.insertRecord(record) > 0;
    }

    @Override
    @Transactional
    public boolean updateRecord(ExerciseRecord record) {
        if (record.getStartTime() != null && record.getEndTime() != null) {
            record.setExerciseDuration((int) Duration.between(record.getStartTime(), record.getEndTime()).toMinutes());
        }
        return exerciseRecordMapper.updateRecord(record) > 0;
    }

    @Override
    @Transactional
    public boolean deleteRecord(Integer recordID) {
        return exerciseRecordMapper.deleteRecord(recordID) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseRecord> getRecordsByDate(Integer userID, String date) {
        return exerciseRecordMapper.getRecordsByDate(userID, date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseRecord> getRecordsByDateRange(Integer userID, String startDate, String endDate) {
        return exerciseRecordMapper.getRecordsByDateRange(userID, startDate, endDate);
    }
}
