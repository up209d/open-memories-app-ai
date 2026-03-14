package com.sony.imaging.app.digitalfilter.shooting.camera;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFExposureCompensationController extends ExposureCompensationController {
    private static final int EV_RANGE_PULS_MINUS_3_MAX = 9;
    private static final int EV_RANGE_PULS_MINUS_3_MIN = -9;
    private static final float EV_STEP_0_5 = 0.5f;
    private static final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureCompensationController, com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        Pair<Integer, Integer> range = getSupportedRange(1);
        if (EVDialDetector.hasEVDial() && ((Integer) range.first).intValue() <= 9 && ((Integer) range.second).intValue() >= EV_RANGE_PULS_MINUS_3_MIN && getExposureCompensationStep() != EV_STEP_0_5) {
            List<String> list = createSupportedValueArray();
            return list;
        }
        List<String> list2 = super.getSupportedValue(tag);
        return list2;
    }

    private static List<String> createSupportedValueArray() {
        List<String> list = new ArrayList<>();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getSupportedParameters();
        int maxValue = ((Camera.Parameters) params.first).getMaxExposureCompensation();
        int minValue = ((Camera.Parameters) params.first).getMinExposureCompensation();
        if (minValue == 0 && maxValue == 0) {
            return null;
        }
        for (int i = minValue; i <= maxValue; i++) {
            list.add(Integer.toString(i));
        }
        return list;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureCompensationController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean ret = isExposureCompensationAvailable();
        return ret;
    }
}
