package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ApscModeController extends AbstractController2<String> {
    private static final String API_NAME = "setApscMode";
    public static final String AUTO = "auto";
    private static final String MSG_AVAIABLE = "isAvailable : ";
    private static final String MSG_COMMA = ", ";
    private static final String MSG_GET_VALUE = "getValue : ";
    private static final String MSG_SET_VALUE = "setValue : ";
    private static final String MSG_TAG = "tag : ";
    private static final String MSG_VALUE = "value : ";
    private static final int NUM_SUPPORTED_VALUE;
    public static final String OFF = "off";
    public static final String ON = "on";
    protected static final String TAG = "ApscModeController";
    public static final String TAG_APSC_MODE_CONDITION = "apsc_mode_condition";
    public static final String TAG_APSC_MODE_SETTING = "apsc_mode_setting";
    public static final String UNKNOWN = "unknown";
    private static final ArrayList<String> VALUES = new ArrayList<>();
    private static ApscModeController mInstance;
    private static final String myName;
    protected CameraNotificationManager mNotifier = CameraNotificationManager.getInstance();
    protected CameraSetting mCamSet = CameraSetting.getInstance();

    static {
        VALUES.add("on");
        VALUES.add("auto");
        VALUES.add("off");
        NUM_SUPPORTED_VALUE = VALUES.size();
        myName = ApscModeController.class.getSimpleName();
    }

    public static final String getName() {
        return myName;
    }

    public static ApscModeController getInstance() {
        if (mInstance == null) {
            mInstance = new ApscModeController();
        }
        return mInstance;
    }

    private static void setController(ApscModeController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected ApscModeController() {
        setController(this);
    }

    public static boolean isSupportedByPf() {
        return 1 <= CameraSetting.getPfApiVersion();
    }

    public void resetToInitial() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> initial;
        if (isSupportedByPf() && (initial = this.mCamSet.getInitialParameters()) != null) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCamSet.getEmptyParameters();
            ((CameraEx.ParametersModifier) params.second).setApscMode(((CameraEx.ParametersModifier) initial.second).getApscMode());
            this.mCamSet.setParameters(params);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        if (!isSupportedByPf()) {
            return false;
        }
        boolean result = ((CameraEx.ParametersModifier) this.mCamSet.getSupportedParameters().second).isApscModeSupported();
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), MSG_AVAIABLE).append(result);
        Log.d(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        return result;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController2, com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        if (isAvailable(tag)) {
            return VALUES;
        }
        return null;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController2, com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        if (!isAvailable(tag)) {
            return null;
        }
        ArrayList<String> availables = new ArrayList<>(NUM_SUPPORTED_VALUE);
        AvailableInfo.update();
        if (AvailableInfo.isAvailable(API_NAME, null)) {
            for (int i = 0; i < NUM_SUPPORTED_VALUE; i++) {
                if (AvailableInfo.isAvailable(API_NAME, convertApp2PF(VALUES.get(i)))) {
                    availables.add(VALUES.get(i));
                }
            }
        }
        if (availables.size() == 0) {
            return null;
        }
        return availables;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController2
    public String getPFValue(String tag) {
        String value = null;
        if (TAG_APSC_MODE_SETTING.equals(tag)) {
            if (isSupportedByPf()) {
                value = ((CameraEx.ParametersModifier) this.mCamSet.getParameters().second).getApscMode();
            }
        } else if (TAG_APSC_MODE_CONDITION.equals(tag) && isSupportedByPf()) {
            String pfSetting = getPFValue(TAG_APSC_MODE_SETTING);
            if ("auto".equals(pfSetting)) {
                pfSetting = this.mCamSet.mAutoApscMode;
            }
            value = pfSetting;
        }
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), MSG_GET_VALUE).append(MSG_TAG).append(tag).append(", ").append(MSG_VALUE).append(value);
        Log.d(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        return value;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController2
    public void setValueToPF(String tag, String value) {
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), MSG_SET_VALUE).append(MSG_TAG).append(tag).append(", ").append(MSG_VALUE).append(value);
        Log.d(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        if (TAG_APSC_MODE_SETTING.equals(tag) && isSupportedByPf()) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCamSet.getEmptyParameters();
            ((CameraEx.ParametersModifier) params.second).setApscMode(value);
            this.mCamSet.setParameters(params);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController2
    public String convertPF2App(String pfValue) {
        if (!isSupportedByPf()) {
            return "unknown";
        }
        if ("on".equals(pfValue)) {
            return "on";
        }
        if ("auto".equals(pfValue)) {
            return "auto";
        }
        if (!"off".equals(pfValue)) {
            return "unknown";
        }
        return "off";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController2
    public String convertApp2PF(String appValue) {
        if (!isSupportedByPf()) {
            return null;
        }
        if ("on".equals(appValue)) {
            return "on";
        }
        if ("auto".equals(appValue)) {
            return "auto";
        }
        if (!"off".equals(appValue)) {
            return null;
        }
        return "off";
    }
}
