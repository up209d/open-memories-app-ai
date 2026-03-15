package com.sony.imaging.app.lightgraffiti.shooting.controller;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import java.util.List;

/* loaded from: classes.dex */
public class LGExposureModeController extends ExposureModeController {
    private static final String TAG = LGExposureModeController.class.getSimpleName();
    private static LGExposureModeController mInstance;

    public static LGExposureModeController getInstance() {
        if (mInstance == null) {
            mInstance = new LGExposureModeController();
        }
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        Log.w(TAG, AppLog.getMethodName() + " : tag=" + tag + ", value=" + value);
    }

    public void setExposureMode(String value) {
        super.setValue(ExposureModeController.EXPOSURE_MODE, value);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController
    public boolean isValidDialPosition() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        if (!ModeDialDetector.hasModeDial()) {
            return null;
        }
        List<String> supportedList = super.getSupportedValue(tag);
        return supportedList;
    }
}
