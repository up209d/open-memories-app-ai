package com.sony.mexi.orb.server.http;

import com.sony.mexi.orb.server.OrbService;

/* loaded from: classes.dex */
public interface OrbHttpHandler extends OrbService {
    void serve(OrbHttpRequest orbHttpRequest, OrbHttpResponse orbHttpResponse);
}
