package com.example.musicplayer.model;

import java.io.Serializable;

public class Music implements Serializable {
    private String title;
    private String artists;
    private String cover;
    private String album;
    private String label;
    private String year;
    private String lyrics;
    public Music(String title, String artists, String cover, String album, String label, String year, String lyrics) {
        this.title = title;
        this.artists = artists;
        this.cover = cover;
        this.album = album;
        this.label = label;
        this.year = year;
        this.lyrics = lyrics;
    }
    public String getAlbum() {
        return album;
    }
}
