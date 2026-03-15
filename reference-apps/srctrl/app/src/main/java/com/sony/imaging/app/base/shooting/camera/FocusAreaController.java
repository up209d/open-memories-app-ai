package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.BaseBackUpKey;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.avio.DisplayManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class FocusAreaController extends AbstractController2<String> {
    public static final String AF_FIXED_TO_ILLUINATOR_FRAME = "AFFixedToIlluminatorFrame";
    private static final int AF_LOCAL_CENTER = 1;
    private static final String API_NAME = "setFocusAreaMode";
    private static final int CAUTION_GRP_ID_FOCUS_AREA_CONTRAST_AF_INVALID_GUIDE = 0;
    private static final int CAUTION_GRP_ID_FOCUS_AREA_PHASE_AF_INVALID_GUIDE = 1;
    private static final int CAUTION_ID_EXCLUSIVE_REASON_EZOOM = 2;
    public static int CENTER_X = 0;
    public static int CENTER_Y = 0;
    public static final int CONTRAST = 2;
    public static final String FLEX = "flex-spot";
    private static final String LOG_CHECKVALUE = "checkValue";
    private static final String LOG_EXCEPTION = ": Exeption occurs";
    private static final String LOG_MSG_APP = "app = ";
    private static final String LOG_MSG_COMMA = ", ";
    private static final String LOG_MSG_GETFOCUSAREAMODE = "getFocusAreaMode = ";
    private static final String LOG_MSG_GETFOCUSPOINT = "getFocusPoint = ";
    private static final String LOG_MSG_GETFOCUSPOINTINDEX = "getFocusPointIndex = ";
    private static final String LOG_MSG_GETSUPPORTEDFOCUSAREAMODES = "getSupportedFocusAreaModes = ";
    private static final String LOG_MSG_INVALID_ARGUMENT = "Invalid argument. ";
    private static final String LOG_MSG_ISDIGITALZOOMMAGOVERAFAREACHANGETO11 = "isDigitalZoomMagAFFrameNumberChangeTo11";
    private static final String LOG_MSG_ISDIGITALZOOMMAGOVERAFAREACHANGETOCENTER = "isDigitalZoomMagOverAFAreaChangeToCenter";
    private static final String LOG_MSG_PF = "pf = ";
    private static final String LOG_MSG_SETFOCUSAREAMODE = "setFocusAreaMode = ";
    private static final String LOG_MSG_SETFOCUSPOINT = "setFocusPoint = ";
    private static final String LOG_MSG_TAG = "tag = ";
    private static final String LOG_MSG_X = " x = ";
    private static final String LOG_MSG_Y = " y = ";
    private static final String LOG_NOT_SUPPORTED_EXCEPTION = "NotSupportedException";
    private static final String LOG_SET_FOCUS_POINT_TRY_IGNORE_INHIBIT = "Flexiblespot position try ignore inhibit";
    private static final String LOG_UNKNOWN_SENSOR_TYPE = "Unknown sensor type";
    public static final int PHASE_DIFF = 1;
    public static final String PHASE_SHIFT_SENSOR_15POINT = "15Points";
    public static final String PHASE_SHIFT_SENSOR_19POINT = "AF 19 points";
    public static final String PHASE_SHIFT_SENSOR_OTHER = "other";
    public static final String PHASE_SHIFT_SENSOR_UNKNOWN = "Unknown";
    public static final String SEMI_MULTI = "semi-multi";
    public static final String SEMI_WIDE = "semiwide";
    private static final String TAG = "FocusAreaController";
    public static final String TAG_FOCUS_AREA = "FocusArea";
    public static final int UNKNOWN = 0;
    private static final String[] additionalFactors;
    private static FocusAreaController mInstance;
    private static final String myName;
    private static boolean sInitFlag;
    private static final String[] tags;
    private boolean mIsAssistApp;
    private static final StringBuilderThreadLocal sStringBuilder = new StringBuilderThreadLocal();
    private static String sImageAspect = null;
    private static String sVideoAspect = null;
    public static final String MULTI = "multi";
    public static final String CENTER_WEIGHTED = "center";
    public static final String[] appSupportedContrastFocusAreaValues = {MULTI, CENTER_WEIGHTED, "flex-spot"};
    public static final String WIDE = "wide";
    public static final String FIX_CENTER = "spot";
    public static final String LOCAL = "local";
    public static final String[] appSupportedPhaseDiffFocusAreaValues = {WIDE, FIX_CENTER, LOCAL};
    private static HashMap<String, String> app2pf = new HashMap<>();
    private static HashMap<String, String> pf2App = new HashMap<>();
    private FocusAreaInhibition mFocusAreaInh = null;
    private NotificationListener mListener = new MyFocusAreaControllerListener();
    CameraSetting mCamSet = CameraSetting.getInstance();
    private BackUpUtil mBackUp = BackUpUtil.getInstance();

    static {
        app2pf.put(MULTI, MULTI);
        app2pf.put(SEMI_MULTI, SEMI_MULTI);
        app2pf.put("flex-spot", "flex-spot");
        app2pf.put(CENTER_WEIGHTED, CENTER_WEIGHTED);
        app2pf.put(WIDE, WIDE);
        app2pf.put(SEMI_WIDE, SEMI_WIDE);
        app2pf.put(LOCAL, LOCAL);
        app2pf.put(FIX_CENTER, FIX_CENTER);
        pf2App.put(MULTI, MULTI);
        pf2App.put(SEMI_MULTI, SEMI_MULTI);
        pf2App.put("flex-spot", "flex-spot");
        pf2App.put(CENTER_WEIGHTED, CENTER_WEIGHTED);
        pf2App.put(WIDE, WIDE);
        pf2App.put(SEMI_WIDE, SEMI_WIDE);
        pf2App.put(LOCAL, LOCAL);
        pf2App.put(FIX_CENTER, FIX_CENTER);
        CENTER_X = 0;
        CENTER_Y = 0;
        sInitFlag = false;
        myName = FocusAreaController.class.getSimpleName();
        additionalFactors = new String[]{"INH_FACTOR_CAM_SET_EXC_STILL_AUTO2_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_AUTO2_MODE_BODY_PHASE_SHIFT_AF_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_AUTO2_MODE_FOCUS_DIAL_TYPE_P", "INH_FACTOR_CAM_SET_EXC_MOVIE_AUTO2_MODE_TYPE_P", "INH_FACTOR_CAM_SET_EXC_STILL_AUTO2_MODE_BODY_FOCAL_PLANE_PHASE_SHIFT_AF_TYPE_P", "INH_FACTOR_CAM_SET_EXC_MOVIE_AUTO2_MODE_MF_NOT_SUPPORT_TYPE_P"};
        tags = new String[]{CameraNotificationManager.DEVICE_LENS_CHANGED, CameraNotificationManager.ZOOM_INFO_CHANGED, CameraNotificationManager.AUTO_FOCUS_AREA, CameraNotificationManager.SCENE_MODE, CameraNotificationManager.AUTOSCENEMODE_CHANGED, DisplayModeObserver.TAG_YUVLAYOUT_CHANGE, CameraNotificationManager.MOVIE_FORMAT};
    }

    public static void initAFFlexibleSpotSetCenterFlag() {
        BootFactor factor = BootFactor.get();
        int bootfactor = factor.bootFactor;
        if (bootfactor == 0) {
            sInitFlag = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAFFlexibleSpotToCenter() {
        if (2 <= CameraSetting.getPfApiVersion() && this.mIsAssistApp && sInitFlag) {
            try {
                String currentValue = getValue();
                if ("flex-spot".equals(currentValue)) {
                    setFocusPointIgnoreInhibit(CENTER_X, CENTER_Y);
                    sInitFlag = false;
                }
            } catch (IController.NotSupportedException e) {
            }
        }
    }

    public static FocusAreaController getInstance() {
        if (mInstance == null) {
            new FocusAreaController();
        }
        return mInstance;
    }

    private static void setController(FocusAreaController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public FocusAreaController() {
        this.mIsAssistApp = false;
        this.mIsAssistApp = ExecutorCreator.getInstance().isAssistApp();
        setController(this);
    }

    public static final String getName() {
        return myName;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraSet() {
        super.onCameraSet();
        checkValue();
        setAFFlexibleSpotToCenter();
        if (this.mBackUp != null) {
            if (isDigitalZoomMagOverAFAreaChangeToCenter()) {
                this.mBackUp.setPreference(BaseBackUpKey.ID_PHASE_DIFF_AF_DIGITAL_ZOOM_STATUS, true);
            } else {
                this.mBackUp.setPreference(BaseBackUpKey.ID_PHASE_DIFF_AF_DIGITAL_ZOOM_STATUS, false);
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        super.onGetInitParameters(params);
        if (this.mListener != null) {
            CameraNotificationManager.getInstance().setNotificationListener(this.mListener);
            DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
        }
        sImageAspect = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        sVideoAspect = MovieFormatController.getInstance().getValue(MovieFormatController.MOVIE_ASPECT);
        this.mFocusAreaInh = new FocusAreaInhibition();
        this.mFocusAreaInh.initialize();
    }

    private void checkValue() {
        Log.i(TAG, LOG_CHECKVALUE);
        if (ExecutorCreator.getInstance().isAssistApp()) {
            String currentValue = null;
            try {
                currentValue = getValue();
            } catch (IController.NotSupportedException e) {
                Log.e(TAG, LOG_NOT_SUPPORTED_EXCEPTION);
            }
            List<String> availableList = getAvailableValue();
            int sensorType = getSensorType();
            int settingSensorType = getSensorType(currentValue);
            if (currentValue != null && availableList != null && sensorType == settingSensorType) {
                if (2 == sensorType) {
                    if (appSupportedContrastFocusAreaValues != null && !Arrays.asList(appSupportedContrastFocusAreaValues).contains(currentValue)) {
                        String[] arr$ = appSupportedContrastFocusAreaValues;
                        for (String s : arr$) {
                            if (availableList.contains(s)) {
                                setValue(s);
                                return;
                            }
                        }
                        return;
                    }
                    updatePreference(currentValue);
                    return;
                }
                if (1 == sensorType) {
                    if (appSupportedPhaseDiffFocusAreaValues != null && !Arrays.asList(appSupportedPhaseDiffFocusAreaValues).contains(currentValue)) {
                        String[] arr$2 = appSupportedPhaseDiffFocusAreaValues;
                        for (String s2 : arr$2) {
                            if (availableList.contains(s2)) {
                                setValue(s2);
                                return;
                            }
                        }
                        return;
                    }
                    updatePreference(currentValue);
                    return;
                }
                Log.e(TAG, LOG_UNKNOWN_SENSOR_TYPE);
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetTermParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        super.onGetTermParameters(params);
        if (this.mListener != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mListener);
            DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
        }
        if (this.mFocusAreaInh != null) {
            this.mFocusAreaInh.terminate();
            this.mFocusAreaInh = null;
        }
    }

    public void setValue(String value) {
        int sensorType = getSensorType();
        int settingSensorType = getSensorType(value);
        if (sensorType == settingSensorType) {
            super.setValue(null, value);
            updatePreference(value);
        }
    }

    protected void updatePreference(String value) {
        int settingSensorType = getSensorType(value);
        BackUpUtil backup = BackUpUtil.getInstance();
        if (2 == settingSensorType) {
            backup.setPreference(BaseBackUpKey.ID_CONTRAST_FOCUS_AREA, value);
            setAFFlexibleSpotToCenter();
        } else if (1 == settingSensorType) {
            backup.setPreference(BaseBackUpKey.ID_PHASE_DIFF_FOCUS_AREA, value);
        }
    }

    public String getValue() throws IController.NotSupportedException {
        return super.getValue(null);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController2, com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        setValue(value);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController2, com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        return getValue();
    }

    public Pair<Integer, Integer> getFocusPoint() {
        Pair<Integer, Integer> p = ((CameraEx.ParametersModifier) this.mCamSet.getParameters().second).getFocusPoint();
        StringBuilder builder = sStringBuilder.get();
        builder.replace(0, builder.length(), LOG_MSG_GETFOCUSPOINT).append(LOG_MSG_X).append(p.first).append(", ").append(LOG_MSG_Y).append(p.second);
        Log.i(TAG, builder.toString());
        return p;
    }

    public void setFocusPoint(int centerX, int centerY) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> emptyParam = this.mCamSet.getEmptyParameters();
        ((CameraEx.ParametersModifier) emptyParam.second).setFocusPoint(centerX, centerY);
        this.mCamSet.setParameters(emptyParam);
    }

    public void setFocusPointIgnoreInhibit(int centerX, int centerY) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> emptyParam = this.mCamSet.getEmptyParameters();
        ((CameraEx.ParametersModifier) emptyParam.second).setFocusPoint(centerX, centerY);
        ((CameraEx.ParametersModifier) emptyParam.second).tryIgnoreInhibit();
        try {
            this.mCamSet.setParameters(emptyParam);
        } catch (Exception e) {
            StringBuilder builder = sStringBuilder.get();
            builder.replace(0, builder.length(), LOG_SET_FOCUS_POINT_TRY_IGNORE_INHIBIT).append(LOG_EXCEPTION);
            Log.e(TAG, builder.toString());
        }
    }

    public void setFocusIndex(int index) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> emptyParam = this.mCamSet.getEmptyParameters();
        ((CameraEx.ParametersModifier) emptyParam.second).setFocusPoint(index);
        StringBuilder builder = sStringBuilder.get();
        builder.replace(0, builder.length(), LOG_MSG_SETFOCUSPOINT).append(index);
        Log.i(TAG, builder.toString());
        this.mCamSet.setParameters(emptyParam);
    }

    public int getFocusIndex() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
        int index = ((CameraEx.ParametersModifier) p.second).getFocusPointIndex();
        StringBuilder builder = sStringBuilder.get();
        builder.replace(0, builder.length(), LOG_MSG_GETFOCUSPOINTINDEX).append(index);
        Log.i(TAG, builder.toString());
        return index;
    }

    public List<String> getSupportedValue() {
        List<String> ret = new ArrayList<>();
        List<String> suplist = ((CameraEx.ParametersModifier) this.mCamSet.getSupportedParameters().second).getSupportedFocusAreaModes();
        StringBuilder builder = sStringBuilder.get();
        builder.replace(0, builder.length(), LOG_MSG_GETSUPPORTEDFOCUSAREAMODES).append(suplist);
        Log.i(TAG, builder.toString());
        int currentSensorType = getSensorType();
        if (suplist != null) {
            ret = new ArrayList<>();
            for (String s : suplist) {
                String app = convertPF2App(s);
                if (app != null && (currentSensorType == getSensorType(app) || currentSensorType == 0)) {
                    ret.add(app);
                }
            }
        }
        if (ret.size() == 0) {
            return null;
        }
        return ret;
    }

    private int getSensorType(String appvalue) {
        if (MULTI.equals(appvalue) || CENTER_WEIGHTED.equals(appvalue) || "flex-spot".equals(appvalue) || SEMI_MULTI.equals(appvalue)) {
            return 2;
        }
        if (!WIDE.equals(appvalue) && !FIX_CENTER.equals(appvalue) && !LOCAL.equals(appvalue) && !SEMI_WIDE.equals(appvalue)) {
            return 0;
        }
        return 1;
    }

    public List<String> getAvailableValue() {
        ArrayList<String> ret = new ArrayList<>();
        List<String> appSupList = getSupportedValue();
        if (appSupList != null) {
            AvailableInfo.update();
            for (String app : appSupList) {
                String pf = convertApp2PF(app);
                if (pf != null && AvailableInfo.isAvailable(API_NAME, pf)) {
                    ret.add(app);
                }
            }
        }
        if (ret.size() == 0) {
            return null;
        }
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController2
    public String convertPF2App(String pf) {
        String ret = pf2App.get(pf);
        if (ret == null) {
            StringBuilder builder = sStringBuilder.get();
            builder.replace(0, builder.length(), LOG_MSG_INVALID_ARGUMENT).append(LOG_MSG_PF).append(pf);
            Log.w(TAG, builder.toString());
        }
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController2
    public String convertApp2PF(String app) {
        String ret = app2pf.get(app);
        if (ret == null) {
            StringBuilder builder = sStringBuilder.get();
            builder.replace(0, builder.length(), LOG_MSG_INVALID_ARGUMENT).append(LOG_MSG_APP).append(app);
            Log.w(TAG, builder.toString());
        }
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController2
    public String getPFValue(String tag) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
        String mode = ((CameraEx.ParametersModifier) p.second).getFocusAreaMode();
        StringBuilder builder = sStringBuilder.get();
        builder.replace(0, builder.length(), LOG_MSG_GETFOCUSAREAMODE).append(mode);
        Log.i(TAG, builder.toString());
        return mode;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController2
    public void setValueToPF(String tag, String value) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> emptyParam = this.mCamSet.getEmptyParameters();
        ((CameraEx.ParametersModifier) emptyParam.second).setFocusAreaMode(value);
        StringBuilder builder = sStringBuilder.get();
        builder.replace(0, builder.length(), LOG_MSG_SETFOCUSAREAMODE).append(value);
        Log.i(TAG, builder.toString());
        this.mCamSet.setParameters(emptyParam);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController2, com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return getSupportedValue();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController2, com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return getAvailableValue();
    }

    protected boolean isAdditionalFactor() {
        boolean ret = false;
        if (true == isNeedToCheckAdditionalFactor()) {
            int c = additionalFactors.length;
            for (int i = 0; i < c && !ret; i++) {
                ret = AvailableInfo.isFactor(additionalFactors[i]);
            }
        }
        return ret;
    }

    protected boolean isNeedToCheckAdditionalFactor() {
        return true;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        if (1 == getSensorType() && isDigitalZoomMagOverAFAreaChangeToCenter()) {
            return false;
        }
        AvailableInfo.update();
        boolean ret = AvailableInfo.isAvailable(API_NAME, null);
        if (true == ret) {
            ret = !isAdditionalFactor();
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public boolean isUnavailableSceneFactor(String tag) {
        if (TAG_FOCUS_AREA.equals(tag)) {
            boolean b = isUnavailableAPISceneFactor(API_NAME, null);
            return b;
        }
        StringBuilder builder = sStringBuilder.get();
        builder.replace(0, builder.length(), LOG_MSG_INVALID_ARGUMENT).append("tag = ").append(tag);
        Log.w(TAG, builder.toString());
        return false;
    }

    public int getSensorType() {
        CameraEx.LensInfo info = this.mCamSet.getLensInfo();
        if (info != null) {
            String sensor = info.PhaseShiftSensor;
            return (PHASE_SHIFT_SENSOR_OTHER.equals(sensor) || "Unknown".equals(sensor)) ? 2 : 1;
        }
        if (2 > CameraSetting.getPfApiVersion()) {
            return 0;
        }
        List<String> suplist = ((CameraEx.ParametersModifier) this.mCamSet.getSupportedParameters().second).getSupportedFocusAreaModes();
        if (suplist != null && (suplist.contains(MULTI) || suplist.contains(CENTER_WEIGHTED) || suplist.contains("flex-spot") || suplist.contains(SEMI_MULTI))) {
            return 2;
        }
        return 1;
    }

    /* loaded from: classes.dex */
    class FocusAreaInhibition {
        String previousSceneMode;
        private boolean other2auto_contrast = false;
        private boolean other2auto_phase = false;
        private boolean auto2other_contrast = false;
        private boolean auto2other2_phase = false;

        FocusAreaInhibition() {
        }

        void initialize() {
            this.previousSceneMode = ((Camera.Parameters) FocusAreaController.this.mCamSet.getParameters().first).getSceneMode();
            if ("auto".equals(this.previousSceneMode)) {
                exec();
            }
        }

        void terminate() {
        }

        void exec() {
            if (FocusAreaController.this.isNeedToCheckAdditionalFactor()) {
                String currentSceneMode = ((Camera.Parameters) FocusAreaController.this.mCamSet.getParameters().first).getSceneMode();
                if ("auto".equals(currentSceneMode)) {
                    toAuto();
                    this.auto2other_contrast = false;
                    this.auto2other2_phase = false;
                } else if ("auto".equals(this.previousSceneMode)) {
                    toOther();
                    this.other2auto_contrast = false;
                    this.other2auto_phase = false;
                } else {
                    toOther();
                    this.other2auto_contrast = false;
                    this.other2auto_phase = false;
                }
                this.previousSceneMode = currentSceneMode;
            }
        }

        void toAuto() {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> current = FocusAreaController.this.mCamSet.getParameters();
            String currentFocusAreaMode = ((CameraEx.ParametersModifier) current.second).getFocusAreaMode();
            int sensorType = FocusAreaController.this.getSensorType();
            if (2 == sensorType && (!this.other2auto_contrast || !FocusAreaController.MULTI.equals(currentFocusAreaMode))) {
                Pair<Camera.Parameters, CameraEx.ParametersModifier> p = FocusAreaController.this.mCamSet.getEmptyParameters();
                ((CameraEx.ParametersModifier) p.second).setFocusAreaMode(FocusAreaController.MULTI);
                FocusAreaController.this.mCamSet.setParametersDirect(p);
                this.other2auto_contrast = true;
                return;
            }
            if (1 == sensorType) {
                if (!this.other2auto_phase || !FocusAreaController.WIDE.equals(currentFocusAreaMode)) {
                    Pair<Camera.Parameters, CameraEx.ParametersModifier> p2 = FocusAreaController.this.mCamSet.getEmptyParameters();
                    ((CameraEx.ParametersModifier) p2.second).setFocusAreaMode(FocusAreaController.WIDE);
                    FocusAreaController.this.mCamSet.setParametersDirect(p2);
                    this.other2auto_phase = true;
                }
            }
        }

        void toOther() {
            int sensorType = FocusAreaController.this.getSensorType();
            if (2 == sensorType && !this.auto2other_contrast) {
                String value = FocusAreaController.this.mBackUp.getPreferenceString(BaseBackUpKey.ID_CONTRAST_FOCUS_AREA, FocusAreaController.MULTI);
                Pair<Camera.Parameters, CameraEx.ParametersModifier> p = FocusAreaController.this.mCamSet.getEmptyParameters();
                ((CameraEx.ParametersModifier) p.second).setFocusAreaMode(value);
                FocusAreaController.this.mCamSet.setParametersDirect(p);
                FocusAreaController.this.setAFFlexibleSpotToCenter();
                this.auto2other_contrast = true;
                return;
            }
            if (1 == sensorType && !this.auto2other2_phase) {
                String value2 = FocusAreaController.this.mBackUp.getPreferenceString(BaseBackUpKey.ID_PHASE_DIFF_FOCUS_AREA, FocusAreaController.WIDE);
                Pair<Camera.Parameters, CameraEx.ParametersModifier> p2 = FocusAreaController.this.mCamSet.getEmptyParameters();
                ((CameraEx.ParametersModifier) p2.second).setFocusAreaMode(value2);
                FocusAreaController.this.mCamSet.setParametersDirect(p2);
                this.auto2other2_phase = true;
            }
        }
    }

    /* loaded from: classes.dex */
    private class MyFocusAreaControllerListener implements NotificationListener {
        private MyFocusAreaControllerListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return FocusAreaController.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (CameraNotificationManager.DEVICE_LENS_CHANGED.equals(tag)) {
                int sensorType = FocusAreaController.this.getSensorType();
                if (2 == sensorType) {
                    FocusAreaController.this.refreshContrastAreaMode();
                    FocusAreaController.this.setAFFlexibleSpotToCenter();
                } else if (1 == sensorType) {
                    FocusAreaController.this.refreshPhaseDiffAreaMode();
                    FocusAreaController.this.changePhaseDiffAFAreaMode();
                }
            } else if (CameraNotificationManager.ZOOM_INFO_CHANGED.equals(tag)) {
                FocusAreaController.this.changePhaseDiffAFAreaMode();
            } else if (CameraNotificationManager.AUTO_FOCUS_AREA.equals(tag)) {
                FocusAreaController.this.setAFFlexibleSpotToCenter();
            } else if (DisplayModeObserver.TAG_YUVLAYOUT_CHANGE.equals(tag)) {
                if (2 <= CameraSetting.getPfApiVersion()) {
                    String currentAsp = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
                    if (FocusAreaController.sImageAspect != null && !FocusAreaController.sImageAspect.equals(currentAsp)) {
                        FocusAreaController.this.setFocusPointIgnoreInhibit(FocusAreaController.CENTER_X, FocusAreaController.CENTER_Y);
                    }
                    String unused = FocusAreaController.sImageAspect = currentAsp;
                }
            } else if (CameraNotificationManager.MOVIE_FORMAT.equals(tag)) {
                String videoAsp = MovieFormatController.getInstance().getValue(MovieFormatController.MOVIE_ASPECT);
                if (FocusAreaController.sVideoAspect != null && !FocusAreaController.sVideoAspect.equals(videoAsp)) {
                    FocusAreaController.this.setFocusPointIgnoreInhibit(FocusAreaController.CENTER_X, FocusAreaController.CENTER_Y);
                }
                String unused2 = FocusAreaController.sVideoAspect = videoAsp;
            }
            if (CameraNotificationManager.SCENE_MODE.equals(tag) || CameraNotificationManager.AUTOSCENEMODE_CHANGED.equals(tag) || CameraNotificationManager.DEVICE_LENS_CHANGED.equals(tag) || CameraNotificationManager.AUTO_FOCUS_AREA.equals(tag)) {
                FocusAreaController.this.mFocusAreaInh.exec();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshContrastAreaMode() {
        if (!this.mIsAssistApp) {
            String currentAFareaValue = this.mBackUp.getPreferenceString(BaseBackUpKey.ID_CONTRAST_FOCUS_AREA, MULTI);
            setValue(currentAFareaValue);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshPhaseDiffAreaMode() {
        if (!this.mIsAssistApp) {
            String currentAFareaValue = this.mBackUp.getPreferenceString(BaseBackUpKey.ID_PHASE_DIFF_FOCUS_AREA, WIDE);
            setValue(currentAFareaValue);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changePhaseDiffAFAreaMode() {
        String currentAFareaValue;
        String currentAFareaValue2;
        Log.i(TAG, "changePhaseDiffAFAreaMode");
        if (getSensorType() == 1) {
            if (isDigitalZoomMagOverAFAreaChangeToCenter()) {
                boolean isMagOverThreshold = this.mBackUp.getPreferenceBoolean(BaseBackUpKey.ID_PHASE_DIFF_AF_DIGITAL_ZOOM_STATUS, true);
                if (!isMagOverThreshold) {
                    this.mBackUp.setPreference(BaseBackUpKey.ID_PHASE_DIFF_AF_DIGITAL_ZOOM_STATUS, true);
                    if (this.mIsAssistApp) {
                        try {
                            currentAFareaValue2 = getValue();
                        } catch (IController.NotSupportedException e) {
                            Log.e(TAG, "NotSupportedException: FocusAreaController getValue()");
                            currentAFareaValue2 = WIDE;
                        }
                    } else {
                        currentAFareaValue2 = this.mBackUp.getPreferenceString(BaseBackUpKey.ID_PHASE_DIFF_FOCUS_AREA, WIDE);
                    }
                    if (LOCAL.equals(currentAFareaValue2)) {
                        setFocusIndex(1);
                        return;
                    }
                    return;
                }
                return;
            }
            boolean isMagOverThreshold2 = this.mBackUp.getPreferenceBoolean(BaseBackUpKey.ID_PHASE_DIFF_AF_DIGITAL_ZOOM_STATUS, false);
            if (isMagOverThreshold2) {
                this.mBackUp.setPreference(BaseBackUpKey.ID_PHASE_DIFF_AF_DIGITAL_ZOOM_STATUS, false);
                if (this.mIsAssistApp) {
                    try {
                        currentAFareaValue = getValue();
                    } catch (IController.NotSupportedException e2) {
                        Log.e(TAG, "NotSupportedException: FocusAreaController getValue()");
                        currentAFareaValue = WIDE;
                    }
                } else {
                    currentAFareaValue = this.mBackUp.getPreferenceString(BaseBackUpKey.ID_PHASE_DIFF_FOCUS_AREA, WIDE);
                }
                setValue(currentAFareaValue);
                if (LOCAL.equals(currentAFareaValue)) {
                    setFocusIndex(1);
                }
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        int ret = super.getCautionIndex(itemId);
        if (TAG_FOCUS_AREA.equals(itemId)) {
            int type = getSensorType();
            if (type != 2 && type == 1) {
                if (2 > CameraSetting.getPfApiVersion() && isDigitalZoomMagOverAFAreaChangeToCenter()) {
                    return 2;
                }
                return 1;
            }
            return 0;
        }
        return ret;
    }

    public boolean isDigitalZoomMagOverAFAreaChangeToCenter() {
        CameraEx.FocusAreaInfos info;
        Log.i(TAG, LOG_MSG_ISDIGITALZOOMMAGOVERAFAREACHANGETOCENTER);
        if (1 > CameraSetting.getPfApiVersion()) {
            return false;
        }
        String currentValue = null;
        try {
            currentValue = getValue();
        } catch (IController.NotSupportedException e) {
            Log.e(TAG, LOG_NOT_SUPPORTED_EXCEPTION);
        }
        if (currentValue == null || (info = getCurrentFocusAreaInfos(currentValue)) == null) {
            return false;
        }
        int enable_cnt = 0;
        CameraEx.FocusAreaRectInfo[] arr$ = info.rectInfos;
        for (CameraEx.FocusAreaRectInfo rectInfo : arr$) {
            if (rectInfo.enable) {
                enable_cnt++;
            }
        }
        if (enable_cnt > 1) {
            return false;
        }
        return true;
    }

    public boolean isDigitalZoomMagAFFrameNumberChangeTo11() {
        int maxPtNum;
        Log.i(TAG, LOG_MSG_ISDIGITALZOOMMAGOVERAFAREACHANGETO11);
        if (1 > CameraSetting.getPfApiVersion()) {
            return false;
        }
        String currentValue = null;
        try {
            currentValue = getValue();
        } catch (IController.NotSupportedException e) {
            Log.e(TAG, LOG_NOT_SUPPORTED_EXCEPTION);
        }
        if (currentValue == null) {
            return false;
        }
        CameraEx.FocusAreaInfos info = getCurrentFocusAreaInfos(currentValue);
        int enable_cnt = 0;
        if (info == null) {
            return false;
        }
        CameraEx.FocusAreaRectInfo[] arr$ = info.rectInfos;
        for (CameraEx.FocusAreaRectInfo rectInfo : arr$) {
            if (rectInfo.enable) {
                enable_cnt++;
            }
        }
        String phaseDiffSensorType = getPhaseShiftSensorType();
        if (PHASE_SHIFT_SENSOR_15POINT.equals(phaseDiffSensorType)) {
            maxPtNum = 15;
        } else {
            maxPtNum = 19;
        }
        if (11 > enable_cnt || enable_cnt >= maxPtNum) {
            return false;
        }
        return true;
    }

    public CameraEx.FocusAreaInfos getFocusAreaInfos(int aspect, int viewPattern, String focusAreaMode) {
        return CameraSetting.getInstance().getFocusAreaRectInfos(focusAreaMode, aspect, viewPattern);
    }

    public CameraEx.FocusAreaInfos getCurrentFocusAreaInfos(String focusAreaMode) {
        DisplayManager.DeviceStatus deviceStatus = DisplayModeObserver.getInstance().getActiveDeviceStatus();
        int viewPattern = deviceStatus == null ? 4 : deviceStatus.viewPattern;
        String picAsp = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        int aspect = -1;
        if (PictureSizeController.ASPECT_16_9.equals(picAsp)) {
            aspect = 1;
        } else if (PictureSizeController.ASPECT_3_2.equals(picAsp)) {
            aspect = 0;
        } else if (PictureSizeController.ASPECT_4_3.equals(picAsp) || PictureSizeController.ASPECT_1_1.equals(picAsp)) {
        }
        return getFocusAreaInfos(aspect, viewPattern, focusAreaMode);
    }

    private String getPhaseShiftSensorType() {
        CameraEx.LensInfo info = this.mCamSet.getLensInfo();
        if (info == null) {
            return "Unknown";
        }
        String ret = info.PhaseShiftSensor;
        return ret;
    }
}
