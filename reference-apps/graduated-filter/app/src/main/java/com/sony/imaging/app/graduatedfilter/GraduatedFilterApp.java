package com.sony.imaging.app.graduatedfilter;

import android.os.Looper;
import android.os.MessageQueue;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.MeteringController;
import com.sony.imaging.app.base.shooting.camera.SilentShutterController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.fw.IFunctionTable;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.GFKikiLogUtil;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.graduatedfilter.shooting.GFExecutorCreator;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFAELController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFCreativeStyleController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFExposureCompensationController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFExposureModeController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFFocusModeController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFMeteringController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.TouchLessShutterController;
import com.sony.imaging.app.graduatedfilter.shooting.trigger.GFCustomFunctionTable;
import com.sony.imaging.app.util.AppInfo;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;

/* loaded from: classes.dex */
public class GraduatedFilterApp extends BaseApp {
    private static final String TAG_VERSION = "DetailedVersion";
    private static final String VERSION = "1.1105";
    private static final String TAG = AppLog.getClassName();
    private static String bootApp = BaseApp.APP_SHOOTING;
    public static int mBootFactor = 0;

    public GraduatedFilterApp() {
        CameraSetting.registController(AELController.getName(), GFAELController.class);
        CameraSetting.registController(ExposureModeController.getName(), GFExposureModeController.class);
        CameraSetting.registController(WhiteBalanceController.getName(), GFWhiteBalanceController.class);
        CameraSetting.registController(CreativeStyleController.getName(), GFCreativeStyleController.class);
        CameraSetting.registController(ExposureCompensationController.getName(), GFExposureCompensationController.class);
        CameraSetting.registController(SilentShutterController.getName(), SilentShutterController.class);
        CameraSetting.registController(FocusModeController.getName(), GFFocusModeController.class);
        CameraSetting.registController(MeteringController.getName(), GFMeteringController.class);
        doAbortInCaseUncautghtException = true;
        new Factory();
        new GFExecutorCreator();
        setBootLogo(R.drawable.p_16_dd_parts_sky_launchericon, R.string.STRID_FUNC_SKYND_LAUNCHER_NAME);
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
        AppInfo.notifyAppInfo(this, getPackageName(), getClass().getName(), AppInfo.LARGE_CATEGORY_REC, AppInfo.SMALL_CATEGORY_STILL, PULLING_BACK_KEYS_FOR_SHOOTING, RESUME_KEYS_FOR_SHOOTING);
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.graduatedfilter.GraduatedFilterApp.1
            @Override // android.os.MessageQueue.IdleHandler
            public boolean queueIdle() {
                MenuTable.getInstance().createDefaultMenuTree(GraduatedFilterApp.this.getApplicationContext());
                return false;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onBoot(BootFactor factor) {
        KeyStatus status;
        super.onBoot(factor);
        AppLog.info(TAG_VERSION, "DetailedVersion 1.1105");
        AppIconView.setIcon(R.drawable.p_16_dd_parts_sky_appicon, R.drawable.p_16_aa_parts_sky_appicon);
        AppNameView.setText(getResources().getString(R.string.STRID_FUNC_SKYND_LAUNCHER_NAME));
        AppNameView.show(true);
        GFCommonUtil.getInstance().setS1_ONfromPlayback(false);
        GFCommonUtil.getInstance().stopFilterSetting();
        GFCommonUtil.getInstance().stopAdjustmentSetting();
        GFCommonUtil.getInstance().setShowFilterEVSettingFlag(true);
        GFCommonUtil.getInstance().checkModelName();
        GFCommonUtil.getInstance().setDuringSelfTimer(false);
        GFCommonUtil.getInstance().needCTempSetting();
        GFWhiteBalanceController.getInstance().setWBDetailValueFromPF(false);
        TouchLessShutterController.getInstance().checkTouchLessAddOn();
        GFKikiLogUtil.getInstance().countBooting();
        mBootFactor = factor.bootFactor;
        switch (factor.bootFactor) {
            case 0:
                GFCommonUtil.getInstance().setShowIrisLensMessage(true);
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
        String lastParams = GFBackUpKey.getInstance().getLastParameters();
        params.unflatten(lastParams);
        GFEffectParameters.getInstance().setParameters(params);
    }

    @Override // com.sony.imaging.app.base.BaseApp
    protected int getDefaultBackupResName() {
        return R.xml.default_value;
    }
}
