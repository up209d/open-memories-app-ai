package com.sony.imaging.app.soundphoto.shooting.state;

import android.annotation.SuppressLint;
import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.ShootingState;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.soundphoto.menu.layout.controller.ApplicationSettingsMenuController;
import com.sony.imaging.app.soundphoto.shooting.audiorecorder.AudioRecorder;
import com.sony.imaging.app.soundphoto.shooting.state.layout.SPAudioBufferAnimationController;
import com.sony.imaging.app.soundphoto.shooting.state.layout.SPShootingProcessingLayout;
import com.sony.imaging.app.soundphoto.state.ExitScreenProcessingLayout;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.media.AudioManager;
import com.sony.scalar.sysutil.ScalarProperties;

@SuppressLint({"SdCardPath"})
/* loaded from: classes.dex */
public class SPShootingState extends ShootingState {
    private static final String TAG = "SPShootingState";
    private static final int mMarginForAppDB = 1048576;
    private static final int mMarginForMPR = 4194304;
    private static final int mSoundMaxSize = 1920000;
    private NotificationListener mDisplayModeListener;
    MediaNotificationListener mMedianotification;
    private static SPShootingState mInstance = null;
    public static boolean mTakePictureEnable = false;
    private static String mFilePathBak = null;
    public static boolean isSyncRequest = false;
    private static final String[] DIAPLAY_MODE_TAGS = {DisplayModeObserver.TAG_4K_PLAYBACK_STATUS_CHANGE};
    private String mOutputBeep = "on";
    int mlastExpoureSeqId = -1;
    private int mPreSequenceCnt = 0;
    private int mLastSequenceID = -1;
    private int mLastSequenceIDng = -1;
    private int mTmpLastSequenceID = -1;
    private int mSequenceCnt = 0;
    private boolean mFirstSequence = false;
    private boolean mFirstSeqLock = false;
    private boolean init_status = false;
    private final CameraEx.ExposureCompleteListener mExposureCompleteListener = new CameraEx.ExposureCompleteListener() { // from class: com.sony.imaging.app.soundphoto.shooting.state.SPShootingState.1
        public void onDone(int sequenceId, CameraEx.ExposureInfo info, CameraEx cameraEx) {
            AppLog.info(SPShootingState.TAG, "ExposureCompleteListener.onDone() call onDone = " + sequenceId);
            if (SPShootingState.this.mlastExpoureSeqId != sequenceId) {
                AudioRecorder.getInstance().rec_single(sequenceId);
                int startRedDot = ApplicationSettingsMenuController.getInstance().getPreAudioTime() - SPAudioBufferAnimationController.getInstance().getCurrentAudioBufferTimeSec();
                SPAudioBufferAnimationController.getInstance().setStartRedDot(startRedDot);
                SPUtil.getInstance().notifyImageClicked();
                SPShootingState.this.mlastExpoureSeqId = sequenceId;
            }
        }
    };
    private final CameraEx.StoreImageCompleteListener mStoreImageCompleteListener = new CameraEx.StoreImageCompleteListener() { // from class: com.sony.imaging.app.soundphoto.shooting.state.SPShootingState.2
        public void onDone(int sequenceId, CameraEx.StoreImageInfo info, CameraEx cameraEx) {
            AppLog.info(SPShootingState.TAG, "StoreImageCompleteListener.onDone() call onDone = " + sequenceId);
            String mcolorParameter = ((CameraEx.ParametersModifier) CameraSetting.getInstance().getParameters().second).getColorSpace();
            if (mcolorParameter.equals("expanded") && !info.FileName.contains("_")) {
                String filaeName = info.FileName;
                info.FileName = "_" + filaeName.replaceFirst(ISOSensitivityController.ISO_AUTO, "");
            }
            String _filePath = "/mnt/sdcard/" + info.DirectoryName + info.FileName + ".JPG";
            AppLog.info(SPShootingState.TAG, "### StoreImageInfo = " + _filePath);
            SPUtil.getInstance().addToStoreImageInfoList(info);
            AudioRecorder.getInstance().storeImageComplete(sequenceId, info);
        }
    };
    private final CameraEx.OnCaptureStatusListener mCaptureStatusListener = new CameraEx.OnCaptureStatusListener() { // from class: com.sony.imaging.app.soundphoto.shooting.state.SPShootingState.3
        public void onStart(int sequenceId, CameraEx cameraEx) {
            AppLog.info(SPShootingState.TAG, "OnCaptureStatusListener.onStart() call sequenceId = " + sequenceId);
            if (SPShootingState.getInstance().getFirstSequenceStatus()) {
                SPShootingState.getInstance().setFirstSequenceLockState(true);
                SPShootingState.getInstance().setFirstSequenceStatus(false);
            }
            SPShootingState.getInstance().setLastSequenceID(sequenceId);
            SPShootingState.getInstance().startCameraSequenceCount();
        }

        public void onEnd(int sequenceId, int reason, CameraEx cameraEx) {
            AppLog.info(SPShootingState.TAG, "OnCaptureStatusListener.onEnd() call sequenceId = " + sequenceId + " reason = " + reason);
            SPShootingState.getInstance().setFirstSequenceLockState(false);
            SPShootingState.getInstance().clrLastSequenceID(sequenceId);
            SPShootingState.getInstance().endCameraSequenceCount();
            AudioRecorder.getInstance().rec_end(sequenceId);
        }
    };
    private final CameraEx.AdditionalDataSizeSetCallback mAdditionalDataSizeSetCallbackSet = new CameraEx.AdditionalDataSizeSetCallback() { // from class: com.sony.imaging.app.soundphoto.shooting.state.SPShootingState.4
        public void onSetComplete(CameraEx cameraEx) {
            Log.d(SPShootingState.TAG, "AdditionalDataSizeSetCallback.onSetComplete() #Set");
            SPShootingState.this.setTakePictureEnable(true);
        }
    };
    private final CameraEx.AdditionalDataSizeSetCallback mAdditionalDataSizeSetCallbackClr = new CameraEx.AdditionalDataSizeSetCallback() { // from class: com.sony.imaging.app.soundphoto.shooting.state.SPShootingState.5
        public void onSetComplete(CameraEx cameraEx) {
            Log.d(SPShootingState.TAG, "AdditionalDataSizeSetCallback.onSetComplete() #Clear");
            SPShootingState.this.setTakePictureEnable(false);
        }
    };

