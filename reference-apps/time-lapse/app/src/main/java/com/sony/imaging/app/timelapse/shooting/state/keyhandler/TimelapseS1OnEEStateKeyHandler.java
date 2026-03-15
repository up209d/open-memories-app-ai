package com.sony.imaging.app.timelapse.shooting.state.keyhandler;

import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.IUserChanging;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.S1OnEEStateKeyHandler;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.shooting.controller.TLExposureCompensasionController;
import com.sony.imaging.app.timelapse.shooting.controller.TestShotController;

/* loaded from: classes.dex */
public class TimelapseS1OnEEStateKeyHandler extends S1OnEEStateKeyHandler {
    private static final String RETURN_STATE = "S1OffEE";
    private static final String TAG = "TimelapseS1OnEEStateKeyHandler";

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        int eventState = 1;
        Log.i(TAG, "TimelapseS1OnEEStateKeyHandlerpushedS2Key()");
        if (TLCommonUtil.getInstance().checkExposureMode()) {
            StateBase state = (StateBase) this.target;
            state.setNextState("S1OffEE", null);
            Log.i(TAG, "TimelapseS1OnEEStateKeyHandlerpushedS2Key()1");
        } else if (MediaNotificationManager.getInstance().isError()) {
            eventState = 1;
        } else {
            if (TLCommonUtil.getInstance().isS2_ONFromPlayBack()) {
                return 1;
            }
            eventState = super.pushedS2Key();
        }
        return eventState;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OnEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        boolean ret = TLExposureCompensasionController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
        if (ret) {
            ((IUserChanging) this.target).changeExposure();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OnEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELHoldCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OnEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELToggleCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int retVal = 1;
        TLCommonUtil.getInstance().setTestShot(false);
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
