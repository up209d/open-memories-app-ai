package com.sony.imaging.app.graduatedfilter.sa;

import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;
import java.util.Timer;
import java.util.TimerTask;

/* loaded from: classes.dex */
public class GFHistgramTask {
    private static final int UPDATE_PERIOD_BASE_SETTING = 500;
    private static final int UPDATE_PERIOD_FILTER_SETTING = 250;
    private static short[] mHistData;
    public static final String TAG = AppLog.getClassName();
    private static GFHistgramTask mInstance = null;
    private static int mHistReqID = 1;
    private static int mHistAckID = 1;
    private Timer mTimer = null;
    private PeriodicTask mPeriodicTask = null;

    private GFHistgramTask() {
    }

    public static GFHistgramTask getInstance() {
        if (mInstance == null) {
            mInstance = new GFHistgramTask();
        }
        return mInstance;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PeriodicTask extends TimerTask {
        private boolean mEndFlag;

        private PeriodicTask() {
            this.mEndFlag = false;
        }

        public void setEndFlag(boolean isEnd) {
            this.mEndFlag = isEnd;
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            if (!this.mEndFlag) {
                try {
                    int ackID = SFRSA.getInstance().getHistgramID();
                    if (ackID != GFHistgramTask.mHistAckID) {
                        int unused = GFHistgramTask.mHistAckID = ackID;
                        int unused2 = GFHistgramTask.mHistReqID = GFHistgramTask.mHistAckID + 1;
                        SFRSA.getInstance().setHistgramReqID(GFHistgramTask.mHistReqID);
                        short[] unused3 = GFHistgramTask.mHistData = SFRSA.getInstance().getHistgramData();
                        CameraNotificationManager.getInstance().requestNotify(GFConstants.HISTOGRAM_UPDATE, GFHistgramTask.mHistData);
                    }
                } catch (Exception e) {
                    AppLog.warning(GFHistgramTask.TAG, "SA is terminated. so HistGram cannot be updated.");
                    int unused4 = GFHistgramTask.mHistReqID = -1;
                    int unused5 = GFHistgramTask.mHistAckID = -1;
                    e.printStackTrace();
                }
            }
        }
    }

    public void startHistgramUpdating() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (SFRSA.getInstance().isAvailableHistgram()) {
            try {
                mHistAckID = SFRSA.getInstance().getHistgramID();
                mHistReqID = mHistAckID + 1;
                SFRSA.getInstance().setHistgramReqID(mHistReqID);
                mHistData = SFRSA.getInstance().getHistgramData();
                CameraNotificationManager.getInstance().requestNotify(GFConstants.HISTOGRAM_UPDATE, mHistData);
            } catch (Exception e) {
                AppLog.warning(TAG, "SA is terminated. so HistGram cannot be updated.");
                mHistReqID = -1;
                mHistAckID = -1;
                e.printStackTrace();
            }
            if (this.mTimer != null) {
                stopHistgramUpdating();
            }
            this.mTimer = new Timer();
            this.mPeriodicTask = new PeriodicTask();
            this.mPeriodicTask.setEndFlag(false);
            if (GFCommonUtil.getInstance().isFilterSetting()) {
                this.mTimer.scheduleAtFixedRate(this.mPeriodicTask, 0L, 250L);
            } else {
                this.mTimer.scheduleAtFixedRate(this.mPeriodicTask, 0L, 500L);
            }
            AppLog.exit(TAG, AppLog.getMethodName());
        }
    }

    public void stopHistgramUpdating() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mPeriodicTask != null) {
            this.mPeriodicTask.setEndFlag(true);
            this.mPeriodicTask.cancel();
            this.mPeriodicTask = null;
        }
        if (this.mTimer != null) {
            this.mTimer.cancel();
            this.mTimer.purge();
            this.mTimer = null;
        }
        mHistData = null;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public boolean isHistgramUpdatingStarted() {
        return this.mTimer != null;
    }

    public void setEnableUpdating(boolean enable) {
        if (this.mPeriodicTask != null) {
            this.mPeriodicTask.setEndFlag(!enable);
        }
    }
}
