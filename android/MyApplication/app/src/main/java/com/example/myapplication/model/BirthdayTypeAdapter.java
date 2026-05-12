package com.example.myapplication.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class BirthdayTypeAdapter implements JsonSerializer<String>, JsonDeserializer<String> {
    @Override
    public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonArray()) {
            JsonArray array = json.getAsJsonArray();
            if (array.size() >= 3) {
                String year = array.get(0).getAsString();
                String month = array.get(1).getAsString();
                String day = array.get(2).getAsString();
                return String.format("%s-%02d-%02d", year, Integer.parseInt(month), Integer.parseInt(day));
            }
            return "";
        } else if (json.isJsonPrimitive()) {
            return json.getAsString();
        }
        return "";
    }

    @Override
    public JsonElement serialize(String src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null || src.isEmpty()) {
            return new JsonPrimitive("");
        }
        String[] parts = src.split("-");
        if (parts.length == 3) {
            JsonArray array = new JsonArray();
            array.add(parts[0]);
            array.add(String.format("%02d", Integer.parseInt(parts[1])));
            array.add(String.format("%02d", Integer.parseInt(parts[2])));
            return array;
        }
        return new JsonPrimitive(src);
    }
}