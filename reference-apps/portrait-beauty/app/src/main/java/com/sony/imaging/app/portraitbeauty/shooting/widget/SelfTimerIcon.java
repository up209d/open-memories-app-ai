package com.sony.imaging.app.portraitbeauty.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyBackUpKey;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyConstants;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil;
import com.sony.imaging.app.portraitbeauty.shooting.PortraitBeautyDisplayModeObserver;
import com.sony.imaging.app.portraitbeauty.shooting.PortraitBeautyEffectProcess;
import com.sony.imaging.app.portraitbeauty.shooting.PortraitBeautyExecutorCreater;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.avio.DisplayManager;

/* loaded from: classes.dex */
public class SelfTimerIcon extends ActiveImage {
    public static int i_dd_previous_display_mode_settings = 0;
    private final String TAG;
    int counter;
    int[] drawables;
    private int[] fiveSecondTimerIcons;
    private SelfTimerPriorityChangeListener mSelfTimerPriorityChangeListener;
    private int[] tenSecondTimerIcons;
    private int time;
    private int[] twoSecondTimerIcons;

    public SelfTimerIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = AppLog.getClassName();
        this.mSelfTimerPriorityChangeListener = null;
        this.time = 0;
        this.twoSecondTimerIcons = new int[]{R.drawable.p_16_dd_parts_portraitbeauty_2_2, R.drawable.p_16_dd_parts_portraitbeauty_2_1, R.drawable.p_16_dd_parts_portraitbeauty_2_1};
        this.fiveSecondTimerIcons = new int[]{R.drawable.p_16_dd_parts_portraitbeauty_5_5, R.drawable.p_16_dd_parts_portraitbeauty_5_4, R.drawable.p_16_dd_parts_portraitbeauty_5_3, R.drawable.p_16_dd_parts_portraitbeauty_5_2, R.drawable.p_16_dd_parts_portraitbeauty_5_1, R.drawable.p_16_dd_parts_portraitbeauty_5_1};
        this.tenSecondTimerIcons = new int[]{R.drawable.p_16_dd_parts_portraitbeauty_10_10, R.drawable.p_16_dd_parts_portraitbeauty_10_9, R.drawable.p_16_dd_parts_portraitbeauty_10_8, R.drawable.p_16_dd_parts_portraitbeauty_10_7, R.drawable.p_16_dd_parts_portraitbeauty_10_6, R.drawable.p_16_dd_parts_portraitbeauty_10_5, R.drawable.p_16_dd_parts_portraitbeauty_10_4, R.drawable.p_16_dd_parts_portraitbeauty_10_3, R.drawable.p_16_dd_parts_portraitbeauty_10_2, R.drawable.p_16_dd_parts_portraitbeauty_10_1, R.drawable.p_16_dd_parts_portraitbeauty_10_1};
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.counter = 0;
        this.mSelfTimerPriorityChangeListener = new SelfTimerPriorityChangeListener();
        this.mNotifier.setNotificationListener(this.mSelfTimerPriorityChangeListener);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        if (!PortraitBeautyExecutorCreater.isHalt) {
            AppLog.enter(this.TAG, AppLog.getMethodName());
            PortraitBeautyDisplayModeObserver displayObserver = PortraitBeautyDisplayModeObserver.getInstance();
            if (displayObserver != null) {
                DisplayManager.DeviceStatus deviceStatus = displayObserver.getActiveDeviceStatus();
                int panelViewPattern = deviceStatus == null ? 4 : deviceStatus.viewPattern;
                if (panelViewPattern != 1) {
                    setVisibility(4);
                }
            }
            if (PortraitBeautyEffectProcess.sIsCaptureStarted == PortraitBeautyEffectProcess.CAPTURE_DONE || PortraitBeautyEffectProcess.sIsCaptureStarted == PortraitBeautyEffectProcess.CAPTURE_NOT_STARTED || PortraitBeautyEffectProcess.sIsCaptureStarted == PortraitBeautyEffectProcess.CAPTURE_CANCELLED) {
                setVisibility(4);
            }
            AppLog.exit(this.TAG, AppLog.getMethodName());
        }
    }

    public void setIcons() {
        switch (PortraitBeautyEffectProcess.captureTime / 1000) {
            case 3:
                this.drawables = this.twoSecondTimerIcons;
                return;
            case 6:
                this.drawables = this.fiveSecondTimerIcons;
                return;
            case 11:
                this.drawables = this.tenSecondTimerIcons;
                return;
            default:
                this.drawables = this.twoSecondTimerIcons;
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class SelfTimerPriorityChangeListener implements NotificationListener {
        private String[] TAGS = {"selftimer", PortraitBeautyConstants.SELFTIMERUPDATEICONSETTINGTAG, PortraitBeautyConstants.SELFTIMERSETTINGFINISHTAG};

        SelfTimerPriorityChangeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if ("selftimer".equals(tag)) {
                SelfTimerIcon.this.setIcons();
                return;
            }
            if (PortraitBeautyConstants.SELFTIMERUPDATEICONSETTINGTAG.equals(tag)) {
                if (SelfTimerIcon.this.drawables != null && SelfTimerIcon.this.counter < SelfTimerIcon.this.drawables.length) {
                    SelfTimerIcon.this.counter = ((Integer) CameraNotificationManager.getInstance().getValue(tag)).intValue();
                    SelfTimerIcon.this.funct_OrientationChecker(SelfTimerIcon.this.drawables[SelfTimerIcon.this.counter]);
                    return;
                }
                return;
            }
            if (PortraitBeautyConstants.SELFTIMERSETTINGFINISHTAG.equals(tag)) {
                SelfTimerIcon.this.counter = 0;
                SelfTimerIcon.this.funct_FinisherCheck();
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mSelfTimerPriorityChangeListener == null) {
            this.mSelfTimerPriorityChangeListener = new SelfTimerPriorityChangeListener();
        }
        return this.mSelfTimerPriorityChangeListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void funct_FinisherCheck() {
        if (!PortraitBeautyExecutorCreater.isHalt) {
            if (this.mNotifier != null && this.mSelfTimerPriorityChangeListener != null) {
                this.mNotifier.removeNotificationListener(this.mSelfTimerPriorityChangeListener);
                this.mSelfTimerPriorityChangeListener = null;
            }
            setVisibility(4);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void funct_OrientationChecker(int i_drawable_id) {
        int device;
        PortraitBeautyDisplayModeObserver displayObserver = PortraitBeautyDisplayModeObserver.getInstance();
        if (displayObserver != null) {
            DisplayManager.DeviceStatus deviceStatus = displayObserver.getActiveDeviceStatus();
            int panelViewPattern = deviceStatus == null ? 4 : deviceStatus.viewPattern;
            if (panelViewPattern == 1) {
                AppLog.info(this.TAG, "########Inside 180");
                if (PortraitBeautyDisplayModeObserver.getInstance().getActiveDevice() == 0) {
                    if (displayObserver.getActiveDispMode(0) != 3) {
                        displayObserver.setDisplayMode(0, 3);
                    }
                    setVisibility(0);
                }
            } else {
                AppLog.info(this.TAG, "#######not Inside 180");
                if (!PortraitBeautyUtil.bIsAdjustModeGuide && ((device = PortraitBeautyDisplayModeObserver.getInstance().getActiveDevice()) == 0 || device == 2)) {
                    String backupDispMode = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_DISPLAY_MODE_NORMAL_SHOOTING_TIMER, "1");
                    if (Integer.parseInt(backupDispMode) != PortraitBeautyDisplayModeObserver.getInstance().getActiveDispMode(0)) {
                        PortraitBeautyDisplayModeObserver.getInstance().setDisplayMode(0, Integer.parseInt(backupDispMode));
                    }
                }
                setVisibility(4);
            }
        }
        setImageResource(i_drawable_id);
    }
}
