package com.sony.imaging.app.base.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.media.AudioManager;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class MicRefLevelController extends AbstractController {
    private static final String API_NAME_GETTER = "getMicRefLevel";
    private static final String API_NAME_SETTER = "setMicRefLevel";
    public static final String LOW = "low";
    public static final String NORMAL = "normal";
    public static final String REF_LEVEL = "ref_level";
    private static final int SUPPORTED_PF_VER = 6;
    private static final String TAG = "MicRefLevelController";
    private static MicRefLevelController mInstance;
    private static List<String> sSupportedValued;
    private static final StringBuilder STRBUILD = new StringBuilder();
    private static final String myName = MicRefLevelController.class.getSimpleName();

    public static final String getName() {
        return myName;
    }

    public static MicRefLevelController getInstance() {
        if (mInstance == null) {
            new MicRefLevelController();
        }
        return mInstance;
    }

    private static void setController(MicRefLevelController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected MicRefLevelController() {
        setController(this);
    }

    public static boolean isSupportedByPF() {
        return 6 <= Environment.getVersionPfAPI();
    }

    protected String appToPf(String value) {
        if (!isSupportedByPF()) {
            return null;
        }
        if ("low".equals(value)) {
            return "low";
        }
        if ("normal".equals(value)) {
            return "normal";
        }
        return null;
    }

    protected String pfToApp(String value) {
        if (!isSupportedByPF()) {
            return null;
        }
        if ("low".equals(value)) {
            return "low";
        }
        if ("normal".equals(value)) {
            return "normal";
        }
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        String value2;
        if (isSupportedByPF() && 2 == ExecutorCreator.getInstance().getRecordingMode() && (value2 = appToPf(value)) != null) {
            CameraSetting cameraSettting = CameraSetting.getInstance();
            AudioManager.Parameters params = new AudioManager.Parameters();
            params.setMicrophoneReferenceLevel(value2);
            cameraSettting.setAudioParameters(params);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        if (!isSupportedByPF()) {
            return null;
        }
        CameraSetting cameraSettting = CameraSetting.getInstance();
        AudioManager.Parameters params = cameraSettting.getAudioParameters();
        if (params == null) {
            return null;
        }
        String value = pfToApp(params.getMicrophoneReferenceLevel());
        return value;
    }

    protected void createSupportedList() {
        AudioManager.Parameters supportedParameters;
        List<String> supported;
        if (sSupportedValued == null && (supportedParameters = CameraSetting.getInstance().getSupportedAudioParameters()) != null && (supported = supportedParameters.getSupportedMicrophoneReferenceLevels()) != null) {
            sSupportedValued = new ArrayList();
            for (String value : supported) {
                sSupportedValued.add(pfToApp(value));
            }
        }
    }

    protected void clearSupportedList() {
        sSupportedValued = null;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraReopened() {
        clearSupportedList();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        if (!isSupportedByPF()) {
            return null;
        }
        createSupportedList();
        return sSupportedValued;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        ArrayList<String> availables = null;
        List<String> supporteds = getSupportedValue(tag);
        if (supporteds != null) {
            AvailableInfo.update();
            availables = new ArrayList<>();
            for (String mode : supporteds) {
                if (isAvailable(tag, mode)) {
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
        if (!isSupportedByPF() || 2 != ExecutorCreator.getInstance().getRecordingMode()) {
            return false;
        }
        AvailableInfo.update();
        boolean available = AvailableInfo.isAvailable(API_NAME_SETTER, null);
        return available;
    }

    protected boolean isAvailable(String tag, String value) {
        boolean available = AvailableInfo.isAvailable(API_NAME_SETTER, value);
        return available;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public boolean isUnavailableSceneFactor(String tag) {
        if (!isSupportedByPF() || 2 != ExecutorCreator.getInstance().getRecordingMode()) {
            return true;
        }
        AvailableInfo.update();
        boolean isInhibited = isUnavailableAPISceneFactor(API_NAME_GETTER, null);
        return isInhibited;
    }
}
