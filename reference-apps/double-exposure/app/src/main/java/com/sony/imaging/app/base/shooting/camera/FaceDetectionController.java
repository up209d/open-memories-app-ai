package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class FaceDetectionController extends AbstractController {
    private static final String API_NAME = "setFaceDetectionMode";
    public static final String FACE_DETECTION_MODE_ADULT = "adult";
    public static final String FACE_DETECTION_MODE_AUTO = "auto";
    public static final String FACE_DETECTION_MODE_CHILD = "child";
    public static final String FACE_DETECTION_MODE_OFF = "off";
    private static final String LOG_MSG_GETFACEDETECTIONMODE = "getFaceDetectionMode = ";
    private static final String LOG_MSG_GETSUPPORTEDFACEDETECTIONMODES = "getSupportedFaceDetectionModes = ";
    private static final String LOG_MSG_SETFACEDETECTIONMODE = "setFaceDetectionMode = ";
    private static final String TAG = "FaceDetectionController";
    public static final String TAG_FACE_DEDTECTIOIN_MODE = "FaceDetection";
    private static FaceDetectionController mInstance;
    private static final String myName;
    CameraSetting mCamSet = CameraSetting.getInstance();
    private static final StringBuilder STRBUILD = new StringBuilder();
    private static HashMap<String, String> app2pf = new HashMap<>();
    private static HashMap<String, String> pf2app = new HashMap<>();

    static {
        app2pf.put("auto", "auto");
        app2pf.put("off", "off");
        pf2app.put("auto", "auto");
        pf2app.put("off", "off");
        myName = FaceDetectionController.class.getSimpleName();
    }

    public static final String getName() {
        return myName;
    }

    public static FaceDetectionController getInstance() {
        if (mInstance == null) {
            new FaceDetectionController();
        }
        return mInstance;
    }

    private static void setController(FaceDetectionController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public FaceDetectionController() {
        setController(this);
    }

    public void setValue(String value) throws IllegalArgumentException {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> emptyParam = this.mCamSet.getEmptyParameters();
        String set = app2pf.get(value);
        if (set == null) {
            throw new IllegalArgumentException();
        }
        ((CameraEx.ParametersModifier) emptyParam.second).setFaceDetectionMode(set);
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_SETFACEDETECTIONMODE).append(set);
        Log.i(TAG, STRBUILD.toString());
        this.mCamSet.setParameters(emptyParam);
    }

    public String getValue() throws IController.NotSupportedException {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
        String pfValue = ((CameraEx.ParametersModifier) p.second).getFaceDetectionMode();
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_GETFACEDETECTIONMODE).append(pfValue);
        Log.i(TAG, STRBUILD.toString());
        String appValue = pf2app.get(pfValue);
        if (appValue == null) {
            throw new IController.NotSupportedException();
        }
        return appValue;
    }

    public List<String> getSupportedValue() {
        List<String> supParams = ((CameraEx.ParametersModifier) this.mCamSet.getSupportedParameters().second).getSupportedFaceDetectionModes();
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_GETSUPPORTEDFACEDETECTIONMODES).append(supParams);
        Log.i(TAG, STRBUILD.toString());
        List<String> ret = new ArrayList<>();
        if (supParams != null) {
            for (String pf : supParams) {
                String app = pf2app.get(pf);
                if (app != null) {
                    ret.add(app);
                }
            }
        }
        if (ret.size() == 0) {
            return null;
        }
        return ret;
    }

    public List<String> getAvailableValue() {
        ArrayList<String> ret = new ArrayList<>();
        List<String> supList = getSupportedValue();
        if (supList != null) {
            AvailableInfo.update();
            for (String s : supList) {
                if (AvailableInfo.isAvailable(API_NAME, app2pf.get(s))) {
                    ret.add(s);
                }
            }
        }
        if (ret.size() == 0) {
            return null;
        }
        return ret;
    }

    public void setFaceFrameRendering(boolean b) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
        ((CameraEx.ParametersModifier) p.second).setFaceFrameRendering(b);
        this.mCamSet.setParameters(p);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        setValue(value);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        return getValue();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return getSupportedValue();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return getAvailableValue();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        AvailableInfo.update();
        return AvailableInfo.isAvailable(API_NAME, null);
    }
}
