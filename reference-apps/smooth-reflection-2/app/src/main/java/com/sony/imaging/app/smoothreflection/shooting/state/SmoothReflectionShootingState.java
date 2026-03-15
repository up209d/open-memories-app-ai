package com.sony.imaging.app.smoothreflection.shooting.state;

import android.hardware.Camera;
import com.sony.imaging.app.base.shooting.ShootingState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class SmoothReflectionShootingState extends ShootingState {
    private static final String[] TAGS = {CameraNotificationManager.FLASH_CHANGE, CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED, CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED};
    private final String TAG = AppLog.getClassName();
    CameraSettingChangeListener mListener = new CameraSettingChangeListener();

    @Override // com.sony.imaging.app.base.shooting.ShootingState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        CameraNotificationManager.getInstance().setNotificationListener(this.mListener);
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        CameraNotificationManager.getInstance().removeNotificationListener(this.mListener);
        super.onPause();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onDestroy();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    /* loaded from: classes.dex */
    private class CameraSettingChangeListener implements NotificationListener {
        private CameraSettingChangeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (tag.equalsIgnoreCase(CameraNotificationManager.FLASH_CHANGE) || tag.equalsIgnoreCase(CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED) || tag.equalsIgnoreCase(CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED)) {
                String value = FlashController.getInstance().getValue();
                if (!value.equals("off")) {
                    CameraEx mCameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx();
                    Camera mCamera = mCameraEx.getNormalCamera();
                    Camera.Parameters params = mCameraEx.createEmptyParameters();
                    params.setFlashMode("on");
                    mCamera.setParameters(params);
                    params.setFlashMode("off");
                    mCamera.setParameters(params);
                }
            }
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return SmoothReflectionShootingState.TAGS;
        }
    }
}
