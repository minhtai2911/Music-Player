package com.example.musicplayer.service;

import static com.example.musicplayer.activity.MainActivity.currPlayedSong;
import static com.example.musicplayer.activity.PlayingActivity.mediaSessionCompat;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

import com.example.musicplayer.Application;
import com.example.musicplayer.R;
import com.example.musicplayer.activity.PlayingActivity;
import com.example.musicplayer.interfaces.ActionPlaying;
import com.example.musicplayer.receiver.NotificationReceiver;

import java.io.IOException;


public class MusicService extends Service {
    MyBinder myBinder = new MyBinder();
    Notification notification;
    ActionPlaying actionPlaying;
    MediaPlayer mediaPlayer;
    Uri uri;
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind", "onBind: Method" );
        return myBinder;
    }
    public class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String songPath = intent.getStringExtra("songPath");
        if(songPath!=null){
            playMedia(songPath);
        }
        String actionName = intent.getStringExtra("ActionName");

        if(actionName!= null){
            switch (actionName){
                case "playPause":
                    if(actionPlaying != null){
                        actionPlaying.playPauseBtnClicked();
                    }
                    break;
                case "playNext":
                    if(actionPlaying != null){
                        actionPlaying.nextBtnClicked();
                    }
                    break;
                case "playPrevious":
                    if(actionPlaying != null){
                        actionPlaying.prevBtnClicked();
                    }
                    break;
            }
        }

        return START_STICKY;
    }
    public void quit() {
        pause();
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE);
        stopSelf();
    }

    private void playMedia(String songPath) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        } else {

        }
        mediaPlayer = new MediaPlayer();
        try {
            uri = Uri.parse(songPath);
            mediaPlayer.setDataSource(getBaseContext(), uri);
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
            });
            mediaPlayer.setOnBufferingUpdateListener((mp, percent) -> {
                Log.d("Loading", "Buffering: " + percent + "%");
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                // Xử lý lỗi
                Log.e("this is error msg", "Error occurred: what=" + what + ", extra=" + extra);
                return true;
            });
            mediaPlayer.prepareAsync(); // Sử dụng prepareAsync để load dữ liệu không đồng bộ
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("this is error msg", "Failed to set data source", e);
        }
    }



    public void start(){
        mediaPlayer.start();
    }
    public void stop(){
        mediaPlayer.stop();
    }
    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }
    public void release(){
        mediaPlayer.release();
    }
    public void pause(){
        mediaPlayer.pause();
    }
    public int getDuration(){
        return mediaPlayer.getDuration();
    }
    public void seekTo(int position){
        mediaPlayer.seekTo(position);
    }
    public int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }
    public void setNullForMediaPlayer() {
        mediaPlayer.release();
        mediaPlayer.stop();
        mediaPlayer = null;
    }


    public void showNotification(int playPauseBtn, float playbackSpeed) {
        Intent intent = new Intent(this, PlayingActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Intent prevIntent = new Intent(this, NotificationReceiver.class).setAction(Application.ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent pauseIntent = new Intent(this, NotificationReceiver.class).setAction(Application.ACTION_PLAY);
        PendingIntent pausePending = PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent nextIntent = new Intent(this, NotificationReceiver.class).setAction(Application.ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE);

        byte[] picture = null;
        picture = currPlayedSong.getImg();
        Bitmap thumb = null;
        if(picture != null){
            thumb = BitmapFactory.decodeByteArray(picture, 0, picture.length);
        }
        else {
            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.imgitem);
        }
        notification = new NotificationCompat.Builder(this, Application.CHANNEL_ID_1)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(playPauseBtn)
                .setLargeIcon(thumb)
                .setContentTitle(currPlayedSong.getTitle())
                .setContentText(currPlayedSong.getArtist())
                .addAction(R.drawable.iconprevious, "Previous", prevPending)
                .addAction(playPauseBtn, "Pause", pausePending)
                .addAction(R.drawable.iconnext, "Next", nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken()).setShowActionsInCompactView(0,1,2))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .build();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            mediaSessionCompat.setMetadata(new MediaMetadataCompat.Builder()
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, Long.parseLong(String.valueOf(mediaPlayer.getDuration())))
                    .build());
            mediaSessionCompat.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, Long.parseLong(String.valueOf(mediaPlayer.getCurrentPosition())), playbackSpeed)
                    .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                    .build());
        }
        startForeground(1,notification);
    }

    public void setCallBack(ActionPlaying actionPlaying){
        this.actionPlaying = actionPlaying;
    }
}
