package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class AngleController extends AbstractController {
    public static final String ANGLE_120 = "angle_120";
    public static final String ANGLE_170 = "angle_170";
    private static final String API_NAME = "setAngle";
    private static final String LOG_MSG_GETANGLE = "getAngle = ";
    private static final String LOG_MSG_GETSUPPORTEDANGLE = "getSupportedAngle = ";
    private static final String LOG_MSG_SETANGLE = "setAngle = ";
    private static final int SUPPORTED_PF_VER = 12;
    private static final String TAG = "AngleController";
    public static final String TAG_ANGLE = "Angle";
    private static AngleController mInstance;
    private static List<String> sSupportedValues;
    private CameraSetting mCameraSetting;
    private static final StringBuilderThreadLocal sStringBuilder = new StringBuilderThreadLocal();
    private static final String MY_NAME = AngleController.class.getSimpleName();

    public static final String getName() {
        return MY_NAME;
    }

    public static AngleController getInstance() {
        if (mInstance == null) {
            new AngleController();
        }
        return mInstance;
    }

    public static boolean isSupportedByPF() {
        return Environment.getVersionPfAPI() >= 12;
    }

    private static void setController(AngleController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected AngleController() {
        this.mCameraSetting = null;
        this.mCameraSetting = CameraSetting.getInstance();
        setController(this);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraReopened() {
        sSupportedValues = null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        if (TAG_ANGLE.equals(tag) && isSupportedByPF()) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getEmptyParameters();
            ((CameraEx.ParametersModifier) params.second).setAngle(value);
            StringBuilder strBuilder = sStringBuilder.get();
            strBuilder.replace(0, strBuilder.length(), LOG_MSG_SETANGLE).append(value);
            Log.i(TAG, strBuilder.toString());
            this.mCameraSetting.setParameters(params);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        if (!TAG_ANGLE.equals(tag) || !isSupportedByPF()) {
            return null;
        }
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getParameters();
        String value = ((CameraEx.ParametersModifier) params.second).getAngle();
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_GETANGLE).append(value);
        Log.i(TAG, strBuilder.toString());
        return value;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        if (TAG_ANGLE.equals(tag) && isSupportedByPF()) {
            List<String> supportedValues = getSupportedValueArray(this.mCameraSetting.getSupportedParameters());
            StringBuilder STRBUILD = sStringBuilder.get();
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_GETSUPPORTEDANGLE).append(supportedValues);
            Log.i(TAG, STRBUILD.toString());
            if (supportedValues.isEmpty()) {
                return null;
            }
            return supportedValues;
        }
        return null;
    }

    private static List<String> getSupportedValueArray(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        if (sSupportedValues == null) {
            List<String> supporteds = ((CameraEx.ParametersModifier) params.second).getSupportedAngle();
            if (supporteds == null) {
                sSupportedValues = new ArrayList();
            } else {
                sSupportedValues = supporteds;
            }
        }
        return sSupportedValues;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        List<String> supporteds = getSupportedValue(tag);
        if (supporteds == null) {
            return null;
        }
        AvailableInfo.update();
        ArrayList<String> availables = new ArrayList<>();
        for (String mode : supporteds) {
            if (AvailableInfo.isAvailable(API_NAME, mode)) {
                availables.add(mode);
            }
        }
        if (availables.isEmpty()) {
            return null;
        }
        return availables;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        if (!TAG_ANGLE.equals(tag) || !isSupportedByPF()) {
            return false;
        }
        AvailableInfo.update();
        return AvailableInfo.isAvailable(API_NAME, null);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public boolean isUnavailableSceneFactor(String tag) {
        if (!TAG_ANGLE.equals(tag) || !isSupportedByPF()) {
            return true;
        }
        AvailableInfo.update();
        return isUnavailableAPISceneFactor(API_NAME, null);
    }
}
