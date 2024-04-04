package com.example.musicplayer.activity;

import static com.example.musicplayer.activity.PlayingActivity.img_status;
import static com.example.musicplayer.activity.PlayingActivity.mediaPlayer;
import static com.example.musicplayer.activity.PlayingActivity.position;
import static com.example.musicplayer.activity.PlayingActivity.song_name;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
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
import com.example.musicplayer.adapter.MainViewPagerAdapter;
import com.example.musicplayer.R;
import com.example.musicplayer.fragment.LibraryFragment;
import com.example.musicplayer.model.ListLibraryModel;
import com.example.musicplayer.model.SongModel;
import com.example.musicplayer.fragment.AlbumFragment;
import com.example.musicplayer.fragment.HomeFragment;
import com.example.musicplayer.fragment.SearchFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 1;
    public static ArrayList<SongModel> songList;
    public static ArrayList<ListLibraryModel> libraryList = new ArrayList<>();
    private LinearLayout playBackStatus;
    private ImageView imgLove, playPause;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
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
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    playPause.setImageResource(R.drawable.nutpause);
                    mediaPlayer.pause();
                }
                else {
                    playPause.setImageResource(R.drawable.nutplay);
                    mediaPlayer.start();
                }
            }
        });
        playBackStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songList != null && !songList.isEmpty() && position > -1) {
                    int currentSongIndex = position;
                    Intent intent = new Intent(MainActivity.this, PlayingActivity.class);
                    intent.putExtra("playBackStatus", true);
                    intent.putExtra("position", currentSongIndex);
                    startActivity(intent);
                }
            }
        });
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
        TextView song = findViewById(R.id.song_status);
        if (song_name != null)
            song.setText(song_name.getText());
        ImageView img = findViewById(R.id.img_status);
        if (img_status != null)
            Glide.with(this).asBitmap().load(img_status).into(img);
        if (mediaPlayer != null) {
            playPause = findViewById(R.id.imgPlay);
            if (mediaPlayer.isPlaying()) playPause.setImageResource(R.drawable.nutplay);
            else playPause.setImageResource(R.drawable.nutpause);
        }
    }

    private void permission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
//            Toast.makeText(this, "Permission Granted! ", Toast.LENGTH_SHORT).show();
            songList = getAllSongs(this);
            initViewPager();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Permission Granted! ", Toast.LENGTH_SHORT).show();
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
}