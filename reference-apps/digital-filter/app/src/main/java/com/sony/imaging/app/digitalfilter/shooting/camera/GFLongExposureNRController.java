package com.sony.imaging.app.digitalfilter.shooting.camera;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFLongExposureNRController extends AbstractController {
    private static final String API_NAME = "setLongExposureNR";
    private static final String GF_LONG_EXPOSURE_NR_KEY = "GF_LONG_EXPOSURE_NR_KEY";
    public static final String LONG_EXPOSURE_NR = "long-exposure-nr";
    public static final String NR_OFF = "nr-off";
    public static final String NR_ON = "nr-on";
    public static final boolean PF_NR_OFF = false;
    public static final boolean PF_NR_ON = true;
    private static final String TAG = AppLog.getClassName();
    private static GFLongExposureNRController mInstance;
    private static ArrayList<String> sSupportedList;
    private BackUpUtil mBackupUtil;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add(LONG_EXPOSURE_NR);
        sSupportedList.add(NR_ON);
        sSupportedList.add(NR_OFF);
    }

    public static GFLongExposureNRController getInstance() {
        if (mInstance == null) {
            mInstance = createInstance();
        }
        return mInstance;
    }

    private static GFLongExposureNRController createInstance() {
        if (mInstance == null) {
            mInstance = new GFLongExposureNRController();
        }
        return mInstance;
    }

    private GFLongExposureNRController() {
        this.mBackupUtil = null;
        this.mBackupUtil = BackUpUtil.getInstance();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        if (tag.equalsIgnoreCase(LONG_EXPOSURE_NR)) {
            this.mBackupUtil.setPreference(GF_LONG_EXPOSURE_NR_KEY, value);
            if (value.equalsIgnoreCase(NR_ON)) {
                Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getEmptyParameters();
                ((CameraEx.ParametersModifier) params.second).setLongExposureNR(true);
                CameraSetting.getInstance().setParameters(params);
            } else if (value.equalsIgnoreCase(NR_OFF)) {
                Pair<Camera.Parameters, CameraEx.ParametersModifier> params2 = CameraSetting.getInstance().getEmptyParameters();
                ((CameraEx.ParametersModifier) params2.second).setLongExposureNR(false);
                CameraSetting.getInstance().setParameters(params2);
            }
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        if (tag.equals(LONG_EXPOSURE_NR)) {
            if (isAvailable(tag)) {
                return this.mBackupUtil.getPreferenceString(GF_LONG_EXPOSURE_NR_KEY, NR_ON);
            }
            return NR_OFF;
        }
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        if (isSupportedLongExposureNR()) {
            return sSupportedList;
        }
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        if (isSupportedLongExposureNR()) {
            return AvailableInfo.isAvailable(API_NAME, null);
        }
        return false;
    }

    private boolean isSupportedLongExposureNR() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> supportedParams = CameraSetting.getInstance().getSupportedParameters();
        boolean isSupported = GFCommonUtil.getInstance().isSupportedVersion(2, 0) && ((CameraEx.ParametersModifier) supportedParams.second).isSupportedLongExposureNR();
        return isSupported && ModeDialDetector.hasModeDial();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return sSupportedList;
    }
}
