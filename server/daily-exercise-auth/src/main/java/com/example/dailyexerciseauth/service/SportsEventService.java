package com.example.dailyexerciseauth.service;

import com.example.dailyexerciseauth.entity.SportsEvent;
import java.util.List;

public interface SportsEventService {
    List<SportsEvent> getAllEvents();
    SportsEvent getEventById(Integer eventID);
    boolean addEvent(SportsEvent event);
    boolean updateEvent(SportsEvent event);
    boolean deleteEvent(Integer eventID);
}
