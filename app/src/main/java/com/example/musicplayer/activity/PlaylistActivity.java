package com.example.musicplayer.activity;

import static com.example.musicplayer.activity.MainActivity.currPlayedPlaylistID;
import static com.example.musicplayer.activity.MainActivity.getAllPlaylist;
import static com.example.musicplayer.activity.MainActivity.getQueuePlaying;
import static com.example.musicplayer.activity.MainActivity.setQueuePlaying;
import static com.example.musicplayer.activity.MainActivity.swapSongInQueue;
//import static com.example.musicplayer.activity.PlayingActivity.mediaPlayer;
import static com.example.musicplayer.activity.MainActivity.songList;
import static com.example.musicplayer.activity.PlayingActivity.musicService;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.adapter.LibraryAdapter;
import com.example.musicplayer.adapter.PlaylistAdapter;
import com.example.musicplayer.adapter.RecommendSongsToPlaylistAdapter;
import com.example.musicplayer.fragment.LibraryFragment;
import com.example.musicplayer.model.PlaylistModel;
import com.example.musicplayer.model.SongModel;
import com.example.musicplayer.tool.DatabaseHelper;
import com.example.musicplayer.tool.GetDominantColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import android.animation.ObjectAnimator;
import de.hdodenhof.circleimageview.CircleImageView;

public class PlaylistActivity extends AppCompatActivity {
    TextView playlistName, songCount, playlistName2;
    ImageView backButton, editButton, backButton2;

    LinearLayout header;
    RelativeLayout playlistHeader;
    CircleImageView playlistImage, playlistImage2;
    RecyclerView recyclerViewPlaylist;

    RecyclerView recommendView;
    @SuppressLint("StaticFieldLeak")
    public static PlaylistAdapter playlistAdapter;

    RecommendSongsToPlaylistAdapter recommendSongsToPlaylistAdapter;
    String currentPlaylistIntent;
    PlaylistModel playlistModel = null;
    ArrayList<PlaylistModel> playlists;
    public static ImageView play_playlist_btn, play_btn_2;

    NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_playlist);
        DataIntent();
        DatabaseHelper myDB = new DatabaseHelper(PlaylistActivity.this);
        this.playlists = getAllPlaylist(myDB);
        backButton = findViewById(R.id.back_btn);
        backButton2 = findViewById(R.id.back_btn_2);
        playlistHeader = findViewById(R.id.playlist_header);
