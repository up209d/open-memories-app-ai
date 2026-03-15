package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.common.FocusModeDialDetector;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class FocusModeController extends ShootingModeController implements AvailableInfo.IInhFactorChange {
    private static final String ADJSUT_CONTROL_WHEEL = "_adjust_conrol_wheel";
    public static final String AF_C = "af-c";
    public static final String AF_S = "af-s";
    public static final String API_NAME = "setFocusMode";
    private static final String API_NAME_TRACKINGFOCUS = "setTrackingFocus";
    public static final String AUTO = "auto";
    public static final String CANCEL_FOCUS_MODE = "CancelFocusMode";
    private static final String LOG_CHECKVALUE = "checkValue";
    private static final String LOG_MSG_AFMFBUTTONSTATE = "AFMFButtonState = ";
    private static final String LOG_MSG_CANCELFOCUSCONTROL = "cancelFocusControl ";
    private static final String LOG_MSG_GETFOCUSMODE = "getFocusMode = ";
    private static final String LOG_MSG_GETSUPPORTEDFOCUSMODES = "getSupportedFocusModes = ";
    private static final String LOG_MSG_GETSUPPORTEDTRACKINGFOCUSMODES = "getSupportedTrackingFocusModes = ";
    private static final String LOG_MSG_GETSUPPORTEDVALUE = "getSupportedValue = ";
    private static final String LOG_MSG_GETTRACKINGFOCUS = "getTrackingFocus = ";
    private static final String LOG_MSG_HOLDFOCUSCONTROL = "holdFocusControl ";
    private static final String LOG_MSG_INITFOCUSCONTROL = "initFocusControl ";
    private static final String LOG_MSG_SETFOCUSMODE = "setFocusMode = ";
    private static final String LOG_MSG_SETTRACKINGFOCUS = "setTrackingFocus = ";
    private static final String LOG_MSG_STARTTRACKINGFOCUS = "startTrackingFocus = ";
    private static final String LOG_MSG_STOPTRACKINGFOCUS = "stopTrackingFocus = ";
    private static final String LOG_MSG_TO = " to ";
    private static final String LOG_MSG_TOGGLEFOCUSCONTROL = "toggleFocusControl ";
    private static final String LOG_UNKNOWN_SHOOTING_MODE = "Unknown shooting mode";
    public static final String MANUAL_CONTROL_WHEEL = "manual_adjust_conrol_wheel";
    public static final String SMF = "neighbor";
    public static final String SMF_CONTROL_WHEEL = "neighbor_adjust_conrol_wheel";
    private static final String TAG = "FocusModeController";
    public static final String TAG_FOCUS_MODE = "FocusSelection";
    public static final String TAG_FORCING_FOCUS_MODE = "ForcingFocusSelection";
    public static final String TAG_TRACKING_FOCUS = "TrackingFocus";
    public static final String TRACKING_FOCUS_OFF = "off";
    public static final String TRACKING_FOCUS_ON = "on";
    private static FocusModeController mInstance;
    private static boolean mIsFocusShiftByControlWheel;
    private static final StringBuilderThreadLocal sStringBuilder = new StringBuilderThreadLocal();
    public static final String DMF = "dmf";
    public static final String MANUAL = "manual";
    public static final String[] appSupportedStillValues = {"af-s", "af-c", DMF, MANUAL};
    public static final String[] appSupportedMovieValues = {"af-c", MANUAL};
    private static boolean mKeepToggle = false;
    private static int mLastRecMode = 32768;
    private static boolean mKeepToggleStillToMovie = false;
    private static List<String> mSupportedTrackingFocus = null;
    private static String INH_AFMF_SW_MF = null;
    private static final String myName = FocusModeController.class.getSimpleName();
    private static final String[] tags = {CameraNotificationManager.DEVICE_LENS_CHANGED};
    private NotificationListener mListener = new FocusModeControllerListener();
    private CameraSetting mCamSet = CameraSetting.getInstance();
    private AutoFocusModeController mAutoFocusModeController = AutoFocusModeController.getInstance();

    public static final String getName() {
        return myName;
    }

    public static FocusModeController getInstance() {
        if (mInstance == null) {
            new FocusModeController();
        }
        return mInstance;
    }

    private static void setController(FocusModeController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected FocusModeController() {
        setController(this);
        setFocusShiftByControlWheelFlg();
    }

    @Override // com.sony.imaging.app.util.AvailableInfo.IInhFactorChange
    public void onInhFactorChanged(String factorID, int value) {
        CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraSet() {
        Log.i(TAG, "onCameraSet");
        mLastRecMode = 32768;
        if (8 <= Environment.getVersionPfAPI()) {
            INH_AFMF_SW_MF = "INH_FACTOR_CAM_LENS_AFMF_SW_MF_TYPE_P";
            AvailableInfo.registerInhFactorListener(INH_AFMF_SW_MF, this);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        Log.i(TAG, "onGetInitParameters");
        int currentRecMode = CameraSetting.getInstance().getCurrentMode();
        mKeepToggleStillToMovie = false;
        if (isFocusControl() && 1 == mLastRecMode && 2 == currentRecMode) {
            mKeepToggleStillToMovie = true;
        }
        mLastRecMode = currentRecMode;
        if (this.mListener != null) {
            CameraNotificationManager.getInstance().setNotificationListener(this.mListener);
        }
        if (!mKeepToggle && !mKeepToggleStillToMovie) {
            initFocusControl();
        }
        checkValue(params);
        mSupportedTrackingFocus = null;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetTermParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        Log.i(TAG, "onGetTermParameters");
        if (this.mListener != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mListener);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraRemoving() {
        Log.i(TAG, "onCameraRemoving");
        if (8 <= Environment.getVersionPfAPI()) {
            AvailableInfo.unregisterInhFactorListener(INH_AFMF_SW_MF, this);
        }
    }

    private void checkValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        Log.i(TAG, LOG_CHECKVALUE);
        String currentValue = getValue();
        if (FocusModeDialDetector.hasFocusModeDial()) {
            currentValue = getFocusModeFromFocusModeDial();
        }
        List<String> availableList = getAvailableValue();
        if (currentValue != null && availableList != null) {
            int shootingmode = CameraSetting.getInstance().getCurrentMode();
            if (1 == shootingmode) {
                if (!Arrays.asList(appSupportedStillValues).contains(currentValue)) {
                    String[] arr$ = appSupportedStillValues;
                    for (String appSupportedValue : arr$) {
                        if (availableList.contains(appSupportedValue)) {
                            setValue(appSupportedValue, params);
                            return;
                        }
                    }
                }
            } else if (2 == shootingmode && Environment.isMovieAPISupported() && !Arrays.asList(appSupportedMovieValues).contains(currentValue)) {
                String[] arr$2 = appSupportedMovieValues;
                for (String appSupportedValue2 : arr$2) {
                    if (availableList.contains(appSupportedValue2)) {
                        setValue(appSupportedValue2, params);
                        return;
                    }
                }
            }
            if (!currentValue.equals(currentValue)) {
                setValue(currentValue, params);
            }
        }
    }

    public void setValue(String value, Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        StringBuilder strBuilder = sStringBuilder.get();
        if ("af-s".equals(value) || "af-c".equals(value)) {
            String setvalue = this.mAutoFocusModeController.convertApp2PF(value);
            if (setvalue != null) {
                ((Camera.Parameters) params.first).setFocusMode("auto");
                ((CameraEx.ParametersModifier) params.second).setAutoFocusMode(setvalue);
                strBuilder.replace(0, strBuilder.length(), LOG_MSG_SETFOCUSMODE).append("auto");
                Log.i(TAG, strBuilder.toString());
                return;
            }
            return;
        }
        String mode = convertApp2PF(value);
        ((Camera.Parameters) params.first).setFocusMode(mode);
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_SETFOCUSMODE).append(mode);
        Log.i(TAG, strBuilder.toString());
    }

    public void setValue(String value) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> emptyParam = this.mCamSet.getEmptyParameters();
        setValue(value, emptyParam);
        this.mCamSet.setParameters(emptyParam);
    }

    public String getValue() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
        String value = ((Camera.Parameters) p.first).getFocusMode();
        if ("auto".equals(value)) {
            return this.mAutoFocusModeController.getValue();
        }
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_GETFOCUSMODE).append(value);
        Log.i(TAG, strBuilder.toString());
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
        List<String> supParams = ((Camera.Parameters) this.mCamSet.getSupportedParameters(mode).first).getSupportedFocusModes();
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_GETSUPPORTEDFOCUSMODES).append(supParams);
        Log.i(TAG, strBuilder.toString());
        if (supParams == null) {
            return null;
        }
        List<String> ret = new ArrayList<>();
        for (String s : supParams) {
            if (s.equals("auto")) {
                List<String> supportedAFValues = this.mAutoFocusModeController.getSupportedValue(mode);
                if (supportedAFValues != null) {
                    ret.addAll(supportedAFValues);
                }
            } else {
                String retVal = convertPF2App(s);
                if (!ret.contains(retVal)) {
                    ret.add(retVal);
                }
            }
        }
        if (ret.size() == 0) {
            return null;
        }
        return ret;
    }

    public List<String> getAvailableValue() {
        ArrayList<String> ret = new ArrayList<>();
        List<String> supList = getSupportedValue();
        List<String> afAvailableList = null;
        boolean isAutoIncluded = false;
        if (supList != null) {
            AvailableInfo.update();
            for (String s : supList) {
                if ("af-s".equals(s) || "af-c".equals(s)) {
                    isAutoIncluded = true;
                } else if (AvailableInfo.isAvailable("setFocusMode", convertApp2PF(s))) {
                    ret.add(s);
                }
            }
            if (isAutoIncluded) {
                afAvailableList = this.mAutoFocusModeController.getAvailableValue();
            }
            if (afAvailableList != null) {
                ret.addAll(afAvailableList);
            }
        }
        if (ret.size() == 0) {
            return null;
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean ret = false;
        if (TAG_FOCUS_MODE.equals(tag)) {
            AvailableInfo.update();
            ret = AvailableInfo.isAvailable("setFocusMode", null);
            if (CameraSetting.mFocusControlState == 1) {
                Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
                String value = ((Camera.Parameters) p.first).getFocusMode();
                if ("auto".equals(value)) {
                    boolean retAF = this.mAutoFocusModeController.isAvailable("AutoFocusMode");
                    if (isFocusShiftByControlWheel() && 2 == this.mCamSet.getCurrentMode()) {
                        return false;
                    }
                    return ret || retAF;
                }
            }
        } else if (TAG_TRACKING_FOCUS.equals(tag)) {
            ret = AvailableInfo.isAvailable(API_NAME_TRACKINGFOCUS, null);
        }
        return ret;
    }

    public void toggleFocusControl() {
        int before = CameraSetting.mFocusControlState;
        switch (CameraSetting.mFocusControlState) {
            case 1:
                setToggleFocusMode(true);
                CameraSetting.mFocusControlState = 3;
                break;
            case 2:
                resetQuickAutoFocus("af_woaf");
                CameraSetting.mFocusControlState = 3;
                break;
            case 3:
                setToggleFocusMode(false);
                CameraSetting.mFocusControlState = 1;
                break;
        }
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_TOGGLEFOCUSCONTROL).append(LOG_MSG_AFMFBUTTONSTATE).append(before).append(LOG_MSG_TO).append(CameraSetting.mFocusControlState);
        Log.d(TAG, strBuilder.toString());
    }

    public void holdFocusControl(boolean control) {
        int before = CameraSetting.mFocusControlState;
        switch (CameraSetting.mFocusControlState) {
            case 1:
                if (control) {
                    setToggleFocusMode(true);
                    setQuickAutoFocus("af_woaf");
                    CameraSetting.mFocusControlState = 2;
                    break;
                }
                break;
            case 2:
                if (!control) {
                    setToggleFocusMode(false);
                    resetQuickAutoFocus("af_woaf");
                    CameraSetting.mFocusControlState = 1;
                    break;
                }
                break;
            case 3:
                if (control) {
                    CameraSetting.mFocusControlState = 2;
                    setQuickAutoFocus("af_woaf");
                    break;
                }
                break;
        }
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_HOLDFOCUSCONTROL).append(LOG_MSG_AFMFBUTTONSTATE).append(before).append(LOG_MSG_TO).append(CameraSetting.mFocusControlState);
        Log.d(TAG, strBuilder.toString());
    }

    public void setQuickAutoFocus(String mode) {
        List<String> list = ((CameraEx.ParametersModifier) CameraSetting.getInstance().getSupportedParameters().second).getSupportedQuickAutoFocus();
        if (list != null && list.contains(mode)) {
            CameraEx cameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx();
            cameraEx.setQuickAutoFocus(mode);
        }
    }

    public void resetQuickAutoFocus(String mode) {
        List<String> list = ((CameraEx.ParametersModifier) CameraSetting.getInstance().getSupportedParameters().second).getSupportedQuickAutoFocus();
        if (list != null && list.contains(mode)) {
            CameraEx cameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx();
            cameraEx.resetQuickAutoFocus(mode);
        }
    }

    public void setToggleFocusMode(boolean mode) {
        boolean isSupported = ((CameraEx.ParametersModifier) CameraSetting.getInstance().getSupportedParameters().second).isSupportedToggleFocusMode();
        if (isSupported) {
            CameraEx cameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx();
            cameraEx.setToggleFocusMode(mode);
        }
    }

    public boolean isFocusControl() {
        return CameraSetting.mFocusControlState != 1;
    }

    public boolean isFocusHeld() {
        return isAfMfControlHold();
    }

    public boolean isAfMfControlHold() {
        return 2 == CameraSetting.mFocusControlState;
    }

    private String convertApp2PF(String focusMode) {
        if (MANUAL.equals(focusMode)) {
            return MANUAL;
        }
        if (DMF.equals(focusMode)) {
            return DMF;
        }
        if ("auto".equals(focusMode)) {
            return "auto";
        }
        if (SMF.equals(focusMode)) {
            return SMF;
        }
        if ("off".equals(focusMode)) {
            return "off";
        }
        if (!"on".equals(focusMode)) {
            return null;
        }
        return "on";
    }

    public void cancelFocusControl() {
        int before = CameraSetting.mFocusControlState;
        resetQuickAutoFocus("af_woaf");
        if (!mKeepToggle) {
            CameraSetting.mFocusControlState = 1;
            setToggleFocusMode(false);
        }
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_CANCELFOCUSCONTROL).append(LOG_MSG_AFMFBUTTONSTATE).append(before).append(LOG_MSG_TO).append(CameraSetting.mFocusControlState);
        Log.d(TAG, strBuilder.toString());
    }

    public void initFocusControl() {
        int before = CameraSetting.mFocusControlState;
        CameraSetting.mFocusControlState = 1;
        List<String> list = ((CameraEx.ParametersModifier) CameraSetting.getInstance().getSupportedParameters().second).getSupportedQuickAutoFocus();
        if (list != null && list.contains("af_woaf")) {
            CameraEx cameraEx = this.mCamSet.getCamera();
            cameraEx.resetQuickAutoFocus("af_woaf");
        }
        boolean isSupported = ((CameraEx.ParametersModifier) CameraSetting.getInstance().getSupportedParameters().second).isSupportedToggleFocusMode();
        if (isSupported) {
            CameraEx cameraEx2 = this.mCamSet.getCamera();
            cameraEx2.setToggleFocusMode(false);
        }
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_INITFOCUSCONTROL).append(LOG_MSG_AFMFBUTTONSTATE).append(before).append(LOG_MSG_TO).append(CameraSetting.mFocusControlState);
        Log.d(TAG, strBuilder.toString());
    }

    private String convertPF2App(String focusMode) {
        if (MANUAL.equals(focusMode)) {
            return MANUAL;
        }
        if ("auto".equals(focusMode)) {
            return "auto";
        }
        if (DMF.equals(focusMode)) {
            return DMF;
        }
        if (SMF.equals(focusMode)) {
            return SMF;
        }
        if ("off".equals(focusMode)) {
            return "off";
        }
        if (!"on".equals(focusMode)) {
            return null;
        }
        return "on";
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        if (TAG_TRACKING_FOCUS.equals(tag)) {
            String pfValue = null;
            if (8 <= CameraSetting.getPfApiVersion()) {
                Pair<Camera.Parameters, CameraEx.ParametersModifier> param = this.mCamSet.getParameters();
                pfValue = ((CameraEx.ParametersModifier) param.second).getTrackingFocus();
            }
            StringBuilder strBuilder = sStringBuilder.get();
            strBuilder.replace(0, strBuilder.length(), LOG_MSG_GETTRACKINGFOCUS).append(pfValue);
            Log.i(TAG, strBuilder.toString());
            return convertPF2App(pfValue);
        }
        if (!mIsFocusShiftByControlWheel) {
            return getValue();
        }
        String value = getValue();
        if (value != null) {
            if (MANUAL.equals(value)) {
                return MANUAL_CONTROL_WHEEL;
            }
            if (SMF.equals(value)) {
                return SMF_CONTROL_WHEEL;
            }
            return value;
        }
        return value;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        if (TAG_TRACKING_FOCUS.equals(tag)) {
            if (8 <= CameraSetting.getPfApiVersion()) {
                String pfValue = convertApp2PF(value);
                Pair<Camera.Parameters, CameraEx.ParametersModifier> emptyParam = this.mCamSet.getEmptyParameters();
                ((CameraEx.ParametersModifier) emptyParam.second).setTrackingFocus(pfValue);
                StringBuilder strBuilder = sStringBuilder.get();
                strBuilder.replace(0, strBuilder.length(), LOG_MSG_SETTRACKINGFOCUS).append(pfValue);
                Log.i(TAG, strBuilder.toString());
                this.mCamSet.setParameters(emptyParam);
                return;
            }
            return;
        }
        if (!mIsFocusShiftByControlWheel) {
            setValue(value);
            return;
        }
        String ret = value;
        if (MANUAL_CONTROL_WHEEL.equals(ret)) {
            ret = MANUAL;
        } else if (SMF_CONTROL_WHEEL.equals(ret)) {
            ret = SMF;
        }
        setValue(ret);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ShootingModeController
    public List<String> getSupportedValue(String tag, int mode) {
        List<String> ret;
        if (TAG_TRACKING_FOCUS.equals(tag)) {
            ret = getSupportedTrackingFocusModes();
        } else {
            if (FocusModeDialDetector.hasFocusModeDial() && !TAG_FORCING_FOCUS_MODE.equals(tag)) {
                return null;
            }
            if (!mIsFocusShiftByControlWheel) {
                ret = getSupportedValue(mode);
            } else {
                ret = getSupportedValue(mode);
                if (ret != null) {
                    int index = ret.indexOf(MANUAL);
                    if (index >= 0) {
                        ret.set(index, MANUAL_CONTROL_WHEEL);
                    }
                    int index2 = ret.indexOf(SMF);
                    if (index2 >= 0) {
                        ret.set(index2, SMF_CONTROL_WHEEL);
                    }
                }
            }
        }
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_GETSUPPORTEDVALUE).append(ret);
        Log.i(TAG, strBuilder.toString());
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        if (TAG_TRACKING_FOCUS.equals(tag)) {
            List<String> ret = new ArrayList<>();
            List<String> supList = getSupportedValue(TAG_TRACKING_FOCUS);
            if (supList != null) {
                AvailableInfo.update();
                for (String str : supList) {
                    if (AvailableInfo.isAvailable(API_NAME_TRACKINGFOCUS, convertApp2PF(str))) {
                        ret.add(str);
                    }
                }
            }
            if (ret.size() == 0) {
                return null;
            }
            return ret;
        }
        if (!mIsFocusShiftByControlWheel) {
            return getAvailableValue();
        }
        List<String> ret2 = getAvailableValue();
        if (ret2 != null) {
            int index = ret2.indexOf(MANUAL);
            if (index >= 0) {
                if (2 == this.mCamSet.getCurrentMode()) {
                    ret2.remove(index);
                } else {
                    ret2.set(index, MANUAL_CONTROL_WHEEL);
                }
            }
            int index2 = ret2.indexOf(SMF);
            if (index2 >= 0) {
                ret2.set(index2, SMF_CONTROL_WHEEL);
                return ret2;
            }
            return ret2;
        }
        return ret2;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ShootingModeController, com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        if (!isMovieMode()) {
            return 0;
        }
        List<String> list = getSupportedValue(2);
        if (itemId.equals(TAG_FOCUS_MODE)) {
            if (list != null && list.size() != 0) {
                return 0;
            }
            return -1;
        }
        if (list.indexOf(itemId) != -1) {
            return 0;
        }
        return -1;
    }

    public static boolean isFocusShiftByControlWheel() {
        return mIsFocusShiftByControlWheel;
    }

    protected void setFocusShiftByControlWheelFlg() {
        mIsFocusShiftByControlWheel = false;
        if (Environment.isAvailableFocusDrive() && 1 == ScalarProperties.getInt("ui.mf.operation")) {
            mIsFocusShiftByControlWheel = true;
        }
    }

    /* loaded from: classes.dex */
    private class FocusModeControllerListener implements NotificationListener {
        private FocusModeControllerListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return FocusModeController.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (CameraNotificationManager.DEVICE_LENS_CHANGED.equals(tag)) {
                if (FocusModeController.mKeepToggle || FocusModeController.mKeepToggleStillToMovie) {
                    boolean unused = FocusModeController.mKeepToggle = false;
                } else {
                    FocusModeController.this.initFocusControl();
                }
            }
        }
    }

    public static void keepToggleFocusModeFlag(boolean bKeepToggle) {
        mKeepToggle = bKeepToggle;
    }

    public String getFocusModeFromFocusModeDial() {
        getValue();
        int scancode = FocusModeDialDetector.getFocusModeDialPosition();
        switch (scancode) {
            case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_MF /* 598 */:
                return MANUAL;
            case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_AF_C /* 599 */:
                return "af-c";
            case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_AF_S /* 600 */:
                return "af-s";
            case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_DMF /* 646 */:
                return DMF;
            default:
                return null;
        }
    }

    public List<String> getSupportedTrackingFocusModes() {
        List<String> supParams;
        List<String> supList = null;
        if (mSupportedTrackingFocus == null) {
            if (8 <= CameraSetting.getPfApiVersion() && (supParams = ((CameraEx.ParametersModifier) this.mCamSet.getSupportedParameters().second).getSupportedTrackingFocusModes()) != null) {
                supList = new ArrayList<>();
                for (String str : supParams) {
                    String appVal = convertPF2App(str);
                    if (appVal != null) {
                        supList.add(appVal);
                    }
                }
                if (supList.size() == 0) {
                    supList = null;
                }
            }
            mSupportedTrackingFocus = supList;
        }
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_GETSUPPORTEDTRACKINGFOCUSMODES).append(mSupportedTrackingFocus);
        Log.i(TAG, strBuilder.toString());
        return mSupportedTrackingFocus;
    }

    public void startTrackingFocus(int osdX, int osdY) {
        CameraEx cameraEx;
        if (8 <= CameraSetting.getPfApiVersion() && (cameraEx = this.mCamSet.getCamera()) != null) {
            cameraEx.startTrackingFocus(osdX, osdY);
            StringBuilder strBuilder = sStringBuilder.get();
            strBuilder.replace(0, strBuilder.length(), LOG_MSG_STARTTRACKINGFOCUS);
            Log.i(TAG, strBuilder.toString());
        }
    }

    public void stopTrackingFocus() {
        CameraEx cameraEx;
        if (8 <= CameraSetting.getPfApiVersion() && (cameraEx = this.mCamSet.getCamera()) != null) {
            cameraEx.stopTrackingFocus();
            StringBuilder strBuilder = sStringBuilder.get();
            strBuilder.replace(0, strBuilder.length(), LOG_MSG_STOPTRACKINGFOCUS);
            Log.i(TAG, strBuilder.toString());
        }
    }

    public CameraEx.TrackingFocusInfo getTrackingFocusInfo() {
        return CameraSetting.getInstance().getTrackingFocusInfo();
    }
}
