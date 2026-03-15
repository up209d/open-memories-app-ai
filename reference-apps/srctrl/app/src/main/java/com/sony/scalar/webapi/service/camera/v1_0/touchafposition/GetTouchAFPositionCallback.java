package com.sony.scalar.webapi.service.camera.v1_0.touchafposition;

import com.sony.mexi.orb.service.Callbacks;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.TouchAFCurrentPositionParams;

/* loaded from: classes.dex */
public interface GetTouchAFPositionCallback extends Callbacks {
    void returnCb(TouchAFCurrentPositionParams touchAFCurrentPositionParams);
}
