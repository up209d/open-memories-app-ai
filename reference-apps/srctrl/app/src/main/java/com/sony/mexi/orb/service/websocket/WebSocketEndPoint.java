package com.sony.mexi.orb.service.websocket;

import com.sony.mexi.orb.server.DebugLogger;
import com.sony.mexi.orb.server.websocket.OrbWebSocket;
import com.sony.mexi.orb.service.EventEmitter;
import com.sony.mexi.orb.service.OrbAbstractClient;
import java.io.IOException;
import java.util.Locale;
import org.json.JSONArray;

/* loaded from: classes.dex */
public class WebSocketEndPoint extends OrbAbstractClient {
    private static final String TAG = WebSocketEndPoint.class.getSimpleName();
    private final OrbWebSocket mWs;
    private StringBuilder mResults = null;
    private OrbWebSocket.DisconnectListener mDisconnectListener = null;

    public WebSocketEndPoint(OrbWebSocket ws) {
        this.mWs = ws;
    }

    public OrbWebSocket connection() {
        return this.mWs;
    }

    @Override // com.sony.mexi.orb.service.OrbAbstractClient
    public OrbAbstractClient duplicateClient() {
        return new WebSocketEndPoint(this.mWs);
    }

    @Override // com.sony.mexi.orb.server.OrbClient
    public String getRequestHeader(String name) {
        return this.mWs.getRequestHeader(name.toLowerCase(Locale.US));
    }

    @Override // com.sony.mexi.orb.server.OrbClient
    public String getRemoteAddress() {
        return this.mWs.getRemoteAddress();
    }

    @Override // com.sony.mexi.orb.server.OrbClient
    public String getRequestURI() {
        return this.mWs.getUrl();
    }

    @Override // com.sony.mexi.orb.server.OrbClient
    public void setResponseHeader(String name, String value) {
        throw new UnsupportedOperationException();
    }

    @Override // com.sony.mexi.orb.service.OrbAbstractClient
    public void sendResult(JSONArray val) {
        flushResultRaw(val.toString());
    }

    @Override // com.sony.mexi.orb.service.OrbAbstractClient
    public void sendResultRaw(String jsonElement) {
        flushResultRaw("[" + jsonElement + "]");
    }

    private void flushResultRaw(String jsonArray) {
        unsetOnClose();
        send("{\"result\":" + jsonArray + ",\"id\":" + this.id + "}");
    }

    @Override // com.sony.mexi.orb.service.OrbAbstractClient
    public void flushResults() {
        unsetOnClose();
        if (this.mResults != null) {
            send("{\"results\":[" + ((Object) this.mResults) + "],\"id\":" + this.id + "}");
            return;
        }
        throw new IllegalStateException("results buffer is null");
    }

    @Override // com.sony.mexi.orb.service.OrbAbstractClient
    public void appendResults(String jsonElement) {
        if (jsonElement != null) {
            if (this.mResults == null) {
                this.mResults = new StringBuilder(jsonElement);
            } else if (this.mResults.length() == 0 || jsonElement.equals("")) {
                this.mResults.append(jsonElement);
            } else {
                this.mResults.append(',').append(jsonElement);
            }
        }
    }

    @Override // com.sony.mexi.orb.service.OrbAbstractClient
    public void sendNotify(String method, JSONArray params, String version) {
        send("{\"method\":\"" + method + "\",\"params\":" + params.toString() + ",\"version\":\"" + version + "\"}");
    }

    @Override // com.sony.mexi.orb.service.OrbAbstractClient
    public void sendNotifyString(String jsonBody) {
        send(jsonBody);
    }

    @Override // com.sony.mexi.orb.service.OrbAbstractClient
    public void sendError(int sc, String msg) {
        unsetOnClose();
        send(createErrorBody(sc, msg));
    }

    private void send(String body) {
        try {
            this.mWs.send(body);
            DebugLogger.debug(TAG, "Send(WebSocket): " + body);
        } catch (IOException e) {
            DebugLogger.debug(TAG, "Send(WebSocket): " + e.getMessage());
        }
    }

    @Override // com.sony.mexi.orb.service.OrbAbstractClient
    public synchronized void setOnClose(final EventEmitter listener) {
        if (this.mDisconnectListener == null && listener != null) {
            this.mDisconnectListener = new OrbWebSocket.DisconnectListener() { // from class: com.sony.mexi.orb.service.websocket.WebSocketEndPoint.1
                @Override // com.sony.mexi.orb.server.websocket.OrbWebSocket.DisconnectListener
                public void invoke() {
                    listener.emit(EventEmitter.Event.CLOSE, "WebSocket Connection closed");
                }
            };
            this.mWs.addDisconnectListener(this.mDisconnectListener);
        }
    }

    @Override // com.sony.mexi.orb.service.OrbAbstractClient
    protected synchronized void unsetOnClose() {
        if (this.mDisconnectListener != null) {
            this.mWs.removeDisconnectListener(this.mDisconnectListener);
            this.mDisconnectListener = null;
        }
    }

    public void close(int statuscode, String message) {
        this.mWs.close(statuscode, message);
    }
}
