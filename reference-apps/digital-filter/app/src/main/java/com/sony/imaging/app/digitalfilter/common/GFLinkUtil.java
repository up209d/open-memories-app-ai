package com.sony.imaging.app.digitalfilter.common;

import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.shooting.ChangeAperture;
import com.sony.imaging.app.digitalfilter.shooting.ChangeSs;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFLinkAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFThemeController;

/* loaded from: classes.dex */
public class GFLinkUtil {
    private static GFLinkUtil mInstance = null;

    protected GFLinkUtil() {
    }

    public static GFLinkUtil getInstance() {
        if (mInstance == null) {
            mInstance = new GFLinkUtil();
        }
        return mInstance;
    }

    public String saveLinkedExpComp(String expCompLevel, int settingLayer, boolean isLand, boolean isSky, boolean isLayer3) {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        String theme = GFThemeController.getInstance().getValue();
        if (isLand) {
            if (!GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_EXPCOMP)) {
                return null;
            }
            GFBackUpKey.getInstance().setCommonExpComp(expCompLevel, theme);
            String value = params.getExposureComp(1);
            params.setExposureComp(1, value);
            String value2 = params.getExposureComp(2);
            params.setExposureComp(2, value2);
            if (!GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_EXPCOMP) || !GFCommonUtil.getInstance().isLayerSetting()) {
                return null;
            }
            return GFConstants.RELOAD_SKY_IMAGE;
        }
        if (isSky) {
            if (!GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_EXPCOMP)) {
                return null;
            }
            GFBackUpKey.getInstance().setCommonExpComp(expCompLevel, theme);
            String value3 = params.getExposureComp(0);
            params.setExposureComp(0, value3);
            String value4 = params.getExposureComp(2);
            params.setExposureComp(2, value4);
            if (!GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_EXPCOMP) || !GFCommonUtil.getInstance().isLayerSetting()) {
                return null;
            }
            return GFConstants.RELOAD_LAND_IMAGE;
        }
        if (!isLayer3 || !GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAYER3_EXPCOMP)) {
            return null;
        }
        GFBackUpKey.getInstance().setCommonExpComp(expCompLevel, theme);
        String value5 = params.getExposureComp(0);
        params.setExposureComp(0, value5);
        String value6 = params.getExposureComp(1);
        params.setExposureComp(1, value6);
        if ((!GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_EXPCOMP) && !GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_EXPCOMP)) || !GFCommonUtil.getInstance().isLayerSetting()) {
            return null;
        }
        return GFConstants.RELOAD_ALL_IMAGE;
    }

    public String saveLinkedAperture(int settingLayer, boolean isLand, boolean isSky, boolean isLayer3) {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        String theme = GFThemeController.getInstance().getValue();
        int aperture = ChangeAperture.getApertureFromPF();
        if (aperture <= 0) {
            return null;
        }
        params.setAperture(settingLayer, aperture);
        if (isLand) {
            if (!GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_APERTURE)) {
                return null;
            }
            GFBackUpKey.getInstance().setCommonAperture(aperture, theme);
            int value = params.getAperture(1);
            params.setAperture(1, value);
            int value2 = params.getAperture(2);
            params.setAperture(2, value2);
            if (!GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_APERTURE) || !GFCommonUtil.getInstance().isLayerSetting()) {
                return null;
            }
            return GFConstants.RELOAD_SKY_IMAGE;
        }
        if (isSky) {
            if (!GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_APERTURE)) {
                return null;
            }
            GFBackUpKey.getInstance().setCommonAperture(aperture, theme);
            int value3 = params.getAperture(0);
            params.setAperture(0, value3);
            int value4 = params.getAperture(2);
            params.setAperture(2, value4);
            if (!GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_EXPCOMP) || !GFCommonUtil.getInstance().isLayerSetting()) {
                return null;
            }
            return GFConstants.RELOAD_LAND_IMAGE;
        }
        if (!isLayer3 || !GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAYER3_APERTURE)) {
            return null;
        }
        GFBackUpKey.getInstance().setCommonAperture(aperture, theme);
        int value5 = params.getAperture(0);
        params.setAperture(0, value5);
        int value6 = params.getAperture(1);
        params.setAperture(1, value6);
        if ((!GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_APERTURE) && !GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_APERTURE)) || !GFCommonUtil.getInstance().isLayerSetting()) {
            return null;
        }
        return GFConstants.RELOAD_ALL_IMAGE;
    }

    public String saveLinkedSS(int settingLayer, boolean isLand, boolean isSky, boolean isLayer3) {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        String theme = GFThemeController.getInstance().getValue();
        Pair<Integer, Integer> ss = ChangeSs.getCurrentSsFromPF();
        params.setSSNumerator(settingLayer, ((Integer) ss.first).intValue());
        params.setSSDenominator(settingLayer, ((Integer) ss.second).intValue());
        if (isLand) {
            if (!GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_SS)) {
                return null;
            }
            GFBackUpKey.getInstance().setCommonSsNumerator(((Integer) ss.first).intValue(), theme);
            GFBackUpKey.getInstance().setCommonSsDenominator(((Integer) ss.second).intValue(), theme);
            int numerator = params.getSSNumerator(1);
            int denominator = params.getSSDenominator(1);
            params.setSSNumerator(1, numerator);
            params.setSSDenominator(1, denominator);
            int numerator2 = params.getSSNumerator(2);
            int denominator2 = params.getSSDenominator(2);
            params.setSSNumerator(2, numerator2);
            params.setSSDenominator(2, denominator2);
            if (!GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_SS)) {
                return null;
            }
            return GFConstants.RELOAD_SKY_IMAGE;
        }
        if (isSky) {
            if (!GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_SS)) {
                return null;
            }
            GFBackUpKey.getInstance().setCommonSsNumerator(((Integer) ss.first).intValue(), theme);
            GFBackUpKey.getInstance().setCommonSsDenominator(((Integer) ss.second).intValue(), theme);
            int numerator3 = params.getSSNumerator(0);
            int denominator3 = params.getSSDenominator(0);
            params.setSSNumerator(0, numerator3);
            params.setSSDenominator(0, denominator3);
            int numerator4 = params.getSSNumerator(2);
            int denominator4 = params.getSSDenominator(2);
            params.setSSNumerator(2, numerator4);
            params.setSSDenominator(2, denominator4);
            if (!GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_SS)) {
                return null;
            }
            return GFConstants.RELOAD_LAND_IMAGE;
        }
        if (!isLayer3 || !GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAYER3_SS)) {
            return null;
        }
        GFBackUpKey.getInstance().setCommonSsNumerator(((Integer) ss.first).intValue(), theme);
        GFBackUpKey.getInstance().setCommonSsDenominator(((Integer) ss.second).intValue(), theme);
        int numerator5 = params.getSSNumerator(0);
        int denominator5 = params.getSSDenominator(0);
        params.setSSNumerator(0, numerator5);
        params.setSSDenominator(0, denominator5);
        int numerator6 = params.getSSNumerator(1);
        int denominator6 = params.getSSDenominator(1);
        params.setSSNumerator(1, numerator6);
        params.setSSDenominator(1, denominator6);
        if ((!GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_SS) && !GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_SS)) || !GFCommonUtil.getInstance().isLayerSetting()) {
            return null;
        }
        return GFConstants.RELOAD_ALL_IMAGE;
    }

    public void saveLinkedISO(int settingLayer, boolean isLand, boolean isSky, boolean isLayer3) {
        String value = ISOSensitivityController.getInstance().getValue();
        String theme = GFThemeController.getInstance().getValue();
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        params.setISO(settingLayer, value);
        if (isLand) {
            if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_ISO)) {
                GFBackUpKey.getInstance().setCommonISO(value, theme);
                params.setISO(1, params.getISO(1));
                params.setISO(2, params.getISO(2));
                if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_ISO) && GFCommonUtil.getInstance().isLayerSetting()) {
                    CameraNotificationManager.getInstance().requestNotify(GFConstants.RELOAD_SKY_IMAGE);
                    return;
                }
                return;
            }
            return;
        }
        if (isSky) {
            if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_ISO)) {
                GFBackUpKey.getInstance().setCommonISO(value, theme);
                params.setISO(0, params.getISO(0));
                params.setISO(2, params.getISO(2));
                if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_ISO) && GFCommonUtil.getInstance().isLayerSetting()) {
                    CameraNotificationManager.getInstance().requestNotify(GFConstants.RELOAD_LAND_IMAGE);
                    return;
                }
                return;
            }
            return;
        }
        if (isLayer3 && GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAYER3_ISO)) {
            GFBackUpKey.getInstance().setCommonISO(value, theme);
            params.setISO(0, params.getISO(0));
            params.setISO(1, params.getISO(1));
            if ((GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_ISO) || GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_ISO)) && GFCommonUtil.getInstance().isLayerSetting()) {
                CameraNotificationManager.getInstance().requestNotify(GFConstants.RELOAD_ALL_IMAGE);
            }
        }
    }

    public void saveLinkedWB(int settingLayer, boolean isLand, boolean isSky, boolean isLayer3) {
        String theme = GFThemeController.getInstance().getValue();
        String wbMode = WhiteBalanceController.getInstance().getValue();
        WhiteBalanceController.WhiteBalanceParam option = (WhiteBalanceController.WhiteBalanceParam) WhiteBalanceController.getInstance().getDetailValue();
        String wbOption = "" + option.getLightBalance() + "/" + option.getColorComp() + "/" + option.getColorTemp();
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        params.setWBMode(settingLayer, wbMode);
        GFBackUpKey.getInstance().saveWBOption(wbMode, wbOption, settingLayer, theme);
        if (isLand) {
            if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_WB)) {
                GFBackUpKey.getInstance().setCommonWB(wbMode, theme);
                GFBackUpKey.getInstance().setCommonWBOption(wbOption, theme);
                String wbMode2 = params.getWBMode(1);
                String wbOption2 = params.getWBOption(1);
                params.setWBMode(1, wbMode2);
                GFBackUpKey.getInstance().saveWBOption(wbMode2, wbOption2, 1, theme);
                String wbMode3 = params.getWBMode(2);
                String wbOption3 = params.getWBOption(2);
                params.setWBMode(2, wbMode3);
                GFBackUpKey.getInstance().saveWBOption(wbMode3, wbOption3, 2, theme);
                if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_WB) && GFCommonUtil.getInstance().isLayerSetting()) {
                    CameraNotificationManager.getInstance().requestNotify(GFConstants.RELOAD_SKY_IMAGE);
                    return;
                }
                return;
            }
            return;
        }
        if (isSky) {
            if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_WB)) {
                GFBackUpKey.getInstance().setCommonWB(wbMode, theme);
                GFBackUpKey.getInstance().setCommonWBOption(wbOption, theme);
                String wbMode4 = params.getWBMode(0);
                String wbOption4 = params.getWBOption(0);
                params.setWBMode(0, wbMode4);
                GFBackUpKey.getInstance().saveWBOption(wbMode4, wbOption4, 0, theme);
                String wbMode5 = params.getWBMode(2);
                String wbOption5 = params.getWBOption(2);
                params.setWBMode(2, wbMode5);
                GFBackUpKey.getInstance().saveWBOption(wbMode5, wbOption5, 2, theme);
                if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_WB) && GFCommonUtil.getInstance().isLayerSetting()) {
                    CameraNotificationManager.getInstance().requestNotify(GFConstants.RELOAD_LAND_IMAGE);
                    return;
                }
                return;
            }
            return;
        }
        if (isLayer3 && GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAYER3_WB)) {
            GFBackUpKey.getInstance().setCommonWB(wbMode, theme);
            GFBackUpKey.getInstance().setCommonWBOption(wbOption, theme);
            String wbMode6 = params.getWBMode(0);
            String wbOption6 = params.getWBOption(0);
            params.setWBMode(0, wbMode6);
            GFBackUpKey.getInstance().saveWBOption(wbMode6, wbOption6, 0, theme);
            String wbMode7 = params.getWBMode(1);
            String wbOption7 = params.getWBOption(1);
            params.setWBMode(1, wbMode7);
            GFBackUpKey.getInstance().saveWBOption(wbMode7, wbOption7, 1, theme);
            if ((GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_WB) || GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_WB)) && GFCommonUtil.getInstance().isLayerSetting()) {
                CameraNotificationManager.getInstance().requestNotify(GFConstants.RELOAD_ALL_IMAGE);
            }
        }
    }

    public void saveLinkedCWB(String wbMode, String wbOption, int settingLayer) {
        String theme = GFThemeController.getInstance().getValue();
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        params.setWBMode(settingLayer, wbMode);
        boolean isLand = settingLayer == 0;
        boolean isSky = settingLayer == 1;
        boolean isLayer3 = settingLayer == 2;
        if (isLand) {
            if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_WB)) {
                GFBackUpKey.getInstance().setCommonWB(wbMode, theme);
                GFBackUpKey.getInstance().setCommonWBOption(wbOption, theme);
                String mode = params.getWBMode(1);
                params.setWBMode(1, mode);
                String mode2 = params.getWBMode(2);
                params.setWBMode(2, mode2);
            }
        } else if (isSky) {
            if (GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_WB)) {
                GFBackUpKey.getInstance().setCommonWB(wbMode, theme);
                GFBackUpKey.getInstance().setCommonWBOption(wbOption, theme);
                String mode3 = params.getWBMode(0);
                params.setWBMode(0, mode3);
                String mode4 = params.getWBMode(2);
                params.setWBMode(2, mode4);
            }
        } else if (isLayer3 && GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAYER3_WB)) {
            GFBackUpKey.getInstance().setCommonWB(wbMode, theme);
            GFBackUpKey.getInstance().setCommonWBOption(wbOption, theme);
            String mode5 = params.getWBMode(0);
            params.setWBMode(0, mode5);
            String mode6 = params.getWBMode(1);
            params.setWBMode(1, mode6);
        }
        GFBackUpKey.getInstance().saveWBOption(wbMode, wbOption, 0, GFThemeController.BLUESKY);
        GFBackUpKey.getInstance().saveWBOption(wbMode, wbOption, 1, GFThemeController.BLUESKY);
        GFBackUpKey.getInstance().saveWBOption(wbMode, wbOption, 2, GFThemeController.BLUESKY);
        GFBackUpKey.getInstance().saveWBOption(wbMode, wbOption, 0, "sunset");
        GFBackUpKey.getInstance().saveWBOption(wbMode, wbOption, 1, "sunset");
        GFBackUpKey.getInstance().saveWBOption(wbMode, wbOption, 2, "sunset");
        GFBackUpKey.getInstance().saveWBOption(wbMode, wbOption, 0, "standard");
        GFBackUpKey.getInstance().saveWBOption(wbMode, wbOption, 1, "standard");
        GFBackUpKey.getInstance().saveWBOption(wbMode, wbOption, 2, "standard");
        GFBackUpKey.getInstance().saveWBOption(wbMode, wbOption, 0, "custom1");
        GFBackUpKey.getInstance().saveWBOption(wbMode, wbOption, 1, "custom1");
        GFBackUpKey.getInstance().saveWBOption(wbMode, wbOption, 2, "custom1");
        GFBackUpKey.getInstance().saveWBOption(wbMode, wbOption, 0, "custom2");
        GFBackUpKey.getInstance().saveWBOption(wbMode, wbOption, 1, "custom2");
        GFBackUpKey.getInstance().saveWBOption(wbMode, wbOption, 2, "custom2");
        GFBackUpKey.getInstance().saveWBOption(wbMode, wbOption, 0, GFThemeController.REVERSE);
        GFBackUpKey.getInstance().saveWBOption(wbMode, wbOption, 1, GFThemeController.REVERSE);
        GFBackUpKey.getInstance().saveWBOption(wbMode, wbOption, 2, GFThemeController.REVERSE);
        GFBackUpKey.getInstance().saveWBOption(wbMode, wbOption, 0, GFThemeController.STRIPE);
        GFBackUpKey.getInstance().saveWBOption(wbMode, wbOption, 1, GFThemeController.STRIPE);
        GFBackUpKey.getInstance().saveWBOption(wbMode, wbOption, 2, GFThemeController.STRIPE);
    }
}
