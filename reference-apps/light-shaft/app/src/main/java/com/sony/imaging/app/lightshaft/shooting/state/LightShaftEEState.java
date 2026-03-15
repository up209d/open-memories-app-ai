package com.sony.imaging.app.lightshaft.shooting.state;

import android.os.Bundle;
import android.os.Message;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.shooting.EEState;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.lightshaft.AppLog;
import com.sony.imaging.app.lightshaft.LightShaft;
import com.sony.imaging.app.lightshaft.caution.LightShaftInfo;
import com.sony.imaging.app.lightshaft.shooting.camera.EffectSelectController;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class LightShaftEEState extends EEState {
    private static final String NEXT_STATE_MENU = "Menu";
    private static final String TAG = "LightShaftEEState";
    private String mItemId;
    private Bundle mBundle = null;
    protected NotificationListener mediaStatusChangedListener = new MediaNotificationListener();

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        if (this.data != null) {
            this.mItemId = this.data.getString(MenuState.ITEM_ID);
            this.data = null;
        }
        super.onResume();
        MediaNotificationManager.getInstance().setNotificationListener(this.mediaStatusChangedListener);
    }

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        MediaNotificationManager.getInstance().removeNotificationListener(this.mediaStatusChangedListener);
        this.mItemId = null;
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.EEState
    public String getNextChildState() {
        if (ExposureModeController.EXPOSURE_MODE.equals(this.mItemId)) {
            AppLog.info(TAG, "for menu state mItemId" + this.mItemId);
            this.mBundle = new Bundle();
            this.mBundle.putString(ExposureModeController.EXPOSURE_MODE, this.mItemId);
            this.mBundle.putString(MenuState.ITEM_ID, this.mItemId);
            this.mItemId = null;
            return "Menu";
        }
        if (LightShaft.isLaunchAppBooted()) {
            LightShaft.setMenuBoot(false);
            AppLog.info(TAG, "for menu state mItemId" + this.mItemId);
            this.mBundle = new Bundle();
            this.mItemId = EffectSelectController.EFFECTSELECT;
            this.mBundle.putString(MenuState.ITEM_ID, this.mItemId);
            this.mItemId = null;
            return "Menu";
        }
        return super.getNextChildState();
    }

    @Override // com.sony.imaging.app.base.shooting.EEState
    protected Bundle setStateBundle() {
        return this.mBundle;
    }

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        if (518 == msg.what && checkMemorySpace()) {
            return false;
        }
        return super.handleMessage(msg);
    }

    private boolean checkMemorySpace() {
        if (!MediaNotificationManager.getInstance().isMounted() || MediaNotificationManager.getInstance().getRemaining() != 1) {
            return false;
        }
        CautionUtilityClass.getInstance().requestTrigger(LightShaftInfo.CAUTION_ID_DLAPP_STORAGE_NO_SPACE);
        return true;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        this.mBundle = null;
        super.onDestroy();
    }

    /* loaded from: classes.dex */
    private static class MediaNotificationListener implements NotificationListener {
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
            CautionUtilityClass.getInstance().disapperTrigger(LightShaftInfo.CAUTION_ID_DLAPP_STORAGE_NO_SPACE);
        }
    }
}
