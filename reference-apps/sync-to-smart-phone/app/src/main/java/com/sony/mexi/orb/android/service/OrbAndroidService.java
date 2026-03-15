package com.sony.mexi.orb.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.sony.imaging.app.synctosmartphone.webapi.AutoSyncConstants;
import com.sony.imaging.app.synctosmartphone.webapi.service.OrbAndroidServiceEx;
import com.sony.mexi.orb.server.OrbServer;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServlet;

/* loaded from: classes.dex */
public class OrbAndroidService extends Service {
    protected OrbServer server = null;
    protected ExecutorService pool = null;

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        Map<String, HttpServlet> servlets = (Map) intent.getSerializableExtra(AutoSyncConstants.SERVLETS);
        this.pool = Executors.newFixedThreadPool(intent.getIntExtra("numThreads", 2));
        this.server = new OrbServer(servlets, intent.getIntExtra(AutoSyncConstants.PORT, 10000), this.pool, intent.getIntExtra(AutoSyncConstants.NUM_OF_BACKLOGS, 10), intent.getBooleanExtra(OrbAndroidServiceEx.AUTO_SERVICE_GUIDE, false));
        this.server.start();
        return null;
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        this.server.finish();
        if (this.pool != null) {
            this.pool.shutdown();
            while (!this.pool.isTerminated()) {
                try {
                    this.pool.awaitTermination(60L, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        this.server.shutdown();
        return super.onUnbind(intent);
    }
}
