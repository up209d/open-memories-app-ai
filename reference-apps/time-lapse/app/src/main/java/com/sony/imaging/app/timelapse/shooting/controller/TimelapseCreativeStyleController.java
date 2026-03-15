package com.sony.imaging.app.timelapse.shooting.controller;

import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class TimelapseCreativeStyleController extends CreativeStyleController {
    private static final String SEPARATOR_SLASH = "/";
    private static TimelapseCreativeStyleController mInstance = new TimelapseCreativeStyleController();

    public static TimelapseCreativeStyleController getInstance() {
        return mInstance;
    }

    public void setTimelapseValue(String itemId, String value) {
        CreativeStyleController.CreativeStyleOptions param = (CreativeStyleController.CreativeStyleOptions) CreativeStyleController.getInstance().getDetailValue();
        TLCommonUtil util = TLCommonUtil.getInstance();
        int theme = util.getCurrentState();
        if (7 != theme) {
            super.setValue(itemId, value);
            param.contrast = 0;
            param.saturation = 0;
            param.sharpness = 0;
        } else {
            String value2 = BackUpUtil.getInstance().getPreferenceString(TimeLapseConstants.CUSTOM_CREATIVE_STYLE_KEY, "standard");
            if (!"off".equalsIgnoreCase(TimelapsePictureEffectController.getInstance().getValue("PictureEffect"))) {
                value2 = "standard";
                BackUpUtil.getInstance().setPreference(TimeLapseConstants.CUSTOM_CREACTIVE_STYLE_STANDARD_BY_PICTUREEFFECT, true);
            }
            super.setValue(itemId, value2);
            String optValue = BackUpUtil.getInstance().getPreferenceString(TimeLapseConstants.CUSTOM_CREACTIVE_STYLE_KEY_OPTION_VALUE, "0/0/0");
            if (!"off".equalsIgnoreCase(TimelapsePictureEffectController.getInstance().getValue("PictureEffect"))) {
                optValue = "0/0/0";
            }
            String[] optvalueArray = optValue.split(SEPARATOR_SLASH);
            param.contrast = Integer.parseInt(optvalueArray[0]);
            param.saturation = Integer.parseInt(optvalueArray[1]);
            param.sharpness = Integer.parseInt(optvalueArray[2]);
        }
        setDetailValue(param);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.CreativeStyleController, com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        TLCommonUtil util = TLCommonUtil.getInstance();
        int theme = util.getCurrentState();
        if (7 == theme) {
            BackUpUtil.getInstance().setPreference(TimeLapseConstants.CUSTOM_CREATIVE_STYLE_KEY, value);
        }
        super.setValue(itemId, value);
        if (7 == theme) {
            backUpDetailValue(value);
        }
    }

    private void backUpDetailValue(String value) {
        CreativeStyleController.CreativeStyleOptions cStyleparam = (CreativeStyleController.CreativeStyleOptions) super.getDetailValue();
        String cStyleoptValue = (("" + cStyleparam.contrast) + SEPARATOR_SLASH + cStyleparam.saturation) + SEPARATOR_SLASH + cStyleparam.sharpness;
        if (value.equalsIgnoreCase("standard") && "off".equalsIgnoreCase(TimelapsePictureEffectController.getInstance().getValue("PictureEffect"))) {
            if (BackUpUtil.getInstance().getPreferenceBoolean(TimeLapseConstants.CUSTOM_CREACTIVE_STYLE_STANDARD_BY_PICTUREEFFECT, false)) {
                BackUpUtil.getInstance().setPreference(TimeLapseConstants.CUSTOM_CREACTIVE_STYLE_STANDARD_BY_PICTUREEFFECT, false);
                cStyleoptValue = BackUpUtil.getInstance().getPreferenceString(TimeLapseConstants.CUSTOM_CREACTIVE_STYLE_STANDARD_KEY_OPTION_VALUE, "0/0/0");
                CreativeStyleController.CreativeStyleOptions param = (CreativeStyleController.CreativeStyleOptions) super.getDetailValue();
                String[] optvalueArray = cStyleoptValue.split(SEPARATOR_SLASH);
                param.contrast = Integer.parseInt(optvalueArray[0]);
                param.saturation = Integer.parseInt(optvalueArray[1]);
                param.sharpness = Integer.parseInt(optvalueArray[2]);
                super.setDetailValue(param);
            } else {
                BackUpUtil.getInstance().setPreference(TimeLapseConstants.CUSTOM_CREACTIVE_STYLE_STANDARD_KEY_OPTION_VALUE, cStyleoptValue);
            }
        }
        BackUpUtil.getInstance().setPreference(TimeLapseConstants.CUSTOM_CREACTIVE_STYLE_KEY_OPTION_VALUE, cStyleoptValue);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.CreativeStyleController
    public Object getDetailValue(String value) {
        CreativeStyleController.CreativeStyleOptions cStyleparam = (CreativeStyleController.CreativeStyleOptions) super.getDetailValue(value);
        TLCommonUtil util = TLCommonUtil.getInstance();
        int theme = util.getCurrentState();
        if (7 == theme && value.equalsIgnoreCase("standard") && "off".equalsIgnoreCase(TimelapsePictureEffectController.getInstance().getValue("PictureEffect")) && BackUpUtil.getInstance().getPreferenceBoolean(TimeLapseConstants.CUSTOM_CREACTIVE_STYLE_STANDARD_BY_PICTUREEFFECT, false)) {
            String cStyleoptValue = BackUpUtil.getInstance().getPreferenceString(TimeLapseConstants.CUSTOM_CREACTIVE_STYLE_STANDARD_KEY_OPTION_VALUE, "0/0/0");
            String[] optvalueArray = cStyleoptValue.split(SEPARATOR_SLASH);
            cStyleparam.contrast = Integer.parseInt(optvalueArray[0]);
            cStyleparam.saturation = Integer.parseInt(optvalueArray[1]);
            cStyleparam.sharpness = Integer.parseInt(optvalueArray[2]);
            super.setDetailValue(cStyleparam);
            BackUpUtil.getInstance().setPreference(TimeLapseConstants.CUSTOM_CREACTIVE_STYLE_STANDARD_BY_PICTUREEFFECT, false);
        }
        return cStyleparam;
    }
}
