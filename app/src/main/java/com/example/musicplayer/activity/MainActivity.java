package com.example.musicplayer.activity;

//import static com.example.musicplayer.activity.PlayingActivity.mediaPlayer;
import static com.example.musicplayer.activity.PlayingActivity.musicService;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Log;
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
import androidx.annotation.Nullable;
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
import com.example.musicplayer.adapter.LibraryAdapter;
import com.example.musicplayer.adapter.MainViewPagerAdapter;
import com.example.musicplayer.R;
import com.example.musicplayer.fragment.LibraryFragment;
import com.example.musicplayer.interfaces.OnTaskCompleted;
import com.example.musicplayer.model.PlaylistModel;
import com.example.musicplayer.model.SongModel;
import com.example.musicplayer.fragment.HomeFragment;
import com.example.musicplayer.fragment.SearchFragment;
import com.example.musicplayer.receiver.LogEventReceiver;
import com.example.musicplayer.service.LogMonitoringService;
import com.example.musicplayer.tool.DatabaseHelper;
import com.example.musicplayer.tool.NetworkChangeReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 1;
    public static ArrayList<SongModel> songList = new ArrayList<>();
    public  static ArrayList<SongModel> queuePlaying = new ArrayList<>();
    public static String currPlayedPlaylistID="";
    public static SongModel currPlayedSong = null;
    public static LinearLayout playBackStatus;
    public static ImageView playPause, addButton;
    private LogEventReceiver logEventReceiver;
//            ImageView shazamButton;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    FirebaseFirestore firebaseFirestore;
    DatabaseHelper myDB;

    public static Context context;

    public static HomeFragment homeFragment;

    public  TabLayout tabLayout;
    private NetworkChangeReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        queuePlaying = new ArrayList<>();
        context = this;
        myDB = new DatabaseHelper(MainActivity.this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });
        permission();

//        broadcastReceiver = new NetworkChangeReceiver();
//        registerBroadcastReceiver();
        Intent intent = new Intent(this, LogMonitoringService.class);
        startService(intent);
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
                String songPath = currPlayedSong.getPath();
                Intent intent = new Intent(v.getContext(), AddToPlaylistActivity.class);
                intent.putExtra("songPath", songPath);
                v.getContext().startActivity(intent);
            }
        });
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currPlayedSong!=null){
                    if (musicService.isPlaying()) {
                        playPause.setImageResource(R.drawable.nutpause);
                        musicService.showNotification(R.drawable.ic_play,0f);
                        musicService.pause();
                    }
                    else {
                        playPause.setImageResource(R.drawable.nutplay);
                        musicService.showNotification(R.drawable.ic_pause,1f);
                        musicService.start();
                    }
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
    @Override
    protected void onPause() {
        super.onPause();
        // Activity bị tạm dừng, lưu trạng thái hiện tại hoặc ngừng các cập nhật không cần thiết
    }

//    @Override
//    protected void onStop() {
//
//        super.onStop();
//
//    }
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
        tabLayout = findViewById(R.id.tab_layout);
        MainViewPagerAdapter mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        homeFragment = new HomeFragment();
        mainViewPagerAdapter.addFragment(homeFragment,"");
        mainViewPagerAdapter.addFragment(new SearchFragment(),"");
        mainViewPagerAdapter.addFragment(new LibraryFragment(),"");
        viewPager.setAdapter(mainViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.icontrangchu);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.icontimkiem);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.iconthuvien);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabId = tab.getPosition();
                if(tabId==0){
                    homeFragment.startRandomSong();
                } else {
                    homeFragment.stopRandomSong();
                }
                Log.d("TabLayout", "Tab clicked: " + tabId); // Print the tab ID (position)
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
    public void loadSongFromDatabase(OnTaskCompleted listener) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("songs_test")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            String urlTemp = (String) data.get("url");
                            retriever.setDataSource(urlTemp);
                            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                            Uri uri = Uri.parse(urlTemp);
                            String path = uri.toString();
                            SongModel song = new SongModel(path,title,artist,duration,1);
                            songList.add(song);
                        }
                        // Gọi phương thức callback khi hoàn tất
                        listener.onTaskCompleted();
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if(intent != null) {
            boolean isConnected = getIntent().getBooleanExtra("checkConnected", true);
            Log.d("checkInternetConnecting", isConnected+" ");
            if(currPlayedSong != null) {
                if(currPlayedSong.getType() == 0 || (isConnected == true && currPlayedSong.getType() == 1)) {
                    Log.d("checkInternetConnect", "finish main activity here");
                    loadStatus();
                }
            }
        }
//        loadStatus();
        if(tabLayout!=null) {
            if (tabLayout.getSelectedTabPosition() == 0) {
                homeFragment.startRandomSong();
            } else {
                homeFragment.stopRandomSong();
            }
        }
    }

    @Override
    protected  void onStop() {
        super.onStop();
        Log.d("OnStop", "onStop");
        homeFragment.stopRandomSong();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop the log monitoring service
        if (logEventReceiver != null) {
            unregisterReceiver(logEventReceiver);
        }
        Intent intent = new Intent(this, LogMonitoringService.class);
        stopService(intent);
    }

    public static void loadStatus() {
        TextView songName = playBackStatus.findViewById(R.id.song_name);
        TextView author = playBackStatus.findViewById(R.id.artist_name);
        ImageView imgSong = playBackStatus.findViewById(R.id.img_status);
        if (currPlayedSong!=null){
            songName.setText(currPlayedSong.getTitle());
            author.setText(currPlayedSong.getArtist());
            byte[] img = currPlayedSong.getImg();
                if (img != null) {
                    Glide.with(context).asBitmap().load(img).apply(RequestOptions.bitmapTransform(new RoundedCorners(10))).into(imgSong);
                }
                else {
                    Glide.with(context).asBitmap().load(R.drawable.default_image).apply(RequestOptions.bitmapTransform(new RoundedCorners(10))).into(imgSong);
                }

        }

//        if (mediaPlayer != null) {
//            playPause = findViewById(R.id.imgPlay);
//            if (mediaPlayer.isPlaying()) playPause.setImageResource(R.drawable.nutplay);
//            else playPause.setImageResource(R.drawable.nutpause);
////            if (musicService.isPlaying()) playPause.setImageResource(R.drawable.nutplay);
//        }

        if (musicService != null) {
            playPause = playBackStatus.findViewById(R.id.imgPlay);
            if (musicService.isPlaying()) playPause.setImageResource(R.drawable.nutplay);
            else playPause.setImageResource(R.drawable.nutpause);
        }
    }

    private void permission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            songList = getAllSongs(this);

            loadSongFromDatabase(new OnTaskCompleted() {
                        @Override
                        public void onTaskCompleted() {
                            Log.d("LoadSong", "Load song completed");
                            initViewPager();
                        }
            });


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
                MediaStore.Audio.Media.ARTIST,
        };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null,null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String title = cursor.getString(0);
                String duration = cursor.getString(1);
                String path = cursor.getString(2);
                String artist = cursor.getString(3);
                SongModel song = new SongModel(path,title,artist,duration, 0);
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

    class waitTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }


}