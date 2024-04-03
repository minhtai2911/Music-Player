package com.example.musicplayer.adapter;


import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
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
import com.example.musicplayer.model.SongModel;

import java.util.ArrayList;

public class DanhsachbaihatAdapter extends RecyclerView.Adapter<DanhsachbaihatAdapter.ViewHolder>{
    Context context;
    ArrayList<SongModel> arrayListSong;
    View view;


    public DanhsachbaihatAdapter(Context context, ArrayList<SongModel> arrayListSong) {
        this.context = context;
        this.arrayListSong = arrayListSong;
    }

    @NonNull
    @Override
    public DanhsachbaihatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.dong_danh_sach_bai_hat, parent, false);
        return new DanhsachbaihatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DanhsachbaihatAdapter.ViewHolder holder, final int position) {
        SongModel song = arrayListSong.get(position);
        holder.txtSong.setText(song.getTitle());
        holder.txtCasi.setText(song.getArtist());
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
                Intent intent = new Intent(context, PlayingActivity.class);
                intent.putExtra("position",position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListSong != null ? arrayListSong.size() : 0;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSong;
        TextView txtSong;
        TextView txtCasi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSong = itemView.findViewById(R.id.imageViewhinhbaihat);
            txtSong = itemView.findViewById(R.id.textViewtenbaihat);
            txtCasi = itemView.findViewById(R.id.textViewtencasi);
        }
    }
    private byte[] getImg(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        return retriever.getEmbeddedPicture();
    }
}