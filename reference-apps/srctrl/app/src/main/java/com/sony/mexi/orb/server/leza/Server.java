package com.sony.mexi.orb.server.leza;

import com.sony.mexi.orb.server.OrbTransport;
import com.sony.mexi.server.jni.HttpServerJni;
import com.sony.mexi.server.jni.ProcessJni;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class Server {
    public static final int CONNECTIONS_NO_LIMIT = -1;
    private final HttpServerJni mHttpServer;
    private List<ServerProcess> mProcessList;

    /* loaded from: classes.dex */
    public interface ServerStatusListener {
        void onServerStatusChange(Status status, Reason reason);
    }

    /* loaded from: classes.dex */
    public static final class GlobalConfig {
        private static GlobalConfig sInstance = new GlobalConfig();
        private String mJniPath = null;
        private boolean mJniLoad = true;

        static /* synthetic */ boolean access$000() {
            return shouldLoadJni();
        }

        private GlobalConfig() {
        }

        public static void setJniPath(String jniPath) {
            sInstance.mJniPath = jniPath;
        }

        public static String getJniPath() {
            return sInstance.mJniPath;
        }

        public static void setJniLoad(boolean jniLoad) {
            sInstance.mJniLoad = jniLoad;
        }

        private static boolean shouldLoadJni() {
            return sInstance.mJniLoad;
        }
    }

    /* loaded from: classes.dex */
    static final class JniMainLoopThread extends Thread {
        private static final JniMainLoopThread INSTANCE = new JniMainLoopThread();

        private JniMainLoopThread() {
            super("server-process");
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            ProcessJni.run();
        }
    }

    /* loaded from: classes.dex */
    public enum Status {
        OPENED(HttpServerJni.HttpServerStatus.OPENED),
        FAILURE(null),
        CLOSED(HttpServerJni.HttpServerStatus.CLOSED);

        private final HttpServerJni.HttpServerStatus lezaStatus;

        Status(HttpServerJni.HttpServerStatus lezaStatus) {
            this.lezaStatus = lezaStatus;
        }

        static Status from(HttpServerJni.HttpServerStatus lezaStatus) {
            Status[] arr$ = values();
            for (Status stat : arr$) {
                if (stat.lezaStatus == lezaStatus) {
                    return stat;
                }
            }
            return CLOSED;
        }
    }

    /* loaded from: classes.dex */
    public enum Reason {
        OK(HttpServerJni.HttpError.STATUS_OK),
        INVALID_HOST_OR_PORT(HttpServerJni.HttpError.ERROR_INVALID_HOST_OR_PORT),
        ADDRESS_IN_USE(HttpServerJni.HttpError.ERROR_ADDRESS_IN_USE),
        UNKNOWN(HttpServerJni.HttpError.ERROR_UNKNOWN);

        private final HttpServerJni.HttpError lezaReazon;

        Reason(HttpServerJni.HttpError lezaReason) {
            this.lezaReazon = lezaReason;
        }

        static Reason from(HttpServerJni.HttpError lezaReason) {
            Reason[] arr$ = values();
            for (Reason reason : arr$) {
                if (reason.lezaReazon == lezaReason) {
                    return reason;
                }
            }
            return UNKNOWN;
        }
    }

    static {
        if (GlobalConfig.access$000()) {
            if (GlobalConfig.getJniPath() != null) {
                System.load(GlobalConfig.getJniPath());
            } else {
                System.loadLibrary("lezaserverjni");
            }
        }
        JniMainLoopThread.INSTANCE.start();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Server(int port, String host, int backlog, int maxBodySize, int maxConnections, int timeoutInMilliSec, Map<OrbTransport, ServerProcess> processMap, final ServerStatusListener statusListener) {
        this.mHttpServer = new HttpServerJni(new HttpServerJni.OnStatusChangeListener() { // from class: com.sony.mexi.orb.server.leza.Server.1
            @Override // com.sony.mexi.server.jni.HttpServerJni.OnStatusChangeListener
            public void onStatus(HttpServerJni.HttpServerStatus httpServerStatus, HttpServerJni.HttpError httpError) {
                Status status;
                Reason reason = Reason.from(httpError);
                if (reason == Reason.OK) {
                    status = Status.from(httpServerStatus);
                } else {
                    status = Status.FAILURE;
                }
                statusListener.onServerStatusChange(status, reason);
            }
        }, port, host, backlog);
        this.mHttpServer.setMaxConnections(maxConnections);
        this.mHttpServer.setTimeout(timeoutInMilliSec);
        this.mProcessList = Collections.unmodifiableList(new ArrayList(processMap.values()));
        for (ServerProcess process : this.mProcessList) {
            process.init(this.mHttpServer, maxBodySize);
        }
    }

    public void startup() {
        this.mHttpServer.open();
    }

    public void shutdown() {
        for (ServerProcess process : this.mProcessList) {
            process.shutdown();
        }
        this.mHttpServer.close();
    }
}
