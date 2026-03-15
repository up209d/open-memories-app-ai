package com.sony.scalar.webapi.service.avcontent.v1_0;

import com.sony.mexi.orb.service.Callbacks;
import com.sony.scalar.webapi.service.avcontent.v1_0.common.struct.ContentScheme;

/* loaded from: classes.dex */
public interface GetSchemeListCallback extends Callbacks {
    void returnCb(ContentScheme[] contentSchemeArr);
}
