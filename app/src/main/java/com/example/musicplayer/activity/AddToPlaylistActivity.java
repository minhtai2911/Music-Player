package com.example.musicplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.AddToPlaylistAdapter;

public class AddToPlaylistActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    AddToPlaylistAdapter addToPlaylistAdapter;
    String selectedSongPath;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_to_playlist);
        DataIntent();
        initView();
        overridePendingTransition(R.anim.anim_intent_in, R.anim.anim_intent_out);
    }

    private void DataIntent() {
        Intent intent = getIntent();
        if (intent != null){
            if (intent.hasExtra("songPath")){
                selectedSongPath = getIntent().getStringExtra("songPath");
            }
        }
    }

    private void initView() {
        recyclerView = findViewById(R.id.recycle_view_playlist);
        addToPlaylistAdapter = new AddToPlaylistAdapter(AddToPlaylistActivity.this, selectedSongPath, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(addToPlaylistAdapter);
        backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
