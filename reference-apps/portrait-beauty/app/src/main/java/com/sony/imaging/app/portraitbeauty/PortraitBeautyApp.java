package com.sony.imaging.app.portraitbeauty;

import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.BaseBackUpKey;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.fw.IFunctionTable;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyConstants;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyFunctionTable;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil;
import com.sony.imaging.app.portraitbeauty.menu.controller.AdjustVisuallyStartupController;
import com.sony.imaging.app.portraitbeauty.shooting.PortraitBeautyDisplayModeObserver;
import com.sony.imaging.app.portraitbeauty.shooting.PortraitBeautyExecutorCreater;
import com.sony.imaging.app.util.AppInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.MemoryMapConfig;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class PortraitBeautyApp extends BaseApp {
    private static final String TAG_VERSION = "DetailedVersion";
    private static final String VERSION = "1.4005";
    private static String bootApp = BaseApp.APP_SHOOTING;
    public static int sBootFactor = -1;
    public static String IMAGE_SETTING = "FLG_USE_ORIGINAL_IMAGE";
    public static final String[] RESUME_KEYS_FOR_SHOOTING = {AppInfo.KEY_POWER_SLIDE_PON, AppInfo.KEY_RELEASE_APO, AppInfo.KEY_PLAY_APO, AppInfo.KEY_MEDIA_INOUT_APO, AppInfo.KEY_LENS_APO, AppInfo.KEY_ACCESSORY_APO, AppInfo.KEY_DEDICATED_APO, AppInfo.KEY_POWER_APO};

    public PortraitBeautyApp() {
        doAbortInCaseUncautghtException = true;
        new Factory();
        new PortraitBeautyExecutorCreater();
        setBootLogo(R.drawable.p_16_dd_parts_portraitbeauty_launchericon, R.string.STRID_FUNC_SELFIE_LAUNCHER_NAME_MY);
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
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.portraitbeauty.PortraitBeautyApp.1
            @Override // android.os.MessageQueue.IdleHandler
            public boolean queueIdle() {
                MenuTable.getInstance().createDefaultMenuTree(PortraitBeautyApp.this.getApplicationContext());
                return false;
            }
        });
        PortraitBeautyUtil.getInstance().setPowerKeyPressed(false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onBoot(BootFactor factor) {
        KeyStatus status;
        super.onBoot(factor);
        AppLog.info(TAG_VERSION, VERSION);
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(StringBuilderThreadLocal.PERIOD)));
        if (pfMajorVersion >= 1) {
            MemoryMapConfig.setAllocationPolicy(1);
        }
        PortraitBeautyDisplayModeObserver.getInstance().resume();
        DisplayModeObserver ovserver = DisplayModeObserver.getInstance();
        int dispMode = ovserver.getActiveDispMode(1);
        if (5 == dispMode) {
            ovserver.setDisplayMode(1, 1);
        }
        sBootFactor = factor.bootFactor;
        AdjustVisuallyStartupController.getInstance().setAdjustVisullySelectedInMenu(false);
        PortraitBeautyUtil.getInstance().setFirstTimeLaunched(false);
        AppIconView.setIcon(R.drawable.p_16_dd_parts_portraitbeauty_appicon, R.drawable.p_16_aa_parts_portraitbeauty_appicon);
        AppNameView.setText(getResources().getString(R.string.STRID_FUNC_SELFIE_LAUNCHER_NAME_MY));
        AppNameView.show(true);
        switch (factor.bootFactor) {
            case 0:
                PortraitBeautyExecutorCreater.isInitialLaunched = true;
                PortraitBeautyUtil.getInstance().setFirstTimeLaunched(true);
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
    @Override // com.sony.imaging.app.base.BaseApp
    public void _needUpdatePreference() {
        super._needUpdatePreference();
        BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_CONTRAST_FOCUS_AREA, FocusAreaController.WIDE);
    }

    @Override // com.sony.imaging.app.base.BaseApp
    protected int getDefaultBackupResName() {
        return R.xml.default_value;
    }

    @Override // com.sony.imaging.app.base.BaseApp
    protected IFunctionTable getFunctionTable() {
        return new PortraitBeautyFunctionTable();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onShutdown() {
        PortraitBeautyDisplayModeObserver.getInstance().pause();
        PortraitBeautyUtil.getInstance().releaseAllStaticOptimizedImage();
        PortraitBeautyUtil.bIsAdjustModeGuide = false;
        CameraNotificationManager.getInstance().requestNotify(PortraitBeautyConstants.SELFTIMERSETTINGFINISHTAG);
        PortraitBeautyUtil.getInstance().setPowerKeyPressed(true);
        PortraitBeautyUtil.getInstance().setLensAttachedOnShootingScreen(false);
        super.onShutdown();
    }
}
