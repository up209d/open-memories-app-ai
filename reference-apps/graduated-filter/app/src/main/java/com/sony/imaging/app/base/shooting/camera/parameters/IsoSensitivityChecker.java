package com.sony.imaging.app.base.shooting.camera.parameters;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class IsoSensitivityChecker extends IntegerValuesChecker {
    protected static final String TRUE = "true";

    public IsoSensitivityChecker(String key) {
        super(key);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.parameters.IntegerValuesChecker, com.sony.imaging.app.base.shooting.camera.parameters.StringValuesChecker, com.sony.imaging.app.base.shooting.camera.parameters.ISupportedParameterChecker
    public void check(String value, Camera.Parameters supported, Camera.Parameters to, Camera.Parameters from) {
        boolean isMultishotNR = false;
        String multishotNR = to.get(Keys.KEY_MULTI_SHOOT_NR_MODE);
        if (multishotNR == null) {
            multishotNR = from.get(Keys.KEY_MULTI_SHOOT_NR_MODE);
        }
        if (multishotNR != null) {
            isMultishotNR = "true".equals(multishotNR);
        } else {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> currentParam = CameraSetting.getInstance().getParameters();
            if (currentParam != null) {
                isMultishotNR = ((CameraEx.ParametersModifier) currentParam.second).getMultiShootNRMode();
            }
        }
        if (isMultishotNR) {
            this.mSupportedKey = "mulit-shoot-nr-iso-values";
        } else {
            this.mSupportedKey = "iso-sensitivity-values";
        }
        super.check(value, supported, to, from);
    }
}
