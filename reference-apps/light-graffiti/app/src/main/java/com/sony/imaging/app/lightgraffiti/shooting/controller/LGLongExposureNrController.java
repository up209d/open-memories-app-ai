package com.sony.imaging.app.lightgraffiti.shooting.controller;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ViewTekiController;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.lightgraffiti.util.LGUtility;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class LGLongExposureNrController {
    private static LGLongExposureNrController mInstance;
    private static final String TAG = LGLongExposureNrController.class.getSimpleName();
    public static String LONG_EXPOSURE_NR_ON = ViewTekiController.VIEWTEKI_ON;
    public static String LONG_EXPOSURE_NR_OFF = "OFF";
    private static String mSavedDiademValue = LONG_EXPOSURE_NR_ON;
    private static boolean saved = false;

    public static LGLongExposureNrController getInstance() {
        if (mInstance == null) {
            new LGLongExposureNrController();
        }
        return mInstance;
    }

    private static void setController(LGLongExposureNrController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected LGLongExposureNrController() {
        setController(this);
    }

    public String getSaveDiademValue() {
        Log.d(TAG, AppLog.getMethodName() + " : mSavedDiademValue=" + mSavedDiademValue);
        return mSavedDiademValue;
    }

    public void setValue(String value) {
        boolean isPFverOver2 = LGUtility.getInstance().isPFverOver2();
        if (isPFverOver2) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> supportedParams = CameraSetting.getInstance().getSupportedParameters();
            CameraSetting.getInstance().getParameters();
            boolean isSupported = ((CameraEx.ParametersModifier) supportedParams.second).isSupportedLongExposureNR();
            if (isSupported) {
                Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getEmptyParameters();
                if (LONG_EXPOSURE_NR_ON.equals(value)) {
                    ((CameraEx.ParametersModifier) params.second).setLongExposureNR(true);
                } else {
                    ((CameraEx.ParametersModifier) params.second).setLongExposureNR(false);
                }
                CameraSetting.getInstance().setParameters(params);
            }
        }
    }

    public void release() {
        Log.d(TAG, AppLog.getMethodName());
        saved = false;
    }
}
