package com.sony.imaging.app.startrails.menu.controller;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STBackUpKey;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ThemeSelectionController extends AbstractController {
    public static final String APP_TOP = "ApplicationTop";
    private static final String TAG = "ThemeSelectionController";
    private static ThemeSelectionController mInstance = null;
    private static final String sAppSettings = "ApplicationSettings";
    private static final String sCustom = "customtrails";
    private static final String sDarkNight = "darknighttrails";
    private static ArrayList<String> sSupportedList;
    public static final String BRIGHT_NIGHT = "brightnighttrails";
    private static String sSelectedTrail = BRIGHT_NIGHT;

    public static ThemeSelectionController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            createInstance();
        }
        if (sSupportedList == null) {
            updateList();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private static void updateList() {
        AppLog.trace(TAG, AppLog.getMethodName());
        sSupportedList = new ArrayList<>();
        sSupportedList.add(APP_TOP);
        sSupportedList.add(sAppSettings);
        sSupportedList.add(sDarkNight);
        sSupportedList.add(BRIGHT_NIGHT);
        sSupportedList.add(sCustom);
    }

    private static ThemeSelectionController createInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = new ThemeSelectionController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.trace(TAG, AppLog.getMethodName());
        int selectedTrail = -1;
        if (value != null) {
            switch (value.charAt(0)) {
                case 'b':
                    selectedTrail = 0;
                    break;
                case 'c':
                    selectedTrail = 2;
                    break;
                case 'd':
                    selectedTrail = 1;
                    break;
            }
            sSelectedTrail = value;
            if (-1 != selectedTrail) {
                BackUpUtil.getInstance().setPreference(STBackUpKey.SELECTED_THEME, value);
                STUtility.getInstance().setCurrentTrail(selectedTrail);
                STUtility.checkIRISState();
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        AppLog.trace(TAG, AppLog.getMethodName());
        return sSelectedTrail;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        AppLog.trace(TAG, AppLog.getMethodName());
        return sSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        AppLog.trace(TAG, AppLog.getMethodName());
        return sSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        AppLog.trace(TAG, AppLog.getMethodName());
        return true;
    }
}
