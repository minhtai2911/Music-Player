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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.activity.DanhsachbaihatActivity;
import com.example.musicplayer.model.ListLibraryModel;
import com.example.musicplayer.model.SongModel;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LibraryPlaylistAdapter extends RecyclerView.Adapter<LibraryPlaylistAdapter.ViewHolder>{
    Context context;
    ArrayList<ListLibraryModel> mangthuvienplaylist;
    View view;
    public LibraryPlaylistAdapter(Context context, ArrayList<ListLibraryModel> mangthuvienplaylist) {
        this.context = context;
        this.mangthuvienplaylist = mangthuvienplaylist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.dong_thuvien_playlist,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListLibraryModel thuVienPlayList = mangthuvienplaylist.get(position);
        holder.txttenthuvienplaylist.setText(thuVienPlayList.getTenThuVienPlayList());
        Log.d(String.valueOf(thuVienPlayList.getListSong()), "onBindViewHolder: ");
        Log.d(String.valueOf(thuVienPlayList.getListSong().size()), "Size: ");
        if(thuVienPlayList.getListSong().size()>0){
            Log.d(String.valueOf(thuVienPlayList.getListSong()), "chay vao if: ");
            holder.txtsoluongnhac.setText(thuVienPlayList.getListSong().size()+" songs");
        }else {
            Log.d(String.valueOf(thuVienPlayList.getListSong()), "chay vao else: ");
            holder.txtsoluongnhac.setText("0 songs");
        }
        if(thuVienPlayList.getListSong().size()>0)
        {
            SongModel lastSongAdd = thuVienPlayList.getListSong().get(thuVienPlayList.getListSong().size()-1);
            String songPath = lastSongAdd.getPath();
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(songPath);
            byte[] img = retriever.getEmbeddedPicture();
            if (img != null) {
                Glide.with(context).asBitmap().load(img).into(holder.imgthuvienplaylist);
            }
            else {
                Glide.with(context).asBitmap().load(R.drawable.imgitem).into(holder.imgthuvienplaylist);
            }
        }
//        Picasso.get().load(thuVienPlayList.getHinhThuVienPlaylist()).into(holder.imgthuvienplaylist);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DanhsachbaihatActivity.class);
                intent.putExtra("idthuvienplaylist", mangthuvienplaylist.get(position).getTenThuVienPlayList());
                context.startActivity(intent);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle("Xóa thư viện")
                        .setMessage("Bạn có muốn xóa thư viện "+thuVienPlayList.getTenThuVienPlayList()+" ?")
                        .setPositiveButton("Xóa", null)
                        .setNegativeButton("Hủy", null)
                        .show();

                Button pos = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button neg = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                pos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletethuvien(thuVienPlayList);
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
    private void deletethuvien(ListLibraryModel tenthuvien) {
        libraryList.remove(tenthuvien);
    }

    @Override
    public int getItemCount() {
        return mangthuvienplaylist != null ? mangthuvienplaylist.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgthuvienplaylist;
        TextView txttenthuvienplaylist, txtsoluongnhac;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgthuvienplaylist = view.findViewById(R.id.imageviewthuvienplaylist);
            txttenthuvienplaylist = view.findViewById(R.id.textviewthuvienplaylist);
            txtsoluongnhac = view.findViewById(R.id.txtsongnum);
        }

    }
}
