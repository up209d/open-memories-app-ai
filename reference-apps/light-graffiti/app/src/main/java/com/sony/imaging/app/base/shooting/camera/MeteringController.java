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
public class MeteringController extends AbstractController {
    private static final String API_NAME = "setMeteringMode";
    public static final String CENTRALMODE = "CENTER";
    private static final String LOG_MSG_GETMETERINGMODE = "getMeteringMode = ";
    private static final String LOG_MSG_GETSUPPORTEDMETERINGMODES = "getSupportedMeteringModes = ";
    private static final String LOG_MSG_SETMETERINGMODE = "setMeteringMode = ";
    public static final String MENU_ITEM_ID_METERINGMODE = "MeteringMode";
    public static final String MULTIMODE = "MULTI";
    public static final String SPOTMODE = "SPOT";
    private static final String TAG = "MeteringSetting";
    private static MeteringController mInstance;
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

    protected MeteringController() {
        this.mCameraSetting = null;
        this.mCameraSetting = CameraSetting.getInstance();
        if (mSupportedMeteringModes == null) {
            mSupportedMeteringModes = createSupportedValueArray(this.mCameraSetting.getSupportedParameters());
        }
        setController(this);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraReopened() {
        mSupportedMeteringModes = createSupportedValueArray(this.mCameraSetting.getSupportedParameters());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getEmptyParameters();
        ((CameraEx.ParametersModifier) params.second).setMeteringMode(value);
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_SETMETERINGMODE).append(value);
        Log.i(TAG, strBuilder.toString());
        this.mCameraSetting.setParameters(params);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String itemId) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getParameters();
        String value = ((CameraEx.ParametersModifier) params.second).getMeteringMode();
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_GETMETERINGMODE).append(value);
        Log.i(TAG, strBuilder.toString());
        return value;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
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

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        ArrayList<String> availables = new ArrayList<>();
        List<String> supporteds = getSupportedValue(tag);
        if (supporteds != null) {
            for (String mode : supporteds) {
                if (AvailableInfo.isAvailable(API_NAME, mode)) {
                    availables.add(mode);
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
