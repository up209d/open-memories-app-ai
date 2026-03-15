package com.sony.imaging.app.srctrl.liveview;

import android.util.Log;
import com.sony.scalar.hardware.CameraSequence;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.hardware.DeviceMemory;
import com.sony.scalar.lib.ssdpdevice.SsdpDevice;
import java.nio.ByteBuffer;
import java.util.Arrays;

/* loaded from: classes.dex */
public class JpegLoader {
    private ByteBuffer bbuf;
    private LiveviewData liveviewData;
    private final String TAG = JpegLoader.class.getName();
    private long lastGetJpegTime = 0;
    private int getCount = 0;
    private long getCountStartTime = -1;
    private long totalSentDataSize = 0;
    private int mCAPA = getCAPA();

    /* loaded from: classes.dex */
    public static class LiveviewData {
        public byte[] headerData;
        public int headerDataSize;
        public byte[] jpegData;
        public int jpegDataAndPaddingSize;
    }

    public JpegLoader() {
        this.bbuf = null;
        this.liveviewData = null;
        this.bbuf = ByteBuffer.allocateDirect(this.mCAPA);
        this.liveviewData = new LiveviewData();
        this.liveviewData.headerData = new byte[LiveviewCommon.HEADER_DATA_SIZE_MAX];
        this.liveviewData.headerDataSize = LiveviewCommon.HEADER_DATA_SIZE_MAX;
        this.liveviewData.jpegData = new byte[this.mCAPA];
    }

    public LiveviewData getLiveviewData(CameraSequence camseq) {
        if (camseq == null) {
            Log.v(this.TAG, "CameraSequence is not started.");
            return null;
        }
        long currentTime = getCurrentTimeAndCheckInterval();
        if (currentTime < 0) {
            return null;
        }
        this.lastGetJpegTime = currentTime;
        int dataSize = getJpegData(camseq, this.liveviewData.jpegData);
        if (dataSize <= 0) {
            Log.w(this.TAG, "Failed to get picture.");
            return null;
        }
        this.liveviewData.jpegDataAndPaddingSize = makeHeaderAndPadding(dataSize);
        this.totalSentDataSize += dataSize;
        printInfo(currentTime);
        return this.liveviewData;
    }

    private long getCurrentTimeAndCheckInterval() {
        long currentTime = System.currentTimeMillis();
        long interval = (currentTime - this.lastGetJpegTime) + 5;
        if (interval < 30) {
            return -1L;
        }
        return currentTime;
    }

    private void printInfo(long currentTime) {
        if (this.getCountStartTime < 0) {
            this.getCountStartTime = currentTime;
            this.getCount = 0;
        }
        this.getCount++;
        long period = currentTime - this.getCountStartTime;
        if (period > 5000) {
            long fps = (this.getCount * SsdpDevice.RETRY_INTERVAL) / period;
            long avarageDataSize = (this.totalSentDataSize / 1024) / this.getCount;
            Log.v(this.TAG, "Output is " + String.valueOf(fps) + " [fps] / " + String.valueOf(avarageDataSize) + "[KiB/f]");
            this.getCount = 0;
            this.totalSentDataSize = 0L;
            this.getCountStartTime = currentTime;
        }
    }

    private int getJpegData(CameraSequence camseq, byte[] dataBuf) {
        try {
            DeviceBuffer[] previewSequenceFrames = camseq.getPreviewSequenceFrames(1);
            if (previewSequenceFrames == null) {
                return -1;
            }
            DeviceBuffer dbuf = previewSequenceFrames[0];
            int dataSize = Math.min(dbuf.getSize(), this.bbuf.capacity());
            if (dataSize > this.mCAPA) {
                String msgDataSizeOver = "JPEG data size (=" + String.valueOf(dataSize) + ") is over! (CAP=" + String.valueOf(this.mCAPA) + " bytes)";
                Log.v(this.TAG, msgDataSizeOver);
                releaseDeviceMemories(previewSequenceFrames);
                return -1;
            }
            this.bbuf.rewind();
            dbuf.read(this.bbuf, Math.min(dataSize, this.bbuf.capacity()), 0);
            releaseDeviceMemories(previewSequenceFrames);
            this.bbuf.get(dataBuf);
            return dataSize;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private int makeHeaderAndPadding(int jpegDataSize) {
        byte[] headerData = this.liveviewData.headerData;
        Arrays.fill(headerData, (byte) 0);
        headerData[0] = -1;
        headerData[1] = 1;
        LiveviewCommon.incrementSequenceNumber();
        LiveviewCommon.setNetworkByte(headerData, 2, LiveviewCommon.getCurrentSequenceNumber(), 2);
        int timeStamp = LiveviewCommon.getTimeStamp();
        LiveviewCommon.setNetworkByte(headerData, 4, timeStamp, 4);
        headerData[8] = LiveviewCommon.PAYLOAD_HEADER_START_CODE_BYTE1;
        headerData[9] = LiveviewCommon.PAYLOAD_HEADER_START_CODE_BYTE2;
        headerData[10] = LiveviewCommon.PAYLOAD_HEADER_START_CODE_BYTE3;
        headerData[11] = LiveviewCommon.PAYLOAD_HEADER_START_CODE_BYTE4;
        LiveviewCommon.setNetworkByte(headerData, 12, jpegDataSize, 3);
        int paddingSize = 4 - (jpegDataSize % 4);
        if (paddingSize == 4) {
            paddingSize = 0;
        }
        LiveviewCommon.setNetworkByte(headerData, 15, paddingSize, 1);
        LiveviewCommon.setNetworkByte(headerData, 16, 0, 2);
        LiveviewCommon.setNetworkByte(headerData, 18, 0, 2);
        headerData[20] = 0;
        return jpegDataSize + paddingSize;
    }

    private static int getCAPA() {
        LiveviewContainer liveview = LiveviewContainer.getInstance();
        int CAPA = liveview.getMaxFileSize() * 1024;
        return CAPA;
    }

    private static void releaseDeviceMemories(DeviceMemory[] memories) {
        for (DeviceMemory dbuf : memories) {
            dbuf.release();
        }
    }
}
