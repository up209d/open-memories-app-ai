package com.sony.imaging.app.startrails.base.menu.controller;

import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STUtility;

/* loaded from: classes.dex */
public class STExposureCompensationController extends ExposureCompensationController {
    private static String TAG = AppLog.getClassName();
    private static STExposureCompensationController mInstance;

    public static STExposureCompensationController getInstance() {
        AppLog.enter(TAG, "getInstance");
        if (mInstance == null) {
            createInstance();
        }
        AppLog.exit(TAG, "getInstance");
        return mInstance;
    }

    private static STExposureCompensationController createInstance() {
        AppLog.enter(TAG, "createInstance");
        if (mInstance == null) {
            mInstance = new STExposureCompensationController();
        }
        AppLog.exit(TAG, "createInstance");
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureCompensationController, com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, "setValue()");
        super.setValue(tag, value);
        STUtility.getInstance().updateBackValue();
        AppLog.exit(TAG, "setValue");
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureCompensationController
    public boolean setEvDialValue(int position) {
        boolean isSetEVDial = super.setEvDialValue(position);
        STUtility.getInstance().updateBackValue();
        return isSetEVDial;
    }

    public void setStartTrailsValue(String itemId, String value) {
        AppLog.enter(TAG, "setValue()");
        super.setValue(itemId, value);
        AppLog.exit(TAG, "setValue()");
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureCompensationController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        AppLog.enter(TAG, "isAvailable");
        boolean isAvailable = super.isAvailable(tag);
        AppLog.exit(TAG, "isAvailable");
        STUtility.getInstance().setEVDialRotated(false);
        return isAvailable;
    }
}
