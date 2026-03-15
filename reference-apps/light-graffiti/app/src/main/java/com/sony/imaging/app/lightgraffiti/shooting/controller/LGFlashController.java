package com.sony.imaging.app.lightgraffiti.shooting.controller;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class LGFlashController extends FlashController {
    private static final String TAG = LGFlashController.class.getSimpleName();
    private static LGFlashController mInstance;

    public static LGFlashController getInstance() {
        if (mInstance == null) {
            new LGFlashController();
        }
        return mInstance;
    }

    private static void setController(LGFlashController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected LGFlashController() {
        setController(this);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.FlashController
    public void setValue(String value) {
        super.setValue(value);
        if (LGStateHolder.getInstance().isShootingStage3rd()) {
            BackUpUtil.getInstance().setPreference(LGConstants.BACKUP_KEY_3RD_SELECTED_FLASH, value);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.FlashController, com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        super.setValue(itemId, value);
        if (LGStateHolder.getInstance().isShootingStage3rd()) {
            if (FlashController.FLASHMODE.equals(itemId)) {
                BackUpUtil.getInstance().setPreference(LGConstants.BACKUP_KEY_3RD_SELECTED_FLASH, value);
            } else if (FlashController.FLASH_COMPENSATION.equals(itemId)) {
                BackUpUtil.getInstance().setPreference(LGConstants.BACKUP_KEY_3RD_SELECTED_FLASH_COMPENSATION, value);
            } else {
                Log.e(TAG, AppLog.getMethodName() + " : itemId=" + itemId);
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.FlashController, com.sony.imaging.app.base.menu.IController
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

    @Override // com.sony.imaging.app.base.shooting.camera.FlashController, com.sony.imaging.app.base.shooting.camera.ShootingModeController, com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        int ret = super.getCautionIndex(itemId);
        if (LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_1ST)) {
            return 1;
        }
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.FlashController
    public void setDefaultValueEx(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        if (LGStateHolder.getInstance().isShootingStage3rd()) {
            super.setDefaultValueEx(params);
        }
    }
}
