package com.example.musicplayer.adapter;


import static com.example.musicplayer.activity.MainActivity.libraryList;
import static com.example.musicplayer.activity.MainActivity.prevPosition;
import static com.example.musicplayer.activity.MainActivity.queuePlaying;
import static com.example.musicplayer.activity.PlayingActivity.currentPostion;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

public class QueuePlayingAdapter extends RecyclerView.Adapter<QueuePlayingAdapter.ViewHolder>{
    Context context;
    ArrayList<SongModel> arrayListSong;
    public boolean checkSongBefore = false;
    View view;
    RecyclerView recyclerViewQueue;
    int currentPositionAdapter;
    public QueuePlayingAdapter(Context context, ArrayList<SongModel> arrayListSong, int currentPosition) {
        this.context = context;
        this.arrayListSong = arrayListSong;
        Log.d("QueuePlayingAdapter", String.valueOf(currentPosition));
        this.currentPositionAdapter = currentPosition;
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
        Log.d("CURRENT POSITION", String.valueOf(currentPositionAdapter));
        int mauHongNhat = android.graphics.Color.parseColor("#FFC0CB");
        int mauTrang = android.graphics.Color.parseColor("#ffffff");
        SongModel song = arrayListSong.get(position);
        Log.d(String.valueOf(arrayListSong), "Song: ");
        holder.txtSong.setText(song.getTitle());
        holder.txtCasi.setText(song.getArtist());
        if(currentPositionAdapter > arrayListSong.size() ||song == arrayListSong.get(currentPositionAdapter))
        {
            if( currentPositionAdapter > arrayListSong.size() && arrayListSong.size() > 1)
            {
                currentPositionAdapter = 0;
            }
            else if(currentPositionAdapter == 0) {
                currentPostion = currentPositionAdapter;
                holder.itemView.setBackgroundColor(mauHongNhat);
            }
            else {
                currentPostion = currentPositionAdapter;
                Log.d("currentPostion ADAPTER", String.valueOf(currentPostion));
                holder.itemView.setBackgroundColor(mauHongNhat);
            }
        }
//        holder.itemView.setBackgroundColor(mauHongNhat);
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
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(song.getTitle(), "onClick: ");
//                Intent intent = new Intent(context, PlayNhacActivity.class);
//                intent.putExtra("cakhuc", (Parcelable) arrayListSong.get(position));
//                context.startActivity(intent);
//            }
//        });
        holder.imgDelete.setOnClickListener(
                new ImageView.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog alertDialogDelete = new AlertDialog.Builder(context)
                                .setTitle("Xóa nhạc khỏi playing queue")
                                .setMessage("Bạn có muốn xóa bài "+song.getTitle()+" ?")
                                .setPositiveButton("Xóa", null)
                                .setNegativeButton("Hủy", null)
                                .show();

                        Button pos = alertDialogDelete.getButton(AlertDialog.BUTTON_POSITIVE);
                        Button neg = alertDialogDelete.getButton(AlertDialog.BUTTON_NEGATIVE);
                        pos.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean checkSongActive = false;
                                for (int i = 0; i < queuePlaying.size(); i++) {
                                    if(position < currentPostion)
                                    {
                                        checkSongBefore = true;
                                    }
                                    if (currentPositionAdapter > queuePlaying.size() ||queuePlaying.get(currentPositionAdapter) == song) {
                                        AlertDialog alertDialogDelete2 = new AlertDialog.Builder(context)
                                                .setTitle("Xóa nhạc khỏi playing queue")
                                                .setMessage("Bạn không thể xóa bài đang được phát")
                                                .setNegativeButton("Hủy", null)
                                                .show();
                                        Button neg2 = alertDialogDelete2.getButton(AlertDialog.BUTTON_NEGATIVE);
                                        neg2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                alertDialogDelete2.dismiss();
                                                alertDialogDelete.dismiss();
                                            }
                                        });
                                        checkSongActive = true;
                                    }
                                }
                                if(!checkSongActive)
                                {
                                    if(checkSongBefore)
                                    {
                                        Log.d("VAOCHECKSONGBEFORE", "onClick: ");
                                        prevPosition -= 1;
                                        currentPositionAdapter -=1;
                                        currentPostion -= 1;
//                                        if(position == currentPositionAdapter)
//                                        {
//                                            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
//                                        }
                                    }
                                    queuePlaying.remove(song);
                                    notifyDataSetChanged();
                                    alertDialogDelete.dismiss();
                                }
                            };
                        });
                        neg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialogDelete.dismiss();
                            }
                        });
                    };
                }
        );
        if(checkSongBefore)
        {
            if(position == currentPositionAdapter + 1)
            {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
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
        ImageView imgDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSong = itemView.findViewById(R.id.imageViewhinhbaihat);
            txtSong = itemView.findViewById(R.id.textViewtenbaihat);
            txtCasi = itemView.findViewById(R.id.textViewtencasi);
            imgDelete = itemView.findViewById(R.id.imageViewxoadanhsachbaihat);
        }
    }
//    private byte[] getImg(String uri) {
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(uri);
//        return retriever.getEmbeddedPicture();
//    }
}