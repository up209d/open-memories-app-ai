package com.sony.imaging.app.digitalfilter.shooting;

import android.hardware.Camera;
import com.sony.imaging.app.base.shooting.camera.DROAutoHDRController;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FaceDetectionController;
import com.sony.imaging.app.base.shooting.camera.ViewTekiController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.imaging.app.base.shooting.camera.executor.IProcess;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFExposureModeController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFIntervalController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFLongExposureNRController;
import com.sony.imaging.app.digitalfilter.shooting.camera.TouchLessShutterController;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFExecutorCreator extends ExecutorCreator {
    private static final String TAG = AppLog.getClassName();
    private static int mCounter = 0;
    private GFCompositProcess mCompositProcess = new GFCompositProcess();

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected IProcess getProcess(Camera camera, CameraEx cameraEx) {
        setDefaultCameraSettings();
        return this.mCompositProcess;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected IAdapter getAdapter(String name) {
        Camera camera = this.mCameraEx.getNormalCamera();
        return new GFAdapterImpl(camera, this.mCameraEx);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isSpinal() {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isSpecial() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isImmediatelyEEStart() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isInheritSetting() {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isSpinalZoomSetting() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isInheritDigitalZoomSetting() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public boolean isEnableDigitalZoom() {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public boolean isInheritRawRecMode() {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public boolean isFocusHoldEnabled() {
        return false;
    }

    public static boolean isEnableShootingFromPlayback() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected void settingBeforeInitialized() {
        String expMode = ExposureModeController.getInstance().getValue(null);
        ExposureModeController emc = ExposureModeController.getInstance();
        if (!emc.isValidValue(expMode)) {
            if (isCameraModeStill()) {
                emc.setValue(ExposureModeController.EXPOSURE_MODE, ExposureModeController.PROGRAM_AUTO_MODE);
                return;
            }
            List<String> supported = emc.getSupportedValue(ExposureModeController.EXPOSURE_MODE);
            if (supported.contains("movie")) {
                emc.setValue(ExposureModeController.EXPOSURE_MODE, "movie");
            } else if (supported.contains(ExposureModeController.MOVIE_PROGRAM_AUTO_MODE)) {
                emc.setValue(ExposureModeController.EXPOSURE_MODE, ExposureModeController.MOVIE_PROGRAM_AUTO_MODE);
            } else if (supported.contains(ExposureModeController.MOVIE_AUTO)) {
                emc.setValue(ExposureModeController.EXPOSURE_MODE, ExposureModeController.MOVIE_AUTO);
            }
        }
    }

    private void setDefaultCameraSettings() {
        ViewTekiController.getInstance().setValue("ON");
        DriveModeController.getInstance().setValue("drivemode", DriveModeController.SINGLE);
        FaceDetectionController.getInstance().setValue(FaceDetectionController.TAG_FACE_DEDTECTIOIN_MODE, "off");
        setExposureMode();
        setDRO();
        setLongExposureNR();
        TouchLessShutterController.ExposingByTouchLessShutter = false;
    }

    private void setExposureMode() {
        if (ModeDialDetector.hasModeDial() && !GFExposureModeController.getInstance().isValidExpoMode(ExposureModeController.getInstance().getValue(null))) {
            ExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, "Aperture");
        }
    }

    private void setDRO() {
        DROAutoHDRController controller = DROAutoHDRController.getInstance();
        String value = controller.getValue();
        ArrayList<String> hdrList = (ArrayList) controller.getSupportedValue(DROAutoHDRController.MENU_ITEM_ID_HDR);
        if (hdrList != null && hdrList.contains(value)) {
            controller.setValue(DROAutoHDRController.MENU_ITEM_ID_DROHDR, DROAutoHDRController.MENU_ITEM_ID_DRO);
            controller.setValue(DROAutoHDRController.MENU_ITEM_ID_DRO, DROAutoHDRController.MODE_DRO_AUTO);
        }
    }

    private void setLongExposureNR() {
        if (GFLongExposureNRController.getInstance().isAvailable(GFLongExposureNRController.LONG_EXPOSURE_NR)) {
            String value = GFLongExposureNRController.getInstance().getValue(GFLongExposureNRController.LONG_EXPOSURE_NR);
            GFLongExposureNRController.getInstance().setValue(GFLongExposureNRController.LONG_EXPOSURE_NR, value);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public boolean isAElockedOnAutoFocus() {
        boolean isInterval = GFIntervalController.INTERVAL_ON.equalsIgnoreCase(GFIntervalController.getInstance().getValue(GFIntervalController.INTERVAL_MODE));
        if (!isInterval) {
            return true;
        }
        String ae = GFIntervalController.getInstance().getValue(GFIntervalController.INTERVAL_MODE);
        return "ae-lock".equalsIgnoreCase(ae);
    }

    public void incrementShootingNumber() {
        mCounter++;
    }

    public static boolean isEnableTouchlessSetting() {
        return true;
    }
}
