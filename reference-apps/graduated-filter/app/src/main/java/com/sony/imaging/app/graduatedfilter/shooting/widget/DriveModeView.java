package com.sony.imaging.app.graduatedfilter.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFSelfTimerController;
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
            this.tags = new String[]{CameraNotificationManager.DRIVE_MODE, GFConstants.START_SELFTIMER, GFConstants.CANCELTAKEPICTURE};
        }

        @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener
        public String[] addTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener, com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (CameraNotificationManager.DRIVE_MODE.equals(tag) || GFConstants.START_SELFTIMER.equals(tag) || GFConstants.CANCELTAKEPICTURE.equals(tag)) {
                if (GFConstants.CANCELTAKEPICTURE.equals(tag)) {
                    GFCommonUtil.getInstance().setDuringSelfTimer(false);
                }
                DriveModeView.this.refresh();
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        int resId;
        String value = GFSelfTimerController.getInstance().getValue("drivemode");
        if (GFSelfTimerController.SELF_TIMER_ON.equalsIgnoreCase(value)) {
            if (GFCommonUtil.getInstance().duringSelfTimer()) {
                resId = 17305519;
            } else {
                resId = R.drawable.stat_sys_download_anim1;
            }
        } else {
            resId = com.sony.imaging.app.graduatedfilter.R.drawable.p_16_dd_parts_gf_timer_off;
        }
        setImageResource(resId);
    }
}
