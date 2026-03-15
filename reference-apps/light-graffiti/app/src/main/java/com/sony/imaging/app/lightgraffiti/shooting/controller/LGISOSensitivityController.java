package com.sony.imaging.app.lightgraffiti.shooting.controller;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class LGISOSensitivityController extends ISOSensitivityController {
    private static final String TAG = LGISOSensitivityController.class.getSimpleName();
    private static LGISOSensitivityController mInstance;

    public static LGISOSensitivityController getInstance() {
        if (mInstance == null) {
            new LGISOSensitivityController();
        }
        return mInstance;
    }

    private static void setController(LGISOSensitivityController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected LGISOSensitivityController() {
        setController(this);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ISOSensitivityController, com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        super.setValue(itemId, value);
        if (LGStateHolder.getInstance().isShootingStage3rd()) {
            BackUpUtil.getInstance().setPreference(LGConstants.BACKUP_KEY_3RD_SELECTED_ISO_SENSITIVITY, value);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        int ret = super.getCautionIndex(itemId);
        if (LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_1ST)) {
            return 1;
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ISOSensitivityController, com.sony.imaging.app.base.menu.IController
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
}
