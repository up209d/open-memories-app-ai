package com.sony.imaging.app.base.common;

import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class OnLayoutModeChangeListener implements NotificationListener {
    private static String[] TAGS = {DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_DISPMODE_CHANGE};
    private int mAppMode;
    private DisplayModeObserver mDisplayModeObserver = DisplayModeObserver.getInstance();
    private IModableLayout mLayout;

    public OnLayoutModeChangeListener(IModableLayout layout, int appMode) {
        this.mLayout = layout;
        this.mAppMode = appMode;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        int device = this.mDisplayModeObserver.getActiveDevice();
        int display = this.mDisplayModeObserver.getActiveDispMode(this.mAppMode);
        this.mLayout.onLayoutModeChanged(device, display);
    }
}
