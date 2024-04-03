package com.example.musicplayer.fragment;

import static com.bumptech.glide.Glide.init;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.musicplayer.R;
import com.example.musicplayer.activity.DanhsachbaihatActivity;
import com.example.musicplayer.activity.MainActivity;
import com.example.musicplayer.adapter.ViewPagerThuVien;
import com.example.musicplayer.model.ListLibraryModel;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;

public class LibraryFragment extends Fragment implements InsertLibraryFragment.ExampleDialogListenerthuvien{
    TabLayout tabLayout;
    ViewPager viewPager;
    ImageView imgAddThuVien;
//    CircleImageView imguser;
    ProgressDialog progressDialog;
    View view;
    ListLibraryModel thuVienPlayList = null
    private String tenThuVien;
    private MainActivity hm;
    public LibraryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_listlibrary, container, false);
        AnhXa();
        init();
        imgAddThuVien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
        return  view;
    }

    private void openDialog() {
        InsertLibraryFragment exampleDialog = new InsertLibraryFragment();
        exampleDialog.show(getFragmentManager(), "InsertLibraryFragment");
        exampleDialog.setTargetFragment(LibraryFragment.this, 1);
    }

    private void init() {
        ViewPagerThuVien viewPagerThuVien = new ViewPagerThuVien(getChildFragmentManager());
        viewPagerThuVien.addFragment(new Fragment_ThuVien_Playlist(), "Playlist");
        viewPager.setAdapter(viewPagerThuVien);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void AnhXa() {
        hm = (MainActivity) getActivity();
        tabLayout = view.findViewById(R.id.tabLayouttv);
        viewPager = view.findViewById(R.id.viewPagertv);
        imgAddThuVien = view.findViewById(R.id.idaddthuvien);
//        imguser = view.findViewById(R.id.imageviewuserthuvien);
    }

    @Override
    public void apply(String tenthuvien) {
        HashMap<String, String> params = new HashMap<>();
        tenThuVien = tenthuvien;
        params.put("tenthuvien", tenThuVien);
        insertthuvien(params);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                GetData();
            }
        }, 3000);
    }

    private void GetData() {
        progressDialog.dismiss();
        Intent intent = new Intent(getActivity(), DanhsachbaihatActivity.class);
        intent.putExtra("idthuvienplaylist", thuVienPlayList.getTenThuVienPlayList());
        startActivity(intent);
    }

    private void insertthuvien(HashMap<String, String> params) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Creating...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
}
