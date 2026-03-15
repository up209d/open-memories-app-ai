package com.sony.imaging.app.lightgraffiti.shooting.timer;

import android.util.Log;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGSelfTimerController;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.scalar.hardware.indicator.Light;
import java.util.Timer;
import java.util.TimerTask;

/* loaded from: classes.dex */
public class LGSelfTimerThread {
    public static final String TAG = LGSelfTimerThread.class.getSimpleName();
    private static LGSelfTimerThread sLGSelfTimerThread = null;
    private Object mTimerLock = new Object();
    private Timer mTimer = null;
    private PeriodicTask mPeriodicTask = null;
    private String mSelftimerLong = null;
    private int mCounter = 0;

    static /* synthetic */ int access$108(LGSelfTimerThread x0) {
        int i = x0.mCounter;
        x0.mCounter = i + 1;
        return i;
    }

    private LGSelfTimerThread() {
    }

    public static LGSelfTimerThread getInstance() {
        if (sLGSelfTimerThread == null) {
            sLGSelfTimerThread = new LGSelfTimerThread();
        }
        return sLGSelfTimerThread;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PeriodicTask extends TimerTask {
        private PeriodicTask() {
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            synchronized (LGSelfTimerThread.this.mTimerLock) {
                LGSelfTimerThread.access$108(LGSelfTimerThread.this);
                switch (LGSelfTimerThread.this.mCounter) {
                    case 4:
                        Light.setState("LID_SELF_TIMER", true);
                        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELFTIMER);
                        break;
                    case 5:
                        Light.setState("LID_SELF_TIMER", false);
                        break;
                    case 8:
                        Light.setState("LID_SELF_TIMER", true);
                        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELFTIMER);
                        break;
                    case 9:
                        Light.setState("LID_SELF_TIMER", false);
                        break;
                    case 12:
                        Light.setState("LID_SELF_TIMER", true);
                        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELFTIMER);
                        break;
                    case 13:
                        Light.setState("LID_SELF_TIMER", false);
                        break;
                    case 16:
                        Light.setState("LID_SELF_TIMER", true);
                        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELFTIMER);
                        break;
                    case 17:
                        Light.setState("LID_SELF_TIMER", false);
                        break;
                    case 20:
                        Light.setState("LID_SELF_TIMER", true);
                        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELFTIMER);
                        break;
                    case 21:
                        Light.setState("LID_SELF_TIMER", false);
                        break;
                    case 24:
                        Light.setState("LID_SELF_TIMER", true);
                        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELFTIMER);
                        break;
                    case 25:
                        Light.setState("LID_SELF_TIMER", false);
                        break;
                    case 26:
                        if (!LGSelfTimerThread.this.mSelftimerLong.equals(LGSelfTimerController.SELC_TIMER_EXPOSING_FINISH)) {
                            Light.setState("LID_SELF_TIMER", true);
                        }
                        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELFTIMER);
                        break;
                    case 27:
                        if (!LGSelfTimerThread.this.mSelftimerLong.equals(LGSelfTimerController.SELC_TIMER_EXPOSING_FINISH)) {
                            Light.setState("LID_SELF_TIMER", false);
                        }
                        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELFTIMER);
                        break;
                    case 28:
                        if (!LGSelfTimerThread.this.mSelftimerLong.equals(LGSelfTimerController.SELC_TIMER_EXPOSING_FINISH)) {
                            Light.setState("LID_SELF_TIMER", true);
                        }
                        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELFTIMER);
                        break;
                    case 29:
                        if (!LGSelfTimerThread.this.mSelftimerLong.equals(LGSelfTimerController.SELC_TIMER_EXPOSING_FINISH)) {
                            Light.setState("LID_SELF_TIMER", false);
                        }
                        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELFTIMER);
                        break;
                    case 30:
                        if (!LGSelfTimerThread.this.mSelftimerLong.equals(LGSelfTimerController.SELC_TIMER_EXPOSING_FINISH)) {
                            Light.setState("LID_SELF_TIMER", true);
                        }
                        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELFTIMER);
                        break;
                    case 31:
                        if (!LGSelfTimerThread.this.mSelftimerLong.equals(LGSelfTimerController.SELC_TIMER_EXPOSING_FINISH)) {
                            Light.setState("LID_SELF_TIMER", false);
                        }
                        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELFTIMER);
                        break;
                    case 32:
                        if (!LGSelfTimerThread.this.mSelftimerLong.equals(LGSelfTimerController.SELC_TIMER_EXPOSING_FINISH)) {
                            Light.setState("LID_SELF_TIMER", true);
                        }
                        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELFTIMER);
                        break;
                    case 33:
                        if (!LGSelfTimerThread.this.mSelftimerLong.equals(LGSelfTimerController.SELC_TIMER_EXPOSING_FINISH)) {
                            Light.setState("LID_SELF_TIMER", false);
                            break;
                        }
                        break;
                }
            }
        }
    }

    public void startSelftimer(String selftimerLong) {
        synchronized (this.mTimerLock) {
            this.mSelftimerLong = selftimerLong;
            if (this.mTimer != null) {
                stopSelftimer();
            }
            this.mTimer = new Timer();
            this.mPeriodicTask = new PeriodicTask();
            if (this.mSelftimerLong.equals(LGSelfTimerController.SELF_TIMER_10)) {
                this.mCounter = 0;
                this.mTimer.scheduleAtFixedRate(this.mPeriodicTask, 312L, 312L);
            } else if (this.mSelftimerLong.equals(LGSelfTimerController.SELF_TIMER_2)) {
                this.mCounter = 25;
                this.mTimer.scheduleAtFixedRate(this.mPeriodicTask, 624L, 312L);
            } else if (this.mSelftimerLong.equals(LGSelfTimerController.SELC_TIMER_EXPOSING_FINISH)) {
                this.mCounter = 26;
                this.mTimer.scheduleAtFixedRate(this.mPeriodicTask, 0L, 312L);
            }
        }
    }

    public void stopSelftimer() {
        synchronized (this.mTimerLock) {
            if (this.mPeriodicTask != null) {
                Log.d(TAG, AppLog.getMethodName() + " periodicTask cancel");
                this.mPeriodicTask.cancel();
                this.mPeriodicTask = null;
            }
            if (this.mTimer != null) {
                Log.d(TAG, AppLog.getMethodName() + " timer cancel, timer purge");
                this.mTimer.cancel();
                this.mTimer.purge();
                this.mTimer = null;
            }
            LGSelfTimerController.getInstance().unsetOneTimeTimer();
        }
    }
}
