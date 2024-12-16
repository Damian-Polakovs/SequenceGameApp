package com.example.gameapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.Executors;

public class GameOver extends AppCompatActivity {
    private int score;
    private HighScoreDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        score = getIntent().getIntExtra("SCORE", 0);
        TextView scoreText = findViewById(R.id.scoreText);
        scoreText.setText("Your Score: " + score);

        database = HighScoreDatabase.getInstance(this);
        checkIfHighScore();
    }

    private void checkIfHighScore() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final boolean isHighScore = database.highScoreDao().getTopFiveScores().size() < 5 ||
                        score > database.highScoreDao().getTopFiveScores().get(4).getScore();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isHighScore) {
                            showHighScorePrompt();
                        } else {
                            setupHiScoreButton();
                        }
                    }
                });
            }
        });
    }
    private void showHighScorePrompt() {
        findViewById(R.id.highScoreLayout).setVisibility(View.VISIBLE);
        Button submitButton = findViewById(R.id.submitButton);
        final EditText nameInput = findViewById(R.id.nameInput);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String playerName = nameInput.getText().toString();
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        database.highScoreDao().insert(new HighScore(playerName, score));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setupHiScoreButton();
                            }
                        });
                    }
                });
            }
        });
    }

    private void setupHiScoreButton() {
        Button hiScoreButton = findViewById(R.id.hiScoreButton);
        hiScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOver.this, HiScore.class);
                startActivity(intent);
                finish();
            }
        });
    }
}