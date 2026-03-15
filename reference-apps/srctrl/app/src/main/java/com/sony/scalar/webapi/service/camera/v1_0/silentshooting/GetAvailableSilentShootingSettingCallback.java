package com.sony.scalar.webapi.service.camera.v1_0.silentshooting;

import com.sony.mexi.orb.service.Callbacks;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.SilentShootingSettingAvailableParams;

/* loaded from: classes.dex */
public interface GetAvailableSilentShootingSettingCallback extends Callbacks {
    void returnCb(SilentShootingSettingAvailableParams silentShootingSettingAvailableParams);
}
