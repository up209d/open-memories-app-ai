package com.sony.imaging.app.srctrl.streaming;

import android.util.Log;
import com.sony.imaging.app.base.playback.player.MediaPlayerManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.srctrl.liveview.LiveviewCommon;
import com.sony.imaging.app.srctrl.liveview.LiveviewContainer;
import com.sony.imaging.app.srctrl.liveview.LiveviewLoader;
import com.sony.imaging.app.srctrl.webapi.servlet.StreamingChunkTransfer;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.hardware.DeviceMemory;
import com.sony.scalar.hardware.YUVPlaneExtractor;
import com.sony.scalar.lib.ssdpdevice.SsdpDevice;
import com.sony.scalar.media.MediaPlayer;
import java.nio.ByteBuffer;
import java.util.Arrays;

/* loaded from: classes.dex */
public class StreamingLoader {
    static final int COMMON_HEADER_PAYLOAD_TYPE_STREAMING_IMAGE = 17;
    static final int COMMON_HEADER_PAYLOAD_TYPE_STREAMING_INFO = 18;
    private static final int COMMON_HEADER_SIZE = 8;
    static final int COMMON_HEADER_START_BYTE = 255;
    private static final int FRAME_INTERVAL = 30;
    private static final int HEADER_DATA_SIZE_MAX = 136;
    private static final int PAYLOAD_HEADER_SIZE = 128;
    static final int PAYLOAD_HEADER_START_CODE_BYTE1 = 36;
    static final int PAYLOAD_HEADER_START_CODE_BYTE2 = 53;
    static final int PAYLOAD_HEADER_START_CODE_BYTE3 = 104;
    static final int PAYLOAD_HEADER_START_CODE_BYTE4 = 121;
    public static final int STREAMING_OBTAINING_INTERVAL = 30;
    private static YUVPlaneExtractor mYUVPlane;
    private static StreamingLoader sInstance;
    private final byte PADDING_SIZE = 0;
    long commonHeaderTimeStampBase = 0;
    private int mCAPA;
    private static final String TAG = StreamingLoader.class.getSimpleName();
    private static int commonHeaderSequenceNumber = 0;
    private static JpegLoader loader = null;
    private static PlayInfoLoader playInfoLoader = null;
    private static volatile boolean isGenerating = false;
    private static volatile boolean isLoading = false;
    private static StreamingChunkTransfer chunkTransfer = null;
    private static final int[] mQParamList = {-2147417855, 131189, 7100710, 7962761, 66052, 16776944, AppRoot.USER_KEYCODE.SK2, -1667457892};

    static /* synthetic */ int access$608() {
        int i = commonHeaderSequenceNumber;
        commonHeaderSequenceNumber = i + 1;
        return i;
    }

    /* loaded from: classes.dex */
    public class StreamData {
        public byte[] headerData;
        public int headerDataSize;
        public byte[] jpegData;
        public int jpegDataAndPaddingSize;

        public StreamData() {
        }
    }

    /* loaded from: classes.dex */
    public class PlayInfoData {
        public byte[] headerData;
        public int headerDataSize;
        public byte[] infoData;
        public int infoDataSize;

        public PlayInfoData() {
        }
    }

    public static StreamingLoader getInstance() {
        if (sInstance == null) {
            sInstance = new StreamingLoader();
        }
        return sInstance;
    }

    public StreamingLoader() {
        isGenerating = false;
        isLoading = false;
        loader = new JpegLoader();
        playInfoLoader = new PlayInfoLoader();
    }

