package com.sony.imaging.app.bracketpro.shooting.state;

import android.hardware.Camera;
import android.util.Log;
import com.sony.imaging.app.base.shooting.ShootingState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.bracketpro.AppLog;
import com.sony.imaging.app.bracketpro.menu.controller.BMMenuController;
import com.sony.imaging.app.bracketpro.menu.controller.BracketMasterController;
import com.sony.imaging.app.bracketpro.shooting.BMCaptureProcess;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class BMShootingState extends ShootingState {
    private static final String[] TAGS = {CameraNotificationManager.FLASH_CHANGE, CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED, CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED};
    private final String TAG = AppLog.getClassName();
    CameraSettingChangeListener mListener = new CameraSettingChangeListener();

    @Override // com.sony.imaging.app.base.shooting.ShootingState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        CameraNotificationManager.getInstance().setNotificationListener(this.mListener);
        BracketMasterController.getInstance().registerNotificationListeners();
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        BracketMasterController.getInstance().unregisterNotificationListeners();
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
                Log.i("HOGE", "################################ BMShootingState::onNotify");
                CameraEx mCameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx();
                Camera mCamera = mCameraEx.getNormalCamera();
                Camera.Parameters params = mCameraEx.createEmptyParameters();
                String value = FlashController.getInstance().getValue();
                if (BMMenuController.getInstance().getSelectedBracket().equalsIgnoreCase(BMMenuController.FlashBracket)) {
                    if (!BMCaptureProcess.isCaptureStarted) {
                        params.setFlashMode("on");
                        mCamera.setParameters(params);
                        return;
                    }
                    return;
                }
                if (!"off".equals(value)) {
                    Log.i("HOGE", "################################ BMShootingState::onNotify FLASH_MODE_ON => FLASH_MODE_OFF");
                    params.setFlashMode("on");
                    mCamera.setParameters(params);
                    Camera.Parameters params2 = mCameraEx.createEmptyParameters();
                    params2.setFlashMode("off");
                    mCamera.setParameters(params2);
                }
            }
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return BMShootingState.TAGS;
        }
    }
}
