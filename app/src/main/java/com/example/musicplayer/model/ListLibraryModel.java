package com.example.musicplayer.model;

import java.util.ArrayList;

public class ListLibraryModel {
    private int idThuVienPlayList;
    private String tenThuVienPlayList;
    private String hinhThuVienPlaylist;
    private ArrayList<SongModel> listSong;
    private String userName;
    public int getIDThuVienPlayList() {
        return idThuVienPlayList;
    }
    public void setIDThuVienPlayList(int idThuVienPlayList) {
        this.idThuVienPlayList = idThuVienPlayList;
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
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ArrayList<SongModel> getListSong() {
        return listSong;
    }

    public void setListSong(ArrayList<SongModel> listSong) {
        this.listSong = listSong;
    }
}