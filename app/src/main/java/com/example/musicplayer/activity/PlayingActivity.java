package com.example.musicplayer.activity;

import static com.example.musicplayer.activity.MainActivity.addSongToQueue;
import static com.example.musicplayer.activity.MainActivity.currPlayedPlaylistID;
import static com.example.musicplayer.activity.MainActivity.currPlayedSong;
import static com.example.musicplayer.activity.MainActivity.getAllPlaylist;
import static com.example.musicplayer.activity.MainActivity.getAllSongs;
import static com.example.musicplayer.activity.MainActivity.getQueuePlaying;
import static com.example.musicplayer.activity.MainActivity.getSongByPath;
import static com.example.musicplayer.activity.MainActivity.queuePlaying;
import static com.example.musicplayer.activity.MainActivity.setQueuePlaying;
import static com.example.musicplayer.activity.MainActivity.showAddCurrSongDialog;
import static com.example.musicplayer.activity.MainActivity.songList;
import static com.example.musicplayer.tool.NetworkChangeReceiver.checkConnected;
//import static com.example.musicplayer.tool.NetworkChangeReceiver.checkConnected;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicplayer.R;
import com.example.musicplayer.adapter.QueuePlayingAdapter;
import com.example.musicplayer.fragment.HomeFragment;
import com.example.musicplayer.adapter.LibraryAdapter;
import com.example.musicplayer.fragment.LibraryFragment;
import com.example.musicplayer.interfaces.ActionPlaying;
import com.example.musicplayer.model.SongModel;
import com.example.musicplayer.service.MusicService;
import com.example.musicplayer.tool.DatabaseHelper;
import com.example.musicplayer.tool.GetDominantColor;
import com.example.musicplayer.tool.NetworkChangeReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


public class PlayingActivity extends AppCompatActivity implements ActionPlaying, ServiceConnection {
    TextView artist_name, duration_played, duration_total;
    ImageView nextBtn, prevBtn, backBtn, shuffleBtn, repeatBtn, playPauseBtn, img_queue, img_add;
    CircleImageView cover_img;
    SeekBar seekBar;
    FirebaseFirestore firebaseFirestore;
    public static int position = -1;
    public static ArrayList<SongModel> listSongs = new ArrayList<>();
    static Uri uri;
    public static MediaSessionCompat mediaSessionCompat;
    public static MusicService musicService;

    private Handler handler = new Handler();
    private Thread playThread, nextThread, prevThread;
    static Boolean shuffleBoolean = false;
    static int repeat = 0;
    public  TextView song_name;
    public  byte[] img_status;
    public static Context playingContext;
    public static int seekBarDuration = 0;
    public PlayingActivity() {

    }
    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetworkChangeReceiver.NETWORK_CHANGE_ACTION.equals(intent.getAction())) {
                boolean isConnected = intent.getBooleanExtra("checkConnected", false);
                Intent networkChangeIntent = new Intent(PlayingActivity.this, MainActivity.class);
                networkChangeIntent.putExtra("checkConnected", isConnected);
                Log.d("checkInternetConnect", isConnected+" ");
                if(isConnected == false) {
                    if(currPlayedSong.getType() == 0) {
                        return;
                    }
                    Log.d("checkInternetConnect", "finish playing activity here");
//                    if(musicService != null) {
//                        unbindService(PlayingActivity.this);
//                    }
                    startActivity(networkChangeIntent);
                    finish();
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playingContext = this;
        LocalBroadcastManager.getInstance(this).registerReceiver(networkChangeReceiver,
                new IntentFilter(NetworkChangeReceiver.NETWORK_CHANGE_ACTION));
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_playing);
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "My Audio");
        mediaSessionCompat.setCallback(new MyMediaSessionCallback());
        mediaSessionCompat.setActive(true);
        initViews();
        getIntentData();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PlaylistActivity.playlistAdapter!=null){
                    if(musicService!=null &&currPlayedPlaylistID!=null && musicService.isPlaying()){
                        PlaylistActivity.play_playlist_btn.setImageResource(R.drawable.green_play);
                        PlaylistActivity.play_btn_2.setImageResource(R.drawable.green_play);
                    } else {
                        PlaylistActivity.play_playlist_btn.setImageResource(R.drawable.green_pause);
                        PlaylistActivity.play_btn_2.setImageResource(R.drawable.green_pause);
                    }
                    PlaylistActivity.playlistAdapter.notifyDataSetChanged();
                }


                if(LibraryFragment.libraryAdapter!=null){
                    LibraryFragment.libraryAdapter.notifyDataSetChanged();
                }

                finish();
            }
        });

        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String songPath = currPlayedSong.getPath();
                Intent intent = new Intent(v.getContext(), AddToPlaylistActivity.class);
                intent.putExtra("songPath", songPath);
                v.getContext().startActivity(intent);
