package com.sony.imaging.app.soundphoto.shooting.audiorecorder;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.soundphoto.database.SPDataBaseUpdater;
import com.sony.imaging.app.soundphoto.menu.layout.controller.ApplicationSettingsMenuController;
import com.sony.imaging.app.soundphoto.microphone.SPFMicrophoneCtrl;
import com.sony.imaging.app.soundphoto.playback.audiotrack.controller.AudioTrackControl;
import com.sony.imaging.app.soundphoto.shooting.state.SPShootingState;
import com.sony.imaging.app.soundphoto.shooting.state.layout.SPAudioBufferAnimationController;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPConstants;
import com.sony.imaging.app.soundphoto.util.SPUtil;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.media.AudioManager;
import com.sony.scalar.media.AudioRecord;
import com.sony.scalar.sysutil.PlainCalendar;
import com.sony.scalar.sysutil.ScalarProperties;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SuppressLint({"SdCardPath"})
/* loaded from: classes.dex */
public class AudioRecorder {
    private static final int CAPACITY = 8192;
    private static final String TAG = "AudioRecorder";
    private RecordingInfo mRecInfo;
    private static AudioRecorder mInstance = null;
    private static DSP m_encorder = null;
    private static DSP m_zoomNr = null;
    private static Object mWaitWrite = new Object();
    private static Object mLockWrite = new Object();
    AudioManager madAudioManager = null;
    private boolean mRecordStatus = false;
    private boolean mContinuationRec = false;
    private boolean mWaveHeader = false;
    private int mRecrodingTime = 10000;
    private int mESBufferSize = 0;
    private RecNotiyThread mRecNotify = null;
    private int mForwardTime = 5000;
    private int mWatchTime = 100;
    private int mFrameTime = 30;
    private int mAudioBufferSize = 4194304;
    public int mEsBufferNum = 0;
    private ArrayList<RecordingInfo> mRecordingInfoArray = null;
    private EsWriteBuffer mEsWriteBuffer = null;
    private DeviceBuffer m_audioRecordingBuff = null;
    private AudioRecord mAudioRecorder = null;
    private AudioRecord.AudioBuffer mAudioBuff = null;
    private DeviceBuffer mNrTable = null;
    private DeviceBuffer m_encorderBootParam = null;
    private DeviceBuffer m_encorderProgramParam = null;
    private DeviceBuffer m_zoomNrBootParam = null;
    private DeviceBuffer m_zoomNrProgramParam = null;
    private int mMicType = 1;
    private int mMicRefLv = 0;
    private boolean mReadMarkerUpdate = true;
    private AudioRecord.OnRecordPositionUpdateListener m_updateListener = null;
    private AudioRecord.OnErrorNotifyListener m_errorListener = null;
    private boolean mFileSave = true;
    private ExecutorService mExec = null;
    public boolean mShutterSound = false;

    public native int ComposeAudioImage(String str, byte[] bArr, int i, int i2, int i3, int i4, boolean z);

    static {
        System.loadLibrary("ComposeAudioImage");
    }

    public static AudioRecorder getInstance() {
        if (mInstance == null) {
            mInstance = new AudioRecorder();
        }
        return mInstance;
    }

