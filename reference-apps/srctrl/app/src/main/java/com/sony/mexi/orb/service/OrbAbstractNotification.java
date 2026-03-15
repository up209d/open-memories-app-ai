package com.sony.mexi.orb.service;

import com.sony.mexi.orb.server.websocket.OrbWebSocket;
import com.sony.mexi.orb.service.EventEmitter;
import com.sony.mexi.orb.service.websocket.WebSocketEndPoint;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;

/* loaded from: classes.dex */
public abstract class OrbAbstractNotification {
    private final Map<OrbWebSocket, OrbAbstractClient> mClients = new HashMap();
    private OnNotificationListenersStatus mStatus = null;

    /* loaded from: classes.dex */
    public interface OnNotificationListenersStatus {
        void onListenersActive();

        void onListenersEmpty();
    }

    public abstract String getAuthLevel();

    public abstract String getName();

    public abstract String getVersion();

    public abstract String[] parameterTypes();

    public boolean addClient(OrbAbstractClient client) {
        if (!(client instanceof WebSocketEndPoint)) {
            return false;
        }
        final WebSocketEndPoint cl = (WebSocketEndPoint) client.duplicateClient();
        synchronized (this.mClients) {
            if (this.mClients.containsKey(cl.connection())) {
                return false;
            }
            this.mClients.put(cl.connection(), cl);
            if (this.mClients.size() == 1 && this.mStatus != null) {
                this.mStatus.onListenersActive();
            }
            cl.setOnClose(new EventEmitter() { // from class: com.sony.mexi.orb.service.OrbAbstractNotification.1
                @Override // com.sony.mexi.orb.service.EventEmitter
                public void emit(EventEmitter.Event event, String message) {
                    switch (AnonymousClass2.$SwitchMap$com$sony$mexi$orb$service$EventEmitter$Event[event.ordinal()]) {
                        case 1:
                            OrbAbstractNotification.this.removeClient(cl);
                            return;
                        default:
                            return;
                    }
                }
            });
            return true;
        }
    }

    /* renamed from: com.sony.mexi.orb.service.OrbAbstractNotification$2, reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$sony$mexi$orb$service$EventEmitter$Event = new int[EventEmitter.Event.values().length];

        static {
            try {
                $SwitchMap$com$sony$mexi$orb$service$EventEmitter$Event[EventEmitter.Event.CLOSE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
        }
    }

    public boolean removeClient(OrbAbstractClient client) {
        boolean z = false;
        if (client instanceof WebSocketEndPoint) {
            synchronized (this.mClients) {
                WebSocketEndPoint cl = (WebSocketEndPoint) client.duplicateClient();
                OrbAbstractClient remCl = this.mClients.remove(cl.connection());
                if (remCl != null) {
                    if (this.mClients.size() == 0 && this.mStatus != null) {
                        this.mStatus.onListenersEmpty();
                    }
                    z = true;
                }
            }
        }
        return z;
    }

    public void sendNotify(JSONArray params) {
        synchronized (this.mClients) {
            String json = "{\"method\":\"" + getName() + "\",\"params\":" + params + ",\"version\":\"" + getVersion() + "\"}";
            for (Map.Entry<OrbWebSocket, OrbAbstractClient> client : this.mClients.entrySet()) {
                client.getValue().sendNotifyString(json);
            }
        }
    }

    public void setOnNotificationListenersStatus(OnNotificationListenersStatus status) {
        synchronized (this.mClients) {
            this.mStatus = status;
        }
    }

    public final boolean isEnabled() {
        boolean z;
        synchronized (this.mClients) {
            z = this.mClients.size() >= 1;
        }
        return z;
    }
}
