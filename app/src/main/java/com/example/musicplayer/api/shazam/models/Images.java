package com.example.musicplayer.api.shazam.models;

import com.google.gson.annotations.SerializedName;
public class Images {
    @SerializedName("background")
    private final String background;

    @SerializedName("coverart")
    private final String coverart;

    @SerializedName("coverarthq")
    private final String coverarthq;

    public Images(String background, String coverart, String coverarthq) {
        this.background = background;
        this.coverart = coverart;
        this.coverarthq = coverarthq;
    }

    public String getBackground() {
        return background;
    }

    public String getCoverart() {
        return coverart;
    }

    public String getCoverarthq() {
        return coverarthq;
    }
}
