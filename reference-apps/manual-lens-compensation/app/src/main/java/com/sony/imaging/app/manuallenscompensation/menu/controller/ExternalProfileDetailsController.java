package com.sony.imaging.app.manuallenscompensation.menu.controller;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import java.util.List;

/* loaded from: classes.dex */
public class ExternalProfileDetailsController extends AbstractController {
    private static String TAG = "ID_EXTERNAL_PROFILE_DETAILS_CONTROLLER";
    private static ExternalProfileDetailsController mInstance;

    public static ExternalProfileDetailsController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            new ExternalProfileDetailsController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private static void setController(ExternalProfileDetailsController controller) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = controller;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    protected ExternalProfileDetailsController() {
        AppLog.enter(TAG, AppLog.getMethodName());
        setController(this);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return false;
    }
}
