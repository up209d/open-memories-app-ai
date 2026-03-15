package com.sony.imaging.app.startrails.base.menu.controller;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STBackUpKey;
import com.sony.imaging.app.startrails.util.STConstants;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class STCreativeStyleController extends CreativeStyleController {
    private static final String SEPARATOR_SLASH = "/";
    private static String TAG = "STCreativeStyleController";
    private static STCreativeStyleController mInstance;

    public static STCreativeStyleController getInstance() {
        AppLog.enter(TAG, "getInstance");
        if (mInstance == null) {
            createInstance();
        }
        AppLog.exit(TAG, "getInstance");
        return mInstance;
    }

    private static STCreativeStyleController createInstance() {
        AppLog.enter(TAG, "createInstance");
        if (mInstance == null) {
            mInstance = new STCreativeStyleController();
        }
        AppLog.exit(TAG, "createInstance");
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.CreativeStyleController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean isAvailable;
        boolean isAvailable2 = super.isAvailable(tag);
        AppLog.enter(TAG, "isAvailable()");
        if (true == isAvailable2 && STUtility.getInstance().getCurrentTrail() == 2) {
            isAvailable = true;
        } else {
            isAvailable = false;
        }
        AppLog.info(TAG, "isAvailable() = " + tag + isAvailable);
        AppLog.exit(TAG, "isAvailable()");
        return isAvailable;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.CreativeStyleController, com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        AppLog.enter(TAG, "setvalue()");
        super.setValue(itemId, value);
        CreativeStyleController.CreativeStyleOptions param = (CreativeStyleController.CreativeStyleOptions) CreativeStyleController.getInstance().getDetailValue();
        if (STUtility.getInstance().getCurrentTrail() == 2 && STPictureEffectController.getInstance().getValue().equalsIgnoreCase("off")) {
            if (!STUtility.getInstance().isPreTakePictureTestShot()) {
                BackUpUtil.getInstance().setPreference(STBackUpKey.CUSTOM_CREATIVE_STYLE_KEY, value);
            }
            updateDetailValueForCustom();
        } else {
            param.contrast = 0;
            param.saturation = 0;
            param.sharpness = 0;
        }
        super.setDetailValue(param);
        AppLog.exit(TAG, "setvalue");
    }

    @Override // com.sony.imaging.app.base.shooting.camera.CreativeStyleController
    public void setDetailValue(String mode, Object obj, Pair<Camera.Parameters, CameraEx.ParametersModifier> p) {
        super.setDetailValue(mode, obj, p);
        if (STUtility.getInstance().getCurrentTrail() == 2 && "standard".equals(getValue(CreativeStyleController.CREATIVESTYLE))) {
            String flattened = BackUpUtil.getInstance().getPreferenceString("ID_CREATIVESTYLE_STD_OPTION", null);
            BackUpUtil.getInstance().setPreference(STBackUpKey.CUSTOM_CREACTIVE_STYLE_STANDARD_KEY_OPTION_VALUE, flattened);
        }
    }

    public void setStarTrailsValue(String itemId, String value) {
        AppLog.enter(TAG, "setvalue");
        super.setValue(itemId, value);
        CreativeStyleController.CreativeStyleOptions param = (CreativeStyleController.CreativeStyleOptions) CreativeStyleController.getInstance().getDetailValue();
        if (STUtility.getInstance().getCurrentTrail() != 2) {
            param.contrast = 0;
            param.saturation = 0;
            param.sharpness = 0;
        } else {
            String optValue = BackUpUtil.getInstance().getPreferenceString(STBackUpKey.CUSTOM_CREACTIVE_STYLE_KEY_OPTION_VALUE, STConstants.CUSTOM_WB_OPTOIN_VALUE);
            String[] optvalueArray = optValue.split(SEPARATOR_SLASH);
            param.contrast = Integer.parseInt(optvalueArray[0]);
            param.saturation = Integer.parseInt(optvalueArray[1]);
            param.sharpness = Integer.parseInt(optvalueArray[2]);
        }
        super.setDetailValue(param);
        AppLog.exit(TAG, "setvalue");
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        if (STUtility.getInstance().getCurrentTrail() != 2 && itemId.endsWith(CreativeStyleController.CREATIVESTYLE)) {
            return 1;
        }
        int getCautionIndex = super.getCautionIndex(itemId);
        return getCautionIndex;
    }

    public void updateDetailValueForCustom() {
        if (STUtility.getInstance().getCurrentTrail() == 2) {
            CreativeStyleController.CreativeStyleOptions param = (CreativeStyleController.CreativeStyleOptions) CreativeStyleController.getInstance().getDetailValue();
            AppLog.enter(TAG, "updateCreativeStyleValue");
            String optValue = (("" + param.contrast) + SEPARATOR_SLASH + param.saturation) + SEPARATOR_SLASH + param.sharpness;
            BackUpUtil.getInstance().setPreference(STBackUpKey.CUSTOM_CREACTIVE_STYLE_KEY_OPTION_VALUE, optValue);
            AppLog.trace(TAG, "CUSTOM_CREACTIVE_STYLE_KEY_OPTION_VALUE  " + optValue);
            AppLog.exit(TAG, "updateCreativeStyleValue");
        }
    }
}
