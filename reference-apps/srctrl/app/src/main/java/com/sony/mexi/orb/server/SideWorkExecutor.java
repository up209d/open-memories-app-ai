package com.sony.mexi.orb.server;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/* loaded from: classes.dex */
public final class SideWorkExecutor {
    private static SideWorkExecutor sInstance = new SideWorkExecutor();
    private ExecutorService mExecutor;
    private final Set<UUID> mUsers = new HashSet();

    private SideWorkExecutor() {
    }

    public static SideWorkExecutor getInstance() {
        return sInstance;
    }

    public synchronized void registerAsUser(UUID userId) {
        this.mUsers.add(userId);
        if (this.mExecutor == null) {
            System.out.println("setMethodCallListener == Executors.newSingleThreadExecutor ==");
            this.mExecutor = Executors.newSingleThreadExecutor();
        }
    }

    public synchronized void unregisterAsUser(UUID userId) {
        if (this.mExecutor != null) {
            this.mUsers.remove(userId);
            if (this.mUsers.size() == 0) {
                this.mExecutor.shutdownNow();
                this.mExecutor = null;
            }
        }
    }

    public synchronized void forceStop() {
        this.mUsers.clear();
        if (this.mExecutor != null) {
            this.mExecutor.shutdownNow();
            this.mExecutor = null;
        }
    }

    public synchronized boolean isRunning() {
        return this.mExecutor != null;
    }

    public synchronized void execute(Runnable task) {
        ExecutorService executor = this.mExecutor;
        if (executor != null) {
            try {
                executor.execute(task);
            } catch (RejectedExecutionException e) {
            }
        }
    }
}
