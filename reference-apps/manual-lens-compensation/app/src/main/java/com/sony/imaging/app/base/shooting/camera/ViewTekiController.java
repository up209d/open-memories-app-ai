package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ViewTekiController extends AbstractController {
    private static final String LOG_MSG_GETOVFPREVIEWMODE = "getOVFPreviewMode = ";
    private static final String LOG_MSG_ISSUPPORTEDOVFPREVIEWMODE = "isSupportedOVFPreviewMode = ";
    private static final String LOG_MSG_SETOVFPREVIEWMODE = "setOVFPreviewMode = ";
    private static final String TAG = "ViewTekiController";
    public static final String VIEWTEKI_OFF = "OFF";
    public static final String VIEWTEKI_ON = "ON";
    private static ViewTekiController mInstance;
    private static final String myName = ViewTekiController.class.getSimpleName();
    private CameraSetting mCamSet = CameraSetting.getInstance();

    public static final String getName() {
        return myName;
    }

    public static ViewTekiController getInstance() {
        if (mInstance == null) {
            new ViewTekiController();
        }
        return mInstance;
    }

    private static void setController(ViewTekiController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected ViewTekiController() {
        setController(this);
    }

    public void setValue(String value) {
        setValue(null, value);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        boolean on = false;
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getEmptyParameters();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> supportedParams = this.mCamSet.getSupportedParameters();
        boolean isSupported = ((CameraEx.ParametersModifier) supportedParams.second).isSupportedOVFPreviewMode();
        if (isSupported) {
            if (2 <= CameraSetting.getPfApiVersion()) {
                if (value.equals("ON")) {
                    on = false;
                } else {
                    on = true;
                }
            } else if (value.equals("ON")) {
                on = true;
            } else {
                on = false;
            }
        }
        ((CameraEx.ParametersModifier) p.second).setOVFPreviewMode(on);
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), LOG_MSG_SETOVFPREVIEWMODE).append(on);
        Log.i(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        this.mCamSet.setParameters(p);
        CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.VIEW_TEKI);
    }

    public String getValue() {
        return getValue(null);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String itemId) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> supportedParams = this.mCamSet.getSupportedParameters();
        boolean isSupported = ((CameraEx.ParametersModifier) supportedParams.second).isSupportedOVFPreviewMode();
        if (isSupported) {
            boolean on = ((CameraEx.ParametersModifier) p.second).getOVFPreviewMode();
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            builder.replace(0, builder.length(), LOG_MSG_GETOVFPREVIEWMODE).append(on);
            Log.i(TAG, builder.toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
            if (2 <= CameraSetting.getPfApiVersion()) {
                if (on) {
                    return "OFF";
                }
                return "ON";
            }
            if (on) {
                return "ON";
            }
            return "OFF";
        }
        return "ON";
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getSupportedParameters();
        boolean isSupported = ((CameraEx.ParametersModifier) p.second).isSupportedOVFPreviewMode();
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), LOG_MSG_ISSUPPORTEDOVFPREVIEWMODE).append(isSupported);
        Log.i(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        if (isSupported) {
            List<String> supported = new ArrayList<>();
            supported.add("ON");
            supported.add("OFF");
            return supported;
        }
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        ArrayList<String> availables = new ArrayList<>();
        List<String> supporteds = getSupportedValue(null);
        if (supporteds != null) {
            for (String mode : supporteds) {
                if (AvailableInfo.isAvailable("setOVFPreviewMode", mode)) {
                    availables.add(mode);
                }
            }
        }
        if (availables.isEmpty()) {
            return null;
        }
        return availables;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return false;
    }
}
