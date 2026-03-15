package com.sony.imaging.app.avi.header;

import android.util.Log;
import com.sony.imaging.app.avi.FileOutputStreamUtil;
import java.io.IOException;

/* loaded from: classes.dex */
public class AVIINDEX extends StructIO {
    private static final byte[] fcc = {105, 100, 120, 49};
    private int cb;
    private AVIINDEXENTRY aIndex = new AVIINDEXENTRY();
    private int entryNum = 0;

    public void createAVIINDEXENTRY(int numOfEntry) {
        this.cb = 0;
        this.entryNum = 0;
    }

    public void releaseAVIINDEXENTRY() {
        this.cb = 0;
        this.entryNum = 0;
    }

    public void setAVIINDEXENTRY(int size) {
        if (this.entryNum == 0) {
            this.aIndex.setOffset(4);
            this.aIndex.setSize(size);
        } else {
            this.aIndex.setOffset(this.aIndex.getOffset() + this.aIndex.getSize() + 8);
            this.aIndex.setSize(size);
        }
        this.entryNum++;
    }

    public int setDataToBuf(byte[] buf, int ofst, int[] sizelist, int listNum) {
        this.cb = listNum * 16;
        int ofst2 = ofst + setByteArrayAt(fcc, buf, ofst, false);
        int ofst3 = ofst2 + setIntAt(this.cb, buf, ofst2, false);
        for (int i = 0; i < this.entryNum; i++) {
            setAVIINDEXENTRY(sizelist[i]);
            ofst3 += this.aIndex.setDataToBuf(buf, ofst3);
        }
        return ofst3 - ofst;
    }

    public int setDataToOstream(FileOutputStreamUtil os, int[] sizelist, int listNum) {
        byte[] buf = new byte[16];
        this.cb = listNum * 16;
        int size = 0 + setByteArrayAt(fcc, buf, 0, false) + setIntAt(this.cb, buf, 4, false);
        try {
            os.write(buf, 0, 8);
            for (int n = 0; n < listNum; n++) {
                setAVIINDEXENTRY(sizelist[n]);
                size += this.aIndex.setDataToOstream(os, buf);
            }
        } catch (IOException e) {
            Log.e("Test", e.toString());
            e.printStackTrace();
        }
        return size;
    }
}
