package com.sony.imaging.app.smoothreflection;

import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.fw.IFunctionTable;
import com.sony.imaging.app.smoothreflection.caution.SmoothReflectionInfo;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.common.SaUtil;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionBackUpKey;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionConstants;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionFunctionTable;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionUtil;
import com.sony.imaging.app.smoothreflection.shooting.SAEProtoExecutorCreator;
import com.sony.imaging.app.smoothreflection.shooting.camera.ThemeController;
import com.sony.imaging.app.util.AppInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class SmoothReflectionApp extends BaseApp {
    private static final String NEX5R = "NEX-5R";
    private static final String NEX5R_CURRENT_FW_VERSION = "1.03";
    private static final String NEX5T = "NEX-5T";
    private static final String NEX5T_CURRENT_FW_VERSION = "1.01";
    private static final String NEX6 = "NEX-6";
    private static final String NEX6_CURRENT_FW_VERSION = "1.03";
    private static final String PROP_MODEL_NAME = "model.name";
    private static final String VERSION = "1.1015";
    private static final String VERSION_TAG = "DetailedVersion";
    private static final String TAG = AppLog.getClassName();
    private static String bootApp = BaseApp.APP_SHOOTING;
    public static int sBootFactor = -1;
    public static boolean sBootFromAPOPowerOff = false;

    public SmoothReflectionApp() {
        doAbortInCaseUncautghtException = true;
        new Factory();
        new SAEProtoExecutorCreator();
        setBootLogo(R.drawable.p_16_dd_parts_sm_launchericon, R.string.STRID_FUNC_SMOOTH_REFLECTION);
    }

    @Override // com.sony.imaging.app.base.BaseApp
    public String getStartApp() {
        AppLog.enter(TAG, AppLog.getMethodName());
        FwVersionCheck();
        AppLog.exit(TAG, AppLog.getMethodName());
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
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onResume();
        AppInfo.notifyAppInfo(this, getPackageName(), getClass().getName(), AppInfo.LARGE_CATEGORY_REC, AppInfo.SMALL_CATEGORY_STILL, PULLING_BACK_KEYS_FOR_SHOOTING, RESUME_KEYS_FOR_SHOOTING);
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.smoothreflection.SmoothReflectionApp.1
            @Override // android.os.MessageQueue.IdleHandler
            public boolean queueIdle() {
                MenuTable.getInstance().createDefaultMenuTree(SmoothReflectionApp.this.getApplicationContext());
                return false;
            }
        });
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onBoot(BootFactor factor) {
        KeyStatus status;
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onBoot(factor);
        Log.i(VERSION_TAG, "DetailedVersion1.1015");
        AppIconView.setIcon(R.drawable.p_16_dd_parts_sm_appicon, R.drawable.p_16_aa_parts_sm_appicon);
        String selectedTheme = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_THEME, ThemeController.TWILIGHTREFLECTION);
        AppNameView.setText(getResources().getString(SmoothReflectionUtil.getInstance().getThemeResourceId(selectedTheme)));
        AppNameView.show(true);
        SmoothReflectionUtil.getInstance().startKikiLog(4244);
        sBootFactor = factor.bootFactor;
        switch (factor.bootFactor) {
            case 0:
                SmoothReflectionUtil.getInstance().setCurrentMenuSelectionScreen(SmoothReflectionConstants.THEME_SELECTION);
                break;
            case 1:
                sBootFromAPOPowerOff = true;
                break;
            case 2:
                sBootFromAPOPowerOff = true;
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
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot, android.app.Activity
    public void onPause() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onShutdown() {
        AppLog.enter(TAG, AppLog.getMethodName());
        SmoothReflectionUtil.getInstance().saveCameraSettings();
        SmoothReflectionUtil.getInstance().clearGammmTable();
        AppLog.exit(TAG, AppLog.getMethodName());
        super.onShutdown();
    }

    @Override // com.sony.imaging.app.base.BaseApp
    protected int getDefaultBackupResName() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return R.xml.default_value;
    }

    @Override // com.sony.imaging.app.base.BaseApp
    protected IFunctionTable getFunctionTable() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return new SmoothReflectionFunctionTable();
    }

    protected void FwVersionCheck() {
        if (SaUtil.isAVIP()) {
            String FwVersion = ScalarProperties.getFirmwareVersion();
            String ModelName = ScalarProperties.getString(PROP_MODEL_NAME);
            Log.d("MODEL NAME", "MODEL NAME = " + ModelName);
            Log.d("FW VERSION", "FW VERSION = " + FwVersion);
            if (ModelName.equals(NEX6)) {
                if (!FwVersion.equals("1.03")) {
                    CautionUtilityClass.getInstance().requestTrigger(SmoothReflectionInfo.CAUTION_ID_DLAPP_SUGGEST_CONFIRMING_UPDATE);
                }
            } else if (ModelName.equals(NEX5R)) {
                if (!FwVersion.equals("1.03")) {
                    CautionUtilityClass.getInstance().requestTrigger(SmoothReflectionInfo.CAUTION_ID_DLAPP_SUGGEST_CONFIRMING_UPDATE);
                }
            } else if (ModelName.equals(NEX5T) && !FwVersion.equals(NEX5T_CURRENT_FW_VERSION)) {
                CautionUtilityClass.getInstance().requestTrigger(SmoothReflectionInfo.CAUTION_ID_DLAPP_SUGGEST_CONFIRMING_UPDATE);
            }
        }
    }
}
