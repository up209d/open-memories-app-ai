package com.sony.imaging.app.soundphoto.database;

import java.util.EventListener;

/* loaded from: classes.dex */
public interface SoundPhotoDataBaseUtilListener extends EventListener {
    void finishedUpdateStatus();

    void progress(int i, int i2);
}
