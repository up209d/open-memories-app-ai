package com.sony.imaging.app.base.shooting.camera.parameters;

import android.hardware.Camera;
import android.util.Log;

/* loaded from: classes.dex */
public class BooleanSupportedChecker extends AbstractSupportedChecker {
    public static final String SUFFIX_SUPPORTED = "-values";
    public static final String TRUE = "true";
    protected final String mKey;
    protected final String mSupported;

    public BooleanSupportedChecker(String key, String supported_key) {
        this.mKey = key;
        this.mSupported = supported_key;
    }

    public BooleanSupportedChecker(String key) {
        this.mKey = key;
        StringBuilder builder = getBuilder();
        builder.replace(0, builder.length(), key).append("-values");
        this.mSupported = builder.toString();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.parameters.ISupportedParameterChecker
    public void check(String value, Camera.Parameters supported, Camera.Parameters to, Camera.Parameters from) {
        if (value != null) {
            boolean isSupported = TRUE.equals(supported.get(this.mSupported));
            if (isSupported) {
                to.set(this.mKey, value);
            }
            StringBuilder builder = getBuilder();
            builder.replace(0, builder.length(), this.mKey).append(AbstractSupportedChecker.COLON).append(value).append(" -> ").append(isSupported);
            Log.i(TAG, builder.toString());
        }
    }
}
