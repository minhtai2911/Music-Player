package com.example.musicplayer.fragment;

        import static com.example.musicplayer.activity.MainActivity.songList;
        import static com.example.musicplayer.activity.PlayingActivity.isPlayable;
        import static com.example.musicplayer.tool.NetworkChangeReceiver.checkConnected;

        import android.content.Intent;
        import android.graphics.drawable.GradientDrawable;
        import android.net.Uri;
        import android.os.Bundle;

        import androidx.activity.EdgeToEdge;
        import androidx.fragment.app.Fragment;

        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

        import com.bumptech.glide.Glide;
        import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
        import com.bumptech.glide.request.RequestOptions;
        import com.example.musicplayer.R;
        import com.example.musicplayer.activity.AddToPlaylistActivity;
        import com.example.musicplayer.activity.PlayingActivity;
        import com.example.musicplayer.model.SongModel;
        import com.example.musicplayer.tool.GetDominantColor;

        import android.os.Handler;
        import android.widget.ImageView;
        import android.widget.RelativeLayout;
        import android.widget.TextView;

        import java.util.Random;

        import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {
    public View view;
    public  TextView song_name, artist_name;
    public  CircleImageView cover_img;

    public ImageView randomImage;
    public  RelativeLayout relativeLayout;
    public  SongModel randomSong = null;
    public  Handler handler;

    public  Runnable runnable;



    public HomeFragment() {
        handler  = new Handler();
        runnable  = new Runnable() {
            @Override
            public void run() {
                if(!songList.isEmpty()){
                    Random random = new Random();
                    int positionRandom = random.nextInt(songList.size());
                    randomSong = songList.get(positionRandom);
                    if(randomSong!=null){
                        while(!isPlayable(randomSong, checkConnected)){
                            int positionRandomAgain = random.nextInt(songList.size());
                            randomSong = songList.get(positionRandomAgain);
                        }
                    }

                    song_name = view.findViewById(R.id.song_name_random);
                    artist_name = view.findViewById(R.id.song_artist_random);
                    cover_img = view.findViewById(R.id.song_img);
                    relativeLayout = view.findViewById(R.id.random_layout);
                    song_name.setText(randomSong.getTitle());
                    artist_name.setText(randomSong.getArtist());
                    randomImage = view.findViewById(R.id.random_img);
                    randomImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(randomSong!=null&&isPlayable(randomSong, checkConnected)){
                                String songPath = randomSong.getPath();
                                Intent intent = new Intent(v.getContext(), PlayingActivity.class);
                                intent.putExtra("songPath",songPath);
                                startActivity(intent);
                            }
                        }
                    });
                    metaData();
                }
                handler.postDelayed(this, 5000);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    public  void startRandomSong() {
        Log.d("Interval", "Task start!");
        handler.post(runnable);
    }

    public  void stopRandomSong() {
        Log.d("Interval", "Task remove!");
        handler.removeCallbacks(runnable);
    }

    public void metaData() {
        byte[] img = randomSong.getImg();
        if (img != null) {
            Glide.with(view.getContext()).asBitmap().load(img).apply(RequestOptions.bitmapTransform(new RoundedCorners(50))).into(cover_img);
        }
        else {
            Glide.with(view.getContext()).asBitmap().load(R.drawable.default_image).apply(RequestOptions.bitmapTransform(new RoundedCorners(50))).into(cover_img);
        }
    }
}