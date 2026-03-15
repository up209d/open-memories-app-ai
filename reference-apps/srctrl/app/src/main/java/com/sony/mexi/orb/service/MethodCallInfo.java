package com.sony.mexi.orb.service;

import com.sony.mexi.orb.server.OrbClient;
import java.util.EnumSet;

/* loaded from: classes.dex */
public class MethodCallInfo {
    private static final String REQUEST_HEADER_KEY_USER_AGENT = "user-agent";
    private OrbClient mClient;
    private String mSessionId;
    private String mUri = "";
    private String mMethodName = "";
    private String mMethodVersion = "";
    private int mStatusCode = -1;
    private String mStatusMessage = "";
    private AuthMode mAuthMode = AuthMode.NONE;
    private EnumSet<AuthMode> mCheckedAuthModes = EnumSet.noneOf(AuthMode.class);

    public String getUri() {
        return this.mUri;
    }

    public String getApiName() {
        return this.mMethodName;
    }

    public String getApiVersion() {
        return this.mMethodVersion;
    }

    public int getStatusCode() {
        return this.mStatusCode;
    }

    public String getStatusMessage() {
        return this.mStatusMessage;
    }

    public AuthMode getAuthorizedMode() {
        return this.mAuthMode;
    }

    public EnumSet<AuthMode> getCheckedAuthModes() {
        return this.mCheckedAuthModes;
    }

    public String getSessionId() {
        return this.mSessionId;
    }

    public String getRequestHeader(String name) {
        if (this.mClient == null) {
            return null;
        }
        return this.mClient.getRequestHeader(name);
    }

    @Deprecated
    public String getUserAgent() {
        return getRequestHeader(REQUEST_HEADER_KEY_USER_AGENT);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setUri(String uri) {
        this.mUri = uri;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMethod(String name, String version) {
        this.mMethodName = name;
        this.mMethodVersion = version;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setStatus(int code, String message) {
        this.mStatusCode = code;
        this.mStatusMessage = message;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAuthorizedMode(AuthMode authMode) {
        this.mAuthMode = authMode;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCheckedAuthMode(EnumSet<AuthMode> checkedModes) {
        this.mCheckedAuthModes = checkedModes;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSessionId(String sessionId) {
        this.mSessionId = sessionId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRequester(OrbClient client) {
        this.mClient = client;
    }
}
