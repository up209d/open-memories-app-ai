package com.sony.mexi.orb.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
public final class SupportedServiceGpHolder {
    private static SupportedServiceGpHolder sInstance;
    private final Set<OrbServiceGroup> mServiceGps = new HashSet();

    private SupportedServiceGpHolder() {
    }

    public static synchronized SupportedServiceGpHolder getInstance() {
        SupportedServiceGpHolder supportedServiceGpHolder;
        synchronized (SupportedServiceGpHolder.class) {
            if (sInstance == null) {
                sInstance = new SupportedServiceGpHolder();
            }
            supportedServiceGpHolder = sInstance;
        }
        return supportedServiceGpHolder;
    }

    public void addServiceGp(OrbServiceGroup gp) {
        synchronized (this.mServiceGps) {
            this.mServiceGps.add(gp);
        }
    }

    public void removeServiceGp(OrbServiceGroup gp) {
        synchronized (this.mServiceGps) {
            this.mServiceGps.remove(gp);
        }
    }

    public Set<OrbServiceGroup> getServiceGps() {
        Set<OrbServiceGroup> unmodifiableSet;
        synchronized (this.mServiceGps) {
            unmodifiableSet = Collections.unmodifiableSet(new HashSet(this.mServiceGps));
        }
        return unmodifiableSet;
    }
}
