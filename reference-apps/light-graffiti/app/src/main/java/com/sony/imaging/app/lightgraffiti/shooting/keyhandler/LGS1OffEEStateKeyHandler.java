package com.sony.imaging.app.lightgraffiti.shooting.keyhandler;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.shooting.IUserChanging;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.lightgraffiti.caution.LGInfo;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGSelfTimerController;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.lightgraffiti.util.LGPreviewEffect;
import com.sony.imaging.app.lightgraffiti.util.LGUtility;

/* loaded from: classes.dex */
public class LGS1OffEEStateKeyHandler extends S1OffEEStateKeyHandler {
    public static final String TAG = LGS1OffEEStateKeyHandler.class.getSimpleName();

    public boolean lgPushedCenterKey() {
        Log.d(TAG, "pushedCenterKey()");
        if (!LGStateHolder.getInstance().isShootingStage3rd() || FocusModeController.isFocusShiftByControlWheel()) {
            return false;
        }
        State state = (State) this.target;
        Bundle bundle = new Bundle();
        bundle.putString(LGConstants.TRANSIT_FROM_WHERE, LGConstants.TRANSIT_FROM_3RD_BY_CENTER);
        state.setNextState("DiscardDialogue", bundle);
        return true;
    }

    public boolean lgPushedReviewKey() {
        Log.d(TAG, "pushedReviewKey()");
        State state = (State) this.target;
        state.setNextState("EasyReview", null);
        return true;
    }

    public boolean lgPushedDetailGuideKey() {
        Log.d(TAG, "pushedDetailGuideKey()");
        CameraNotificationManager.getInstance().requestNotify(LGConstants.SHOOTING_GUIDE_DETAIL_OPEN_EVENT);
        return true;
    }

