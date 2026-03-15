package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/* loaded from: classes.dex */
public class CreativeStyleController extends AbstractController {
    public static final String CLEAR = "clear";
    public static final String CONTRAST = "Contrast";
    public static final String CREATIVESTYLE = "CreativeStyle";
    public static final String DEEP = "deep";
    public static final String IMAGESTYLE = "ImageStyle";
    public static final String LANDSCAPE = "landscape";
    public static final String LIGHT = "light";
    private static final String LOG_CONTRAST = " Contrast :";
    private static final String LOG_CREATIVESTYLE_MODE = "CreativeStyle mode :";
    private static final String LOG_CREATIVESTYLE_OPTION = "CreativeStyle option ";
    private static final String LOG_ERROR_BACKUP = "backup error. invalid backup key";
    private static final String LOG_ERROR_NOTFOUND = "error NotFound Category";
    private static final String LOG_GET_DETAIL_VALUE = "getDetailValue_PF";
    private static final String LOG_INVALID_TAG = ": Invalid tag. ";
    private static final String LOG_MSG_ROUND_BRACKET_L = "(";
    private static final String LOG_MSG_ROUND_BRACKET_R = ")=";
    private static final String LOG_SATURATION = " Saturation :";
    private static final String LOG_SET_DETAIL_VALUE = "setDetailValue";
    private static final String LOG_SET_IMAGE_STYLE = "setImageStyle=";
    private static final String LOG_SHARPNESS = " Sharpness :";
    public static final int MAX_SATURATION = 3;
    public static final int MIN_SATURATION = -3;
    public static final String MONO = "mono";
    public static final String NEUTRAL = "neutral";
    public static final String NIGHT = "night";
    protected static final String OPTION_CONTRAST = "contrast";
    protected static final String OPTION_DELIM = "=";
    protected static final String OPTION_SATURATION = "saturation";
    protected static final String OPTION_SHARPNESS = "sharpness";
    protected static final String OPTION_TOKEN = ";";
    public static final String PORTRAIT = "portrait";
    public static final String RED_LEAVES = "red-leaves";
    public static final String SATURATION = "Saturation";
    public static final String SEPIA = "sepia";
    public static final String SHARPNESS = "Sharpness";
    public static final String STANDARD = "standard";
    public static final String SUNSET = "sunset";
    private static final String TAG = "CreativeStyleController";
    public static final String VIVID = "vivid";
    private static CreativeStyleController mInstance;
    private CreativeStyleControllerListener mCreativeStyleControllerListener;
    private static StringBuilder builder = new StringBuilder();
    private static CreativeStyleOptions tmpOption = new CreativeStyleOptions(0, 0, 0);
    private static StringBuilder mBackUpKeyBuilder = new StringBuilder();
    private static StringBuilder mflattenBuilder = new StringBuilder();
    private static final String myName = CreativeStyleController.class.getSimpleName();
    protected static final String[] tags = {CameraNotificationManager.SCENE_MODE, CameraNotificationManager.PICTURE_EFFECT_CHANGE};
    private final String API_NAME_CS = "setColorMode";
    private final String API_NAME_CT = "setContrast";
    private final String API_NAME_ST = "setSaturation";
    private final String API_NAME_SH = "setSharpness";
    private CameraSetting mCamSet = CameraSetting.getInstance();

    public static final String getName() {
        return myName;
    }

    public static CreativeStyleController getInstance() {
        if (mInstance == null) {
            new CreativeStyleController();
        }
        return mInstance;
    }

    private static void setController(CreativeStyleController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected CreativeStyleController() {
        setController(this);
    }

    /* loaded from: classes.dex */
    public static class CreativeStyleOptions {
        private static StringBuilder mStrBuilder = new StringBuilder();
        public int contrast;
        public int saturation;
        public int sharpness;

        CreativeStyleOptions(int cont, int satu, int shar) {
            this.contrast = cont;
            this.saturation = satu;
            this.sharpness = shar;
        }

        CreativeStyleOptions(CreativeStyleOptions option) {
            this.contrast = option.contrast;
            this.saturation = option.saturation;
            this.sharpness = option.sharpness;
        }

        public void set(int cont, int satu, int shar) {
            this.contrast = cont;
            this.saturation = satu;
            this.sharpness = shar;
        }

        public void set(Pair<Camera.Parameters, CameraEx.ParametersModifier> getParam) {
            set(((CameraEx.ParametersModifier) getParam.second).getContrast(), ((CameraEx.ParametersModifier) getParam.second).getSaturation(), ((CameraEx.ParametersModifier) getParam.second).getSharpness());
        }

        public void setToParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> setParam) {
            ((CameraEx.ParametersModifier) setParam.second).setContrast(this.contrast);
            ((CameraEx.ParametersModifier) setParam.second).setSaturation(this.saturation);
            ((CameraEx.ParametersModifier) setParam.second).setSharpness(this.sharpness);
        }

        public String toString() {
            mStrBuilder.replace(0, mStrBuilder.length(), CreativeStyleController.LOG_CONTRAST).append(this.contrast).append(CreativeStyleController.LOG_SATURATION).append(this.saturation).append(CreativeStyleController.LOG_SHARPNESS).append(this.sharpness);
            return mStrBuilder.toString();
        }

        public boolean equals(CreativeStyleOptions option) {
            if (this.contrast != option.contrast || this.saturation != option.saturation || this.sharpness != option.sharpness) {
                return false;
            }
            return true;
        }

        public boolean equals(Pair<Camera.Parameters, CameraEx.ParametersModifier> getParam) {
            int pf_contrast = ((CameraEx.ParametersModifier) getParam.second).getContrast();
            int pf_saturation = ((CameraEx.ParametersModifier) getParam.second).getSaturation();
            int pf_sharpness = ((CameraEx.ParametersModifier) getParam.second).getSharpness();
            if (this.contrast != pf_contrast || this.saturation != pf_saturation || this.sharpness != pf_sharpness) {
                return false;
            }
            return true;
        }
    }

