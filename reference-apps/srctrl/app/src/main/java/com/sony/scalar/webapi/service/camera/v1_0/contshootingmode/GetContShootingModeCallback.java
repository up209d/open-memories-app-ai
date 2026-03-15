package com.sony.scalar.webapi.service.camera.v1_0.contshootingmode;

import com.sony.mexi.orb.service.Callbacks;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.ContShootingModeParams;

/* loaded from: classes.dex */
public interface GetContShootingModeCallback extends Callbacks {
    void returnCb(ContShootingModeParams contShootingModeParams);
}
