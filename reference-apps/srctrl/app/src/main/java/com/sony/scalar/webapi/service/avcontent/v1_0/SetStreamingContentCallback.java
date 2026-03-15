package com.sony.scalar.webapi.service.avcontent.v1_0;

import com.sony.mexi.orb.service.Callbacks;
import com.sony.scalar.webapi.service.avcontent.v1_0.common.struct.StreamingURL;

/* loaded from: classes.dex */
public interface SetStreamingContentCallback extends Callbacks {
    void returnCb(StreamingURL streamingURL);
}
