package com.example.musicplayer.activity;

import static com.example.musicplayer.activity.MainActivity.prevPosition;
import static com.example.musicplayer.activity.MainActivity.queuePlaying;
import static com.example.musicplayer.activity.MainActivity.songList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicplayer.Application;
import com.example.musicplayer.R;
import com.example.musicplayer.interfaces.ActionPlaying;
import com.example.musicplayer.model.SongModel;
import com.example.musicplayer.receiver.NotificationReceiver;
import com.example.musicplayer.service.MusicService;
import com.example.musicplayer.tool.GetDominantColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;



public class PlayingActivity extends AppCompatActivity implements ActionPlaying, ServiceConnection {
    TextView artist_name, duration_played, duration_total, title;
    ImageView cover_img, nextBtn, prevBtn, backBtn, shuffleBtn, repeatBtn, playPauseBtn, img_queue;
    SeekBar seekBar;
    public static int position = -1;
    public static int currentPostion = -1;
    public static ArrayList<SongModel> listSongs = new ArrayList<>();
    static Uri uri;

    public static MusicService musicService;
    private Handler handler = new Handler();
    private Thread playThread, nextThread, prevThread;
    static Boolean shuffleBoolean = false;
    static int repeat = 0;
    public static TextView song_name;
    public static byte[] img_status;
    MediaSessionCompat mediaSessionCompat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_playing);
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "My Audio");
        initViews();
        getIntentMethod();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (musicService != null && fromUser) {
                    musicService.seekTo(progress*1000);
                }
                if (seekBar.getProgress() == seekBar.getMax()) {
                    if (repeat == 2) {
                        repeatPlay();
                    }
                    else {
                        nextBtnClicked();
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
                Log.d("POSITION CLICK", String.valueOf(position));
                Intent intent = new Intent(PlayingActivity.this, QueuePlayingActivity.class);
                intent.putExtra("queueList", listSongs);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }

//    @Override
//    protected void onResumeFragments() {
//        super.onResumeFragments();
//        getIntentMethod();
//    }
    private void repeatPlay() {
        uri = Uri.parse(listSongs.get(position).getPath());
        if (musicService != null) {
            musicService.stop();
            musicService.release();
            musicService.createMediaPlayer(position);
            musicService.start();
        }
        else {
            musicService.createMediaPlayer(position);
            musicService.start();
        }
        seekBar.setMax(musicService.getDuration()/1000);
        metaData(uri);
        song_name.setText(listSongs.get(position).getTitle());
        title.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist());
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

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position",-1);
//        queuePlaying.clear();
        Log.d("CURRENT POSITIO ", String.valueOf(currentPostion));
        if(currentPostion != -1)
        {
            position = currentPostion;
        }
        String songName;
        if(queuePlaying.size()>1)
        {
            if(position > queuePlaying.size())
            {
                songName = songList.get(position).getTitle();
            }
            else{
                songName = queuePlaying.get(position).getTitle();
            }
        }
        else {
            songName = songList.get(position).getTitle();
        }
        Log.d("POSITION OLD", String.valueOf(position));
        Log.d("POSITION PREV", String.valueOf(prevPosition));
        Log.d("songNamePosition: ", songName);
        boolean playBackStatus = getIntent().getBooleanExtra("playBackStatus", false);
        listSongs = songList;
        if (!playBackStatus) {
            if (listSongs != null) {

                uri = Uri.parse(listSongs.get(position).getPath());
            }
            if (musicService != null) {
                musicService.stop();
                musicService.release();
                musicService.createMediaPlayer(position);
                musicService.start();

            }
        }
        else playPauseBtn.setImageResource(R.drawable.nutpause);


        metaData(uri);
        song_name.setText(listSongs.get(position).getTitle());
        title.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist());
    }

    private void initViews() {
        backBtn = findViewById(R.id.backBtn);
        song_name = findViewById(R.id.song_name);
        title = findViewById(R.id.toolbar_title);
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
        img_queue = findViewById(R.id.img_queue);
    }
    private void metaData(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationToTal = Integer.parseInt(listSongs.get(position).getDuration()) / 1000;
        duration_total.setText(formattedTime(durationToTal));
        byte[] img = retriever.getEmbeddedPicture();
        img_status = img;
        if (img != null) {
            Glide.with(this).asBitmap().load(img).apply(RequestOptions.bitmapTransform(new RoundedCorners(50))).into(cover_img);
        }
        else {
            Glide.with(this).asBitmap().load(R.drawable.imgitem).into(cover_img);
        }
        if ((position == listSongs.size() - 1) && repeat != 1 && !shuffleBoolean) nextBtn.setImageResource(R.drawable.iconnextnull);
        else nextBtn.setImageResource(R.drawable.iconnext);
        if (position == 0 && repeat != 1 && !shuffleBoolean) prevBtn.setImageResource(R.drawable.iconpreviousnull);
        else prevBtn.setImageResource(R.drawable.iconprevious);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColors(new int[]{GetDominantColor.getDominantColor(img), R.color.colorPrimaryDark});
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
//        getIntentMethod();
//        Log.d("ON RESUME!!!!", String.valueOf(position));
//        for(int i = 0; i<listSongs.size();i++)
//        {
//            Log.d("ON RESUME LISTSONG!!!!", listSongs.get(i).getTitle());
//        }
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    private void prevThreadBtn() {
        prevThread = new Thread() {
            @Override
            public void run() {
                super.run();
                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    public void prevBtnClicked() {
        listSongs = songList;
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
        showNotification(R.drawable.ic_pause);
        playPauseBtn.setImageResource(R.drawable.nutplay);
        uri = Uri.parse(listSongs.get(position).getPath());
        if (musicService != null) {
            musicService.stop();
            musicService.release();
            musicService.createMediaPlayer(position);
            musicService.start();
        }
        else {
            musicService.createMediaPlayer(position);
            musicService.start();
        }
        seekBar.setMax(musicService.getDuration()/1000);
        metaData(uri);
        song_name.setText(listSongs.get(position).getTitle());
        title.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist());
    }

    private void nextThreadBtn() {
        nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    public void nextBtnClicked() {
        listSongs = songList;
        if (repeat == 2) {
            repeatBtn.setImageResource(R.drawable.iconrepeated);
            repeat = 1;
            return;
        }
        if (repeat != 1 && !shuffleBoolean) {
            if (position == listSongs.size()-1) return;
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
            showNotification(R.drawable.ic_pause);
            playPauseBtn.setImageResource(R.drawable.nutplay);
            uri = Uri.parse(listSongs.get(position).getPath());
            prevPosition = position;
        }
        else {
            return;
        }
        if (musicService != null) {
            musicService.stop();
            musicService.release();
            musicService.createMediaPlayer(position);
            musicService.start();
        }
        else {
            musicService.createMediaPlayer(position);
            musicService.start();
        }
        seekBar.setMax(musicService.getDuration()/1000);
        metaData(uri);
        song_name.setText(listSongs.get(position).getTitle());
        title.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist());
    }

    private int getRandom(int size) {
        Random random = new Random();
        return random.nextInt(size);
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
            showNotification(R.drawable.ic_play);
            playPauseBtn.setImageResource(R.drawable.nutpause);
            musicService.pause();
            seekBar.setMax(musicService.getDuration()/1000);
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
            showNotification(R.drawable.ic_pause);
            playPauseBtn.setImageResource(R.drawable.nutplay);
            musicService.start();
            seekBar.setMax(musicService.getDuration()/1000);
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
        Log.e("Check service", "onServiceConnected: "+musicService);
        Log.e("Check Position", "onServiceConnected: " + position);
        musicService.setCallBack(PlayingActivity.this);
        musicService.createMediaPlayer(position);
        musicService.start();
        if (musicService.isPlaying()) playPauseBtn.setImageResource(R.drawable.nutplay);
        seekBar.setMax(musicService.getDuration() / 1000);
        showNotification(R.drawable.ic_pause);
        metaData(uri);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService = null;
    }
    void showNotification(int playPauseBtn) {
        Intent intent = new Intent(this, PlayingActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Intent prevIntent = new Intent(this, NotificationReceiver.class).setAction(Application.ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent pauseIntent = new Intent(this, NotificationReceiver.class).setAction(Application.ACTION_PLAY);
        PendingIntent pausePending = PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent nextIntent = new Intent(this, NotificationReceiver.class).setAction(Application.ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE);

        byte[] picture = null;
        picture = getImg(songList.get(position).getPath());
        Bitmap thumb = null;
        if(picture != null){
            thumb = BitmapFactory.decodeByteArray(picture, 0, picture.length);
        }
        else {
            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.imgitem);
        }
        Notification notification = new NotificationCompat.Builder(this, Application.CHANNEL_ID_2)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(playPauseBtn)
                .setLargeIcon(thumb)
                .setContentTitle(songList.get(position).getTitle())
                .setContentText(songList.get(position).getArtist())
                .addAction(R.drawable.iconprevious, "Previous", prevPending)
                .addAction(playPauseBtn, "Pause", pausePending)
                .addAction(R.drawable.iconnext, "Next", nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
    private byte[] getImg(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        return retriever.getEmbeddedPicture();
    }

}