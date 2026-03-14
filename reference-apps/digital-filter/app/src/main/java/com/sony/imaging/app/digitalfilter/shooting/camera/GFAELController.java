package com.sony.imaging.app.digitalfilter.shooting.camera;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.digitalfilter.caution.GFInfo;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import java.util.List;

/* loaded from: classes.dex */
public class GFAELController extends AELController {
    private static GFAELController mInstance = null;

    public static GFAELController getInstance() {
        if (mInstance == null) {
            mInstance = new GFAELController();
        }
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AELController
    public void holdAELock(boolean control) {
        if (control && ((GFCommonUtil.getInstance().hasIrisRing() || GFCommonUtil.getInstance().isFixedAperture()) && GFExposureModeController.getInstance().getValue(null).equalsIgnoreCase(ExposureModeController.MANUAL_MODE))) {
            CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_INVALID_FUNCTION_MANUAL_MODE);
        } else {
            super.holdAELock(control);
        }
    }

    public void holdAELockWithoutCaution(boolean control) {
        if (!control || ((!GFCommonUtil.getInstance().hasIrisRing() && !GFCommonUtil.getInstance().isFixedAperture()) || !GFExposureModeController.getInstance().getValue(null).equalsIgnoreCase(ExposureModeController.MANUAL_MODE))) {
            super.holdAELock(control);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AELController
    public void repressAELock() {
        if ((GFCommonUtil.getInstance().hasIrisRing() || GFCommonUtil.getInstance().isFixedAperture()) && GFExposureModeController.getInstance().getValue(null).equalsIgnoreCase(ExposureModeController.MANUAL_MODE)) {
            CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_INVALID_FUNCTION_MANUAL_MODE);
        } else {
            super.repressAELock();
        }
    }

    public void unlock() {
        CameraSetting cameraSetting = CameraSetting.getInstance();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> supported = cameraSetting.getSupportedParameters();
        List<String> list = ((CameraEx.ParametersModifier) supported.second).getSupportedAutoExposureLocks();
        if (list != null && list.contains("unlocked")) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> p = cameraSetting.getEmptyParameters();
            ((CameraEx.ParametersModifier) p.second).setAutoExposureLock("unlocked");
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
            CameraSetting.getInstance().setParameters(p);
        }
    }
}
