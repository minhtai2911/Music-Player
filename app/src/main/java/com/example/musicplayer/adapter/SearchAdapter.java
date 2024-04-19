package com.example.musicplayer.adapter;

import static com.example.musicplayer.activity.MainActivity.queuePlaying;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
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
            Glide.with(context).asBitmap().load(R.drawable.imgitem).into(holder.img_song);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PlayingActivity.class);
                intent.putExtra("position", search);
                context.startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle("Thêm nhạc vào PlayingQueue")
                        .setMessage("Bạn có muốn thêm bài "+song.getTitle()+" ?")
                        .setPositiveButton("Thêm", null)
                        .setNegativeButton("Hủy", null)
                        .show();

                Button pos = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button neg = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                pos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean checkDuplicate = false;
                        for(int i =0;i<queuePlaying.size();i++)
                        {
                            if(queuePlaying.get(i) == song)
                            {
                                AlertDialog alertDialog2 = new AlertDialog.Builder(context)
                                        .setTitle("Thêm nhạc vào PlayingQueue")
                                        .setMessage("Bài "+song.getTitle()+"đã tồn tại trong danh sách")
                                        .setNegativeButton("Hủy", null)
                                        .show();
                                Button neg2 = alertDialog2.getButton(AlertDialog.BUTTON_NEGATIVE);
                                neg2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog2.dismiss();
                                        alertDialog.dismiss();
                                    }
                                });
                                checkDuplicate = true;
                            }
                        }
                        if(!checkDuplicate)
                        {
                            queuePlaying.add(song);
                            if(queuePlayingAdapter != null)
                            {
                                queuePlayingAdapter.notifyDataSetChanged();
                            }
                        }
                        for(int i = 0; i<queuePlaying.size();i++)
                        {
                            Log.d("DANHSACHQUEUE: ", queuePlaying.get(i).getTitle());
                        }
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

    @Override
    public int getItemCount() {
        return songList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView song_name, artist_name;
        ImageView img_song;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            song_name = itemView.findViewById(R.id.song_name);
            artist_name = itemView.findViewById(R.id.artist_name);
            img_song = itemView.findViewById(R.id.img_song);
        }
    }
    private byte[] getImg(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        return retriever.getEmbeddedPicture();
    }
}


