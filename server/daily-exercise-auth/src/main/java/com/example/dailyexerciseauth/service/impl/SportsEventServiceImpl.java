package com.example.dailyexerciseauth.service.impl;

import com.example.dailyexerciseauth.entity.SportsEvent;
import com.example.dailyexerciseauth.mapper.SportsEventMapper;
import com.example.dailyexerciseauth.service.SportsEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SportsEventServiceImpl implements SportsEventService {

    @Autowired
    private SportsEventMapper sportsEventMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SportsEvent> getAllEvents() {
        return sportsEventMapper.getAllEvents();
    }

    @Override
    @Transactional(readOnly = true)
    public SportsEvent getEventById(Integer eventID) {
        return sportsEventMapper.getEventById(eventID);
    }

    @Override
    @Transactional
    public boolean addEvent(SportsEvent event) {
        return sportsEventMapper.insertEvent(event) > 0;
    }

    @Override
    @Transactional
    public boolean updateEvent(SportsEvent event) {
        return sportsEventMapper.updateEvent(event) > 0;
    }

    @Override
    @Transactional
    public boolean deleteEvent(Integer eventID) {
        return sportsEventMapper.deleteEvent(eventID) > 0;
    }
}
