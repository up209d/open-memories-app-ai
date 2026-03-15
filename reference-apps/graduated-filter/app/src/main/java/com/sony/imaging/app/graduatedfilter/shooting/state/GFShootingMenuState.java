package com.sony.imaging.app.graduatedfilter.shooting.state;

import android.os.Bundle;
import android.view.KeyEvent;
import com.sony.imaging.app.base.shooting.ShootingMenuState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.graduatedfilter.GFBackUpKey;
import com.sony.imaging.app.graduatedfilter.GraduatedFilterApp;
import com.sony.imaging.app.graduatedfilter.common.AppContext;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;
import com.sony.imaging.app.graduatedfilter.menu.layout.GFBorderMenuLayout;
import com.sony.imaging.app.graduatedfilter.menu.layout.GFExposureMenuLayout;
import com.sony.imaging.app.graduatedfilter.menu.layout.GFGuideLayout;
import com.sony.imaging.app.graduatedfilter.menu.layout.GFSettingMenuLayout;
import com.sony.imaging.app.graduatedfilter.menu.layout.GFShadingMenuLayout;
import com.sony.imaging.app.graduatedfilter.sa.GFHistgramTask;
import com.sony.imaging.app.graduatedfilter.sa.SFRSA;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class GFShootingMenuState extends ShootingMenuState implements NotificationListener {
    private static final String TAG = AppLog.getClassName();
    private static boolean runningSFRSA = false;
    private static String[] TAGS = {GFConstants.START_APPSETTING, GFConstants.UPDATE_APPSETTING, GFConstants.STOP_APPSETTING, GFConstants.START_BASESETTING, CameraNotificationManager.HISTOGRAM_UPDATE};

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        GFCommonUtil.getInstance().openMenu();
        GFEffectParameters.Parameters parameter = GFEffectParameters.getInstance().getParameters();
        GFBackUpKey.getInstance().saveLastParameters(parameter.flatten());
        CameraNotificationManager.getInstance().setNotificationListener(this);
        SFRSA.getInstance().terminateAll();
        runningSFRSA = false;
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        GFCommonUtil.getInstance().closeMenu();
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        GFEffectParameters.Parameters parameter = GFEffectParameters.getInstance().getParameters();
        GFBackUpKey.getInstance().saveLastParameters(parameter.flatten());
        GFHistgramTask.getInstance().stopHistgramUpdating();
        stopLiveveiwEffect();
        if (2 != RunStatus.getStatus()) {
            GFCommonUtil.getInstance().stopFilterSetting();
        }
        closeLayout(GFBorderMenuLayout.MENU_ID);
        closeLayout(GFShadingMenuLayout.MENU_ID);
        closeLayout(GFExposureMenuLayout.MENU_ID);
        closeLayout(GFSettingMenuLayout.MENU_ID);
        closeLayout(GFGuideLayout.ID_GFGUIDELAYOUT);
        closeLayout("ID_GFINTRODUCTIONLAYOUT");
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState
    public void finishMenuState(Bundle bundle) {
        if (GraduatedFilterApp.mBootFactor == 0) {
            GraduatedFilterApp.mBootFactor = 2;
        }
        super.finishMenuState(bundle);
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        CameraEx cameraEx;
        if (GFConstants.START_APPSETTING.equalsIgnoreCase(tag)) {
            if (!runningSFRSA) {
                runningSFRSA = true;
                startLiveveiwEffect();
                return;
            } else {
                updateLiveveiwEffect();
                return;
            }
        }
        if (GFConstants.UPDATE_APPSETTING.equalsIgnoreCase(tag)) {
            if (runningSFRSA) {
                updateLiveveiwEffect();
            }
        } else if (GFConstants.STOP_APPSETTING.equalsIgnoreCase(tag)) {
            stopLiveveiwEffect();
            runningSFRSA = false;
        } else if (GFConstants.START_BASESETTING.equalsIgnoreCase(tag)) {
            runningSFRSA = true;
        } else if (CameraNotificationManager.HISTOGRAM_UPDATE.equalsIgnoreCase(tag) && (cameraEx = CameraSetting.getInstance().getCamera()) != null) {
            AppLog.info(TAG, "inhibits listener-NOT-found histoupdate message.");
            cameraEx.setPreviewAnalizeListener((CameraEx.PreviewAnalizeListener) null);
        }
    }

    private void startLiveveiwEffect() {
        SFRSA.getInstance().setPackageName(AppContext.getAppContext().getPackageName());
        SFRSA.getInstance().initialize();
        SFRSA.getInstance().setCommand(10);
        SFRSA.getInstance().startLiveViewEffect(true);
        try {
            Thread.sleep(384L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        GFCommonUtil.getInstance().setFilterCameraSettings();
    }

    private void updateLiveveiwEffect() {
        SFRSA.getInstance().setCommand(8);
        SFRSA.getInstance().startLiveViewEffect(true);
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

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        if (!GFCommonUtil.getInstance().isTriggerMfAssist(code)) {
            return super.onKeyDown(keyCode, event);
        }
        super.pushedMfAssistCustomKey();
        return 1;
    }
}
