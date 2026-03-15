package com.sony.imaging.app.soundphoto.playback.audiotrack.controller;

import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPUtil;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.media.AudioTrack;

/* loaded from: classes.dex */
public class AudioTrackControl {
    public static final int CURSOL_LEFT = -1;
    public static final int CURSOL_RIGHT = 1;
    public static final int MAX_SOUND_DATA_SIZE = 2097152;
    public static final long NOTIFY_PLAY_INTERVAL = 6;
    public static final int SEPALATE_SOUND_DATA_SIZE = 512000;
    public static final int SPF_FOOT_SIZE = 12;
    public static final int SPF_HEAD_FOOT_SIZE = 28;
    public static final int SPF_HEAD_SIZE = 16;
    private static final String TAG = "AudioTrackControl";
    public static final int WAV_OFFSET_OF_SPF_DATA = 16;
    private AudioTrack m_audioTrack = null;
    private DSP dsp = null;
    private DeviceBuffer deviceBuffer = null;
    private DeviceBuffer dbuf = null;
    AudioTrack.AudioBuffer m_audioBuffer = null;
    private AudioTrackControlListener listener = null;
    int bufferSize = 0;
    int maxBufferSize = 0;
    byte[] soundFromMSL = null;

    public native int closeMSL();

    public native int delSoundSPFData(String str);

    public native int getSoundData(byte[] bArr, int i, int i2, int i3);

    public native int openMSL(String str, byte[] bArr, int i);

    public void intialize() {
        Log.i(TAG, "Initialize");
        this.m_audioTrack = new AudioTrack(48000, 1, 1, 1);
        this.dsp = DSP.createProcessor("sony-di-dsp");
        this.deviceBuffer = this.dsp.directCreateBuffer(756);
        this.dsp.release();
        this.bufferSize = this.deviceBuffer.getSize();
        this.maxBufferSize = this.m_audioTrack.getMaxAudioBufferSize();
        Log.d(TAG, "bufferSize=" + this.bufferSize + "maxBufferSize=" + this.maxBufferSize);
        if (this.bufferSize > this.maxBufferSize) {
            this.bufferSize = this.maxBufferSize;
        }
        Log.i(TAG, "starts subBuf");
        this.soundFromMSL = new byte[MAX_SOUND_DATA_SIZE];
        Log.i(TAG, "ends subBuf");
        this.m_audioBuffer = this.m_audioTrack.createAudioBuffer(this.deviceBuffer, 0, this.bufferSize);
        this.m_audioTrack.setAudioBuffer(this.m_audioBuffer);
        setAudioTrack(this.m_audioTrack);
    }

    public AudioTrack getAudioTrack() {
        return this.m_audioTrack;
    }

    public void setAudioTrack(AudioTrack m_audioTrack) {
        this.m_audioTrack = m_audioTrack;
    }

    public boolean loadAudio(String filePath, AudioTrackControlListener audioTrackControlListener) {
        AppLog.info(TAG, "loadAudio");
        boolean ret = false;
        if (this.dbuf != null) {
            this.dbuf.release();
            this.dbuf = null;
        }
        if (this.dsp != null) {
            this.dsp.release();
            this.dsp = null;
        }
        try {
            int sound_len = openMSL(filePath, this.soundFromMSL, MAX_SOUND_DATA_SIZE);
            if (sound_len <= 0) {
                Log.i(TAG, "getSPFData sound_len : not SPF");
                AppLog.info(TAG, "loadAudio sound_len not SPF " + sound_len);
                AppLog.info(TAG, "loadAudio sound_len" + sound_len);
                ret = false;
            } else {
                Log.i(TAG, "getSPFInfo sound_data : " + sound_len);
                AppLog.info(TAG, "loadAudio sound_len" + sound_len);
                if (2 != this.m_audioTrack.getBufferState()) {
                    Log.d(TAG, "audioTrack.flush()");
                    this.m_audioTrack.flush();
                }
                Log.i(TAG, "Debug SPFdata [Total Size : " + sound_len + "] [sound Offset : 16] [sound data size : " + (sound_len - 28) + "]");
                Log.i(TAG, " sound_len : " + sound_len + ", (sound_len - SPF_HEAD_FOOT_SIZE): " + (sound_len - 28));
                int iMax = (sound_len - 28) / SEPALATE_SOUND_DATA_SIZE;
                int iRest = (sound_len - 28) % SEPALATE_SOUND_DATA_SIZE;
                int soundSize = sound_len - 28;
                Log.i(TAG, " iMax : " + iMax + ", dbuf iRest = " + iRest);
                Log.i(TAG, "Writing Start AudioTrack.");
                try {
                    this.m_audioTrack.write(this.soundFromMSL, 16, soundSize, true);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "Writing End AudioTrack.");
                ret = true;
            }
        } catch (Exception e2) {
            Log.e(TAG, e2.toString());
            e2.printStackTrace();
        } finally {
            closeMSL();
            this.soundFromMSL = null;
            setListener(audioTrackControlListener);
        }
        return ret;
    }

