package com.example.musicplayer.api.shazam.models;

import com.google.gson.annotations.SerializedName;

public class Genres {
    @SerializedName("primary")
    private String primary;
    public Genres(String primary) {
        this.primary = primary;
    }
}
