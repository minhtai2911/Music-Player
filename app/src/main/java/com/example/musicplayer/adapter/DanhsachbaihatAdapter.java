package com.example.musicplayer.adapter;


import static com.example.musicplayer.activity.MainActivity.libraryList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.activity.PlayNhacActivity;
import com.example.musicplayer.activity.PlayingActivity;
import com.example.musicplayer.model.ListLibraryModel;
import com.example.musicplayer.model.SongModel;

import java.util.ArrayList;

public class DanhsachbaihatAdapter extends RecyclerView.Adapter<DanhsachbaihatAdapter.ViewHolder>{
    Context context;
    ArrayList<SongModel> arrayListSong;
    View view;
    String TenThuVienPlayList;

    public DanhsachbaihatAdapter(Context context, ArrayList<SongModel> arrayListSong, String TenThuVienPlayList) {
        this.context = context;
        this.arrayListSong = arrayListSong;
        this.TenThuVienPlayList = TenThuVienPlayList;
    }

    @NonNull
    @Override
    public DanhsachbaihatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.dong_danh_sach_bai_hat, parent, false);
        return new DanhsachbaihatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DanhsachbaihatAdapter.ViewHolder holder, int position) {
        SongModel song = arrayListSong.get(position);
        Log.d(String.valueOf(arrayListSong), "Song: ");
        holder.txtSong.setText(song.getTitle());
        holder.txtCasi.setText(song.getArtist());
        String songPath = arrayListSong.get(position).getPath();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(songPath);
        byte[] img = retriever.getEmbeddedPicture();
        if (img != null) {
            Glide.with(context).asBitmap().load(img).into(holder.imgSong);
        }
        else {
            Glide.with(context).asBitmap().load(R.drawable.imgitem).into(holder.imgSong);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(song.getTitle(), "onClick: ");
                Intent intent = new Intent(context, PlayNhacActivity.class);
                intent.putExtra("cakhuc", (Parcelable) arrayListSong.get(position));
                context.startActivity(intent);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle("Xóa nhạc khỏi thư viện")
                        .setMessage("Bạn có muốn xóa bài "+song.getTitle()+" ?")
                        .setPositiveButton("Xóa", null)
                        .setNegativeButton("Hủy", null)
                        .show();

                Button pos = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button neg = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                pos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletethuvien(song, TenThuVienPlayList);
                        alertDialog.dismiss();
                    }
                });
                neg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                return false;
            }
        });
    }
    private void deletethuvien(SongModel nameDeleteSong, String TenThuVienPlayList) {
        for (int i = 0; i<libraryList.size();i++)
        {
            if(TenThuVienPlayList.equals(libraryList.get(i).getTenThuVienPlayList()))
            {
                libraryList.get(i).getListSong().remove(nameDeleteSong);
            }
        }
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
//    private byte[] getImg(String uri) {
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(uri);
//        return retriever.getEmbeddedPicture();
//    }
}