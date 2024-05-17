package com.example.musicplayer.nativeJNI;

public class ShazamSignature {
    static{
        System.loadLibrary("shazam_signature_jni");
    }

    public native String create(short[] input);
}
