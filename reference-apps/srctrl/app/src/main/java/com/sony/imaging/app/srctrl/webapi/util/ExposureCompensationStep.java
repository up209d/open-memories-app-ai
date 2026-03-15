package com.sony.imaging.app.srctrl.webapi.util;

import java.math.BigDecimal;

/* loaded from: classes.dex */
public class ExposureCompensationStep {
    private static final String TAG = ExposureCompensationStep.class.getSimpleName();

    /* loaded from: classes.dex */
    public enum EVStep {
        EV_UNKNOWN,
        EV_1_3,
        EV_1_2
    }

    public static EVStep getStepIndex(float exposure) {
        BigDecimal bigDecimal = new BigDecimal(exposure);
        float scaledExposure = bigDecimal.setScale(3, 4).floatValue();
        if (Float.compare(scaledExposure, 0.5f) == 0) {
            return EVStep.EV_1_2;
        }
        if (Float.compare(scaledExposure, 0.333f) == 0) {
            return EVStep.EV_1_3;
        }
        return EVStep.EV_UNKNOWN;
    }

    public static float getStepFloat(int stepIndex) {
        if (EVStep.EV_1_2.equals(EVStep.values()[stepIndex])) {
            return 0.5f;
        }
        if (EVStep.EV_1_3.equals(EVStep.values()[stepIndex])) {
            return 0.333f;
        }
        return 0.0f;
    }
}
