package com.sony.imaging.app.startrails;

import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import com.sony.imaging.app.avi.DatabaseUtil;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.fw.IFunctionTable;
import com.sony.imaging.app.startrails.common.STCaptureDisplayModeObserver;
import com.sony.imaging.app.startrails.common.caution.STInfo;
import com.sony.imaging.app.startrails.database.DataBaseAdapter;
import com.sony.imaging.app.startrails.database.DataBaseOperations;
import com.sony.imaging.app.startrails.playback.STPlayRootContainer;
import com.sony.imaging.app.startrails.playback.controller.PlayBackController;
import com.sony.imaging.app.startrails.shooting.STAVICaptureProcess;
import com.sony.imaging.app.startrails.shooting.STExecutorCreator;
import com.sony.imaging.app.startrails.shooting.trigger.custom.STNormalFunctionTable;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.startrails.util.ThemeChangeListener;
import com.sony.imaging.app.util.AppInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.didep.Kikilog;
import java.io.IOException;

/* loaded from: classes.dex */
public class StarTrails extends BaseApp {
    private static final String TAG = "StarTrails";
    private static final String TAG2 = "DetailedVersion";
    private static final String VERSION = "1.5001";
    private static String bootApp = BaseApp.APP_SHOOTING;
    private static STUtility stUtility = null;
    public static boolean isIn_onShutdown = false;

    public StarTrails() {
        doAbortInCaseUncautghtException = true;
        new STFactory();
        new STExecutorCreator().setInitialSettingRequired(true);
        stUtility = STUtility.getInstance();
        BaseMenuService.setEachCautionIdClass(STInfo.class);
        setBootLogo(R.drawable.p_16_dd_parts_strl_launchericon, R.string.STRID_FUNC_STRS_APP_NAME);
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
        AppInfo.notifyAppInfo(this, getPackageName(), getClass().getName(), AppInfo.LARGE_CATEGORY_REC, "STILL", PULLING_BACK_KEYS_FOR_SHOOTING, RESUME_KEYS_FOR_SHOOTING);
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.startrails.StarTrails.1
            @Override // android.os.MessageQueue.IdleHandler
            public boolean queueIdle() {
                MenuTable.getInstance().createDefaultMenuTree(StarTrails.this.getApplicationContext());
                DataBaseOperations dBaseOperation = DataBaseOperations.getInstance();
                try {
                    dBaseOperation.exportXMLtoMedia();
                } catch (IOException e) {
                    Log.d(StarTrails.TAG, e.toString());
                }
                DatabaseUtil.DbResult result = DataBaseOperations.getInstance().importDatabase();
                if (result == DatabaseUtil.DbResult.DB_ERROR) {
                    DataBaseAdapter.getInstance().setDBCorruptStatus(true);
                    return false;
                }
                return false;
            }
        });
        ThemeChangeListener.getInstance().initialize();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onBoot(BootFactor factor) {
        KeyStatus status;
        super.onBoot(factor);
        AppLog.info(TAG2, "DetailedVersion 1.5001");
        STCaptureDisplayModeObserver.getInstance().updataActiveDisplayModeWithCommonSettingsForStarTrails();
        DatabaseUtil.initialize(DataBaseAdapter.getInstance());
        AppIconView.setIcon(R.drawable.p_16_dd_parts_strl_appicon, R.drawable.p_16_aa_parts_strl_appicon);
        AppNameView.setText(getResources().getString(R.string.STRID_FUNC_STRS_APP_NAME));
        AppNameView.show(true);
        switch (factor.bootFactor) {
            case 0:
                stUtility.setMenuBoot(true);
                stUtility.setIsEEStateBoot(true);
                printAppStartKikiLog();
                break;
            default:
                stUtility.setMenuBoot(false);
                stUtility.setPlayBackKeyPressed(false);
                break;
        }
        STUtility.isSettingsApplied = false;
        if (AppInfo.KEY_PLAY_APO.equals(factor.bootKey) || AppInfo.KEY_PLAY_PON.equals(factor.bootKey)) {
            bootApp = BaseApp.APP_PLAY;
        } else {
            boolean isHousingAttached = false;
            if (!isHousingSupported() && (status = ScalarInput.getKeyStatus(640)) != null && 1 == status.valid && 1 == status.status) {
                isHousingAttached = true;
            }
            if (!isHousingAttached) {
                bootApp = BaseApp.APP_SHOOTING;
                Thread initThread = new ExecutorInitThread();
                initThread.start();
            }
        }
        stUtility.setEVDialRotated(false);
        isIn_onShutdown = false;
    }

    @Override // com.sony.imaging.app.base.BaseApp
    protected int getDefaultBackupResName() {
        return R.xml.default_value;
    }

    public static void printAppStartKikiLog() {
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 32;
        Integer kikilogId = 4238;
        Kikilog.setUserLog(kikilogId.intValue(), options);
    }

    @Override // com.sony.imaging.app.base.BaseApp
    protected IFunctionTable getFunctionTable() {
        return new STNormalFunctionTable();
    }

    @Override // com.sony.imaging.app.base.BaseApp
    public void changeApp(String app, Bundle bundle) {
        if (BaseApp.APP_PLAY.equals(app)) {
            STUtility.getInstance().setPlayBackKeyPressed(true);
        } else {
            STUtility.getInstance().setPlayBackKeyPressed(false);
        }
        super.changeApp(app, bundle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot, android.app.Activity
    public void onPause() {
        ThemeChangeListener.getInstance().terminate();
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onShutdown() {
        Log.i("TAG", "onShutdown <<<");
        isIn_onShutdown = true;
        if (UtilPFWorkaround.isHighSpeedLensPowerOffWorkaroundNeeded && STAVICaptureProcess.isIn_onShutterSequence) {
            throw new IllegalStateException("Reboot Camera to avoid freeze.(IMDLAPP12-465)");
        }
        STCaptureDisplayModeObserver.getInstance().writeDisplayModeToCommonSettingsForStarTrails();
        STUtility.getInstance().releaseBitmapList();
        ThemeChangeListener.getInstance().terminate();
        PlayBackController.getInstance().setDefaultPBState();
        DataBaseOperations.getInstance().removeAllBOObjects();
        STPlayRootContainer.isSTListView = false;
        STCaptureDisplayModeObserver.getInstance().getActiveDevice();
        super.onShutdown();
        isIn_onShutdown = false;
        DatabaseUtil.initialize(null);
    }
}
