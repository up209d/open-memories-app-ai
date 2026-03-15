package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.media.MediaRecorder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class SilentShutterController extends ShootingModeController {
    private static final String API_NAME_SILENT_SHOOTING = "setSilentShutterMode";
    private static final String LOG_MSG_INVALID_ARGUMENT = "Invalid argument. ";
    private static final String TAG = "SilentShutterController";
    public static final String TAG_SILENT_SHUTTER = "SilentShutter";
    public static final String TAG_SILENT_SHUTTER_OFF = "off";
    public static final String TAG_SILENT_SHUTTER_OFF_PF = "False";
    public static final String TAG_SILENT_SHUTTER_ON = "on";
    public static final String TAG_SILENT_SHUTTER_ON_PF = "True";
    public static SilentShutterController mInstance;
    protected CameraSetting mCamSet = CameraSetting.getInstance();
    private static final StringBuilderThreadLocal sStringBuilder = new StringBuilderThreadLocal();
    private static List<String> mSupportedSilentShutterForStill = null;
    private static List<String> mSupportedSilentShutterForMovie = null;
    private static HashMap<String, String> map = null;
    private static final String myName = SilentShutterController.class.getSimpleName();

    /* JADX INFO: Access modifiers changed from: protected */
    public static void creatAppToPFTable() {
        map = new HashMap<>();
        map.put("on", TAG_SILENT_SHUTTER_ON_PF);
        map.put("off", TAG_SILENT_SHUTTER_OFF_PF);
    }

    public static final String getName() {
        return myName;
    }

    private static void setController(SilentShutterController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SilentShutterController() {
        setController(this);
        creatAppToPFTable();
    }

    public static SilentShutterController getInstance() {
        if (mInstance == null) {
            mInstance = new SilentShutterController();
        }
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        if (Environment.isSilentShutterAPISupported() && TAG_SILENT_SHUTTER.equals(tag)) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> emptyParam = this.mCamSet.getEmptyParameters();
            if ("on".equals(value)) {
                ((CameraEx.ParametersModifier) emptyParam.second).setSilentShutterMode(true);
            } else if ("off".equals(value)) {
                ((CameraEx.ParametersModifier) emptyParam.second).setSilentShutterMode(false);
            } else {
                Log.w(TAG, LOG_MSG_INVALID_ARGUMENT);
                return;
            }
            this.mCamSet.setParameters(emptyParam);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        if (isMovieMode() || !Environment.isSilentShutterAPISupported() || getSupportedValue(tag) == null) {
            return null;
        }
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
        boolean pfValue = ((CameraEx.ParametersModifier) p.second).isSilentShutterMode();
        if (pfValue) {
            return "on";
        }
        return "off";
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ShootingModeController
    public List<String> getSupportedValue(String tag, int mode) {
        List<String> supParams;
        if (!Environment.isMovieAPISupported()) {
            mode = 1;
        }
        switch (mode) {
            case 1:
                supParams = mSupportedSilentShutterForStill;
                break;
            case 2:
                supParams = mSupportedSilentShutterForMovie;
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
    public List<String> getAvailableValue(String tag) {
        List<String> availables = new ArrayList<>();
        List<String> supporteds = getSupportedValue(TAG_SILENT_SHUTTER);
        if (Environment.isSilentShutterAPISupported() && supporteds != null) {
            AvailableInfo.update();
            for (String s : supporteds) {
                if (AvailableInfo.isAvailable(API_NAME_SILENT_SHOOTING, map.get(s))) {
                    availables.add(s);
                }
            }
        }
        if (availables.size() == 0) {
            return null;
        }
        return availables;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        if (!Environment.isSilentShutterAPISupported()) {
            return false;
        }
        AvailableInfo.update();
        boolean ret = AvailableInfo.isAvailable(API_NAME_SILENT_SHOOTING, null);
        if (ret) {
            return (MediaNotificationManager.getInstance().isMounted() && AvailableInfo.isFactor("INH_FACTOR_STILL_WRITING")) ? false : true;
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        if (mSupportedSilentShutterForStill == null) {
            mSupportedSilentShutterForStill = createSupportedValueArray(this.mCamSet.getSupportedParameters(1));
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitRecorderParameters(MediaRecorder.Parameters params) {
        if (mSupportedSilentShutterForMovie == null) {
            mSupportedSilentShutterForMovie = createSupportedValueArray(this.mCamSet.getSupportedParameters(2));
        }
    }

    private static List<String> createSupportedValueArray(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        ArrayList<String> supporteds = new ArrayList<>();
        if (Environment.isSilentShutterAPISupported() && ((CameraEx.ParametersModifier) params.second).isSupportedSilentShutterMode()) {
            supporteds.add("on");
            supporteds.add("off");
        }
        return supporteds;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ShootingModeController, com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        if (!itemId.equals(TAG_SILENT_SHUTTER)) {
            return 0;
        }
        if (2 != ExecutorCreator.getInstance().getRecordingMode() && 8 != ExecutorCreator.getInstance().getRecordingMode()) {
            return 0;
        }
        return -1;
    }
}
