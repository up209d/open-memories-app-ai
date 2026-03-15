package com.sony.mexi.server.jni;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/* loaded from: classes.dex */
public class HttpServerJni {
    private static final String CHARACTER_ENCODING = "UTF-8";
    private final int mBacklog;
    private final String mHost;
    private OnStatusChangeListener mOnStatusChangeListener;
    private final int mPort;
    private OnHttpRequestListener mOnHttpRequestListener = null;
    private Map<String, OnUpgradeRequestListener> mUpgradeListeners = new TreeMap();
    private long mNativeServerReference = 0;
    private int mMaxConnections = -1;
    private int mTimeout = -1;

    /* loaded from: classes.dex */
    public enum HttpError {
        STATUS_OK,
        ERROR_INVALID_HOST_OR_PORT,
        ERROR_ADDRESS_IN_USE,
        ERROR_UNKNOWN
    }

    /* loaded from: classes.dex */
    public enum HttpServerStatus {
        OPENED,
        CLOSED
    }

    /* loaded from: classes.dex */
    public interface OnHttpRequestListener extends Serializable {
        void onRequest(HttpServletJni httpServletJni);
    }

    /* loaded from: classes.dex */
    public interface OnStatusChangeListener extends Serializable {
        void onStatus(HttpServerStatus httpServerStatus, HttpError httpError);
    }

    /* loaded from: classes.dex */
    public interface OnUpgradeRequestListener extends Serializable {
        void onUpgradeRequest(String str, String str2, int i, String str3, Map<String, byte[]> map, long j);
    }

    public HttpServerJni(OnStatusChangeListener onStatusChangeListener, int i, String str, int i2) {
        this.mOnStatusChangeListener = onStatusChangeListener;
        this.mHost = str;
        this.mPort = i;
        this.mBacklog = i2;
    }

    private native void jniClose(long j);

    private native long jniCreate(int i, String str, int i2);

    private native void jniOpen(long j);

    private native void jniSetMaxConnections(long j, int i);

    private native void jniSetTimeout(long j, int i);

    private void onRequest(String str, String str2, int i, String str3, String str4, String str5, TreeMap<String, byte[]> treeMap, long j) {
        if (this.mOnHttpRequestListener == null) {
            return;
        }
        this.mOnHttpRequestListener.onRequest(new HttpServletJni(str, str2, i, str3, str4, str5, treeMap, j));
    }

    private void onStatusChange(HttpServerStatus httpServerStatus, HttpError httpError) {
        synchronized (this) {
            if (httpServerStatus == HttpServerStatus.CLOSED) {
                this.mNativeServerReference = 0L;
            }
        }
        if (this.mOnStatusChangeListener != null) {
            this.mOnStatusChangeListener.onStatus(httpServerStatus, httpError);
        }
    }

    private boolean onUpgradeRequest(String str, String str2, int i, String str3, String str4, String str5, TreeMap<String, byte[]> treeMap, long j) {
        try {
            byte[] bArr = treeMap.get("Upgrade");
            if (bArr == null) {
                return false;
            }
            OnUpgradeRequestListener onUpgradeRequestListener = this.mUpgradeListeners.get(new String(bArr, "UTF-8").toLowerCase(Locale.US));
            if (onUpgradeRequestListener == null) {
                return false;
            }
            onUpgradeRequestListener.onUpgradeRequest(str, str2, i, str5, treeMap, j);
            return true;
        } catch (UnsupportedEncodingException e) {
            return false;
        }
    }

    public void addUpgradeRequestListener(String str, OnUpgradeRequestListener onUpgradeRequestListener) {
        this.mUpgradeListeners.put(str, onUpgradeRequestListener);
    }

    public synchronized boolean close() {
        boolean z;
        if (this.mNativeServerReference == 0) {
            z = false;
        } else {
            jniClose(this.mNativeServerReference);
            z = true;
        }
        return z;
    }

    public synchronized boolean open() {
        boolean z;
        if (this.mNativeServerReference != 0) {
            z = false;
        } else {
            this.mNativeServerReference = jniCreate(this.mPort, this.mHost, this.mBacklog);
            jniSetMaxConnections(this.mNativeServerReference, this.mMaxConnections);
            jniSetTimeout(this.mNativeServerReference, this.mTimeout);
            jniOpen(this.mNativeServerReference);
            z = true;
        }
        return z;
    }

    public synchronized void setMaxConnections(int i) {
        this.mMaxConnections = i;
        if (this.mNativeServerReference != 0) {
            jniSetMaxConnections(this.mNativeServerReference, this.mMaxConnections);
        }
    }

    public void setOnHttpRequestListener(OnHttpRequestListener onHttpRequestListener) {
        this.mOnHttpRequestListener = onHttpRequestListener;
    }

    public synchronized void setTimeout(int i) {
        this.mTimeout = i;
        if (this.mNativeServerReference != 0) {
            jniSetTimeout(this.mNativeServerReference, this.mTimeout);
        }
    }
}
