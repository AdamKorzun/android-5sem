package com.example.lab04;

import static java.lang.Math.abs;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    Button start;
    TextView username, name;
    PlayerAdapter adapter;
    ArrayList<TableEntry> players = new ArrayList<>();
    Button sortButton;
    boolean sorted = false;
    DbHelper db;
    String usernameStr, nameStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = findViewById(R.id.startButton);
        name = findViewById(R.id.nameView);
        username = findViewById(R.id.usernameView);
        sortButton = findViewById(R.id.sortButton);
        ListView playerTable = findViewById(R.id.playerList);
        db =  new DbHelper(getApplicationContext());

        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.sort(new Comparator<TableEntry>() {
                    @Override
                    public int compare(TableEntry tableEntry, TableEntry t1) {
                        return Integer.compare(t1.getScore(), tableEntry.getScore());
                    }
                });
            }
        });
        fillArray();
        adapter = new PlayerAdapter(getApplicationContext(), R.layout.list_view_item, players);
        playerTable.setAdapter(adapter);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()){

                    if (db.isNameAvailable(username.getText().toString())){
                        usernameStr = username.getText().toString();
                        nameStr = name.getText().toString();
                        Intent intent = new Intent(getApplication(), Game.class);
                        gameLauncher.launch(intent);
                    }else{
                        Toast.makeText(getApplicationContext(), "Name is not available", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }
    private void fillArray(){
        players.clear();
        Cursor cursor = db.getAllData();
        if (cursor.getCount() == 0){
            return;
        }
        while (cursor.moveToNext()){
            String username = cursor.getString(1);
            String name = cursor.getString(2);
            int score = cursor.getInt(3);
            long timePlayed = cursor.getInt(4);
            players.add(new TableEntry(username, name, score, timePlayed));
        }
        cursor.close();

    }
    public boolean validateFields(){
        return username.getText().length() > 0 && name.getText().length() > 0;
    }
    ActivityResultLauncher gameLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() {
        @Override
        public void onActivityResult(Object result) {

            ActivityResult res = (ActivityResult)result;
            if (res.getResultCode() == RESULT_OK) {
                Bundle extras = res.getData().getExtras();
                int score = extras.getInt("levelsBeaten");
                long time = extras.getLong("TotalTime");
                db.addScore(new TableEntry(usernameStr, nameStr, score, time));
                fillArray();
                adapter.notifyDataSetChanged();

            }
        }
    });

}