package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class PictureSizeController extends ShootingModeController {
    private static final String API_NAME = "setPictureSize";
    private static final String API_NAME_ASPECT_VER1 = "setImageAspectRatio";
    private static final String API_NAME_SIZE_VER1 = "setImageSize";
    public static final String ASPECT_16_9 = "16_9";
    public static final String ASPECT_1_1 = "1_1";
    public static final String ASPECT_3_2 = "3_2";
    public static final String ASPECT_4_3 = "4_3";
    private static final String LOG_MSG_ASPECT = "aspect = ";
    private static final String LOG_MSG_COMMA = ", ";
    private static final String LOG_MSG_GETIMAGEASPECT = "getImageAspect = ";
    private static final String LOG_MSG_GETIMAGESIZE = "getImageSize = ";
    private static final String LOG_MSG_GETPICTURESIZE = "getPictureSize = ";
    private static final String LOG_MSG_GETSUPPORTEDIMAGEASPECT = "getSupportedImageAspect = ";
    private static final String LOG_MSG_GETSUPPORTEDIMAGESIZES = "getSupportedImageSizes = ";
    private static final String LOG_MSG_GETSUPPORTEDPICTURESIZES = "getSupportedPictureSizes = ";
    private static final String LOG_MSG_INVALID_ARGUMENT = "Invalid argument. ";
    private static final String LOG_MSG_ITEMID = "itemId = ";
    private static final String LOG_MSG_LIST_SIZE_IS_NOT_EXPECTED = "Picture size list length is not expected.";
    private static final String LOG_MSG_NULL = "null";
    private static final String LOG_MSG_SETIMAGEASPECT = "setImageAspect = ";
    private static final String LOG_MSG_SETIMAGESIZE = "setImageSize = ";
    private static final String LOG_MSG_SETPICTURESIZE = "setPictureSize = ";
    private static final String LOG_MSG_SIZE = "size = ";
    private static final String LOG_MSG_SQUARE_BRACKET_L = " [ ";
    private static final String LOG_MSG_SQUARE_BRACKET_R = " ] ";
    public static final String MENU_ITEM_ID_ASPECT_RATIO = "Still_AspectRatio";
    public static final String MENU_ITEM_ID_IMAGE_SIZE = "Still_ImageSize";
    private static final int MIN_LIST_COUNT;
    private static final double PROP_11_MAX = 1.1d;
    private static final double PROP_11_MIN = 0.9d;
    private static final double PROP_169_MAX = 1.8d;
    private static final double PROP_169_MIN = 1.6d;
    private static final double PROP_32_MAX = 1.6d;
    private static final double PROP_32_MIN = 1.4d;
    private static final double PROP_43_MAX = 1.4d;
    private static final double PROP_43_MIN = 1.2d;
    private static final int RESOLUTION_10M = 100;
    private static final int RESOLUTION_12M = 120;
    private static final int RESOLUTION_13M = 130;
    private static final int RESOLUTION_14M = 140;
    private static final int RESOLUTION_15M = 150;
    private static final int RESOLUTION_16M = 160;
    private static final int RESOLUTION_17M = 170;
    private static final int RESOLUTION_18M = 180;
    private static final int RESOLUTION_1_7M = 17;
    private static final int RESOLUTION_20M = 200;
    private static final int RESOLUTION_24M = 240;
    private static final int RESOLUTION_2_0M = 20;
    private static final int RESOLUTION_2_5M = 25;
    private static final int RESOLUTION_36M = 360;
    private static final int RESOLUTION_3_4M = 34;
    private static final int RESOLUTION_3_7M = 37;
    private static final int RESOLUTION_3_8M = 38;
    private static final int RESOLUTION_3_9M = 39;
    private static final int RESOLUTION_4_0M = 40;
    private static final int RESOLUTION_4_2M = 42;
    private static final int RESOLUTION_4_6M = 46;
    private static final int RESOLUTION_5_0M = 50;
    private static final int RESOLUTION_5_1M = 51;
    private static final int RESOLUTION_6_0M = 60;
    private static final int RESOLUTION_6_5M = 65;
    private static final int RESOLUTION_7_1M = 71;
    private static final int RESOLUTION_7_5M = 75;
    private static final int RESOLUTION_7_6M = 76;
    private static final int RESOLUTION_8_4M = 84;
    private static final int RESOLUTION_8_7M = 87;
    private static final int RESOLUTION_9_0M = 90;
    public static final String SIZE_L = "SIZE_L";
    public static final String SIZE_M = "SIZE_M";
    public static final String SIZE_S = "SIZE_S";
    public static final String SIZE_VGA = "SIZE_VGA";
    private static final String TAG = "PictureSizeController";
    private static PictureSizeController mInstance;
    private static List<String> mSupportedPictureAspects;
    private static List<String> mSupportedPictureSizes11;
    private static List<String> mSupportedPictureSizes169;
    private static List<String> mSupportedPictureSizes32;
    private static List<String> mSupportedPictureSizes43;
    private static final String myName;
    private static final StringBuilderThreadLocal sStringBuilder = new StringBuilderThreadLocal();
    private static final ArrayList<String> SIZES = new ArrayList<>();
    private final int CAUTION_TYPE_L_24M_3_2 = 0;
    private final int CAUTION_TYPE_L_20M_3_2 = 1;
    private final int CAUTION_TYPE_L_16M_3_2 = 2;
    private final int CAUTION_TYPE_L_20M_4_3 = 3;
    private final int CAUTION_TYPE_L_18M_4_3 = 4;
    private final int CAUTION_TYPE_L_20M_16_9 = 5;
    private final int CAUTION_TYPE_L_17M_16_9 = 6;
    private final int CAUTION_TYPE_L_15M_16_9 = 7;
    private final int CAUTION_TYPE_L_14M_16_9 = 8;
    private final int CAUTION_TYPE_L_13M_1_1 = 9;
    private final int CAUTION_TYPE_L_10M_APSC_3_2 = 10;
    private final int CAUTION_TYPE_L_8_7M_APSC_16_9 = 11;
    private final int CAUTION_TYPE_L_36M_3_2 = 12;
    private final int CAUTION_TYPE_L_15M_3_2 = 13;
    private final int CAUTION_TYPE_L_15M_APS_C_3_2 = 14;
    private final int CAUTION_TYPE_L_13M_APS_C_16_9 = 15;
    private final int CAUTION_TYPE_M_12M_3_2 = 0;
    private final int CAUTION_TYPE_M_10M_3_2 = 1;
    private final int CAUTION_TYPE_M_8_4M_3_2 = 2;
    private final int CAUTION_TYPE_M_10M_4_3 = 3;
    private final int CAUTION_TYPE_M_10M_16_9 = 4;
    private final int CAUTION_TYPE_M_8_7M_16_9 = 5;
    private final int CAUTION_TYPE_M_7_5M_16_9 = 6;
    private final int CAUTION_TYPE_M_7_1M_16_9 = 7;
    private final int CAUTION_TYPE_M_6_5M_1_1 = 8;
    private final int CAUTION_TYPE_M_4_6M_APSC_3_2 = 9;
    private final int CAUTION_TYPE_M_3_9M_APSC_16_9 = 10;
    private final int CAUTION_TYPE_M_9_0M_3_2 = 11;
    private final int CAUTION_TYPE_M_13M_16_9 = 12;
    private final int CAUTION_TYPE_M_8_4M_16_9 = 13;
    private final int CAUTION_TYPE_M_9_0M_APS_C_3_2 = 14;
    private final int CAUTION_TYPE_M_6_0M_APS_C_3_2 = 15;
    private final int CAUTION_TYPE_M_7_6M_APS_C_16_9 = 16;
    private final int CAUTION_TYPE_M_5_0M_APS_C_16_9 = 17;
    private final int CAUTION_TYPE_S_6_0M_3_2 = 0;
    private final int CAUTION_TYPE_S_5_0M_3_2 = 1;
    private final int CAUTION_TYPE_S_4_6M_3_2 = 2;
    private final int CAUTION_TYPE_S_4_0M_3_2 = 3;
    private final int CAUTION_TYPE_S_5_0M_4_3 = 4;
    private final int CAUTION_TYPE_S_5_1M_16_9 = 5;
    private final int CAUTION_TYPE_S_4_2M_16_9 = 6;
    private final int CAUTION_TYPE_S_3_9M_16_9 = 7;
    private final int CAUTION_TYPE_S_3_4M_16_9 = 8;
    private final int CAUTION_TYPE_S_2_0M_16_9 = 9;
    private final int CAUTION_TYPE_S_3_7M_1_1 = 10;
    private final int CAUTION_TYPE_S_2_0M_APSC_3_2 = 11;
    private final int CAUTION_TYPE_S_1_7M_APSC_16_9 = 12;
    private final int CAUTION_TYPE_S_7_6M_16_9 = 13;
    private final int CAUTION_TYPE_S_5_0M_16_9 = 14;
    private final int CAUTION_TYPE_S_3_8M_APS_C_3_2 = 15;
    private final int CAUTION_TYPE_S_2_5M_APS_C_3_2 = 16;
    private CameraSetting mCameraSetting = CameraSetting.getInstance();
    private PictureSizeData mSizeData = new PictureSizeData();

    static {
        SIZES.add(SIZE_L);
        SIZES.add(SIZE_M);
        SIZES.add(SIZE_S);
        SIZES.add(SIZE_VGA);
        MIN_LIST_COUNT = SIZES.size();
        myName = PictureSizeController.class.getSimpleName();
    }

    public static final String getName() {
        return myName;
    }

    public static PictureSizeController getInstance() {
        if (mInstance == null) {
            new PictureSizeController();
        }
        return mInstance;
    }

    private static void setController(PictureSizeController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected PictureSizeController() {
        if (this.mSizeData.mList32.size() < MIN_LIST_COUNT && this.mSizeData.mList169.size() < MIN_LIST_COUNT && this.mSizeData.mList43.size() < MIN_LIST_COUNT && this.mSizeData.mList11.size() < MIN_LIST_COUNT) {
            Log.e(TAG, LOG_MSG_LIST_SIZE_IS_NOT_EXPECTED);
        }
        createSupported();
        setController(this);
    }

    protected void createSupported() {
        if (mSupportedPictureSizes32 == null) {
            mSupportedPictureSizes32 = createSupportedValueArray(this.mSizeData, ASPECT_3_2, MENU_ITEM_ID_IMAGE_SIZE);
        }
        if (mSupportedPictureSizes169 == null) {
            mSupportedPictureSizes169 = createSupportedValueArray(this.mSizeData, ASPECT_16_9, MENU_ITEM_ID_IMAGE_SIZE);
        }
        if (mSupportedPictureSizes43 == null) {
            mSupportedPictureSizes43 = createSupportedValueArray(this.mSizeData, ASPECT_4_3, MENU_ITEM_ID_IMAGE_SIZE);
        }
        if (mSupportedPictureSizes11 == null) {
            mSupportedPictureSizes11 = createSupportedValueArray(this.mSizeData, ASPECT_1_1, MENU_ITEM_ID_IMAGE_SIZE);
        }
        if (mSupportedPictureAspects == null) {
            mSupportedPictureAspects = createSupportedValueArray(this.mSizeData, null, MENU_ITEM_ID_ASPECT_RATIO);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraReopened() {
        mSupportedPictureSizes32 = null;
        mSupportedPictureSizes169 = null;
        mSupportedPictureSizes43 = null;
        mSupportedPictureSizes11 = null;
        mSupportedPictureAspects = null;
        createSupported();
    }

    private String convertToPfVer1(String appValue) {
        if (SIZE_L.equals(appValue)) {
            return "large";
        }
        if (SIZE_M.equals(appValue)) {
            return "midium";
        }
        if (SIZE_S.equals(appValue)) {
            return "small";
        }
        if (SIZE_VGA.equals(appValue)) {
            if (2 > CameraSetting.getPfApiVersion()) {
                return null;
            }
            return "vga";
        }
        if (ASPECT_16_9.equals(appValue)) {
            return "16:9";
        }
        if (ASPECT_3_2.equals(appValue)) {
            return "3:2";
        }
        if (ASPECT_1_1.equals(appValue)) {
            return "1:1";
        }
        if (!ASPECT_4_3.equals(appValue)) {
            return null;
        }
        return "4:3";
    }

    private String convertToApp(String pfValue) {
        if ("large".equals(pfValue)) {
            return SIZE_L;
        }
        if ("midium".equals(pfValue)) {
            return SIZE_M;
        }
        if ("small".equals(pfValue)) {
            return SIZE_S;
        }
        if ("vga".equals(pfValue)) {
            return SIZE_VGA;
        }
        if ("16:9".equals(pfValue)) {
            return ASPECT_16_9;
        }
        if ("3:2".equals(pfValue)) {
            return ASPECT_3_2;
        }
        if ("1:1".equals(pfValue)) {
            return ASPECT_1_1;
        }
        if (!"4:3".equals(pfValue)) {
            return null;
        }
        return ASPECT_4_3;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        StringBuilder builder = sStringBuilder.get();
        if (MENU_ITEM_ID_ASPECT_RATIO.equals(itemId)) {
            if (1 <= CameraSetting.getPfApiVersion()) {
                Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getEmptyParameters();
                builder.replace(0, builder.length(), LOG_MSG_SETIMAGEASPECT).append(value);
                Log.i(TAG, builder.toString());
                String pfValue = convertToPfVer1(value);
                if (pfValue == null) {
                    Log.w(TAG, LOG_MSG_INVALID_ARGUMENT);
                    return;
                } else {
                    ((CameraEx.ParametersModifier) params.second).setImageAspectRatio(pfValue);
                    this.mCameraSetting.setParameters(params);
                    return;
                }
            }
            setPictureSize(value, getSize());
            return;
        }
        if (1 <= CameraSetting.getPfApiVersion()) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params2 = this.mCameraSetting.getEmptyParameters();
            builder.replace(0, builder.length(), LOG_MSG_SETIMAGESIZE).append(value);
            Log.i(TAG, builder.toString());
            String pfValue2 = convertToPfVer1(value);
            if (pfValue2 == null) {
                Log.w(TAG, LOG_MSG_INVALID_ARGUMENT);
                return;
            } else {
                ((CameraEx.ParametersModifier) params2.second).setImageSize(pfValue2);
                this.mCameraSetting.setParameters(params2);
                return;
            }
        }
        setPictureSize(getAspect(), value);
    }

    private void setPictureSize(String aspect, String size) {
        ArrayList<Dimension> list = this.mSizeData.getSizeList(aspect);
        StringBuilder builder = sStringBuilder.get();
        if (list == null) {
            builder.replace(0, builder.length(), LOG_MSG_INVALID_ARGUMENT).append(LOG_MSG_ASPECT).append(aspect);
            Log.e(TAG, builder.toString());
            return;
        }
        int index = SIZES.indexOf(size);
        if (-1 == index) {
            builder.replace(0, builder.length(), LOG_MSG_INVALID_ARGUMENT).append(LOG_MSG_SIZE).append(size);
            Log.e(TAG, builder.toString());
            return;
        }
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getEmptyParameters();
        ((Camera.Parameters) params.first).setPictureSize(list.get(index).width, list.get(index).height);
        builder.replace(0, builder.length(), LOG_MSG_SETPICTURESIZE).append(list.get(index).width).append(", ").append(list.get(index).height);
        Log.i(TAG, builder.toString());
        this.mCameraSetting.setParameters(params);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String itemId) {
        if (MENU_ITEM_ID_IMAGE_SIZE.equals(itemId)) {
            if (isMovieMode()) {
                return null;
            }
            String result = getSize();
            return result;
        }
        if (MENU_ITEM_ID_ASPECT_RATIO.equals(itemId)) {
            if (isMovieMode()) {
                return null;
            }
            String result2 = getAspect();
            return result2;
        }
        StringBuilder builder = sStringBuilder.get();
        builder.replace(0, builder.length(), LOG_MSG_INVALID_ARGUMENT).append(LOG_MSG_ITEMID).append(itemId);
        Log.e(TAG, builder.toString());
        return null;
    }

    private String getSize() {
        String result = null;
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getParameters();
        StringBuilder builder = sStringBuilder.get();
        if (1 <= CameraSetting.getPfApiVersion()) {
            String pfValue = ((CameraEx.ParametersModifier) params.second).getImageSize();
            builder.replace(0, builder.length(), LOG_MSG_GETIMAGESIZE).append(pfValue);
            Log.i(TAG, builder.toString());
            result = convertToApp(pfValue);
            if (result == null) {
                builder.replace(0, builder.length(), LOG_MSG_INVALID_ARGUMENT).append(LOG_MSG_SIZE).append(pfValue);
                Log.e(TAG, builder.toString());
            }
        } else {
            Camera.Size size = ((Camera.Parameters) params.first).getPictureSize();
            builder.replace(0, builder.length(), LOG_MSG_GETPICTURESIZE).append(size.width).append(", ").append(size.height);
            Log.i(TAG, builder.toString());
            ArrayList<Dimension> list = this.mSizeData.getSizeList(getAspect());
            if (list == null) {
                builder.replace(0, builder.length(), LOG_MSG_INVALID_ARGUMENT).append(LOG_MSG_ASPECT).append(getAspect());
                Log.e(TAG, builder.toString());
                return null;
            }
            int num = list.size();
            int i = 0;
            while (true) {
                if (i >= num) {
                    break;
                }
                if (!list.get(i).sameSize(size)) {
                    i++;
                } else {
                    String result2 = SIZES.get(i);
                    result = result2;
                    break;
                }
            }
        }
        return result;
    }

    private String getAspect() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getParameters();
        StringBuilder builder = sStringBuilder.get();
        if (1 <= CameraSetting.getPfApiVersion()) {
            String pfValue = ((CameraEx.ParametersModifier) params.second).getImageAspectRatio();
            builder.replace(0, builder.length(), LOG_MSG_GETIMAGEASPECT).append(pfValue);
            Log.i(TAG, builder.toString());
            String result = convertToApp(pfValue);
            if (result == null) {
                builder.replace(0, builder.length(), LOG_MSG_INVALID_ARGUMENT).append(LOG_MSG_ASPECT).append(pfValue);
                Log.e(TAG, builder.toString());
                return result;
            }
            return result;
        }
        Camera.Size size = ((Camera.Parameters) params.first).getPictureSize();
        builder.replace(0, builder.length(), LOG_MSG_GETPICTURESIZE).append(size.width).append(", ").append(size.height);
        Log.i(TAG, builder.toString());
        float prop = size.width / size.height;
        return convertAspect(prop);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ShootingModeController
    public List<String> getSupportedValue(String itemId, int mode) {
        List<String> list = null;
        if (!Environment.isMovieAPISupported()) {
            mode = 1;
        }
        if (MENU_ITEM_ID_IMAGE_SIZE.equals(itemId)) {
            if (1 <= CameraSetting.getPfApiVersion()) {
                List<String> supported = ((CameraEx.ParametersModifier) this.mCameraSetting.getSupportedParameters(mode).second).getSupportedImageSizes();
                if (supported != null) {
                    list = new ArrayList<>(supported.size());
                    int c = supported.size();
                    for (int i = 0; i < c; i++) {
                        String value = convertToApp(supported.get(i));
                        if (value != null) {
                            list.add(value);
                        }
                    }
                }
            } else if (ASPECT_3_2.equals(getAspect())) {
                list = mSupportedPictureSizes32;
            } else if (ASPECT_16_9.equals(getAspect())) {
                list = mSupportedPictureSizes169;
            } else if (ASPECT_4_3.equals(getAspect())) {
                list = mSupportedPictureSizes43;
            } else if (ASPECT_1_1.equals(getAspect())) {
                list = mSupportedPictureSizes11;
            }
        } else if (MENU_ITEM_ID_ASPECT_RATIO.equals(itemId)) {
            if (1 <= CameraSetting.getPfApiVersion()) {
                List<String> supported2 = ((CameraEx.ParametersModifier) this.mCameraSetting.getSupportedParameters(mode).second).getSupportedImageAspectRatios();
                if (supported2 != null) {
                    list = new ArrayList<>(supported2.size());
                    int c2 = supported2.size();
                    for (int i2 = 0; i2 < c2; i2++) {
                        String value2 = convertToApp(supported2.get(i2));
                        if (value2 != null) {
                            list.add(value2);
                        }
                    }
                }
            } else {
                list = mSupportedPictureAspects;
            }
        } else {
            StringBuilder builder = sStringBuilder.get();
            builder.replace(0, builder.length(), LOG_MSG_INVALID_ARGUMENT).append(LOG_MSG_ITEMID).append(itemId);
            Log.e(TAG, builder.toString());
        }
        if (list == null || !list.isEmpty()) {
            return list;
        }
        return null;
    }

    private static List<String> createSupportedValueArray(PictureSizeData sizeData, String aspect, String tag) {
        ArrayList<String> supporteds = new ArrayList<>();
        StringBuilder builder = sStringBuilder.get();
        if (MENU_ITEM_ID_IMAGE_SIZE.equals(tag)) {
            ArrayList<Dimension> list = sizeData.getSizeList(aspect);
            if (list == null) {
                builder.replace(0, builder.length(), LOG_MSG_INVALID_ARGUMENT).append(LOG_MSG_ASPECT).append(aspect);
                Log.e(TAG, builder.toString());
            } else if (list.size() < MIN_LIST_COUNT) {
                builder.replace(0, builder.length(), LOG_MSG_INVALID_ARGUMENT).append(LOG_MSG_SIZE).append(list.size());
                Log.e(TAG, builder.toString());
            } else {
                supporteds.add(SIZE_L);
                supporteds.add(SIZE_M);
                supporteds.add(SIZE_S);
                int i = 0;
                while (true) {
                    if (i < MIN_LIST_COUNT) {
                        if (list.get(i).width != 640 || list.get(i).height != 480) {
                            i++;
                        } else {
                            supporteds.add(SIZE_VGA);
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        } else if (MENU_ITEM_ID_ASPECT_RATIO.equals(tag)) {
            if (sizeData.mList32.size() >= MIN_LIST_COUNT) {
                supporteds.add(ASPECT_3_2);
            }
            if (sizeData.mList169.size() >= MIN_LIST_COUNT) {
                supporteds.add(ASPECT_16_9);
            }
            if (sizeData.mList43.size() >= MIN_LIST_COUNT) {
                supporteds.add(ASPECT_4_3);
            }
            if (sizeData.mList11.size() >= MIN_LIST_COUNT) {
                supporteds.add(ASPECT_1_1);
            }
        }
        return supporteds;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String itemId) {
        ArrayList<String> availables = new ArrayList<>();
        if (1 <= CameraSetting.getPfApiVersion()) {
            List<String> supported = null;
            String apiName = null;
            if (MENU_ITEM_ID_IMAGE_SIZE.equals(itemId)) {
                supported = ((CameraEx.ParametersModifier) this.mCameraSetting.getSupportedParameters().second).getSupportedImageSizes();
                apiName = API_NAME_SIZE_VER1;
            } else if (MENU_ITEM_ID_ASPECT_RATIO.equals(itemId)) {
                supported = ((CameraEx.ParametersModifier) this.mCameraSetting.getSupportedParameters().second).getSupportedImageAspectRatios();
                apiName = API_NAME_ASPECT_VER1;
            }
            if (supported != null && apiName != null) {
                AvailableInfo.update();
                int c = supported.size();
                for (int i = 0; i < c; i++) {
                    String value = supported.get(i);
                    if (AvailableInfo.isAvailable(apiName, value)) {
                        availables.add(convertToApp(value));
                    }
                }
            }
        } else {
            if (MENU_ITEM_ID_IMAGE_SIZE.equals(itemId)) {
                return getAvailablePictureSizeByAspect(getAspect());
            }
            if (MENU_ITEM_ID_ASPECT_RATIO.equals(itemId)) {
                if (getAvailablePictureSizeByAspect(ASPECT_3_2) != null) {
                    availables.add(ASPECT_3_2);
                }
                if (getAvailablePictureSizeByAspect(ASPECT_16_9) != null) {
                    availables.add(ASPECT_16_9);
                }
                if (getAvailablePictureSizeByAspect(ASPECT_4_3) != null) {
                    availables.add(ASPECT_4_3);
                }
                if (getAvailablePictureSizeByAspect(ASPECT_1_1) != null) {
                    availables.add(ASPECT_1_1);
                }
            }
        }
        if (availables.isEmpty()) {
            return null;
        }
        return availables;
    }

    private List<String> getAvailablePictureSizeByAspect(String aspect) {
        ArrayList<String> availables = new ArrayList<>();
        StringBuilder builder = sStringBuilder.get();
        if (AvailableInfo.isAvailable(API_NAME, null)) {
            ArrayList<Dimension> list = this.mSizeData.getSizeList(aspect);
            if (list == null) {
                builder.replace(0, builder.length(), LOG_MSG_INVALID_ARGUMENT).append(LOG_MSG_ASPECT).append(aspect);
                Log.e(TAG, builder.toString());
                return null;
            }
            if (list.size() < MIN_LIST_COUNT) {
                builder.replace(0, builder.length(), LOG_MSG_INVALID_ARGUMENT).append(LOG_MSG_SIZE).append(list.size());
                Log.e(TAG, builder.toString());
                return null;
            }
            for (int i = 0; i < MIN_LIST_COUNT; i++) {
                if (AvailableInfo.isAvailable(API_NAME, list.get(i).text)) {
                    availables.add(SIZES.get(i));
                }
            }
        }
        if (availables.isEmpty()) {
            return null;
        }
        return availables;
    }

    static String convertAspect(float aspect) {
        if (1.4d <= aspect && aspect < 1.6d) {
            return ASPECT_3_2;
        }
        if (1.6d <= aspect && aspect < PROP_169_MAX) {
            return ASPECT_16_9;
        }
        if (PROP_43_MIN <= aspect && aspect < 1.4d) {
            return ASPECT_4_3;
        }
        if (PROP_11_MIN > aspect || aspect >= PROP_11_MAX) {
            return null;
        }
        return ASPECT_1_1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PictureSizeData {
        ArrayList<Dimension> mList32 = new ArrayList<>();
        ArrayList<Dimension> mList169 = new ArrayList<>();
        ArrayList<Dimension> mList43 = new ArrayList<>();
        ArrayList<Dimension> mList11 = new ArrayList<>();

        ArrayList<Dimension> getSizeList(String aspect) {
            if (PictureSizeController.ASPECT_3_2.equals(aspect)) {
                return this.mList32;
            }
            if (PictureSizeController.ASPECT_16_9.equals(aspect)) {
                return this.mList169;
            }
            if (PictureSizeController.ASPECT_4_3.equals(aspect)) {
                return this.mList43;
            }
            if (PictureSizeController.ASPECT_1_1.equals(aspect)) {
                return this.mList11;
            }
            return null;
        }

        public PictureSizeData() {
            CameraSetting cameraSetting = CameraSetting.getInstance();
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = cameraSetting.getSupportedParameters();
            StringBuilder builder = PictureSizeController.sStringBuilder.get();
            List<Camera.Size> sizelist = ((Camera.Parameters) params.first).getSupportedPictureSizes();
            if (sizelist != null) {
                builder.replace(0, builder.length(), PictureSizeController.LOG_MSG_GETSUPPORTEDPICTURESIZES);
                for (Camera.Size size : sizelist) {
                    builder.append(PictureSizeController.LOG_MSG_SQUARE_BRACKET_L).append(size.width).append(", ").append(size.height).append(PictureSizeController.LOG_MSG_SQUARE_BRACKET_R);
                }
                Log.i(PictureSizeController.TAG, builder.toString());
                if (!sizelist.isEmpty()) {
                    HashMap<String, Float> map = duplicateDataDelete(sizelist);
                    setArrayListProp(map);
                    Collections.sort(this.mList32, new DimensionComparator());
                    Collections.sort(this.mList169, new DimensionComparator());
                    Collections.sort(this.mList43, new DimensionComparator());
                    Collections.sort(this.mList11, new DimensionComparator());
                    return;
                }
                return;
            }
            builder.replace(0, builder.length(), PictureSizeController.LOG_MSG_GETSUPPORTEDPICTURESIZES).append(PictureSizeController.LOG_MSG_NULL);
            Log.i(PictureSizeController.TAG, builder.toString());
        }

        private void setArrayListProp(HashMap<String, Float> map) {
            for (String key : map.keySet()) {
                String[] strAry = key.split("_");
                float value = map.get(key).floatValue();
                ArrayList<Dimension> list = getSizeList(PictureSizeController.convertAspect(value));
                if (list != null) {
                    list.add(new Dimension(Integer.parseInt(strAry[0]), Integer.parseInt(strAry[1])));
                }
            }
        }

        private HashMap<String, Float> duplicateDataDelete(List<Camera.Size> sizelist) {
            HashMap<String, Float> map = new HashMap<>();
            int num = sizelist.size();
            for (int i = 0; i < num; i++) {
                float prop = sizelist.get(i).width / sizelist.get(i).height;
                String key = Integer.toString(sizelist.get(i).width) + "_" + sizelist.get(i).height;
                map.put(key, Float.valueOf(prop));
            }
            return map;
        }
    }

    /* loaded from: classes.dex */
    private static class DimensionComparator implements Comparator<Dimension> {
        private DimensionComparator() {
        }

        @Override // java.util.Comparator
        public int compare(Dimension o1, Dimension o2) {
            if (o1.width < o2.width) {
                return 1;
            }
            return o1.width > o2.width ? -1 : 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Dimension {
        static final String SIZE_SEPARATOR = ":";
        int height;
        String text;
        int width;

        Dimension(int w, int h) {
            this.width = w;
            this.height = h;
            this.text = "" + w + SIZE_SEPARATOR + h;
        }

        boolean sameSize(Camera.Size size) {
            boolean sameWidth = this.width == size.width;
            boolean sameHeight = this.height == size.height;
            return sameWidth && sameHeight;
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        if (MENU_ITEM_ID_ASPECT_RATIO.equals(tag)) {
            List<String> lists = getAvailableValue(MENU_ITEM_ID_ASPECT_RATIO);
            if (lists != null && lists.size() > 1) {
                return true;
            }
            return false;
        }
        AvailableInfo.update();
        boolean ret = (AvailableInfo.isInhibition("INH_FEATURE_CAM_PARAMID_STILL_SIZE") || AvailableInfo.isInhibition("INH_FEATURE_CAM_VALUEID_STILL_SIZE_L") || AvailableInfo.isInhibition("INH_FEATURE_CAM_VALUEID_STILL_SIZE_M") || (MediaNotificationManager.getInstance().isMounted() && AvailableInfo.isFactor("INH_FACTOR_STILL_WRITING"))) ? false : true;
        if (1 == Environment.getVersionOfHW() && ret) {
            try {
                String quality = PictureQualityController.getInstance().getValue(null);
                ret = !PictureQualityController.PICTURE_QUALITY_RAW.equals(quality);
            } catch (IController.NotSupportedException e) {
                e.printStackTrace();
            }
        }
        if (2 == CameraSetting.getInstance().getCurrentMode()) {
            return false;
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public boolean isUnavailableSceneFactor(String tag) {
        if (MENU_ITEM_ID_ASPECT_RATIO.equals(tag)) {
            return false;
        }
        String[] strArr = new String[2];
        strArr[0] = 1 <= CameraSetting.getPfApiVersion() ? API_NAME_SIZE_VER1 : API_NAME;
        strArr[1] = null;
        return isUnavailableAPISceneFactor(strArr);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ShootingModeController, com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        if (isMovieMode()) {
            return -1;
        }
        int cautionIndex = super.getCautionIndex(itemId);
        String aspect = getAspect();
        if (SIZE_L.equals(itemId)) {
            int resolution = getPictureResolutuion(SIZE_L, aspect);
            String apscCondition = getApscCondition();
            if (ASPECT_3_2.equals(aspect)) {
                switch (resolution) {
                    case 25:
                        return 16;
                    case 38:
                        return 15;
                    case RESOLUTION_6_0M /* 60 */:
                        return 15;
                    case RESOLUTION_9_0M /* 90 */:
                        if ("on".equals(apscCondition)) {
                            return 14;
                        }
                        return 11;
                    case 100:
                        return 10;
                    case RESOLUTION_15M /* 150 */:
                        if ("on".equals(apscCondition)) {
                            return 14;
                        }
                        return 13;
                    case RESOLUTION_16M /* 160 */:
                        return 2;
                    case 200:
                        return 1;
                    case RESOLUTION_24M /* 240 */:
                        return 0;
                    case RESOLUTION_36M /* 360 */:
                        return 12;
                    default:
                        return cautionIndex;
                }
            }
            if (ASPECT_4_3.equals(aspect)) {
                switch (resolution) {
                    case RESOLUTION_18M /* 180 */:
                        return 4;
                    case 200:
                        return 3;
                    default:
                        return cautionIndex;
                }
            }
            if (ASPECT_16_9.equals(aspect)) {
                switch (resolution) {
                    case 50:
                        if ("on".equals(apscCondition)) {
                            return 17;
                        }
                        return 14;
                    case RESOLUTION_7_6M /* 76 */:
                        if ("on".equals(apscCondition)) {
                            return 16;
                        }
                        return 13;
                    case RESOLUTION_8_4M /* 84 */:
                        return 13;
                    case RESOLUTION_8_7M /* 87 */:
                        return 11;
                    case RESOLUTION_13M /* 130 */:
                        if ("on".equals(apscCondition)) {
                            return 15;
                        }
                        return 12;
                    case RESOLUTION_14M /* 140 */:
                        return 8;
                    case RESOLUTION_15M /* 150 */:
                        return 7;
                    case RESOLUTION_17M /* 170 */:
                        return 6;
                    case 200:
                        return 5;
                    default:
                        return cautionIndex;
                }
            }
            if (ASPECT_1_1.equals(aspect)) {
                switch (resolution) {
                    case RESOLUTION_13M /* 130 */:
                        return 9;
                    default:
                        return cautionIndex;
                }
            }
            return cautionIndex;
        }
        if (SIZE_M.equals(itemId)) {
            int resolution2 = getPictureResolutuion(SIZE_S, aspect);
            if (ASPECT_3_2.equals(aspect)) {
                switch (resolution2) {
                    case RESOLUTION_4_6M /* 46 */:
                        return 9;
                    case RESOLUTION_8_4M /* 84 */:
                        return 2;
                    case 100:
                        return 1;
                    case RESOLUTION_12M /* 120 */:
                        return 0;
                    default:
                        return cautionIndex;
                }
            }
            if (ASPECT_4_3.equals(aspect)) {
                switch (resolution2) {
                    case 100:
                        return 3;
                    default:
                        return cautionIndex;
                }
            }
            if (ASPECT_16_9.equals(aspect)) {
                switch (resolution2) {
                    case 39:
                        return 10;
                    case RESOLUTION_7_1M /* 71 */:
                        return 7;
                    case RESOLUTION_7_5M /* 75 */:
                        return 6;
                    case RESOLUTION_8_7M /* 87 */:
                        return 5;
                    case 100:
                        return 4;
                    default:
                        return cautionIndex;
                }
            }
            if (ASPECT_1_1.equals(aspect)) {
                switch (resolution2) {
                    case RESOLUTION_6_5M /* 65 */:
                        return 8;
                    default:
                        return cautionIndex;
                }
            }
            return cautionIndex;
        }
        if (SIZE_S.equals(itemId)) {
            int resolution3 = getPictureResolutuion(SIZE_S, aspect);
            if (ASPECT_3_2.equals(aspect)) {
                switch (resolution3) {
                    case 20:
                        return 11;
                    case 40:
                        return 3;
                    case RESOLUTION_4_6M /* 46 */:
                        return 2;
                    case 50:
                        return 1;
                    case RESOLUTION_6_0M /* 60 */:
                        return 0;
                    default:
                        return cautionIndex;
                }
            }
            if (ASPECT_4_3.equals(aspect)) {
                switch (resolution3) {
                    case 50:
                        return 4;
                    default:
                        return cautionIndex;
                }
            }
            if (ASPECT_16_9.equals(aspect)) {
                switch (resolution3) {
                    case 17:
                        return 12;
                    case 20:
                        return 9;
                    case 34:
                        return 8;
                    case 39:
                        return 7;
                    case 42:
                        return 6;
                    case RESOLUTION_5_1M /* 51 */:
                        return 5;
                    default:
                        return cautionIndex;
                }
            }
            if (ASPECT_1_1.equals(aspect)) {
                switch (resolution3) {
                    case 37:
                        return 10;
                    default:
                        return cautionIndex;
                }
            }
            return cautionIndex;
        }
        return cautionIndex;
    }

    protected int getPictureResolutuion(String size, String aspect) {
        if (1 <= CameraSetting.getPfApiVersion()) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getEmptyParameters();
            Camera.Size pixcel = ((CameraEx.ParametersModifier) params.second).getImagePixelSize(convertToPfVer1(size), convertToPfVer1(aspect));
            int resolution = Math.round((pixcel.height * pixcel.width) / EVDialDetector.INVALID_EV_CODE);
            return resolution;
        }
        if (SIZE_L.equals(size)) {
            if (ASPECT_3_2.equals(aspect)) {
                return RESOLUTION_16M;
            }
            if (!ASPECT_16_9.equals(aspect)) {
                return 0;
            }
            return RESOLUTION_14M;
        }
        if (SIZE_M.equals(size)) {
            if (ASPECT_3_2.equals(aspect)) {
                return RESOLUTION_8_4M;
            }
            if (!ASPECT_16_9.equals(aspect)) {
                return 0;
            }
            return RESOLUTION_7_1M;
        }
        if (!SIZE_S.equals(size)) {
            return 0;
        }
        if (ASPECT_3_2.equals(aspect)) {
            return 40;
        }
        if (!ASPECT_16_9.equals(aspect)) {
            return 0;
        }
        return 34;
    }

    private String getApscCondition() {
        ApscModeController apscont = ApscModeController.getInstance();
        if (apscont.getSupportedValue(null) == null || !apscont.isAvailable(ApscModeController.TAG_APSC_MODE_CONDITION)) {
            return "unknown";
        }
        try {
            String apscMode = apscont.getValue(ApscModeController.TAG_APSC_MODE_CONDITION);
            return apscMode;
        } catch (IController.NotSupportedException e) {
            Log.e(TAG, "error");
            return "unknown";
        }
    }
}
