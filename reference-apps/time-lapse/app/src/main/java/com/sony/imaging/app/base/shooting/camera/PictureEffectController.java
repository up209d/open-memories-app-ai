package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class PictureEffectController extends AbstractController {
    private static final String API_NAME = "setPictureEffect";
    public static final String HDR_ART_HIGH = "hdr-art-3";
    public static final String HDR_ART_LOW = "hdr-art-1";
    public static final String HDR_ART_MEDIUM = "hdr-art-2";
    private static final String HDR_ART_PREFIX = "hdr-art-";
    public static final String ILLUST_HIGH = "illustration-3";
    public static final String ILLUST_LOW = "illustration-1";
    public static final String ILLUST_MEDIUM = "illustration-2";
    private static final String ILLUST_PREFIX = "illustration-";
    private static final String LOG_MSG_COMMA = ", ";
    private static final String LOG_MSG_GETMAXPICTUREEFFECTHDRARTEFFECTLEVEL = "getMaxPictureEffectHDRArtEffectLevel = ";
    private static final String LOG_MSG_GETMAXPICTUREEFFECTILLUSTEFFECTLEVEL = "getMaxPictureEffectIllustEffectLevel = ";
    private static final String LOG_MSG_GETMAXPICTUREEFFECTSOFTFOCUSEFFECTLEVEL = "getMaxPictureEffectSoftFocusEffectLevel = ";
    private static final String LOG_MSG_GETMINPICTUREEFFECTHDRARTEFFECTLEVEL = "getMinPictureEffectHDRArtEffectLevel = ";
    private static final String LOG_MSG_GETMINPICTUREEFFECTILLUSTEFFECTLEVEL = "getMinPictureEffectIllustEffectLevel = ";
    private static final String LOG_MSG_GETMINPICTUREEFFECTSOFTFOCUSEFFECTLEVEL = "getMinPictureEffectSoftFocusEffectLevel = ";
    private static final String LOG_MSG_GETPICTUREEFFECT = "getPictureEffect = ";
    private static final String LOG_MSG_GETPICTUREEFFECTHDRARTEFFECTLEVEL = "getPictureEffectHDRArtEffectLevel = ";
    private static final String LOG_MSG_GETPICTUREEFFECTMINIATUREFOCUSAREA = "getPictureEffectMiniatureFocusArea = ";
    private static final String LOG_MSG_GETPICTUREEFFECTPARTCOLOREFFECT = "getPictureEffectPartColorEffect = ";
    private static final String LOG_MSG_GETPICTUREEFFECTPOSTERIZATIONEFFECT = "getPictureEffectPosterizationEffect = ";
    private static final String LOG_MSG_GETPICTUREEFFECTSOFTFOCUSEFFECTLEVEL = "getPictureEffectSoftFocusEffectLevel = ";
    private static final String LOG_MSG_GETPICTUREEFFECTTOYCAMERAEFFECT = "getPictureEffectToyCameraEffect = ";
    private static final String LOG_MSG_GETSUPPORTEDPICTUREEFFECTMINIATUREFOCUSAREAS = "getSupportedPictureEffectMiniatureFocusAreas = ";
    private static final String LOG_MSG_GETSUPPORTEDPICTUREEFFECTPARTCOLOREFFECTS = "getSupportedPictureEffectPartColorEffects = ";
    private static final String LOG_MSG_GETSUPPORTEDPICTUREEFFECTPOSTERIZATIONEFFECTS = "getSupportedPictureEffectPosterizationEffects = ";
    private static final String LOG_MSG_GETSUPPORTEDPICTUREEFFECTS = "getSupportedPictureEffects = ";
    private static final String LOG_MSG_GETSUPPORTEDPICTUREEFFECTTOYCAMERAEFFECTS = "getSupportedPictureEffectToyCameraEffects = ";
    private static final String LOG_MSG_ILLEGALARG = "IllegalArgument is set. ";
    private static final String LOG_MSG_ITEMID = "itemId = ";
    private static final String LOG_MSG_SETPICTUREEFFECT = "setPictureEffect = ";
    private static final String LOG_MSG_SETPICTUREEFFECTHDRARTEFFECTLEVEL = "setPictureEffectHDRArtEffectLevel = ";
    private static final String LOG_MSG_SETPICTUREEFFECTILLUSTEFFECTLEVEL = "setPictureEffectIllustEffectLevel = ";
    private static final String LOG_MSG_SETPICTUREEFFECTMINIATUREFOCUSAREA = "setPictureEffectMiniatureFocusArea = ";
    private static final String LOG_MSG_SETPICTUREEFFECTPARTCOLOREFFECT = "setPictureEffectPartColorEffect = ";
    private static final String LOG_MSG_SETPICTUREEFFECTPOSTERIZATIONEFFECT = "setPictureEffectPosterizationEffect = ";
    private static final String LOG_MSG_SETPICTUREEFFECTSOFTFOCUSEFFECTLEVEL = "setPictureEffectSoftFocusEffectLevel = ";
    private static final String LOG_MSG_SETPICTUREEFFECTTOYCAMERAEFFECT = "setPictureEffectToyCameraEffect = ";
    private static final String LOG_MSG_TAG = "tag = ";
    public static final String MINIATURE_AUTO = "auto";
    public static final String MINIATURE_HCENTER = "hcenter";
    public static final String MINIATURE_LEFT = "left";
    public static final String MINIATURE_LOWER = "lower";
    public static final String MINIATURE_RIGHT = "right";
    public static final String MINIATURE_UPPER = "upper";
    public static final String MINIATURE_VCENTER = "vcenter";
    public static final String MODE_HDR_ART = "hdr-art";
    public static final String MODE_ILLUST = "illust";
    public static final String MODE_MINIATURE = "miniature";
    public static final String MODE_OFF = "off";
    public static final String MODE_PART_COLOR = "part-color";
    public static final String MODE_POP_COLOR = "pop-color";
    public static final String MODE_POSTERIZATION = "posterization";
    public static final String MODE_RETRO_PHOTO = "retro-photo";
    public static final String MODE_RICH_TONE_MONOCHROME = "richtone-mono";
    public static final String MODE_ROUGH_MONO = "rough-mono";
    public static final String MODE_SOFT_FOCUS = "soft-focus";
    public static final String MODE_SOFT_HIGH_KEY = "soft-high-key";
    public static final String MODE_TOY_CAMERA = "toy-camera";
    public static final String PART_COLOR_BLUE = "blue";
    public static final String PART_COLOR_GREEN = "green";
    public static final String PART_COLOR_RED = "red";
    public static final String PART_COLOR_YELLOW = "yellow";
    public static final String PICTUREEFFECT = "PictureEffect";
    public static final String POSTERIZATION_COLOR = "posterization-color";
    public static final String POSTERIZATION_MONO = "posterization-mono";
    public static final String SOFT_FOCUS_HIGH = "soft-focus-3";
    public static final String SOFT_FOCUS_LOW = "soft-focus-1";
    public static final String SOFT_FOCUS_MEDIUM = "soft-focus-2";
    private static final String SOFT_FOCUS_PREFIX = "soft-focus-";
    private static final String TAG = "PictureEffectController";
    public static final String TOY_CAMERA_COOL = "cool";
    public static final String TOY_CAMERA_GREEN = "green";
    public static final String TOY_CAMERA_MAGENTA = "magenta";
    public static final String TOY_CAMERA_NORMAL = "normal";
    public static final String TOY_CAMERA_WARM = "warm";
    public static final String WATERCOLOR = "watercolor";
    private static PictureEffectController mInstance;
    protected ArrayList<String> ITEM_ID_NEEDS_OPTION;
    protected HashMap<String, String> OPTION_API_NAME_DICTIONARY;
    private List<String> mRawSupportedPictureEffects;
    private HashMap<String, List<String>> mSupportedOptionValues;
    private List<String> mSupportedPictureEffectModes;
    protected static final StringBuilderThreadLocal STRBUILDS = new StringBuilderThreadLocal();
    private static boolean mMiniatureForSpecialSequence = false;
    private static final String myName = PictureEffectController.class.getSimpleName();
    private int mSoftfocusMin = 1;
    private int mSoftfocusMax = 1;
    private int mHdrMin = 1;
    private int mHdrMax = 1;
    private int mIllustMin = 1;
    private int mIllustMax = 1;
    private NotificationListener mListener = new FocusChangedListener();
    protected CameraSetting mCamSet = CameraSetting.getInstance();

    protected void setItemNeedsOptionArray() {
        this.ITEM_ID_NEEDS_OPTION = new ArrayList<>();
        this.ITEM_ID_NEEDS_OPTION.add(MODE_TOY_CAMERA);
        this.ITEM_ID_NEEDS_OPTION.add(MODE_POSTERIZATION);
        this.ITEM_ID_NEEDS_OPTION.add(MODE_PART_COLOR);
        this.ITEM_ID_NEEDS_OPTION.add(MODE_SOFT_FOCUS);
        this.ITEM_ID_NEEDS_OPTION.add(MODE_HDR_ART);
        this.ITEM_ID_NEEDS_OPTION.add(MODE_MINIATURE);
        this.ITEM_ID_NEEDS_OPTION.add(MODE_ILLUST);
    }

    protected void setOptionApiNameDirectory() {
        this.OPTION_API_NAME_DICTIONARY = new HashMap<>();
        this.OPTION_API_NAME_DICTIONARY.put(MODE_TOY_CAMERA, "setPictureEffectToyCameraEffect");
        this.OPTION_API_NAME_DICTIONARY.put(MODE_POSTERIZATION, "setPictureEffectPosterizationEffect");
        this.OPTION_API_NAME_DICTIONARY.put(MODE_PART_COLOR, "setPictureEffectPartColorEffect");
        this.OPTION_API_NAME_DICTIONARY.put(MODE_SOFT_FOCUS, "setPictureEffectSoftFocusEffectLevel");
        this.OPTION_API_NAME_DICTIONARY.put(MODE_HDR_ART, "setPictureEffectHDRArtEffectLevel");
        this.OPTION_API_NAME_DICTIONARY.put(MODE_MINIATURE, "setPictureEffectMiniatureFocusArea");
        this.OPTION_API_NAME_DICTIONARY.put(MODE_ILLUST, "setPictureEffectIllustEffectLevel");
    }

    public static final String getName() {
        return myName;
    }

    public static PictureEffectController getInstance() {
        if (mInstance == null) {
            new PictureEffectController();
        }
        return mInstance;
    }

    private static void setController(PictureEffectController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public PictureEffectController() {
        setItemNeedsOptionArray();
        setOptionApiNameDirectory();
        createSupported();
        setController(this);
    }

    protected void createSupported() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getSupportedParameters();
        if (this.mRawSupportedPictureEffects == null) {
            this.mRawSupportedPictureEffects = ((CameraEx.ParametersModifier) p.second).getSupportedPictureEffects();
            StringBuilder builder = STRBUILDS.get();
            builder.replace(0, builder.length(), LOG_MSG_GETSUPPORTEDPICTUREEFFECTS).append(this.mRawSupportedPictureEffects);
            Log.i(TAG, builder.toString());
        }
        if (this.mSupportedOptionValues == null) {
            this.mSupportedOptionValues = new HashMap<>();
        }
        Iterator i$ = this.ITEM_ID_NEEDS_OPTION.iterator();
        while (i$.hasNext()) {
            String itemId = i$.next();
            if (this.mSupportedOptionValues.get(itemId) == null) {
                this.mSupportedOptionValues.put(itemId, createSupportedOptionValueArray(p, itemId));
            }
        }
        if (this.mSupportedPictureEffectModes == null) {
            this.mSupportedPictureEffectModes = createSupportedValueArray(p);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraReopened() {
        this.mRawSupportedPictureEffects = null;
        this.mSupportedOptionValues = null;
        this.mSupportedPictureEffectModes = null;
        createSupported();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
        if ("PictureEffect".equals(itemId)) {
            setPictureEffect(p, value);
        } else {
            setOptionValue(p, itemId, value);
        }
    }

    private void setPictureEffect(Pair<Camera.Parameters, CameraEx.ParametersModifier> p, String value) {
        ((CameraEx.ParametersModifier) p.second).setPictureEffect(value);
        StringBuilder builder = STRBUILDS.get();
        builder.replace(0, builder.length(), LOG_MSG_SETPICTUREEFFECT).append(value);
        Log.i(TAG, builder.toString());
        this.mCamSet.setParameters(p);
        if (Environment.DEVICE_TYPE == 4) {
            CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.PICTURE_EFFECT_CHANGE);
        }
    }

    public NotificationListener getCameraNotificationListener() {
        return this.mListener;
    }

    /* loaded from: classes.dex */
    private class FocusChangedListener implements NotificationListener {
        private final String[] tags;

        private FocusChangedListener() {
            this.tags = new String[]{CameraNotificationManager.FOCUS_CHANGE};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.PICTURE_EFFECT_CHANGE);
        }
    }

    private void setOptionValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> p, String itemId, String value) {
        StringBuilder builder = STRBUILDS.get();
        if (MODE_TOY_CAMERA.equals(itemId)) {
            ((CameraEx.ParametersModifier) p.second).setPictureEffectToyCameraEffect(value);
            builder.replace(0, builder.length(), LOG_MSG_SETPICTUREEFFECTTOYCAMERAEFFECT).append(value);
            Log.i(TAG, builder.toString());
        } else if (MODE_POSTERIZATION.equals(itemId)) {
            ((CameraEx.ParametersModifier) p.second).setPictureEffectPosterizationEffect(value);
            builder.replace(0, builder.length(), LOG_MSG_SETPICTUREEFFECTPOSTERIZATIONEFFECT).append(value);
            Log.i(TAG, builder.toString());
        } else if (MODE_PART_COLOR.equals(itemId)) {
            ((CameraEx.ParametersModifier) p.second).setPictureEffectPartColorEffect(value);
            builder.replace(0, builder.length(), LOG_MSG_SETPICTUREEFFECTPARTCOLOREFFECT).append(value);
            Log.i(TAG, builder.toString());
        } else if (MODE_SOFT_FOCUS.equals(itemId)) {
            if (value.startsWith(SOFT_FOCUS_PREFIX)) {
                String level = value.substring(SOFT_FOCUS_PREFIX.length());
                int num = Integer.valueOf(level).intValue();
                if (this.mSoftfocusMax < num) {
                    num = this.mSoftfocusMax;
                }
                if (num < this.mSoftfocusMin) {
                    num = this.mSoftfocusMin;
                }
                ((CameraEx.ParametersModifier) p.second).setPictureEffectSoftFocusEffectLevel(num);
                builder.replace(0, builder.length(), LOG_MSG_SETPICTUREEFFECTSOFTFOCUSEFFECTLEVEL).append(value);
                Log.i(TAG, builder.toString());
            }
        } else if (MODE_HDR_ART.equals(itemId)) {
            if (value.startsWith(HDR_ART_PREFIX)) {
                String level2 = value.substring(HDR_ART_PREFIX.length());
                int num2 = Integer.valueOf(level2).intValue();
                if (this.mHdrMax < num2) {
                    num2 = this.mHdrMax;
                }
                if (num2 < this.mHdrMin) {
                    num2 = this.mHdrMin;
                }
                ((CameraEx.ParametersModifier) p.second).setPictureEffectHDRArtEffectLevel(num2);
                builder.replace(0, builder.length(), LOG_MSG_SETPICTUREEFFECTHDRARTEFFECTLEVEL).append(value);
                Log.i(TAG, builder.toString());
            }
        } else if (MODE_MINIATURE.equals(itemId)) {
            ((CameraEx.ParametersModifier) p.second).setPictureEffectMiniatureFocusArea(value);
            builder.replace(0, builder.length(), LOG_MSG_SETPICTUREEFFECTMINIATUREFOCUSAREA).append(value);
            Log.i(TAG, builder.toString());
        } else if (MODE_ILLUST.equals(itemId)) {
            String level3 = value.substring(ILLUST_PREFIX.length());
            int num3 = Integer.valueOf(level3).intValue();
            if (this.mIllustMax < num3) {
                num3 = this.mIllustMax;
            }
            if (num3 < this.mIllustMin) {
                num3 = this.mIllustMin;
            }
            ((CameraEx.ParametersModifier) p.second).setPictureEffectIllustEffectLevel(num3);
            builder.replace(0, builder.length(), LOG_MSG_SETPICTUREEFFECTILLUSTEFFECTLEVEL).append(value);
            Log.i(TAG, builder.toString());
        } else {
            builder.replace(0, builder.length(), LOG_MSG_ILLEGALARG).append(LOG_MSG_ITEMID).append(itemId);
            Log.w(TAG, builder.toString());
        }
        this.mCamSet.setParameters(p);
    }

    public String getValue() {
        String value = getValue("PictureEffect");
        if (hasOptions(value)) {
            return getValue(value);
        }
        return value;
    }

    private boolean hasOptions(String itemId) {
        return this.ITEM_ID_NEEDS_OPTION.contains(itemId);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String itemId) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
        if ("PictureEffect".equals(itemId)) {
            String ret = ((CameraEx.ParametersModifier) p.second).getPictureEffect();
            StringBuilder builder = STRBUILDS.get();
            builder.replace(0, builder.length(), LOG_MSG_GETPICTUREEFFECT).append(ret);
            Log.i(TAG, builder.toString());
            return ret;
        }
        return getOptionValue(p, itemId);
    }

    private String getOptionValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> p, String itemId) {
        StringBuilder builder = STRBUILDS.get();
        if (MODE_TOY_CAMERA.equals(itemId)) {
            String ret = ((CameraEx.ParametersModifier) p.second).getPictureEffectToyCameraEffect();
            builder.replace(0, builder.length(), LOG_MSG_GETPICTUREEFFECTTOYCAMERAEFFECT).append(ret);
            Log.i(TAG, builder.toString());
            return ret;
        }
        if (MODE_POSTERIZATION.equals(itemId)) {
            String ret2 = ((CameraEx.ParametersModifier) p.second).getPictureEffectPosterizationEffect();
            builder.replace(0, builder.length(), LOG_MSG_GETPICTUREEFFECTPOSTERIZATIONEFFECT).append(ret2);
            Log.i(TAG, builder.toString());
            return ret2;
        }
        if (MODE_PART_COLOR.equals(itemId)) {
            String ret3 = ((CameraEx.ParametersModifier) p.second).getPictureEffectPartColorEffect();
            builder.replace(0, builder.length(), LOG_MSG_GETPICTUREEFFECTPARTCOLOREFFECT).append(ret3);
            Log.i(TAG, builder.toString());
            return ret3;
        }
        if (MODE_SOFT_FOCUS.equals(itemId)) {
            int level = ((CameraEx.ParametersModifier) p.second).getPictureEffectSoftFocusEffectLevel();
            builder.replace(0, builder.length(), LOG_MSG_GETPICTUREEFFECTSOFTFOCUSEFFECTLEVEL).append(level);
            Log.i(TAG, builder.toString());
            return SOFT_FOCUS_PREFIX + level;
        }
        if (MODE_HDR_ART.equals(itemId)) {
            int level2 = ((CameraEx.ParametersModifier) p.second).getPictureEffectHDRArtEffectLevel();
            builder.replace(0, builder.length(), LOG_MSG_GETPICTUREEFFECTHDRARTEFFECTLEVEL).append(level2);
            Log.i(TAG, builder.toString());
            return HDR_ART_PREFIX + level2;
        }
        if (MODE_MINIATURE.equals(itemId)) {
            String ret4 = ((CameraEx.ParametersModifier) p.second).getPictureEffectMiniatureFocusArea();
            builder.replace(0, builder.length(), LOG_MSG_GETPICTUREEFFECTMINIATUREFOCUSAREA).append(ret4);
            Log.i(TAG, builder.toString());
            return ret4;
        }
        if (MODE_ILLUST.equals(itemId)) {
            int level3 = ((CameraEx.ParametersModifier) p.second).getPictureEffectIllustEffectLevel();
            builder.replace(0, builder.length(), LOG_MSG_GETMINPICTUREEFFECTILLUSTEFFECTLEVEL).append(level3);
            Log.i(TAG, builder.toString());
            return ILLUST_PREFIX + level3;
        }
        builder.replace(0, builder.length(), LOG_MSG_ILLEGALARG).append(LOG_MSG_ITEMID).append(itemId);
        Log.w(TAG, builder.toString());
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        List<String> availables = new ArrayList<>();
        AvailableInfo.update();
        if ("PictureEffect".equals(tag)) {
            for (String mode : this.mSupportedPictureEffectModes) {
                if (AvailableInfo.isAvailable(API_NAME, mode)) {
                    if (hasOptions(mode)) {
                        List<String> options = gerAvailableOptionValue(mode);
                        if (!options.isEmpty()) {
                            availables.add(mode);
                        }
                    } else {
                        availables.add(mode);
                    }
                }
            }
        } else if (hasOptions(tag)) {
            availables = gerAvailableOptionValue(tag);
        } else if (AvailableInfo.isAvailable(API_NAME, tag)) {
            availables.add(tag);
        }
        if (availables.isEmpty()) {
            return null;
        }
        return availables;
    }

    private List<String> gerAvailableOptionValue(String tag) {
        ArrayList<String> availables = new ArrayList<>();
        String apiName = this.OPTION_API_NAME_DICTIONARY.get(tag);
        List<String> options = this.mSupportedOptionValues.get(tag);
        if (apiName != null && options != null) {
            for (String value : options) {
                if (AvailableInfo.isAvailable(apiName, value)) {
                    availables.add(value);
                }
            }
        }
        return availables;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return "PictureEffect".equals(tag) ? this.mSupportedPictureEffectModes : this.mSupportedOptionValues.get(tag);
    }

    protected List<String> createSupportedOptionValueArray(Pair<Camera.Parameters, CameraEx.ParametersModifier> params, String tag) {
        StringBuilder builder = STRBUILDS.get();
        if (MODE_TOY_CAMERA.equals(tag)) {
            List<String> list = ((CameraEx.ParametersModifier) params.second).getSupportedPictureEffectToyCameraEffects();
            builder.replace(0, builder.length(), LOG_MSG_GETSUPPORTEDPICTUREEFFECTTOYCAMERAEFFECTS).append(list);
            Log.i(TAG, builder.toString());
            return list;
        }
        if (MODE_POSTERIZATION.equals(tag)) {
            List<String> list2 = ((CameraEx.ParametersModifier) params.second).getSupportedPictureEffectPosterizationEffects();
            builder.replace(0, builder.length(), LOG_MSG_GETSUPPORTEDPICTUREEFFECTPOSTERIZATIONEFFECTS).append(list2);
            Log.i(TAG, builder.toString());
            return list2;
        }
        if (MODE_PART_COLOR.equals(tag)) {
            List<String> list3 = ((CameraEx.ParametersModifier) params.second).getSupportedPictureEffectPartColorEffects();
            builder.replace(0, builder.length(), LOG_MSG_GETSUPPORTEDPICTUREEFFECTPARTCOLOREFFECTS).append(list3);
            Log.i(TAG, builder.toString());
            return list3;
        }
        if (MODE_SOFT_FOCUS.equals(tag)) {
            this.mSoftfocusMin = ((CameraEx.ParametersModifier) params.second).getMinPictureEffectSoftFocusEffectLevel();
            this.mSoftfocusMax = ((CameraEx.ParametersModifier) params.second).getMaxPictureEffectSoftFocusEffectLevel();
            builder.replace(0, builder.length(), LOG_MSG_GETMINPICTUREEFFECTSOFTFOCUSEFFECTLEVEL).append(this.mSoftfocusMin).append(", ").append(LOG_MSG_GETMAXPICTUREEFFECTSOFTFOCUSEFFECTLEVEL).append(this.mSoftfocusMax);
            Log.i(TAG, builder.toString());
            List<String> list4 = new ArrayList<>();
            for (int i = this.mSoftfocusMin; i <= this.mSoftfocusMax; i++) {
                list4.add(SOFT_FOCUS_PREFIX + i);
            }
            return list4;
        }
        if (MODE_HDR_ART.equals(tag)) {
            this.mHdrMin = ((CameraEx.ParametersModifier) params.second).getMinPictureEffectHDRArtEffectLevel();
            this.mHdrMax = ((CameraEx.ParametersModifier) params.second).getMaxPictureEffectHDRArtEffectLevel();
            builder.replace(0, builder.length(), LOG_MSG_GETMINPICTUREEFFECTHDRARTEFFECTLEVEL).append(this.mHdrMin).append(", ").append(LOG_MSG_GETMAXPICTUREEFFECTHDRARTEFFECTLEVEL).append(this.mHdrMax);
            Log.i(TAG, builder.toString());
            List<String> list5 = new ArrayList<>();
            for (int i2 = this.mHdrMin; i2 <= this.mHdrMax; i2++) {
                list5.add(HDR_ART_PREFIX + i2);
            }
            return list5;
        }
        if (MODE_MINIATURE.equals(tag)) {
            List<String> list6 = ((CameraEx.ParametersModifier) params.second).getSupportedPictureEffectMiniatureFocusAreas();
            builder.replace(0, builder.length(), LOG_MSG_GETSUPPORTEDPICTUREEFFECTMINIATUREFOCUSAREAS).append(list6);
            Log.i(TAG, builder.toString());
            return list6;
        }
        if (2 <= Environment.getVersionOfHW() && MODE_ILLUST.equals(tag)) {
            this.mIllustMin = ((CameraEx.ParametersModifier) params.second).getMinPictureEffectIllustEffectLevel();
            this.mIllustMax = ((CameraEx.ParametersModifier) params.second).getMaxPictureEffectIllustEffectLevel();
            builder.replace(0, builder.length(), LOG_MSG_GETMINPICTUREEFFECTILLUSTEFFECTLEVEL).append(this.mIllustMin).append(", ").append(LOG_MSG_GETMAXPICTUREEFFECTILLUSTEFFECTLEVEL).append(this.mIllustMax);
            Log.i(TAG, builder.toString());
            List<String> list7 = new ArrayList<>();
            for (int i3 = this.mIllustMin; i3 <= this.mIllustMax; i3++) {
                list7.add(ILLUST_PREFIX + i3);
            }
            return list7;
        }
        builder.replace(0, builder.length(), LOG_MSG_ILLEGALARG).append("tag = ").append(tag);
        Log.w(TAG, builder.toString());
        return null;
    }

    protected List<String> createSupportedValueArray(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        ArrayList<String> list = new ArrayList<>();
        if (this.mRawSupportedPictureEffects != null) {
            for (String mode : this.mRawSupportedPictureEffects) {
                if (this.ITEM_ID_NEEDS_OPTION.contains(mode)) {
                    List<String> options = this.mSupportedOptionValues.get(mode);
                    if (options != null && !options.isEmpty()) {
                        if (Environment.getVersionOfHW() < 2 && mode.equals(MODE_ILLUST)) {
                            break;
                        }
                        list.add(mode);
                    }
                } else {
                    if (Environment.getVersionOfHW() < 2 && mode.equals(WATERCOLOR)) {
                        break;
                    }
                    list.add(mode);
                }
            }
        }
        return list;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public boolean isUnavailableSceneFactor(String tag) {
        if ("PictureEffect".equals(tag)) {
            return isUnavailableAPISceneFactor(API_NAME, null);
        }
        StringBuilder builder = STRBUILDS.get();
        builder.replace(0, builder.length(), LOG_MSG_ILLEGALARG).append("tag = ").append(tag);
        Log.w(TAG, builder.toString());
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return AvailableInfo.isAvailable(API_NAME, null);
    }

    protected List<String> getRawSupportedPictureEffects() {
        return this.mRawSupportedPictureEffects;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        super.onGetInitParameters(params);
        if (this.mListener != null) {
            CameraNotificationManager.getInstance().setNotificationListener(this.mListener);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetTermParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        super.onGetTermParameters(params);
        if (this.mListener != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mListener);
        }
    }

    public void setMiniatureForSpecialSequence(boolean miniatureMode) {
        if (mMiniatureForSpecialSequence != miniatureMode) {
            mMiniatureForSpecialSequence = miniatureMode;
            CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.MINIATURE_SHADED_REGION_CHANGE);
        }
    }

    public boolean isMiniatureForSpecialSequence() {
        return mMiniatureForSpecialSequence;
    }
}
