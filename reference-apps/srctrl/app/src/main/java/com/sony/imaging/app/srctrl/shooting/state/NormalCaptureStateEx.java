package com.sony.imaging.app.srctrl.shooting.state;

import android.util.Log;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.NormalCaptureState;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.srctrl.caution.InfoEx;
import com.sony.imaging.app.srctrl.shooting.state.CaptureStateUtil;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;
import com.sony.imaging.app.srctrl.util.SRCtrlKikiLogUtil;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.specific.ShootingHandler;
import com.sony.imaging.app.srctrl.webapi.specific.ShutterListenerNotifier;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class NormalCaptureStateEx extends NormalCaptureState {
    private static final String TAG = NormalCaptureStateEx.class.getName();

    @Override // com.sony.imaging.app.base.shooting.NormalCaptureState, com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        if (!CaptureStateUtil.getUtil().isReadyToTakePicture()) {
            setNextState("EE", null);
            CautionUtilityClass.getInstance().setDispatchKeyEvent(InfoEx.CAUTION_ID_SMART_REMOTE_RSRC_ID_STRID_MSG_NO_MEMCARD, new CaptureStateUtil.SRCtrlKeyDispatchForDLT06(null));
            CautionUtilityClass.getInstance().requestTrigger(InfoEx.CAUTION_ID_SMART_REMOTE_RSRC_ID_STRID_MSG_NO_MEMCARD);
            ShootingHandler.getInstance().setShootingStatus(ShootingHandler.ShootingStatus.FAILED);
            ParamsGenerator.addTriggeredErrorParams("No Media");
            return;
        }
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
    }

    private void createShutterListener() {
        this.shutterListener = new CameraEx.ShutterListener() { // from class: com.sony.imaging.app.srctrl.shooting.state.NormalCaptureStateEx.1
            public void onShutter(int status, CameraEx cam) {
                NormalCaptureStateEx.this.mCaptureStatus = status;
                NormalCaptureStateEx.this.onShuttered(status, cam);
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
                } else {
                    if (notifier != null) {
                        notifier.onShutterNotify(status, cam);
                        SRCtrlKikiLogUtil.logRemoteShooting();
                    } else {
                        SRCtrlKikiLogUtil.logLocalShooting();
                        if (status != 0) {
                            cam.cancelTakePicture();
                            cam.getNormalCamera().cancelAutoFocus();
                            ShootingHandler.getInstance().setShootingStatus(ShootingHandler.ShootingStatus.FAILED);
                        }
                    }
                    if (status != 0) {
                        cam.cancelTakePicture();
                        cam.getNormalCamera().cancelAutoFocus();
                    }
                }
                if (status == 0 && CaptureStateUtil.isBlibShootingMode()) {
                    CaptureStateUtil.getUtil().onBulbShuterStart();
                }
            }
        };
    }

    public void setNotifier(ShutterListenerNotifier notifier) {
        Log.v(TAG, "setNotifier in NomalCaptureStateEx");
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
