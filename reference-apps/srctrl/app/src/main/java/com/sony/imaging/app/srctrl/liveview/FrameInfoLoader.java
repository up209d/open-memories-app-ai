package com.sony.imaging.app.srctrl.liveview;

import android.util.Log;
import android.util.SparseArray;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.srctrl.liveview.AFFrameConverter;
import com.sony.imaging.app.srctrl.shooting.state.MfAssistStateEx;
import com.sony.imaging.app.srctrl.shooting.state.S1OnEEStateForTouchAFAssist;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.PfBugAvailability;
import com.sony.scalar.hardware.CameraSequence;
import com.sony.scalar.lib.ssdpdevice.SsdpDevice;
import java.lang.ref.WeakReference;
import java.util.Arrays;

/* loaded from: classes.dex */
public class FrameInfoLoader {
    private static final boolean Debug = false;
    long commonHeaderTimeStampBase;
    private byte faceAspect;
    private static final String TAG = FrameInfoLoader.class.getName();
    private static volatile boolean isEnable = false;
    private static String notifyTag = null;
    private static SparseArray<AFFrameConverter.AFFrameInfo> afFrameInfo = null;
    private static Object lock = new Object();
    private static FrameInfoData emptyFrameInfoData = null;
    private static WeakReference<NotificationListener> s_CameraNotificationListenerRef = new WeakReference<>(null);
    private static WeakReference<NotificationListener> s_DisplayModeNotificationListenerRef = new WeakReference<>(null);
    private long lastGetFrameTime = 0;
    private long getCountStartTime = -1;
    private int totalFaceNum = 0;
    private int totalFocusNum = 0;
    private int totalTrackingNum = 0;
    private final int FACE_DATA_NUM_MAX = 8;
    private final int FACE_DATA_SIZE = 20;
    private final int FACE_DATA_ROOT = 220;
    private final int YC_FACE_MODE_ON = 1;
    private final int YC_FACE_REGISTERED_NORMAL = 0;
    private final int YC_FACE_REGISTERED_TRACKING = 1;
    private final int YC_FACE_REGISTERED_RECORDED = 2;
    private final int YC_TRACKING_DATA_VALID = 1;
    private final int YC_ASPECT_4_3 = 0;
    private final int YC_ASPECT_3_2 = 1;
    private final int YC_ASPECT_16_9 = 2;
    private final int YC_ASPECT_1_1 = 3;
    private final int YC_ASPECT_OTHER = 4;
    private final int YC_BOOL_FALSE = 0;
    private final int YC_BOOL_TRUE = 1;
    private Thread mThread = null;
    private CameraSequence.PreviewSequenceFrameInfo mFinfo = null;
    private Object lock_getFrameInfo_avoiding_HSLPO = new Object();
    private CameraSequence mCamseq = null;
    private FrameInfoData frameInfoData = new FrameInfoData();

    /* loaded from: classes.dex */
    public static class FrameInfoData {
        public byte[] frameData;
        public int frameDataSize;
        public byte[] headerData;
        public int headerDataSize;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class FaceData {
        private byte generation;
        private int height;
        private byte isDisplay;
        private byte isLocked;
        private byte isPreferred;
        private byte isRegistered;
        private byte isSmile;
        private byte isSmileTarget;
        private byte node;
        private int posX;
        private int posY;
        private int score;
        private int width;

        public FaceData(CameraSequence.PreviewSequenceFrameInfo finfo, int root) {
            this.posX = ((finfo.detailData[root + 1] << 8) & 65280) + (finfo.detailData[root + 0] & LiveviewCommon.COMMON_HEADER_START_BYTE);
            this.posY = ((finfo.detailData[root + 3] << 8) & 65280) + (finfo.detailData[root + 2] & LiveviewCommon.COMMON_HEADER_START_BYTE);
            this.width = ((finfo.detailData[root + 5] << 8) & 65280) + (finfo.detailData[root + 4] & LiveviewCommon.COMMON_HEADER_START_BYTE);
            this.height = ((finfo.detailData[root + 7] << 8) & 65280) + (finfo.detailData[root + 6] & LiveviewCommon.COMMON_HEADER_START_BYTE);
            this.score = ((finfo.detailData[root + 11] << 32) & (-16777216)) + ((finfo.detailData[root + 10] << LiveviewCommon.SINGLE_FRAME_INFO_DATA_SIZE) & 16711680) + ((finfo.detailData[root + 9] << 8) & 65280) + (finfo.detailData[root + 8] & LiveviewCommon.COMMON_HEADER_START_BYTE);
            this.node = finfo.detailData[root + 12];
            this.generation = finfo.detailData[root + 13];
            this.isSmile = finfo.detailData[root + 14];
            this.isPreferred = finfo.detailData[root + 15];
            this.isRegistered = finfo.detailData[root + 16];
            this.isLocked = finfo.detailData[root + 17];
            this.isSmileTarget = finfo.detailData[root + 18];
            this.isDisplay = finfo.detailData[root + 19];
        }
    }

