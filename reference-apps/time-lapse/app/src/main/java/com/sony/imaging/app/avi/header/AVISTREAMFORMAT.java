package com.sony.imaging.app.avi.header;

/* loaded from: classes.dex */
public class AVISTREAMFORMAT extends StructIO {
    private static final int cb = 40;
    private static final int dwBiClrImportant = 0;
    private static final int dwBiClrUsed = 0;
    private static final int dwBiSize = 40;
    private static final int dwBiXPelsPerMeter = 0;
    private static final int dwBiYPelsPerMeter = 0;
    private static final short wBiBitCount = 24;
    private static final short wBiPlanes = 1;
    private static final byte[] fcc = {115, 116, 114, 102};
    private static final byte[] biCompression = {77, 74, 80, 71};
    private int dwBiWidth = 1280;
    private int dwBiHeight = 720;
    private int dwBiSizeImage = ((this.dwBiWidth * this.dwBiHeight) * 24) / 8;

    public void setPictureSize(int width, int height) {
        this.dwBiWidth = width;
        this.dwBiHeight = height;
        this.dwBiSizeImage = ((this.dwBiWidth * this.dwBiHeight) * 24) / 8;
    }

    public int setDataToBuf(byte[] buf, int ofst) {
        int ofst2 = ofst + setByteArrayAt(fcc, buf, ofst, false);
        int ofst3 = ofst2 + setIntAt(40, buf, ofst2, false);
        int ofst4 = ofst3 + setIntAt(40, buf, ofst3, false);
        int ofst5 = ofst4 + setIntAt(this.dwBiWidth, buf, ofst4, false);
        int ofst6 = ofst5 + setIntAt(this.dwBiHeight, buf, ofst5, false);
        int ofst7 = ofst6 + setShortAt((short) 1, buf, ofst6, false);
        int ofst8 = ofst7 + setShortAt(wBiBitCount, buf, ofst7, false);
        int ofst9 = ofst8 + setByteArrayAt(biCompression, buf, ofst8, false);
        int ofst10 = ofst9 + setIntAt(this.dwBiSizeImage, buf, ofst9, false);
        int ofst11 = ofst10 + setIntAt(0, buf, ofst10, false);
        int ofst12 = ofst11 + setIntAt(0, buf, ofst11, false);
        int ofst13 = ofst12 + setIntAt(0, buf, ofst12, false);
        return (ofst13 + setIntAt(0, buf, ofst13, false)) - ofst;
    }
}
