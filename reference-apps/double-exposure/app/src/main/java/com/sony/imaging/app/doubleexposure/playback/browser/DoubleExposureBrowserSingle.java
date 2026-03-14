package com.sony.imaging.app.doubleexposure.playback.browser;

import com.sony.imaging.app.base.playback.browser.BrowserSingle;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.playback.DoubleExposurePlayRootContainer;
import com.sony.imaging.app.doubleexposure.playback.layout.IDoubleExposurePlaybackTriggerFunction;

/* loaded from: classes.dex */
public class DoubleExposureBrowserSingle extends BrowserSingle implements IDoubleExposurePlaybackTriggerFunction {
    public static final int TRANSIT_TO_PREPAIRINGPLAYSTATE = 100;
    private final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.doubleexposure.playback.layout.IDoubleExposurePlaybackTriggerFunction
    public boolean transitionDeleteMultiple() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        getRootContainer().changeApp(DoubleExposurePlayRootContainer.ID_DELETE_MULTIPLE);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return true;
    }
}
