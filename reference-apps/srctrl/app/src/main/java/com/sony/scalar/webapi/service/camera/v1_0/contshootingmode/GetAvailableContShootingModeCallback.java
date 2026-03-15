package com.sony.scalar.webapi.service.camera.v1_0.contshootingmode;

import com.sony.mexi.orb.service.Callbacks;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.ContShootingModeAvailableParams;

/* loaded from: classes.dex */
public interface GetAvailableContShootingModeCallback extends Callbacks {
    void returnCb(ContShootingModeAvailableParams contShootingModeAvailableParams);
}
