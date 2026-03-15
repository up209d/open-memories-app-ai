package com.sony.imaging.app.fw;

/* loaded from: classes.dex */
public class RunStatus {
    public static final int BOOTING = 1;
    public static final int FINISHED = 5;
    public static final int FINISHING = 3;
    public static final int PULLINGBACK = 2;
    public static final int RUNNING = 4;
    private static int mStatus = 5;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setStatus(int status) {
        mStatus = status;
    }

    public static int getStatus() {
        return mStatus;
    }
}
