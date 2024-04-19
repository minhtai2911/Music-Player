package com.example.musicplayer.model;

public class LibraryModel{

    private int idBaiHatThuVienPlayList;
    private int idThuVienPlayList;
    private int idBaiHat;
    private String title;
    private String hinhBaiHat;
    private String artist;
    private String path;

    public LibraryModel(int idBaiHatThuVienPlayList, int idThuVienPlayList, int idBaiHat,
                                      String title, String hinhBaiHat, String artist, String path) {
        this.idBaiHatThuVienPlayList = idBaiHatThuVienPlayList;
        this.idThuVienPlayList = idThuVienPlayList;
        this.idBaiHat = idBaiHat;
        this.title = title;
        this.hinhBaiHat = hinhBaiHat;
        this.artist = artist;
        this.path = path;
    }

    public int getIdBaiHatThuVienPlayList() {
        return idBaiHatThuVienPlayList;
    }

    public void setIdBaiHatThuVienPlayList(int idBaiHatThuVienPlayList) {
        this.idBaiHatThuVienPlayList = idBaiHatThuVienPlayList;
    }

    public int getIdThuVienPlayList() {
        return idThuVienPlayList;
    }

    public void setIdThuVienPlayList(int idThuVienPlayList) {
        this.idThuVienPlayList = idThuVienPlayList;
    }

    public int getIdBaiHat() {
        return idBaiHat;
    }

    public void setIdBaiHat(int idBaiHat) {
        this.idBaiHat = idBaiHat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHinhBaiHat() {
        return hinhBaiHat;
    }

    public void setHinhBaiHat(String hinhBaiHat) {
        this.hinhBaiHat = hinhBaiHat;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