    private boolean startGeneratingPreview() {
        if (isGeneratingPreview()) {
            Log.v(TAG, "Streaming has been already being generated...");
            return true;
        }
        if (4 != RunStatus.getStatus()) {
            Log.e(TAG, "CAMERA STATUS ERROR: Camera is not running (Status=" + RunStatus.getStatus() + ") at " + Thread.currentThread().getStackTrace()[2].toString());
            return false;
        }
        LiveviewContainer liveview = LiveviewContainer.getInstance();
        mYUVPlane = new YUVPlaneExtractor();
        YUVPlaneExtractor.Options opts = new YUVPlaneExtractor.Options();
        opts.setOption("ENCODING_FRAME_RATE", liveview.getFrameRate());
        opts.setOption("ENCODING_WIDTH", liveview.getWidth());
        opts.setOption("ENCODING_HEIGHT", 0);
        opts.setOption("ENCODING_FORMAT", 256);
        opts.setOption("GET_ENCODED_DATA_MAX_NUM", 1);
        opts.setOption("JPEG_COMPRESS_RATE_DENOM", liveview.getCompressRate());
        opts.setOption("JPEG_COMPRESS_MAX_SIZE", liveview.getMaxFileSize());
        if (liveview.getLiveviewSize().equals(LiveviewContainer.s_LARGE_LIVEVIEW_SIZE)) {
            opts.setOption("JPEG_COMPRESS_QUALITY_PARAM_1", mQParamList[0]);
            opts.setOption("JPEG_COMPRESS_QUALITY_PARAM_2", mQParamList[1]);
            opts.setOption("JPEG_COMPRESS_QUALITY_PARAM_3", mQParamList[2]);
            opts.setOption("JPEG_COMPRESS_QUALITY_PARAM_4", mQParamList[3]);
            opts.setOption("JPEG_COMPRESS_QUALITY_PARAM_5", mQParamList[4]);
            opts.setOption("JPEG_COMPRESS_QUALITY_PARAM_6", mQParamList[5]);
            opts.setOption("JPEG_COMPRESS_QUALITY_PARAM_7", mQParamList[6]);
            opts.setOption("JPEG_COMPRESS_QUALITY_PARAM_8", mQParamList[7]);
        }
        if (!LiveviewLoader.isGeneratingPreview()) {
            try {
                mYUVPlane.startExtracting(opts);
                setGeneratingPreview(true);
                return true;
            } catch (RuntimeException e) {
                Log.e(TAG, "Failed to Start Extracting");
                return false;
            }
        }
        Log.e(TAG, "LiveviewLoader started");
        return false;
    }

    private boolean stopGeneratingPreview() {
        if (isGenerating) {
            setGeneratingPreview(false);
            try {
                mYUVPlane.stopExtracting();
                mYUVPlane.release();
            } catch (RuntimeException e) {
                Log.e(TAG, "Failed to stop Preview Sequence");
                return false;
            }
        }
        return true;
    }

    private static void setGeneratingPreview(boolean lo) {
        isGenerating = lo;
        Log.v(TAG, "generating = " + String.valueOf(isGenerating));
    }

    public static boolean isGeneratingPreview() {
        return isGenerating;
    }

    public static void setLoadingPreview(boolean lo) {
        if (isLoading != lo) {
            isLoading = lo;
            Log.v(TAG, "loading = " + String.valueOf(isLoading));
        }
    }

    public static boolean isLoadingPreview() {
        return isLoading;
    }

    public static void setStreamingChunkTransferInstance(StreamingChunkTransfer transfer) {
        chunkTransfer = transfer;
    }

    public static StreamData getStreamData() throws IllegalStateException {
        if (!isGeneratingPreview()) {
            Log.e(TAG, "StreamingLoader is not generating stream data.");
            throw new IllegalStateException();
        }
        return loader.getStreamData();
    }

    public static PlayInfoData getPlayInfoData() throws IllegalStateException {
        if (!isGeneratingPreview()) {
            Log.e(TAG, "StreamingLoader is not generating playinfo data.");
            throw new IllegalStateException();
        }
        return playInfoLoader.getPlayInfoData();
    }

