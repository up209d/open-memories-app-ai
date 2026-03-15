package com.sony.scalar.webapi.service.camera.v1_0.contshootingspeed;

import com.sony.mexi.orb.service.Callbacks;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.ContShootingSpeedParams;

/* loaded from: classes.dex */
public interface GetContShootingSpeedCallback extends Callbacks {
    void returnCb(ContShootingSpeedParams contShootingSpeedParams);
}
