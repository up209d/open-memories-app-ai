package com.sony.mexi.webapi.serviceguide.v1_1;

import com.sony.mexi.webapi.ContinuousCallbacks;

/* loaded from: classes.dex */
public interface InformationHandler extends ContinuousCallbacks {
    void handleInformation(String str, ServiceInformation[] serviceInformationArr);
}
