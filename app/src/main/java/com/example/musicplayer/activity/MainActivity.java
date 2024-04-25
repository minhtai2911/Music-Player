package com.example.musicplayer.activity;

//import static com.example.musicplayer.activity.PlayingActivity.mediaPlayer;
import static com.example.musicplayer.activity.PlayingActivity.musicService;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicplayer.adapter.MainViewPagerAdapter;
import com.example.musicplayer.R;
import com.example.musicplayer.fragment.LibraryFragment;
import com.example.musicplayer.model.PlaylistModel;
import com.example.musicplayer.model.SongModel;
import com.example.musicplayer.fragment.HomeFragment;
import com.example.musicplayer.fragment.SearchFragment;
import com.example.musicplayer.tool.DatabaseHelper;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 1;
    public static ArrayList<SongModel> songList;
    public  static ArrayList<SongModel> queuePlaying = new ArrayList<>();
    public static String currPlayedPlaylistID="";
    public static SongModel currPlayedSong = null;
    private LinearLayout playBackStatus;
    private ImageView playPause, addButton;
    DatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        queuePlaying = new ArrayList<>();
        myDB = new DatabaseHelper(MainActivity.this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        permission();
        playBackStatus();
    }

    private void playBackStatus() {
        playBackStatus = findViewById(R.id.linearLayoutPlayBackStatus);
        playPause = findViewById(R.id.imgPlay);
        addButton = findViewById(R.id.img_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currPlayedSong==null){
                    return;
                }
                showAddCurrSongDialog(currPlayedSong, MainActivity.this);
            }
        });
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicService.isPlaying()) {
                    playPause.setImageResource(R.drawable.nutpause);
                    musicService.pause();
                }
                else {
                    playPause.setImageResource(R.drawable.nutplay);
                    musicService.start();
                }
            }
        });
        playBackStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currPlayedSong!=null){
                    Intent intent = new Intent(MainActivity.this, PlayingActivity.class);
                    String songPath = currPlayedSong.getPath();
                    if(currPlayedPlaylistID!=null){
                        intent.putExtra("playlistID", currPlayedPlaylistID);
                    }
                    intent.putExtra("songPath", songPath);
                    startActivity(intent);
                }

            }
        });
    }

    public static void showAddCurrSongDialog(SongModel song, Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_current_song_dialog);
        LinearLayout addPlaylist = dialog.findViewById(R.id.add_playlist);
        ImageView closeIcon = dialog.findViewById(R.id.layout_close);
        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        addPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String songPath = song.getPath();
                Intent intent = new Intent(v.getContext(), AddToPlaylistActivity.class);
                intent.putExtra("songPath", songPath);
                v.getContext().startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void initViewPager() {
        ViewPager viewPager = findViewById(R.id.viewpager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        MainViewPagerAdapter mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        mainViewPagerAdapter.addFragment(new HomeFragment(),"");
        mainViewPagerAdapter.addFragment(new SearchFragment(),"");
        mainViewPagerAdapter.addFragment(new LibraryFragment(),"");
        viewPager.setAdapter(mainViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.icontrangchu);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.icontimkiem);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.iconthuvien);
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadStatus();
    }

    private void loadStatus() {

        TextView songName = playBackStatus.findViewById(R.id.song_name);
        TextView author = playBackStatus.findViewById(R.id.artist_name);
        ImageView imgSong = playBackStatus.findViewById(R.id.img_status);
        if (currPlayedSong!=null){
            songName.setText(currPlayedSong.getTitle());
            author.setText(currPlayedSong.getArtist());
            Uri uri = Uri.parse(currPlayedSong.getPath());
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(uri.toString());
            byte[] img = retriever.getEmbeddedPicture();
            if (img != null) {
                Glide.with(this).asBitmap().load(img).apply(RequestOptions.bitmapTransform(new RoundedCorners(10))).into(imgSong);
            }
            else {
                Glide.with(this).asBitmap().load(R.drawable.default_image).apply(RequestOptions.bitmapTransform(new RoundedCorners(10))).into(imgSong);
            }
        }

//        if (mediaPlayer != null) {
//            playPause = findViewById(R.id.imgPlay);
//            if (mediaPlayer.isPlaying()) playPause.setImageResource(R.drawable.nutplay);
//            else playPause.setImageResource(R.drawable.nutpause);
////            if (musicService.isPlaying()) playPause.setImageResource(R.drawable.nutplay);
//        }

        if (musicService != null) {
            playPause = findViewById(R.id.imgPlay);
            if (musicService.isPlaying()) playPause.setImageResource(R.drawable.nutplay);
            else playPause.setImageResource(R.drawable.nutpause);
        }
    }

    private void permission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            songList = getAllSongs(this);
            initViewPager();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                songList = getAllSongs(this);
                initViewPager();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }
    public static ArrayList<SongModel> getAllSongs(Context context) {
        ArrayList<SongModel> songList = new ArrayList<>();
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST
        };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null,null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String title = cursor.getString(0);
                String duration = cursor.getString(1);
                String path = cursor.getString(2);
                String artist = cursor.getString(3);
                SongModel song = new SongModel(path,title,artist,duration);
                songList.add(song);
            }
            cursor.close();
        }
        return songList;
    }

    public static ArrayList<PlaylistModel> getAllPlaylist( DatabaseHelper myDB) {
        return myDB.QueryAllPlaylists();
    }

    public static void setQueuePlaying(ArrayList<SongModel> listSong){
        queuePlaying.clear();
        queuePlaying = new ArrayList<>(listSong);
    }

    public static ArrayList<SongModel> getQueuePlaying(){
        return queuePlaying;
    }

    public static void swapSongInQueue(int fromPosition, int toPosition){
        Collections.swap(queuePlaying,fromPosition,toPosition);
    }

    public static void addSongToQueue(SongModel song, Context context){
        for(int i =0;i<queuePlaying.size();i++)
        {
            if(queuePlaying.get(i).getPath().equals(song.getPath()))
            {
                Toast.makeText(context, "Existed in queue", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        queuePlaying.add(song);
        Toast.makeText(context, "Added to queue", Toast.LENGTH_SHORT).show();
    }

    public static void removeSongFromQueue(SongModel song){
        queuePlaying.remove(song);
    }


    public static SongModel getSongByPath(ArrayList<SongModel>list, String songPath){
        SongModel song = null;
        for(int i=0;i< list.size();i++){
            if(list.get(i).getPath().equals(songPath)){
                song = list.get(i);
                break;
            }
        }
        return song;
    }

    public static int getSongPositonByPath(ArrayList<SongModel> list, String songPath){
        int positon = -1;
        for(int i=0;i< list.size();i++){
            if(list.get(i).getPath().equals(songPath)){
                positon = i;
                break;
            }
        }
        return positon;
    }
}