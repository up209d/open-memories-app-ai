package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class WhiteBalanceController extends AbstractController {
    public static final String AUTO = "auto";
    public static final String BUNDLE_RESET_ITEMID = "resetItemId";
    public static final String CLOUDY = "cloudy-daylight";
    public static final String COLOR_TEMP = "color-temp";
    public static final String COMP = "Color-Compensation";
    public static final String CUSTOM = "custom";
    public static final String CUSTOM1 = "custom1";
    public static final String CUSTOM2 = "custom2";
    public static final String CUSTOM3 = "custom3";
    public static final String CUSTOM_SET = "custom-set";
    public static final String DAYLIGHT = "daylight";
    public static final int DEF_COMP = 0;
    public static final int DEF_LIGHT = 0;
    public static final int DEF_TEMP = 5500;
    public static final String DISP_TEXT_AB_MINUS = "A-B: B";
    public static final String DISP_TEXT_AB_PLUS = "A-B: A";
    public static final String DISP_TEXT_AB_ZERO = "A-B: ";
    public static final String DISP_TEXT_GM_MINUS = "G-M: G";
    public static final String DISP_TEXT_GM_PLUS = "G-M: M";
    public static final String DISP_TEXT_GM_ZERO = "G-M: ";
    public static final String DISP_TEXT_K = "K";
    public static final String FLASH = "flash";
    public static final String FLUORESCENT = "fluorescent";
    public static final String FLUORESCENT_COOLWHITE = "fluorescent-coolwhite";
    public static final String FLUORESCENT_DAYLIGHT = "fluorescent-daylight";
    public static final String FLUORESCENT_DAYWHITE = "fluorescent-daywhite";
    public static final String INCANDESCENT = "incandescent";
    public static final String LIGHT = "Light-Balance";
    private static final String LOG_COMP = " Compensation :";
    private static final String LOG_ERROR_BACKUP = "backup error. invalid backup key";
    private static final String LOG_ERROR_CATEGOLY_IS_NULL = "getSupportedValue error. categoly is null";
    private static final String LOG_ERROR_NO_VALUE = "error. don't have a some value.";
    private static final String LOG_ERROR_SETDETAILVALUE = "setDetailValue is fail";
    private static final String LOG_ERROR_SETVALUE = "setValue error. must call another API on the third layer";
    private static final String LOG_ERROR_UNKNOWN_CATEGOLY = "getSupportedValue error. unknown categoly";
    private static final String LOG_GETNUMOFCUSTOM = "getNumOfCustomWhiteBalance = ";
    private static final String LOG_INVALID_TAG = ": Invalid tag. ";
    private static final String LOG_LIGHT = " Light :";
    private static final String LOG_NUMOFCUSTOM_IS_0 = "getNumOfCustomWhiteBalance returns 0.";
    private static final String LOG_TEMP = " Temperature :";
    private static final String LOG_WHITEBALANCE_OPTION = "Whitebalance option ";
    public static final String SETTING_COMPENSATION = "color-compensation";
    public static final String SETTING_LIGHTBALANCE = "light-balance";
    public static final String SETTING_TEMPERATURE = "color-temperature";
    public static final String SHADE = "shade";
    private static final String TAG = "WhiteBalanceController";
    public static final String TEMP = "Color-Temperature";
    public static final int TEMPERATURE_STEP = 100;
    public static final String TWILIGHT = "twilight";
    public static final String UNDERWATER_AUTO = "underwater-auto";
    public static final String UNDERWATER_BLUE = "underwater-blue";
    public static final String UNDERWATER_GREEN = "underwater-green";
    public static final String WARM_FLUORESCENT = "warm-fluorescent";
    public static final String WHITEBALANCE = "WhiteBalance";
    public static final String WHITEBALANCEFN = "WhiteBalanceFn";
    private static WhiteBalanceController mInstance;
    private static StringBuilder builder = new StringBuilder();
    private static final String myName = WhiteBalanceController.class.getSimpleName();
    private final String API_NAME_WB = "setWhiteBalance";
    private final String API_NAME_LB = "setLightBalanceForWhiteBalance";
    private final String API_NAME_CC = "setColorCompensationForWhiteBalance";
    private final String API_NAME_CT = "setColorTemperatureForWhiteBalance";
    private final String API_NAME_CWB = "setCustomWhiteBalance";
    String[] wbMode = {"auto", DAYLIGHT, SHADE, CLOUDY, INCANDESCENT, FLUORESCENT_DAYLIGHT, WARM_FLUORESCENT, FLUORESCENT_DAYWHITE, FLUORESCENT_COOLWHITE, FLASH, COLOR_TEMP, CUSTOM};
    private NotificationListener mListener = new ExposureModeChangedListener();
    private CameraSetting mCamSet = CameraSetting.getInstance();
    private BackUpUtil mBackUp = BackUpUtil.getInstance();

    /* loaded from: classes.dex */
    public static class WhiteBalanceParam {
        static final /* synthetic */ boolean $assertionsDisabled;
        int colorComp;
        int colorTemp;
        int lightBalance;

        static {
            $assertionsDisabled = !WhiteBalanceController.class.desiredAssertionStatus();
        }

        public WhiteBalanceParam() {
            this.lightBalance = 0;
            this.colorComp = 0;
            this.colorTemp = WhiteBalanceController.DEF_TEMP;
        }

        public WhiteBalanceParam(int light, int comp, int temp) {
            this.lightBalance = 0;
            this.colorComp = 0;
            this.colorTemp = WhiteBalanceController.DEF_TEMP;
            this.lightBalance = light;
            this.colorComp = comp;
            this.colorTemp = temp;
        }

        public WhiteBalanceParam(WhiteBalanceParam param) {
            this.lightBalance = 0;
            this.colorComp = 0;
            this.colorTemp = WhiteBalanceController.DEF_TEMP;
            this.lightBalance = param.lightBalance;
            this.colorComp = param.colorComp;
            this.colorTemp = param.colorTemp;
        }

        public void setLightBalance(int value) {
            this.lightBalance = value;
        }

        public int getLightBalance() {
            return this.lightBalance;
        }

        public void setColorComp(int value) {
            this.colorComp = value;
        }

        public int getColorComp() {
            return this.colorComp;
        }

        public void setColorTemp(int value) {
            this.colorTemp = value;
        }

        public int getColorTemp() {
            return this.colorTemp;
        }

        public boolean equals(Object o) {
            if (o == null || !(o instanceof WhiteBalanceParam)) {
                return false;
            }
            WhiteBalanceParam param = (WhiteBalanceParam) o;
            return this.lightBalance == param.lightBalance && this.colorComp == param.colorComp && this.colorTemp == param.colorTemp;
        }

        public int hashCode() {
            if ($assertionsDisabled) {
                return -1;
            }
            throw new AssertionError("hashCodeが呼び出されることは想定されていません。");
        }
    }

    public static final String getName() {
        return myName;
    }

    public static WhiteBalanceController getInstance() {
        if (mInstance == null) {
            new WhiteBalanceController();
        }
        return mInstance;
    }

    private static void setController(WhiteBalanceController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    public WhiteBalanceController() {
        setController(this);
    }

    public void setCustomWhiteBalance(int customNo) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
        ((CameraEx.ParametersModifier) p.second).setCustomWhiteBalance(customNo);
        this.mCamSet.setParameters(p);
    }

    public void saveCustomWhiteBalance(String itemId) {
        int customNo = getCustomWhiteBalanceNum(itemId);
        this.mCamSet.saveCustomWhiteBalance(customNo);
    }

    public int getCustomWhiteBalanceNum(String itemId) {
        if (itemId.equals(CUSTOM) || itemId.equals("custom1")) {
            return 1;
        }
        if (itemId.equals("custom2")) {
            return 2;
        }
        if (!itemId.equals("custom3")) {
            return 1;
        }
        return 3;
    }

    public void setCustomWhiteBalance(String itemId) {
        int customNo = getCustomWhiteBalanceNum(itemId);
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
        ((CameraEx.ParametersModifier) p.second).setCustomWhiteBalance(customNo);
        this.mCamSet.setParameters(p);
    }

    public List<String> getSupportedCustomWhiteBalance() {
        ArrayList<String> supportedlist = new ArrayList<>();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getSupportedParameters();
        int num = ((CameraEx.ParametersModifier) p.second).getNumOfCustomWhiteBalance();
        if (num == 1) {
            supportedlist.add(CUSTOM);
        } else if (1 < num && num <= 3) {
            supportedlist.add("custom1");
            supportedlist.add("custom2");
            supportedlist.add("custom3");
        }
        return supportedlist;
    }

    public int getMaxColorTemoerature() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getSupportedParameters();
        return ((CameraEx.ParametersModifier) p.second).getMaxColorTemperatureForWhiteBalance();
    }

    public int getMinColorTemoerature() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getSupportedParameters();
        return ((CameraEx.ParametersModifier) p.second).getMinColorTemperatureForWhiteBalance();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
        List<String> supportcustomlist = getSupportedCustomWhiteBalance();
        int listsize = supportcustomlist.size();
        if (itemId != null) {
            if (itemId.equals(WHITEBALANCE)) {
                if (value.equals(CUSTOM_SET)) {
                    ((Camera.Parameters) p.first).setWhiteBalance(CUSTOM);
                    ((CameraEx.ParametersModifier) p.second).setCustomWhiteBalance(1);
                } else if (value.equals(CUSTOM)) {
                    if (1 == listsize) {
                        ((Camera.Parameters) p.first).setWhiteBalance(value);
                        ((CameraEx.ParametersModifier) p.second).setCustomWhiteBalance(1);
                    } else {
                        Log.e(TAG, LOG_NUMOFCUSTOM_IS_0);
                    }
                } else if (value.equals("custom1")) {
                    if (listsize != 0) {
                        ((Camera.Parameters) p.first).setWhiteBalance(CUSTOM);
                        ((CameraEx.ParametersModifier) p.second).setCustomWhiteBalance(1);
                    } else {
                        Log.e(TAG, LOG_NUMOFCUSTOM_IS_0);
                    }
                } else if (value.equals("custom2")) {
                    if (listsize != 0) {
                        ((Camera.Parameters) p.first).setWhiteBalance(CUSTOM);
                        ((CameraEx.ParametersModifier) p.second).setCustomWhiteBalance(2);
                    } else {
                        Log.e(TAG, LOG_NUMOFCUSTOM_IS_0);
                    }
                } else if (value.equals("custom3")) {
                    if (listsize != 0) {
                        ((Camera.Parameters) p.first).setWhiteBalance(CUSTOM);
                        ((CameraEx.ParametersModifier) p.second).setCustomWhiteBalance(3);
                    } else {
                        Log.e(TAG, LOG_NUMOFCUSTOM_IS_0);
                    }
                } else {
                    ((Camera.Parameters) p.first).setWhiteBalance(value);
                }
                if (!ExecutorCreator.getInstance().isAssistApp()) {
                    WhiteBalanceParam option = getWBOptionFromBackUp(value);
                    setWBOption(p, option);
                }
            } else {
                logcat(LOG_ERROR_SETVALUE);
            }
        }
        this.mCamSet.setParameters(p);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public void setDetailValue(Object obj) {
        if (obj == null) {
            logcat(LOG_ERROR_SETDETAILVALUE);
            return;
        }
        WhiteBalanceParam option = (WhiteBalanceParam) obj;
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
        setWBOption(p, option);
        setWBOptionToBackUp(getValue(), option);
        this.mCamSet.setParameters(p);
    }

    private void setWBOption(Pair<Camera.Parameters, CameraEx.ParametersModifier> p, WhiteBalanceParam option) {
        if (getSupportedValue(SETTING_LIGHTBALANCE).contains(String.valueOf(option.lightBalance))) {
            ((CameraEx.ParametersModifier) p.second).setLightBalanceForWhiteBalance(option.lightBalance);
        }
        if (getSupportedValue(SETTING_COMPENSATION).contains(String.valueOf(option.colorComp))) {
            ((CameraEx.ParametersModifier) p.second).setColorCompensationForWhiteBalance(option.colorComp);
        }
        String mode = ((Camera.Parameters) p.first).getWhiteBalance();
        if (mode == null) {
            mode = ((Camera.Parameters) this.mCamSet.getParameters().first).getWhiteBalance();
        }
        if (COLOR_TEMP.equals(mode) && getSupportedValue(SETTING_TEMPERATURE).contains(String.valueOf(option.colorTemp))) {
            ((CameraEx.ParametersModifier) p.second).setColorTemperatureForWhiteBalance(option.colorTemp);
        }
    }

    public String getValue() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
        String ret = ((Camera.Parameters) p.first).getWhiteBalance();
        Log.i(TAG, "ret = " + ret);
        if (CUSTOM.equals(ret)) {
            List<String> supportedcustomlist = getSupportedCustomWhiteBalance();
            int size = supportedcustomlist.size();
            if (1 < size && size <= 3) {
                int customNum = ((CameraEx.ParametersModifier) p.second).getCustomWhiteBalance();
                ret = getCustomnumtoitemId(customNum);
            }
        }
        if (Environment.DEVICE_TYPE == 4 && ret == null) {
            return "auto";
        }
        return ret;
    }

    private String getCustomnumtoitemId(int customNum) {
        if (customNum == 1) {
            return "custom1";
        }
        if (customNum == 2) {
            return "custom2";
        }
        if (customNum != 3) {
            return "custom1";
        }
        return "custom3";
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String itemId) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
        if (itemId == null) {
            return "";
        }
        if (itemId.equals(WHITEBALANCE)) {
            String ret = ((Camera.Parameters) p.first).getWhiteBalance();
            if (CUSTOM.equals(ret)) {
                List<String> supportedcustomlist = getSupportedCustomWhiteBalance();
                int size = supportedcustomlist.size();
                if (1 < size && size <= 3) {
                    int customNum = ((CameraEx.ParametersModifier) p.second).getCustomWhiteBalance();
                    String ret2 = getCustomnumtoitemId(customNum);
                    return ret2;
                }
                return ret;
            }
            return ret;
        }
        if (itemId.equals(COLOR_TEMP)) {
            if (ExecutorCreator.getInstance().isAssistApp()) {
                String ret3 = String.valueOf(((CameraEx.ParametersModifier) p.second).getColorTemperatureForWhiteBalance());
                return ret3;
            }
            WhiteBalanceParam param = getWBOptionFromBackUp(getValue());
            String ret4 = String.valueOf(param.colorTemp);
            return ret4;
        }
        Log.e(TAG, LOG_ERROR_NO_VALUE);
        return "";
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public Object getDetailValue() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
        return ExecutorCreator.getInstance().isAssistApp() ? getWBOption(p) : getWBOptionFromBackUp(getValue());
    }

    private WhiteBalanceParam getWBOption(Pair<Camera.Parameters, CameraEx.ParametersModifier> modeParam) {
        WhiteBalanceParam option = new WhiteBalanceParam();
        option.lightBalance = ((CameraEx.ParametersModifier) modeParam.second).getLightBalanceForWhiteBalance();
        option.colorComp = ((CameraEx.ParametersModifier) modeParam.second).getColorCompensationForWhiteBalance();
        String mode = ((Camera.Parameters) modeParam.first).getWhiteBalance();
        if (COLOR_TEMP.equals(mode) || CUSTOM.equals(mode)) {
            option.colorTemp = ((CameraEx.ParametersModifier) modeParam.second).getColorTemperatureForWhiteBalance();
        } else {
            option.colorTemp = DEF_TEMP;
        }
        return option;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public WhiteBalanceParam getWBOptionFromBackUp(String mode) {
        int lightBalance = this.mBackUp.getPreferenceInt(getBackUpKey(mode, LIGHT), 0);
        int colorComp = this.mBackUp.getPreferenceInt(getBackUpKey(mode, COMP), 0);
        int colorTemp = DEF_TEMP;
        if (COLOR_TEMP.equals(mode)) {
            colorTemp = this.mBackUp.getPreferenceInt(getBackUpKey(mode, TEMP), DEF_TEMP);
        } else if (CUSTOM.equals(mode) || "custom1".equals(mode) || "custom2".equals(mode) || "custom3".equals(mode)) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> camera_param = this.mCamSet.getParameters();
            colorTemp = ((CameraEx.ParametersModifier) camera_param.second).getColorTemperatureForWhiteBalance();
        }
        WhiteBalanceParam option = new WhiteBalanceParam(lightBalance, colorComp, colorTemp);
        return option;
    }

    private void setWBOptionToBackUp(String mode, WhiteBalanceParam option) {
        this.mBackUp.setPreference(getBackUpKey(mode, LIGHT), Integer.valueOf(option.lightBalance));
        this.mBackUp.setPreference(getBackUpKey(mode, COMP), Integer.valueOf(option.colorComp));
        if (!CUSTOM.equals(mode) && !"custom1".equals(mode) && !"custom2".equals(mode) && !"custom3".equals(mode)) {
            this.mBackUp.setPreference(getBackUpKey(mode, TEMP), Integer.valueOf(option.colorTemp));
        }
    }

    protected String getBackUpKey(String mode, String option) {
        String ret = "ID_WB_";
        if ("auto".equals(mode)) {
            ret = "ID_WB_AUTO_";
        } else if (DAYLIGHT.equals(mode)) {
            ret = "ID_WB_DAYLIGHT_";
        } else if (SHADE.equals(mode)) {
            ret = "ID_WB_SHADE_";
        } else if (CLOUDY.equals(mode)) {
            ret = "ID_WB_CLOUDY_";
        } else if (INCANDESCENT.equals(mode)) {
            ret = "ID_WB_INCANDESCENT_";
        } else if (WARM_FLUORESCENT.equals(mode)) {
            ret = "ID_WB_FLOUR_WARM_WHITE_";
        } else if (FLUORESCENT_COOLWHITE.equals(mode)) {
            ret = "ID_WB_FLOUR_COOL_WHITE_";
        } else if (FLUORESCENT_DAYWHITE.equals(mode)) {
            ret = "ID_WB_FLOUR_DAY_WHITE_";
        } else if (FLUORESCENT_DAYLIGHT.equals(mode)) {
            ret = "ID_WB_FLOUR_DAYLIGHT_";
        } else if (FLASH.equals(mode)) {
            ret = "ID_WB_FLASH_";
        } else if (UNDERWATER_AUTO.equals(mode)) {
            ret = "ID_WB_UNDERWATER_AUTO_";
        } else if (COLOR_TEMP.equals(mode)) {
            ret = "ID_WB_CTEMP_FILTER_";
        } else if (CUSTOM.equals(mode)) {
            ret = "ID_WB_CUSTOM_";
        } else if ("custom1".equals(mode)) {
            ret = "ID_WB_CUSTOM_";
        } else if ("custom2".equals(mode)) {
            ret = "ID_WB_CUSTOM2_";
        } else if ("custom3".equals(mode)) {
            ret = "ID_WB_CUSTOM3_";
        } else {
            Log.e(TAG, LOG_ERROR_BACKUP);
        }
        if (LIGHT.equals(option)) {
            return ret + "LIGHT";
        }
        if (COMP.equals(option)) {
            return ret + "COMP";
        }
        if (TEMP.equals(option)) {
            return ret + "TEMP";
        }
        Log.e(TAG, LOG_ERROR_BACKUP);
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        List<String> supportedList = new ArrayList<>();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getSupportedParameters();
        if (tag != null) {
            if (tag.equals(WHITEBALANCE)) {
                supportedList = ((Camera.Parameters) p.first).getSupportedWhiteBalance();
                List<String> supportedcustomlist = getSupportedCustomWhiteBalance();
                int size = supportedcustomlist.size();
                if (supportedList != null && 1 == size) {
                    supportedList.add(CUSTOM_SET);
                }
                if (supportedList != null && 1 < size) {
                    supportedList.remove(CUSTOM);
                    supportedList.add("custom1");
                    supportedList.add("custom2");
                    supportedList.add("custom3");
                    supportedList.add(CUSTOM_SET);
                }
            } else if (tag.equals(WHITEBALANCEFN)) {
                supportedList = ((Camera.Parameters) p.first).getSupportedWhiteBalance();
                List<String> supportedcustomlist2 = getSupportedCustomWhiteBalance();
                int size2 = supportedcustomlist2.size();
                if (supportedList != null && 1 < size2) {
                    supportedList.remove(CUSTOM);
                    supportedList.add("custom1");
                    supportedList.add("custom2");
                    supportedList.add("custom3");
                }
            } else if (tag.equals(SETTING_LIGHTBALANCE)) {
                int max = ((CameraEx.ParametersModifier) p.second).getMaxLightBalanceForWhiteBalance();
                int min = ((CameraEx.ParametersModifier) p.second).getMinLightBalanceForWhiteBalance();
                for (int i = min; max >= i; i++) {
                    supportedList.add(String.valueOf(i));
                }
            } else if (tag.equals(SETTING_COMPENSATION)) {
                int max2 = ((CameraEx.ParametersModifier) p.second).getMaxColorCompensationForWhiteBalance();
                int min2 = ((CameraEx.ParametersModifier) p.second).getMinColorCompensationForWhiteBalance();
                for (int i2 = min2; max2 >= i2; i2++) {
                    supportedList.add(String.valueOf(i2));
                }
            } else if (tag.equals(SETTING_TEMPERATURE)) {
                int max3 = ((CameraEx.ParametersModifier) p.second).getMaxColorTemperatureForWhiteBalance();
                int min3 = ((CameraEx.ParametersModifier) p.second).getMinColorTemperatureForWhiteBalance();
                for (int i3 = min3; max3 >= i3; i3 += 100) {
                    supportedList.add(String.valueOf(i3));
                }
            } else {
                Log.e(TAG, LOG_ERROR_UNKNOWN_CATEGOLY);
                return null;
            }
            return supportedList;
        }
        Log.e(TAG, LOG_ERROR_CATEGOLY_IS_NULL);
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        if (!tag.startsWith(WHITEBALANCE)) {
            return false;
        }
        boolean ret = AvailableInfo.isAvailable("setWhiteBalance", null);
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        if (tag == null) {
            tag = WHITEBALANCE;
        }
        ArrayList<String> availables = new ArrayList<>();
        String api_name = "";
        String api_name2 = "";
        if (tag.startsWith(WHITEBALANCE)) {
            api_name = "setWhiteBalance";
            api_name2 = "setCustomWhiteBalance";
        } else if (tag.equals(SETTING_LIGHTBALANCE)) {
            api_name = "setLightBalanceForWhiteBalance";
        } else if (tag.equals(SETTING_COMPENSATION)) {
            api_name = "setColorCompensationForWhiteBalance";
        } else if (tag.equals(SETTING_TEMPERATURE)) {
            api_name = "setColorTemperatureForWhiteBalance";
        }
        List<String> supporteds = getSupportedValue(tag);
        if (supporteds != null) {
            for (String mode : supporteds) {
                if (!CUSTOM_SET.equals(mode)) {
                    if (!CUSTOM.equals(mode) || api_name2.equals("")) {
                        if (AvailableInfo.isAvailable(api_name, mode)) {
                            availables.add(mode);
                        }
                    } else if (AvailableInfo.isAvailable(api_name, mode, api_name2, 1)) {
                        availables.add(mode);
                    }
                }
            }
            List<String> supportedcustomlist = getSupportedCustomWhiteBalance();
            if (1 <= supportedcustomlist.size() && 1 == CameraSetting.getInstance().getCurrentMode() && !tag.equals(WHITEBALANCEFN)) {
                availables.add(CUSTOM_SET);
            }
        }
        if (availables.isEmpty()) {
            return null;
        }
        return availables;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public boolean isUnavailableSceneFactor(String tag) {
        if (WHITEBALANCE.equals(tag)) {
            return isUnavailableAPISceneFactor("setWhiteBalance", null);
        }
        Log.w(TAG, builder.replace(0, tag.length(), tag).append(LOG_INVALID_TAG).append("setWhiteBalance").toString());
        return false;
    }

    private void logcat(String str) {
        Log.i(TAG, str);
    }

    public NotificationListener getCameraNotificationListener() {
        return this.mListener;
    }

    /* loaded from: classes.dex */
    private class ExposureModeChangedListener implements NotificationListener {
        private final String[] tags;

        private ExposureModeChangedListener() {
            this.tags = new String[]{CameraNotificationManager.SCENE_MODE};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (!Environment.isFixedABGMofAWB() && CameraNotificationManager.SCENE_MODE.equals(tag)) {
                String mode = WhiteBalanceController.this.getValue();
                if ("auto".equals(mode) && !ExposureModeController.INTELLIGENT_AUTO_MODE.equals(ExposureModeController.getInstance().getValue(ExposureModeController.EXPOSURE_MODE)) && !"SceneSelectionMode".equals(ExposureModeController.getInstance().getValue(ExposureModeController.EXPOSURE_MODE))) {
                    Pair<Camera.Parameters, CameraEx.ParametersModifier> p = WhiteBalanceController.this.mCamSet.getEmptyParameters();
                    ((CameraEx.ParametersModifier) p.second).setLightBalanceForWhiteBalance(WhiteBalanceController.this.mBackUp.getPreferenceInt(WhiteBalanceController.this.getBackUpKey(mode, WhiteBalanceController.LIGHT), 0));
                    ((CameraEx.ParametersModifier) p.second).setColorCompensationForWhiteBalance(WhiteBalanceController.this.mBackUp.getPreferenceInt(WhiteBalanceController.this.getBackUpKey(mode, WhiteBalanceController.COMP), 0));
                    ((CameraEx.ParametersModifier) p.second).tryIgnoreInhibit();
                    WhiteBalanceController.this.mCamSet.setParametersDirect(p);
                }
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        super.onGetInitParameters(params);
        if (!Environment.isFixedABGMofAWB()) {
            CameraNotificationManager.getInstance().setNotificationListener(this.mListener);
            Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
            if ("auto".equals(((Camera.Parameters) p.first).getWhiteBalance())) {
                this.mBackUp.setPreference(getBackUpKey("auto", LIGHT), Integer.valueOf(((CameraEx.ParametersModifier) p.second).getLightBalanceForWhiteBalance()));
                this.mBackUp.setPreference(getBackUpKey("auto", COMP), Integer.valueOf(((CameraEx.ParametersModifier) p.second).getColorCompensationForWhiteBalance()));
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetTermParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        CameraNotificationManager.getInstance().removeNotificationListener(this.mListener);
    }
}
