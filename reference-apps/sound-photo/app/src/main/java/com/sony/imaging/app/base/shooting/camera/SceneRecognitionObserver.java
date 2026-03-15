package com.sony.imaging.app.base.shooting.camera;

import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.scalar.hardware.CameraEx;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class SceneRecognitionObserver {
    private static final boolean DBG = true;
    public static final String DISPLAY_TYPE_BABY = "Baby";
    public static final String DISPLAY_TYPE_BACKLIGHT = "Backlight";
    public static final String DISPLAY_TYPE_BACKLIGTH_PORTRAIT = "Backligth+Portrait";
    private static final String DISPLAY_TYPE_CHECK_STABILIZE = "CHECK_STABILIZE";
    public static final String DISPLAY_TYPE_CLOSEFOCUS = "Close focus";
    private static final String DISPLAY_TYPE_DEFAULT = "None";
    public static final String DISPLAY_TYPE_LANDSCAPE = "Landscape";
    public static final String DISPLAY_TYPE_LOWILLUMINATION = "Low illumination";
    public static final String DISPLAY_TYPE_MACRO = "Macro";
    public static final String DISPLAY_TYPE_MAGNIFIER = "Magnifier";
    public static final String DISPLAY_TYPE_NIGHTVIEW = "NightView";
    public static final String DISPLAY_TYPE_NIGHTVIEW_PORTRAIT = "NightView+Portrait";
    public static final String DISPLAY_TYPE_NONE = "None";
    public static final String DISPLAY_TYPE_PORTRAIT = "Portrait";
    public static final String DISPLAY_TYPE_SPOTLIGHT = "Spot Light";
    public static final String DISPLAY_TYPE_SUNSET = "Sunset";
    public static final String DISPLAY_TYPE_UNDERWATER = "Underwater";
    public static final String FACE_DETECTION_MODE_ADULT = "ADULT";
    public static final String FACE_DETECTION_MODE_CHILD = "CHILD";
    private static final String FACE_DETECTION_MODE_DEFAULT = "OFF";
    public static final String FACE_DETECTION_MODE_OFF = "OFF";
    private static final int SCENERECOGNITION_SUPPORTING_PFAPI_VERSION = 8;
    public static final String SCENE_MODE_AUTO = "AUTO";
    public static final String SCENE_MODE_BACK_LIGHT = "BACK_LIGHT";
    private static final String SCENE_MODE_DEFAULT = "AUTO";
    public static final String SCENE_MODE_LANDSCAPE = "LANDSCAPE";
    public static final String SCENE_MODE_LOWLIGHT = "LOWLIGHT";
    public static final String SCENE_MODE_MACRO = "MACRO";
    public static final String SCENE_MODE_MAGNIFIER = "MAGNIFIER";
    public static final String SCENE_MODE_NIGHT = "NIGHT";
    public static final String SCENE_MODE_SPOTLIGHT = "SPOTLIGHT";
    public static final String SCENE_MODE_SUNRISE_SUNSET = "SUNRISE_SUNSET";
    public static final String SCENE_MODE_UNDERWATER = "UNDERWATER";
    private static final String TAG = "SceneRecognitionObserver";
    public static final String TAG_DISPLAY_TYPE = "DisplayType";
    public static final String TAG_FACE = "Face";
    public static final String TAG_SCENE = "Scene";
    private static SceneRecognitionObserver mObserver = null;
    private OnSceneRecognitionJudgeListener mSceneRecognitionJudgeListener;
    private final Map<String, String> t_DISPLAY_TYPE_FACE_CHILD = new HashMap<String, String>() { // from class: com.sony.imaging.app.base.shooting.camera.SceneRecognitionObserver.1
        {
            put(SceneRecognitionObserver.SCENE_MODE_SPOTLIGHT, SceneRecognitionObserver.DISPLAY_TYPE_SPOTLIGHT);
            put(SceneRecognitionObserver.SCENE_MODE_NIGHT, SceneRecognitionObserver.DISPLAY_TYPE_NIGHTVIEW_PORTRAIT);
            put(SceneRecognitionObserver.SCENE_MODE_BACK_LIGHT, SceneRecognitionObserver.DISPLAY_TYPE_BACKLIGTH_PORTRAIT);
            put(SceneRecognitionObserver.SCENE_MODE_LANDSCAPE, SceneRecognitionObserver.DISPLAY_TYPE_BABY);
            put(SceneRecognitionObserver.SCENE_MODE_LOWLIGHT, SceneRecognitionObserver.DISPLAY_TYPE_BABY);
            put(SceneRecognitionObserver.SCENE_MODE_MACRO, SceneRecognitionObserver.DISPLAY_TYPE_BABY);
            put("AUTO", SceneRecognitionObserver.DISPLAY_TYPE_BABY);
            put(SceneRecognitionObserver.SCENE_MODE_SUNRISE_SUNSET, SceneRecognitionObserver.DISPLAY_TYPE_SUNSET);
            put(SceneRecognitionObserver.SCENE_MODE_MAGNIFIER, SceneRecognitionObserver.DISPLAY_TYPE_MAGNIFIER);
            put(SceneRecognitionObserver.SCENE_MODE_UNDERWATER, SceneRecognitionObserver.DISPLAY_TYPE_UNDERWATER);
        }
    };
    private final Map<String, String> t_DISPLAY_TYPE_FACE_ADULT = new HashMap<String, String>() { // from class: com.sony.imaging.app.base.shooting.camera.SceneRecognitionObserver.2
        {
            put(SceneRecognitionObserver.SCENE_MODE_SPOTLIGHT, SceneRecognitionObserver.DISPLAY_TYPE_SPOTLIGHT);
            put(SceneRecognitionObserver.SCENE_MODE_NIGHT, SceneRecognitionObserver.DISPLAY_TYPE_NIGHTVIEW_PORTRAIT);
            put(SceneRecognitionObserver.SCENE_MODE_BACK_LIGHT, SceneRecognitionObserver.DISPLAY_TYPE_BACKLIGTH_PORTRAIT);
            put(SceneRecognitionObserver.SCENE_MODE_LANDSCAPE, SceneRecognitionObserver.DISPLAY_TYPE_PORTRAIT);
            put(SceneRecognitionObserver.SCENE_MODE_LOWLIGHT, SceneRecognitionObserver.DISPLAY_TYPE_PORTRAIT);
            put(SceneRecognitionObserver.SCENE_MODE_MACRO, SceneRecognitionObserver.DISPLAY_TYPE_PORTRAIT);
            put("AUTO", SceneRecognitionObserver.DISPLAY_TYPE_PORTRAIT);
            put(SceneRecognitionObserver.SCENE_MODE_SUNRISE_SUNSET, SceneRecognitionObserver.DISPLAY_TYPE_SUNSET);
            put(SceneRecognitionObserver.SCENE_MODE_MAGNIFIER, SceneRecognitionObserver.DISPLAY_TYPE_MAGNIFIER);
            put(SceneRecognitionObserver.SCENE_MODE_UNDERWATER, SceneRecognitionObserver.DISPLAY_TYPE_UNDERWATER);
        }
    };
    private final Map<String, String> t_DISPLAY_TYPE_FACE_OFF = new HashMap<String, String>() { // from class: com.sony.imaging.app.base.shooting.camera.SceneRecognitionObserver.3
        {
            put(SceneRecognitionObserver.SCENE_MODE_SPOTLIGHT, SceneRecognitionObserver.DISPLAY_TYPE_SPOTLIGHT);
            put(SceneRecognitionObserver.SCENE_MODE_NIGHT, SceneRecognitionObserver.DISPLAY_TYPE_CHECK_STABILIZE);
            put(SceneRecognitionObserver.SCENE_MODE_BACK_LIGHT, SceneRecognitionObserver.DISPLAY_TYPE_BACKLIGHT);
            put(SceneRecognitionObserver.SCENE_MODE_LANDSCAPE, SceneRecognitionObserver.DISPLAY_TYPE_LANDSCAPE);
            put(SceneRecognitionObserver.SCENE_MODE_LOWLIGHT, SceneRecognitionObserver.DISPLAY_TYPE_LOWILLUMINATION);
            put(SceneRecognitionObserver.SCENE_MODE_MACRO, SceneRecognitionObserver.DISPLAY_TYPE_MACRO);
            put("AUTO", "None");
            put(SceneRecognitionObserver.SCENE_MODE_SUNRISE_SUNSET, SceneRecognitionObserver.DISPLAY_TYPE_SUNSET);
            put(SceneRecognitionObserver.SCENE_MODE_MAGNIFIER, SceneRecognitionObserver.DISPLAY_TYPE_MAGNIFIER);
            put(SceneRecognitionObserver.SCENE_MODE_UNDERWATER, SceneRecognitionObserver.DISPLAY_TYPE_UNDERWATER);
        }
    };
    private Object mLock = new Object();
    private String mScene = "AUTO";
    private String mFace = "OFF";
    private String mDisplayType = "None";

    public static SceneRecognitionObserver getInstance() {
        if (mObserver == null) {
            mObserver = new SceneRecognitionObserver();
        }
        return mObserver;
    }

    protected SceneRecognitionObserver() {
        this.mSceneRecognitionJudgeListener = null;
        if (isSupportedByPF()) {
            this.mSceneRecognitionJudgeListener = new OnSceneRecognitionJudgeListener();
        }
    }

    public Object getValue(String tag) {
        String ret;
        String ret2;
        String ret3;
        if (TAG_SCENE.equals(tag)) {
            if (this.mScene == null) {
                return "AUTO";
            }
            synchronized (this.mLock) {
                ret3 = this.mScene;
            }
            return ret3;
        }
        if (TAG_FACE.equals(tag)) {
            if (this.mFace == null) {
                return "OFF";
            }
            synchronized (this.mLock) {
                ret2 = this.mFace;
            }
            return ret2;
        }
        if (TAG_DISPLAY_TYPE.equals(tag)) {
            if (this.mDisplayType == null) {
                return "None";
            }
            synchronized (this.mLock) {
                ret = this.mDisplayType;
            }
            return ret;
        }
        return null;
    }

    protected void onFirstListenerSet(String tag) {
    }

    protected void onAllListenerRemoved(String tag) {
    }

    public void startListener(CameraEx cameraEx) {
        if (isSupportedByPF()) {
            Log.v(TAG, "startListener()");
            cameraEx.setSceneRecognitionJudgeListener(this.mSceneRecognitionJudgeListener);
            requestNotify();
        }
    }

    public void stopListener(CameraEx cameraEx) {
        if (isSupportedByPF()) {
            Log.v(TAG, "stopListener()");
            cameraEx.setSceneRecognitionJudgeListener((CameraEx.SceneRecognitionJudgeListener) null);
        }
    }

    public boolean isSupportedByPF() {
        return 8 <= CameraSetting.getPfApiVersion();
    }

    protected void requestNotify() {
        Log.v(TAG, "requestNotify()");
        CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.SCENE_RECOGNITION_CHANGED);
    }

    protected String convertPF2App(String pfValue) {
        if (pfValue == null || !isSupportedByPF()) {
            return null;
        }
        if ("auto".equals(pfValue)) {
            return "AUTO";
        }
        if (CreativeStyleController.NIGHT.equals(pfValue)) {
            return SCENE_MODE_NIGHT;
        }
        if (ExposureModeController.BACK_LIGHT.equals(pfValue)) {
            return SCENE_MODE_BACK_LIGHT;
        }
        if ("landscape".equals(pfValue)) {
            return SCENE_MODE_LANDSCAPE;
        }
        if (ExposureModeController.MACRO.equals(pfValue)) {
            return SCENE_MODE_MACRO;
        }
        if ("magnifier".equals(pfValue)) {
            return SCENE_MODE_MAGNIFIER;
        }
        if ("spotlight".equals(pfValue)) {
            return SCENE_MODE_SPOTLIGHT;
        }
        if ("low-light".equals(pfValue)) {
            return SCENE_MODE_LOWLIGHT;
        }
        if (ExposureModeController.UNDERWATER.equals(pfValue)) {
            return SCENE_MODE_UNDERWATER;
        }
        if ("off".equals(pfValue)) {
            return "OFF";
        }
        if (FaceDetectionController.FACE_DETECTION_MODE_ADULT.equals(pfValue)) {
            return FACE_DETECTION_MODE_ADULT;
        }
        if (FaceDetectionController.FACE_DETECTION_MODE_CHILD.equals(pfValue)) {
            return FACE_DETECTION_MODE_CHILD;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class OnSceneRecognitionJudgeListener implements CameraEx.SceneRecognitionJudgeListener {
        protected OnSceneRecognitionJudgeListener() {
        }

        public void onChanged(String scene, String face, CameraEx cameraEx) {
            Log.v(SceneRecognitionObserver.TAG, "onChanged(scene=" + (scene != null ? scene : "null") + ", face=" + (face != null ? face : "null") + LogHelper.MSG_CLOSE_BRACKET);
            SceneRecognitionObserver.this.mScene = SceneRecognitionObserver.this.convertPF2App(scene);
            SceneRecognitionObserver.this.mFace = SceneRecognitionObserver.this.convertPF2App(face);
            synchronized (SceneRecognitionObserver.this.mLock) {
                if ("OFF".equals(SceneRecognitionObserver.this.mFace)) {
                    SceneRecognitionObserver.this.mDisplayType = (String) SceneRecognitionObserver.this.t_DISPLAY_TYPE_FACE_OFF.get(SceneRecognitionObserver.this.mScene);
                } else if (SceneRecognitionObserver.FACE_DETECTION_MODE_ADULT.equals(SceneRecognitionObserver.this.mFace)) {
                    SceneRecognitionObserver.this.mDisplayType = (String) SceneRecognitionObserver.this.t_DISPLAY_TYPE_FACE_ADULT.get(SceneRecognitionObserver.this.mScene);
                } else if (SceneRecognitionObserver.FACE_DETECTION_MODE_CHILD.equals(SceneRecognitionObserver.this.mFace)) {
                    SceneRecognitionObserver.this.mDisplayType = (String) SceneRecognitionObserver.this.t_DISPLAY_TYPE_FACE_CHILD.get(SceneRecognitionObserver.this.mScene);
                }
                if (SceneRecognitionObserver.DISPLAY_TYPE_CHECK_STABILIZE.equals(SceneRecognitionObserver.this.mDisplayType)) {
                    SceneRecognitionObserver.this.mDisplayType = SceneRecognitionObserver.DISPLAY_TYPE_NIGHTVIEW;
                }
            }
            SceneRecognitionObserver.this.requestNotify();
        }
    }
}
