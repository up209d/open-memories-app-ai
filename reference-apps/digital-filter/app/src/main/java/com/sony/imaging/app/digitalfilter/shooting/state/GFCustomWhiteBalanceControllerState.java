package com.sony.imaging.app.digitalfilter.shooting.state;

import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceControllerState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.caution.GFInfo;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFilterSetController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFLinkAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFThemeController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class GFCustomWhiteBalanceControllerState extends CustomWhiteBalanceControllerState implements NotificationListener {
    private static final String[] TAGS = {CameraNotificationManager.FLASH_CHANGE, CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED, CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED};

    @Override // com.sony.imaging.app.base.shooting.CustomWhiteBalanceControllerState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (!GFWhiteBalanceController.getInstance().isSupportedABGM()) {
            CameraNotificationManager.getInstance().setNotificationListener(this);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.CustomWhiteBalanceControllerState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (!GFWhiteBalanceController.getInstance().isSupportedABGM()) {
            CameraNotificationManager.getInstance().removeNotificationListener(this);
        }
        super.onPause();
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equalsIgnoreCase(CameraNotificationManager.FLASH_CHANGE) || tag.equalsIgnoreCase(CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED) || tag.equalsIgnoreCase(CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED)) {
            String theme = GFThemeController.getInstance().getValue();
            GFEffectParameters.Parameters param = GFEffectParameters.getInstance().getParameters();
            String baseWBmode = param.getWBMode(0);
            String filterWBmode = param.getWBMode(1);
            String layer3WBmode = param.getWBMode(2);
            boolean isBaseAWB = "auto".equalsIgnoreCase(baseWBmode) || WhiteBalanceController.UNDERWATER_AUTO.equalsIgnoreCase(baseWBmode);
            boolean isFilterAWB = "auto".equalsIgnoreCase(filterWBmode) || WhiteBalanceController.UNDERWATER_AUTO.equalsIgnoreCase(filterWBmode);
            boolean isLayer3AWB = "auto".equalsIgnoreCase(layer3WBmode) || WhiteBalanceController.UNDERWATER_AUTO.equalsIgnoreCase(layer3WBmode);
            if (!GFFilterSetController.getInstance().need3rdShooting()) {
                isLayer3AWB = false;
            }
            boolean isLandLink = GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_WB);
            boolean isSkyLink = GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_WB);
            boolean isLayer3Link = GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAYER3_WB);
            if (GFCommonUtil.getInstance().isEnableFlash()) {
                if (isBaseAWB) {
                    param.setWBMode(0, WhiteBalanceController.FLASH);
                    if (isLandLink) {
                        String wbOption = GFBackUpKey.getInstance().getWBOption(WhiteBalanceController.FLASH, 0, theme);
                        GFBackUpKey.getInstance().setCommonWB(WhiteBalanceController.FLASH, theme);
                        GFBackUpKey.getInstance().setCommonWBOption(wbOption, theme);
                        if (isSkyLink) {
                            GFBackUpKey.getInstance().saveWBOption(WhiteBalanceController.FLASH, wbOption, 1, theme);
                        }
                        if (isLayer3Link) {
                            GFBackUpKey.getInstance().saveWBOption(WhiteBalanceController.FLASH, wbOption, 2, theme);
                        }
                    }
                }
                if (isFilterAWB) {
                    param.setWBMode(1, WhiteBalanceController.FLASH);
                    if (isSkyLink && !isLandLink) {
                        String wbOption2 = GFBackUpKey.getInstance().getWBOption(WhiteBalanceController.FLASH, 1, theme);
                        GFBackUpKey.getInstance().setCommonWB(WhiteBalanceController.FLASH, theme);
                        GFBackUpKey.getInstance().setCommonWBOption(wbOption2, theme);
                        if (isLayer3Link) {
                            GFBackUpKey.getInstance().saveWBOption(WhiteBalanceController.FLASH, wbOption2, 2, theme);
                        }
                    }
                }
                if (isLayer3AWB) {
                    param.setWBMode(2, WhiteBalanceController.FLASH);
                    if (isLayer3Link && !isSkyLink && !isLandLink) {
                        String wbOption3 = GFBackUpKey.getInstance().getWBOption(WhiteBalanceController.FLASH, 2, theme);
                        GFBackUpKey.getInstance().setCommonWB(WhiteBalanceController.FLASH, theme);
                        GFBackUpKey.getInstance().setCommonWBOption(wbOption3, theme);
                    }
                }
                if (isBaseAWB || isFilterAWB || isLayer3AWB) {
                    GFBackUpKey.getInstance().saveLastParameters(param.flatten());
                    CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_AWB_WITH_FLASH);
                }
            }
        }
    }
}
