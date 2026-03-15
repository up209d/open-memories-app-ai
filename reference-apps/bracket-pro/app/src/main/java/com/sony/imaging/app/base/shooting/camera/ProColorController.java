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
import java.util.List;

/* loaded from: classes.dex */
public class ProColorController extends AbstractController {
    private static final String API_NAME = "setProColorMode";
    private static final String LOG_MSG_GETPROCOLORMODE = "getProColorMode = ";
    private static final String LOG_MSG_GETSUPPORTEDPROCOLORMODES = "getSupportedProColorModes = ";
    private static final String LOG_MSG_SETPROCOLORMODE = "setProColorMode = ";
    public static final String MENU_ITEM_ID_PRO_COLOR = "ProColor";
    public static final String PRO_COLOR_MODE_NEUTRAL = "pro-neutral";
    public static final String PRO_COLOR_MODE_VIVID = "pro-vivid";
    private static final String TAG = "ProColorController";
    private static ProColorController mInstance;
    private static List<String> mSupportedMovieColorForStill = null;
    private static List<String> mSupportedMovieColorForMovie = null;
    private static final String myName = ProColorController.class.getSimpleName();
    private static final String[] tags = {CameraNotificationManager.PRO_COLOR_MODE_CHANGED};
    private NotificationListener mListener = new MovieColorListener();
    CameraSetting mCamSet = CameraSetting.getInstance();

    public static final String getName() {
        return myName;
    }

    public static ProColorController getInstance() {
        if (mInstance == null) {
            new ProColorController();
        }
        return mInstance;
    }

    private static void setController(ProColorController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected ProColorController() {
        setController(this);
    }

    private List<String> createSupportedValueArray(int mode) {
        List<String> supList = null;
        if (9 <= CameraSetting.getPfApiVersion()) {
            supList = ((CameraEx.ParametersModifier) this.mCamSet.getSupportedParameters(mode).second).getSupportedProColorModes();
        }
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), LOG_MSG_GETSUPPORTEDPROCOLORMODES).append(supList);
        Log.i(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        return supList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> emptyParam = this.mCamSet.getEmptyParameters();
        ((CameraEx.ParametersModifier) emptyParam.second).setProColorMode(value);
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), LOG_MSG_SETPROCOLORMODE).append(value);
        Log.i(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        this.mCamSet.setParameters(emptyParam);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        String pfValue = null;
        if (getSupportedValueList(2) != null) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> param = this.mCamSet.getParameters();
            pfValue = ((CameraEx.ParametersModifier) param.second).getProColorMode();
        }
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), LOG_MSG_GETPROCOLORMODE).append(pfValue);
        Log.i(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        return pfValue;
    }

    public List<String> getSupportedValueList(int mode) {
        switch (mode) {
            case 1:
                if (mSupportedMovieColorForStill == null) {
                    mSupportedMovieColorForStill = createSupportedValueArray(mode);
                }
                List<String> supList = mSupportedMovieColorForStill;
                return supList;
            case 2:
                if (mSupportedMovieColorForMovie == null) {
                    mSupportedMovieColorForMovie = createSupportedValueArray(mode);
                }
                List<String> supList2 = mSupportedMovieColorForMovie;
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
                for (String value : supList) {
                    if (value != null) {
                        ret.add(value);
                    }
                }
                return ret;
            }
            return null;
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

    /* loaded from: classes.dex */
    private class MovieColorListener implements NotificationListener {
        private MovieColorListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return ProColorController.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        Log.i(TAG, "onGetInitParameters");
        if (this.mListener != null) {
            CameraNotificationManager.getInstance().setNotificationListener(this.mListener);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetTermParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        if (this.mListener != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mListener);
        }
    }
}
