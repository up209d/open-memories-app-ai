package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.BaseBackUpKey;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ISOAutoMinSSController extends AbstractController {
    private static final String API_NAME = "setIsoAutoMinShutterSpeedMode";
    private static final String LOG_MSG_GETISOAUTOMINSS = "getISOAutoMinSS = ";
    private static final String LOG_MSG_GETSUPPORTEDISOAUTOMINSS = "getSupportedISOAutoMinSS = ";
    private static final String LOG_MSG_ILLEGALVALUE = "Illegal value is set. ";
    private static final String LOG_MSG_SETISOAUTOMINSS = "setISOAutoMinSS = ";
    public static final String MENU_ITEM_ID_ISO_AUTO_MIN_SS = "isoautominss";
    public static final String MENU_ITEM_ID_ISO_AUTO_MIN_SS_MODE = "isoautominss-auto-mode";
    public static final String MENU_ITEM_ID_ISO_AUTO_MIN_SS_VALUE = "isoautominss-manual-value";
    protected static final String S_FAST = "fast1";
    protected static final String S_FASTER = "fast2";
    protected static final String S_MANUAL = "manual";
    protected static final String S_SLOW = "slow1";
    protected static final String S_SLOWER = "slow2";
    protected static final String S_STD = "auto";
    private static final String TAG = "ISOAutoMinSSController";
    private static ISOAutoMinSSController mInstance;
    private CameraSetting mCameraSetting;
    private List<String> mSupportedISOAutoMinSSModes;
    private List<String> mSupportedISOAutoMinSSValues;
    private List<String> mSupportedISOAutoMinSSs;
    private static final StringBuilderThreadLocal sStringBuilder = new StringBuilderThreadLocal();
    private static final String myName = ISOAutoMinSSController.class.getSimpleName();

    public static final String getName() {
        return myName;
    }

    public static ISOAutoMinSSController getInstance() {
        if (mInstance == null) {
            new ISOAutoMinSSController();
        }
        return mInstance;
    }

    private static void setController(ISOAutoMinSSController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected ISOAutoMinSSController() {
        this.mCameraSetting = null;
        this.mCameraSetting = CameraSetting.getInstance();
        setController(this);
    }

    private static List<String> createSupportedISOAutoMinSSValues(Pair<Camera.Parameters, CameraEx.ParametersModifier> supportedParams) {
        List<Pair<Integer, Integer>> values = ((CameraEx.ParametersModifier) supportedParams.second).getSupportedIsoAutoMinShutterSpeedValues();
        if (values == null) {
            return null;
        }
        ArrayList<String> result = new ArrayList<>();
        for (Pair<Integer, Integer> value : values) {
            if (((Integer) value.first).intValue() == 16 && ((Integer) value.second).intValue() == 1) {
                value = Pair.create(15, 1);
            }
            result.add(value.first + "/" + value.second);
        }
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_GETSUPPORTEDISOAUTOMINSS).append(result);
        Log.i(TAG, strBuilder.toString());
        return result;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        if (value != null) {
            try {
                if (MENU_ITEM_ID_ISO_AUTO_MIN_SS.equals(itemId)) {
                    if (S_FASTER.equals(value) || S_FAST.equals(value) || "auto".equals(value) || S_SLOW.equals(value) || S_SLOWER.equals(value)) {
                        setISOAutoMinSSMode(value);
                        BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_ISOAUTOMINSS_MODE, value);
                    } else if (MENU_ITEM_ID_ISO_AUTO_MIN_SS_MODE.equals(value)) {
                        setISOAutoMinSSMode(BackUpUtil.getInstance().getPreferenceString(BaseBackUpKey.ID_ISOAUTOMINSS_MODE, ""));
                    } else {
                        setISOAutoMinSSValue(value);
                    }
                } else if (MENU_ITEM_ID_ISO_AUTO_MIN_SS_VALUE.equals(itemId)) {
                    setISOAutoMinSSValue(value);
                } else if (MENU_ITEM_ID_ISO_AUTO_MIN_SS_MODE.equals(itemId) && (S_FASTER.equals(value) || S_FAST.equals(value) || "auto".equals(value) || S_SLOW.equals(value) || S_SLOWER.equals(value))) {
                    setISOAutoMinSSMode(value);
                    BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_ISOAUTOMINSS_MODE, value);
                }
                StringBuilder strBuilder = sStringBuilder.get();
                strBuilder.replace(0, strBuilder.length(), LOG_MSG_SETISOAUTOMINSS).append(value);
                Log.i(TAG, strBuilder.toString());
            } catch (NumberFormatException e) {
                StringBuilder strBuilder2 = sStringBuilder.get();
                strBuilder2.replace(0, strBuilder2.length(), LOG_MSG_ILLEGALVALUE).append(value);
                Log.e(TAG, strBuilder2.toString());
            }
        }
    }

    private void setISOAutoMinSSMode(String value) {
        if (Environment.isISOAutoMinShutterSpeedAPISupported()) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getEmptyParameters();
            ((CameraEx.ParametersModifier) params.second).setIsoAutoMinShutterSpeedMode(value);
            this.mCameraSetting.setParameters(params);
            StringBuilder strBuilder = sStringBuilder.get();
            strBuilder.replace(0, strBuilder.length(), LOG_MSG_SETISOAUTOMINSS).append(value);
            Log.i(TAG, strBuilder.toString());
        }
    }

    private void setISOAutoMinSSValue(String value) {
        if (Environment.isISOAutoMinShutterSpeedAPISupported()) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getEmptyParameters();
            ((CameraEx.ParametersModifier) params.second).setIsoAutoMinShutterSpeedMode("manual");
            String[] data = value.split("/");
            ((CameraEx.ParametersModifier) params.second).setIsoAutoMinShutterSpeedValue(Pair.create(Integer.valueOf(Integer.parseInt(data[0])), Integer.valueOf(Integer.parseInt(data[1]))));
            this.mCameraSetting.setParameters(params);
            StringBuilder strBuilder = sStringBuilder.get();
            strBuilder.replace(0, strBuilder.length(), LOG_MSG_SETISOAUTOMINSS).append(value);
            Log.i(TAG, strBuilder.toString());
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String itemId) {
        String value = null;
        if (MENU_ITEM_ID_ISO_AUTO_MIN_SS.equals(itemId)) {
            String mode = getISOAutoMinSSMode();
            if (mode != null) {
                value = !"manual".equals(mode) ? MENU_ITEM_ID_ISO_AUTO_MIN_SS_MODE : getISOAutoMinSSValue();
            }
        } else if (MENU_ITEM_ID_ISO_AUTO_MIN_SS_VALUE.equals(itemId)) {
            value = getISOAutoMinSSValue();
        } else if (MENU_ITEM_ID_ISO_AUTO_MIN_SS_MODE.equals(itemId)) {
            value = getISOAutoMinSSMode();
            if ("manual".equals(value)) {
                value = BackUpUtil.getInstance().getPreferenceString(BaseBackUpKey.ID_ISOAUTOMINSS_MODE, "");
            }
        }
        if (value.isEmpty()) {
            value = null;
        }
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_GETISOAUTOMINSS).append(value);
        Log.i(TAG, strBuilder.toString());
        return value;
    }

    private String getISOAutoMinSSValue() {
        if (!Environment.isISOAutoMinShutterSpeedAPISupported()) {
            return null;
        }
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getParameters();
        Pair<Integer, Integer> valuep = ((CameraEx.ParametersModifier) params.second).getIsoAutoMinShutterSpeedValue();
        if (valuep == null) {
            return null;
        }
        String value = valuep.first + "/" + valuep.second;
        return value;
    }

    private String getISOAutoMinSSMode() {
        if (!Environment.isISOAutoMinShutterSpeedAPISupported()) {
            return null;
        }
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getParameters();
        String value = ((CameraEx.ParametersModifier) params.second).getIsoAutoMinShutterSpeedMode();
        return value;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String itemId) {
        if (MENU_ITEM_ID_ISO_AUTO_MIN_SS.equals(itemId)) {
            return this.mSupportedISOAutoMinSSs;
        }
        if (MENU_ITEM_ID_ISO_AUTO_MIN_SS_VALUE.equals(itemId)) {
            return this.mSupportedISOAutoMinSSValues;
        }
        if (MENU_ITEM_ID_ISO_AUTO_MIN_SS_MODE.equals(itemId)) {
            return this.mSupportedISOAutoMinSSModes;
        }
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String itemId) {
        ArrayList<String> availables = new ArrayList<>();
        if (MENU_ITEM_ID_ISO_AUTO_MIN_SS.equals(itemId)) {
            List<String> supportedValue = getSupportedValue(itemId);
            if (supportedValue == null) {
                return null;
            }
            for (String value : supportedValue) {
                if (MENU_ITEM_ID_ISO_AUTO_MIN_SS_MODE.equals(value)) {
                    List<String> availableValue = getAvailableValue(MENU_ITEM_ID_ISO_AUTO_MIN_SS_MODE);
                    if (availableValue != null && (availableValue.size() > 1 || !availableValue.get(0).equals("manual"))) {
                        availables.add(value);
                    }
                } else if (AvailableInfo.isAvailable(API_NAME, "manual")) {
                    availables.add(value);
                }
            }
        } else if (MENU_ITEM_ID_ISO_AUTO_MIN_SS_VALUE.equals(itemId)) {
            List<String> supportedValue2 = getSupportedValue(itemId);
            if (supportedValue2 == null) {
                return null;
            }
            for (String value2 : supportedValue2) {
                if (AvailableInfo.isAvailable(API_NAME, "manual")) {
                    availables.add(value2);
                }
            }
        } else if (MENU_ITEM_ID_ISO_AUTO_MIN_SS_MODE.equals(itemId)) {
            List<String> supportedValue3 = getSupportedValue(itemId);
            if (supportedValue3 == null) {
                return null;
            }
            for (String value3 : supportedValue3) {
                if (AvailableInfo.isAvailable(API_NAME, value3)) {
                    availables.add(value3);
                }
            }
        }
        if (availables.isEmpty()) {
            availables = null;
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

    private boolean isSupportedMode(String value) {
        if (this.mSupportedISOAutoMinSSModes == null || !this.mSupportedISOAutoMinSSModes.contains(value)) {
            return false;
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        Log.i(TAG, "onGetInitParameters");
        if (Environment.isISOAutoMinShutterSpeedAPISupported()) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> supportedParams = this.mCameraSetting.getSupportedParameters();
            this.mSupportedISOAutoMinSSModes = ((CameraEx.ParametersModifier) supportedParams.second).getSupportedIsoAutoMinShutterSpeedModes();
            this.mSupportedISOAutoMinSSValues = createSupportedISOAutoMinSSValues(supportedParams);
            this.mSupportedISOAutoMinSSs = createSupportedISOAutoMinSSs();
            String BackUpMode = BackUpUtil.getInstance().getPreferenceString(BaseBackUpKey.ID_ISOAUTOMINSS_MODE, "");
            if (BackUpMode.isEmpty()) {
                String mode = ((CameraEx.ParametersModifier) this.mCameraSetting.getParameters().second).getIsoAutoMinShutterSpeedMode();
                if (mode != null && !"manual".equals(mode)) {
                    BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_ISOAUTOMINSS_MODE, mode);
                    return;
                }
                if (isSupportedMode("auto")) {
                    BackUpMode = "auto";
                } else if (isSupportedMode(S_FAST)) {
                    BackUpMode = S_FAST;
                } else if (isSupportedMode(S_SLOW)) {
                    BackUpMode = S_SLOW;
                } else if (isSupportedMode(S_FASTER)) {
                    BackUpMode = S_FASTER;
                } else if (isSupportedMode(S_SLOWER)) {
                    BackUpMode = S_SLOWER;
                }
                BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_ISOAUTOMINSS_MODE, BackUpMode);
            }
        }
    }

    private List<String> createSupportedISOAutoMinSSs() {
        if (this.mSupportedISOAutoMinSSModes == null && this.mSupportedISOAutoMinSSValues == null) {
            return null;
        }
        List<String> result = new ArrayList<>(this.mSupportedISOAutoMinSSValues.size() + 1);
        if ((this.mSupportedISOAutoMinSSModes != null && this.mSupportedISOAutoMinSSModes.size() <= 1) || !this.mSupportedISOAutoMinSSModes.get(0).equals("manual")) {
            result.add(MENU_ITEM_ID_ISO_AUTO_MIN_SS_MODE);
        }
        if (this.mSupportedISOAutoMinSSValues != null) {
            result.addAll(this.mSupportedISOAutoMinSSValues);
            return result;
        }
        return result;
    }
}
