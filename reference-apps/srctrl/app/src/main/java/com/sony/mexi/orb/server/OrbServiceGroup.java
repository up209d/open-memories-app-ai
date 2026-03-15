package com.sony.mexi.orb.server;

import java.util.Map;

/* loaded from: classes.dex */
public interface OrbServiceGroup {
    Map<String, OrbServiceInformation[]> getServiceInformationMap();

    void init();

    void initServiceInformation(String str);

    void syncInitServiceInformation();
}
