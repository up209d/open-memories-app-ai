package com.sony.imaging.app.timelapse.angleshift.common;

import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.angleshift.common.RCSA;
import com.sony.imaging.app.timelapse.angleshift.controller.AngleShiftFaderController;
import com.sony.imaging.app.timelapse.angleshift.controller.AngleShiftFrameRateController;
import com.sony.imaging.app.timelapse.angleshift.controller.AngleShiftMovieController;
import com.sony.imaging.app.timelapse.caution.TimelapseInfo;
import com.sony.imaging.app.timelapse.databaseutil.AppContext;
import com.sony.imaging.app.timelapse.databaseutil.DataBaseAdapter;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.BatteryObserver;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.imagefilter.CropImageFilter;
import com.sony.scalar.graphics.imagefilter.ScaleImageFilter;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.sysutil.ScalarProperties;
import java.io.File;

/* loaded from: classes.dex */
public class AngleShiftImageEditor {
    private static final int NUMBER_OF_COMPLETE_FADER_24P = 144;
    private static final int NUMBER_OF_COMPLETE_FADER_30P = 180;
    private static final int NUMBER_OF_TWO_SECONDE_FADER_24P = 48;
    private static final int NUMBER_OF_TWO_SECONDE_FADER_30P = 60;
    private static AngleShiftImageEditor sInstance = null;
    public static final String LIB_PATH = "/android/data/lib/" + AppContext.getAppContext().getPackageName() + "/lib/";
    private static int BATTERY_THRESHOLD_990 = 40;
    private final String TAG = AngleShiftImageEditor.class.getName();
    private int mTheme = 0;
    private CropImageFilter mCropImageFilter = null;
    private ScaleImageFilter mScaleImageFilter = null;
    private int mScaledInputWidth = 0;
    private int mScaledInputHeight = 0;
    private int mWidth = 0;
    private int mHeight = 0;
    private Rect mStartRect = null;
    private Rect mEndRect = null;
    private RCSA mRCSA = null;
    private DSP mDSP1 = null;
    private float mFaderStep = TimeLapseConstants.INVALID_APERTURE_VALUE;
    private int mFadeInEndPosition = 0;
    private int mFadeOutStartPosition = 0;
    private final int BYTE_IMAGE_SIZE = 33554432;
    private final int MOVIE_SIZE_FHD = 829440;
    private final int MOVIE_SIZE_HD = 368640;
    private final int LSI_TYPE_AVIP = 1;
    private long mPictureDecodeTime = 0;

    private AngleShiftImageEditor() {
    }

    public static AngleShiftImageEditor getInstance() {
        if (sInstance == null) {
            sInstance = new AngleShiftImageEditor();
        }
        return sInstance;
    }

    public void prepare() {
        this.mTheme = BackUpUtil.getInstance().getPreferenceInt(AngleShiftConstants.AS_CURRENT_THEME, 0);
        this.mCropImageFilter = new CropImageFilter();
        this.mScaleImageFilter = new ScaleImageFilter();
        if (AngleShiftMovieController.MOVIE_1920x1080.equalsIgnoreCase(AngleShiftMovieController.getInstance().getValue())) {
            this.mWidth = 1920;
            this.mHeight = 1080;
        } else {
            this.mWidth = 1280;
            this.mHeight = 720;
        }
        this.mStartRect = AngleShiftSetting.getInstance().getAngleShiftStartRect();
        this.mEndRect = AngleShiftSetting.getInstance().getAngleShiftEndRect();
        this.mScaledInputWidth = AngleShiftSetting.getInstance().getAngleShiftImageWidth();
        this.mScaledInputHeight = AngleShiftSetting.getInstance().getAngleShiftImageHeight();
        this.mRCSA = new RCSA();
        this.mDSP1 = DSP.createProcessor("sony-di-dsp");
        RCSA.Options options = new RCSA.Options();
        options.inWidth = this.mScaledInputWidth;
        options.inHeight = this.mScaledInputHeight;
        options.outWidth = this.mWidth;
        options.outHeight = this.mHeight;
        options.fader = AngleShiftFaderController.FADE_OFF.equalsIgnoreCase(AngleShiftFaderController.getInstance().getValue()) ? false : true;
        options.dsp1 = this.mDSP1;
        options.dsp2 = null;
        options.libpath = LIB_PATH;
        this.mRCSA.initParam(options);
        setTransparencyConditions();
    }

