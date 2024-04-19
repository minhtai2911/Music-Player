package com.example.musicplayer.fragment;

import static com.example.musicplayer.activity.MainActivity.songList;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicplayer.R;
import com.example.musicplayer.activity.InsertNhacThuVienActivity;
import com.example.musicplayer.adapter.InsertNhacThuVienAdapter;
import com.example.musicplayer.adapter.SearchAdapter;
import com.example.musicplayer.model.ListLibraryModel;
import com.example.musicplayer.model.SongModel;

import java.util.ArrayList;
import java.util.List;

public class Fragment_insert_nhac_thu_vien extends Fragment {
    View view;
    SearchView searchView;
    RecyclerView recyclerView;
    TextView textViewNull;
    InsertNhacThuVienAdapter searchAdapter;
    ArrayList<SongModel> listSongs;
    ImageView btnBack;
    InsertNhacThuVienActivity thisLibraryvA;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        listSongs = songList;
        view = inflater.inflate(R.layout.fragment_insert_nhac_thu_vien, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_search);
        textViewNull = view.findViewById(R.id.search_null);
        btnBack = view.findViewById(R.id.backBtn);
        searchView = view.findViewById(R.id.searchView);
        thisLibraryvA =(InsertNhacThuVienActivity) getActivity();
        searchAdapter = new InsertNhacThuVienAdapter(getActivity(), listSongs,thisLibraryvA.getId());
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
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisLibraryvA.finish();
            }
        });
        return view;
    }
    private void search(String song) {
        thisLibraryvA =(InsertNhacThuVienActivity) getActivity();
        ArrayList<SongModel> filteredSongs = new ArrayList<>();

        for (SongModel songModel : listSongs) {
            String title = songModel.getTitle().toLowerCase();
            String artist = songModel.getArtist().toLowerCase();

            if (title.contains(song.toLowerCase()) || artist.contains(song.toLowerCase())) {
                filteredSongs.add(songModel);
            }
        }

        if (filteredSongs.isEmpty()) {
            textViewNull.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            searchAdapter = new InsertNhacThuVienAdapter(getActivity(), filteredSongs, thisLibraryvA.getId());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(searchAdapter);
            textViewNull.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
