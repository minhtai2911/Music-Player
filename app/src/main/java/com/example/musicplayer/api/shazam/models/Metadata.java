package com.example.musicplayer.api.shazam.models;

import com.google.gson.annotations.SerializedName;

public class Metadata {
    @SerializedName("text")
    private final String text;

    @SerializedName("title")
    private final String title;

    public Metadata(String text, String title) {
        this.text = text;
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }
}