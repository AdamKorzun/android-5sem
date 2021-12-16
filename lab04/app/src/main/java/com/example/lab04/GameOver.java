package com.example.lab04;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameOver extends AppCompatActivity {
    Button retryButton;
    Button leaveButton;
    TextView scoreView;
    DbHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        Intent returnIntent = new Intent();
        scoreView = findViewById(R.id.scoreView);
        Bundle extras = getIntent().getExtras();

        scoreView.setText(String.valueOf(extras.getInt("score")));

        retryButton = findViewById(R.id.retryButton);
        leaveButton = findViewById(R.id.leaveButton);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnIntent.putExtra("finishGame", false);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnIntent.putExtra("finishGame", true);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}