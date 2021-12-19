package com.example.lab03;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AudioActivity extends AppCompatActivity implements ServiceConnection {
    SeekBar mSeekBar;
    Button backFifteen, forwardFifteen, pause, next, last;
    TextView songName;
    boolean playing = false;
    ArrayList<Uri> songs;
    Handler mSeekbarUpdateHandler = new Handler();
//    MediaPlayer mPlayer;
    GestureDetector gestureDetector;
    int currentIndex;
    MediaService mediaService;
    ArrayList<MediaSongVid> songVideos;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent2= new Intent(this, MediaService.class);
        bindService(intent2, this, BIND_AUTO_CREATE);
        setContentView(R.layout.activity_audio);
        Intent intent = new Intent(getApplicationContext(), MediaService.class);
        Bundle extras = getIntent().getExtras();
        songVideos = (ArrayList<MediaSongVid>)extras.get("MediaList");
        currentIndex = extras.getInt("CurrentIndex");
        MediaSongVid song = (MediaSongVid) extras.get("CurrentSong");
        intent.setAction(MediaService.ACTION_PLAY);
        intent.putExtra("Song", song);
//        intent.addFlags(PendingIntent.FLAG_IMMUTABLE);
        startService(intent);
        songName = findViewById(R.id.songName);
        mSeekBar = findViewById(R.id.seekBar);
        backFifteen = findViewById(R.id.backFifteen);
        forwardFifteen = findViewById(R.id.forwardFifteen);
        pause  = findViewById(R.id.pauseButton);
        next = findViewById(R.id.forwardButton);
        last = findViewById(R.id.backButton);

        setView(song);




        pause.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (mediaService.getmMediaPlayer().isPlaying()){
                    mediaService.buildNotification( mediaService.generateAction( android.R.drawable.ic_media_play, "Play", MediaService.ACTION_PLAY ) );

                    mediaService.getmMediaPlayer().pause();
                    pause.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_play));
                }
                else{
//                    mPlayer.start();
                    mediaService.buildNotification( mediaService.generateAction( android.R.drawable.ic_media_pause, "Pause", MediaService.ACTION_PAUSE ) );
                    mediaService.getmMediaPlayer().start();
                    pause.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_pause));

                }
            }
        });
        forwardFifteen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mediaService.getmMediaPlayer().pause();
                mediaService.getmMediaPlayer().seekTo(mediaService.getmMediaPlayer().getCurrentPosition() + 15000);
                mediaService.getmMediaPlayer().start();
            }
        });
        backFifteen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaService.getmMediaPlayer().pause();
                if (mediaService.getmMediaPlayer().getCurrentPosition() - 15000 < 0){
                    mediaService.getmMediaPlayer().seekTo(0);
                }
                else{
                    mediaService.getmMediaPlayer().seekTo(mediaService.getmMediaPlayer().getCurrentPosition() - 15000);

                }
                mediaService.getmMediaPlayer().start();
            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mediaService.getmMediaPlayer().seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });
        next.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                int i = currentIndex + 1;
                while (i  < songVideos.size()){
                    System.out.println(songVideos.get(i).getFormat());
                    if (songVideos.get(i).getFormat().equals("mp3")){
                        System.out.println("Hi");
                        changeSong(i);
                        currentIndex = i;
                        break;
                    }
                    i += 1;
                }
            }
        });
        last.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                int i = currentIndex - 1;
                while (i  >= 0){
                    System.out.println(songVideos.get(i).getFormat());
                    if (songVideos.get(i).getFormat().equals("mp3")){
                        changeSong(i);
                        currentIndex = i;
                        break;
                    }
                    i -= 1;
                }
            }
        });
        ImageView iv = findViewById(R.id.songImage);
        final float[] x = new float[2];
        iv.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:{

                        x[0] = motionEvent.getX();
//                        System.out.println(x[0]);
                        return true;
                    }
                    case MotionEvent.ACTION_UP:{
                        x[1] = motionEvent.getX();
//                        System.out.println(x[1]);

                        float diff = x[1] - x[0];
                        if (diff > 150 ){
                            System.out.println("Swipe right");
                            int i = currentIndex - 1;
                            while (i  >= 0){
                                System.out.println(songVideos.get(i).getFormat());
                                if (songVideos.get(i).getFormat().equals("mp3")){
                                    System.out.println("Hi");

                                    changeSong(i);
                                    currentIndex = i;
                                    break;
                                }
                                i -= 1;
                            }
                            return true;
                        }
                        else if(Math.abs(diff) > 150){
                            System.out.println("Swipe left");
                            int i = currentIndex + 1;
                            while (i  < songVideos.size()){
                                System.out.println(songVideos.get(i).getFormat());
                                if (songVideos.get(i).getFormat().equals("mp3")){
                                    System.out.println("Hi");
                                    changeSong(i);
                                    currentIndex = i;
                                    break;
                                }
                                i += 1;
                            }
                            return true;
                        }
                    }
                }


                return false;
            }
        });
