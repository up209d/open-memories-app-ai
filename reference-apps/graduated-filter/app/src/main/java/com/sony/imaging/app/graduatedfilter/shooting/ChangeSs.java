package com.sony.imaging.app.graduatedfilter.shooting;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.executor.IntervalRecExecutor;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class ChangeSs {
    private Callback mCallback = null;
    private ShutterSpeedChangeListener mChangeSsNotification;
    private static String TAG = AppLog.getClassName();
    public static int[] SS_array_numerator_OneThirdStep = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 10, 1, 10, 4, 1, 13, 16, 2, 25, 16, 4, 5, 6, 8, 10, 13, 15, 20, 25, 30, Info.INVALID_CAUTION_ID};
    public static int[] SS_array_denominator_OneThirdStep = {99999, 32000, 25600, 20000, 16000, 12800, 10000, GFConstants.SHOOTING_GUIDE_DISP_TIME, 6400, 5000, 4000, 3200, 2500, 2000, 1600, 1250, 1000, 800, AppRoot.USER_KEYCODE.WATER_HOUSING, 500, 400, 320, 250, IntervalRecExecutor.INTVL_REC_INITIALIZED, 160, 125, 100, 80, 60, 50, 40, 30, 25, 20, 15, 13, 10, 8, 6, 5, 4, 3, 25, 2, 16, 5, 1, 10, 10, 1, 10, 5, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    public static int mTargetSs_numerator = 1;
    public static int mTargetSs_denominator = GFConstants.SHOOTING_GUIDE_DISP_TIME;
    private static int mRetryCounter = 10;

    /* loaded from: classes.dex */
    public interface Callback {
        void cbFunction();
    }

    static /* synthetic */ int access$310() {
        int i = mRetryCounter;
        mRetryCounter = i - 1;
        return i;
    }

    public ChangeSs() {
        if (this.mChangeSsNotification == null) {
            this.mChangeSsNotification = new ShutterSpeedChangeListener();
        }
    }

    public void execute(int SS_adjustment, int sS_numerator, int sS_denominator, Callback callBack) {
        AppLog.info(TAG, "execute. target SS:" + sS_numerator + "/" + sS_denominator);
        try {
            Pair<Integer, Integer> ss = getCurrentSsFromPF();
            if (ss == null) {
                AppLog.info(TAG, "could not get current SS value ");
                if (this.mChangeSsNotification != null) {
                    CameraNotificationManager.getInstance().removeNotificationListener(this.mChangeSsNotification);
                }
                callBack.cbFunction();
                return;
            }
            AppLog.info(TAG, "  current SS:" + ss.first + "/" + ss.second);
            if (SS_adjustment == 0) {
                AppLog.info(TAG, "no change needed. ");
                callBack.cbFunction();
                return;
            }
            this.mCallback = callBack;
            mRetryCounter = 3;
            if (this.mChangeSsNotification != null) {
                CameraNotificationManager.getInstance().removeNotificationListener(this.mChangeSsNotification);
            }
            CameraNotificationManager.getInstance().setNotificationListener(this.mChangeSsNotification);
            AppLog.info(TAG, "call SS change.  SS shift:" + SS_adjustment);
            CameraSetting.getInstance().getCamera().adjustShutterSpeed(-SS_adjustment);
            mTargetSs_numerator = sS_numerator;
            mTargetSs_denominator = sS_denominator;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static void executeWithoutCallBack(int sS_numerator, int sS_denominator) {
        AppLog.info(TAG, "executeWithoutCallBack. target SS:" + sS_numerator + "/" + sS_denominator);
        Pair<Integer, Integer> ss = getCurrentSsFromPF();
        if (ss == null) {
            AppLog.info(TAG, "could not get current SS value ");
            return;
        }
        AppLog.info(TAG, "  current SS:" + ss.first + "/" + ss.second);
        int step = getSsAdjustmentStep(((Integer) ss.first).intValue(), ((Integer) ss.second).intValue(), sS_numerator, sS_denominator);
        if (step == 0) {
            AppLog.info(TAG, "no change needed. ");
        } else {
            CameraSetting.getInstance().getCamera().adjustShutterSpeed(-step);
        }
    }

    public static int getSsAdjustmentStep(int sS_from__numerator, int sS_from__denominator, int sS_to__numerator, int sS_to__denominator) {
        int sS_milisec_from__step_fromBulb = 1000;
        int sS_milisec_to__step_fromBulb = 0;
        int i1 = 0;
        while (true) {
            if (i1 < SS_array_numerator_OneThirdStep.length) {
                if (SS_array_numerator_OneThirdStep[i1] != sS_from__numerator || SS_array_denominator_OneThirdStep[i1] != sS_from__denominator) {
                    i1++;
                } else {
                    sS_milisec_from__step_fromBulb = i1;
                    Log.d(TAG, "sS_milisec_from__step_fromBulb:" + sS_milisec_from__step_fromBulb);
                    break;
                }
            } else {
                break;
            }
        }
        if (sS_from__numerator == 65535 && sS_from__denominator == 1) {
            sS_milisec_from__step_fromBulb--;
        }
        int i12 = 0;
        while (true) {
            if (i12 < SS_array_numerator_OneThirdStep.length) {
                if (SS_array_numerator_OneThirdStep[i12] != sS_to__numerator || SS_array_denominator_OneThirdStep[i12] != sS_to__denominator) {
                    i12++;
                } else {
                    sS_milisec_to__step_fromBulb = i12;
                    AppLog.info(TAG, "sS_milisec_to__step_fromBulb:" + sS_milisec_to__step_fromBulb);
                    break;
                }
            } else {
                break;
            }
        }
        int adjustmentStep = sS_milisec_to__step_fromBulb - sS_milisec_from__step_fromBulb;
        AppLog.info(TAG, "adjustment step:" + adjustmentStep);
        return adjustmentStep;
    }

    public static int getSsCurrentStep(int sS_from__numerator, int sS_from__denominator) {
        int sS_milisec_from__step_fromBulb = 1000;
        int i = 0;
        while (true) {
            if (i < SS_array_numerator_OneThirdStep.length) {
                if (SS_array_numerator_OneThirdStep[i] != sS_from__numerator || SS_array_denominator_OneThirdStep[i] != sS_from__denominator) {
                    i++;
                } else {
                    sS_milisec_from__step_fromBulb = i;
                    AppLog.info(TAG, "sS_milisec_from__step_fromBulb:" + sS_milisec_from__step_fromBulb);
                    break;
                }
            } else {
                break;
            }
        }
        int step = sS_milisec_from__step_fromBulb;
        AppLog.info(TAG, "step:" + step);
        return step;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ShutterSpeedChangeListener implements NotificationListener {
        private String[] TAGS;

        private ShutterSpeedChangeListener() {
            this.TAGS = new String[]{CameraNotificationManager.SHUTTER_SPEED};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            Log.d(ChangeSs.TAG, "ShutterSpeedChangeListener onNotify");
            Pair<Integer, Integer> ss = ChangeSs.getCurrentSsFromPF();
            if (ss == null) {
                AppLog.info(ChangeSs.TAG, "ss was null!!");
                ChangeSs.this.finish_change(ss);
                return;
            }
            ChangeSs.access$310();
            if (!ChangeSs.this.isEqual(ChangeSs.mTargetSs_numerator, ChangeSs.mTargetSs_denominator, ss)) {
                AppLog.info(ChangeSs.TAG, "SS change failed. current SS:" + ss.first + "/" + ss.second);
                if (ChangeSs.mRetryCounter > 0) {
                    AppLog.info(ChangeSs.TAG, "!!!could not change shutter-speed appropriately.  Try again.");
                    ChangeSs.executeWithoutCallBack(ChangeSs.mTargetSs_numerator, ChangeSs.mTargetSs_denominator);
                    return;
                } else {
                    AppLog.info(ChangeSs.TAG, "!!!could not change shutter-speed appropriately.  Cannot retry any more..");
                    ChangeSs.this.finish_change(ss);
                    return;
                }
            }
            ChangeSs.this.finish_change(ss);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void finish_change(Pair<Integer, Integer> ss) {
        if (this.mChangeSsNotification != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mChangeSsNotification);
            this.mChangeSsNotification = null;
        }
        if (ss == null) {
            AppLog.info(TAG, "SS is not available");
        } else {
            AppLog.info(TAG, "SS change succeeded execute. current SS:" + ss.first + "/" + ss.second);
        }
        if (this.mCallback != null) {
            this.mCallback.cbFunction();
            this.mCallback = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isEqual(int numerator, int denominator, Pair<Integer, Integer> ss) {
        return ((Integer) ss.first).intValue() == numerator && ((Integer) ss.second).intValue() == denominator;
    }

    public static int getCurrentSsInMilisec() {
        Pair<Integer, Integer> ss = getCurrentSsFromPF();
        Log.d(TAG, "current SS in milisec " + ((((Integer) ss.first).intValue() * 1000) / ((Integer) ss.second).intValue()));
        return (((Integer) ss.first).intValue() * 1000) / ((Integer) ss.second).intValue();
    }

    public static int getCurrentSsInMicrosec() {
        Pair<Integer, Integer> ss = CameraSetting.getInstance().getShutterSpeed();
        Log.d(TAG, "current SS in milisec " + ((((Integer) ss.first).intValue() * 1000) / ((Integer) ss.second).intValue()));
        return (1000000 * ((Integer) ss.first).intValue()) / ((Integer) ss.second).intValue();
    }

    public static Pair<Integer, Integer> getCurrentSsFromPF() {
        Log.v(TAG, "getCurrentSsFromPF <<<< ");
        if (CameraSetting.getInstance() == null || CameraSetting.getInstance().getCamera() == null || CameraSetting.getInstance().getCamera().getNormalCamera() == null) {
            return null;
        }
        Camera.Parameters p = CameraSetting.getInstance().getCamera().getNormalCamera().getParameters();
        CameraEx.ParametersModifier m = CameraSetting.getInstance().getCamera().createParametersModifier(p);
        Log.v(TAG, "getCurrentSsFromPF >>>> ");
        if (m != null) {
            return m.getShutterSpeed();
        }
        return null;
    }
}
