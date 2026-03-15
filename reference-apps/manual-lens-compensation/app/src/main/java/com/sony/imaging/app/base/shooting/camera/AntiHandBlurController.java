package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class AntiHandBlurController extends AbstractController {
    private static final String API_NAME_MODE = "setAntiHandBlurMode";
    private static final String API_NAME_MOVIE_MODE = "setMovieAntiHandBlurMode";
    private static final int API_VERSION_SUPPORTING_MOVIE_ANTI_HAND_BLUR = 3;
    public static final String CONTINUOUS = "keep";
    private static final String LOG_MSG_GETANTIHANDBLURMODE = ":getValue = ";
    private static final String LOG_MSG_SETANTIHANDBLURMODE = ":setValue = ";
    public static final String MOVIE = "movie";
    public static final String MOVIE_ACTIVE = "active";
    public static final String MOVIE_OFF = "off";
    public static final String MOVIE_STANDARD = "standard";
    public static final String OFF = "off";
    public static final String SHOOT = "onetime";
    public static final String STILL = "still";
    private static final String TAG = "AntiHandBlur";
    private static AntiHandBlurController mInstance;
    private static final String myName = AntiHandBlurController.class.getSimpleName();
    private CameraSetting mCamSet = CameraSetting.getInstance();

    public static final String getName() {
        return myName;
    }

    public static AntiHandBlurController getInstance() {
        if (mInstance == null) {
            new AntiHandBlurController();
        }
        return mInstance;
    }

    private static void setController(AntiHandBlurController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AntiHandBlurController() {
        setController(this);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
        if (STILL.equals(tag)) {
            ((CameraEx.ParametersModifier) p.second).setAntiHandBlurMode(value);
            this.mCamSet.setParameters(p);
        } else if ("movie".equals(tag) && CameraSetting.getPfApiVersion() >= 3) {
            ((CameraEx.ParametersModifier) p.second).setMovieAntiHandBlurMode(value);
            CameraSetting.getInstance().setParameters(p);
        }
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), tag).append(LOG_MSG_SETANTIHANDBLURMODE).append(value);
        Log.i("AntiHandBlur", builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        String value = null;
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
        if (STILL.equals(tag)) {
            value = ((CameraEx.ParametersModifier) p.second).getAntiHandBlurMode();
        } else if ("movie".equals(tag) && CameraSetting.getPfApiVersion() >= 3) {
            value = ((CameraEx.ParametersModifier) p.second).getMovieAntiHandBlurMode();
        }
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), tag).append(LOG_MSG_GETANTIHANDBLURMODE).append(value);
        Log.i("AntiHandBlur", builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        return value;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getSupportedParameters();
        if (STILL.equals(tag)) {
            List<String> supported = ((CameraEx.ParametersModifier) p.second).getSupportedAntiHandBlurModes();
            return supported;
        }
        if (!"movie".equals(tag) || CameraSetting.getPfApiVersion() < 3) {
            return null;
        }
        List<String> supported2 = ((CameraEx.ParametersModifier) p.second).getSupportedMovieAntiHandBlurModes();
        return supported2;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        ArrayList<String> availables = new ArrayList<>();
        List<String> supporteds = getSupportedValue(tag);
        if (supporteds != null) {
            AvailableInfo.update();
            for (String mode : supporteds) {
                if (isAvailable(tag, mode)) {
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
        if (STILL.equals(tag)) {
            boolean available = AvailableInfo.isAvailable(API_NAME_MODE, null);
            return available;
        }
        if (!"movie".equals(tag) || CameraSetting.getPfApiVersion() < 3) {
            return false;
        }
        boolean available2 = AvailableInfo.isAvailable(API_NAME_MOVIE_MODE, null);
        return available2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isAvailable(String tag, String mode) {
        if (STILL.equals(tag)) {
            boolean available = AvailableInfo.isAvailable(API_NAME_MODE, mode);
            return available;
        }
        if (!"movie".equals(tag) || CameraSetting.getPfApiVersion() < 3) {
            return false;
        }
        boolean available2 = AvailableInfo.isAvailable(API_NAME_MOVIE_MODE, mode);
        return available2;
    }
}
