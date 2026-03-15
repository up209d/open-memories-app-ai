package com.sony.imaging.app.lightgraffiti.shooting.controller;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.DROAutoHDRController;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class LGDROAutoHDRController extends DROAutoHDRController {
    private static final String TAG = LGDROAutoHDRController.class.getSimpleName();
    private static LGDROAutoHDRController mInstance;

    public static LGDROAutoHDRController getInstance() {
        if (mInstance == null) {
            new LGDROAutoHDRController();
        }
        return mInstance;
    }

    private static void setController(LGDROAutoHDRController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected LGDROAutoHDRController() {
        setController(this);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.DROAutoHDRController, com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        super.setValue(itemId, value);
        if (LGStateHolder.getInstance().isShootingStage3rd() && DROAutoHDRController.MENU_ITEM_ID_DROHDR.equals(itemId)) {
            BackUpUtil.getInstance().setPreference(LGConstants.BACKUP_KEY_3RD_SELECTED_DRO_AUTO_HDR, value);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.DROAutoHDRController, com.sony.imaging.app.base.menu.IController
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
