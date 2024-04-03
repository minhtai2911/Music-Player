package com.example.musicplayer;

import static com.example.musicplayer.activity.MainActivity.libraryList;
import static com.example.musicplayer.activity.MainActivity.songList;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.musicplayer.adapter.PlaylistAdapter;
import com.example.musicplayer.adapter.SongAdapter;

public class AlbumFragment extends Fragment {
    RecyclerView recyclerView;
    PlaylistAdapter playlistAdapter;
    TextView title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_album);
        title = view.findViewById(R.id.txtAlbums);
        if (!(libraryList.isEmpty())) {
            playlistAdapter = new PlaylistAdapter(getActivity(), libraryList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(playlistAdapter);
        }
        return view;
    }
}