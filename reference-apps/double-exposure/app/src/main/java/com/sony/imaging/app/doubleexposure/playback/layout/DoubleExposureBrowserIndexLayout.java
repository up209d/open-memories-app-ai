package com.sony.imaging.app.doubleexposure.playback.layout;

import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.playback.layout.BrowserIndexLayout;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.fw.AppRoot;

/* loaded from: classes.dex */
public class DoubleExposureBrowserIndexLayout extends BrowserIndexLayout {
    private final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.playback.layout.BrowserIndexLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int retValue = 1;
        if (DoubleExposureUtil.getInstance().isImageSelection()) {
            transitionShooting(new EventParcel(AppRoot.USER_KEYCODE.PLAYBACK));
        } else {
            retValue = super.pushedMenuKey();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return retValue;
    }
}
