package com.sony.mexi.orb.service.websocket;

import com.sony.mexi.orb.server.OrbServiceInformation;
import com.sony.mexi.orb.server.PathValidator;
import com.sony.mexi.orb.server.websocket.OrbWebSocketHandler;
import com.sony.mexi.orb.server.websocket.OrbWebSocketHandlerGroup;
import com.sony.mexi.orb.service.Authenticator;
import com.sony.mexi.orb.service.OrbServiceInformationBase;
import com.sony.mexi.orb.service.guide.v1_0.GuideWebSocketHandler;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/* loaded from: classes.dex */
public class WebSocketHandlerGroup implements OrbWebSocketHandlerGroup {
    private Authenticator mAuthenticator;
    private final CountDownLatch mGate;
    private final Map<String, OrbServiceInformation[]> mInformationMap;
    private final Map<String, WebSocketScalarApiHandler> mServiceGp;

    public WebSocketHandlerGroup() {
        this(null);
    }

    public WebSocketHandlerGroup(Authenticator authenticator) {
        this.mServiceGp = new HashMap();
        this.mInformationMap = new ConcurrentHashMap();
        this.mAuthenticator = null;
        this.mGate = new CountDownLatch(1);
        this.mAuthenticator = authenticator;
    }

    @Override // com.sony.mexi.orb.server.OrbServiceGroup
    public void init() {
        boolean isGuideAdded = false;
        Iterator i$ = this.mServiceGp.entrySet().iterator();
        while (i$.hasNext()) {
            if (i$.next().getValue() instanceof GuideWebSocketHandler) {
                isGuideAdded = true;
            }
        }
        if (!isGuideAdded) {
            add("/sony/guide", new GuideWebSocketHandler());
        }
        for (Map.Entry<String, WebSocketScalarApiHandler> service : this.mServiceGp.entrySet()) {
            service.getValue().setAuthenticator(this.mAuthenticator);
            service.getValue().init(service.getKey());
        }
    }

    @Override // com.sony.mexi.orb.server.OrbServiceGroup
    public void initServiceInformation(String protocol) {
        Thread thread = new InfoCollectorThread(protocol);
        thread.start();
    }

    @Override // com.sony.mexi.orb.server.OrbServiceGroup
    public void syncInitServiceInformation() {
        try {
            this.mGate.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* loaded from: classes.dex */
    private class InfoCollectorThread extends Thread {
        String mProtocol;

        InfoCollectorThread(String protocol) {
            this.mProtocol = null;
            this.mProtocol = protocol;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            List<String> protocols = Arrays.asList(this.mProtocol);
            for (Map.Entry<String, WebSocketScalarApiHandler> serviceSet : WebSocketHandlerGroup.this.mServiceGp.entrySet()) {
                String url = serviceSet.getKey();
                WebSocketScalarApiHandler service = serviceSet.getValue();
                String[] urls = url.split("/");
                String relativeUrl = urls[urls.length - 1];
                if (relativeUrl != null) {
                    OrbServiceInformationBase[] infoList = service.getServiceInformation();
                    for (OrbServiceInformationBase info : infoList) {
                        info.setProtocols(protocols);
                        info.setMethodProtocols(protocols);
                    }
                    WebSocketHandlerGroup.this.mInformationMap.put(relativeUrl, infoList);
                }
            }
            WebSocketHandlerGroup.this.mGate.countDown();
        }
    }

    public final void add(String path, WebSocketScalarApiHandler service) {
        PathValidator.throwForInvalidPath(path);
        if (this.mAuthenticator != null) {
            service.setAuthenticator(this.mAuthenticator);
        }
        this.mServiceGp.put(path, service);
    }

    @Override // com.sony.mexi.orb.server.websocket.OrbWebSocketHandlerGroup
    public Map<String, ? extends OrbWebSocketHandler> getHandlerMap() {
        return this.mServiceGp;
    }

    @Override // com.sony.mexi.orb.server.OrbServiceGroup
    public Map<String, OrbServiceInformation[]> getServiceInformationMap() {
        return this.mInformationMap;
    }
}