//                showAddCurrSongDialog(currPlayedSong, PlayingActivity.this);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (musicService != null && fromUser) {
                    musicService.seekTo(progress*1000);
                    if(musicService.isPlaying()){
                        musicService.showNotification(R.drawable.ic_pause,1f);
                    } else {
                        musicService.showNotification(R.drawable.ic_play,0f);
                    }
                }
                if (seekBar.getProgress() == (seekBarDuration)) {
                    seekBar.setProgress(0);
                    musicService.seekTo(0);
                    if (repeat == 2) {
                        repeatPlay();
                    }
                    else {
                        nextBtnClicked();
                    }
                }

                if(seekBar.getProgress()==1 && musicService!=null){
                    if(musicService.isPlaying()) {
                        musicService.showNotification(R.drawable.ic_pause,1f);
                    } else {
                        musicService.showNotification(R.drawable.ic_play,0f);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        PlayingActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (musicService != null) {
                    int mCurrentPosition = musicService.getCurrentPosition()/1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }

                handler.postDelayed(this,1000);
            }
        });
        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffleBoolean = !shuffleBoolean;
                if (shuffleBoolean) {
                    shuffleBtn.setImageResource(R.drawable.iconshuffled);
                    nextBtn.setImageResource(R.drawable.iconnext);
                    prevBtn.setImageResource(R.drawable.iconprevious);
                }
                else {
                    if (position == listSongs.size() - 1) nextBtn.setImageResource(R.drawable.iconnextnull);
                    else nextBtn.setImageResource(R.drawable.iconnext);
                    if (position == 0) prevBtn.setImageResource(R.drawable.iconpreviousnull);
                    else prevBtn.setImageResource(R.drawable.iconprevious);
                    shuffleBtn.setImageResource(R.drawable.iconshuffle);
                }
            }
        });
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeat = (repeat+1) % 3;
                if (repeat == 0) {
                    if (position == listSongs.size() - 1) nextBtn.setImageResource(R.drawable.iconnextnull);
                    else nextBtn.setImageResource(R.drawable.iconnext);
                    if (position == 0) prevBtn.setImageResource(R.drawable.iconpreviousnull);
                    else prevBtn.setImageResource(R.drawable.iconprevious);
                    repeatBtn.setImageResource(R.drawable.iconrepeat);
                }

                if (repeat == 1) {
                    nextBtn.setImageResource(R.drawable.iconnext);
                    prevBtn.setImageResource(R.drawable.iconprevious);
                    repeatBtn.setImageResource(R.drawable.iconrepeated);
                }

                if (repeat == 2) {
                    if (position == listSongs.size() - 1) nextBtn.setImageResource(R.drawable.iconnextnull);
                    else nextBtn.setImageResource(R.drawable.iconnext);
                    if (position == 0) prevBtn.setImageResource(R.drawable.iconpreviousnull);
                    else prevBtn.setImageResource(R.drawable.iconprevious);
                    repeatBtn.setImageResource(R.drawable.iconrepeat_one);
                }
            }
        });
        img_queue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayingActivity.this, QueuePlayingActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }

    public PlayingActivity(NetworkChangeReceiver networkChangeReceiver) {
        this.networkChangeReceiver = networkChangeReceiver;
    }
    private Animation loadRotation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(10000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        return rotateAnimation;
    }

    private void repeatPlay() {
        uri = Uri.parse(listSongs.get(position).getPath());
        setMediaPlayer(listSongs.get(position).getPath());
//        setDataView();
    }

    private String formattedTime(int mCurrentPosition) {
        String totalout = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition%60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalout = minutes + ":" +seconds;
        totalNew = minutes +":" +"0" +seconds;
        if (seconds.length() == 1) {
            return totalNew;
        }
        else {
            return totalout;
        }
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null){
            String songPath = getIntent().getStringExtra("songPath");
            String playlistID = getIntent().getStringExtra("playlistID");

            if(playlistID!=null && songPath!=null) {
                playPlaylist(playlistID, songPath);
                return;
            }
            if (songPath!=null){
                playQueue(songPath);
            }
        }
    }

    public void playPlaylist(String playlistID, String songPath){
        //phat playlist
        if (currPlayedPlaylistID == null || !currPlayedPlaylistID.equals(playlistID)) {
            currPlayedPlaylistID = playlistID;
            DatabaseHelper myDB = new DatabaseHelper(PlayingActivity.this);
            setQueuePlaying(myDB.QueryAllSongInGivenPlaylist(currPlayedPlaylistID));
            listSongs = getQueuePlaying();
            position = MainActivity.getSongPositonByPath(listSongs, songPath);
            currPlayedSong = listSongs.get(position);
            uri = Uri.parse(currPlayedSong.getPath());
            setMediaPlayer(currPlayedSong.getPath());
//            setDataView();
        } else {
            //khong trung bai
            if (!songPath.equals(currPlayedSong.getPath())) {
                position = MainActivity.getSongPositonByPath(listSongs, songPath);
                //bai ton tai trong playlist cu
                if(position!=-1){
                    currPlayedSong = listSongs.get(position);
                    uri = Uri.parse(currPlayedSong.getPath());
                    setMediaPlayer(currPlayedSong.getPath());
                } else {//bai moi duoc them vao
                    DatabaseHelper myDB = new DatabaseHelper(PlayingActivity.this);
                    setQueuePlaying(myDB.QueryAllSongInGivenPlaylist(currPlayedPlaylistID));
                    listSongs = getQueuePlaying();
                    position = MainActivity.getSongPositonByPath(listSongs, songPath);
                    currPlayedSong = listSongs.get(position);
                    uri = Uri.parse(currPlayedSong.getPath());
                    setMediaPlayer(currPlayedSong.getPath());
                }

            } else {
                setDataView();
            }
        }
    }

    public void playQueue(String songPath){
        uri = Uri.parse(songPath);
        //ngung phat playlist
        if(currPlayedPlaylistID!=null){
            currPlayedPlaylistID=null;
            setQueuePlaying(new ArrayList<>());
        }

        position = MainActivity.getSongPositonByPath(getQueuePlaying(), songPath);
        //khong nam trong queue
        if(position==-1) {
            SongModel song = getSongByPath(songList, songPath);
            ArrayList<SongModel> newQueue = new ArrayList<>();
            newQueue.add(song);
            setQueuePlaying(newQueue);
            position = MainActivity.getSongPositonByPath(getQueuePlaying(), songPath);
            currPlayedSong = getQueuePlaying().get(position);
            setMediaPlayer(song.getPath());
        } else {
            //khong trung bai
            if (!songPath.equals(currPlayedSong.getPath())) {
                position = MainActivity.getSongPositonByPath(listSongs, songPath);
                currPlayedSong = listSongs.get(position);
                setMediaPlayer(currPlayedSong.getPath());
                uri = Uri.parse(currPlayedSong.getPath());
            } else {
                uri = Uri.parse(currPlayedSong.getPath());
                setDataView();
            }
        }
        listSongs = getQueuePlaying();
    }

    private void initViews() {
        backBtn = findViewById(R.id.backBtn);
        song_name = findViewById(R.id.song_name);
        artist_name = findViewById(R.id.artist_name);
        duration_played = findViewById(R.id.duration_played);
        duration_total = findViewById(R.id.duration_total);
        cover_img = findViewById(R.id.cover_img);
        nextBtn = findViewById(R.id.nextBtn);
        prevBtn = findViewById(R.id.prevBtn);
        shuffleBtn = findViewById(R.id.shuffleBtn);
        repeatBtn = findViewById(R.id.repeatBtn);
        playPauseBtn = findViewById(R.id.playPauseBtn);
        seekBar = findViewById(R.id.seekBar);
        img_queue = findViewById(R.id.layout_queue);
        img_add = findViewById(R.id.img_add);
        overridePendingTransition(R.anim.anim_intent_in, R.anim.anim_intent_out);
    }


    public void setMediaPlayer(String songPath){
        if(!isPlayable(currPlayedSong, checkConnected)){
            return;
        }
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("songPath", songPath);
        startService(intent);
        seekBarDuration = Integer.parseInt(currPlayedSong.getDuration()) / 1000;
        seekBar.setMax(seekBarDuration);
        setDataView();
        playPauseBtn.setImageResource(R.drawable.nutplay);
        cover_img.startAnimation(loadRotation());
    }

    public void setDataView(){
        seekBar.setMax(seekBarDuration);
        if(!this.isDestroyed()){
            metaData();
        } else {
            if(MainActivity.context!=null){
                MainActivity.loadStatus();
            }
        }

        if(musicService!=null){
            if(musicService.isPlaying()) {
                playPauseBtn.setImageResource(R.drawable.nutplay);
                musicService.showNotification(R.drawable.ic_pause,1f);
                cover_img.startAnimation(loadRotation());
            } else {
                playPauseBtn.setImageResource(R.drawable.nutpause);
                musicService.showNotification(R.drawable.ic_play,0f);
                cover_img.clearAnimation();
            }

        }

        song_name.setText(currPlayedSong.getTitle());
        artist_name.setText(currPlayedSong.getArtist());
        duration_total.setText(formattedTime(seekBarDuration));
        if(PlaylistActivity.playlistAdapter!=null){
                PlaylistActivity.playlistAdapter.notifyDataSetChanged();
        }
        if(QueuePlayingActivity.queuePlayingAdapter!=null){
            QueuePlayingActivity.queuePlayingAdapter.notifyDataSetChanged();
        }
    }
    private void metaData() {
        byte[] img = currPlayedSong.getImg();
        img_status = img;

        if (img != null) {
            Glide.with(this).asBitmap().load(img).apply(RequestOptions.bitmapTransform(new RoundedCorners(50))).into(cover_img);
        }
        else {
            Glide.with(this).asBitmap().load(R.drawable.imageitem).apply(RequestOptions.bitmapTransform(new RoundedCorners(50))).into(cover_img);
        }
        if ((position == listSongs.size() - 1) && repeat != 1 && !shuffleBoolean) nextBtn.setImageResource(R.drawable.iconnextnull);
        else nextBtn.setImageResource(R.drawable.iconnext);
        if (position == 0 && repeat != 1 && !shuffleBoolean) prevBtn.setImageResource(R.drawable.iconpreviousnull);
        else prevBtn.setImageResource(R.drawable.iconprevious);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        int domainColor = GetDominantColor.getDominantColor(img);
        gradientDrawable.setColors(new int[]{domainColor-10, domainColor,domainColor+10, domainColor});
        gradientDrawable.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
        gradientDrawable.setCornerRadius(10);
        RelativeLayout relativeLayout = findViewById(R.id.mContainer);
        relativeLayout.setBackground(gradientDrawable);
    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
        NetworkChangeReceiver.appContext = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(networkChangeReceiver);
    }

    private void prevThreadBtn() {
        prevThread = new Thread() {
            @Override
            public void run() {
                super.run();
                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!(position == 0 && repeat != 1 && !shuffleBoolean))
                            prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    public void prevBtnClicked() {
        if (repeat == 2) {
            repeatBtn.setImageResource(R.drawable.iconrepeated);
            repeat = 1;
            return;
        }
        if (repeat != 1 && !shuffleBoolean) {
            if (position == 0) return;
            position -= 1;
        }
        if (repeat == 1) {
            if (position == 0) position = listSongs.size();
            position -= 1;
        }
        if (shuffleBoolean) {
            int positionRandom = getRandom(listSongs.size());
            while (position == positionRandom)
            {
                positionRandom = getRandom(listSongs.size());
            }
            position = positionRandom;
        }

        playPauseBtn.setImageResource(R.drawable.nutplay);
        uri = Uri.parse(listSongs.get(position).getPath());
        currPlayedSong = listSongs.get(position);
        setMediaPlayer(currPlayedSong.getPath());
//        setDataView();
    }

    private void nextThreadBtn() {
        nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!((position == listSongs.size() - 1) && repeat != 1 && !shuffleBoolean)){
                            nextBtnClicked();
                        }
                    }
                });
            }
        };
        nextThread.start();
    }

    public void nextBtnClicked() {
        if (repeat == 2) {
            repeatBtn.setImageResource(R.drawable.iconrepeated);
            repeat = 1;
            return;
        }
        if (repeat != 1 && !shuffleBoolean) {
            if (position == listSongs.size()-1){
                    getSongRandom();
                return;
            }
            position += 1;
        }

        if (repeat == 1) {
            position = (position + 1) % listSongs.size();
        }
        if (shuffleBoolean) {
            int positionRandom = getRandom(listSongs.size());
            while (position == positionRandom)
            {
                positionRandom = getRandom(listSongs.size());
            }
            position = positionRandom;
        }
        if (listSongs.size() > position) {
            playPauseBtn.setImageResource(R.drawable.nutplay);
            cover_img.startAnimation(loadRotation());
            uri = Uri.parse(listSongs.get(position).getPath());
            currPlayedSong = listSongs.get(position);
        }
        else {
            return;
        }
        setMediaPlayer(currPlayedSong.getPath());
//        setDataView();
    }


    private int getRandom(int size) {
        Random random = new Random();
        return random.nextInt(size);
    }

    public void getSongRandom(){
            currPlayedPlaylistID=null;
            int positionRandom = getRandom(songList.size());
            int posInSongList = MainActivity.getSongPositonByPath(songList, currPlayedSong.getPath());
            while (posInSongList == positionRandom) {
                positionRandom = getRandom(songList.size());
            }
            SongModel song = songList.get(positionRandom);
            ArrayList<SongModel> newQueue = new ArrayList<>();
            newQueue.add(song);
            setQueuePlaying(newQueue);
            listSongs = getQueuePlaying();
            position = listSongs.size()-1;
            currPlayedSong = listSongs.get(position);
            uri = Uri.parse(currPlayedSong.getPath());
            setMediaPlayer(currPlayedSong.getPath());
//            setDataView();
    }

    private void playThreadBtn() {
        playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        playThread.start();
    }
    @Override
    public void playPauseBtnClicked() {
        if (musicService.isPlaying()) {
            playPauseBtn.setImageResource(R.drawable.nutpause);
            musicService.pause();
            cover_img.clearAnimation();
            musicService.showNotification(R.drawable.ic_play,0f);
            PlayingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                        duration_played.setText(formattedTime(mCurrentPosition));
                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
        else {
            playPauseBtn.setImageResource(R.drawable.nutplay);
            musicService.start();
            cover_img.startAnimation(loadRotation());
            musicService.showNotification(R.drawable.ic_pause,1f);
            PlayingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                        duration_played.setText(formattedTime(mCurrentPosition));
                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MusicService.MyBinder myBinder = (MusicService.MyBinder) iBinder;
        musicService = myBinder.getService();
        musicService.setCallBack(PlayingActivity.this);
        Log.e("Check service", "onServiceConnected: "+musicService);
        Log.e("Check Position", "onServiceConnected: " + position);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService = null;
    }
    private class MyMediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onSeekTo(long position) {
            super.onSeekTo(position);
                if(musicService!=null){
                    try {
                        musicService.seekTo((int) (position));
                    } catch (IllegalStateException e) {
                        Log.w("MediaSessionCompat", "Seek error: " + e.getMessage());
                    }
                }
        }
    }

    public static boolean isPlayable(SongModel songModel, boolean isConnected){
        if(songModel==null){
            return false;
        }
        if(isConnected){
            return true;
        } else {
            if(songModel.getType()==1){
                return false;
            } else if(songModel.getType()==0){
                return true;
            } else {
                return false;
            }
        }
    }

    public static void staticUpdateQueue(){
        if(currPlayedSong==null){
            return;
        }
        ArrayList<SongModel> currentQueue = new ArrayList<>();
        ArrayList<SongModel> queue = getQueuePlaying();
        for(SongModel song: queue){
            if(isPlayable(song,checkConnected)){
                currentQueue.add(song);
            }
        }
        setQueuePlaying(currentQueue);
        listSongs = getQueuePlaying();
        position = MainActivity.getSongPositonByPath(listSongs, currPlayedSong.getPath());
        uri = Uri.parse(currPlayedSong.getPath());
        if(QueuePlayingActivity.queuePlayingAdapter!=null){
            QueuePlayingActivity.queuePlayingAdapter.listSongs = getQueuePlaying();
            QueuePlayingActivity.queuePlayingAdapter.notifyDataSetChanged();
        }
    }

    public static void staticUpdateCurrentsong(){
        if(currPlayedSong==null){
            return;
        }
        if(playingContext!=null){
            while(!isPlayable(currPlayedSong,checkConnected)){
                ((PlayingActivity)playingContext).nextBtnClicked();
            }
        }

    }
}