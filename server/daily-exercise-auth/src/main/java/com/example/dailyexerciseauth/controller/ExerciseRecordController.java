package com.example.dailyexerciseauth.controller;

import com.example.dailyexerciseauth.common.Result;
import com.example.dailyexerciseauth.common.TypeConverter;
import com.example.dailyexerciseauth.entity.ExerciseRecord;
import com.example.dailyexerciseauth.service.ExerciseRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exerciseRecord")
@CrossOrigin
public class ExerciseRecordController {

    @Autowired
    private ExerciseRecordService exerciseRecordService;

    @GetMapping("/user/{userId}")
    public Result getRecordsByUserId(@PathVariable Integer userId) {
        List<ExerciseRecord> records = exerciseRecordService.getRecordsByUserId(userId);
        return Result.success(records);
    }

    @GetMapping("/{id}")
    public Result getRecordById(@PathVariable Integer id) {
        ExerciseRecord record = exerciseRecordService.getRecordById(id);
        if (record != null) {
            return Result.success(record);
        } else {
            return Result.error("打卡记录不存在");
        }
    }

    @GetMapping("/user/{userId}/date/{date}")
    public Result getRecordsByDate(@PathVariable Integer userId, @PathVariable String date) {
        List<ExerciseRecord> records = exerciseRecordService.getRecordsByDate(userId, date);
        return Result.success(records);
    }

    @PostMapping("/listByUser")
    public Result getRecordsByUserIdPost(@RequestBody java.util.Map<String, Object> params) {
        Integer userId = TypeConverter.toInteger(params.get("userID"));
        List<ExerciseRecord> records = exerciseRecordService.getRecordsByUserId(userId);
        return Result.success(records);
    }

    @PostMapping("/get")
    public Result getRecordByIdPost(@RequestBody java.util.Map<String, Object> params) {
        Integer recordID = TypeConverter.toInteger(params.get("recordID"));
        ExerciseRecord record = exerciseRecordService.getRecordById(recordID);
        if (record != null) {
            return Result.success(record);
        } else {
            return Result.error("打卡记录不存在");
        }
    }

    @PostMapping("/delete")
    public Result deleteRecordPost(@RequestBody java.util.Map<String, Object> params) {
        Integer recordID = TypeConverter.toInteger(params.get("recordID"));
        boolean success = exerciseRecordService.deleteRecord(recordID);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    @PostMapping("/listByDateRange")
    public Result getRecordsByDateRange(@RequestBody java.util.Map<String, Object> params) {
        Integer userId = TypeConverter.toInteger(params.get("userID"));
        String startDate = (String) params.get("startDate");
        String endDate = (String) params.get("endDate");
        
        List<ExerciseRecord> records = exerciseRecordService.getRecordsByDateRange(userId, startDate, endDate);
        return Result.success(records);
    }

    @PostMapping("/add")
    public Result addRecord(@RequestBody ExerciseRecord record) {
        boolean success = exerciseRecordService.addRecord(record);
        if (success) {
            return Result.success("添加成功");
        } else {
            return Result.error("添加失败");
        }
    }

    @PutMapping("/update")
    public Result updateRecord(@RequestBody ExerciseRecord record) {
        boolean success = exerciseRecordService.updateRecord(record);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result deleteRecord(@PathVariable Integer id) {
        boolean success = exerciseRecordService.deleteRecord(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
}
