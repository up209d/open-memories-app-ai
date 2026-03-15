package com.sony.scalar.webapi.service.camera.v1_0.silentshooting;

import com.sony.mexi.orb.service.Callbacks;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.SilentShootingSettingParams;

/* loaded from: classes.dex */
public interface GetSilentShootingSettingCallback extends Callbacks {
    void returnCb(SilentShootingSettingParams silentShootingSettingParams);
}
