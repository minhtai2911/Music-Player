package com.example.musicplayer.service;

import static com.example.musicplayer.activity.MainActivity.songList;
import static com.example.musicplayer.activity.PlayingActivity.listSongs;
import static com.example.musicplayer.activity.PlayingActivity.mediaSessionCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
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

import com.example.musicplayer.Application;
import com.example.musicplayer.R;
import com.example.musicplayer.activity.PlayingActivity;
import com.example.musicplayer.interfaces.ActionPlaying;
import com.example.musicplayer.model.SongModel;
import com.example.musicplayer.receiver.NotificationReceiver;

import java.util.ArrayList;

public class MusicService extends Service {
    MyBinder myBinder = new MyBinder();
    ActionPlaying actionPlaying;
    MediaPlayer mediaPlayer;

    ArrayList<SongModel> tempListSongs = new ArrayList<>();
    Uri uri;
    int position = -1;
    @Override
    public void onCreate() {

        super.onCreate();
        tempListSongs = listSongs;

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
        int myPosition = intent.getIntExtra("servicePosition", -1);
        String actionName = intent.getStringExtra("ActionName");

        if(myPosition != -1){
            playMedia(myPosition);
        }
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

    private void playMedia(int StartPosition) {
        tempListSongs = listSongs;
        position = StartPosition;
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (tempListSongs != null) {
                createMediaPlayer(position);
                mediaPlayer.start();
            }
        }
        else{
            createMediaPlayer(position);
            mediaPlayer.start();
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
    public void createMediaPlayer(int positionInner){
        position = positionInner;
        uri = Uri.parse(tempListSongs.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getBaseContext(),uri);

    }
    public void showNotification(int playPauseBtn, float playbackSpeed) {
        Log.e("Check position in noti", "showNotification: " + position + " "+listSongs.size() );
        Intent intent = new Intent(this, PlayingActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Intent prevIntent = new Intent(this, NotificationReceiver.class).setAction(Application.ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent pauseIntent = new Intent(this, NotificationReceiver.class).setAction(Application.ACTION_PLAY);
        PendingIntent pausePending = PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent nextIntent = new Intent(this, NotificationReceiver.class).setAction(Application.ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE);

        byte[] picture = null;
        picture = getImg(tempListSongs.get(PlayingActivity.position).getPath());
        Bitmap thumb = null;
        if(picture != null){
            thumb = BitmapFactory.decodeByteArray(picture, 0, picture.length);
        }
        else {
            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.imgitem);
        }
        Notification notification = new NotificationCompat.Builder(this, Application.CHANNEL_ID_2)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(playPauseBtn)
                .setLargeIcon(thumb)
                .setContentTitle(tempListSongs.get(PlayingActivity.position).getTitle())
                .setContentText(tempListSongs.get(PlayingActivity.position).getArtist())
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
        startForeground(2,notification);
    }
    private byte[] getImg(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        return retriever.getEmbeddedPicture();
    }

    public void setCallBack(ActionPlaying actionPlaying){
        this.actionPlaying = actionPlaying;
    }
}
