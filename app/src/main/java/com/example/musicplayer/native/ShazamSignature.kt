package com.example.musicplayer.native

import android.util.Log

class ShazamSignature {
    init {
        System.loadLibrary("shazam_signature_jni")
        Log.d("ShazamSignature", "Native library loaded successfully.")
    }
    external fun create(input: ShortArray): String
}