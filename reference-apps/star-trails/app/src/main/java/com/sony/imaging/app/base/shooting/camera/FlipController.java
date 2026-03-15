package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.common.AudioSetting;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.media.AudioManager;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class FlipController extends AbstractController {
    private static final String API_NAME = "setFlip";
    private static final int API_VER_SUPPORTS_FLIP = 5;
    public static final String FLIP = "FLIP";
    private static final String LOG_ERROR_NOTFOUND = "Error NotFound Category";
    public static final String OFF = "Off";
    public static final String ON = "On";
    private static final String TAG = "FlipController";
    private static FlipController mInstance;
    private static final String myName = FlipController.class.getSimpleName();
    protected static final String[] tags = {FlipNotificationManager.FLIP_CHANGE};
    private AudioManager.Parameters mAudioParameters;
    private FlipControllerListener mFlipControllerListener;
    public final String FLIP_SOUND_LEFT_RIGHT = "flip-left-right";
    public final String FLIP_IMAGE_UP_DOWN = "flip-up-down";
    public final String FLIP_IMAGE_LEFT_RIGHT = "flip-left-right";
    private CameraSetting mCamSet = CameraSetting.getInstance();

    protected FlipController() {
        setController(this);
    }

    private static void setController(FlipController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    public static FlipController getInstance() {
        if (mInstance == null) {
            new FlipController();
        }
        return mInstance;
    }

    public static final String getName() {
        return myName;
    }

    public static boolean isSupportedByPF() {
        return 5 <= Environment.getVersionPfAPI();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        if (tag.equals(FLIP)) {
            AudioSetting audioSetting = AudioSetting.getInstance();
            if (isSupportedByPF()) {
                if (value.equals("On")) {
                    AudioManager.Parameters params = new AudioManager.Parameters();
                    params.setFlip("flip-left-right", true);
                    audioSetting.setAudioParameters(params);
                    Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
                    ((CameraEx.ParametersModifier) p.second).setFlip("flip-left-right", true);
                    ((CameraEx.ParametersModifier) p.second).setFlip("flip-up-down", true);
                    this.mCamSet.setParameters(p);
                    FlipNotificationManager.getInstance().requestNotify(FlipNotificationManager.FLIP_CHANGE);
                    return;
                }
                if (value.equals("Off")) {
                    AudioManager.Parameters params2 = new AudioManager.Parameters();
                    params2.setFlip("flip-left-right", false);
                    audioSetting.setAudioParameters(params2);
                    Pair<Camera.Parameters, CameraEx.ParametersModifier> p2 = this.mCamSet.getEmptyParameters();
                    ((CameraEx.ParametersModifier) p2.second).setFlip("flip-left-right", false);
                    ((CameraEx.ParametersModifier) p2.second).setFlip("flip-up-down", false);
                    this.mCamSet.setParameters(p2);
                    FlipNotificationManager.getInstance().requestNotify(FlipNotificationManager.FLIP_CHANGE);
                    return;
                }
                return;
            }
            return;
        }
        Log.v(TAG, LOG_ERROR_NOTFOUND);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        String ret = null;
        boolean sound = false;
        if (FLIP.equals(tag)) {
            if (isSupportedByPF()) {
                AudioSetting audioSetting = AudioSetting.getInstance();
                AudioManager.Parameters params = audioSetting.getAudioParameters();
                if (params != null) {
                    sound = params.isFlip("flip-left-right");
                }
                Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
                boolean vertical = ((CameraEx.ParametersModifier) p.second).isFlip("flip-up-down");
                boolean horizontal = ((CameraEx.ParametersModifier) p.second).isFlip("flip-left-right");
                Log.v(TAG, "getValue : sound = " + sound + ", vertical = " + vertical + ", horizontal = " + horizontal);
                if (sound && vertical && horizontal) {
                    ret = "On";
                } else {
                    ret = "Off";
                }
            }
            return ret;
        }
        Log.e(TAG, LOG_ERROR_NOTFOUND);
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getSupportedParameters();
        AudioSetting audioSetting = AudioSetting.getInstance();
        this.mAudioParameters = audioSetting.getSupportedAudioParameters();
        List<String> ret = new ArrayList<>();
        boolean isSuppored = false;
        if (FLIP.equals(tag)) {
            if (isSupportedByPF()) {
                List<String> audioSupportList = this.mAudioParameters.getSupportedFlipModes();
                List<String> imageSupportList = ((CameraEx.ParametersModifier) p.second).getSupportedFlipModes();
                if (audioSupportList == null || imageSupportList == null) {
                    Log.v(TAG, "Flip not supported");
                } else if (audioSupportList.contains("flip-left-right") && imageSupportList.contains("flip-left-right") && imageSupportList.contains("flip-up-down")) {
                    isSuppored = true;
                }
            }
        } else {
            Log.v(TAG, LOG_ERROR_NOTFOUND);
        }
        if (isSuppored) {
            ret.add("On");
            ret.add("Off");
            return ret;
        }
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        ArrayList<String> availables = new ArrayList<>();
        if (FLIP.equals(tag)) {
            List<String> supporteds = getSupportedValue(tag);
            if (supporteds != null) {
                for (String support : supporteds) {
                    availables.add(support);
                }
            }
        } else {
            Log.v(TAG, LOG_ERROR_NOTFOUND);
        }
        return availables;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        if (!isSupportedByPF()) {
            return false;
        }
        AvailableInfo.update();
        boolean isAudio = AvailableInfo.isAvailable(API_NAME, "flip-left-right");
        boolean isImageVertical = AvailableInfo.isAvailable(API_NAME, "flip-up-down");
        boolean isImageHorizontal = AvailableInfo.isAvailable(API_NAME, "flip-left-right");
        if (isAudio && isImageVertical && isImageHorizontal) {
            return true;
        }
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        Log.i(TAG, "onGetInitParameters");
        updateDetailValue(params);
        if (this.mFlipControllerListener == null) {
            this.mFlipControllerListener = new FlipControllerListener();
            FlipNotificationManager.getInstance().setNotificationListener(this.mFlipControllerListener);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetTermParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        Log.i(TAG, "onGetTermParameters");
        FlipNotificationManager.getInstance().removeNotificationListener(this.mFlipControllerListener);
        this.mFlipControllerListener = null;
    }

    /* loaded from: classes.dex */
    protected class FlipControllerListener implements NotificationListener {
        protected FlipControllerListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return FlipController.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            FlipController.this.updateDetailValue();
        }
    }

    protected synchronized void updateDetailValue() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> getParam = this.mCamSet.getParameters();
        if (updateDetailValue(getParam)) {
            AudioSetting audioSetting = AudioSetting.getInstance();
            AudioManager.Parameters params = new AudioManager.Parameters();
            params.setFlip("flip-left-right", true);
            audioSetting.setAudioParameters(params);
        }
    }

    protected boolean updateDetailValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> getParam) {
        if (!isSupportedByPF()) {
            return false;
        }
        boolean isImageVertical = ((CameraEx.ParametersModifier) getParam.second).isFlip("flip-up-down");
        boolean isImageHorizontal = ((CameraEx.ParametersModifier) getParam.second).isFlip("flip-left-right");
        if (!isImageHorizontal || !isImageVertical) {
            return false;
        }
        return true;
    }
}
