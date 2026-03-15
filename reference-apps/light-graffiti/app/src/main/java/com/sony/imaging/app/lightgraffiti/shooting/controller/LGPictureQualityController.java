package com.sony.imaging.app.lightgraffiti.shooting.controller;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class LGPictureQualityController extends PictureQualityController {
    private static final String LOG_MSG_GETJPEGQUALITY = "getJpegQuality = ";
    private static final String LOG_MSG_GETPICTURESTORAGEFORMAT = "getPictureStorageFormat = ";
    private static LGPictureQualityController mInstance;
    private ArrayList<String> mParamList = null;
    private static final String TAG = LGPictureQualityController.class.getSimpleName();
    private static final StringBuilderThreadLocal STRBUILDS = new StringBuilderThreadLocal();
    private static final HashMap<String, Pair<String, Integer>> PICTURE_QUALITY_COMMANDS = new HashMap<>();

    static {
        PICTURE_QUALITY_COMMANDS.put(PictureQualityController.PICTURE_QUALITY_FINE, new Pair<>("jpeg", 50));
        PICTURE_QUALITY_COMMANDS.put(PictureQualityController.PICTURE_QUALITY_STANDARD, new Pair<>("jpeg", 25));
        Log.d(TAG, "Environment.isAvailableGetSupportedJPEGQuality()=" + Environment.isAvailableGetSupportedJPEGQuality());
        if (Environment.isAvailableGetSupportedJPEGQuality()) {
            PICTURE_QUALITY_COMMANDS.put(PictureQualityController.PICTURE_QUALITY_EXTRAFINE, new Pair<>("jpeg", 95));
        }
    }

    public static LGPictureQualityController getInstance() {
        if (mInstance == null) {
            new LGPictureQualityController();
        }
        return mInstance;
    }

    private static void setController(LGPictureQualityController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected LGPictureQualityController() {
        setController(this);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureQualityController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean ret;
        String stage = LGStateHolder.getInstance().getShootingStage();
        if (stage.equals(LGStateHolder.SHOOTING_1ST)) {
            ret = super.isAvailable(tag);
        } else {
            ret = false;
        }
        Log.d(TAG, AppLog.getMethodName() + " : tag=" + tag + ", ret=" + ret);
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ShootingModeController, com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        int ret = super.getCautionIndex(itemId);
        if (LGStateHolder.getInstance().isShootingStage3rd()) {
            return 1;
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureQualityController, com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        String quality = null;
        if (isMovieMode()) {
            return null;
        }
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getParameters();
        String format = ((CameraEx.ParametersModifier) params.second).getPictureStorageFormat();
        StringBuilder builder = STRBUILDS.get();
        builder.replace(0, builder.length(), LOG_MSG_GETPICTURESTORAGEFORMAT).append(format);
        Log.i(TAG, builder.toString());
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
                builder.replace(0, builder.length(), LOG_MSG_GETJPEGQUALITY).append(jpegQuality);
                Log.i(TAG, builder.toString());
                if (jpegQuality == ((Integer) value.second).intValue()) {
                    String quality3 = command.getKey();
                    quality = quality3;
                    break;
                }
            }
        }
        return quality;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ShootingModeController, com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        Log.e(TAG, "getSupportedValue tag=" + tag);
        if (tag.equals("setPictureStorageFormat")) {
            if (this.mParamList == null) {
                this.mParamList = (ArrayList) super.getSupportedValue(tag);
                try {
                    this.mParamList.remove(this.mParamList.indexOf(PictureQualityController.PICTURE_QUALITY_RAW));
                    this.mParamList.remove(this.mParamList.indexOf(PictureQualityController.PICTURE_QUALITY_RAWJPEG));
                } catch (IndexOutOfBoundsException e) {
                    Log.e(TAG, "getSupportedValue MenuItem delete fail." + e);
                }
            }
        } else {
            this.mParamList = (ArrayList) super.getSupportedValue(tag);
        }
        return this.mParamList;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureQualityController, com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        Log.e(TAG, "getAvailableValue tag=" + tag);
        if (tag.equals("setPictureStorageFormat")) {
            if (this.mParamList == null) {
                this.mParamList = (ArrayList) super.getAvailableValue(tag);
                try {
                    this.mParamList.remove(this.mParamList.indexOf(PictureQualityController.PICTURE_QUALITY_RAW));
                    this.mParamList.remove(this.mParamList.indexOf(PictureQualityController.PICTURE_QUALITY_RAWJPEG));
                } catch (IndexOutOfBoundsException e) {
                    Log.e(TAG, "getSupportedValue MenuItem delete fail." + e);
                }
            }
        } else {
            this.mParamList = (ArrayList) super.getAvailableValue(tag);
        }
        return this.mParamList;
    }
}
