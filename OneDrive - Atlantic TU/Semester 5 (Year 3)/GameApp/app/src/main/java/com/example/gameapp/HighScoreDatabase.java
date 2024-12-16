package com.example.gameapp;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {HighScore.class}, version = 1)
public abstract class HighScoreDatabase extends RoomDatabase {
    private static HighScoreDatabase instance;

    public abstract HighScoreDao highScoreDao();

    public static synchronized HighScoreDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            HighScoreDatabase.class, "high_score_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
