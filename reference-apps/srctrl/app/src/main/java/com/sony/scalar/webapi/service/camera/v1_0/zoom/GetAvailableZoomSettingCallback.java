package com.sony.scalar.webapi.service.camera.v1_0.zoom;

import com.sony.mexi.orb.service.Callbacks;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.ZoomSettingAvailableParams;

/* loaded from: classes.dex */
public interface GetAvailableZoomSettingCallback extends Callbacks {
    void returnCb(ZoomSettingAvailableParams zoomSettingAvailableParams);
}
