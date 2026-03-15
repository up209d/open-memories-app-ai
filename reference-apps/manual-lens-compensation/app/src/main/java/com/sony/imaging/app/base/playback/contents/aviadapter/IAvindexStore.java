package com.sony.imaging.app.base.playback.contents.aviadapter;

import android.content.ContentResolver;

/* loaded from: classes.dex */
public interface IAvindexStore {
    boolean cancelWaitAndUpdateDatabase(ContentResolver contentResolver, String str);

    boolean cancelWaitLoadMediaComplete(String str);

    boolean loadMedia(String str, int i);

    boolean loadMedia(String str, int[] iArr);

    boolean waitAndUpdateDatabase(ContentResolver contentResolver, String str);

    boolean waitLoadMediaComplete(String str);

    boolean waitLoadMediaComplete(String str, int[] iArr);
}
