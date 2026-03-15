package com.sony.imaging.app.synctosmartphone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.AppNameConstantView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.synctosmartphone.commonUtil.ConstantsSync;
import com.sony.imaging.app.synctosmartphone.commonUtil.NetworkStateUtil;
import com.sony.imaging.app.synctosmartphone.commonUtil.SyncBackUpUtil;
import com.sony.imaging.app.synctosmartphone.commonUtil.SyncKikiLogUtil;
import com.sony.imaging.app.synctosmartphone.database.AutoSyncDataBaseUtil;
import com.sony.imaging.app.synctosmartphone.database.DataBaseAdapter;
import com.sony.imaging.app.util.AppInfo;
import com.sony.imaging.app.util.DatabaseUtil;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class AutoSyncSettingApp extends BaseApp {
    private static final String TAG2 = "DetailedVersion";
    private static final String VERSION = "1.2012";
    private static final String TAG = AutoSyncSettingApp.class.getSimpleName();
    private static String bootApp = BaseApp.APP_SHOOTING;
    private static boolean bIsOnBoot = false;
    private static boolean bTimerStateWaitSystem = false;
    private static boolean bTimerStateDisplay = false;
    private static boolean bTimerOutAppFin = false;
    private static View mSystemWaitView = null;
    private static Handler mHandler = null;
    private static Object mSystemReady = null;
    private static long mLastStartTime = 0;
    private static int mNumOfReservedImages = 0;
    private static int mAutoTransferStartNum = 1;
    private static boolean mResisteredDevice = false;
    public static final byte[] SECRET_WORDS_SHA = {83, 111, 110, 121, 32, 68, 105, 103, 105, 116, 97, 108, 32, 73, 109, 97, 103, 105, 110, 103, 32, 80, 108, 97, 121, 32, 77, 101, 109, 111, 114, 105, 101, 115, 32, 67, 97, 109, 101, 114, 97, 32, 65, 112, 112, 115, 32, 50, 48, 49, 50, 47, 49, 50, 47, 50, 49, 32, 70, 114, 105, 100, 97, 121};
    public static final byte[] SEED_AES = {112, 109, 99, 97, 75, 65, 78, 69, 85, 69, 75, 73, 77, 65, 82, 85, 89, 65, 77, 65, 49, 50, 50, 49};
    private static Integer CACHED_APP_STRING_ID = null;
    private boolean bIsBootLauncherEnable = false;
    private boolean bIsBootLauncher = true;
    private Runnable mWaitReadyTimerTask = new Runnable() { // from class: com.sony.imaging.app.synctosmartphone.AutoSyncSettingApp.1
        @Override // java.lang.Runnable
        public void run() {
            boolean unused = AutoSyncSettingApp.bTimerStateWaitSystem = false;
            if (0 == AutoSyncSettingApp.mLastStartTime || AutoSyncSettingApp.mAutoTransferStartNum > AutoSyncSettingApp.mNumOfReservedImages || !AutoSyncSettingApp.mResisteredDevice) {
                boolean unused2 = AutoSyncSettingApp.bTimerStateDisplay = false;
                boolean unused3 = AutoSyncSettingApp.bTimerOutAppFin = true;
                AutoSyncSettingApp.this.finish(false);
                return;
            }
            AutoSyncSettingApp.mSystemWaitView.setVisibility(0);
            AutoSyncSettingApp.mHandler.postDelayed(AutoSyncSettingApp.this.mDisplayWaitReadyTimerTask, 3000L);
            try {
                Method m = AutoSyncSettingApp.mSystemReady.getClass().getMethod("stop", new Class[0]);
                m.invoke(AutoSyncSettingApp.mSystemReady, new Object[0]);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(AutoSyncSettingApp.TAG, "onResume: cannot call SystemReady.stop");
            }
            boolean unused4 = AutoSyncSettingApp.bTimerStateDisplay = true;
        }
    };
    private Runnable mDisplayWaitReadyTimerTask = new Runnable() { // from class: com.sony.imaging.app.synctosmartphone.AutoSyncSettingApp.2
        @Override // java.lang.Runnable
        public void run() {
            boolean unused = AutoSyncSettingApp.bTimerStateDisplay = false;
            boolean unused2 = AutoSyncSettingApp.bTimerOutAppFin = true;
            AutoSyncSettingApp.this.finish(false);
        }
    };

    public AutoSyncSettingApp() {
        doAbortInCaseUncautghtException = true;
        new Factory();
        setBootLogo(-1, -1);
    }

    @Override // com.sony.imaging.app.base.BaseApp
    public String getStartApp() {
        return bootApp;
    }

    public static int getAppStringId(Context context) {
        if (CACHED_APP_STRING_ID != null) {
            return CACHED_APP_STRING_ID.intValue();
        }
        if (context == null) {
            Log.e(TAG, "ERROR: context is null.");
            return R.string.STRID_FUNC_AUTOSYNC;
        }
        CACHED_APP_STRING_ID = new Integer(R.string.STRID_FUNC_AUTOSYNC);
        return CACHED_APP_STRING_ID.intValue();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.fw.AppRoot, android.app.Activity
    public void onResume() {
        super.onResume();
        if (6 > Environment.getVersionPfAPI()) {
            if (mSystemWaitView != null) {
                ((ViewGroup) getView()).removeView(mSystemWaitView);
                mSystemWaitView = null;
            }
            if (bTimerStateWaitSystem) {
                mHandler.removeCallbacks(this.mWaitReadyTimerTask);
                bTimerStateWaitSystem = false;
            }
            if (bTimerStateDisplay) {
                mHandler.removeCallbacks(this.mDisplayWaitReadyTimerTask);
                bTimerStateDisplay = false;
            }
            Intent intent = getIntent();
            String prmSuspendFactor = intent.getStringExtra("param_off_factor");
            if (prmSuspendFactor != null && ((prmSuspendFactor.equals("OFF_BY_KEY") || prmSuspendFactor.equals("OFF_BY_UM")) && !bIsOnBoot)) {
                AutoSyncDataBaseUtil dbUtil = AutoSyncDataBaseUtil.getInstance();
                SyncBackUpUtil sbuUtil = SyncBackUpUtil.getInstance();
                sbuUtil.Init(getApplicationContext());
                mLastStartTime = sbuUtil.getStartUTC(mLastStartTime);
                mAutoTransferStartNum = sbuUtil.getImageStartNum(mAutoTransferStartNum);
                mResisteredDevice = sbuUtil.getRegister(mResisteredDevice);
                mNumOfReservedImages = dbUtil.getNumberOfTransferReservationFiles(mLastStartTime);
                sbuUtil.finishSettings();
                mHandler = getHandler();
                mHandler.postDelayed(this.mWaitReadyTimerTask, 5000L);
                bTimerStateWaitSystem = true;
                try {
                    Field f = AppRoot.class.getDeclaredField("systemReady");
                    f.setAccessible(true);
                    mSystemReady = f.get(this);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "onResume: cannot call SystemReady.stop");
                }
                mSystemWaitView = new View(getApplicationContext());
                mSystemWaitView = getLayoutInflater().inflate(R.layout.timeout_system_ready, (ViewGroup) null);
                ((ViewGroup) getView()).addView(mSystemWaitView);
                mSystemWaitView.setVisibility(4);
            }
        }
        setAPOState();
        getBootMode();
        if (this.bIsBootLauncher) {
            AppInfo.notifyAppInfo(this, getPackageName(), getClass().getName(), AppInfo.LARGE_CATEGORY_REC, AppInfo.SMALL_CATEGORY_STILL, null, null);
        } else {
            AppInfo.notifyAppInfo(this, null, null, AppInfo.LARGE_CATEGORY_REC, AppInfo.SMALL_CATEGORY_STILL, null, null);
        }
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.synctosmartphone.AutoSyncSettingApp.3
            @Override // android.os.MessageQueue.IdleHandler
            public boolean queueIdle() {
                MenuTable.getInstance().createDefaultMenuTree(AutoSyncSettingApp.this.getApplicationContext());
                return false;
            }
        });
    }

    @Override // com.sony.imaging.app.fw.AppRoot
    protected boolean startSystemReadyOnCreate() {
        Log.i(TAG, "startSystemReadyOnCreate is called. ret = false.");
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onBoot(BootFactor factor) {
        super.onBoot(factor);
        Log.i(TAG, "DetailedVersion 1.2012");
        if (6 > Environment.getVersionPfAPI()) {
            if (mSystemWaitView != null) {
                ((ViewGroup) getView()).removeView(mSystemWaitView);
                mSystemWaitView = null;
            }
            if (bTimerStateWaitSystem) {
                mHandler.removeCallbacks(this.mWaitReadyTimerTask);
                bTimerStateWaitSystem = false;
            }
            if (bTimerStateDisplay) {
                mHandler.removeCallbacks(this.mDisplayWaitReadyTimerTask);
                bTimerStateDisplay = false;
            }
            bIsOnBoot = true;
        }
        DisplayModeObserver.getInstance().controlGraphicsOutputAll(true);
        AppIconView.setIcon(R.drawable.p_16_dd_parts_sync_appicon);
        AppNameConstantView.setText(getResources().getString(R.string.STRID_FUNC_AUTOSYNC));
        DatabaseUtil.initialize(DataBaseAdapter.getInstance(), SEED_AES, SECRET_WORDS_SHA);
        NotificationListener listener = DatabaseUtil.getInvalidator(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE);
        MediaNotificationManager.getInstance().setNotificationListener(listener);
        getBootMode();
        if (this.bIsBootLauncherEnable) {
            if (this.bIsBootLauncher) {
                bootApp = ConstantsSync.BOOT_STATE;
                SyncKikiLogUtil.logAppLaunch();
                return;
            }
            bootApp = ConstantsSync.POWEROFF_STATE;
            Intent intent = getIntent();
            String prmSuspendFactor = intent.getStringExtra("param_off_factor");
            if (prmSuspendFactor.equals("OFF_BY_KEY")) {
                SyncKikiLogUtil.logAppOffByKey();
            } else if (prmSuspendFactor.equals("OFF_BY_UM")) {
                SyncKikiLogUtil.logAppOffByUm();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.fw.AppRoot, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onShutdown() {
        Log.i(TAG, "IMDLAPP6-1547 onShutdown");
        if (!bTimerOutAppFin) {
            appFinishProc();
        }
        super.onShutdown();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot, android.app.Activity
    public void onPause() {
        Log.i(TAG, "IMDLAPP6-1547 onPause");
        if (bTimerOutAppFin) {
            appFinishProc();
        }
        super.onPause();
    }

    void appFinishProc() {
        bIsOnBoot = false;
        bTimerOutAppFin = false;
        if (mSystemWaitView != null) {
            ((ViewGroup) getView()).removeView(mSystemWaitView);
            mSystemWaitView = null;
        }
        if (bTimerStateWaitSystem) {
            mHandler.removeCallbacks(this.mWaitReadyTimerTask);
            bTimerStateWaitSystem = false;
        }
        if (bTimerStateDisplay) {
            mHandler.removeCallbacks(this.mDisplayWaitReadyTimerTask);
            bTimerStateDisplay = false;
        }
        NetworkStateUtil.getInstance().end();
        NotificationListener listener = DatabaseUtil.getInvalidator(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE);
        MediaNotificationManager.getInstance().removeNotificationListener(listener);
        DatabaseUtil.terminate();
    }

    private void setAPOState() {
        Intent intent = new Intent();
        intent.setAction("com.android.server.DAConnectionManagerService.apo");
        intent.putExtra("apo_info", "APO/NO");
        sendBroadcast(intent);
    }

    private void getBootMode() {
        Intent intent = getIntent();
        String prmSuspendFactor = intent.getStringExtra("param_off_factor");
        Log.d(TAG, "onCreate param_off_factor=[" + prmSuspendFactor + "]");
        if (prmSuspendFactor != null) {
            this.bIsBootLauncherEnable = true;
            this.bIsBootLauncher = false;
        } else {
            this.bIsBootLauncherEnable = true;
            this.bIsBootLauncher = true;
        }
    }
}
