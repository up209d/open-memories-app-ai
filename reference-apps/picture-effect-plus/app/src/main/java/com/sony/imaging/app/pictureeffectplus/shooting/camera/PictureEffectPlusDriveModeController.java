package com.sony.imaging.app.pictureeffectplus.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;

/* loaded from: classes.dex */
public class PictureEffectPlusDriveModeController extends DriveModeController {
    private static PictureEffectPlusDriveModeController sInstance = null;
    private PictureEffectPlusController mController = null;

    private PictureEffectPlusDriveModeController() {
    }

    public static PictureEffectPlusDriveModeController getInstance() {
        if (sInstance == null) {
            sInstance = new PictureEffectPlusDriveModeController();
        }
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.DriveModeController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String value) {
        boolean ret = super.isAvailable(value);
        this.mController = PictureEffectPlusController.getInstance();
        String curEffect = this.mController.getBackupEffectValue();
        if (curEffect.equals(PictureEffectPlusController.MODE_MINIATURE_PLUS) || curEffect.equals("illust") || curEffect.equals("watercolor") || curEffect.equals(PictureEffectController.MODE_HDR_ART) || curEffect.equals(PictureEffectController.MODE_SOFT_FOCUS) || curEffect.equals(PictureEffectController.MODE_RICH_TONE_MONOCHROME)) {
            return false;
        }
        return ret;
    }
}
