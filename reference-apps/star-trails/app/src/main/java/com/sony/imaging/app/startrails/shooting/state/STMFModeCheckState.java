package com.sony.imaging.app.startrails.shooting.state;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.common.FocusModeDialDetector;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.Definition;
import com.sony.imaging.app.startrails.common.caution.STInfo;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class STMFModeCheckState extends MFModeCheckState implements NotificationListener {
    public static final String TAG = "STMFModeCheckState";
    private static String[] TAGS = {CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED};

    @Override // com.sony.imaging.app.startrails.shooting.state.MFModeCheckState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        CameraNotificationManager.getInstance().setNotificationListener(this);
        checkStatusOfLensFocusMode();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void transitNextState() {
        setNextState(STExposureModeCheckState.TAG, this.data);
        Message msg = Message.obtain(getHandler(), Definition.STATE_TREE_CHANGED, 0, 0);
        getHandler().sendMessageAtFrontOfQueue(msg);
    }

    @Override // com.sony.imaging.app.startrails.shooting.state.MFModeCheckState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        CautionUtilityClass.getInstance().executeTerminate();
        Message msg = Message.obtain(getHandler(), Definition.STATE_TREE_CHANGED, 0, 0);
        getHandler().sendMessageAtFrontOfQueue(msg);
    }

    protected void displayCaution() {
        if (STUtility.getInstance().isPFverOver2() || FocusModeDialDetector.hasFocusModeDial()) {
            CautionUtilityClass.getInstance().setDispatchKeyEvent(STInfo.CAUTION_ID_DLAPP_MF_MODE_SUPPORTED_EXIT, new IkeyDispatchEachPF2());
            CautionUtilityClass.getInstance().requestTrigger(STInfo.CAUTION_ID_DLAPP_MF_MODE_SUPPORTED_EXIT);
        } else {
            CautionUtilityClass.getInstance().setDispatchKeyEvent(STInfo.CAUTION_ID_DLAPP_MF_MODE_SUPPORTED, new IkeyDispatchEachPF1());
            CautionUtilityClass.getInstance().requestTrigger(STInfo.CAUTION_ID_DLAPP_MF_MODE_SUPPORTED);
        }
    }

    /* loaded from: classes.dex */
    class IkeyDispatchEachPF1 extends IkeyDispatchEach {
        IkeyDispatchEachPF1() {
        }

        @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
        public int onKeyDown(int keyCode, KeyEvent event) {
            if (event.getKeyCode() == 23) {
                STMFModeCheckState.this.transitNextState();
                return 1;
            }
            switch (event.getScanCode()) {
                case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                    STMFModeCheckState.this.pushedPlayBackKey();
                    return 1;
                case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                    STMFModeCheckState.this.transitNextState();
                    return 1;
                default:
                    return 1;
            }
        }
    }

    /* loaded from: classes.dex */
    class IkeyDispatchEachPF2 extends IkeyDispatchEach {
        IkeyDispatchEachPF2() {
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPlayBackKey() {
            CautionUtilityClass.getInstance().executeTerminate();
            BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
            STMFModeCheckState.this.pushedPlayBackKey();
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
            STMFModeCheckState.this.getActivity().finish();
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
    }

    @Override // com.sony.imaging.app.startrails.shooting.state.MFModeCheckState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
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

    @Override // com.sony.imaging.app.startrails.shooting.state.MFModeCheckState, com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.startrails.shooting.state.MFModeCheckState, com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equals(CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED)) {
            checkStatusOfLensFocusMode();
        }
    }
}
