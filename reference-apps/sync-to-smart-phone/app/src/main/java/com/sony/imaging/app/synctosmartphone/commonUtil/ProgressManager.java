package com.sony.imaging.app.synctosmartphone.commonUtil;

import com.sony.imaging.app.util.NotificationManager;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes.dex */
public class ProgressManager extends NotificationManager {
    private static ProgressManager instance;
    public static HashSet<String> mThinnedOut = new HashSet<>();
    private Map<String, Object> mValues;

    static {
        mThinnedOut.clear();
        instance = new ProgressManager(false);
    }

    public void clear() {
        this.mValues.clear();
    }

    private ProgressManager(boolean b) {
        super(b, mThinnedOut);
        this.mValues = new ConcurrentHashMap();
    }

    public static ProgressManager getInstance() {
        return instance;
    }

    @Override // com.sony.imaging.app.util.NotificationManager
    public Object getValue(String tag) {
        return this.mValues.get(tag);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.util.NotificationManager
    public void onFirstListenerSet(String tag) {
        super.onFirstListenerSet(tag);
    }

    public void requestNotify(String tag) {
        notify(tag);
    }
}
