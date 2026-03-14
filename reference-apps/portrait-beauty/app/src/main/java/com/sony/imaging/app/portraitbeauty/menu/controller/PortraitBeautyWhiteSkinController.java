package com.sony.imaging.app.portraitbeauty.menu.controller;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class PortraitBeautyWhiteSkinController extends AbstractController {
    public static final int DEFAULT_WHITE_SKIN_VALUE = 4;
    public static final String Level_1 = "Level1";
    public static final String Level_2 = "Level2";
    public static final String Level_3 = "Level3";
    public static final String Level_4 = "Level4";
    public static final String Level_5 = "Level5";
    public static final String Level_6 = "Level6";
    public static final String Level_7 = "Level7";
    private static final String TAG = "PortraitBeautyWhiteSkinController";
    public static final String TAG_SELECTED_WHITE_SKIN_LEVEL = "TAG_SELECTED_WHITE_SKIN_LEVEL";
    private static PortraitBeautyWhiteSkinController mInstance = null;
    public static final String off = "off";
    private String mSelectedLevel = Level_4;
    private ArrayList<String> mSupportedValue;

    protected PortraitBeautyWhiteSkinController() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mSupportedValue = new ArrayList<>();
        this.mSupportedValue.add(Level_1);
        this.mSupportedValue.add(Level_2);
        this.mSupportedValue.add(Level_3);
        this.mSupportedValue.add(Level_4);
        this.mSupportedValue.add(Level_5);
        this.mSupportedValue.add(Level_6);
        this.mSupportedValue.add(Level_7);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static PortraitBeautyWhiteSkinController getInstance() {
        if (mInstance == null) {
            mInstance = new PortraitBeautyWhiteSkinController();
        }
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
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
