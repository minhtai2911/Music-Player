package com.example.musicplayer.tool;

import static androidx.core.content.ContextCompat.startActivity;
import static com.example.musicplayer.activity.MainActivity.addSongToQueue;
import static com.example.musicplayer.activity.MainActivity.context;
import static com.example.musicplayer.activity.MainActivity.currPlayedPlaylistID;
import static com.example.musicplayer.activity.MainActivity.currPlayedSong;
import static com.example.musicplayer.activity.MainActivity.getQueuePlaying;
import static com.example.musicplayer.activity.MainActivity.homeFragment;
import static com.example.musicplayer.activity.MainActivity.setQueuePlaying;
import static com.example.musicplayer.activity.MainActivity.songList;
import static com.example.musicplayer.activity.MainActivity.tabLayout;
import static com.example.musicplayer.activity.PlayingActivity.listSongs;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.musicplayer.R;
import com.example.musicplayer.activity.AddToPlaylistActivity;
import com.example.musicplayer.activity.MainActivity;
import com.example.musicplayer.activity.PlayingActivity;
import com.example.musicplayer.activity.PlaylistActivity;
import com.example.musicplayer.activity.QueuePlayingActivity;
import com.example.musicplayer.fragment.LibraryFragment;
import com.example.musicplayer.fragment.SongFragment;
import com.example.musicplayer.model.SongModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class NetworkChangeReceiver extends BroadcastReceiver {
    FirebaseFirestore firebaseFirestore;
    public static boolean checkConnected=false;
    public static Dialog dialog;
    public static boolean isLoadedSongFromDataBase = false;

    public static Context appContext;
    public static final String NETWORK_CHANGE_ACTION = "com.example.musicplayer.NETWORK_CHANGE";
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent networkChangeIntent = new Intent(NETWORK_CHANGE_ACTION);
        if (isConnectedToNetwork(context)) {
            checkConnected = true;
            if(!isLoadedSongFromDataBase){
                loadSongFromDatabase(context);
            } else {
                offToOnl();
            }
            networkChangeIntent.putExtra("checkConnected", checkConnected);
            LocalBroadcastManager.getInstance(context).sendBroadcast(networkChangeIntent);
            Log.e("checkInternetConnect", "connect");
            Toast.makeText(context, "You're online", Toast.LENGTH_SHORT).show();
        } else {
            checkConnected = false;
            networkChangeIntent.putExtra("checkConnected", checkConnected);
            Toast.makeText(context, "You're offline", Toast.LENGTH_SHORT).show();
            LocalBroadcastManager.getInstance(context).sendBroadcast(networkChangeIntent);
            onlToOff();
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

    public void loadSongFromDatabase(Context context) {
        showLoadingLayer(appContext);
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
                        isLoadedSongFromDataBase=true;
                        offToOnl();
                        stopLoadingLayer();
                        try {
                            retriever.release();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }

    public static void onlToOff(){
        if(tabLayout!=null && homeFragment!=null && tabLayout.getSelectedTabPosition()==0){
            homeFragment.startRandomSong();
        }
        if(LibraryFragment.libraryAdapter!=null){
            LibraryFragment.staticUpdateLibrary();
        }
        if(PlaylistActivity.playlistAdapter!=null && PlaylistActivity.recommendSongsToPlaylistAdapter!=null){
            PlaylistActivity.staticUpdateRecommend();
        }
        PlayingActivity.staticUpdateCurrentsong();
        PlayingActivity.staticUpdateQueue();
        SongFragment.staticUpdateHomeSong();
    }

    public static void offToOnl(){
        if(LibraryFragment.libraryAdapter!=null){
            LibraryFragment.staticUpdateLibrary();
        }
        if(PlaylistActivity.playlistAdapter!=null && PlaylistActivity.recommendSongsToPlaylistAdapter!=null){
            PlaylistActivity.staticUpdateRecommend();
        }
        PlayingActivity.staticUpdateCurrentsong();
        PlayingActivity.staticUpdateQueue();
        SongFragment.staticUpdateHomeSong();
    }

    public static void showLoadingLayer(Context context) {
        if(context==null){
            return;
        }
        if(tabLayout!=null && homeFragment!=null && tabLayout.getSelectedTabPosition()==0){
            homeFragment.stopRandomSong();
        }
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading_layer);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public static void stopLoadingLayer(){
        if(tabLayout!=null && homeFragment!=null && tabLayout.getSelectedTabPosition()==0){
            homeFragment.startRandomSong();
        }
        dialog.dismiss();
    }
}

