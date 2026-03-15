package com.sony.imaging.app.lightgraffiti.shooting.controller;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.hardware.CameraEx;
import java.util.List;

/* loaded from: classes.dex */
public class LGRemoConController extends AbstractController {
    public static final String C_REMOCON_MODE_OFF = "remocon_off";
    public static final String C_REMOCON_MODE_ON = "remocon_on";
    private static final String TAG = LGRemoConController.class.getSimpleName();
    private static LGRemoConController mInstance;
    private boolean alreadySetDiademRemoconModeflag = false;
    private boolean mDiademRemoconMode;

    public static final String getName() {
        return TAG;
    }

    public static LGRemoConController getInstance() {
        if (mInstance == null) {
            new LGRemoConController();
        }
        return mInstance;
    }

    private static void setController(LGRemoConController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected LGRemoConController() {
        setController(this);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraSet() {
        this.alreadySetDiademRemoconModeflag = false;
        CameraEx.ParametersModifier currentParams = (CameraEx.ParametersModifier) CameraSetting.getInstance().getParameters().second;
        boolean TempRemote = currentParams.getRemoteControlMode();
        setDiademRemoconMode(TempRemote);
        Log.i("LGRemoConController onCameraSet", "get Remocom mode from Diadem:" + TempRemote);
    }

    public boolean getDiademRemoconMode() {
        Log.i(TAG, "getDiademRemoconMode:" + this.mDiademRemoconMode);
        return this.mDiademRemoconMode;
    }

    public void setDiademRemoconMode(boolean remmode) {
        Log.i(TAG, "setDiademRemoconMode");
        if (!this.alreadySetDiademRemoconModeflag) {
            Log.i(TAG, "setDiademRemoconMode:" + remmode);
            this.mDiademRemoconMode = remmode;
            this.alreadySetDiademRemoconModeflag = true;
        }
    }

    public void setValue(boolean value) {
        Log.i(TAG, "controller setValue run:" + value);
        Pair<Camera.Parameters, CameraEx.ParametersModifier> setParams = CameraSetting.getInstance().getEmptyParameters();
        ((CameraEx.ParametersModifier) setParams.second).setRemoteControlMode(value);
        CameraSetting.getInstance().setParametersDirect(setParams);
    }

    public void resetRemoconModeToDiademValue() {
        Log.i(TAG, "controller resetRemoconModeToDiademValue run:" + getDiademRemoconMode());
        if (Environment.getVersionOfHW() < 2) {
            setValue(false);
        } else {
            setValue(getDiademRemoconMode());
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        Log.e(TAG, "WARN:controller dummy setValue run:" + value + "   RemortControl Mode is not set parametor. Are you as intended this?");
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return false;
    }
}
