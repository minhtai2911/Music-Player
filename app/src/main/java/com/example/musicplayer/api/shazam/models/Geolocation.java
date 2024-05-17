package com.example.musicplayer.api.shazam.models;

import com.google.gson.annotations.SerializedName;

public class Geolocation {
    @SerializedName("altitude")
    private double altitude;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
    public Geolocation(double altitude, double latitude, double longitude) {
        this.altitude = altitude;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
