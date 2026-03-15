package com.sony.imaging.app.graduatedfilter.shooting;

import android.hardware.Camera;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.base.shooting.camera.executor.IntervalRecExecutor;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class ChangeAperture {
    private ApertureChangeListener mChangeApertureNotification;
    private static String TAG = AppLog.getClassName();
    public static int[] Aperture_array_ILC_OneThirdStep = {100, DigitalZoomController.ZOOM_INIT_VALUE_MODIFIED, 120, 140, 160, 170, IntervalRecExecutor.INTVL_REC_INITIALIZED, 220, 250, 280, 320, 350, 400, 450, 500, 560, AppRoot.USER_KEYCODE.MODE_S, 710, 800, 900, 1000, 1100, 1300, 1400, 1600, 1800, 2000, 2200, 2500, 2900, 3200, 3600, 4000, 4500, 5100, 5700, 6400};
    private Callback mCallback = null;
    private int mTargetAperture = 1;

    /* loaded from: classes.dex */
    public interface Callback {
        void cbFunction();
    }

    public ChangeAperture() {
        if (this.mChangeApertureNotification == null) {
            this.mChangeApertureNotification = new ApertureChangeListener();
        }
    }

    public void execute(int aperture_adjustment, int aperture, Callback callBack) {
        boolean isUnsupportedFixedAperture = false;
        try {
            Log.d(TAG, "execute. target steps:" + aperture_adjustment);
            if (aperture_adjustment == 0) {
                Log.i(TAG, "no change needed. ");
                if (this.mChangeApertureNotification != null) {
                    CameraNotificationManager.getInstance().removeNotificationListener(this.mChangeApertureNotification);
                }
                callBack.cbFunction();
                return;
            }
            this.mTargetAperture = aperture;
            this.mCallback = callBack;
            if (this.mChangeApertureNotification != null) {
                CameraNotificationManager.getInstance().removeNotificationListener(this.mChangeApertureNotification);
            }
            CameraNotificationManager.getInstance().setNotificationListener(this.mChangeApertureNotification);
            String version = ScalarProperties.getString("version.platform");
            int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(StringBuilderThreadLocal.PERIOD)));
            int pfAPIVersion = Integer.parseInt(version.substring(version.indexOf(StringBuilderThreadLocal.PERIOD) + 1));
            if (pfMajorVersion == 1 || (pfMajorVersion == 2 && pfAPIVersion < 7)) {
                isUnsupportedFixedAperture = true;
            }
            if (isUnsupportedFixedAperture) {
                new Thread(new Runnable() { // from class: com.sony.imaging.app.graduatedfilter.shooting.ChangeAperture.1
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            Thread.sleep(2000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (ChangeAperture.this.mCallback != null) {
                            Log.w(ChangeAperture.TAG, "ChangeAperture.execute took more than 500msec.");
                            ChangeAperture.this.finish_change();
                            if (ChangeAperture.this.mChangeApertureNotification != null) {
                                CameraNotificationManager.getInstance().removeNotificationListener(ChangeAperture.this.mChangeApertureNotification);
                            }
                        }
                    }
                }).start();
            }
            Log.d(TAG, "call aperture change.  aperture shift:" + aperture_adjustment);
            CameraSetting.getInstance().getCamera().adjustAperture(aperture_adjustment);
        } catch (Exception e) {
            Log.e(TAG, "ERROR at execute()");
            e.printStackTrace();
        }
    }

    public static void executeWithoutCallBack(int targetAperture) {
        Log.d(TAG, "execute. target aperture:" + targetAperture);
        int step = getApertureAdjustmentStep(getApertureFromPF(), targetAperture);
        if (step == 0) {
            Log.i(TAG, "no change needed. ");
        } else {
            CameraSetting.getInstance().getCamera().adjustAperture(step);
        }
    }

    public static int getApertureFromPF() {
        Log.d(TAG, "getApertureFromPF <<<< ");
        if (CameraSetting.getInstance() == null) {
            Log.e(TAG, "CameraSetting.getInstance()==null");
            return 0;
        }
        if (CameraSetting.getInstance().getCamera() == null) {
            Log.e(TAG, "CameraSetting.getInstance().getCamera()==null");
            return 0;
        }
        if (CameraSetting.getInstance().getCamera().getNormalCamera() == null) {
            Log.e(TAG, "CameraSetting.getInstance().getCamera().getNormalCamera()==null");
            return 0;
        }
        Camera.Parameters p = CameraSetting.getInstance().getCamera().getNormalCamera().getParameters();
        CameraEx.ParametersModifier m = CameraSetting.getInstance().getCamera().createParametersModifier(p);
        if (m == null) {
            Log.e(TAG, "CameraSetting.getInstance().getCamera().createParametersModifier(p)==null");
            return 0;
        }
        Log.i(TAG, "current aperture: " + m.getAperture());
        Log.d(TAG, "getApertureFromPF >>>> ");
        return m.getAperture();
    }

    public static int getApertureAdjustmentStep(int aperture_from, int aperture_to) {
        int aperture_from__stepFrom1 = 0;
        int aperture_to__stepFrom1 = 0;
        if (aperture_from == 240) {
            aperture_from = 250;
        }
        if (aperture_to == 240) {
            aperture_to = 250;
        }
        int i1 = 0;
        while (true) {
            if (i1 >= Aperture_array_ILC_OneThirdStep.length) {
                break;
            }
            if (Aperture_array_ILC_OneThirdStep[i1] != aperture_from) {
                i1++;
            } else {
                aperture_from__stepFrom1 = i1;
                break;
            }
        }
        int i12 = 0;
        while (true) {
            if (i12 >= Aperture_array_ILC_OneThirdStep.length) {
                break;
            }
            if (Aperture_array_ILC_OneThirdStep[i12] != aperture_to) {
                i12++;
            } else {
                aperture_to__stepFrom1 = i12;
                break;
            }
        }
        int adjustmentStep = aperture_to__stepFrom1 - aperture_from__stepFrom1;
        Log.d(TAG, "adjustment step:" + adjustmentStep);
        return adjustmentStep;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ApertureChangeListener implements NotificationListener {
        private String[] TAGS;

        private ApertureChangeListener() {
            this.TAGS = new String[]{"Aperture"};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            Log.d(ChangeAperture.TAG, "ApertureChangeListener onNotify");
            int currentAperture = ChangeAperture.getApertureFromPF();
            if (ChangeAperture.this.mTargetAperture != currentAperture) {
                try {
                    Thread.sleep(300L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int currentAperture2 = ChangeAperture.getApertureFromPF();
                if (ChangeAperture.this.mTargetAperture != currentAperture2) {
                    Log.w(ChangeAperture.TAG, "!!!could not change aperture appropriately.  But we will give-up to fix and hit CallBack. Since F range may change by Lense zoom position.");
                    Log.w(ChangeAperture.TAG, "Try to set aperture again only once");
                    int step = ChangeAperture.getApertureAdjustmentStep(ChangeAperture.getApertureFromPF(), ChangeAperture.this.mTargetAperture);
                    if (CameraSetting.getInstance().getCamera() != null) {
                        CameraSetting.getInstance().getCamera().adjustAperture(step);
                    }
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                }
            }
            ChangeAperture.this.finish_change();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void finish_change() {
        if (this.mChangeApertureNotification != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mChangeApertureNotification);
            this.mChangeApertureNotification = null;
        }
        if (this.mCallback != null) {
            this.mCallback.cbFunction();
            this.mCallback = null;
        }
    }
}
