package com.example.musicplayer.fragment;

import static com.example.musicplayer.activity.MainActivity.currPlayedSong;
import static com.example.musicplayer.activity.MainActivity.getQueuePlaying;
import static com.example.musicplayer.activity.MainActivity.setQueuePlaying;
import static com.example.musicplayer.activity.MainActivity.songList;
import static com.example.musicplayer.activity.PlayingActivity.isPlayable;
import static com.example.musicplayer.tool.NetworkChangeReceiver.checkConnected;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.musicplayer.R;
import com.example.musicplayer.activity.MainActivity;
import com.example.musicplayer.activity.QueuePlayingActivity;
import com.example.musicplayer.adapter.SongAdapter;
import com.example.musicplayer.model.SongModel;

import java.util.ArrayList;
import java.util.Collections;

public class SongFragment extends Fragment {
    RecyclerView recyclerView;
    public static SongAdapter songAdapter;
    TextView title, artist;


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_song, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_song);
        title = view.findViewById(R.id.txtSong);
        artist = view.findViewById(R.id.artist_song);
        if (!(songList.isEmpty())) {
            songAdapter = new SongAdapter(getActivity(), songList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(songAdapter);
        }
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @SuppressLint("NotifyDataSetChanged")
    public static void staticUpdateHomeSong(){
        if(songAdapter==null){
            return;
        }
        ArrayList<SongModel> playableSong = new ArrayList<>();
        for(SongModel song: songList){
            if(isPlayable(song, checkConnected)){
                playableSong.add(song);
            }
        }
        Collections.shuffle(playableSong);
        songAdapter.arrayListSong = playableSong;
        songAdapter.notifyDataSetChanged();
    }
}