    public static synchronized boolean startObtainingImages() {
        boolean ret;
        synchronized (StreamingLoader.class) {
            Log.v(TAG, "Make streaming ready!");
            ret = false;
            if (sInstance == null) {
                sInstance = new StreamingLoader();
            }
            if (sInstance != null) {
                ret = sInstance.startGeneratingPreview();
            }
        }
        return ret;
    }

    public static synchronized boolean stopObtainingImages() {
        synchronized (StreamingLoader.class) {
            if (isLoading) {
                setLoadingPreview(false);
            }
            if (sInstance != null) {
                Log.v(TAG, "Make streaming down!");
                sInstance.stopGeneratingPreview();
            }
        }
        return true;
    }

    public static synchronized void clean() {
        synchronized (StreamingLoader.class) {
            stopObtainingImages();
            if (sInstance != null) {
                sInstance.kill();
                sInstance = null;
            }
        }
    }

    public void kill() {
        if (chunkTransfer != null) {
            chunkTransfer.notifyGetScalarInfraIsKilled();
        }
        loader = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class JpegLoader {
        static final int PAYLOAD_HEADER_START_CODE_BYTE1 = 36;
        static final int PAYLOAD_HEADER_START_CODE_BYTE2 = 53;
        static final int PAYLOAD_HEADER_START_CODE_BYTE3 = 104;
        static final int PAYLOAD_HEADER_START_CODE_BYTE4 = 121;
        private ByteBuffer bbuf;
        long commonHeaderTimeStampBase;
        private DeviceBuffer dbuf;
        private byte[] headerData;
        private byte[] jpegData;
        private int jpegDataSize;
        private DeviceMemory[] memList;
        private StreamData streamData;
        private long lastGetJpegTime = 0;
        private int getCount = 0;
        private long getCountStartTime = -1;
        private long totalSentDataSize = 0;
        private final String TAG = JpegLoader.class.getName();

        public JpegLoader() {
            this.commonHeaderTimeStampBase = 0L;
            StreamingLoader.this.mCAPA = StreamingLoader.this.getCAPA();
            this.bbuf = ByteBuffer.allocateDirect(StreamingLoader.this.mCAPA);
            this.jpegData = new byte[StreamingLoader.this.mCAPA];
            this.headerData = new byte[136];
            this.streamData = new StreamData();
            this.streamData.headerData = this.headerData;
            this.streamData.headerDataSize = 136;
            this.streamData.jpegData = this.jpegData;
            this.commonHeaderTimeStampBase = System.currentTimeMillis();
            boolean unused = StreamingLoader.isLoading = false;
        }

        public StreamData getStreamData() throws IllegalStateException {
            if (StreamingLoader.isLoading) {
                long currentTime = getCurrentTimeAndCheckInterval();
                if (currentTime < 0) {
                    return null;
                }
                this.lastGetJpegTime = currentTime;
                int dataSize = getJpegData(this.jpegData);
                Log.v(this.TAG, "datasize" + dataSize);
                if (dataSize > 0) {
                    this.jpegDataSize = dataSize;
                } else if (this.jpegDataSize <= 0) {
                    Log.e(this.TAG, "Failed to get picture.");
                    return null;
                }
                this.streamData.jpegDataAndPaddingSize = makeHeaderAndPadding(currentTime);
                this.totalSentDataSize += this.jpegDataSize;
                printInfo(currentTime);
                return this.streamData;
            }
            Log.v(this.TAG, "JpegLoader is not loading stream data.");
            throw new IllegalStateException();
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

        private int getJpegData(byte[] dataBuf) {
            try {
                this.memList = StreamingLoader.mYUVPlane.getEncodedData(1);
                if (this.memList == null) {
                    Log.v(this.TAG, "memList == null");
                    return -1;
                }
                this.dbuf = this.memList[0];
                int dataSize = Math.min(this.dbuf.getSize(), this.bbuf.capacity());
                if (dataSize > StreamingLoader.this.mCAPA) {
                    String msgDataSizeOver = "JPEG data size (=" + String.valueOf(dataSize) + ") is over! (CAP=" + String.valueOf(StreamingLoader.this.mCAPA) + " bytes)";
                    Log.v(this.TAG, msgDataSizeOver);
                    StreamingLoader.releaseDeviceMemories(this.memList);
                    return -1;
                }
                this.bbuf.rewind();
                this.dbuf.read(this.bbuf, Math.min(dataSize, this.bbuf.capacity()), 0);
                StreamingLoader.releaseDeviceMemories(this.memList);
                this.bbuf.get(dataBuf);
                return dataSize;
            } catch (RuntimeException e) {
                e.printStackTrace();
                return -1;
            }
        }

        private int makeHeaderAndPadding(long currentTime) {
            Arrays.fill(this.headerData, (byte) 0);
            this.headerData[0] = -1;
            this.headerData[1] = 17;
            StreamingLoader.this.setNetworkByte(this.headerData, 2, StreamingLoader.commonHeaderSequenceNumber, 2);
            StreamingLoader.access$608();
            int timeStamp = (int) (System.currentTimeMillis() - this.commonHeaderTimeStampBase);
            StreamingLoader.this.setNetworkByte(this.headerData, 4, timeStamp, 4);
            this.headerData[8] = LiveviewCommon.PAYLOAD_HEADER_START_CODE_BYTE1;
            this.headerData[9] = LiveviewCommon.PAYLOAD_HEADER_START_CODE_BYTE2;
            this.headerData[10] = LiveviewCommon.PAYLOAD_HEADER_START_CODE_BYTE3;
            this.headerData[11] = LiveviewCommon.PAYLOAD_HEADER_START_CODE_BYTE4;
            StreamingLoader.this.setNetworkByte(this.headerData, 12, this.jpegDataSize, 3);
            int paddingSize = 4 - (this.jpegDataSize % 4);
            if (paddingSize == 4) {
                paddingSize = 0;
            }
            StreamingLoader.this.setNetworkByte(this.headerData, 15, paddingSize, 1);
            LiveviewContainer liveviewContainer = LiveviewContainer.getInstance();
            int pixelWidth = liveviewContainer.getWidth();
            StreamingLoader.this.setNetworkByte(this.headerData, 16, pixelWidth, 2);
            int pixelHeight = getPixelHeight(pixelWidth);
            StreamingLoader.this.setNetworkByte(this.headerData, 18, pixelHeight, 2);
            return this.jpegDataSize + paddingSize;
        }

        private int getPixelHeight(int pixelWidth) {
            MediaPlayer mediaPlayer = MediaPlayerManager.getInstance().getMediaPlayer();
            if (mediaPlayer != null) {
                double videoWidth = mediaPlayer.getVideoWidth();
                double videoHeight = mediaPlayer.getVideoHeight();
                if (videoWidth >= pixelWidth) {
                    double compressionWidth = videoWidth / pixelWidth;
                    int pixelHeight = (int) (videoHeight / compressionWidth);
                    return pixelHeight;
                }
                double compressionWidth2 = pixelWidth / videoWidth;
                int pixelHeight2 = (int) (videoHeight * compressionWidth2);
                return pixelHeight2;
            }
            Log.e(this.TAG, "MediaPlayerManager.getInstance().getMediaPlayer() == null");
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PlayInfoLoader {
        static final int PAYLOAD_DATA_SIZE_MAX = 32;
        static final int PAYLOAD_HEADER_START_CODE_BYTE1 = 36;
        static final int PAYLOAD_HEADER_START_CODE_BYTE2 = 53;
        static final int PAYLOAD_HEADER_START_CODE_BYTE3 = 104;
        static final int PAYLOAD_HEADER_START_CODE_BYTE4 = 121;
        private final byte PLAYBACK_INFO_DATA_VERSION_MJ;
        private final byte PLAYBACK_INFO_DATA_VERSION_MN;
        private final byte PLAY_INFO_DATA_SIZE;
        private final String TAG;
        private int getCount;
        private long getCountStartTime;
        private byte[] headerData;
        private long lastGetPlayTime;
        private byte[] playData;
        private PlayInfoData playInfoData;
        private long totalSentDataSize;

        private PlayInfoLoader() {
            this.lastGetPlayTime = 0L;
            this.getCount = 0;
            this.getCountStartTime = -1L;
            this.totalSentDataSize = 0L;
            this.TAG = PlayInfoLoader.class.getName();
            this.PLAYBACK_INFO_DATA_VERSION_MJ = (byte) 1;
            this.PLAYBACK_INFO_DATA_VERSION_MN = (byte) 0;
            this.PLAY_INFO_DATA_SIZE = (byte) 32;
            StreamingLoader.this.mCAPA = StreamingLoader.this.getCAPA();
            this.playData = new byte[32];
            this.headerData = new byte[136];
            this.playInfoData = new PlayInfoData();
            this.playInfoData.headerData = this.headerData;
            this.playInfoData.headerDataSize = 136;
            this.playInfoData.infoData = this.playData;
            this.playInfoData.infoDataSize = 32;
            StreamingLoader.this.commonHeaderTimeStampBase = System.currentTimeMillis();
            boolean unused = StreamingLoader.isLoading = false;
        }

        public PlayInfoData getPlayInfoData() {
            if (StreamingLoader.isLoading) {
                makeHeaderAndPadding();
                getInfoData();
                this.totalSentDataSize += this.playInfoData.infoDataSize;
                return this.playInfoData;
            }
            Log.v(this.TAG, "PlayInfoLoader is not loading streaming data.");
            throw new IllegalStateException();
        }

        private int getInfoData() {
            int duration = MediaPlayerManager.getInstance().getDuration();
            int lapTime = MediaPlayerManager.getInstance().getLapTime();
            if (duration == -1) {
                duration = 0;
            }
            if (lapTime == -1) {
                lapTime = 0;
            }
            StreamingLoader.this.setNetworkByte(this.playData, 0, duration, 4);
            StreamingLoader.this.setNetworkByte(this.playData, 4, lapTime, 4);
            return 0;
        }

        private void makeHeaderAndPadding() {
            Arrays.fill(this.headerData, (byte) 0);
            this.headerData[0] = -1;
            this.headerData[1] = 18;
            StreamingLoader.this.setNetworkByte(this.headerData, 2, StreamingLoader.commonHeaderSequenceNumber, 2);
            int timeStamp = (int) (System.currentTimeMillis() - StreamingLoader.this.commonHeaderTimeStampBase);
            StreamingLoader.this.setNetworkByte(this.headerData, 4, timeStamp, 4);
            this.headerData[8] = LiveviewCommon.PAYLOAD_HEADER_START_CODE_BYTE1;
            this.headerData[9] = LiveviewCommon.PAYLOAD_HEADER_START_CODE_BYTE2;
            this.headerData[10] = LiveviewCommon.PAYLOAD_HEADER_START_CODE_BYTE3;
            this.headerData[11] = LiveviewCommon.PAYLOAD_HEADER_START_CODE_BYTE4;
            StreamingLoader.this.setNetworkByte(this.headerData, 12, 32, 3);
            this.headerData[15] = 0;
            this.headerData[16] = 1;
            this.headerData[17] = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getCAPA() {
        LiveviewContainer liveview = LiveviewContainer.getInstance();
        int CAPA = liveview.getMaxFileSize() * 1024;
        return CAPA;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setNetworkByte(byte[] dst, int dstIndex, int src, int srcSize) {
        int di = dstIndex + srcSize;
        int s = src;
        for (int i = 0; i < srcSize; i++) {
            di--;
            dst[di] = (byte) (s & 255);
            s >>>= 8;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void releaseDeviceMemories(DeviceMemory[] memories) {
        for (DeviceMemory dbuf : memories) {
            dbuf.release();
        }
    }
}
