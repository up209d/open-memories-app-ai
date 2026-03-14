package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class AutoFocusModeController extends AbstractController {
    public static final String AF_A = "af-a";
    public static final String AF_C = "af-c";
    public static final String AF_MOVIE = "movie";
    public static final String AF_S = "af-s";
    private static final String API_NAME = "setAutoFocusMode";
    private static final String LOG_MSG_FOCUSMODE = "focusMode = ";
    private static final String LOG_MSG_GETAUTOFOCUSMODE = "getAutoFocusMode = ";
    private static final String LOG_MSG_GETSUPPORTEDAUTOFOCUSMODES = "getSupportedAutoFocusModes = ";
    private static final String LOG_MSG_INVALID_ARGUMENT = "Invalid argument. ";
    private static final String LOG_MSG_SETAUTOFOCUSMODE = "setAutoFocusMode = ";
    private static final String TAG = "AutoFocusModeController";
    public static final String TAG_AUTO_FOCUS_MODE = "AutoFocusMode";
    private static AutoFocusModeController mInstance;
    private CameraSetting mCamSet = CameraSetting.getInstance();
    private static final StringBuilder STRBUILD = new StringBuilder();
    private static final String myName = AutoFocusModeController.class.getSimpleName();

    public static final String getName() {
        return myName;
    }

    public static AutoFocusModeController getInstance() {
        if (mInstance == null) {
            new AutoFocusModeController();
        }
        return mInstance;
    }

    private static void setController(AutoFocusModeController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected AutoFocusModeController() {
        setController(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String convertApp2PF(String focusMode) {
        if (AF_A.equals(focusMode)) {
            return AF_A;
        }
        if ("af-s".equals(focusMode)) {
            return "af-s";
        }
        if ("af-c".equals(focusMode)) {
            if (Environment.isMovieAPISupported() && this.mCamSet.getCurrentMode() == 2) {
                return "movie";
            }
            return "af-c";
        }
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_INVALID_ARGUMENT).append(LOG_MSG_FOCUSMODE).append(focusMode);
        Log.w(TAG, STRBUILD.toString());
        return null;
    }

    protected String convertPF2App(String focusMode) {
        if (AF_A.equals(focusMode)) {
            return AF_A;
        }
        if ("af-s".equals(focusMode)) {
            return "af-s";
        }
        if ("af-c".equals(focusMode)) {
            return "af-c";
        }
        if (Environment.isMovieAPISupported() && "movie".equals(focusMode)) {
            return "af-c";
        }
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_INVALID_ARGUMENT).append(LOG_MSG_FOCUSMODE).append(focusMode);
        Log.w(TAG, STRBUILD.toString());
        return null;
    }

    public void setValue(String value) {
        String mode = convertApp2PF(value);
        Pair<Camera.Parameters, CameraEx.ParametersModifier> emptyParam = this.mCamSet.getEmptyParameters();
        ((CameraEx.ParametersModifier) emptyParam.second).setAutoFocusMode(mode);
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_SETAUTOFOCUSMODE).append(mode);
        Log.i(TAG, STRBUILD.toString());
        this.mCamSet.setParameters(emptyParam);
    }

    public String getValue() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
        String value = ((CameraEx.ParametersModifier) p.second).getAutoFocusMode();
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_GETAUTOFOCUSMODE).append(value);
        Log.i(TAG, STRBUILD.toString());
        return convertPF2App(value);
    }

    public List<String> getSupportedValue() {
        int currentMode = CameraSetting.getInstance().getCurrentMode();
        return getSupportedValue(currentMode);
    }

    public List<String> getSupportedValue(int mode) {
        if (!Environment.isMovieAPISupported()) {
            mode = 1;
        }
        List<String> supportValues = ((CameraEx.ParametersModifier) this.mCamSet.getSupportedParameters(mode).second).getSupportedAutoFocusModes();
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_GETSUPPORTEDAUTOFOCUSMODES).append(supportValues);
        Log.i(TAG, STRBUILD.toString());
        if (supportValues == null) {
            return null;
        }
        List<String> ret = new ArrayList<>();
        for (String s : supportValues) {
            String retVal = convertPF2App(s);
            if (retVal != null) {
                ret.add(retVal);
            }
        }
        if (ret.size() == 0) {
            return null;
        }
        return ret;
    }

    public List<String> getAvailableValue() {
        ArrayList<String> ret = new ArrayList<>();
        List<String> supporteds = getSupportedValue();
        if (supporteds != null) {
            AvailableInfo.update();
            for (String mode : supporteds) {
                if (AvailableInfo.isAvailable(FocusModeController.API_NAME, "auto", API_NAME, convertApp2PF(mode))) {
                    ret.add(mode);
                }
            }
        }
        if (ret.size() == 0) {
            return null;
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        setValue(value);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        return getValue();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return getSupportedValue();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return getAvailableValue();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        List<String> list;
        if (!tag.equals("AutoFocusMode") || (list = getAvailableValue()) == null || list.size() <= 1 || !AvailableInfo.isAvailable(FocusModeController.API_NAME, null, API_NAME, null)) {
            return false;
        }
        return true;
    }
}
