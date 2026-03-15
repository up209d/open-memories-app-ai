package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.BaseBackUpKey;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.media.MediaRecorder;
import com.sony.scalar.sysutil.ScalarProperties;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class FlashController extends ShootingModeController implements CompensationControllable {
    private static final String API_NAME_COMP = "setFlashCompensation";
    private static final String API_NAME_MODE = "setFlashMode";
    private static final String API_NAME_TYPE = "setFlashType";
    public static final String AUTO = "auto";
    public static final String FILL = "on";
    public static final String FLASHMODE = "FlashMode";
    public static final String FLASHTYPE = "FlashType";
    public static final String FLASH_COMPENSATION = "FlashCompensation";
    public static final String FLASH_REDEYE = "FlashRedEyd";
    public static final String FRONTSYNC = "front-sync";
    public static final String INVISIBLE = "invisible";
    private static final String LOG_ITEMID = " itemId :";
    private static final String LOG_NO_CATEGOLY = "NotFound Category";
    private static final String LOG_VALUE = ", value :";
    public static final String OFF = "off";
    private static final String PF_VERSION = "1.";
    public static final String REARSYN = "rear-sync";
    public static final String RED_EYE_AUTO = "auto";
    public static final String RED_EYE_OFF = "off";
    public static final String RED_EYE_ON = "on";
    public static final String SLOWREARSYN = "slow-rear-sync";
    public static final String SLOWSYN = "slow-sync";
    private static final String TAG = "FlashController";
    public static final String WIRELESS = "wireless";
    private static FlashController mInstance;
    private static boolean mIsInitFlashMode = false;
    private static final String myName = FlashController.class.getSimpleName();
    private static final String[] tags = {CameraNotificationManager.SCENE_MODE, CameraNotificationManager.REC_MODE_CHANGED, CameraNotificationManager.DRIVE_MODE};
    private List<String> mCompensationList = null;
    private List<String> mCompensationListForMovie = null;
    private FlashControllerListener mFlashControllerListener = null;
    private CameraSetting mCamSet = CameraSetting.getInstance();

    public static final String getName() {
        return myName;
    }

    public static FlashController getInstance() {
        if (mInstance == null) {
            new FlashController();
        }
        return mInstance;
    }

    private static void setController(FlashController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected FlashController() {
        setController(this);
    }

    public void setValue(String value) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
        setFlashType(p, value);
        CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.FLASH_CHANGE);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
        if (FLASHMODE.equals(itemId)) {
            if ("off".equals(value) || "auto".equals(value)) {
                setFlashMode(p, value);
            } else {
                setFlashMode(p, "on");
                setFlashType(p, value);
            }
        } else if (FLASH_COMPENSATION.equals(itemId)) {
            setFlashCompensation(p, value);
        } else {
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            Log.e(TAG, builder.replace(0, LOG_NO_CATEGOLY.length(), LOG_NO_CATEGOLY).append(LOG_ITEMID).append(itemId).append(LOG_VALUE).append(value).toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
        }
        CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.FLASH_CHANGE);
    }

    private void setFlashMode(Pair<Camera.Parameters, CameraEx.ParametersModifier> p, String value) {
        ((Camera.Parameters) p.first).setFlashMode(value);
        this.mCamSet.setParameters(p);
        if (!"on".equals(value)) {
            BackUpUtil.getInstance().removePreference(BaseBackUpKey.ID_FLASH_TYPE);
        }
    }

    private void setFlashType(Pair<Camera.Parameters, CameraEx.ParametersModifier> p, String value) {
        ((CameraEx.ParametersModifier) p.second).setFlashType(value);
        this.mCamSet.setParameters(p);
        BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_FLASH_TYPE, value);
    }

    private void setFlashCompensation(Pair<Camera.Parameters, CameraEx.ParametersModifier> p, String value) {
        int index = Integer.valueOf(value).intValue();
        ((CameraEx.ParametersModifier) p.second).setFlashCompensation(index);
        this.mCamSet.setParameters(p);
    }

    public String getValue() {
        if (!isMovieMode()) {
            String ret = getFlashMode();
            return ret;
        }
        return "off";
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String itemId) {
        String ret = null;
        if (FLASHMODE.equals(itemId)) {
            if (!isMovieMode()) {
                ret = getFlashMode();
                if ("on".equals(ret)) {
                    ret = getFlashType();
                }
            }
        } else if (FLASH_REDEYE.equals(itemId)) {
            ret = !isMovieMode() ? getFlashRedEye() : "off";
        } else if (FLASH_COMPENSATION.equals(itemId)) {
            if (!isMovieMode()) {
                ret = getFlashCompensation();
            }
        } else {
            Log.e(TAG, LOG_NO_CATEGOLY);
            return null;
        }
        return ret;
    }

    private String getFlashMode() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
        String flashMode = ((Camera.Parameters) p.first).getFlashMode();
        return flashMode;
    }

    private String getFlashType() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
        String flashType = ((CameraEx.ParametersModifier) p.second).getFlashType();
        return flashType;
    }

    private String getFlashRedEye() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
        String redEye = ((CameraEx.ParametersModifier) p.second).getRedEyeReductionMode();
        return redEye;
    }

    private String getFlashCompensation() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
        int value = ((CameraEx.ParametersModifier) p.second).getFlashCompensation();
        return String.valueOf(value);
    }

    private void removeUnsupportedValue(List<String> supported) {
        String version = ScalarProperties.getString("version.platform");
        if (version != null && version.indexOf(PF_VERSION) == 0 && !ModeDialDetector.hasModeDial()) {
            supported.remove(WIRELESS);
        }
    }

    public List<String> getSupportedValue() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getSupportedParameters();
        List<String> ret = ((Camera.Parameters) p.first).getSupportedFlashModes();
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ShootingModeController
    public List<String> getSupportedValue(String itemId, int mode) {
        if (!Environment.isMovieAPISupported()) {
            mode = 1;
        }
        List<String> ret = new ArrayList<>();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getSupportedParameters(mode);
        if (FLASHMODE.equals(itemId)) {
            List<String> ret2 = ((Camera.Parameters) p.first).getSupportedFlashModes();
            if (ret2 != null && ret2.contains("on")) {
                ret2.remove("on");
                List<String> fill_supportedList = ((CameraEx.ParametersModifier) p.second).getSupportedFlashTypes();
                for (String item : fill_supportedList) {
                    ret2.add(item);
                }
            }
            removeUnsupportedValue(ret2);
            return ret2;
        }
        if (FLASH_COMPENSATION.equals(itemId)) {
            switch (mode) {
                case 1:
                    List<String> ret3 = this.mCompensationList;
                    return ret3;
                case 2:
                    List<String> ret4 = this.mCompensationListForMovie;
                    return ret4;
                default:
                    return null;
            }
        }
        return ret;
    }

    private List<String> makeSupportedCompensation(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        List<String> list = new ArrayList<>();
        int rawMaxFlashCompensation = ((CameraEx.ParametersModifier) params.second).getMaxFlashCompensation();
        int rawMinFlashCompensation = ((CameraEx.ParametersModifier) params.second).getMinFlashCompensation();
        if (rawMaxFlashCompensation == 0 && rawMinFlashCompensation == 0) {
            return null;
        }
        for (int i = rawMinFlashCompensation; i <= rawMaxFlashCompensation; i++) {
            list.add(Integer.toString(i));
        }
        return list;
    }

    public List<String> getSupportedValueInMode(int mode) {
        if (!Environment.isMovieAPISupported()) {
            mode = 1;
        }
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCamSet.getSupportedParameters(mode);
        if (params == null) {
            return null;
        }
        int max = ((CameraEx.ParametersModifier) params.second).getMaxFlashCompensation();
        int min = ((CameraEx.ParametersModifier) params.second).getMinFlashCompensation();
        if (max == 0 && min == 0) {
            return null;
        }
        List<String> list = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            list.add(Integer.toString(i));
        }
        return list;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        if (FLASHMODE.equals(tag)) {
            if (isMovieMode()) {
                return false;
            }
            boolean ret = AvailableInfo.isAvailable(API_NAME_MODE, null);
            if (!ret) {
                return AvailableInfo.isAvailable(API_NAME_MODE, null, API_NAME_TYPE, null);
            }
            return ret;
        }
        if (!FLASH_COMPENSATION.equals(tag) || isMovieMode()) {
            return false;
        }
        return AvailableInfo.isAvailable(API_NAME_COMP, null);
    }

    protected void removeUnavailableValue(List<String> availables) {
        String expMode = ExposureModeController.getInstance().getValue(ExposureModeController.EXPOSURE_MODE);
        if (ExposureModeController.PROGRAM_AUTO_MODE.equals(expMode) || "Aperture".equals(expMode) || "Shutter".equals(expMode) || ExposureModeController.MANUAL_MODE.equals(expMode)) {
            availables.remove("off");
        }
    }

    protected void setDefaultValueEx() {
        setDefaultValueEx(null);
    }

    protected void setDefaultValueEx(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        String expMode = ExposureModeController.getInstance().getValue(ExposureModeController.EXPOSURE_MODE);
        if (ExposureModeController.PROGRAM_AUTO_MODE.equals(expMode) || "Aperture".equals(expMode) || "Shutter".equals(expMode) || ExposureModeController.MANUAL_MODE.equals(expMode)) {
            String flashMode = getFlashMode();
            if ("off".equals(flashMode)) {
                AvailableInfo.update();
                String flashType = BackUpUtil.getInstance().getPreferenceString(BaseBackUpKey.ID_FLASH_TYPE, FRONTSYNC);
                if (!AvailableInfo.isAvailable(API_NAME_MODE, "on", API_NAME_TYPE, flashType)) {
                    if (AvailableInfo.isAvailable(API_NAME_MODE, "on", API_NAME_TYPE, FRONTSYNC)) {
                        flashType = FRONTSYNC;
                    } else {
                        flashType = null;
                    }
                }
                if (flashType != null) {
                    if (params == null) {
                        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
                        setFlashMode(p, "on");
                        setFlashType(p, flashType);
                    } else {
                        ((Camera.Parameters) params.first).setFlashMode("on");
                        ((CameraEx.ParametersModifier) params.second).setFlashType(flashType);
                    }
                }
            }
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        List<String> supporteds;
        AvailableInfo.update();
        if (tag == null) {
            tag = FLASHMODE;
        }
        int currentMode = CameraSetting.getInstance().getCurrentMode();
        ArrayList<String> availables = new ArrayList<>();
        if (tag.equals(FLASHMODE)) {
            List<String> supporteds2 = getSupportedValue(tag, currentMode);
            if (supporteds2 != null) {
                for (String mode : supporteds2) {
                    if ("off".equals(mode) || "auto".equals(mode)) {
                        if (AvailableInfo.isAvailable(API_NAME_MODE, mode)) {
                            availables.add(mode);
                        }
                    } else if (AvailableInfo.isAvailable(API_NAME_MODE, "on", API_NAME_TYPE, mode)) {
                        availables.add(mode);
                    }
                }
                removeUnavailableValue(availables);
            }
        } else if (tag.equals(FLASH_COMPENSATION) && (supporteds = getSupportedValue(tag, currentMode)) != null) {
            for (String mode2 : supporteds) {
                if (AvailableInfo.isAvailable(API_NAME_COMP, mode2)) {
                    availables.add(mode2);
                }
            }
        }
        if (availables.isEmpty()) {
            return null;
        }
        return availables;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public boolean isUnavailableSceneFactor(String tag) {
        if (FLASHMODE.equals(tag)) {
            boolean mode = isUnavailableAPISceneFactor(API_NAME_MODE, null);
            return mode;
        }
        if (FLASHTYPE.equals(tag)) {
            boolean mode2 = isUnavailableAPISceneFactor(API_NAME_TYPE, null);
            return mode2;
        }
        if (FLASH_COMPENSATION.equals(tag)) {
            boolean mode3 = isUnavailableAPISceneFactor(API_NAME_COMP, null);
            return mode3;
        }
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        Log.w(TAG, builder.replace(0, tag.length(), tag).append("").toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        return false;
    }

    public float getmStep() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCamSet.getSupportedParameters();
        float step = ((CameraEx.ParametersModifier) params.second).getFlashCompensationStep();
        return step;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.CompensationControllable
    public float getExternalCompensation() {
        if (isMovieMode()) {
            return 0.0f;
        }
        String strValue = getFlashCompensation();
        int index = Integer.valueOf(strValue).intValue();
        float value = index * getmStep();
        float ret = roundExposureCompensationValue(value);
        return ret;
    }

    public float calcFlashCompensationValue(int index) {
        float value = index * getmStep();
        return roundExposureCompensationValue(value);
    }

    protected float roundExposureCompensationValue(float value) {
        BigDecimal bi = new BigDecimal(String.valueOf(value));
        return bi.setScale(1, 4).floatValue();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ShootingModeController, com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        int ret = super.getCautionIndex(itemId);
        if (itemId.equals(FLASHMODE)) {
            if (isMovieMode()) {
                return -1;
            }
            return ret;
        }
        if (itemId.equals(FLASH_COMPENSATION) && isMovieMode()) {
            return -1;
        }
        return ret;
    }

    public static void setInitFlashModeFlg(boolean initFlg) {
        mIsInitFlashMode = initFlg;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        Log.i(TAG, "onGetInitParameters");
        if (this.mCompensationList == null) {
            this.mCompensationList = makeSupportedCompensation(this.mCamSet.getSupportedParameters(1));
        }
        if (mIsInitFlashMode) {
            setDefaultValueEx(params);
            mIsInitFlashMode = false;
        }
        if (this.mFlashControllerListener == null) {
            this.mFlashControllerListener = new FlashControllerListener();
            CameraNotificationManager.getInstance().setNotificationListener(this.mFlashControllerListener);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetTermParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        Log.i(TAG, "onGetTermParameters");
        if (this.mFlashControllerListener != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mFlashControllerListener);
            this.mFlashControllerListener = null;
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitRecorderParameters(MediaRecorder.Parameters params) {
        if (this.mCompensationListForMovie == null) {
            this.mCompensationListForMovie = makeSupportedCompensation(this.mCamSet.getSupportedParameters(2));
        }
    }

    /* loaded from: classes.dex */
    private class FlashControllerListener implements NotificationListener {
        private FlashControllerListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return FlashController.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (CameraNotificationManager.SCENE_MODE.equals(tag) || CameraNotificationManager.REC_MODE_CHANGED.equals(tag) || CameraNotificationManager.DRIVE_MODE.equals(tag)) {
                FlashController.this.setDefaultValueEx();
            }
        }
    }
}
