package com.sony.imaging.app.base.playback.contents;

import com.sony.imaging.app.util.NotificationManager;

/* loaded from: classes.dex */
public class ContentsObserver extends NotificationManager {
    private static final int DEFAULT_CAPACITY = 64;
    private ContentInfo mContentInfo;

    public ContentsObserver(boolean b) {
        super(b, true);
        this.mContentInfo = new ContentInfo(null, new ContentsIdentifier(-1L, null, null));
    }

    @Override // com.sony.imaging.app.util.NotificationManager
    protected int getDefaultListenerCapacity() {
        return DEFAULT_CAPACITY;
    }

    @Override // com.sony.imaging.app.util.NotificationManager
    public Object getValue(String tag) {
        return this.mContentInfo;
    }

    public void setData(ContentInfo info) {
        this.mContentInfo = info;
        notify(ContentsManager.NOTIFICATION_TAG_CURRENT_FILE);
    }

    public void requestNotify(String tag) {
        notify(tag);
    }
}
