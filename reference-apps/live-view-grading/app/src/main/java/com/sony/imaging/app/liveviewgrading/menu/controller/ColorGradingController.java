package com.sony.imaging.app.liveviewgrading.menu.controller;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.liveviewgrading.AppLog;
import com.sony.imaging.app.liveviewgrading.ColorGradingConstants;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.sysutil.didep.Kikilog;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ColorGradingController extends AbstractController {
    public static final String GROUP_PRO = "pro";
    public static final String GROUP_STD = "standard";
    public static final String GROUP_XTREME = "xtreme";
    public static final String PRESET_CINEMA_COAST_SIDE_LIGHT = "coast";
    public static final String PRESET_CINEMA_MISTY_BLUE = "misty";
    public static final String PRESET_CINEMA_VELVETY_DEW = "velvet";
    public static final String PRESET_CUSTOM = "custom";
    public static final String PRESET_STD_CLEAR = "clear";
    public static final String PRESET_STD_MONO = "mono";
    public static final String PRESET_STD_VIVID = "vivid";
    public static final String PRESET_XTREME_180 = "180";
    public static final String PRESET_XTREME_BIG_AIR = "bigair";
    public static final String PRESET_XTREME_SNOW_TRICKS = "snow";
    public static final String PRESET_XTREME_SURF_TRIP = "surftrip";
    private BackUpUtil mBackUpUtil;
    private static ColorGradingController mInstance = null;
    private static ArrayList<String> mColorGradingList = null;
    public static String COLORGRADING = "ApplicationTop";
    public static String STANDARD = "standard";
    public static final String GROUP_CINEMA = "cinema";
    public static String CINEMA = GROUP_CINEMA;
    public static String EXTREME = "extreme";
    public static String CLEAR = "clear";
    public static String VIVID = "vivid";
    public static String MONOCHROME = "monochrome";
    public static final String PRESET_STD_BOLD = "bold";
    public static String BOLD = PRESET_STD_BOLD;
    public static String COAST_SIDE_LIGHT = "coast_side_light";
    public static final String PRESET_CINEMA_SILKY = "silky";
    public static String SILKY = PRESET_CINEMA_SILKY;
    public static String MISTY_BLUE = "misty_blue";
    public static String VELVETY_DEW = "velvety_dew";
    public static String _180 = "_180";
    public static String SURF_TRIP = "surf_trip";
    public static String BIG_AIR = "big_air";
    public static String SNOW_TRICKS = "snow_tricks";
    private String TAG = AppLog.getClassName();
    private ArrayList<String> mSupportedgList = null;
    public String mSelectedGroup = STANDARD;
    public String mSelectedGroup1Preset = CLEAR;
    public String mSelectedGroup2Preset = COAST_SIDE_LIGHT;
    public String mSelectedGroup3Preset = _180;
    private boolean isComeFromMenu = false;
    private boolean isShootingScreenOpened = false;
    private boolean isComingFromApplicationSettings = false;
    private boolean isPlayBackKeyPressedOnMenu = false;
    private boolean isRecStopped = true;
    public boolean isPlayKeyInvalid = false;

    public static ColorGradingController getInstance() {
        if (mInstance == null) {
            mInstance = new ColorGradingController();
        }
        return mInstance;
    }

    private ColorGradingController() {
        this.mBackUpUtil = null;
        mColorGradingList = new ArrayList<>();
        mColorGradingList.add(STANDARD);
        mColorGradingList.add(CINEMA);
        mColorGradingList.add(EXTREME);
        this.mBackUpUtil = BackUpUtil.getInstance();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(this.TAG, AppLog.getMethodName() + "TAG ::" + tag + " Value ::" + value);
        setCurrentValue(tag, value);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void setSelectedGroup(String value) {
        if (this.mBackUpUtil != null) {
            this.mBackUpUtil.setPreference(ColorGradingConstants.LAST_SELECTED_GROUP, value);
        }
    }

    public String getLastSelectedGroup() {
        if (this.mBackUpUtil == null) {
            return null;
        }
        String retGroup = this.mBackUpUtil.getPreferenceString(ColorGradingConstants.LAST_SELECTED_GROUP, STANDARD);
        return retGroup;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        String value = getCurrentValue(tag);
        return value;
    }

    private String getCurrentValue(String tag) {
        AppLog.enter(this.TAG, AppLog.getMethodName() + " TAG ::" + tag);
        String retValue = null;
        if (COLORGRADING.equals(tag)) {
            retValue = getLastSelectedGroup();
        } else if (STANDARD.equals(tag)) {
            retValue = getGroup1SelectedPreset();
        } else if (CINEMA.equals(tag)) {
            retValue = getGroup2SelectedPreset();
        } else if (EXTREME.equals(tag)) {
            retValue = getGroup3SelectedPreset();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return retValue;
    }

    private void setGroup1SelectedPreset(String value) {
        if (this.mBackUpUtil != null) {
            this.mBackUpUtil.setPreference(ColorGradingConstants.G1_LAST_SELECTED_PRESET, value);
        }
    }

    public String getGroup1SelectedPreset() {
        if (this.mBackUpUtil == null) {
            return null;
        }
        String retGroup1Preset = this.mBackUpUtil.getPreferenceString(ColorGradingConstants.G1_LAST_SELECTED_PRESET, CLEAR);
        return retGroup1Preset;
    }

    private void setGroup2SelectedPreset(String value) {
        if (this.mBackUpUtil != null) {
            this.mBackUpUtil.setPreference(ColorGradingConstants.G2_LAST_SELECTED_PRESET, value);
        }
    }

    public String getGroup2SelectedPreset() {
        if (this.mBackUpUtil == null) {
            return null;
        }
        String retGroup1Preset = this.mBackUpUtil.getPreferenceString(ColorGradingConstants.G2_LAST_SELECTED_PRESET, COAST_SIDE_LIGHT);
        return retGroup1Preset;
    }

    public boolean isPlayKeyInvalid() {
        return this.isPlayKeyInvalid;
    }

    public void setPlayKeyInvalid(boolean isPlayKeyInvalid) {
        this.isPlayKeyInvalid = isPlayKeyInvalid;
    }

    private void setGroup3SelectedPreset(String value) {
        if (this.mBackUpUtil != null) {
            this.mBackUpUtil.setPreference(ColorGradingConstants.G3_LAST_SELECTED_PRESET, value);
        }
    }

    public String getGroup3SelectedPreset() {
        if (this.mBackUpUtil == null) {
            return null;
        }
        String retGroup1Preset = this.mBackUpUtil.getPreferenceString(ColorGradingConstants.G3_LAST_SELECTED_PRESET, _180);
        return retGroup1Preset;
    }

    private void setCurrentValue(String tag, String value) {
        AppLog.enter(this.TAG, AppLog.getMethodName() + " TAG ::" + tag + " Value :: " + value);
        if (COLORGRADING.equals(tag)) {
            setSelectedGroup(value);
        } else if (STANDARD.equals(tag)) {
            setGroup1SelectedPreset(value);
        } else if (CINEMA.equals(tag)) {
            setGroup2SelectedPreset(value);
        } else if (EXTREME.equals(tag)) {
            setGroup3SelectedPreset(value);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.info(this.TAG, AppLog.getMethodName() + "TAG :::" + tag);
        if (!tag.equals(COLORGRADING)) {
            this.mSupportedgList = (ArrayList) getSupportedListItems(tag);
        } else {
            this.mSupportedgList = mColorGradingList;
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mSupportedgList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.info(this.TAG, AppLog.getMethodName() + "TAG :::" + tag);
        if (!tag.equals(COLORGRADING)) {
            this.mSupportedgList = (ArrayList) getSupportedListItems(tag);
        } else {
            this.mSupportedgList = mColorGradingList;
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mSupportedgList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return true;
    }

    private List<String> getSupportedListItems(String tag) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        List<String> list = new ArrayList<>();
        if (tag.equals(STANDARD)) {
            list.add(CLEAR);
            list.add(VIVID);
            list.add(MONOCHROME);
            list.add(BOLD);
        } else if (tag.equals(CINEMA)) {
            list.add(COAST_SIDE_LIGHT);
            list.add(SILKY);
            list.add(MISTY_BLUE);
            list.add(VELVETY_DEW);
        } else if (tag.equals(EXTREME)) {
            list.add(_180);
            list.add(SURF_TRIP);
            list.add(BIG_AIR);
            list.add(SNOW_TRICKS);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName() + " List ::" + list);
        return list;
    }

    public boolean isShootingScreenOpened() {
        return this.isShootingScreenOpened;
    }

    public void setShootingScreenOpened(boolean isShootingScreenOpened) {
        this.isShootingScreenOpened = isShootingScreenOpened;
    }

    public boolean isComingFromApplicationSettings() {
        return this.isComingFromApplicationSettings;
    }

    public void setComingFromApplicationSettings(boolean isComingFromApplicationSettings) {
        this.isComingFromApplicationSettings = isComingFromApplicationSettings;
    }

    public boolean isPlayBackKeyPressedOnMenu() {
        return this.isPlayBackKeyPressedOnMenu;
    }

    public void setPlayBackKeyPressedOnMenu(boolean isPlayBackKeyPressedOnMenu) {
        this.isPlayBackKeyPressedOnMenu = isPlayBackKeyPressedOnMenu;
    }

    public boolean isComeFromMenu() {
        return this.isComeFromMenu;
    }

    public void setComeFromMenu(boolean isComeFromMenu) {
        this.isComeFromMenu = isComeFromMenu;
    }

    public void startKikiLog(int kikilogId) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 32;
        AppLog.info(this.TAG, "kikilogStart");
        Kikilog.setUserLog(kikilogId, options);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    public boolean isRecStopped() {
        return this.isRecStopped;
    }

    public void setRecStopped(boolean isRecStopped) {
        this.isRecStopped = isRecStopped;
    }
}
