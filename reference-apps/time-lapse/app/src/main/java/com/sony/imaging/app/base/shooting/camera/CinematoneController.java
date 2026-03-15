package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class CinematoneController extends AbstractController {
    private static final String API_NAME = "setCinemaTone";
    public static final String CINEMA_TONE_INVALID = "invalid";
    public static final String CINEMA_TONE_OFF = "off";
    public static final String CINEMA_TONE_ON1 = "on1";
    public static final String CINEMA_TONE_ON2 = "on2";
    private static final String LOG_MSG_GETCINEMATONEMODE = "getCinemaTone = ";
    private static final String LOG_MSG_GETSUPPORTEDCINEMATONES = "getSupportedCinemaTones = ";
    private static final String LOG_MSG_SETCINEMATONEMODE = "setCinemaTone = ";
    public static final String MENU_ITEM_ID_CINEMATONE = "Cinematone";
    private static final String TAG = "CinematoneController";
    private static CinematoneController mInstance;
    private static List<String> mSupportedCinematone;
    private static List<String> mSupportedCinematoneForMovie;
    private static final String myName;
    CameraSetting mCamSet = CameraSetting.getInstance();
    private static HashMap<String, String> app2pf = new HashMap<>();
    private static HashMap<String, String> pf2app = new HashMap<>();

    static {
        app2pf.put("off", "off");
        app2pf.put(CINEMA_TONE_ON1, "on-1");
        app2pf.put(CINEMA_TONE_ON2, "on-2");
        pf2app.put("off", "off");
        pf2app.put("on-1", CINEMA_TONE_ON1);
        pf2app.put("on-2", CINEMA_TONE_ON2);
        pf2app.put(null, "off");
        mSupportedCinematone = null;
        mSupportedCinematoneForMovie = null;
        myName = CinematoneController.class.getSimpleName();
    }

    public static final String getName() {
        return myName;
    }

    public static CinematoneController getInstance() {
        if (mInstance == null) {
            new CinematoneController();
        }
        return mInstance;
    }

    private static void setController(CinematoneController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected CinematoneController() {
        setController(this);
    }

    private List<String> createSupportedValueArray(int mode) {
        List<String> supList = null;
        if (3 <= CameraSetting.getPfApiVersion()) {
            supList = ((CameraEx.ParametersModifier) this.mCamSet.getSupportedParameters(mode).second).getSupportedCinemaTones();
        }
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), LOG_MSG_GETSUPPORTEDCINEMATONES).append(supList);
        Log.i(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        return supList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        String pfValue = app2pf.get(value);
        Pair<Camera.Parameters, CameraEx.ParametersModifier> emptyParam = this.mCamSet.getEmptyParameters();
        ((CameraEx.ParametersModifier) emptyParam.second).setCinemaTone(pfValue);
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), LOG_MSG_SETCINEMATONEMODE).append(value);
        Log.i(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        this.mCamSet.setParameters(emptyParam);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        String appValue = null;
        String pfValue = null;
        if (getSupportedValueList(2) != null) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> param = this.mCamSet.getParameters();
            pfValue = ((CameraEx.ParametersModifier) param.second).getCinemaTone();
            String appValue2 = pf2app.get(pfValue);
            appValue = appValue2;
        }
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), LOG_MSG_GETCINEMATONEMODE).append(pfValue);
        Log.i(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        return appValue;
    }

    public List<String> getSupportedValueList(int mode) {
        switch (mode) {
            case 1:
                if (mSupportedCinematone == null) {
                    mSupportedCinematone = createSupportedValueArray(mode);
                }
                List<String> supList = mSupportedCinematone;
                return supList;
            case 2:
                if (mSupportedCinematoneForMovie == null) {
                    mSupportedCinematoneForMovie = createSupportedValueArray(mode);
                }
                List<String> supList2 = mSupportedCinematoneForMovie;
                return supList2;
            default:
                return null;
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        List<String> supList = null;
        if (Environment.isMovieAPISupported()) {
            supList = getSupportedValueList(2);
        }
        int mode = this.mCamSet.getCurrentMode();
        if (supList != null) {
            List<String> currentSupList = getSupportedValueList(mode);
            List<String> ret = new ArrayList<>();
            if (currentSupList != null) {
                for (String pf : supList) {
                    String app = pf2app.get(pf);
                    if (app != null) {
                        ret.add(app);
                    }
                }
                return ret;
            }
            ret.add("off");
            return ret;
        }
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        ArrayList<String> ret = new ArrayList<>();
        int mode = this.mCamSet.getCurrentMode();
        List<String> supList = getSupportedValueList(mode);
        if (supList != null) {
            AvailableInfo.update();
            for (String tmp : supList) {
                if (AvailableInfo.isAvailable(API_NAME, tmp)) {
                    ret.add(tmp);
                }
                String appValue = pf2app.get(tmp);
                ret.add(appValue);
            }
        }
        if (ret.size() == 0) {
            return null;
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        AvailableInfo.update();
        boolean ret = AvailableInfo.isAvailable(API_NAME, null);
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        if (!isMovieMode()) {
            return 0;
        }
        return -2;
    }

    protected boolean isMovieMode() {
        if (!Environment.isMovieAPISupported() || 2 != this.mCamSet.getCurrentMode()) {
            return false;
        }
        return true;
    }
}
