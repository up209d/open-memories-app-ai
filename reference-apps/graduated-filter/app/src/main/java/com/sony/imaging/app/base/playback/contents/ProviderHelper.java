package com.sony.imaging.app.base.playback.contents;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.util.Log;
import com.sony.imaging.app.base.common.AudioVolumeController;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.scalar.graphics.OptimizedImage;

/* loaded from: classes.dex */
public abstract class ProviderHelper {
    public static final int CACHE_KIND_ALL = 7;
    public static final int CACHE_KIND_CONTENT = 1;
    public static final int CACHE_KIND_IMAGE = 2;
    public static final int CACHE_KIND_THUMBNAIL = 4;
    private static final String MSG_CREATED = "Created";
    static String TAG;
    protected ContentResolver mResolver = null;

    public abstract boolean delete(ContentsIdentifier contentsIdentifier);

    public abstract ContentInfo getContentInfo(ContentsIdentifier contentsIdentifier);

    @Deprecated
    public abstract Bitmap getJpeg(ContentsIdentifier contentsIdentifier);

    public abstract void invalidateCachedInfo();

    public abstract void invalidateCachedInfo(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public ProviderHelper() {
        TAG = getClass().getSimpleName();
        Log.i(TAG, MSG_CREATED);
    }

    public void initialize(ContentResolver resolver) {
        this.mResolver = resolver;
    }

    public boolean isInitialized() {
        return this.mResolver != null;
    }

    public void terminate() {
        invalidateCachedInfo();
        this.mResolver = null;
    }

    public ContentResolver getResolver() {
        return this.mResolver;
    }

    public int getInt(Cursor c, String tag) {
        int index = c.getColumnIndex(tag);
        if (-1 == index) {
            return AudioVolumeController.INVALID_VALUE;
        }
        try {
            return c.getInt(index);
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
            return AudioVolumeController.INVALID_VALUE;
        }
    }

    public long getLong(Cursor c, String tag) {
        int index = c.getColumnIndex(tag);
        if (-1 == index) {
            return Long.MIN_VALUE;
        }
        try {
            return c.getLong(index);
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
            return Long.MIN_VALUE;
        }
    }

    public String getString(Cursor c, String tag) {
        int index = c.getColumnIndex(tag);
        if (-1 == index) {
            return null;
        }
        try {
            return c.getString(index);
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean hasThumbnail(ContentsIdentifier id) {
        ContentInfo info = getContentInfo(id);
        if (info != null) {
            return info.hasThumbnail();
        }
        return false;
    }

    public Bitmap getThumbnail(ContentsIdentifier id, ContentsManager.ThumbnailOption option) {
        ContentInfo info = getContentInfo(id);
        if (info != null) {
            return info.getThumbnail(option);
        }
        return null;
    }

    public OptimizedImage getOptimizedImage(ContentsIdentifier id, int imageType, ContentInfo[] info) {
        return null;
    }

    public OptimizedImage getOptimizedImageWithoutCache(ContentsIdentifier id, int imageType, ContentInfo[] info) {
        return null;
    }

    public ContentInfo getContentInfoFromCache(ContentsIdentifier id) {
        return null;
    }
}
