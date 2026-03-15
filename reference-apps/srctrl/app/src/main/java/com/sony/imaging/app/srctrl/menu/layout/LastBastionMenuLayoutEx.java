package com.sony.imaging.app.srctrl.menu.layout;

import android.util.Log;
import com.sony.imaging.app.base.caution.CautionProcessingFunction;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.menu.layout.LastBastionMenuLayout;
import com.sony.imaging.app.fw.Layout;

/* loaded from: classes.dex */
public class LastBastionMenuLayoutEx extends LastBastionMenuLayout {
    private static final String TAG = LastBastionMenuLayoutEx.class.getName();

    @Override // com.sony.imaging.app.base.menu.layout.LastBastionMenuLayout
    protected IkeyDispatchEach getKeyHandler() {
        InvalidExposureModeKeyHandlerEx changeModeKeyHandler = new InvalidExposureModeKeyHandlerEx();
        return changeModeKeyHandler;
    }

    /* loaded from: classes.dex */
    protected class InvalidExposureModeKeyHandlerEx extends LastBastionMenuLayout.InvalidExposureModeKeyHandler {
        public InvalidExposureModeKeyHandlerEx() {
            super();
        }

        public InvalidExposureModeKeyHandlerEx(CautionProcessingFunction p, Layout l) {
            super(p, l);
        }

        @Override // com.sony.imaging.app.base.menu.layout.LastBastionMenuLayout.InvalidExposureModeKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPlayBackKey() {
            Log.v(LastBastionMenuLayoutEx.TAG, "pushedPlayBackKey -> INVALID");
            return -1;
        }
    }
}