//        NotificationCompat.Builder build = new NotificationCompat.Builder(this);
//        Notification notification =build.build();
//        NotificationManagerCompat.from(this).notify(1, notification);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notificationDialog();
//        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void changeSong(int index){
        MediaSongVid item = songVideos.get(index);
        setView(item);
        mSeekbarUpdateHandler.removeCallbacks(runnable);

        mediaService.getmMediaPlayer().reset();
        try {
            mediaService.getmMediaPlayer().setDataSource(getApplicationContext(), item.getUri());
            mediaService.getmMediaPlayer().prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }
        mSeekBar.setMax(mediaService.getmMediaPlayer().getDuration());
        mSeekbarUpdateHandler.postDelayed(runnable, 0);

        mediaService.getmMediaPlayer().start();
        pause.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_pause));
        mediaService.setSong(item);
        mediaService.buildNotification( mediaService.generateAction( android.R.drawable.ic_media_pause, "Pause", MediaService.ACTION_NEXT ) );

//        mediaService.buildNotification(MediaService.ACTION_NEXT);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void notificationDialog() {
        NotificationManager notificationManager = (NotificationManager)       getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "Channel1";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            notificationChannel.setDescription("Sample Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
//        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_background)
//                .setContentTitle("Track title")
//                .setContentText(" Artist")
//                .setStyle(new Notification.MediaStyle().setMediaSession(setMediaController(mPlayer.getAudioSessionId())));
        notificationManager.notify(1, notificationBuilder.build());
    }
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mSeekBar.setProgress(mediaService.getmMediaPlayer().getCurrentPosition());
            mSeekbarUpdateHandler.postDelayed(this, 50);

        }
    };
    public void setView(MediaSongVid media){
        songName.setText(media.getName());

    }
    @Override
    public void onBackPressed()
    {
        mediaService.getmMediaPlayer().pause();
        mSeekbarUpdateHandler.removeCallbacks(runnable);
        mediaService.getmMediaPlayer().release();
        Intent intent = new Intent(getApplicationContext(), mediaService.getClass());
        stopService(intent);
        finish();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onBindingComplete(){
//        Toast.makeText(getApplicationContext(), "Binding complete", Toast.LENGTH_SHORT).show();
        mediaService.getmMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                int i = currentIndex + 1;
                while (i  < songVideos.size()){
                    if (songVideos.get(i).getFormat().equals("mp3")){
                        changeSong(i);
                        currentIndex = i;
                        break;
                    }
                    i += 1;
                }
            }
        });
        changeSong(currentIndex);
//        mSeekBar.setMax(mPlayer.getDuration());
//        mSeekbarUpdateHandler.postDelayed(runnable, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mediaService = ((MediaService.customBinder)(iBinder)).getMediaService();
//        mPlayer = mediaService.getmMediaPlayer();
        onBindingComplete();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }
}