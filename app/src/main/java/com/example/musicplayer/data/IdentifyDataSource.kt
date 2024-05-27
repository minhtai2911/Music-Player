package com.example.musicplayer.data

import com.example.musicplayer.model.Music

interface IdentifyDataSource {
    suspend fun identify(data: ByteArray, duration: Int): Music?
}