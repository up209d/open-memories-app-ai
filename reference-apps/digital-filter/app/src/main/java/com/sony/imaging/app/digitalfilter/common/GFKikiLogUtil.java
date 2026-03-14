package com.sony.imaging.app.digitalfilter.common;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFilterSetController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFImageAdjustmentController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFImageSavingController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFPositionLinkController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFShootingOrderController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFThemeController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFVerticalLinkController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceLimitController;
import com.sony.scalar.sysutil.didep.Kikilog;

/* loaded from: classes.dex */
public class GFKikiLogUtil {
    private static final int ID_DF_SHOTS_2_AREAS_FILTER = 4475;
    private static final int ID_DF_SHOTS_3_AREAS_FILTER = 4476;
    private static final int ID_DF_SHOTS_ADJUST_SKIP_OFF = 4477;
    private static final int ID_DF_SHOTS_ADJUST_SKIP_ON = 4478;
    private static final int ID_DF_SHOTS_Q_FINE_ALL = 4471;
    private static final int ID_DF_SHOTS_Q_FINE_EFFECT = 4472;
    private static final int ID_DF_SHOTS_Q_RAWJ_ALL = 4467;
    private static final int ID_DF_SHOTS_Q_RAWJ_EFFECT = 4468;
    private static final int ID_DF_SHOTS_Q_RAW_ALL = 4465;
    private static final int ID_DF_SHOTS_Q_RAW_EFFECT = 4466;
    private static final int ID_DF_SHOTS_Q_STD_ALL = 4473;
    private static final int ID_DF_SHOTS_Q_STD_EFFECT = 4474;
    private static final int ID_DF_SHOTS_Q_XFINE_ALL = 4469;
    private static final int ID_DF_SHOTS_Q_XFINE_EFFECT = 4470;
    private static final int ID_DF_SHOTS_T_BLUESKY = 4461;
    private static final int ID_DF_SHOTS_T_CUSTOM1 = 4463;
    private static final int ID_DF_SHOTS_T_CUSTOM2 = 4464;
    private static final int ID_DF_SHOTS_T_REVERSE = 4459;
    private static final int ID_DF_SHOTS_T_STANDARD = 4458;
    private static final int ID_DF_SHOTS_T_STRIPE = 4460;
    private static final int ID_DF_SHOTS_T_SUNSET = 4462;
    private static final int ID_DF_SHOTS_VERTICAL_LINK_OFF = 4482;
    private static final int ID_DF_SHOTS_VERTICAL_LINK_ON = 4483;
    private static final int ID_DF_SHOTS_WB_LIMIT_AWB_CTEMP = 4480;
    private static final int ID_DF_SHOTS_WB_LIMIT_CTEMP = 4481;
    private static final int ID_DF_SHOTS_WB_LIMIT_NONE = 4479;
    private static final int ID_DF_TIMES_BOOT = 4448;
    private static final int ID_DF_TIMES_EXP_WB_LINK = 4457;
    private static final int ID_DF_TIMES_FIN_ADJUSTMENT = 4484;
    private static final int ID_DF_TIMES_FIN_SKIP = 4455;
    private static final int ID_DF_TIMES_POS_LINK = 4456;
    private static final int ID_DF_TIMES_SHOOTING_GUIDE = 4450;
    private static final int ID_DF_TIMES_SHOOTING_ORDER = 4451;
    private static final int ID_DF_TIMES_SHOOTING_SCREEN = 4452;
    private static final int ID_DF_TIMES_THEME_RESET = 4449;
    private static final int ID_DF_TIMES_TOGGLE_AV = 4486;
    private static final int ID_DF_TIMES_TOGGLE_EXPCOMP = 4485;
    private static final int ID_DF_TIMES_TOGGLE_ISO = 4488;
    private static final int ID_DF_TIMES_TOGGLE_POS = 4490;
    private static final int ID_DF_TIMES_TOGGLE_TV = 4487;
    private static final int ID_DF_TIMES_TOGGLE_WB = 4489;
    private static final int ID_DF_TIMES_VERTICAL_LINK = 4453;
    private static final int ID_DF_TIMES_WB_LIMIT = 4454;
    private static final String TAG = AppLog.getClassName();
    private static GFKikiLogUtil mInstance = null;
    private static Integer mThemeId = null;
    private static Integer mQualityAndSaveImageId = null;
    private static Integer mFilterSetsId = null;
    private static Integer mSkipAdjustmentId = null;
    private static Integer mWBLimitId = null;
    private static Integer mVerticalLinkId = null;

