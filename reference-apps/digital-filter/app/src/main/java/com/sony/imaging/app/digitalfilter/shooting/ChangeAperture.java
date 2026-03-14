package com.sony.imaging.app.digitalfilter.shooting;

import android.hardware.Camera;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.base.shooting.camera.executor.IntervalRecExecutor;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class ChangeAperture {
    private ApertureChangeListener mChangeApertureNotification;
    private static String TAG = AppLog.getClassName();
    public static int[] Aperture_array_ILC_OneThirdStep = {100, DigitalZoomController.ZOOM_INIT_VALUE_MODIFIED, 120, 140, 160, 170, IntervalRecExecutor.INTVL_REC_INITIALIZED, 220, 250, 280, 320, 350, 400, 450, 500, 560, AppRoot.USER_KEYCODE.MODE_S, 710, 800, 900, 1000, 1100, 1300, 1400, 1600, GFCommonUtil.FRAME_NUMBER_PER_30P_AVI, 2000, 2200, 2500, 2900, 3200, 3600, 4000, 4500, 5100, 5700, 6400, 7200, 8100, 9000};
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
        try {
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            builder.replace(0, builder.length(), "target aperture:").append(aperture);
            Log.d(TAG, builder.toString());
            builder.replace(0, builder.length(), "aperture shift:").append(aperture_adjustment);
            Log.d(TAG, builder.toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
            this.mTargetAperture = aperture;
            if (this.mChangeApertureNotification != null) {
                CameraNotificationManager.getInstance().removeNotificationListener(this.mChangeApertureNotification);
            }
            if (aperture_adjustment == 0) {
                callBack.cbFunction();
                return;
            }
            this.mCallback = callBack;
            CameraNotificationManager.getInstance().setNotificationListener(this.mChangeApertureNotification);
            boolean isSupportedFixedAperture = GFCommonUtil.getInstance().isSupportedVersion(2, 7);
            if (!isSupportedFixedAperture) {
                new Thread(new Runnable() { // from class: com.sony.imaging.app.digitalfilter.shooting.ChangeAperture.1
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
            CameraSetting.getInstance().getCamera().adjustAperture(aperture_adjustment);
        } catch (Exception e) {
            Log.e(TAG, "ERROR at execute()");
            e.printStackTrace();
        }
    }

    public void execute(int aperture_adjustment, int aperture, Callback callBack, boolean isPolling) {
        int min;
        int currentAperture = getApertureFromPF();
        if (currentAperture == 0) {
            Log.w(TAG, "Aperture is zero, lens is not stable.");
            if (this.mChangeApertureNotification != null) {
                CameraNotificationManager.getInstance().removeNotificationListener(this.mChangeApertureNotification);
                return;
            }
            return;
        }
        int targetAperture = aperture;
        CameraEx.ApertureInfo apertureInfo = CameraSetting.getInstance().getApertureInfo();
        if (apertureInfo != null && targetAperture < (min = apertureInfo.currentAvailableMin)) {
            AppLog.info(TAG, "Target Aperture is less than currentAvailableMin:" + min);
            targetAperture = min;
        }
        execute(aperture_adjustment, targetAperture, callBack);
        if (isPolling) {
            int count = 20;
            while (true) {
                if (count <= 0) {
                    break;
                }
                try {
                    currentAperture = getApertureFromPF();
                    if (currentAperture == this.mTargetAperture) {
                        if (this.mCallback != null) {
                            finish_change();
                            if (this.mChangeApertureNotification != null) {
                                CameraNotificationManager.getInstance().removeNotificationListener(this.mChangeApertureNotification);
                            }
                        }
                    } else {
                        if (currentAperture == 0) {
                            Log.w(TAG, "Aperture is zero, lens is not stable.");
                            break;
                        }
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        count--;
                    }
                } catch (Exception e2) {
                    Log.e(TAG, "ERROR at execute()");
                    e2.printStackTrace();
                }
            }
            if (currentAperture != this.mTargetAperture && currentAperture != 0 && this.mCallback != null) {
                finish_change();
                if (this.mChangeApertureNotification != null) {
                    CameraNotificationManager.getInstance().removeNotificationListener(this.mChangeApertureNotification);
                }
            }
        }
        if (this.mChangeApertureNotification != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mChangeApertureNotification);
        }
    }

    public static void executeWithoutCallBack(int targetAperture) {
        int currentAperture = getApertureFromPF();
        int step = getApertureAdjustmentStep(currentAperture, targetAperture);
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), "target aperture:").append(targetAperture);
        Log.d(TAG, builder.toString());
        builder.replace(0, builder.length(), "aperture shift:").append(step);
        Log.d(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        if (step != 0 && currentAperture != 0) {
            CameraSetting.getInstance().getCamera().adjustAperture(step);
        }
    }

    public static int getApertureFromPF() {
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
        int aperture = m.getAperture();
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), "getApertureFromPF:").append(aperture);
        Log.d(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        return aperture;
    }

    public static int getApertureAdjustmentStep(int aperture_from, int aperture_to) {
        int aperture_from__stepFrom1 = 0;
        int aperture_to__stepFrom1 = 0;
        if (aperture_from == 180) {
            aperture_from = 170;
        }
        if (aperture_to == 180) {
            aperture_to = 170;
        }
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
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            builder.replace(0, builder.length(), "onNotify:").append(tag);
            Log.d(ChangeAperture.TAG, builder.toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
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
