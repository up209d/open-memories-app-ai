package com.sony.imaging.app.base.shooting;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCConstants;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.PTag;
import com.sony.scalar.hardware.CameraEx;
import java.util.Timer;
import java.util.TimerTask;

/* loaded from: classes.dex */
public class DevelopmentState extends StateBase {
    protected static final String CURRENT_STATE_NAME = "Development";
    public static final String DEVELOPMENT_STATE = "DEVELOPMENT_STATE";
    public static final String GLID_LAYOUT = "GLID_LAYOUT";
    private static final String LOG_S2_ON = "handling S2 on";
    protected static final String NEXT_AUTOREVIEW_FOR_LIMITEDCONTSHOOTING_STATE = "AutoReviewForLimitedContShooting";
    protected static final String NEXT_AUTOREVIEW_STATE = "AutoReview";
    protected static final String NEXT_CAPTURE_STATE = "Capture";
    protected static final String NEXT_EE_STATE = "EE";
    protected static final String PTAG_DEVELOP_END = "autoreview start is called";
    protected static final String PTAG_EE_START = "ee start is called";
    private static final String TAG = "DevelopmentState";
    protected static int WAITING_TIME_FOR_DISPLAY_PROGRESS = 1200;
    public static final int WAITING_TIME_FOR_UNMUTE = 300;
    private Timer mProgressTimer;
    private TimerTask mProgressTimerTask;
    private boolean isPreviewStartedOnPreviousState = false;
    private CameraEx.PreviewStartListener previewListener = new CameraEx.PreviewStartListener() { // from class: com.sony.imaging.app.base.shooting.DevelopmentState.2
        public void onStart(CameraEx cam) {
            DevelopmentState.this.onPreviewStart();
        }
    };
    private CameraEx.PictureReviewStartListener pictureReviewListener = new CameraEx.PictureReviewStartListener() { // from class: com.sony.imaging.app.base.shooting.DevelopmentState.3
        public void onStart(CameraEx cam) {
            DevelopmentState.this.onPictureReveiwStart();
        }
    };

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        ShootingExecutor.setPictureReviewStartListener(this.pictureReviewListener);
        ShootingExecutor.setPreviewStartListener(this.previewListener);
        startProgressTimer();
        if (this.data != null) {
            this.isPreviewStartedOnPreviousState = this.data.getBoolean(CaptureStateBase.IS_PREVIEW_STARTED);
            removeData(CaptureStateBase.IS_PREVIEW_STARTED);
            if (this.isPreviewStartedOnPreviousState) {
                setNextState("EE", null);
            }
        }
        openLayout(GLID_LAYOUT);
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        ShootingExecutor.setPictureReviewStartListener(null);
        ShootingExecutor.setPreviewStartListener(null);
        notifyProgressStatus(1.0d);
        stopProgressTimer();
        super.onPause();
        closeLayout(GLID_LAYOUT);
    }

    public void notifyProgressStatusForBulb() {
        notifyProgressStatus(OCConstants.DOUBLE_ZERO);
    }

    protected void notifyProgressStatus(double progress) {
        CameraNotificationManager mgr = CameraNotificationManager.getInstance();
        mgr.requestNotify(CameraNotificationManager.PROCESSING_PROGRESS, Double.valueOf(progress));
    }

    protected boolean isInvalidSetting() {
        DriveModeController cntl = DriveModeController.getInstance();
        String value = cntl.getValue(DriveModeController.DRIVEMODE);
        return value.equals(DriveModeController.BURST) || value.equals(DriveModeController.SPEED_PRIORITY_BURST) || value.equals(DriveModeController.SELF_TIMER_BURST);
    }

    private void startProgressTimer() {
        int shutterSpeed;
        if (!isInvalidSetting()) {
            if (this.mProgressTimer == null) {
                this.mProgressTimer = new Timer(true);
            }
            if (this.mProgressTimerTask == null) {
                this.mProgressTimerTask = new TimerTask() { // from class: com.sony.imaging.app.base.shooting.DevelopmentState.1
                    @Override // java.util.TimerTask, java.lang.Runnable
                    public void run() {
                        DevelopmentState.this.notifyProgressStatus(OCConstants.DOUBLE_ZERO);
                    }
                };
            }
            Pair<Integer, Integer> ss = CameraSetting.getInstance().getShutterSpeed();
            if (ss != null) {
                shutterSpeed = (((Integer) ss.first).intValue() * 1000) / ((Integer) ss.second).intValue();
            } else {
                Log.i(TAG, "Failed to get the ShutterSpeed value from CameraSetting.");
                shutterSpeed = 0;
            }
            this.mProgressTimer.schedule(this.mProgressTimerTask, (WAITING_TIME_FOR_DISPLAY_PROGRESS + shutterSpeed) - 300);
        }
    }

    private void stopProgressTimer() {
        if (this.mProgressTimer != null) {
            this.mProgressTimer.cancel();
        }
        this.mProgressTimer = null;
        this.mProgressTimerTask = null;
    }

    protected void onPreviewStart() {
        setNextState("EE", null);
        PTag.end(PTAG_EE_START);
    }

    protected void onPictureReveiwStart() {
        int reviewtype = ShootingExecutor.getReviewType();
        if (2 == reviewtype) {
            setNextState(NEXT_AUTOREVIEW_FOR_LIMITEDCONTSHOOTING_STATE, null);
        } else {
            setNextState("AutoReview", null);
        }
        PTag.end(PTAG_DEVELOP_END);
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase
    protected int getMyBeepPattern() {
        return 6;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        if (518 != msg.what) {
            return false;
        }
        Log.i(TAG, LOG_S2_ON);
        if (cancelTakePictureOnS2Pushed()) {
            ExecutorCreator.getInstance().getSequence().cancelTakePicture();
        }
        Bundle returnBundle = new Bundle();
        returnBundle.putString(StateBase.RETURN_STATE_KEY, CURRENT_STATE_NAME);
        setNextState(NEXT_CAPTURE_STATE, returnBundle);
        return true;
    }

    protected boolean cancelTakePictureOnS2Pushed() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.NONE;
    }

    @Override // com.sony.imaging.app.fw.State
    public void setNextState(String name, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        super.setNextState(name, bundle);
    }
}
