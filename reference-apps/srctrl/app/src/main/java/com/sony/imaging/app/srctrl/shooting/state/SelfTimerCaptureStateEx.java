package com.sony.imaging.app.srctrl.shooting.state;

import android.util.Log;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.SelfTimerCaptureState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.srctrl.caution.InfoEx;
import com.sony.imaging.app.srctrl.shooting.state.CaptureStateUtil;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;
import com.sony.imaging.app.srctrl.util.SRCtrlKikiLogUtil;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.specific.ShootingHandler;
import com.sony.imaging.app.srctrl.webapi.specific.ShutterListenerNotifier;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class SelfTimerCaptureStateEx extends SelfTimerCaptureState {
    private static final String TAG = SelfTimerCaptureStateEx.class.getSimpleName();
    private NotificationListener mMediaNotificationListener = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.shooting.state.SelfTimerCaptureStateEx.1
        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return new String[]{MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (MediaNotificationManager.getInstance().isNoCard() && !CaptureStateUtil.getUtil().isReadyToTakePicture()) {
                SelfTimerCaptureStateEx.this.abortSelfTimer();
                MediaNotificationManager.getInstance().removeNotificationListener(SelfTimerCaptureStateEx.this.mMediaNotificationListener);
                SelfTimerCaptureStateEx.this.mMediaNotificationListener = null;
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public void abortSelfTimer() {
        cancelSelfTimer(null);
        setNextState("EE", null);
        CautionUtilityClass.getInstance().setDispatchKeyEvent(InfoEx.CAUTION_ID_SMART_REMOTE_RSRC_ID_STRID_MSG_NO_MEMCARD, new CaptureStateUtil.SRCtrlKeyDispatchForDLT06(null));
        CautionUtilityClass.getInstance().requestTrigger(InfoEx.CAUTION_ID_SMART_REMOTE_RSRC_ID_STRID_MSG_NO_MEMCARD);
        ShootingHandler.getInstance().setShootingStatus(ShootingHandler.ShootingStatus.FAILED);
        ParamsGenerator.addTriggeredErrorParams("No Media");
    }

    @Override // com.sony.imaging.app.base.shooting.SelfTimerCaptureState, com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        createShutterListener();
        CaptureStateUtil.getUtil().startCapture();
        CaptureStateUtil.remote_shooting_mode = false;
        StateController stateController = StateController.getInstance();
        if (!StateController.AppCondition.SHOOTING_REMOTE.equals(stateController.getAppCondition())) {
            stateController.setAppCondition(StateController.AppCondition.SHOOTING_LOCAL);
        }
        stateController.setState(this);
        stateController.setNotifierOnCaptureState();
        super.onResume();
        CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.SELFTIMER_COUNTDOWN_STATUS, true);
        if (!CaptureStateUtil.getUtil().isReadyToTakePicture()) {
            abortSelfTimer();
            this.mMediaNotificationListener = null;
        } else {
            MediaNotificationManager.getInstance().setNotificationListener(this.mMediaNotificationListener);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.SelfTimerCaptureState, com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mMediaNotificationListener != null) {
            MediaNotificationManager.getInstance().removeNotificationListener(this.mMediaNotificationListener);
        }
        CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.SELFTIMER_COUNTDOWN_STATUS, false);
        super.onPause();
    }

    private void createShutterListener() {
        this.shutterListener = new CameraEx.ShutterListener() { // from class: com.sony.imaging.app.srctrl.shooting.state.SelfTimerCaptureStateEx.2
            public void onShutter(int status, CameraEx cam) {
                SelfTimerCaptureStateEx.this.mCaptureStatus = status;
                SelfTimerCaptureStateEx.this.onShuttered(status, cam);
                ShutterListenerNotifier notifier = CaptureStateUtil.getUtil().getNotifier();
                if (SRCtrlEnvironment.getInstance().isEnableContinuousShooting()) {
                    if (notifier != null) {
                        notifier.onShutterNotify(status, cam);
                    }
                    if (status != 0) {
                        ((CaptureStateUtilContShootingSupported) CaptureStateUtil.getUtil()).updateCaptureInfoOnShutterNG();
                    } else {
                        ((CaptureStateUtilContShootingSupported) CaptureStateUtil.getUtil()).updateCaptureInfoOnShutterOK();
                    }
                    if (CaptureStateUtil.remote_shooting_mode) {
                        SRCtrlKikiLogUtil.logRemoteShooting();
                    } else {
                        SRCtrlKikiLogUtil.logLocalShooting();
                    }
                } else if (notifier != null) {
                    SRCtrlKikiLogUtil.logRemoteShooting();
                    notifier.onShutterNotify(status, cam);
                } else {
                    SRCtrlKikiLogUtil.logLocalShooting();
                    if (status != 0) {
                        cam.cancelTakePicture();
                        cam.getNormalCamera().cancelAutoFocus();
                        ShootingHandler.getInstance().setShootingStatus(ShootingHandler.ShootingStatus.FAILED);
                    }
                }
                if (status == 0 && CaptureStateUtil.isBlibShootingMode()) {
                    CaptureStateUtil.getUtil().onBulbShuterStart();
                }
            }
        };
    }

    public void setNotifier(ShutterListenerNotifier notifier) {
        Log.v(TAG, "setNotifier in SelfTimerCaptureStateEx");
        CaptureStateUtil.getUtil().setNotifier(notifier);
    }

    @Override // com.sony.imaging.app.base.shooting.CaptureStateBase
    protected boolean isTakePictureByRemote() {
        if (CaptureStateUtil.isSingleShooting() && !CameraSetting.getInstance().isShutterSpeedBulb()) {
            return CaptureStateUtil.remote_shooting_mode;
        }
        return false;
    }
}
