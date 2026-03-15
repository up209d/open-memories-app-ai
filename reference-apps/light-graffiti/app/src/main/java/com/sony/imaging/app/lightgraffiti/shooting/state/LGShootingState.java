package com.sony.imaging.app.lightgraffiti.shooting.state;

import android.hardware.Camera;
import android.util.Log;
import com.sony.imaging.app.base.shooting.ShootingState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGFlashController;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.lightgraffiti.util.LGUtility;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class LGShootingState extends ShootingState {
    private final String TAG = "LGShootingState";
    LensStateListener mLensListener;
    CameraSettingChangeListener mListener;
    ShootingStageChangeListener mStageListener;
    private static final String[] TAGS_SHOOTING_STAGE = {LGConstants.SHOOTING_1ST_OPEN, LGConstants.CONFIRM_REVIEW_FINISH};
    private static final String[] TAGS = {CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED, CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED};

    public LGShootingState() {
        this.mLensListener = new LensStateListener();
        this.mStageListener = new ShootingStageChangeListener();
        this.mListener = new CameraSettingChangeListener();
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        Log.d("LGShootingState", "onResume");
        super.onResume();
        CameraNotificationManager.getInstance().setNotificationListener(this.mStageListener);
        CameraNotificationManager.getInstance().setNotificationListener(this.mListener);
        if (!LGUtility.getInstance().isModelDSC()) {
            Log.d("LGShootingState", "Model is *not* DSC. App set the LensProblem Listener.");
            CameraNotificationManager.getInstance().setNotificationListener(this.mLensListener);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        Log.d("LGShootingState", "onPause");
        CameraNotificationManager.getInstance().removeNotificationListener(this.mStageListener);
        CameraNotificationManager.getInstance().removeNotificationListener(this.mListener);
        if (!LGUtility.getInstance().isModelDSC()) {
            Log.d("LGShootingState", "Model is *not* DSC. App remove the LensProblem Listener.");
            CameraNotificationManager.getInstance().removeNotificationListener(this.mLensListener);
        }
        super.onPause();
    }

    /* loaded from: classes.dex */
    private class ShootingStageChangeListener implements NotificationListener {
        private ShootingStageChangeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            Log.i("ShootingStageChangeListener", "onNotify: " + tag);
            if (tag.equalsIgnoreCase(LGConstants.CONFIRM_REVIEW_FINISH)) {
                CameraNotificationManager.getInstance().removeNotificationListener(LGShootingState.this.mListener);
                FlashController.getInstance().getValue();
                String value = BackUpUtil.getInstance().getPreferenceString(LGConstants.BACKUP_KEY_3RD_SELECTED_FLASH, "on");
                LGFlashController.getInstance().setValue(FlashController.FLASHMODE, value);
                return;
            }
            if (tag.equalsIgnoreCase(LGConstants.SHOOTING_1ST_OPEN)) {
                CameraNotificationManager.getInstance().setNotificationListener(LGShootingState.this.mListener);
            }
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return LGShootingState.TAGS_SHOOTING_STAGE;
        }
    }

    /* loaded from: classes.dex */
    private class CameraSettingChangeListener implements NotificationListener {
        private CameraSettingChangeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            Log.i("LGShootingState", "onNotify");
            if (tag.equalsIgnoreCase(CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED) || tag.equalsIgnoreCase(CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED)) {
                String value = FlashController.getInstance().getValue();
                if (!"off".equals(value) && !LGStateHolder.getInstance().isShootingStage3rd()) {
                    CameraEx mCameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx();
                    Camera mCamera = mCameraEx.getNormalCamera();
                    Camera.Parameters params = mCameraEx.createEmptyParameters();
                    Log.i("LGShootingState", "onNotify FLASH_MODE_ON => FLASH_MODE_OFF");
                    params.setFlashMode("on");
                    mCamera.setParameters(params);
                    Camera.Parameters params2 = mCameraEx.createEmptyParameters();
                    params2.setFlashMode("off");
                    mCamera.setParameters(params2);
                    return;
                }
                if ("off".equals(value) || LGStateHolder.getInstance().isShootingStage3rd()) {
                }
            }
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return LGShootingState.TAGS;
        }
    }

    public static void startFirstTimeLensCare() {
        LGUtility.getInstance().isFirstTimeLaunchCareStarted = true;
    }

    /* loaded from: classes.dex */
    private class LensStateListener implements NotificationListener {
        private final String[] lensTAGS;

        private LensStateListener() {
            this.lensTAGS = new String[]{CameraNotificationManager.DEVICE_LENS_CHANGED};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            Log.i("LensStateListener", "onNotify : " + tag + "  isFirstTimeLaunchCareStarted=" + LGUtility.getInstance().isFirstTimeLaunchCareStarted);
            if (!LGUtility.getInstance().isFirstTimeLaunchCareStarted) {
                Log.i("LensStateListener", "isFirstTimeLaunchCareStarted=" + LGUtility.getInstance().isFirstTimeLaunchCareStarted);
            } else if (tag.equalsIgnoreCase(CameraNotificationManager.DEVICE_LENS_CHANGED) && !LGUtility.getInstance().isLensAttachEventReady) {
                CameraNotificationManager.getInstance().requestNotify(LGConstants.LG_MESSAGE_LENS_STATE_CHANGED);
            }
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.lensTAGS;
        }
    }
}
