package com.sony.imaging.app.digitalfilter.shooting.state;

import android.util.Log;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.NormalCaptureState;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.caution.GFInfo;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.shooting.GFCompositProcess;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFilterSetController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFImageAdjustmentController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFImageSavingController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFLinkAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFThemeController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.digitalfilter.shooting.layout.BlackMuteLayout;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class GFNormalCaptureState extends NormalCaptureState implements NotificationListener {
    private static final String NEXT_ADJUST_STATE = "Adjustment";
    private static final String NEXT_PROCESSING_STATE = "Development";
    private static final String TAG = AppLog.getClassName();
    private static boolean isOpenedProcessingLayout = false;
    private static final String[] TAGS = {GFConstants.REQUEST_ADJUSTMENT, GFConstants.REQUEST_AUTOREVIEW, GFConstants.START_PROCESSING, GFConstants.END_PROCESSING, CameraNotificationManager.FLASH_CHANGE, CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED, CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED, CameraNotificationManager.DEVICE_INTERNAL_FLASH_ONOFF, CameraNotificationManager.DEVICE_EXTERNAL_FLASH_ONOFF};

    @Override // com.sony.imaging.app.base.shooting.NormalCaptureState, com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        if (GFImageSavingController.COMPOSIT.equalsIgnoreCase(GFImageSavingController.getInstance().getValue())) {
            GFCommonUtil.getInstance().setVirtualMediaIds();
            GFCommonUtil.getInstance().setActualMediaStatus(false);
        } else {
            GFCommonUtil.getInstance().setActualMediaStatus(true);
        }
        CameraNotificationManager.getInstance().setNotificationListener(this);
        super.onResume();
        isOpenedProcessingLayout = false;
        openLayout(BlackMuteLayout.TAG);
    }

    @Override // com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.info(TAG, "isOpenedProcessingLayout: " + isOpenedProcessingLayout);
        int runStatus = RunStatus.getStatus();
        if (isOpenedProcessingLayout && 2 == runStatus && GFCompositProcess.canStoreCompositImageByCaputureState) {
            AppLog.info(TAG, "storeCompositImage() by PULLINGBACK");
            GFCompositProcess.storeCompositImage();
        }
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        closeLayout("ProcessingLayout");
        closeLayout(BlackMuteLayout.TAG);
        GFCommonUtil.getInstance().setInvalidShutter(true);
        super.onPause();
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        int currentId;
        Log.i(TAG, "onNotify tag = " + tag);
        if (GFConstants.REQUEST_ADJUSTMENT.equalsIgnoreCase(tag)) {
            setNextState(NEXT_ADJUST_STATE, null);
            return;
        }
        if (GFConstants.REQUEST_AUTOREVIEW.equals(tag)) {
            setNextState(NEXT_PROCESSING_STATE, null);
            return;
        }
        if (GFConstants.START_PROCESSING.equals(tag)) {
            closeLayout(StateBase.DEFAULT_LAYOUT);
            isOpenedProcessingLayout = true;
            openLayout("ProcessingLayout");
            return;
        }
        if (GFConstants.END_PROCESSING.equals(tag)) {
            closeLayout("ProcessingLayout");
            boolean isSkipAdjustment = GFImageAdjustmentController.ON.equalsIgnoreCase(GFImageAdjustmentController.getInstance().getValue(GFImageAdjustmentController.ADJUSTMENT));
            boolean isEffectImageOnly = GFImageSavingController.COMPOSIT.equalsIgnoreCase(GFImageSavingController.getInstance().getValue(GFImageSavingController.SAVE));
            if (!isSkipAdjustment || !isEffectImageOnly) {
                isOpenedProcessingLayout = false;
                return;
            }
            return;
        }
        if (CameraNotificationManager.FLASH_CHANGE.equals(tag) || CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED.equals(tag) || CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED.equals(tag)) {
            if (!GFWhiteBalanceController.getInstance().isSupportedABGM()) {
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
                    if (isBaseAWB || isFilterAWB || isLayer3AWB) {
                        CameraNotificationManager.getInstance().requestNotify(GFConstants.CANCELTAKEPICTURE);
                        CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_AWB_WITH_FLASH);
                    }
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
                        if (GFEEAreaController.getInstance().isLand() && !GFCommonUtil.getInstance().isLayerSetting()) {
                            GFWhiteBalanceController.getInstance().setValue(WhiteBalanceController.WHITEBALANCE, WhiteBalanceController.FLASH);
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
                        if (GFEEAreaController.getInstance().isSky() && !GFCommonUtil.getInstance().isLayerSetting()) {
                            GFWhiteBalanceController.getInstance().setValue(WhiteBalanceController.WHITEBALANCE, WhiteBalanceController.FLASH);
                        }
                    }
                    if (isLayer3AWB) {
                        param.setWBMode(2, WhiteBalanceController.FLASH);
                        if (isLayer3Link && !isSkyLink && !isLandLink) {
                            String wbOption3 = GFBackUpKey.getInstance().getWBOption(WhiteBalanceController.FLASH, 2, theme);
                            GFBackUpKey.getInstance().setCommonWB(WhiteBalanceController.FLASH, theme);
                            GFBackUpKey.getInstance().setCommonWBOption(wbOption3, theme);
                        }
                        if (GFEEAreaController.getInstance().isLayer3() && !GFCommonUtil.getInstance().isLayerSetting()) {
                            GFWhiteBalanceController.getInstance().setValue(WhiteBalanceController.WHITEBALANCE, WhiteBalanceController.FLASH);
                        }
                    }
                    if (isBaseAWB || isFilterAWB || isLayer3AWB) {
                        GFBackUpKey.getInstance().saveLastParameters(param.flatten());
                        CautionUtilityClass.cautionData data = CautionUtilityClass.getInstance().getCurrentCautionData();
                        boolean showCaution = true;
                        if (data != null && ((currentId = data.maxPriorityId) == 131078 || currentId == 131079)) {
                            showCaution = false;
                        }
                        if (showCaution) {
                            CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_AWB_WITH_FLASH);
                            CameraNotificationManager.getInstance().requestNotify(GFConstants.CHANGE_AWB_BY_FLASH);
                            return;
                        }
                        return;
                    }
                    return;
                }
                return;
            }
            return;
        }
        if (tag.equalsIgnoreCase(CameraNotificationManager.DEVICE_INTERNAL_FLASH_ONOFF) || tag.equalsIgnoreCase(CameraNotificationManager.DEVICE_EXTERNAL_FLASH_ONOFF)) {
            GFCommonUtil.getInstance().setFlashMode();
        }
    }
}
