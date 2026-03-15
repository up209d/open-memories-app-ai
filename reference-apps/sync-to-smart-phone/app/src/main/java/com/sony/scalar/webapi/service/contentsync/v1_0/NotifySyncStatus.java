package com.sony.scalar.webapi.service.contentsync.v1_0;

import com.sony.mexi.webapi.Service;
import com.sony.scalar.webapi.service.contentsync.v1_0.common.struct.SyncStatusSource;

/* loaded from: classes.dex */
public interface NotifySyncStatus extends Service {
    int notifySyncStatus(SyncStatusSource syncStatusSource, NotifySyncStatusCallback notifySyncStatusCallback);
}
