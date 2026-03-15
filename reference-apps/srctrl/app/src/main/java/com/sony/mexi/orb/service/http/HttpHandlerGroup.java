package com.sony.mexi.orb.service.http;

import com.sony.mexi.orb.server.OrbServiceInformation;
import com.sony.mexi.orb.server.PathValidator;
import com.sony.mexi.orb.server.http.OrbHttpHandler;
import com.sony.mexi.orb.server.http.OrbHttpHandlerGroup;
import com.sony.mexi.orb.service.Authenticator;
import com.sony.mexi.orb.service.OrbServiceInformationBase;
import com.sony.mexi.orb.service.guide.v1_0.GuideHttpHandler;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/* loaded from: classes.dex */
public class HttpHandlerGroup implements OrbHttpHandlerGroup {
    private Authenticator mDefaultAuthenticator;
    private final CountDownLatch mGate;
    private final Map<String, OrbServiceInformation[]> mInformationMap;
    private final Map<String, GenericHttpRequestHandler> mStrictPathHandlers;
    private final SortedMap<String, GenericHttpRequestHandler> mVaguePathHandlers;

    public HttpHandlerGroup() {
        this(null);
    }

    public HttpHandlerGroup(Authenticator defaultAuthenticator) {
        this.mStrictPathHandlers = new HashMap();
        this.mVaguePathHandlers = new TreeMap(new PathLengthComparator());
        this.mInformationMap = new ConcurrentHashMap();
        this.mDefaultAuthenticator = null;
        this.mGate = new CountDownLatch(1);
        this.mDefaultAuthenticator = defaultAuthenticator;
    }

    @Override // com.sony.mexi.orb.server.OrbServiceGroup
    public void init() {
        boolean isGuideAdded = false;
        Iterator i$ = this.mStrictPathHandlers.entrySet().iterator();
        while (i$.hasNext()) {
            if (i$.next().getValue() instanceof GuideHttpHandler) {
                isGuideAdded = true;
            }
        }
        if (!isGuideAdded) {
            add("/sony/guide", new GuideHttpHandler());
        }
        for (Map.Entry<String, GenericHttpRequestHandler> service : this.mStrictPathHandlers.entrySet()) {
            service.getValue().init(service.getKey());
        }
        for (Map.Entry<String, GenericHttpRequestHandler> service2 : this.mVaguePathHandlers.entrySet()) {
            service2.getValue().init(service2.getKey());
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
            for (Map.Entry<String, GenericHttpRequestHandler> serviceSet : HttpHandlerGroup.this.mStrictPathHandlers.entrySet()) {
                String url = serviceSet.getKey();
                GenericHttpRequestHandler servlet = serviceSet.getValue();
                if (servlet instanceof HttpScalarApiHandler) {
                    String[] urls = url.split("/");
                    String relativeUrl = urls[urls.length - 1];
                    if (relativeUrl != null) {
                        OrbServiceInformationBase[] infoList = ((HttpScalarApiHandler) servlet).getServiceInformation();
                        for (OrbServiceInformationBase info : infoList) {
                            info.setProtocols(protocols);
                            info.setMethodProtocols(protocols);
                        }
                        HttpHandlerGroup.this.mInformationMap.put(relativeUrl, infoList);
                    }
                }
            }
            HttpHandlerGroup.this.mGate.countDown();
        }
    }

    public void add(String path, GenericHttpRequestHandler handler) {
        add(path, handler, false);
    }

    public void add(String path, GenericHttpRequestHandler handler, boolean wildcard) {
        PathValidator.throwForInvalidPath(path);
        if ((handler instanceof HttpScalarApiHandler) && wildcard) {
            throw new IllegalArgumentException("Scalar API path can't use wildcard path");
        }
        if (this.mDefaultAuthenticator != null && (handler instanceof HttpScalarApiHandler)) {
            ((HttpScalarApiHandler) handler).setAuthenticator(this.mDefaultAuthenticator);
        }
        if (wildcard) {
            this.mVaguePathHandlers.put(path, handler);
        } else {
            this.mStrictPathHandlers.put(path, handler);
        }
    }

    /* loaded from: classes.dex */
    private static class PathLengthComparator implements Comparator<String> {
        private PathLengthComparator() {
        }

        @Override // java.util.Comparator
        public int compare(String o1, String o2) {
            return o1.length() == o2.length() ? o1.compareTo(o2) : o2.length() - o1.length();
        }
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpHandlerGroup
    public Map<String, ? extends OrbHttpHandler> getHandlerMap() {
        return this.mStrictPathHandlers;
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpHandlerGroup
    public SortedMap<String, ? extends OrbHttpHandler> getWildcardedHandlerMap() {
        return this.mVaguePathHandlers;
    }

    @Override // com.sony.mexi.orb.server.OrbServiceGroup
    public Map<String, OrbServiceInformation[]> getServiceInformationMap() {
        return this.mInformationMap;
    }
}