    public boolean lgPushedPBKey() {
        Log.d(TAG, "pushedPBKey()");
        if (!LGStateHolder.getInstance().isShootingStage3rd()) {
            return false;
        }
        State state = (State) this.target;
        Bundle bundle = new Bundle();
        bundle.putString(LGConstants.TRANSIT_FROM_WHERE, LGConstants.TRANSIT_FROM_3RD_BY_PB);
        state.setNextState("DiscardDialogue", bundle);
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        if (lgPushedPBKey()) {
            return 1;
        }
        return super.pushedPlayBackKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        if (LGStateHolder.getInstance().isShootingStage3rd()) {
            LGPreviewEffect.getInstance().stopPreviewEffect();
        }
        if (LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_2ND)) {
            return -1;
        }
        return super.pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        LGPreviewEffect.getInstance().stopPreviewEffect();
        return super.pushedFnKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        try {
            ((IUserChanging) this.target).changeOther();
        } catch (ClassCastException e) {
            Log.w(TAG, "CHANGING NOSUPPORT");
        }
        boolean handled = false;
        int scanCode = event.getScanCode();
        if (scanCode == 232) {
            handled = lgPushedCenterKey();
        }
        if (scanCode == 513 || scanCode == 595) {
            if (LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_3RD_BEFORE_SHOOT) || LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_1ST)) {
                handled = lgPushedDetailGuideKey();
            } else if (LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_3RD_AFTER_SHOOT)) {
                handled = lgPushedReviewKey();
            }
        }
        if (handled) {
            return 1;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedShutterSpeedCustomKey() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedShutterSpeedCustomKey() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterKey() {
        Log.d(TAG, "pushedIRShutterKey");
        int state = MediaNotificationManager.getInstance().getMediaState();
        if (LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_3RD_BEFORE_SHOOT) && 2 != state) {
            CautionUtilityClass.getInstance().requestTrigger(LGInfo.CAUTION_ID_DLAPP_NO_MEMEORY_CARD_SHOOTING);
            return -1;
        }
        int cautionId = LGUtility.getInstance().getCautionId();
        if (cautionId != 0) {
            CautionUtilityClass.getInstance().requestTrigger(cautionId);
            return -1;
        }
        LGStateHolder.getInstance().setIsRemoteShutter(true);
        return super.pushedIRShutterKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIR2SecKey() {
        Log.d(TAG, "pushedIR2secKey");
        int state = MediaNotificationManager.getInstance().getMediaState();
        boolean is3rdShooting = LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_3RD_BEFORE_SHOOT) || LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_3RD_AFTER_SHOOT);
        if (is3rdShooting && 2 != state) {
            CautionUtilityClass.getInstance().requestTrigger(LGInfo.CAUTION_ID_DLAPP_NO_MEMEORY_CARD_SHOOTING);
            return -1;
        }
        int cautionId = LGUtility.getInstance().getCautionId();
        if (cautionId != 0) {
            CautionUtilityClass.getInstance().requestTrigger(cautionId);
            return -1;
        }
        try {
            if (LGSelfTimerController.getInstance().getValue("call by LGS1OffEEStateKeyHandler").equals(LGSelfTimerController.SELF_TIMER_OFF)) {
                LGSelfTimerController.getInstance().setOneTimeTimer(LGSelfTimerController.SELF_TIMER_2);
            }
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
        }
        LGStateHolder.getInstance().setIsRemoteShutter(true);
        return super.pushedIRShutterKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterNotCheckDrivemodeKey() {
        Log.d(TAG, "pushedIRShutterNotCheckDrivemodeKey");
        int state = MediaNotificationManager.getInstance().getMediaState();
        boolean is3rdShooting = LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_3RD_BEFORE_SHOOT) || LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_3RD_AFTER_SHOOT);
        if (is3rdShooting && 2 != state) {
            CautionUtilityClass.getInstance().requestTrigger(LGInfo.CAUTION_ID_DLAPP_NO_MEMEORY_CARD_SHOOTING);
            return -1;
        }
        int cautionId = LGUtility.getInstance().getCautionId();
        if (cautionId != 0) {
            CautionUtilityClass.getInstance().requestTrigger(cautionId);
            return -1;
        }
        LGStateHolder.getInstance().setIsRemoteShutter(true);
        return super.pushedIRShutterKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        if (!LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_1ST)) {
            return super.turnedEVDial();
        }
        CautionUtilityClass.getInstance().requestTrigger(LGInfo.CAUTION_ID_DLAPP_FUNC_LIGHTGRAFFITI_CAUTION_NOT_AVAILABLE_2ND);
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSettingFuncCustomKey(IKeyFunction keyFunction) {
        if (CustomizableFunction.DigitalZoom.equals(keyFunction)) {
            CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
            return -1;
        }
        if (!ModeDialDetector.hasModeDial() && true == CustomizableFunction.ExposureMode.equals(keyFunction)) {
            CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
            return -1;
        }
        return super.pushedSettingFuncCustomKey(keyFunction);
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        Log.d(TAG, "attachedLens");
        LGUtility.getInstance().isLensAttachEventReady = true;
        State state = (State) this.target;
        state.setNextState("LensProblem", null);
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        Log.d(TAG, "detachedLens");
        State state = (State) this.target;
        state.setNextState("LensProblem", null);
        return 1;
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
        boolean isStage3rd = LGStateHolder.getInstance().isShootingStage3rd();
        if (true == CustomizableFunction.ExposureCompensation.equals(func)) {
            Log.e(TAG, AppLog.getMethodName() + " : ExposureCompensation is not avaliable");
            return isStage3rd ? 0 : 1;
        }
        if (true == CustomizableFunction.ExposureCompensationIncrement.equals(func)) {
            Log.e(TAG, AppLog.getMethodName() + " : ExposureCompensationIncrement is not avaliable");
            return isStage3rd ? 0 : 1;
        }
        if (true == CustomizableFunction.ExposureCompensationDecrement.equals(func)) {
            Log.e(TAG, AppLog.getMethodName() + " : ExposureCompensationDecrement is not avaliable");
            return isStage3rd ? 0 : 1;
        }
        Log.d(TAG, AppLog.getMethodName() + LogHelper.MSG_COLON + func + " : received the unknown logical event. KeyHandler will do nothing and through to base.");
        return 0;
    }
}
