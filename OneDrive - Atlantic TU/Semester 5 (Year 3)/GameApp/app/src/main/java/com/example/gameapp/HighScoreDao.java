package com.example.gameapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface HighScoreDao {
    @Query("SELECT * FROM high_scores ORDER BY score DESC LIMIT 5")
    List<HighScore> getTopFiveScores();

    @Insert
    void insert(HighScore highScore);
}