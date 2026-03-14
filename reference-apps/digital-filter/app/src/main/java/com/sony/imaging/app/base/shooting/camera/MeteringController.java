package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class MeteringController extends AbstractController {
    private static final String API_NAME = "setMeteringMode";
    public static final String CENTRALMODE = "CENTER";
    public static final String LARGE = "3x";
    private static final String LOG_MSG_GETMETERINGMODE = "getMeteringMode = ";
    private static final String LOG_MSG_GETMETERINGMODEPOINT = "getMeteringModePoint = ";
    private static final String LOG_MSG_GETMETERINGMODESPOTSIZE = "getMeteringModeSpotSize = ";
    private static final String LOG_MSG_GETSUPPORTEDMETERINGMODEPOINT = "getSupportedMeteringModePoints = ";
    private static final String LOG_MSG_GETSUPPORTEDMETERINGMODES = "getSupportedMeteringModes = ";
    private static final String LOG_MSG_GETSUPPORTEDMETERINGMODESPOTSIZE = "getSupportedMeteringModeSpotSizes = ";
    private static final String LOG_MSG_SETMETERINGMODE = "setMeteringMode = ";
    private static final String LOG_MSG_SETMETERINGMODEPONT = "setMeteringModePoint = ";
    private static final String LOG_MSG_SETMETERINGMODESPOTSIZE = "setMeteringModeSpotSize = ";
    public static final String MENU_ITEM_ID_METERINGMODE = "MeteringMode";
    public static final String MENU_ITEM_ID_METERINGMODEPOINT = "MeteringModePoint";
    public static final String MENU_ITEM_ID_METERING_CENTER_SPOT = "center-spot";
    public static final String MULTIMODE = "MULTI";
    public static final String NONE = "none";
    public static final String SPOTMODE = "SPOT";
    public static final String STANDARD = "std";
    private static final String TAG = "MeteringSetting";
    private static MeteringController mInstance;
    private static List<String> mSupportedMeteringModePoints;
    private static HashMap<String, List<String>> mSupportedMeteringModeSpotSizes;
    private static List<String> mSupportedMeteringModes;
    private CameraSetting mCameraSetting;
    private static final StringBuilderThreadLocal sStringBuilder = new StringBuilderThreadLocal();
    private static final String myName = MeteringController.class.getSimpleName();

    public static final String getName() {
        return myName;
    }

    public static MeteringController getInstance() {
        if (mInstance == null) {
            new MeteringController();
        }
        return mInstance;
    }

    private static void setController(MeteringController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MeteringController() {
        this.mCameraSetting = null;
        this.mCameraSetting = CameraSetting.getInstance();
        if (mSupportedMeteringModes == null) {
            mSupportedMeteringModes = createSupportedValueArray(this.mCameraSetting.getSupportedParameters());
        }
        if (Environment.isMeteringSpotSizeAPISupported() && mSupportedMeteringModeSpotSizes == null) {
            mSupportedMeteringModeSpotSizes = new HashMap<>();
            mSupportedMeteringModeSpotSizes.put(MENU_ITEM_ID_METERING_CENTER_SPOT, createSupportedSpotSizeValueArray(this.mCameraSetting.getSupportedParameters()));
        }
        if (Environment.isMeteringModePointAPISupported() && mSupportedMeteringModePoints == null) {
            mSupportedMeteringModePoints = createSupportedModePointValueArray(this.mCameraSetting.getSupportedParameters());
        }
        setController(this);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraReopened() {
        mSupportedMeteringModes = createSupportedValueArray(this.mCameraSetting.getSupportedParameters());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        if (MENU_ITEM_ID_METERING_CENTER_SPOT.equals(itemId)) {
            setMeteringModeSpotSize(value);
        } else if ("MeteringModePoint".equals(itemId)) {
            setMeteringModePoint(value);
        } else {
            setMeteringMode(value);
        }
    }

    private void setMeteringMode(String value) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getEmptyParameters();
        ((CameraEx.ParametersModifier) params.second).setMeteringMode(value);
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_SETMETERINGMODE).append(value);
        Log.i(TAG, strBuilder.toString());
        this.mCameraSetting.setParameters(params);
    }

    private void setMeteringModeSpotSize(String value) {
        if (Environment.isMeteringSpotSizeAPISupported()) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getEmptyParameters();
            ((CameraEx.ParametersModifier) params.second).setMeteringModeSpotSize(value);
            StringBuilder strBuilder = sStringBuilder.get();
            strBuilder.replace(0, strBuilder.length(), LOG_MSG_SETMETERINGMODESPOTSIZE).append(value);
            Log.i(TAG, strBuilder.toString());
            this.mCameraSetting.setParameters(params);
            return;
        }
        Log.w(TAG, "Not supported setMeteringModeSpotSize");
    }

    private void setMeteringModePoint(String value) {
        if (Environment.isMeteringModePointAPISupported()) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getEmptyParameters();
            ((CameraEx.ParametersModifier) params.second).setMeteringModePoint(value);
            StringBuilder strBuilder = sStringBuilder.get();
            strBuilder.replace(0, strBuilder.length(), LOG_MSG_SETMETERINGMODEPONT).append(value);
            Log.i(TAG, strBuilder.toString());
            this.mCameraSetting.setParameters(params);
            return;
        }
        Log.w(TAG, "Not supported setMeteringModePoint");
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String itemId) {
        if (MENU_ITEM_ID_METERING_CENTER_SPOT.equals(itemId)) {
            String value = getMeteringModeSpotsize();
            return value;
        }
        if ("MeteringModePoint".equals(itemId)) {
            String value2 = getMeteringModePoint();
            return value2;
        }
        String value3 = getMeteringMode();
        return value3;
    }

    private String getMeteringMode() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getParameters();
        String value = ((CameraEx.ParametersModifier) params.second).getMeteringMode();
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_GETMETERINGMODE).append(value);
        Log.i(TAG, strBuilder.toString());
        return value;
    }

    private String getMeteringModeSpotsize() {
        if (!Environment.isMeteringSpotSizeAPISupported()) {
            return null;
        }
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getParameters();
        String value = ((CameraEx.ParametersModifier) params.second).getMeteringModeSpotSize();
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_GETMETERINGMODESPOTSIZE).append(value);
        Log.i(TAG, strBuilder.toString());
        return value;
    }

    private String getMeteringModePoint() {
        if (!Environment.isMeteringModePointAPISupported()) {
            return null;
        }
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getParameters();
        String value = ((CameraEx.ParametersModifier) params.second).getMeteringModePoint();
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_GETMETERINGMODEPOINT).append(value);
        Log.i(TAG, strBuilder.toString());
        return value;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        if (MENU_ITEM_ID_METERING_CENTER_SPOT.equals(tag)) {
            if (!Environment.isMeteringSpotSizeAPISupported() || mSupportedMeteringModeSpotSizes.isEmpty()) {
                return null;
            }
            return mSupportedMeteringModeSpotSizes.get(tag);
        }
        if ("MeteringModePoint".equals(tag)) {
            if (!Environment.isMeteringModePointAPISupported() || mSupportedMeteringModePoints.isEmpty()) {
                return null;
            }
            return mSupportedMeteringModePoints;
        }
        if (mSupportedMeteringModes.isEmpty()) {
            return null;
        }
        return mSupportedMeteringModes;
    }

    private static List<String> createSupportedValueArray(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        List<String> supporteds = ((CameraEx.ParametersModifier) params.second).getSupportedMeteringModes();
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_GETSUPPORTEDMETERINGMODES).append(supporteds);
        Log.i(TAG, strBuilder.toString());
        return supporteds == null ? new ArrayList<>() : supporteds;
    }

    private static List<String> createSupportedSpotSizeValueArray(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        List<String> supporteds = ((CameraEx.ParametersModifier) params.second).getSupportedMeteringModeSpotSizes();
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_GETSUPPORTEDMETERINGMODESPOTSIZE).append(supporteds);
        Log.i(TAG, strBuilder.toString());
        return supporteds == null ? new ArrayList<>() : supporteds;
    }

    private static List<String> createSupportedModePointValueArray(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        List<String> supporteds = ((CameraEx.ParametersModifier) params.second).getSupportedMeteringModePoints();
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_GETSUPPORTEDMETERINGMODEPOINT).append(supporteds);
        Log.i(TAG, strBuilder.toString());
        return supporteds == null ? new ArrayList<>() : supporteds;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        ArrayList<String> availables = new ArrayList<>();
        List<String> supporteds = getSupportedValue(tag);
        if (supporteds != null) {
            if ("MeteringModePoint".equals(tag)) {
                for (String mode : supporteds) {
                    if (AvailableInfo.isAvailable("setMeteringModePoint", mode)) {
                        availables.add(mode);
                    }
                }
            } else if (MENU_ITEM_ID_METERING_CENTER_SPOT.equals(tag)) {
                for (String mode2 : supporteds) {
                    if (AvailableInfo.isAvailable("setMeteringSpotSize", mode2)) {
                        availables.add(mode2);
                    }
                }
            } else {
                for (String mode3 : supporteds) {
                    if (AvailableInfo.isAvailable(API_NAME, mode3)) {
                        availables.add(mode3);
                    }
                }
            }
        }
        if (availables.isEmpty()) {
            return null;
        }
        return availables;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return AvailableInfo.isAvailable(API_NAME, null);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public boolean isUnavailableSceneFactor(String tag) {
        boolean b = isUnavailableAPISceneFactor(API_NAME, null);
        return b;
    }
}
