package com.sony.mexi.server.jni;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/* loaded from: classes.dex */
public final class WebSocketConnectionJni {
    private final Map<String, byte[]> mHeaders;
    private final String mRemoteAddress;
    private final String mServerAddress;
    private final int mServerPort;
    private final String mUrl;
    private long mWebSocketReference;
    private boolean mUpgradeRequested = false;
    private OnMessageListener mMessageListener = null;
    private final List<OnCloseListener> mCloseListeners = new LinkedList();

    /* loaded from: classes.dex */
    public interface OnCloseListener extends Serializable {
        void onClose();
    }

    /* loaded from: classes.dex */
    public interface OnMessageListener extends Serializable {
        void onBytesMessage(byte[] bArr);

        void onStringMessage(String str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WebSocketConnectionJni(String str, String str2, int i, String str3, Map<String, byte[]> map, long j) {
        this.mRemoteAddress = str;
        this.mServerAddress = str2;
        this.mServerPort = i;
        this.mUrl = str3;
        this.mHeaders = map;
        this.mWebSocketReference = j;
    }

    private native void jniClose(int i, String str, long j);

    private native void jniClose(long j);

    private native void jniFailUpgradeRequest(int i, String str, long j);

    private native boolean jniSend(String str, long j);

    private native boolean jniSend(byte[] bArr, int i, int i2, long j);

    private native void jniSetMaxBodySize(int i, long j);

    private native void jniUpgrade(String[] strArr, Object[] objArr, long j);

    private void onClose() {
        synchronized (this) {
            this.mWebSocketReference = 0L;
        }
        synchronized (this.mCloseListeners) {
            Iterator<OnCloseListener> it = this.mCloseListeners.iterator();
            while (it.hasNext()) {
                it.next().onClose();
            }
        }
        this.mMessageListener = null;
    }

    private void onMessage(String str) {
        if (this.mMessageListener != null) {
            this.mMessageListener.onStringMessage(str);
        }
    }

    private void onMessage(byte[] bArr) {
        if (this.mMessageListener != null) {
            this.mMessageListener.onBytesMessage(bArr);
        }
    }

    public synchronized boolean addOnCloseListener(OnCloseListener onCloseListener) {
        boolean z;
        if (this.mWebSocketReference == 0) {
            z = false;
        } else {
            synchronized (this.mCloseListeners) {
                this.mCloseListeners.add(onCloseListener);
            }
            z = true;
        }
        return z;
    }

    public synchronized void close() {
        if (this.mWebSocketReference != 0 && this.mUpgradeRequested) {
            jniClose(this.mWebSocketReference);
        }
    }

    public synchronized void close(int i, String str) {
        if (this.mWebSocketReference != 0 && this.mUpgradeRequested) {
            if (str == null) {
                str = "";
            }
            jniClose(i, str, this.mWebSocketReference);
        }
    }

    public synchronized void failUpgradeRequest() {
        failUpgradeRequest(0, "");
    }

    public synchronized void failUpgradeRequest(int i, String str) {
        if (this.mWebSocketReference != 0 && !this.mUpgradeRequested) {
            jniFailUpgradeRequest(i, str, this.mWebSocketReference);
            this.mWebSocketReference = 0L;
        }
    }

    public Map<String, byte[]> headers() {
        return this.mHeaders;
    }

    public String remoteAddress() {
        return this.mRemoteAddress;
    }

    public void removeOnCloseListener(OnCloseListener onCloseListener) {
        synchronized (this.mCloseListeners) {
            this.mCloseListeners.remove(onCloseListener);
        }
    }

    public synchronized boolean send(String str) {
        return (this.mWebSocketReference == 0 || !this.mUpgradeRequested) ? false : jniSend(str, this.mWebSocketReference);
    }

    public synchronized boolean send(byte[] bArr, int i, int i2) {
        return (this.mWebSocketReference == 0 || !this.mUpgradeRequested) ? false : jniSend(bArr, i, i2, this.mWebSocketReference);
    }

    public String serverAddress() {
        return this.mServerAddress;
    }

    public int serverPort() {
        return this.mServerPort;
    }

    public synchronized void setMaxBodySize(int i) {
        if (this.mWebSocketReference != 0 && this.mUpgradeRequested) {
            jniSetMaxBodySize(i, this.mWebSocketReference);
        }
    }

    public void setOnMessageListener(OnMessageListener onMessageListener) {
        this.mMessageListener = onMessageListener;
    }

    public void upgrade() {
        upgrade(null);
    }

    public synchronized void upgrade(Map<String, Object> map) {
        if (this.mWebSocketReference != 0 && !this.mUpgradeRequested) {
            if (map == null) {
                map = new TreeMap<>();
            }
            this.mUpgradeRequested = true;
            int size = map.size();
            ArrayList arrayList = new ArrayList(size);
            ArrayList arrayList2 = new ArrayList(size);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if ((value instanceof String) || (value instanceof String[])) {
                    arrayList.add(key);
                    arrayList2.add(value);
                }
            }
            jniUpgrade((String[]) arrayList.toArray(new String[arrayList.size()]), arrayList2.toArray(), this.mWebSocketReference);
        }
    }

    public String url() {
        return this.mUrl;
    }
}
