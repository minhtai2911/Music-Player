package com.example.musicplayer.fragment;

import static com.example.musicplayer.activity.MainActivity.libraryList;
import static com.example.musicplayer.activity.MainActivity.songList;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.musicplayer.adapter.PlaylistAdapter;
import com.example.musicplayer.adapter.SongAdapter;

import com.example.musicplayer.R;

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
        Log.d("Danh sach nhac: ", String.valueOf(songList));
        Log.d("Danh sach bai hat: ", String.valueOf(libraryList));
        if (libraryList != null) {
            Log.d("Danh sach bai hat: ", String.valueOf(libraryList));
            System.out.println("Danh sach bai hat: "+ libraryList);
            playlistAdapter = new PlaylistAdapter(getActivity(), libraryList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(playlistAdapter);
        }
        return view;
    }
}