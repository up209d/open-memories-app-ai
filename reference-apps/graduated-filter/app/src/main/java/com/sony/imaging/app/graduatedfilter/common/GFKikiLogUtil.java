package com.sony.imaging.app.graduatedfilter.common;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFImageAdjustmentController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFImageSavingController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFThemeController;
import com.sony.scalar.sysutil.didep.Kikilog;

/* loaded from: classes.dex */
public class GFKikiLogUtil {
    private static final int ID_BOOT_TIMES = 4416;
    private static final int ID_CHANGING_TIMES_SKIP_ADJUSTMENT = 4436;
    private static final int ID_DISP_TIMES_SHOOTING_TIPS = 4418;
    private static final int ID_RESET_TIMES = 4417;
    private static final int ID_SHOTS_FINE_ALLIMAGES = 4430;
    private static final int ID_SHOTS_FINE_EFFECTIMAGE = 4431;
    private static final int ID_SHOTS_RAWJ_ALLIMAGES = 4426;
    private static final int ID_SHOTS_RAWJ_EFFECTIMAGE = 4427;
    private static final int ID_SHOTS_RAW_ALLIMAGES = 4424;
    private static final int ID_SHOTS_RAW_EFFECTIMAGE = 4425;
    private static final int ID_SHOTS_SKIP_ADJUSTMENT_OFF = 4434;
    private static final int ID_SHOTS_SKIP_ADJUSTMENT_ON = 4435;
    private static final int ID_SHOTS_STD_ALLIMAGES = 4432;
    private static final int ID_SHOTS_STD_EFFECTIMAGE = 4433;
    private static final int ID_SHOTS_THEME_BLUESKY = 4419;
    private static final int ID_SHOTS_THEME_CUSTOM1 = 4422;
    private static final int ID_SHOTS_THEME_CUSTOM2 = 4423;
    private static final int ID_SHOTS_THEME_ND = 4421;
    private static final int ID_SHOTS_THEME_SUNSET = 4420;
    private static final int ID_SHOTS_XFINE_ALLIMAGES = 4428;
    private static final int ID_SHOTS_XFINE_EFFECTIMAGE = 4429;
    private static final int ID_TRANSITION_TIMES_FINISH_ADJUSTMENT = 4437;
    private static final String TAG = AppLog.getClassName();
    private static GFKikiLogUtil mInstance = null;
    private static Integer mThemeId = null;
    private static Integer mQualityAndSaveImageId = null;
    private static Integer mSkipAdjustmentId = null;

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
        setUserAccumulateLog(Integer.valueOf(ID_BOOT_TIMES));
    }

    public void countParamReset() {
        setUserAccumulateLog(Integer.valueOf(ID_RESET_TIMES));
    }

    public void countTipsShowing() {
        setUserAccumulateLog(Integer.valueOf(ID_DISP_TIMES_SHOOTING_TIPS));
    }

    private void countShotsByTheme() {
        if (mThemeId != null) {
            setUserAccumulateLog(mThemeId);
        }
    }

    private static void setThemeId() {
        String theme = GFThemeController.getInstance().getValue(GFThemeController.THEME);
        if ("skyblue".equalsIgnoreCase(theme)) {
            mThemeId = Integer.valueOf(ID_SHOTS_THEME_BLUESKY);
            return;
        }
        if ("sunset".equalsIgnoreCase(theme)) {
            mThemeId = Integer.valueOf(ID_SHOTS_THEME_SUNSET);
            return;
        }
        if ("standard".equalsIgnoreCase(theme)) {
            mThemeId = Integer.valueOf(ID_SHOTS_THEME_ND);
        } else if ("custom1".equalsIgnoreCase(theme)) {
            mThemeId = Integer.valueOf(ID_SHOTS_THEME_CUSTOM1);
        } else if ("custom2".equalsIgnoreCase(theme)) {
            mThemeId = Integer.valueOf(ID_SHOTS_THEME_CUSTOM2);
        }
    }

    private void countShotsByQualityAndSaveImage() {
        if (mQualityAndSaveImageId != null) {
            setUserAccumulateLog(mQualityAndSaveImageId);
        }
    }

    private static void setQualityAndSaveImageId() {
        boolean isAllImagesSaved = GFImageSavingController.ALL.equalsIgnoreCase(GFImageSavingController.getInstance().getValue(GFImageSavingController.SAVE));
        try {
            String pictureQuality = PictureQualityController.getInstance().getValue(null);
            if (PictureQualityController.PICTURE_QUALITY_RAW.equalsIgnoreCase(pictureQuality)) {
                if (isAllImagesSaved) {
                    mQualityAndSaveImageId = Integer.valueOf(ID_SHOTS_RAW_ALLIMAGES);
                    return;
                } else {
                    mQualityAndSaveImageId = Integer.valueOf(ID_SHOTS_RAW_EFFECTIMAGE);
                    return;
                }
            }
            if (PictureQualityController.PICTURE_QUALITY_RAWJPEG.equalsIgnoreCase(pictureQuality)) {
                if (isAllImagesSaved) {
                    mQualityAndSaveImageId = Integer.valueOf(ID_SHOTS_RAWJ_ALLIMAGES);
                    return;
                } else {
                    mQualityAndSaveImageId = Integer.valueOf(ID_SHOTS_RAWJ_EFFECTIMAGE);
                    return;
                }
            }
            if (PictureQualityController.PICTURE_QUALITY_EXTRAFINE.equalsIgnoreCase(pictureQuality)) {
                if (isAllImagesSaved) {
                    mQualityAndSaveImageId = Integer.valueOf(ID_SHOTS_XFINE_ALLIMAGES);
                    return;
                } else {
                    mQualityAndSaveImageId = Integer.valueOf(ID_SHOTS_XFINE_EFFECTIMAGE);
                    return;
                }
            }
            if (PictureQualityController.PICTURE_QUALITY_FINE.equalsIgnoreCase(pictureQuality)) {
                if (isAllImagesSaved) {
                    mQualityAndSaveImageId = Integer.valueOf(ID_SHOTS_FINE_ALLIMAGES);
                    return;
                } else {
                    mQualityAndSaveImageId = Integer.valueOf(ID_SHOTS_FINE_EFFECTIMAGE);
                    return;
                }
            }
            if (PictureQualityController.PICTURE_QUALITY_STANDARD.equalsIgnoreCase(pictureQuality)) {
                if (isAllImagesSaved) {
                    mQualityAndSaveImageId = Integer.valueOf(ID_SHOTS_STD_ALLIMAGES);
                } else {
                    mQualityAndSaveImageId = Integer.valueOf(ID_SHOTS_STD_EFFECTIMAGE);
                }
            }
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
        }
    }

    private void countShotsBySkipAdjustment() {
        if (mSkipAdjustmentId != null) {
            setUserAccumulateLog(mSkipAdjustmentId);
        }
    }

    private static void setSkipAdjustmentId() {
        if (GFImageAdjustmentController.ON.equalsIgnoreCase(GFImageAdjustmentController.getInstance().getValue(GFImageAdjustmentController.ADJUSTMENT))) {
            mSkipAdjustmentId = Integer.valueOf(ID_SHOTS_SKIP_ADJUSTMENT_ON);
        } else {
            mSkipAdjustmentId = Integer.valueOf(ID_SHOTS_SKIP_ADJUSTMENT_OFF);
        }
    }

    public void countSkipAdjustmentChanging() {
        setUserAccumulateLog(Integer.valueOf(ID_CHANGING_TIMES_SKIP_ADJUSTMENT));
    }

    public void countAdjustmentTransition() {
        setUserAccumulateLog(Integer.valueOf(ID_TRANSITION_TIMES_FINISH_ADJUSTMENT));
    }

    public void countShots() {
        countShotsByTheme();
        countShotsByQualityAndSaveImage();
        countShotsBySkipAdjustment();
    }

    public void setCaptureIds() {
        setThemeId();
        setQualityAndSaveImageId();
        setSkipAdjustmentId();
    }
}
