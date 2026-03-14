package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.BaseBackUpKey;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.media.MediaRecorder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class PictureQualityController extends ShootingModeController {
    private static final String API_NAME = "setPictureStorageFormat";
    private static final String LOG_MSG_GETJPEGQUALITY = "getJpegQuality = ";
    private static final String LOG_MSG_GETPICTURESTORAGEFORMAT = "getPictureStorageFormat = ";
    private static final String LOG_MSG_GETSUPPORTEDPICTURESTORAGEFORMATS = "getSupportedPictureStorageFormats = ";
    private static final String LOG_MSG_SETJPEGQUALITY = "setJpegQuality = ";
    private static final String LOG_MSG_SETPICTURESTORAGEFORMAT = "setPictureStorageFormat = ";
    public static final String PICTURE_QUALITY_EXTRAFINE = "jpegextrafine";
    public static final String PICTURE_QUALITY_FINE = "jpegfine";
    public static final String PICTURE_QUALITY_RAW = "raw";
    public static final String PICTURE_QUALITY_RAWJPEG = "rawjpeg";
    public static final String PICTURE_QUALITY_STANDARD = "jpegstandard";
    public static final int QUALITY_EXFINE = 95;
    public static final int QUALITY_FINE = 50;
    public static final int QUALITY_INVALID = -1;
    public static final int QUALITY_STANDARD = 25;
    private static final String TAG = "PictureQualityController";
    private static PictureQualityController mInstance;
    private static List<String> mSupportedPictureQualities;
    private static List<String> mSupportedPictureQualitiesForMovie;
    private static final String myName;
    private CameraSetting mCameraSetting;
    private static final StringBuilder STRBUILD = new StringBuilder();
    private static final HashMap<String, Pair<String, Integer>> PICTURE_QUALITY_COMMANDS = new HashMap<>();

    static {
        PICTURE_QUALITY_COMMANDS.put(PICTURE_QUALITY_RAW, new Pair<>(PICTURE_QUALITY_RAW, -1));
        PICTURE_QUALITY_COMMANDS.put(PICTURE_QUALITY_RAWJPEG, new Pair<>(PICTURE_QUALITY_RAWJPEG, -1));
        PICTURE_QUALITY_COMMANDS.put(PICTURE_QUALITY_FINE, new Pair<>("jpeg", 50));
        PICTURE_QUALITY_COMMANDS.put(PICTURE_QUALITY_STANDARD, new Pair<>("jpeg", 25));
        if (Environment.isAvailableGetSupportedJPEGQuality()) {
            PICTURE_QUALITY_COMMANDS.put(PICTURE_QUALITY_EXTRAFINE, new Pair<>("jpeg", 95));
        }
        mSupportedPictureQualities = null;
        mSupportedPictureQualitiesForMovie = null;
        myName = PictureQualityController.class.getSimpleName();
    }

    public static final String getName() {
        return myName;
    }

    public static PictureQualityController getInstance() {
        if (mInstance == null) {
            mInstance = new PictureQualityController();
        }
        return mInstance;
    }

    private static void setController(PictureQualityController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public PictureQualityController() {
        this.mCameraSetting = null;
        this.mCameraSetting = CameraSetting.getInstance();
        setController(this);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        if (mSupportedPictureQualities == null) {
            mSupportedPictureQualities = createSupportedValueArray(this.mCameraSetting.getSupportedParameters(1));
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitRecorderParameters(MediaRecorder.Parameters params) {
        if (mSupportedPictureQualitiesForMovie == null) {
            mSupportedPictureQualitiesForMovie = createSupportedValueArray(this.mCameraSetting.getSupportedParameters(2));
        }
    }

    private static List<String> createSupportedValueArray(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        ArrayList<String> supporteds = new ArrayList<>();
        List<String> formats = ((CameraEx.ParametersModifier) params.second).getSupportedPictureStorageFormats();
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_GETSUPPORTEDPICTURESTORAGEFORMATS).append(formats);
        Log.i(TAG, STRBUILD.toString());
        if (formats != null) {
            Set<Map.Entry<String, Pair<String, Integer>>> sets = PICTURE_QUALITY_COMMANDS.entrySet();
            for (Map.Entry<String, Pair<String, Integer>> set : sets) {
                String srt = (String) set.getValue().first;
                if (formats.contains(set.getValue().first)) {
                    if (Environment.isAvailableGetSupportedJPEGQuality()) {
                        if (!srt.equals("jpeg")) {
                            supporteds.add(set.getKey());
                        } else {
                            List<Integer> list = ((CameraEx.ParametersModifier) params.second).getSupportedJpegQualities();
                            if (list != null && list.contains(set.getValue().second)) {
                                supporteds.add(set.getKey());
                            }
                        }
                    } else {
                        supporteds.add(set.getKey());
                    }
                }
            }
        }
        return supporteds;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        Pair<String, Integer> command = PICTURE_QUALITY_COMMANDS.get(value);
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getEmptyParameters();
        if (command != null) {
            ((CameraEx.ParametersModifier) params.second).setPictureStorageFormat((String) command.first);
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_SETPICTURESTORAGEFORMAT).append((String) command.first);
            Log.i(TAG, STRBUILD.toString());
            if (-1 != ((Integer) command.second).intValue()) {
                ((Camera.Parameters) params.first).setJpegQuality(((Integer) command.second).intValue());
                STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_SETJPEGQUALITY).append(command.second);
                Log.i(TAG, STRBUILD.toString());
            }
            PictureSizeController pictureSizeController = PictureSizeController.getInstance();
            BackUpUtil backup = BackUpUtil.getInstance();
            if (PICTURE_QUALITY_RAW.equals(value)) {
                String quality = PICTURE_QUALITY_FINE;
                try {
                    quality = getValue(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (quality != null && !quality.equals(value)) {
                    backup.setPreference(BaseBackUpKey.ID_PICTURE_SIZE_FOR_RAW_QUALITY, pictureSizeController.getValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE));
                    pictureSizeController.setValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE, PictureSizeController.SIZE_L);
                }
            } else {
                String size = backup.getPreferenceString(BaseBackUpKey.ID_PICTURE_SIZE_FOR_RAW_QUALITY, null);
                if (size != null) {
                    pictureSizeController.setValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE, size);
                    backup.setPreference(BaseBackUpKey.ID_PICTURE_SIZE_FOR_RAW_QUALITY, "");
                }
            }
            if (PICTURE_QUALITY_RAWJPEG.equals(value)) {
                ((Camera.Parameters) params.first).setJpegQuality(50);
                CameraSetting.getInstance().setParameters(params);
            }
        }
        this.mCameraSetting.setParameters(params);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        String quality = null;
        if (isMovieMode()) {
            return null;
        }
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getParameters();
        String format = ((CameraEx.ParametersModifier) params.second).getPictureStorageFormat();
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_GETPICTURESTORAGEFORMAT).append(format);
        Log.i(TAG, STRBUILD.toString());
        Iterator i$ = PICTURE_QUALITY_COMMANDS.entrySet().iterator();
        while (true) {
            if (!i$.hasNext()) {
                break;
            }
            Map.Entry<String, Pair<String, Integer>> command = i$.next();
            Pair<String, Integer> value = command.getValue();
            if (((String) value.first).equals(format)) {
                if (-1 == ((Integer) value.second).intValue()) {
                    String quality2 = command.getKey();
                    quality = quality2;
                    break;
                }
                int jpegQuality = ((Camera.Parameters) params.first).getJpegQuality();
                STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_GETJPEGQUALITY).append(jpegQuality);
                Log.i(TAG, STRBUILD.toString());
                if (jpegQuality == ((Integer) value.second).intValue()) {
                    String quality3 = command.getKey();
                    quality = quality3;
                    break;
                }
            }
        }
        return quality;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ShootingModeController
    public List<String> getSupportedValue(String tag, int mode) {
        List<String> list;
        if (!Environment.isMovieAPISupported()) {
            mode = 1;
        }
        switch (mode) {
            case 1:
                list = mSupportedPictureQualities;
                break;
            case 2:
                list = mSupportedPictureQualitiesForMovie;
                break;
            default:
                list = null;
                break;
        }
        if (list != null && list.isEmpty()) {
            return null;
        }
        return list;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        ArrayList<String> availables = new ArrayList<>();
        int currentMode = CameraSetting.getInstance().getCurrentMode();
        List<String> supporteds = getSupportedValue(null, currentMode);
        if (supporteds != null) {
            AvailableInfo.update();
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
        AvailableInfo.update();
        return (AvailableInfo.isInhibition("INH_FEATURE_CAM_PARAMID_JPEG_QUALITY") || (MediaNotificationManager.getInstance().isMounted() && AvailableInfo.isFactor("INH_FACTOR_STILL_WRITING"))) ? false : true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public boolean isUnavailableSceneFactor(String tag) {
        boolean b = isUnavailableAPISceneFactor(API_NAME, null);
        return b;
    }
}
