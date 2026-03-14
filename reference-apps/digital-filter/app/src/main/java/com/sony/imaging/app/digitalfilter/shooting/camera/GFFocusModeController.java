package com.sony.imaging.app.digitalfilter.shooting.camera;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class GFFocusModeController extends FocusModeController {
    private static GFFocusModeController mInstance = null;

    public static GFFocusModeController getInstance() {
        if (mInstance == null) {
            mInstance = new GFFocusModeController();
        }
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.FocusModeController
    public void setValue(String value, Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        String focusval = value;
        if (focusval.equals("af-c")) {
            focusval = "af-s";
        }
        super.setValue(focusval, params);
    }
}