    public FrameInfoLoader() {
        this.commonHeaderTimeStampBase = 0L;
        this.frameInfoData.headerData = new byte[LiveviewCommon.HEADER_DATA_SIZE_MAX];
        this.frameInfoData.headerDataSize = LiveviewCommon.HEADER_DATA_SIZE_MAX;
        this.frameInfoData.frameData = new byte[624];
        this.commonHeaderTimeStampBase = System.currentTimeMillis();
    }

    public FrameInfoData getEmptyFrameInfo() {
        if (emptyFrameInfoData == null) {
            emptyFrameInfoData = new FrameInfoData();
            emptyFrameInfoData.headerData = new byte[LiveviewCommon.HEADER_DATA_SIZE_MAX];
            emptyFrameInfoData.headerDataSize = LiveviewCommon.HEADER_DATA_SIZE_MAX;
            emptyFrameInfoData.frameData = new byte[1];
            emptyFrameInfoData.frameDataSize = 0;
            makeHeaderAndPadding(System.currentTimeMillis(), 0, emptyFrameInfoData);
        }
        return emptyFrameInfoData;
    }

    public static void setEnabled(boolean enable) {
        synchronized (lock) {
            if (enable) {
                AFFrameConverter afc = AFFrameConverter.getInstance();
                String strShowIlluminator = afc.isShowIlluminator();
                if (!AFFrameConverter.SHOW_ILLUMINATOR_INVALID.equals(strShowIlluminator)) {
                    afc.updateWithCurrentStatus(Boolean.TRUE.toString().equals(strShowIlluminator));
                } else {
                    afc.updateWithCurrentStatus(Boolean.TRUE.toString().equals(false));
                }
                afFrameInfo = afc.getList(notifyTag);
            }
            isEnable = enable;
        }
    }

    public static boolean isEnabled() {
        return isEnable;
    }

    /* loaded from: classes.dex */
    public static class AfFrameInfoNotificationListener implements NotificationListener {
        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            synchronized (FrameInfoLoader.lock) {
                String unused = FrameInfoLoader.notifyTag = tag;
                Log.d(FrameInfoLoader.TAG, "notifyTag = " + FrameInfoLoader.notifyTag);
                if (FrameInfoLoader.isEnable) {
                    SparseArray unused2 = FrameInfoLoader.afFrameInfo = AFFrameConverter.getInstance().getList(tag);
                } else {
                    AFFrameConverter.getInstance().updateFlags(tag);
                }
            }
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return null;
        }
    }

