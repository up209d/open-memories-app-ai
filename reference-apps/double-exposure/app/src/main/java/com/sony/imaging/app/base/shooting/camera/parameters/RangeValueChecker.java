package com.sony.imaging.app.base.shooting.camera.parameters;

import android.hardware.Camera;
import android.util.Log;

/* loaded from: classes.dex */
public class RangeValueChecker extends AbstractSupportedChecker {
    private static final String MSG_SAME_VALUE = "Cannot set value because max equals min ";
    public static final String PREFIX_MAX = "max-";
    public static final String PREFIX_MIN = "min-";
    protected static final String TAG = "RangeValueChecker";
    protected final String mKey;
    protected final String mMaxKey;
    protected final String mMinKey;

    public RangeValueChecker(String key) {
        this.mKey = key;
        StringBuilder builder = getBuilder();
        this.mMaxKey = builder.replace(0, builder.length(), PREFIX_MAX).append(this.mKey).toString();
        this.mMinKey = builder.replace(0, builder.length(), PREFIX_MIN).append(this.mKey).toString();
    }

    public RangeValueChecker(String key, String max, String min) {
        this.mKey = key;
        this.mMaxKey = max;
        this.mMinKey = min;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getMax(Camera.Parameters supported) {
        String sValue;
        if (this.mMaxKey == null || (sValue = supported.get(this.mMaxKey)) == null) {
            return Integer.MAX_VALUE;
        }
        return Integer.parseInt(sValue);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getMin(Camera.Parameters supported) {
        String sValue;
        if (this.mMinKey == null || (sValue = supported.get(this.mMinKey)) == null) {
            return Integer.MIN_VALUE;
        }
        return Integer.parseInt(sValue);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.parameters.ISupportedParameterChecker
    public void check(String value, Camera.Parameters supported, Camera.Parameters to, Camera.Parameters from) {
        try {
            int iValue = Integer.parseInt(value);
            int max = getMax(supported);
            int min = getMin(supported);
            if (max == min) {
                StringBuilder builder = getBuilder();
                builder.replace(0, builder.length(), MSG_SAME_VALUE).append(this.mKey).append(AbstractSupportedChecker.COLON).append(min);
                Log.i(TAG, builder.toString());
            } else {
                if (iValue < min) {
                    iValue = min;
                } else if (iValue > max) {
                    iValue = max;
                }
                to.set(this.mKey, String.valueOf(iValue));
                StringBuilder builder2 = getBuilder();
                builder2.replace(0, builder2.length(), this.mKey).append(AbstractSupportedChecker.COLON).append(value).append(" -> ").append(iValue);
                Log.i(TAG, builder2.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
