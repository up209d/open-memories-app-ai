package com.sony.imaging.app.timelapse.shooting.state;

import com.sony.imaging.app.base.shooting.ShootingState;
import com.sony.imaging.app.base.shooting.camera.AutoFocusModeController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.shooting.controller.TimelapseMonitorBrightnessController;
import com.sony.imaging.app.timelapse.shooting.controller.TimelapseWhiteBalanceController;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class TimelapseShootingState extends ShootingState {
    private static final String[] TAGS = {CameraNotificationManager.FOCUS_CHANGE, "AutoFocusMode", CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED, CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED};
    CameraSettingChangeListener mListener = new CameraSettingChangeListener();

    @Override // com.sony.imaging.app.base.shooting.ShootingState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        new TimelapseWhiteBalanceController();
        CameraNotificationManager.getInstance().setNotificationListener(this.mListener);
        setAFS();
        TLCommonUtil.getInstance().setFlashModeOff();
        TimelapseMonitorBrightnessController.getInstance().initializeFromBackup();
        TLCommonUtil.getInstance().setIsPowerOffDuringCapturing(false);
        TLCommonUtil.getInstance().setPictureEffectIconUpdate(true);
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        TLCommonUtil.getInstance().setIsPowerOffDuringCapturing(true);
        CameraNotificationManager.getInstance().removeNotificationListener(this.mListener);
        System.gc();
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAFS() {
        String focusMode = FocusModeController.getInstance().getValue();
        String autoFocusMode = AutoFocusModeController.getInstance().getValue();
        if ("auto".equals(focusMode) && !"af-s".equals(autoFocusMode)) {
            AutoFocusModeController.getInstance().setValue("af-s");
        }
    }

    /* loaded from: classes.dex */
    private class CameraSettingChangeListener implements NotificationListener {
        private CameraSettingChangeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (tag.equalsIgnoreCase(CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED) || tag.equalsIgnoreCase(CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED)) {
                TLCommonUtil.getInstance().setFlashModeOff();
            } else if (tag.equalsIgnoreCase(CameraNotificationManager.FOCUS_CHANGE) || tag.equalsIgnoreCase("AutoFocusMode")) {
                TimelapseShootingState.this.setAFS();
            }
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return TimelapseShootingState.TAGS;
        }
    }
}
