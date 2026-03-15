package com.sony.imaging.app.lightgraffiti.shooting.controller;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class LGCreativeStyleController extends CreativeStyleController {
    private static final String TAG = LGCreativeStyleController.class.getSimpleName();
    private static LGCreativeStyleController mInstance;

    public static LGCreativeStyleController getInstance() {
        if (mInstance == null) {
            new LGCreativeStyleController();
        }
        return mInstance;
    }

    private static void setController(LGCreativeStyleController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected LGCreativeStyleController() {
        setController(this);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.CreativeStyleController, com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        if (LGStateHolder.getInstance().isShootingStage3rd() && CreativeStyleController.CREATIVESTYLE.equals(itemId)) {
            BackUpUtil.getInstance().setPreference(LGConstants.BACKUP_KEY_3RD_SELECTED_CREATIVE_STYLE, value);
        }
        super.setValue(itemId, value);
        if (LGStateHolder.getInstance().isShootingStage3rd() && "off".equalsIgnoreCase(PictureEffectController.getInstance().getValue())) {
            CreativeStyleController.CreativeStyleOptions options = (CreativeStyleController.CreativeStyleOptions) getDetailValue(value);
            BackUpUtil.getInstance().setPreference(LGConstants.BACKUP_KEY_3RD_SELECTED_CREATIVE_STYLE_OPTIONS_CONTRAST + value, Integer.valueOf(options.contrast));
            BackUpUtil.getInstance().setPreference(LGConstants.BACKUP_KEY_3RD_SELECTED_CREATIVE_STYLE_OPTIONS_SATURATION + value, Integer.valueOf(options.saturation));
            BackUpUtil.getInstance().setPreference(LGConstants.BACKUP_KEY_3RD_SELECTED_CREATIVE_STYLE_OPTIONS_SHARPNESS + value, Integer.valueOf(options.sharpness));
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.CreativeStyleController, com.sony.imaging.app.base.menu.IController
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
