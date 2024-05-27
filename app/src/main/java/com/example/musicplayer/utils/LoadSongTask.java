package com.example.musicplayer.utils;

import static com.example.musicplayer.activity.MainActivity.songList;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.musicplayer.model.SongModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class LoadSongTask extends AsyncTask<Void, Void, ArrayList<SongModel>> {
    private FirebaseFirestore db;

    public LoadSongTask() {
        db = FirebaseFirestore.getInstance();
    }
    MediaMetadataRetriever retriever;
    @Override
    protected ArrayList<SongModel> doInBackground(Void... voids) {
        ArrayList<SongModel> songs = new ArrayList<>();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Task<QuerySnapshot> task = db.collection("songs_test").get();
        try {
            Tasks.await(task);
            if (task.isSuccessful()) {
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
//                        SongModel song = new SongModel(path,title,artist,duration,1);
//                        songs.add(song);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Log.d("Firebase", "Error getting documents: ", task.getException());
            }
        } catch (ExecutionException | InterruptedException e) {
            Log.d("Firebase", "Error: ", e);
        }
        return songs;
    }

    @Override
    protected void onPostExecute(ArrayList<SongModel> songs) {
        for (SongModel song : songs) {
            songList.add(song);
        }
    }
}
