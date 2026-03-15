package com.sony.imaging.app.srctrl.webapi.util;

import java.math.BigDecimal;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class EvConverter {
    public static float[] convertToValueArray(int max, int min, int step) {
        Float stepFloat = Float.valueOf(ExposureCompensationStep.getStepFloat(step));
        ArrayList<Float> list = new ArrayList<>();
        for (int i = min; i < max + 1; i++) {
            BigDecimal bigDecimal = new BigDecimal(i * stepFloat.floatValue());
            list.add(Float.valueOf(bigDecimal.setScale(2, 4).floatValue()));
        }
        return null;
    }
}
