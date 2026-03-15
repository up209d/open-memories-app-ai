package com.sony.mexi.orb.server.leza.http;

import com.sony.mexi.orb.server.http.HttpDefs;
import com.sony.mexi.orb.server.http.StatusCode;
import com.sony.mexi.server.jni.HttpServletJni;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: classes.dex */
public class HttpResponseOutputStream extends OutputStream {
    private HttpServletJni mHttpServletJni;
    private StatusCode mStatusCode = StatusCode.OK;
    private final ReentrantLock mLock = new ReentrantLock();
    private final Condition mLockCondition = this.mLock.newCondition();
    private boolean mIsClosed = false;
    private boolean mIsCommitted = false;

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpResponseOutputStream(HttpServletJni httpServletJni) {
        this.mHttpServletJni = httpServletJni;
        initializeListeners();
    }

    @Override // java.io.OutputStream
    public void write(int i) throws IOException {
        try {
            this.mLock.lock();
            throwForClosed();
            writeHead();
            boolean ret = this.mHttpServletJni.write(i);
            if (!ret) {
                throw HttpDefs.STREAM_CLOSED_EXCEPTION;
            }
            try {
                this.mLockCondition.await();
                throwForClosed();
            } catch (InterruptedException e) {
                throw new InterruptedIOException();
            }
        } finally {
            this.mLock.unlock();
        }
    }

    @Override // java.io.OutputStream
    public void write(byte[] b, int offset, int length) throws IOException {
        if (b == null) {
            throw new NullPointerException("byte array is null");
        }
        if (offset < -1 || length < -1 || b.length < offset + length) {
            throw new IndexOutOfBoundsException("array length, offset, length mismatch");
        }
        try {
            this.mLock.lock();
            throwForClosed();
            writeHead();
            boolean ret = this.mHttpServletJni.write(b, offset, length);
            if (!ret) {
                throw HttpDefs.STREAM_CLOSED_EXCEPTION;
            }
            try {
                this.mLockCondition.await();
                throwForClosed();
            } catch (InterruptedException e) {
                throw new InterruptedIOException();
            }
        } finally {
            this.mLock.unlock();
        }
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        try {
            this.mLock.lock();
            if (!this.mIsClosed) {
                this.mIsClosed = true;
                boolean ret = this.mHttpServletJni.end();
                if (!ret) {
                    throw HttpDefs.STREAM_CLOSED_EXCEPTION;
                }
                this.mLockCondition.signalAll();
            }
        } finally {
            this.mLock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setStatus(StatusCode code) {
        try {
            this.mLock.lock();
            if (!this.mIsClosed) {
                if (this.mIsCommitted) {
                    throw new IllegalStateException("Header is already committed.");
                }
                this.mStatusCode = code;
            }
        } finally {
            this.mLock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setHeader(String name, String value) {
        try {
            this.mLock.lock();
            if (!this.mIsClosed) {
                if (this.mIsCommitted) {
                    throw new IllegalStateException("Header is already committed.");
                }
                this.mHttpServletJni.setHeader(name, value);
            }
        } finally {
            this.mLock.unlock();
        }
    }

    private void initializeListeners() {
        this.mHttpServletJni.setOnWriterDrainListener(new HttpServletJni.OnWriterDrainListener() { // from class: com.sony.mexi.orb.server.leza.http.HttpResponseOutputStream.1
            @Override // com.sony.mexi.server.jni.HttpServletJni.OnWriterDrainListener
            public void onDrain() {
                try {
                    HttpResponseOutputStream.this.mLock.lock();
                    HttpResponseOutputStream.this.mLockCondition.signalAll();
                } finally {
                    HttpResponseOutputStream.this.mLock.unlock();
                }
            }
        });
        this.mHttpServletJni.addOnCloseListener(new HttpServletJni.OnCloseListener() { // from class: com.sony.mexi.orb.server.leza.http.HttpResponseOutputStream.2
            @Override // com.sony.mexi.server.jni.HttpServletJni.OnCloseListener
            public void onClose() {
                try {
                    HttpResponseOutputStream.this.mLock.lock();
                    HttpResponseOutputStream.this.mIsClosed = true;
                    HttpResponseOutputStream.this.mLockCondition.signalAll();
                } finally {
                    HttpResponseOutputStream.this.mLock.unlock();
                }
            }
        });
    }

    private void throwForClosed() throws IOException {
        try {
            this.mLock.lock();
            if (this.mIsClosed) {
                throw new IOException("Connection closed");
            }
        } finally {
            this.mLock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeHead() throws IOException {
        try {
            this.mLock.lock();
            if (!this.mIsCommitted) {
                boolean ret = this.mHttpServletJni.writeHead(this.mStatusCode.toCode(), this.mStatusCode.toReasonPhrase());
                if (!ret) {
                    throw HttpDefs.STREAM_CLOSED_EXCEPTION;
                }
                this.mIsCommitted = true;
            }
        } finally {
            this.mLock.unlock();
        }
    }
}
