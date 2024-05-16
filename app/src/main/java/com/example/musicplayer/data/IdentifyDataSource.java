package com.example.musicplayer.data;

import com.example.musicplayer.model.Music;

import java.util.concurrent.CompletableFuture;

public interface IdentifyDataSource {
    CompletableFuture<Music> identify(byte[] data, int duration);
}
