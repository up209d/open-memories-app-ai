package com.sony.imaging.app.lightgraffiti.shooting.keyhandler;

import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGSelfTimerController;
import com.sony.imaging.app.lightgraffiti.shooting.state.LGNormalCaptureState;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.lightgraffiti.util.LGUtility;

/* loaded from: classes.dex */
public class LGNormalCaptureStateKeyHandler extends CaptureStateKeyHandler {
    private static final String TAG = LGNormalCaptureStateKeyHandler.class.getSimpleName();

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        Log.d(TAG, "Pushed S2 Key in second shooting.");
        int cautionId = LGUtility.getInstance().getCautionId();
        if (cautionId != 0) {
            CautionUtilityClass.getInstance().requestTrigger(cautionId);
            return -1;
        }
        CameraNotificationManager.getInstance().requestNotify(LGConstants.IN_CAPTURE_S2_PUSHED);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterKey() {
        int cautionId = LGUtility.getInstance().getCautionId();
        if (cautionId != 0) {
            CautionUtilityClass.getInstance().requestTrigger(cautionId);
            return -1;
        }
        if (DriveModeController.getInstance().isRemoteControl()) {
            CameraNotificationManager.getInstance().requestNotify(LGConstants.IN_CAPTURE_S2_PUSHED);
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIR2SecKey() {
        int cautionId = LGUtility.getInstance().getCautionId();
        if (cautionId != 0) {
            CautionUtilityClass.getInstance().requestTrigger(cautionId);
            return -1;
        }
        if (DriveModeController.getInstance().isRemoteControl()) {
            try {
                if (LGSelfTimerController.getInstance().getValue("call by LGS1OffEEStateKeyHandler").equals(LGSelfTimerController.SELF_TIMER_OFF)) {
                    LGSelfTimerController.getInstance().setOneTimeTimer(LGSelfTimerController.SELF_TIMER_2);
                }
            } catch (IController.NotSupportedException e) {
                e.printStackTrace();
            }
            CameraNotificationManager.getInstance().requestNotify(LGConstants.IN_CAPTURE_S2_PUSHED);
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterNotCheckDrivemodeKey() {
        int cautionId = LGUtility.getInstance().getCautionId();
        if (cautionId != 0) {
            CautionUtilityClass.getInstance().requestTrigger(cautionId);
            return -1;
        }
        if (DriveModeController.getInstance().isRemoteControl()) {
            CameraNotificationManager.getInstance().requestNotify(LGConstants.IN_CAPTURE_S2_PUSHED);
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        Log.d(TAG, "Pushed S1 Key in second shooting.");
        CameraNotificationManager.getInstance().requestNotify(LGConstants.IN_CAPTURE_S1_PUSHED);
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler, com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS1Key() {
        Log.d(TAG, "Released S1 Key in second shooting.");
        CameraNotificationManager.getInstance().requestNotify(LGConstants.IN_CAPTURE_S1_RELEASED);
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        Log.d(TAG, "attachedLens");
        LGNormalCaptureState state = (LGNormalCaptureState) this.target;
        state.onLensProblemDetected();
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        Log.d(TAG, "detachedLens");
        LGNormalCaptureState state = (LGNormalCaptureState) this.target;
        state.onLensProblemDetected();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int ret = isConvertedKeyPermit(event, func);
        Log.e(TAG, AppLog.getMethodName() + " func: " + func + " return :" + ret);
        switch (ret) {
            case 0:
                return super.onConvertedKeyDown(event, func);
            default:
                return ret;
        }
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
        int ret = isConvertedKeyPermit(event, func);
        Log.e(TAG, AppLog.getMethodName() + " func: " + func + " return :" + ret);
        switch (ret) {
            case 0:
                return super.onConvertedKeyUp(event, func);
            default:
                return ret;
        }
    }

    private int isConvertedKeyPermit(KeyEvent event, IKeyFunction func) {
        if (true == CustomizableFunction.AelHold.equals(func)) {
            Log.e(TAG, AppLog.getMethodName() + " : AelHold is not avaliable");
            return 1;
        }
        if (true == CustomizableFunction.AelToggle.equals(func)) {
            Log.e(TAG, AppLog.getMethodName() + " : AelToggle is not avaliable");
            return 1;
        }
        if (true == CustomizableFunction.AfMfHold.equals(func)) {
            Log.e(TAG, AppLog.getMethodName() + " : AfMfHold is not avaliable");
            return 1;
        }
        if (true == CustomizableFunction.AfMfToggle.equals(func)) {
            Log.e(TAG, AppLog.getMethodName() + " : AfMfToggle is not avaliable");
            return 1;
        }
        if (true == CustomizableFunction.AfLock.equals(func)) {
            Log.e(TAG, AppLog.getMethodName() + " : AfLock is not avaliable");
            return 1;
        }
        if (true == CustomizableFunction.AutoFocusMode.equals(func)) {
            Log.e(TAG, AppLog.getMethodName() + " : AfLock is not avaliable");
            return 1;
        }
        Log.d(TAG, AppLog.getMethodName() + LogHelper.MSG_COLON + func + " : received the unknown logical event. KeyHandler will do nothing and through to base.");
        return 0;
    }
}
