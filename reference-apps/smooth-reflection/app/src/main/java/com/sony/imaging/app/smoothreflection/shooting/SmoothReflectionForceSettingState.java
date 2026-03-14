package com.sony.imaging.app.smoothreflection.shooting;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.ForceSettingState;
import com.sony.imaging.app.base.shooting.camera.DROAutoHDRController;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionBackUpKey;
import com.sony.imaging.app.smoothreflection.shooting.camera.SelfTimerIntervalPriorityController;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class SmoothReflectionForceSettingState extends ForceSettingState {
    private static final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.ForceSettingState
    protected boolean isForceSetting() {
        AppLog.enter(TAG, AppLog.getMethodName());
        setDROToOff();
        String SelfTimerSetting = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_SELF_TIMER, SelfTimerIntervalPriorityController.SELFTIMEROFF);
        SelfTimerIntervalPriorityController.getInstance().setValue("selftimer", SelfTimerSetting);
        AppLog.exit(TAG, AppLog.getMethodName());
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.ForceSettingState
    protected Pair<Camera.Parameters, CameraEx.ParametersModifier> onSetForceSetting(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        if (!AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_OSS_SW_ON_TYPE_C") && !AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_OSS_SW_ON_TYPE_P") && !AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_TYPE_NONE_TYPE_P")) {
            ((CameraEx.ParametersModifier) params.second).setAntiHandBlurMode("off");
        }
        return params;
    }

    private void setDROToOff() {
        AppLog.enter(TAG, AppLog.getMethodName());
        DROAutoHDRController droCntl = DROAutoHDRController.getInstance();
        if (droCntl != null) {
            String dvStr = droCntl.getValue();
            if (dvStr != "off") {
                droCntl.setValue(DROAutoHDRController.MENU_ITEM_ID_DROHDR, "off");
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
