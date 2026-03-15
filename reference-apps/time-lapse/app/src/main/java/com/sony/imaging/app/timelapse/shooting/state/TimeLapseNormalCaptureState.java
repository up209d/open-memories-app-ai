package com.sony.imaging.app.timelapse.shooting.state;

import android.os.Bundle;
import android.util.Log;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.NormalCaptureState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.shooting.TimelapseExecutorCreator;
import com.sony.imaging.app.timelapse.shooting.controller.TLShootModeSettingController;
import com.sony.imaging.app.timelapse.shooting.controller.TimelapseMonitorBrightnessController;
import com.sony.imaging.app.timelapse.shooting.layout.AutoReviewBlackLayout;
import com.sony.imaging.app.timelapse.shooting.layout.TimeLapseStableLayout;

/* loaded from: classes.dex */
public class TimeLapseNormalCaptureState extends NormalCaptureState {
    private static final String NEXT_EE_STATE = "EE";
    private static final String NEXT_STATE = "ExposureModeCheck";
    private static final String TAG = TimeLapseNormalCaptureState.class.getName();
    public static final String TWOSECONDTIMERLAYOUT = "TWOSECONDTIMERLAYOUT";

    @Override // com.sony.imaging.app.base.shooting.NormalCaptureState, com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() == 0 || TLCommonUtil.getInstance().isTestShot()) {
            TLCommonUtil.getInstance().setVirtualMediaIds();
        }
        TimeLapseStableLayout.isCapturing = true;
        CameraNotificationManager.getInstance().requestNotify(TimeLapseConstants.CAPTURE_PROCESSING);
        if (!TLCommonUtil.getInstance().isTestShot()) {
            TimelapseExecutorCreator creator = (TimelapseExecutorCreator) TimelapseExecutorCreator.getInstance();
            creator.createTimeLapseBOData();
            TLCommonUtil.getInstance().setIsPowerOffDuringCapturing(false);
        }
        super.onResume();
        Log.i(TAG, "TimeLapseNormalCaptureState  onResume( )");
        if (TLCommonUtil.getInstance().checkExposureMode()) {
            moveToS1OffEEState(AppRoot.USER_KEYCODE.S1_OFF);
            return;
        }
        TLCommonUtil.getInstance().setDetachedLensStatus(false);
        TimeLapseStableLayout.isCapturing = true;
        openLayout(TWOSECONDTIMERLAYOUT);
        openLayout(AutoReviewBlackLayout.TAG);
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() != 1) {
            MediaNotificationManager.getInstance().pause(getActivity());
        }
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 3);
    }

    @Override // com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        TimeLapseConstants.sCaptureImageCounter = 0;
        closeLayout(TWOSECONDTIMERLAYOUT);
        closeLayout(AutoReviewBlackLayout.TAG);
        if (TimeLapseStableLayout.isCapturing) {
            CameraNotificationManager.getInstance().requestNotify(TimeLapseConstants.CAPTURE_PROCESSING_FINISH);
        }
        TimeLapseStableLayout.isCapturing = false;
        TLCommonUtil.getInstance().setDetachedLensStatus(false);
        TimeLapseStableLayout.is2SecondTimerFinish = false;
        if (!TLCommonUtil.getInstance().isTestShot() && TLShootModeSettingController.getInstance().getCurrentCaptureState() == 0) {
            TLCommonUtil.getInstance().setActualMediaIds();
        }
        if (DisplayModeObserver.getInstance().getActiveDevice() != 2) {
            TimelapseMonitorBrightnessController.getInstance().clearSetting();
        }
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() != 1) {
            MediaNotificationManager.getInstance().resume(getActivity());
        }
    }

    private void moveToS1OffEEState(int keyEvent) {
        Log.i(TAG, "TimeLapseNormalCaptureState:   moveToS1OffEEState()");
        Bundle data = new Bundle();
        data.putParcelable(EventParcel.KEY_KEYCODE, new EventParcel(keyEvent));
        setNextState(getNextState(), data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.CaptureStateBase
    public String getNextState() {
        if (!TLCommonUtil.getInstance().isTestShot() && TLShootModeSettingController.getInstance().getCurrentCaptureState() == 1 && TLCommonUtil.getInstance().getAETrakingStatus() == 4) {
            return NEXT_STATE;
        }
        String stateNext = super.getNextState();
        if (stateNext.equals("EE")) {
            return NEXT_STATE;
        }
        return stateNext;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.CaptureStateBase
    public boolean isTakePictureByRemote() {
        boolean isRemoteCaptured = super.isTakePictureByRemote();
        TLCommonUtil.getInstance().setTakePictureByRemote(isRemoteCaptured);
        return isRemoteCaptured;
    }
}
