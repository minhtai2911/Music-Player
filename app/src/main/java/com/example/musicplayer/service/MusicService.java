package com.example.musicplayer.service;

import static com.example.musicplayer.activity.PlayingActivity.listSongs;
import static com.example.musicplayer.activity.PlayingActivity.position;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.musicplayer.interfaces.ActionPlaying;
import com.example.musicplayer.model.SongModel;

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
    public void createMediaPlayer(int position){
        uri = Uri.parse(tempListSongs.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getBaseContext(),uri);
    }
    public void setCallBack(ActionPlaying actionPlaying){
        this.actionPlaying = actionPlaying;
    }
}
