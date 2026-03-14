package com.sony.imaging.app.portraitbeauty.menu.controller;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class PortraitBeautySoftSkinController extends AbstractController {
    public static final String SOFTSKIN = "SoftSkin";
    public static final String SOFTSKIN_CHANGE = "SoftSkinChange";
    public static final String SOFTSKIN_HIGH = "High";
    public static final String SOFTSKIN_LOW = "Low";
    public static final String SOFTSKIN_MID = "Mid";
    public static final String SOFTSKIN_OFF = "Off";
    private static final String TAG = AppLog.getClassName();
    private static PortraitBeautySoftSkinController mInstance = null;
    private String mSelectedLevel = SOFTSKIN_MID;
    private List<String> mSupportedValue;

    public static PortraitBeautySoftSkinController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = new PortraitBeautySoftSkinController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private PortraitBeautySoftSkinController() {
        this.mSupportedValue = null;
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mSupportedValue = new ArrayList();
        this.mSupportedValue.add(SOFTSKIN);
        this.mSupportedValue.add("Off");
        this.mSupportedValue.add(SOFTSKIN_LOW);
        this.mSupportedValue.add(SOFTSKIN_MID);
        this.mSupportedValue.add(SOFTSKIN_HIGH);
        this.mSupportedValue.add("ApplicationSettings");
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

    public int convValue_String2Int(String softskinlevel) {
        if (SOFTSKIN_HIGH.equals(softskinlevel)) {
            return 4;
        }
        if (SOFTSKIN_MID.equals(softskinlevel)) {
            return 3;
        }
        if (SOFTSKIN_LOW.equals(softskinlevel)) {
            return 2;
        }
        return 1;
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
