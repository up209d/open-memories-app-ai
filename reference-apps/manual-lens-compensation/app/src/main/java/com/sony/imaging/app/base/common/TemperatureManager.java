package com.sony.imaging.app.base.common;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.movie.MovieShootingExecutor;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.sysutil.didep.ScalarSystemManager;
import com.sony.scalar.sysutil.didep.Temperature;

/* loaded from: classes.dex */
public class TemperatureManager {
    public static final int API_VERSION_COUNTDOWN_SUPPORTED = 16;
    private static final String COUNTDOWN_FINISH = "Heat count down finish. requestPowerOff.";
    private static final int COUNTDOWN_POWEROFF_SUPPORT_PFAPI_VERSION = 16;
    private static final String COUNTDOWN_START = "Heat count down start.";
    private static final String COUNTDOWN_STOP = "Heat count down stop";
    private static final String TAG = "TemperatureManager";
    private static Temperature.CountDownInfo mCountDownInfo;
    private static long mCountDownStartTime;
    private static TemperatureManager mInstance;
    private static Temperature mTemp = null;
    private TemperatureCountDooInfoChangedListener mTemperatureCountDooInfoChangedListener = null;
    private RecStatusChangedListener mRecStatusNotificationListener = null;
    private Handler countDownHandler = new Handler(Looper.getMainLooper());
    private Runnable countDownRunnable = new Runnable() { // from class: com.sony.imaging.app.base.common.TemperatureManager.1
        @Override // java.lang.Runnable
        public void run() {
            if (16 <= Environment.getVersionPfAPI()) {
                Log.i(TemperatureManager.TAG, TemperatureManager.COUNTDOWN_FINISH);
                ScalarSystemManager.requestPowerOff(32, 1, 1);
            }
        }
    };
    private Temperature.StatusCallback tempCallback = new Temperature.StatusCallback() { // from class: com.sony.imaging.app.base.common.TemperatureManager.2
        public void onTempStatusChanged() {
            TemperatureNotificationManager.getInstance().requestNotify(TemperatureNotificationManager.TEMP_STATUS_CHANGED);
        }
    };

    public static TemperatureManager getInstance() {
        if (mInstance == null) {
            mInstance = new TemperatureManager();
        }
        return mInstance;
    }

    public void resume(Context c) {
        mTemp = new Temperature();
        mTemp.setStatusCallback(this.tempCallback);
        if (isTempCounDownSupportedPF()) {
            this.mTemperatureCountDooInfoChangedListener = new TemperatureCountDooInfoChangedListener();
            mTemp.setCountDownInfoListener(this.mTemperatureCountDooInfoChangedListener);
            this.mRecStatusNotificationListener = new RecStatusChangedListener();
            CameraNotificationManager.getInstance().setNotificationListener(this.mRecStatusNotificationListener);
        }
    }

    public void pause(Context c) {
        if (this.countDownHandler != null && this.countDownRunnable != null) {
            this.countDownHandler.removeCallbacks(this.countDownRunnable);
        }
        if (isTempCounDownSupportedPF()) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mRecStatusNotificationListener);
            this.mRecStatusNotificationListener = null;
            mTemp.setCountDownInfoListener((Temperature.CountDownInfoListener) null);
            this.mTemperatureCountDooInfoChangedListener = null;
        }
        mTemp.setStatusCallback((Temperature.StatusCallback) null);
        mTemp.release();
        mTemp = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void countDownCheck() {
        if (1 != CameraSetting.getInstance().getCurrentMode()) {
            boolean isMovieRecording = MovieShootingExecutor.isMovieRecording();
            Temperature.CountDownInfo info = getCountDownInfo();
            boolean enabled = info.enabled;
            if (enabled && isMovieRecording) {
                Log.i(TAG, COUNTDOWN_START);
                mCountDownStartTime = System.currentTimeMillis();
                this.countDownHandler.postDelayed(this.countDownRunnable, info.countDownSeconds * 1000);
                TemperatureNotificationManager.getInstance().requestNotify(TemperatureNotificationManager.TEMP_COUNTDOWN_START);
                return;
            }
            if (this.countDownHandler.hasMessages(0)) {
                this.countDownHandler.removeCallbacks(this.countDownRunnable);
                TemperatureNotificationManager.getInstance().requestNotify(TemperatureNotificationManager.TEMP_COUNTDOWN_STOP);
                Log.i(TAG, COUNTDOWN_STOP);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class TemperatureCountDooInfoChangedListener implements Temperature.CountDownInfoListener {
        private TemperatureCountDooInfoChangedListener() {
        }

        public void onChanged(Temperature.CountDownInfo info) {
            Temperature.CountDownInfo unused = TemperatureManager.mCountDownInfo = info;
            TemperatureNotificationManager.getInstance().requestNotify(TemperatureNotificationManager.TEMP_COUNTDOWN_INFO_CHANGED);
            TemperatureManager.this.countDownCheck();
        }
    }

    public Temperature.CountDownInfo getCountDownInfo() {
        return mCountDownInfo;
    }

    public Temperature.Status getTemperatureStatus() {
        if (mTemp != null) {
            return mTemp.getStatus();
        }
        Log.w("TemperatureNotificationManager", "Temperature status is null !!");
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RecStatusChangedListener implements NotificationListener {
        private final String[] tags;

        private RecStatusChangedListener() {
            this.tags = new String[]{CameraNotificationManager.MOVIE_REC_START_SUCCEEDED, CameraNotificationManager.MOVIE_REC_START_FAILED, CameraNotificationManager.MOVIE_REC_STOP};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            TemperatureManager.this.countDownCheck();
        }
    }

    public static boolean isTempCounDownSupportedPF() {
        if (16 > Environment.getVersionPfAPI()) {
            return false;
        }
        return true;
    }

    public long getCountDownTime() {
        long mCountDownTime = (mCountDownStartTime + (mCountDownInfo.countDownSeconds * 1000)) - System.currentTimeMillis();
        return mCountDownTime;
    }

    public boolean isOverheating() {
        boolean ret = false;
        Temperature.Status tempStatus = getInstance().getTemperatureStatus();
        int battStatus = tempStatus.get(1);
        int boxStatus = tempStatus.get(2);
        int tempFactor = tempStatus.getTempInhFactors();
        if (boxStatus == 3 || boxStatus == 2) {
            ret = true;
        }
        if (battStatus == 3 || battStatus == 2) {
            ret = true;
        }
        if (tempFactor == 1 || tempFactor == 2) {
            ret = true;
        }
        Temperature.CountDownInfo info = getInstance().getCountDownInfo();
        if (info != null && info.enabled) {
            return true;
        }
        return ret;
    }
}
