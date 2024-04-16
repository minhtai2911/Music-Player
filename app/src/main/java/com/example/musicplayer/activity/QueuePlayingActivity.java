package com.example.musicplayer.activity;

import static com.example.musicplayer.activity.MainActivity.libraryList;
import static com.example.musicplayer.activity.MainActivity.prevPosition;
import static com.example.musicplayer.activity.MainActivity.queuePlaying;
import static com.example.musicplayer.activity.MainActivity.songList;
import static com.example.musicplayer.activity.PlayingActivity.currentPostion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.adapter.DanhsachbaihatAdapter;
import com.example.musicplayer.adapter.QueuePlayingAdapter;
import com.example.musicplayer.adapter.SearchAdapter;
import com.example.musicplayer.model.LibraryModel;
import com.example.musicplayer.model.ListLibraryModel;
import com.example.musicplayer.model.SongModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class QueuePlayingActivity extends AppCompatActivity {
    RecyclerView recyclerViewQueue;
    RecyclerView recyclerViewSearch;
    SearchView searchView;
    HashMap<Integer,SongModel> listSongs = new HashMap<>();
    SearchAdapter searchAdapter;
    QueuePlayingAdapter queuePlayingAdapter;
     int currentPositionIntent;
    TextView textViewNull;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int i = 0;i < songList.size();i++) {
            listSongs.put(i, songList.get(i));
        }
        setContentView(R.layout.activity_queueplaying);
        AnhXa();
        DataIntent();
        GetDataPlaylist();
        overridePendingTransition(R.anim.anim_intent_in, R.anim.anim_intent_out);
        searchAdapter = new SearchAdapter(this, listSongs, queuePlayingAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewSearch.setLayoutManager(linearLayoutManager);
        recyclerViewSearch.setAdapter(searchAdapter);
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

//        swipeRefreshLayout = findViewById(R.id.swipedanhsachbaihat);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Intent intent = getIntent();
//                if(intent.hasExtra("idthuvienplaylist")){
//                    GetDataThuVienPlayList(String.valueOf(thuVienPlayList.getIDThuVienPlayList()));
//                    dsbhthuvienplaylistadapter.notifyDataSetChanged();
//                }
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });

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
            recyclerViewSearch.setVisibility(View.GONE);
        } else {
            searchAdapter = new SearchAdapter(QueuePlayingActivity.this, listSongs, queuePlayingAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(QueuePlayingActivity.this);
            recyclerViewSearch.setLayoutManager(linearLayoutManager);
            recyclerViewSearch.setAdapter(searchAdapter);
            textViewNull.setVisibility(View.GONE);
            recyclerViewSearch.setVisibility(View.VISIBLE);
        }
    }

    //    private void setValueInView(String hinh) {
//        Picasso.get().load(hinh).into(imgdanhsachcakhuc);
//    }
    private void GetDataPlaylist() {
        queuePlayingAdapter = new QueuePlayingAdapter(QueuePlayingActivity.this,queuePlaying, currentPositionIntent);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(QueuePlayingActivity.this);
        recyclerViewQueue.setLayoutManager(linearLayoutManager2);
        recyclerViewQueue.setAdapter(queuePlayingAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewQueue);
    }
    private void DataIntent() {
        Intent intent = getIntent();
        if (intent != null){
            if (intent.hasExtra("position")){
                currentPositionIntent = getIntent().getIntExtra("position",-1);
                Log.d("POSITION INTENT", String.valueOf(currentPositionIntent));
            }
        }
    }
    private void AnhXa() {
        recyclerViewSearch = findViewById(R.id.recyclerview_search);
        textViewNull = findViewById(R.id.search_null);
        searchView = findViewById(R.id.searchView);
        recyclerViewQueue = findViewById(R.id.recyclerview_queuePlaying);
    }
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |
            ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(queuePlaying,fromPosition,toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);
            if(fromPosition == currentPostion)
            {
                Log.d("ONMOVE FROMPOSITION: ", String.valueOf(fromPosition));
                Log.d("ONMOVE TOPOSITION: ", String.valueOf(toPosition));
                currentPostion = toPosition;
                prevPosition = toPosition;
                Log.d("ONMOVE currentPosition: ", String.valueOf(currentPostion));
                Log.d("ONMOVE prevPosition: ", String.valueOf(prevPosition));
            }
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };
}
