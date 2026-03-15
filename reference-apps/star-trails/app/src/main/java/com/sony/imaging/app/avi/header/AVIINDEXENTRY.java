package com.sony.imaging.app.avi.header;

import com.sony.imaging.app.avi.FileOutputStreamUtil;
import java.io.IOException;

/* loaded from: classes.dex */
public class AVIINDEXENTRY extends StructIO {
    private static final byte[] dwChunkId = {48, 48, 100, 99};
    private static final int dwFlags = 16;
    private int dwOffset = 0;
    private int dwSize = 0;

    public void setOffset(int value) {
        this.dwOffset = value;
    }

    public void setSize(int value) {
        this.dwSize = value;
    }

    public int getOffset() {
        return this.dwOffset;
    }

    public int getSize() {
        return this.dwSize;
    }

    public int setDataToBuf(byte[] buf, int ofst) {
        int ofst2 = ofst + setByteArrayAt(dwChunkId, buf, ofst, false);
        int ofst3 = ofst2 + setIntAt(16, buf, ofst2, false);
        int ofst4 = ofst3 + setIntAt(this.dwOffset, buf, ofst3, false);
        return (ofst4 + setIntAt(this.dwSize, buf, ofst4, false)) - ofst;
    }

    public int setDataToOstream(FileOutputStreamUtil os, byte[] buf) throws IOException {
        int size = setDataToBuf(buf, 0);
        os.write(buf, 0, 16);
        return size;
    }
}
