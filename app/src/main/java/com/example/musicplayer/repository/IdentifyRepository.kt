package com.example.musicplayer.repository

import com.example.musicplayer.data.IdentifyDataSource

class IdentifyRepository(private val source: IdentifyDataSource) {
    suspend fun identify(data: ByteArray, duration: Int) = source.identify(data, duration)
}