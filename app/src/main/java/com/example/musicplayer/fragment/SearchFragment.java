package com.example.musicplayer.fragment;

import static com.example.musicplayer.activity.MainActivity.songList;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.SearchAdapter;
import com.example.musicplayer.model.SongModel;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchFragment extends Fragment {
    View view;
    SearchView searchView;
    RecyclerView recyclerView;
    TextView textViewNull;
    SearchAdapter searchAdapter;
    HashMap<Integer,SongModel> listSongs = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        for (int i = 0;i < songList.size();i++) {
            listSongs.put(i, songList.get(i));
        }
        view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_search);
        textViewNull = view.findViewById(R.id.search_null);
        searchView = view.findViewById(R.id.searchView);
        searchAdapter = new SearchAdapter(getActivity(), listSongs, null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(searchAdapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String search) {
                search(search);
                return false;
            }
        });
        return view;
    }

    private void search(String song) {
        listSongs = new HashMap<>();
        for (int i = 0; i<songList.size(); i++) {
            String title = songList.get(i).getTitle().toLowerCase();
            String artist = songList.get(i).getArtist().toLowerCase();

            if (title.contains(song.toLowerCase()) || artist.contains(song.toLowerCase())) {
                listSongs.put(i,songList.get(i));
            }
        }

        if (listSongs.isEmpty()) {
            textViewNull.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            searchAdapter = new SearchAdapter(getActivity(), listSongs, null);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(searchAdapter);
            textViewNull.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}