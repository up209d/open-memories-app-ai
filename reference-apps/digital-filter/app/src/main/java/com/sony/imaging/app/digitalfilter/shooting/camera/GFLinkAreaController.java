package com.sony.imaging.app.digitalfilter.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class GFLinkAreaController extends AbstractController {
    public static final String LAND = "land";
    public static final String LAYER3 = "layer3";
    public static final String NONE = "none";
    public static final String OFF = "link-off";
    public static final String ON = "link-on";
    public static final String SKY = "sky";
    private static boolean copySetting;
    private static final HashMap<String, String> mBlueSkyDefaultTable;
    private static final HashMap<String, String> mCustomDefaultTable;
    private static GFLinkAreaController mInstance;
    private static final HashMap<String, String> mReverseDefaultTable;
    private static final HashMap<String, String> mStandardDefaultTable;
    private static final HashMap<String, String> mStripeDefaultTable;
    private static final HashMap<String, String> mSunsetDefaultTable;
    private static ArrayList<String> sLandItemList;
    private static ArrayList<String> sLayer3ItemList;
    private static ArrayList<String> sSkyItemList;
    private static ArrayList<String> sSupportedList;
    private GFBackUpKey mGFBackUpKey;
    private static final String TAG = AppLog.getClassName();
    public static final String LAND_APERTURE = "land-aperture";
    public static final String LAND_SS = "land-ss";
    public static final String LAND_EXPCOMP = "land-expcomp";
    public static final String LAND_ISO = "land-iso";
    public static final String LAND_WB = "land-wb";
    public static final String SKY_APERTURE = "sky-aperture";
    public static final String SKY_SS = "sky-ss";
    public static final String SKY_EXPCOMP = "sky-expcomp";
    public static final String SKY_ISO = "sky-iso";
    public static final String SKY_WB = "sky-wb";
    public static final String LAYER3_APERTURE = "layer3-aperture";
    public static final String LAYER3_SS = "layer3-ss";
    public static final String LAYER3_EXPCOMP = "layer3-expcomp";
    public static final String LAYER3_ISO = "layer3-iso";
    public static final String LAYER3_WB = "layer3-wb";
    private static final String[] mLinkTags = {LAND_APERTURE, LAND_SS, LAND_EXPCOMP, LAND_ISO, LAND_WB, SKY_APERTURE, SKY_SS, SKY_EXPCOMP, SKY_ISO, SKY_WB, LAYER3_APERTURE, LAYER3_SS, LAYER3_EXPCOMP, LAYER3_ISO, LAYER3_WB};

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add(LAND_APERTURE);
        sSupportedList.add(LAND_SS);
        sSupportedList.add(LAND_EXPCOMP);
        sSupportedList.add(LAND_ISO);
        sSupportedList.add(LAND_WB);
        sSupportedList.add(SKY_APERTURE);
        sSupportedList.add(SKY_SS);
        sSupportedList.add(SKY_EXPCOMP);
        sSupportedList.add(SKY_ISO);
        sSupportedList.add(SKY_WB);
        sSupportedList.add(LAYER3_APERTURE);
        sSupportedList.add(LAYER3_SS);
        sSupportedList.add(LAYER3_EXPCOMP);
        sSupportedList.add(LAYER3_ISO);
        sSupportedList.add(LAYER3_WB);
        sSupportedList.add(ON);
        sSupportedList.add(OFF);
        if (sLandItemList == null) {
            sLandItemList = new ArrayList<>();
        }
        sLandItemList.add(LAND_APERTURE);
        sLandItemList.add(LAND_SS);
        sLandItemList.add(LAND_EXPCOMP);
        sLandItemList.add(LAND_ISO);
        sLandItemList.add(LAND_WB);
        if (sSkyItemList == null) {
            sSkyItemList = new ArrayList<>();
        }
        sSkyItemList.add(SKY_APERTURE);
        sSkyItemList.add(SKY_SS);
        sSkyItemList.add(SKY_EXPCOMP);
        sSkyItemList.add(SKY_ISO);
        sSkyItemList.add(SKY_WB);
        if (sLayer3ItemList == null) {
            sLayer3ItemList = new ArrayList<>();
        }
        sLayer3ItemList.add(LAYER3_APERTURE);
        sLayer3ItemList.add(LAYER3_SS);
        sLayer3ItemList.add(LAYER3_EXPCOMP);
        sLayer3ItemList.add(LAYER3_ISO);
        sLayer3ItemList.add(LAYER3_WB);
        mStandardDefaultTable = new HashMap<>();
        mStandardDefaultTable.put(LAYER3_APERTURE, ON);
        mStandardDefaultTable.put(LAYER3_SS, ON);
        mStandardDefaultTable.put(LAYER3_ISO, ON);
        mStandardDefaultTable.put(LAYER3_WB, ON);
        mStandardDefaultTable.put(LAYER3_EXPCOMP, OFF);
        mStandardDefaultTable.put(SKY_APERTURE, ON);
        mStandardDefaultTable.put(SKY_SS, ON);
        mStandardDefaultTable.put(SKY_ISO, ON);
        mStandardDefaultTable.put(SKY_WB, ON);
        mStandardDefaultTable.put(SKY_EXPCOMP, OFF);
        mStandardDefaultTable.put(LAND_APERTURE, ON);
        mStandardDefaultTable.put(LAND_SS, ON);
        mStandardDefaultTable.put(LAND_ISO, ON);
        mStandardDefaultTable.put(LAND_WB, ON);
        mStandardDefaultTable.put(LAND_EXPCOMP, OFF);
        mReverseDefaultTable = new HashMap<>();
        mReverseDefaultTable.put(LAYER3_APERTURE, ON);
        mReverseDefaultTable.put(LAYER3_SS, ON);
        mReverseDefaultTable.put(LAYER3_ISO, ON);
        mReverseDefaultTable.put(LAYER3_WB, ON);
        mReverseDefaultTable.put(LAYER3_EXPCOMP, OFF);
        mReverseDefaultTable.put(SKY_APERTURE, ON);
        mReverseDefaultTable.put(SKY_SS, ON);
        mReverseDefaultTable.put(SKY_ISO, ON);
        mReverseDefaultTable.put(SKY_WB, ON);
        mReverseDefaultTable.put(SKY_EXPCOMP, OFF);
        mReverseDefaultTable.put(LAND_APERTURE, ON);
        mReverseDefaultTable.put(LAND_SS, ON);
        mReverseDefaultTable.put(LAND_ISO, ON);
        mReverseDefaultTable.put(LAND_WB, ON);
        mReverseDefaultTable.put(LAND_EXPCOMP, OFF);
        mStripeDefaultTable = new HashMap<>();
        mStripeDefaultTable.put(LAYER3_APERTURE, ON);
        mStripeDefaultTable.put(LAYER3_SS, ON);
        mStripeDefaultTable.put(LAYER3_ISO, ON);
        mStripeDefaultTable.put(LAYER3_WB, ON);
        mStripeDefaultTable.put(LAYER3_EXPCOMP, ON);
        mStripeDefaultTable.put(SKY_APERTURE, ON);
        mStripeDefaultTable.put(SKY_SS, ON);
        mStripeDefaultTable.put(SKY_ISO, ON);
        mStripeDefaultTable.put(SKY_WB, OFF);
        mStripeDefaultTable.put(SKY_EXPCOMP, ON);
        mStripeDefaultTable.put(LAND_APERTURE, ON);
        mStripeDefaultTable.put(LAND_SS, ON);
        mStripeDefaultTable.put(LAND_ISO, ON);
        mStripeDefaultTable.put(LAND_WB, ON);
        mStripeDefaultTable.put(LAND_EXPCOMP, ON);
        mBlueSkyDefaultTable = new HashMap<>();
        mBlueSkyDefaultTable.put(LAYER3_APERTURE, ON);
        mBlueSkyDefaultTable.put(LAYER3_SS, ON);
        mBlueSkyDefaultTable.put(LAYER3_ISO, ON);
        mBlueSkyDefaultTable.put(LAYER3_WB, OFF);
        mBlueSkyDefaultTable.put(LAYER3_EXPCOMP, OFF);
        mBlueSkyDefaultTable.put(SKY_APERTURE, ON);
        mBlueSkyDefaultTable.put(SKY_SS, ON);
        mBlueSkyDefaultTable.put(SKY_ISO, ON);
        mBlueSkyDefaultTable.put(SKY_WB, OFF);
        mBlueSkyDefaultTable.put(SKY_EXPCOMP, OFF);
        mBlueSkyDefaultTable.put(LAND_APERTURE, ON);
        mBlueSkyDefaultTable.put(LAND_SS, ON);
        mBlueSkyDefaultTable.put(LAND_ISO, ON);
        mBlueSkyDefaultTable.put(LAND_WB, OFF);
        mBlueSkyDefaultTable.put(LAND_EXPCOMP, OFF);
        mSunsetDefaultTable = new HashMap<>();
        mSunsetDefaultTable.put(LAYER3_APERTURE, ON);
        mSunsetDefaultTable.put(LAYER3_SS, ON);
        mSunsetDefaultTable.put(LAYER3_ISO, ON);
        mSunsetDefaultTable.put(LAYER3_WB, OFF);
        mSunsetDefaultTable.put(LAYER3_EXPCOMP, OFF);
        mSunsetDefaultTable.put(SKY_APERTURE, ON);
        mSunsetDefaultTable.put(SKY_SS, ON);
        mSunsetDefaultTable.put(SKY_ISO, ON);
        mSunsetDefaultTable.put(SKY_WB, OFF);
        mSunsetDefaultTable.put(SKY_EXPCOMP, OFF);
        mSunsetDefaultTable.put(LAND_APERTURE, ON);
        mSunsetDefaultTable.put(LAND_SS, ON);
        mSunsetDefaultTable.put(LAND_ISO, ON);
        mSunsetDefaultTable.put(LAND_WB, OFF);
        mSunsetDefaultTable.put(LAND_EXPCOMP, OFF);
        mCustomDefaultTable = new HashMap<>();
        mCustomDefaultTable.put(LAYER3_APERTURE, ON);
        mCustomDefaultTable.put(LAYER3_SS, ON);
        mCustomDefaultTable.put(LAYER3_ISO, ON);
        mCustomDefaultTable.put(LAYER3_WB, ON);
        mCustomDefaultTable.put(LAYER3_EXPCOMP, OFF);
        mCustomDefaultTable.put(SKY_APERTURE, ON);
        mCustomDefaultTable.put(SKY_SS, ON);
        mCustomDefaultTable.put(SKY_ISO, ON);
        mCustomDefaultTable.put(SKY_WB, ON);
        mCustomDefaultTable.put(SKY_EXPCOMP, OFF);
        mCustomDefaultTable.put(LAND_APERTURE, ON);
        mCustomDefaultTable.put(LAND_SS, ON);
        mCustomDefaultTable.put(LAND_ISO, ON);
        mCustomDefaultTable.put(LAND_WB, ON);
        mCustomDefaultTable.put(LAND_EXPCOMP, OFF);
        copySetting = true;
    }

    public static GFLinkAreaController getInstance() {
        if (mInstance == null) {
            mInstance = createInstance();
        }
        return mInstance;
    }

    private static GFLinkAreaController createInstance() {
        if (mInstance == null) {
            mInstance = new GFLinkAreaController();
        }
        return mInstance;
    }

    private GFLinkAreaController() {
        this.mGFBackUpKey = null;
        this.mGFBackUpKey = GFBackUpKey.getInstance();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.info(TAG, "setValue  tag = " + tag + "  value =  " + value);
        if (isValidTag(tag)) {
            String theme = GFThemeController.getInstance().getValue();
            if (isFirstSetting(tag, value)) {
                setCommonSetting(tag, theme);
                setParent(tag, theme);
            }
            if (isValidValue(value)) {
                if (copySetting && value.equals(ON)) {
                    copyCommonSetting(tag, theme);
                }
                this.mGFBackUpKey.saveLinkValue(tag, value, theme);
            }
            switchParent(tag, value, theme);
        }
    }

    public void startToggleSetting() {
        copySetting = false;
    }

    public void stopToggleSetting() {
        copySetting = true;
    }

    public String getDefaultValue(String tag, String theme) {
        if (!isValidTag(tag)) {
            return OFF;
        }
        if (theme.equals("standard")) {
            String defaultValue = mStandardDefaultTable.get(tag);
            return defaultValue;
        }
        if (theme.equals(GFThemeController.REVERSE)) {
            String defaultValue2 = mReverseDefaultTable.get(tag);
            return defaultValue2;
        }
        if (theme.equals(GFThemeController.STRIPE)) {
            String defaultValue3 = mStripeDefaultTable.get(tag);
            return defaultValue3;
        }
        if (theme.equals(GFThemeController.BLUESKY)) {
            String defaultValue4 = mBlueSkyDefaultTable.get(tag);
            return defaultValue4;
        }
        if (theme.equals("sunset")) {
            String defaultValue5 = mSunsetDefaultTable.get(tag);
            return defaultValue5;
        }
        if (theme.equals("custom1")) {
            String defaultValue6 = mCustomDefaultTable.get(tag);
            return defaultValue6;
        }
        if (!theme.equals("custom2")) {
            return OFF;
        }
        String defaultValue7 = mCustomDefaultTable.get(tag);
        return defaultValue7;
    }

    private void setCommonSetting(String tag, String theme) {
        int layer = 0;
        if (isSky(tag)) {
            layer = 1;
        } else if (isLayer3(tag)) {
            layer = 2;
        }
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        if (isExpComp(tag)) {
            String value = params.getExposureComp(layer);
            this.mGFBackUpKey.setCommonExpComp(value, theme);
            return;
        }
        if (isAperture(tag)) {
            int value2 = params.getAperture(layer);
            this.mGFBackUpKey.setCommonAperture(value2, theme);
            return;
        }
        if (isSS(tag)) {
            int numerator = params.getSSNumerator(layer);
            int denominator = params.getSSDenominator(layer);
            this.mGFBackUpKey.setCommonSsNumerator(numerator, theme);
            this.mGFBackUpKey.setCommonSsDenominator(denominator, theme);
            return;
        }
        if (isISO(tag)) {
            String value3 = params.getISO(layer);
            this.mGFBackUpKey.setCommonISO(value3, theme);
        } else if (isWB(tag)) {
            String value4 = params.getWBMode(layer);
            this.mGFBackUpKey.setCommonWB(value4, theme);
            String value5 = params.getWBOption(layer);
            this.mGFBackUpKey.setCommonWBOption(value5, theme);
        }
    }

    private void setParent(String tag, String theme) {
        if (isExpComp(tag)) {
            this.mGFBackUpKey.setParentExpComp(tag, theme);
            return;
        }
        if (isAperture(tag)) {
            this.mGFBackUpKey.setParentAperture(tag, theme);
            return;
        }
        if (isSS(tag)) {
            this.mGFBackUpKey.setParentSS(tag, theme);
        } else if (isISO(tag)) {
            this.mGFBackUpKey.setParentISO(tag, theme);
        } else if (isWB(tag)) {
            this.mGFBackUpKey.setParentWB(tag, theme);
        }
    }

    public void checkParent(String theme) {
        String parent = this.mGFBackUpKey.getParentExpComp(theme);
        if (parent.equals("none")) {
            boolean isLandLink = isExpCompLink(0);
            boolean isSkyLink = isExpCompLink(1);
            boolean isLayer3Link = isExpCompLink(2);
            String tag = "none";
            if (isLandLink) {
                tag = LAND_EXPCOMP;
            } else if (isSkyLink) {
                tag = SKY_EXPCOMP;
            } else if (isLayer3Link) {
                tag = LAYER3_EXPCOMP;
            }
            this.mGFBackUpKey.setParentExpComp(tag, theme);
        }
        String parent2 = this.mGFBackUpKey.getParentAperture(theme);
        if (parent2.equals("none")) {
            boolean isLandLink2 = isApertureLink(0);
            boolean isSkyLink2 = isApertureLink(1);
            boolean isLayer3Link2 = isApertureLink(2);
            String tag2 = "none";
            if (isLandLink2) {
                tag2 = LAND_APERTURE;
            } else if (isSkyLink2) {
                tag2 = SKY_APERTURE;
            } else if (isLayer3Link2) {
                tag2 = LAYER3_APERTURE;
            }
            this.mGFBackUpKey.setParentAperture(tag2, theme);
        }
        String parent3 = this.mGFBackUpKey.getParentSS(theme);
        if (parent3.equals("none")) {
            boolean isLandLink3 = isSSLink(0);
            boolean isSkyLink3 = isSSLink(1);
            boolean isLayer3Link3 = isSSLink(2);
            String tag3 = "none";
            if (isLandLink3) {
                tag3 = LAND_SS;
            } else if (isSkyLink3) {
                tag3 = SKY_SS;
            } else if (isLayer3Link3) {
                tag3 = LAYER3_SS;
            }
            this.mGFBackUpKey.setParentSS(tag3, theme);
        }
        String parent4 = this.mGFBackUpKey.getParentISO(theme);
        if (parent4.equals("none")) {
            boolean isLandLink4 = isISOLink(0);
            boolean isSkyLink4 = isISOLink(1);
            boolean isLayer3Link4 = isISOLink(2);
            String tag4 = "none";
            if (isLandLink4) {
                tag4 = LAND_ISO;
            } else if (isSkyLink4) {
                tag4 = SKY_ISO;
            } else if (isLayer3Link4) {
                tag4 = LAYER3_ISO;
            }
            this.mGFBackUpKey.setParentISO(tag4, theme);
        }
        String parent5 = this.mGFBackUpKey.getParentWB(theme);
        if (parent5.equals("none")) {
            boolean isLandLink5 = isWBLink(0);
            boolean isSkyLink5 = isWBLink(1);
            boolean isLayer3Link5 = isWBLink(2);
            String tag5 = "none";
            if (isLandLink5) {
                tag5 = LAND_WB;
            } else if (isSkyLink5) {
                tag5 = SKY_WB;
            } else if (isLayer3Link5) {
                tag5 = LAYER3_WB;
            }
            this.mGFBackUpKey.setParentWB(tag5, theme);
        }
    }

    public boolean isExpCompParent(String area, String theme) {
        String parent = this.mGFBackUpKey.getParentExpComp(theme);
        return parent.contains(area);
    }

    public boolean isApertureParent(String area, String theme) {
        String parent = this.mGFBackUpKey.getParentAperture(theme);
        return parent.contains(area);
    }

    public boolean isSSParent(String area, String theme) {
        String parent = this.mGFBackUpKey.getParentSS(theme);
        return parent.contains(area);
    }

    public boolean isISOParent(String area, String theme) {
        String parent = this.mGFBackUpKey.getParentISO(theme);
        return parent.contains(area);
    }

    public boolean isWBParent(String area, String theme) {
        String parent = this.mGFBackUpKey.getParentWB(theme);
        return parent.contains(area);
    }

    private void copyCommonSetting(String tag, String theme) {
        int layer = 0;
        if (isSky(tag)) {
            layer = 1;
        } else if (isLayer3(tag)) {
            layer = 2;
        }
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        if (isExpComp(tag)) {
            String value = this.mGFBackUpKey.getCommonExpComp(theme);
            params.setExposureComp(layer, value);
            return;
        }
        if (isAperture(tag)) {
            int value2 = this.mGFBackUpKey.getCommonAperture(theme);
            params.setAperture(layer, value2);
            return;
        }
        if (isSS(tag)) {
            int numerator = this.mGFBackUpKey.getCommonSsNumerator(theme);
            int denominator = this.mGFBackUpKey.getCommonSsDenominator(theme);
            params.setSSNumerator(layer, numerator);
            params.setSSDenominator(layer, denominator);
            return;
        }
        if (isISO(tag)) {
            String value3 = this.mGFBackUpKey.getCommonISO(theme);
            params.setISO(layer, value3);
        } else if (isWB(tag)) {
            String wbMode = this.mGFBackUpKey.getCommonWB(theme);
            params.setWBMode(layer, wbMode);
            String wbOption = this.mGFBackUpKey.getCommonWBOption(theme);
            this.mGFBackUpKey.saveWBOption(wbMode, wbOption, layer, theme);
        }
    }

    private boolean isValidTag(String tag) {
        return sLandItemList.contains(tag) || sSkyItemList.contains(tag) || sLayer3ItemList.contains(tag);
    }

    private boolean isValidValue(String value) {
        return value.equals(ON) || value.equals(OFF);
    }

    private boolean isFirstSetting(String tag, String value) {
        if (value.equals(OFF)) {
            return false;
        }
        int index = -1;
        if (isLand(tag)) {
            index = sLandItemList.indexOf(tag);
        } else if (isSky(tag)) {
            index = sSkyItemList.indexOf(tag);
        } else if (isLayer3(tag)) {
            index = sLayer3ItemList.indexOf(tag);
        }
        boolean isLandOn = getValue(sLandItemList.get(index)).equals(ON);
        boolean isSkyOn = getValue(sSkyItemList.get(index)).equals(ON);
        boolean isLayer3On = getValue(sLayer3ItemList.get(index)).equals(ON);
        return (isLandOn || isSkyOn || isLayer3On) ? false : true;
    }

    private void switchParent(String tag, String value, String theme) {
        String parent;
        String parent2;
        String parent3;
        String parent4;
        String parent5;
        if (!value.equals(ON)) {
            if (isExpComp(tag)) {
                String parent6 = this.mGFBackUpKey.getParentExpComp(theme);
                if (tag.equals(parent6)) {
                    if (isExpCompLink(0)) {
                        parent5 = LAND_EXPCOMP;
                    } else if (isExpCompLink(1)) {
                        parent5 = SKY_EXPCOMP;
                    } else if (isExpCompLink(2)) {
                        parent5 = LAYER3_EXPCOMP;
                    } else {
                        parent5 = "none";
                    }
                    this.mGFBackUpKey.setParentExpComp(parent5, theme);
                    return;
                }
                return;
            }
            if (isAperture(tag)) {
                String parent7 = this.mGFBackUpKey.getParentAperture(theme);
                if (tag.equals(parent7)) {
                    if (isApertureLink(0)) {
                        parent4 = LAND_APERTURE;
                    } else if (isApertureLink(1)) {
                        parent4 = SKY_APERTURE;
                    } else if (isApertureLink(2)) {
                        parent4 = LAYER3_APERTURE;
                    } else {
                        parent4 = "none";
                    }
                    this.mGFBackUpKey.setParentAperture(parent4, theme);
                    return;
                }
                return;
            }
            if (isSS(tag)) {
                String parent8 = this.mGFBackUpKey.getParentSS(theme);
                if (tag.equals(parent8)) {
                    if (isSSLink(0)) {
                        parent3 = LAND_SS;
                    } else if (isSSLink(1)) {
                        parent3 = SKY_SS;
                    } else if (isSSLink(2)) {
                        parent3 = LAYER3_SS;
                    } else {
                        parent3 = "none";
                    }
                    this.mGFBackUpKey.setParentSS(parent3, theme);
                    return;
                }
                return;
            }
            if (isISO(tag)) {
                String parent9 = this.mGFBackUpKey.getParentISO(theme);
                if (tag.equals(parent9)) {
                    if (isISOLink(0)) {
                        parent2 = LAND_ISO;
                    } else if (isISOLink(1)) {
                        parent2 = SKY_ISO;
                    } else if (isISOLink(2)) {
                        parent2 = LAYER3_ISO;
                    } else {
                        parent2 = "none";
                    }
                    this.mGFBackUpKey.setParentISO(parent2, theme);
                    return;
                }
                return;
            }
            if (isWB(tag)) {
                String parent10 = this.mGFBackUpKey.getParentWB(theme);
                if (tag.equals(parent10)) {
                    if (isWBLink(0)) {
                        parent = LAND_WB;
                    } else if (isWBLink(1)) {
                        parent = SKY_WB;
                    } else if (isWBLink(2)) {
                        parent = LAYER3_WB;
                    } else {
                        parent = "none";
                    }
                    this.mGFBackUpKey.setParentWB(parent, theme);
                }
            }
        }
    }

    private boolean isLand(String tag) {
        return tag.contains(LAND);
    }

    private boolean isSky(String tag) {
        return tag.contains(SKY);
    }

    private boolean isLayer3(String tag) {
        return tag.contains(LAYER3);
    }

    private boolean isExpComp(String tag) {
        return tag.contains("exp");
    }

    private boolean isAperture(String tag) {
        return tag.contains("aperture");
    }

    private boolean isSS(String tag) {
        return tag.contains("ss");
    }

    private boolean isISO(String tag) {
        return tag.contains("iso");
    }

    private boolean isWB(String tag) {
        return tag.contains("wb");
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        if (tag == null || !isValidTag(tag)) {
            return null;
        }
        String value = this.mGFBackUpKey.getLinkValue(tag, GFThemeController.getInstance().getValue());
        return value;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return sSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return true;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return sSupportedList;
    }

    public boolean isExpCompLink(int layer) {
        String value = OFF;
        if (layer == 0) {
            value = getInstance().getValue(LAND_EXPCOMP);
        } else if (layer == 1) {
            value = getInstance().getValue(SKY_EXPCOMP);
        } else if (layer == 2) {
            value = getInstance().getValue(LAYER3_EXPCOMP);
        }
        return value.equals(ON);
    }

    public boolean isApertureLink(int layer) {
        String value = OFF;
        if (layer == 0) {
            value = getInstance().getValue(LAND_APERTURE);
        } else if (layer == 1) {
            value = getInstance().getValue(SKY_APERTURE);
        } else if (layer == 2) {
            value = getInstance().getValue(LAYER3_APERTURE);
        }
        return value.equals(ON);
    }

    public boolean isSSLink(int layer) {
        String value = OFF;
        if (layer == 0) {
            value = getInstance().getValue(LAND_SS);
        } else if (layer == 1) {
            value = getInstance().getValue(SKY_SS);
        } else if (layer == 2) {
            value = getInstance().getValue(LAYER3_SS);
        }
        return value.equals(ON);
    }

    public boolean isISOLink(int layer) {
        String value = OFF;
        if (layer == 0) {
            value = getInstance().getValue(LAND_ISO);
        } else if (layer == 1) {
            value = getInstance().getValue(SKY_ISO);
        } else if (layer == 2) {
            value = getInstance().getValue(LAYER3_ISO);
        }
        return value.equals(ON);
    }

    public boolean isWBLink(int layer) {
        String value = OFF;
        if (layer == 0) {
            value = getInstance().getValue(LAND_WB);
        } else if (layer == 1) {
            value = getInstance().getValue(SKY_WB);
        } else if (layer == 2) {
            value = getInstance().getValue(LAYER3_WB);
        }
        return value.equals(ON);
    }

    public boolean isLink(String tag) {
        String value = getValue(tag);
        return value != null && value.equals(ON);
    }

    public String[] getLinkTags() {
        return mLinkTags;
    }
}
