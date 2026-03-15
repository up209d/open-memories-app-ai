package com.sony.mexi.orb.server.leza.http;

import com.sony.mexi.orb.server.http.HttpDefs;
import com.sony.mexi.orb.server.http.OrbHttpInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

/* loaded from: classes.dex */
public class HttpRequestInputStream extends OrbHttpInputStream {
    private static final byte[] ZERO_LENGTH_BYTE = new byte[0];
    private LinkedBlockingQueue<byte[]> mByteStream = new LinkedBlockingQueue<>();
    private byte[] mTopChunk = null;
    private int mTopChunkOffset = 0;
    private boolean mIsClosed = false;
    private final Object mCloseLock = new Object();

    @Override // java.io.InputStream
    public int read() throws IOException {
        loadTopChunk();
        if (this.mTopChunk.length == 0) {
            return -1;
        }
        byte b = this.mTopChunk[this.mTopChunkOffset];
        this.mTopChunkOffset++;
        if (this.mTopChunkOffset == this.mTopChunk.length) {
            resetTopChunk();
            return b;
        }
        return b;
    }

    @Override // java.io.InputStream
    public int read(byte[] b, int offset, int length) throws IOException {
        if (b == null) {
            throw new NullPointerException("buffer is null");
        }
        if (offset < -1 || length < -1 || b.length < offset + length) {
            throw new IndexOutOfBoundsException("array length, offset, length mismatch");
        }
        if (b.length == 0) {
            return 0;
        }
        loadTopChunk();
        if (this.mTopChunk.length == 0) {
            return -1;
        }
        int written = Math.min(this.mTopChunk.length - this.mTopChunkOffset, length);
        System.arraycopy(this.mTopChunk, this.mTopChunkOffset, b, offset, written);
        this.mTopChunkOffset += written;
        if (this.mTopChunkOffset == this.mTopChunk.length) {
            resetTopChunk();
            return written;
        }
        return written;
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        int length;
        synchronized (this.mCloseLock) {
            if (this.mIsClosed) {
                throw HttpDefs.STREAM_CLOSED_EXCEPTION;
            }
            if (this.mTopChunk == null) {
                if (this.mByteStream.size() == 0) {
                    length = 0;
                    return length;
                }
                loadTopChunk();
            }
            length = this.mTopChunk.length - this.mTopChunkOffset;
            return length;
        }
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpInputStream
    public byte[] readNextBuffer() throws IOException {
        loadTopChunk();
        if (this.mTopChunk.length == 0) {
            return null;
        }
        byte[] copyOfRange = Arrays.copyOfRange(this.mTopChunk, this.mTopChunkOffset, this.mTopChunk.length);
        resetTopChunk();
        return copyOfRange;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void write(byte[] data) {
        synchronized (this.mCloseLock) {
            if (!this.mIsClosed) {
                this.mByteStream.offer(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void end() {
        this.mByteStream.offer(ZERO_LENGTH_BYTE);
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        synchronized (this.mCloseLock) {
            this.mIsClosed = true;
            this.mByteStream.clear();
            this.mByteStream.offer(ZERO_LENGTH_BYTE);
        }
    }

    private void loadTopChunk() throws IOException {
        synchronized (this.mCloseLock) {
            if (this.mIsClosed) {
                resetTopChunk();
                throw HttpDefs.STREAM_CLOSED_EXCEPTION;
            }
        }
        if (this.mTopChunk == null) {
            try {
                byte[] data = this.mByteStream.take();
                synchronized (this.mCloseLock) {
                    if (this.mIsClosed) {
                        throw HttpDefs.STREAM_CLOSED_EXCEPTION;
                    }
                }
                this.mTopChunk = data;
            } catch (InterruptedException e) {
                throw new InterruptedIOException("reading thread is interrupted");
            }
        }
    }

    private void resetTopChunk() {
        this.mTopChunk = null;
        this.mTopChunkOffset = 0;
    }
}
