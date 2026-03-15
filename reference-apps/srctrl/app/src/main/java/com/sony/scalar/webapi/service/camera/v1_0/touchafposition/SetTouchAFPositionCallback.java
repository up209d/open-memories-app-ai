package com.sony.scalar.webapi.service.camera.v1_0.touchafposition;

import com.sony.mexi.orb.service.Callbacks;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.TouchAFPositionParams;

/* loaded from: classes.dex */
public interface SetTouchAFPositionCallback extends Callbacks {
    void returnCb(int i, TouchAFPositionParams touchAFPositionParams);
}
