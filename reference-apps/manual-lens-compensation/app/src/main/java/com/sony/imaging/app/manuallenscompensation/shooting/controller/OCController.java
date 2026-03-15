package com.sony.imaging.app.manuallenscompensation.shooting.controller;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class OCController extends AbstractController {
    public static final String BLUE_CHROMATIC_ABERRATION = "chroma-b";
    public static final String BLUE_COLOR_VIGNETTING = "shading-cb";
    public static final String DISTORTION = "distotion";
    public static final String LIGHT_VIGNETTING = "shading-w";
    public static final String OFF = "OFF";
    public static final String ON = "ON";
    public static final String RED_CHROMATIC_ABERRATION = "chroma-r";
    public static final String RED_COLOR_VIGNETTING = "shading-cr";
    public static final String TAG = "MLCController";
    private static OCController mManualLensCorrectionController = new OCController();
    private HashMap<String, List<String>> mSupportedMap = new HashMap<>();

    public static OCController getInstance() {
        return mManualLensCorrectionController;
    }

    public void enable(boolean onOff) {
        CameraSetting cameraSetting = CameraSetting.getInstance();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = cameraSetting.getEmptyParameters();
        ((CameraEx.ParametersModifier) params.second).setLensCorrection(onOff);
        cameraSetting.setParameters(params);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        setLensCorrectionLevel(tag, Integer.getInteger(value).intValue());
    }

    public void setLensCorrectionLevel(String tag, int value) {
        if (tag != null && isSupported(tag)) {
            CameraSetting cameraSetting = CameraSetting.getInstance();
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = cameraSetting.getEmptyParameters();
            ((CameraEx.ParametersModifier) params.second).setLensCorrectionLevel(tag, value);
            cameraSetting.setParameters(params);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        int value = getLensCorrectionLevel(tag);
        return Integer.toString(value);
    }

    public int getLensCorrectionLevel(String tag) {
        if (tag == null || !isSupported(tag)) {
            return 0;
        }
        CameraSetting cameraSetting = CameraSetting.getInstance();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = cameraSetting.getParameters();
        return ((CameraEx.ParametersModifier) params.second).getLensCorrectionLevel(tag);
    }

    public boolean isSupported(String tag) {
        List<String> list = getSupportedValue(null);
        if (list == null) {
            return false;
        }
        boolean ret = list.contains(tag);
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        List<String> list = this.mSupportedMap.get(tag);
        if (list == null) {
            if (tag == null) {
                Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getSupportedParameters();
                list = ((CameraEx.ParametersModifier) params.second).getSupportedLensCorrections();
            } else {
                list = new ArrayList<>();
                int min = getMinLensCorrectionLevel(tag);
                int max = getMaxLensCorrectionLevel(tag);
                for (int i = min; i <= max; i++) {
                    list.add(Integer.toString(i));
                }
            }
            this.mSupportedMap.put(tag, list);
        }
        return list;
    }

    public int getMinLensCorrectionLevel(String tag) {
        if (tag == null || !isSupported(tag)) {
            return 0;
        }
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getSupportedParameters();
        return ((CameraEx.ParametersModifier) params.second).getMinLensCorrectionLevel(tag);
    }

    public int getMaxLensCorrectionLevel(String tag) {
        if (tag == null || !isSupported(tag)) {
            return 0;
        }
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getSupportedParameters();
        return ((CameraEx.ParametersModifier) params.second).getMaxLensCorrectionLevel(tag);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return null;
    }

    public boolean isSupportPictureEffect() {
        String picEffect = PictureEffectController.getInstance().getValue(PictureEffectController.PICTUREEFFECT);
        if (PictureEffectController.MODE_RICH_TONE_MONOCHROME.equalsIgnoreCase(picEffect) || PictureEffectController.MODE_TOY_CAMERA.equalsIgnoreCase(picEffect) || PictureEffectController.MODE_HDR_ART.equalsIgnoreCase(picEffect)) {
            return false;
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return true;
    }
}
