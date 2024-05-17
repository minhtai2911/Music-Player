package com.example.musicplayer.repository;

import com.example.musicplayer.data.IdentifyDataSource;
import com.example.musicplayer.model.Music;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class IdentifyRepository {
    private IdentifyDataSource source;

    public IdentifyRepository(IdentifyDataSource source) {
        this.source = source;
    }

    public Music identify(Future<byte[]> data, int duration) {
        return source.identify(data, duration);
    }
}
