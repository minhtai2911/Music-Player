package com.example.musicplayer.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ListLibraryModel implements Serializable {
    private String tenThuVienPlayList;
    private String hinhThuVienPlaylist;
    private ArrayList<SongModel> listSong;
    public ListLibraryModel() {
        this.listSong = new ArrayList<>();
    }
    public String getTenThuVienPlayList() {
        return tenThuVienPlayList;
    }
    public void setTenThuVienPlayList(String tenThuVienPlayList) {
        this.tenThuVienPlayList = tenThuVienPlayList;
    }
    public String getHinhThuVienPlaylist() {
        return hinhThuVienPlaylist;
    }
    public void setHinhThuVienPlaylist(String hinhThuVienPlaylist) {
        this.hinhThuVienPlaylist = hinhThuVienPlaylist;
    }
    public ArrayList<SongModel> getListSong() {
        return listSong;
    }
    public void setListSong(SongModel thisSong) {
        listSong.add(thisSong);
    }
}
