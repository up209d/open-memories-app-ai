package com.sony.imaging.app.startrails.shooting.state;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Message;
import android.util.Pair;
import android.view.KeyEvent;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.common.FocusModeDialDetector;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.Definition;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.startrails.common.caution.STInfo;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class MFModeCheckState extends State implements NotificationListener {
    public static final String TAG = "MFModeCheckState";
    private static String[] TAGS = {CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED};
    private boolean isGPMAAttached;
    boolean isGPMALensMF = false;

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        CameraNotificationManager.getInstance().setNotificationListener(this);
        checkStatusOfLensFocusMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void checkStatusOfLensFocusMode() {
        if (!FocusModeDialDetector.hasFocusModeDial() && Environment.getVersionOfHW() != 1) {
            FocusModeController.getInstance().setValue(FocusModeController.MANUAL);
        }
        updateGPMALensInfo();
        boolean isMfMode = isMFModeSet();
        AppLog.trace(TAG, "set FocusMode is MF " + isMfMode);
        if (isMfMode || (this.isGPMAAttached && this.isGPMALensMF)) {
            transitNextState();
        } else {
            displayCaution();
        }
    }

    private boolean isMFModeSet() {
        boolean isMFModeSet;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (FocusModeController.MANUAL.equals(FocusModeController.getInstance().getValue())) {
            isMFModeSet = true;
        } else {
            isMFModeSet = false;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return isMFModeSet;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void transitNextState() {
        setNextState(STExposureModeCheckState.TAG, this.data);
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        CautionUtilityClass.getInstance().executeTerminate();
        Message msg = Message.obtain(getHandler(), Definition.STATE_TREE_CHANGED, 0, 0);
        getHandler().sendMessageAtFrontOfQueue(msg);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        CautionUtilityClass.getInstance().executeTerminate();
        Bundle b = new Bundle();
        Bundle data = new Bundle();
        data.putParcelable(EventParcel.KEY_KEYCODE, new EventParcel(AppRoot.USER_KEYCODE.PLAYBACK));
        b.putParcelable(S1OffEEState.STATE_NAME, data);
        Activity appRoot = getActivity();
        ((BaseApp) appRoot).changeApp(BaseApp.APP_PLAY, b);
        return 1;
    }

    private void displayCaution() {
        CautionUtilityClass.getInstance().setDispatchKeyEvent(STInfo.CAUTION_ID_DLAPP_MF_MODE_SUPPORTED_EXIT, new IkeyDispatchEachPF2());
        CautionUtilityClass.getInstance().requestTrigger(STInfo.CAUTION_ID_DLAPP_MF_MODE_SUPPORTED_EXIT);
    }

    private boolean updateGPMALensInfo() {
        int sensorType = FocusAreaController.getInstance().getSensorType();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = CameraSetting.getInstance().getParameters();
        String focusMode = ((Camera.Parameters) p.first).getFocusMode();
        if (sensorType == 1) {
            this.isGPMAAttached = true;
            if (!FocusModeController.MANUAL.equals(focusMode)) {
                this.isGPMALensMF = false;
            } else {
                this.isGPMALensMF = true;
            }
        } else {
            this.isGPMAAttached = false;
        }
        return this.isGPMALensMF;
    }

    /* loaded from: classes.dex */
    class IkeyDispatchEachPF1 extends IkeyDispatchEach {
        IkeyDispatchEachPF1() {
        }

        @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
        public int onKeyDown(int keyCode, KeyEvent event) {
            if (event.getKeyCode() == 23) {
                MFModeCheckState.this.transitNextState();
                return 1;
            }
            switch (event.getScanCode()) {
                case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                    MFModeCheckState.this.pushedPlayBackKey();
                    return 1;
                case AppRoot.USER_KEYCODE.SK1 /* 229 */:
                case AppRoot.USER_KEYCODE.MENU /* 514 */:
                    MFModeCheckState.this.getActivity().finish();
                    return 1;
                case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                    MFModeCheckState.this.transitNextState();
                    return 1;
                default:
                    return 1;
            }
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedFocusModeDial() {
        if (FocusModeDialDetector.getFocusModeDialPosition() != -1) {
            FocusModeController focusModeController = FocusModeController.getInstance();
            focusModeController.setValue(focusModeController.getFocusModeFromFocusModeDial());
            checkStatusOfLensFocusMode();
            return 1;
        }
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class IkeyDispatchEachPF2 extends IkeyDispatchEach {
        IkeyDispatchEachPF2() {
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPlayBackKey() {
            CautionUtilityClass.getInstance().executeTerminate();
            BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
            MFModeCheckState.this.pushedPlayBackKey();
            return 1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMovieRecKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedUmRemoteRecKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIRRecKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedShootingModeKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedFnKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMenuKey() {
            MFModeCheckState.this.getActivity().finish();
            BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
            return 1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedEVDial() {
            STUtility.getInstance().setEVDialRotated(true);
            ExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedFocusModeDial() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.IkeyDispatchEach, com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_FOCUS_SETTING;
        }
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        checkStatusOfLensFocusMode();
    }
}
