package com.sony.imaging.app.portraitbeauty.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyBackUpKey;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyConstants;
import com.sony.imaging.app.portraitbeauty.menu.controller.SelfTimerIntervalPriorityController;
import com.sony.imaging.app.portraitbeauty.shooting.PortraitBeautyDisplayModeObserver;
import com.sony.imaging.app.portraitbeauty.shooting.PortraitBeautyEffectProcess;
import com.sony.imaging.app.portraitbeauty.shooting.PortraitBeautyExecutorCreater;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class TimerIndicatorDriveModeLiveViewWidget extends ActiveImage {
    private final String TAG;
    private TimerIndicatorDriveModeLiveViewWidgetChangeListener mTimerIndicatorDriveModeLiveViewWidgetChangeListener;
    private boolean o_isTimerRunning;
    String s_SelfTimerValue;

    public TimerIndicatorDriveModeLiveViewWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = AppLog.getClassName();
        this.mTimerIndicatorDriveModeLiveViewWidgetChangeListener = null;
        this.o_isTimerRunning = false;
        this.s_SelfTimerValue = null;
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage, android.widget.ImageView, android.view.View
    public void onDetachedFromWindow() {
        if (this.mNotifier != null && this.mTimerIndicatorDriveModeLiveViewWidgetChangeListener != null) {
            this.mNotifier.removeNotificationListener(this.mTimerIndicatorDriveModeLiveViewWidgetChangeListener);
            this.mTimerIndicatorDriveModeLiveViewWidgetChangeListener = null;
            this.s_SelfTimerValue = null;
        }
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage, android.widget.ImageView, android.view.View
    public void onAttachedToWindow() {
        this.mTimerIndicatorDriveModeLiveViewWidgetChangeListener = new TimerIndicatorDriveModeLiveViewWidgetChangeListener();
        this.mNotifier.setNotificationListener(this.mTimerIndicatorDriveModeLiveViewWidgetChangeListener);
        this.s_SelfTimerValue = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_SELF_TIMER, SelfTimerIntervalPriorityController.SELFTIMERON5);
        super.onAttachedToWindow();
        refresh();
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (PortraitBeautyExecutorCreater.isHalt) {
            this.o_isTimerRunning = false;
            onDetachedFromWindow();
        } else {
            setVisibility(0);
            if (PortraitBeautyEffectProcess.sIsCaptureStarted == PortraitBeautyEffectProcess.CAPTURE_DONE || PortraitBeautyEffectProcess.sIsCaptureStarted == PortraitBeautyEffectProcess.CAPTURE_NOT_STARTED || PortraitBeautyEffectProcess.sIsCaptureStarted == PortraitBeautyEffectProcess.CAPTURE_CANCELLED) {
                this.o_isTimerRunning = false;
            }
            if (this.s_SelfTimerValue == null) {
                this.s_SelfTimerValue = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_SELF_TIMER, SelfTimerIntervalPriorityController.SELFTIMERON5);
            }
            int i_device = PortraitBeautyDisplayModeObserver.getInstance().getActiveDevice();
            if (this.s_SelfTimerValue != null) {
                if (this.s_SelfTimerValue.equalsIgnoreCase(SelfTimerIntervalPriorityController.SELFTIMERON2)) {
                    if (i_device == 1) {
                        if (this.o_isTimerRunning) {
                            setImageResource(R.drawable.p_16_aa_parts_tm_timer_2sec_countdown);
                        } else {
                            setImageResource(R.drawable.p_16_aa_parts_tm_timer_2sec);
                        }
                    } else if (this.o_isTimerRunning) {
                        setImageResource(R.drawable.p_16_dd_parts_tm_timer_2sec_countdown);
                    } else {
                        setImageResource(R.drawable.p_16_dd_parts_tm_timer_2sec);
                    }
                } else if (this.s_SelfTimerValue != null) {
                    if (this.s_SelfTimerValue.equalsIgnoreCase(SelfTimerIntervalPriorityController.SELFTIMERON5)) {
                        if (i_device == 1) {
                            if (this.o_isTimerRunning) {
                                setImageResource(R.drawable.p_16_aa_parts_tm_timer_5sec_countdown);
                            } else {
                                setImageResource(R.drawable.p_16_aa_parts_tm_timer_5sec);
                            }
                        } else if (this.o_isTimerRunning) {
                            setImageResource(R.drawable.p_16_dd_parts_tm_timer_5sec_countdown);
                        } else {
                            setImageResource(R.drawable.p_16_dd_parts_tm_timer_5sec);
                        }
                    } else if (this.s_SelfTimerValue != null) {
                        if (this.s_SelfTimerValue.equalsIgnoreCase(SelfTimerIntervalPriorityController.SELFTIMERON10)) {
                            if (i_device == 1) {
                                if (this.o_isTimerRunning) {
                                    setImageResource(R.drawable.p_16_aa_parts_tm_timer_10sec_countdown);
                                } else {
                                    setImageResource(R.drawable.p_16_aa_parts_tm_timer_10sec);
                                }
                            } else if (this.o_isTimerRunning) {
                                setImageResource(R.drawable.p_16_dd_parts_tm_timer_10sec_countdown);
                            } else {
                                setImageResource(R.drawable.p_16_dd_parts_tm_timer_10sec);
                            }
                        } else if (this.s_SelfTimerValue != null) {
                            if (i_device == 1) {
                                setImageResource(R.drawable.p_16_aa_parts_tm_timer_off);
                            } else {
                                setImageResource(R.drawable.p_16_dd_parts_tm_timer_off);
                            }
                        }
                    }
                }
            }
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mTimerIndicatorDriveModeLiveViewWidgetChangeListener == null) {
            this.mTimerIndicatorDriveModeLiveViewWidgetChangeListener = new TimerIndicatorDriveModeLiveViewWidgetChangeListener();
        }
        return this.mTimerIndicatorDriveModeLiveViewWidgetChangeListener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class TimerIndicatorDriveModeLiveViewWidgetChangeListener implements NotificationListener {
        private String[] TAGS = {"selftimer", PortraitBeautyConstants.SELFTIMERUPDATEICONSETTINGTAG, PortraitBeautyConstants.SELFTIMERSETTINGFINISHTAG};

        TimerIndicatorDriveModeLiveViewWidgetChangeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (PortraitBeautyConstants.SELFTIMERUPDATEICONSETTINGTAG.equals(tag) || "selftimer".equals(tag)) {
                TimerIndicatorDriveModeLiveViewWidget.this.o_isTimerRunning = true;
                TimerIndicatorDriveModeLiveViewWidget.this.refresh();
            } else if (PortraitBeautyConstants.SELFTIMERSETTINGFINISHTAG.equals(tag)) {
                TimerIndicatorDriveModeLiveViewWidget.this.o_isTimerRunning = false;
                TimerIndicatorDriveModeLiveViewWidget.this.refresh();
            }
        }
    }
}
