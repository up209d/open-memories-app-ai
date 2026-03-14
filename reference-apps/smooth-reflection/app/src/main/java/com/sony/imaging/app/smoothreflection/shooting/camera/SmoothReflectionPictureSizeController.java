package com.sony.imaging.app.smoothreflection.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;

/* loaded from: classes.dex */
public class SmoothReflectionPictureSizeController extends PictureSizeController {
    private static SmoothReflectionPictureSizeController mInstance;

    private SmoothReflectionPictureSizeController() {
    }

    public static SmoothReflectionPictureSizeController getInstance() {
        if (mInstance == null) {
            mInstance = new SmoothReflectionPictureSizeController();
        }
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureSizeController, com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.REC_MODE_CHANGED);
        super.setValue(itemId, value);
    }
}
