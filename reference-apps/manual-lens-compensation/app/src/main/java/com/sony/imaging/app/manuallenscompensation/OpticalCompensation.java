package com.sony.imaging.app.manuallenscompensation;

import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.BaseBackUpKey;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCBackUpKey;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;
import com.sony.imaging.app.manuallenscompensation.database.LensCompensationParameter;
import com.sony.imaging.app.manuallenscompensation.shooting.OCExecutorCreator;
import com.sony.imaging.app.util.AppInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.didep.Kikilog;

/* loaded from: classes.dex */
public class OpticalCompensation extends BaseApp {
    private static final String TAG = "OpticalCompensation";
    private static final String TAG2 = "DetailedVersion";
    private static final String VERSION = "2.4001";
    private static String bootApp = BaseApp.APP_SHOOTING;
    public static boolean sIsFirstTimeLaunched = true;
    private final String START_APP = "StartApp";

    public OpticalCompensation() {
        doAbortInCaseUncautghtException = true;
        new OCFactory();
        new OCExecutorCreator();
        setBootLogo(R.drawable.p_16_dd_parts_oc_launchericon, R.string.STRID_FUNC_OPTICAL_COMPENSATION);
    }

    @Override // com.sony.imaging.app.base.BaseApp
    public int getSupportingRecMode() {
        return 3;
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
        AppLog.enter(TAG, "StartApp");
        AppInfo.notifyAppInfo(this, getPackageName(), getClass().getName(), AppInfo.LARGE_CATEGORY_REC, AppInfo.SMALL_CATEGORY_STILL, PULLING_BACK_KEYS_FOR_SHOOTING, RESUME_KEYS_FOR_SHOOTING);
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.manuallenscompensation.OpticalCompensation.1
            @Override // android.os.MessageQueue.IdleHandler
            public boolean queueIdle() {
                MenuTable.getInstance().createDefaultMenuTree(OpticalCompensation.this.getApplicationContext());
                return false;
            }
        });
        AppLog.exit(TAG, "StartApp");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot, android.app.Activity
    public void onPause() {
        AppLog.enter(TAG, AppLog.getMethodName());
        OCUtil.getInstance().deleteAllProfile();
        super.onPause();
        System.runFinalization();
        System.gc();
        System.runFinalization();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onBoot(BootFactor factor) {
        KeyStatus status;
        super.onBoot(factor);
        AppLog.info(TAG2, "DetailedVersion 2.4001");
        AppLog.enter(TAG, "onBoot factor ID" + factor);
        AppIconView.setIcon(R.drawable.p_16_dd_parts_oc_appicon, R.drawable.p_16_aa_parts_oc_appicon);
        OCUtil.getInstance().setLastCaptureMode(1);
        if (AppInfo.KEY_PLAY_APO.equals(factor.bootKey) || AppInfo.KEY_PLAY_PON.equals(factor.bootKey)) {
            bootApp = BaseApp.APP_PLAY;
            OCUtil.getInstance().synchDBonMediaChange();
        } else {
            boolean isHousingAttached = false;
            if (!isHousingSupported() && (status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.WATER_HOUSING)) != null && 1 == status.valid && 1 == status.status) {
                isHousingAttached = true;
            }
            if (!isHousingAttached) {
                bootApp = BaseApp.APP_SHOOTING;
                Thread initThread = new ExecutorInitThread();
                initThread.start();
                OCUtil.getInstance().synchDBonMediaChange();
            }
        }
        if (factor.bootFactor == 0) {
            sIsFirstTimeLaunched = true;
            appStartKikiLog();
            OCUtil.getInstance().setLauncherBooted(true);
        } else {
            sIsFirstTimeLaunched = false;
            OCUtil.getInstance().setLauncherBooted(false);
        }
        OCUtil.getInstance().setExitExecute(false);
        setLastSelectedProfile();
        AppLog.exit(TAG, "onBoot  ");
    }

    private void setLastSelectedProfile() {
        LensCompensationParameter mParam;
        int lastAppliedParamDbID = BackUpUtil.getInstance().getPreferenceInt(OCBackUpKey.KEY_BKUP_PREV_RECORD_ID, -1);
        if (lastAppliedParamDbID != -1 && (mParam = LensCompensationParameter.queryPreviousProfileParam(getApplicationContext(), lastAppliedParamDbID)) != null) {
            OCUtil.getInstance().setAppTitle(mParam);
        }
    }

    public static void appStartKikiLog() {
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 32;
        Integer kikilogId = 4165;
        if (kikilogId != null) {
            Kikilog.setUserLog(kikilogId.intValue(), options);
        }
    }

    @Override // com.sony.imaging.app.base.BaseApp
    protected int getDefaultBackupResName() {
        return R.xml.default_value;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp
    public void _needUpdatePreference() {
        super._needUpdatePreference();
        BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_SCENE_SELECTION_VALUE, "portrait");
        BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_MOVIE_MODE_VALUE, ExposureModeController.MOVIE_PROGRAM_AUTO_MODE);
    }
}
