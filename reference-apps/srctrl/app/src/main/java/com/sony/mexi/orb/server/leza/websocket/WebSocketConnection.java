package com.sony.mexi.orb.server.leza.websocket;

import com.sony.mexi.orb.server.websocket.OrbWebSocket;
import com.sony.mexi.orb.server.websocket.WebSocketDefs;
import com.sony.mexi.server.jni.WebSocketConnectionJni;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
public class WebSocketConnection implements OrbWebSocket {
    private WebSocketConnectionJni mConnection;
    private final Set<OrbWebSocket.DisconnectListener> mDisconnectListeners = new HashSet();
    private boolean mIsClosed = false;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WebSocketConnection(WebSocketConnectionJni connection) {
        this.mConnection = connection;
        initializeListeners();
    }

    @Override // com.sony.mexi.orb.server.websocket.OrbWebSocket
    public String getRemoteAddress() {
        return this.mConnection.remoteAddress();
    }

    @Override // com.sony.mexi.orb.server.websocket.OrbWebSocket
    public String getRequestHeader(String headerName) {
        try {
            byte[] headerValue = this.mConnection.headers().get(headerName);
            if (headerValue == null) {
                return null;
            }
            return new String(headerValue, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @Override // com.sony.mexi.orb.server.websocket.OrbWebSocket
    public String getUrl() {
        return this.mConnection.url();
    }

    @Override // com.sony.mexi.orb.server.websocket.OrbWebSocket
    public void send(String message) throws IOException {
        boolean ret = this.mConnection.send(message);
        if (!ret) {
            throw WebSocketDefs.STREAM_CLOSED_EXCEPTION;
        }
    }

    @Override // com.sony.mexi.orb.server.websocket.OrbWebSocket
    public void send(byte[] message) throws IOException {
        boolean ret = this.mConnection.send(message, 0, message.length);
        if (!ret) {
            throw WebSocketDefs.STREAM_CLOSED_EXCEPTION;
        }
    }

    @Override // com.sony.mexi.orb.server.websocket.OrbWebSocket
    public boolean addDisconnectListener(OrbWebSocket.DisconnectListener listener) {
        boolean add;
        synchronized (this.mDisconnectListeners) {
            add = this.mIsClosed ? false : this.mDisconnectListeners.add(listener);
        }
        return add;
    }

    @Override // com.sony.mexi.orb.server.websocket.OrbWebSocket
    public boolean removeDisconnectListener(OrbWebSocket.DisconnectListener listener) {
        boolean remove;
        synchronized (this.mDisconnectListeners) {
            remove = this.mIsClosed ? false : this.mDisconnectListeners.remove(listener);
        }
        return remove;
    }

    public void close() {
        this.mConnection.close();
    }

    @Override // com.sony.mexi.orb.server.websocket.OrbWebSocket
    public void close(int statuscode, String message) {
        this.mConnection.close(statuscode, message);
    }

    private void initializeListeners() {
        this.mConnection.addOnCloseListener(new WebSocketConnectionJni.OnCloseListener() { // from class: com.sony.mexi.orb.server.leza.websocket.WebSocketConnection.1
            @Override // com.sony.mexi.server.jni.WebSocketConnectionJni.OnCloseListener
            public void onClose() {
                synchronized (WebSocketConnection.this.mDisconnectListeners) {
                    WebSocketConnection.this.mIsClosed = true;
                    for (OrbWebSocket.DisconnectListener listener : WebSocketConnection.this.mDisconnectListeners) {
                        listener.invoke();
                    }
                    WebSocketConnection.this.mDisconnectListeners.clear();
                }
            }
        });
    }
}
