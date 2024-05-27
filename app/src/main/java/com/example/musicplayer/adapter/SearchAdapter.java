package com.example.musicplayer.adapter;

import static com.example.musicplayer.activity.MainActivity.addSongToQueue;
import static com.example.musicplayer.adapter.SongAdapter.showAddSongDialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.activity.AddToPlaylistActivity;
import com.example.musicplayer.activity.PlayingActivity;
import com.example.musicplayer.model.SongModel;

import java.util.HashMap;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{
    Context context;
    HashMap<Integer,SongModel> songList;
    QueuePlayingAdapter queuePlayingAdapter;
    public SearchAdapter(Context context, HashMap<Integer,SongModel> songList,QueuePlayingAdapter queuePlayingAdapter) {
        this.context = context;
        this.songList = songList;
        this.queuePlayingAdapter= queuePlayingAdapter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int search = (int) songList.keySet().toArray()[position];
        SongModel song = songList.get(search);
        if (song == null) return;
        holder.song_name.setText(song.getTitle());
        holder.artist_name.setText(song.getArtist());
        byte[] img = getImg(song.getPath());
        if (img != null) {
            Glide.with(context).asBitmap().load(img).into(holder.img_song);
        }
        else {
            Glide.with(context).asBitmap().load(R.drawable.imageitem).into(holder.img_song);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String songPath = song.getPath();
                Intent intent = new Intent(context, PlayingActivity.class);
                intent.putExtra("songPath",songPath);
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showAddSongDialog(song, v.getContext());
                return false;
            }
        });

        holder.threeDotImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddSongDialog(song, v.getContext());
            }
        });
    }



    @Override
    public int getItemCount() {
        return songList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView song_name, artist_name;
        ImageView img_song, threeDotImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            song_name = itemView.findViewById(R.id.song_name);
            artist_name = itemView.findViewById(R.id.artist_name);
            img_song = itemView.findViewById(R.id.img_song);
            threeDotImg = itemView.findViewById(R.id.three_dot);
        }
    }
    private byte[] getImg(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        return retriever.getEmbeddedPicture();
    }
}


