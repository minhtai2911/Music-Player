package com.example.musicplayer.activity;

import static com.example.musicplayer.activity.MainActivity.currPlayedPlaylistID;
import static com.example.musicplayer.activity.MainActivity.getAllPlaylist;
import static com.example.musicplayer.activity.MainActivity.getQueuePlaying;
import static com.example.musicplayer.activity.MainActivity.setQueuePlaying;
import static com.example.musicplayer.activity.MainActivity.swapSongInQueue;
import static com.example.musicplayer.activity.PlayingActivity.mediaPlayer;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.adapter.PlaylistAdapter;
import com.example.musicplayer.model.PlaylistModel;
import com.example.musicplayer.model.SongModel;
import com.example.musicplayer.tool.DatabaseHelper;
import com.example.musicplayer.tool.GetDominantColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class PlaylistActivity extends AppCompatActivity {
    TextView playlistName;
    ImageView playlistImage, backButton;
    RecyclerView recyclerViewPlaylist;
    PlaylistAdapter playlistAdapter;
    String currentPlaylistIntent;
    PlaylistModel playlistModel = null;
    ArrayList<PlaylistModel> playlists;
    Button play_playlist_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        DataIntent();
        initView();
        overridePendingTransition(R.anim.anim_intent_in, R.anim.anim_intent_out);
    }

    private void DataIntent() {
        Intent intent = getIntent();
        if (intent != null){
            if (intent.hasExtra("playlistId")){
                currentPlaylistIntent = getIntent().getStringExtra("playlistId");
            }
        }
    }

    private void initView() {
        DatabaseHelper myDB = new DatabaseHelper(PlaylistActivity.this);
        this.playlists = getAllPlaylist(myDB);

        for (PlaylistModel playlist: playlists) {
            if(Objects.equals(playlist.getPlaylistId(), currentPlaylistIntent)) {
                playlistModel = new PlaylistModel(playlist.getPlaylistId(), playlist.getPlaylistName(), playlist.getListSong());
                break;
            }
        }
        play_playlist_btn = findViewById(R.id.play_playlist_btn);
        playlistName = findViewById(R.id.playlist_name);
        playlistName.setText(playlistModel.getPlaylistName());
        playlistImage = findViewById(R.id.playlist_image);
        String playlistImagePath = playlistModel.getPlaylistImagePath();

        if (playlistImagePath != null) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(playlistImagePath);
            byte[] img = retriever.getEmbeddedPicture();
            Glide.with(this).asBitmap().load(img).into(playlistImage);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(GradientDrawable.RECTANGLE);
            int darkColor = android.graphics.Color.parseColor("#121212");
            gradientDrawable.setColors(new int[]{darkColor, GetDominantColor.getDominantColor(img)});
            gradientDrawable.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
            gradientDrawable.setCornerRadius(10);
            LinearLayout linearLayout = findViewById(R.id.playlist_info);
            linearLayout.setBackground(gradientDrawable);
        }
        else {
            Glide.with(this).asBitmap().load(R.drawable.imgitem).into(playlistImage);
        }

        play_playlist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playlistModel.getListSong().size()>=1){
                    if(currPlayedPlaylistID==null||!currPlayedPlaylistID.equals(playlistModel.getPlaylistId())) {
                        ArrayList<SongModel> songs = playlistModel.getListSong();
                        setQueuePlaying(songs);
                        Intent intent = new Intent(PlaylistActivity.this, PlayingActivity.class);
                        intent.putExtra("playlistID", playlistModel.getPlaylistId());
                        PlaylistActivity.this.startActivity(intent);
                    } else {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                        }
                        else {
                            mediaPlayer.start();
                        }
                    }
                }
            }
        });

        recyclerViewPlaylist = findViewById(R.id.recyclerview_playlist_songs);
        playlistAdapter = new PlaylistAdapter(this, playlistModel.getListSong(),currentPlaylistIntent);
        recyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPlaylist.setAdapter(playlistAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerViewPlaylist);

        backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(0,
                    ItemTouchHelper.START| ItemTouchHelper.END);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            playlistAdapter.deleteSong(viewHolder.getAdapterPosition());
        }
    };
}
