package com.sony.imaging.app.lightshaft;

import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.fw.IFunctionTable;
import com.sony.imaging.app.lightshaft.shooting.LSNormalFunctionTable;
import com.sony.imaging.app.lightshaft.shooting.ShaftsEffect;
import com.sony.imaging.app.lightshaft.shooting.camera.LightShaftExecutorCreator;
import com.sony.imaging.app.util.AppInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.didep.Kikilog;

/* loaded from: classes.dex */
public class LightShaft extends BaseApp {
    private static final String TAG = "LightShaft";
    private static boolean mIsMenuBoot;
    private static String sBootApp = BaseApp.APP_SHOOTING;
    private static boolean sIsEEStateBoot = false;
    private static boolean sIsLauncherBoot;

    public LightShaft() {
        doAbortInCaseUncautghtException = true;
        new LSFactory();
        new LightShaftExecutorCreator().setInitialSettingRequired(true);
        setBootLogo(R.drawable.p_16_dd_parts_ls_launchericon, R.string.STRID_FUNC_LIGHTSHAFTS);
    }

    @Override // com.sony.imaging.app.base.BaseApp
    public int getSupportingRecMode() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.BaseApp
    public String getStartApp() {
        return sBootApp;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BackUpUtil backup = BackUpUtil.getInstance();
        backup.Init(getApplicationContext());
        backup.setDefaultValues(getDefaultBackupResName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.fw.AppRoot, android.app.Activity
    public void onResume() {
        super.onResume();
        AppInfo.notifyAppInfo(this, getPackageName(), getClass().getName(), AppInfo.LARGE_CATEGORY_REC, AppInfo.SMALL_CATEGORY_STILL, PULLING_BACK_KEYS_FOR_SHOOTING, RESUME_KEYS_FOR_SHOOTING);
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.lightshaft.LightShaft.1
            @Override // android.os.MessageQueue.IdleHandler
            public boolean queueIdle() {
                MenuTable.getInstance().createDefaultMenuTree(LightShaft.this.getApplicationContext());
                return false;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onBoot(BootFactor factor) {
        KeyStatus status;
        super.onBoot(factor);
        AppIconView.setIcon(R.drawable.p_16_dd_parts_ls_appicon, R.drawable.p_16_dd_parts_ls_appicon);
        AppNameView.setText(getResources().getString(R.string.STRID_FUNC_LIGHTSHAFTS));
        AppNameView.show(true);
        switch (factor.bootFactor) {
            case 0:
                setMenuBoot(true);
                printAppStartKikiLog();
                break;
            default:
                setMenuBoot(false);
                break;
        }
        applyShaftEffectParameter();
        if (AppInfo.KEY_PLAY_APO.equals(factor.bootKey) || AppInfo.KEY_PLAY_PON.equals(factor.bootKey)) {
            sBootApp = BaseApp.APP_PLAY;
        } else {
            boolean isHousingAttached = false;
            if (!isHousingSupported() && (status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.WATER_HOUSING)) != null && 1 == status.valid && 1 == status.status) {
                isHousingAttached = true;
            }
            if (!isHousingAttached) {
                sBootApp = BaseApp.APP_SHOOTING;
                Thread initThread = new ExecutorInitThread();
                initThread.start();
            }
        }
        LightShaftConstants.getInstance().setEVDialRotated(false);
    }

    private void applyShaftEffectParameter() {
        ShaftsEffect.getInstance().initialize(getPackageName());
        ShaftsEffect.Parameters mParams = ShaftsEffect.getInstance().getParameters();
        mParams.setEffect(LightShaftBackUpKey.getInstance().getLastSelectedEffect());
        String lastSavedOptionParams = LightShaftBackUpKey.getInstance().getLastSavedOptionSettings();
        mParams.unflatten(lastSavedOptionParams);
        ShaftsEffect.getInstance().setParameters(mParams);
        AppLog.info(TAG, "Parameters:initialization");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onShutdown() {
        ShaftsEffect.getInstance().terminate();
        super.onShutdown();
    }

    public static void setIsLauncherBoot(boolean isLauncherBoot) {
        sIsLauncherBoot = isLauncherBoot;
    }

    public static boolean isLauncherBoot() {
        return sIsLauncherBoot;
    }

    public static boolean isLaunchAppBooted() {
        return mIsMenuBoot;
    }

    @Override // com.sony.imaging.app.base.BaseApp
    protected int getDefaultBackupResName() {
        return R.xml.default_value;
    }

    public static void setMenuBoot(boolean isBooting) {
        mIsMenuBoot = isBooting;
        setIsLauncherBoot(isBooting);
    }

    public static void printAppStartKikiLog() {
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 32;
        Integer kikilogId = 4157;
        Kikilog.setUserLog(kikilogId.intValue(), options);
    }

    public static boolean isEEStateBoot() {
        return sIsEEStateBoot;
    }

    public static void setIsEEStateBoot(boolean mIsEEStateBoot) {
        sIsEEStateBoot = mIsEEStateBoot;
    }

    @Override // com.sony.imaging.app.base.BaseApp
    protected IFunctionTable getFunctionTable() {
        return new LSNormalFunctionTable();
    }
}
