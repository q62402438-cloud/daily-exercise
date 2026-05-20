package com.example.dailyexerciseauth.controller;

import com.example.dailyexerciseauth.common.Result;
import com.example.dailyexerciseauth.common.TypeConverter;
import com.example.dailyexerciseauth.entity.SportsEvent;
import com.example.dailyexerciseauth.service.SportsEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sportsEvent")
@CrossOrigin
public class SportsEventController {

    @Autowired
    private SportsEventService sportsEventService;

    @GetMapping("/list")
    public Result getAllEvents() {
        List<SportsEvent> events = sportsEventService.getAllEvents();
        return Result.success(events);
    }

    @GetMapping("/{id}")
    public Result getEventById(@PathVariable Integer id) {
        SportsEvent event = sportsEventService.getEventById(id);
        if (event != null) {
            return Result.success(event);
        } else {
            return Result.error("运动项目不存在");
        }
    }

    @PostMapping("/get")
    public Result getEventByIdPost(@RequestBody java.util.Map<String, Object> params) {
        Integer eventID = TypeConverter.toInteger(params.get("eventID"));
        SportsEvent event = sportsEventService.getEventById(eventID);
        if (event != null) {
            return Result.success(event);
        } else {
            return Result.error("运动项目不存在");
        }
    }

    @PostMapping("/add")
    public Result addEvent(@RequestBody SportsEvent event) {
        boolean success = sportsEventService.addEvent(event);
        if (success) {
            return Result.success("添加成功");
        } else {
            return Result.error("添加失败");
        }
    }

    @PutMapping("/update")
    public Result updateEvent(@RequestBody SportsEvent event) {
        boolean success = sportsEventService.updateEvent(event);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result deleteEvent(@PathVariable Integer id) {
        boolean success = sportsEventService.deleteEvent(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
}
