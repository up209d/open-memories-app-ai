package com.sony.imaging.app.lightgraffiti.shooting.keyhandler;

import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.BaseKeyHandler;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.lightgraffiti.caution.LGInfo;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;

/* loaded from: classes.dex */
public class LGFakeS1OffEEStateKeyHandler extends BaseKeyHandler {
    private static final String TAG = LGFakeS1OffEEStateKeyHandler.class.getSimpleName();

    @Override // com.sony.imaging.app.fw.BaseKeyHandler
    protected String getKeyConvCategory() {
        return ICustomKey.CATEGORY_SHOOTING_S;
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        boolean handled;
        int scanCode = event.getScanCode();
        if (scanCode == 513 || scanCode == 595) {
            CameraNotificationManager.getInstance().requestNotify(LGConstants.SHOOTING_GUIDE_DETAIL_OPEN_EVENT);
            handled = true;
        } else if (scanCode == 232) {
            handled = lgPushedCenterKey();
        } else if (scanCode == 516 || scanCode == 772) {
            Log.d(TAG, "S1_ON/S1_OFF: " + scanCode);
            handled = false;
        } else if (scanCode == 518 || scanCode == 774) {
            Log.d(TAG, "S2_ON/S2_OFF: " + scanCode);
            handled = false;
        } else if (scanCode == 552 || scanCode == 553) {
            Log.d(TAG, "IR_SHUTTER/IR_2SEC: " + scanCode + " do nothing!");
            handled = false;
        } else if (scanCode == 572) {
            Log.d(TAG, "Exposure Mode Dial Changed: " + scanCode);
            handled = lgTurnedExModeDial();
        } else if (scanCode == 648 || scanCode == 649 || scanCode == 655) {
            Log.d(TAG, "Focus Ring Changed: " + scanCode);
            handled = false;
        } else if (scanCode == 515 || scanCode == 643 || scanCode == 616) {
            Log.d(TAG, "Movie Rec Key Pushed: " + scanCode);
            handled = lgPushedRecKey();
        } else if (scanCode == 530 || scanCode == 786) {
            Log.d(TAG, "LENS Attach/Detach : " + scanCode);
            handled = false;
        } else if (scanCode == 517) {
            Log.d(TAG, "!!!! IRREGULAR KEY CODE !!!! : " + scanCode);
            handled = false;
        } else if (scanCode == 654) {
            Log.d(TAG, "Lens hard key operation (State changed) : " + scanCode);
            handled = false;
        } else if (scanCode == 655) {
            Log.d(TAG, "Lens hard key operation : " + scanCode);
            handled = false;
        } else if (scanCode == 610 || scanCode == 611 || scanCode == 645) {
            Log.d(TAG, "Power zoom bar operation : " + scanCode);
            handled = false;
        } else {
            Log.d(TAG, "Invalid Key: " + scanCode);
            handled = lgPushedInvalidKey();
        }
        if (handled) {
            return 1;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int ret = isConvertedKeyPermit(event, func);
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
        if (true == CustomizableFunction.ExposureCompensation.equals(func)) {
            Log.e(TAG, AppLog.getMethodName() + " : ExposureCompensation is not avaliable");
            return 1;
        }
        if (true == CustomizableFunction.ExposureCompensationIncrement.equals(func)) {
            Log.e(TAG, AppLog.getMethodName() + " : ExposureCompensationIncrement is not avaliable");
            return 1;
        }
        if (true == CustomizableFunction.ExposureCompensationDecrement.equals(func)) {
            Log.e(TAG, AppLog.getMethodName() + " : ExposureCompensationDecrement is not avaliable");
            return 1;
        }
        Log.d(TAG, AppLog.getMethodName() + LogHelper.MSG_COLON + func + " : received the unknown logical event. KeyHandler will do nothing and through to base.");
        return 0;
    }

    public boolean lgPushedCenterKey() {
        Log.d(TAG, "pushedCenterKey()");
        CameraNotificationManager.getInstance().requestNotify(LGConstants.DISCARD_LAYOUT_OPEN_EVENT);
        return true;
    }

    protected boolean lgPushedInvalidKey() {
        CautionUtilityClass.getInstance().requestTrigger(LGInfo.CAUTION_ID_DLAPP_FUNC_LIGHTGRAFFITI_CAUTION_NOT_AVAILABLE_2ND_FUNCTIONS);
        return true;
    }

    public boolean lgTurnedExModeDial() {
        CautionUtilityClass.getInstance().requestTrigger(LGInfo.CAUTION_ID_DLAPP_FUNC_INV_APP_APO_NONE);
        return true;
    }

    public boolean lgPushedRecKey() {
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_CANNOT_REC_MOVIE);
        return true;
    }
}
