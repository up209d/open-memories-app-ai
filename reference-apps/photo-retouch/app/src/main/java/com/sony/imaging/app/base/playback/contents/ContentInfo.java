package com.sony.imaging.app.base.playback.contents;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.util.Log;
import com.sony.imaging.app.base.playback.contents.ContentsManager;

/* loaded from: classes.dex */
public class ContentInfo {
    private static final String MSG_NULL_ID = "id is null";
    private static final String MSG_PREFIX_GET_DOUBLE = "getDouble : ";
    private static final String MSG_PREFIX_GET_INT = "getInt : ";
    private static final String MSG_PREFIX_GET_LONG = "getLong : ";
    private static final String MSG_PREFIX_GET_STRING = "getString : ";
    private static final String MSG_PREFIX_GET_VALUE = "getValue : ";
    private static final String TAG = "ContentInfo";
    protected ContentsIdentifier mId;
    protected ContentResolver mResolver;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ContentInfo(ContentResolver cr, ContentsIdentifier id) {
        this.mResolver = cr;
        this.mId = id;
        if (id == null) {
            Log.w(TAG, MSG_NULL_ID);
        }
    }

    public Object getValue(String tag) {
        return null;
    }

    public int getInt(String tag) {
        return Integer.MIN_VALUE;
    }

    public double getDouble(String tag) {
        return Double.MIN_VALUE;
    }

    public long getLong(String tag) {
        if (!tag.equals("_id") || this.mId == null) {
            return Long.MIN_VALUE;
        }
        return this.mId._id;
    }

    public String getString(String tag) {
        if (!tag.equals("_data") || this.mId == null) {
            return null;
        }
        return this.mId.data;
    }

    public boolean hasThumbnail() {
        return false;
    }

    public Bitmap getThumbnail(ContentsManager.ThumbnailOption option) {
        return null;
    }
}
