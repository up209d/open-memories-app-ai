package com.sony.imaging.app.avi.header;

/* loaded from: classes.dex */
public class AVISTREAMHEADER extends StructIO {
    private static final int cb = 56;
    private static final int dwFlags = 0;
    private static final int dwInitialFrames = 0;
    private static final int dwQuality = 10000;
    private static final int dwSampleSize = 0;
    private static final int dwStart = 0;
    private static final short wLanguage = 0;
    private static final short wPriority = 0;
    private static final byte[] fcc = {115, 116, 114, 104};
    private static final byte[] fccType = {118, 105, 100, 115};
    private static final byte[] fccHandler = {109, 106, 112, 103};
    private int dwScale = 1001;
    private int dwRate = 30000;
    private int dwLength = -1;
    private int dwSuggestedBufferSize = -1;
    private short[] rcFrame = {0, 0, 1280, 720};

    public void setFrameRate(int scale, int rate) {
        this.dwScale = scale;
        this.dwRate = rate;
    }

    public void setFrameNum(int framenum) {
        this.dwLength = framenum;
    }

    public void setPictureSize(short width, short height) {
        this.rcFrame[2] = width;
        this.rcFrame[3] = height;
        this.dwSuggestedBufferSize = ((width * height) * 2) / 5;
    }

    public int setDataToBuf(byte[] buf, int ofst) {
        int ofst2 = ofst + setByteArrayAt(fcc, buf, ofst, false);
        int ofst3 = ofst2 + setIntAt(cb, buf, ofst2, false);
        int ofst4 = ofst3 + setByteArrayAt(fccType, buf, ofst3, false);
        int ofst5 = ofst4 + setByteArrayAt(fccHandler, buf, ofst4, false);
        int ofst6 = ofst5 + setIntAt(0, buf, ofst5, false);
        int ofst7 = ofst6 + setShortAt((short) 0, buf, ofst6, false);
        int ofst8 = ofst7 + setShortAt((short) 0, buf, ofst7, false);
        int ofst9 = ofst8 + setIntAt(0, buf, ofst8, false);
        int ofst10 = ofst9 + setIntAt(this.dwScale, buf, ofst9, false);
        int ofst11 = ofst10 + setIntAt(this.dwRate, buf, ofst10, false);
        int ofst12 = ofst11 + setIntAt(0, buf, ofst11, false);
        int ofst13 = ofst12 + setIntAt(this.dwLength, buf, ofst12, false);
        int ofst14 = ofst13 + setIntAt(this.dwSuggestedBufferSize, buf, ofst13, false);
        int ofst15 = ofst14 + setIntAt(dwQuality, buf, ofst14, false);
        int ofst16 = ofst15 + setIntAt(0, buf, ofst15, false);
        return (ofst16 + setShortArrayAt(this.rcFrame, buf, ofst16, false)) - ofst;
    }
}
