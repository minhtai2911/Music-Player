package com.example.musicplayer.model

import java.io.Serializable

data class Music(
    val title: String,
    val artists: String,
    val cover: String,
    val album: String?,
    val label: String?,
    val year: String?,
    val lyrics: String?,
) : Serializable