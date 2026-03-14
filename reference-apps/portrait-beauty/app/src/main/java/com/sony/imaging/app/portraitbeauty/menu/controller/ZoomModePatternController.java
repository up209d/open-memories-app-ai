package com.sony.imaging.app.portraitbeauty.menu.controller;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ZoomModePatternController extends AbstractController {
    public static final String PATTERN = "Pattern";
    public static final String PATTERN_1 = "Pattern1";
    public static final String PATTERN_2 = "Pattern2";
    public static final String PATTERN_3 = "Pattern3";
    public static final String PATTERN_4 = "Pattern4";
    public static final String PATTERN_5 = "Pattern5";
    public static final String PATTERN_6 = "Pattern6";
    public static final String PATTERN_7 = "Pattern7";
    public static final String PATTERN_CHANGE = "ZoomModeChange";
    private static final String TAG = AppLog.getClassName();
    private static ZoomModePatternController mInstance = null;
    private String mSelectedLevel = PATTERN_3;
    private List<String> mSupportedValue;

    public static ZoomModePatternController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = new ZoomModePatternController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private ZoomModePatternController() {
        this.mSupportedValue = null;
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mSupportedValue = new ArrayList();
        this.mSupportedValue.add(PATTERN);
        this.mSupportedValue.add(PATTERN_1);
        this.mSupportedValue.add(PATTERN_2);
        this.mSupportedValue.add(PATTERN_3);
        this.mSupportedValue.add(PATTERN_4);
        this.mSupportedValue.add(PATTERN_5);
        this.mSupportedValue.add(PATTERN_6);
        this.mSupportedValue.add(PATTERN_7);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mSelectedLevel = value;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mSelectedLevel;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return this.mSupportedValue;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return this.mSupportedValue;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return true;
    }
}
