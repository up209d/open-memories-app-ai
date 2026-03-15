package com.sony.imaging.app.synctosmartphone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.os.Process;
import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.AppNameConstantView;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.synctosmartphone.commonUtil.ConstantsSync;
import com.sony.imaging.app.synctosmartphone.database.DataBaseAdapter;
import com.sony.imaging.app.util.AppInfo;
import com.sony.imaging.app.util.DatabaseUtil;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class ContentSyncApp extends BaseApp {
    private static final String TAG = ContentSyncApp.class.getSimpleName();
    private static String bootApp = BaseApp.APP_SHOOTING;
    private static String POWEROFF_STATE = ConstantsSync.POWEROFF_STATE;
    public static final byte[] SECRET_WORDS_SHA = {83, 111, 110, 121, 32, 68, 105, 103, 105, 116, 97, 108, 32, 73, 109, 97, 103, 105, 110, 103, 32, 80, 108, 97, 121, 32, 77, 101, 109, 111, 114, 105, 101, 115, 32, 67, 97, 109, 101, 114, 97, 32, 65, 112, 112, 115, 32, 50, 48, 49, 50, 47, 49, 50, 47, 50, 49, 32, 70, 114, 105, 100, 97, 121};
    public static final byte[] SEED_AES = {112, 109, 99, 97, 75, 65, 78, 69, 85, 69, 75, 73, 77, 65, 82, 85, 89, 65, 77, 65, 49, 50, 50, 49};
    private static Integer CACHED_APP_STRING_ID = null;

    public ContentSyncApp() {
        new Factory();
        Log.d(TAG, "ContentSyncApp start");
    }

    @Override // com.sony.imaging.app.base.BaseApp
    public String getStartApp() {
        Log.d(TAG, "getStartApp start");
        return bootApp;
    }

    public static int getAppStringId(Context context) {
        Log.d(TAG, "getAppStringId start");
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
        Log.d(TAG, "onResume start");
        super.onResume();
        setAPOState();
        AppInfo.notifyAppInfo(this, getPackageName(), getClass().getName(), AppInfo.LARGE_CATEGORY_REC, AppInfo.SMALL_CATEGORY_STILL, PULLING_BACK_KEYS_FOR_SHOOTING, RESUME_KEYS_FOR_SHOOTING);
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.synctosmartphone.ContentSyncApp.1
            @Override // android.os.MessageQueue.IdleHandler
            public boolean queueIdle() {
                MenuTable.getInstance().createDefaultMenuTree(ContentSyncApp.this.getApplicationContext());
                return false;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onBoot(BootFactor factor) {
        Log.d(TAG, "onBoot start");
        super.onBoot(factor);
        AppIconView.setIcon(R.drawable.p_16_dd_parts_sync_appicon);
        AppNameConstantView.setText(getResources().getString(R.string.STRID_FUNC_AUTOSYNC));
        DatabaseUtil.initialize(DataBaseAdapter.getInstance(), SEED_AES, SECRET_WORDS_SHA);
        NotificationListener listener = DatabaseUtil.getInvalidator(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE);
        MediaNotificationManager.getInstance().setNotificationListener(listener);
        bootApp = POWEROFF_STATE;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.fw.AppRoot, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate start");
        super.onCreate(savedInstanceState);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot, android.app.Activity
    public void onPause() {
        Log.d(TAG, "<<< onPause start");
        super.onPause();
        Process.killProcess(Process.myPid());
        Log.d(TAG, ">>> onPause end");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onShutdown() {
        Log.d(TAG, "onShutdown start");
        NotificationListener listener = DatabaseUtil.getInvalidator(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE);
        MediaNotificationManager.getInstance().removeNotificationListener(listener);
        DatabaseUtil.terminate();
        super.onShutdown();
    }

    private void setAPOState() {
        Intent intent = new Intent();
        intent.setAction("com.android.server.DAConnectionManagerService.apo");
        intent.putExtra("apo_info", "APO/NO");
        sendBroadcast(intent);
    }
}