    public static SPShootingState getInstance() {
        AppLog.info(TAG, "getInstance");
        if (mInstance == null) {
            mInstance = new SPShootingState();
        }
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        openLayout(SPShootingProcessingLayout.TAG);
        DisplayModeObserver.getInstance().showDialogOn4kOutputConnected(isDialog4kOutputConnectedShown());
        if (this.mDisplayModeListener != null) {
            DisplayModeObserver.getInstance().removeNotificationListener(this.mDisplayModeListener);
        }
        this.mDisplayModeListener = getDisplayObserverListener();
        DisplayModeObserver.getInstance().setNotificationListener(this.mDisplayModeListener);
        AppLog.info(TAG, "onResume");
        AppLog.info(TAG, "SPShootingState :: onResume() entered...");
        SPAudioBufferAnimationController.getInstance().setAudiobufferStatus(0);
        SPAudioBufferAnimationController.getInstance().setmStartTime(SPUtil.getInstance().getCurrentTimeinSec());
        AppLog.info(TAG, "SPShootingState :: onResume() exit...");
        if (!AudioRecorder.getInstance().getRecStatus()) {
            AudioRecorder.getInstance().start();
        }
        if (this.mMedianotification == null) {
            this.mMedianotification = new MediaNotificationListener();
            MediaNotificationManager.getInstance().setNotificationListener(this.mMedianotification);
        }
        CameraEx cameraEx = CameraSetting.getInstance().getCamera();
        cameraEx.setCaptureStatusListener(this.mCaptureStatusListener);
        cameraEx.setExposureCompleteListener(this.mExposureCompleteListener);
        cameraEx.setStoreImageCompleteListener(this.mStoreImageCompleteListener);
        setTakePictureEnable(false);
        int ESBufferSize = AudioRecorder.getInstance().getEsBufferSize();
        int mediaMargin = mMarginForMPR + ESBufferSize + mMarginForAppDB;
        AppLog.info(TAG, "setAdditionalDataSize param1 = 1920000, 10M Pram2 = " + mediaMargin);
        cameraEx.setAdditionalDataSize(mSoundMaxSize, mediaMargin, true, this.mAdditionalDataSizeSetCallbackSet);
        this.mlastExpoureSeqId = -1;
        AudioManager mAudioManager = new AudioManager();
        AudioManager.Parameters getParams = mAudioManager.getParameters();
        mAudioManager.getSupportedParameters();
        this.mOutputBeep = getParams.getOutputBeep();
        AppLog.info(TAG, " set : off");
        getParams.setOutputBeep("off");
        mAudioManager.setParameters(getParams);
        AppLog.info(TAG, "BeepSetting is " + getParams.getOutputBeep());
        mAudioManager.release();
        int category = ScalarProperties.getInt("model.category");
        if (category == 2) {
            AppLog.info(TAG, "This Model is DSC. Set ShutterSound On ");
            AudioRecorder.getInstance().setShutterSettings(true);
        } else {
            AppLog.info(TAG, "This Model is not DSC. Set ShutterSound Off ");
            AudioRecorder.getInstance().setShutterSettings(false);
        }
    }

