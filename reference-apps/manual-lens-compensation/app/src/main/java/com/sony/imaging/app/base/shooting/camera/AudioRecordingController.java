package com.sony.imaging.app.base.shooting.camera;

import android.util.Log;
import com.sony.imaging.app.base.common.AudioSetting;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.media.AudioManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class AudioRecordingController extends AbstractController {
    private static final String API_NAME_GETTER = "getMicrophoneEnable";
    private static final String API_NAME_SETTER = "setMicrophoneEnable";
    public static final String AUDIORECORDING_OFF = "OFF";
    public static final String AUDIORECORDING_ON = "ON";
    public static final String AUDIO_RECORDING = "AudioRecording";
    private static final int SETTING_CHANGED_LISTENER_SUPPORTED_PF_VER = 9;
    private static final int SUPPORTED_PF_VER = 3;
    private static AudioRecordingController mInstance;
    private static final String TAG = AudioRecordingController.class.getSimpleName();
    private static final StringBuilderThreadLocal STRBUILD = new StringBuilderThreadLocal();
    private static final String myName = TAG;
    protected static HashMap<String, Boolean> app2pf = null;
    protected static HashMap<Boolean, String> pf2app = null;

    protected static void setDictionary() {
        app2pf = new HashMap<>();
        pf2app = new HashMap<>();
        app2pf.put("ON", true);
        app2pf.put("OFF", false);
        pf2app.put(true, "ON");
        pf2app.put(false, "OFF");
    }

    public static final String getName() {
        return myName;
    }

    public static AudioRecordingController getInstance() {
        if (mInstance == null) {
            mInstance = new AudioRecordingController();
        }
        return mInstance;
    }

    private static void setController(AudioRecordingController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected AudioRecordingController() {
        setController(this);
        setDictionary();
    }

    public static boolean isSupportedByPF() {
        return 3 <= Environment.getVersionPfAPI();
    }

    public static boolean isSupported() {
        return isSupportedByPF() && 2 == CameraSetting.getInstance().getCurrentMode();
    }

    public static boolean isSupportedSettingChangedListener() {
        return 9 <= Environment.getVersionPfAPI();
    }

    public static boolean isUnSupportedSettingChangedListener() {
        return isSupported() && Environment.getVersionPfAPI() < 9;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        if (isSupported() && value != null && AUDIO_RECORDING.equals(tag)) {
            AudioSetting audioSetting = AudioSetting.getInstance();
            AudioManager.Parameters params = new AudioManager.Parameters();
            StringBuilder builder = STRBUILD.get();
            builder.replace(0, builder.length(), "setValue:").append(tag).append(", value:").append(value);
            Log.i(TAG, builder.toString());
            if ("OFF".equals(value)) {
                params.setMicrophoneEnable(false);
            } else {
                params.setMicrophoneEnable(true);
            }
            audioSetting.setAudioParameters(params);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        AudioManager.Parameters params;
        String value = null;
        if (isSupported() && AUDIO_RECORDING.equals(tag)) {
            AudioSetting audioSetting = AudioSetting.getInstance();
            List<String> supportedList = getSupportedValue(tag);
            if (supportedList != null && (params = audioSetting.getAudioParameters()) != null) {
                boolean isEnabled = params.getMicrophoneEnable();
                value = isEnabled ? "ON" : "OFF";
            }
        }
        StringBuilder builder = STRBUILD.get();
        builder.replace(0, builder.length(), "getValue:").append(tag).append(", value:").append(value);
        Log.i(TAG, builder.toString());
        return value;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        if (!isSupported() || !AUDIO_RECORDING.equals(tag)) {
            return null;
        }
        boolean supportedMicrophoneEnable = AudioSetting.getInstance().getSupportedAudioParameters().isSupportedMicrophoneEnable();
        List<String> supportedList = new ArrayList<>();
        if (supportedMicrophoneEnable) {
            supportedList.add("ON");
            supportedList.add("OFF");
        } else {
            supportedList = null;
        }
        StringBuilder builder = STRBUILD.get();
        builder.replace(0, builder.length(), "getSupportedValue:").append(tag).append(", value:").append(supportedList);
        Log.i(TAG, builder.toString());
        return supportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        if (!isSupported() || !AUDIO_RECORDING.equals(tag)) {
            return null;
        }
        List<String> availableList = new ArrayList<>();
        List<String> supporteds = getSupportedValue(tag);
        if (supporteds != null) {
            AvailableInfo.update();
            for (String supported : supporteds) {
                if (AvailableInfo.isAvailable(API_NAME_SETTER, app2pf.get(supported))) {
                    availableList.add(supported);
                }
            }
        }
        if (availableList.isEmpty()) {
            availableList = null;
        }
        StringBuilder builder = STRBUILD.get();
        builder.replace(0, builder.length(), "getAvailableValue:").append(tag).append(", value:").append(availableList);
        Log.i(TAG, builder.toString());
        return availableList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        if (!isSupported() || !AUDIO_RECORDING.equals(tag)) {
            return false;
        }
        AvailableInfo.update();
        boolean available = AvailableInfo.isAvailable(API_NAME_SETTER, null);
        return available;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public boolean isUnavailableSceneFactor(String tag) {
        if (!isSupported() || !AUDIO_RECORDING.equals(tag)) {
            return true;
        }
        AvailableInfo.update();
        boolean isInhibited = isUnavailableAPISceneFactor(API_NAME_GETTER, null);
        return isInhibited;
    }
}
