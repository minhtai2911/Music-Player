package com.example.musicplayer.api.shazam.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Section {
    @SerializedName("metadata")
    private final List<Metadata> metadata;

    @SerializedName("text")
    private final List<String> text;

    @SerializedName("type")
    private final String type;

    public Section(List<Metadata> metadata, List<String> text, String type) {
        this.metadata = metadata;
        this.text = text;
        this.type = type;
    }

    public List<Metadata> getMetadata() {
        return metadata;
    }

    public List<String> getText() {
        return text;
    }

    public String getType() {
        return type;
    }
}