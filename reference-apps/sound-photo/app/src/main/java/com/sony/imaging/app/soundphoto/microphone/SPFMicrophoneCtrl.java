package com.sony.imaging.app.soundphoto.microphone;

import android.util.Log;
import com.sony.imaging.app.soundphoto.shooting.audiorecorder.AudioRecorder;
import com.sony.scalar.media.AudioManager;

/* loaded from: classes.dex */
public class SPFMicrophoneCtrl {
    private static final String TAG = "SPFMicrophoneCtrl";
    private static SPFMicrophoneCtrl mInstance = null;
    private AudioManager mAudioManager = null;
    private String mMicType = "UNKNOWN";
    private String mMicRefLv = "UNKNOWN";
    private String mMicRefCh = "UNKNOWN";
    private final AudioManager.SettingChangedListener mAudioSettingChangedListener = new AudioManager.SettingChangedListener() { // from class: com.sony.imaging.app.soundphoto.microphone.SPFMicrophoneCtrl.1
        public void onChanged(int[] types, AudioManager.Parameters param, AudioManager am) {
            Log.i(SPFMicrophoneCtrl.TAG, "onChanged() call");
            for (int in : types) {
                if (in == 1) {
                    Log.i(SPFMicrophoneCtrl.TAG, "types = TYPE_MICROPHONE_REFRENCE_LEVLEL");
                    if (SPFMicrophoneCtrl.this.mAudioManager != null) {
                        SPFMicrophoneCtrl.this.mAudioManager.getParameters();
                        AudioManager.Parameters getParams = SPFMicrophoneCtrl.this.mAudioManager.getParameters();
                        String level = getParams.getMicrophoneReferenceLevel();
                        AudioRecorder.getInstance().setMicReferenceLevel(level);
                        if (level.equals("low")) {
                            SPFMicrophoneCtrl.this.mMicRefLv = "LOW";
                            return;
                        } else {
                            if (level.equals("normal")) {
                                SPFMicrophoneCtrl.this.mMicRefLv = "NORMAL";
                                return;
                            }
                            return;
                        }
                    }
                    return;
                }
            }
        }
    };
    private final AudioManager.OnMicrophoneChangedListener mMicrophoneChangedListener = new AudioManager.OnMicrophoneChangedListener() { // from class: com.sony.imaging.app.soundphoto.microphone.SPFMicrophoneCtrl.2
        public void onChanged(String microphoneType, String microphoneChannel, AudioManager am) {
            Log.i(SPFMicrophoneCtrl.TAG, "onPeriodicNotification() call microphoneType = " + microphoneType + " microphoneChannel = " + microphoneChannel);
            AudioRecorder.getInstance().setMicrophoneType(microphoneType, microphoneChannel);
            if (microphoneType.equals("inner-mic")) {
                SPFMicrophoneCtrl.this.mMicType = "INNER";
                return;
            }
            if (microphoneType.equals("shoe-mic")) {
                SPFMicrophoneCtrl.this.mMicType = "SHOE";
                return;
            }
            if (microphoneType.equals("zoom-mic")) {
                SPFMicrophoneCtrl.this.mMicType = "ZOOM";
                return;
            }
            if (microphoneType.equals("wireless-mic")) {
                SPFMicrophoneCtrl.this.mMicType = "WIRELESS";
                return;
            }
            if (microphoneType.equals("external-mic")) {
                SPFMicrophoneCtrl.this.mMicType = "EXTERNAL";
            } else if (microphoneType.equals("line-in")) {
                Log.i(SPFMicrophoneCtrl.TAG, "value = ");
                SPFMicrophoneCtrl.this.mMicType = "LINE";
            } else {
                Log.i(SPFMicrophoneCtrl.TAG, "value = UNKNOWN");
            }
        }
    };

    public static SPFMicrophoneCtrl getInstance() {
        if (mInstance == null) {
            mInstance = new SPFMicrophoneCtrl();
        }
        return mInstance;
    }

    public void initialMicSetting() {
        this.mAudioManager = new AudioManager();
        Log.v(TAG, "setSettingChangedListener() call");
        this.mAudioManager.setSettingChangedListener(this.mAudioSettingChangedListener);
        int[] types = {1};
        this.mAudioManager.enableSettingChangedTypes(types);
        Log.v(TAG, "setMicrophoneChangedListener() call");
        this.mAudioManager.setMicrophoneChangedListener(this.mMicrophoneChangedListener);
        Log.v(TAG, "setMicrophoneChangedListener() call end");
    }

    public void finishMicSetting() {
        if (this.mAudioManager != null) {
            this.mAudioManager.setSettingChangedListener((AudioManager.SettingChangedListener) null);
            this.mAudioManager.setMicrophoneChangedListener((AudioManager.OnMicrophoneChangedListener) null);
            Log.d(TAG, "AudioManager.release()");
            this.mAudioManager.release();
            this.mAudioManager = null;
        }
    }

    public int getMicrophoneType() {
        int micType;
        Log.i(TAG, "getMicrophoneType() call microphoneType = " + this.mMicType);
        if (this.mMicType.equals("INNER")) {
            micType = 1;
        } else if (this.mMicType.equals("SHOE")) {
            micType = 2;
        } else if (this.mMicType.equals("ZOOM")) {
            micType = 2;
        } else if (this.mMicType.equals("WIRELESS")) {
            micType = 2;
        } else if (this.mMicType.equals("EXTERNAL")) {
            micType = 2;
        } else if (this.mMicType.equals("LINE")) {
            micType = 2;
        } else {
            micType = 0;
        }
        Log.i(TAG, "getMicrophoneType() call end micType = " + micType);
        return micType;
    }
}
