package com.sony.imaging.app.timelapse.shooting.controller;

import android.util.Log;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class MovieController extends AbstractController {
    private static final ArrayList<String> SIZES = new ArrayList<>();
    public static final String TAG = "MovieSize";
    public static MovieController mInstance;
    private String LAST_SELECTED_TAG = "MOVIE_1920_1080";

    static {
        SIZES.add("MOVIE_1920_1080");
        SIZES.add("MOVIE_1280_720");
        SIZES.add(TAG);
    }

    public static MovieController getInstance() {
        if (mInstance == null) {
            mInstance = createInstance();
        }
        return mInstance;
    }

    private static MovieController createInstance() {
        if (mInstance == null) {
            mInstance = new MovieController();
        }
        return mInstance;
    }

    private MovieController() {
        getSupportedValue(TAG);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        this.LAST_SELECTED_TAG = value;
        if (value.equalsIgnoreCase("MOVIE_1920_1080")) {
            BackUpUtil.getInstance().setPreference("MOVIE_1920_1080", value);
        } else {
            BackUpUtil.getInstance().setPreference("MOVIE_1920_1080", value);
        }
        CameraNotificationManager.getInstance().requestNotify(TAG);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        Log.e(TAG, "MovieController.getValue()");
        return this.LAST_SELECTED_TAG.equalsIgnoreCase("MOVIE_1920_1080") ? "MOVIE_1920_1080" : "MOVIE_1280_720";
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
        return TLShootModeSettingController.getInstance().getCurrentCaptureState() != 1;
    }
}
