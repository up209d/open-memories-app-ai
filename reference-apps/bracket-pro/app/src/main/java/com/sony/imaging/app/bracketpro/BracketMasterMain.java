package com.sony.imaging.app.bracketpro;

import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterUtil;
import com.sony.imaging.app.bracketpro.menu.controller.BMFlashController;
import com.sony.imaging.app.bracketpro.menu.controller.BMMenuController;
import com.sony.imaging.app.bracketpro.shooting.BMCaptureProcess;
import com.sony.imaging.app.bracketpro.shooting.BMExecutorCreator;
import com.sony.imaging.app.bracketpro.shooting.state.BMEEState;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.fw.IFunctionTable;
import com.sony.imaging.app.util.AppInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;

/* loaded from: classes.dex */
public class BracketMasterMain extends BaseApp {
    private static final String TAG_VERSION = "DetailedVersion";
    private static final String VERSION = "1.4029";
    private static boolean mIsLauncherBoot;
    private static final String TAG = AppLog.getClassName();
    private static String bootApp = BaseApp.APP_SHOOTING;

    public BracketMasterMain() {
        doAbortInCaseUncautghtException = true;
        new Factory();
        new BMExecutorCreator();
        setBootLogo(R.drawable.p_16_dd_parts_bp_launchericon, R.string.STRID_FUNC_BRKMASTER);
        CameraSetting.registController(FlashController.getName(), BMFlashController.class);
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
            BMExecutorCreator.getInstance().init();
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
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.bracketpro.BracketMasterMain.1
            @Override // android.os.MessageQueue.IdleHandler
            public boolean queueIdle() {
                MenuTable.getInstance().createDefaultMenuTree(BracketMasterMain.this.getApplicationContext());
                return false;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onBoot(BootFactor factor) {
        KeyStatus status;
        super.onBoot(factor);
        BMCaptureProcess.sHaltState = false;
        AppLog.info(TAG_VERSION, VERSION);
        AppIconView.setIcon(R.drawable.p_16_dd_parts_bp_appicon, R.drawable.p_16_aa_parts_bp_appicon);
        AppNameView.setText(getResources().getString(BracketMasterUtil.getBracketTypeString()));
        AppNameView.show(true);
        switch (factor.bootFactor) {
            case 0:
                setIsLauncherBoot(true);
                BMEEState.setIsMenuStateAdd(true);
                break;
            case 1:
                setIsLauncherBoot(false);
                BMEEState.setIsMenuStateAdd(false);
                break;
            case 2:
                setIsLauncherBoot(false);
                BMEEState.setIsMenuStateAdd(false);
                break;
            default:
                setIsLauncherBoot(false);
                break;
        }
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
        BMCaptureProcess.sHaltState = true;
        AppLog.enter(TAG, AppLog.getMethodName());
        BMMenuController.getInstance().setShootingScreenOpened(false);
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
    protected int getDefaultBackupResName() {
        return R.xml.default_value;
    }

    @Override // com.sony.imaging.app.base.BaseApp
    protected IFunctionTable getFunctionTable() {
        return new BMNormalFunctionTable();
    }
}
