package com.example.musicplayer.repository;

import com.example.musicplayer.data.IdentifyDataSource;
import com.example.musicplayer.model.Music;

import java.util.concurrent.CompletableFuture;
public class IdentifyRepository {
    private IdentifyDataSource source;

    public IdentifyRepository(IdentifyDataSource source) {
        this.source = source;
    }

    public CompletableFuture<Music> identify(byte[] data, int duration) {
        return source.identify(data, duration);
    }
}
