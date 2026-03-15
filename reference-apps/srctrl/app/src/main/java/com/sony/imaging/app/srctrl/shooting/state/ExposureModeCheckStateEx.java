package com.sony.imaging.app.srctrl.shooting.state;

import android.util.Log;
import com.sony.imaging.app.base.caution.CautionProcessingFunction;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.shooting.ExposureModeCheckState;
import com.sony.imaging.app.fw.Layout;

/* loaded from: classes.dex */
public class ExposureModeCheckStateEx extends ExposureModeCheckState {
    private static final String TAG = ExposureModeCheckStateEx.class.getName();

    @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        Log.i(TAG, "pushedPlayBackKey -> INVALID");
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayIndexKey() {
        Log.i(TAG, "pushedPlayIndexKey -> INVALID");
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState
    protected IkeyDispatchEach getKeyHandler() {
        InvalidExposureModeKeyHandlerEx ret = new InvalidExposureModeKeyHandlerEx();
        return ret;
    }

    /* loaded from: classes.dex */
    protected class InvalidExposureModeKeyHandlerEx extends ExposureModeCheckState.InvalidExposureModeKeyHandler {
        public InvalidExposureModeKeyHandlerEx() {
            super();
        }

        public InvalidExposureModeKeyHandlerEx(CautionProcessingFunction p, Layout l) {
            super(p, l);
        }

        @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState.InvalidExposureModeKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPlayBackKey() {
            Log.v(ExposureModeCheckStateEx.TAG, "PlayBackKey -> INVALID");
            return -1;
        }
    }
}
