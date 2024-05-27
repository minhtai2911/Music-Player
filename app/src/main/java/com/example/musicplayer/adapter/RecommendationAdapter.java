package com.example.musicplayer.adapter;

import static com.example.musicplayer.activity.MainActivity.addSongToQueue;

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

import java.util.ArrayList;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {
    Context context;
    ArrayList<SongModel> arrayListSongRecommend;
    View view;
    public RecommendationAdapter(Context context, ArrayList<SongModel> arrayListSongRecommend) {
        this.context = context;
        this.arrayListSongRecommend = arrayListSongRecommend;
    }

    @NonNull
    @Override
    public RecommendationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_song, parent, false);
        return new RecommendationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationAdapter.ViewHolder holder, int position) {

    }

//    @Override
//    public void onBindViewHolder(@NonNull SongAdapter.ViewHolder holder, int position) {
//
//    }

    @Override
    public int getItemCount() {
        return arrayListSongRecommend.size();
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
