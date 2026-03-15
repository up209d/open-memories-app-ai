package com.sony.imaging.app.srctrl.shooting.state;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DROAutoHDRController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.srctrl.shooting.SRCtrlExecutorCreator;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;
import com.sony.imaging.app.srctrl.util.SRCtrlNotificationManager;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationTouchAFPosition;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxyPostviewImageSize;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.definition.Name;
import com.sony.imaging.app.srctrl.webapi.servlet.ContinuousResourceLoader;
import com.sony.imaging.app.srctrl.webapi.servlet.SingleResourceLoader;
import com.sony.imaging.app.srctrl.webapi.specific.ShootingHandler;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.lib.ssdpdevice.SsdpDevice;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.PlainCalendar;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.TimeUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class CaptureStateUtilContShootingSupported extends CaptureStateUtil {
    static boolean jpeg_generation_finished;
    static int numOfBurstPictures;
    static int numOfFinishedPictures;
    static boolean useFinalPicture;
    private static final String TAG = CaptureStateUtilContShootingSupported.class.getSimpleName();
    private static CaptureInfo mCaptureInfo = null;
    private static final String[] tags = {SRCtrlNotificationManager.SHOOT_END};
    private Handler mHandlerToMain = null;
    private Object mOnCaptureStatusToken = new Object();
    private Object mStoreImageCompleteToken = new Object();
    private Object mLock = new Object();
    private boolean mLockedAutoFocusFlg = false;
    private int urlListMax = SsdpDevice.RETRY_INTERVAL;
    private CameraEx.OnCaptureStatusListener mOnCaptureStatusCallback = new CameraEx.OnCaptureStatusListener() { // from class: com.sony.imaging.app.srctrl.shooting.state.CaptureStateUtilContShootingSupported.2
        private boolean isNoCard_atOnStart = false;
        private MediaMountEventListener mMediaListener;
        private MediaNotificationManager mMediaNotifier;

        public void onStart(int sequenceId, CameraEx cameraEx) {
            Log.v(CaptureStateUtilContShootingSupported.TAG, "onStart:sequenceId=" + sequenceId);
            CaptureStateUtilContShootingSupported.mCaptureInfo.addCaptureStateCount(1);
            CaptureStateUtilContShootingSupported.mCaptureInfo.updateLatestSequenceID(sequenceId);
            this.isNoCard_atOnStart = MediaNotificationManager.getInstance().isNoCard();
            if (this.mMediaListener == null) {
                this.mMediaListener = new MediaMountEventListener();
                this.mMediaNotifier = MediaNotificationManager.getInstance();
                this.mMediaNotifier.setNotificationListener(this.mMediaListener);
            }
        }

        public void onEnd(int sequenceId, int reason, CameraEx cameraEx) {
            Log.v(CaptureStateUtilContShootingSupported.TAG, "onEnd:sequenceId=" + sequenceId);
            CaptureStateUtilContShootingSupported.mCaptureInfo.subCaptureStateCount(1);
            if (CaptureStateUtilContShootingSupported.mCaptureInfo.isCaptureEnd()) {
                this.mMediaNotifier.removeNotificationListener(this.mMediaListener);
                this.mMediaListener = null;
                CaptureStateUtilContShootingSupported.end();
            }
        }

        /* renamed from: com.sony.imaging.app.srctrl.shooting.state.CaptureStateUtilContShootingSupported$2$MediaMountEventListener */
        /* loaded from: classes.dex */
        class MediaMountEventListener implements NotificationListener {
            private final String[] tags = {MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};

            MediaMountEventListener() {
            }

            @Override // com.sony.imaging.app.util.NotificationListener
            public String[] getTags() {
                return this.tags;
            }

            @Override // com.sony.imaging.app.util.NotificationListener
            public void onNotify(String tag) {
                Log.d(CaptureStateUtilContShootingSupported.TAG, tag + " is received.");
                if (!AnonymousClass2.this.isNoCard_atOnStart && MediaNotificationManager.getInstance().isNoCard()) {
                    Log.w(CaptureStateUtilContShootingSupported.TAG, "Card substructed while capturing");
                    CaptureStateUtilContShootingSupported.this.updateCaptureInfoOnShutterNG();
                }
            }
        }
    };
    private CameraEx.StoreImageCompleteListener mStoreImageCompleteCallback = new CameraEx.StoreImageCompleteListener() { // from class: com.sony.imaging.app.srctrl.shooting.state.CaptureStateUtilContShootingSupported.4
        public void onDone(int sequenceId, CameraEx.StoreImageInfo info, CameraEx cameraEx) {
            Log.v(CaptureStateUtilContShootingSupported.TAG, "onDone:sequenceId=" + sequenceId + " currentID " + CaptureStateUtilContShootingSupported.mCaptureInfo.mValidSequenceID);
            if (CaptureStateUtilContShootingSupported.mCaptureInfo.mValidSequenceID == sequenceId) {
                if (CaptureStateUtilContShootingSupported.mCaptureInfo.mFilePathList.size() >= CaptureStateUtilContShootingSupported.this.urlListMax) {
                    Log.v(CaptureStateUtilContShootingSupported.TAG, "File path list Max!! " + CaptureStateUtilContShootingSupported.this.urlListMax);
                    CaptureStateUtilContShootingSupported.mCaptureInfo.mFilePathList.remove(0);
                }
                String filePath = info.DirectoryName + info.FileName + ".JPG";
                if (!CaptureStateUtil.isContinuousShooting()) {
                    if (CaptureStateUtilContShootingSupported.mCaptureInfo.mIsMediaMounted) {
                        String postviewDirectoryOnMemory = SRCtrlEnvironment.getInstance().getPostviewOnMemoryBaseURL();
                        String filePath2 = postviewDirectoryOnMemory + "/" + filePath;
                        if (CameraProxyPostviewImageSize.isSizeOriginal()) {
                            filePath = filePath2 + "?size=Origin";
                        } else {
                            filePath = filePath2 + "?size=Scn";
                        }
                        Log.v(CaptureStateUtilContShootingSupported.TAG, filePath);
                    }
                } else {
                    String currentSize = CameraProxyPostviewImageSize.get();
                    Log.v(CaptureStateUtilContShootingSupported.TAG, filePath + ExposureModeController.SOFT_SNAP + currentSize + " filenum = " + CaptureStateUtilContShootingSupported.mCaptureInfo.mFilePathList.size());
                }
                CaptureStateUtilContShootingSupported.mCaptureInfo.mFilePathList.add(filePath);
            }
        }
    };

    @Override // com.sony.imaging.app.srctrl.shooting.state.CaptureStateUtil
    public void init() {
        synchronized (this.mLock) {
            mCaptureInfo = new CaptureInfo();
            this.mHandlerToMain = new Handler(Looper.getMainLooper());
            this.mLockedAutoFocusFlg = false;
            setOnCaptureStatusListener();
            setStoreImageCompleteListener();
        }
    }

    @Override // com.sony.imaging.app.srctrl.shooting.state.CaptureStateUtil
    public void term() {
        synchronized (this.mLock) {
            removeStoreImageCompleteListener();
            removeOnCaptureStatusListener();
            ShootingExecutor.setJpegListener(null);
            this.mLockedAutoFocusFlg = false;
            this.mHandlerToMain = null;
            mCaptureInfo = null;
        }
    }

    @Override // com.sony.imaging.app.srctrl.shooting.state.CaptureStateUtil
    public void startCapture() {
        if (mCaptureInfo.mLatestSequenceID == mCaptureInfo.INVALID_SEQUENCE_ID) {
            mCaptureInfo.mIsMediaMounted = isExternalMediaMounted();
            mCaptureInfo.mIsSingle = isSingleShooting();
            mCaptureInfo.mIsBulbShooting = isBlibShootingMode();
            mCaptureInfo.mIsContinuous = isContinuousShooting();
        }
        outputLogCaptureInfo(Name.PREFIX_START);
        String mediaId = SRCtrlExecutorCreator.getRecordingMedia();
        if (mCaptureInfo.mIsContinuous) {
            ShootingExecutor.setJpegListener(null);
        } else {
            ShootingExecutor.setJpegListener(new JpegListenerEx(mediaId));
        }
        if (DROAutoHDRController.MENU_ITEM_ID_HDR.equals(DROAutoHDRController.getInstance().getValue(DROAutoHDRController.MENU_ITEM_ID_DROHDR))) {
            mCaptureInfo.addJpegListenerCount(2);
        } else {
            mCaptureInfo.addJpegListenerCount(1);
        }
        ShootingHandler.getInstance().setShootingStatus(ShootingHandler.ShootingStatus.PROCESSING);
        if (DROAutoHDRController.MENU_ITEM_ID_HDR.equals(DROAutoHDRController.getInstance().getValue(DROAutoHDRController.MENU_ITEM_ID_DROHDR))) {
            numOfBurstPictures++;
            useFinalPicture = true;
        } else {
            numOfFinishedPictures = 0;
            numOfBurstPictures = 1;
            useFinalPicture = false;
        }
        SingleResourceLoader.getInstance().terminate();
        SingleResourceLoader.getInstance().initialize();
        ContinuousResourceLoader.getInstance().terminate();
        ContinuousResourceLoader.getInstance().initialize();
    }

    /* loaded from: classes.dex */
    private static final class JpegListenerEx implements CameraEx.JpegListener {
        private String mediaId;

        public JpegListenerEx(String id) {
            this.mediaId = id;
        }

        public void onPictureTaken(byte[] jpeg, CameraEx cam) {
            Log.v(CaptureStateUtilContShootingSupported.TAG, "onPictureTaken in JpegListenerEx");
            if (!CaptureStateUtilContShootingSupported.mCaptureInfo.mIsContinuous) {
                if (!CaptureStateUtilContShootingSupported.mCaptureInfo.mIsMediaMounted) {
                    if (CaptureStateUtilContShootingSupported.useFinalPicture) {
                        if (CaptureStateUtilContShootingSupported.numOfFinishedPictures + 1 == CaptureStateUtilContShootingSupported.numOfBurstPictures) {
                            addToServer(jpeg);
                        }
                    } else {
                        addToServer(jpeg);
                    }
                }
                CaptureStateUtilContShootingSupported.numOfFinishedPictures++;
                Log.v(CaptureStateUtilContShootingSupported.TAG, "Finished: " + CaptureStateUtilContShootingSupported.numOfFinishedPictures + ", Full: " + CaptureStateUtilContShootingSupported.numOfBurstPictures);
            }
            CaptureStateUtilContShootingSupported.mCaptureInfo.subJpegListenerCount(1);
            if (CaptureStateUtilContShootingSupported.mCaptureInfo.isCaptureEnd()) {
                CaptureStateUtilContShootingSupported.end();
            }
        }

        private void addToServer(byte[] jpeg) {
            ShootingHandler.ShootingStatus currentShootingState = ShootingHandler.getInstance().getShootingStatus();
            if (currentShootingState != ShootingHandler.ShootingStatus.FINISHED) {
                PlainCalendar cal = TimeUtil.getCurrentCalendar();
                String filename_base = SRCtrlConstants.POSTVIEW_FILENAME_PREFIX + String.format("%04d", Integer.valueOf(cal.year)) + String.format("%02d", Integer.valueOf(cal.month)) + String.format("%02d", Integer.valueOf(cal.day)) + SRCtrlConstants.URI_CONTENT_ID_SEPARATOR + String.format("%02d", Integer.valueOf(cal.hour)) + String.format("%02d", Integer.valueOf(cal.minute)) + String.format("%02d", Integer.valueOf(cal.second)) + SRCtrlConstants.URI_CONTENT_ID_SEPARATOR;
                String url = filename_base + CaptureStateUtilContShootingSupported.numOfFinishedPictures + ".JPG";
                CaptureStateUtilContShootingSupported.mCaptureInfo.mFilePathList.add(SRCtrlEnvironment.getInstance().getPostviewBaseURL() + url);
                SingleResourceLoader.addPicture(jpeg, url);
                Log.v(CaptureStateUtilContShootingSupported.TAG, url);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void end() {
        outputLogCaptureInfo("end");
        ShootingHandler.ShootingStatus currentShootingState = ShootingHandler.getInstance().getShootingStatus();
        if (currentShootingState != ShootingHandler.ShootingStatus.FINISHED) {
            if (!mCaptureInfo.mIsContinuous) {
                ShootingHandler.getInstance().clearUrlList();
                int size = mCaptureInfo.mFilePathList.size();
                if (size > 0) {
                    ShootingHandler.getInstance().addToUrlList((String) mCaptureInfo.mFilePathList.get(size - 1));
                }
            } else {
                ParamsGenerator.updateContShootingParams(mCaptureInfo.mFilePathList);
            }
            if (!mCaptureInfo.mIsSingle) {
                ExecutorCreator.getInstance().getSequence().cancelTakePicture();
                if (isCallCancelAutoFocus()) {
                    ExecutorCreator.getInstance().getSequence().cancelAutoFocus();
                }
            } else if (mCaptureInfo.mIsBulbShooting) {
                ShootingHandler.getInstance().notifyBulbPictureUrl();
            } else if (!remote_shooting_mode) {
                ShootingHandler.getInstance().notifyPictureUrl();
            }
            ShootingHandler.getInstance().setShootingStatus(ShootingHandler.ShootingStatus.FINISHED);
            SRCtrlNotificationManager.getInstance().requestNotify(SRCtrlNotificationManager.SHOOT_END);
        }
        mCaptureInfo.clear();
    }

    private static boolean isCallCancelAutoFocus() {
        boolean isCancel;
        KeyStatus s1OnStatus = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S1_ON);
        if (1 == s1OnStatus.status) {
            isCancel = false;
        } else if (CameraOperationTouchAFPosition.isSet()) {
            isCancel = false;
        } else {
            isCancel = true;
        }
        Log.v(TAG, "isCallCancelAutoFocus isCancel=" + isCancel);
        return isCancel;
    }

    public void updateLockAutoFocusFlg(boolean lock) {
        this.mLockedAutoFocusFlg = lock;
        Log.v(TAG, "mLockedAutoFocusFlg " + lock);
    }

    public boolean getLockAutoFocusFlg() {
        if (mCaptureInfo.mIsContinuous) {
            Log.v(TAG, "getLockAutoFocusFlg  mLockedAutoFocusFlg=" + this.mLockedAutoFocusFlg);
            return this.mLockedAutoFocusFlg;
        }
        Log.v(TAG, "getLockAutoFocusFlg  return false");
        return false;
    }

    private void setOnCaptureStatusListener() {
        CameraEx.OnCaptureStatusListener listener = new CameraEx.OnCaptureStatusListener() { // from class: com.sony.imaging.app.srctrl.shooting.state.CaptureStateUtilContShootingSupported.1
            public void onStart(int sequenceId, CameraEx cameraEx) {
                synchronized (CaptureStateUtilContShootingSupported.this.mLock) {
                    Log.v(CaptureStateUtilContShootingSupported.TAG, "onStart(Listener): sequenceId=" + sequenceId);
                    if (CaptureStateUtilContShootingSupported.this.mHandlerToMain != null) {
                        CaptureStateUtilContShootingSupported.this.mHandlerToMain.postAtTime(new OnCaptureStatusStartRunnable(sequenceId, cameraEx), CaptureStateUtilContShootingSupported.this.mOnCaptureStatusToken, SystemClock.uptimeMillis());
                    } else {
                        Log.w(CaptureStateUtilContShootingSupported.TAG, "onStart(Listener) is called after termination");
                    }
                }
            }

            public void onEnd(int sequenceId, int reason, CameraEx cameraEx) {
                synchronized (CaptureStateUtilContShootingSupported.this.mLock) {
                    Log.v(CaptureStateUtilContShootingSupported.TAG, "onEnd(Listener): sequenceId=" + sequenceId);
                    if (CaptureStateUtilContShootingSupported.this.mHandlerToMain != null) {
                        CaptureStateUtilContShootingSupported.this.mHandlerToMain.postAtTime(new OnCaptureStatusEndRunnable(sequenceId, reason, cameraEx), CaptureStateUtilContShootingSupported.this.mOnCaptureStatusToken, SystemClock.uptimeMillis());
                    } else {
                        Log.w(CaptureStateUtilContShootingSupported.TAG, "onEnd(Listener) is called after termination");
                    }
                }
            }
        };
        CameraEx cameraEx = CameraSetting.getInstance().getCamera();
        cameraEx.setCaptureStatusListener(listener);
    }

    private void removeOnCaptureStatusListener() {
        CameraEx cameraEx = CameraSetting.getInstance().getCamera();
        cameraEx.setCaptureStatusListener((CameraEx.OnCaptureStatusListener) null);
        this.mHandlerToMain.removeCallbacksAndMessages(this.mOnCaptureStatusToken);
    }

    /* loaded from: classes.dex */
    private class OnCaptureStatusStartRunnable implements Runnable {
        private CameraEx mCameraEx;
        private int mSequenceId;

        public OnCaptureStatusStartRunnable(int sequenceId, CameraEx cameraEx) {
            this.mSequenceId = sequenceId;
            this.mCameraEx = cameraEx;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (CaptureStateUtilContShootingSupported.this.mOnCaptureStatusCallback != null) {
                CaptureStateUtilContShootingSupported.this.mOnCaptureStatusCallback.onStart(this.mSequenceId, this.mCameraEx);
            }
        }
    }

    /* loaded from: classes.dex */
    private class OnCaptureStatusEndRunnable implements Runnable {
        private CameraEx mCameraEx;
        private int mReason;
        private int mSequenceId;

        public OnCaptureStatusEndRunnable(int sequenceId, int reason, CameraEx cameraEx) {
            this.mSequenceId = sequenceId;
            this.mReason = reason;
            this.mCameraEx = cameraEx;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (CaptureStateUtilContShootingSupported.this.mOnCaptureStatusCallback != null) {
                CaptureStateUtilContShootingSupported.this.mOnCaptureStatusCallback.onEnd(this.mSequenceId, this.mReason, this.mCameraEx);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class SRCtrlShootEndListener implements NotificationListener {
        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return CaptureStateUtilContShootingSupported.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class CaptureInfo {
        private int INVALID_SEQUENCE_ID = -1;
        private int mLatestSequenceID = this.INVALID_SEQUENCE_ID;
        private int mValidSequenceID = this.INVALID_SEQUENCE_ID;
        private int mCaptureStateCount = 0;
        private int mJpegListenerCount = 0;
        private boolean mIsMediaMounted = false;
        private boolean mIsSingle = false;
        private boolean mIsBulbShooting = false;
        private boolean mIsContinuous = false;
        private List<String> mFilePathList = new ArrayList();

        public CaptureInfo() {
        }

        public void clear() {
            this.mLatestSequenceID = this.INVALID_SEQUENCE_ID;
            this.mValidSequenceID = this.INVALID_SEQUENCE_ID;
            this.mFilePathList.clear();
        }

        public void addCaptureStateCount(int val) {
            this.mCaptureStateCount += val;
            Log.v(CaptureStateUtilContShootingSupported.TAG, "mCaptureStateCount " + (this.mCaptureStateCount - val) + " -> " + this.mCaptureStateCount);
        }

        public void subCaptureStateCount(int val) {
            this.mCaptureStateCount -= val;
            if (this.mCaptureStateCount < 0) {
                Log.e(CaptureStateUtilContShootingSupported.TAG, "mCaptureStateCount(Invalid counter value!!) " + (this.mCaptureStateCount + val) + " -> " + this.mCaptureStateCount);
                this.mCaptureStateCount = 0;
            } else {
                Log.v(CaptureStateUtilContShootingSupported.TAG, "mCaptureStateCount " + (this.mCaptureStateCount + val) + " -> " + this.mCaptureStateCount);
            }
        }

        public void addJpegListenerCount(int val) {
            if (!this.mIsContinuous) {
                this.mJpegListenerCount += val;
                Log.v(CaptureStateUtilContShootingSupported.TAG, "mJpegListenerCount " + (this.mJpegListenerCount - val) + " -> " + this.mJpegListenerCount);
            }
        }

        public void subJpegListenerCount(int val) {
            if (!this.mIsContinuous) {
                this.mJpegListenerCount -= val;
                if (this.mJpegListenerCount < 0) {
                    Log.e(CaptureStateUtilContShootingSupported.TAG, "mJpegListenerCount(Invalid counter value!!) " + (this.mJpegListenerCount + val) + " -> " + this.mJpegListenerCount);
                    this.mJpegListenerCount = 0;
                } else {
                    Log.v(CaptureStateUtilContShootingSupported.TAG, "mJpegListenerCount " + (this.mJpegListenerCount + val) + " -> " + this.mJpegListenerCount);
                }
            }
        }

        public void updateLatestSequenceID(int sequenceID) {
            Log.v(CaptureStateUtilContShootingSupported.TAG, "updateLatestsequenceId=" + this.mLatestSequenceID + " to " + sequenceID);
            this.mLatestSequenceID = sequenceID;
        }

        public void updateValidSequenceID() {
            Log.v(CaptureStateUtilContShootingSupported.TAG, "updateValidSequenceID=" + this.mValidSequenceID + " to " + this.mLatestSequenceID);
            this.mValidSequenceID = this.mLatestSequenceID;
        }

        public boolean isCaptureEnd() {
            Log.v(CaptureStateUtilContShootingSupported.TAG, "isCaptureEnd: mCaptureStateCount=" + this.mCaptureStateCount + " mJpegListenerCount=" + this.mJpegListenerCount);
            if (this.mCaptureStateCount > 0 || this.mJpegListenerCount > 0) {
                return false;
            }
            return true;
        }
    }

    public void updateCaptureInfoOnShutterOK() {
        Log.v(TAG, "updateCaptureInfoOnShutterOK");
        if (!CameraOperationTouchAFPosition.isSet() && getCaptureInfoIsSingle() && isCallCancelAutoFocus()) {
            Log.v(TAG, "cancelAutoFocus!!!!!");
            BaseShootingExecutor shootingExecutor = ExecutorCreator.getInstance().getSequence();
            shootingExecutor.cancelAutoFocus();
        }
        mCaptureInfo.updateValidSequenceID();
    }

    public void updateCaptureInfoOnShutterNG() {
        Log.v(TAG, "updateCaptureInfoOnShutterNG");
        ExecutorCreator.getInstance().getSequence().cancelTakePicture();
        updateLockAutoFocusFlg(false);
        if (isCallCancelAutoFocus()) {
            ExecutorCreator.getInstance().getSequence().cancelAutoFocus();
        }
        if (DROAutoHDRController.MENU_ITEM_ID_HDR.equals(DROAutoHDRController.getInstance().getValue(DROAutoHDRController.MENU_ITEM_ID_DROHDR))) {
            mCaptureInfo.subJpegListenerCount(2);
        } else {
            mCaptureInfo.subJpegListenerCount(1);
        }
        ShootingHandler.getInstance().setShootingStatus(ShootingHandler.ShootingStatus.FAILED);
    }

    public boolean getCaptureInfoIsSingle() {
        return mCaptureInfo.mIsSingle;
    }

    public boolean getCaptureInfoIsContinuous() {
        return mCaptureInfo.mIsContinuous;
    }

    private static void outputLogCaptureInfo(String func) {
        Log.v(TAG, func + ExposureModeController.SOFT_SNAP + " CaptureInfo: LatestID=" + mCaptureInfo.mLatestSequenceID + " CurrentID=" + mCaptureInfo.mValidSequenceID + " MediaMount=" + mCaptureInfo.mIsMediaMounted + " Single=" + mCaptureInfo.mIsSingle + " Cont=" + mCaptureInfo.mIsContinuous);
    }

    private void setStoreImageCompleteListener() {
        CameraEx.StoreImageCompleteListener listener = new CameraEx.StoreImageCompleteListener() { // from class: com.sony.imaging.app.srctrl.shooting.state.CaptureStateUtilContShootingSupported.3
            public void onDone(int sequenceId, CameraEx.StoreImageInfo info, CameraEx cameraEx) {
                synchronized (CaptureStateUtilContShootingSupported.this.mLock) {
                    Log.v(CaptureStateUtilContShootingSupported.TAG, "onDone(Listener): sequenceId=" + sequenceId);
                    if (CaptureStateUtilContShootingSupported.this.mHandlerToMain != null) {
                        CaptureStateUtilContShootingSupported.this.mHandlerToMain.postAtTime(new StoreImageCompleteRunnable(sequenceId, info, cameraEx), CaptureStateUtilContShootingSupported.this.mStoreImageCompleteToken, SystemClock.uptimeMillis());
                    } else {
                        Log.w(CaptureStateUtilContShootingSupported.TAG, "onDone(Listener) is called after termination");
                    }
                }
            }
        };
        CameraEx cameraEx = CameraSetting.getInstance().getCamera();
        cameraEx.setStoreImageCompleteListener(listener);
    }

    private void removeStoreImageCompleteListener() {
        CameraEx cameraEx = CameraSetting.getInstance().getCamera();
        cameraEx.setStoreImageCompleteListener((CameraEx.StoreImageCompleteListener) null);
        this.mHandlerToMain.removeCallbacksAndMessages(this.mStoreImageCompleteToken);
    }

    /* loaded from: classes.dex */
    private class StoreImageCompleteRunnable implements Runnable {
        private CameraEx mCameraEx;
        private CameraEx.StoreImageInfo mInfo;
        private int mSequenceId;

        public StoreImageCompleteRunnable(int sequenceId, CameraEx.StoreImageInfo info, CameraEx cameraEx) {
            this.mSequenceId = sequenceId;
            this.mInfo = info;
            this.mCameraEx = cameraEx;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (CaptureStateUtilContShootingSupported.this.mStoreImageCompleteCallback != null) {
                CaptureStateUtilContShootingSupported.this.mStoreImageCompleteCallback.onDone(this.mSequenceId, this.mInfo, this.mCameraEx);
            }
        }
    }
}
