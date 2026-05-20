package com.example.dailyexerciseauth.mapper;

import com.example.dailyexerciseauth.entity.SportsEvent;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SportsEventMapper {

    @Select("SELECT * FROM sports_event")
    List<SportsEvent> getAllEvents();

    @Select("SELECT * FROM sports_event WHERE eventID = #{eventID}")
    SportsEvent getEventById(Integer eventID);

    @Insert("INSERT INTO sports_event (sportName, averageCalorie) VALUES (#{sportName}, #{averageCalorie})")
    int insertEvent(SportsEvent event);

    @Update("UPDATE sports_event SET sportName = #{sportName}, averageCalorie = #{averageCalorie} WHERE eventID = #{eventID}")
    int updateEvent(SportsEvent event);

    @Delete("DELETE FROM sports_event WHERE eventID = #{eventID}")
    int deleteEvent(Integer eventID);
}
