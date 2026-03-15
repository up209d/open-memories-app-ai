package com.sony.mexi.server.jni;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/* loaded from: classes.dex */
public final class HttpServletJni {
    private static final String CHARACTER_ENCODING = "UTF-8";
    private long mJniRequest;
    private final String mMethod;
    private final String mProtocol;
    private final String mRemoteAddress;
    private final Map<String, byte[]> mRequestHeaders;
    private final String mServerAddress;
    private final int mServerPort;
    private final String mUrl;
    private OnIncomingDataListener mOnIncomingDataListener = null;
    private OnWriterDrainListener mOnWriterDrainListener = null;
    private final List<OnCloseListener> mCloseListeners = new ArrayList();
    private int mPendingWrites = 0;
    private boolean mIsHeadWritten = false;
    private final Map<String, List<String>> mResponseHeaders = new TreeMap(String.CASE_INSENSITIVE_ORDER);

    /* loaded from: classes.dex */
    public interface OnCloseListener extends Serializable {
        void onClose();
    }

    /* loaded from: classes.dex */
    public interface OnIncomingDataListener extends Serializable {
        void onData(byte[] bArr);

        void onEnd();
    }

    /* loaded from: classes.dex */
    public interface OnWriterDrainListener extends Serializable {
        void onDrain();
    }

    public HttpServletJni(String str, String str2, int i, String str3, String str4, String str5, Map<String, byte[]> map, long j) {
        this.mJniRequest = 0L;
        this.mRemoteAddress = str;
        this.mServerAddress = str2;
        this.mServerPort = i;
        this.mProtocol = str3;
        this.mMethod = str4;
        this.mUrl = str5;
        this.mRequestHeaders = map;
        this.mJniRequest = j;
        jniSetJavaServletReference(this.mJniRequest);
    }

    private static void addHeaderValue(List<String> list, String str) {
        int indexOf;
        if (str.length() == 0) {
            return;
        }
        int indexOf2 = str.indexOf("\"");
        int indexOf3 = str.indexOf(",");
        if (indexOf3 == -1 && indexOf2 == -1) {
            addToList(list, str.trim());
            return;
        }
        if (indexOf3 != -1 && (indexOf3 < indexOf2 || indexOf2 == -1)) {
            addToList(list, str.substring(0, indexOf3).trim());
            addHeaderValue(list, str.substring(indexOf3 + 1));
        } else if ((indexOf2 < indexOf3 || indexOf3 == -1) && (indexOf = str.indexOf("\"", indexOf2 + 1)) != -1) {
            addToList(list, str.substring(indexOf2, indexOf + 1).trim());
            int indexOf4 = str.indexOf(",", indexOf + 1);
            if (indexOf4 != -1) {
                addHeaderValue(list, str.substring(indexOf4 + 1));
            }
        }
    }

    private static void addToList(List<String> list, String str) {
        if (str.length() == 0) {
            return;
        }
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            if (it.next().equalsIgnoreCase(str)) {
                return;
            }
        }
        list.add(str);
    }

    private native boolean jniEnd(long j);

    private native void jniSetHeader(String str, String str2, long j);

    private native void jniSetJavaServletReference(long j);

    private native boolean jniWrite(byte[] bArr, int i, int i2, long j);

    private native void jniWriteHead(int i, String str, long j);

    private void onClose() {
        synchronized (this) {
            this.mJniRequest = 0L;
        }
        Iterator<OnCloseListener> it = this.mCloseListeners.iterator();
        while (it.hasNext()) {
            it.next().onClose();
        }
    }

    private void onData(byte[] bArr) {
        if (this.mOnIncomingDataListener != null) {
            this.mOnIncomingDataListener.onData(bArr);
        }
    }

    private void onDataEnd() {
        if (this.mOnIncomingDataListener != null) {
            this.mOnIncomingDataListener.onEnd();
        }
    }

    private void onDrain() {
        synchronized (this) {
            if (this.mPendingWrites != 0) {
                return;
            }
            if (this.mOnWriterDrainListener != null) {
                this.mOnWriterDrainListener.onDrain();
            }
        }
    }

    private synchronized void onWriteToBuffer() {
        this.mPendingWrites--;
    }

    public void addOnCloseListener(OnCloseListener onCloseListener) {
        this.mCloseListeners.add(onCloseListener);
    }

    public synchronized boolean end() {
        return this.mJniRequest == 0 ? false : jniEnd(this.mJniRequest);
    }

    public String getHeader(String str) {
        try {
            byte[] bArr = this.mRequestHeaders.get(str);
            if (bArr == null) {
                return null;
            }
            return new String(bArr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public String getProtocol() {
        return this.mProtocol;
    }

    public String getRemoteAddress() {
        return this.mRemoteAddress;
    }

    public String getRequestMethod() {
        return this.mMethod;
    }

    public String getServerAddress() {
        return this.mServerAddress;
    }

    public int getServerPort() {
        return this.mServerPort;
    }

    public String getUrl() {
        return this.mUrl;
    }

    public synchronized void setHeader(String str, String str2) {
        if (this.mJniRequest != 0 && !this.mIsHeadWritten) {
            if (str == null || str2 == null) {
                throw new NullPointerException("header or value cannot be null");
            }
            List<String> list = this.mResponseHeaders.get(str);
            if (list == null) {
                list = new ArrayList<>(1);
                this.mResponseHeaders.put(str, list);
            }
            addHeaderValue(list, str2);
        }
    }

    public void setOnIncomingDataListener(OnIncomingDataListener onIncomingDataListener) {
        this.mOnIncomingDataListener = onIncomingDataListener;
    }

    public void setOnWriterDrainListener(OnWriterDrainListener onWriterDrainListener) {
        this.mOnWriterDrainListener = onWriterDrainListener;
    }

    public synchronized boolean write(int i) {
        return write(new byte[]{(byte) i}, 0, 1);
    }

    public synchronized boolean write(byte[] bArr, int i, int i2) {
        boolean jniWrite;
        if (this.mJniRequest == 0) {
            jniWrite = false;
        } else {
            jniWrite = jniWrite(bArr, i, i2, this.mJniRequest);
            if (jniWrite) {
                this.mPendingWrites++;
            }
        }
        return jniWrite;
    }

    public synchronized boolean writeHead(int i, String str) {
        boolean z;
        if (this.mJniRequest == 0 || this.mIsHeadWritten) {
            z = false;
        } else {
            for (Map.Entry<String, List<String>> entry : this.mResponseHeaders.entrySet()) {
                String key = entry.getKey();
                Iterator<String> it = entry.getValue().iterator();
                while (it.hasNext()) {
                    jniSetHeader(key, it.next(), this.mJniRequest);
                }
            }
            jniWriteHead(i, str, this.mJniRequest);
            this.mIsHeadWritten = true;
            z = true;
        }
        return z;
    }
}
