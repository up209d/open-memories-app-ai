package com.sony.imaging.app.each;

import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.each.shooting.EachExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.touchlessshutter.R;
import com.sony.imaging.app.util.AppInfo;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.sysutil.didep.Kikilog;

/* loaded from: classes.dex */
public class EachApp extends BaseApp {
    private static final String TAG = "EachApp";
    public static final int kikilog_subid_BootTime = 327776;
    public static final int kikilog_subid_BulbCaptureTimes = 327778;
    public static final int kikilog_subid_MovieCaptureTimes = 327779;
    public static final int kikilog_subid_NormalCaptureTimes = 327777;
    private static String bootApp = BaseApp.APP_SHOOTING;
    public static boolean ExposingByTouchLessShutter = false;
    public static boolean RequestCautionAtEE = false;
    public static Kikilog.Options kikilog_options = new Kikilog.Options();

    public EachApp() {
        doAbortInCaseUncautghtException = true;
        new Factory();
        new EachExecutorCreator();
        Kikilog.Options options = kikilog_options;
        kikilog_options.getClass();
        options.recType = 32;
        setBootLogo(R.drawable.p_16_dd_parts_xxxxxxxx_launchericon, R.string.STRID_FUNC_MYAPP_NAME);
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
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.each.EachApp.1
            @Override // android.os.MessageQueue.IdleHandler
            public boolean queueIdle() {
                MenuTable.getInstance().createDefaultMenuTree(EachApp.this.getApplicationContext());
                return false;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onBoot(BootFactor factor) {
        KeyStatus status;
        super.onBoot(factor);
        ExposingByTouchLessShutter = false;
        AppIconView.setIcon(R.drawable.p_16_dd_parts_xxxxxxxx_appicon, R.drawable.p_16_aa_parts_xxxxxxxx_appicon);
        AppNameView.setText(getResources().getString(R.string.STRID_FUNC_MYAPP_NAME));
        AppNameView.show(true);
        switch (factor.bootFactor) {
            case 0:
                Kikilog.setUserLog(kikilog_subid_BootTime, kikilog_options);
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

    @Override // com.sony.imaging.app.base.BaseApp
    protected int getDefaultBackupResName() {
        return R.xml.default_value;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onShutdown() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onShutdown();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static boolean is_cancelExposure_available() {
        String version = ScalarProperties.getString("version.platform");
        int apiVersion = Integer.parseInt(version.substring(version.indexOf(".") + 1));
        Log.v(TAG, "apiVersion : " + apiVersion);
        String modelName = ScalarProperties.getString("model.name");
        Log.v(TAG, "modelName : " + modelName);
        return apiVersion >= 6 && !modelName.equals("DSC-RX100M3");
    }
}
