package com.example.musicplayer.tool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.palette.graphics.Palette;

public class GetDominantColor {

    public static int getDominantColor(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) {
            return 0x16777215;
        }


        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);


        Palette palette = Palette.from(bitmap).generate();


        Palette.Swatch dominantSwatch = palette.getDominantSwatch();

        if (dominantSwatch != null) {
            return dominantSwatch.getRgb();
        } else {
            return 0x16777215;
        }
    }
}

