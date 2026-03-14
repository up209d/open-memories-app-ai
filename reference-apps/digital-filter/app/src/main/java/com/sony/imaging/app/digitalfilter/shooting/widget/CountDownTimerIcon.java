package com.sony.imaging.app.digitalfilter.shooting.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class CountDownTimerIcon extends ActiveImage {
    private static final int ONE_SECOND_DELAY = 1000;
    private static final String TAG = AppLog.getClassName();
    private static int mCount = 2;
    private OneSecondTimerRunnableTask mOneSecondTimerRunnableTask;
    private SelfTimerChangedListener mSelfTimerChangedListener;
    private Object mTimerLock;

    static /* synthetic */ int access$110() {
        int i = mCount;
        mCount = i - 1;
        return i;
    }

    public CountDownTimerIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mSelfTimerChangedListener = null;
        this.mOneSecondTimerRunnableTask = null;
        this.mTimerLock = new Object();
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        setImageResource(R.drawable.p_16_dd_parts_gf_timer_1sec_during_countdown);
        setVisibility(4);
    }

    /* loaded from: classes.dex */
    class SelfTimerChangedListener implements NotificationListener {
        private String[] TAGS = {GFConstants.START_SELFTIMER, GFConstants.UPDATED_COUNTER, GFConstants.CANCELTAKEPICTURE};

        SelfTimerChangedListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            AppLog.info(CountDownTimerIcon.TAG, "onNotify: " + tag);
            if (GFConstants.START_SELFTIMER.equalsIgnoreCase(tag)) {
                int unused = CountDownTimerIcon.mCount = 2;
                CountDownTimerIcon.this.setImageResource(R.drawable.p_16_dd_parts_gf_timer_2sec_during_countdown);
                CountDownTimerIcon.this.setVisibility(0);
                if (CountDownTimerIcon.this.mOneSecondTimerRunnableTask == null) {
                    CountDownTimerIcon.this.mOneSecondTimerRunnableTask = new OneSecondTimerRunnableTask();
                }
                CountDownTimerIcon.this.mOneSecondTimerRunnableTask.execute();
                return;
            }
            if (GFConstants.UPDATED_COUNTER.equalsIgnoreCase(tag)) {
                if (CountDownTimerIcon.mCount != 1) {
                    CountDownTimerIcon.this.terminateRunnableTask();
                    CountDownTimerIcon.this.setVisibility(4);
                    GFCommonUtil.getInstance().setDuringSelfTimer(false);
                    return;
                }
                CountDownTimerIcon.this.setImageResource(R.drawable.p_16_dd_parts_gf_timer_1sec_during_countdown);
                return;
            }
            if (GFConstants.CANCELTAKEPICTURE.equalsIgnoreCase(tag)) {
                CountDownTimerIcon.this.terminateRunnableTask();
                CountDownTimerIcon.this.setVisibility(4);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void terminateRunnableTask() {
        if (this.mOneSecondTimerRunnableTask != null) {
            this.mOneSecondTimerRunnableTask.removeCallbacks();
            this.mOneSecondTimerRunnableTask = null;
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mSelfTimerChangedListener == null) {
            this.mSelfTimerChangedListener = new SelfTimerChangedListener();
        }
        return this.mSelfTimerChangedListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class OneSecondTimerRunnableTask {
        private Handler mHandler;
        Runnable mRunnable;

        private OneSecondTimerRunnableTask() {
            this.mHandler = new Handler();
            this.mRunnable = new Runnable() { // from class: com.sony.imaging.app.digitalfilter.shooting.widget.CountDownTimerIcon.OneSecondTimerRunnableTask.1
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (CountDownTimerIcon.this.mTimerLock) {
                        CountDownTimerIcon.access$110();
                        if (CountDownTimerIcon.mCount >= 0) {
                            CameraNotificationManager.getInstance().requestNotify(GFConstants.UPDATED_COUNTER);
                            OneSecondTimerRunnableTask.this.mHandler.postDelayed(OneSecondTimerRunnableTask.this.mRunnable, 1000L);
                        }
                    }
                }
            };
        }

        public void execute() {
            synchronized (CountDownTimerIcon.this.mTimerLock) {
                this.mHandler.postDelayed(this.mRunnable, 1000L);
            }
        }

        public void removeCallbacks() {
            synchronized (CountDownTimerIcon.this.mTimerLock) {
                this.mHandler.removeCallbacks(this.mRunnable);
                this.mRunnable = null;
                this.mHandler = null;
            }
        }
    }
}
