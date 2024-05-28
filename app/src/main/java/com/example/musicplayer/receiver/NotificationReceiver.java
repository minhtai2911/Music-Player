package com.example.musicplayer.receiver;

import static com.example.musicplayer.Application.ACTION_NEXT;
import static com.example.musicplayer.Application.ACTION_PLAY;
import static com.example.musicplayer.Application.ACTION_PREVIOUS;
import static com.example.musicplayer.activity.PlayingActivity.musicService;

import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.musicplayer.service.MusicService;

import java.util.List;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String actionName = intent.getAction();
        Intent serviceIntent = new Intent(context, MusicService.class);
            if (actionName != null) {
                switch (actionName) {
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
    public boolean isAppRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

        if (runningAppProcesses != null) {
            String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : runningAppProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
