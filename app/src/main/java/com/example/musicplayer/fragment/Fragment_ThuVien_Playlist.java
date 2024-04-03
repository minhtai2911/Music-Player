package com.example.musicplayer.fragment;

import static com.example.musicplayer.activity.MainActivity.libraryList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.musicplayer.R;
import com.example.musicplayer.activity.MainActivity;
import com.example.musicplayer.adapter.LibraryPlaylistAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Fragment_ThuVien_Playlist extends Fragment {
    View view;
    LibraryPlaylistAdapter thuVienPlayListAdapter;
//    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerViewThuVienPlayList;
    TextView tenThuVienPlayList;
    MainActivity hm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_listlibrary_playlist, container, false);
        recyclerViewThuVienPlayList = view.findViewById(R.id.recyclerviewthuvienplaylist);
        tenThuVienPlayList = view.findViewById(R.id.textviewthuvienplaylist);
        if(libraryList == null || !(libraryList.isEmpty()))
        {
            thuVienPlayListAdapter = new LibraryPlaylistAdapter(getActivity(), libraryList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerViewThuVienPlayList.setLayoutManager(linearLayoutManager);
            recyclerViewThuVienPlayList.setAdapter(thuVienPlayListAdapter);
        }
        return view;
    }
}
