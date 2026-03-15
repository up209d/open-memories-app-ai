package com.sony.imaging.app.avi;

import android.util.Log;
import com.sony.imaging.app.avi.sa.SaUtil;
import java.io.IOException;
import java.io.RandomAccessFile;

/* loaded from: classes.dex */
public class AviIndexParser {
    private static final int FRAMENUM_ERROR = -1;
    public static final int GETFRAME_ERROR = -1;
    private static final int IDX1SIZE = 16;
    private static final int MAXFRAMENUM = 4096;
    private byte[] m_Idx1List = null;
    private int m_sizeOfIdx = 0;
    private int m_frameNum = 0;
    private int mAviOfstDwLISTmoviSize = 216;
    private int mAviSizeDwLISTmoviSize = 4;
    private int mAviOfstMovi = this.mAviOfstDwLISTmoviSize + this.mAviSizeDwLISTmoviSize;
    private int mSizeOfIdx1Tag = 8;
    private int mSizeOf00DCTag = 8;
    private int mOfsetToOffset = 8;
    private int mOfsetToSize = 12;

    public void release() {
        this.m_Idx1List = null;
        this.m_sizeOfIdx = 0;
        this.m_frameNum = 0;
    }

    public boolean setIndex(RandomAccessFile raf) {
        this.m_Idx1List = new byte[65536];
        try {
            raf.seek(this.mAviOfstDwLISTmoviSize);
            try {
                int dwLISTmoviSize = SaUtil.INT_little_endian_TO_big_endian(raf.readInt());
                int offsetToIdx1Data = this.mAviOfstMovi + dwLISTmoviSize + this.mSizeOfIdx1Tag;
                try {
                    raf.seek(offsetToIdx1Data);
                    try {
                        this.m_sizeOfIdx = raf.read(this.m_Idx1List);
                        this.m_frameNum = this.m_sizeOfIdx / 16;
                        if (this.m_frameNum >= MAXFRAMENUM) {
                            this.m_frameNum = -1;
                            Log.e("AviIndexParser", "m_frameNum is too large!!");
                            return false;
                        }
                        return true;
                    } catch (IOException e) {
                        Log.e("AviIndexParser", e.toString());
                        e.printStackTrace();
                        return false;
                    }
                } catch (IOException e2) {
                    Log.e("AviIndexParser", e2.toString());
                    e2.printStackTrace();
                    return false;
                }
            } catch (IOException e3) {
                Log.e("AviIndexParser", e3.toString());
                e3.printStackTrace();
                return false;
            }
        } catch (IOException e4) {
            Log.e("AviIndexParser", e4.toString());
            e4.printStackTrace();
            return false;
        }
    }

    private int getOffset(int frameNum) throws Exception {
        int result = this.mSizeOf00DCTag + SaUtil.readLittleIntFromByteArray(this.m_Idx1List, (frameNum * 16) + this.mOfsetToOffset) + this.mAviOfstMovi;
        Log.i("AviIndexParser", "Offset(" + frameNum + ")=" + result);
        return result;
    }

    private int getSize(int frameNum) throws Exception {
        int result = SaUtil.readLittleIntFromByteArray(this.m_Idx1List, (frameNum * 16) + this.mOfsetToSize);
        Log.i("AviIndexParser", "Size(" + frameNum + ")=" + result);
        return result;
    }

    public int getFrameNum() {
        return this.m_frameNum;
    }

    public int getFrame(int frameNum, RandomAccessFile raf, byte[] ba) {
        if (frameNum >= this.m_frameNum) {
            return -1;
        }
        try {
            int offset = getOffset(frameNum);
            int size = getSize(frameNum);
            if (ba == null) {
                return -1;
            }
            if (size <= 0 || size > ba.length || offset <= 0) {
                return -1;
            }
            if (raf == null) {
                return -1;
            }
            try {
                raf.seek(0L);
                raf.seek(offset);
                try {
                    raf.read(ba, 0, size);
                    return size;
                } catch (IOException e) {
                    Log.e("AviIndexParser", e.toString());
                    e.printStackTrace();
                    return -1;
                } catch (IndexOutOfBoundsException e2) {
                    Log.e("AviIndexParser", e2.toString());
                    e2.printStackTrace();
                    return -1;
                }
            } catch (IOException e3) {
                Log.e("AviIndexParser", e3.toString());
                e3.printStackTrace();
                return -1;
            }
        } catch (Exception e4) {
            Log.e(toString(), e4.toString());
            e4.printStackTrace();
            return -1;
        }
    }
}
