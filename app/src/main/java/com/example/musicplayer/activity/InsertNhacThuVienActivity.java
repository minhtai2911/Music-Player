//package com.example.musicplayer.activity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import com.example.musicplayer.R;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//
//public class InsertNhacThuVienActivity extends AppCompatActivity {
//    private int idThuVien;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_insert_nhac_thu_vien);
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        Fragment_insert_nhac_thu_vien insert_nhac_thu_vien = new Fragment_insert_nhac_thu_vien();
//        fragmentTransaction.add(R.id.frameContent, insert_nhac_thu_vien);
//        fragmentTransaction.commit();
//
//        Intent intent = getIntent();
//        idThuVien = intent.getIntExtra("id", 1);
//
//
//    }
//
//    public int getId() {
//        return idThuVien;
//    }
//}
