package com.sony.imaging.app.timelapse.shooting.state.keyhandler;

import android.view.KeyEvent;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.shooting.controller.TestShotController;

/* loaded from: classes.dex */
public class TLMfAssistKeyHandler extends MfAssistKeyHandler {
    private static final String TAG = "TLMfAssistKeyHandler";

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int retVal = 1;
        if ((event.getScanCode() == 595 || event.getScanCode() == 513) && TestShotController.TESTSHOT_ON.equalsIgnoreCase(TestShotController.getInstance().getValue(TestShotController.TESTSHOT_ASSIGN_KEY))) {
            TLCommonUtil.getInstance().setTestShot(true);
            ExecutorCreator.getInstance().getSequence().inquireKey(AppRoot.USER_KEYCODE.S2_ON);
        } else {
            retVal = super.onKeyDown(keyCode, event);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return retVal;
    }
}
