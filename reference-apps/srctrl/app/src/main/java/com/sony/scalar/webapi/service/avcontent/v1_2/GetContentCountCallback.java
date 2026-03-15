package com.sony.scalar.webapi.service.avcontent.v1_2;

import com.sony.mexi.orb.service.Callbacks;
import com.sony.scalar.webapi.service.avcontent.v1_2.common.struct.ContentCount;

/* loaded from: classes.dex */
public interface GetContentCountCallback extends Callbacks {
    void returnCb(ContentCount contentCount);
}
