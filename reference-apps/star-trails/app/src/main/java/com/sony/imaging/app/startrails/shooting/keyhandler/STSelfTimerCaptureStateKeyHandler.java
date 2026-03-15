package com.sony.imaging.app.startrails.shooting.keyhandler;

import com.sony.imaging.app.base.shooting.trigger.SelfTimerCaptureStateKeyHandler;
import com.sony.imaging.app.startrails.common.STCaptureDisplayModeObserver;
import com.sony.imaging.app.startrails.util.AppLog;

/* loaded from: classes.dex */
public class STSelfTimerCaptureStateKeyHandler extends SelfTimerCaptureStateKeyHandler {
    private static final String TAG = "STSelfTimerCaptureStateKeyHandler";

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDispFuncKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        STCaptureDisplayModeObserver.getInstance().toggleDisplayMode(0);
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }
}
