package com.sony.imaging.app.soundphoto.shooting.state.keyHandler;

import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPUtil;

/* loaded from: classes.dex */
public class SPS1OffEEStateKeyHandler extends S1OffEEStateKeyHandler {
    private static final String TAG = "SPS1OffEEStateKeyHandler";

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        AppLog.enter(TAG, "onKeyDown");
        int returnResult = super.onKeyDown(keyCode, event);
        AppLog.exit(TAG, "onKeyDown");
        return returnResult;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterNotCheckDrivemodeKey() {
        int cautionId = SPUtil.getInstance().getCautionId();
        if (cautionId == 0) {
            return super.pushedIRShutterNotCheckDrivemodeKey();
        }
        CautionUtilityClass.getInstance().requestTrigger(cautionId);
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIR2SecKey() {
        int cautionId = SPUtil.getInstance().getCautionId();
        if (cautionId == 0) {
            return super.pushedIR2SecKey();
        }
        CautionUtilityClass.getInstance().requestTrigger(cautionId);
        return 1;
    }
}
