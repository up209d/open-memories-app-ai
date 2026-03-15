package com.sony.mexi.orb.service.guide.v1_0;

import com.sony.mexi.orb.service.guide.v1_0.GuideServiceBase;
import com.sony.mexi.webapi.guide.v1_0.ApiInfoHandler;
import com.sony.mexi.webapi.guide.v1_0.ProtocolHandler;
import com.sony.mexi.webapi.guide.v1_0.common.struct.ApiInfoRequest;
import com.sony.mexi.webapi.guide.v1_0.common.struct.ServiceInfo;
import com.sony.mexi.webapi.guide.v1_0.common.struct.ServiceProtocol;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public final class GuideServiceProvider extends GuideServiceBase {
    public static final String DEFAULT_PATH = "/sony/guide";

    @Override // com.sony.mexi.orb.service.guide.v1_0.GuideServiceBase
    public int getServiceProtocols(ServiceProtocol[] infoList, ProtocolHandler handler) {
        for (ServiceProtocol info : infoList) {
            handler.handleProtocols(info.serviceName, info.protocols);
        }
        ((GuideServiceBase.ProtocolHandlerImpl) handler).getClient().flushResults();
        return 0;
    }

    @Override // com.sony.mexi.orb.service.guide.v1_0.GuideServiceBase
    public int getSupportedApiInfo(ServiceInfo[] infoList, ApiInfoRequest req, ApiInfoHandler callback) {
        if (req == null || req.services == null) {
            callback.handleApiInfo(infoList);
            return 0;
        }
        Map<String, ServiceInfo> map = new HashMap<>();
        List<String> filter = Arrays.asList(req.services);
        for (ServiceInfo info : infoList) {
            if (filter.contains(info.service)) {
                map.put(info.service, info);
            }
        }
        callback.handleApiInfo((ServiceInfo[]) map.values().toArray(new ServiceInfo[map.size()]));
        return 0;
    }
}
