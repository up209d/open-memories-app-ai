package com.github.up209d.openmemories.easylut.filter;

import android.graphics.Bitmap;

import com.github.up209d.openmemories.easylut.lutimage.LUTImage;

public interface BitmapStrategy {

    String TAG = BitmapStrategy.class.getSimpleName();

    Bitmap applyLut(Bitmap src, LUTImage lutImage);

    enum Type{
        APPLY_ON_ORIGINAL_BITMAP, CREATING_NEW_BITMAP
    }
}
