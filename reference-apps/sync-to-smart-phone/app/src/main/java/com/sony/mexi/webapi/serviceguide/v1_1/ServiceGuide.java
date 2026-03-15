package com.sony.mexi.webapi.serviceguide.v1_1;

import com.sony.mexi.webapi.Service;
import com.sony.mexi.webapi.serviceguide.v1_0.ProtocolHandler;

/* loaded from: classes.dex */
public interface ServiceGuide extends Service {
    int getServiceInformation(InformationHandler informationHandler);

    int getServiceProtocols(ProtocolHandler protocolHandler);
}
