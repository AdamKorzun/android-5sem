package com.example.lab03;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.Rating;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class MediaService extends Service {
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_STOP = "action_stop";
    public Notification notification;
    public MediaSongVid getSong() {
        return song;
    }

    public void setSong(MediaSongVid song) {
        this.song = song;
    }

    private MediaSongVid song;
    public MediaPlayer getmMediaPlayer() {
        return mMediaPlayer;
    }

    public void setmMediaPlayer(MediaPlayer mMediaPlayer) {
        this.mMediaPlayer = mMediaPlayer;
    }

    private MediaPlayer mMediaPlayer;
    private MediaSessionManager mManager;
    private MediaSession mSession;
    private MediaController mController;
    private Binder binder = new customBinder();
    public class customBinder extends Binder {
        public MediaService getMediaService(){
            return MediaService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void handleIntent( Intent intent ) {

        if( intent == null || intent.getAction() == null )
            return;
        if (intent.getExtras()!= null){
            song = (MediaSongVid) intent.getExtras().get("Song");
        }
//        Toast.makeText(getApplicationContext(), "intent called", Toast.LENGTH_SHORT).show();
//        String action = intent.getAction();
//        if( action.equalsIgnoreCase( ACTION_PLAY ) ) {
//            mController.getTransportControls().play();
//        } else if( action.equalsIgnoreCase( ACTION_PAUSE ) ) {
//            mController.getTransportControls().pause();
//        } else if( action.equalsIgnoreCase( ACTION_PREVIOUS ) ) {
//            mController.getTransportControls().skipToPrevious();
//        } else if( action.equalsIgnoreCase( ACTION_NEXT ) ) {
//            mController.getTransportControls().skipToNext();
//        } else if( action.equalsIgnoreCase( ACTION_STOP ) ) {
//            mController.getTransportControls().stop();
//        }
    }

    public Notification.Action generateAction(int icon, String title, String intentAction ) {
        Intent intent = new Intent( getApplicationContext(), MediaService.class );
        intent.setAction( intentAction );
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, PendingIntent.FLAG_MUTABLE);
        return new Notification.Action.Builder( icon, title, pendingIntent ).build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void buildNotification(Notification.Action action ) {
        Notification.MediaStyle style = new Notification.MediaStyle();

        Intent intent = new Intent( getApplicationContext(), MediaService.class );
//        intent.setAction( ACTION_STOP );
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, PendingIntent.FLAG_MUTABLE);
        String NOTIFICATION_CHANNEL_ID = "Channel1";
        NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            notificationChannel.setDescription("Sample Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Notification.Builder builder = new Notification.Builder( this ,NOTIFICATION_CHANNEL_ID);

        if (song != null){
            builder.setSmallIcon(R.drawable.ic_note)
                    .setContentTitle( song.getName() )
                    .setContentText( song.getAuthor())
                    .setDeleteIntent( pendingIntent )
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_note))
                    .setStyle(style);
        }
        else{
            builder.setSmallIcon(R.drawable.ic_note)
                    .setContentTitle( "Media Title" )
                    .setContentText( "Media Artist" )
                    .setDeleteIntent( pendingIntent )
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_note))
                    .setStyle(style);
        }


//        builder.addAction( generateAction( android.R.drawable.ic_media_previous, "Previous", ACTION_PREVIOUS ) );
//        builder.addAction( action );
//        builder.addAction( generateAction( android.R.drawable.ic_media_next, "Next", ACTION_NEXT ) );
//        style.setShowActionsInCompactView(0,1,2);
        notification = builder.build();
        notificationManager.notify( 1, notification );


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if( mManager == null ) {
            initMediaSessions(null);
        }

        handleIntent( intent );
        return super.onStartCommand(intent, flags, startId);
    }

    private void initMediaSessions(@Nullable MediaPlayer mp) {
        if (mp != null){
            mMediaPlayer = mp;
        }else{
            mMediaPlayer = new MediaPlayer();
        }

        mSession = new MediaSession(getApplicationContext(), "simple player session");
        mController =new MediaController(getApplicationContext(), mSession.getSessionToken());

        mSession.setCallback(new MediaSession.Callback(){
                                 @RequiresApi(api = Build.VERSION_CODES.O)
                                 @Override
                                 public void onPlay() {
                                     super.onPlay();
                                     mMediaPlayer.start();

                                     buildNotification( generateAction( android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE ) );
                                 }

                                 @RequiresApi(api = Build.VERSION_CODES.O)
                                 @Override
                                 public void onPause() {
                                     super.onPause();
                                     mMediaPlayer.pause();
                                     Toast.makeText(getApplicationContext(), String.valueOf(mMediaPlayer.getDuration()), Toast.LENGTH_SHORT).show();
                                     buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
                                 }

                                 @RequiresApi(api = Build.VERSION_CODES.O)
                                 @Override
                                 public void onSkipToNext() {
                                     super.onSkipToNext();
//                                     Log.e( "MediaPlayerService", "onSkipToNext");
                                     //Change media here
                                     buildNotification( generateAction( android.R.drawable.ic_media_pause, "Pause", ACTION_NEXT ) );
                                 }

                                 @RequiresApi(api = Build.VERSION_CODES.O)
                                 @Override
                                 public void onSkipToPrevious() {
                                     super.onSkipToPrevious();
                                     //Change media here
                                     buildNotification( generateAction( android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE ) );
                                 }


                                 @Override
                                 public void onRewind() {
                                     super.onRewind();
                                 }

                                 @Override
                                 public void onStop() {
                                     super.onStop();
                                     //Stop media player here
                                     NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                     notificationManager.cancel( 1 );
                                     Intent intent = new Intent( getApplicationContext(), MediaService.class );
                                     stopService( intent );
                                 }

                                 @Override
                                 public void onSeekTo(long pos) {
                                     super.onSeekTo(pos);
                                 }

                                @Override
                                public void onFastForward() {
                                super.onFastForward();
                                }


                                @Override
                                 public void onSetRating(Rating rating) {
                                     super.onSetRating(rating);
                                 }
                             }
        );
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mSession.release();
        return super.onUnbind(intent);
    }
}
