package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import com.sony.imaging.app.base.BaseBackUpKey;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class DROAutoHDRController extends AbstractController {
    private static final String API_NAME_DRO_LEVEL = "setDROLevel";
    private static final String API_NAME_DRO_MODE = "setDROMode";
    private static final String API_NAME_HDR_EV = "setHDRExposureDifferenceLevel";
    private static final String API_NAME_HDR_MODE = "setHDRMode";
    private static final int BACKUP_VALUE_MODE_AUTO = 100;
    private static final int DEFAULT_BACKUP_VALUE_EV = 100;
    private static final int DEFAULT_BACKUP_VALUE_LEVEL = 100;
    public static final String DRO_LEVEL_1 = "DroL1";
    public static final String DRO_LEVEL_2 = "DroL2";
    public static final String DRO_LEVEL_3 = "DroL3";
    public static final String DRO_LEVEL_4 = "DroL4";
    public static final String DRO_LEVEL_5 = "DroL5";
    private static final String DRO_LEVEL_SUFFIX = "DroL";
    public static final String HDR_EV_1 = "HdrEV1";
    public static final String HDR_EV_2 = "HdrEV2";
    public static final String HDR_EV_3 = "HdrEV3";
    public static final String HDR_EV_4 = "HdrEV4";
    public static final String HDR_EV_5 = "HdrEV5";
    public static final String HDR_EV_6 = "HdrEV6";
    private static final String HDR_EV_SUFFIX = "HdrEV";
    private static final String LOG_MSG_COMMA = ", ";
    private static final String LOG_MSG_GETDROLEVEL = "getDROLevel = ";
    private static final String LOG_MSG_GETDROMODE = "getDROMode = ";
    private static final String LOG_MSG_GETHDREV = "getHDRExposureDifferenceLevel = ";
    private static final String LOG_MSG_GETHDRMODE = "getHDRMode = ";
    private static final String LOG_MSG_GETMAXDROLEVEL = "getMaxDROLevel = ";
    private static final String LOG_MSG_GETMAXHDREV = "getMaxHDRExposureDifferenceLevel = ";
    private static final String LOG_MSG_GETMINHDREV = "getMinHDRExposureDifferenceLevel = ";
    private static final String LOG_MSG_GETSUPPORTEDDROMODE = "getSupportedDROModes = ";
    private static final String LOG_MSG_GETSUPPORTEDHDRMODE = "getSupportedHDRModes = ";
    private static final String LOG_MSG_ILLEGALARG = "IllegalArgument is set. ";
    private static final String LOG_MSG_INVALIDMODE = "Invalid DRO / HDR mode. mode = ";
    private static final String LOG_MSG_ITEMID = "itemId = ";
    private static final String LOG_MSG_MODE = "mode = ";
    private static final String LOG_MSG_SETDROLEVEL = "setDROLevel = ";
    private static final String LOG_MSG_SETDROMODE = "setDROMode = ";
    private static final String LOG_MSG_SETHDREV = "setHDRExposureDifferenceLevel = ";
    private static final String LOG_MSG_SETHDRMODE = "setHDRMode = ";
    private static final String LOG_MSG_TAG = "tag = ";
    public static final String MENU_ITEM_ID_DRO = "Dro";
    public static final String MENU_ITEM_ID_DROHDR = "DroHdr";
    public static final String MENU_ITEM_ID_HDR = "Hdr";
    public static final String MODE_DRO_AUTO = "DroAuto";
    private static final String MODE_DRO_ON = "DroOn";
    public static final String MODE_HDR_AUTO = "HdrAuto";
    private static final String MODE_HDR_ON = "HdrOn";
    public static final String MODE_OFF = "off";
    private static final int PARAM_INDEX_AUTO = 2;
    private static final int PARAM_INDEX_OFF = 0;
    private static final int PARAM_INDEX_ON = 1;
    private static final int RAW_MIN_DRO_LEVEL = 1;
    private static final String TAG = "DROAutoHDRController";
    private static DROAutoHDRController mInstance;
    private static int mRawMaxDROLevel;
    private static int mRawMaxHDRExposureDifferenceLevel;
    private static int mRawMinHDRExposureDifferenceLevel;
    private static List<String> mRawSupportedDroModes;
    private static List<String> mRawSupportedHdrModes;
    private static List<String> mSupportedDroLevels;
    private static boolean[] mSupportedDroModeBooleanTable;
    private static List<String> mSupportedHdrLevels;
    private static boolean[] mSupportedHdrModeBooleanTable;
    private static List<String> mSupportedItemIds;
    private static List<String> mSupportedModes;
    private static final String myName;
    private CameraSetting mCameraSetting;
    private static final StringBuilder STRBUILD = new StringBuilder();
    private static final SparseArray<String> DRO_LEVEL_STRING_POOL = new SparseArray<>();
    private static final SparseArray<String> HDR_EV_STRING_POOL = new SparseArray<>();
    private static final String[] DRO_MODE_PARAMS = {"off", "on", "auto"};
    private static final String[] HDR_MODE_PARAMS = {"off", "on", "auto"};
    private static final HashMap<String, Pair<String, String>> MODE_API_SETS = new HashMap<>();

    static {
        MODE_API_SETS.put(API_NAME_DRO_LEVEL, new Pair<>(API_NAME_DRO_MODE, "on"));
        MODE_API_SETS.put(API_NAME_HDR_EV, new Pair<>(API_NAME_HDR_MODE, "on"));
        myName = DROAutoHDRController.class.getSimpleName();
    }

    public static final String getName() {
        return myName;
    }

    public static DROAutoHDRController getInstance() {
        if (mInstance == null) {
            new DROAutoHDRController();
        }
        return mInstance;
    }

    private static void setController(DROAutoHDRController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public DROAutoHDRController() {
        this.mCameraSetting = null;
        this.mCameraSetting = CameraSetting.getInstance();
        createSupported();
        setController(this);
    }

    protected void createSupported() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getSupportedParameters(1);
        if (mRawSupportedDroModes == null) {
            mRawSupportedDroModes = ((CameraEx.ParametersModifier) params.second).getSupportedDROModes();
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_GETSUPPORTEDDROMODE).append(mRawSupportedDroModes);
            Log.i(TAG, STRBUILD.toString());
        }
        if (mRawSupportedHdrModes == null) {
            mRawSupportedHdrModes = ((CameraEx.ParametersModifier) params.second).getSupportedHDRModes();
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_GETSUPPORTEDHDRMODE).append(mRawSupportedHdrModes);
            Log.i(TAG, STRBUILD.toString());
        }
        if (mSupportedDroModeBooleanTable == null) {
            mSupportedDroModeBooleanTable = createDroSupportedBooleanTable();
        }
        if (mSupportedHdrModeBooleanTable == null) {
            mSupportedHdrModeBooleanTable = createHdrSupportedBooleanTable();
        }
        if (mSupportedModes == null) {
            mSupportedModes = getValidModes(mSupportedDroModeBooleanTable, mSupportedHdrModeBooleanTable);
        }
        if (mSupportedItemIds == null) {
            mSupportedItemIds = createSupportedItemIds();
        }
        if (mSupportedDroLevels == null) {
            mRawMaxDROLevel = ((CameraEx.ParametersModifier) params.second).getMaxDROLevel();
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_GETMAXDROLEVEL).append(mRawMaxDROLevel);
            Log.i(TAG, STRBUILD.toString());
            mSupportedDroLevels = createSupportedDROLevels();
        }
        if (mSupportedHdrLevels == null) {
            mRawMinHDRExposureDifferenceLevel = ((CameraEx.ParametersModifier) params.second).getMinHDRExposureDifferenceLevel();
            mRawMaxHDRExposureDifferenceLevel = ((CameraEx.ParametersModifier) params.second).getMaxHDRExposureDifferenceLevel();
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_GETMINHDREV).append(mRawMinHDRExposureDifferenceLevel).append(", ").append(LOG_MSG_GETMAXHDREV).append(mRawMaxHDRExposureDifferenceLevel);
            Log.i(TAG, STRBUILD.toString());
            mSupportedHdrLevels = createSupportedHDRExposureDifferenceLevels();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraReopened() {
        mRawSupportedDroModes = null;
        mRawSupportedHdrModes = null;
        mSupportedDroModeBooleanTable = null;
        mSupportedHdrModeBooleanTable = null;
        mSupportedModes = null;
        mSupportedItemIds = null;
        mSupportedDroLevels = null;
        mSupportedHdrLevels = null;
        createSupported();
    }

    public String getValue() {
        String mode = getValueMode();
        if ("off".equals(mode) || MODE_DRO_AUTO.equals(mode) || MODE_HDR_AUTO.equals(mode)) {
            return mode;
        }
        if (MODE_DRO_ON.equals(mode)) {
            String result = getDroLevelString(getValueLevel(mode));
            return result;
        }
        if (MODE_HDR_ON.equals(mode)) {
            String result2 = getHdrEvString(getValueEv(mode));
            return result2;
        }
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_INVALIDMODE).append(mode);
        Log.e(TAG, STRBUILD.toString());
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String itemId) {
        String mode = getValueMode();
        if (MENU_ITEM_ID_DROHDR.equals(itemId)) {
            if (MODE_DRO_ON.equals(mode) || MODE_DRO_AUTO.equals(mode)) {
                return MENU_ITEM_ID_DRO;
            }
            if (MODE_HDR_ON.equals(mode) || MODE_HDR_AUTO.equals(mode)) {
                return MENU_ITEM_ID_HDR;
            }
            if ("off".equals(mode)) {
                return "off";
            }
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_INVALIDMODE).append(mode);
            Log.e(TAG, STRBUILD.toString());
            return null;
        }
        if (MENU_ITEM_ID_DRO.equals(itemId)) {
            int level = getValueLevel(mode);
            if (MODE_DRO_AUTO.equals(mode) || level == 100) {
                return MODE_DRO_AUTO;
            }
            String ret = getDroLevelString(level);
            return ret;
        }
        if (!MENU_ITEM_ID_HDR.equals(itemId)) {
            return null;
        }
        int ev = getValueEv(mode);
        if (MODE_HDR_AUTO.equals(mode) || ev == 100) {
            return MODE_HDR_AUTO;
        }
        String ret2 = getHdrEvString(ev);
        return ret2;
    }

    private String getValueMode() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getParameters();
        return getValueMode(params);
    }

    private String getValueMode(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        String droMode = ((CameraEx.ParametersModifier) params.second).getDROMode();
        String hdrMode = ((CameraEx.ParametersModifier) params.second).getHDRMode();
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_GETDROMODE).append(droMode).append(", ").append(LOG_MSG_GETHDRMODE).append(hdrMode);
        Log.i(TAG, STRBUILD.toString());
        if ("off".equals(droMode) && "off".equals(hdrMode)) {
            return "off";
        }
        if ("on".equals(hdrMode)) {
            return MODE_HDR_ON;
        }
        if ("auto".equals(hdrMode)) {
            return MODE_HDR_AUTO;
        }
        if ("off".equals(hdrMode)) {
            if ("on".equals(droMode)) {
                return MODE_DRO_ON;
            }
            if ("auto".equals(droMode)) {
                return MODE_DRO_AUTO;
            }
        }
        return null;
    }

    private int getValueLevel(String mode) {
        if (MODE_DRO_ON.equals(mode)) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getParameters();
            int level = ((CameraEx.ParametersModifier) params.second).getDROLevel();
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_GETDROLEVEL).append(level);
            Log.i(TAG, STRBUILD.toString());
            return level;
        }
        return BackUpUtil.getInstance().getPreferenceInt(BaseBackUpKey.ID_DROAUTOHDR_DRO_LEVEL, 100);
    }

    private int getValueEv(String mode) {
        if (MODE_HDR_ON.equals(mode)) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getParameters();
            int level = ((CameraEx.ParametersModifier) params.second).getHDRExposureDifferenceLevel();
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_GETHDREV).append(level);
            Log.i(TAG, STRBUILD.toString());
            return level;
        }
        return BackUpUtil.getInstance().getPreferenceInt(BaseBackUpKey.ID_DROAUTOHDR_HDR_EV, 100);
    }

    protected void setDROAuto(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        ((CameraEx.ParametersModifier) params.second).setDROMode("auto");
        ((CameraEx.ParametersModifier) params.second).setHDRMode("off");
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_SETDROMODE).append("auto").append(", ").append(LOG_MSG_SETHDRMODE).append("off");
        Log.i(TAG, STRBUILD.toString());
    }

    protected void setDROOn(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        ((CameraEx.ParametersModifier) params.second).setDROMode("on");
        ((CameraEx.ParametersModifier) params.second).setHDRMode("off");
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_SETDROMODE).append("on").append(", ").append(LOG_MSG_SETHDRMODE).append("off");
        Log.i(TAG, STRBUILD.toString());
    }

    protected void setHDRAuto(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        ((CameraEx.ParametersModifier) params.second).setHDRMode("auto");
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_SETDROMODE).append("off").append(", ").append(LOG_MSG_SETHDRMODE).append("auto");
        Log.i(TAG, STRBUILD.toString());
    }

    protected void setHDROn(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        ((CameraEx.ParametersModifier) params.second).setHDRMode("on");
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_SETDROMODE).append("off").append(", ").append(LOG_MSG_SETHDRMODE).append("on");
        Log.i(TAG, STRBUILD.toString());
    }

    protected void setModeOff(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        ((CameraEx.ParametersModifier) params.second).setDROMode("off");
        ((CameraEx.ParametersModifier) params.second).setHDRMode("off");
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_SETDROMODE).append("off").append(", ").append(LOG_MSG_SETHDRMODE).append("off");
        Log.i(TAG, STRBUILD.toString());
    }

    protected void setDROHDR(Pair<Camera.Parameters, CameraEx.ParametersModifier> params, String drohdr) {
        String level;
        if (MENU_ITEM_ID_DRO.equals(drohdr) || MENU_ITEM_ID_HDR.equals(drohdr)) {
            level = getValue(drohdr);
        } else {
            level = "off";
        }
        if (MENU_ITEM_ID_DRO.equals(drohdr)) {
            if (MODE_DRO_AUTO.equals(level)) {
                setDROAuto(params);
            } else {
                setDROOn(params);
            }
            updateCameraParameterLevel(params, retrieveDroLevel(level));
            return;
        }
        if (MENU_ITEM_ID_HDR.equals(drohdr)) {
            if (MODE_HDR_AUTO.equals(level)) {
                setHDRAuto(params);
            } else {
                setHDROn(params);
            }
            updateCameraParameterEv(params, retrieveHdrEv(level));
            return;
        }
        setModeOff(params);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getEmptyParameters();
        if (MENU_ITEM_ID_DROHDR.equals(itemId)) {
            setDROHDR(params, value);
        } else {
            setOptionValue(params, itemId, value);
        }
        CameraSetting.getInstance().setParameters(params);
    }

    protected void setOptionValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> params, String itemId, String level) {
        if (MENU_ITEM_ID_DRO.equals(itemId)) {
            updateCameraParameterLevel(params, retrieveDroLevel(level));
        } else if (MENU_ITEM_ID_HDR.equals(itemId)) {
            updateCameraParameterEv(params, retrieveHdrEv(level));
        }
    }

    private void updateCameraParameterLevel(Pair<Camera.Parameters, CameraEx.ParametersModifier> params, int value) {
        String mode = getValueMode(params);
        if (mode == null) {
            mode = getValueMode();
        }
        if (100 == value) {
            if (MODE_DRO_ON.equals(mode)) {
                setDROAuto(params);
            }
        } else {
            if (MODE_DRO_AUTO.equals(mode)) {
                setDROOn(params);
            }
            ((CameraEx.ParametersModifier) params.second).setDROLevel(value);
        }
        BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_DROAUTOHDR_DRO_LEVEL, Integer.valueOf(value));
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_SETDROLEVEL).append(value);
        Log.i(TAG, STRBUILD.toString());
    }

    private void updateCameraParameterEv(Pair<Camera.Parameters, CameraEx.ParametersModifier> params, int value) {
        String mode = getValueMode(params);
        if (mode == null) {
            mode = getValueMode();
        }
        if (100 == value) {
            if (MODE_HDR_ON.equals(mode)) {
                setHDRAuto(params);
            }
        } else {
            if (MODE_HDR_AUTO.equals(mode)) {
                setHDROn(params);
            }
            ((CameraEx.ParametersModifier) params.second).setHDRExposureDifferenceLevel(value);
        }
        BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_DROAUTOHDR_HDR_EV, Integer.valueOf(value));
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_SETHDREV).append(value);
        Log.i(TAG, STRBUILD.toString());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String itemId) {
        if (MENU_ITEM_ID_DROHDR.equals(itemId)) {
            return mSupportedItemIds;
        }
        if (MENU_ITEM_ID_DRO.equals(itemId)) {
            return mSupportedDroLevels;
        }
        if (MENU_ITEM_ID_HDR.equals(itemId)) {
            return mSupportedHdrLevels;
        }
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String itemId) {
        if (MENU_ITEM_ID_DROHDR.equals(itemId)) {
            return getAvailableitemIds();
        }
        if (MENU_ITEM_ID_DRO.equals(itemId)) {
            return getAvailableDROLevels();
        }
        if (MENU_ITEM_ID_HDR.equals(itemId)) {
            return getAvailableHDRExposureDifferenceLevels();
        }
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_ILLEGALARG).append(LOG_MSG_ITEMID).append(itemId);
        Log.e(TAG, STRBUILD.toString());
        return null;
    }

    private List<String> getAvailableModes() {
        boolean[] isDroAvailables = getDroAvailableBooleanTable();
        boolean[] isHdrAvailables = getHdrAvailableBooleanTable();
        return getValidModes(isDroAvailables, isHdrAvailables);
    }

    private static List<String> createSupportedItemIds() {
        return getValidItemIds(mSupportedDroModeBooleanTable, mSupportedHdrModeBooleanTable);
    }

    private List<String> getAvailableitemIds() {
        boolean[] isDroAvailables = getDroAvailableBooleanTable();
        boolean[] isHdrAvailables = getHdrAvailableBooleanTable();
        return getValidItemIds(isDroAvailables, isHdrAvailables);
    }

    private static boolean[] createDroSupportedBooleanTable() {
        boolean[] isDroSupporteds = new boolean[DRO_MODE_PARAMS.length];
        for (int i = 0; i < DRO_MODE_PARAMS.length; i++) {
            isDroSupporteds[i] = mRawSupportedDroModes != null && mRawSupportedDroModes.contains(DRO_MODE_PARAMS[i]);
        }
        return isDroSupporteds;
    }

    private static boolean[] createHdrSupportedBooleanTable() {
        boolean[] isHdrSupporteds = new boolean[HDR_MODE_PARAMS.length];
        for (int i = 0; i < HDR_MODE_PARAMS.length; i++) {
            isHdrSupporteds[i] = mRawSupportedHdrModes != null && mRawSupportedHdrModes.contains(HDR_MODE_PARAMS[i]);
        }
        return isHdrSupporteds;
    }

    private boolean[] getDroAvailableBooleanTable() {
        boolean isApiAvailable;
        boolean[] isDroAvailables = new boolean[DRO_MODE_PARAMS.length];
        for (int i = 0; i < DRO_MODE_PARAMS.length; i++) {
            if ("on".equals(DRO_MODE_PARAMS[i])) {
                isApiAvailable = AvailableInfo.isAvailable(API_NAME_DRO_MODE, null, API_NAME_DRO_LEVEL, null);
            } else {
                isApiAvailable = AvailableInfo.isAvailable(API_NAME_DRO_MODE, DRO_MODE_PARAMS[i]);
            }
            isDroAvailables[i] = mRawSupportedDroModes != null && mRawSupportedDroModes.contains(DRO_MODE_PARAMS[i]) && isApiAvailable;
        }
        return isDroAvailables;
    }

    private boolean[] getHdrAvailableBooleanTable() {
        boolean isApiAvailable;
        boolean[] isHdrAvailables = new boolean[HDR_MODE_PARAMS.length];
        for (int i = 0; i < HDR_MODE_PARAMS.length; i++) {
            if ("on".equals(HDR_MODE_PARAMS[i])) {
                isApiAvailable = AvailableInfo.isAvailable(API_NAME_HDR_MODE, null, API_NAME_HDR_EV, null);
            } else {
                isApiAvailable = AvailableInfo.isAvailable(API_NAME_HDR_MODE, HDR_MODE_PARAMS[i]);
            }
            isHdrAvailables[i] = mRawSupportedHdrModes != null && mRawSupportedHdrModes.contains(HDR_MODE_PARAMS[i]) && isApiAvailable;
        }
        return isHdrAvailables;
    }

    private static List<String> getValidModes(boolean[] drdModes, boolean[] hdrModes) {
        List<String> modes = new ArrayList<>();
        if (drdModes[0] && hdrModes[0]) {
            modes.add("off");
        }
        if (drdModes[1] && hdrModes[0]) {
            modes.add(MODE_DRO_ON);
        }
        if (drdModes[0] && hdrModes[1]) {
            modes.add(MODE_HDR_ON);
        }
        if (drdModes[2] && hdrModes[0]) {
            modes.add(MODE_DRO_AUTO);
        }
        if (drdModes[0] && hdrModes[2]) {
            modes.add(MODE_HDR_AUTO);
        }
        if (modes.isEmpty()) {
            return null;
        }
        return modes;
    }

    private static List<String> getValidItemIds(boolean[] drdModes, boolean[] hdrModes) {
        List<String> modes = new ArrayList<>();
        if (drdModes[0] && hdrModes[0]) {
            modes.add("off");
        }
        if ((drdModes[1] && hdrModes[0]) || (drdModes[2] && hdrModes[0])) {
            modes.add(MENU_ITEM_ID_DRO);
        }
        if ((drdModes[0] && hdrModes[1]) || (drdModes[0] && hdrModes[2])) {
            modes.add(MENU_ITEM_ID_HDR);
        }
        if (modes.isEmpty()) {
            return null;
        }
        return modes;
    }

    private static List<String> createSupportedDROLevels() {
        List<String> levels = getValidLevels(1, mRawMaxDROLevel, DRO_LEVEL_STRING_POOL, DRO_LEVEL_SUFFIX, null);
        if (mSupportedModes != null && mSupportedModes.contains(MODE_DRO_AUTO)) {
            levels.add(0, MODE_DRO_AUTO);
        }
        if (levels.isEmpty()) {
            return null;
        }
        return levels;
    }

    private List<String> getAvailableDROLevels() {
        List<String> levels = getValidLevels(1, mRawMaxDROLevel, DRO_LEVEL_STRING_POOL, DRO_LEVEL_SUFFIX, API_NAME_DRO_LEVEL);
        if (getAvailableModes().contains(MODE_DRO_AUTO)) {
            levels.add(0, MODE_DRO_AUTO);
        }
        if (levels.isEmpty()) {
            return null;
        }
        return levels;
    }

    private static List<String> createSupportedHDRExposureDifferenceLevels() {
        List<String> levels = getValidLevels(mRawMinHDRExposureDifferenceLevel, mRawMaxHDRExposureDifferenceLevel, HDR_EV_STRING_POOL, HDR_EV_SUFFIX, null);
        if (mSupportedModes != null && mSupportedModes.contains(MODE_HDR_AUTO)) {
            levels.add(0, MODE_HDR_AUTO);
        }
        if (levels.isEmpty()) {
            return null;
        }
        return levels;
    }

    private List<String> getAvailableHDRExposureDifferenceLevels() {
        List<String> levels = getValidLevels(mRawMinHDRExposureDifferenceLevel, mRawMaxHDRExposureDifferenceLevel, HDR_EV_STRING_POOL, HDR_EV_SUFFIX, API_NAME_HDR_EV);
        if (getAvailableModes().contains(MODE_HDR_AUTO)) {
            levels.add(0, MODE_HDR_AUTO);
        }
        if (levels.isEmpty()) {
            return null;
        }
        return levels;
    }

    private static List<String> getValidLevels(int min, int max, SparseArray<String> pool, String suffix, String api) {
        List<String> levels = new ArrayList<>();
        if (min != 0 && max != 0) {
            Pair<String, String> apiSet = MODE_API_SETS.get(api);
            for (int i = min; i <= max; i++) {
                if (api == null || api.isEmpty() || AvailableInfo.isAvailable(apiSet.first, apiSet.second, api, Integer.toString(i))) {
                    levels.add(getStringFromPool(pool, suffix, i));
                }
            }
        }
        return levels;
    }

    private static String getDroLevelString(int level) {
        return getStringFromPool(DRO_LEVEL_STRING_POOL, DRO_LEVEL_SUFFIX, level);
    }

    private static String getHdrEvString(int ev) {
        return getStringFromPool(HDR_EV_STRING_POOL, HDR_EV_SUFFIX, ev);
    }

    private static String getStringFromPool(SparseArray<String> pool, String suffix, int key) {
        String text = pool.get(key);
        if (text == null) {
            String text2 = suffix + key;
            pool.append(key, text2);
            return text2;
        }
        return text;
    }

    private static int retrieveDroLevel(String droText) {
        if (MODE_DRO_AUTO.equals(droText)) {
            return 100;
        }
        String levelText = droText.substring(DRO_LEVEL_SUFFIX.length());
        int ret = Integer.parseInt(levelText);
        return ret;
    }

    private static int retrieveHdrEv(String hdrText) {
        if (MODE_HDR_AUTO.equals(hdrText)) {
            return 100;
        }
        String evText = hdrText.substring(HDR_EV_SUFFIX.length());
        int ret = Integer.parseInt(evText);
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        if (MENU_ITEM_ID_DROHDR.equals(tag)) {
            boolean dro = AvailableInfo.isAvailable(API_NAME_DRO_MODE, null);
            boolean drolevel = AvailableInfo.isAvailable(API_NAME_DRO_MODE, null, API_NAME_DRO_LEVEL, null);
            boolean hdr = AvailableInfo.isAvailable(API_NAME_HDR_MODE, null);
            boolean hdrlevel = AvailableInfo.isAvailable(API_NAME_HDR_MODE, null, API_NAME_HDR_EV, null);
            return dro || drolevel || hdr || hdrlevel;
        }
        if (MENU_ITEM_ID_DRO.equals(tag)) {
            boolean dro2 = AvailableInfo.isAvailable(API_NAME_DRO_MODE, null);
            boolean drolevel2 = AvailableInfo.isAvailable(API_NAME_DRO_MODE, null, API_NAME_DRO_LEVEL, null);
            return dro2 || drolevel2;
        }
        if (!MENU_ITEM_ID_HDR.equals(tag)) {
            return false;
        }
        boolean hdr2 = AvailableInfo.isAvailable(API_NAME_HDR_MODE, null);
        boolean hdrlevel2 = AvailableInfo.isAvailable(API_NAME_HDR_MODE, null, API_NAME_HDR_EV, null);
        return hdr2 || hdrlevel2;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public boolean isUnavailableSceneFactor(String tag) {
        if (MENU_ITEM_ID_DROHDR.equals(tag)) {
            boolean dro = isUnavailableAPISceneFactor(API_NAME_DRO_MODE, null);
            boolean drolevel = isUnavailableAPISceneFactor(API_NAME_DRO_MODE, null, API_NAME_DRO_LEVEL, null);
            boolean hdr = isUnavailableAPISceneFactor(API_NAME_HDR_MODE, null);
            boolean hdrlevel = isUnavailableAPISceneFactor(API_NAME_HDR_MODE, null, API_NAME_HDR_EV, null);
            return dro && drolevel && hdr && hdrlevel;
        }
        if (MENU_ITEM_ID_DRO.equals(tag)) {
            boolean dro2 = isUnavailableAPISceneFactor(API_NAME_DRO_MODE, null);
            boolean drolevel2 = isUnavailableAPISceneFactor(API_NAME_DRO_MODE, null, API_NAME_DRO_LEVEL, null);
            return dro2 && drolevel2;
        }
        if (MENU_ITEM_ID_HDR.equals(tag)) {
            boolean hdr2 = isUnavailableAPISceneFactor(API_NAME_HDR_MODE, null);
            boolean hdrlevel2 = isUnavailableAPISceneFactor(API_NAME_HDR_MODE, null, API_NAME_HDR_EV, null);
            return hdr2 && hdrlevel2;
        }
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_ILLEGALARG).append(LOG_MSG_TAG).append(tag);
        Log.e(TAG, STRBUILD.toString());
        return false;
    }
}
