package com.sony.mexi.orb.service;

import com.sony.mexi.orb.server.DebugLogger;
import com.sony.mexi.orb.server.OrbClient;
import com.sony.mexi.orb.server.http.StatusCode;
import com.sony.mexi.orb.service.Authenticator;
import com.sony.mexi.webapi.Status;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public abstract class OrbAbstractClient implements OrbClient {
    private static final String TAG = OrbAbstractClient.class.getSimpleName();
    protected long id = -1;
    protected Authenticator.AuthErrorHandler mAuthErrorHandler = null;

    public abstract void appendResults(String str);

    public abstract OrbAbstractClient duplicateClient();

    public abstract void flushResults();

    public abstract void sendError(int i, String str);

    public abstract void sendNotify(String str, JSONArray jSONArray, String str2);

    public abstract void sendNotifyString(String str);

    public abstract void sendResult(JSONArray jSONArray);

    public abstract void sendResultRaw(String str);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void setOnClose(EventEmitter eventEmitter);

    protected abstract void unsetOnClose();

    public final void setId(long id) {
        this.id = id;
    }

    public final void sendError(Status status) {
        sendError(status.toInt(), status.toMessage());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final String createErrorBody(int sc, String msg) {
        JSONArray err = new JSONArray();
        err.put(sc);
        err.put(msg);
        JSONObject obj = new JSONObject();
        try {
            if (sc == StatusCode.FORBIDDEN.toCode() && this.mAuthErrorHandler != null) {
                Map<String, String> authURL = this.mAuthErrorHandler.getAuthURL();
                JSONObject authObj = new JSONObject(authURL);
                obj.put("auth_url", authObj);
            }
            obj.put("error", err);
            if (this.id > 0) {
                obj.put("id", this.id);
            }
        } catch (JSONException e) {
            DebugLogger.error(TAG, e.getMessage());
        }
        return obj.toString();
    }

    public void setAuthErrorHandler(Authenticator.AuthErrorHandler handler) {
        this.mAuthErrorHandler = handler;
    }
}
