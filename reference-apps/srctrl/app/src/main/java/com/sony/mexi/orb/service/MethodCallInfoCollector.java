package com.sony.mexi.orb.service;

import com.sony.mexi.orb.server.SideWorkExecutor;
import com.sony.mexi.orb.service.Authenticator;
import com.sony.mexi.webapi.Protocol;
import com.sony.mexi.webapi.Status;
import com.sony.mexi.webapi.WebSocketCloseCode;

/* loaded from: classes.dex */
public class MethodCallInfoCollector {
    private static final String REQUEST_HEADER_KEY_HOST = "host";
    private final MethodCallInfo mCallInfo;

    public MethodCallInfoCollector() {
        if (GlobalSettings.getInstance().getAccessLogListener() != null) {
            this.mCallInfo = new MethodCallInfo();
        } else {
            this.mCallInfo = null;
        }
    }

    public void setConnectionInfo(OrbAbstractClient client, Protocol protocol) {
        if (this.mCallInfo != null) {
            this.mCallInfo.setUri(protocol.scheme + "://" + client.getRequestHeader(REQUEST_HEADER_KEY_HOST) + client.getRequestURI());
            this.mCallInfo.setRequester(client);
        }
    }

    public void setMethodInfo(String method, String version) {
        if (this.mCallInfo != null) {
            this.mCallInfo.setMethod(method, version);
        }
    }

    public void setAuthInfo(Authenticator.AuthResult authInfo) {
        if (this.mCallInfo != null) {
            this.mCallInfo.setCheckedAuthMode(authInfo.getCheckedAuthModes());
            if (authInfo.isAuthorized()) {
                AuthMode authorizedMode = authInfo.getAuthorizedMode();
                this.mCallInfo.setAuthorizedMode(authorizedMode);
                if (authorizedMode == AuthMode.COOKIE) {
                    this.mCallInfo.setSessionId(authInfo.getSessionId());
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onRequestSuccess() {
        sendLog(Status.OK.toInt(), "request success");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onRequestFailed(Status status) {
        sendLog(status.toInt(), status.toMessage());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onRequestFailed(Status status, String message) {
        sendLog(status.toInt(), message);
    }

    void onRequestFailed(WebSocketCloseCode code, String message) {
        sendLog(code.toInt(), message);
    }

    private void sendLog(int code, String message) {
        if (this.mCallInfo != null) {
            this.mCallInfo.setStatus(code, message);
            SideWorkExecutor.getInstance().execute(new Runnable() { // from class: com.sony.mexi.orb.service.MethodCallInfoCollector.1
                @Override // java.lang.Runnable
                public void run() {
                    AccessLogListener listener = GlobalSettings.getInstance().getAccessLogListener();
                    if (listener != null) {
                        listener.onCalled(MethodCallInfoCollector.this.mCallInfo);
                    }
                }
            });
        }
    }
}
