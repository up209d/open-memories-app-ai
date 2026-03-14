package com.sony.imaging.app.doubleexposure.menu.controller;

import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ThemeSelectionController extends AbstractController {
    public static final String MANUAL = "Manual";
    public static final String MIRROR = "Mirror";
    public static final String ROTATION = "Rotation";
    public static final String SIL = "Sil";
    public static final String SKY = "Sky";
    public static final String SOFTFILTER = "SoftFilter";
    public static final String TEXTURE = "Texture";
    public static final String THEMESELECTION = "ApplicationTop";
    private String mSelectedTheme = SKY;
    private ArrayList<String> mSupportedValue;
    private static final String TAG = AppLog.getClassName();
    private static ThemeSelectionController sInstance = null;

    private ThemeSelectionController() {
        this.mSupportedValue = null;
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mSupportedValue = new ArrayList<>();
        this.mSupportedValue.add(THEMESELECTION);
        this.mSupportedValue.add(SKY);
        this.mSupportedValue.add(SIL);
        this.mSupportedValue.add(TEXTURE);
        this.mSupportedValue.add(SOFTFILTER);
        this.mSupportedValue.add("Rotation");
        this.mSupportedValue.add(MIRROR);
        this.mSupportedValue.add("Manual");
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static ThemeSelectionController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new ThemeSelectionController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mSelectedTheme = value;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mSelectedTheme;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mSupportedValue;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mSupportedValue;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return true;
    }
}
