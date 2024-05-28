package com.example.musicplayer.adapter;
import static com.example.musicplayer.activity.MainActivity.getAllPlaylist;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.activity.AddToPlaylistActivity;
import com.example.musicplayer.fragment.LibraryFragment;
import com.example.musicplayer.model.PlaylistModel;
import com.example.musicplayer.model.SongModel;
import com.example.musicplayer.tool.DatabaseHelper;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AddToPlaylistAdapter extends RecyclerView.Adapter<AddToPlaylistAdapter.MyViewHolder> {
    Context context;
    View view;
    ArrayList<PlaylistModel> libraryList;
    String selectedSongPath;
    AddToPlaylistActivity activity;
    DatabaseHelper myDB;

    public AddToPlaylistAdapter( Context context,  String selectedSongPath, AddToPlaylistActivity activity){
        this.context = context;
        this.selectedSongPath = selectedSongPath;
        this.activity = activity;
        this.myDB = new DatabaseHelper(context);
        this.libraryList = getAllPlaylist(myDB);
    }

    @NonNull
    @Override
    public AddToPlaylistAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.add_to_library_row, parent, false);
        return new AddToPlaylistAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddToPlaylistAdapter.MyViewHolder holder, int position) {
        holder.playlistName.setText(libraryList.get(position).getPlaylistName());
        holder.playlistCount.setText(String.format("%d songs",libraryList.get(position).getListSong().size()));
        byte [] img = libraryList.get(position).getPlaylistImage();
        if (img != null) {
            Glide.with(context).asBitmap().load(img).into(holder.playlistImg);
        } else {
            Glide.with(context).asBitmap().load(R.drawable.default_playlist_image).into(holder.playlistImg);
        }

        PlaylistModel playlist = libraryList.get(position);
        ArrayList<SongModel> playlistSongs = playlist.getListSong();
        for(int i =0;i<playlistSongs.size();i++)
        {
            if(playlistSongs.get(i).getPath().equals(selectedSongPath))
            {
                holder.addImg.setImageResource(R.drawable.check);
                break;
            }
        }

        holder.addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToPlaylist(position, holder);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToPlaylist(position, holder);
            }
        });
    }

    public void addToPlaylist(int position, AddToPlaylistAdapter.MyViewHolder holder){
        PlaylistModel playlist = libraryList.get(position);
        ArrayList<SongModel> playlistSongs = playlist.getListSong();
        for(int i =0;i<playlistSongs.size();i++)
        {
            if(playlistSongs.get(i).getPath().equals(selectedSongPath))
            {
                Toast.makeText(view.getContext(), "Existed in "+ playlist.getPlaylistName(), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        holder.addImg.setImageResource(R.drawable.check);
        myDB.InsertSongIntoPlaylistSong(selectedSongPath,playlist);
        if(LibraryFragment.libraryAdapter!=null){
            LibraryAdapter.libraryList = getAllPlaylist(myDB);
            LibraryFragment.libraryAdapter.notifyDataSetChanged();
        }
        activity.finish();
    }


    @Override
    public int getItemCount() {
        return libraryList != null ? libraryList.size() : 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView playlistImg, addImg;
        TextView playlistName, playlistCount;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistImg = itemView.findViewById(R.id.img_playlist);
            playlistName = itemView.findViewById(R.id.playlist_name);
            playlistCount = itemView.findViewById(R.id.count);
            addImg = itemView.findViewById(R.id.add_img);
        }
    }


}
