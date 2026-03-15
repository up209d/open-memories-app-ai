package com.sony.imaging.app.startrails.shooting.state;

import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.common.FocusModeDialDetector;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.shooting.ExposureModeCheckState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.Definition;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.startrails.base.menu.controller.STExposureModeController;
import com.sony.imaging.app.startrails.common.caution.STInfo;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.startrails.util.ThemeParameterSettingUtility;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class STExposureModeCheckState extends ExposureModeCheckState implements NotificationListener {
    public static final String TAG = "STExposureModeCheckState";
    private static String[] TAGS = {CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED};
    private boolean isFromForceSetting = false;

    @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        if (this.data != null) {
            this.isFromForceSetting = this.data.getBoolean(TAG);
            this.data.putBoolean(TAG, false);
        }
        CameraNotificationManager.getInstance().setNotificationListener(this);
        STExposureModeController emc = STExposureModeController.getInstance();
        String expMode = emc.getValue(null);
        AppLog.trace(TAG, "Exposure mode = " + expMode);
        if (!STUtility.getInstance().isAppTopBooted() && ModeDialDetector.hasModeDial()) {
            if (STUtility.getInstance().getCurrentTrail() == 2 && !STExposureModeController.getInstance().isValidDialPosition(ModeDialDetector.getModeDialPosition())) {
                if (emc.getCautionId() != 65535) {
                    this.isFromForceSetting = true;
                    ExecutorCreator.getInstance().stableSequence((BaseShootingExecutor.ReleasedListener) null);
                    displayCaution();
                    return;
                }
                return;
            }
            transitNextState();
            return;
        }
        if (!emc.isValidExpoMode(expMode)) {
            STExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, ExposureModeController.PROGRAM_AUTO_MODE);
        }
        transitNextState();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedFocusModeDial() {
        if (FocusModeDialDetector.getFocusModeDialPosition() != -1) {
            FocusModeController focusModeController = FocusModeController.getInstance();
            focusModeController.setValue(focusModeController.getFocusModeFromFocusModeDial());
            setNextState(MFModeCheckState.TAG, null);
            return 1;
        }
        return 1;
    }

    private void displayCaution() {
        AppLog.enter(TAG, AppLog.getMethodName());
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 3);
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.startrails.shooting.state.STExposureModeCheckState.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 0;
                    case AppRoot.USER_KEYCODE.SK1 /* 229 */:
                    case AppRoot.USER_KEYCODE.MENU /* 514 */:
                        STExposureModeCheckState.this.getActivity().finish();
                        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
                        return 1;
                    case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                    case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED /* 597 */:
                    case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
                        return 0;
                    case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                        if (ModeDialDetector.getModeDialPosition() != -1) {
                            CautionUtilityClass.getInstance().executeTerminate();
                            if (STExposureModeCheckState.this.data == null) {
                                STExposureModeCheckState.this.data = new Bundle();
                            }
                            STExposureModeCheckState.this.data.putString(MenuState.ITEM_ID, ExposureModeController.EXPOSURE_MODE);
                            STExposureModeCheckState.this.transitNextState();
                        }
                        return 1;
                    default:
                        return 1;
                }
            }
        };
        CautionUtilityClass.getInstance().setDispatchKeyEvent(STInfo.CAUTION_ID_DLAPP_CHANGE_DIAL_TO_PASM, mKey);
        CautionUtilityClass.getInstance().requestTrigger(STInfo.CAUTION_ID_DLAPP_CHANGE_DIAL_TO_PASM);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void transitNextState() {
        setNextState(getNextState(), this.data);
        STUtility.getInstance().updateExposureMode();
        if (this.isFromForceSetting && !STUtility.getInstance().isAppTopBooted()) {
            this.isFromForceSetting = false;
            ThemeParameterSettingUtility.getInstance().initializeThemeParameters();
            ExecutorCreator.getInstance().updateSequence();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        ExposureModeController emc = STExposureModeController.getInstance();
        CautionUtilityClass.getInstance().executeTerminate();
        CautionUtilityClass.getInstance().setDispatchKeyEvent(emc.getCautionId(), null);
        Message msg = Message.obtain(getHandler(), Definition.STATE_TREE_CHANGED, 0, 0);
        getHandler().sendMessageAtFrontOfQueue(msg);
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equals(CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED)) {
            setNextState(MFModeCheckState.TAG, null);
        }
    }
}
