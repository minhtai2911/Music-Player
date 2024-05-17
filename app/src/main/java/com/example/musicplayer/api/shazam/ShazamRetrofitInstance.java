package com.example.musicplayer.api.shazam;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class ShazamRetrofitInstance {
    private static final String BASE_URL = "https://amp.shazam.com/";

    private static Retrofit instance = null;

    public static Retrofit getInstance() {
        if (instance == null) {
            instance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance;
    }

    private static ShazamAPI api = null;

    public static ShazamAPI getApi() {
        if (api == null) {
            api = getInstance().create(ShazamAPI.class);
        }
        return api;
    }
}
