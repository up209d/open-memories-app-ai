package com.sony.imaging.app.pictureeffectplus.shooting.camera;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class PictureEffectPlusAppSettingController extends AbstractController {
    public static final String ApplicationSettings = "ApplicationSettings";
    private static PictureEffectPlusAppSettingController sInstance = null;
    private PictureEffectPlusController mController = null;
    private String mSelectedTheme = ApplicationSettings;
    private ArrayList<String> mSupportedValue;

    private PictureEffectPlusAppSettingController() {
        this.mSupportedValue = null;
        this.mSupportedValue = new ArrayList<>();
        this.mSupportedValue.add(ApplicationSettings);
    }

    public static PictureEffectPlusAppSettingController getInstance() {
        if (sInstance == null) {
            sInstance = new PictureEffectPlusAppSettingController();
        }
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        return this.mSelectedTheme;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return this.mSupportedValue;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return this.mSupportedValue;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        this.mController = PictureEffectPlusController.getInstance();
        String curEffect = this.mController.getBackupEffectValue();
        if (!curEffect.equals(PictureEffectController.MODE_ROUGH_MONO) && !curEffect.equals(PictureEffectController.MODE_RICH_TONE_MONOCHROME) && !curEffect.equals("watercolor") && !curEffect.equals(PictureEffectController.MODE_POP_COLOR) && !curEffect.equals(PictureEffectController.MODE_RETRO_PHOTO)) {
            return true;
        }
        return false;
    }
}
