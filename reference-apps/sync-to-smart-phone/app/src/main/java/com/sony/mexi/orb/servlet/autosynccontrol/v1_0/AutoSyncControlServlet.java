package com.sony.mexi.orb.servlet.autosynccontrol.v1_0;

import com.sony.scalar.webapi.service.contentsync.v1_0.ActPairingCallback;
import com.sony.scalar.webapi.service.contentsync.v1_0.GetInterfaceInformationCallback;
import com.sony.scalar.webapi.service.contentsync.v1_0.NotifySyncStatusCallback;
import com.sony.scalar.webapi.service.contentsync.v1_0.common.struct.Pairing;
import com.sony.scalar.webapi.service.contentsync.v1_0.common.struct.SyncStatusSource;

/* loaded from: classes.dex */
public class AutoSyncControlServlet extends AutoSyncControlServletBase {
    private static final long serialVersionUID = 1;

    @Override // com.sony.scalar.webapi.service.contentsync.v1_0.ActPairing
    public int actPairing(Pairing pair, ActPairingCallback returnCb) {
        return 0;
    }

    @Override // com.sony.scalar.webapi.service.contentsync.v1_0.GetInterfaceInformation
    public int getInterfaceInformation(GetInterfaceInformationCallback returnCb) {
        return 0;
    }

    @Override // com.sony.scalar.webapi.service.contentsync.v1_0.NotifySyncStatus
    public int notifySyncStatus(SyncStatusSource syncStatus, NotifySyncStatusCallback returnCb) {
        return 0;
    }
}
