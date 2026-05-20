package com.example.dailyexerciseauth.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TypeConverter {
    
    public static Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof String) {
            String strValue = (String) value;
            if (strValue.isEmpty()) {
                return null;
            }
            return Integer.parseInt(strValue);
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return null;
    }
    
    public static Float toFloat(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Float) {
            return (Float) value;
        }
        if (value instanceof String) {
            String strValue = (String) value;
            if (strValue.isEmpty()) {
                return null;
            }
            return Float.parseFloat(strValue);
        }
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        return null;
    }
    
    public static Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof String) {
            String strValue = (String) value;
            if (strValue.isEmpty()) {
                return null;
            }
            return Long.parseLong(strValue);
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }
    
    public static LocalDateTime toLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        if (value instanceof String) {
            String strValue = (String) value;
            if (strValue.isEmpty()) {
                return null;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            try {
                return LocalDateTime.parse(strValue, formatter);
            } catch (DateTimeParseException e) {
                try {
                    return LocalDateTime.parse(strValue);
                } catch (DateTimeParseException e2) {
                    try {
                        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        return LocalDateTime.parse(strValue + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    } catch (DateTimeParseException e3) {
                        return null;
                    }
                }
            }
        }
        return null;
    }
}
