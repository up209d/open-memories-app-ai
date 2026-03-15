package com.sony.imaging.app.base.beep;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.common.AudioSetting;
import com.sony.imaging.app.base.shooting.camera.AbstractController2;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.media.AudioManager;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class BeepController extends AbstractController2<String> {
    private static final String API_NAME_SETTER = "setOutputBeep";
    public static final String BEEP_OFF = "Off";
    public static final String BEEP_ON = "On";
    public static final String BEEP_SHUTTER = "Shutter";
    public static final String BEEP_SILENT = "Silent";
    private static final boolean DBG = false;
    private static final int OUTPUT_BEEP_SUPPORT_PFAPI_VERSION = 7;
    private static final String TAG = "BeepController";
    public static final String TAG_BEEP = "Beep";
    private static BeepController mInstance = null;
    private static final String myName = BeepController.class.getSimpleName();

    protected BeepController() {
    }

    public static BeepController getInstance() {
        if (mInstance == null) {
            mInstance = new BeepController();
        }
        return mInstance;
    }

    protected boolean isSupportedByPF() {
        return 7 <= Environment.getVersionPfAPI();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        if (!isSupportedByPF()) {
            return false;
        }
        AvailableInfo.update();
        return AvailableInfo.isAvailable(API_NAME_SETTER, null);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController2, com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        AudioManager.Parameters supportedParameters;
        List<String> pfSupported;
        if (!isSupportedByPF() || (supportedParameters = AudioSetting.getInstance().getSupportedAudioParameters()) == null || (pfSupported = supportedParameters.getSupportedOutputBeep()) == null) {
            return null;
        }
        List<String> supporteds = new ArrayList<>();
        for (String value : pfSupported) {
            supporteds.add(convertPF2App(value));
        }
        if (supporteds.isEmpty()) {
            return null;
        }
        return supporteds;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController2, com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        List<String> supporteds;
        if (!isSupportedByPF() || (supporteds = getSupportedValue(tag)) == null) {
            return null;
        }
        AvailableInfo.update();
        ArrayList<String> availables = new ArrayList<>();
        for (String mode : supporteds) {
            if (AvailableInfo.isAvailable(API_NAME_SETTER, mode)) {
                availables.add(mode);
            }
        }
        if (availables.isEmpty()) {
            return null;
        }
        return availables;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController2
    public void setValueToPF(String tag, String value) {
        if (isSupportedByPF() && value != null) {
            AudioSetting audioSettting = AudioSetting.getInstance();
            AudioManager.Parameters params = new AudioManager.Parameters();
            params.setOutputBeep(value);
            audioSettting.setAudioParameters(params);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController2
    public String getPFValue(String tag) {
        AudioManager.Parameters params;
        if (!isSupportedByPF() || (params = AudioSetting.getInstance().getAudioParameters()) == null) {
            return null;
        }
        String ret = params.getOutputBeep();
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController2
    public String convertPF2App(String pfValue) {
        if (!isSupportedByPF()) {
            return null;
        }
        if ("on".equals(pfValue)) {
            return "On";
        }
        if ("off".equals(pfValue)) {
            return "Off";
        }
        if ("shutter".equals(pfValue)) {
            return "Shutter";
        }
        if ("silent".equals(pfValue)) {
            return BEEP_SILENT;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController2
    public String convertApp2PF(String appValue) {
        if (!isSupportedByPF()) {
            return null;
        }
        if ("On".equals(appValue)) {
            return "on";
        }
        if ("Off".equals(appValue)) {
            return "off";
        }
        if ("Shutter".equals(appValue)) {
            return "shutter";
        }
        if (BEEP_SILENT.equals(appValue)) {
            return "silent";
        }
        return null;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetTermParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
    }

    public static final String getName() {
        return myName;
    }
}
