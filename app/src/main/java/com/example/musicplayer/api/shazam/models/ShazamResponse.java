package com.example.musicplayer.api.shazam.models;

import com.google.gson.annotations.SerializedName;

public class ShazamResponse {
    @SerializedName("track")
    private final Track track;

    public ShazamResponse(Track track) {
        this.track = track;
    }

    public Track getTrack() {
        return track;
    }
}