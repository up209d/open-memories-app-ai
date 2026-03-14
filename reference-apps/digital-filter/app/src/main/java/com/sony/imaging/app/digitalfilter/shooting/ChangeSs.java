package com.sony.imaging.app.digitalfilter.shooting;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.executor.IntervalRecExecutor;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
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
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), "target SS:").append(sS_numerator).append("/").append(sS_denominator);
        Log.d(TAG, builder.toString());
        builder.replace(0, builder.length(), "SS shift:").append(SS_adjustment);
        Log.d(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        if (this.mChangeSsNotification != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mChangeSsNotification);
        }
        if (SS_adjustment == 0) {
            Log.d(TAG, "no change needed. ");
            callBack.cbFunction();
            return;
        }
        this.mCallback = callBack;
        mRetryCounter = 3;
        CameraNotificationManager.getInstance().setNotificationListener(this.mChangeSsNotification);
        CameraSetting.getInstance().getCamera().adjustShutterSpeed(-SS_adjustment);
        mTargetSs_numerator = sS_numerator;
        mTargetSs_denominator = sS_denominator;
    }

    public static void executeWithoutCallBack(int sS_numerator, int sS_denominator) {
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), "target SS:").append(sS_numerator).append("/").append(sS_denominator);
        Log.d(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        Pair<Integer, Integer> ss = getCurrentSsFromPF();
        if (ss == null) {
            Log.d(TAG, "could not get current SS value ");
            return;
        }
        int step = getSsAdjustmentStep(((Integer) ss.first).intValue(), ((Integer) ss.second).intValue(), sS_numerator, sS_denominator);
        StringBuilder builder2 = StringBuilderThreadLocal.getScratchBuilder();
        builder2.replace(0, builder2.length(), "adjustment step:").append(step);
        Log.d(TAG, builder2.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder2);
        if (step == 0) {
            Log.d(TAG, "no change needed. ");
        } else {
            CameraSetting.getInstance().getCamera().adjustShutterSpeed(-step);
        }
    }

    public static int getSsAdjustmentStep(int sS_from__numerator, int sS_from__denominator, int sS_to__numerator, int sS_to__denominator) {
        int sS_milisec_from__step_fromBulb = 0;
        int sS_milisec_to__step_fromBulb = 0;
        int i1 = 0;
        while (true) {
            if (i1 < SS_array_numerator_OneThirdStep.length) {
                if (SS_array_numerator_OneThirdStep[i1] != sS_from__numerator || SS_array_denominator_OneThirdStep[i1] != sS_from__denominator) {
                    i1++;
                } else {
                    sS_milisec_from__step_fromBulb = i1;
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
                    break;
                }
            } else {
                break;
            }
        }
        int adjustmentStep = sS_milisec_to__step_fromBulb - sS_milisec_from__step_fromBulb;
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), "current ss(index):").append(sS_milisec_from__step_fromBulb);
        Log.d(TAG, builder.toString());
        builder.replace(0, builder.length(), "target ss(index):").append(sS_milisec_to__step_fromBulb);
        Log.d(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        return adjustmentStep;
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
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            builder.replace(0, builder.length(), "onNotify:").append(tag);
            Log.d(ChangeSs.TAG, builder.toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
            Pair<Integer, Integer> ss = ChangeSs.getCurrentSsFromPF();
            if (ss == null) {
                Log.d(ChangeSs.TAG, "ss was null!!");
                ChangeSs.this.finish_change(ss);
                return;
            }
            ChangeSs.access$310();
            if (ChangeSs.this.isEqual(ChangeSs.mTargetSs_numerator, ChangeSs.mTargetSs_denominator, ss)) {
                ChangeSs.this.finish_change(ss);
                return;
            }
            StringBuilder builder2 = StringBuilderThreadLocal.getScratchBuilder();
            builder2.replace(0, builder2.length(), "change failed. current SS:").append(ss.first).append("/").append(ss.second);
            Log.d(ChangeSs.TAG, builder2.toString());
            if (ChangeSs.mRetryCounter > 0) {
                builder2.replace(0, builder2.length(), "!!!could not change shutter-speed appropriately.  Try again.");
                Log.d(ChangeSs.TAG, builder2.toString());
                ChangeSs.executeWithoutCallBack(ChangeSs.mTargetSs_numerator, ChangeSs.mTargetSs_denominator);
            } else {
                builder2.replace(0, builder2.length(), "!!!could not change shutter-speed appropriately.  Cannot retry any more..");
                Log.d(ChangeSs.TAG, builder2.toString());
                ChangeSs.this.finish_change(ss);
            }
            StringBuilderThreadLocal.releaseScratchBuilder(builder2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void finish_change(Pair<Integer, Integer> ss) {
        if (this.mChangeSsNotification != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mChangeSsNotification);
            this.mChangeSsNotification = null;
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

    public static Pair<Integer, Integer> getCurrentSsFromPF() {
        Pair<Integer, Integer> ss = null;
        if (CameraSetting.getInstance() != null && CameraSetting.getInstance().getCamera() != null && CameraSetting.getInstance().getCamera().getNormalCamera() != null) {
            Camera.Parameters p = CameraSetting.getInstance().getCamera().getNormalCamera().getParameters();
            CameraEx.ParametersModifier m = CameraSetting.getInstance().getCamera().createParametersModifier(p);
            if (m != null && (ss = m.getShutterSpeed()) != null) {
                StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
                builder.replace(0, builder.length(), "current SS:").append(ss.first).append("/").append(ss.second);
                Log.d(TAG, builder.toString());
                StringBuilderThreadLocal.releaseScratchBuilder(builder);
            }
        }
        return ss;
    }
}
