package com.example.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LogMonitoringService extends Service {
    private Thread logThread;
    private boolean isRunning;

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        logThread = new Thread(new Runnable() {
            @Override
            public void run() {
                captureLogs();
            }
        });
        logThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        if (logThread != null) {
            logThread.interrupt();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void captureLogs() {
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while (isRunning && (line = bufferedReader.readLine()) != null) {
                if (line.contains("ProfileInstaller") && line.contains("Installing profile for com.example.musicplayer")) {
                    // Handle the log event here
                    // For example, broadcast an Intent or update a database
                    handleLogEvent(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleLogEvent(String log) {
        // Implement your logic to handle the log event
        // For example, you can broadcast an Intent
        Intent intent = new Intent("com.example.LOG_EVENT");
        intent.putExtra("log", log);
        sendBroadcast(intent);
    }
}

