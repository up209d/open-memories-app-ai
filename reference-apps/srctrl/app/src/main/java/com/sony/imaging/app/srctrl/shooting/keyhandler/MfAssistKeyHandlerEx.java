package com.sony.imaging.app.srctrl.shooting.keyhandler;

import android.util.Log;
import com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler;

/* loaded from: classes.dex */
public class MfAssistKeyHandlerEx extends MfAssistKeyHandler {
    private static final String TAG = MfAssistKeyHandlerEx.class.getName();

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        Log.i(TAG, "pushedPlayBackKey -> INVALID");
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayIndexKey() {
        Log.i(TAG, "pushedPlayIndexKey -> INVALID");
        return -1;
    }
}
