package com.sony.imaging.app.srctrl.webapi.availability;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.srctrl.util.OperationRequester;
import com.sony.imaging.app.srctrl.webapi.util.ExposureCompensationStep;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class SupportedValueCache {
    private static final String TAG = SupportedValueCache.class.getSimpleName();
    private static SupportedValueCache cache = new SupportedValueCache();
    private int[][] supportedExposureCompensation = (int[][]) null;
    private int[] supportedSelfTimer = null;

    public static SupportedValueCache getInstance() {
        return cache;
    }

    public int[][] getSupportedExposureCompensation() {
        if (this.supportedExposureCompensation == null) {
            List<String> supported = (List) new OperationRequester().request(4, (Object) null);
            if (supported != null && supported.size() != 0) {
                int[] max = new int[1];
                max[0] = 0;
                int[] min = new int[1];
                min[0] = 0;
                float[] step = {0.0f};
                try {
                    min[0] = Integer.parseInt(supported.get(0));
                    max[0] = Integer.parseInt(supported.get(supported.size() - 1));
                } catch (NumberFormatException e) {
                    Log.e(TAG, "NumberFormatException in getSupportedExposureCompensation");
                }
                Float step_result = (Float) new OperationRequester().request(3, (Object) null);
                if (step_result != null) {
                    step[0] = step_result.floatValue();
                    ExposureCompensationStep.EVStep[] stepIndex = {ExposureCompensationStep.getStepIndex(step[0])};
                    int[] stepIndexOrdinal = new int[1];
                    stepIndexOrdinal[0] = stepIndex[0].ordinal();
                    this.supportedExposureCompensation = new int[3];
                    this.supportedExposureCompensation[0] = max;
                    this.supportedExposureCompensation[1] = min;
                    this.supportedExposureCompensation[2] = stepIndexOrdinal;
                } else {
                    this.supportedExposureCompensation = (int[][]) null;
                }
            } else {
                this.supportedExposureCompensation = (int[][]) null;
            }
        }
        return this.supportedExposureCompensation;
    }

    public int[] getAvailableExposureCompensation() {
        Boolean result = (Boolean) new OperationRequester().request(0, (Object) null);
        if (result == null || !result.booleanValue()) {
            return null;
        }
        if (this.supportedExposureCompensation == null) {
            getSupportedExposureCompensation();
        }
        if (this.supportedExposureCompensation == null) {
            return null;
        }
        int[] array = new int[3];
        for (int i = 0; i < 3; i++) {
            array[i] = this.supportedExposureCompensation[i][0];
        }
        return array;
    }

    public int[] getSupportedSelfTimer() {
        List<String> timerList;
        if (this.supportedSelfTimer == null) {
            ArrayList<Integer> supported = new ArrayList<>();
            boolean isTimerSupported = false;
            List<String> dModeList = (List) new OperationRequester().request(9, (Object) null);
            if (dModeList != null) {
                for (String str : dModeList) {
                    if (DriveModeController.SINGLE.equals(str)) {
                        supported.add(0);
                    } else if (DriveModeController.SELF_TIMER.equals(str)) {
                        isTimerSupported = true;
                    }
                }
            }
            if (isTimerSupported && (timerList = (List) new OperationRequester().request(14, (Object) null)) != null) {
                for (String str2 : timerList) {
                    if (DriveModeController.SELF_TIMER_2S.equals(str2)) {
                        supported.add(2);
                    } else if (DriveModeController.SELF_TIMER_10S.equals(str2)) {
                        supported.add(10);
                    }
                }
            }
            this.supportedSelfTimer = new int[supported.size()];
            int i = 0;
            Iterator i$ = supported.iterator();
            while (i$.hasNext()) {
                Integer value = i$.next();
                this.supportedSelfTimer[i] = value.intValue();
                i++;
            }
        }
        return this.supportedSelfTimer;
    }

    public int[] getAvailableSelfTimer() {
        Boolean result = (Boolean) new OperationRequester().request(11, DriveModeController.SELF_TIMER);
        if (result == null || !result.booleanValue()) {
            return null;
        }
        return getSupportedSelfTimer();
    }
}
