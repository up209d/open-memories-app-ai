package com.sony.mexi.orb.service;

import com.sony.mexi.orb.server.OrbClient;
import com.sony.mexi.orb.server.OrbService;
import com.sony.mexi.webapi.Protocol;

/* loaded from: classes.dex */
public interface OrbServiceProvider extends OrbService {
    void addVersion(OrbAbstractVersion orbAbstractVersion);

    void addVersion(String str, OrbAbstractVersion orbAbstractVersion);

    boolean authorize(String str, OrbClient orbClient);

    Authenticator getAuthenticator();

    OrbAbstractVersion getProvider();

    OrbServiceInformationBase[] getServiceInformation();

    Protocol getTransportProtocol();

    void initService();
}