    public void waitCameraSequence() {
        AppLog.enter(TAG, AppLog.getMethodName() + ", mPreSequenceCnt = " + this.mPreSequenceCnt);
        try {
            synchronized (this) {
                while (true) {
                    if (this.mPreSequenceCnt == 0 && this.mLastSequenceID < 0) {
                        break;
                    }
                    Log.i(TAG, "wait CameraSequence Start. mPreSequenceCnt =  " + this.mPreSequenceCnt);
                    wait();
                    Log.i(TAG, "wait CameraSequence End.");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public boolean waitCameraSequenceState() {
        boolean waitState = false;
        AppLog.enter(TAG, AppLog.getMethodName() + ", mPreSequenceCnt = " + this.mPreSequenceCnt + ", mLastSequenceID = " + this.mLastSequenceID);
        synchronized (this) {
            if (this.mPreSequenceCnt != 0 || this.mLastSequenceID >= 0) {
                Log.i(TAG, "Now waiting CameraSequence.");
                waitState = true;
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName() + ", waitState:" + waitState);
        return waitState;
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        closeLayout(SPShootingProcessingLayout.TAG);
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mDisplayModeListener != null) {
            DisplayModeObserver.getInstance().removeNotificationListener(this.mDisplayModeListener);
            this.mDisplayModeListener = null;
        }
        if (this.mMedianotification != null) {
            MediaNotificationManager.getInstance().removeNotificationListener(this.mMedianotification);
            this.mMedianotification = null;
        }
        DisplayModeObserver.getInstance().showDialogOn4kOutputConnected(false);
        Log.i(TAG, "AudioRecorder.getInstance().getRecStatus() : " + AudioRecorder.getInstance().getRecStatus());
        if (AudioRecorder.getInstance().getRecStatus()) {
            Log.i(TAG, "AudioRecorder.getInstance().stop() starts ");
            AudioRecorder.getInstance().stop();
            Log.i(TAG, "AudioRecorder.getInstance().stop() ends ");
        }
        CameraEx cameraEx = CameraSetting.getInstance().getCamera();
        cameraEx.setCaptureStatusListener((CameraEx.OnCaptureStatusListener) null);
        cameraEx.setExposureCompleteListener((CameraEx.ExposureCompleteListener) null);
        cameraEx.setStoreImageCompleteListener((CameraEx.StoreImageCompleteListener) null);
        setTakePictureEnable(false);
        cameraEx.setAdditionalDataSize(0, 0, false, this.mAdditionalDataSizeSetCallbackClr);
        AudioManager mAudioManager = new AudioManager();
        AudioManager.Parameters getParams = mAudioManager.getParameters();
        getParams.setOutputBeep(this.mOutputBeep);
        mAudioManager.setParameters(getParams);
        AppLog.info(TAG, "BeepSettin is " + getParams.getOutputBeep());
        mAudioManager.release();
        AppLog.exit(TAG, AppLog.getMethodName());
        closeLayout(ExitScreenProcessingLayout.TAG);
        super.onPause();
    }

    public void setTakePictureEnable(boolean status) {
        AppLog.enter(TAG, AppLog.getMethodName() + " status :" + status);
        mTakePictureEnable = status;
        AppLog.exit(TAG, AppLog.getMethodName() + " mTakePictureEnable :" + mTakePictureEnable);
    }

    public boolean getTakePictureEnable() {
        AppLog.info(TAG, AppLog.getMethodName() + " mTakePictureEnable :" + mTakePictureEnable);
        return mTakePictureEnable;
    }

    public void startPreCameraSequenceCount() {
        AppLog.enter(TAG, AppLog.getMethodName());
        synchronized (this) {
            this.mPreSequenceCnt++;
            AppLog.info(TAG, " mPreSequenceCnt = " + this.mPreSequenceCnt);
            notifyAll();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void endPreCameraSequenceCount() {
        AppLog.enter(TAG, AppLog.getMethodName());
        synchronized (this) {
            if (this.mPreSequenceCnt > 0) {
                this.mPreSequenceCnt--;
            }
            AppLog.info(TAG, " mPreSequenceCnt = " + this.mPreSequenceCnt);
            notifyAll();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void startCameraSequenceCount() {
        AppLog.enter(TAG, AppLog.getMethodName());
        synchronized (this) {
            this.mSequenceCnt++;
            AppLog.info(TAG, " mSequenceCnt = " + this.mSequenceCnt);
            notifyAll();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void endCameraSequenceCount() {
        AppLog.enter(TAG, AppLog.getMethodName());
        synchronized (this) {
            this.mSequenceCnt--;
            AppLog.info(TAG, " mSequenceCnt = " + this.mSequenceCnt);
            notifyAll();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public boolean getCameraSequenceStatus() {
        AppLog.enter(TAG, AppLog.getMethodName() + ", SequenceCnt = " + this.mSequenceCnt);
        boolean restStatus = true;
        synchronized (this) {
            if (this.mSequenceCnt >= AudioRecorder.getInstance().getEsBufferNum() - 2) {
                restStatus = false;
            }
            notifyAll();
        }
        AppLog.exit(TAG, AppLog.getMethodName() + ", restStatus = " + restStatus);
        return restStatus;
    }

    public void setLastSequenceID(int sequenceId) {
        AppLog.enter(TAG, AppLog.getMethodName());
        synchronized (this) {
            this.mTmpLastSequenceID = sequenceId;
            AppLog.info(TAG, " mTmpLastSequenceID = " + this.mTmpLastSequenceID);
            notifyAll();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void setDecideLastSequenceID(boolean status) {
        AppLog.enter(TAG, AppLog.getMethodName());
        synchronized (this) {
            if (this.mTmpLastSequenceID != -1) {
                if (status) {
                    this.mLastSequenceID = this.mTmpLastSequenceID;
                } else {
                    this.mLastSequenceIDng = this.mTmpLastSequenceID;
                }
                this.mTmpLastSequenceID = -1;
            }
            AppLog.info(TAG, " mLastSequenceID = " + this.mLastSequenceID);
            notifyAll();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void clrLastSequenceID(int sequenceId) {
        AppLog.enter(TAG, AppLog.getMethodName() + ", mLastSequenceIDng:" + this.mLastSequenceIDng + ", mLastSequenceID:" + this.mLastSequenceID + ", sequenceId:" + sequenceId);
        synchronized (this) {
            if (this.mLastSequenceIDng != -1 && sequenceId == this.mLastSequenceIDng) {
                this.mLastSequenceIDng = -1;
            }
            if (this.mLastSequenceIDng == -1 && this.mLastSequenceID <= sequenceId) {
                this.mLastSequenceID = -1;
            }
            notifyAll();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void cancelWaitCameraSequenceID() {
        AppLog.enter(TAG, AppLog.getMethodName());
        synchronized (this) {
            this.mPreSequenceCnt = 0;
            this.mLastSequenceID = -1;
            this.mLastSequenceIDng = -1;
            this.mTmpLastSequenceID = -1;
            this.mSequenceCnt = 0;
            this.mFirstSequence = false;
            this.mFirstSeqLock = false;
            notifyAll();
        }
        AudioRecorder.getInstance().deleteCreateSoundStatus();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void setFirstSequenceStatus(boolean status) {
        synchronized (this) {
            this.mFirstSequence = status;
            notifyAll();
        }
        AppLog.info(TAG, " setFirstSequenceStatus : " + this.mFirstSequence);
    }

    public boolean getFirstSequenceStatus() {
        boolean firstSeqstatus;
        AppLog.enter(TAG, " getFirstSequenceStatus : " + this.mFirstSequence);
        synchronized (this) {
            firstSeqstatus = this.mFirstSequence;
        }
        AppLog.exit(TAG, " getFirstSequenceStatus : " + firstSeqstatus);
        return firstSeqstatus;
    }

    public void setFirstSequenceLockState(boolean lock) {
        synchronized (this) {
            this.mFirstSeqLock = lock;
            notifyAll();
        }
        AppLog.info(TAG, " setFirstSequenceStatus : " + this.mFirstSeqLock);
    }

    public boolean getFirstSequenceLockState() {
        boolean lockstatus;
        AppLog.enter(TAG, " getFirstSequenceLockState : " + this.mFirstSeqLock);
        synchronized (this) {
            lockstatus = this.mFirstSeqLock;
        }
        AppLog.exit(TAG, " getFirstSequenceLockState : " + lockstatus);
        return lockstatus;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isDialog4kOutputConnectedShown() {
        return ((BaseApp) getActivity()).is4kPlaybackSupported() && DisplayModeObserver.getInstance().is4kDeviceAvailable();
    }

    private NotificationListener getDisplayObserverListener() {
        return new DisplayModeListener();
    }

    /* loaded from: classes.dex */
    private class MediaNotificationListener implements NotificationListener {
        private final String[] tags;

        private MediaNotificationListener() {
            this.tags = new String[]{MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (tag != null && tag.equals(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE) && SPShootingState.this.mMedianotification != null && MediaNotificationManager.getInstance().isNoCard()) {
                AppLog.info(SPShootingState.TAG, "TESTTAGMEDIA  MediaCard deteched....");
                SPShootingState.this.cancelWaitCameraSequenceID();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DisplayModeListener implements NotificationListener {
        private DisplayModeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return SPShootingState.DIAPLAY_MODE_TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (DisplayModeObserver.TAG_4K_PLAYBACK_STATUS_CHANGE.equals(tag) && DisplayModeObserver.getInstance().is4kDeviceAvailable() && SPShootingState.this.isDialog4kOutputConnectedShown() && 1 == DisplayModeObserver.getInstance().get4kOutputStatus() && 4 == RunStatus.getStatus()) {
                CautionUtilityClass.getInstance().requestTrigger(2913);
            }
        }
    }
}
