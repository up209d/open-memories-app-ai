package com.sony.imaging.app.liveviewgrading;

import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.fw.IFunctionTable;
import com.sony.imaging.app.liveviewgrading.menu.controller.ColorGradingController;
import com.sony.imaging.app.liveviewgrading.menu.controller.ColorGradingCreativeStyleController;
import com.sony.imaging.app.liveviewgrading.menu.controller.ColorGradingExposureModeController;
import com.sony.imaging.app.liveviewgrading.shooting.ColorGradingEEState;
import com.sony.imaging.app.liveviewgrading.shooting.ColorGradingExecutorCreator;
import com.sony.imaging.app.liveviewgrading.shooting.LVGEffectValueController;
import com.sony.imaging.app.util.AppInfo;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.didep.Kikilog;

/* loaded from: classes.dex */
public class ColorGradingApp extends BaseApp {
    private static final String TAG2 = "DetailedVersion";
    private static final String VERSION = "1.3004";
    private static boolean mIsLauncherBoot;
    private static final String TAG = AppLog.getClassName();
    private static String bootApp = BaseApp.APP_SHOOTING;
    public static final String[] RESUME_KEYS_FOR_MOVIE_LVG = {AppInfo.KEY_POWER_SLIDE_PON, AppInfo.KEY_RELEASE_APO, AppInfo.KEY_MOVREC_APO, AppInfo.KEY_MEDIA_INOUT_APO, AppInfo.KEY_LENS_APO, AppInfo.KEY_ACCESSORY_APO, AppInfo.KEY_DEDICATED_APO, AppInfo.KEY_POWER_APO};

    public ColorGradingApp() {
        doAbortInCaseUncautghtException = true;
        new Factory();
        new ColorGradingExecutorCreator();
        setBootLogo(R.drawable.p_16_dd_parts_lvg_launchericon_s, R.string.STRID_FUNC_LVG_LAUNCHER_NAME);
        CameraSetting.registController(ExposureModeController.getName(), ColorGradingExposureModeController.class);
        CameraSetting.registController(CreativeStyleController.getName(), ColorGradingCreativeStyleController.class);
    }

    @Override // com.sony.imaging.app.base.BaseApp
    public void changeApp(String app, Bundle bundle) {
        super.changeApp(BaseApp.APP_SHOOTING, bundle);
    }

    @Override // com.sony.imaging.app.base.BaseApp
    public int getSupportingRecMode() {
        return 2;
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
            ColorGradingExecutorCreator.getInstance().init();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.fw.AppRoot, android.app.Activity
    public void onResume() {
        super.onResume();
        AppInfo.notifyAppInfo(this, getPackageName(), getClass().getName(), AppInfo.LARGE_CATEGORY_REC, AppInfo.SMALL_CATEGORY_MOVIE, PULLING_BACK_KEYS_FOR_MOVIE, RESUME_KEYS_FOR_MOVIE_LVG);
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.liveviewgrading.ColorGradingApp.1
            @Override // android.os.MessageQueue.IdleHandler
            public boolean queueIdle() {
                MenuTable.getInstance().createDefaultMenuTree(ColorGradingApp.this.getApplicationContext());
                return false;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onBoot(BootFactor factor) {
        KeyStatus status;
        super.onBoot(factor);
        AppLog.info(TAG2, "DetailedVersion 1.3004");
        AppIconView.setIcon(R.drawable.p_16_dd_parts_lvg_appicon);
        AppNameView.setText(getResources().getString(R.string.STRID_FUNC_LVG_LAUNCHER_NAME));
        AppNameView.show(true);
        ColorGradingController.getInstance().setRecStopped(true);
        LVGEffectValueController.getInstance().setContext(getApplicationContext());
        ColorGradingController.getInstance().startKikiLog(4208);
        switch (factor.bootFactor) {
            case 0:
                setIsLauncherBoot(true);
                ColorGradingEEState.setIsMenuStateAdd(true);
                break;
            case 1:
                setIsLauncherBoot(false);
                ColorGradingEEState.setIsMenuStateAdd(false);
                break;
            case 2:
                setIsLauncherBoot(false);
                ColorGradingEEState.setIsMenuStateAdd(false);
                break;
            default:
                setIsLauncherBoot(false);
                break;
        }
        if (AppInfo.KEY_PLAY_APO.equals(factor.bootKey) || AppInfo.KEY_PLAY_PON.equals(factor.bootKey)) {
            bootApp = BaseApp.APP_SHOOTING;
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
        AppLog.enter(TAG, AppLog.getMethodName());
        ColorGradingController.getInstance().setShootingScreenOpened(false);
        ColorGradingController.getInstance().setPlayBackKeyPressedOnMenu(false);
        LVGEffectValueController.getInstance().clearGammmTable();
        AppLog.exit(TAG, AppLog.getMethodName());
        super.onShutdown();
    }

    public static void setIsLauncherBoot(boolean isLauncherBoot) {
        mIsLauncherBoot = isLauncherBoot;
    }

    public static boolean getIsLauncherBoot() {
        return mIsLauncherBoot;
    }

    @Override // com.sony.imaging.app.base.BaseApp
    public void selectShootingMode() {
        super.selectShootingMode();
    }

    @Override // com.sony.imaging.app.base.BaseApp
    protected int getDefaultBackupResName() {
        return R.xml.default_value;
    }

    @Override // com.sony.imaging.app.base.BaseApp
    protected IFunctionTable getFunctionTable() {
        return new ColorGradingFunctionTable();
    }

    public static void appStartKikiLog(int id) {
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 32;
        Integer kikilogId = 4165;
        if (kikilogId != null) {
            Kikilog.setUserLog(kikilogId.intValue(), options);
        }
    }
}
