package com.sony.imaging.app.avi;

import android.util.Log;
import com.sony.imaging.app.base.common.widget.BatteryIcon;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/* loaded from: classes.dex */
public class FileOutputStreamUtil {
    private static final int PACKET_BUFFER_SIZE = 65536;
    private static final String TAG = "FileOutputStreamUtil";
    private FileOutputStream mFileOutputStream;
    private byte[] mPacketBuffer = null;
    private int mPacketBufferedLength = 0;

    private void commonNew() {
        this.mPacketBuffer = new byte[PACKET_BUFFER_SIZE];
        this.mPacketBufferedLength = 0;
    }

    private void commonDelete() {
        this.mPacketBuffer = null;
        this.mPacketBufferedLength = 0;
    }

    public FileOutputStreamUtil(File file, boolean append) throws FileNotFoundException {
        this.mFileOutputStream = null;
        this.mFileOutputStream = new FileOutputStream(file, append);
        commonNew();
    }

    public FileOutputStreamUtil(File file) throws FileNotFoundException {
        this.mFileOutputStream = null;
        this.mFileOutputStream = new FileOutputStream(file);
        commonNew();
    }

    public FileOutputStreamUtil(FileDescriptor fd) {
        this.mFileOutputStream = null;
        this.mFileOutputStream = new FileOutputStream(fd);
        commonNew();
    }

    public FileOutputStreamUtil(String path, boolean append) throws FileNotFoundException {
        this.mFileOutputStream = null;
        this.mFileOutputStream = new FileOutputStream(path, append);
        commonNew();
    }

    public FileOutputStreamUtil(String path) throws FileNotFoundException {
        this.mFileOutputStream = null;
        Log.i(TAG, "FileOutputStreamUtil( " + path + " )");
        this.mFileOutputStream = new FileOutputStream(path);
        commonNew();
    }

    public void close() throws IOException {
        Log.i(TAG, "FileOutputStreamUtil#close()");
        flushPacketBuffer();
        commonDelete();
        if (this.mFileOutputStream != null) {
            try {
                this.mFileOutputStream.close();
            } finally {
                this.mFileOutputStream = null;
            }
        }
    }

    public void write(byte[] buffer, int offset, int count) throws IOException {
        Log.i(TAG, "write( " + buffer + ", " + offset + ", " + count + " )");
        int packetLength = setPacketBuffer(buffer, offset, count);
        if (this.mPacketBufferedLength == PACKET_BUFFER_SIZE) {
            this.mFileOutputStream.write(this.mPacketBuffer, 0, PACKET_BUFFER_SIZE);
            this.mPacketBufferedLength = 0;
        }
        int count2 = count - packetLength;
        int offset2 = offset + packetLength;
        while (count2 >= PACKET_BUFFER_SIZE) {
            Log.i(TAG, " count = " + count2);
            int currentSize = Math.min(count2, PACKET_BUFFER_SIZE);
            this.mFileOutputStream.write(buffer, offset2, currentSize);
            count2 -= currentSize;
            offset2 += currentSize;
        }
        setPacketBuffer(buffer, offset2, count2);
    }

    public void write(byte[] buffer) throws IOException {
        write(buffer, 0, buffer.length);
    }

    public void write(int oneByte) throws IOException {
        byte[] data = {0};
        data[0] = (byte) (oneByte & BatteryIcon.BATTERY_STATUS_CHARGING);
        write(data, 0, 1);
    }

    private int setPacketBuffer(byte[] buffer, int offset, int count) {
        if (count == 0) {
            return 0;
        }
        int copyLength = Math.min(PACKET_BUFFER_SIZE - this.mPacketBufferedLength, count);
        System.arraycopy(buffer, offset, this.mPacketBuffer, this.mPacketBufferedLength, copyLength);
        this.mPacketBufferedLength += copyLength;
        return copyLength;
    }

    private void flushPacketBuffer() throws IOException {
        if (this.mPacketBufferedLength != 0) {
            this.mFileOutputStream.write(this.mPacketBuffer, 0, this.mPacketBufferedLength);
            this.mPacketBufferedLength = 0;
        }
    }
}
