package com.sony.imaging.app.lightgraffiti.shooting.timer;

import android.os.Handler;
import android.util.Log;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGAppTopController;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/* loaded from: classes.dex */
public class LGTimerThread {
    private static LGTimerThread mInstance = null;
    public static int EXPOSING_TIME_5 = 5000;
    public static int EXPOSING_TIME_10 = 10000;
    public static int EXPOSING_TIME_20 = 20000;
    public static int EXPOSING_TIME_30 = 32000;
    private final String TAG = LGTimerThread.class.getSimpleName();
    private Object mTimerLock = new Object();
    private Timer mTimer = null;
    private PeriodicTask mPeriodicTask = null;
    private Handler mHandlerMainThread = null;
    private ILGTimerCallback mCallbackInstance = null;
    private int mExposingTime = 0;
    private long mExposeStartedDatetime = 0;

    /* loaded from: classes.dex */
    public interface ILGTimerCallback {
        void onPeriodic(int i);
    }

    public static LGTimerThread getInstance() {
        if (mInstance == null) {
            mInstance = new LGTimerThread();
        }
        return mInstance;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PeriodicTask extends TimerTask {
        private int mTimerCount;

        private PeriodicTask() {
            this.mTimerCount = 0;
        }

        /* loaded from: classes.dex */
        private class CallbackRunnable implements Runnable {
            private int mCount;

            public CallbackRunnable(int count) {
                this.mCount = count;
            }

            @Override // java.lang.Runnable
            public void run() {
                LGTimerThread.this.mCallbackInstance.onPeriodic(this.mCount);
            }
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            synchronized (LGTimerThread.this.mTimerLock) {
                if (LGTimerThread.this.mHandlerMainThread != null) {
                    CallbackRunnable callbackRunnable = new CallbackRunnable(this.mTimerCount);
                    LGTimerThread.this.mHandlerMainThread.post(callbackRunnable);
                }
                this.mTimerCount++;
                Date current_datetime = new Date(System.currentTimeMillis());
                long passage = current_datetime.getTime() - LGTimerThread.this.mExposeStartedDatetime;
                Log.d(LGTimerThread.this.TAG, Long.toString(passage));
                if (LGTimerThread.this.mExposingTime + 1000 <= passage) {
                    LGTimerThread.this.timerStop();
                    LGStateHolder.getInstance().setShootingStageNext();
                }
            }
        }
    }

    public void setHandler() {
        synchronized (this.mTimerLock) {
            this.mHandlerMainThread = new Handler();
        }
    }

    public void timerStart(String value, ILGTimerCallback cb) {
        synchronized (this.mTimerLock) {
            if (this.mTimer != null) {
                timerStop();
            }
            if (value.equals(LGAppTopController.DURATION_TIME_10)) {
                this.mExposingTime = EXPOSING_TIME_10;
            } else if (value.equals(LGAppTopController.DURATION_TIME_20)) {
                this.mExposingTime = EXPOSING_TIME_20;
            } else if (value.equals(LGAppTopController.DURATION_TIME_30)) {
                this.mExposingTime = EXPOSING_TIME_30;
            } else {
                this.mExposingTime = EXPOSING_TIME_5;
            }
            Date current_datetime = new Date(System.currentTimeMillis());
            this.mExposeStartedDatetime = current_datetime.getTime();
            this.mCallbackInstance = cb;
            this.mTimer = new Timer();
            this.mPeriodicTask = new PeriodicTask();
            this.mTimer.scheduleAtFixedRate(this.mPeriodicTask, 1000L, 1000L);
        }
    }

    public void timerStop() {
        synchronized (this.mTimerLock) {
            if (this.mPeriodicTask != null) {
                Log.d(this.TAG, AppLog.getMethodName() + " periodicTask cancel");
                this.mPeriodicTask.cancel();
                this.mPeriodicTask = null;
            }
            if (this.mTimer != null) {
                Log.d(this.TAG, AppLog.getMethodName() + " timer cancel, timer purge");
                this.mTimer.cancel();
                this.mTimer.purge();
                this.mTimer = null;
            }
        }
    }
}