    public static GFKikiLogUtil getInstance() {
        if (mInstance == null) {
            mInstance = new GFKikiLogUtil();
        }
        return mInstance;
    }

    private static void setUserAccumulateLog(Integer kikilogId) {
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 32;
        Kikilog.setUserLog(kikilogId.intValue(), options);
    }

    public void countBooting() {
        setUserAccumulateLog(Integer.valueOf(ID_DF_TIMES_BOOT));
    }

    public void countParamReset() {
        setUserAccumulateLog(Integer.valueOf(ID_DF_TIMES_THEME_RESET));
    }

    public void countTipsShowing() {
        setUserAccumulateLog(Integer.valueOf(ID_DF_TIMES_SHOOTING_GUIDE));
    }

    public void countMenuSettings(String itemId) {
        if (itemId.equals(GFShootingOrderController.ORDER)) {
            countShootingOrderSetting();
            return;
        }
        if (itemId.equals(GFEEAreaController.EE)) {
            countShootingScreenSetting();
            return;
        }
        if (itemId.equals(GFVerticalLinkController.BOUNDARY_MODE)) {
            countVerticalLinkSetting();
            return;
        }
        if (itemId.equals(GFWhiteBalanceLimitController.WB_LIMIT)) {
            countWBLimitSetting();
        } else if (itemId.equals(GFImageAdjustmentController.ADJUSTMENT)) {
            countSkipSetting();
        } else if (itemId.equals(GFPositionLinkController.RELATIVE_MODE)) {
            countPosLinkSetting();
        }
    }

    public void countShootingOrderSetting() {
        setUserAccumulateLog(Integer.valueOf(ID_DF_TIMES_SHOOTING_ORDER));
    }

    public void countShootingScreenSetting() {
        setUserAccumulateLog(Integer.valueOf(ID_DF_TIMES_SHOOTING_SCREEN));
    }

    public void countVerticalLinkSetting() {
        setUserAccumulateLog(Integer.valueOf(ID_DF_TIMES_VERTICAL_LINK));
    }

    public void countWBLimitSetting() {
        setUserAccumulateLog(Integer.valueOf(ID_DF_TIMES_WB_LIMIT));
    }

    public void countSkipSetting() {
        setUserAccumulateLog(Integer.valueOf(ID_DF_TIMES_FIN_SKIP));
    }

    public void countPosLinkSetting() {
        setUserAccumulateLog(Integer.valueOf(ID_DF_TIMES_POS_LINK));
    }

    public void countExpWBLinkSetting() {
        setUserAccumulateLog(Integer.valueOf(ID_DF_TIMES_EXP_WB_LINK));
    }

    public void countToggleExpComp() {
        setUserAccumulateLog(Integer.valueOf(ID_DF_TIMES_TOGGLE_EXPCOMP));
    }

    public void countToggleAv() {
        setUserAccumulateLog(Integer.valueOf(ID_DF_TIMES_TOGGLE_AV));
    }

    public void countToggleTv() {
        setUserAccumulateLog(Integer.valueOf(ID_DF_TIMES_TOGGLE_TV));
    }

    public void countToggleISO() {
        setUserAccumulateLog(Integer.valueOf(ID_DF_TIMES_TOGGLE_ISO));
    }

