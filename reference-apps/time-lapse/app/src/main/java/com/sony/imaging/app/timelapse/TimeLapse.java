package com.sony.imaging.app.timelapse;

import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import com.sony.imaging.app.avi.DatabaseUtil;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.fw.IFunctionTable;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.timelapse.caution.TimelapseInfo;
import com.sony.imaging.app.timelapse.databaseutil.DataBaseAdapter;
import com.sony.imaging.app.timelapse.databaseutil.DataBaseOperations;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.playback.TimeLapsePlayRootContainer;
import com.sony.imaging.app.timelapse.playback.controller.PlayBackController;
import com.sony.imaging.app.timelapse.shooting.TimelapseExecutorCreator;
import com.sony.imaging.app.timelapse.shooting.TimelapseFunctionTable;
import com.sony.imaging.app.timelapse.shooting.controller.TimelapseCreativeStyleController;
import com.sony.imaging.app.timelapse.shooting.controller.TimelapseFocusModeController;
import com.sony.imaging.app.timelapse.shooting.controller.TimelapsePictureEffectController;
import com.sony.imaging.app.timelapse.shooting.layout.TimeLapseStableLayout;
import com.sony.imaging.app.util.AppInfo;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.FileHelper;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.didep.Kikilog;
import java.io.File;
import java.io.IOException;

/* loaded from: classes.dex */
public class TimeLapse extends BaseApp {
    private static final String TAG_VERSION = "DetailedVersion";
    private static final String VERSION = "3.4001";
    private static final String mAngleShiftAddonPath = "/data/data/com.sony.imaging.app.angleshiftaddon";
    private boolean isTimelapseLite = false;
    private static final String TAG = TimeLapse.class.getName();
    private static String bootApp = BaseApp.APP_SHOOTING;
    public static boolean isIn_onShutdown = false;

    public TimeLapse() {
        CameraSetting.registController(CreativeStyleController.getName(), TimelapseCreativeStyleController.class);
        CameraSetting.registController(PictureEffectController.getName(), TimelapsePictureEffectController.class);
        CameraSetting.registController(TimelapseFocusModeController.getName(), TimelapseFocusModeController.class);
        doAbortInCaseUncautghtException = true;
        TLCommonUtil.setTimelapseLite(this.isTimelapseLite);
        BaseMenuService.setEachCautionIdClass(TimelapseInfo.class);
        new TLFactory();
        if (this.isTimelapseLite) {
            setBootLogo(R.drawable.p_16_dd_parts_tm_std_launchericon, R.string.STRID_FUNC_TIMELAPSE_STD);
        } else {
            File addOnDir = new File(mAngleShiftAddonPath);
            if (FileHelper.exists(addOnDir)) {
                setBootLogo(R.drawable.p_16_dd_parts_splash_as_launchericon, R.string.STRID_FUNC_TIMELAPSE);
            } else {
                setBootLogo(R.drawable.p_16_dd_parts_tm_launchericon, R.string.STRID_FUNC_TIMELAPSE);
            }
        }
        new TimelapseExecutorCreator().setSettingDone(true);
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
        System.gc();
        super.onResume();
        AppInfo.notifyAppInfo(this, getPackageName(), getClass().getName(), AppInfo.LARGE_CATEGORY_REC, "STILL", PULLING_BACK_KEYS_FOR_SHOOTING, RESUME_KEYS_FOR_SHOOTING);
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.timelapse.TimeLapse.1
            @Override // android.os.MessageQueue.IdleHandler
            public boolean queueIdle() {
                MenuTable.getInstance().createDefaultMenuTree(TimeLapse.this.getApplicationContext());
                DataBaseOperations dBaseOperation = DataBaseOperations.getInstance();
                try {
                    dBaseOperation.exportXMLtoMedia();
                } catch (IOException e) {
                    Log.d(TimeLapse.TAG, e.toString());
                }
                DatabaseUtil.DbResult result = DataBaseOperations.getInstance().importDatabase();
                if (result == DatabaseUtil.DbResult.DB_ERROR) {
                    DataBaseAdapter.getInstance().setDBCorruptStatus(true);
                    return false;
                }
                return false;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onBoot(BootFactor factor) {
        KeyStatus status;
        Layout.clear();
        super.onBoot(factor);
        isIn_onShutdown = false;
        File addOnDir = new File(mAngleShiftAddonPath);
        TLCommonUtil.getInstance().setAngleShiftAddon(FileHelper.exists(addOnDir));
        if (FileHelper.exists(addOnDir)) {
            setBootLogo(R.drawable.p_16_dd_parts_splash_as_launchericon, R.string.STRID_FUNC_TIMELAPSE);
        } else {
            setBootLogo(R.drawable.p_16_dd_parts_tm_launchericon, R.string.STRID_FUNC_TIMELAPSE);
        }
        AppLog.info(TAG_VERSION, "DetailedVersion 3.4001");
        DatabaseUtil.initialize(DataBaseAdapter.getInstance());
        AvailableInfo.addDatabase("InhInf_Additional.db");
        setAppNameIcon();
        TLCommonUtil.getInstance().setBootFactor(factor.bootFactor);
        TimeLapseConstants.isEVDial = false;
        TimeLapseStableLayout.isCapturing = false;
        switch (factor.bootFactor) {
            case 0:
                appStartKikiLog();
                break;
        }
        if (AppInfo.KEY_PLAY_APO.equals(factor.bootKey) || AppInfo.KEY_PLAY_PON.equals(factor.bootKey)) {
            bootApp = BaseApp.APP_PLAY;
            return;
        }
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

    private void setAppNameIcon() {
        if (this.isTimelapseLite) {
            AppIconView.setIcon(R.drawable.p_16_dd_parts_tm_std_appicon, R.drawable.p_16_aa_parts_tm_std_appicon);
            AppNameView.setText(getResources().getString(R.string.STRID_FUNC_TIMELAPSE_STD));
        } else {
            AppIconView.setIcon(R.drawable.p_16_dd_parts_tm_appicon, R.drawable.p_16_aa_parts_tm_appicon);
            AppNameView.setText(getResources().getString(R.string.STRID_FUNC_TIMELAPSE));
        }
        AppNameView.show(true);
    }

    @Override // com.sony.imaging.app.base.BaseApp
    protected int getDefaultBackupResName() {
        return R.xml.default_value;
    }

    public static void appStartKikiLog() {
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 32;
        Integer kikilogId = 4188;
        if (TLCommonUtil.getInstance().isTimelapseLiteApplication()) {
            kikilogId = 4190;
        }
        if (kikilogId != null) {
            Kikilog.setUserLog(kikilogId.intValue(), options);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onShutdown() {
        if (TLCommonUtil.getInstance().maybePhaseDiffFlag()) {
            isIn_onShutdown = true;
        }
        TimeLapseStableLayout.is2SecondTimerFinish = false;
        TimeLapseStableLayout.isCapturing = false;
        PlayBackController.getInstance().setDefaultPBState();
        DataBaseOperations.getInstance().removeAllBOObjects();
        TimeLapsePlayRootContainer.isTimeLapseListView = false;
        super.onShutdown();
        DatabaseUtil.initialize(null);
    }

    @Override // com.sony.imaging.app.base.BaseApp
    protected IFunctionTable getFunctionTable() {
        return new TimelapseFunctionTable();
    }
}
