package com.sony.mexi.webapi.guide.v1_0;

import com.sony.mexi.orb.service.Callbacks;
import com.sony.mexi.webapi.guide.v1_0.common.struct.ServiceInfo;

/* loaded from: classes.dex */
public interface ApiInfoHandler extends Callbacks {
    void handleApiInfo(ServiceInfo[] serviceInfoArr);
}
