package com.sony.imaging.app.avi.header;

/* loaded from: classes.dex */
public class AVIMAINHEADER extends StructIO {
    private static final int cb = 56;
    private static final int dwFlags = 16;
    private static final int dwInitialFrames = 0;
    private static final int dwPaddingGranularity = 0;
    private static final int dwStreams = 1;
    private static final byte[] fcc = {97, 118, 105, 104};
    private static final int[] dwReserved = {0, 0, 0, 0};
    private int dwMicroSecPerFrame = 33366;
    private int dwMaxBytesPerSec = 0;
    private int dwTotalFrames = -1;
    private int dwSuggestedBufferSize = 0;
    private int dwWidth = 1280;
    private int dwHeight = 720;

    public void setFrameRate(int scale, int rate) {
        this.dwMicroSecPerFrame = ((scale * 1000) * 1000) / rate;
    }

    public void setPictureSize(int width, int height) {
        this.dwWidth = width;
        this.dwHeight = height;
        this.dwMaxBytesPerSec = (((this.dwWidth * this.dwHeight) * 2) * 30) / 5;
    }

    public void setFrameNum(int framenum) {
        this.dwTotalFrames = framenum;
    }

    public int setDataToBuf(byte[] buf, int ofst) {
        int ofst2 = ofst + setByteArrayAt(fcc, buf, ofst, false);
        int ofst3 = ofst2 + setIntAt(cb, buf, ofst2, false);
        int ofst4 = ofst3 + setIntAt(this.dwMicroSecPerFrame, buf, ofst3, false);
        int ofst5 = ofst4 + setIntAt(this.dwMaxBytesPerSec, buf, ofst4, false);
        int ofst6 = ofst5 + setIntAt(0, buf, ofst5, false);
        int ofst7 = ofst6 + setIntAt(16, buf, ofst6, false);
        int ofst8 = ofst7 + setIntAt(this.dwTotalFrames, buf, ofst7, false);
        int ofst9 = ofst8 + setIntAt(0, buf, ofst8, false);
        int ofst10 = ofst9 + setIntAt(1, buf, ofst9, false);
        int ofst11 = ofst10 + setIntAt(this.dwSuggestedBufferSize, buf, ofst10, false);
        int ofst12 = ofst11 + setIntAt(this.dwWidth, buf, ofst11, false);
        int ofst13 = ofst12 + setIntAt(this.dwHeight, buf, ofst12, false);
        return (ofst13 + setIntArrayAt(dwReserved, buf, ofst13, false)) - ofst;
    }
}
