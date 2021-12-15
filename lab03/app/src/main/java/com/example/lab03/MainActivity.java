package com.example.lab03;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.projection.MediaProjection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<MediaSongVid> songVideos;
    SongAdapter adapter;
    int REQuEST_CODE = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAllAudio();
        findAllVideos();
        ListView mediaList = findViewById(R.id.mediaList);
        adapter = new SongAdapter(getApplicationContext(),R.layout.list_item_view, songVideos);
        mediaList.setAdapter(adapter);
        mediaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), AudioActivity.class);
                MediaSongVid item = adapter.getItem(i);
                if (item.getFormat().equals("mp3")){
                    intent.putExtra("MediaList", songVideos);
                    intent.putExtra("CurrentSong", item);
                    intent.putExtra("CurrentIndex", i);
                    startActivity(intent);
                }
                else {
                    intent = new Intent(getApplicationContext(), VideoActivity.class);
                    intent.putExtra("CurrentVideo", item.getUri());
                    startActivity(intent);
                }

            }
        });
        askPermission();


    }
    private void askPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQuEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQuEST_CODE){
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
                askPermission();
            }
        }
    }
    public void getAllAudio(){
        songVideos = new ArrayList<>();
        String[] projection = new String[]{
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA
        };
        Cursor c=getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        c.moveToFirst();
        do
        {
            String path = c.getString(3);
            if (!path.contains("/Music/")){
                continue;
            }
            Uri uri = Uri.fromFile(new File(path));
            String title = c.getString(0);
            String author = c.getString(1);
            if (author == null){
                author = "Unknown artist";
            }
            int duration = c.getInt(2);
            songVideos.add(new MediaSongVid(title, author, "mp3", duration, uri));


        }
        while(c.moveToNext());

        c.close();


    }
    private void findAllVideos(){
        String[] projection = new String[]{
                MediaStore.Video.VideoColumns.TITLE,
                MediaStore.Video.VideoColumns.ARTIST,
                MediaStore.Video.VideoColumns.DURATION,
                MediaStore.Video.VideoColumns.DATA
        };

        Cursor c=getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        if (c == null){
            return;
        }
        c.moveToFirst();
        do
        {
            String path = c.getString(3);
            System.out.println(path);
            if (!path.contains("/Movies/")){
                continue;
            }
            Uri uri = Uri.fromFile(new File(path));
            String title = c.getString(0);
            String author = c.getString(1);
            if (author == null){
                author = "Unknown artist";
            }
            int duration = c.getInt(2);
            songVideos.add(new MediaSongVid(title, author, "mp4", duration, uri));


        }
        while(c.moveToNext());
        c.close();
    }


}