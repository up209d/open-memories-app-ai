package com.sony.imaging.app.bracketpro.caution;

import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionLayout;
import com.sony.imaging.app.bracketpro.AppLog;

/* loaded from: classes.dex */
public class BMCautionLayout extends CautionLayout {
    private static final String TAG = "BMCautionLayout";

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int ret = super.onKeyUp(keyCode, event);
        int scanCode = event.getScanCode();
        if (scanCode == 772 || scanCode == 516) {
            ret = 0;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return ret;
    }
}
