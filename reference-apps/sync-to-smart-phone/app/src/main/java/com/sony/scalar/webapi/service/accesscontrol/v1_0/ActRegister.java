package com.sony.scalar.webapi.service.accesscontrol.v1_0;

import com.sony.mexi.webapi.Service;
import com.sony.scalar.webapi.service.accesscontrol.v1_0.common.struct.Capability;
import com.sony.scalar.webapi.service.accesscontrol.v1_0.common.struct.ClientInfo;

/* loaded from: classes.dex */
public interface ActRegister extends Service {
    int actRegister(ClientInfo clientInfo, Capability[] capabilityArr, ActRegisterCallback actRegisterCallback);
}
