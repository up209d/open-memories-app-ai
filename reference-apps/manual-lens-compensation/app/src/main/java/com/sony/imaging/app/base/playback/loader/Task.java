package com.sony.imaging.app.base.playback.loader;

/* loaded from: classes.dex */
public abstract class Task {
    static final String MSG_PREFIX_GET_DATA = "getData : ";
    private int mPos;

    public abstract Object getData();

    public Task(int position) {
        this.mPos = position;
    }

    public int getPos() {
        return this.mPos;
    }
}
