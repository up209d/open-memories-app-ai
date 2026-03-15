package com.sony.imaging.app.synctosmartphone.webapi.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.sony.imaging.app.synctosmartphone.webapi.AutoSyncConstants;
import com.sony.mexi.orb.server.OrbServer;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* loaded from: classes.dex */
public class OrbAndroidServiceEx extends Service {
    public static final String AUTO_SERVICE_GUIDE = "autoServiceGuide";
    private static final String TAG = OrbAndroidServiceEx.class.getSimpleName();
    protected OrbServer server = null;
    protected ExecutorService pool = null;

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "onBind");
        Log.v(TAG, "intent = " + intent);
        Log.v(TAG, "intent.getIntExtra() = " + intent.getIntExtra(AutoSyncConstants.NUM_OF_THREADS, 7));
        this.pool = Executors.newFixedThreadPool(intent.getIntExtra(AutoSyncConstants.NUM_OF_THREADS, 7));
        Log.v(TAG, "pool = " + this.pool);
        this.server = new OrbServer((Map) intent.getSerializableExtra(AutoSyncConstants.SERVLETS), intent.getIntExtra(AutoSyncConstants.PORT, AutoSyncConstants.HTTP_PORT_INT), this.pool, intent.getIntExtra(AutoSyncConstants.NUM_OF_BACKLOGS, 10), intent.getBooleanExtra(AUTO_SERVICE_GUIDE, true));
        Log.v(TAG, "server = " + this.server);
        this.server.start();
        return null;
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        this.server.finish();
        this.pool.shutdown();
        this.server.shutdown();
        this.pool.shutdownNow();
        return super.onUnbind(intent);
    }

    @Override // android.app.Service
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();
    }
}
