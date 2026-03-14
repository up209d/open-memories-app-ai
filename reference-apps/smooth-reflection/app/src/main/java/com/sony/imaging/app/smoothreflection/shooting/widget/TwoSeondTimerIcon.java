package com.sony.imaging.app.smoothreflection.shooting.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.smoothreflection.R;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.shooting.SmoothReflectionCompositProcess;
import com.sony.imaging.app.smoothreflection.shooting.keyhandler.SmoothReflectionCaptureStateKeyHandler;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class TwoSeondTimerIcon extends ActiveImage {
    private final int CAPTURE_DELAY_TIME;
    private final String TAG;
    private OneSecondTimerRunnableTask mOneSecondTimerRunnableTask;
    private SelfTimerPriorityChangeListener mSelfTimerPriorityChangeListener;
    private TwoSecondTimerRunnableTask mTwoSecondTimerRunnableTask;

    public TwoSeondTimerIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = AppLog.getClassName();
        this.CAPTURE_DELAY_TIME = 1000;
        this.mSelfTimerPriorityChangeListener = null;
        this.mOneSecondTimerRunnableTask = null;
        this.mTwoSecondTimerRunnableTask = null;
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        setImageResource(R.drawable.p_16_dd_parts_smooth_timer_1sec_during_countdown);
        setVisibility(4);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    /* loaded from: classes.dex */
    class SelfTimerPriorityChangeListener implements NotificationListener {
        private String[] TAGS = {SmoothReflectionCompositProcess.SELFTIMERSETTINGTAG, SmoothReflectionCaptureStateKeyHandler.CANCELTAKEPICTURETAG};

        SelfTimerPriorityChangeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (tag.equals(SmoothReflectionCompositProcess.SELFTIMERSETTINGTAG)) {
                TwoSeondTimerIcon.this.setVisibility(0);
                TwoSeondTimerIcon.this.setImageResource(R.drawable.p_16_dd_parts_smooth_timer_2sec_during_countdown);
                if (TwoSeondTimerIcon.this.mTwoSecondTimerRunnableTask == null) {
                    TwoSeondTimerIcon.this.mOneSecondTimerRunnableTask = new OneSecondTimerRunnableTask();
                }
                TwoSeondTimerIcon.this.mOneSecondTimerRunnableTask.execute();
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

    /* loaded from: classes.dex */
    private class OneSecondTimerRunnableTask {
        private Handler mHandler;
        Runnable mRunnable;

        private OneSecondTimerRunnableTask() {
            this.mHandler = new Handler();
            this.mRunnable = new Runnable() { // from class: com.sony.imaging.app.smoothreflection.shooting.widget.TwoSeondTimerIcon.OneSecondTimerRunnableTask.1
                @Override // java.lang.Runnable
                public void run() {
                    TwoSeondTimerIcon.this.setImageResource(R.drawable.p_16_dd_parts_smooth_timer_1sec_during_countdown);
                    if (TwoSeondTimerIcon.this.mTwoSecondTimerRunnableTask == null) {
                        TwoSeondTimerIcon.this.mTwoSecondTimerRunnableTask = new TwoSecondTimerRunnableTask();
                    }
                    TwoSeondTimerIcon.this.mTwoSecondTimerRunnableTask.execute();
                }
            };
        }

        public void execute() {
            this.mHandler.postDelayed(this.mRunnable, 1000L);
        }
    }

    /* loaded from: classes.dex */
    private class TwoSecondTimerRunnableTask {
        private Handler mHandler;
        Runnable mRunnable;

        private TwoSecondTimerRunnableTask() {
            this.mHandler = new Handler();
            this.mRunnable = new Runnable() { // from class: com.sony.imaging.app.smoothreflection.shooting.widget.TwoSeondTimerIcon.TwoSecondTimerRunnableTask.1
                @Override // java.lang.Runnable
                public void run() {
                    TwoSeondTimerIcon.this.setVisibility(4);
                }
            };
        }

        public void execute() {
            this.mHandler.postDelayed(this.mRunnable, 1000L);
        }
    }
}
