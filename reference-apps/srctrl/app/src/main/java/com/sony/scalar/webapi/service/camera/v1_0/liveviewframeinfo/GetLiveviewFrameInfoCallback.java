package com.sony.scalar.webapi.service.camera.v1_0.liveviewframeinfo;

import com.sony.mexi.orb.service.Callbacks;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.LiveviewFrameInfoParams;

/* loaded from: classes.dex */
public interface GetLiveviewFrameInfoCallback extends Callbacks {
    void returnCb(LiveviewFrameInfoParams liveviewFrameInfoParams);
}
