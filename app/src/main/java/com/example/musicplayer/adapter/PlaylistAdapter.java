package com.example.musicplayer.adapter;

import static com.example.musicplayer.activity.MainActivity.currPlayedPlaylistID;
import static com.example.musicplayer.activity.MainActivity.currPlayedSong;
import static com.example.musicplayer.activity.MainActivity.getAllPlaylist;
//import static com.example.musicplayer.activity.MainActivity.prevPosition;
//import static com.example.musicplayer.activity.PlayingActivity.currentPostion;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class PlaylistAdapter  extends RecyclerView.Adapter<PlaylistAdapter.MyViewHolder>{
    Context context;
    ArrayList<SongModel> playlistSongs;
    String playlistID;
    View view;

    public PlaylistAdapter(Context context,  ArrayList<SongModel> playlistSongs, String playlistID){
        this.context = context;
        this.playlistSongs = playlistSongs;
        this.playlistID = playlistID;
    }

    @NonNull
    @Override
    public PlaylistAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.dong_danh_sach_queue, parent, false);
        return new PlaylistAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.MyViewHolder holder, int position) {
        SongModel song = playlistSongs.get(position);
        holder.songName.setText(song.getTitle());
        holder.songArtist.setText(song.getArtist());
        if (song.getPath() != null) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(song.getPath());
            byte[] img = retriever.getEmbeddedPicture();
            Glide.with(context).asBitmap().load(img).into(holder.imgSong);
        }
        else {
            Glide.with(context).asBitmap().load(R.drawable.imgitem).into(holder.imgSong);
        }
        int greenColor = android.graphics.Color.parseColor("#1ED760");
        int whiteColor = android.graphics.Color.parseColor("#FFFFFF");

        if(currPlayedPlaylistID!=null&&currPlayedPlaylistID.equals(playlistID)&&song.getPath().equals(currPlayedSong.getPath()))
        {
            holder.songName.setTextColor(greenColor);
        }else {
            holder.songName.setTextColor(whiteColor);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String songPath = playlistSongs.get(position).getPath();
                Intent intent = new Intent(context, PlayingActivity.class);
                intent.putExtra("songPath",songPath);
                context.startActivity(intent);
            }
        });
    }
    public void deleteSong(int position){
            if(currPlayedPlaylistID!=null&&currPlayedPlaylistID.equals(playlistID)){
                Toast.makeText(context, "Can not delete song in playing playlist", Toast.LENGTH_SHORT).show();
                notifyItemChanged(position);
                return;
            }
            SongModel song = playlistSongs.get(position);
            AlertDialog alertDialogDelete = new AlertDialog.Builder(context, R.style.MyDialogTheme)
                    .setTitle("Delete song")
                    .setMessage("Are you sure to delete "+song.getTitle()+" ?")
                    .setPositiveButton("DELETE", null)
                    .setNegativeButton("CANCEL", null)
                    .show();
            int colorTextButton = android.graphics.Color.parseColor("#36CB67");
            alertDialogDelete.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(colorTextButton);
            alertDialogDelete.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(colorTextButton);
            Button pos = alertDialogDelete.getButton(AlertDialog.BUTTON_POSITIVE);
            Button neg = alertDialogDelete.getButton(AlertDialog.BUTTON_NEGATIVE);

            neg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyItemChanged(position);
                    alertDialogDelete.dismiss();
                }
            });

            pos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SongModel song = playlistSongs.get(position);
                    DatabaseHelper myDB = new DatabaseHelper(context);
                    boolean isDeleted = myDB.DeleteMediaInAPlaylist(song, playlistID);
                    if(isDeleted){
                    playlistSongs.remove(song);
                    notifyItemRemoved(position);
                    }
                    alertDialogDelete.dismiss();
                };
            });
    }



    @Override
    public int getItemCount() {
        return playlistSongs != null ? playlistSongs.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imgSong;
        TextView songName, songArtist;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSong = itemView.findViewById(R.id.imageViewhinhbaihat);
            songName = itemView.findViewById(R.id.textViewtenbaihat);
            songArtist = itemView.findViewById(R.id.textViewtencasi);
        }
    }
}
