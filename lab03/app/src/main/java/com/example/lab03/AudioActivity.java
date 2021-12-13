package com.example.lab03;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
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
import java.util.ArrayList;

public class AudioActivity extends AppCompatActivity {
    SeekBar mSeekBar;
    Button backFifteen, forwardFifteen, pause, next, last;
    TextView songName;
    boolean playing = false;
    ArrayList<Uri> songs;
    Handler mSeekbarUpdateHandler = new Handler();
    MediaPlayer mPlayer;
    GestureDetector gestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        songName = findViewById(R.id.songName);
        mSeekBar = findViewById(R.id.seekBar);
        backFifteen = findViewById(R.id.backFifteen);
        forwardFifteen = findViewById(R.id.forwardFifteen);
        pause  = findViewById(R.id.pauseButton);
        next = findViewById(R.id.forwardButton);
        last = findViewById(R.id.backButton);
        Bundle extras = getIntent().getExtras();

        System.out.println(extras.get("Filepath"));
        Uri songUri = (Uri)extras.get("CurrentSong");
        mPlayer =   MediaPlayer.create(getApplicationContext(), songUri);
        mPlayer.start();
        Cursor returnCursor =
                getContentResolver().query(songUri, null, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);

        returnCursor.moveToFirst();
        songName.setText(returnCursor.getString(nameIndex));
        mSeekBar.setMax(mPlayer.getDuration());
        mSeekbarUpdateHandler.postDelayed(runnable, 0);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlayer.isPlaying()){
                    mPlayer.pause();
                    pause.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_play));
                }
                else{
                    mPlayer.start();
                    pause.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_pause));

                }
                System.out.println(getPath(songUri));
            }
        });
        forwardFifteen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPlayer.pause();
                mPlayer.seekTo(mPlayer.getCurrentPosition() + 15000);
                mPlayer.start();
            }
        });
        backFifteen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer.pause();
                if (mPlayer.getCurrentPosition() - 15000 < 0){
                    mPlayer.seekTo(0);
                }
                else{
                    mPlayer.seekTo(mPlayer.getCurrentPosition() - 15000);

                }
                mPlayer.start();
            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });
        ImageView iv = findViewById(R.id.songImage);
        final float[] x = new float[2];
//        gestureDetector = new GestureDetector(AudioActivity.this, this);
        iv.setOnTouchListener(new View.OnTouchListener() {
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
                            return true;
                        }
                        else if(Math.abs(diff) > 150){
                            System.out.println("Swipe left");
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationDialog();
        }
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
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Tutorialspoint")
                //.setPriority(Notification.PRIORITY_MAX)
                .setContentTitle("sample notification")
                .setContentText("This is sample notification")
                .setContentInfo("Information");
        notificationManager.notify(1, notificationBuilder.build());
    }
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mSeekBar.setProgress(mPlayer.getCurrentPosition());
            mSeekbarUpdateHandler.postDelayed(this, 50);

        }
    };
    public String getPath(Uri uri) {

        String path = null;
        String[] projection = { MediaStore.Files.FileColumns.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if(cursor == null){
            path = uri.getPath();
        }
        else{
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }

        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
    }
    @Override
    public void onBackPressed()
    {
        mPlayer.pause();
        mSeekbarUpdateHandler.removeCallbacks(runnable);
        mPlayer.release();
        finish();
    }

}