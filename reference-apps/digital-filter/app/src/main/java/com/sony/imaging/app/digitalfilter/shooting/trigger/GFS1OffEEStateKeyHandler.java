package com.sony.imaging.app.digitalfilter.shooting.trigger;

import android.hardware.Camera;
import android.os.Bundle;
import android.util.Pair;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.shooting.IUserChanging;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.common.SaUtil;
import com.sony.imaging.app.digitalfilter.menu.layout.GFSettingMenuLayout;
import com.sony.imaging.app.digitalfilter.sa.SFRSA;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.base.GFDisplayModeObserver;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFLinkAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFSelfTimerController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFThemeController;
import com.sony.imaging.app.digitalfilter.shooting.state.GFS1OffEEState;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class GFS1OffEEStateKeyHandler extends S1OffEEStateKeyHandler {
    private static final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedApertureCustomKey() {
        if (!GFCommonUtil.getInstance().hasIrisRing() && GFCommonUtil.getInstance().isFixedAperture() && (GFCommonUtil.getInstance().isAperture() || GFCommonUtil.getInstance().isManual())) {
            GFS1OffEEState.userSettingAperture = false;
            return -1;
        }
        GFS1OffEEState.userSettingAperture = true;
        return super.incrementedApertureCustomKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedApertureCustomKey() {
        if (!GFCommonUtil.getInstance().hasIrisRing() && GFCommonUtil.getInstance().isFixedAperture() && (GFCommonUtil.getInstance().isAperture() || GFCommonUtil.getInstance().isManual())) {
            GFS1OffEEState.userSettingAperture = false;
            return -1;
        }
        GFS1OffEEState.userSettingAperture = true;
        return super.decrementedApertureCustomKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedShutterSpeedCustomKey() {
        GFS1OffEEState.userSettingShutterSpeed = true;
        return super.incrementedShutterSpeedCustomKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedShutterSpeedCustomKey() {
        GFS1OffEEState.userSettingShutterSpeed = true;
        return super.decrementedShutterSpeedCustomKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedProgramShiftCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedProgramShiftCustomKey() {
        return -1;
    }

    private void saveExpComp() {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        Integer index = Integer.valueOf(ExposureCompensationController.getInstance().getExposureCompensationIndex());
        String value = index.toString();
        String theme = GFThemeController.getInstance().getValue();
        if (GFEEAreaController.getInstance().isLand()) {
            params.setExposureComp(0, value);
            if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_EXPCOMP)) {
                GFBackUpKey.getInstance().setCommonExpComp(value, theme);
                params.setExposureComp(1, params.getExposureComp(1));
                params.setExposureComp(2, params.getExposureComp(2));
                return;
            }
            return;
        }
        if (GFEEAreaController.getInstance().isSky()) {
            params.setExposureComp(1, value);
            if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_EXPCOMP)) {
                GFBackUpKey.getInstance().setCommonExpComp(value, theme);
                params.setExposureComp(0, params.getExposureComp(0));
                params.setExposureComp(2, params.getExposureComp(2));
                return;
            }
            return;
        }
        if (GFEEAreaController.getInstance().isLayer3()) {
            params.setExposureComp(2, value);
            if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAYER3_EXPCOMP)) {
                GFBackUpKey.getInstance().setCommonExpComp(value, theme);
                params.setExposureComp(0, params.getExposureComp(0));
                params.setExposureComp(1, params.getExposureComp(1));
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedExposureCompensationCustomKey() {
        ExposureCompensationController.getInstance().incrementExposureCompensation();
        ((IUserChanging) this.target).changeExposure();
        saveExpComp();
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedExposureCompensationCustomKey() {
        ExposureCompensationController.getInstance().decrementExposureCompensation();
        ((IUserChanging) this.target).changeExposure();
        saveExpComp();
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        GFCommonUtil.getInstance().setPrevMenuItemId("Main1");
        return super.pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDispFuncKey() {
        GFDisplayModeObserver.getInstance().toggleDisplayMode(0);
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        AppLog.info(TAG, "S2 but no S1_ON.");
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterKey() {
        if (!chekStatus()) {
            return -1;
        }
        preProcessingIRShutter(false);
        GFCommonUtil.getInstance().pinCalendar();
        return super.pushedIRShutterKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterNotCheckDrivemodeKey() {
        if (!chekStatus()) {
            return -1;
        }
        preProcessingIRShutter(false);
        GFCommonUtil.getInstance().pinCalendar();
        return super.pushedIRShutterNotCheckDrivemodeKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIR2SecKey() {
        if (!chekStatus()) {
            return -1;
        }
        preProcessingIRShutter(true);
        GFCommonUtil.getInstance().pinCalendar();
        return super.pushedIRShutterNotCheckDrivemodeKey();
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        if (CustomizableFunction.ExposureCompensation.equals(func)) {
            int ret = handleExposreCompensationFunction();
            return ret;
        }
        int ret2 = super.onConvertedKeyDown(event, func);
        return ret2;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        if (code == 595 || code == 513) {
            GFS1OffEEState.saveParameters();
            SFRSA.getInstance().terminate();
            GFCommonUtil.getInstance().setPrevMenuItemId(null);
            int retVal = openMenuLayout(GFSettingMenuLayout.getCurrentArea());
            if (GFSettingMenuLayout.getCurrentArea().equals(GFSettingMenuLayout.LAND_ID)) {
                if (!GFEEAreaController.getInstance().isSky()) {
                    GFCommonUtil.getInstance().setFilterCameraSettings(true);
                    return retVal;
                }
                return retVal;
            }
            if (GFSettingMenuLayout.getCurrentArea().equals("SkySettings")) {
                if (!GFEEAreaController.getInstance().isLand()) {
                    GFCommonUtil.getInstance().setBaseCameraSettings(true);
                    return retVal;
                }
                return retVal;
            }
            if (GFSettingMenuLayout.getCurrentArea().equals("Layer3Settings") && !GFEEAreaController.getInstance().isLand()) {
                GFCommonUtil.getInstance().setBaseCameraSettings(true);
                return retVal;
            }
            return retVal;
        }
        if (code == 105 || (code == 108 && SaUtil.isAVIP())) {
            return handleExposreCompensationFunction();
        }
        if (code == 106) {
            return openMenuLayout(ISOSensitivityController.MENU_ITEM_ID_ISO);
        }
        if ((code == 526 || code == 525) && GFCommonUtil.getInstance().hasIrisRing() && GFCommonUtil.getInstance().isAperture()) {
            if (code == 526) {
                decrementedApertureCustomKey();
            } else {
                incrementedApertureCustomKey();
            }
            return 1;
        }
        if ((code == 529 || code == 528) && GFCommonUtil.getInstance().hasIrisRing() && (GFCommonUtil.getInstance().isAperture() || GFCommonUtil.getInstance().isManual())) {
            if (code == 529) {
                decrementedApertureCustomKey();
            } else {
                incrementedApertureCustomKey();
            }
            return 1;
        }
        if (GFCommonUtil.getInstance().isTriggerMfAssist(code)) {
            super.pushedMfAssistCustomKey();
            return 1;
        }
        return super.onKeyDown(keyCode, event);
    }

    private int handleExposreCompensationFunction() {
        if (ExposureModeController.MANUAL_MODE.equalsIgnoreCase(ExposureModeController.getInstance().getValue(ExposureModeController.EXPOSURE_MODE)) && !ISOSensitivityController.ISO_AUTO.equalsIgnoreCase(ISOSensitivityController.getInstance().getValue())) {
            if (SaUtil.isAVIP()) {
                CautionUtilityClass.getInstance().requestTrigger(1470);
            } else {
                CautionUtilityClass.getInstance().requestTrigger(3083);
            }
            return -1;
        }
        int retVal = openMenuLayout("ExposureCompensation");
        return retVal;
    }

    private int openMenuLayout(String layoutID) {
        Bundle bundle = new Bundle();
        bundle.putString(MenuState.ITEM_ID, layoutID);
        openMenu(bundle);
        return 1;
    }

    private boolean chekStatus() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getParameters();
        if (((CameraEx.ParametersModifier) params.second).getRemoteControlMode() && !MediaNotificationManager.getInstance().isError()) {
            int cautionID = GFCommonUtil.getInstance().getCautionId();
            if (cautionID != 0) {
                CautionUtilityClass.getInstance().requestTrigger(cautionID);
                return false;
            }
            return true;
        }
        return false;
    }

    private void preProcessingIRShutter(boolean isIR2SecKey) {
        CameraNotificationManager.OnFocusInfo obj;
        GFCommonUtil.getInstance().setPushedIR2SecKey(isIR2SecKey);
        ExecutorCreator.getInstance().getSequence().autoFocus(null);
        CameraEx.LensInfo info = CameraSetting.getInstance().getLensInfo();
        boolean maybePhaseDiff = info != null && "A-mount".equalsIgnoreCase(info.LensType) && FocusAreaController.PHASE_SHIFT_SENSOR_UNKNOWN.equalsIgnoreCase(info.PhaseShiftSensor) && GFCommonUtil.getInstance().isSupportedVersion(2, 14);
        GFCommonUtil.getInstance().setMaybePhaseDiffFlag(true);
        if (maybePhaseDiff && ((obj = (CameraNotificationManager.OnFocusInfo) CameraNotificationManager.getInstance().getValue(CameraNotificationManager.DONE_AUTO_FOCUS)) == null || obj.status != 1)) {
            AppLog.info(TAG, "maybePhaseDiff is true, so I cannot cancel S2 trigger.(Focus is not locked)");
            GFCommonUtil.getInstance().setMaybePhaseDiffFlag(true);
        }
        if (isIR2SecKey || GFSelfTimerController.SELF_TIMER_ON.equalsIgnoreCase(GFSelfTimerController.getInstance().getValue("drivemode"))) {
            GFCommonUtil.getInstance().setDuringSelfTimer(true);
            CameraNotificationManager.getInstance().requestNotify(GFConstants.START_SELFTIMER);
        }
    }
}
