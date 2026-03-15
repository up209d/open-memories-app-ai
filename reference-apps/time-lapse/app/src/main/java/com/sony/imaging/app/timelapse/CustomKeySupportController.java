package com.sony.imaging.app.timelapse;

import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.timelapse.caution.TimelapseInfo;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.shooting.controller.TLShootModeSettingController;
import com.sony.imaging.app.util.Environment;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class CustomKeySupportController {
    private static CustomKeySupportController mInstance;
    private final String TAG = "CustomKeySupportController";
    private static String mPitureQuality = TimeLapseConstants.API_NAME;
    private static ArrayList<String> sSupportedStandardMenu = null;
    private static ArrayList<String> sSupportedMiniatureMenu = null;
    private static ArrayList<String> sSupportedCustomMenu = null;
    private static ArrayList<String> sSupportedStillModeCustomMenu = null;
    private static ArrayList<String> sSupportedStillmovieModeCustomMenu = null;

    public static CustomKeySupportController getInstance() {
        if (mInstance == null) {
            createInstance();
        }
        return mInstance;
    }

    private static CustomKeySupportController createInstance() {
        if (mInstance == null) {
            mInstance = new CustomKeySupportController();
            initializeMenuList();
        }
        return mInstance;
    }

    private static void initializeMenuList() {
        sSupportedStandardMenu = new ArrayList<>();
        sSupportedMiniatureMenu = new ArrayList<>();
        sSupportedCustomMenu = new ArrayList<>();
        sSupportedStillModeCustomMenu = new ArrayList<>();
        sSupportedStillmovieModeCustomMenu = new ArrayList<>();
        sSupportedStandardMenu.add("drivemode");
        sSupportedStandardMenu.add(ExposureModeController.EXPOSURE_MODE);
        sSupportedStandardMenu.add(FocusModeController.TAG_FOCUS_MODE);
        sSupportedStandardMenu.add(FocusAreaController.TAG_FOCUS_AREA);
        sSupportedStandardMenu.add("ExposureCompensation");
        sSupportedCustomMenu.add(ISOSensitivityController.MENU_ITEM_ID_ISO);
        sSupportedCustomMenu.add(CreativeStyleController.CREATIVESTYLE);
        sSupportedCustomMenu.add(WhiteBalanceController.WHITEBALANCE);
        sSupportedCustomMenu.add("PictureEffect");
        sSupportedMiniatureMenu.add("PictureEffect");
        sSupportedStillModeCustomMenu.add(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE);
        sSupportedStillModeCustomMenu.add(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        sSupportedStillModeCustomMenu.add(mPitureQuality);
        sSupportedStillModeCustomMenu.add(DigitalZoomController.DIGITAL_ZOOM);
        sSupportedStillmovieModeCustomMenu.add(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE);
        sSupportedStillmovieModeCustomMenu.add(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        sSupportedStillmovieModeCustomMenu.add(mPitureQuality);
    }

    private CustomKeySupportController() {
    }

    private boolean isSupportedMPEGMode(String itemeID) {
        AppLog.trace("CustomKeySupportController", "isSupportedMenuForMPEGRecording check for Menu ItemId" + itemeID);
        if (sSupportedStandardMenu.contains(itemeID)) {
            return true;
        }
        switch (TLCommonUtil.getInstance().getCurrentState()) {
            case 5:
                if (!"PictureEffect".equals(itemeID)) {
                    return false;
                }
                return true;
            case 6:
            default:
                return false;
            case 7:
                if (!sSupportedCustomMenu.contains(itemeID)) {
                    return false;
                }
                return true;
        }
    }

    private boolean isSupportedStillMovieMode(String itemeID) {
        AppLog.trace("CustomKeySupportController", "isSupportedMenuForMPEGRecording check for Menu ItemId" + itemeID);
        if (sSupportedStandardMenu.contains(itemeID) || sSupportedStillmovieModeCustomMenu.contains(itemeID)) {
            return true;
        }
        switch (TLCommonUtil.getInstance().getCurrentState()) {
            case 5:
                if (!"PictureEffect".equals(itemeID)) {
                    return false;
                }
                return true;
            case 6:
            default:
                return false;
            case 7:
                if (!sSupportedCustomMenu.contains(itemeID)) {
                    return false;
                }
                return true;
        }
    }

    private boolean isSupportedStillMode(String itemeID) {
        AppLog.trace("CustomKeySupportController", "isSupportedMenuForStillRecording check for Menu ItemId" + itemeID);
        if (sSupportedStandardMenu.contains(itemeID) || sSupportedStillModeCustomMenu.contains(itemeID)) {
            return true;
        }
        switch (TLCommonUtil.getInstance().getCurrentState()) {
            case 5:
                if (!"PictureEffect".equals(itemeID)) {
                    return false;
                }
                return true;
            case 6:
            default:
                return false;
            case 7:
                if (!sSupportedCustomMenu.contains(itemeID)) {
                    return false;
                }
                return true;
        }
    }

    public boolean isSupportedMenu(String menuId) {
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() == 1) {
            boolean isSupportedMenu = isSupportedStillMode(menuId);
            return isSupportedMenu;
        }
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() == 0) {
            boolean isSupportedMenu2 = isSupportedMPEGMode(menuId);
            return isSupportedMenu2;
        }
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() != 2) {
            return false;
        }
        boolean isSupportedMenu3 = isSupportedStillMovieMode(menuId);
        return isSupportedMenu3;
    }

    public int getCautionID(String itemID) {
        if ((TLCommonUtil.getInstance().getCurrentState() == 7 || TLCommonUtil.getInstance().getCurrentState() == 5) && "PictureEffect".equals(itemID)) {
            if (1 == Environment.getVersionOfHW()) {
                return Info.CAUTION_ID_DLAPP_INVALID_FUNCTION;
            }
            return 33570;
        }
        if (itemID == PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE && TLShootModeSettingController.getInstance().getCurrentCaptureState() != 0) {
            if (1 == Environment.getVersionOfHW()) {
                return Info.CAUTION_ID_DLAPP_INVALID_FUNCTION;
            }
            return 33261;
        }
        if (itemID.equals(CreativeStyleController.CREATIVESTYLE) && TLCommonUtil.getInstance().getCurrentState() == 7) {
            if (1 == Environment.getVersionOfHW()) {
                return Info.CAUTION_ID_DLAPP_INVALID_FUNCTION;
            }
            return 33546;
        }
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() == 0 && sSupportedStillModeCustomMenu.contains(itemID)) {
            return TimelapseInfo.CAUTION_ID_DLAPP_INVALID_MOVIE_MODE;
        }
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() != 2 || !DigitalZoomController.DIGITAL_ZOOM.equalsIgnoreCase(itemID)) {
            return TimelapseInfo.CAUTION_ID_DLAPP_INVALID_THEME;
        }
        return TimelapseInfo.CAUTION_ID_DLAPP_INVALID_MOVIE_MODE;
    }
}
