package com.sony.imaging.app.startrails.shooting.state;

import com.sony.imaging.app.base.shooting.ShootingState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.startrails.base.menu.controller.STWhiteBalanceController;
import com.sony.imaging.app.startrails.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.startrails.util.STBackUpKey;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.startrails.util.ThemeRecomandedMenuSetting;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class STShootingState extends ShootingState {
    private static final String TAG = "STShootingState";
    private static final String[] TAGS = {CameraNotificationManager.FLASH_CHANGE, CameraNotificationManager.FOCUS_CHANGE, CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED, CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED};
    CameraSettingChangeListener mListener = new CameraSettingChangeListener();

    @Override // com.sony.imaging.app.base.shooting.ShootingState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        new STWhiteBalanceController();
        CameraNotificationManager.getInstance().setNotificationListener(this.mListener);
        ThemeSelectionController.getInstance().setValue(TAG, BackUpUtil.getInstance().getPreferenceString(STBackUpKey.SELECTED_THEME, ThemeSelectionController.BRIGHT_NIGHT));
        if (STUtility.getInstance().getCurrentTrail() == 1 && STUtility.getInstance().isAppTopBooted()) {
            ThemeRecomandedMenuSetting.getInstance().updateDarkNightAperture();
        }
        STUtility.checkIRISState();
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this.mListener);
        super.onPause();
    }

    /* loaded from: classes.dex */
    private class CameraSettingChangeListener implements NotificationListener {
        private CameraSettingChangeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (tag.equalsIgnoreCase(CameraNotificationManager.FLASH_CHANGE) || tag.equalsIgnoreCase(CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED) || tag.equalsIgnoreCase(CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED)) {
                STUtility.getInstance().setFlashModeOff();
            } else if (tag.equalsIgnoreCase(CameraNotificationManager.FOCUS_CHANGE)) {
                checkFocusMode();
            }
        }

        private void checkFocusMode() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return STShootingState.TAGS;
        }
    }
}
