package com.example.musicplayer.api.shazam.models

import com.google.gson.annotations.SerializedName

data class ShazamResponse(
    @SerializedName("track")
    val track: Track?
)
