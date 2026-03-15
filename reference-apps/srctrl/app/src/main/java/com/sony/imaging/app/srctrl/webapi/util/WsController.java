package com.sony.imaging.app.srctrl.webapi.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.webapi.availability.AvailabilityDetector;
import com.sony.imaging.app.srctrl.webapi.service.OrbAndroidServiceEx;
import com.sony.imaging.app.srctrl.webapi.servlet.PostviewResourceLoader;
import com.sony.scalar.webapi.lib.authlib.AuthLibManager;
import com.sony.scalar.webapi.lib.authlib.EnableMethodsState;
import com.sony.scalar.webapi.lib.authlib.EnableMethodsStateListener;
import java.util.ArrayList;
import java.util.Arrays;

/* loaded from: classes.dex */
public class WsController {
    private static final String TAG = WsController.class.getSimpleName();
    private Context context;
    private Intent intent;
    private ServiceConnection emptyConn = new ServiceConnection() { // from class: com.sony.imaging.app.srctrl.webapi.util.WsController.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private boolean isReady = false;

    public WsController(Context context) {
        this.context = context;
    }

    public synchronized boolean start() {
        if (!this.isReady) {
            Log.v(TAG, "Starting AuthLibManager...");
            AuthLibManager authLibManager = AuthLibManager.getInstance();
            authLibManager.clearEnableMethods();
            authLibManager.clearEnableMethodsHookHandler();
            authLibManager.clearPrivateMethods();
            authLibManager.initialize();
            ArrayList<String> privateApiList = AvailabilityDetector.getPrivateApiList();
            Log.v(TAG, "  PRIVATE API LIST: " + Arrays.toString(privateApiList.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY)));
            authLibManager.setPrivateMethods(privateApiList);
            authLibManager.addEnableMethodsStateListener(new EnableMethodsStateListener() { // from class: com.sony.imaging.app.srctrl.webapi.util.WsController.2
                @Override // com.sony.scalar.webapi.lib.authlib.EnableMethodsStateListener
                public void onStateChange(EnableMethodsState state, String devName, String devId, String methods, String sg) {
                    Log.v(WsController.TAG, "AuthLib: state=" + state.name() + ", devName=" + devName + ", devId=" + devId + ", methods=" + methods + ", sg=" + sg);
                }

                @Override // com.sony.scalar.webapi.lib.authlib.EnableMethodsStateListener
                public void onError(int responseCode, String devName, String devId, String methods, String sg) {
                    Log.v(WsController.TAG, "AuthLib: responseCode=" + responseCode + ", devName=" + devName + ", devId=" + devId + ", methods=" + methods + ", sg=" + sg);
                }
            });
            this.intent = new Intent(this.context, (Class<?>) OrbAndroidServiceEx.class);
            this.intent.putExtra(SRCtrlConstants.PORT, SRCtrlConstants.HTTP_PORT_INT);
            this.intent.putExtra(SRCtrlConstants.NUM_OF_THREADS, 7);
            this.intent.putExtra(SRCtrlConstants.NUM_OF_BACKLOGS, 10);
            if (bind()) {
                this.isReady = true;
            } else {
                Log.e(TAG, "Failed binding Web Server");
                this.isReady = false;
                stopAuthManager();
            }
        } else {
            Log.v(TAG, "Web Server has already been bound.");
        }
        return this.isReady;
    }

    private boolean bind() {
        try {
            if (!this.context.bindService(this.intent, this.emptyConn, 1)) {
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
        if (this.isReady) {
            PostviewResourceLoader.initData();
            try {
                this.context.unbindService(this.emptyConn);
                Log.v(TAG, "unbound");
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            this.isReady = false;
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
