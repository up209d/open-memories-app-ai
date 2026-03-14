package com.sony.imaging.app.portraitbeauty.playback;

import com.sony.imaging.app.base.common.DisplayModeObserver;

/* loaded from: classes.dex */
public class PortraitBeautyPlayDisplayModeObserver extends DisplayModeObserver {
    private static PortraitBeautyPlayDisplayModeObserver sInstance = null;

    public static PortraitBeautyPlayDisplayModeObserver getInstance() {
        if (sInstance == null) {
            sInstance = new PortraitBeautyPlayDisplayModeObserver();
        }
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.common.DisplayModeObserver
    public void toggleDisplayMode(int appMode) {
        DisplayModeObserver ovserver = DisplayModeObserver.getInstance();
        ovserver.toggleDisplayMode(appMode);
        int dispMode = ovserver.getActiveDispMode(appMode);
        if (5 == dispMode) {
            ovserver.toggleDisplayMode(appMode);
        }
    }
}
