package com.sony.imaging.app.base.shooting;

import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.common.FocusModeDialDetector;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.ContainerState;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.util.ApoWrapper;

/* loaded from: classes.dex */
public class StateBase extends ContainerState {
    public static final String DEFAULT_LAYOUT = "DefaultLayout";
    public static final String FOCUS_LAYOUT = "FocusLayout";
    public static final String GUIDE_LAYOUT = "GuideLayout";
    public static final String PROGRESS_LAYOUT = "ProgressLayout";
    public static final String RETURN_STATE_KEY = "ReturnStateKey";
    public static final String S1OFF_LAYOUT = "S1OffLayout";
    private static StringBuilder mStringBuilder = new StringBuilder();

    protected final StringBuilder getStringBuilder() {
        return mStringBuilder;
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        openLayout(DEFAULT_LAYOUT);
        setKeyBeepPattern(getMyBeepPattern());
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        closeLayout(DEFAULT_LAYOUT);
        super.onPause();
    }

    protected int getMyBeepPattern() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        ApoWrapper.APO_TYPE type = ApoWrapper.APO_TYPE.NORMAL;
        if (DriveModeController.getInstance().isRemoteControl()) {
            ApoWrapper.APO_TYPE type2 = ApoWrapper.APO_TYPE.NONE;
            return type2;
        }
        return type;
    }

    @Override // com.sony.imaging.app.fw.State
    public Integer getCautionMode() {
        return 3;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void stopObserveEVDial() {
        if (HoldKeyServer.getObserveKeyInfo(AppRoot.USER_KEYCODE.EV_DIAL_CHANGED)) {
            ExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
        }
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.EV_DIAL_CHANGED, 5);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void startObserveEVDial() {
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.EV_DIAL_CHANGED, 4);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void stopObserveFocusModeDial() {
        if (HoldKeyServer.getObserveKeyInfo(AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED) && FocusModeDialDetector.getFocusModeDialPosition() != -1) {
            FocusModeController focusModeController = FocusModeController.getInstance();
            focusModeController.setValue(focusModeController.getFocusModeFromFocusModeDial());
        }
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED, 5);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void startObserveFocusModeDial() {
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED, 4);
    }
}
