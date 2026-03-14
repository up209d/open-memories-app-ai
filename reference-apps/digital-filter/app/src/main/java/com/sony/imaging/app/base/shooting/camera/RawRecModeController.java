package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.media.MediaRecorder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class RawRecModeController extends ShootingModeController {
    private static final String API_NAME_RAW_REC_MODE = "setRawRecMode";
    private static final String TAG = "RawCompressController";
    public static final String TAG_RAW_REC_MODE = "RawRecMode";
    public static RawRecModeController mInstance;
    protected CameraSetting mCamSet = CameraSetting.getInstance();
    private static List<String> mSupportedRawRecModeForStill = null;
    private static List<String> mSupportedRawRecModeForMovie = null;
    public static final String DEFAULT_SETTING = "compress";
    public static final List<String> KNOWN_SETTINGS = Arrays.asList(DEFAULT_SETTING, "uncompress");
    private static final String myName = RawRecModeController.class.getSimpleName();
    private static boolean sInitFlag = false;

    public static final String getName() {
        return myName;
    }

    private static void setController(RawRecModeController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected RawRecModeController() {
        setController(this);
    }

    public static RawRecModeController getInstance() {
        if (mInstance == null) {
            mInstance = new RawRecModeController();
        }
        return mInstance;
    }

    public static void inheritRawRecModeFromDiadem() {
        BootFactor factor = BootFactor.get();
        int bootfactor = factor.bootFactor;
        if (bootfactor == 0) {
            sInitFlag = true;
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        if (Environment.isRawRecModeAPISupported()) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> emptyParam = this.mCamSet.getEmptyParameters();
            if (KNOWN_SETTINGS.contains(value)) {
                ((CameraEx.ParametersModifier) emptyParam.second).setRawRecMode(value);
                this.mCamSet.setParameters(emptyParam);
            } else {
                Log.w(TAG, "setValue Invalid argument.   Input value:" + value);
            }
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        if (isMovieMode() || !Environment.isRawRecModeAPISupported()) {
            return null;
        }
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
        String appValue = ((CameraEx.ParametersModifier) p.second).getRawRecMode();
        return appValue;
    }

    public String getDiademValue() {
        if (this.mCamSet.getCamera() == null) {
            Log.e(TAG, "Error. cameraEx is not ready.", new Throwable());
            return null;
        }
        if (!Environment.isRawRecModeAPISupported()) {
            Log.w(TAG, "isRawRecModeAPISupported not supported");
            return null;
        }
        Camera.Parameters p = this.mCamSet.getCamera().getInitialParameters();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> mStillSupportedParams = new Pair<>(p, this.mCamSet.getCamera().createParametersModifier(p));
        return ((CameraEx.ParametersModifier) mStillSupportedParams.second).getRawRecMode();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ShootingModeController
    public List<String> getSupportedValue(String tag, int mode) {
        List<String> supParams;
        if (!Environment.isMovieAPISupported()) {
            mode = 1;
        }
        switch (mode) {
            case 1:
                supParams = mSupportedRawRecModeForStill;
                break;
            case 2:
                supParams = mSupportedRawRecModeForMovie;
                break;
            default:
                supParams = null;
                break;
        }
        if (supParams != null && supParams.isEmpty()) {
            return null;
        }
        return supParams;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        if (!Environment.isRawRecModeAPISupported()) {
            return false;
        }
        AvailableInfo.update();
        boolean ret = AvailableInfo.isAvailable(API_NAME_RAW_REC_MODE, null);
        if (ret) {
            return (MediaNotificationManager.getInstance().isMounted() && AvailableInfo.isFactor("INH_FACTOR_STILL_WRITING")) ? false : true;
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        List<String> availables = new ArrayList<>();
        List<String> supporteds = getSupportedValue(TAG_RAW_REC_MODE);
        if (Environment.isRawRecModeAPISupported() && supporteds != null) {
            AvailableInfo.update();
            for (String s : supporteds) {
                if (AvailableInfo.isAvailable(API_NAME_RAW_REC_MODE, s)) {
                    availables.add(s);
                }
            }
        }
        if (availables.size() == 0) {
            return null;
        }
        return availables;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        if (mSupportedRawRecModeForStill == null) {
            mSupportedRawRecModeForStill = createSupportedValueArray(this.mCamSet.getSupportedParameters(1));
        }
        if (sInitFlag) {
            sInitFlag = false;
            String diademValue = getDiademValue();
            if (!KNOWN_SETTINGS.contains(diademValue)) {
                Log.w(TAG, "setValue Invalid argument. set compress.  Input value:" + diademValue);
                diademValue = DEFAULT_SETTING;
            }
            setValue(null, diademValue);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitRecorderParameters(MediaRecorder.Parameters params) {
        if (mSupportedRawRecModeForMovie == null) {
            mSupportedRawRecModeForMovie = createSupportedValueArray(this.mCamSet.getSupportedParameters(2));
        }
    }

    private static List<String> createSupportedValueArray(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        List<String> pfSupported;
        List<String> supporteds = new ArrayList<>();
        if (Environment.isRawRecModeAPISupported() && (pfSupported = ((CameraEx.ParametersModifier) params.second).getSupportedRawRecModes()) != null) {
            for (String SettingItem : KNOWN_SETTINGS) {
                if (pfSupported.contains(SettingItem)) {
                    supporteds.add(SettingItem);
                }
            }
        }
        return supporteds;
    }
}
