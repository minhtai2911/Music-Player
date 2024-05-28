package com.example.musicplayer.adapter;

import static com.example.musicplayer.adapter.SongAdapter.showAddSongDialog;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.activity.PlayingActivity;
import com.example.musicplayer.activity.PlaylistActivity;
import com.example.musicplayer.model.PlaylistModel;
import com.example.musicplayer.model.SongModel;
import com.example.musicplayer.tool.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;



public class RecommendSongsToPlaylistAdapter extends RecyclerView.Adapter<RecommendSongsToPlaylistAdapter.ViewHolder>{
    Context context;
    ArrayList<SongModel> songList;
    PlaylistModel playlistModel;
    DatabaseHelper myDB;
    public RecommendSongsToPlaylistAdapter(Context context, ArrayList<SongModel> songList,  PlaylistModel playlistModel) {
        this.context = context;
        myDB = new DatabaseHelper(context);
        this.songList =songList;
        this.playlistModel = playlistModel;
    }
    
    public void setSongList( ArrayList<SongModel> songList){
        this.songList =songList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.add_to_library_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SongModel song = songList.get(position);
        holder.song_name.setText(song.getTitle());
        holder.artist_name.setText(song.getArtist());
        byte[] img = song.getImg();
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
                myDB.InsertSongIntoPlaylistSong(song.getPath(), playlistModel);
                ((PlaylistActivity)(v.getContext())).updateRecommend();
                return false;
            }
        });

        holder.addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDB.InsertSongIntoPlaylistSong(song.getPath(), playlistModel);
                ((PlaylistActivity)(v.getContext())).updateRecommend();
            }
        });
    }



    @Override
    public int getItemCount() {
        return songList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView song_name, artist_name;
        ImageView img_song, addImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            song_name = itemView.findViewById(R.id.playlist_name);
            artist_name = itemView.findViewById(R.id.count);
            img_song = itemView.findViewById(R.id.img_playlist);
            addImg = itemView.findViewById(R.id.add_img);
        }
    }
}


