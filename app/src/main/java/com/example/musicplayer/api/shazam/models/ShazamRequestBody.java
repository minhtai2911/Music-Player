package com.example.musicplayer.api.shazam.models;

import com.google.gson.annotations.SerializedName;

public class ShazamRequestBody {
    @SerializedName("geolocation")
    private final Geolocation geolocation;

    @SerializedName("signature")
    private final Signature signature;

    @SerializedName("timestamp")
    private final int timestamp;

    @SerializedName("timezone")
    private final String timezone;

    public ShazamRequestBody(Geolocation geolocation, Signature signature, int timestamp, String timezone) {
        this.geolocation = geolocation;
        this.signature = signature;
        this.timestamp = timestamp;
        this.timezone = timezone;
    }

    public Geolocation getGeolocation() {
        return geolocation;
    }

    public Signature getSignature() {
        return signature;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public String getTimezone() {
        return timezone;
    }
}