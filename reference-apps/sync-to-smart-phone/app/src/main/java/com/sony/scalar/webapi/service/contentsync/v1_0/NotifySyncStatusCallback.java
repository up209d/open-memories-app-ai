package com.sony.scalar.webapi.service.contentsync.v1_0;

import com.sony.mexi.webapi.Callbacks;
import com.sony.scalar.webapi.service.contentsync.v1_0.common.struct.SyncStatus;

/* loaded from: classes.dex */
public interface NotifySyncStatusCallback extends Callbacks {
    void returnCb(SyncStatus syncStatus);
}
