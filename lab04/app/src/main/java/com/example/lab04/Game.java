package com.example.lab04;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.graphics.Canvas;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class Game extends AppCompatActivity {
    GameView gameView;
    Timer updateTimer;
    long startTime = Constants.STARTING_TIME;
    int level = 0;
    int counter = 0;
    TextView timerValue;
    TextView levelValue;
    private OrientationManager orientationManager;
    boolean isGameOver = false;
    long startTimeFull;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameView = findViewById(R.id.gameView);
        timerValue = findViewById(R.id.timerValue);
        levelValue = findViewById(R.id.levelnumber);
        startGame();
    }
    private void startGame(){
        startTimeFull = System.currentTimeMillis();
        orientationManager = new OrientationManager();
        orientationManager.newGame();
        orientationManager.register();
        updateTimer = new Timer("update");
        timerValue.setText(String.valueOf(startTime / 1000));
        levelValue.setText(String.valueOf(level + 1));
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (isGameOver){
                    return;
                }
                if (gameView.hasWon()){
                    if (level < Constants.MAX_LEVELS){
                        level += 1;
                        startTime = Constants.STARTING_TIME + level * 3000;
                        gameView.recreate(gameView.COLS + 1, gameView.ROWS + 1);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                levelValue.setText(String.valueOf(level + 1));
                            }
                        });
                    }
                    else {
                        leaveGame();
                    }
                }
                if (startTime < 0){
                    isGameOver = true;
                    Intent gameIntent = new Intent(getApplicationContext(), GameOver.class);
                    gameIntent.putExtra("score", level);
                    gameIntent.putExtra("time", System.currentTimeMillis() - startTimeFull);
                    gameOverLauncher.launch(gameIntent);
                }
                if (counter > 1 && orientationManager.getOrientation() != null && orientationManager.getStartOrientation() != null){
                    float pitch = orientationManager.getOrientation()[1] - orientationManager.getStartOrientation()[1];
                    float roll = orientationManager.getOrientation()[2] - orientationManager.getStartOrientation()[2];

                    if (pitch < -1 * Constants.PITCH_THRESHOLD){
                        gameView.moveRight();
                    }
                    else if (pitch > Constants.PITCH_THRESHOLD){
                        gameView.moveLeft();
                    }
                    else if(roll > Constants.PITCH_THRESHOLD){
                        gameView.moveUp();
                    }
                    else if(roll < -1 * Constants.PITCH_THRESHOLD){
                        gameView.moveDown();
                    }

                    counter = 0;
                }
                counter++;
                startTime -= (long)(1000 / 16);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String str = String.valueOf((float)(startTime / (float)1000));
                        timerValue.setText(str);


                    }
                });

                gameView.invalidate();
            }
        }, 0, 1000 / 16);
    }
    ActivityResultLauncher gameOverLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() {
        @Override
        public void onActivityResult(Object result) {
            ActivityResult res = (ActivityResult)result;
            if (res.getResultCode() == RESULT_OK) {
                Bundle extras = res.getData().getExtras();
                boolean finishGame = extras.getBoolean("finishGame");
                if (finishGame){
                    leaveGame();
                }
                else {
                    gameView.restartLevel();
                    isGameOver = false;
                    startTime = Constants.STARTING_TIME + level * 3000;
                }
            }
        }
    });
    public void leaveGame(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("TotalTime", System.currentTimeMillis() - startTimeFull);
        returnIntent.putExtra("levelsBeaten", level);
        updateTimer.cancel();
        setResult(RESULT_OK, returnIntent);
        finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updateTimer.cancel();
        finish();
    }
}