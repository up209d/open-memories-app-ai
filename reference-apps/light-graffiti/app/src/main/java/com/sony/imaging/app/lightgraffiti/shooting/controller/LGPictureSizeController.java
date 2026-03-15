package com.sony.imaging.app.lightgraffiti.shooting.controller;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.AppLog;

/* loaded from: classes.dex */
public class LGPictureSizeController extends PictureSizeController {
    private static final String TAG = LGPictureSizeController.class.getSimpleName();
    private static LGPictureSizeController mInstance;

    public static LGPictureSizeController getInstance() {
        if (mInstance == null) {
            new LGPictureSizeController();
        }
        return mInstance;
    }

    private static void setController(LGPictureSizeController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected LGPictureSizeController() {
        setController(this);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureSizeController, com.sony.imaging.app.base.shooting.camera.ShootingModeController, com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        int ret = super.getCautionIndex(itemId);
        if (LGStateHolder.getInstance().isShootingStage3rd()) {
            return 1;
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureSizeController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean ret;
        String stage = LGStateHolder.getInstance().getShootingStage();
        if (stage.equals(LGStateHolder.SHOOTING_1ST)) {
            ret = super.isAvailable(tag);
        } else {
            ret = false;
        }
        Log.d(TAG, AppLog.getMethodName() + " : tag=" + tag + ", ret=" + ret);
        return ret;
    }
}
