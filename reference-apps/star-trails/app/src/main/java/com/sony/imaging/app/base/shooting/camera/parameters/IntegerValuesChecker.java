package com.sony.imaging.app.base.shooting.camera.parameters;

import android.hardware.Camera;
import android.util.Log;
import java.util.StringTokenizer;

/* loaded from: classes.dex */
public class IntegerValuesChecker extends StringValuesChecker {
    public IntegerValuesChecker(String key, String supportedKey) {
        super(key, supportedKey);
    }

    public IntegerValuesChecker(String key) {
        super(key);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.parameters.StringValuesChecker, com.sony.imaging.app.base.shooting.camera.parameters.ISupportedParameterChecker
    public void check(String value, Camera.Parameters supported, Camera.Parameters to, Camera.Parameters from) {
        if (value != null) {
            Integer iValue = Integer.valueOf(value);
            boolean isSupported = false;
            String supportedValues = supported.get(this.mSupportedKey);
            if (supportedValues == null) {
                Log.w(TAG, "ValueChecker " + this.mKey + ": supported is NULL");
                return;
            }
            StringTokenizer tokenizer = new StringTokenizer(supportedValues, ",");
            while (true) {
                if (!tokenizer.hasMoreElements()) {
                    break;
                }
                Integer supportedValue = Integer.valueOf(tokenizer.nextToken());
                if (iValue.equals(supportedValue)) {
                    isSupported = true;
                    to.set(this.mKey, value);
                    break;
                }
            }
            StringBuilder builder = getBuilder();
            builder.replace(0, builder.length(), this.mKey).append(AbstractSupportedChecker.COLON).append(value).append(" -> ").append(isSupported);
            Log.i(TAG, builder.toString());
        }
    }
}
