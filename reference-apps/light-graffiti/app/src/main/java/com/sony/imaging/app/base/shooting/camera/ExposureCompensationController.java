package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ExposureCompensationController extends AbstractController implements CompensationControllable {
    private static final String API_NAME = "setExposureCompensation";
    private static final int EV_RANGE_PULS_MINUS_3_MAX = 9;
    private static final int EV_RANGE_PULS_MINUS_3_MIN = -9;
    private static final float EV_STEP_0_5 = 0.5f;
    public static final String EXPOSURE_COMPENSATION = "ExposureCompensation";
    private static final String LOG_MSG_COMMA = ", ";
    private static final String LOG_MSG_GETEXPOSURECOMPENSATION = "getExposureCompensation = ";
    private static final String LOG_MSG_GETEXPOSURECOMPENSATIONSTEP = "getExposureCompensationStep = ";
    private static final String LOG_MSG_GETMAXEXPOSURECOMPENSATION = "getMaxExposureCompensation = ";
    private static final String LOG_MSG_GETMINEXPOSURECOMPENSATION = "getMinExposureCompensation = ";
    private static final String LOG_MSG_SETEXPOSURECOMPENSATION = "setExposureCompensation = ";
    public static final int MOVIE = 2;
    public static final int STILL = 1;
    private static final String TAG = "ExposureCompensationController";
    private static ExposureCompensationController mInstance;
    private static int mRawMaxExposureCompensation;
    private static int mRawMinExposureCompensation;
    private static List<String> mSupportedExposureCompensations;
    private CameraSetting mCameraSetting;
    private ExposureCompensationListener mExposureCompensationListener = null;
    private static final StringBuilderThreadLocal sStringBuilder = new StringBuilderThreadLocal();
    private static boolean mHasEvDial = false;
    private static boolean mIsBoot = false;
    private static boolean mTurnedEvDialButNotSetting = false;
    private static EvCacheInfo mCacheInfo = null;
    private static final String myName = ExposureCompensationController.class.getSimpleName();
    private static final String[] tags = {CameraNotificationManager.SCENE_MODE, CameraNotificationManager.ISO_SENSITIVITY, CameraNotificationManager.REC_MODE_CHANGED};

    public static final String getName() {
        return myName;
    }

    public static ExposureCompensationController getInstance() {
        if (mInstance == null) {
            new ExposureCompensationController();
        }
        return mInstance;
    }

    private static void setController(ExposureCompensationController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ExposureCompensationController() {
        this.mCameraSetting = null;
        this.mCameraSetting = CameraSetting.getInstance();
        createSupported();
        setController(this);
    }

    protected void createSupported() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getSupportedParameters();
        if (mSupportedExposureCompensations == null) {
            mRawMaxExposureCompensation = ((Camera.Parameters) params.first).getMaxExposureCompensation();
            mRawMinExposureCompensation = ((Camera.Parameters) params.first).getMinExposureCompensation();
            StringBuilder strBuilder = sStringBuilder.get();
            strBuilder.replace(0, strBuilder.length(), LOG_MSG_GETMINEXPOSURECOMPENSATION).append(mRawMinExposureCompensation).append(", ").append(LOG_MSG_GETMAXEXPOSURECOMPENSATION).append(mRawMaxExposureCompensation);
            Log.i(TAG, strBuilder.toString());
            mSupportedExposureCompensations = createSupportedValueArray(params);
            if (CameraSetting.getPfApiVersion() >= 2) {
                mHasEvDial = EVDialDetector.hasEVDial();
            }
        }
        setController(this);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraReopened() {
        mSupportedExposureCompensations = null;
        createSupported();
        if (!isMovieMode()) {
            mCacheInfo.setCameraReopenFromMovie();
        }
    }

    public float getExposureCompensationValue() {
        float value = getExposureCompensationIndex() * getExposureCompensationStep();
        return CameraSetting.roundExposureCompensationValue(value);
    }

    public float calcExposureCompensationValue(int index) {
        float value = index * getExposureCompensationStep();
        return CameraSetting.roundExposureCompensationValue(value);
    }

    public int getExposureCompensationIndex() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getParameters();
        int index = ((Camera.Parameters) params.first).getExposureCompensation();
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_GETEXPOSURECOMPENSATION).append(index);
        Log.i(TAG, strBuilder.toString());
        return index;
    }

    public float getExposureCompensationStep() {
        if (4 == Environment.DEVICE_TYPE) {
            return 0.333333f;
        }
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getSupportedParameters();
        float step = ((Camera.Parameters) params.first).getExposureCompensationStep();
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_GETEXPOSURECOMPENSATIONSTEP).append(step);
        Log.i(TAG, strBuilder.toString());
        return step;
    }

    public void incrementExposureCompensation() {
        int now = getExposureCompensationIndex();
        int value = Math.min(now + 1, mRawMaxExposureCompensation);
        setExposureCompensation(value);
    }

    public void decrementExposureCompensation() {
        int now = getExposureCompensationIndex();
        int value = Math.max(now - 1, mRawMinExposureCompensation);
        setExposureCompensation(value);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setExposureCompensation(int value) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getEmptyParameters();
        ((Camera.Parameters) params.first).setExposureCompensation(value);
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_SETEXPOSURECOMPENSATION).append(value);
        Log.i(TAG, strBuilder.toString());
        CameraSetting.getInstance().setParameters(params);
        mCacheInfo.clearCache();
    }

    private static List<String> createSupportedValueArray(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        List<String> list = new ArrayList<>();
        for (int i = mRawMinExposureCompensation; i <= mRawMaxExposureCompensation; i++) {
            list.add(Integer.toString(i));
        }
        return list;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        int valueIndex = Integer.parseInt(value);
        setExposureCompensation(valueIndex);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        return Integer.toString(getExposureCompensationIndex());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        if ("ExposureCompensation".equals(tag)) {
            if (!mHasEvDial) {
                if (mSupportedExposureCompensations.isEmpty()) {
                    return null;
                }
                List<String> list = mSupportedExposureCompensations;
                return list;
            }
            Pair<Integer, Integer> range = getSupportedRange(1);
            if (((Integer) range.first).intValue() <= 9 && ((Integer) range.second).intValue() >= EV_RANGE_PULS_MINUS_3_MIN && getExposureCompensationStep() != EV_STEP_0_5) {
                return null;
            }
            List<String> list2 = mSupportedExposureCompensations;
            return list2;
        }
        if (mSupportedExposureCompensations.isEmpty()) {
            return null;
        }
        List<String> list3 = mSupportedExposureCompensations;
        return list3;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        AvailableInfo.update();
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
        boolean ret = isExposureCompensationAvailable();
        if (ret && isEvDialPreference()) {
            return false;
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public boolean isUnavailableSceneFactor(String tag) {
        boolean b = isUnavailableAPISceneFactor(API_NAME, null);
        return b;
    }

    protected boolean isEvDialPreference() {
        if (!mHasEvDial) {
            return false;
        }
        Pair<Integer, Integer> range = getSupportedRange(1);
        if (((Integer) range.first).intValue() <= 9 && ((Integer) range.second).intValue() >= EV_RANGE_PULS_MINUS_3_MIN && getExposureCompensationStep() != EV_STEP_0_5) {
            return true;
        }
        int ev = EVDialDetector.getEVDialPosition();
        if (ev == 0) {
            return false;
        }
        return true;
    }

    protected Pair<Integer, Integer> getSupportedRange(int mode) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params;
        int max = 0;
        int min = 0;
        if (2 == mode) {
            params = this.mCameraSetting.getSupportedParameters(2);
        } else {
            params = this.mCameraSetting.getSupportedParameters(1);
        }
        if (params != null) {
            max = ((Camera.Parameters) params.first).getMaxExposureCompensation();
            min = ((Camera.Parameters) params.first).getMinExposureCompensation();
        }
        return new Pair<>(Integer.valueOf(max), Integer.valueOf(min));
    }

    public boolean isExposureCompensationAvailable() {
        boolean ret = true;
        if (CameraSetting.getPfApiVersion() >= 2) {
            ret = AvailableInfo.isAvailable(API_NAME, null);
        } else {
            ExposureModeController emc = ExposureModeController.getInstance();
            if (emc.isScnMode()) {
                return false;
            }
            String expMode = emc.getValue(null);
            if (ExposureModeController.INTELLIGENT_AUTO_MODE.equals(expMode)) {
                return false;
            }
            if (ExposureModeController.MANUAL_MODE.equals(expMode)) {
                ISOSensitivityController isoctrl = ISOSensitivityController.getInstance();
                String isoValue = isoctrl.getValue();
                List<String> available = isoctrl.getAvailableValue(null);
                if (!ISOSensitivityController.ISO_AUTO.equals(isoValue)) {
                    ret = available.indexOf(ISOSensitivityController.ISO_AUTO) != -1 ? false : false;
                }
            }
        }
        return ret;
    }

    protected boolean isModeDialAvailable() {
        if (!ModeDialDetector.hasModeDial()) {
            return true;
        }
        boolean ret = ExposureModeController.getInstance().isValidDialPosition();
        return ret;
    }

    public boolean setEvDialValue(int position) {
        return setEvDialValue(position, null);
    }

    public boolean setEvDialValue(int position, Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        boolean ret = false;
        if (mHasEvDial) {
            mTurnedEvDialButNotSetting = true;
            if (isExposureCompensationAvailable()) {
                if (!isEvDialPositionAvailable(position)) {
                    if (position > mRawMaxExposureCompensation) {
                        position = mRawMaxExposureCompensation;
                    } else if (position < mRawMinExposureCompensation) {
                        position = mRawMinExposureCompensation;
                    }
                } else {
                    mTurnedEvDialButNotSetting = false;
                }
                if (position != getExposureCompensationIndex()) {
                    if (params == null) {
                        setExposureCompensation(position);
                    } else {
                        ((Camera.Parameters) params.first).setExposureCompensation(position);
                    }
                }
                ret = true;
            }
            mCacheInfo.clearCache();
        }
        return ret;
    }

    protected boolean isEvDialPositionAvailable(int position) {
        if (mSupportedExposureCompensations == null || mSupportedExposureCompensations.size() <= 0 || mSupportedExposureCompensations.indexOf(Integer.toString(position)) == -1) {
            return false;
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.CompensationControllable
    public float getExternalCompensation() {
        return getExposureCompensationValue();
    }

    public List<String> getSupportedValueInMode(int mode) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params;
        List<String> list = null;
        if (2 == mode) {
            params = this.mCameraSetting.getSupportedParameters(2);
        } else {
            params = this.mCameraSetting.getSupportedParameters(1);
        }
        if (params != null) {
            int max = ((Camera.Parameters) params.first).getMaxExposureCompensation();
            int min = ((Camera.Parameters) params.first).getMinExposureCompensation();
            list = new ArrayList<>();
            for (int i = min; i <= max; i++) {
                list.add(Integer.toString(i));
            }
        }
        return list;
    }

    /* loaded from: classes.dex */
    private class ExposureCompensationListener implements NotificationListener {
        private ExposureCompensationListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return ExposureCompensationController.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (ExposureCompensationController.mHasEvDial && ExposureCompensationController.mTurnedEvDialButNotSetting && (CameraNotificationManager.SCENE_MODE.equals(tag) || CameraNotificationManager.ISO_SENSITIVITY.equals(tag) || CameraNotificationManager.REC_MODE_CHANGED.equals(tag))) {
                ExposureCompensationController.this.setEvDialValue(EVDialDetector.getEVDialPosition());
            }
            if (CameraNotificationManager.SCENE_MODE.equals(tag) && !ExposureCompensationController.this.isMovieMode()) {
                ExposureCompensationController.mCacheInfo.saveCacheValue();
            }
        }
    }

    public static void setBootFlg(boolean boot) {
        mIsBoot = boot;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        Log.i(TAG, "onGetInitParameters");
        if (mIsBoot) {
            if (mCacheInfo == null) {
                mCacheInfo = new EvCacheInfo();
            } else {
                mCacheInfo.init();
            }
            if (!isMovieMode()) {
                mCacheInfo.saveCacheValue();
            }
        } else if (!isMovieMode()) {
            mCacheInfo.setCacheValue();
        }
        if (mIsBoot) {
            mTurnedEvDialButNotSetting = false;
            if (mHasEvDial) {
                int ev = EVDialDetector.getEVDialPosition();
                BootFactor factor = BootFactor.get();
                int bootfactor = factor.bootFactor;
                if (bootfactor == 0) {
                    if (ev != 0) {
                        setEvDialValue(ev, params);
                    }
                } else if (2 == bootfactor || 1 == bootfactor) {
                    Pair<Camera.Parameters, CameraEx.ParametersModifier> emptyParams = this.mCameraSetting.getEmptyParameters();
                    setEvDialValue(ev, emptyParams);
                    CameraSetting.getInstance().setParameters(emptyParams);
                }
            }
            mIsBoot = false;
        }
        if (this.mExposureCompensationListener == null) {
            this.mExposureCompensationListener = new ExposureCompensationListener();
            CameraNotificationManager.getInstance().setNotificationListener(this.mExposureCompensationListener);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetTermParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        Log.i(TAG, "onGetTermParameters");
        if (this.mExposureCompensationListener != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mExposureCompensationListener);
            this.mExposureCompensationListener = null;
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraRemoving() {
        mSupportedExposureCompensations = null;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraSet() {
        createSupported();
    }

    protected boolean isMovieMode() {
        if (!Environment.isMovieAPISupported() || 2 != CameraSetting.getInstance().getCurrentMode()) {
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class EvCacheInfo {
        private boolean mReopenFromMovie;
        private boolean mUserSetting;
        private int mValue;

        EvCacheInfo() {
            init();
        }

        public void init() {
            this.mUserSetting = false;
            this.mValue = 0;
            this.mReopenFromMovie = false;
        }

        public void setCameraReopenFromMovie() {
            this.mReopenFromMovie = true;
        }

        public void saveCacheValue() {
            if (Environment.isMovieAPISupported() && !this.mUserSetting) {
                this.mValue = ExposureCompensationController.this.getExposureCompensationIndex();
            }
        }

        public void setCacheValue() {
            if (Environment.isMovieAPISupported() && !this.mUserSetting && this.mReopenFromMovie) {
                ExposureCompensationController.this.setExposureCompensation(this.mValue);
                clearCache();
            }
        }

        public void clearCache() {
            this.mUserSetting = true;
            this.mValue = 0;
        }
    }
}
