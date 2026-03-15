package com.sony.imaging.app.base.shooting;

import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.FocusMagnificationController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.layout.IStableLayout;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import java.util.List;

/* loaded from: classes.dex */
public class S1OnEEState extends StateBase implements IUserChanging {
    public static final String MF_ASSIST_STATE = "MfAssist";
    private static final String RETURN_STATE = "S1OffEE";
    protected NotificationListener mFocusInfoListener;
    private IStableLayout mLayout = null;
    protected NotificationListener mListener;
    private static final String[] TAGS = {CameraNotificationManager.FOCUS_MAGNIFICATION_CHANGED, CameraNotificationManager.FOCUS_RING_ROTATION_STARTED};
    private static final String[] FOCUS_TAGS = {CameraNotificationManager.DONE_AUTO_FOCUS};

    protected String getMfAssistStateName() {
        return "MfAssist";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class MfAssistListener implements NotificationListener {
        protected MfAssistListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return S1OnEEState.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            FocusMagnificationController controller;
            List<Integer> supported;
            if (CameraNotificationManager.FOCUS_RING_ROTATION_STARTED.equals(tag)) {
                if (FocusAreaController.getInstance().getSensorType() == 2) {
                    String focusMode = FocusModeController.getInstance().getValue();
                    if ((FocusModeController.MANUAL.equals(focusMode) || FocusModeController.DMF.equals(focusMode)) && (supported = (controller = FocusMagnificationController.getInstance()).getSupportedValueInt(FocusMagnificationController.TAG_MAGNIFICATION_RATIO)) != null && supported.size() > 0 && !"off".equals(controller.getValue(FocusMagnificationController.TAG_IS_MFASSIST_AVAILABLE_IN_SETTINGS)) && controller.isAvailable(null) && controller.isAvailableInDigitalZoom()) {
                        controller.startMfAssist();
                        return;
                    }
                    return;
                }
                return;
            }
            if (CameraNotificationManager.FOCUS_MAGNIFICATION_CHANGED.equals(tag) && FocusMagnificationController.getInstance().isMagnifying()) {
                S1OnEEState.this.setNextState(S1OnEEState.this.getMfAssistStateName(), null);
            }
        }
    }

    protected NotificationListener getListener() {
        if (FocusMagnificationController.isSupportedByPf()) {
            return new MfAssistListener();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class FocusInfoListener implements NotificationListener {
        protected FocusInfoListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return S1OnEEState.FOCUS_TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            S1OnEEState.this.refresh((CameraNotificationManager.OnFocusInfo) CameraNotificationManager.getInstance().getValue(tag));
        }
    }

    protected NotificationListener getFocusInfoListener() {
        if (CameraSetting.getInstance().isFocusHoldSupported()) {
            return new FocusInfoListener();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refresh(CameraNotificationManager.OnFocusInfo info) {
        if (info.status == 0) {
            KeyStatus key = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S1_ON);
            if (key.valid == 1 && key.status == 0) {
                setNextState("S1OffEE", null);
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 2);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AF_MF, 1);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED, 1);
        openLayout(StateBase.FOCUS_LAYOUT);
        this.mLayout = (IStableLayout) getLayout(StateBase.DEFAULT_LAYOUT);
        AELController.getInstance().setS1AEL(true);
        this.mListener = getListener();
        if (this.mListener != null) {
            CameraNotificationManager.getInstance().setNotificationListener(this.mListener, true);
        }
        this.mFocusInfoListener = getFocusInfoListener();
        if (this.mFocusInfoListener != null) {
            CameraNotificationManager.getInstance().setNotificationListener(this.mFocusInfoListener, true);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mListener != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mListener);
            this.mListener = null;
        }
        if (this.mFocusInfoListener != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mFocusInfoListener);
            this.mFocusInfoListener = null;
        }
        this.mLayout.setEETappedListener(null);
        FocusMagnificationController controller = FocusMagnificationController.getInstance();
        if (!controller.isMagnifying() && controller.isStarting()) {
            controller.stop();
        }
        super.onPause();
        closeLayout(StateBase.FOCUS_LAYOUT);
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase
    protected int getMyBeepPattern() {
        return 6;
    }

    @Override // com.sony.imaging.app.base.shooting.IUserChanging
    public void changeAperture() {
    }

    @Override // com.sony.imaging.app.base.shooting.IUserChanging
    public void changeShutterSpeed() {
    }

    @Override // com.sony.imaging.app.base.shooting.IUserChanging
    public void changeExposure() {
        this.mLayout.setUserChanging(2);
    }

    @Override // com.sony.imaging.app.base.shooting.IUserChanging
    public void changeOther() {
    }

    @Override // com.sony.imaging.app.base.shooting.IUserChanging
    public void changeISOSensitivity() {
    }
}
