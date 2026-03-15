package com.sony.mexi.orb.service.guide.v1_0;

import com.sony.mexi.orb.service.http.HttpScalarApiHandler;

/* loaded from: classes.dex */
public final class GuideHttpHandler extends HttpScalarApiHandler {
    @Override // com.sony.mexi.orb.service.OrbServiceProvider
    public void initService() {
        addVersion(new GuideServiceProvider());
    }
}
