package com.sony.imaging.app.base.shooting;

import android.os.Bundle;
import android.view.MotionEvent;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionID;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.FocusMagnificationController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.layout.IStableLayout;
import com.sony.imaging.app.base.shooting.layout.StableLayout;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.OLEDWrapper;
import com.sony.imaging.app.util.PTag;
import java.util.List;

/* loaded from: classes.dex */
public class S1OffEEState extends StateBase implements IUserChanging {
    private static final int FOCUS_MAGNIFICATION_DISABLE = 1;
    private static final int FOCUS_MAGNIFICATION_ENABLE = 0;
    private static final int FOCUS_MAGNIFICATION_EXCLUDED = 2;
    private static final String INH_FACTOR_STILL_WRITING = "INH_FACTOR_STILL_WRITING";
    private static final String KEY_CODE = "keyCode";
    public static final String MF_ASSIST_STATE = "MfAssist";
    private static final String PTAG_TRANSLATION_FROM_AUTOREVIEW = "Transits to EE from AutoReview";
    public static final String STATE_NAME = "S1OffEE";
    private static final String TAG = "S1OffEEState";
    private static final String[] TAGS = {CameraNotificationManager.FOCUS_MAGNIFICATION_CHANGED, CameraNotificationManager.FOCUS_RING_ROTATION_STARTED, CameraNotificationManager.FOCUS_MAGNIFICATION_AVAILABILITY_CHANGED};
    private StableLayout.IEETouchedListener mEETouchedListener;
    private IStableLayout mLayout = null;
    protected NotificationListener mListener;

    static /* synthetic */ int access$100() {
        return getFocusMagnificationState();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class MfAssistListener implements NotificationListener {
        protected MfAssistListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return S1OffEEState.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            FocusMagnificationController controller;
            List<Integer> supported;
            if (CameraNotificationManager.FOCUS_RING_ROTATION_STARTED.equals(tag)) {
                if (FocusAreaController.getInstance().getSensorType() == 2) {
                    String focusMode = FocusModeController.getInstance().getValue();
                    if (FocusModeController.MANUAL.equals(focusMode) && (supported = (controller = FocusMagnificationController.getInstance()).getSupportedValueInt(FocusMagnificationController.TAG_MAGNIFICATION_RATIO)) != null && supported.size() > 0 && !"off".equals(controller.getValue(FocusMagnificationController.TAG_IS_MFASSIST_AVAILABLE_IN_SETTINGS)) && controller.isAvailable(null) && controller.isAvailableInDigitalZoom()) {
                        controller.startMfAssist();
                        return;
                    }
                    return;
                }
                return;
            }
            if (CameraNotificationManager.FOCUS_MAGNIFICATION_CHANGED.equals(tag)) {
                if (FocusMagnificationController.getInstance().isMagnifying()) {
                    S1OffEEState.this.setNextState("MfAssist", null);
                }
            } else if (CameraNotificationManager.FOCUS_MAGNIFICATION_AVAILABILITY_CHANGED.equals(tag)) {
                int focusMagnificationState = S1OffEEState.access$100();
                if (focusMagnificationState == 0 || focusMagnificationState == 2) {
                    S1OffEEState.this.mLayout.setEETappedListener(S1OffEEState.this.getEETouchedListener());
                } else {
                    S1OffEEState.this.mLayout.setEETappedListener(null);
                }
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
    public static class EETouchedListener implements StableLayout.IEETouchedListener {
        protected EETouchedListener() {
        }

        @Override // com.sony.imaging.app.base.shooting.layout.StableLayout.IEETouchedListener
        public boolean onEETouchDown(MotionEvent e) {
            return false;
        }

        @Override // com.sony.imaging.app.base.shooting.layout.StableLayout.IEETouchedListener
        public void onEETouchUp(MotionEvent e, boolean isReleasedInside) {
            int touchOperationVersion = Environment.getTouchOperationVersion();
            if (touchOperationVersion == 1 && isReleasedInside) {
                int focusMagnificationState = S1OffEEState.access$100();
                switch (focusMagnificationState) {
                    case 0:
                        int x = Math.round(e.getRawX());
                        int y = Math.round(e.getRawY());
                        FocusMagnificationController controller = FocusMagnificationController.getInstance();
                        controller.startAtCoordinateOnEE(x, y);
                        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_TAP);
                        return;
                    case 1:
                    default:
                        return;
                    case 2:
                        CautionUtilityClass.getInstance().requestTrigger(CautionID.CAUTION_GRP_ID_FOCUS_MAGNIFIER_IMAGER_INVALID_GUIDE);
                        return;
                }
            }
        }

        @Override // com.sony.imaging.app.base.shooting.layout.StableLayout.IEETouchedListener
        public boolean onEEDoubleTap(MotionEvent e) {
            int touchOperationVersion = Environment.getTouchOperationVersion();
            if (touchOperationVersion < 2) {
                return false;
            }
            int focusMagnificationState = S1OffEEState.access$100();
            switch (focusMagnificationState) {
                case 0:
                    int x = Math.round(e.getRawX());
                    int y = Math.round(e.getRawY());
                    FocusMagnificationController controller = FocusMagnificationController.getInstance();
                    controller.startAtCoordinateOnEEDirectly(x, y);
                    BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_TAP);
                    return true;
                case 1:
                default:
                    return false;
                case 2:
                    CautionUtilityClass.getInstance().requestTrigger(CautionID.CAUTION_GRP_ID_FOCUS_MAGNIFIER_IMAGER_INVALID_GUIDE);
                    return true;
            }
        }
    }

    protected StableLayout.IEETouchedListener getEETouchedListener() {
        if (this.mEETouchedListener == null) {
            this.mEETouchedListener = new EETouchedListener();
        }
        return this.mEETouchedListener;
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.mLayout.setEETappedListener(null);
        if (this.mListener != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mListener);
            this.mListener = null;
        }
        FocusMagnificationController controller = FocusMagnificationController.getInstance();
        if (!controller.isMagnifying() && controller.isStarting()) {
            controller.stop();
        }
        if (FocusModeController.isFocusShiftByControlWheel()) {
            closeLayout(StateBase.GUIDE_LAYOUT);
        }
        closeLayout(StateBase.S1OFF_LAYOUT);
        closeLayout(StateBase.FOCUS_LAYOUT);
        rememberDefaultBootMode();
        super.onPause();
    }

