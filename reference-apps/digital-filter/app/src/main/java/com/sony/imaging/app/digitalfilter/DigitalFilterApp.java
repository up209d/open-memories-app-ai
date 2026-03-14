package com.sony.imaging.app.digitalfilter;

import android.os.Looper;
import android.os.MessageQueue;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FaceDetectionController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.MeteringController;
import com.sony.imaging.app.base.shooting.camera.SilentShutterController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFKikiLogUtil;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.GFExecutorCreator;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFAELController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFCreativeStyleController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFExposureCompensationController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFExposureModeController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFaceDetectionController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFocusModeController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFISOSensitivityController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFMeteringController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.digitalfilter.shooting.trigger.GFCustomFunctionTable;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.fw.IFunctionTable;
import com.sony.imaging.app.util.AppInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;

/* loaded from: classes.dex */
public class DigitalFilterApp extends BaseApp {
    private static final String TAG_VERSION = "DetailedVersion";
    private static final String VERSION = "0.0053";
    private static final String TAG = AppLog.getClassName();
    private static String bootApp = BaseApp.APP_SHOOTING;
    public static int mBootFactor = 0;
    public static String mSSID = null;
    public static String mWifiPW = null;
    public static String mIPAdder = null;

    public DigitalFilterApp() {
        CameraSetting.registController(AELController.getName(), GFAELController.class);
        CameraSetting.registController(ExposureModeController.getName(), GFExposureModeController.class);
        CameraSetting.registController(WhiteBalanceController.getName(), GFWhiteBalanceController.class);
        CameraSetting.registController(CreativeStyleController.getName(), GFCreativeStyleController.class);
        CameraSetting.registController(ExposureCompensationController.getName(), GFExposureCompensationController.class);
        CameraSetting.registController(SilentShutterController.getName(), SilentShutterController.class);
        CameraSetting.registController(FocusModeController.getName(), GFFocusModeController.class);
        CameraSetting.registController(MeteringController.getName(), GFMeteringController.class);
        CameraSetting.registController(FaceDetectionController.getName(), GFFaceDetectionController.class);
        CameraSetting.registController(ISOSensitivityController.getName(), GFISOSensitivityController.class);
        doAbortInCaseUncautghtException = true;
        new Factory();
        new GFExecutorCreator();
        setBootLogo(R.drawable.p_16_dd_parts_df_launchericon, R.string.STRID_FUNC_DF_LAUNCHER_NAME);
    }

    @Override // com.sony.imaging.app.base.BaseApp
    public int getSupportingRecMode() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.BaseApp
    public String getStartApp() {
        return bootApp;
    }

    /* loaded from: classes.dex */
    class ExecutorInitThread extends Thread {
        ExecutorInitThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            ExecutorCreator.getInstance().init();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.fw.AppRoot, android.app.Activity
    public void onResume() {
        super.onResume();
        AppInfo.notifyAppInfo(this, getPackageName(), getClass().getName(), AppInfo.LARGE_CATEGORY_REC, "STILL", PULLING_BACK_KEYS_FOR_SHOOTING, RESUME_KEYS_FOR_SHOOTING);
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.digitalfilter.DigitalFilterApp.1
            @Override // android.os.MessageQueue.IdleHandler
            public boolean queueIdle() {
                MenuTable.getInstance().createDefaultMenuTree(DigitalFilterApp.this.getApplicationContext());
                return false;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onBoot(BootFactor factor) {
        KeyStatus status;
        super.onBoot(factor);
        GFCommonUtil cUtil = GFCommonUtil.getInstance();
        AppLog.info(TAG_VERSION, "DetailedVersion 0.0053");
        AppIconView.setIcon(R.drawable.p_16_dd_parts_df_appicon, R.drawable.p_16_aa_parts_df_appicon);
        AppNameView.setText(getResources().getString(R.string.STRID_FUNC_DF_LAUNCHER_NAME));
        AppNameView.show(true);
        cUtil.setS1_ONfromPlayback(false);
        cUtil.setLayerFlag(GFEEAreaController.getInstance().isLand(), GFEEAreaController.getInstance().isSky(), GFEEAreaController.getInstance().isLayer3());
        cUtil.stopAdjustmentSetting();
        cUtil.setShow3rdAreaSettingFlag(true);
        cUtil.checkModelName();
        cUtil.setDuringSelfTimer(false);
        GFWhiteBalanceController.getInstance().setWBDetailValueFromPF(false);
        GFKikiLogUtil.getInstance().countBooting();
        mBootFactor = factor.bootFactor;
        cUtil.setBootFactor(factor.bootFactor);
        switch (factor.bootFactor) {
            case 0:
                BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_RESET_APSC_SETTING, false);
                cUtil.setShowIrisLensMessage(true);
                break;
        }
        applyEffectParameter();
        if (AppInfo.KEY_PLAY_APO.equals(factor.bootKey) || AppInfo.KEY_PLAY_PON.equals(factor.bootKey)) {
            bootApp = BaseApp.APP_PLAY;
            return;
        }
        boolean isHousingAttached = false;
        if (!isHousingSupported() && (status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.WATER_HOUSING)) != null && 1 == status.valid && 1 == status.status) {
            isHousingAttached = true;
        }
        if (!isHousingAttached) {
            bootApp = BaseApp.APP_SHOOTING;
            Thread initThread = new ExecutorInitThread();
            initThread.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onShutdown() {
        GFEffectParameters.getInstance().terminate();
        super.onShutdown();
    }

    @Override // com.sony.imaging.app.base.BaseApp
    protected IFunctionTable getFunctionTable() {
        return new GFCustomFunctionTable();
    }

    private void applyEffectParameter() {
        GFEffectParameters.getInstance().initialize();
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        params.setEffect(GFBackUpKey.getInstance().getLastEffect());
        GFBackUpKey.getInstance().checkWBLimit();
        String lastParams = GFBackUpKey.getInstance().getLastParameters();
        params.unflatten(lastParams);
        GFEffectParameters.getInstance().setParameters(params);
        GFBackUpKey.getInstance().checkWBLimit();
    }

    @Override // com.sony.imaging.app.base.BaseApp
    protected int getDefaultBackupResName() {
        return R.xml.default_value;
    }
}
