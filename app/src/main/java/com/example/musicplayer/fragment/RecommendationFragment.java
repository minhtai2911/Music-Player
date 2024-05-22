package com.example.musicplayer.fragment;

import static com.example.musicplayer.activity.MainActivity.songList;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.RecommendationAdapter;
import com.example.musicplayer.adapter.SongAdapter;

public class RecommendationFragment extends Fragment {
    RecyclerView recyclerView;
    RecommendationAdapter recommendationAdapter;
    TextView title;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_recommendation, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_recommendation);
        title = view.findViewById(R.id.txtSong);
        if (!(songList.isEmpty())) {
            Log.e("songListRecommendation", "onCreateView: " + songList.size());
            recommendationAdapter = new RecommendationAdapter(getActivity(), songList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(recommendationAdapter);
        }
        return view;
    }
}
