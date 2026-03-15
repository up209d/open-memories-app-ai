package com.sony.imaging.app.startrails.menu.controller;

import android.util.Log;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STConstants;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class MovieController extends AbstractController {
    private static String LAST_SELECTED_TAG = STConstants.MOVIE_1920_1080;
    private static final ArrayList<String> SIZES = new ArrayList<>();
    public static final String TAG = "MovieSize";
    public static MovieController mInstance;

    static {
        SIZES.add(STConstants.MOVIE_1920_1080);
        SIZES.add(STConstants.MOVIE_1280_720);
        SIZES.add(TAG);
    }

    public static MovieController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            LAST_SELECTED_TAG = BackUpUtil.getInstance().getPreferenceString(STConstants.MOVIE_1920_1080, STConstants.MOVIE_1920_1080);
            mInstance = createInstance();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private static MovieController createInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = new MovieController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private MovieController() {
        getSupportedValue(TAG);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, AppLog.getMethodName());
        LAST_SELECTED_TAG = value;
        if (value.equalsIgnoreCase(STConstants.MOVIE_1920_1080)) {
            BackUpUtil.getInstance().setPreference(STConstants.MOVIE_1920_1080, value);
        } else {
            BackUpUtil.getInstance().setPreference(STConstants.MOVIE_1920_1080, value);
        }
        CameraNotificationManager.getInstance().requestNotify(TAG);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        AppLog.enter(TAG, AppLog.getMethodName());
        Log.e(TAG, "MovieController.getValue()");
        if (LAST_SELECTED_TAG.equalsIgnoreCase(STConstants.MOVIE_1920_1080)) {
            AppLog.exit(TAG, AppLog.getMethodName());
            return STConstants.MOVIE_1920_1080;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return STConstants.MOVIE_1280_720;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        Log.e(TAG, "MovieController.getSupportedValue()");
        return SIZES;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        Log.e(TAG, "MovieController.getAvailableValue()");
        return SIZES;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        Log.e(TAG, "MovieController.isAvailable()");
        return true;
    }
}
