package com.sony.scalar.webapi.service.avcontent.v1_0;

import com.sony.mexi.orb.service.Callbacks;
import com.sony.scalar.webapi.service.avcontent.v1_0.common.struct.StreamingStatus;

/* loaded from: classes.dex */
public interface RequestToNotifyStreamingStatusCallback extends Callbacks {
    void returnCb(StreamingStatus streamingStatus);
}
