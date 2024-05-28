package com.example.musicplayer.api.shazam.models

import com.google.gson.annotations.SerializedName

data class Metadata(
    @SerializedName("text")
    val text: String?,
    @SerializedName("title")
    val title: String?
)