    public boolean deletedAudio(String filePath) {
        Log.i(TAG, "deletedAudio");
        int result = delSoundSPFData(filePath);
        if (result < 0) {
            Log.i(TAG, "delSoundSPFData is failed");
            return false;
        }
        Log.i(TAG, "delSoundSPFData is succeed");
        return true;
    }

    public void playAudio() {
        int playState = this.m_audioTrack.getPlayState();
        if (playState == 1) {
            this.m_audioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() { // from class: com.sony.imaging.app.soundphoto.playback.audiotrack.controller.AudioTrackControl.1
                public void onMarkerReached(long positionInFrames, AudioTrack track) {
                    Log.d(AudioTrackControl.TAG, "onMarkerReached() positionInFrames=" + positionInFrames);
                    Log.d(AudioTrackControl.TAG, "getWriteMarkerPosition=" + AudioTrackControl.this.m_audioTrack.getWriteMarkerPosition());
                    Log.d(AudioTrackControl.TAG, "getReadMarkerPosition=" + AudioTrackControl.this.m_audioTrack.getReadMarkerPosition());
                }

                public void onPeriodicNotification(long positionInFrames, AudioTrack track) {
                    Log.d(AudioTrackControl.TAG, "onPeriodicNotification() positionInFrames=" + positionInFrames);
                    long writePositon = AudioTrackControl.this.m_audioTrack.getWriteMarkerPosition();
                    long readPositon = AudioTrackControl.this.m_audioTrack.getReadMarkerPosition();
                    Log.d(AudioTrackControl.TAG, "getWriteMarkerPosition=" + writePositon);
                    Log.d(AudioTrackControl.TAG, "getReadMarkerPosition=" + readPositon);
                    int currentPlayTime = ((int) readPositon) * 32;
                    AppLog.info(AudioTrackControl.TAG, "currentPlayTime onPeriodicNotification = " + currentPlayTime);
                    AudioTrackControl.this.listener.notifiedPlayTime(currentPlayTime);
                    SPUtil.getInstance().setSoundPlayingState(true);
                    Log.d(AudioTrackControl.TAG, "call notifiedPlayTime : " + currentPlayTime);
                }

                public void onEndPositionReached(AudioTrack track) {
                    Log.d(AudioTrackControl.TAG, "onEndPositionReached()");
                    Log.d(AudioTrackControl.TAG, "getReadMarkerPosition=" + AudioTrackControl.this.m_audioTrack.getReadMarkerPosition());
                    Log.d(AudioTrackControl.TAG, "getWriteMarkerPosition=" + AudioTrackControl.this.m_audioTrack.getWriteMarkerPosition());
                    long readPositon = AudioTrackControl.this.m_audioTrack.getReadMarkerPosition();
                    int currentPlayTime = ((int) readPositon) * 32;
                    Log.d(AudioTrackControl.TAG, "call notifiedPlayTime : " + currentPlayTime);
                    AppLog.info(AudioTrackControl.TAG, "currentPlayTime onPeriodicNotification onEndPositionReached= " + currentPlayTime);
                    AudioTrackControl.this.listener.notifiedPlayTime(currentPlayTime);
                    SPUtil.getInstance().setSoundPlayingState(true);
                    Log.d(AudioTrackControl.TAG, "audioTrack.stop()");
                    AudioTrackControl.this.m_audioTrack.stop();
                    Log.d(AudioTrackControl.TAG, "audioTrack.reloadStaticData()");
                    AudioTrackControl.this.m_audioTrack.reloadStaticData();
                    AudioTrackControl.this.listener.finishedUpdateStatus();
                    SPUtil.getInstance().setSoundPlayingState(false);
                }
            });
            this.m_audioTrack.setErrorNotifyListener(new AudioTrack.OnErrorNotifyListener() { // from class: com.sony.imaging.app.soundphoto.playback.audiotrack.controller.AudioTrackControl.2
                public void onErrorNotification(int error, AudioTrack track) {
                    Log.d(AudioTrackControl.TAG, "onErrorNotification() error=" + error);
                    Log.d(AudioTrackControl.TAG, "getReadMarkerPosition=" + AudioTrackControl.this.m_audioTrack.getReadMarkerPosition());
                    Log.d(AudioTrackControl.TAG, "getWriteMarkerPosition=" + AudioTrackControl.this.m_audioTrack.getWriteMarkerPosition());
                    Log.d(AudioTrackControl.TAG, "audioTrack.stop()");
                    AudioTrackControl.this.m_audioTrack.stop();
                    Log.d(AudioTrackControl.TAG, "audioTrack.reloadStaticData()");
                    AudioTrackControl.this.m_audioTrack.reloadStaticData();
                }
            });
            int frameCount = this.m_audioTrack.getSampleRate() / this.m_audioTrack.getSamplePerFrame();
            Log.d(TAG, "audioTrack.getSampleRate(" + this.m_audioTrack.getSampleRate() + LogHelper.MSG_CLOSE_BRACKET);
            Log.d(TAG, "audioTrack.getFrameSize(" + this.m_audioTrack.getSamplePerFrame() + LogHelper.MSG_CLOSE_BRACKET);
            Log.d(TAG, "audioTrack.setPositionNotificationPeriod(" + frameCount + LogHelper.MSG_CLOSE_BRACKET);
            this.m_audioTrack.setPositionNotificationPeriod(6L);
            Log.d(TAG, "audioTrack.play()");
            this.m_audioTrack.play();
            return;
        }
        if (playState == 2) {
            Log.d(TAG, "audioTrack.play()");
            this.m_audioTrack.play();
        }
    }

    public void restart_audio() {
        stopAudio();
        playAudio();
    }

    public void pauseAudio() {
        if (this.m_audioTrack.getPlayState() == 3) {
            Log.d(TAG, "audioTrack.pause()");
            SPUtil.getInstance().setSoundPlayingState(false);
            this.m_audioTrack.pause();
        }
    }

    public void stopAudio() {
        if (this.m_audioTrack.getPlayState() != 1) {
            Log.d(TAG, "audioTrack.stop()");
            this.m_audioTrack.stop();
        }
    }

    public void stopAudioOnRelease() {
        stopAudio();
        if (this.m_audioTrack != null && 2 != this.m_audioTrack.getBufferState()) {
            Log.d(TAG, "audioTrack.flush()");
            this.m_audioTrack.flush();
        }
    }

    public void moveLeft() {
        Log.d(TAG, "audioTrack.moveLeft()");
    }

    public void moveRight() {
        Log.d(TAG, "audioTrack.moveRight()");
    }

    private void selectContents(int cursol) {
        if (-1 == cursol) {
        }
    }

    public void release() {
        stopAudioOnRelease();
        if (this.m_audioTrack != null) {
            this.m_audioTrack.release();
            this.m_audioTrack = null;
        }
        if (this.m_audioBuffer != null) {
            DeviceBuffer deviceBuffer = this.m_audioBuffer.getDeviceBuffer();
            deviceBuffer.release();
            this.m_audioBuffer.release();
            this.m_audioBuffer = null;
        }
        if (this.dbuf != null) {
            this.dbuf.release();
            this.dbuf = null;
        }
        if (this.dsp != null) {
            this.dsp.release();
            this.dsp = null;
        }
        removeListener();
    }

    static {
        System.loadLibrary("PlayAudioImage");
    }

    public void setListener(AudioTrackControlListener listener) {
        AppLog.enter(TAG, AppLog.getMethodName() + ExposureModeController.SOFT_SNAP + listener);
        this.listener = listener;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void removeListener() {
        AppLog.enter(TAG, AppLog.getMethodName() + ExposureModeController.SOFT_SNAP + this.listener);
        this.listener = null;
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
