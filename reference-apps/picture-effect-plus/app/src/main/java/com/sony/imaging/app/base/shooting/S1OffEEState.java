package com.sony.imaging.app.base.shooting;

import android.os.Bundle;
import android.view.MotionEvent;
import com.sony.imaging.app.base.caution.CautionID;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.FocusMagnificationController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.layout.StableLayout;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;
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
    private static final String INH_FACTOR_STILL_WRITING = "INH_FACTOR_STILL_WRITING";
    private static final String KEY_CODE = "keyCode";
    public static final String MF_ASSIST_STATE = "MfAssist";
    private static final String PTAG_TRANSLATION_FROM_AUTOREVIEW = "Transits to EE from AutoReview";
    public static final String STATE_NAME = "S1OffEE";
    private static final String[] TAGS = {CameraNotificationManager.FOCUS_MAGNIFICATION_CHANGED, CameraNotificationManager.FOCUS_RING_ROTATION_STARTED};
    private StableLayout mLayout = null;
    protected NotificationListener mListener;

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
            if (CameraNotificationManager.FOCUS_MAGNIFICATION_CHANGED.equals(tag) && FocusMagnificationController.getInstance().isMagnifying()) {
                S1OffEEState.this.setNextState("MfAssist", null);
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
    public class EETouchedListener implements StableLayout.IEETouchedListener {
        protected EETouchedListener() {
        }

        @Override // com.sony.imaging.app.base.shooting.layout.StableLayout.IEETouchedListener
        public boolean onEETouchDown(MotionEvent e) {
            return false;
        }

        @Override // com.sony.imaging.app.base.shooting.layout.StableLayout.IEETouchedListener
        public void onEETouchUp(MotionEvent e, boolean isReleasedInside) {
            FocusMagnificationController controller;
            List<Integer> supported;
            if (isReleasedInside && FocusModeController.MANUAL.equals(FocusModeController.getInstance().getValue()) && (supported = (controller = FocusMagnificationController.getInstance()).getSupportedValueInt(FocusMagnificationController.TAG_MAGNIFICATION_RATIO)) != null && supported.size() > 0 && "on".equals(controller.getValue(FocusMagnificationController.TAG_IS_MFASSIST_AVAILABLE_IN_SETTINGS))) {
                if (!controller.isAvailable(null)) {
                    CautionUtilityClass.getInstance().requestTrigger(CautionID.CAUTION_GRP_ID_FOCUS_MAGNIFIER_IMAGER_INVALID_GUIDE);
                } else if (controller.isAvailableInDigitalZoom()) {
                    int x = Math.round(e.getRawX());
                    int y = Math.round(e.getRawY());
                    controller.startAtCoordinateOnEE(x, y);
                    BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_TAP);
                }
            }
        }
    }

    protected StableLayout.IEETouchedListener getEETouchedListener() {
        return new EETouchedListener();
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
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 0);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED, 0);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED, 0);
        openLayout(StateBase.FOCUS_LAYOUT);
        openLayout(StateBase.S1OFF_LAYOUT);
        if (FocusModeController.isFocusShiftByControlWheel()) {
            openLayout(StateBase.GUIDE_LAYOUT);
        }
        this.mLayout = (StableLayout) getLayout(StateBase.DEFAULT_LAYOUT);
        AELController.getInstance().setS1AEL(false);
        this.mListener = getListener();
        if (this.mListener != null) {
            CameraNotificationManager.getInstance().setNotificationListener(this.mListener, true);
        }
        this.mLayout.setEETappedListener(getEETouchedListener());
        PTag.traceStopWithEmptyQueue(1);
        PTag.end(PTAG_TRANSLATION_FROM_AUTOREVIEW);
        Bundle b = this.data;
        if (b != null && b.containsKey("keyCode")) {
            if (620 == b.getInt("keyCode")) {
                ExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
            } else if (624 == b.getInt("keyCode")) {
                if (Environment.isMovieAPISupported() && ExecutorCreator.getInstance().isMovieModeSupported()) {
                    AvailableInfo.update();
                    if (!AvailableInfo.isFactor(INH_FACTOR_STILL_WRITING)) {
                        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 2);
                        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.EV_DIAL_CHANGED, 1);
                        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AF_MF, 1);
                        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED, 1);
                        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED, 1);
                        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED, 1);
                        String expMode = ExposureModeController.getInstance().getValue(null);
                        if (ExposureModeController.PROGRAM_AUTO_MODE.equals(expMode)) {
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
        if (ExposureCompensationController.getInstance().isTurnedEvDial()) {
            ExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
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
}
