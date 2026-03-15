package com.sony.scalar.webapi.service.avcontent.v1_0;

import com.sony.mexi.orb.service.Callbacks;
import com.sony.scalar.webapi.service.avcontent.v1_0.common.struct.ContentSource;

/* loaded from: classes.dex */
public interface GetSourceListCallback extends Callbacks {
    void returnCb(ContentSource[] contentSourceArr);
}
