package com.sony.imaging.app.base.shooting.camera.parameters;

import android.hardware.Camera;
import android.util.Log;
import java.util.StringTokenizer;

/* loaded from: classes.dex */
public class StringValuesChecker extends AbstractSupportedChecker {
    public static final String SUFFIX_SUPPORTED = "-values";
    protected String mKey;
    protected String mSupportedKey;

    public StringValuesChecker(String key, String supportedKey) {
        this.mKey = key;
        this.mSupportedKey = supportedKey;
    }

    public StringValuesChecker(String key) {
        this.mKey = key;
        StringBuilder builder = getBuilder();
        builder.replace(0, builder.length(), key).append("-values");
        this.mSupportedKey = builder.toString();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.parameters.ISupportedParameterChecker
    public void check(String value, Camera.Parameters supported, Camera.Parameters to, Camera.Parameters from) {
        if (value != null) {
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
                if (value.equals(tokenizer.nextToken())) {
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