    /* loaded from: classes.dex */
    public static class CreativeStyleOptionsAvailable {
        public boolean mContrast;
        public boolean mSaturation;
        public boolean mSharpness;

        CreativeStyleOptionsAvailable(boolean cont, boolean satu, boolean shar) {
            this.mContrast = cont;
            this.mSaturation = satu;
            this.mSharpness = shar;
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
        if (CREATIVESTYLE.equals(itemId)) {
            setImageStyle(p, value);
        } else {
            Log.e(TAG, LOG_ERROR_NOTFOUND);
        }
    }

    private void setImageStyle(Pair<Camera.Parameters, CameraEx.ParametersModifier> p, String value) {
        ((CameraEx.ParametersModifier) p.second).setColorMode(value);
        CreativeStyleOptions obj = getDetailValueFromBackUp(value);
        setDetailValue(value, obj, p);
        this.mCamSet.setParameters(p);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public void setDetailValue(Object obj) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
        setDetailValue(getImageStyle(), obj, p);
        this.mCamSet.setParameters(p);
    }

    public void setDetailValue(String mode, Object obj, Pair<Camera.Parameters, CameraEx.ParametersModifier> p) {
        CreativeStyleOptions option = (CreativeStyleOptions) obj;
        setDetailValueToBackUp(mode, option);
        option.setToParameters(p);
        builder.replace(0, builder.length(), LOG_SET_DETAIL_VALUE).append("(").append(mode).append(LOG_MSG_ROUND_BRACKET_R);
        builder.append(option.toString());
        Log.i(TAG, builder.toString());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String itemId) {
        if (CREATIVESTYLE.equals(itemId)) {
            return getImageStyle();
        }
        Log.e(TAG, LOG_ERROR_NOTFOUND);
        return null;
    }

    private String getImageStyle() {
        String ret = ((CameraEx.ParametersModifier) this.mCamSet.getParameters().second).getColorMode();
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public Object getDetailValue() {
        CreativeStyleOptions option = getDetailValueFromBackUp(getImageStyle());
        return option;
    }

    public Object getDetailValue(String value) {
        return getDetailValueFromBackUp(value);
    }

    private CreativeStyleOptions getDetailValueFromBackUp(String mode) {
        tmpOption.set(0, 0, 0);
        if (isAvailable(CREATIVESTYLE)) {
            CreativeStyleOptions option = getUnflattenBackUp(mode, tmpOption);
            return option;
        }
        CreativeStyleOptions option2 = new CreativeStyleOptions(tmpOption);
        return option2;
    }

    private void setDetailValueToBackUp(String mode, CreativeStyleOptions param) {
        BackUpUtil BackUp = BackUpUtil.getInstance();
        BackUp.setPreference(getBackUpKey(mode), flatten(param));
    }

    public void initialize() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> getParam = this.mCamSet.getParameters();
        String mode = ((CameraEx.ParametersModifier) getParam.second).getColorMode();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> setParam = this.mCamSet.getEmptyParameters();
        setImageStyle(setParam, mode);
        tmpOption.set(getParam);
        setDetailValueToBackUp(mode, tmpOption);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String itemId) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getSupportedParameters();
        if (CREATIVESTYLE.equals(itemId)) {
            List<String> ret = ((CameraEx.ParametersModifier) p.second).getSupportedColorModes();
            return ret;
        }
        List<String> ret2 = makeSupportedValue(p, itemId);
        return ret2;
    }

