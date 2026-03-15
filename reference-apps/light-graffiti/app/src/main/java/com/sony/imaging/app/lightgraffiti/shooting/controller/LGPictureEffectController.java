package com.sony.imaging.app.lightgraffiti.shooting.controller;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class LGPictureEffectController extends PictureEffectController {
    private static final String TAG = LGPictureEffectController.class.getSimpleName();
    private static LGPictureEffectController mInstance;

    public static LGPictureEffectController getInstance() {
        if (mInstance == null) {
            new LGPictureEffectController();
        }
        return mInstance;
    }

    private static void setController(LGPictureEffectController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected LGPictureEffectController() {
        setController(this);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureEffectController, com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        super.setValue(itemId, value);
        if (LGStateHolder.getInstance().isShootingStage3rd() && PictureEffectController.PICTUREEFFECT.equals(itemId)) {
            BackUpUtil.getInstance().setPreference(LGConstants.BACKUP_KEY_3RD_SELECTED_PICTURE_EFFECT, value);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureEffectController, com.sony.imaging.app.base.menu.IController
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
