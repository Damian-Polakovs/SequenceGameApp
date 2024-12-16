package com.example.gameapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "PlayActivity";
    private static final int INITIAL_SEQUENCE_LENGTH = 4;
    private static final int SEQUENCE_INCREMENT = 2;

    private GameLogic gameLogic;
    private List<Integer> playerSequence;
    private List<Integer> currentSequence;
    private int currentIndex;
    private int currentScore;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private HighScoreDatabase highScoreDb;
    private ExecutorService executorService;

    private volatile boolean isProcessing = false;
    private volatile boolean isGameOver = false;

    private Button redButton, blueButton, greenButton, yellowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        highScoreDb = HighScoreDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        gameLogic = new GameLogic(INITIAL_SEQUENCE_LENGTH);
        currentScore = 0;

        ArrayList<Integer> passedSequence = getIntent().getIntegerArrayListExtra("SEQUENCE");
        if (passedSequence != null && !passedSequence.isEmpty()) {
            currentSequence = passedSequence;
        } else {
            currentSequence = gameLogic.generateSequence(INITIAL_SEQUENCE_LENGTH);
        }

        playerSequence = new ArrayList<>();
        currentIndex = 0;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        initialiseButtons();
        displaySequence();
    }

    private void displaySequence() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            for (int i = 0; i < currentSequence.size(); i++) {
                final int color = currentSequence.get(i);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    highlightButton(color);
                }, i * 1000);
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isGameOver = false;
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    private void initialiseButtons() {
        redButton = findViewById(R.id.redButton);
        blueButton = findViewById(R.id.blueButton);
        greenButton = findViewById(R.id.greenButton);
        yellowButton = findViewById(R.id.yellowButton);
    }

    private void processUserInput(int tiltColor) {
        if (isGameOver) return;

        try {
            if (currentIndex < currentSequence.size() && tiltColor == currentSequence.get(currentIndex)) {
                playerSequence.add(tiltColor);
                highlightButton(tiltColor);

                if (currentIndex == currentSequence.size() - 1) {
                    // Round completed successfully
                    currentScore = currentSequence.size();

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Correct Sequence! Next Round...", Toast.LENGTH_SHORT).show();
                    });

                    currentSequence = gameLogic.generateSequence(currentSequence.size() + SEQUENCE_INCREMENT);
                    playerSequence.clear();
                    currentIndex = 0;

                    displaySequence();
                    return;
                }
                currentIndex++;
            } else {
                handleGameOver();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in processing input", e);
            handleGameOver();
        }
    }

    private void handleGameOver() {
        if (isGameOver) return;
        isGameOver = true;

        executorService.execute(() -> {
            List<HighScore> topScores = highScoreDb.highScoreDao().getTopFiveScores();
            boolean isHighScore = topScores.size() < 5 || currentScore > topScores.get(topScores.size() - 1).getScore();

            runOnUiThread(() -> {
                Intent intent = new Intent(PlayActivity.this, GameOver.class);
                intent.putExtra("SCORE", currentScore);
                intent.putExtra("IS_HIGH_SCORE", isHighScore);
                startActivity(intent);
                finish();
            });
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isGameOver) return;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && !isProcessing) {
            float x = event.values[0];
            float y = event.values[1];

            int tiltColor = determineTiltColor(x, y);
            if (tiltColor != -1) {
                isProcessing = true;
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    processUserInput(tiltColor);
                    isProcessing = false;
                }, 250);
            }
        }
    }

    private int determineTiltColor(float x, float y) {
        float gForce = Math.abs(y);
        final float GRAVITY_THRESHOLD = 1.5f;
        if (y < -GRAVITY_THRESHOLD) return 0; // North - Red
        if (y > GRAVITY_THRESHOLD) return 3;  // South - Yellow
        if (x > 6) return 1;  // East - Blue
        if (x < -6) return 2; // West - Green
        return -1;
    }

    private void highlightButton(int color) {
        Button targetButton;
        switch (color) {
            case 0: targetButton = redButton; break;
            case 1: targetButton = blueButton; break;
            case 2: targetButton = greenButton; break;
            case 3: targetButton = yellowButton; break;
            default: return;
        }

        targetButton.setAlpha(0.5f);
        new Handler(Looper.getMainLooper()).postDelayed(() -> targetButton.setAlpha(1f), 200);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}