    private List<String> makeSupportedValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> p, String option) {
        List<String> compensationList = new ArrayList<>();
        Pair<Integer, Integer> range = getSupportedRange(p, option);
        int min = ((Integer) range.second).intValue();
        for (int tempValue = ((Integer) range.first).intValue(); min <= tempValue; tempValue--) {
            compensationList.add(String.valueOf(tempValue));
        }
        return compensationList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return CREATIVESTYLE.equals(tag) && !AvailableInfo.isInhibition("INH_FEATURE_CAM_MSGID_SET_COLOR_MODE");
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        if (tag == null) {
            tag = CREATIVESTYLE;
        }
        ArrayList<String> availables = new ArrayList<>();
        String api_name = "";
        if (tag.equals(CREATIVESTYLE)) {
            api_name = "setColorMode";
        } else if (tag.equals(CONTRAST)) {
            api_name = "setContrast";
        } else if (tag.equals(SATURATION)) {
            api_name = "setSaturation";
        } else if (tag.equals(SHARPNESS)) {
            api_name = "setSharpness";
        }
        List<String> supporteds = getSupportedValue(tag);
        if (supporteds != null) {
            for (String mode : supporteds) {
                if (AvailableInfo.isAvailable(api_name, mode)) {
                    availables.add(mode);
                }
            }
        }
        if (availables.isEmpty()) {
            return null;
        }
        return availables;
    }

    private String getBackUpKey(String mode) {
        mBackUpKeyBuilder.replace(0, mBackUpKeyBuilder.length(), "ID_CREATIVESTYLE_");
        if ("standard".equals(mode)) {
            mBackUpKeyBuilder.append("STD");
        } else if (VIVID.equals(mode)) {
            mBackUpKeyBuilder.append("VIVID");
        } else if (NEUTRAL.equals(mode)) {
            mBackUpKeyBuilder.append("NEUTRAL");
        } else if (CLEAR.equals(mode)) {
            mBackUpKeyBuilder.append("CLEAR");
        } else if (DEEP.equals(mode)) {
            mBackUpKeyBuilder.append("DEEP");
        } else if (LIGHT.equals(mode)) {
            mBackUpKeyBuilder.append("LIGHT");
        } else if ("portrait".equals(mode)) {
            mBackUpKeyBuilder.append("PORT");
        } else if ("landscape".equals(mode)) {
            mBackUpKeyBuilder.append("LAND");
        } else if ("sunset".equals(mode)) {
            mBackUpKeyBuilder.append("SUNSET");
        } else if (NIGHT.equals(mode)) {
            mBackUpKeyBuilder.append("NIGHT");
        } else if (RED_LEAVES.equals(mode)) {
            mBackUpKeyBuilder.append("AUTUMN");
        } else if (MONO.equals(mode)) {
            mBackUpKeyBuilder.append("MONO");
        } else if (SEPIA.equals(mode)) {
            mBackUpKeyBuilder.append("SEPIA");
        } else {
            Log.e(TAG, LOG_ERROR_BACKUP);
        }
        mBackUpKeyBuilder.append("_OPTION");
        return mBackUpKeyBuilder.toString();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public boolean isUnavailableSceneFactor(String tag) {
        if (CREATIVESTYLE.equals(tag)) {
            return isUnavailableAPISceneFactor("setColorMode", null);
        }
        Log.w(TAG, builder.replace(0, tag.length(), tag).append(LOG_INVALID_TAG).append("setColorMode").toString());
        return false;
    }

    public Pair<Integer, Integer> getSupportedRange(String itemId) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getSupportedParameters();
        return getSupportedRange(p, itemId);
    }

    public static Pair<Integer, Integer> getSupportedRange(Pair<Camera.Parameters, CameraEx.ParametersModifier> supported, String itemId) {
        int max = 0;
        int min = 0;
        if (itemId.equals(CONTRAST)) {
            max = ((CameraEx.ParametersModifier) supported.second).getMaxContrast();
            min = ((CameraEx.ParametersModifier) supported.second).getMinContrast();
        } else if (itemId.equals(SATURATION)) {
            max = Math.min(((CameraEx.ParametersModifier) supported.second).getMaxSaturation(), 3);
            min = Math.max(((CameraEx.ParametersModifier) supported.second).getMinSaturation(), -3);
        } else if (itemId.equals(SHARPNESS)) {
            max = ((CameraEx.ParametersModifier) supported.second).getMaxSharpness();
            min = ((CameraEx.ParametersModifier) supported.second).getMinSharpness();
        }
        return new Pair<>(Integer.valueOf(max), Integer.valueOf(min));
    }

    public Object getOptionAvailable(String mode) {
        CreativeStyleOptionsAvailable optionAvailable = new CreativeStyleOptionsAvailable(true, true, true);
        if (MONO.equals(mode)) {
            optionAvailable.mSaturation = false;
        } else if (SEPIA.equals(mode)) {
            optionAvailable.mSaturation = false;
        }
        return optionAvailable;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        Log.i(TAG, "onGetInitParameters");
        updateDetailValue(params);
        if (this.mCreativeStyleControllerListener == null) {
            this.mCreativeStyleControllerListener = new CreativeStyleControllerListener();
            CameraNotificationManager.getInstance().setNotificationListener(this.mCreativeStyleControllerListener);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetTermParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        Log.i(TAG, "onGetTermParameters");
        CameraNotificationManager.getInstance().removeNotificationListener(this.mCreativeStyleControllerListener);
        this.mCreativeStyleControllerListener = null;
    }

    /* loaded from: classes.dex */
    protected class CreativeStyleControllerListener implements NotificationListener {
        protected CreativeStyleControllerListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return CreativeStyleController.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            CreativeStyleController.this.updateDetailValue();
        }
    }

    protected synchronized void updateDetailValue() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> setParam = this.mCamSet.getEmptyParameters();
        if (updateDetailValue(setParam)) {
            this.mCamSet.setParameters(setParam);
            Pair<Camera.Parameters, CameraEx.ParametersModifier> currentParam = this.mCamSet.getParameters();
            tmpOption.set(currentParam);
            builder.replace(0, builder.length(), LOG_GET_DETAIL_VALUE).append("(").append(((CameraEx.ParametersModifier) currentParam.second).getColorMode()).append(LOG_MSG_ROUND_BRACKET_R).append(tmpOption.toString());
            Log.i(TAG, builder.toString());
        }
    }

    protected boolean updateDetailValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> param) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> getParam = this.mCamSet.getParameters();
        String mode = ((CameraEx.ParametersModifier) getParam.second).getColorMode();
        boolean isAvailable = isAvailable(CREATIVESTYLE);
        if (isAvailable) {
            tmpOption.set(getParam);
            CreativeStyleOptions option = getUnflattenBackUp(mode, tmpOption);
            setDetailValue(mode, option, param);
            return true;
        }
        tmpOption.set(0, 0, 0);
        if (tmpOption.equals(getParam)) {
            return false;
        }
        tmpOption.setToParameters(param);
        builder.replace(0, builder.length(), LOG_SET_DETAIL_VALUE).append("(").append(mode).append(LOG_MSG_ROUND_BRACKET_R);
        builder.append(tmpOption.toString());
        Log.i(TAG, builder.toString());
        return true;
    }

    protected CreativeStyleOptions getUnflattenBackUp(String mode, CreativeStyleOptions def) {
        BackUpUtil BackUp = BackUpUtil.getInstance();
        String flattened = BackUp.getPreferenceString(getBackUpKey(mode), null);
        CreativeStyleOptions option = unflatten(flattened, def);
        CreativeStyleOptionsAvailable availavle = (CreativeStyleOptionsAvailable) getOptionAvailable(mode);
        if (!availavle.mContrast) {
            option.contrast = 0;
        }
        if (!availavle.mSaturation) {
            option.saturation = 0;
        }
        if (!availavle.mSharpness) {
            option.sharpness = 0;
        }
        return option;
    }

    protected String flatten(CreativeStyleOptions option) {
        mflattenBuilder.replace(0, mflattenBuilder.length(), "contrast").append("=").append(option.contrast).append(";").append("saturation").append("=").append(option.saturation).append(";").append("sharpness").append("=").append(option.sharpness);
        return mflattenBuilder.toString();
    }

    protected CreativeStyleOptions unflatten(String flattened, CreativeStyleOptions def) {
        CreativeStyleOptions option = new CreativeStyleOptions(def);
        if (flattened != null) {
            StringTokenizer tokenizer = new StringTokenizer(flattened, ";");
            while (tokenizer.hasMoreElements()) {
                String ov = tokenizer.nextToken();
                int pos = ov.indexOf("=");
                if (pos == -1) {
                    Log.i(TAG, ov);
                } else {
                    String o = ov.substring(0, pos);
                    String v = ov.substring(pos + 1);
                    if (o.equals("contrast")) {
                        List<String> supported = getSupportedValue(CONTRAST);
                        option.contrast = checkSupportedList(supported, v, def.contrast);
                    } else if (o.equals("saturation")) {
                        List<String> supported2 = getSupportedValue(SATURATION);
                        option.saturation = checkSupportedList(supported2, v, def.saturation);
                    } else if (o.equals("sharpness")) {
                        List<String> supported3 = getSupportedValue(SHARPNESS);
                        option.sharpness = checkSupportedList(supported3, v, def.sharpness);
                    }
                }
            }
        }
        return option;
    }

    protected int checkSupportedList(List<String> supported, String value, int def) {
        if (supported == null || supported.indexOf(value) < 0) {
            return def;
        }
        int ret = Integer.parseInt(value);
        return ret;
    }
}
