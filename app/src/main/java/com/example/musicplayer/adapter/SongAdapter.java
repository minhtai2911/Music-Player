package com.example.musicplayer.adapter;

import static com.example.musicplayer.activity.MainActivity.addSongToQueue;
import static com.example.musicplayer.activity.MainActivity.queuePlaying;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
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
import com.example.musicplayer.activity.AddToPlaylistActivity;
import com.example.musicplayer.activity.MainActivity;
import com.example.musicplayer.activity.PlaylistActivity;
import com.example.musicplayer.model.PlaylistModel;
import com.example.musicplayer.model.SongModel;
import com.example.musicplayer.activity.PlayingActivity;
import com.example.musicplayer.tool.DatabaseHelper;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    Context context;
    ArrayList<SongModel> arrayListSong;
    View view;

    public SongAdapter(Context context, ArrayList<SongModel> arrayListSong) {
        this.context = context;
        this.arrayListSong = arrayListSong;
    }

    @NonNull
    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_song, parent, false);
        return new SongAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.ViewHolder holder, final int position) {
        SongModel song = arrayListSong.get(position);
        holder.txtSong.setText(song.getTitle());
        byte[] img = getImg(song.getPath());
        if (img != null) {
            Glide.with(context).asBitmap().load(img).into(holder.imgSong);
        }
        else {
            Glide.with(context).asBitmap().load(R.drawable.imgitem).into(holder.imgSong);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String songPath = arrayListSong.get(position).getPath();
                Intent intent = new Intent(context, PlayingActivity.class);
                intent.putExtra("songPath",songPath);
                context.startActivity(intent);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDialog(position, song);
                return false;
            }
        });
    }

    public void showDialog(int position, SongModel song) {
        final Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_song_dialog);
        LinearLayout addQueue = dialog.findViewById(R.id.add_queue);
        LinearLayout addPlaylist = dialog.findViewById(R.id.add_playlist);
        ImageView closeIcon = dialog.findViewById(R.id.layout_close);
        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        addQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSongToQueue(song, v.getContext());
                dialog.dismiss();
            }
        });
        addPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String songPath = song.getPath();
                Intent intent = new Intent(context, AddToPlaylistActivity.class);
                intent.putExtra("songPath", songPath);
                context.startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    public int getItemCount() {
        return arrayListSong != null ? arrayListSong.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSong;
        TextView txtSong;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSong = itemView.findViewById(R.id.imgSong);
            txtSong = itemView.findViewById(R.id.txtSong);
        }
    }
    private byte[] getImg(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        return retriever.getEmbeddedPicture();
    }
}
