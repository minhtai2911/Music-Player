package com.example.musicplayer.data;

import com.example.musicplayer.model.Music;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface IdentifyDataSource {
    Music identify(Future<byte[]> data, int duration);
}
