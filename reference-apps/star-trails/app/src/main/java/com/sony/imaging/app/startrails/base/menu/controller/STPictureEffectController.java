package com.sony.imaging.app.startrails.base.menu.controller;

import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STBackUpKey;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class STPictureEffectController extends PictureEffectController {
    private static String TAG = AppLog.getClassName();
    private static STPictureEffectController mInstance;
    ArrayList<String> ITEM_ID_NEEDS_HAVING_OPTION;

    public static STPictureEffectController getInstance() {
        AppLog.enter(TAG, "getInstance()");
        if (mInstance == null) {
            createInstance();
        }
        AppLog.exit(TAG, "getInstance()");
        return mInstance;
    }

    protected void createOptionList() {
        this.ITEM_ID_NEEDS_HAVING_OPTION = new ArrayList<>();
        this.ITEM_ID_NEEDS_HAVING_OPTION.add(PictureEffectController.MODE_TOY_CAMERA);
        this.ITEM_ID_NEEDS_HAVING_OPTION.add(PictureEffectController.MODE_POSTERIZATION);
        this.ITEM_ID_NEEDS_HAVING_OPTION.add(PictureEffectController.MODE_PART_COLOR);
        this.ITEM_ID_NEEDS_HAVING_OPTION.add(PictureEffectController.MODE_SOFT_FOCUS);
        this.ITEM_ID_NEEDS_HAVING_OPTION.add(PictureEffectController.MODE_MINIATURE);
    }

    private static STPictureEffectController createInstance() {
        AppLog.enter(TAG, "createInstance()");
        if (mInstance == null) {
            mInstance = new STPictureEffectController();
        }
        AppLog.exit(TAG, "createInstance()");
        return mInstance;
    }

    private STPictureEffectController() {
        createOptionList();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureEffectController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean isAvailable;
        AppLog.enter(TAG, "isAvailable()");
        boolean isAvailable2 = super.isAvailable(tag);
        if (isAvailable2 && STUtility.getInstance().getCurrentTrail() == 2) {
            isAvailable = true;
        } else {
            isAvailable = false;
            updateCreativeStyle();
        }
        AppLog.info(TAG, "isAvailable() = " + tag + isAvailable);
        AppLog.exit(TAG, "isAvailable()");
        return isAvailable;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.PictureEffectController, com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        AppLog.enter(TAG, "setvalue()");
        super.setValue(itemId, value);
        if (PictureEffectController.PICTUREEFFECT.equals(itemId)) {
            updateCustomPctureEffectBackupValue(value);
            if (this.ITEM_ID_NEEDS_HAVING_OPTION.contains(value)) {
                value = getValue(value);
                updateCustomPctureEffectOptionBackupValue(value);
            }
        } else {
            updateCustomPctureEffectOptionBackupValue(value);
        }
        if (STUtility.getInstance().getCurrentTrail() == 2 && value.equalsIgnoreCase("off")) {
            updateCreativeStyle();
        }
        AppLog.exit(TAG, "setvalue()");
    }

    private void updateCustomPctureEffectOptionBackupValue(String value) {
        if (STUtility.getInstance().getCurrentTrail() == 2 && !STUtility.getInstance().isPreTakePictureTestShot()) {
            BackUpUtil.getInstance().setPreference(STBackUpKey.CUSTOM_PICTURE_EFFECT_KEY_OPTION, value);
        }
    }

    public void setStarTrailsValue(String itemId, String value) {
        AppLog.enter(TAG, "setvalue()");
        super.setValue(itemId, value);
        if (STUtility.getInstance().getCurrentTrail() == 2 && value.equalsIgnoreCase("off")) {
            updateCreativeStyle();
        }
        AppLog.exit(TAG, "setvalue()");
    }

    public void updateCustomPctureEffectBackupValue(String value) {
        String value2 = getValue(PictureEffectController.PICTUREEFFECT);
        AppLog.enter(TAG, "updateCustomPctureEffectBackupValue()  getValue()" + value2);
        if (STUtility.getInstance().getCurrentTrail() == 2 && !STUtility.getInstance().isPreTakePictureTestShot()) {
            BackUpUtil.getInstance().setPreference(STBackUpKey.CUSTOM_PICTURE_EFFECT_KEY, value2);
        }
    }

    private void updateCreativeStyle() {
        AppLog.enter(TAG, "updateCreativeStyle()");
        String creativeStyle = BackUpUtil.getInstance().getPreferenceString(STBackUpKey.CUSTOM_CREATIVE_STYLE_KEY, "standard");
        STCreativeStyleController.getInstance().setStarTrailsValue(CreativeStyleController.CREATIVESTYLE, creativeStyle);
        AppLog.exit(TAG, "updateCreativeStyle()");
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        if (STUtility.getInstance().getCurrentTrail() != 2 && itemId.endsWith(PictureEffectController.PICTUREEFFECT)) {
            return 1;
        }
        int getCautionIndex = super.getCautionIndex(itemId);
        return getCautionIndex;
    }
}