//        playlistHeader.setVisibility(View.GONE);
        scrollView = findViewById(R.id.nest);
        header = findViewById(R.id.header);
        editButton = findViewById(R.id.edit_btn);
        play_playlist_btn = findViewById(R.id.play_playlist_btn);
        play_btn_2 = findViewById(R.id.play_btn_2);
        playlistName = findViewById(R.id.playlist_name);
        playlistName2 = findViewById(R.id.playlist_name_2);
        playlistImage = findViewById(R.id.playlist_image);
        playlistImage2 = findViewById(R.id.playlist_image_2);
        songCount = findViewById(R.id.playlist_count);
        recyclerViewPlaylist = findViewById(R.id.recyclerview_playlist_songs);
        initView();
        playlistHeader.setAlpha(0.0f);
        playlistAdapter = new PlaylistAdapter(this, playlistModel.getListSong(),currentPlaylistIntent);
        recyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPlaylist.setAdapter(playlistAdapter);
        ArrayList<SongModel> recommendedSongList = (ArrayList<SongModel>) recommendSongs(songList, playlistModel.getListSong());
        recommendSongsToPlaylistAdapter = new RecommendSongsToPlaylistAdapter(this,recommendedSongList, this.playlistModel);
        recommendView = findViewById(R.id.recyclerview_recommended_songs);
        recommendView.setLayoutManager(new LinearLayoutManager(this));
        recommendView.setAdapter(recommendSongsToPlaylistAdapter);
            play_playlist_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(playlistModel!=null && playlistModel.getListSong().size()>=1){
                        if(currPlayedPlaylistID==null||!currPlayedPlaylistID.equals(playlistModel.getPlaylistId())) {
                            Intent intent = new Intent(PlaylistActivity.this, PlayingActivity.class);
                            intent.putExtra("playlistID", playlistModel.getPlaylistId());
                            intent.putExtra("songPath", playlistModel.getListSong().get(0).getPath());
                            PlaylistActivity.this.startActivity(intent);
                            play_playlist_btn.setImageResource(R.drawable.green_play);
                            play_btn_2.setImageResource(R.drawable.green_play);
                        } else if (musicService.isPlaying()) {
                            musicService.pause();
                            play_playlist_btn.setImageResource(R.drawable.green_pause);
                            play_btn_2.setImageResource(R.drawable.green_pause);
                        } else {
                            musicService.start();
                            play_playlist_btn.setImageResource(R.drawable.green_play);
                            play_btn_2.setImageResource(R.drawable.green_play);
                        }
                    }
                }
            });

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if (scrollY < 150) {
//                    playlistHeader.setVisibility(View.GONE);
//                } else if(scrollY>450){
//                    playlistHeader.setVisibility(View.VISIBLE);
//                } else {
//                    playlistHeader.setVisibility(scrollY/56);
//                }
                float alpha = scrollY > 570 ? 1.0f : 0.0f; // Fade in when scrolled past 450, fade out otherwise (adjust as needed)
                // Create and apply the fade animation
                ObjectAnimator animation = ObjectAnimator.ofFloat(playlistHeader, "alpha", alpha);
                animation.setDuration(40); // Adjust duration for animation speed (in milliseconds)
                animation.start();
            }
        });


        play_btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playlistModel!=null && playlistModel.getListSong().size()>=1){
                    if(currPlayedPlaylistID==null||!currPlayedPlaylistID.equals(playlistModel.getPlaylistId())) {
                        Intent intent = new Intent(PlaylistActivity.this, PlayingActivity.class);
                        intent.putExtra("playlistID", playlistModel.getPlaylistId());
                        intent.putExtra("songPath", playlistModel.getListSong().get(0).getPath());
                        PlaylistActivity.this.startActivity(intent);
                        play_playlist_btn.setImageResource(R.drawable.green_play);
                        play_btn_2.setImageResource(R.drawable.green_play);
                    } else if (musicService.isPlaying()) {
                        musicService.pause();
                        play_playlist_btn.setImageResource(R.drawable.green_pause);
                        play_btn_2.setImageResource(R.drawable.green_pause);
                    } else {
                        musicService.start();
                        play_playlist_btn.setImageResource(R.drawable.green_play);
                        play_btn_2.setImageResource(R.drawable.green_play);
                    }
                }
            }
        });
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(LibraryFragment.libraryAdapter!=null){
                        DatabaseHelper myDB = new DatabaseHelper(getBaseContext());
                        LibraryAdapter.libraryList = getAllPlaylist(myDB);
                        LibraryFragment.libraryAdapter.notifyDataSetChanged();
                    }
                    finish();
                }
            });

            backButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LibraryFragment.libraryAdapter!=null){
                    DatabaseHelper myDB = new DatabaseHelper(getBaseContext());
                    LibraryAdapter.libraryList = getAllPlaylist(myDB);
                    LibraryFragment.libraryAdapter.notifyDataSetChanged();
                }
                finish();
            }
        });
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edit(v, playlistName);
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

    public void initView() {
        for (PlaylistModel playlist: playlists) {
            if(Objects.equals(playlist.getPlaylistId(), currentPlaylistIntent)) {
                playlistModel = new PlaylistModel(playlist.getPlaylistId(), playlist.getPlaylistName(), playlist.getListSong());
                break;
            }
        }
        overridePendingTransition(R.anim.anim_intent_in, R.anim.anim_intent_out);
        if(musicService!=null && currPlayedPlaylistID!=null &&musicService.isPlaying() && currPlayedPlaylistID.equals(this.playlistModel.getPlaylistId())){
            play_playlist_btn.setImageResource(R.drawable.green_play);
            play_btn_2.setImageResource(R.drawable.green_play);
        } else {
            play_playlist_btn.setImageResource(R.drawable.green_pause);
            play_btn_2.setImageResource(R.drawable.green_pause);
        }
        playlistName.setText(playlistModel.getPlaylistName());
        playlistName2.setText(playlistModel.getPlaylistName());
        int count = playlistModel.getListSong().size();
        if(count > 1){
            String countStr = count +" songs";
            songCount.setText(countStr);
        } else {
            String countStr = count +" song";
            songCount.setText(countStr);
        }
        setBackground();
    }

    public void setBackground(){
        String playlistImagePath = playlistModel.getPlaylistImagePath();
        GradientDrawable gradientDrawable = new GradientDrawable();
        GradientDrawable gradientDrawableHeader = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);

        if (playlistImagePath != null) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(playlistImagePath);
            byte[] img = retriever.getEmbeddedPicture();
            Glide.with(this).asBitmap().load(img).into(playlistImage2);
            Glide.with(this).asBitmap().load(img).into(playlistImage);
            int domainColor = GetDominantColor.getDominantColor(img);
            gradientDrawable.setColors(new int[]{domainColor-10,domainColor, domainColor +10, domainColor});
            gradientDrawableHeader.setColors(new int[]{domainColor-10,domainColor, domainColor +10, domainColor});
        }
        else {
            Glide.with(this).asBitmap().load(R.drawable.default_playlist_image).into(playlistImage2);
            Glide.with(this).asBitmap().load(R.drawable.default_playlist_image).into(playlistImage);
            int domainColor = -13627344;
            gradientDrawable.setColors(new int[]{domainColor-10,domainColor, domainColor +10, domainColor});
            gradientDrawableHeader.setColors(new int[]{domainColor-10,domainColor, domainColor +10, domainColor});
        }
        gradientDrawable.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
        gradientDrawableHeader.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
        androidx.core.widget.NestedScrollView nestedScrollView = findViewById(R.id.nest);
        header.setBackground(gradientDrawableHeader);
        nestedScrollView.setBackground(gradientDrawable);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateRecommend(){
        DatabaseHelper myDB = new DatabaseHelper(this);
        playlists = getAllPlaylist(myDB);
        for (PlaylistModel playlist: playlists) {
            if(Objects.equals(playlist.getPlaylistId(), currentPlaylistIntent)) {
                playlistModel = new PlaylistModel(playlist.getPlaylistId(), playlist.getPlaylistName(), playlist.getListSong());
                break;
            }
        }
        playlistAdapter.setPlaylistSongs(playlistModel.getListSong());
        ArrayList<SongModel> recommendedSongList = (ArrayList<SongModel>) recommendSongs(songList, playlistModel.getListSong());

        recommendSongsToPlaylistAdapter.setSongList(recommendedSongList);
        playlistAdapter.notifyItemInserted(playlistModel.getListSong().size()-1);
        recommendSongsToPlaylistAdapter.notifyItemRangeChanged(0,5);

        int count = playlistModel.getListSong().size();
        if(count > 1){
            String countStr = count +" songs";
            songCount.setText(countStr);
        } else {
            String countStr = count +" song";
            songCount.setText(countStr);
        }
        setBackground();
    }

    public void edit(View v, TextView playlistName){
        if(playlistModel==null){
            return;
        }
        String curPlaylistName = playlistModel.getPlaylistName();
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        LayoutInflater inflater = LayoutInflater.from(v.getContext());
        View viewDialog = inflater.inflate(R.layout.add_playlist_dialog, null);
        EditText eName = viewDialog.findViewById(R.id.edt_playlist_name);
        TextView title = viewDialog.findViewById(R.id.dialogTitle);
        title.setText("Edit playlist");
        eName.setHint(curPlaylistName);
        Button btnCancel = viewDialog.findViewById(R.id.btn_cancel);
        Button btnSave = viewDialog.findViewById(R.id.btn_create);
        btnSave.setText("Save");
        builder.setView(viewDialog);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newPlaylistName = String.valueOf(eName.getText()).trim();
                if(newPlaylistName.equals("")){
                    newPlaylistName=String.valueOf(eName.getHint()).trim();
                }
                PlaylistModel updatePlaylist = new PlaylistModel(playlistModel.getPlaylistId(), newPlaylistName, playlistModel.getListSong());
                DatabaseHelper myDB = new DatabaseHelper(v.getContext());
                boolean isUpdated = myDB.UpdatePlaylist(updatePlaylist);
                if(isUpdated){
                    playlistName.setText(newPlaylistName);
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static List<SongModel> recommendSongs(List<SongModel> allSongs, List<SongModel> playlistSongs) {
        // Set to store playlist song Title/unique identifiers
        Set<String> playlistSongTitle = new HashSet<>();
        HashMap<String, Integer> recommendationCounts = new HashMap<>();
        for (SongModel playlistSong : playlistSongs) {
            playlistSongTitle.add(playlistSong.getTitle());
//            String album = playlistSong.getAlbum();
            String artist = playlistSong.getArtist();
//            if (album != null) {
//                recommendationCounts.put(album, recommendationCounts.getOrDefault(album, 0) +  2 );
//            }
            recommendationCounts.put(artist, recommendationCounts.getOrDefault(artist, 0) + 1);
        }

        // Sort entries by count in descending order
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(recommendationCounts.entrySet());
        sortedEntries.sort((entry1, entry2) -> entry2.getValue() - entry1.getValue());

        // Extract recommended songs (max 5 with diversity check)
        List<SongModel> recommendedSongs = new ArrayList<>();
        Set<String> recommendedArtists = new HashSet<>(); // Track already recommended artists (for diversity)

            int addedSongCount = 0;
            for(int idx = 0;addedSongCount<5 && idx <sortedEntries.size(); idx++){
                String recommendation = sortedEntries.get(idx).getKey();
                for (SongModel song : allSongs) {
                    if (!playlistSongTitle.contains(song.getTitle()) &&
                            ((
//                                    song.getAlbum()!=null&&recommendation.equals(song.getAlbum()) ||
                                            recommendation.equals(song.getArtist()))
                                    && !recommendedSongs.contains(song))) {
                        //diversify
                        if (
//                                song.getAlbum()!=null&&recommendation.equals(song.getAlbum()) ||
                                        recommendation.equals(song.getArtist())) {
                            if (
//                                    recommendedAlbumsArtists.contains(song.getAlbum()) ||
                                    recommendedArtists.contains(song.getArtist())) {
                                continue; // Skip if exceeding allowed  repeats
                            }
                        }
                        recommendedSongs.add(song);
                        addedSongCount++;
//                        if(song.getAlbum()!=null){
//                            recommendedAlbumsArtists.add(song.getAlbum());
//                        }
                        recommendedArtists.add(song.getArtist());
                        break; // Only add one song per recommendation entry
                    }
                }
            }

        // Fallback recommendations if less than 5 songs found
        if (recommendedSongs.size() < 5) {
            int neededSongs = 5 - recommendedSongs.size();
            Set<SongModel> usedSongs = new HashSet<>(recommendedSongs); // Track already recommended songs
            for (int  idx=0;neededSongs>0 && idx < allSongs.size();idx++) {
                SongModel song = allSongs.get(idx);
                if (!playlistSongTitle.contains(song.getTitle()) && !usedSongs.contains(song)) {
                    recommendedSongs.add(song);
                    usedSongs.add(song);
                    neededSongs--;
                }
            }
        }

        return recommendedSongs;
    }


}
