package com.example.musicplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class LogEventReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.example.LOG_EVENT".equals(intent.getAction())) {
            String log = intent.getStringExtra("log");
            // Handle the log event, for example, show a toast
            Toast.makeText(context, "Log Event: " + log, Toast.LENGTH_LONG).show();
        }
    }
}
