package com.example.gameapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class DisplaySequence extends AppCompatActivity {

    private GameLogic gameLogic;
    private Button redButton, blueButton, greenButton, yellowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sequence);
        gameLogic = getIntent().hasExtra("GAME_LOGIC")
                ? (GameLogic) getIntent().getSerializableExtra("GAME_LOGIC")
                : new GameLogic();

        initializeButtons();
        displaySequence();
    }

    private void initializeButtons() {
        redButton = findViewById(R.id.redButton);
        blueButton = findViewById(R.id.blueButton);
        greenButton = findViewById(R.id.greenButton);
        yellowButton = findViewById(R.id.yellowButton);
    }

    private void displaySequence() {
        List<Integer> sequence = gameLogic.getSequence();
        Handler handler = new Handler();

        for (int i = 0; i < sequence.size(); i++) {
            final int color = sequence.get(i);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    highlightButton(color);
                }
            }, i * 1000);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startPlayActivity();
            }
        }, sequence.size() * 1000);
    }

    private void highlightButton(int color) {
        switch (color) {
            case 0: redButton.setAlpha(0.5f); break;
            case 1: blueButton.setAlpha(0.5f); break;
            case 2: greenButton.setAlpha(0.5f); break;
            case 3: yellowButton.setAlpha(0.5f); break;
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                redButton.setAlpha(1f);
                blueButton.setAlpha(1f);
                greenButton.setAlpha(1f);
                yellowButton.setAlpha(1f);
            }
        }, 500);
    }

    private void startPlayActivity() {
        Intent intent = new Intent(DisplaySequence.this, PlayActivity.class);
        intent.putIntegerArrayListExtra("SEQUENCE", new ArrayList<>(gameLogic.getSequence()));
        startActivity(intent);
        finish();
    }
}