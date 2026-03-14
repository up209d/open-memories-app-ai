package com.sony.imaging.app.doubleexposure.menu.controller;

import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ModeSelectionController extends AbstractController {
    public static final String ADD = "Add";
    public static final String DARKEN = "Darken";
    public static final String LIGHTEN = "Lighten";
    public static final String MODESELECTION = "ModeSelection";
    public static final String MULTIPLY = "Multiply";
    public static final String SCREEN = "Screen";
    public static final String WEIGHTED = "Weighted";
    private String mSelectedLevel = ADD;
    private ArrayList<String> mSupportedValue;
    private static final String TAG = AppLog.getClassName();
    private static ModeSelectionController sInstance = null;

    private ModeSelectionController() {
        this.mSupportedValue = null;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mSupportedValue == null) {
            this.mSupportedValue = new ArrayList<>();
            this.mSupportedValue.add(MODESELECTION);
            this.mSupportedValue.add(ADD);
            this.mSupportedValue.add(WEIGHTED);
            this.mSupportedValue.add(SCREEN);
            this.mSupportedValue.add(MULTIPLY);
            this.mSupportedValue.add(LIGHTEN);
            this.mSupportedValue.add(DARKEN);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static ModeSelectionController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new ModeSelectionController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mSelectedLevel = value;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mSelectedLevel;
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
