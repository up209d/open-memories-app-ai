package com.sony.imaging.app.avi.header;

import android.util.Log;
import com.sony.imaging.app.avi.FileOutputStreamUtil;
import com.sony.scalar.hardware.DeviceBuffer;
import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class AVI00DC extends StructIO {
    public static final int AVI00DC_HEADER_SIZE_BYTE_8 = 8;
    private static final String TAG = "AVI00DC";
    private static final byte[] fcc = {48, 48, 100, 99};
    static final int idxofFFE1 = 4;
    static final int idxofFFE2 = 2;
    static final int sizeofFFD8 = 2;

    public static int setDataToBuf(byte[] buf, int ofst, DeviceBuffer dbuf) {
        int jpegSize = dbuf.getSize();
        ByteBuffer bbuf = ByteBuffer.allocateDirect(jpegSize);
        dbuf.read(bbuf);
        dbuf.release();
        bbuf.get(buf, ofst + 8, jpegSize);
        int ofst2 = ofst + setByteArrayAt(fcc, buf, ofst, false);
        return ((ofst2 + setIntAt(jpegSize, buf, ofst2, false)) + jpegSize) - ofst;
    }

    public static int setDataToOstream(FileOutputStreamUtil os, DeviceBuffer dbuf, byte[] tmpbuf) {
        int jpegSize = dbuf.getSize();
        ByteBuffer bbuf = ByteBuffer.allocateDirect(jpegSize);
        dbuf.read(bbuf);
        dbuf.release();
        int ofst = 0 + setByteArrayAt(fcc, tmpbuf, 0, false);
        int ofst2 = ofst + setIntAt(jpegSize, tmpbuf, ofst, false);
        bbuf.get(tmpbuf, 8, jpegSize);
        int ofst3 = ofst2 + jpegSize;
        try {
            os.write(tmpbuf, 0, ofst3);
        } catch (IOException e) {
            Log.e("Test", e.toString());
            e.printStackTrace();
        }
        return ofst3;
    }

    public static int setDataToOstreamWithExifRemoval(FileOutputStreamUtil os, byte[] jpeg, int jpeg_size) {
        int paddingSize;
        byte[] headbuf = new byte[8];
        byte[] FFD8 = {-1, -40};
        int offsetMainJpeg = findMainJpeg(jpeg, jpeg_size);
        int sizeMainJpeg = (jpeg_size - offsetMainJpeg) + 2;
        if ((sizeMainJpeg & 1) == 1) {
            sizeMainJpeg++;
            paddingSize = 1;
        } else {
            paddingSize = 0;
        }
        int size = 0 + setByteArrayAt(fcc, headbuf, 0, false);
        int size2 = size + setIntAt(sizeMainJpeg, headbuf, 4, false) + sizeMainJpeg;
        try {
            os.write(headbuf, 0, headbuf.length);
            os.write(FFD8, 0, FFD8.length);
            os.write(jpeg, offsetMainJpeg, (sizeMainJpeg - 2) - paddingSize);
            if (paddingSize != 0) {
                os.write(0);
            }
        } catch (IOException e) {
            Log.e("Test", e.toString());
            e.printStackTrace();
        }
        return size2;
    }

    private static int findMainJpeg(byte[] jpeg, int jpeg_size) {
        int sizeofFFE1 = ((jpeg[4] << 8) & 65280) + (jpeg[5] & 255);
        int totalIdxofFFE2 = sizeofFFE1 + 4 + 2;
        int sizeofFFE2 = ((jpeg[totalIdxofFFE2 + 0] << 8) & 65280) + (jpeg[totalIdxofFFE2 + 1] & 255);
        int idx = sizeofFFE1 + 4 + 2 + sizeofFFE2;
        if (jpeg[idx + 0] == -1 && jpeg[idx + 1] == -37) {
            Log.i("avi", "remove EXIF and thum OK.");
            return idx;
        }
        Log.e("avi", "remove EXIF and thum failed!!! jpeg_size=" + jpeg_size + " idx=" + idx);
        Log.e("avi", "jpeg[idx+0]=" + ((int) jpeg[idx + 0]));
        Log.e("avi", "jpeg[idx+1]=" + ((int) jpeg[idx + 1]));
        return 0;
    }

    public static int setDataToOstream(FileOutputStreamUtil os, byte[] jpeg, int jpeg_size) {
        int paddingSize;
        byte[] headbuf = new byte[8];
        int sizeMainJpeg = jpeg_size;
        if ((sizeMainJpeg & 1) == 1) {
            sizeMainJpeg++;
            paddingSize = 1;
        } else {
            paddingSize = 0;
        }
        int size = 0 + setByteArrayAt(fcc, headbuf, 0, false) + setIntAt(sizeMainJpeg, headbuf, 4, false) + sizeMainJpeg;
        long t0 = System.nanoTime();
        try {
            os.write(headbuf, 0, headbuf.length);
            os.write(jpeg, 0, sizeMainJpeg - paddingSize);
            if (paddingSize != 0) {
                os.write(0);
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
            size = -size;
        }
        long t1 = System.nanoTime();
        Log.e("100secissue", "OutputStream#write time = " + (t1 - t0) + "ns");
        return size;
    }
}
