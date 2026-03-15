package com.sony.mexi.orb.server;

/* loaded from: classes.dex */
public interface OrbProcess {
    OrbServiceGroup getServiceGroup();

    OrbTransport getTransport();

    String protocol();
}
