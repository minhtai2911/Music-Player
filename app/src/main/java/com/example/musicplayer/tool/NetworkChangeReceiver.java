package com.example.musicplayer.tool;

import static androidx.core.content.ContextCompat.startActivity;
import static com.example.musicplayer.activity.MainActivity.songList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.musicplayer.activity.MainActivity;
import com.example.musicplayer.activity.PlayingActivity;
import com.example.musicplayer.model.SongModel;
import com.example.musicplayer.utils.LoadSongTask;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class NetworkChangeReceiver extends BroadcastReceiver {
    FirebaseFirestore firebaseFirestore;
    public boolean checkConnected;
    public static final String NETWORK_CHANGE_ACTION = "com.example.musicplayer.NETWORK_CHANGE";
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent networkChangeIntent = new Intent(NETWORK_CHANGE_ACTION);
        if (isConnectedToNetwork(context)) {

            checkConnected = true;
            networkChangeIntent.putExtra("checkConnected", checkConnected);
            LocalBroadcastManager.getInstance(context).sendBroadcast(networkChangeIntent);
            Log.e("checkInternetConnect", "connect");
            Toast.makeText(context, "you're online", Toast.LENGTH_SHORT).show();
        } else {
            checkConnected = false;
            networkChangeIntent.putExtra("checkConnected", checkConnected);
            Toast.makeText(context, "you're offline", Toast.LENGTH_LONG).show();
            LocalBroadcastManager.getInstance(context).sendBroadcast(networkChangeIntent);
            if(songList.size() != 0) {
                Iterator<SongModel> iterator = songList.iterator();
                while (iterator.hasNext()) {
                    SongModel song = iterator.next();
                    if (song.getType() == 1) {
                        iterator.remove();
                    }
                }
            }
//            startActivity();
            Log.e("checkInternetConnect", "not connect");
        }
    }

    public boolean isConnectedToNetwork(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return (activeNetwork != null && activeNetwork.isConnected());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public void loadSongFromDatabase() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("songs_test")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Map<String, Object> data = document.getData();
                                String urlTemp = (String) data.get("url");
                                retriever.setDataSource(urlTemp);
                                String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                                String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                                Uri uri = Uri.parse(urlTemp);
                                String path = uri.toString();
                                SongModel song = new SongModel(path,title,artist,duration,1);
                                songList.add(song);
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            retriever.release();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }

}