    public void terminate() {
        if (this.mRCSA != null) {
            this.mRCSA.release();
            this.mRCSA = null;
        }
        if (this.mDSP1 != null) {
            this.mDSP1.release();
            this.mDSP1 = null;
        }
        if (this.mCropImageFilter != null) {
            this.mCropImageFilter.release();
            this.mCropImageFilter = null;
        }
        if (this.mScaleImageFilter != null) {
            this.mScaleImageFilter.release();
            this.mScaleImageFilter = null;
        }
        this.mStartRect = null;
        this.mEndRect = null;
        this.mRCSA = null;
    }

    private OptimizedImage getFrameNoEffect(OptimizedImage inImage) {
        OptimizedImage cropImage = get16to9CroppedImage(inImage, true);
        OptimizedImage outImage = getScaledImage(cropImage, this.mWidth, this.mHeight, true);
        return outImage;
    }

    private OptimizedImage get16to9CroppedImage(OptimizedImage inImage, boolean isReleased) {
        OptimizedImage croppedImg = null;
        this.mCropImageFilter.setSrcRect(this.mStartRect);
        this.mCropImageFilter.setSource(inImage, isReleased);
        boolean resultCrop = this.mCropImageFilter.execute();
        if (resultCrop) {
            croppedImg = this.mCropImageFilter.getOutput();
        }
        this.mCropImageFilter.clearSources();
        return croppedImg;
    }

    private OptimizedImage getScaledImage(OptimizedImage inImage, int width, int height, boolean isReleased) {
        OptimizedImage scaledImage = null;
        this.mScaleImageFilter.setDestSize(width, height);
        this.mScaleImageFilter.setSource(inImage, isReleased);
        boolean result = this.mScaleImageFilter.execute();
        if (result) {
            scaledImage = this.mScaleImageFilter.getOutput();
        }
        this.mScaleImageFilter.clearSources();
        return scaledImage;
    }

    private OptimizedImage getCroppedImage(OptimizedImage inImage, Rect rect, boolean isReleased) {
        OptimizedImage croppedImg = null;
        this.mCropImageFilter.setSrcRect(rect);
        this.mCropImageFilter.setSource(inImage, isReleased);
        boolean resultCrop = this.mCropImageFilter.execute();
        if (resultCrop) {
            croppedImg = this.mCropImageFilter.getOutput();
        }
        this.mCropImageFilter.clearSources();
        return croppedImg;
    }

    private OptimizedImage getFrameCustom(OptimizedImage inImage, Rect rect, boolean isReleased) {
        OptimizedImage cropImage = getCroppedImage(inImage, rect, true);
        OptimizedImage outImage = getScaledImage(cropImage, this.mWidth, this.mHeight, true);
        return outImage;
    }

    public OptimizedImage getFrame(OptimizedImage inImage, int position) {
        if (AngleShiftSetting.getInstance().isPreRC()) {
            inImage = getScaledImage(inImage, this.mScaledInputWidth, this.mScaledInputHeight, true);
        }
        OptimizedImage outImage = this.mDSP1.createImage(this.mWidth, this.mHeight);
        RCSA.VOptions voptions = new RCSA.VOptions();
        voptions.rectf = getCurrentRectF(inImage, position);
        voptions.transparency = getCurrentTransparency(position);
        this.mRCSA.getFrame(inImage, outImage, voptions);
        return outImage;
    }

    public OptimizedImage getThubnailImage(OptimizedImage inImage, int position) {
        if (AngleShiftSetting.getInstance().isPreRC()) {
            inImage = getScaledImage(inImage, this.mScaledInputWidth, this.mScaledInputHeight, true);
        }
        OptimizedImage outImage = this.mDSP1.createImage(this.mWidth, this.mHeight);
        RCSA.VOptions voptions = new RCSA.VOptions();
        voptions.rectf = getCurrentRectF(inImage, position);
        if (AngleShiftFaderController.FADE_ON.equalsIgnoreCase(AngleShiftFaderController.getInstance().getValue())) {
            voptions.transparency = 8192;
        } else {
            voptions.transparency = 32768;
        }
        this.mRCSA.getFrame(inImage, outImage, voptions);
        return outImage;
    }

    public OptimizedImage getInputFrame(OptimizedImage inImage, int position) {
        if (!AngleShiftSetting.getInstance().isPreRC()) {
            return inImage;
        }
        OptimizedImage outImage = getScaledImage(inImage, this.mScaledInputWidth, this.mScaledInputHeight, true);
        return outImage;
    }

    public OptimizedImage getRCFrame(OptimizedImage inImage, int position) {
        OptimizedImage outImage = this.mDSP1.createImage(this.mWidth, this.mHeight);
        RCSA.VOptions voptions = new RCSA.VOptions();
        voptions.rectf = getCurrentRectF(inImage, position);
        voptions.transparency = getCurrentTransparency(position);
        this.mRCSA.getFrame(inImage, outImage, voptions);
        return outImage;
    }

