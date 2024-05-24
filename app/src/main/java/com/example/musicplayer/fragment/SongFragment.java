package com.example.musicplayer.fragment;

import static com.example.musicplayer.activity.MainActivity.songList;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.SongAdapter;

public class SongFragment extends Fragment {
    RecyclerView recyclerView;
    SongAdapter songAdapter;
    TextView title;


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_song, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_song);
        title = view.findViewById(R.id.txtSong);
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
//        CheckConnected.isConnectedToInternet(getContext());
        super.onResume();
    }
}