package com.sony.imaging.app.manuallenscompensation.shooting.controller;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.AntiHandBlurController;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;
import com.sony.imaging.app.manuallenscompensation.database.LensCompensationParameter;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class OCAntiHandBlurController extends AntiHandBlurController {
    private static final int CAUTION_GRP_ID_MOVIE_STEADY_SHOT_INVALID_GUIDE = 1;
    private static final int CAUTION_GRP_ID_STILL_IMAGE_STEADY_SHOT_INVALID_GUIDE = 0;
    private static final int DEFAULT_SETTIGN_TYPE = 0;
    public static final String MENU_TAG = "AntiHandBlur";
    private static final String TAG = AppLog.getClassName();
    private static OCAntiHandBlurController sInstance = null;
    private CameraSetting mCamSet;
    private final String sisAvaliableStringTAG = "tag in isAvailable is...";
    private final String sisAvaliableSpecialStringTAG = "special Check for tag in isAvailable is...";
    private final String sisAvaliableNormalStringTAG = "Other than Standard Check for tag in isAvailable is...";
    private final String sisAvaliableModeStringTAG = " and mode is....";

    private OCAntiHandBlurController() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCamSet = CameraSetting.getInstance();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static OCAntiHandBlurController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new OCAntiHandBlurController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    private void updateStillParameters(LensCompensationParameter profileParam) {
        AppLog.enter(TAG, AppLog.getMethodName());
        Pair<Camera.Parameters, CameraEx.ParametersModifier> pair = CameraSetting.getInstance().getEmptyParameters();
        if (true == OCUtil.isSupportedByPF()) {
            ((CameraEx.ParametersModifier) pair.second).setAntiHandBlurInfo("from-lens");
            if (profileParam != null && profileParam.mFocalLength != null && profileParam.mFocalLength.length() > 0) {
                int focalLength = OCFocalLengthController.getInstance().getCameraSupportedFocalLenght(Integer.parseInt(profileParam.mFocalLength));
                ((CameraEx.ParametersModifier) pair.second).setAntiHandBlurFocalLength(focalLength);
            } else {
                ((CameraEx.ParametersModifier) pair.second).setAntiHandBlurFocalLength(8);
            }
        }
        CameraSetting.getInstance().setParameters(pair);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void updateMovieParameters(LensCompensationParameter profileParam) {
        AppLog.enter(TAG, AppLog.getMethodName());
        Pair<Camera.Parameters, CameraEx.ParametersModifier> pair = CameraSetting.getInstance().getEmptyParameters();
        if (true == OCUtil.isSupportedByPF()) {
            ((CameraEx.ParametersModifier) pair.second).setMovieAntiHandBlurInfo("from-lens");
            if (profileParam != null && profileParam.mFocalLength != null && profileParam.mFocalLength.length() > 0) {
                int focalLength = OCFocalLengthController.getInstance().getCameraSupportedFocalLenght(Integer.parseInt(profileParam.mFocalLength));
                ((CameraEx.ParametersModifier) pair.second).setMovieAntiHandBlurFocalLength(focalLength);
            } else {
                ((CameraEx.ParametersModifier) pair.second).setMovieAntiHandBlurFocalLength(8);
            }
        }
        CameraSetting.getInstance().setParameters(pair);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraSet() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onCameraSet();
        initSettingListener();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onGetInitParameters(params);
        initSettingListener();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void initSettingListener() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (1 <= CameraSetting.getPfApiVersion()) {
            int[] types = {28, 29};
            this.mCamSet.getCamera().enableSettingChangedTypes(types);
            this.mCamSet.getCamera().setSettingChangedListener(this.mCamSet);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraRemoving() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onCameraRemoving();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetTermParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onGetTermParameters(params);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AntiHandBlurController, com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (isMovieMode()) {
            super.setValue("movie", value);
            if (value.equalsIgnoreCase("off")) {
                super.setValue(AntiHandBlurController.STILL, "off");
            } else {
                super.setValue(AntiHandBlurController.STILL, AntiHandBlurController.SHOOT);
            }
        } else {
            super.setValue(AntiHandBlurController.STILL, value);
            if (value.equalsIgnoreCase("off")) {
                super.setValue("movie", "off");
            } else {
                super.setValue("movie", "standard");
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AntiHandBlurController, com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        String tag2;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (isMovieMode()) {
            tag2 = "movie";
        } else {
            tag2 = AntiHandBlurController.STILL;
        }
        String getValue = super.getValue(tag2);
        if ("standard".equals(getValue) && CameraSetting.getPfApiVersion() >= 2) {
            int settingType = ScalarProperties.getInt("ui.steady.shot");
            if (settingType == 0) {
                getValue = AntiHandBlurController.MOVIE_ACTIVE;
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return getValue;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AntiHandBlurController, com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        List<String> supportedList;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (isMovieMode()) {
            supportedList = super.getSupportedValue("movie");
        } else {
            supportedList = super.getSupportedValue(AntiHandBlurController.STILL);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return supportedList;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AntiHandBlurController, com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        String tag2;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (isMovieMode()) {
            tag2 = "movie";
        } else {
            tag2 = AntiHandBlurController.STILL;
        }
        ArrayList<String> availables = new ArrayList<>();
        List<String> supporteds = getSupportedValue(tag2);
        if (supporteds != null) {
            for (String mode : supporteds) {
                if (isAvailable(tag2, mode)) {
                    availables.add(mode);
                }
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        if (availables.isEmpty()) {
            return null;
        }
        return availables;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AntiHandBlurController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean available;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (isMovieMode()) {
            available = super.isAvailable("movie");
        } else {
            available = super.isAvailable(AntiHandBlurController.STILL);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return available;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AntiHandBlurController
    protected boolean isAvailable(String tag, String mode) {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean available = false;
        if (AntiHandBlurController.STILL.equals(tag)) {
            AppLog.trace(TAG, "tag in isAvailable is...still");
            available = super.isAvailable(tag, mode);
        } else if ("movie".equals(tag)) {
            AppLog.trace(TAG, "tag in isAvailable is...movie");
            if ("standard".equals(mode)) {
                AppLog.trace(TAG, "special Check for tag in isAvailable is...movie and mode is....standard");
                available = true;
            } else {
                AppLog.trace(TAG, "Other than Standard Check for tag in isAvailable is...movie and mode is...." + mode);
                available = super.isAvailable(tag, mode);
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return available;
    }

    protected boolean isMovieMode() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 2 == ExecutorCreator.getInstance().getRecordingMode();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int ret = super.getCautionIndex(itemId);
        if ("AntiHandBlur".equals(itemId)) {
            if (isMovieMode()) {
                ret = 1;
            } else {
                ret = 0;
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return ret;
    }

    public int getCautionID() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int cautionID = 33000;
        boolean isMovieMode = isMovieMode();
        AppLog.info(TAG, AppLog.getMethodName() + isMovieMode);
        if (isMovieMode) {
            cautionID = 33004;
        }
        AppLog.info(TAG, "getCautionIndex = " + cautionID);
        AppLog.exit(TAG, AppLog.getMethodName());
        return cautionID;
    }

    public void updateSteadyShot(LensCompensationParameter params) {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean isMovieMode = isMovieMode();
        AppLog.info(TAG, AppLog.getMethodName() + isMovieMode);
        if (isMovieMode) {
            updateMovieParameters(params);
        } else {
            updateStillParameters(params);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
