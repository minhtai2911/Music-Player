package com.example.musicplayer.adapter;

import static com.example.musicplayer.activity.MainActivity.currPlayedSong;
import static com.example.musicplayer.activity.MainActivity.getQueuePlaying;
import static com.example.musicplayer.activity.MainActivity.removeSongFromQueue;

import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.model.SongModel;

import java.util.ArrayList;

public class QueuePlayingAdapter extends RecyclerView.Adapter<QueuePlayingAdapter.ViewHolder>{
    Context context;
    ArrayList<SongModel> listSongs;
    View view;
    public QueuePlayingAdapter(Context context) {
        this.context = context;
        this.listSongs = getQueuePlaying();
    }

    @NonNull
    @Override
    public QueuePlayingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.dong_danh_sach_queue, parent, false);
        return new QueuePlayingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QueuePlayingAdapter.ViewHolder holder, int position) {
        SongModel song = listSongs.get(position);
        holder.songName.setText(song.getTitle());
        holder.songAuthor.setText(song.getArtist());
        int colorTextButton = android.graphics.Color.parseColor("#36CB67");
        int darkColor = android.graphics.Color.parseColor("#121212");
        if(song.getPath().equals(currPlayedSong.getPath())){
            holder.itemView.setBackgroundColor(colorTextButton);
        } else {
            holder.itemView.setBackgroundColor(darkColor);
        }
        String songPath = listSongs.get(position).getPath();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(songPath);
        byte[] img = retriever.getEmbeddedPicture();
        if (img != null) {
            Glide.with(context).asBitmap().load(img).into(holder.imgSong);
        }
        else {
            Glide.with(context).asBitmap().load(R.drawable.imageitem).into(holder.imgSong);
        }
    }

    public void deleteSong(int position){
        SongModel song = listSongs.get(position);
        if(song.getPath().equals(currPlayedSong.getPath())){
            Toast.makeText(context, "Can not remove playing song", Toast.LENGTH_SHORT).show();
            notifyItemChanged(position);
            return;
        }
        String songName = listSongs.get(position).getTitle();
        AlertDialog alertDialogDelete = new AlertDialog.Builder(context, R.style.MyDialogTheme)
                .setTitle("Delete song")
                .setMessage("Are you sure to delete "+songName+" ?")
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
                SongModel song = listSongs.get(position);
                removeSongFromQueue(song);
                notifyItemRemoved(position);
                Toast.makeText(context, "removed "+song.getTitle()+" from queue", Toast.LENGTH_SHORT).show();
                alertDialogDelete.dismiss();
            };
        });
    }
    @Override
    public int getItemCount() {
        return listSongs != null ? listSongs.size() : 0;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSong, imgDrag;
        TextView songName;
        TextView songAuthor;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSong = itemView.findViewById(R.id.imageViewhinhbaihat);
            songName = itemView.findViewById(R.id.textViewtenbaihat);
            songAuthor = itemView.findViewById(R.id.textViewtencasi);
            imgDrag = itemView.findViewById(R.id.image_drag);
        }
    }
}