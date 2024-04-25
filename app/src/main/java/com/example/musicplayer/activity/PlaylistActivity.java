package com.example.musicplayer.activity;

import static com.example.musicplayer.activity.MainActivity.currPlayedPlaylistID;
import static com.example.musicplayer.activity.MainActivity.getAllPlaylist;
import static com.example.musicplayer.activity.MainActivity.getQueuePlaying;
import static com.example.musicplayer.activity.MainActivity.setQueuePlaying;
import static com.example.musicplayer.activity.MainActivity.swapSongInQueue;
//import static com.example.musicplayer.activity.PlayingActivity.mediaPlayer;
import static com.example.musicplayer.activity.PlayingActivity.musicService;

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
        recyclerViewPlaylist = findViewById(R.id.recyclerview_playlist_songs);
            playlistAdapter = new PlaylistAdapter(this, playlistModel.getListSong(),currentPlaylistIntent);
            recyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewPlaylist.setAdapter(playlistAdapter);
            playlistName.setText(playlistModel.getPlaylistName());
            String playlistImagePath = playlistModel.getPlaylistImagePath();
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(GradientDrawable.RECTANGLE);
            int darkColor = android.graphics.Color.parseColor("#121212");

            if (playlistImagePath != null) {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(playlistImagePath);
                byte[] img = retriever.getEmbeddedPicture();
                Glide.with(this).asBitmap().load(img).into(playlistImage);
                int domainColor = GetDominantColor.getDominantColor(img);
                gradientDrawable.setColors(new int[]{darkColor,domainColor, domainColor +20, domainColor +30});
            }
            else {
                Glide.with(this).asBitmap().load(R.drawable.default_playlist_image).into(playlistImage);
                int domainColor = -13627344;
                gradientDrawable.setColors(new int[]{darkColor,domainColor, domainColor +20, domainColor +30});
            }
            gradientDrawable.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
            gradientDrawable.setCornerRadius(10);
            LinearLayout linearLayout = findViewById(R.id.playlist_info);
            linearLayout.setBackground(gradientDrawable);
            play_playlist_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(playlistModel!=null && playlistModel.getListSong().size()>=1){
                        if(currPlayedPlaylistID==null||!currPlayedPlaylistID.equals(playlistModel.getPlaylistId())) {
                            Intent intent = new Intent(PlaylistActivity.this, PlayingActivity.class);
                            intent.putExtra("playlistID", playlistModel.getPlaylistId());
                            intent.putExtra("songPath", playlistModel.getListSong().get(0).getPath());
                            PlaylistActivity.this.startActivity(intent);
                        } else if (musicService.isPlaying()) {
                            musicService.pause();
                        } else {
                            musicService.start();
                        }
                    }
                }
            });
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
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
        playlistImage = findViewById(R.id.playlist_image);
        backButton = findViewById(R.id.back_btn);
        overridePendingTransition(R.anim.anim_intent_in, R.anim.anim_intent_out);
    }

}
