package com.example.musicplayer.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.musicplayer.R;
import com.example.musicplayer.activity.DanhsachbaihatActivity;
import com.example.musicplayer.model.ListLibraryModel;

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
        holder.txtsoluongnhac.setText(thuVienPlayList.getListSong().size()+" songs");
//        Picasso.get().load(thuVienPlayList.getHinhThuVienPlaylist()).into(holder.imgthuvienplaylist);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DanhsachbaihatActivity.class);
                intent.putExtra("idthuvienplaylist", (Parcelable) mangthuvienplaylist.get(position));
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
                        deletethuvien(thuVienPlayList.getTenThuVienPlayList());
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
    private void deletethuvien(String tenthuvien) {

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