    private void setTransparencyConditions() {
        int totalNumberOfFrames = AngleShiftSetting.getInstance().getTargetNumber();
        if ("framerate-24p".equalsIgnoreCase(AngleShiftFrameRateController.getInstance().getValue())) {
            if (NUMBER_OF_COMPLETE_FADER_24P <= totalNumberOfFrames) {
                this.mFadeInEndPosition = 47;
                this.mFaderStep = 0.020833334f;
            } else {
                this.mFadeInEndPosition = totalNumberOfFrames / 3;
                if (this.mFadeInEndPosition != 0) {
                    this.mFaderStep = 1.0f / this.mFadeInEndPosition;
                } else {
                    this.mFadeInEndPosition = -1;
                    this.mFaderStep = 1.0f;
                }
            }
        } else if (NUMBER_OF_COMPLETE_FADER_30P <= totalNumberOfFrames) {
            this.mFadeInEndPosition = NUMBER_OF_TWO_SECONDE_FADER_30P;
            this.mFaderStep = 0.016666668f;
        } else {
            this.mFadeInEndPosition = totalNumberOfFrames / 3;
            if (this.mFadeInEndPosition != 0) {
                this.mFaderStep = 1.0f / this.mFadeInEndPosition;
            } else {
                this.mFadeInEndPosition = -1;
                this.mFaderStep = 1.0f;
            }
        }
        this.mFadeOutStartPosition = totalNumberOfFrames - this.mFadeInEndPosition;
    }

    private int getCurrentTransparency(int position) {
        int transparency = 32768;
        if (AngleShiftFaderController.FADE_ON.equalsIgnoreCase(AngleShiftFaderController.getInstance().getValue())) {
            if (position <= this.mFadeInEndPosition) {
                transparency = (int) (32768 * position * this.mFaderStep);
            } else if (position >= this.mFadeOutStartPosition) {
                float transparencyf = ((position - this.mFadeOutStartPosition) + 1) * this.mFaderStep;
                transparency = 32768 - ((int) (32768 * transparencyf));
                AppLog.info(this.TAG, "transparencyf : " + transparencyf);
                AppLog.info(this.TAG, "mFaderStep : " + this.mFaderStep);
                AppLog.info(this.TAG, "mFadeOutStartPosition : " + this.mFadeOutStartPosition);
            }
        }
        AppLog.info(this.TAG, "position : " + position + ", transparency : 0x" + Integer.toHexString(transparency));
        return transparency;
    }

    public boolean isSameRectangle() {
        return this.mStartRect.equals(this.mEndRect);
    }

    private RectF getCurrentRectF(OptimizedImage inImage, int position) {
        float steps = AngleShiftSetting.getInstance().getTargetNumber() - 1.0f;
        if (steps == TimeLapseConstants.INVALID_APERTURE_VALUE) {
            steps = 1.0f;
        }
        float left = this.mStartRect.left + (((this.mEndRect.left - this.mStartRect.left) * position) / steps);
        float top = this.mStartRect.top + (((this.mEndRect.top - this.mStartRect.top) * position) / steps);
        float right = this.mStartRect.right + (((this.mEndRect.right - this.mStartRect.right) * position) / steps);
        float bottom = this.mStartRect.bottom + (((this.mEndRect.bottom - this.mStartRect.bottom) * position) / steps);
        RectF currentRect = new RectF(left, top, right, bottom);
        return currentRect;
    }

    private Rect getCurrentRect(OptimizedImage inImage, int position) {
        int steps = AngleShiftSetting.getInstance().getTargetNumber() - 1;
        if (steps == 0) {
            steps = 1;
        }
        int left = this.mStartRect.left + (((this.mEndRect.left - this.mStartRect.left) * position) / steps);
        int top = this.mStartRect.top + (((this.mEndRect.top - this.mStartRect.top) * position) / steps);
        int right = this.mStartRect.right + (((this.mEndRect.right - this.mStartRect.right) * position) / steps);
        int bottom = this.mStartRect.bottom + (((this.mEndRect.bottom - this.mStartRect.bottom) * position) / steps);
        Rect currentRect = new Rect(left, top, right, bottom);
        return currentRect;
    }

