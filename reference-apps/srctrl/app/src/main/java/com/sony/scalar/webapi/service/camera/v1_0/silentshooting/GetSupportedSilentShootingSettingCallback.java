package com.sony.scalar.webapi.service.camera.v1_0.silentshooting;

import com.sony.mexi.orb.service.Callbacks;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.SilentShootingSettingSupportedParams;

/* loaded from: classes.dex */
public interface GetSupportedSilentShootingSettingCallback extends Callbacks {
    void returnCb(SilentShootingSettingSupportedParams silentShootingSettingSupportedParams);
}
