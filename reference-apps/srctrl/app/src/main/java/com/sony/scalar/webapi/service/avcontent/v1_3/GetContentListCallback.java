package com.sony.scalar.webapi.service.avcontent.v1_3;

import com.sony.mexi.orb.service.Callbacks;
import com.sony.scalar.webapi.service.avcontent.v1_3.common.struct.Content;

/* loaded from: classes.dex */
public interface GetContentListCallback extends Callbacks {
    void returnCb(Content[] contentArr);
}
