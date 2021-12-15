package com.example.lab03;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    VideoView vid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        vid = findViewById(R.id.video_player);
        ActivityResultLauncher fileLoadLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResultCallback) result -> {
            ActivityResult res = (ActivityResult)result;
            if (res.getResultCode()  == RESULT_OK) {
                Uri fileUri = res.getData().getData();
                String filepath = res.getData().getData().getPath();
                Intent intent = new Intent(getApplicationContext(), AudioActivity.class);
                intent.putExtra("CurrentSong", fileUri);
                intent.putExtra("Filepath", filepath);
                startActivity(intent);
            }
        });
        ActivityResultLauncher fileVideoLoad = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResultCallback) result -> {
            ActivityResult res = (ActivityResult)result;
            if (res.getResultCode()  == RESULT_OK) {
                Uri fileUri = res.getData().getData();
                String filepath = res.getData().getData().getPath();
                Intent intent = new Intent(getApplicationContext(), VideoActivity.class);
                intent.putExtra("CurrentVideo", fileUri);
                intent.putExtra("Filepath", filepath);
                startActivity(intent);
            }
        });
        Button openButton = findViewById(R.id.openButton);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType( "audio/mpeg");
                fileLoadLauncher.launch(intent);
            }
        });
        Button openVideoButton = findViewById((R.id.openVideoButton));
        openVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType( "video/mp4");
                fileVideoLoad.launch(intent);
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 111);
        }

    }
    public void playVideo(Uri uri) throws IOException {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        mediaPlayer.setDataSource(getApplicationContext(), uri);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }
    public void createNotification(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("1", "Channel1", NotificationManager.IMPORTANCE_HIGH);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel) ;

        }
    }
}