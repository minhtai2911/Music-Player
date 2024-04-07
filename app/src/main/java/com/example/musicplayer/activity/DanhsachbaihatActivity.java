package com.example.musicplayer.activity;

import static com.example.musicplayer.activity.MainActivity.libraryList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.adapter.DanhsachbaihatAdapter;
import com.example.musicplayer.model.LibraryModel;
import com.example.musicplayer.model.ListLibraryModel;
import com.example.musicplayer.model.SongModel;

import java.util.ArrayList;

public class DanhsachbaihatActivity extends AppCompatActivity {
    androidx.appcompat.widget.Toolbar toolbar;
    RecyclerView recyclerViewdanhsachbaihat;
    Button floatingActionButton;
    TextView txtcollapsing;
    ListLibraryModel playlist = null;
    ImageView imgdanhsachcakhuc;
    ArrayList<SongModel> mangbaihat;
    ArrayList<LibraryModel> mangbaihatthuvienplaylist;
    DanhsachbaihatAdapter danhsachbaihatAdapter;
//    dsbhthuvienplaylistAdapter dsbhthuvienplaylistadapter;
    ImageView btnThemnhac;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_librarysong);
        AnhXa();
        floatingActionButton.setEnabled(false);
        DataIntent();
        overridePendingTransition(R.anim.anim_intent_in, R.anim.anim_intent_out);
        if (playlist != null && !playlist.equals("")){
            Log.d(String.valueOf(playlist.getListSong().size()), "PlaylistSize: ");
            if(playlist.getListSong().size() > 0)
            {
                Log.d(String.valueOf(playlist), "onCreate: ");
                GetHinhPlaylist(playlist.getListSong());
            }
            GetDataPlaylist(playlist.getTenThuVienPlayList());
            txtcollapsing.setText(playlist.getTenThuVienPlayList());
            getSupportActionBar().setTitle(playlist.getTenThuVienPlayList());
        }
        floatActionButtonClick();
        btnThemnhac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                if(intent.hasExtra("idthuvienplaylist")){
                    intent = new Intent(DanhsachbaihatActivity.this, InsertNhacThuVienActivity.class);
                    intent.putExtra("thisLibrary", playlist.getTenThuVienPlayList());
                    startActivity(intent);
                }
            }
        });

//        swipeRefreshLayout = findViewById(R.id.swipedanhsachbaihat);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Intent intent = getIntent();
//                if(intent.hasExtra("idthuvienplaylist")){
//                    GetDataThuVienPlayList(String.valueOf(thuVienPlayList.getIDThuVienPlayList()));
//                    dsbhthuvienplaylistadapter.notifyDataSetChanged();
//                }
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });

    }

    private void GetHinhPlaylist(ArrayList<SongModel> tenThuVienPlayList) {
        SongModel lastSongAdd = tenThuVienPlayList.get(tenThuVienPlayList.size()-1);
        String songPath = lastSongAdd.getPath();
        playlist.setHinhThuVienPlaylist(songPath);
        Log.d(songPath, "GetHinhPlaylist: ");
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(songPath);
        byte[] img = retriever.getEmbeddedPicture();
        if(img != null)
        {
            Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            imgdanhsachcakhuc.setImageBitmap(bitmap);
        }
    }

    //    private void setValueInView(String hinh) {
//        Picasso.get().load(hinh).into(imgdanhsachcakhuc);
//    }
    private void GetDataPlaylist(String name) {
        for(int i = 0; i<libraryList.size();i++)
        {
            if(name.equals(libraryList.get(i).getTenThuVienPlayList()))
            {
                if(!libraryList.get(i).getListSong().isEmpty())
                {
                    danhsachbaihatAdapter = new DanhsachbaihatAdapter(DanhsachbaihatActivity.this, libraryList.get(i).getListSong());
                    recyclerViewdanhsachbaihat.setLayoutManager(new LinearLayoutManager(DanhsachbaihatActivity.this));
                    recyclerViewdanhsachbaihat.setAdapter(danhsachbaihatAdapter);
                }
            }
        }
    }
    private void AnhXa() {
        toolbar = findViewById(R.id.toolbardanhsachbaihat);
        recyclerViewdanhsachbaihat = findViewById(R.id.recyclerviewdanhsachbaihat);
        imgdanhsachcakhuc = findViewById(R.id.imageviewdanhsachcakhuc);
        floatingActionButton = findViewById(R.id.floatingactionbutton);
        txtcollapsing = findViewById(R.id.textViewcollapsing);
        btnThemnhac = findViewById(R.id.btnthemnhacthuvien);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void DataIntent() {
        Intent intent = getIntent();
        if (intent != null){
            if (intent.hasExtra("idthuvienplaylist")){
                String getTenThuVien = intent.getStringExtra("idthuvienplaylist");
                for(int i = 0;i<libraryList.size();i++)
                {
                    if(getTenThuVien.equals(libraryList.get(i).getTenThuVienPlayList()))
                    {
                        playlist = libraryList.get(i);
                    }
                }
            }
        }
    }
    private void floatActionButtonClick(){
        mangbaihat = playlist.getListSong();
        floatingActionButton.setEnabled(true);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DanhsachbaihatActivity.this, PlayNhacActivity.class);
                if (mangbaihat!=null){
                    if (mangbaihat.size() > 0){
                        intent.putExtra("cacbaihat", mangbaihat);
                        startActivity(intent);
                    }else {
                        Toast.makeText(DanhsachbaihatActivity.this, "Danh sách không có bài hát nào cả :(", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if (mangbaihatthuvienplaylist != null){
                        if (mangbaihatthuvienplaylist.size() > 0){
                            intent.putExtra("cacbaihatthuvien", mangbaihatthuvienplaylist);
                            startActivity(intent);
                        }else {
                            Toast.makeText(DanhsachbaihatActivity.this, "Danh sách không có bài hát nào cả :(", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
}
