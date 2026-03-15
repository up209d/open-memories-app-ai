package com.sony.imaging.app.manuallenscompensation.shooting.state;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.ShootingState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCBackUpKey;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;
import com.sony.imaging.app.manuallenscompensation.database.LensCompensationParameter;
import com.sony.imaging.app.manuallenscompensation.shooting.controller.OCAntiHandBlurController;
import com.sony.imaging.app.manuallenscompensation.shooting.controller.OCController;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class OCShootingState extends ShootingState {
    private static final String EXPOSURE_MODE_CHECK_STATE = "ExposureModeCheck";
    private static String TAG = "OCShootingState";
    private static final String[] tag = {"Aperture"};
    private NotificationListener mMediaNotificationListener = null;
    private LensChangedListener mListener = null;

    @Override // com.sony.imaging.app.base.shooting.ShootingState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mMediaNotificationListener == null) {
            this.mMediaNotificationListener = new MediaNotificationListener();
        }
        MediaNotificationManager.getInstance().setNotificationListener(this.mMediaNotificationListener);
        int state = MediaNotificationManager.getInstance().getMediaState();
        BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_LAST_MEDIASTATE_FOR_START, Integer.valueOf(state));
        CameraSetting cameraSetting = CameraSetting.getInstance();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = cameraSetting.getSupportedParameters();
        boolean ovfMode = ((CameraEx.ParametersModifier) params.second).getOVFPreviewMode();
        AppLog.checkIf("", "Applog ovfmode" + ovfMode);
        OCUtil.getInstance().setDiademOVfMode(ovfMode);
        OCUtil.getInstance().setCameraPreviewMode("iris_ss_iso_aeunlock", ovfMode);
        OCController controller = OCController.getInstance();
        controller.enable(true);
        OCUtil.getInstance().setExifData();
        if (this.mListener == null) {
            this.mListener = new LensChangedListener();
        }
        if (this.mListener != null) {
            CameraNotificationManager.getInstance().setNotificationListener(this.mListener);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        OCUtil.getInstance().resetExifDataOff();
        OCController controller = OCController.getInstance();
        controller.enable(false);
        boolean ovfMode = OCUtil.getInstance().isDiademOVfMode();
        OCUtil.getInstance().setCameraPreviewMode("off", ovfMode);
        if (this.mMediaNotificationListener != null) {
            MediaNotificationManager.getInstance().removeNotificationListener(this.mMediaNotificationListener);
            this.mMediaNotificationListener = null;
        }
        if (this.mListener != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mListener);
            this.mListener = null;
        }
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingState
    protected String getNextState() {
        return "ExposureModeCheck";
    }

    /* loaded from: classes.dex */
    private class MediaNotificationListener implements NotificationListener {
        private String[] tags;

        private MediaNotificationListener() {
            this.tags = new String[]{MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            AppLog.info(OCShootingState.TAG, AppLog.getMethodName() + " " + tag);
            if (tag.equals(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE)) {
                int mLastMediaStateForStart = BackUpUtil.getInstance().getPreferenceInt(OCBackUpKey.KEY_BKUP_LAST_MEDIASTATE_FOR_START, 0);
                int state = MediaNotificationManager.getInstance().getMediaState();
                if (mLastMediaStateForStart != state) {
                    BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_LAST_MEDIASTATE_FOR_START, Integer.valueOf(state));
                    AppLog.info(OCShootingState.TAG, AppLog.getMethodName() + " " + state);
                    if (state == 0) {
                        OCUtil.getInstance().deleteAllProfile();
                        AppLog.info("SyncDB", "SyncDB " + AppLog.getMethodName() + "  SyncDB  called from service media removed");
                    } else if (state == 2) {
                        OCUtil.getInstance().synchDBonMediaChange();
                    }
                }
            }
        }
    }

    /* loaded from: classes.dex */
    private class LensChangedListener implements NotificationListener {
        private CameraEx.LensInfo mLens = CameraSetting.getInstance().getLensInfo();

        LensChangedListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return OCShootingState.tag;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            LensCompensationParameter params;
            AppLog.enter(OCShootingState.TAG, AppLog.getMethodName());
            if ("Aperture".equals(tag)) {
                CameraEx.LensInfo lens = CameraSetting.getInstance().getLensInfo();
                boolean previous = this.mLens == null;
                boolean current = lens == null;
                this.mLens = lens;
                if (previous != current && (params = OCUtil.getInstance().getLensParameterObject()) != null && OCAntiHandBlurController.getInstance() != null) {
                    OCAntiHandBlurController.getInstance().updateSteadyShot(params);
                }
            }
            AppLog.exit(OCShootingState.TAG, AppLog.getMethodName());
        }
    }
}
