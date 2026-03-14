package com.sony.imaging.app.digitalfilter.shooting.state;

import android.os.Bundle;
import android.view.KeyEvent;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.ShootingMenuState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.digitalfilter.DigitalFilterApp;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.common.AppContext;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.menu.layout.GFAvMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFBorderMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFGuideLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFSetting15LayerTvAvLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFSettingMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFShadingMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFTvMenuLayout;
import com.sony.imaging.app.digitalfilter.sa.GFHistgramTask;
import com.sony.imaging.app.digitalfilter.sa.SFRSA;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class GFShootingMenuState extends ShootingMenuState implements NotificationListener {
    private static final boolean FROM_LAND = true;
    private static final boolean FROM_LAYER3 = false;
    private static final int WAIT_TIME = 700;
    private static final String TAG = AppLog.getClassName();
    private static boolean runningSFRSA = false;
    private static String[] TAGS = {GFConstants.LAND_APPSETTING, GFConstants.SKY_APPSETTING, GFConstants.LAYER3_APPSETTING, GFConstants.UPDATE_APPSETTING, GFConstants.CHANGED_SKY_TO_LAND, GFConstants.CHANGED_LAND_TO_SKY, GFConstants.CHANGED_SKY_TO_THIRD, GFConstants.CHANGED_THIRD_TO_SKY, GFConstants.RELOAD_SKY_IMAGE, GFConstants.RELOAD_LAND_IMAGE, GFConstants.RELOAD_ALL_IMAGE, GFConstants.STOP_APPSETTING, GFConstants.START_BASESETTING, GFConstants.CHANGE_AWB_BY_FLASH, CameraNotificationManager.HISTOGRAM_UPDATE, CameraNotificationManager.FOCAL_LENGTH_CHANGED};
    private static int mHistCmd = SFRSA.COPY_HGF_LAND_TO_SKY;

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        System.gc();
        if (GFEEAreaController.getInstance().isLayer3()) {
            GFCommonUtil.getInstance().setBorderId(1);
        } else {
            GFCommonUtil.getInstance().setBorderId(0);
        }
        GFCommonUtil.getInstance().openMenu();
        GFEffectParameters.Parameters parameter = GFEffectParameters.getInstance().getParameters();
        GFBackUpKey.getInstance().saveLastParameters(parameter.flatten());
        CameraNotificationManager.getInstance().setNotificationListener(this);
        SFRSA.getInstance().terminateAll();
        runningSFRSA = false;
        super.onResume();
        boolean isShownIntroGuide = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_SHOW_INTRODUCTION, false);
        if (!isShownIntroGuide) {
            openLayout("ID_GFINTRODUCTIONLAYOUT");
        }
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (GFCommonUtil.getInstance().getBootFactor() == 0) {
            GFCommonUtil.getInstance().setBootFactor(2);
        }
        GFCommonUtil.getInstance().closeMenu();
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        GFEffectParameters.Parameters parameter = GFEffectParameters.getInstance().getParameters();
        GFBackUpKey.getInstance().saveLastParameters(parameter.flatten());
        GFHistgramTask.getInstance().stopHistgramUpdating();
        stopLiveveiwEffect();
        if (2 != RunStatus.getStatus()) {
            GFCommonUtil.getInstance().setLayerFlag(GFEEAreaController.getInstance().isLand(), GFEEAreaController.getInstance().isSky(), GFEEAreaController.getInstance().isLayer3());
        }
        closeLayout(GFBorderMenuLayout.MENU_ID);
        closeLayout(GFShadingMenuLayout.MENU_ID);
        closeLayout(GFAvMenuLayout.MENU_ID);
        closeLayout(GFTvMenuLayout.MENU_ID);
        closeLayout(GFSettingMenuLayout.MENU_ID);
        closeLayout(GFGuideLayout.ID_GFGUIDELAYOUT);
        closeLayout("ID_GFINTRODUCTIONLAYOUT");
        GFCommonUtil.getInstance().endLayerSetting();
        super.onPause();
        System.gc();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState
    public void finishMenuState(Bundle bundle) {
        if (DigitalFilterApp.mBootFactor == 0) {
            DigitalFilterApp.mBootFactor = 2;
        }
        super.finishMenuState(bundle);
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (GFConstants.LAND_APPSETTING.equalsIgnoreCase(tag)) {
            if (!runningSFRSA) {
                runningSFRSA = true;
                initLandLiveveiwEffect();
                return;
            } else {
                updateLiveveiwEffect();
                return;
            }
        }
        if (GFConstants.SKY_APPSETTING.equalsIgnoreCase(tag)) {
            if (!runningSFRSA) {
                runningSFRSA = true;
                initSkyLiveveiwEffect();
                return;
            } else {
                updateLiveveiwEffect();
                return;
            }
        }
        if (GFConstants.LAYER3_APPSETTING.equalsIgnoreCase(tag)) {
            if (!runningSFRSA) {
                runningSFRSA = true;
                initLayer3LiveveiwEffect();
                return;
            } else {
                updateLiveveiwEffect();
                return;
            }
        }
        if (GFConstants.UPDATE_APPSETTING.equalsIgnoreCase(tag)) {
            if (runningSFRSA) {
                updateLiveveiwEffect();
                return;
            }
            return;
        }
        if (GFConstants.RELOAD_SKY_IMAGE.equalsIgnoreCase(tag)) {
            if (runningSFRSA) {
                restartLandLiveveiwEffect();
                return;
            }
            return;
        }
        if (GFConstants.RELOAD_LAND_IMAGE.equalsIgnoreCase(tag)) {
            if (runningSFRSA) {
                restartSkyLiveveiwEffect();
                return;
            }
            return;
        }
        if (GFConstants.RELOAD_ALL_IMAGE.equalsIgnoreCase(tag)) {
            if (runningSFRSA) {
                restartLayer3LiveveiwEffect();
                return;
            }
            return;
        }
        if (GFConstants.CHANGED_SKY_TO_LAND.equalsIgnoreCase(tag)) {
            if (runningSFRSA) {
                startLandLiveveiwEffect();
                return;
            }
            return;
        }
        if (GFConstants.CHANGED_SKY_TO_THIRD.equalsIgnoreCase(tag)) {
            if (runningSFRSA) {
                startLayer3LiveveiwEffect();
                return;
            }
            return;
        }
        if (GFConstants.CHANGED_LAND_TO_SKY.equalsIgnoreCase(tag)) {
            if (runningSFRSA) {
                startSkyLiveveiwEffect(true);
                return;
            }
            return;
        }
        if (GFConstants.CHANGED_THIRD_TO_SKY.equalsIgnoreCase(tag)) {
            if (runningSFRSA) {
                startSkyLiveveiwEffect(false);
                return;
            }
            return;
        }
        if (GFConstants.STOP_APPSETTING.equalsIgnoreCase(tag)) {
            stopLiveveiwEffect();
            runningSFRSA = false;
            return;
        }
        if (GFConstants.START_BASESETTING.equalsIgnoreCase(tag)) {
            runningSFRSA = true;
            return;
        }
        if (CameraNotificationManager.HISTOGRAM_UPDATE.equalsIgnoreCase(tag)) {
            CameraEx cameraEx = CameraSetting.getInstance().getCamera();
            if (cameraEx != null) {
                AppLog.info(TAG, "inhibits listener-NOT-found histoupdate message.");
                cameraEx.setPreviewAnalizeListener((CameraEx.PreviewAnalizeListener) null);
                return;
            }
            return;
        }
        if (CameraNotificationManager.FOCAL_LENGTH_CHANGED.equalsIgnoreCase(tag)) {
            if (GFAvMenuLayout.isAvSetting() || GFSetting15LayerTvAvLayout.isAvSetting() || GFCommonUtil.getInstance().isFixedAperture()) {
                return;
            }
            if (GFCommonUtil.getInstance().isAperture() || GFCommonUtil.getInstance().isManual()) {
                if (GFCommonUtil.getInstance().isLand()) {
                    GFCommonUtil.getInstance().setApertureRetry(0);
                    return;
                } else if (GFCommonUtil.getInstance().isSky()) {
                    GFCommonUtil.getInstance().setApertureRetry(1);
                    return;
                } else {
                    if (GFCommonUtil.getInstance().isLayer3()) {
                        GFCommonUtil.getInstance().setApertureRetry(2);
                        return;
                    }
                    return;
                }
            }
            return;
        }
        if (tag.equals(GFConstants.CHANGE_AWB_BY_FLASH)) {
            setNextState(S1OffEEState.STATE_NAME, null);
        }
    }

    private void waitCameraSetting(int waitTime) {
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initLandLiveveiwEffect() {
        SFRSA.getInstance().setPackageName(AppContext.getAppContext().getPackageName());
        SFRSA.getInstance().initialize();
        mHistCmd = SFRSA.COPY_HGF_SKY_TO_LAND;
        SFRSA.getInstance().setCommand(mHistCmd);
        SFRSA.getInstance().startLiveViewEffect(true);
        GFCommonUtil.getInstance().setBaseCameraSettings(false);
    }

    private void initSkyLiveveiwEffect() {
        SFRSA.getInstance().setPackageName(AppContext.getAppContext().getPackageName());
        SFRSA.getInstance().initialize();
        mHistCmd = SFRSA.COPY_HGF_LAND_TO_SKY;
        SFRSA.getInstance().setCommand(mHistCmd);
        SFRSA.getInstance().startLiveViewEffect(true);
        GFCommonUtil.getInstance().setFilterCameraSettings(false);
    }

    private void initLayer3LiveveiwEffect() {
        SFRSA.getInstance().setPackageName(AppContext.getAppContext().getPackageName());
        SFRSA.getInstance().initialize();
        mHistCmd = SFRSA.COPY_HGF_LAND_TO_SKY;
        SFRSA.getInstance().setCommand(mHistCmd);
        SFRSA.getInstance().startLiveViewEffect(true);
        GFCommonUtil.getInstance().setFilterCameraSettings(true);
        waitCameraSetting(WAIT_TIME);
        startLayer3LiveveiwEffect();
    }

    private void updateLiveveiwEffect() {
        SFRSA.getInstance().setCommand(mHistCmd & SFRSA.REMOVE_COPY_BITS);
        SFRSA.getInstance().startLiveViewEffect(true);
    }

    private void startLandLiveveiwEffect() {
        SFRSA.getInstance().setCommand(SFRSA.COPY_HGF_SKY_TO_LAND);
        SFRSA.getInstance().startLiveViewEffect(true);
        mHistCmd = SFRSA.COPY_HGF_SKY_TO_LAND;
        GFCommonUtil.getInstance().setBaseCameraSettings(false);
    }

    private void restartLandLiveveiwEffect() {
        GFCommonUtil.getInstance().setFilterCameraSettings(true);
        waitCameraSetting(WAIT_TIME);
        SFRSA.getInstance().setCommand(SFRSA.COPY_HGF_SKY_TO_LAND);
        SFRSA.getInstance().startLiveViewEffect(true);
        mHistCmd = SFRSA.COPY_HGF_SKY_TO_LAND;
        GFCommonUtil.getInstance().setBaseCameraSettings(false);
    }

    private void restartSkyLiveveiwEffect() {
        GFCommonUtil.getInstance().setBaseCameraSettings(true);
        waitCameraSetting(WAIT_TIME);
        SFRSA.getInstance().setCommand(SFRSA.COPY_HGF_LAND_TO_SKY);
        SFRSA.getInstance().startLiveViewEffect(true);
        mHistCmd = SFRSA.COPY_HGF_LAND_TO_SKY;
        GFCommonUtil.getInstance().setFilterCameraSettings(false);
    }

    private void restartLayer3LiveveiwEffect() {
        GFCommonUtil.getInstance().setBaseCameraSettings(true);
        waitCameraSetting(WAIT_TIME);
        SFRSA.getInstance().setCommand(SFRSA.COPY_HGF_LAND_TO_SKY);
        SFRSA.getInstance().startLiveViewEffect(true);
        mHistCmd = SFRSA.COPY_HGF_LAND_TO_SKY;
        GFCommonUtil.getInstance().setFilterCameraSettings(true);
        waitCameraSetting(WAIT_TIME);
        startLayer3LiveveiwEffect();
    }

    private void startLayer3LiveveiwEffect() {
        SFRSA.getInstance().setCommand(SFRSA.COPY_WORK_SKY_TO_LAYER3);
        SFRSA.getInstance().startLiveViewEffect(true);
        mHistCmd = SFRSA.HGF_SKY_TO_LAYER3;
        SFRSA.getInstance().setCommand(mHistCmd);
        SFRSA.getInstance().startLiveViewEffect(true);
        GFCommonUtil.getInstance().setLayer3CameraSettings(false);
    }

    private void startSkyLiveveiwEffect(boolean fromLand) {
        if (fromLand) {
            SFRSA.getInstance().setCommand(SFRSA.COPY_HGF_LAND_TO_SKY);
            SFRSA.getInstance().startLiveViewEffect(true);
            mHistCmd = SFRSA.COPY_HGF_LAND_TO_SKY;
        } else {
            SFRSA.getInstance().setCommand(SFRSA.HGF_LAYER3_TO_SKY);
            SFRSA.getInstance().startLiveViewEffect(true);
            mHistCmd = SFRSA.HGF_LAYER3_TO_SKY;
        }
        GFCommonUtil.getInstance().setFilterCameraSettings(false);
    }

    private void stopLiveveiwEffect() {
        if (runningSFRSA) {
            AppLog.info(TAG, "released mCameraSequence");
            SFRSA.getInstance().terminateAll();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int code = event.getScanCode();
        if (GFCommonUtil.getInstance().isTriggerMfAssist(code)) {
            super.pushedMfAssistCustomKey();
            return 1;
        }
        int ret = super.onConvertedKeyDown(event, func);
        return ret;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        if (code == 655 && !GFAvMenuLayout.isAvSetting() && !GFSetting15LayerTvAvLayout.isAvSetting() && !GFCommonUtil.getInstance().isFixedAperture() && (GFCommonUtil.getInstance().isAperture() || GFCommonUtil.getInstance().isManual())) {
            if (GFCommonUtil.getInstance().isLand()) {
                GFCommonUtil.getInstance().setApertureRetry(0);
            } else if (GFCommonUtil.getInstance().isSky()) {
                GFCommonUtil.getInstance().setApertureRetry(1);
            } else if (GFCommonUtil.getInstance().isLayer3()) {
                GFCommonUtil.getInstance().setApertureRetry(2);
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}
