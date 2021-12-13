package com.example.lab03;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {
    VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        videoView = findViewById(R.id.video);
        Bundle extras = getIntent().getExtras();

        videoView.setVideoURI((Uri)extras.get("CurrentVideo"));
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        videoView.start();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBackPressed()
    {
        videoView.pause();
        videoView.releasePointerCapture();
        finish();
    }
}