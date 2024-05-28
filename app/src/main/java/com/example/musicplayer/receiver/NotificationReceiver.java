package com.example.musicplayer.receiver;

import static com.example.musicplayer.Application.ACTION_NEXT;
import static com.example.musicplayer.Application.ACTION_PLAY;
import static com.example.musicplayer.Application.ACTION_PREVIOUS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.musicplayer.service.MusicService;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String actionName = intent.getAction();
        Intent serviceIntent = new Intent(context, MusicService.class);
        if(actionName != null){
            switch (actionName){
                case ACTION_PLAY:
                    serviceIntent.putExtra("ActionName", "playPause");
                    context.startService(serviceIntent);
                    break;
                case ACTION_NEXT:
                    serviceIntent.putExtra("ActionName", "playNext");
                    context.startService(serviceIntent);
                    break;
                case ACTION_PREVIOUS:
                    serviceIntent.putExtra("ActionName", "playPrevious");
                    context.startService(serviceIntent);
                    break;
            }
        }
    }
}
