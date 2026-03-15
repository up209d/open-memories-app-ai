package com.sony.mexi.server.jni;

import com.sony.mexi.server.jni.HttpServerJni;
import java.io.Serializable;
import java.util.Map;

/* loaded from: classes.dex */
public final class WebSocketJni {

    /* loaded from: classes.dex */
    public interface OnUpgradeRequestListener extends Serializable {
        void onUpgradeRequest(WebSocketConnectionJni webSocketConnectionJni);
    }

    private WebSocketJni() {
    }

    public static void listen(HttpServerJni httpServerJni, final OnUpgradeRequestListener onUpgradeRequestListener) {
        if (onUpgradeRequestListener == null) {
            throw new IllegalArgumentException("OnUpgradeRequestListener must not be null");
        }
        httpServerJni.addUpgradeRequestListener("websocket", new HttpServerJni.OnUpgradeRequestListener() { // from class: com.sony.mexi.server.jni.WebSocketJni.1
            private static final long serialVersionUID = 1;

            @Override // com.sony.mexi.server.jni.HttpServerJni.OnUpgradeRequestListener
            public void onUpgradeRequest(String str, String str2, int i, String str3, Map<String, byte[]> map, long j) {
                OnUpgradeRequestListener.this.onUpgradeRequest(new WebSocketConnectionJni(str, str2, i, str3, map, j));
            }
        });
    }
}
