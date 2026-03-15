package com.sony.imaging.app.synctosmartphone.webapi.util;

import android.util.Log;
import com.sony.imaging.app.synctosmartphone.webapi.AutoSyncConstants;
import java.util.concurrent.TimeoutException;

/* loaded from: classes.dex */
public class ApiCallLog {
    private static final String TAG = ApiCallLog.class.getName();
    private static final Object lockObj = new Object();
    private static boolean locked = false;
    private long callTime;
    private String methodName = MethodName.getMethodName(1);
    private long startTime;

    public ApiCallLog() {
        this.callTime = 0L;
        this.startTime = 0L;
        this.callTime = System.currentTimeMillis();
        this.startTime = this.callTime;
        Log.v(TAG, "[CALL:   " + this.methodName + "] Called.");
    }

    public void init() throws InterruptedException, TimeoutException {
        init(true);
    }

    public void init(boolean lock) throws InterruptedException, TimeoutException {
        if (lock) {
            synchronized (lockObj) {
                if (locked) {
                    lockObj.wait(15000L);
                    if (locked) {
                        throw new TimeoutException("[CREATE: " + this.methodName + "] Timed out in " + AutoSyncConstants.RECEIVE_EVENT_TIMEOUT);
                    }
                }
                locked = true;
            }
            this.startTime = System.currentTimeMillis();
        }
        Log.v(TAG, "[START:  " + this.methodName + "] Started.");
    }

    public void clear() {
        synchronized (lockObj) {
            locked = false;
            lockObj.notify();
        }
        long endTime = System.currentTimeMillis();
        Log.v(TAG, "[END:    " + this.methodName + "] End: Total=" + (endTime - this.callTime) + ", Process=" + (endTime - this.startTime));
    }

    public String getMethodName() {
        return this.methodName;
    }
}