    protected void rememberDefaultBootMode() {
        if (Environment.isSupportingDefaultBootMode() && 2 == RunStatus.getStatus()) {
            BaseApp.rememberDefaultBootModeOnPullingBack();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 0);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED, 0);
        openLayout(StateBase.FOCUS_LAYOUT);
        openLayout(StateBase.S1OFF_LAYOUT);
        if (FocusModeController.isFocusShiftByControlWheel()) {
            openLayout(StateBase.GUIDE_LAYOUT);
        }
        this.mLayout = (IStableLayout) getLayout(StateBase.DEFAULT_LAYOUT);
        AELController.getInstance().setS1AEL(false);
        this.mListener = getListener();
        if (this.mListener != null) {
            CameraNotificationManager.getInstance().setNotificationListener(this.mListener, true);
        }
        int focusMagnificationState = getFocusMagnificationState();
        if (focusMagnificationState == 0 || focusMagnificationState == 2) {
            this.mLayout.setEETappedListener(getEETouchedListener());
        } else {
            this.mLayout.setEETappedListener(null);
        }
        PTag.traceStopWithEmptyQueue(1);
        PTag.end(PTAG_TRANSLATION_FROM_AUTOREVIEW);
        Bundle b = this.data;
        if (b != null && b.containsKey("keyCode")) {
            if (624 == b.getInt("keyCode")) {
                if (Environment.isMovieAPISupported() && ExecutorCreator.getInstance().isMovieModeSupported()) {
                    AvailableInfo.update();
                    if (!AvailableInfo.isFactor(INH_FACTOR_STILL_WRITING)) {
                        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 2);
                        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AF_MF, 1);
                        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED, 1);
                        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED, 1);
                        String expMode = ExposureModeController.getInstance().getValue(null);
                        if ("ProgramAuto".equals(expMode)) {
                            CameraSetting.getInstance().resetProgramLine();
                        }
                        ExecutorCreator ec = ExecutorCreator.getInstance();
                        ec.setRecordingMode(2, null);
                        setNextState("EE", null);
                    } else {
                        CautionUtilityClass.getInstance().requestTrigger(1410);
                    }
                } else {
                    CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_CANNOT_REC_MOVIE);
                }
            }
            b.remove("keyCode");
        }
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase
    protected int getMyBeepPattern() {
        return 6;
    }

    @Override // com.sony.imaging.app.base.shooting.IUserChanging
    public void changeAperture() {
        this.mLayout.setUserChanging(0);
    }

    @Override // com.sony.imaging.app.base.shooting.IUserChanging
    public void changeShutterSpeed() {
        this.mLayout.setUserChanging(1);
    }

    @Override // com.sony.imaging.app.base.shooting.IUserChanging
    public void changeExposure() {
        this.mLayout.setUserChanging(2);
    }

    @Override // com.sony.imaging.app.base.shooting.IUserChanging
    public void changeOther() {
        this.mLayout.setUserChanging(-1);
    }

    @Override // com.sony.imaging.app.base.shooting.IUserChanging
    public void changeISOSensitivity() {
        this.mLayout.setUserChanging(3);
    }

    @Override // com.sony.imaging.app.fw.State
    public OLEDWrapper.OLED_TYPE getOledType() {
        return OLEDWrapper.OLED_TYPE.LUMINANCE_ALPHA;
    }

    private static int getFocusMagnificationState() {
        FocusMagnificationController controller;
        List<Integer> supported;
        if (!FocusModeController.MANUAL.equals(FocusModeController.getInstance().getValue()) || (supported = (controller = FocusMagnificationController.getInstance()).getSupportedValueInt(FocusMagnificationController.TAG_MAGNIFICATION_RATIO)) == null || supported.isEmpty()) {
            return 1;
        }
        if (controller.isAvailable(null)) {
            return controller.isAvailableInDigitalZoom() ? 0 : 1;
        }
        return 2;
    }
}
