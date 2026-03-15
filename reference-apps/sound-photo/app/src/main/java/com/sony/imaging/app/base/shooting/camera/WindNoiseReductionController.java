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
public class WindNoiseReductionController extends AbstractController {
    private static final String API_NAME = "setMicrophoneWindNoiseReduction";
    private static final String LOG_MSG_GETMICROPHONEWINDNOISEREDUCTION = "getMicrophoneWindNoiseReduction = ";
    private static final String LOG_MSG_GETSUPPORTEDMICROPHONEWINDNOISEREDUCTIONS = "getSupportedMicrophoneWindNoiseReductions = ";
    private static final String LOG_MSG_SETMICROPHONEWINDNOISEREDUCTION = "setMicrophoneWindNoiseReduction = ";
    public static final String MICROPHONE_WIND_NOISE_REDUCION_OFF = "off";
    public static final String MICROPHONE_WIND_NOISE_REDUCION_ON = "on";
    private static final int SUPPORTED_PF_VER = 3;
    private static final String TAG = "WindNoiseReductionController";
    public static final String TAG_AUDIO_WIND_NR = "AudioWindNoiseReduction";
    private static WindNoiseReductionController mInstance;
    private static final String myName;
    private static List<String> sSupportedValued;
    CameraSetting mCamSet = CameraSetting.getInstance();
    private static final StringBuilderThreadLocal sStringBuilder = new StringBuilderThreadLocal();
    private static HashMap<String, String> app2pf = new HashMap<>();
    private static HashMap<String, String> pf2app = new HashMap<>();

    static {
        app2pf.put("off", "off");
        app2pf.put("on", "on");
        pf2app.put("off", "off");
        pf2app.put("on", "on");
        myName = WindNoiseReductionController.class.getSimpleName();
    }

    public static final String getName() {
        return myName;
    }

    public static WindNoiseReductionController getInstance() {
        if (mInstance == null) {
            new WindNoiseReductionController();
        }
        return mInstance;
    }

    private static void setController(WindNoiseReductionController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected WindNoiseReductionController() {
        setController(this);
    }

    public static boolean isSupportedByPF() {
        return 3 <= Environment.getVersionPfAPI();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        String pfValue;
        StringBuilder STRBUILD = sStringBuilder.get();
        if (isSupportedByPF() && isMovieMode() && "AudioWindNoiseReduction".equals(tag) && (pfValue = app2pf.get(value)) != null) {
            AudioSetting audioSetting = AudioSetting.getInstance();
            AudioManager.Parameters params = new AudioManager.Parameters();
            params.setMicrophoneWindNoiseReduction(pfValue);
            audioSetting.setAudioParameters(params);
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_SETMICROPHONEWINDNOISEREDUCTION).append(pfValue);
            Log.i(TAG, STRBUILD.toString());
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        StringBuilder STRBUILD = sStringBuilder.get();
        String appValue = null;
        String pfValue = null;
        if (!isSupportedByPF()) {
            return null;
        }
        if ("AudioWindNoiseReduction".equals(tag)) {
            AudioSetting audioSetting = AudioSetting.getInstance();
            AudioManager.Parameters params = audioSetting.getAudioParameters();
            if (params != null) {
                pfValue = params.getMicrophoneWindNoiseReduction();
                appValue = pf2app.get(pfValue);
            }
        }
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_GETMICROPHONEWINDNOISEREDUCTION).append(pfValue);
        Log.i(TAG, STRBUILD.toString());
        return appValue;
    }

    protected void createSupportedList() {
        AudioManager.Parameters supportedParameters;
        List<String> supported;
        if (sSupportedValued == null && (supportedParameters = AudioSetting.getInstance().getSupportedAudioParameters()) != null && (supported = supportedParameters.getSupportedMicrophoneWindNoiseReductions()) != null) {
            sSupportedValued = new ArrayList();
            for (String value : supported) {
                sSupportedValued.add(pf2app.get(value));
            }
        }
    }

    protected void clearSupportedList() {
        sSupportedValued = null;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraSet() {
        clearSupportedList();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraReopened() {
        clearSupportedList();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        StringBuilder STRBUILD = sStringBuilder.get();
        if (!isSupportedByPF()) {
            return null;
        }
        if ("AudioWindNoiseReduction".equals(tag)) {
            createSupportedList();
        }
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_GETSUPPORTEDMICROPHONEWINDNOISEREDUCTIONS).append(sSupportedValued);
        Log.i(TAG, STRBUILD.toString());
        return sSupportedValued;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        List<String> supporteds;
        ArrayList<String> availables = null;
        if ("AudioWindNoiseReduction".equals(tag) && (supporteds = getSupportedValue(tag)) != null) {
            AvailableInfo.update();
            availables = new ArrayList<>();
            if (isAvailable(tag)) {
                for (String mode : supporteds) {
                    availables.add(mode);
                }
            }
            if (availables.isEmpty()) {
                return null;
            }
        }
        return availables;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        if (!isSupportedByPF() || !isMovieMode()) {
            return false;
        }
        if (!"AudioWindNoiseReduction".equals(tag)) {
            return false;
        }
        AvailableInfo.update();
        boolean available = AvailableInfo.isAvailable(API_NAME, null);
        return available;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public boolean isUnavailableSceneFactor(String tag) {
        if (!isSupportedByPF() || !isMovieMode()) {
            return true;
        }
        if (!"AudioWindNoiseReduction".equals(tag)) {
            return true;
        }
        AvailableInfo.update();
        boolean isInhibited = isUnavailableAPISceneFactor(API_NAME, null);
        return isInhibited;
    }

    protected boolean isMovieMode() {
        if (!Environment.isMovieAPISupported() || 2 != CameraSetting.getInstance().getCurrentMode()) {
            return false;
        }
        return true;
    }
}
