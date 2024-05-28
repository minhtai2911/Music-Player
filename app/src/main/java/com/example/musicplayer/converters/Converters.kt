package com.example.musicplayer.converters
import com.example.musicplayer.api.shazam.models.ShazamResponse
import com.example.musicplayer.model.Music

fun ByteArray.toShortArray(): ShortArray {
    val result = ShortArray(size / 2)
    for (i in 0..result.size step 2) {
        result[i / 2] = (this[i].toInt() and 0xFF or (this[i + 1].toInt() shl 8)).toShort()
    }
    return result
}

fun ShazamResponse.toMusic() = Music(
    track?.title!!,
    track?.subtitle!!,
    track?.images?.coverarthq!!,
    track?.sections
        ?.firstOrNull { section -> section.type?.uppercase() == "SONG" }
        ?.metadata
        ?.firstOrNull { metadata -> metadata.title?.uppercase() == "ALBUM" }
        ?.text,
    track?.sections
        ?.firstOrNull { section -> section.type?.uppercase() == "SONG" }
        ?.metadata
        ?.firstOrNull { metadata -> metadata.title?.uppercase() == "LABEL" }
        ?.text,
    track?.sections
        ?.firstOrNull { section -> section.type?.uppercase() == "SONG" }
        ?.metadata
        ?.firstOrNull { metadata -> metadata.title?.uppercase() == "RELEASED" }
        ?.text,
    track?.sections
        ?.firstOrNull { section -> section.type?.uppercase() == "LYRICS" }
        ?.text
        ?.joinToString("\n")
)

