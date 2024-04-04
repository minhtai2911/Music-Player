package com.example.musicplayer.adapter;

import static com.example.musicplayer.activity.MainActivity.libraryList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.musicplayer.R;
import com.example.musicplayer.model.ListLibraryModel;
import com.example.musicplayer.model.SongModel;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class InsertNhacThuVienAdapter extends RecyclerView.Adapter<InsertNhacThuVienAdapter.ViewHolder>{

    Context context;
    ArrayList<SongModel> mangbaihat;
    String thisLibrary;

    public InsertNhacThuVienAdapter(Context context, ArrayList<SongModel> mangbaihat, String thisLibrary) {
        this.context = context;
        this.mangbaihat = mangbaihat;
        this.thisLibrary = thisLibrary;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.dong_tim_kiem_nhac_thuvien, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SongModel baiHat = mangbaihat.get(position);
        holder.tenbaihat.setText(baiHat.getTitle());
        holder.temcasi.setText(baiHat.getArtist());
//        Picasso.get(/*context*/).load(baiHat.getHinhBaiHat()).into(holder.imgtimkiem);
        holder.imginsertnhac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertDataBaiHatThuVien(thisLibrary, baiHat.getTitle(),baiHat.getArtist(),
                        baiHat.getDuration(), baiHat.getPath());
//                UpdateHinhThuVien(idthuvien, mangbaihat.get(position).getHinhBaiHat());
            }
        });

    }
    public void InsertDataBaiHatThuVien(String thisLibrary, String tbh, String tcs, String thgianbh, String lbh) {
//        String path, String title, String artist, String duration
        SongModel tempSong = new SongModel(lbh,tbh,tcs,thgianbh);
        for (int i = 0;i<libraryList.size();i++)
        {
            if(thisLibrary.equals(libraryList.get(i).getTenThuVienPlayList()))
            {
                libraryList.get(i).setListSong(tempSong);
            }
        }
        Toast.makeText(context, "Đã thêm", Toast.LENGTH_SHORT).show();
    }

//    public void UpdateHinhThuVien(int idtv, String hbh) {
//
//    }

    @Override
    public int getItemCount() {
        return mangbaihat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tenbaihat, temcasi;
        ImageView imgtimkiem, imginsertnhac;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tenbaihat = itemView.findViewById(R.id.txttennhacthuvien);
            temcasi = itemView.findViewById(R.id.txtcasinhacthuvien);
            imgtimkiem = itemView.findViewById(R.id.imgnhacthuvien);
            imginsertnhac = itemView.findViewById(R.id.imginsertnhacthuvien);

        }
    }
}
