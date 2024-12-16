package com.example.gameapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;
import java.util.concurrent.Executors;

public class HiScore extends AppCompatActivity {

    private HighScoreDatabase database;
    private TextView[] rankTextViews;
    private TextView[] nameTextViews;
    private TextView[] scoreTextViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hi_score);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initialiseTextViews();
        database = HighScoreDatabase.getInstance(this);
        loadHighScores();

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HiScore.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initialiseTextViews() {
        rankTextViews = new TextView[]{
                findViewById(R.id.rank1),
                findViewById(R.id.rank2),
                findViewById(R.id.rank3),
                findViewById(R.id.rank4),
                findViewById(R.id.rank5)
        };

        nameTextViews = new TextView[]{
                findViewById(R.id.name1),
                findViewById(R.id.name2),
                findViewById(R.id.name3),
                findViewById(R.id.name4),
                findViewById(R.id.name5)
        };

        scoreTextViews = new TextView[]{
                findViewById(R.id.score1),
                findViewById(R.id.score2),
                findViewById(R.id.score3),
                findViewById(R.id.score4),
                findViewById(R.id.score5)
        };
    }

    private void loadHighScores() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final List<HighScore> topScores = database.highScoreDao().getTopFiveScores();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < Math.min(topScores.size(), 5); i++) {
                            rankTextViews[i].setText(String.valueOf(i + 1));
                            nameTextViews[i].setText(topScores.get(i).getPlayerName());
                            scoreTextViews[i].setText(String.valueOf(topScores.get(i).getScore()));
                        }

                        // Clear if slots < 5
                        for (int i = topScores.size(); i < 5; i++) {
                            rankTextViews[i].setText("---");
                            nameTextViews[i].setText("---");
                            scoreTextViews[i].setText("---");
                        }
                    }
                });
            }
        });
    }
}