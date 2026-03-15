package com.sony.imaging.app.snsdirect.util;

import android.content.Intent;

/* loaded from: classes.dex */
public class MovieInfo {
    private int fps;
    private int height;
    private int length;
    private int width;

    public MovieInfo(Intent intent) {
        this.width = intent.getIntExtra(IntentKeyDefs.WIDTH_PIX, 0);
        this.height = intent.getIntExtra(IntentKeyDefs.HEIGHT_PIX, 0);
        this.length = intent.getIntExtra(IntentKeyDefs.MOVIE_LENGTH_SEC, 0);
        this.fps = intent.getIntExtra(IntentKeyDefs.FRAME_PER_SEC, 0);
    }

    public MovieInfo(int widthPx, int heightPx, int lengthSec, int fps) {
        if (widthPx < 0 || heightPx < 0 || lengthSec < 0 || fps < 0.0f) {
            throw new IllegalArgumentException();
        }
        this.width = widthPx;
        this.height = heightPx;
        this.length = lengthSec;
        this.fps = fps;
    }

    public int getWidthPx() {
        return this.width;
    }

    public int getHeightPx() {
        return this.height;
    }

    public int getLengthSec() {
        return this.length;
    }

    public int getFps() {
        return this.fps;
    }

    public void setWidthPx(int width) {
        if (width < 0) {
            throw new IllegalArgumentException();
        }
        this.width = width;
    }

    public void setHeightPx(int height) {
        if (height < 0) {
            throw new IllegalArgumentException();
        }
        this.height = height;
    }

    public void setLengthSec(int length) {
        if (length < 0) {
            throw new IllegalArgumentException();
        }
        this.length = length;
    }

    public void setFps(int fps) {
        if (fps < 0) {
            throw new IllegalArgumentException();
        }
        this.fps = fps;
    }

    public static Intent setMovieInfoExtrasAsPrimitive(Intent intent, MovieInfo mInfo) {
        intent.putExtra(IntentKeyDefs.WIDTH_PIX, mInfo.getWidthPx());
        intent.putExtra(IntentKeyDefs.HEIGHT_PIX, mInfo.getHeightPx());
        intent.putExtra(IntentKeyDefs.MOVIE_LENGTH_SEC, mInfo.getLengthSec());
        intent.putExtra(IntentKeyDefs.FRAME_PER_SEC, mInfo.getFps());
        return intent;
    }
}
