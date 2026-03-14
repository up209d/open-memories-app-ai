package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.BaseBackUpKey;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.sysutil.ScalarProperties;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class DigitalZoomController extends AbstractController {
    public static final String DIGITAL_ZOOM = "DigitalZoom";
    public static final String DIGITAL_ZOOM_DISABLE = "disable";
    public static final String DIGITAL_ZOOM_ENABLE = "enable";
    public static final String DIGITAL_ZOOM_TYPE_PRECISION = "precision";
    public static final String DIGITAL_ZOOM_TYPE_SMART = "smart";
    public static final String DIGITAL_ZOOM_TYPE_SUPER_RESOLUTION = "super-resolution";
    public static final int DIRECTION_TELE = 0;
    public static final int DIRECTION_WIDE = 1;
    private static final int ERROR_VALUE = -1;
    private static final String LOG_CREATE_STEP_ZOOM_ARRAY = "createStepZoomArray";
    private static final String LOG_GET_CURRENT_DIGITAL_MODE_ERROR = "getCurrentDigitalZoomMode error";
    private static final String LOG_GET_CURRENT_DIGITAL_ZOOM_MAG_ERROR = "getCurrentDigitalZoomMagnification error";
    private static final String LOG_GET_DIGITAL_ZOOM_POTSITION_ERROR = "getDigitalZoomPosition error";
    private static final String LOG_GET_OPTICAL_ZOOM_MAG_ERROR = "getOpticalZoomMagnification error";
    private static final String LOG_GET_OPTICAL_ZOOM_POSITION_ERROR = "getOpticalZoomPosition error";
    private static final String LOG_INIT_SETTING_LISTENER = "initSettingListener";
    private static final String LOG_MSG_COMMA = ", ";
    private static final String LOG_MSG_GETAVAILABLEVALUE = "getAvailableValue = ";
    private static final String LOG_MSG_GETCURRENTDIGITALZOOMMAGNIFICATION = "getCurrentDigitalZoomMagnification";
    private static final String LOG_MSG_GETCURRENTDIGITALZOOMMAGNIFICATION_TEXT = "getCurrentDigitalZoomMagnificationText";
    private static final String LOG_MSG_GETCURRENTDIGITALZOOMMODE = "getCurrentDigitalZoomMode = ";
    private static final String LOG_MSG_GETDIGITALZOOMPOSITION = "getDigitalZoomPosition";
    private static final String LOG_MSG_GETMAXDIGITALZOOMMAGNIFICATION = "getMaxDigitalZoomMagnification";
    private static final String LOG_MSG_GETOPTICALZOOMMAGNIFICATION = "getOpticalZoomMagnification";
    private static final String LOG_MSG_GETOPTICALZOOMPOSITION = "getOpticalZoomPosition";
    private static final String LOG_MSG_GETSUPPORTEDDIGITALZOOMTYPES = "getSupportedDigitalZoomTypes = ";
    private static final String LOG_MSG_GETSUPPORTEDVALUE = "getSupportedValue = ";
    private static final String LOG_MSG_GETVALUE = "getValue";
    private static final String LOG_MSG_INVALID_ARGUMENT = "Invalid argument. ";
    private static final String LOG_MSG_ISAVAILABLE = "iSAvailable";
    private static final String LOG_MSG_ISDIGITALZOOMSTATUS = "isDigitalZoomStatus";
    private static final String LOG_MSG_ISSUPPORTEDSEAMLESSZOOM = "isSupportedSeamlessZoom";
    private static final String LOG_MSG_RESETDIGITALZOOM = "resetDigitalZoom";
    private static final String LOG_MSG_SETZOOMMAGNIFICATION = "setZoomMagnification";
    private static final String LOG_RESET_DIGITA_ZOOM_ERROR = "resetDigitalZoom error";
    private static final String LOG_SET_BACKUP_DIGITAL_MAG = "setBackupDigitalZoomMagnification";
    private static final String LOG_SET_DIGITAL_ZOOM_INITIAL_SETTING = "setDigitalZoomInitialSetting";
    private static final String LOG_SET_STEP_ZOOM = "setStepZoomMagnification";
    private static final String LOG_SET_ZOOM_MAG_ERROR = "setZoomMagnification error";
    private static final String LOG_START_SPINAL_ZOOM = "turn on spinal zoom";
    private static final String LOG_START_ZOOM_EXCEPTION = "start zoom runtime exception";
    private static final String LOG_STOP_SPINAL_ZOOM = "turn off spinal zoom";
    private static final String LOG_STOP_ZOOM_EXCEPTION = "stop zoom runtime exception";
    private static final int MAG_SHOW_LIMIT = 10;
    private static final int PF_NOTRESET_DIGITALZOOM_VERSION = 4;
    public static final String PROP_DEVICE_ZOOM_LEVER = "device.zoom.lever";
    public static final int STATUS_ZOOM_LEVER_NOT_SUPPORTED = 0;
    public static final int STATUS_ZOOM_LEVER_SUPPORTED = 1;
    private static final double STEP_RATE = 1.4142d;
    public static final String STEP_ZOOM_LOWER = "STEP_ZOOM_LOWER";
    public static final String STEP_ZOOM_UPPER = "STEP_ZOOM_UPPER";
    private static final String STR_FORMAT_MAGNIFICATION_OVER_TEN = "%.0f";
    private static final String STR_FORMAT_MAGNIFICATION_UNDER_TEN = "%.1f";
    private static final String TAG = "DigitalZoomController";
    public static final String TAG_DIGITAL_ZOOM_MODE_NOT_SET = "digitalZoomModeNotSet";
    public static final String TAG_DIGITAL_ZOOM_TYPE = "digitalZoomType";
    public static final String TAG_ZOOM_DRIVE_MODE = "zoomDriveMode";
    public static final String ZOOM_DRIVE_TYPE_AUTO = "auto";
    public static final String ZOOM_DRIVE_TYPE_DIGITAL = "digital";
    public static final String ZOOM_DRIVE_TYPE_OPTICAL = "optical";
    public static final int ZOOM_INIT_VALUE = 100;
    public static final int ZOOM_INIT_VALUE_MODIFIED = 110;
    public static final int ZOOM_MAG_INVALID_BACKUP_VALUE = -1;
    public static final int ZOOM_POSITION_MAX = 100;
    public static final int ZOOM_POSITION_MIN = 0;
    public static final int ZOOM_STEP_UNIT = 10;
    private static DigitalZoomController mInstance;
    private boolean mIsDigitalZoomInitializing;
    private static final StringBuilder STRBUILD = new StringBuilder();
    private static final StringBuilder STRBUILD_CAMERA = new StringBuilder();
    private static final StringBuilder STRBUILD_UI = new StringBuilder();
    private static final int[] mStepMag = {100, 140, 200, 280, 400, 560, 800};
    private static final String myName = DigitalZoomController.class.getSimpleName();
    private static boolean wantReset = false;
    private static final String[] tag = {CameraNotificationManager.ZOOM_MAX_MAG_CHANGED};
    private boolean isSupportZoomLever = false;
    private List<Integer> mMagStepMagList = null;
    private NotificationListener mListener = new DigitalZoomMaxMagnificationChangedListener();
    private boolean mIsTempDigitalZoomSettingOFF = false;
    private boolean mIsSpecialSeq = false;
    private CameraSetting mCamSet = CameraSetting.getInstance();

    public static final String getName() {
        return myName;
    }

    public static DigitalZoomController getInstance() {
        if (mInstance == null) {
            mInstance = new DigitalZoomController();
        }
        return mInstance;
    }

    private static void setController(DigitalZoomController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected DigitalZoomController() {
        createSupported();
        setController(this);
    }

    protected void createSupported() {
        if (isPFverOver2()) {
            int zoomLeverStatus = ScalarProperties.getInt("device.zoom.lever");
            if (1 == zoomLeverStatus) {
                this.isSupportZoomLever = true;
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraReopened() {
        createSupported();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag2) {
        if (this.isSupportZoomLever || !isPFverOver1() || !ExecutorCreator.getInstance().isEnableDigitalZoom()) {
            return null;
        }
        List<String> ret = new ArrayList<>();
        ret.add(DIGITAL_ZOOM_ENABLE);
        return ret;
    }

    public List<String> getSupportedDigitalZoomTypeValues() {
        List<String> supportValues;
        new ArrayList();
        if (!isPFverOver1() || (supportValues = ((CameraEx.ParametersModifier) this.mCamSet.getSupportedParameters().second).getSupportedDigitalZoomTypes()) == null) {
            return null;
        }
        List<String> list = new ArrayList<>();
        for (String s : supportValues) {
            String retVal = convertPF2App(s);
            if (retVal != null) {
                list.add(retVal);
            }
        }
        if (list.size() == 0) {
            return null;
        }
        return list;
    }

    private String convertPF2App(String zoomType) {
        if ("auto".equals(zoomType)) {
            return "auto";
        }
        if (ZOOM_DRIVE_TYPE_DIGITAL.equals(zoomType)) {
            return ZOOM_DRIVE_TYPE_DIGITAL;
        }
        if (ZOOM_DRIVE_TYPE_OPTICAL.equals(zoomType)) {
            return ZOOM_DRIVE_TYPE_OPTICAL;
        }
        if (DIGITAL_ZOOM_TYPE_PRECISION.equals(zoomType)) {
            return DIGITAL_ZOOM_TYPE_PRECISION;
        }
        if (DIGITAL_ZOOM_TYPE_SMART.equals(zoomType)) {
            return DIGITAL_ZOOM_TYPE_SMART;
        }
        if (DIGITAL_ZOOM_TYPE_SUPER_RESOLUTION.equals(zoomType)) {
            return DIGITAL_ZOOM_TYPE_SUPER_RESOLUTION;
        }
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_INVALID_ARGUMENT).append("convertPF2App").append(zoomType);
        Log.w(TAG, STRBUILD.toString());
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag2) {
        if (this.isSupportZoomLever || !isPFverOver1() || !ExecutorCreator.getInstance().isEnableDigitalZoom() || !isAvailable(tag2)) {
            return null;
        }
        ArrayList<String> ret = new ArrayList<>();
        ret.add(DIGITAL_ZOOM_ENABLE);
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag2) {
        if (this.isSupportZoomLever || !isPFverOver1() || !ExecutorCreator.getInstance().isEnableDigitalZoom()) {
            return false;
        }
        int powerZoomStatus = CameraSetting.getInstance().getPowerZoomStatus();
        if (powerZoomStatus == 1) {
            return false;
        }
        boolean ret = isZoomAvailable();
        return ret;
    }

    public boolean isZoomAvailable() {
        boolean ret = false;
        List<String> list = getSupportedDigitalZoomTypeValues();
        if (list != null) {
            for (String s : list) {
                int mag = ((CameraEx.ParametersModifier) this.mCamSet.getParameters().second).getMaxDigitalZoomMagnification(s);
                if (mag > 100) {
                    return true;
                }
                ret = false;
            }
        }
        return ret;
    }

    public static boolean isPFverOver1() {
        return 1 <= CameraSetting.getPfApiVersion();
    }

    public static boolean isPFverOver2() {
        return 2 <= CameraSetting.getPfApiVersion();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag2, String value) {
    }

    public int getMaxDigitalZoomMagnification(String type) {
        int maxMagnification = 100;
        if (isPFverOver1()) {
            if (type == null) {
                List<String> list = getSupportedDigitalZoomTypeValues();
                if (list != null) {
                    for (String s : list) {
                        int mag = ((CameraEx.ParametersModifier) this.mCamSet.getParameters().second).getMaxDigitalZoomMagnification(s);
                        if (mag > maxMagnification) {
                            maxMagnification = mag;
                        }
                    }
                }
            } else {
                int mag2 = ((CameraEx.ParametersModifier) this.mCamSet.getParameters().second).getMaxDigitalZoomMagnification(type);
                if (mag2 > 100) {
                    return mag2;
                }
            }
        }
        return maxMagnification;
    }

    public void setZoomMagnification(int mag) {
        if (isPFverOver1()) {
            int maxMag = getMaxDigitalZoomMagnification(null);
            if (mag >= 100) {
                if (mag > maxMag) {
                    mag = maxMag;
                }
                try {
                    if (mag == 100) {
                        resetDigitalZoom();
                    } else {
                        this.mCamSet.getCamera().setDigitalZoom(mag);
                    }
                } catch (RuntimeException e) {
                    Log.e(TAG, LOG_SET_ZOOM_MAG_ERROR, e);
                }
            }
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag2) {
        return null;
    }

    public String getCurrentDigitalZoomMode() {
        if (!isPFverOver1()) {
            return TAG_DIGITAL_ZOOM_MODE_NOT_SET;
        }
        String zoomType = CameraSetting.getInstance().getDigitalZoomType();
        if (zoomType == null) {
            return TAG_DIGITAL_ZOOM_MODE_NOT_SET;
        }
        return zoomType;
    }

    public int getCurrentDigitalZoomMagnification() {
        int mag;
        if (!isPFverOver1() || -1 == (mag = CameraSetting.getInstance().getDigitalZoomMagnification())) {
            return 100;
        }
        return mag;
    }

    public String getCurrentDigitalZoomMagnificationText() {
        float mag;
        int category = ScalarProperties.getInt("model.category");
        if (2 == category) {
            mag = (CameraSetting.getInstance().getOpticalZoomMagnification() * getCurrentDigitalZoomMagnification()) / 10000.0f;
        } else {
            mag = getCurrentDigitalZoomMagnification() / 100.0f;
        }
        BigDecimal bigDecimal = new BigDecimal(mag);
        float digitalMagValue = bigDecimal.setScale(1, 4).floatValue();
        if (digitalMagValue < 10.0f) {
            String magText = String.format(STR_FORMAT_MAGNIFICATION_UNDER_TEN, Float.valueOf(digitalMagValue));
            return magText;
        }
        String magText2 = String.format(STR_FORMAT_MAGNIFICATION_OVER_TEN, Float.valueOf(digitalMagValue));
        return magText2;
    }

    public void resetDigitalZoom() {
        if (isPFverOver1() && isDigitalZoomStatus()) {
            try {
                this.mCamSet.getCamera().resetDigitalZoom();
            } catch (RuntimeException e) {
                Log.e(TAG, LOG_RESET_DIGITA_ZOOM_ERROR, e);
            }
        }
    }

    public boolean isDigitalZoomStatus() {
        return !TAG_DIGITAL_ZOOM_MODE_NOT_SET.equals(getCurrentDigitalZoomMode());
    }

    public boolean isDigitalZoomMagOverInitValue() {
        return getCurrentDigitalZoomMagnification() > 100;
    }

    public boolean isOpticalZoomMagOverInitValue() {
        return getOpticalZoomMagnification() > 100;
    }

    public List<String> getSupportedZoomDriveTypes() {
        if (!isPFverOver1()) {
            return null;
        }
        List<String> list = ((CameraEx.ParametersModifier) this.mCamSet.getSupportedParameters().second).getSupportedDigitalZoomTypes();
        return list;
    }

    public boolean isSupportedSeamlessZoom() {
        if (getSupportedZoomDriveTypes() != null && (getSupportedZoomDriveTypes() == null || !getSupportedZoomDriveTypes().contains("auto"))) {
            return true;
        }
        return false;
    }

    public int getDigitalZoomPosition() {
        if (!isPFverOver1()) {
            return 1;
        }
        int pos = this.mCamSet.getDigitalPosition();
        if (-1 == pos) {
            Log.e(TAG, LOG_GET_DIGITAL_ZOOM_POTSITION_ERROR);
            return 1;
        }
        return pos;
    }

    public int getOpticalZoomPosition() {
        if (!isPFverOver1()) {
            return 1;
        }
        int pos = this.mCamSet.getOpticalPosition();
        if (-1 == pos) {
            Log.e(TAG, LOG_GET_OPTICAL_ZOOM_POSITION_ERROR);
            return 1;
        }
        return pos;
    }

    public int getOpticalZoomMagnification() {
        if (!isPFverOver1()) {
            return 100;
        }
        int mag = this.mCamSet.getOpticalZoomMagnification();
        if (-1 == mag) {
            Log.e(TAG, LOG_GET_OPTICAL_ZOOM_MAG_ERROR);
            return 100;
        }
        return mag;
    }

    public void startZoom(int direction, int speed) {
        if (isPFverOver1() && speed >= 0 && getMaxZoomSpeed() >= speed) {
            if (1 == direction || direction == 0) {
                try {
                    this.mCamSet.getCamera().startZoom(direction, speed);
                } catch (RuntimeException e) {
                    Log.e(TAG, LOG_START_ZOOM_EXCEPTION);
                }
            }
        }
    }

    public int getMaxZoomSpeed() {
        if (!isPFverOver1()) {
            return -1;
        }
        int ret = ((CameraEx.ParametersModifier) this.mCamSet.getSupportedParameters().second).getMaxZoomSpeed();
        return ret;
    }

    public void stopZoom() {
        if (isPFverOver1()) {
            try {
                this.mCamSet.getCamera().stopZoom();
            } catch (RuntimeException e) {
                Log.e(TAG, LOG_STOP_ZOOM_EXCEPTION);
            }
        }
    }

    public void createStepZoomArray() {
        Log.d(TAG, LOG_CREATE_STEP_ZOOM_ARRAY);
        if (this.mMagStepMagList != null) {
            this.mMagStepMagList.clear();
            this.mMagStepMagList = null;
        }
        this.mMagStepMagList = new ArrayList();
        for (int i = 0; i < mStepMag.length; i++) {
            this.mMagStepMagList.add(Integer.valueOf(mStepMag[i]));
        }
        int maxMag = getMaxDigitalZoomMagnification(null);
        if (maxMag <= mStepMag[mStepMag.length - 1]) {
            return;
        }
        while (true) {
            float calc = this.mMagStepMagList.get(this.mMagStepMagList.size() - 1).intValue() * 1.4142f;
            BigDecimal bigDecimal = new BigDecimal(calc / 10.0f);
            float calc2 = bigDecimal.setScale(0, 4).floatValue();
            int stepMag = (int) (calc2 * 10.0f);
            if (stepMag > 100) {
                if (maxMag < stepMag) {
                    this.mMagStepMagList.add(Integer.valueOf(maxMag));
                    return;
                }
                this.mMagStepMagList.add(Integer.valueOf(stepMag));
            } else {
                return;
            }
        }
    }

    public void setStepZoomMagnification(String s) {
        int currentMag = getCurrentDigitalZoomMagnification();
        if (STEP_ZOOM_UPPER.equals(s)) {
            for (int i = 0; i < this.mMagStepMagList.size(); i++) {
                if (currentMag < this.mMagStepMagList.get(i).intValue()) {
                    setZoomMagnification(this.mMagStepMagList.get(i).intValue());
                    return;
                }
            }
            return;
        }
        if (STEP_ZOOM_LOWER.equals(s)) {
            for (int i2 = this.mMagStepMagList.size() - 1; i2 >= 0; i2--) {
                if (currentMag > this.mMagStepMagList.get(i2).intValue()) {
                    setZoomMagnification(this.mMagStepMagList.get(i2).intValue());
                    return;
                }
            }
        }
    }

    public static boolean getWantResetZoom() {
        return wantReset;
    }

    public static void setWantResetZoom(boolean b) {
        if (ExecutorCreator.getInstance().isEnableDigitalZoom()) {
            wantReset = b;
        }
    }

    public static int getMagWhenCameraClosed() {
        return BackUpUtil.getInstance().getPreferenceInt(BaseBackUpKey.ID_ZOOM_MAG_WHEN_CAMERA_CLOSE, -1);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraSet() {
        super.onCameraSet();
        initSettingListener();
        setDigitalZoomInitialSetting(this.mCamSet.getCamera());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        super.onGetInitParameters(params);
        initSettingListener();
        if (this.mListener != null) {
            CameraNotificationManager.getInstance().setNotificationListener(this.mListener);
        }
        if (CameraSetting.getPfApiVersion() <= 2) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> initialParams = this.mCamSet.getInitialParameters();
            if (initialParams == null || !ExecutorCreator.getInstance().isEnableDigitalZoom()) {
                setBackupDigitalZoomMagnification();
                return;
            }
            return;
        }
        if (3 <= CameraSetting.getPfApiVersion() && ExecutorCreator.getInstance().isEnableDigitalZoom()) {
            setBackupDigitalZoomMagnification();
        }
    }

    public void initSettingListener() {
        Log.i(TAG, LOG_INIT_SETTING_LISTENER);
        if (1 <= CameraSetting.getPfApiVersion()) {
            int[] types = {15};
            this.mCamSet.getCamera().enableSettingChangedTypes(types);
            this.mCamSet.getCamera().setSettingChangedListener(this.mCamSet);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraRemoving() {
        super.onCameraRemoving();
        finishDigitalZoom();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetTermParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        super.onGetTermParameters(params);
        CameraSetting camSet = CameraSetting.getInstance();
        if (1 <= CameraSetting.getPfApiVersion() && ExecutorCreator.getInstance().isEnableDigitalZoom()) {
            int digitalZoomMagnification = camSet.getDigitalZoomMagnification();
            int runStatus = RunStatus.getStatus();
            if (4 == runStatus || 2 == runStatus) {
                BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_ZOOM_MAG_WHEN_CAMERA_CLOSE, Integer.valueOf(digitalZoomMagnification));
            }
        }
        if (this.mListener != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mListener);
        }
    }

    private void finishDigitalZoom() {
        CameraSetting camSet = CameraSetting.getInstance();
        if (1 <= CameraSetting.getPfApiVersion() && ExecutorCreator.getInstance().isEnableDigitalZoom()) {
            int digitalZoomMagnification = camSet.getDigitalZoomMagnification();
            int runStatus = RunStatus.getStatus();
            if (3 == runStatus) {
                if (isDigitalZoomResetOnBoot()) {
                    BackUpUtil.getInstance().removePreference(BaseBackUpKey.ID_ZOOM_MAG_WHEN_CAMERA_CLOSE);
                    camSet.getCamera().resetDigitalZoom();
                    return;
                }
                return;
            }
            if (4 == runStatus || 2 == runStatus) {
                if (digitalZoomMagnification > 100) {
                    wantReset = false;
                } else {
                    wantReset = true;
                }
                if (!ExecutorCreator.getInstance().isRecordingModeChanging() && isDigitalZoomResetOnBoot()) {
                    camSet.getCamera().resetDigitalZoom();
                }
            }
        }
    }

    private void setDigitalZoomInitialSetting(CameraEx cameraEx) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> initialParams = this.mCamSet.getInitialParameters();
        this.mIsDigitalZoomInitializing = false;
        if (1 <= CameraSetting.getPfApiVersion()) {
            this.mIsDigitalZoomInitializing = true;
            Log.i(TAG, LOG_SET_DIGITAL_ZOOM_INITIAL_SETTING);
            startSpinalZoom();
            if (CameraSetting.getPfApiVersion() <= 2) {
                if (initialParams != null && ExecutorCreator.getInstance().isEnableDigitalZoom()) {
                    Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
                    List<String> digitalZoomTypes = getSupportedDigitalZoomTypeValues();
                    for (String s : digitalZoomTypes) {
                        boolean b = ((CameraEx.ParametersModifier) initialParams.second).getDigitalZoomMode(s);
                        ((CameraEx.ParametersModifier) p.second).setDigitalZoomMode(s, b);
                    }
                    this.mCamSet.setParametersDirect(p);
                    return;
                }
                return;
            }
            if (3 <= CameraSetting.getPfApiVersion() && !ExecutorCreator.getInstance().isEnableDigitalZoom()) {
                List<String> digitalZoomTypes2 = getSupportedDigitalZoomTypeValues();
                Pair<Camera.Parameters, CameraEx.ParametersModifier> p2 = this.mCamSet.getEmptyParameters();
                Iterator i$ = digitalZoomTypes2.iterator();
                while (i$.hasNext()) {
                    ((CameraEx.ParametersModifier) p2.second).setDigitalZoomMode(i$.next(), false);
                }
                this.mCamSet.setParametersDirect(p2);
            }
        }
    }

    /* loaded from: classes.dex */
    private class DigitalZoomMaxMagnificationChangedListener implements NotificationListener {
        private DigitalZoomMaxMagnificationChangedListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return DigitalZoomController.tag;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (CameraNotificationManager.ZOOM_MAX_MAG_CHANGED.equals(tag)) {
                DigitalZoomController.this.setBackupDigitalZoomMagnification();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setBackupDigitalZoomMagnification() {
        if (1 <= CameraSetting.getPfApiVersion() && ExecutorCreator.getInstance().isEnableDigitalZoom()) {
            if (this.mIsDigitalZoomInitializing) {
                this.mIsDigitalZoomInitializing = false;
                BootFactor factor = BootFactor.get();
                int bootfactor = factor.bootFactor;
                int mag_apo = getMagWhenCameraClosed();
                STRBUILD_CAMERA.replace(0, STRBUILD_CAMERA.length(), LOG_SET_BACKUP_DIGITAL_MAG).append(Integer.toString(bootfactor)).append(", ").append(Integer.toString(mag_apo));
                Log.w(TAG, STRBUILD_CAMERA.toString());
                if (bootfactor == 0 || 2 == bootfactor) {
                    if (getWantResetZoom()) {
                        if (canPfContinueOnOpen() && (isCategoryResetOnBoot() || 2 == bootfactor)) {
                            forceResetDigitalZoom();
                        }
                        setWantResetZoom(false);
                    } else if (mag_apo > 100 && -1 != mag_apo && isDigitalZoomResetOnBoot()) {
                        setZoomMagnification(mag_apo);
                    }
                } else if (1 == bootfactor && mag_apo > 100 && -1 != mag_apo && isDigitalZoomResetOnBoot()) {
                    setZoomMagnification(mag_apo);
                }
            }
            BackUpUtil.getInstance().removePreference(BaseBackUpKey.ID_ZOOM_MAG_WHEN_CAMERA_CLOSE);
        }
    }

    public void forceResetDigitalZoom() {
        try {
            this.mCamSet.getCamera().resetDigitalZoom();
        } catch (RuntimeException e) {
            Log.e(TAG, LOG_RESET_DIGITA_ZOOM_ERROR, e);
        }
    }

    public void startSpinalZoom() {
        if (2 <= CameraSetting.getPfApiVersion() && ExecutorCreator.getInstance().isSpinalZoom()) {
            Log.d(TAG, LOG_START_SPINAL_ZOOM);
            CameraEx cameraEx = this.mCamSet.getCamera();
            if (cameraEx != null) {
                cameraEx.startAutoZoom();
            }
        }
    }

    public void stopSpinalZoom() {
        if (2 <= CameraSetting.getPfApiVersion() && ExecutorCreator.getInstance().isSpinalZoom()) {
            Log.d(TAG, LOG_STOP_SPINAL_ZOOM);
            CameraEx cameraEx = this.mCamSet.getCamera();
            if (cameraEx != null) {
                cameraEx.stopAutoZoom();
            }
        }
    }

    public void setDigitalZoomOFFTemporarily(boolean isSetZoomOFF) {
        this.mIsTempDigitalZoomSettingOFF = isSetZoomOFF;
        switchDigitalZoomONOFF();
    }

    public void setDigitalZoomOnSpecialSeq(boolean isSpesialSeq) {
        this.mIsSpecialSeq = isSpesialSeq;
        switchDigitalZoomONOFF();
    }

    private void switchDigitalZoomONOFF() {
        if (1 <= CameraSetting.getPfApiVersion() && ExecutorCreator.getInstance().isEnableDigitalZoom()) {
            boolean isSetZoomOff = false;
            if (this.mIsSpecialSeq) {
                isSetZoomOff = true;
            } else if (this.mIsTempDigitalZoomSettingOFF) {
                isSetZoomOff = true;
            }
            CameraEx cameraEx = this.mCamSet.getCamera();
            List<String> digitalZoomTypes = getSupportedDigitalZoomTypeValues();
            if (cameraEx != null) {
                if (isSetZoomOff) {
                    Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
                    cameraEx.resetDigitalZoom();
                    Iterator i$ = digitalZoomTypes.iterator();
                    while (i$.hasNext()) {
                        ((CameraEx.ParametersModifier) p.second).setDigitalZoomMode(i$.next(), false);
                    }
                    this.mCamSet.setParametersDirect(p);
                    return;
                }
                Pair<Camera.Parameters, CameraEx.ParametersModifier> initialParams = this.mCamSet.getInitialParameters();
                if (initialParams != null) {
                    Pair<Camera.Parameters, CameraEx.ParametersModifier> p_ = this.mCamSet.getEmptyParameters();
                    for (String s : digitalZoomTypes) {
                        boolean b = ((CameraEx.ParametersModifier) initialParams.second).getDigitalZoomMode(s);
                        ((CameraEx.ParametersModifier) p_.second).setDigitalZoomMode(s, b);
                    }
                    this.mCamSet.setParametersDirect(p_);
                }
            }
        }
    }

    protected static boolean canPfContinueOnOpen() {
        return 4 <= Environment.getVersionPfAPI();
    }

    protected static boolean isCategoryResetOnBoot() {
        int category = ScalarProperties.getInt("model.category");
        return category == 0 || 1 == category || 1 == category;
    }

    protected static boolean isDigitalZoomResetOnBoot() {
        if (canPfContinueOnOpen()) {
            return isCategoryResetOnBoot();
        }
        return true;
    }
}