    public static NotificationListener getCameraNotificationListener() {
        NotificationListener notificationListener = s_CameraNotificationListenerRef.get();
        if (notificationListener == null) {
            NotificationListener notificationListener2 = new AfFrameInfoNotificationListener() { // from class: com.sony.imaging.app.srctrl.liveview.FrameInfoLoader.1
                @Override // com.sony.imaging.app.srctrl.liveview.FrameInfoLoader.AfFrameInfoNotificationListener, com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{CameraNotificationManager.START_AUTO_FOCUS, CameraNotificationManager.DONE_AUTO_FOCUS, CameraNotificationManager.AUTO_FOCUS_AREA, CameraNotificationManager.FOCUS_AREA_INFO, CameraNotificationManager.FOCUS_CHANGE, CameraNotificationManager.ZOOM_INFO_CHANGED, CameraNotificationManager.FOCUS_POINT, CameraNotificationManager.FACE_DETECTION_MODE, CameraNotificationManager.DEVICE_LENS_CHANGED};
                }
            };
            s_CameraNotificationListenerRef = new WeakReference<>(notificationListener2);
            return notificationListener2;
        }
        return notificationListener;
    }

    public static NotificationListener getDisplayModeNotificationListener() {
        NotificationListener notificationListener = s_DisplayModeNotificationListenerRef.get();
        if (notificationListener == null) {
            NotificationListener notificationListener2 = new AfFrameInfoNotificationListener() { // from class: com.sony.imaging.app.srctrl.liveview.FrameInfoLoader.2
                @Override // com.sony.imaging.app.srctrl.liveview.FrameInfoLoader.AfFrameInfoNotificationListener, com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{DisplayModeObserver.TAG_YUVLAYOUT_CHANGE, DisplayModeObserver.TAG_DEVICE_CHANGE};
                }
            };
            s_DisplayModeNotificationListenerRef = new WeakReference<>(notificationListener2);
            return notificationListener2;
        }
        return notificationListener;
    }

    public FrameInfoData getFrameInfoData(CameraSequence camseq) throws IllegalStateException {
        if (StateController.getInstance().getServerStatus() == StateController.ServerStatus.MOVIE_RECORDING) {
            try {
                Thread.sleep(170L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        State state = StateController.getInstance().getState();
        if (state != null && ((state instanceof MfAssistStateEx) || (state instanceof S1OnEEStateForTouchAFAssist))) {
            return getEmptyFrameInfo();
        }
        if ((StateController.getInstance().getServerStatus() != StateController.ServerStatus.IDLE && StateController.getInstance().getServerStatus() != StateController.ServerStatus.MOVIE_RECORDING) || CameraSetting.getInstance().getCurrentMode() == 32768) {
            return null;
        }
        long currentTime = getCurrentTimeAndCheckInterval();
        if (currentTime < 0) {
            return null;
        }
        this.lastGetFrameTime = currentTime;
        int frameCount = getFrameData(camseq);
        this.frameInfoData.frameDataSize = frameCount * 16;
        makeHeaderAndPadding(currentTime, frameCount, this.frameInfoData);
        printInfo(currentTime);
        return this.frameInfoData;
    }

    private long getCurrentTimeAndCheckInterval() {
        long currentTime = System.currentTimeMillis();
        long interval = (currentTime - this.lastGetFrameTime) + 5;
        if (interval < 30) {
            return -1L;
        }
        return currentTime;
    }

    private void printInfo(long currentTime) {
        if (this.getCountStartTime < 0) {
            this.getCountStartTime = currentTime;
        }
        long period = currentTime - this.getCountStartTime;
        if (period > 5000) {
            Log.v(TAG, "TotalFace=" + this.totalFaceNum + ", TotalFocus=" + this.totalFocusNum + ", TotalTracking=" + this.totalTrackingNum);
            this.totalFaceNum = 0;
            this.totalFocusNum = 0;
            this.totalTrackingNum = 0;
            this.getCountStartTime = currentTime;
        }
    }

    private int getFrameData(CameraSequence camseq) {
        if (!isEnable) {
            return 0;
        }
        int frameCount = 0;
        if (!PfBugAvailability.highSpeedLensPowerOff) {
            try {
                CameraSequence.PreviewSequenceFrameInfo finfo = camseq.getPreviewSequenceFrameInfo();
                frameCount = 0 + getFaceFrameData(finfo);
            } catch (RuntimeException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return frameCount + getFocusFrameData(frameCount * 16);
    }

    private int getFaceFrameData(CameraSequence.PreviewSequenceFrameInfo finfo) {
        int frameCount = 0;
        byte faceMode = finfo.detailData[428];
        if (1 == faceMode) {
            this.faceAspect = finfo.detailData[433];
            int faceNum = finfo.detailData[429];
            for (int cnt = 0; cnt < faceNum; cnt++) {
                FaceData faceData = new FaceData(finfo, (cnt * 20) + 220);
                if (1 == faceData.isDisplay) {
                    boolean ret = convertFaceFrameData(faceData, frameCount * 16, this.frameInfoData);
                    if (ret) {
                        frameCount++;
                    }
                }
            }
        }
        this.totalFaceNum += frameCount;
        return frameCount;
    }

    private boolean convertFaceFrameData(FaceData faceData, int root, FrameInfoData frameInfo) {
        int srcCoordinateX;
        int srcCoordinateY;
        byte[] frameData = frameInfo.frameData;
        switch (this.faceAspect) {
            case 0:
                srcCoordinateX = AppRoot.USER_KEYCODE.WATER_HOUSING;
                srcCoordinateY = 480;
                break;
            case 1:
                srcCoordinateX = AppRoot.USER_KEYCODE.WATER_HOUSING;
                srcCoordinateY = 427;
                break;
            case 2:
                srcCoordinateX = AppRoot.USER_KEYCODE.WATER_HOUSING;
                srcCoordinateY = 360;
                break;
            case 3:
                srcCoordinateX = 480;
                srcCoordinateY = 480;
                break;
            default:
                Log.e(TAG, "faceAspect is invalid");
                return false;
        }
        int posTopLeftX = convertCoordinate(faceData.posX, srcCoordinateX, 10000);
        LiveviewCommon.setNetworkByte(frameData, root + 0, posTopLeftX, 2);
        int posTopLeftY = convertCoordinate(faceData.posY, srcCoordinateY, 10000);
        LiveviewCommon.setNetworkByte(frameData, root + 2, posTopLeftY, 2);
        int posBottomRightX = convertCoordinate(faceData.posX + faceData.width, srcCoordinateX, 10000);
        LiveviewCommon.setNetworkByte(frameData, root + 4, posBottomRightX, 2);
        int posBottomRightY = convertCoordinate(faceData.posY + faceData.height, srcCoordinateY, 10000);
        LiveviewCommon.setNetworkByte(frameData, root + 6, posBottomRightY, 2);
        if (1 == faceData.isRegistered) {
            frameData[root + 8] = 5;
        } else {
            frameData[root + 8] = 4;
        }
        if (1 == faceData.isLocked) {
            frameData[root + 9] = 4;
        } else if (1 == faceData.isSmileTarget) {
            frameData[root + 9] = 5;
        } else if (2 == faceData.isRegistered) {
            frameData[root + 9] = 6;
        } else if (1 == faceData.isPreferred) {
            frameData[root + 9] = 2;
        } else {
            frameData[root + 9] = 3;
        }
        frameData[root + 10] = 0;
        return true;
    }

    private int getFocusFrameData(int root) {
        int frameCount = 0;
        SparseArray<AFFrameConverter.AFFrameInfo> afFrameInfoList = new SparseArray<>();
        synchronized (lock) {
            if (afFrameInfo != null) {
                for (int i = 0; i < afFrameInfo.size(); i++) {
                    afFrameInfoList.put(i, afFrameInfo.get(i).m0clone());
                }
            }
        }
        if (afFrameInfoList.size() > 0) {
            for (int i2 = 0; i2 < afFrameInfoList.size(); i2++) {
                if (afFrameInfoList.get(i2).isEnable()) {
                    boolean ret = convertFocusFrameData(afFrameInfoList.get(i2), (frameCount * 16) + root, this.frameInfoData);
                    if (ret) {
                        frameCount++;
                    }
                }
            }
        }
        this.totalFocusNum += frameCount;
        return frameCount;
    }

    private boolean convertFocusFrameData(AFFrameConverter.AFFrameInfo afFrameInfo2, int root, FrameInfoData frameInfo) {
        byte[] frameData = frameInfo.frameData;
        int posTopLeftX = convertCoordinate(afFrameInfo2.getFocusRect().left);
        LiveviewCommon.setNetworkByte(frameData, root + 0, posTopLeftX, 2);
        int posTopLeftY = convertCoordinate(afFrameInfo2.getFocusRect().top);
        LiveviewCommon.setNetworkByte(frameData, root + 2, posTopLeftY, 2);
        int posBottomRightX = convertCoordinate(afFrameInfo2.getFocusRect().right);
        LiveviewCommon.setNetworkByte(frameData, root + 4, posBottomRightX, 2);
        int posBottomRightY = convertCoordinate(afFrameInfo2.getFocusRect().bottom);
        LiveviewCommon.setNetworkByte(frameData, root + 6, posBottomRightY, 2);
        if (2 == afFrameInfo2.getSensorType()) {
            frameData[root + 8] = 1;
        } else if (1 == afFrameInfo2.getSensorType()) {
            frameData[root + 8] = 2;
        } else {
            Log.e(TAG, "sensorType" + afFrameInfo2.getSensorType() + "is invalid");
            return false;
        }
        if (1 == afFrameInfo2.getColor()) {
            frameData[root + 9] = 1;
        } else if (3 == afFrameInfo2.getColor()) {
            frameData[root + 9] = 4;
        } else if (6 == afFrameInfo2.getColor()) {
            frameData[root + 9] = 1;
        } else if (5 == afFrameInfo2.getColor()) {
            return false;
        }
        if (6 == afFrameInfo2.getColor()) {
            frameData[root + 10] = 1;
        } else if (FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME.equals(afFrameInfo2.getFocusAreaMode())) {
            frameData[root + 10] = 2;
        } else {
            frameData[root + 10] = 0;
        }
        return true;
    }

    private int convertCoordinate(int src, int srcCoordinate, int dstCoordinate) {
        double quotient = src / srcCoordinate;
        int dst = (int) (dstCoordinate * quotient);
        if (10000 < dst) {
            try {
                Log.e(TAG, "dst=" + dst);
                return dst;
            } catch (ArithmeticException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return dst;
    }

    private int convertCoordinate(int src) {
        int ret = ((src + SsdpDevice.RETRY_INTERVAL) * 100) / 20;
        return ret;
    }

    private void makeHeaderAndPadding(long currentTime, int frameCount, FrameInfoData frameInfo) {
        byte[] headerData = frameInfo.headerData;
        Arrays.fill(headerData, (byte) 0);
        headerData[0] = -1;
        headerData[1] = 2;
        LiveviewCommon.setNetworkByte(headerData, 2, LiveviewCommon.getCurrentSequenceNumber(), 2);
        int timeStamp = LiveviewCommon.getTimeStamp();
        LiveviewCommon.setNetworkByte(headerData, 4, timeStamp, 4);
        headerData[8] = LiveviewCommon.PAYLOAD_HEADER_START_CODE_BYTE1;
        headerData[9] = LiveviewCommon.PAYLOAD_HEADER_START_CODE_BYTE2;
        headerData[10] = LiveviewCommon.PAYLOAD_HEADER_START_CODE_BYTE3;
        headerData[11] = LiveviewCommon.PAYLOAD_HEADER_START_CODE_BYTE4;
        LiveviewCommon.setNetworkByte(headerData, 12, frameCount * 16, 3);
        headerData[15] = 0;
        headerData[16] = 1;
        headerData[17] = 0;
        LiveviewCommon.setNetworkByte(headerData, 18, frameCount, 2);
        LiveviewCommon.setNetworkByte(headerData, 20, 16, 2);
    }
}
