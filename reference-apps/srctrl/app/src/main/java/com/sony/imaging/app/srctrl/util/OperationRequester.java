package com.sony.imaging.app.srctrl.util;

import android.os.Message;
import java.util.List;

/* loaded from: classes.dex */
public class OperationRequester<T> {
    protected Object mResult;
    protected Object mSync = new Object();

    public T request(int requestID, Object... params) {
        return request(requestID, false, params);
    }

    public T requestPriority(int requestID, Object... params) {
        return request(requestID, true, params);
    }

    private T request(int i, boolean z, Object... objArr) {
        OperationReceiver receiver = StateController.getInstance().getReceiver();
        if (receiver == null || !receiver.isAlive()) {
            throw new RuntimeException("Receiver is already terminated");
        }
        Message obtainMessage = receiver.mHandler.obtainMessage(i, new Object[]{this, objArr});
        synchronized (this.mSync) {
            if (z) {
                receiver.mHandler.sendMessageAtFrontOfQueue(obtainMessage);
            } else {
                receiver.mHandler.sendMessage(obtainMessage);
            }
            try {
                this.mSync.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            return (T) this.mResult;
        } catch (ClassCastException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static boolean[] copy(List<Boolean> src) {
        if (src == null) {
            return null;
        }
        int size = src.size();
        boolean[] ret = new boolean[size];
        for (int i = 0; i < size; i++) {
            ret[i] = src.get(i).booleanValue();
        }
        return ret;
    }

    public static boolean[] copy(Boolean[] src) {
        if (src == null) {
            return null;
        }
        boolean[] ret = new boolean[src.length];
        for (int i = 0; i < src.length; i++) {
            ret[i] = src[i].booleanValue();
        }
        return ret;
    }
}
