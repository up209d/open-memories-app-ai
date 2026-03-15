package com.sony.scalar.webapi.service.contentsync.v1_0;

import com.sony.mexi.webapi.Service;
import com.sony.scalar.webapi.service.contentsync.v1_0.common.struct.Pairing;

/* loaded from: classes.dex */
public interface ActPairing extends Service {
    int actPairing(Pairing pairing, ActPairingCallback actPairingCallback);
}
