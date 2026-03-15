package com.sony.imaging.app.base.shooting.camera.parameters;

import android.hardware.Camera;

/* loaded from: classes.dex */
public class SaturationChecker extends RangeValueChecker {
    public SaturationChecker(String key, String max, String min) {
        super(key, max, min);
    }

    public SaturationChecker(String key) {
        super(key);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.parameters.RangeValueChecker
    public int getMax(Camera.Parameters supported) {
        return Math.min(super.getMax(supported), 3);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.parameters.RangeValueChecker
    public int getMin(Camera.Parameters supported) {
        return Math.max(super.getMin(supported), -3);
    }
}
