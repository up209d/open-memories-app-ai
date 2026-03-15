package com.sony.imaging.app.lightgraffiti.shooting.controller;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class LGExposureCompensationController extends ExposureCompensationController {
    private static final String TAG = LGExposureCompensationController.class.getSimpleName();
    private static LGExposureCompensationController mInstance;

    public static LGExposureCompensationController getInstance() {
        if (mInstance == null) {
            new LGExposureCompensationController();
        }
        return mInstance;
    }

    private static void setController(LGExposureCompensationController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected LGExposureCompensationController() {
        setController(this);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureCompensationController, com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        super.setValue(tag, value);
        if (LGStateHolder.getInstance().isShootingStage3rd()) {
            BackUpUtil.getInstance().setPreference(LGConstants.BACKUP_KEY_3RD_SELECTED_EXPOSURE_COMPENSATION, value);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureCompensationController, com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        super.onGetInitParameters(params);
        save3rdSetting();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureCompensationController
    public boolean setEvDialValue(int position, Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        boolean ret;
        if (LGStateHolder.getInstance().isShootingStage3rd()) {
            ret = super.setEvDialValue(position, params);
        } else {
            ret = super.setEvDialValue(0, params);
        }
        save3rdSetting();
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureCompensationController
    public void incrementExposureCompensation() {
        super.incrementExposureCompensation();
        save3rdSetting();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureCompensationController
    public void decrementExposureCompensation() {
        super.decrementExposureCompensation();
        save3rdSetting();
    }

    private void save3rdSetting() {
        try {
            if (LGStateHolder.getInstance().isShootingStage3rd()) {
                String value = getValue("ExposureCompensation");
                BackUpUtil.getInstance().setPreference(LGConstants.BACKUP_KEY_3RD_SELECTED_EXPOSURE_COMPENSATION, value);
            }
        } catch (IController.NotSupportedException e) {
            Log.e(TAG, AppLog.getMethodName() + "getValue is Exception!");
            e.printStackTrace();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureCompensationController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean ret;
        if (LGStateHolder.getInstance().isShootingStage3rd()) {
            ret = super.isAvailable(tag);
        } else {
            ret = false;
        }
        Log.d(TAG, AppLog.getMethodName() + " : tag=" + tag + ", ret=" + ret);
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        int ret = super.getCautionIndex(itemId);
        if (LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_1ST)) {
            return 1;
        }
        return ret;
    }
}
