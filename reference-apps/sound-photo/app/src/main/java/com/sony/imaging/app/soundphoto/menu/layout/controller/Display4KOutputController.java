package com.sony.imaging.app.soundphoto.menu.layout.controller;

import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.soundphoto.util.AppLog;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class Display4KOutputController extends AbstractController {
    private static final String TAG = "AutoPlayBackStatusController";
    public static final String UPLOAD_SELECTOR = "ApplicationSettings";
    private static Display4KOutputController mInstance;
    private static ArrayList<String> sSupportedList;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add("ApplicationSettings");
    }

    public static Display4KOutputController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = createInstance();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private static Display4KOutputController createInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = new Display4KOutputController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private Display4KOutputController() {
        getSupportedValue(TAG);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.info(TAG, "setValue  tag = " + tag + "  value =  " + value);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return sSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean isSupported = DisplayModeObserver.getInstance().is4kDeviceAvailable();
        return isSupported;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return sSupportedList;
    }
}