    public void countToggleWB() {
        setUserAccumulateLog(Integer.valueOf(ID_DF_TIMES_TOGGLE_WB));
    }

    public void countTogglePos() {
        setUserAccumulateLog(Integer.valueOf(ID_DF_TIMES_TOGGLE_POS));
    }

    private void countShotsByTheme() {
        if (mThemeId != null) {
            setUserAccumulateLog(mThemeId);
        }
    }

    private static void setThemeId() {
        String theme = GFThemeController.getInstance().getValue(GFThemeController.THEME);
        if (GFThemeController.BLUESKY.equals(theme)) {
            mThemeId = Integer.valueOf(ID_DF_SHOTS_T_BLUESKY);
            return;
        }
        if ("sunset".equals(theme)) {
            mThemeId = Integer.valueOf(ID_DF_SHOTS_T_SUNSET);
            return;
        }
        if ("standard".equals(theme)) {
            mThemeId = Integer.valueOf(ID_DF_SHOTS_T_STANDARD);
            return;
        }
        if ("custom1".equals(theme)) {
            mThemeId = Integer.valueOf(ID_DF_SHOTS_T_CUSTOM1);
            return;
        }
        if ("custom2".equals(theme)) {
            mThemeId = Integer.valueOf(ID_DF_SHOTS_T_CUSTOM2);
        } else if (GFThemeController.REVERSE.equals(theme)) {
            mThemeId = Integer.valueOf(ID_DF_SHOTS_T_REVERSE);
        } else if (GFThemeController.STRIPE.equals(theme)) {
            mThemeId = Integer.valueOf(ID_DF_SHOTS_T_STRIPE);
        }
    }

    private void countShotsByQualityAndSaveImage() {
        if (mQualityAndSaveImageId != null) {
            setUserAccumulateLog(mQualityAndSaveImageId);
        }
    }

    private static void setQualityAndSaveImageId() {
        boolean isAllImagesSaved = GFImageSavingController.ALL.equals(GFImageSavingController.getInstance().getValue(GFImageSavingController.SAVE));
        try {
            String pictureQuality = PictureQualityController.getInstance().getValue(null);
            if (PictureQualityController.PICTURE_QUALITY_RAW.equals(pictureQuality)) {
                if (isAllImagesSaved) {
                    mQualityAndSaveImageId = Integer.valueOf(ID_DF_SHOTS_Q_RAW_ALL);
                    return;
                } else {
                    mQualityAndSaveImageId = Integer.valueOf(ID_DF_SHOTS_Q_RAW_EFFECT);
                    return;
                }
            }
            if (PictureQualityController.PICTURE_QUALITY_RAWJPEG.equals(pictureQuality)) {
                if (isAllImagesSaved) {
                    mQualityAndSaveImageId = Integer.valueOf(ID_DF_SHOTS_Q_RAWJ_ALL);
                    return;
                } else {
                    mQualityAndSaveImageId = Integer.valueOf(ID_DF_SHOTS_Q_RAWJ_EFFECT);
                    return;
                }
            }
            if (PictureQualityController.PICTURE_QUALITY_EXTRAFINE.equals(pictureQuality)) {
                if (isAllImagesSaved) {
                    mQualityAndSaveImageId = Integer.valueOf(ID_DF_SHOTS_Q_XFINE_ALL);
                    return;
                } else {
                    mQualityAndSaveImageId = Integer.valueOf(ID_DF_SHOTS_Q_XFINE_EFFECT);
                    return;
                }
            }
            if (PictureQualityController.PICTURE_QUALITY_FINE.equals(pictureQuality)) {
                if (isAllImagesSaved) {
                    mQualityAndSaveImageId = Integer.valueOf(ID_DF_SHOTS_Q_FINE_ALL);
                    return;
                } else {
                    mQualityAndSaveImageId = Integer.valueOf(ID_DF_SHOTS_Q_FINE_EFFECT);
                    return;
                }
            }
            if (PictureQualityController.PICTURE_QUALITY_STANDARD.equals(pictureQuality)) {
                if (isAllImagesSaved) {
                    mQualityAndSaveImageId = Integer.valueOf(ID_DF_SHOTS_Q_STD_ALL);
                } else {
                    mQualityAndSaveImageId = Integer.valueOf(ID_DF_SHOTS_Q_STD_EFFECT);
                }
            }
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
        }
    }

