package com.sony.scalar.webapi.service.camera.v1_0.contshootingspeed;

import com.sony.mexi.orb.service.Callbacks;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.ContShootingSpeedSupportedParams;

/* loaded from: classes.dex */
public interface GetSupportedContShootingSpeedCallback extends Callbacks {
    void returnCb(ContShootingSpeedSupportedParams contShootingSpeedSupportedParams);
}
