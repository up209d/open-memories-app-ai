package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.scalar.hardware.CameraEx;
import java.util.Timer;
import java.util.TimerTask;

/* loaded from: classes.dex */
public class ForceSetting {
    private static final String LOG_FORCE_SETTING_FAIL = "ForceSetting fail. the factor is ";
    private static final String LOG_FORCE_SETTING_SUCCESS = "ForceSetting success";
    private static final int REPEAT_TIME = 1000;
    private static final String TAG = "ForceSetting";
    private static FinishCallback callback = null;
    private static Pair<Camera.Parameters, CameraEx.ParametersModifier> params = null;
    private static Handler handler = new Handler(Looper.getMainLooper());
    private static Timer timer = null;
    private static ForceSettingRunnable mForceSettingRunnable = new ForceSettingRunnable();

    /* loaded from: classes.dex */
    public interface FinishCallback {
        void onCallback();
    }

    public static void exec(Pair<Camera.Parameters, CameraEx.ParametersModifier> params2) {
        if (timer != null) {
            throw new RuntimeException("ForceSetting.exec() already called.");
        }
        params = params2;
        ((CameraEx.ParametersModifier) params2.second).tryIgnoreInhibit();
        myExec();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void myExec() {
        try {
            CameraSetting.getInstance().setParameters(params);
            Log.i(TAG, LOG_FORCE_SETTING_SUCCESS);
            CautionUtilityClass.getInstance().bootFailedFactorDisapper();
            timer = null;
            if (callback != null) {
                callback.onCallback();
            }
        } catch (Exception e) {
            int failFactor = ExecutorCreator.getInstance().getSequence().getCameraEx().getInhibitionInfo();
            Log.i(TAG, LOG_FORCE_SETTING_FAIL + failFactor);
            CautionUtilityClass.getInstance().bootFailFactor(failFactor);
            timer = new Timer();
            timer.schedule(new ForceSettingTimerTask(), 1000L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ForceSettingTimerTask extends TimerTask {
        private ForceSettingTimerTask() {
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            ForceSetting.handler.post(ForceSetting.mForceSettingRunnable);
        }
    }

    /* loaded from: classes.dex */
    private static class ForceSettingRunnable implements Runnable {
        private ForceSettingRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            ForceSetting.myExec();
        }
    }

    public static void cancel() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public static void setFinishCallback(FinishCallback callback2) {
        callback = callback2;
    }
}