    private void countShotsByFilterSets() {
        if (mFilterSetsId != null) {
            setUserAccumulateLog(mFilterSetsId);
        }
    }

    private static void setFilterSetId() {
        if (GFFilterSetController.TWO_AREAS.equals(GFFilterSetController.getInstance().getValue())) {
            mFilterSetsId = Integer.valueOf(ID_DF_SHOTS_2_AREAS_FILTER);
        } else {
            mFilterSetsId = Integer.valueOf(ID_DF_SHOTS_3_AREAS_FILTER);
        }
    }

    private void countShotsBySkipAdjustment() {
        if (mSkipAdjustmentId != null) {
            setUserAccumulateLog(mSkipAdjustmentId);
        }
    }

    private static void setSkipAdjustmentId() {
        if (GFImageAdjustmentController.ON.equals(GFImageAdjustmentController.getInstance().getValue(GFImageAdjustmentController.ADJUSTMENT))) {
            mSkipAdjustmentId = Integer.valueOf(ID_DF_SHOTS_ADJUST_SKIP_ON);
        } else {
            mSkipAdjustmentId = Integer.valueOf(ID_DF_SHOTS_ADJUST_SKIP_OFF);
        }
    }

    private void countShotsByWBLimit() {
        if (mWBLimitId != null) {
            setUserAccumulateLog(mWBLimitId);
        }
    }

    private static void setWBLimitId() {
        String value = GFWhiteBalanceLimitController.getInstance().getValue(GFWhiteBalanceLimitController.WB_LIMIT);
        if (GFWhiteBalanceLimitController.CTEMP.equals(value)) {
            mWBLimitId = Integer.valueOf(ID_DF_SHOTS_WB_LIMIT_CTEMP);
        } else if (GFWhiteBalanceLimitController.CTEMP_AWB.equals(value)) {
            mWBLimitId = Integer.valueOf(ID_DF_SHOTS_WB_LIMIT_AWB_CTEMP);
        } else {
            mWBLimitId = Integer.valueOf(ID_DF_SHOTS_WB_LIMIT_NONE);
        }
    }

    private void countShotsByVerticalLink() {
        if (mVerticalLinkId != null) {
            setUserAccumulateLog(mVerticalLinkId);
        }
    }

    private static void setVerticalLinkId() {
        if (GFVerticalLinkController.VERTICAL.equals(GFVerticalLinkController.getInstance().getValue(GFVerticalLinkController.BOUNDARY_MODE))) {
            mVerticalLinkId = Integer.valueOf(ID_DF_SHOTS_VERTICAL_LINK_ON);
        } else {
            mVerticalLinkId = Integer.valueOf(ID_DF_SHOTS_VERTICAL_LINK_OFF);
        }
    }

    public void countShots() {
        countShotsByTheme();
        countShotsByQualityAndSaveImage();
        countShotsByFilterSets();
        countShotsBySkipAdjustment();
        countShotsByWBLimit();
        countShotsByVerticalLink();
    }

    public void setCaptureIds() {
        setThemeId();
        setQualityAndSaveImageId();
        setFilterSetId();
        setSkipAdjustmentId();
        setWBLimitId();
        setVerticalLinkId();
    }

    public void countAdjustmentTransition() {
        setUserAccumulateLog(Integer.valueOf(ID_DF_TIMES_FIN_ADJUSTMENT));
    }
}
