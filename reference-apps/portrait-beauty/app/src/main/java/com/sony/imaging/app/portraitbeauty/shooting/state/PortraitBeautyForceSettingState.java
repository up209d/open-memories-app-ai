package com.sony.imaging.app.portraitbeauty.shooting.state;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.ForceSettingState;
import com.sony.imaging.app.base.shooting.camera.DROAutoHDRController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyBackUpKey;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class PortraitBeautyForceSettingState extends ForceSettingState {
    private static final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.ForceSettingState
    protected boolean isForceSetting() {
        ExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, ExposureModeController.PROGRAM_AUTO_MODE);
        PortraitBeautyUtil.getInstance().readAvailableValue_AspectRatios();
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.ForceSettingState
    protected Pair<Camera.Parameters, CameraEx.ParametersModifier> onSetForceSetting(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        ((CameraEx.ParametersModifier) params.second).setMultiShootNRMode(false);
        return params;
    }

    private void setDROToAuto() {
        AppLog.enter(TAG, AppLog.getMethodName());
        DROAutoHDRController droCntl = DROAutoHDRController.getInstance();
        if (droCntl != null) {
            String dvStr = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_MODE_DRO, DROAutoHDRController.MODE_DRO_AUTO);
            if ("off".equals(dvStr)) {
                droCntl.setValue(DROAutoHDRController.MENU_ITEM_ID_DROHDR, dvStr);
            } else {
                droCntl.setValue(DROAutoHDRController.MENU_ITEM_ID_DRO, dvStr);
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
