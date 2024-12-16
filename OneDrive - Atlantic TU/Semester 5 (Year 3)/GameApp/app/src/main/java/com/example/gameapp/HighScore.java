package com.example.gameapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "high_scores")
public class HighScore {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String playerName;
    private int score;

    public HighScore(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "HighScore{" +
                "id=" + id +
                ", playerName='" + playerName + '\'' +
                ", score=" + score +
                '}';
    }
}
