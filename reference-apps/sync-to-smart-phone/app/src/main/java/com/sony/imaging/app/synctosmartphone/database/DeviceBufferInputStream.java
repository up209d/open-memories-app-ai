package com.sony.imaging.app.synctosmartphone.database;

import android.util.Log;
import com.sony.scalar.hardware.DeviceBuffer;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public class DeviceBufferInputStream extends InputStream {
    private static final String TAG = DeviceBufferInputStream.class.getSimpleName();
    private DeviceBuffer buf;
    private int count;
    private int mark;
    private int pos;
    private byte[] tmpBuf;

    public DeviceBufferInputStream(DeviceBuffer buf, int dataLength) {
        this.tmpBuf = new byte[1];
        Log.d(TAG, "DeviceBufferInputStream buf :" + buf.getSize());
        this.buf = buf;
        this.pos = 0;
        this.mark = 0;
        this.count = dataLength;
    }

    public DeviceBufferInputStream(DeviceBuffer buf, int offset, int length) {
        this.tmpBuf = new byte[1];
        Log.d(TAG, "DeviceBufferInputStream buf :" + buf.getSize() + ", offset :" + offset + ", length :" + length);
        this.buf = buf;
        this.pos = offset;
        this.mark = offset;
        this.count = offset + length > buf.getSize() ? buf.getSize() : offset + length;
    }

    @Override // java.io.InputStream
    public synchronized int available() {
        Log.d(TAG, "available :" + (this.count - this.pos));
        return this.count - this.pos;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        Log.d(TAG, "close");
        Log.i(TAG, "HEAP_MESURE: dbuf release");
        if (this.buf != null) {
            Log.i(TAG, "HEAP_MESURE_CHECK: dbuf release in close");
            this.buf.release();
            Log.i(TAG, "HEAP_MESURE: dbuf released");
            this.buf = null;
        }
    }

    @Override // java.io.InputStream
    public synchronized void mark(int readlimit) {
        Log.d(TAG, "mark :" + readlimit);
        this.mark = this.pos;
    }

    @Override // java.io.InputStream
    public boolean markSupported() {
        Log.d(TAG, "markSupported");
        return true;
    }

    @Override // java.io.InputStream
    public synchronized int read() {
        int i;
        if (this.pos < this.count) {
            DeviceBuffer deviceBuffer = this.buf;
            byte[] bArr = this.tmpBuf;
            int i2 = this.pos;
            this.pos = i2 + 1;
            deviceBuffer.read(bArr, 0, 1, i2);
            i = this.tmpBuf[0] & 255;
        } else {
            i = -1;
        }
        return i;
    }

    @Override // java.io.InputStream
    public synchronized int read(byte[] buffer, int offset, int length) {
        int copylen;
        if ((offset | length) >= 0) {
            if (offset <= buffer.length && buffer.length - offset >= length) {
                if (this.pos >= this.count) {
                    copylen = -1;
                } else if (length == 0) {
                    copylen = 0;
                } else {
                    copylen = this.count - this.pos < length ? this.count - this.pos : length;
                    this.buf.read(buffer, offset, copylen, this.pos);
                    this.pos += copylen;
                }
            }
        }
        throw new ArrayIndexOutOfBoundsException(buffer.length);
        return copylen;
    }

    @Override // java.io.InputStream
    public synchronized void reset() {
        Log.d(TAG, "reset");
        this.pos = this.mark;
    }

    @Override // java.io.InputStream
    public synchronized long skip(long byteCount) {
        long j = 0;
        synchronized (this) {
            Log.d(TAG, "skip");
            if (byteCount > 0) {
                int temp = this.pos;
                this.pos = ((long) (this.count - this.pos)) < byteCount ? this.count : (int) (this.pos + byteCount);
                j = this.pos - temp;
            }
        }
        return j;
    }
}