    public boolean checkStatus() {
        int batteryThreshold = (((int) (((AngleShiftSetting.getInstance().getTargetNumber() * getProcessingTime()) * 1.2d) / 1000.0d)) * 100) / 7200;
        int batteryThreshold2 = Math.max(2, batteryThreshold);
        if (TLCommonUtil.getInstance().isMediaInhOn()) {
            CautionUtilityClass.getInstance().requestTrigger(32769);
            return false;
        }
        if (DataBaseAdapter.getInstance().isDBSizeFull()) {
            CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_DB_FULL);
            return false;
        }
        if (getRemainingSpace() == 0) {
            CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_MEMORY_CARD_FULL);
            return false;
        }
        if (getRemainingSpace() < AngleShiftSetting.getInstance().getTargetNumber()) {
            CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_REMAINING_MEMORY_LESS);
            return true;
        }
        if (getValueOfBattery() >= batteryThreshold2) {
            return true;
        }
        CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_REMAINING_BATTERY_LESS);
        return true;
    }

    public boolean checkBatteryStatus() {
        int batteryThreshold = (BATTERY_THRESHOLD_990 * AngleShiftSetting.getInstance().getTargetNumber()) / 990;
        if (getValueOfBattery() >= batteryThreshold) {
            return true;
        }
        CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_REMAINING_BATTERY_LESS);
        return false;
    }

    public int getRemainingSpace() {
        int totalAvailRemaining;
        File memoryCard = new File(Environment.getExternalStorageDirectory().getAbsoluteFile().toString());
        if (AngleShiftMovieController.MOVIE_1920x1080.equalsIgnoreCase(AngleShiftMovieController.getInstance().getValue())) {
            totalAvailRemaining = (int) ((memoryCard.getUsableSpace() - 33554432) / 829440);
        } else {
            totalAvailRemaining = (int) ((memoryCard.getUsableSpace() - 33554432) / 368640);
        }
        if (totalAvailRemaining < 0) {
            return 0;
        }
        return totalAvailRemaining;
    }

    public int getValueOfBattery() {
        if (1 == BatteryObserver.getInt(BatteryObserver.TAG_PLUGGED)) {
            return 10000;
        }
        int value = BatteryObserver.getInt(BatteryObserver.TAG_LEVEL);
        return value;
    }

    private int getLsiType() {
        String pfVer = ScalarProperties.getString("version.platform");
        float lsiType = Float.parseFloat(pfVer);
        return (int) lsiType;
    }

    public boolean isAVIP() {
        return 1 == getLsiType();
    }

    public void setPictureDecodingTime(long pictureDecodeTime) {
        this.mPictureDecodeTime = pictureDecodeTime;
    }

    public long getPictureDecodingTime() {
        return this.mPictureDecodeTime;
    }

    public long getSAProcessingTime() {
        float a;
        float b;
        int averageWidth = (AngleShiftSetting.getInstance().getAngleShiftStartRect().width() + AngleShiftSetting.getInstance().getAngleShiftEndRect().width()) / 2;
        int averageWidth2 = Math.abs(averageWidth);
        if (AngleShiftMovieController.MOVIE_1920x1080.equalsIgnoreCase(AngleShiftMovieController.getInstance().getValue())) {
            if (isAVIP()) {
                a = 0.25066844f;
                b = 1320.0f - (0.25066844f * 4912.0f);
            } else {
                a = 0.25735295f;
                b = 1200.0f - (0.25735295f * 4912.0f);
            }
        } else if (isAVIP()) {
            a = 0.15625f;
            b = 710.0f - (0.15625f * 3264.0f);
        } else {
            a = 0.17137097f;
            b = 600.0f - (0.17137097f * 3264.0f);
        }
        int processingTime = (int) ((averageWidth2 * a) + b);
        return processingTime;
    }

    public int getProcessingTime() {
        int estTimeByDecoding = (int) getInstance().getPictureDecodingTime();
        int estTimeBySAProcessing = (int) getInstance().getSAProcessingTime();
        int estTime = Math.max(estTimeByDecoding, estTimeBySAProcessing) + getProcessingTimeOffset();
        return estTime;
    }

    public int getProcessingTimeOffset() {
        int offset;
        if (isAVIP()) {
            offset = AngleShiftConstants.AS_SAVING_TIME_OFFSET_AVIP;
        } else {
            offset = AngleShiftConstants.AS_SAVING_TIME_OFFSET_MUSASHI;
        }
        if (AngleShiftSetting.getInstance().isPreRC()) {
            if (isAVIP()) {
                offset += 70;
            } else {
                offset += DigitalZoomController.ZOOM_INIT_VALUE_MODIFIED;
            }
        }
        if (AngleShiftSetting.getInstance().isRotatedImage()) {
            if (isAVIP()) {
                return offset + 70;
            }
            return offset + DigitalZoomController.ZOOM_INIT_VALUE_MODIFIED;
        }
        return offset;
    }
}
