package com.example.dailyexerciseauth.controller;

import com.example.dailyexerciseauth.common.Result;
import com.example.dailyexerciseauth.common.TypeConverter;
import com.example.dailyexerciseauth.entity.Favorite;
import com.example.dailyexerciseauth.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorite")
@CrossOrigin
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping("/user/{userId}")
    public Result getFavoritesByUserId(@PathVariable Integer userId) {
        List<Favorite> favorites = favoriteService.getFavoritesByUserId(userId);
        return Result.success(favorites);
    }

    @GetMapping("/user/{userId}/type/{targetType}")
    public Result getFavoritesByType(@PathVariable Integer userId, @PathVariable Integer targetType) {
        List<Favorite> favorites = favoriteService.getFavoritesByType(userId, targetType);
        return Result.success(favorites);
    }

    @PostMapping("/listByUser")
    public Result getFavoritesByUserIdPost(@RequestBody java.util.Map<String, Object> params) {
        Integer userId = TypeConverter.toInteger(params.get("userID"));
        List<Favorite> favorites = favoriteService.getFavoritesByUserId(userId);
        return Result.success(favorites);
    }

    @PostMapping("/delete")
    public Result deleteFavoritePost(@RequestBody java.util.Map<String, Object> params) {
        Integer favoriteID = TypeConverter.toInteger(params.get("favoriteID"));
        boolean success = favoriteService.deleteFavorite(favoriteID);
        if (success) {
            return Result.success("取消收藏成功");
        } else {
            return Result.error("取消收藏失败");
        }
    }

    @PostMapping("/check")
    public Result checkFavoritePost(@RequestBody java.util.Map<String, Object> params) {
        Integer userId = TypeConverter.toInteger(params.get("userID"));
        Integer targetId = TypeConverter.toInteger(params.get("targetID"));
        Integer targetType = TypeConverter.toInteger(params.get("targetType"));
        Favorite favorite = favoriteService.getFavoriteByTarget(userId, targetId, targetType);
        if (favorite != null) {
            return Result.success(favorite);
        } else {
            Map<String, Boolean> result = new HashMap<>();
            result.put("isFavorite", false);
            return Result.success(result);
        }
    }

    @PostMapping("/add")
    public Result addFavorite(@RequestBody Favorite favorite) {
        boolean success = favoriteService.addFavorite(favorite);
        if (success) {
            return Result.success("收藏成功");
        } else {
            return Result.error("收藏失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result deleteFavorite(@PathVariable Integer id) {
        boolean success = favoriteService.deleteFavorite(id);
        if (success) {
            return Result.success("取消收藏成功");
        } else {
            return Result.error("取消收藏失败");
        }
    }

    @DeleteMapping("/user/{userId}/target/{targetId}/type/{targetType}")
    public Result deleteFavoriteByTarget(@PathVariable Integer userId, @PathVariable Integer targetId, @PathVariable Integer targetType) {
        boolean success = favoriteService.deleteFavoriteByTarget(userId, targetId, targetType);
        if (success) {
            return Result.success("取消收藏成功");
        } else {
            return Result.error("取消收藏失败");
        }
    }

    @GetMapping("/check")
    public Result checkFavorite(@RequestParam Integer userId, @RequestParam Integer targetId, @RequestParam Integer targetType) {
        boolean isFavorite = favoriteService.isFavorite(userId, targetId, targetType);
        Map<String, Boolean> result = new HashMap<>();
        result.put("isFavorite", isFavorite);
        return Result.success(result);
    }
}
