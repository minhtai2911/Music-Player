package com.example.musicplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class
 SongModel implements Parcelable {
    private String path;
    private String title;
    private String artist;
    private String duration;

//    private String album;

    public SongModel(String path, String title, String artist, String duration) {
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
//        this.album=null;
    }

//    public SongModel(String path, String title, String artist, String duration, String album) {
//        this.path = path;
//        this.title = title;
//        this.artist = artist;
//        this.duration = duration;
//        this.album = album;
//    }

    protected SongModel(Parcel in) {
        path = in.readString();
        title = in.readString();
        artist = in.readString();
        duration = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(duration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SongModel> CREATOR = new Creator<SongModel>() {
        @Override
        public SongModel createFromParcel(Parcel in) {
            return new SongModel(in);
        }

        @Override
        public SongModel[] newArray(int size) {
            return new SongModel[size];
        }
    };

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getDuration() { return duration; }
    
    public void setPath(String path) {
        this.path = path;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

//    public String getAlbum(){
//        return album;
//    }
}