    public void updateRecordingTimeInfo() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mRecrodingTime = ApplicationSettingsMenuController.getInstance().getTotalDuration() * 1000;
        if (SPAudioBufferAnimationController.getInstance().getCurrentAudioBufferTimeSec() < ApplicationSettingsMenuController.getInstance().getPreAudioTime()) {
            this.mRecrodingTime = (SPAudioBufferAnimationController.getInstance().getCurrentAudioBufferTimeSec() + ApplicationSettingsMenuController.getInstance().getPostDuration()) * 1000;
            AppLog.info(TAG, "............... Early Clicked");
        }
        this.mForwardTime = this.mRecrodingTime - (ApplicationSettingsMenuController.getInstance().getPostDuration() * 1000);
        AppLog.exit(TAG, AppLog.getMethodName() + " mRecrodingTime: " + this.mRecrodingTime + " mForwardTime: " + this.mForwardTime);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RecordingInfo {
        AudioRecord.AudioData mBackwardData;
        int mCreateFileIndex;
        long mCurrentFrame;
        long mEndFrame;
        int mEsWriteOffset;
        AudioRecord.AudioData mForwardData;
        boolean mIsRecCompleted;
        ArrayList<SPFileCreateInfo> mSPFCreateInfoArray;
        boolean mSequenceEnd;
        int mSequenceId;
        long mStartFrame;

        private RecordingInfo() {
            this.mSequenceId = -1;
            this.mIsRecCompleted = false;
            this.mSequenceEnd = false;
            this.mStartFrame = -1L;
            this.mCurrentFrame = -1L;
            this.mEndFrame = -1L;
            this.mEsWriteOffset = -1;
            this.mForwardData = null;
            this.mBackwardData = null;
            this.mCreateFileIndex = -1;
            this.mSPFCreateInfoArray = new ArrayList<>();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SPFileCreateInfo {
        String mFilePath;
        boolean mSoundDateExist;
        boolean mSoundDateRequest;

        private SPFileCreateInfo() {
            this.mSoundDateExist = false;
            this.mSoundDateRequest = false;
            this.mFilePath = null;
        }
    }

    public boolean getEsBufferRest() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean restStatus = true;
        if (!this.mEsWriteBuffer.getEsBufferUsedStatus()) {
            AppLog.info(TAG, " getEsBufferUsedStatus is full ");
            restStatus = false;
        }
        if (!SPShootingState.getInstance().getCameraSequenceStatus()) {
            AppLog.info(TAG, " First sequence is not-finish ");
            restStatus = false;
        }
        AppLog.exit(TAG, AppLog.getMethodName() + " restStatus :" + restStatus);
        return restStatus;
    }

    public int getEsBufferNum() {
        return this.mEsBufferNum;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class EsWriteBuffer {
        public int[] mOffset;
        public int[] mSize;
        public boolean[] mUsed;

        public EsWriteBuffer(int BuffOffset, int buffSize, int recordingSize) {
            this.mOffset = null;
            this.mUsed = null;
            this.mSize = null;
            Log.d(AudioRecorder.TAG, "EsWriteBuffer start BuffOffset : " + BuffOffset + " buffSize : " + buffSize + " recordingSize : " + recordingSize);
            int remainSize = buffSize;
            int cnt = 0;
            while (remainSize >= recordingSize) {
                cnt++;
                remainSize -= recordingSize;
            }
            if (cnt > 0) {
                Log.d(AudioRecorder.TAG, "EsWriteBuffer cnt : " + cnt);
                this.mUsed = new boolean[cnt];
                this.mOffset = new int[cnt];
                this.mSize = new int[cnt];
                for (int i = 0; i < cnt; i++) {
                    this.mUsed[i] = false;
                    this.mOffset[i] = (recordingSize * i) + BuffOffset;
                    this.mSize[i] = recordingSize;
                }
            }
        }

        public boolean getEsBufferUsedStatus() {
            AppLog.enter(AudioRecorder.TAG, AppLog.getMethodName());
            boolean restStatus = false;
            int i = 0;
            while (true) {
                if (i >= this.mUsed.length) {
                    break;
                }
                if (this.mUsed[i]) {
                    i++;
                } else {
                    Log.d(AudioRecorder.TAG, " found enable EsBuff index : " + i);
                    restStatus = true;
                    break;
                }
            }
            AppLog.exit(AudioRecorder.TAG, AppLog.getMethodName() + " restStatus :" + restStatus);
            return restStatus;
        }

        public int alloc() {
            for (int i = 0; i < this.mUsed.length; i++) {
                if (!this.mUsed[i]) {
                    this.mUsed[i] = true;
                    return this.mOffset[i];
                }
            }
            return -1;
        }

        public void free(int offset) {
            for (int i = 0; i < this.mUsed.length; i++) {
                if (this.mUsed[i] && this.mOffset[i] == offset) {
                    this.mUsed[i] = false;
                    return;
                }
            }
        }
    }

    private void setup() {
        Log.d(TAG, "### AudioRecord.setup() ###");
        Log.d(TAG, " new AudioRecord() start ");
        this.mAudioRecorder = new AudioRecord();
        Log.d(TAG, " new AudioRecord() end ");
        m_encorder = DSP.createProcessor("sony-di-dsp");
        m_encorder.setProgram("SA_PREINSTALLED_PRG:SA_A_ENC_LPCM");
        this.m_encorderBootParam = m_encorder.directCreateBuffer(AppRoot.USER_KEYCODE.FOCUS_HOLD);
        ByteBuffer enc_bootParam = createEncoderBootParam();
        this.m_encorderBootParam.write(enc_bootParam);
        m_encorder.setArg(0, this.m_encorderBootParam);
        this.m_encorderProgramParam = m_encorder.directCreateBuffer(AppRoot.USER_KEYCODE.FOCUS_HOLD);
        ByteBuffer enc_progParam = createEncoderProgramParam();
        this.m_encorderProgramParam.write(enc_progParam);
        m_encorder.setArg(1, this.m_encorderProgramParam);
        int isSupprtedZoomNr = ScalarProperties.getInt("dsp.zoomnr.supported");
        if (isSupprtedZoomNr == 1) {
            m_zoomNr = DSP.createProcessor("sony-di-dsp");
            m_zoomNr.setProgram("SA_PREINSTALLED_PRG:SA_A_ENC_ZNR");
            this.m_zoomNrBootParam = m_zoomNr.directCreateBuffer(AppRoot.USER_KEYCODE.FOCUS_HOLD);
            ByteBuffer nr_bootParam = createZoomNrBootParam();
            this.m_zoomNrBootParam.write(nr_bootParam);
            m_zoomNr.setArg(0, this.m_zoomNrBootParam);
            this.mNrTable = m_zoomNr.directCreateBuffer(609398);
            this.mAudioRecorder.loadZoomNrTable(this.mNrTable);
            int nrTableAddr = m_zoomNr.getPropertyAsInt(this.mNrTable, "memory-address");
            this.m_zoomNrProgramParam = m_zoomNr.directCreateBuffer(AppRoot.USER_KEYCODE.FOCUS_HOLD);
            ByteBuffer nr_progParam = createZoomNrProgramParam(nrTableAddr, this.mMicType, this.mMicRefLv);
            this.m_zoomNrProgramParam.write(nr_progParam);
            m_zoomNr.setArg(1, this.m_zoomNrProgramParam);
        }
        this.mRecNotify = new RecNotiyThread(this);
        this.mRecNotify.setName("mRecNotify");
        this.mRecNotify.start();
        this.m_audioRecordingBuff = m_encorder.directCreateBuffer(756);
        AudioRecord.EncoderParameters params = new AudioRecord.EncoderParameters();
        if (isSupprtedZoomNr == 1) {
            params.setParamter("AUDIO_FRAME_MAX_SIZE", 5760);
            params.setParamter("AUDIO_ENCODER_TYPE", 0);
            params.setParamter("SHORT_FADER_LENGTH", 0);
            params.setParamter("LPCM_PROC_SIZE", 6144);
            params.setParamter("ES_PROC_SIZE", 11520);
            params.setParamter("AAUG_PROC_MAX_NUM", 2);
            params.setParamter("OVERWRITE_MODE", false);
        } else {
            params.setParamter("AUDIO_FRAME_MAX_SIZE", 5760);
            params.setParamter("AUDIO_ENCODER_TYPE", 0);
            params.setParamter("SHORT_FADER_LENGTH", 0);
            params.setParamter("LPCM_PROC_SIZE", 5760);
            params.setParamter("ES_PROC_SIZE", 5760);
            params.setParamter("AAUG_PROC_MAX_NUM", 1);
            params.setParamter("OVERWRITE_MODE", false);
        }
        Log.v(TAG, "createAudioBuffer() call");
        this.mAudioBuff = this.mAudioRecorder.createAudioBuffer(this.m_audioRecordingBuff, 0, this.mAudioBufferSize, params);
        Log.i(TAG, "Control Offset\t  =" + this.mAudioBuff.getControlInfoOffset());
        Log.i(TAG, "Control Size\t  =" + this.mAudioBuff.getControlBufferSize());
        Log.i(TAG, "ES Offset\t\t  =" + this.mAudioBuff.getEsBufferOffset());
        Log.i(TAG, "ES Size\t\t  =" + this.mAudioBuff.getEsBufferSize());
        Log.i(TAG, "AAUG Info Offset =" + this.mAudioBuff.getAaugiBufferOffset());
        Log.i(TAG, "AAUG Info Size\t  =" + this.mAudioBuff.getAaugiBufferSize());
        Log.i(TAG, "Frame Count\t  =" + this.mAudioBuff.getFrameCount());
        Log.v(TAG, "createAudioBuffer() call end");
        Log.v(TAG, "setAudioBuffer() call");
        this.mAudioRecorder.setAudioBuffer(this.mAudioBuff);
        Log.v(TAG, "setAudioBuffer() call end");
        Log.v(TAG, "setDspPlugin() call");
        this.mAudioRecorder.setDspPlugin(0, m_encorder);
        Log.v(TAG, "setDspPlugin() call end");
        if (m_zoomNr != null) {
            Log.v(TAG, "setDspPlugin() call");
            this.mAudioRecorder.setDspPlugin(1, m_zoomNr);
            Log.v(TAG, "setDspPlugin() call end");
        }
        Log.v(TAG, "setPositionNotificationPeriod() call");
        this.mAudioRecorder.setPositionNotificationPeriod(this.mWatchTime / this.mFrameTime);
        Log.v(TAG, "setPositionNotificationPeriod() call end");
        int phySize = m_encorder.getPropertyAsInt(this.m_audioRecordingBuff, "memory-size");
        this.mRecordingInfoArray = new ArrayList<>();
        this.mESBufferSize = phySize - this.mAudioBufferSize;
        this.mEsWriteBuffer = new EsWriteBuffer(this.mAudioBufferSize, this.mESBufferSize, AudioTrackControl.MAX_SOUND_DATA_SIZE);
        this.mEsBufferNum = this.mESBufferSize / AudioTrackControl.MAX_SOUND_DATA_SIZE;
        Log.v(TAG, "BufferNum is " + this.mEsBufferNum);
        Log.d(TAG, "mnewSingleThreadExecutor start");
        this.mExec = Executors.newSingleThreadExecutor();
        Log.d(TAG, "mnewSingleThreadExecutor end");
    }

    public int getEsBufferSize() {
        return this.mESBufferSize;
    }

    private void end() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mRecordingInfoArray != null) {
            this.mRecordingInfoArray = null;
        }
        if (this.mEsWriteBuffer != null) {
            this.mEsWriteBuffer = null;
        }
        this.mAudioRecorder.setAudioBuffer((AudioRecord.AudioBuffer) null);
        if (this.mAudioBuff != null) {
            try {
                Log.d(TAG, "mAudioBuff = release & null ");
                this.mAudioBuff.release();
                this.mAudioBuff = null;
            } catch (Exception e) {
                Log.d(TAG, "mAudioBuff release " + e.toString());
            }
        }
        if (this.m_zoomNrBootParam != null) {
            this.m_zoomNrBootParam.release();
            this.m_zoomNrBootParam = null;
        }
        if (this.m_zoomNrProgramParam != null) {
            this.m_zoomNrProgramParam.release();
            this.m_zoomNrProgramParam = null;
        }
        if (m_zoomNr != null) {
            m_zoomNr.release();
            m_zoomNr = null;
        }
        if (this.mNrTable != null) {
            this.mNrTable.release();
            this.mNrTable = null;
        }
        if (this.m_encorderBootParam != null) {
            this.m_encorderBootParam.release();
            this.m_encorderBootParam = null;
        }
        if (this.m_encorderProgramParam != null) {
            this.m_encorderProgramParam.release();
            this.m_encorderProgramParam = null;
        }
        if (m_encorder != null) {
            m_encorder.release();
            m_encorder = null;
        }
        if (this.m_audioRecordingBuff != null) {
            this.m_audioRecordingBuff.release();
            this.m_audioRecordingBuff = null;
        }
        if (this.mAudioRecorder != null) {
            this.mAudioRecorder.release();
            this.mAudioRecorder = null;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public boolean getRecStatus() {
        AppLog.info(TAG, AppLog.getMethodName() + " mRecordStatus : " + this.mRecordStatus);
        return this.mRecordStatus;
    }

    public void start() {
        AppLog.enter(TAG, AppLog.getMethodName());
        Log.d(TAG, "### AudioRecord.start() ###");
        setup();
        Log.v(TAG, "startEE() call");
        this.mAudioRecorder.startEE();
        Log.v(TAG, "startEE() call end");
        Log.v(TAG, "startRecording() call");
        this.mAudioRecorder.startRecording();
        Log.v(TAG, "startRecording() call end");
        this.mRecordStatus = true;
        AppLog.exit(TAG, AppLog.getMethodName());
        SPAudioBufferAnimationController.getInstance().setmStartTime(SPUtil.getInstance().getCurrentTimeinSec());
        SPShootingState.getInstance().setFirstSequenceStatus(true);
    }

    public void stop() {
        AppLog.enter(TAG, AppLog.getMethodName());
        SPShootingState.getInstance().waitCameraSequence();
        if (getCreateSoundStatus()) {
            AppLog.info(TAG, " execCreateSPF start ");
            execCreateSPF();
            AppLog.info(TAG, " execCreateSPF end ");
        }
        if (this.mExec != null) {
            Log.d(TAG, "mExec.shutdown() start");
            this.mExec.shutdown();
            Log.d(TAG, "mExec.shutdown() end");
            try {
                boolean result = this.mExec.awaitTermination(30L, TimeUnit.SECONDS);
                if (result) {
                    Log.d(TAG, "all thread is done");
                } else {
                    Log.d(TAG, "remain thread");
                    this.mExec.shutdownNow();
                }
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                Log.d(TAG, "mExec.wait() exception is occured");
            }
        }
        Log.v(TAG, "stopRecording() call");
        this.mAudioRecorder.stopRecording();
        Log.v(TAG, "stopRecording() call end");
        Log.v(TAG, "stopEE() call");
        this.mAudioRecorder.stopEE();
        Log.v(TAG, "stopEE() call end");
        this.mRecNotify.halt();
        try {
            this.mRecNotify.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        end();
        this.mRecordStatus = false;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void rec_time(int recordingTime, int forwardTime) {
        Log.d(TAG, "### AudioRecord.rec_time() ###");
        Log.d(TAG, "  recordingTime = " + recordingTime);
        Log.d(TAG, "  forwardTime   = " + forwardTime);
        this.mRecrodingTime = recordingTime;
        this.mForwardTime = forwardTime;
    }

    public PlainCalendar get_rec_start_time() {
        if (this.mAudioRecorder == null) {
            return null;
        }
        PlainCalendar calender = this.mAudioRecorder.getRecordingDateTime();
        return calender;
    }

    public void enableReadmarkerUpdate(boolean flag) {
        Log.d(TAG, "enableReadmarkerUpdate = " + flag);
        this.mReadMarkerUpdate = flag;
    }

    public void updateProtectPosition(long notifyPosition) {
        if (this.mReadMarkerUpdate) {
            long currentFrames = this.mAudioRecorder.getWriteMarkerPosition();
            long guardFrames = currentFrames - ((this.mRecrodingTime + 1000) / this.mFrameTime);
            if (guardFrames < 0) {
                guardFrames = 0;
            }
            try {
                this.mAudioRecorder.setReadMarkerPosition(guardFrames);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void rec_end(int sequenceId) {
        AppLog.enter(TAG, AppLog.getMethodName() + " sequenceId : " + sequenceId);
        synchronized (mLockWrite) {
            int i = 0;
            while (true) {
                if (i >= this.mRecordingInfoArray.size()) {
                    break;
                }
                if (this.mRecordingInfoArray.get(i).mSequenceId != sequenceId) {
                    i++;
                } else {
                    this.mRecordingInfoArray.get(i).mSequenceEnd = true;
                    AppLog.info(TAG, " End Sequence iS found. i : " + i + ", sequenceId : " + sequenceId);
                    break;
                }
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void rec_single(int sequenceId) {
        Log.d(TAG, "### AudioRecord.rec_single()  Start sequenceID : " + sequenceId + " ###");
        synchronized (mLockWrite) {
            for (int i = 0; i < this.mRecordingInfoArray.size(); i++) {
                if (this.mRecordingInfoArray.get(i).mSequenceId == sequenceId) {
                    Log.d(TAG, AppLog.getMethodName() + "is finished by Starting Rec already");
                    return;
                }
            }
            updateRecordingTimeInfo();
            rec_time(setRecordTime(), getShutterTiming());
            long currentFrame = this.mAudioRecorder.getWriteMarkerPosition();
            long startFrame = currentFrame - ((this.mForwardTime + this.mFrameTime) / this.mFrameTime);
            long endFrame = currentFrame + (((this.mRecrodingTime - this.mForwardTime) + this.mFrameTime) / this.mFrameTime);
            if (startFrame < 0) {
                startFrame = 0;
            }
            if (this.mForwardTime <= 0) {
                startFrame = currentFrame;
            }
            if (this.mRecrodingTime - this.mForwardTime <= 0) {
                endFrame = currentFrame;
            }
            long recFrame = endFrame - startFrame;
            long diff = recFrame - (((this.mRecrodingTime + this.mFrameTime) / this.mFrameTime) - 1);
            if (diff > 0) {
                if (this.mForwardTime <= 0) {
                    endFrame -= diff;
                } else {
                    startFrame += diff;
                }
            }
            Log.d(TAG, "### startFrame   = " + startFrame);
            Log.d(TAG, "### currentFrame = " + currentFrame);
            Log.d(TAG, "### endFrame     = " + endFrame);
            int writeOffset = this.mEsWriteBuffer.alloc();
            if (writeOffset < 0) {
                Log.e(TAG, "### AudioRecord.rec_single()  End by mEsWriteBuffer.acquire() is false ###");
                return;
            }
            try {
                this.mAudioRecorder.addNotificationMarkerPosition(endFrame);
            } catch (IOException e) {
                e.printStackTrace();
            }
            RecordingInfo recInfo = new RecordingInfo();
            recInfo.mIsRecCompleted = this.mForwardTime == this.mRecrodingTime;
            recInfo.mSequenceId = sequenceId;
            recInfo.mStartFrame = startFrame;
            recInfo.mCurrentFrame = currentFrame;
            recInfo.mEndFrame = endFrame;
            recInfo.mEsWriteOffset = writeOffset;
            recInfo.mForwardData = null;
            recInfo.mSequenceEnd = false;
            synchronized (mLockWrite) {
                this.mRecordingInfoArray.add(recInfo);
            }
            Log.d(TAG, "### AudioRecord.rec_single()  End by normal ###");
        }
    }

    public void storeImageComplete(int sequenceId, CameraEx.StoreImageInfo info) {
        Log.d(TAG, "storeImageComplete() in sequenceId = " + sequenceId + " info.DirectoryName = " + info.DirectoryName + " info.FileName = " + info.FileName);
        RecordingInfo recInfo = null;
        synchronized (mLockWrite) {
            for (int i = 0; i < this.mRecordingInfoArray.size(); i++) {
                try {
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                if (this.mRecordingInfoArray.get(i).mSequenceId != sequenceId) {
                    continue;
                } else {
                    recInfo = this.mRecordingInfoArray.get(i);
                    Log.d(TAG, "recInfo is exist (i) = " + i);
                    break;
                }
            }
        }
        if (recInfo != null) {
            Log.d(TAG, "### recInfo.mSequenceId   = " + recInfo.mSequenceId);
            Log.d(TAG, "### recInfo.mSequenceEnd   = " + recInfo.mSequenceEnd);
            Log.d(TAG, "### recInfo.mStartFrame   = " + recInfo.mStartFrame);
            Log.d(TAG, "### recInfo.mCurrentFrame = " + recInfo.mCurrentFrame);
            Log.d(TAG, "### recInfo.mEndFrame     = " + recInfo.mEndFrame);
            SPFileCreateInfo spfCreateInfo = new SPFileCreateInfo();
            spfCreateInfo.mSoundDateExist = false;
            spfCreateInfo.mFilePath = Environment.getExternalStorageDirectory().getPath() + SPConstants.FILE_SEPARATER + info.DirectoryName + info.FileName + ".JPG";
            File saveFolder = new File(spfCreateInfo.mFilePath);
            if (!saveFolder.exists()) {
                String[] names = info.DirectoryName.split(SPConstants.FILE_SEPARATER);
                String dirNumber = names[1].substring(0, 3);
                File dir = new File(Environment.getExternalStorageDirectory().getPath() + SPConstants.FILE_SEPARATER + names[0]);
                String[] list = dir.list();
                for (String folderName : list) {
                    if (folderName.startsWith(dirNumber)) {
                        spfCreateInfo.mFilePath = Environment.getExternalStorageDirectory().getPath() + SPConstants.FILE_SEPARATER + names[0] + SPConstants.FILE_SEPARATER + folderName + SPConstants.FILE_SEPARATER + info.FileName + ".JPG";
                        Log.d(TAG, " MK142H-9811 new path = " + spfCreateInfo.mFilePath);
                        break;
                    }
                }
            }
            try {
                recInfo.mSPFCreateInfoArray.add(spfCreateInfo);
                for (int i2 = 0; i2 < recInfo.mSPFCreateInfoArray.size(); i2++) {
                    if (recInfo.mSPFCreateInfoArray.get(i2).mFilePath == spfCreateInfo.mFilePath && recInfo.mIsRecCompleted) {
                        recInfo.mSPFCreateInfoArray.get(i2).mSoundDateRequest = true;
                        recInfo.mCreateFileIndex = i2;
                        Log.d(TAG, "requestCreateSoundPhotoFile() : recInfo.mCreateFileIndex = " + recInfo.mCreateFileIndex + " mFilePath : " + recInfo.mSPFCreateInfoArray.get(i2).mFilePath);
                        requestCreateSoundPhotoFile(recInfo);
                        return;
                    }
                }
                return;
            } catch (IndexOutOfBoundsException e2) {
                e2.printStackTrace();
                return;
            }
        }
        Log.d(TAG, "### recInfo is null");
    }

    public void recordComplete(long notifyPosition) {
        AppLog.exit(TAG, AppLog.getMethodName() + " notifyPosition : " + notifyPosition);
        RecordingInfo recInfo = null;
        synchronized (mLockWrite) {
            int i = 0;
            while (true) {
                if (i >= this.mRecordingInfoArray.size()) {
                    break;
                }
                if (this.mRecordingInfoArray.get(i).mEndFrame != notifyPosition) {
                    i++;
                } else {
                    Log.d(TAG, "Rec Info is exist mRecordingInfoArray(" + i + LogHelper.MSG_CLOSE_BRACKET);
                    recInfo = this.mRecordingInfoArray.get(i);
                    break;
                }
            }
        }
        if (recInfo != null) {
            Log.d(TAG, "### recInfo.mSequenceId   = " + recInfo.mSequenceId);
            Log.d(TAG, "### recInfo.mStartFrame   = " + recInfo.mStartFrame);
            Log.d(TAG, "### recInfo.mCurrentFrame = " + recInfo.mCurrentFrame);
            Log.d(TAG, "### recInfo.mEndFrame     = " + recInfo.mEndFrame);
            try {
                int readSize = this.mAudioRecorder.getReadEsBufferSize(recInfo.mStartFrame, notifyPosition);
                int esWriteOffset = recInfo.mEsWriteOffset;
                if (recInfo.mForwardData != null) {
                    esWriteOffset += recInfo.mForwardData.getDataSize();
                }
                Log.d(TAG, "### readSize = " + readSize);
                AudioRecord.AudioData audioData = this.mAudioRecorder.readEsBuffer(recInfo.mStartFrame, notifyPosition, this.m_audioRecordingBuff, esWriteOffset, readSize);
                recInfo.mBackwardData = audioData;
                recInfo.mIsRecCompleted = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int i2 = 0; i2 < recInfo.mSPFCreateInfoArray.size(); i2++) {
                if (recInfo.mSPFCreateInfoArray != null && recInfo.mSPFCreateInfoArray.size() > 0 && recInfo.mSPFCreateInfoArray.get(i2).mFilePath != null && !recInfo.mSPFCreateInfoArray.get(i2).mSoundDateExist && recInfo.mIsRecCompleted) {
                    recInfo.mSPFCreateInfoArray.get(i2).mSoundDateRequest = true;
                    recInfo.mCreateFileIndex = i2;
                    Log.d(TAG, "requestCreateSoundPhotoFile() : recInfo.mCreateFileIndex = " + recInfo.mCreateFileIndex + " mFilePath : " + recInfo.mSPFCreateInfoArray.get(i2).mFilePath);
                    requestCreateSoundPhotoFile(recInfo);
                }
            }
            return;
        }
        Log.d(TAG, "### recInfo is null");
    }

    public void setRecordTime(int time_value) {
        this.mRecrodingTime = time_value;
    }

    public void setShutterTiming(int timing_value) {
        this.mForwardTime = timing_value;
    }

    public int setRecordTime() {
        return this.mRecrodingTime;
    }

    public int getShutterTiming() {
        return this.mForwardTime;
    }

    public boolean getShutterSound() {
        return true;
    }

    public void setMicrophoneType(String microphoneType, String microphoneChannel) {
        if (microphoneType.equals("inner-mic")) {
            Log.i(TAG, "value = INNER");
            this.mMicType = 1;
        } else if (microphoneType.equals("shoe-mic")) {
            Log.i(TAG, "value = SHOE");
            this.mMicType = 2;
        } else if (microphoneType.equals("zoom-mic")) {
            Log.i(TAG, "value = ZOOM");
            this.mMicType = 3;
        } else if (microphoneType.equals("wireless-mic")) {
            Log.i(TAG, "value = WIRELESS");
            this.mMicType = 4;
        } else if (microphoneType.equals("external-mic")) {
            Log.i(TAG, "value = EXTERNAL");
            this.mMicType = 5;
        } else if (microphoneType.equals("line-in")) {
            Log.i(TAG, "value = LINE");
            this.mMicType = 6;
        } else {
            Log.i(TAG, "value = unknown");
        }
        if (this.m_zoomNrProgramParam != null) {
            ByteBuffer buff = ByteBuffer.allocateDirect(4);
            buff.order(ByteOrder.nativeOrder());
            buff.putInt(this.mMicType);
            this.m_zoomNrProgramParam.write(buff, 4, 404);
        }
    }

    public void setMicReferenceLevel(String referenceLevel) {
        if (referenceLevel.equals("low")) {
            Log.i(TAG, "value = MICROPHONE_REFERENCE_LEVEL_LOW");
            this.mMicRefLv = 1;
        } else if (referenceLevel.equals("normal")) {
            Log.i(TAG, "value = MICROPHONE_REFERENCE_LEVEL_NORMAL");
            this.mMicRefLv = 0;
        } else {
            Log.i(TAG, "value = unknown");
        }
        if (this.m_zoomNrProgramParam != null) {
            ByteBuffer buff = ByteBuffer.allocateDirect(4);
            buff.order(ByteOrder.nativeOrder());
            buff.putInt(this.mMicRefLv);
            this.m_zoomNrProgramParam.write(buff, 4, 408);
        }
    }

    private ByteBuffer createEncoderBootParam() {
        ByteBuffer enc_bootParam = ByteBuffer.allocateDirect(20);
        enc_bootParam.order(ByteOrder.nativeOrder());
        enc_bootParam.put((byte) 0);
        enc_bootParam.put((byte) 17);
        enc_bootParam.putShort((short) 0);
        enc_bootParam.putInt(0);
        enc_bootParam.put((byte) 0);
        enc_bootParam.put((byte) 0);
        enc_bootParam.put((byte) 30);
        enc_bootParam.put((byte) 0);
        enc_bootParam.putInt(0);
        enc_bootParam.putInt(0);
        return enc_bootParam;
    }

    private ByteBuffer createEncoderProgramParam() {
        ByteBuffer enc_progParam = ByteBuffer.allocateDirect(20);
        enc_progParam.order(ByteOrder.nativeOrder());
        enc_progParam.putInt(7);
        enc_progParam.putInt(0);
        enc_progParam.putInt(0);
        enc_progParam.putInt(0);
        enc_progParam.putInt(0);
        return enc_progParam;
    }

    private ByteBuffer createZoomNrBootParam() {
        ByteBuffer nr_bootParam = ByteBuffer.allocateDirect(20);
        nr_bootParam.order(ByteOrder.nativeOrder());
        nr_bootParam.put((byte) 0);
        nr_bootParam.put((byte) 20);
        nr_bootParam.putShort((short) 0);
        nr_bootParam.putInt(0);
        nr_bootParam.put((byte) 0);
        nr_bootParam.put((byte) 0);
        nr_bootParam.put((byte) 30);
        nr_bootParam.put((byte) 1);
        nr_bootParam.putInt(0);
        nr_bootParam.putInt(0);
        return nr_bootParam;
    }

    private ByteBuffer createZoomNrProgramParam(int nrTableAddr, int micType, int micRefLv) {
        int GroundNoiseReduction = ScalarProperties.getInt("device.ground.noise.reduction");
        ByteBuffer nr_progParam = ByteBuffer.allocateDirect(512);
        nr_progParam.order(ByteOrder.nativeOrder());
        if (GroundNoiseReduction == 1) {
            nr_progParam.put((byte) 9);
            Log.i(TAG, "OnOff: 0x09");
        } else {
            nr_progParam.put((byte) 0);
            Log.i(TAG, "OnOff: 0x00");
        }
        nr_progParam.put((byte) 0);
        nr_progParam.put((byte) 0);
        nr_progParam.put((byte) 0);
        nr_progParam.putInt(nrTableAddr);
        nr_progParam.putInt(0);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putLong(0L);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        nr_progParam.putInt(micType);
        nr_progParam.putInt(micRefLv);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        nr_progParam.putInt(0);
        return nr_progParam;
    }

    public void execCreateSPF() {
        AppLog.enter(TAG, AppLog.getMethodName());
        synchronized (mLockWrite) {
            for (int i = 0; i < this.mRecordingInfoArray.size(); i++) {
                Log.d(TAG, "mRecordingInfoArray.size() : " + this.mRecordingInfoArray.size());
                for (int j = 0; j < this.mRecordingInfoArray.get(i).mSPFCreateInfoArray.size(); j++) {
                    Log.d(TAG, "mRecordingInfoArray.get(i).mSPFCreateInfoArray().size() : " + this.mRecordingInfoArray.get(i).mSPFCreateInfoArray.size() + ", i : " + i);
                    if (!this.mRecordingInfoArray.get(i).mSPFCreateInfoArray.get(j).mSoundDateExist) {
                        this.mRecordingInfoArray.get(i).mCreateFileIndex = j;
                        RecordingInfo recInfo = this.mRecordingInfoArray.get(i);
                        long CurrentnotifyPosition = this.mAudioRecorder.getWriteMarkerPosition();
                        if (recInfo.mEndFrame > CurrentnotifyPosition) {
                            Log.d(TAG, "EndFrame is big rather than CurrentFrame!");
                            recInfo.mEndFrame = CurrentnotifyPosition;
                        }
                        Log.d(TAG, "### recInfo.mSequenceId   = " + recInfo.mSequenceId);
                        Log.d(TAG, "### recInfo.mFilePath     = " + recInfo.mSPFCreateInfoArray.get(recInfo.mCreateFileIndex).mFilePath);
                        Log.d(TAG, "### recInfo.mStartFrame   = " + recInfo.mStartFrame);
                        Log.d(TAG, "### recInfo.mCurrentFrame = " + recInfo.mCurrentFrame);
                        Log.d(TAG, "### recInfo.mEndFrame     = " + recInfo.mEndFrame);
                        try {
                            int readSize = this.mAudioRecorder.getReadEsBufferSize(recInfo.mStartFrame, recInfo.mEndFrame);
                            int esWriteOffset = recInfo.mEsWriteOffset;
                            if (recInfo.mForwardData != null) {
                                esWriteOffset += recInfo.mForwardData.getDataSize();
                            }
                            Log.d(TAG, "### readSize = " + readSize);
                            AudioRecord.AudioData audioData = this.mAudioRecorder.readEsBuffer(recInfo.mStartFrame, recInfo.mEndFrame, this.m_audioRecordingBuff, esWriteOffset, readSize);
                            recInfo.mBackwardData = audioData;
                            recInfo.mIsRecCompleted = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        for (int k = 0; k < recInfo.mSPFCreateInfoArray.size(); k++) {
                            if (recInfo.mSPFCreateInfoArray != null && recInfo.mSPFCreateInfoArray.size() > 0 && recInfo.mSPFCreateInfoArray.get(k).mFilePath != null && !recInfo.mSPFCreateInfoArray.get(k).mSoundDateExist && recInfo.mIsRecCompleted && !recInfo.mSPFCreateInfoArray.get(k).mSoundDateRequest) {
                                recInfo.mSPFCreateInfoArray.get(k).mSoundDateRequest = true;
                                recInfo.mCreateFileIndex = k;
                                Log.d(TAG, "requestCreateSoundPhotoFile() : recInfo.mCreateFileIndex = " + recInfo.mCreateFileIndex + " mFilePath : " + recInfo.mSPFCreateInfoArray.get(k).mFilePath);
                                requestCreateSoundPhotoFile(recInfo);
                            }
                        }
                    }
                }
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void requestCreateSoundPhotoFile(RecordingInfo recInfo) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.info(TAG, AppLog.getMethodName() + ", CHECK_THREAD , Index:" + recInfo.mCreateFileIndex + ", path" + recInfo.mSPFCreateInfoArray.get(recInfo.mCreateFileIndex).mFilePath);
        createSoundPhotoFileThread createSPF = new createSoundPhotoFileThread(this, recInfo, recInfo.mCreateFileIndex);
        createSPF.setName("createSoundPhotoFileThread");
        this.mExec.execute(createSPF);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class createSoundPhotoFileThread extends Thread {
        static final String TAG = "createSoundPhotoFileThread";
        private AudioRecorder mAudioRecorder;
        int mCreateFileIndex;
        private RecordingInfo mRecInfo;

        public createSoundPhotoFileThread(AudioRecorder audioRecorder, RecordingInfo recInfo, int index) {
            this.mAudioRecorder = null;
            this.mRecInfo = null;
            AppLog.enter(TAG, AppLog.getMethodName() + " audioRecorder: " + audioRecorder);
            this.mAudioRecorder = audioRecorder;
            this.mRecInfo = recInfo;
            this.mCreateFileIndex = index;
            AppLog.exit(TAG, AppLog.getMethodName());
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            AppLog.enter(TAG, AppLog.getMethodName());
            this.mAudioRecorder.createSoundPhotoFile(this.mRecInfo, this.mCreateFileIndex);
            Log.d(TAG, "mAudioRecorder.createSoundPhotoFile() end");
            AppLog.exit(TAG, AppLog.getMethodName());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createSoundPhotoFile(RecordingInfo recInfo, int fileIndex) {
        int mRealRecrodingTime;
        int mRealForwardTime;
        if (recInfo == null) {
            Log.e(TAG, "not found SequenceID.");
            return;
        }
        if (recInfo.mSPFCreateInfoArray.get(fileIndex).mFilePath != null && recInfo.mIsRecCompleted) {
            Log.d(TAG, "createSoundPhotoFile() recInfo.mFilePath = " + recInfo.mSPFCreateInfoArray.get(fileIndex).mFilePath);
            String filePath = recInfo.mSPFCreateInfoArray.get(fileIndex).mFilePath;
            int dataSize = recInfo.mForwardData != null ? 0 + recInfo.mForwardData.getDataSize() : 0;
            if (recInfo.mBackwardData != null) {
                dataSize += recInfo.mBackwardData.getDataSize();
            }
            Log.i(TAG, "Saving file.");
            Log.i(TAG, " file  : " + filePath);
            Log.i(TAG, " offset: " + recInfo.mEsWriteOffset);
            Log.i(TAG, " size  : " + dataSize);
            File file = new File(filePath);
            if (recInfo.mSequenceId >= 0 && file.exists()) {
                DeviceBuffer deviceBuffer = null;
                int offset = 0;
                if (recInfo.mForwardData != null) {
                    deviceBuffer = recInfo.mForwardData.getDeviceBuffer();
                    offset = recInfo.mForwardData.getBufferOffset();
                } else if (recInfo.mBackwardData != null) {
                    deviceBuffer = recInfo.mBackwardData.getDeviceBuffer();
                    offset = recInfo.mBackwardData.getBufferOffset();
                }
                int setDataSize = 48000 * ApplicationSettingsMenuController.getInstance().getTotalDuration() * 2 * 2;
                if (dataSize >= setDataSize) {
                    dataSize = setDataSize;
                    mRealRecrodingTime = ApplicationSettingsMenuController.getInstance().getTotalDuration() * 1000;
                    mRealForwardTime = this.mRecrodingTime - (ApplicationSettingsMenuController.getInstance().getPostDuration() * 1000);
                } else {
                    int mRealRecrodingTime2 = ApplicationSettingsMenuController.getInstance().getTotalDuration() * 1000;
                    int mRealForwardTime2 = this.mRecrodingTime - (ApplicationSettingsMenuController.getInstance().getPostDuration() * 1000);
                    int i = 48000 * mRealForwardTime2 * 2 * 2;
                    int mRealRecrodingTime3 = (int) (recInfo.mEndFrame - recInfo.mStartFrame);
                    int mRealForwardTime3 = mRealRecrodingTime3 - ((int) (recInfo.mEndFrame - recInfo.mCurrentFrame));
                    mRealRecrodingTime = mRealRecrodingTime3 * 30;
                    mRealForwardTime = mRealForwardTime3 * 30;
                }
                Log.i(TAG, "dataSize: " + dataSize + ", mRealRecrodingTime: " + mRealRecrodingTime + ", mRealForwardTime : " + mRealForwardTime);
                byte[] data_wav_data = new byte[dataSize + 44];
                try {
                    ByteBuffer waveHeader = ByteBuffer.allocateDirect(44);
                    waveHeader.order(ByteOrder.LITTLE_ENDIAN);
                    waveHeader.put((byte) 82);
                    waveHeader.put((byte) 73);
                    waveHeader.put((byte) 70);
                    waveHeader.put((byte) 70);
                    waveHeader.putInt(dataSize + 36);
                    waveHeader.put((byte) 87);
                    waveHeader.put((byte) 65);
                    waveHeader.put((byte) 86);
                    waveHeader.put((byte) 69);
                    waveHeader.put((byte) 102);
                    waveHeader.put((byte) 109);
                    waveHeader.put((byte) 116);
                    waveHeader.put((byte) 32);
                    waveHeader.putInt(16);
                    waveHeader.putShort((short) 1);
                    waveHeader.putShort((short) 2);
                    waveHeader.putInt(48000);
                    waveHeader.putInt(192000);
                    waveHeader.putShort((short) 4);
                    waveHeader.putShort((short) 16);
                    waveHeader.put((byte) 100);
                    waveHeader.put((byte) 97);
                    waveHeader.put((byte) 116);
                    waveHeader.put((byte) 97);
                    waveHeader.putInt(dataSize);
                    waveHeader.position(0);
                    waveHeader.limit(44);
                    waveHeader.get(data_wav_data, 0, 44);
                    Log.i(TAG, "data_wav_data head = " + data_wav_data);
                    int remain = dataSize;
                    ByteBuffer waveData = ByteBuffer.allocateDirect(CAPACITY);
                    waveData.order(ByteOrder.LITTLE_ENDIAN);
                    Log.i(TAG, "Sound data size = " + dataSize);
                    int getData = 44;
                    while (remain > 0) {
                        int size = Math.min(CAPACITY, remain);
                        waveData.position(0);
                        waveData.limit(size);
                        deviceBuffer.read(waveData, 0, size, offset);
                        waveData.position(0);
                        waveData.limit(size);
                        waveData.get(data_wav_data, getData, size);
                        getData += size;
                        offset += size;
                        remain -= size;
                    }
                    Log.i(TAG, "data_wav_data all = " + data_wav_data + " datanum = " + getData);
                } catch (Exception e) {
                    Log.i(TAG, "Exception is occurd" + e.toString());
                }
                Log.d(TAG, "### recInfo.mSequenceId   = " + recInfo.mSequenceId);
                Log.d(TAG, "### recInfo.mFilePath     = " + recInfo.mSPFCreateInfoArray.get(fileIndex).mFilePath);
                Log.d(TAG, "### filePath     = " + filePath);
                Log.d(TAG, "### recInfo.mStartFrame   = " + recInfo.mStartFrame);
                Log.d(TAG, "### recInfo.mCurrentFrame = " + recInfo.mCurrentFrame);
                Log.d(TAG, "### recInfo.mEndFrame     = " + recInfo.mEndFrame);
                int MicType = SPFMicrophoneCtrl.getInstance().getMicrophoneType();
                Log.i(TAG, "Call MSL");
                Log.i(TAG, " file  : " + recInfo.mSPFCreateInfoArray.get(fileIndex).mFilePath);
                Log.i(TAG, " filePath  : " + filePath);
                Log.i(TAG, " offset: " + recInfo.mEsWriteOffset);
                Log.i(TAG, " mRecrodingTime: " + mRealRecrodingTime);
                Log.i(TAG, " mRealForwardTime: " + mRealForwardTime);
                Log.i(TAG, " ComposeAudioImage start ID : " + recInfo.mSequenceId);
                int err = ComposeAudioImage(filePath, data_wav_data, dataSize + 44, mRealRecrodingTime, mRealForwardTime, MicType, this.mShutterSound);
                recInfo.mSPFCreateInfoArray.get(fileIndex).mSoundDateExist = true;
                SPDataBaseUpdater.getInstance().saveSoundPhotoBOData();
                if (data_wav_data != null) {
                }
                Log.d(TAG, "recInfo.mSPFCreateInfoArray.get(index).mSoundDateExist = true (index): " + fileIndex);
                Log.i(TAG, " ComposeAudioImage end ID : " + recInfo.mSequenceId + " err : " + err);
            } else {
                Log.e(TAG, "file not exists.");
            }
            if (recInfo.mSequenceEnd) {
                Log.d(TAG, "SequenceID ( " + recInfo.mSequenceId + " ) is received End.");
                boolean finishedSeqence = true;
                Log.d(TAG, "recInfo.mSPFCreateInfoArray.size() : " + recInfo.mSPFCreateInfoArray.size());
                int i2 = 0;
                while (true) {
                    if (i2 >= recInfo.mSPFCreateInfoArray.size()) {
                        break;
                    }
                    Log.d(TAG, "recInfo.mSPFCreateInfoArray.get(i).mSoundDateExist : " + recInfo.mSPFCreateInfoArray.get(i2).mSoundDateExist);
                    if (recInfo.mSPFCreateInfoArray.get(i2).mSoundDateExist) {
                        i2++;
                    } else {
                        Log.i(TAG, recInfo.mSPFCreateInfoArray.get(i2).mFilePath + " is not SPF.");
                        finishedSeqence = false;
                        break;
                    }
                }
                if (finishedSeqence) {
                    Log.e(TAG, " All files are became SPF. mEsWriteBuffer release mSequenceId : " + recInfo.mSequenceId);
                    this.mEsWriteBuffer.free(recInfo.mEsWriteOffset);
                    if (recInfo.mForwardData != null) {
                        recInfo.mForwardData.recycle();
                        recInfo.mForwardData = null;
                    }
                    if (recInfo.mBackwardData != null) {
                        recInfo.mBackwardData.recycle();
                        recInfo.mBackwardData = null;
                    }
                    recInfo.mSPFCreateInfoArray = null;
                    synchronized (mLockWrite) {
                        this.mRecordingInfoArray.remove(recInfo);
                    }
                    AppLog.trace(TAG, "createSoundPhotoFile saved in Database saved started");
                }
            }
        }
        Log.d(TAG, "createSoundPhotoFile end");
    }

    public boolean getCreateSoundStatus() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean createSoundPhotoStatus = false;
        if (SPShootingState.getInstance().waitCameraSequenceState()) {
            createSoundPhotoStatus = true;
        } else if (this.mRecordingInfoArray != null) {
            synchronized (mLockWrite) {
                for (int i = 0; i < this.mRecordingInfoArray.size(); i++) {
                    int j = 0;
                    while (true) {
                        if (j >= this.mRecordingInfoArray.get(i).mSPFCreateInfoArray.size()) {
                            break;
                        }
                        if (this.mRecordingInfoArray.get(i).mSPFCreateInfoArray.get(j).mSoundDateExist) {
                            j++;
                        } else {
                            Log.i(TAG, "Not sound Photo is exist. i: " + i + ", j:" + j);
                            createSoundPhotoStatus = true;
                            break;
                        }
                    }
                    if (createSoundPhotoStatus) {
                        break;
                    }
                }
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName() + ", createSoundPhotoStatus:" + createSoundPhotoStatus);
        return createSoundPhotoStatus;
    }

    public void deleteCreateSoundStatus() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mRecordingInfoArray != null) {
            synchronized (mLockWrite) {
                while (0 < this.mRecordingInfoArray.size()) {
                    AppLog.info(TAG, AppLog.getMethodName() + "mRecordingInfoArray.size():" + this.mRecordingInfoArray.size() + ", i:0");
                    this.mEsWriteBuffer.free(this.mRecordingInfoArray.get(0).mEsWriteOffset);
                    if (this.mRecordingInfoArray.get(0).mForwardData != null) {
                        this.mRecordingInfoArray.get(0).mForwardData.recycle();
                        this.mRecordingInfoArray.get(0).mForwardData = null;
                    }
                    if (this.mRecordingInfoArray.get(0).mBackwardData != null) {
                        this.mRecordingInfoArray.get(0).mBackwardData.recycle();
                        this.mRecordingInfoArray.get(0).mBackwardData = null;
                    }
                    this.mRecordingInfoArray.get(0).mSPFCreateInfoArray = null;
                    this.mRecordingInfoArray.remove(this.mRecordingInfoArray.get(0));
                }
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* loaded from: classes.dex */
    public class RecNotiyThread extends Thread {
        static final String TAG = "RecNotiyThread";
        private AudioRecorder mRecorderNotify;
        private Looper mLooper = null;
        private Handler mHandler = null;

        public RecNotiyThread(AudioRecorder recorder) {
            this.mRecorderNotify = null;
            this.mRecorderNotify = recorder;
        }

        /* loaded from: classes.dex */
        private class myRecordPositionUpdateListener implements AudioRecord.OnRecordPositionUpdateListener {
            private myRecordPositionUpdateListener() {
            }

            public void onMarkerReached(long notifyPosition, AudioRecord recorder) {
                Log.i(RecNotiyThread.TAG, "onMarkerReached() call position = " + notifyPosition);
                RecNotiyThread.this.mRecorderNotify.recordComplete(notifyPosition);
            }

            public void onPeriodicNotification(long notifyPosition, AudioRecord recorder) {
                if (0 == notifyPosition % (1000 / AudioRecorder.this.mFrameTime)) {
                    Log.i(RecNotiyThread.TAG, "onPeriodicNotification() call position = " + notifyPosition);
                }
                RecNotiyThread.this.mRecorderNotify.updateProtectPosition(notifyPosition);
            }
        }

        /* loaded from: classes.dex */
        private class myErrorNotifyListener implements AudioRecord.OnErrorNotifyListener {
            private myErrorNotifyListener() {
            }

            public void onErrorNotification(int error, AudioRecord recorder) {
                Log.e(RecNotiyThread.TAG, "onErrorNotification() call error = " + error);
                Log.e(RecNotiyThread.TAG, "mRecorderNotify.stop start");
                RecNotiyThread.this.mRecorderNotify.stop();
                Log.e(RecNotiyThread.TAG, "mRecorderNotify.stop end");
                Log.e(RecNotiyThread.TAG, "mRecorderNotify.start start");
                RecNotiyThread.this.mRecorderNotify.start();
                Log.e(RecNotiyThread.TAG, "mRecorderNotify.start end");
            }
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Looper.prepare();
            this.mLooper = Looper.myLooper();
            this.mHandler = new Handler(this.mLooper);
            this.mRecorderNotify.mAudioRecorder.setRecordPositionUpdateListener(new myRecordPositionUpdateListener(), this.mHandler);
            this.mRecorderNotify.mAudioRecorder.setErrorNotifyListener(new myErrorNotifyListener(), this.mHandler);
            Looper.loop();
        }

        public void halt() {
            Log.i(TAG, "halt");
            this.mLooper.quit();
        }
    }

    public void setShutterSettings(boolean settings) {
        AppLog.info(TAG, " settings : " + settings);
        this.mShutterSound = settings;
    }
}
