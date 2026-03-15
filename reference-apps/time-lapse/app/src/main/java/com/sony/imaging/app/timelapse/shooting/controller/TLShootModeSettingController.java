package com.sony.imaging.app.timelapse.shooting.controller;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class TLShootModeSettingController extends AbstractController {
    public static final String APPLICATION_SETTINGS = "ApplicationSettings";
    public static final String BACKUP_ID_CLOUDY_SKY = "CloudySky";
    public static final String BACKUP_ID_CUSTOM = "CustomTheme";
    public static final String BACKUP_ID_MINIATURE = "Miniature";
    public static final String BACKUP_ID_NIGHTY_SCENE = "NightScene";
    public static final String BACKUP_ID_NIGHTY_SKY = "NightySky";
    public static final String BACKUP_ID_STANDARD = "Standard";
    public static final String BACKUP_ID_SUN_RISE = "SunRise";
    public static final String BACKUP_ID_SUN_SET = "SunSet";
    private static final String TAG = "TLShootModeSettingController";
    public static final int TIME_LAPSE_MOVIE = 0;
    public static final int TIME_LAPSE_STILL = 1;
    public static final int TIME_LAPSE_STILL_MOVIE = 2;
    private static TLShootModeSettingController mInstance;
    private static ArrayList<String> sSupportedList;
    private static ArrayList<String> sSupportedListLite;
    private int captureState;
    private String[] mShootModeArray;
    private String[] mThemeArray;
    public List<String> mThemeList;

    public String getShootModeString(int index) {
        return this.mShootModeArray[index];
    }

    public int getCurrentCaptureState() {
        return this.captureState;
    }

    public boolean isStillMovieShootMode() {
        return 2 == this.captureState;
    }

    public void setCurrentCaptureState(int currentState) {
        BackUpUtil.getInstance().setPreference(TimeLapseConstants.TIMELAPSE_CURRENT_CAPTURE_STATE_KEY, Integer.valueOf(currentState));
        this.captureState = currentState;
    }

    public static TLShootModeSettingController getInstance() {
        if (mInstance == null) {
            mInstance = createInstance();
        }
        return mInstance;
    }

    private static TLShootModeSettingController createInstance() {
        if (mInstance == null) {
            mInstance = new TLShootModeSettingController();
        }
        return mInstance;
    }

    private TLShootModeSettingController() {
        this.captureState = 0;
        this.mThemeArray = null;
        this.mThemeList = null;
        this.mShootModeArray = null;
        AppLog.enter(TAG, "init block executing started");
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add(BACKUP_ID_CLOUDY_SKY);
        sSupportedList.add(BACKUP_ID_NIGHTY_SKY);
        sSupportedList.add(BACKUP_ID_NIGHTY_SCENE);
        sSupportedList.add(BACKUP_ID_SUN_RISE);
        sSupportedList.add(BACKUP_ID_SUN_SET);
        sSupportedList.add(BACKUP_ID_MINIATURE);
        sSupportedList.add(BACKUP_ID_CUSTOM);
        sSupportedList.add(BACKUP_ID_STANDARD);
        sSupportedList.add(TimeLapseConstants.APPTOPLAYOUT);
        sSupportedList.add(APPLICATION_SETTINGS);
        AppLog.enter(TAG, "init block executing sSupportedList created");
        if (sSupportedListLite == null) {
            sSupportedListLite = new ArrayList<>();
        }
        sSupportedListLite.add(BACKUP_ID_STANDARD);
        sSupportedListLite.add(TimeLapseConstants.APPTOPLAYOUT);
        sSupportedListLite.add(APPLICATION_SETTINGS);
        AppLog.enter(TAG, "init block executing sSupportedListLite created");
        this.mShootModeArray = new String[]{"MOVIE_FORMAT", TimeLapseConstants.TIME_LAPSE_FILE_FORMAT_STILL, TimeLapseConstants.TIME_LAPSE_FILE_FORMAT_STILL_MOVIE};
        this.mThemeArray = new String[]{BACKUP_ID_CLOUDY_SKY, BACKUP_ID_NIGHTY_SKY, BACKUP_ID_NIGHTY_SCENE, BACKUP_ID_SUN_SET, BACKUP_ID_SUN_RISE, BACKUP_ID_MINIATURE, BACKUP_ID_STANDARD, BACKUP_ID_CUSTOM};
        AppLog.enter(TAG, "init block executing mThemeArray created");
        this.mThemeList = Arrays.asList(this.mThemeArray);
        AppLog.enter(TAG, "init block executing mThemeList created");
        this.captureState = BackUpUtil.getInstance().getPreferenceInt(TimeLapseConstants.TIMELAPSE_CURRENT_CAPTURE_STATE_KEY, 0);
        AppLog.enter(TAG, "init block executing completed");
        AppLog.enter(TAG, "TLShootModeSettingController()  executing started");
        setRecordingFactorIds(this.captureState);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return getListofItem();
    }

    private List<String> getListofItem() {
        return TLCommonUtil.getInstance().isTimelapseLiteApplication() ? sSupportedListLite : sSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        String value = this.mThemeArray[TLCommonUtil.getInstance().getCurrentState()];
        if (TLCommonUtil.getInstance().isTimelapseLiteApplication()) {
            return BACKUP_ID_STANDARD;
        }
        return value;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return getListofItem();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        if (tag == null || !TLCommonUtil.getInstance().isTimelapseLiteApplication() || tag.equals(BACKUP_ID_STANDARD) || tag.equals(TimeLapseConstants.APPTOPLAYOUT) || tag.equals(APPLICATION_SETTINGS)) {
            return true;
        }
        return false;
    }

    public void setCaptureState(int captureState) {
        BackUpUtil.getInstance().setPreference(TimeLapseConstants.TIMELAPSE_CURRENT_CAPTURE_STATE_KEY, Integer.valueOf(captureState));
        setCurrentCaptureState(captureState);
        setRecordingFactorIds(captureState);
        TLPictureQualityController.getInstance().validateRecordingModeExclusion(isStillMovieShootMode());
    }

    public void setRecordingFactorIds(int recMode) {
        AvailableInfo.setFactor("INH_FACTOR_RECMODE_MOVIE", false);
        AvailableInfo.setFactor("INH_FACTOR_RECMODE_STILL", false);
        if (recMode == 1) {
            AvailableInfo.setFactor("INH_FACTOR_RECMODE_STILL", true);
        } else if (recMode == 0) {
            AvailableInfo.setFactor("INH_FACTOR_RECMODE_MOVIE", true);
        }
    }

    public void setMiniatureOptionValue(int captureState) {
        TimelapsePictureEffectController controller = TimelapsePictureEffectController.getInstance();
        String option = controller.getValue(PictureEffectController.MODE_MINIATURE);
        if (captureState != 1 && "auto".equals(option)) {
            if (TLCommonUtil.getInstance().getCurrentState() == 7) {
                String miniatureOption = BackUpUtil.getInstance().getPreferenceString(TimeLapseConstants.LASTSELECTEDITEMID_MINIATURE_OPTION_IN_CUSTOM_THEME, "hcenter");
                controller.setThemeValue(PictureEffectController.MODE_MINIATURE, miniatureOption);
                BackUpUtil.getInstance().setPreference(TimeLapseConstants.IS_INHERITED_MINIATURE_AUTO_CUSTOM, true);
                AppLog.info(TAG, "setMiniatureOption(A1): " + miniatureOption);
                return;
            }
            if (TLCommonUtil.getInstance().getCurrentState() == 5) {
                String miniatureOption2 = BackUpUtil.getInstance().getPreferenceString(TimeLapseConstants.LASTSELECTEDITEMID_MINIATURE_OPTION_IN_MINIATURE_THEME, "hcenter");
                controller.setThemeValue(PictureEffectController.MODE_MINIATURE, miniatureOption2);
                BackUpUtil.getInstance().setPreference(TimeLapseConstants.MINIATURE_PICTURE_EFFECT_KEY, miniatureOption2);
                BackUpUtil.getInstance().setPreference(TimeLapseConstants.IS_INHERITED_MINIATURE_AUTO_MINIATURE, true);
                AppLog.info(TAG, "setMiniatureOption(A2): " + miniatureOption2);
            }
        }
    }
}
