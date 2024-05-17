package com.example.musicplayer.api.shazam.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Track {
    @SerializedName("genres")
    private final Genres genres;

    @SerializedName("images")
    private final Images images;

    @SerializedName("sections")
    private final List<Section> sections;

    @SerializedName("subtitle")
    private final String subtitle;

    @SerializedName("title")
    private final String title;

    public Track(Genres genres, Images images, List<Section> sections, String subtitle, String title) {
        this.genres = genres;
        this.images = images;
        this.sections = sections;
        this.subtitle = subtitle;
        this.title = title;
    }

    public List<Section> getSections() {
        return sections;
    }

    public Genres getGenres() {
        return genres;
    }

    public Images getImages() {
        return images;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getTitle() {
        return title;
    }
}
