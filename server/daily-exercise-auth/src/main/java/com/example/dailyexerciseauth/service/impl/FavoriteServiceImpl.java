package com.example.dailyexerciseauth.service.impl;

import com.example.dailyexerciseauth.entity.Favorite;
import com.example.dailyexerciseauth.mapper.FavoriteMapper;
import com.example.dailyexerciseauth.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Favorite> getFavoritesByUserId(Integer userID) {
        return favoriteMapper.getFavoritesByUserId(userID);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Favorite> getFavoritesByType(Integer userID, Integer targetType) {
        return favoriteMapper.getFavoritesByType(userID, targetType);
    }

    @Override
    @Transactional
    public boolean addFavorite(Favorite favorite) {
        favorite.setFavoriteTime(LocalDateTime.now());
        return favoriteMapper.insertFavorite(favorite) > 0;
    }

    @Override
    @Transactional
    public boolean deleteFavorite(Integer favoriteID) {
        return favoriteMapper.deleteFavorite(favoriteID) > 0;
    }

    @Override
    @Transactional
    public boolean deleteFavoriteByTarget(Integer userID, Integer targetID, Integer targetType) {
        return favoriteMapper.deleteFavoriteByTarget(userID, targetID, targetType) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorite(Integer userID, Integer targetID, Integer targetType) {
        return favoriteMapper.getFavoriteByTarget(userID, targetID, targetType) != null;
    }

    @Override
    @Transactional(readOnly = true)
    public Favorite getFavoriteByTarget(Integer userID, Integer targetID, Integer targetType) {
        return favoriteMapper.getFavoriteByTarget(userID, targetID, targetType);
    }
}
