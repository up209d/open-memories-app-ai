package com.sony.imaging.app.srctrl.webapi.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;
import com.sony.imaging.app.srctrl.webapi.AccessControlInterfaceServlet;
import com.sony.imaging.app.srctrl.webapi.SRCtrlAvContentServlet;
import com.sony.imaging.app.srctrl.webapi.SRCtrlServlet;
import com.sony.imaging.app.srctrl.webapi.availability.AvailabilityDetector;
import com.sony.imaging.app.srctrl.webapi.servlet.ContentsTransfer;
import com.sony.imaging.app.srctrl.webapi.servlet.ContinuousResourceLoader;
import com.sony.imaging.app.srctrl.webapi.servlet.LiveviewChunkTransfer;
import com.sony.imaging.app.srctrl.webapi.servlet.OrbAndroidResourceLoader;
import com.sony.imaging.app.srctrl.webapi.servlet.PostviewResourceLoader;
import com.sony.imaging.app.srctrl.webapi.servlet.SingleResourceLoader;
import com.sony.imaging.app.srctrl.webapi.servlet.StreamingChunkTransfer;
import com.sony.imaging.app.srctrl.webapi.util.SRCtrlServletLogger;
import com.sony.imaging.app.util.PfBugAvailability;
import com.sony.mexi.orb.server.OrbServiceGroup;
import com.sony.mexi.orb.server.leza.Server;
import com.sony.mexi.orb.server.leza.ServerBuilder;
import com.sony.mexi.orb.server.leza.http.HttpProcess;
import com.sony.mexi.orb.server.leza.http.HttpProcessBuilder;
import com.sony.mexi.orb.service.http.GenericHttpRequestHandler;
import com.sony.mexi.orb.service.http.HttpHandlerGroup;
import com.sony.scalar.webapi.lib.authlib.AuthLibManager;
import com.sony.scalar.webapi.lib.authlib.EnableMethodsState;
import com.sony.scalar.webapi.lib.authlib.EnableMethodsStateListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class OrbAndroidServiceEx extends Service {
    public static final String AUTO_SERVICE_GUIDE = "autoServiceGuide";
    private static final String TAG = OrbAndroidServiceEx.class.getSimpleName();
    private static CountDownLatch latch = null;
    protected Server server = null;
    protected ExecutorService pool = null;
    private boolean DBG = false;
    EnableMethodsStateListener myEnableMethodsStateListener = null;

    /* loaded from: classes.dex */
    private static class LezaServerStatusListener implements Server.ServerStatusListener {
        static final LezaServerStatusListener INSTANCE = new LezaServerStatusListener();

        private LezaServerStatusListener() {
        }

        @Override // com.sony.mexi.orb.server.leza.Server.ServerStatusListener
        public void onServerStatusChange(Server.Status serverStatus, Server.Reason statusReason) {
            Log.i(OrbAndroidServiceEx.TAG, "Server status :" + serverStatus + " reason: " + statusReason);
            if (serverStatus == Server.Status.CLOSED) {
                OrbAndroidServiceEx.latch.countDown();
            }
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        GenericHttpRequestHandler singleLoader;
        Log.v(TAG, "onBind");
        startAuthManager();
        List<OrbServiceGroup> serviceGroups = new ArrayList<>();
        HttpHandlerGroup servletGp = new HttpHandlerGroup();
        SRCtrlServlet sRCtrlServlet = new SRCtrlServlet();
        servletGp.add(SRCtrlConstants.SERVLET_ROOT_PATH_WEBAPI_CAMERA, sRCtrlServlet);
        if (!PfBugAvailability.encodeAtPlay) {
            SRCtrlAvContentServlet sRCtrlAvContentServlet = new SRCtrlAvContentServlet();
            servletGp.add(SRCtrlConstants.SERVLET_ROOT_PATH_WEBAPI_AVCONTENT, sRCtrlAvContentServlet);
        }
        OrbAndroidResourceLoader.setAssertManager(SRCtrlEnvironment.getInstance().getContext().getAssets());
        OrbAndroidResourceLoader resourceLoader = new OrbAndroidResourceLoader();
        LiveviewChunkTransfer liveviewChunkTransfer = new LiveviewChunkTransfer();
        ContentsTransfer contentsTransfer = ContentsTransfer.getInstance();
        StreamingChunkTransfer streamingChunkTransfer = new StreamingChunkTransfer();
        GenericHttpRequestHandler continuousLoader = null;
        if (SRCtrlEnvironment.getInstance().isEnableContinuousShooting()) {
            singleLoader = SingleResourceLoader.getInstance();
            continuousLoader = ContinuousResourceLoader.getInstance();
        } else {
            singleLoader = new PostviewResourceLoader();
        }
        servletGp.add("/sony/index.html", resourceLoader);
        servletGp.add("/sony/orb-client.min.js", resourceLoader);
        servletGp.add("/postview", singleLoader, true);
        if (continuousLoader != null) {
            servletGp.add("/continuous", continuousLoader, true);
        }
        servletGp.add("/liveview/liveviewstream", liveviewChunkTransfer);
        servletGp.add("/contentstransfer", contentsTransfer, true);
        servletGp.add("/streaming", streamingChunkTransfer, true);
        AccessControlInterfaceServlet accessControlInterfaceServlet = new AccessControlInterfaceServlet();
        servletGp.add(SRCtrlConstants.SERVLET_ROOT_PATH_WEBAPI_ACCESS_CONTROL, accessControlInterfaceServlet);
        serviceGroups.add(servletGp);
        if (this.DBG) {
            new SRCtrlServletLogger();
        }
        this.pool = Executors.newFixedThreadPool(intent.getIntExtra(SRCtrlConstants.NUM_OF_THREADS, 7));
        HttpProcess httpProcess = new HttpProcessBuilder().setHandlerGroup(servletGp).setExecutorService(this.pool).build();
        Log.v(TAG, String.valueOf(intent.getIntExtra(SRCtrlConstants.PORT, SRCtrlConstants.HTTP_PORT_INT)));
        Log.v(TAG, String.valueOf(intent.getIntExtra(SRCtrlConstants.NUM_OF_BACKLOGS, 10)));
        this.server = new ServerBuilder(intent.getIntExtra(SRCtrlConstants.PORT, SRCtrlConstants.HTTP_PORT_INT)).setBacklog(intent.getIntExtra(SRCtrlConstants.NUM_OF_BACKLOGS, 10)).setMaxBodySizeInByte(SRCtrlConstants.MEXI_SERVER_TRANSFER_MAX_BODY_SIZE).setServerStatusListener(LezaServerStatusListener.INSTANCE).setMaxConnections(10).addProcess(httpProcess).build();
        this.server.startup();
        Binder binder = new MyBinder();
        return binder;
    }

    private void serverShutdown() {
        Log.v(TAG, "server shutdown");
        if (this.server != null) {
            latch = new CountDownLatch(1);
            try {
                this.server.shutdown();
                try {
                    Log.v(TAG, "onUnbind shutdown Wating.");
                    latch.await(4500L, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    Log.v(TAG, "InterruptedException! : " + e.getMessage());
                }
                Log.v(TAG, "onUnbind shutdown End");
            } finally {
                latch = null;
                this.server = null;
            }
        }
    }

    private void executorShutdown() {
        Log.v(TAG, "pool shutdown");
        if (this.pool != null) {
            this.pool.shutdown();
            try {
                Log.v(TAG, "pool awaitTermination");
                if (!this.pool.awaitTermination(100L, TimeUnit.MILLISECONDS)) {
                    Log.v(TAG, "pool shutdownNow");
                    this.pool.shutdownNow();
                    if (!this.pool.awaitTermination(200L, TimeUnit.MILLISECONDS)) {
                        Log.v(TAG, "pool not terminated.");
                    }
                }
            } catch (InterruptedException e) {
                Log.v(TAG, "pool shutdownNow");
                this.pool.shutdownNow();
            }
            if (!this.pool.isShutdown()) {
                Log.e(TAG, "pool not shutdowned!");
            }
            if (!this.pool.isTerminated()) {
                Log.e(TAG, "pool not terminated!");
            }
        }
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "Start onUnbind");
        stopAuthManager();
        if (this.server != null) {
            serverShutdown();
        }
        if (this.pool != null) {
            executorShutdown();
        }
        boolean ret = super.onUnbind(intent);
        Log.v(TAG, "Complete onUnbind");
        return ret;
    }

    @Override // android.app.Service
    public void onDestroy() {
        Log.v(TAG, "Start onDestroy");
        if (this.server != null) {
            serverShutdown();
        }
        if (this.pool != null) {
            executorShutdown();
        }
        super.onDestroy();
        Log.v(TAG, "Complete onDestroy");
    }

    /* loaded from: classes.dex */
    public class MyBinder extends Binder {
        public MyBinder() {
        }

        public MyBinder getService() {
            return this;
        }
    }

    private void startAuthManager() {
        Log.v(TAG, "Starting AuthLibManager...");
        AuthLibManager authLibManager = AuthLibManager.getInstance();
        authLibManager.clearEnableMethods();
        authLibManager.clearEnableMethodsHookHandler();
        authLibManager.clearPrivateMethods();
        authLibManager.initialize();
        ArrayList<String> privateApiList = AvailabilityDetector.getPrivateApiList();
        Log.v(TAG, "  PRIVATE API LIST: " + Arrays.toString(privateApiList.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY)));
        authLibManager.setPrivateMethods(privateApiList);
        this.myEnableMethodsStateListener = new MyEnableMethodsStateListener();
        authLibManager.addEnableMethodsStateListener(this.myEnableMethodsStateListener);
    }

    private void stopAuthManager() {
        Log.v(TAG, "Stopping AuthLibManager...");
        AuthLibManager authLibManager = AuthLibManager.getInstance();
        authLibManager.removeEnableMethodsEventListener(this.myEnableMethodsStateListener);
        this.myEnableMethodsStateListener = null;
        authLibManager.clearPrivateMethods();
        authLibManager.clearEnableMethodsHookHandler();
        authLibManager.clearEnableMethods();
        authLibManager.uninitialize();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MyEnableMethodsStateListener implements EnableMethodsStateListener {
        private MyEnableMethodsStateListener() {
        }

        @Override // com.sony.scalar.webapi.lib.authlib.EnableMethodsStateListener
        public void onStateChange(EnableMethodsState state, String devName, String devId, String methods, String sg) {
            Log.v(OrbAndroidServiceEx.TAG, "AuthLib: state=" + state.name() + ", devName=" + devName + ", devId=" + devId + ", methods=" + methods + ", sg=" + sg);
        }

        @Override // com.sony.scalar.webapi.lib.authlib.EnableMethodsStateListener
        public void onError(int responseCode, String devName, String devId, String methods, String sg) {
            Log.v(OrbAndroidServiceEx.TAG, "AuthLib: responseCode=" + responseCode + ", devName=" + devName + ", devId=" + devId + ", methods=" + methods + ", sg=" + sg);
        }
    }
}
