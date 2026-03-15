package com.sony.imaging.app.avi.header;

import android.util.Log;
import com.sony.imaging.app.avi.FileOutputStreamUtil;
import java.io.IOException;
import java.io.RandomAccessFile;

/* loaded from: classes.dex */
public class AVITOP extends StructIO {
    private static final int BUFSIZE = 32768;
    private static final int dwLISThdrlSize = 192;
    private static final int dwLISTstrlSize = 116;
    private static final byte[] RIFF = {82, 73, 70, 70};
    private static final byte[] AVI = {65, 86, 73, 32};
    private static final byte[] LISThdrl = {76, 73, 83, 84};
    private static final byte[] hdrl = {104, 100, 114, 108};
    private static final byte[] LISTstrl = {76, 73, 83, 84};
    private static final byte[] strl = {115, 116, 114, 108};
    private static final byte[] LISTmovi = {76, 73, 83, 84};
    private static final byte[] movi = {109, 111, 118, 105};
    private int fileSize = -1;
    private AVIMAINHEADER avih = new AVIMAINHEADER();
    private AVISTREAMHEADER strh = new AVISTREAMHEADER();
    private AVISTREAMFORMAT strf = new AVISTREAMFORMAT();
    private int dwLISTmoviSize = -1;

    public int getFileSize() {
        return this.fileSize;
    }

    public void setStreamSize(int streamsize, int indexsize) {
        this.fileSize = ((streamsize + 220) + indexsize) - 4;
        this.dwLISTmoviSize = streamsize + 4;
    }

    public void setFrameNum(int framenum) {
        this.avih.setFrameNum(framenum);
        this.strh.setFrameNum(framenum);
    }

    public void setFrameRate(int scale, int rate) {
        this.avih.setFrameRate(scale, rate);
        this.strh.setFrameRate(scale, rate);
    }

    public void setPictureSize(int width, int height) {
        this.avih.setPictureSize(width, height);
        this.strh.setPictureSize((short) width, (short) height);
        this.strf.setPictureSize(width, height);
    }

    public int setDataToBuf(byte[] buf, int ofst) {
        int ofst2 = ofst + setByteArrayAt(RIFF, buf, ofst, false);
        int ofst3 = ofst2 + setIntAt(this.fileSize, buf, ofst2, false);
        int ofst4 = ofst3 + setByteArrayAt(AVI, buf, ofst3, false);
        int ofst5 = ofst4 + setByteArrayAt(LISThdrl, buf, ofst4, false);
        int ofst6 = ofst5 + setIntAt(dwLISThdrlSize, buf, ofst5, false);
        int ofst7 = ofst6 + setByteArrayAt(hdrl, buf, ofst6, false);
        int ofst8 = ofst7 + this.avih.setDataToBuf(buf, ofst7);
        int ofst9 = ofst8 + setByteArrayAt(LISTstrl, buf, ofst8, false);
        int ofst10 = ofst9 + setIntAt(dwLISTstrlSize, buf, ofst9, false);
        int ofst11 = ofst10 + setByteArrayAt(strl, buf, ofst10, false);
        int ofst12 = ofst11 + this.strh.setDataToBuf(buf, ofst11);
        int ofst13 = ofst12 + this.strf.setDataToBuf(buf, ofst12);
        int ofst14 = ofst13 + setByteArrayAt(LISTmovi, buf, ofst13, false);
        int ofst15 = ofst14 + setIntAt(this.dwLISTmoviSize, buf, ofst14, false);
        return (ofst15 + setByteArrayAt(movi, buf, ofst15, false)) - ofst;
    }

    public int setDataToOstream(FileOutputStreamUtil os) {
        byte[] tmpbuf = new byte[32768];
        int ofst = 0 + setDataToBuf(tmpbuf, 0);
        try {
            os.write(tmpbuf, 0, ofst);
        } catch (IOException e) {
            Log.e("Test", e.toString());
            e.printStackTrace();
        }
        return ofst;
    }

    public int setDataToRAF(RandomAccessFile raf) {
        byte[] tmpbuf = new byte[32768];
        int ofst = 0 + setDataToBuf(tmpbuf, 0);
        try {
            raf.seek(0L);
            raf.write(tmpbuf, 0, ofst);
        } catch (IOException e) {
            Log.e("Test", e.toString());
            e.printStackTrace();
        }
        return ofst;
    }
}
