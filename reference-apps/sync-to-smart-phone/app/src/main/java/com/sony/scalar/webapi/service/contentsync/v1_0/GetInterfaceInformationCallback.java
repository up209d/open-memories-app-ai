package com.sony.scalar.webapi.service.contentsync.v1_0;

import com.sony.mexi.webapi.Callbacks;
import com.sony.scalar.webapi.service.contentsync.v1_0.common.struct.InterfaceInformation;

/* loaded from: classes.dex */
public interface GetInterfaceInformationCallback extends Callbacks {
    void returnCb(InterfaceInformation interfaceInformation);
}
