package com.example.musicplayer.api.shazam.models;

import com.google.gson.annotations.SerializedName;

public class Signature {
    @SerializedName("samplems")
    private final int samplems;

    @SerializedName("timestamp")
    private final int timestamp;

    @SerializedName("uri")
    private final String uri;

    public Signature(int samplems, int timestamp, String uri) {
        this.samplems = samplems;
        this.timestamp = timestamp;
        this.uri = uri;
    }

    public int getSamplems() {
        return samplems;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public String getUri() {
        return uri;
    }
}