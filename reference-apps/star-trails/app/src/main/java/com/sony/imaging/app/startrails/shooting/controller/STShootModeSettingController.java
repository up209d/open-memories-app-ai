package com.sony.imaging.app.startrails.shooting.controller;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.base.shooting.camera.FocusMagnificationController;
import com.sony.imaging.app.startrails.AppContext;
import com.sony.imaging.app.startrails.shooting.STEESA;
import com.sony.imaging.app.startrails.util.AppLog;
import java.util.List;

/* loaded from: classes.dex */
public class STShootModeSettingController extends AbstractController {
    public static final int STAR_TRAILS_AVI_MODE = 1;
    public static final int STAR_TRAILS_PSEUDO_MODE = 0;
    private static STShootModeSettingController mInstance;
    private STEESA mSTEESA = null;
    private static String TAG = AppLog.getClassName();
    private static int sCaptureState = 1;

    public static STShootModeSettingController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            createInstance();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private static STShootModeSettingController createInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = new STShootModeSettingController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.info(TAG, "Selected Capture Mode  = " + sCaptureState);
        sCaptureState = Integer.parseInt(value);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        AppLog.info(TAG, "Selected Capture Mode  = " + sCaptureState);
        return String.valueOf(sCaptureState);
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
        return true;
    }

    public int getCurrentCaptureState() {
        AppLog.info(TAG, "Selected Capture Mode  = " + sCaptureState);
        return sCaptureState;
    }

    public void initializeSA() {
        FocusMagnificationController.getInstance().setMagnificationRatio(1);
        this.mSTEESA = STEESA.getInstance();
        this.mSTEESA.setPackageName(AppContext.getAppContext().getPackageName());
        this.mSTEESA.intialize();
        this.mSTEESA.startLiveViewEffect();
        this.mSTEESA.updateSaparamread();
    }

    public void releaseSA() {
        if (this.mSTEESA != null) {
            this.mSTEESA.updateSaparamread();
            this.mSTEESA.terminate();
            this.mSTEESA = null;
        }
    }
}
