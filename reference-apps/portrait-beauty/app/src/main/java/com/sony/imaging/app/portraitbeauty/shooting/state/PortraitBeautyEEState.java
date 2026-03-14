package com.sony.imaging.app.portraitbeauty.shooting.state;

import android.os.Bundle;
import android.os.Message;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.shooting.EEState;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.portraitbeauty.caution.PortraitBeautyInfo;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyBackUpKey;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil;
import com.sony.imaging.app.portraitbeauty.menu.controller.AdjustVisuallyStartupController;
import com.sony.imaging.app.portraitbeauty.shooting.PortraitBeautyDisplayModeObserver;
import com.sony.imaging.app.portraitbeauty.shooting.PortraitBeautyEffectProcess;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.DatabaseUtil;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class PortraitBeautyEEState extends EEState {
    public static final String CHILD_ADJUST_MODE_ENTRY_STATE = "AdjustModeEntry";
    private static final String CHILD_S1_OFF_STATE = "S1OffEE";
    private final String TAG = AppLog.getClassName();
    private final String DATA_TAG_ITEMID = MenuState.ITEM_ID;
    private NotificationListener mMediaNotificationListener = null;
    private String mItemId = null;
    private Bundle mBundle = null;
    private int mCautionId = 0;

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.mMediaNotificationListener == null) {
            this.mMediaNotificationListener = new MediaNotificationListener();
        }
        MediaNotificationManager.getInstance().setNotificationListener(this.mMediaNotificationListener);
        if (this.data != null) {
            this.mItemId = this.data.getString(MenuState.ITEM_ID);
            this.data = null;
        }
        PortraitBeautyEffectProcess.sIsCaptureStarted = PortraitBeautyEffectProcess.CAPTURE_NOT_STARTED;
        AELController.getInstance().holdAELock(true);
        AELController.getInstance().cancelAELock();
        super.onResume();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        MediaNotificationManager.getInstance().removeNotificationListener(this.mMediaNotificationListener);
        super.onPause();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mMediaNotificationListener = null;
        this.mCautionId = 0;
        super.onDestroy();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.EEState
    public String getNextChildState() {
        String currentSetting = BackUpUtil.getInstance().getPreferenceString(AdjustVisuallyStartupController.ADJUST_VISULLY_STARTUP_SETTING, "On");
        if ((currentSetting != null && currentSetting.equals("On") && PortraitBeautyUtil.getInstance().isFirstTimeLaunched()) || AdjustVisuallyStartupController.getInstance().isAdjustVisullySelectedInMenu()) {
            AdjustVisuallyStartupController.getInstance().setAdjustVisullySelectedInMenu(false);
            PortraitBeautyUtil.getInstance().setFirstTimeLaunched(false);
            return CHILD_ADJUST_MODE_ENTRY_STATE;
        }
        if (!PortraitBeautyUtil.bIsAdjustModeGuide) {
            int device = PortraitBeautyDisplayModeObserver.getInstance().getActiveDevice();
            if (device == 0) {
                String backupDispMode = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_DISPLAY_MODE_NORMAL_SHOOTING, "1");
                PortraitBeautyDisplayModeObserver.getInstance().setDisplayMode(0, Integer.parseInt(backupDispMode));
            }
            if (device == 1) {
                String backupDispMode2 = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_DISPLAY_MODE_NORMAL_SHOOTING_EVF, "1");
                PortraitBeautyDisplayModeObserver.getInstance().setDisplayMode(0, Integer.parseInt(backupDispMode2));
            }
            if (device == 2) {
                String backupDispMode3 = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_DISPLAY_MODE_NORMAL_SHOOTING_HDMI, "1");
                PortraitBeautyDisplayModeObserver.getInstance().setDisplayMode(0, Integer.parseInt(backupDispMode3));
            }
        }
        String nextChildState = super.getNextChildState();
        if ("S1OffEE".equals(nextChildState)) {
            AELController.getInstance().holdAELock(true);
            AELController.getInstance().cancelAELock();
        }
        return super.getNextChildState();
    }

    @Override // com.sony.imaging.app.base.shooting.EEState
    protected Bundle setStateBundle() {
        return this.mBundle;
    }

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        boolean ret = false;
        int cautionId = 0;
        if (518 == msg.what) {
            AvailableInfo.update();
            if (!(DatabaseUtil.MediaStatus.READ_ONLY == DatabaseUtil.checkMediaStatus())) {
                if (AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_OSS_SW_ON_TYPE_C") || AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_OSS_SW_ON_TYPE_P")) {
                    cautionId = PortraitBeautyInfo.CAUTION_ID_DLAPP_STEADY_SHOT;
                    CautionUtilityClass.getInstance().requestTrigger(PortraitBeautyInfo.CAUTION_ID_DLAPP_STEADY_SHOT);
                } else if (AvailableInfo.isFactor("INH_FACTOR_CAM_NO_LENS_RELEASE")) {
                    if (1 == Environment.getVersionOfHW()) {
                        cautionId = 1399;
                        CautionUtilityClass.getInstance().requestTrigger(1399);
                    } else {
                        cautionId = 3289;
                        CautionUtilityClass.getInstance().requestTrigger(3289);
                    }
                }
                if (MediaNotificationManager.getInstance().isMounted() && MediaNotificationManager.getInstance().getRemaining() < 1 && !PortraitBeautyUtil.bIsAdjustModeGuide) {
                    AppLog.info(this.TAG, "No Space in Memory");
                    cautionId = PortraitBeautyInfo.CAUTION_ID_DLAPP_NO_STORAGE_SPACE_SHOOTING;
                    this.mCautionId = PortraitBeautyInfo.CAUTION_ID_DLAPP_NO_STORAGE_SPACE_SHOOTING;
                    CautionUtilityClass.getInstance().requestTrigger(PortraitBeautyInfo.CAUTION_ID_DLAPP_NO_STORAGE_SPACE_SHOOTING);
                }
            }
        }
        if (cautionId == 0) {
            ret = super.handleMessage(msg);
        }
        return ret;
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
            AppLog.enter(PortraitBeautyEEState.this.TAG, AppLog.getMethodName());
            int state = MediaNotificationManager.getInstance().getMediaState();
            if (state == 0 && (PortraitBeautyEEState.this.mCautionId == 131380 || PortraitBeautyEEState.this.mCautionId == 131381)) {
                CautionUtilityClass.getInstance().disapperTrigger(PortraitBeautyEEState.this.mCautionId);
                PortraitBeautyEEState.this.mCautionId = 0;
            }
            if (1 == state && (PortraitBeautyEEState.this.mCautionId == 131380 || PortraitBeautyEEState.this.mCautionId == 131220)) {
                CautionUtilityClass.getInstance().disapperTrigger(PortraitBeautyEEState.this.mCautionId);
                PortraitBeautyEEState.this.mCautionId = 0;
            }
            AppLog.exit(PortraitBeautyEEState.this.TAG, AppLog.getMethodName());
        }
    }
}
