package com.sony.imaging.app.synctosmartphone.webapi.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.sony.imaging.app.synctosmartphone.webapi.AutoSyncConstants;
import com.sony.imaging.app.synctosmartphone.webapi.SyncServlet;
import com.sony.imaging.app.synctosmartphone.webapi.availability.AvailabilityDetector;
import com.sony.imaging.app.synctosmartphone.webapi.service.OrbAndroidServiceEx;
import com.sony.imaging.app.synctosmartphone.webapi.servlet.ContentSyncResourceLoader;
import com.sony.imaging.app.synctosmartphone.webapi.servlet.OrbAndroidResourceLoader;
import com.sony.mexi.orb.servlet.accesscontrolinterface.AccessControlInterfaceServlet;
import com.sony.scalar.webapi.lib.authlib.AuthLibManager;
import com.sony.scalar.webapi.lib.authlib.EnableMethodsState;
import com.sony.scalar.webapi.lib.authlib.EnableMethodsStateListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServlet;

/* loaded from: classes.dex */
public class WsController {
    private static Context context;
    private static Intent intent;
    private static boolean isReady;
    private ServiceConnection emptyConn = new ServiceConnection() { // from class: com.sony.imaging.app.synctosmartphone.webapi.util.WsController.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private static final String TAG = WsController.class.getSimpleName();
    private static WsController mInstance = null;

    public static WsController getInstance(Context context2) {
        if (mInstance == null) {
            mInstance = new WsController(context2);
        }
        return mInstance;
    }

    public WsController(Context context2) {
        context = context2;
        isReady = false;
    }

    public synchronized boolean start() {
        if (!isReady) {
            Log.v(TAG, "Starting AuthLibManager...");
            AuthLibManager authLibManager = AuthLibManager.getInstance();
            authLibManager.clearEnableMethods();
            authLibManager.clearEnableMethodsHookHandler();
            authLibManager.clearPrivateMethods();
            authLibManager.initialize();
            ArrayList<String> privateApiList = AvailabilityDetector.getPrivateApiList();
            authLibManager.setPrivateMethods(privateApiList);
            authLibManager.addEnableMethodsStateListener(new EnableMethodsStateListener() { // from class: com.sony.imaging.app.synctosmartphone.webapi.util.WsController.2
                @Override // com.sony.scalar.webapi.lib.authlib.EnableMethodsStateListener
                public void onStateChange(EnableMethodsState state, String devName, String devId, String methods, String sg) {
                    Log.v(WsController.TAG, "AuthLib: state=" + state.name() + ", devName=" + devName + ", devId=" + devId + ", methods=" + methods + ", sg=" + sg);
                }

                @Override // com.sony.scalar.webapi.lib.authlib.EnableMethodsStateListener
                public void onError(int responseCode, String devName, String devId, String methods, String sg) {
                    Log.v(WsController.TAG, "AuthLib: responseCode=" + responseCode + ", devName=" + devName + ", devId=" + devId + ", methods=" + methods + ", sg=" + sg);
                }
            });
            Map<String, HttpServlet> servlets = new HashMap<>();
            SyncServlet sSyncServlet = new SyncServlet();
            OrbAndroidResourceLoader.setAssertManager(context.getAssets());
            OrbAndroidResourceLoader resourceLoader = new OrbAndroidResourceLoader();
            ContentSyncResourceLoader contentSyncResourceLoader = new ContentSyncResourceLoader();
            AccessControlInterfaceServlet accessControlInterfaceServlet = new AccessControlInterfaceServlet();
            servlets.put(AutoSyncConstants.SERVLET_ROOT_PATH_WEBAPI_CONTENTSYNC, sSyncServlet);
            servlets.put("/sony/index.html", resourceLoader);
            servlets.put("/sony/orb-client.min.js", resourceLoader);
            servlets.put("/contentSync/*", contentSyncResourceLoader);
            servlets.put(AutoSyncConstants.SERVLET_ROOT_PATH_WEBAPI_ACCESS_CONTROL, accessControlInterfaceServlet);
            authLibManager.setServlets(servlets);
            intent = new Intent(context, (Class<?>) OrbAndroidServiceEx.class);
            intent.putExtra(AutoSyncConstants.SERVLETS, (Serializable) servlets);
            intent.putExtra(AutoSyncConstants.PORT, AutoSyncConstants.HTTP_PORT_INT);
            intent.putExtra(AutoSyncConstants.NUM_OF_THREADS, 7);
            intent.putExtra(AutoSyncConstants.NUM_OF_BACKLOGS, 10);
            if (bind()) {
                isReady = true;
            } else {
                Log.e(TAG, "Failed binding Web Server");
                isReady = false;
                stopAuthManager();
            }
        } else {
            Log.v(TAG, "Web Server has already been bound.");
        }
        return isReady;
    }

    private boolean bind() {
        try {
            if (!context.bindService(intent, this.emptyConn, 1)) {
                return false;
            }
            Log.v(TAG, "bound");
            return true;
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException in bind");
            return false;
        }
    }

    public synchronized boolean unbind() {
        if (isReady) {
            try {
                context.unbindService(this.emptyConn);
                Log.v(TAG, "unbound");
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            isReady = false;
            stopAuthManager();
        } else {
            Log.v(TAG, "Web Server has already been unbound.");
        }
        return true;
    }

    private void stopAuthManager() {
        Log.v(TAG, "Stopping AuthLibManager...");
        AuthLibManager authLibManager = AuthLibManager.getInstance();
        authLibManager.uninitialize();
    }
}
