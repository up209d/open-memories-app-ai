package com.sony.scalar.webapi.service.camera.v1_0.contshootingspeed;

import com.sony.mexi.orb.service.Callbacks;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.ContShootingSpeedAvailableParams;

/* loaded from: classes.dex */
public interface GetAvailableContShootingSpeedCallback extends Callbacks {
    void returnCb(ContShootingSpeedAvailableParams contShootingSpeedAvailableParams);
}
