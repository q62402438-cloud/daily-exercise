package com.example.dailyexerciseauth.service;

import com.example.dailyexerciseauth.entity.Favorite;
import java.util.List;

public interface FavoriteService {
    List<Favorite> getFavoritesByUserId(Integer userID);
    List<Favorite> getFavoritesByType(Integer userID, Integer targetType);
    boolean addFavorite(Favorite favorite);
    boolean deleteFavorite(Integer favoriteID);
    boolean deleteFavoriteByTarget(Integer userID, Integer targetID, Integer targetType);
    boolean isFavorite(Integer userID, Integer targetID, Integer targetType);
    Favorite getFavoriteByTarget(Integer userID, Integer targetID, Integer targetType);
}
