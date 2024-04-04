package com.example.musicplayer.adapter;

import static com.example.musicplayer.activity.MainActivity.libraryList;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.activity.DanhsachbaihatActivity;
import com.example.musicplayer.activity.PlayingActivity;
import com.example.musicplayer.model.ListLibraryModel;
import com.example.musicplayer.model.SongModel;

import java.io.Serializable;
import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    Context context;
    ArrayList<ListLibraryModel> arrayListPlaylist;
    View view;

    public PlaylistAdapter(Context context, ArrayList<ListLibraryModel> arrayListPlaylist) {
        this.context = context;
        this.arrayListPlaylist = arrayListPlaylist;
        Log.d("Danh sach bai hat goc: ", String.valueOf(libraryList));
        Log.d("Danh sach bai hat: ", String.valueOf(arrayListPlaylist));
    }

    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_song, parent, false);
        return new PlaylistAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, final int position) {
        ListLibraryModel playlist = arrayListPlaylist.get(position);
        holder.txtSong.setText(playlist.getTenThuVienPlayList());
//        byte[] img = getImg(playlist.getHinhThuVienPlaylist());
//        if (img != null) {
//            Glide.with(context).asBitmap().load(img).into(holder.imgSong);
//        }
//        else {
//            Glide.with(context).asBitmap().load(R.drawable.imgitem).into(holder.imgSong);
//        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DanhsachbaihatActivity.class);
                intent.putExtra("idthuvienplaylist",arrayListPlaylist.get(position).getTenThuVienPlayList());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListPlaylist != null ? arrayListPlaylist.size() : 0;
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
//    private byte[] getImg(String uri) {
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(uri);
//        return retriever.getEmbeddedPicture();
//    }
}
