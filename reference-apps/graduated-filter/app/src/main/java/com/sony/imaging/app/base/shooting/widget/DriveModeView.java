package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import java.util.List;

/* loaded from: classes.dex */
public class DriveModeView extends ActiveImage {
    protected final DisplayModeObserver mDisplayObserver;
    NotificationListener mNotificationListener;

    public DriveModeView(Context context) {
        this(context, null);
    }

    public DriveModeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mNotificationListener = null;
        this.mDisplayObserver = DisplayModeObserver.getInstance();
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected boolean isVisible() {
        List<String> supported = DriveModeController.getInstance().getSupportedValue("drivemode");
        return (supported == null || supported.size() <= 0 || DriveModeController.getInstance().isUnavailableSceneFactor("drivemode")) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    public NotificationListener getNotificationListener() {
        if (this.mNotificationListener == null) {
            this.mNotificationListener = new NotificationListener();
        }
        return this.mNotificationListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class NotificationListener extends ActiveImage.ActiveImageListener {
        private final String[] tags;

        private NotificationListener() {
            super();
            this.tags = new String[]{CameraNotificationManager.DRIVE_MODE, CameraNotificationManager.SELFTIMER_COUNTDOWN_STATUS};
        }

        @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener
        public String[] addTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener, com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (CameraNotificationManager.DRIVE_MODE.equals(tag) || CameraNotificationManager.SELFTIMER_COUNTDOWN_STATUS.equals(tag)) {
                DriveModeView.this.refresh();
            } else {
                super.onNotify(tag);
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        int resId;
        String value = DriveModeController.getInstance().getValue();
        int device = this.mDisplayObserver.getActiveDevice();
        if (device != 1) {
            resId = getIconResourceId_osd(value);
        } else {
            resId = getIconResourceId_evf(value);
        }
        setImageResource(resId);
    }

    private int getIconResourceId_osd(String value) {
        boolean isCountdown = isCountDown();
        if (value.equals(DriveModeController.SINGLE)) {
            return R.drawable.stat_sys_battery_charge_anim85;
        }
        if (value.equals(DriveModeController.BURST)) {
            return 17305577;
        }
        if (value.equals(DriveModeController.BURST_SPEED_HIGH)) {
            return R.drawable.stat_sys_certificate_info;
        }
        if (value.equals(DriveModeController.BURST_SPEED_MID)) {
            return 17305784;
        }
        if (value.equals("low")) {
            return R.drawable.stat_sys_data_wimax_signal_3_fully;
        }
        if (value.equals(DriveModeController.SELF_TIMER_10S)) {
            if (isCountdown) {
                return 17305517;
            }
            return R.drawable.stat_sys_download_anim0;
        }
        if (value.equals(DriveModeController.SELF_TIMER_2S)) {
            if (isCountdown) {
                return 17305519;
            }
            return R.drawable.stat_sys_download_anim1;
        }
        if (value.equals(DriveModeController.SELF_TIMER_BURST_10S_3SHOT)) {
            if (isCountdown) {
                return 17305943;
            }
            return 17305676;
        }
        if (value.equals(DriveModeController.SELF_TIMER_BURST_10S_5SHOT)) {
            if (isCountdown) {
                return 17305953;
            }
            return 17305678;
        }
        if (value.equals(DriveModeController.SPEED_PRIORITY_BURST)) {
            return R.drawable.textfield_search_right_selected_holo_light;
        }
        return R.drawable.stat_sys_battery_charge_anim85;
    }

    private int getIconResourceId_evf(String value) {
        boolean isCountdown = isCountDown();
        if (value.equals(DriveModeController.SINGLE)) {
            return 17303872;
        }
        if (value.equals(DriveModeController.BURST)) {
            return 17306195;
        }
        if (value.equals(DriveModeController.BURST_SPEED_HIGH)) {
            return 17305469;
        }
        if (value.equals(DriveModeController.BURST_SPEED_MID)) {
            return 17304544;
        }
        if (value.equals("low")) {
            return 17303860;
        }
        if (value.equals(DriveModeController.SELF_TIMER_10S)) {
            if (isCountdown) {
                return 17305544;
            }
            return 17305464;
        }
        if (value.equals(DriveModeController.SELF_TIMER_2S)) {
            if (isCountdown) {
                return 17303914;
            }
            return R.drawable.textfield_search_empty_selected;
        }
        if (value.equals(DriveModeController.SELF_TIMER_BURST_10S_3SHOT)) {
            if (isCountdown) {
                return 17306265;
            }
            return 17306158;
        }
        if (value.equals(DriveModeController.SELF_TIMER_BURST_10S_5SHOT)) {
            if (isCountdown) {
                return 17306262;
            }
            return 17306174;
        }
        if (value.equals(DriveModeController.SPEED_PRIORITY_BURST)) {
            return 17306112;
        }
        return 17303872;
    }

    private boolean isCountDown() {
        Boolean status = (Boolean) CameraNotificationManager.getInstance().getValue(CameraNotificationManager.SELFTIMER_COUNTDOWN_STATUS);
        if (status == null) {
            return false;
        }
        boolean ret = status.booleanValue();
        return ret;
    }
}
