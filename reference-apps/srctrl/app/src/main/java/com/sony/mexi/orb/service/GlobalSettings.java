package com.sony.mexi.orb.service;

import com.sony.mexi.orb.server.SideWorkExecutor;
import java.util.UUID;

/* loaded from: classes.dex */
public final class GlobalSettings {
    private static GlobalSettings sInstance = new GlobalSettings();
    private AccessLogListener mCallListener;
    private final UUID mExecutorUserId = UUID.randomUUID();

    private GlobalSettings() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static GlobalSettings getInstance() {
        return sInstance;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AccessLogListener getAccessLogListener() {
        return this.mCallListener;
    }

    public static void setAccessLogListener(AccessLogListener listener) {
        sInstance.mCallListener = listener;
        if (listener == null) {
            SideWorkExecutor.getInstance().unregisterAsUser(sInstance.mExecutorUserId);
        } else {
            SideWorkExecutor.getInstance().registerAsUser(sInstance.mExecutorUserId);
        }
    }
}
