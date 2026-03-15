package com.sony.imaging.app.timelapse.shooting.controller;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.SilentShutterController;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class TLSilentShutterController extends SilentShutterController {
    public static TLSilentShutterController mTLInstance;

    protected TLSilentShutterController() {
        this.mCamSet = CameraSetting.getInstance();
        creatAppToPFTable();
    }

    public static TLSilentShutterController getInstance() {
        if (mTLInstance == null) {
            mTLInstance = new TLSilentShutterController();
        }
        return mTLInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.SilentShutterController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean ret = super.isAvailable(tag);
        if (TLCommonUtil.getInstance().getCurrentState() == 5) {
            return false;
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.SilentShutterController, com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        super.setValue(tag, value);
        BackUpUtil.getInstance().setPreference(TimeLapseConstants.TIME_LAPSE_SILENT_SHUTTER_STATUS_KEY, value);
        if (TLCommonUtil.getInstance().getCurrentState() == 7) {
            updateCreativeStyle();
        }
    }

    public void setSilentShutterHaitaValue() {
        if (Environment.isSilentShutterAPISupported()) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> emptyParam = this.mCamSet.getEmptyParameters();
            if (TLCommonUtil.getInstance().getCurrentState() == 5) {
                ((CameraEx.ParametersModifier) emptyParam.second).setSilentShutterMode(false);
                this.mCamSet.setParameters(emptyParam);
                return;
            }
            String value = BackUpUtil.getInstance().getPreferenceString(TimeLapseConstants.TIME_LAPSE_SILENT_SHUTTER_STATUS_KEY, "off");
            if ("on".equals(value)) {
                ((CameraEx.ParametersModifier) emptyParam.second).setSilentShutterMode(true);
            } else if ("off".equals(value)) {
                ((CameraEx.ParametersModifier) emptyParam.second).setSilentShutterMode(false);
            } else {
                return;
            }
            this.mCamSet.setParameters(emptyParam);
        }
    }

    private void updateCreativeStyle() {
        String creativeStyle = BackUpUtil.getInstance().getPreferenceString(TimeLapseConstants.CUSTOM_CREACTIVE_STYLE_KEY, "standard");
        TimelapseCreativeStyleController.getInstance().setTimelapseValue(CreativeStyleController.CREATIVESTYLE, creativeStyle);
    }
}
