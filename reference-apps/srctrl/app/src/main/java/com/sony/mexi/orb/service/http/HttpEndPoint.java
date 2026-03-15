package com.sony.mexi.orb.service.http;

import com.sony.mexi.orb.server.DebugLogger;
import com.sony.mexi.orb.server.http.OrbHttpRequest;
import com.sony.mexi.orb.server.http.StatusCode;
import com.sony.mexi.orb.service.EventEmitter;
import com.sony.mexi.orb.service.OrbAbstractClient;
import java.io.IOException;
import java.io.OutputStream;
import org.json.JSONArray;

/* loaded from: classes.dex */
public class HttpEndPoint extends OrbAbstractClient {
    private static final String TAG = HttpEndPoint.class.getSimpleName();
    private final HttpRequest req;
    private final HttpResponse res;
    private StringBuilder mResults = null;
    OrbHttpRequest.DisconnectListener mDisconnectListener = null;

    public HttpEndPoint(HttpRequest req, HttpResponse res) {
        this.req = req;
        this.res = res;
    }

    @Override // com.sony.mexi.orb.service.OrbAbstractClient
    public OrbAbstractClient duplicateClient() {
        return new HttpEndPoint(this.req, this.res);
    }

    public HttpRequest getRequest() {
        return this.req;
    }

    @Override // com.sony.mexi.orb.server.OrbClient
    public String getRequestHeader(String name) {
        return this.req.getHeader(name);
    }

    @Override // com.sony.mexi.orb.server.OrbClient
    public void setResponseHeader(String name, String value) {
        this.res.setHeader(name, value);
    }

    @Override // com.sony.mexi.orb.server.OrbClient
    public String getRequestURI() {
        return this.req.getRequestURI();
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
        throw new UnsupportedOperationException("Cannot call notify over HTTP");
    }

    @Override // com.sony.mexi.orb.service.OrbAbstractClient
    public void sendNotifyString(String jsonBody) {
        throw new UnsupportedOperationException("Cannot call notify over HTTP");
    }

    @Override // com.sony.mexi.orb.service.OrbAbstractClient
    public void sendError(int sc, String msg) {
        String body = createErrorBody(sc, msg);
        sendErrorRaw(sc, body);
    }

    public void sendErrorRaw(int sc, String body) {
        unsetOnClose();
        StatusCode status = StatusCode.fromCode(sc);
        if (status != null) {
            try {
                this.res.setContentType("application/json");
                this.res.sendStatusAsResponse(status, body);
                return;
            } catch (IOException e) {
                return;
            } finally {
                this.res.end();
            }
        }
        send(body);
    }

    private void send(String body) {
        OutputStream out = null;
        try {
            try {
                this.res.setContentType("application/json");
                byte[] bodyBytes = body.getBytes("UTF-8");
                this.res.setContentLength(bodyBytes.length);
                out = this.res.getOutputStream();
                DebugLogger.debug(TAG, "send: " + body);
                out.write(bodyBytes);
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
            } catch (IOException e2) {
                DebugLogger.debug(TAG, e2.getMessage());
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e3) {
                    }
                }
            }
        } catch (Throwable th) {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e4) {
                }
            }
            throw th;
        }
    }

    @Override // com.sony.mexi.orb.server.OrbClient
    public String getRemoteAddress() {
        return this.req.getRemoteAddress();
    }

    @Override // com.sony.mexi.orb.service.OrbAbstractClient
    public synchronized void setOnClose(final EventEmitter onClose) {
        if (this.mDisconnectListener == null && onClose != null) {
            this.mDisconnectListener = new OrbHttpRequest.DisconnectListener() { // from class: com.sony.mexi.orb.service.http.HttpEndPoint.1
                @Override // com.sony.mexi.orb.server.http.OrbHttpRequest.DisconnectListener
                public void invoke() {
                    onClose.emit(EventEmitter.Event.CLOSE, "Stream Closed");
                }
            };
            this.req.addDisconnectListener(this.mDisconnectListener);
        }
    }

    @Override // com.sony.mexi.orb.service.OrbAbstractClient
    protected synchronized void unsetOnClose() {
        if (this.mDisconnectListener != null) {
            this.req.removeDisconnectListener(this.mDisconnectListener);
            this.mDisconnectListener = null;
        }
    }
}
