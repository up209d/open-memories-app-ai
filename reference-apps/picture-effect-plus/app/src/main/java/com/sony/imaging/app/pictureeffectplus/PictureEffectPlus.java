package com.sony.imaging.app.pictureeffectplus;

import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.fw.IFunctionTable;
import com.sony.imaging.app.pictureeffectplus.shooting.PictureEffectEEState;
import com.sony.imaging.app.pictureeffectplus.shooting.PictureEffectPlusExecutorCreator;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.PictureEffectPlusController;
import com.sony.imaging.app.pictureeffectplus.shooting.trigger.PictureEffectPlusFunctionTable;
import com.sony.imaging.app.util.AppInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.PTag;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;

/* loaded from: classes.dex */
public class PictureEffectPlus extends BaseApp {
    private static final String TAG_ON_SHUTDOWN = "PictureEffectPlus onShutdown";
    private static String bootApp = BaseApp.APP_SHOOTING;
    private static boolean mIsLauncherBoot;
    private final String TAG = AppLog.getClassName();

    public PictureEffectPlus() {
        doAbortInCaseUncautghtException = true;
        new Factory();
        new PictureEffectPlusExecutorCreator();
        setBootLogo(R.drawable.p_16_dd_parts_pe_launchericon_s, R.string.STRID_FUNC_EFFECT_MASTER);
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
            PictureEffectPlusExecutorCreator.getInstance().init();
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

    @Override // com.sony.imaging.app.base.BaseApp
    protected int getDefaultBackupResName() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return R.xml.default_value;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.fw.AppRoot, android.app.Activity
    public void onResume() {
        super.onResume();
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppInfo.notifyAppInfo(this, getPackageName(), getClass().getName(), AppInfo.LARGE_CATEGORY_REC, AppInfo.SMALL_CATEGORY_STILL, PULLING_BACK_KEYS_FOR_SHOOTING, RESUME_KEYS_FOR_SHOOTING);
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.pictureeffectplus.PictureEffectPlus.1
            @Override // android.os.MessageQueue.IdleHandler
            public boolean queueIdle() {
                MenuTable.getInstance().createDefaultMenuTree(PictureEffectPlus.this.getApplicationContext());
                return false;
            }
        });
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onBoot(BootFactor factor) {
        KeyStatus status;
        super.onBoot(factor);
        AppLog.enter(this.TAG, AppLog.getMethodName());
        Log.i(this.TAG, "super onBoot");
        PictureEffectKikilogUtil.PictureEffectPlusStart();
        Log.i(this.TAG, "PictureEffectKikilogUtil PictureEffectPlusStart");
        AppIconView.setIcon(R.drawable.p_16_dd_parts_pe_appicon, R.drawable.p_16_aa_parts_pe_appicon);
        AppNameView.setText(getResources().getString(R.string.STRID_FUNC_EFFECT_MASTER));
        AppNameView.show(true);
        PictureEffectPlusExecutorCreator creator = (PictureEffectPlusExecutorCreator) PictureEffectPlusExecutorCreator.getInstance();
        creator.setIsCautionDisplaying(false);
        switch (factor.bootFactor) {
            case 0:
                setIsLauncherBoot(true);
                PictureEffectEEState.setIsMenuStateAdd(true);
                Log.i(this.TAG, "BootFactor.LUNCHER");
                break;
            case 1:
                setIsLauncherBoot(false);
                PictureEffectEEState.setIsMenuStateAdd(false);
                Log.i(this.TAG, "BootFactor.APO");
                break;
            case 2:
                setIsLauncherBoot(false);
                PictureEffectEEState.setIsMenuStateAdd(false);
                Log.i(this.TAG, "BootFactor.POWERON");
                break;
            default:
                setIsLauncherBoot(false);
                break;
        }
        if (AppInfo.KEY_PLAY_APO.equals(factor.bootKey) || AppInfo.KEY_PLAY_PON.equals(factor.bootKey)) {
            bootApp = BaseApp.APP_PLAY;
        } else {
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
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onShutdown() {
        PTag.start(TAG_ON_SHUTDOWN);
        PictureEffectPlusController.getInstance().setAppTopOpenFromMenu(false);
        PictureEffectPlusController.getInstance().setShootingScreenOpened(false);
        super.onShutdown();
    }

    public static void setIsLauncherBoot(boolean isLauncherBoot) {
        mIsLauncherBoot = isLauncherBoot;
    }

    public static boolean getIsLauncherBoot() {
        return mIsLauncherBoot;
    }

    @Override // com.sony.imaging.app.base.BaseApp
    public int getSupportingRecMode() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.BaseApp
    protected IFunctionTable getFunctionTable() {
        return new PictureEffectPlusFunctionTable();
    }
}
