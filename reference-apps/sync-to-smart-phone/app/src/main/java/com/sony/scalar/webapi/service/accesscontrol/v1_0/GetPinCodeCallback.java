package com.sony.scalar.webapi.service.accesscontrol.v1_0;

import com.sony.mexi.webapi.Callbacks;
import com.sony.scalar.webapi.service.accesscontrol.v1_0.common.struct.PinCode;

/* loaded from: classes.dex */
public interface GetPinCodeCallback extends Callbacks {
    void returnCb(PinCode pinCode);
}
